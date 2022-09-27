package com.chen.crossTest.pojo;

import lombok.Data;

import java.math.BigInteger;
@Data
public class ClientArgs {
    String invokeId;
    String assetId;
    String num;
    BigInteger sendValue;
    String from;
    String to;
}
