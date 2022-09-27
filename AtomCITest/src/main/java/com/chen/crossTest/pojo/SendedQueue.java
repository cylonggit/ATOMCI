package com.chen.crossTest.pojo;

import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.util.Vector;

public class SendedQueue {

    //list of event
    public static Vector<EthSendTransaction> SendedList = new Vector<>();
    public static Vector<String> TranHashList = new Vector<>();
    public static synchronized EthSendTransaction getAndDrop(){
        EthSendTransaction obj =null;
        int size =SendedList.size();
        if(size>0) {
            obj = SendedList.get(size - 1);
            SendedList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addTran(EthSendTransaction obj) {
        SendedList.add(obj);
        return true;
    }

    public static synchronized String getAndDropCrossTran(){
        String obj =null;
        int size =TranHashList.size();
        if(size>0) {
            obj = TranHashList.get(size - 1);
            TranHashList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addCrossTran(String obj) {
        TranHashList.add(obj);
        return true;
    }

}
