package com.chen.crossTest.config;

import lombok.Data;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Properties;

@Data
public class BaseChainEthConf {
    public  String id;

    public  String GETHURL;

    public  String communityAddr;
    public  String serviceAddr;
    public  String entryAddr;

    public  String serviceAddrDApp1;
    public  String entryAddrDApp1;
    public  String serviceAddrDApp2;
    public  String entryAddrDApp2;

    public  String serviceAddrDApp3;
    public  String entryAddrDApp3;

    public  String NFTServiceAddr;
    public  String NFTEntryAddr;

    public  String priKeyServer;
    public  String priKeyDApp;
    public  String priKeyJudger1;
    public  String priKeyJudger2;
    public  String priKeyJudger3;
    public  String priKeyJudger4;
    public  String priKeyJudger5;
    public  String priKeyClient;
    public  String priKeyInitMan;

//    // keystore文件
//    public  String KEYSTORE;
//    public  String PASSWORD;
    // GAS price
    //public BigInteger GAS_PRICE = BigInteger.valueOf(10L);
    public BigInteger GAS_PRICE = Contract.GAS_PRICE;
    // GAS limit
   // public  BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);
    public  BigInteger GAS_LIMIT =Contract.GAS_LIMIT;
    public BaseChainEthConf(String id, String path) {
        Properties myConfig= new Properties();;
        //直读文件式读属性值
        InputStream in = BaseChainEthConf.class.getResourceAsStream(path);//"/logChain.properties"
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        try {
            myConfig.load(reader);
            in.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.communityAddr = myConfig.getProperty("community");
        this.serviceAddr = myConfig.getProperty("serviceDApp1");
        this.entryAddr = myConfig.getProperty("entryDApp1");
        this.serviceAddrDApp1 = myConfig.getProperty("serviceDApp1");
        this.entryAddrDApp1 = myConfig.getProperty("entryDApp1");
        this.serviceAddrDApp2 = myConfig.getProperty("serviceDApp2");
        this.entryAddrDApp2 = myConfig.getProperty("entryDApp2");
        this.serviceAddrDApp3 = myConfig.getProperty("serviceDApp3");
        this.entryAddrDApp3 = myConfig.getProperty("entryDApp3");
        this.NFTServiceAddr = myConfig.getProperty("ServiceNFT");
        this.NFTEntryAddr = myConfig.getProperty("EntryNFT");
        this.GETHURL= myConfig.getProperty("gethUrl");
        this.priKeyServer=myConfig.getProperty("priKeyServer");
        this.priKeyDApp=myConfig.getProperty("priKeyDApp");
        this.priKeyJudger1=myConfig.getProperty("priKeyJudger1");
        this.priKeyJudger2=myConfig.getProperty("priKeyJudger2");
        this.priKeyJudger3=myConfig.getProperty("priKeyJudger3");
        this.priKeyJudger4=myConfig.getProperty("priKeyJudger4");
        this.priKeyJudger5=myConfig.getProperty("priKeyJudger5");
        this.priKeyClient=myConfig.getProperty("priKeyClient");
        this.priKeyInitMan=myConfig.getProperty("priKeyInitMan");
        // keystore文件
//        this.KEYSTORE = this.getClass().getClassLoader().getResource(myConfig.getProperty("keystorePath")).getPath();
//        this.PASSWORD = myConfig.getProperty("keystorePWD");
    }

}
