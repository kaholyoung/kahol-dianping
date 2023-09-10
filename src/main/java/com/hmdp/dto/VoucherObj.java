package com.hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherObj implements Serializable {
    private Long userId;
    private Long voucherId;
    private Long orderId;
}
