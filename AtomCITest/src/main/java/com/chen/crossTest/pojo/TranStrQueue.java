package com.chen.crossTest.pojo;

import java.util.Vector;

public class TranStrQueue {

    //list of event
    public static Vector<String> TranList = new Vector<>();

    public static synchronized String getAndDrop(){
        String obj =null;
        int size =TranList.size();
        if(size>0) {
            obj = TranList.get(size - 1);
            TranList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addTran(String obj) {
        TranList.add(obj);
        return true;
    }


}
