package com.chen.crossTest.pojo;

import com.google.gson.Gson;
import lombok.Data;

import java.util.List;
@Data
public class EthLog {

    private String data;
    private List<String> topics;

    private String address;

    public EthLog(String data, List<String> topics,String address) {
        this.data = data;
        this.topics = topics;
        this.address = address;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String logJson = gson.toJson(this);
        return "["+logJson+"]";
    }
}
