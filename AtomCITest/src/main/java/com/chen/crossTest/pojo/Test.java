package com.chen.crossTest.pojo;

import lombok.Data;

@Data
public class Test {
    Long id;
    String myname;
    String start;
    String end;

    public Test(String myname, String start, String end) {
        this.myname = myname;
        this.start = start;
        this.end = end;
    }
}
