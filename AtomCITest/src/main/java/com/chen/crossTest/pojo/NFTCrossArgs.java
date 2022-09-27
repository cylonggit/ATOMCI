package com.chen.crossTest.pojo;

import lombok.Data;

import java.math.BigInteger;
@Data
public class NFTCrossArgs {
    BigInteger type;
    BigInteger tokenId;
    String ethAddr;
    String msp;
    byte[] _ethSign;
    String fabricSign;
    String invokeId;
    BigInteger sendValue;
    String from;
    String to;

}
