package com.chen.crossTest.utils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Utils {
    //length用户要求产生字符串的长度
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static byte[] getRandomBytes(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString().getBytes();
    }
    //fabirc的lockHash是String类型
    public static String getLockHashString(String plaintext){
        //String lockHashStr = Hash.sha3(plaintext.getBytes()).toString();
        String lockHashStr=new String(Hash.sha3(plaintext.getBytes()),Charset.forName("ISO-8859-1"));
        return lockHashStr;
    }
    //以太坊的LockHash是byte[]类型
    public static byte[] getLockHashBytes(String plaintext){
        byte[] lockHash =  Hash.sha3(plaintext.getBytes());
        return lockHash;
    }


    //用log链合约支持的签名加密方式对数据签名，这里是以太坊方式，对数据签名即返回s\v\r
    public  static Map<String,String> getLogChainSupportSignData(Credentials credentials, String plainText){
        Map<String,String> signData=new HashMap<>();
        Sign.SignatureData signatureData = Sign.signMessage(plainText.getBytes(), credentials.getEcKeyPair());
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte byte_v = signatureData.getV();
        String rStr = new String(r, Charset.forName("ISO-8859-1"));
        String sStr = new String(s, Charset.forName("ISO-8859-1"));
        String vStr = new String(new byte[]{byte_v}, Charset.forName("ISO-8859-1"));
        signData.put("r",rStr);
        signData.put("s",sStr);
        signData.put("v",vStr);
        return signData;
    }

    public  static String getLogChainSupportSignStr(Credentials credentials, String plainText) {
        Sign.SignatureData signatureData = Sign.signMessage(plainText.getBytes(), credentials.getEcKeyPair());
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte byte_v = signatureData.getV();
        byte[] signMessage = new byte[r.length + s.length + 1];
        System.arraycopy(r, 0, signMessage, 0, r.length);
        System.arraycopy(s, 0, signMessage, r.length, s.length);
        signMessage[r.length + s.length] = byte_v;
        return new String(signMessage, Charset.forName("ISO-8859-1"));
    }
    public  static byte[] getLogChainSupportSignBytes(Credentials credentials, String plainText) {
        Sign.SignatureData signatureData = Sign.signMessage(plainText.getBytes(), credentials.getEcKeyPair());
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte byte_v = signatureData.getV();
        byte[] signMessage = new byte[r.length + s.length + 1];
        System.arraycopy(r, 0, signMessage, 0, r.length);
        System.arraycopy(s, 0, signMessage, r.length, s.length);
        signMessage[r.length + s.length] = byte_v;
        return signMessage;
    }

    public  static String printeTimeNow(){
        return new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date());
    }

    public static void priteTimenow(String mark){
        System.out.println("time is: "+ printeTimeNow()+" for: "+mark+"    ------------------------------");
    }

    public static void priteTime(String mark){
        Instant startTime = Instant.now();
        System.out.println("time stamp: "+ startTime.getEpochSecond()+" for: "+mark+"    ------------------------------");
    }
    public static void printTimeAndThread(String tag){
        String result=new StringJoiner("\t|\t")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.valueOf(Thread.currentThread().getId()))
                .add(Thread.currentThread().getName())
                .add(tag)
                .toString();
        System.out.println(result);
    }
}
