package com.hmdp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.hmdp.Constants.QueueConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.dto.VoucherObj;
import com.hmdp.entity.User;
import com.hmdp.service.IUserService;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@SpringBootTest
class MessageTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testSend() throws InterruptedException {

        VoucherObj obj = new VoucherObj(1l, 2l, 3l);
        String s = JSON.toJSONString(obj);
        rabbitTemplate.convertAndSend(QueueConstants.ORDER_EXCHANGE, "order", s);

    }


    @Test
    void testReceive() throws InterruptedException {

        Long icr = redisTemplate.opsForValue().increment("icr");
        System.out.println(icr);


    }

    @Autowired
    private IUserService userService;

    @Test
    void testMultiLogin() throws IOException {
        List<User> userList = userService.lambdaQuery().last("limit 1000").list();

        for (User user : userList) {
            String token = UUID.randomUUID().toString(true);
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create().ignoreNullValue()
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
            String tokenKey = LOGIN_USER_KEY + token;
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        }

        Set<String> keys = stringRedisTemplate.keys(LOGIN_USER_KEY + "*");
        @Cleanup FileWriter fileWriter = new FileWriter( "D:\\BaiduNetdiskDownload\\tokens.txt");
        @Cleanup BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        assert keys != null;
        for (String key : keys) {
            String token = key.substring(LOGIN_USER_KEY.length());
            String text = token + "\n";
            bufferedWriter.write(text);
        }
    }



    @Test
    void testInsert(){

        for (int i = 0; i < 1000; i++) {
            String phone = "132"+RandomUtil.randomNumbers(8);
            User user = new User();
            user.setPhone(phone);
            user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
            // 2.保存用户
            userService.save(user);
        }
    }

}
