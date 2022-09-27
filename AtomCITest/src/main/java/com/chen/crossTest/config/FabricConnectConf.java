package com.chen.crossTest.config;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class FabricConnectConf {
    public String id;
    public X509Certificate certificate;
    public PrivateKey privateKey;
    public GatewayImpl.Builder builder;
    public Gateway gateway;
    public String channelName;
    public String chainCodeName;

    public FabricConnectConf(X509Certificate certificate, PrivateKey privateKey, Gateway gateway) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.gateway=gateway;
    }

    public FabricConnectConf(String id, Path NETWORK_CONFIG_PATH, Path credentialPath, String channelName, String configPath) throws IOException, CertificateException, InvalidKeyException {
        //使用org1中的user1初始化一个网关wallet账户用于连接网络
        Wallet wallet = Wallets.newInMemoryWallet();
        //组织?用户1的签名证书
        Path certificatePath=null;
        if(id.equals("org1user1")) {
             certificatePath = credentialPath.resolve(Paths.get("signcerts", "User1@org1.example.com-cert.pem"));
        }else {
             certificatePath = credentialPath.resolve(Paths.get("signcerts", "User1@org2.example.com-cert.pem"));
        }
        this.certificate = readX509Certificate(certificatePath);
        //组织?用户1的私钥
        Path privateKeyPath = credentialPath.resolve(Paths.get("keystore", "priv_sk"));
        this.privateKey = getPrivateKey(privateKeyPath);
        //账户对象都可以存放到wallet里面，方便存取
        if(id.equals("org1user1")) {
            wallet.put("user", Identities.newX509Identity("Org1MSP",certificate,privateKey));
        }else {
            wallet.put("user", Identities.newX509Identity("Org2MSP",certificate,privateKey));
        }

        //根据connection-org1.json 获取Fabric网络连接对象
        this.builder = (GatewayImpl.Builder) Gateway.createBuilder();
        this.builder.identity(wallet, "user").networkConfig(NETWORK_CONFIG_PATH);
        this.channelName=channelName;


        //this.chainCodeName=chainCodeName;
        Properties myConfig= new Properties();;
        //直读文件式读属性值
        InputStream in = FabricConnectConf.class.getResourceAsStream(configPath);//"/Fabric.properties"
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        try {
            myConfig.load(reader);
            in.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.chainCodeName = myConfig.getProperty("chainCodeName");
    }
    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public GatewayImpl.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(GatewayImpl.Builder builder) {
        this.builder = builder;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChainCodeName() {
        return chainCodeName;
    }

    public void setChainCodeName(String chainCodeName) {
        this.chainCodeName = chainCodeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
