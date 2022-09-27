package com.chen.crossTest.utils;




import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Properties;



public class ConfigReader {

    //public static Credentials userCredentials =Credentials.create(Consts.PRIVATEKEY);

    public static Properties myConfig;
    //直读文件式读属性值
    static {
        myConfig = new Properties();
        InputStream in = com.chen.crossTest.utils.ConfigReader.class.getResourceAsStream("/eth.properties");
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        try {
            myConfig.load(reader);
            in.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    // 从my.propoties中直读合约地址
    public static String ETH_CONTRACT_ADDR1 = myConfig.getProperty("contrastAddr1");
    //public static String ETH_CONTRACT_ADDR2 = myConfig.getProperty("contrastAddr2");
    public static String GETHURL= myConfig.getProperty("gethUrl");
    public  static  String PRIVATEKEY1=myConfig.getProperty("priKey1");
    // keystore文件
    public  String KEYSTORE = this.getClass().getClassLoader().getResource(myConfig.getProperty("keystorePath")).getPath();
    public static String PASSWORD = myConfig.getProperty("keystorePWD");


    //经com.chen.common.config.ChenProperties类从application-dev.yml中读入属性值
    //public static ChenProperties config = SpringContextHolder.getApplicationContext().getBean(ChenProperties.class);
    //public static String GAOKAO_CONTRACT_ADDR =config.getGaokaoContrastAddr();
    //public static String RENZHENG_CONTRACT_ADDR =config.getRenzhengContrastAddr();




    // GAS price
    public static BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    // GAS limit
    public static BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);

}
