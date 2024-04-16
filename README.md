# kahol_dianping

#### 介绍
点评系统

#### 随便说两句

1.  nginx监听了8081端口，需要在全英文目录下运行。
2.  在Test文件中，testMultiLogin方法是为了压测准备的，即生成一千个token，存在【你的自定义目录】//tokens.txt，并存储在redis中，使用JMeter压测，需要用到该文件
3.  在application.yml文件中记得修改自己的redis，rabbitmq的配置。


