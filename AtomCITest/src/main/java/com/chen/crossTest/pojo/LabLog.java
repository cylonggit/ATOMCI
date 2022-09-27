package com.chen.crossTest.pojo;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class LabLog {
    Long id;
    Date testId;
    String labType;
    String startTime;
    String endTime;
    BigInteger gas;
    String tran;
    BigInteger blockNum;
    String remark;

    public LabLog(Date testId,String labType, String tran,String startTime, String endTime, BigInteger gas,  BigInteger blockNum, String remark) {
        this.testId=testId;
        this.labType = labType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gas = gas;
        this.tran = tran;
        this.blockNum = blockNum;
        this.remark = remark;
    }
}
