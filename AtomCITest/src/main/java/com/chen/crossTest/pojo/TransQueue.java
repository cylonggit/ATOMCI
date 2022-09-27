package com.chen.crossTest.pojo;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Vector;

public class TransQueue {

    //list of event
    public static Vector<RemoteCall<TransactionReceipt>> TranList = new Vector<>();
    public static Vector<RemoteCall<TransactionReceipt>> TranListFail = new Vector<>();

    public static synchronized RemoteCall<TransactionReceipt> getAndDrop(){
        RemoteCall<TransactionReceipt> obj =null;
        int size =TranList.size();
        if(size>0) {
            obj = TranList.get(size - 1);
            TranList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized RemoteCall<TransactionReceipt> getAndDropFromBackup(){
        RemoteCall<TransactionReceipt> obj =null;
        int size =TranListFail.size();
        if(size>0) {
            obj = TranListFail.get(size - 1);
            TranListFail.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addElement(RemoteCall<TransactionReceipt> obj) {
        TranList.add(obj);
        return true;
    }

    public static synchronized boolean addElementBackup(RemoteCall<TransactionReceipt> obj) {
        TranListFail.add(obj);
        return true;
    }
}
