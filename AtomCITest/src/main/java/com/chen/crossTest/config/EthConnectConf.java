package com.chen.crossTest.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Properties;

public class EthConnectConf {
    public  String id;
    public  String CONTRACT_ADDR;

    public  String NFT_ADDR;
    public  String GETHURL;
    public    String PRIVATEKEY;
    public    String IINITMANPRIVATEKEY;
    // keystore文件
    public  String KEYSTORE;
    public  String PASSWORD;
    // GAS price
    public  BigInteger GAS_PRICE = BigInteger.valueOf(10L);
    // GAS limit
    public  BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);

    public int BLOCKINTERVAL;
    public int BLOCKSNEED;

    public EthConnectConf(String id, String path) {
        Properties myConfig= new Properties();;
        //直读文件式读属性值
        InputStream in = EthConnectConf.class.getResourceAsStream(path);//"/logChain.properties"
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        try {
            myConfig.load(reader);
            in.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.CONTRACT_ADDR = myConfig.getProperty("expContractAddr");
        this.NFT_ADDR = myConfig.getProperty("nftContractAddr");
        this.GETHURL= myConfig.getProperty("gethUrl");
        this.PRIVATEKEY=myConfig.getProperty("priKeyServer");
        this.IINITMANPRIVATEKEY=myConfig.getProperty("priKeyInitMan");
        this.BLOCKINTERVAL=Integer.parseInt(myConfig.getProperty("blockInterval"));
        this.BLOCKSNEED=Integer.parseInt(myConfig.getProperty("blocksNeed"));
        // keystore文件
        this.KEYSTORE = this.getClass().getClassLoader().getResource(myConfig.getProperty("keystorePath")).getPath();
        this.PASSWORD = myConfig.getProperty("keystorePWD");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCONTRACT_ADDR() {
        return CONTRACT_ADDR;
    }

    public void setCONTRACT_ADDR(String CONTRACT_ADDR) {
        this.CONTRACT_ADDR = CONTRACT_ADDR;
    }

    public String getGETHURL() {
        return GETHURL;
    }

    public void setGETHURL(String GETHURL) {
        this.GETHURL = GETHURL;
    }

    public String getPRIVATEKEY() {
        return PRIVATEKEY;
    }

    public void setPRIVATEKEY(String PRIVATEKEY) {
        this.PRIVATEKEY = PRIVATEKEY;
    }

    public String getKEYSTORE() {
        return KEYSTORE;
    }

    public void setKEYSTORE(String KEYSTORE) {
        this.KEYSTORE = KEYSTORE;
    }

    public  String getPASSWORD() {
        return PASSWORD;
    }

    public  void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public  BigInteger getGasPrice() {
        return GAS_PRICE;
    }

    public  void setGasPrice(BigInteger gasPrice) {
        this.GAS_PRICE = gasPrice;
    }

    public  BigInteger getGasLimit() {
        return GAS_LIMIT;
    }

    public  void setGasLimit(BigInteger gasLimit) {
        this.GAS_LIMIT = gasLimit;
    }

    public String getIINITMANPRIVATEKEY() {
        return IINITMANPRIVATEKEY;
    }

    public void setIINITMANPRIVATEKEY(String IINITMANPRIVATEKEY) {
        this.IINITMANPRIVATEKEY = IINITMANPRIVATEKEY;
    }

    public BigInteger getGAS_PRICE() {
        return GAS_PRICE;
    }

    public void setGAS_PRICE(BigInteger GAS_PRICE) {
        this.GAS_PRICE = GAS_PRICE;
    }

    public BigInteger getGAS_LIMIT() {
        return GAS_LIMIT;
    }

    public void setGAS_LIMIT(BigInteger GAS_LIMIT) {
        this.GAS_LIMIT = GAS_LIMIT;
    }

    public int getBLOCKINTERVAL() {
        return BLOCKINTERVAL;
    }

    public void setBLOCKINTERVAL(int BLOCKINTERVAL) {
        this.BLOCKINTERVAL = BLOCKINTERVAL;
    }

    public String getNFT_ADDR() {
        return NFT_ADDR;
    }

    public void setNFT_ADDR(String NFT_ADDR) {
        this.NFT_ADDR = NFT_ADDR;
    }

    public int getBLOCKSNEED() {
        return BLOCKSNEED;
    }

    public void setBLOCKSNEED(int BLOCKSNEED) {
        this.BLOCKSNEED = BLOCKSNEED;
    }
}
