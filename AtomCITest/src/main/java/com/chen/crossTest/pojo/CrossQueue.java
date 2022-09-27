package com.chen.crossTest.pojo;

import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.util.Vector;

public class CrossQueue {



    public static Vector<String> CrossTranList = new Vector<>();
    public static synchronized String getAndDropCrossTranStr(){
        String obj =null;
        int size =CrossTranList.size();
        if(size>0) {
            obj = CrossTranList.get(size - 1);
            CrossTranList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addCrossTranStr(String obj) {
        CrossTranList.add(obj);
        return true;
    }

    public static Vector<EthSendTransaction> CrossSendedList = new Vector<>();
    public static synchronized EthSendTransaction getAndDropCrossSended(){
        EthSendTransaction obj =null;
        int size =CrossSendedList.size();
        if(size>0) {
            obj = CrossSendedList.get(size - 1);
            CrossSendedList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addTranSended(EthSendTransaction obj) {
        CrossSendedList.add(obj);
        return true;
    }
    public static Vector<String> CrossTranHashList = new Vector<>();
    public static synchronized String getAndDropCrossTranHash(){
        String obj =null;
        int size =CrossTranHashList.size();
        if(size>0) {
            obj = CrossTranHashList.get(size - 1);
            CrossTranHashList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addCrossTranHash(String obj) {
        CrossTranHashList.add(obj);
        return true;
    }

    public static Vector<String> CrossUnLockTranList = new Vector<>();
    public static synchronized String getAndDropCrossUnLockTranStr(){
        String obj =null;
        int size =CrossUnLockTranList.size();
        if(size>0) {
            obj = CrossUnLockTranList.get(size - 1);
            CrossUnLockTranList.remove(size - 1);
        }
        return obj;
    }

    public static synchronized boolean addCrossUnLockTranStr(String obj) {
        CrossUnLockTranList.add(obj);
        return true;
    }

}
