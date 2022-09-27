package com.chen.crossTest.service;

import com.chen.crossTest.utils.Base64;
import com.chen.crossTest.utils.RSAEncrypt;
import com.chen.crossTest.utils.Utils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class Parameters {
    //common
    public static String dAppAddr="0x65CE35093D037031DcE8F37B790D72a4E37Ae47C";
    public static String serverAddr="0xEd869df2e1a1A17A21F3ce8ADF437F868D53167C";
    public static String clientAddr="0xC412344eBCE1b06e6882C76f78e7cC96cF8141d3";
    public static String judger1Addr="0x960082d1b215929f426517EE8b0c34F8b6d4dAF3";
    public static String judger2Addr="0xD4F7e685AEEB0C0F697F9DfCA2f36bb113FB9d39";
    public static String judger3Addr="0x897a792665E48D1cAAED5cea50f396b8A77d6650";
    public static String judger4Addr="0x3B9eD344D2885d33dD8C3D2411411dF94ab8E824";
    public static String judger5Addr="0xeF77D5819f56bB7d20Cf99B951E90E074A1577cd";
    public static String initManAddr="0xd33cc2d4c8d85298e1fB36B26e0d0Bdb34572271";
    public static String hashKey ="abcd"; //Utils.getRandomString(10);
    public static String lockHashStr= Utils.getLockHashString(hashKey);
    public static byte[] lockHash=Utils.getLockHashBytes(hashKey);
    public static BigInteger fenny=new BigInteger("1000000000000000");
    public static String invokeId=  "abcdefghijklmn"; // here for test the invokeId is not randomly generated
    public static String invokeIdRandom=  Utils.getRandomString(10);


    //dApp 1~3
    public static String assetId=  "asset001";
    public static String numString="1";
    public static BigInteger num =BigInteger.valueOf(1);
    public static BigInteger initNum =BigInteger.valueOf(1000);

    //CrossChain NFT
    public static BigInteger nftTokenID=BigInteger.valueOf(12345678);
    public static String ownerLock="myownerlock";
    public static String serverMsp="x509::CN=User1@org2.example.com, OU=client, L=San Francisco, ST=California, C=US::CN=ca.org2.example.com, O=org2.example.com, L=San Francisco, ST=California, C=US";
    public static String ownerMsp="x509::CN=User1@org1.example.com, OU=client, L=San Francisco, ST=California, C=US::CN=ca.org1.example.com, O=org1.example.com, L=San Francisco, ST=California, C=US";
    public static BigInteger typeE2F=BigInteger.valueOf(1);
    public static BigInteger typeF2E=BigInteger.valueOf(2);
    static String filepath=ClassLoader.getSystemResource("keyPares").getPath();
    public static String pubKey;
    static {
        try {
            pubKey = RSAEncrypt.loadPublicKeyByFile(filepath+ "/publicKey.keystore");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFabricSign(String plainText) throws Exception {
        byte[] cipherData= RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath+ "/privateKey.keystore")),plainText.getBytes());
        String cipher= Base64.encode(cipherData);
        return cipher;
    }

    public static byte[] getEthSign(Credentials credentials,String plainText) throws Exception {
        byte[] hexMessage =plainText.getBytes();
        Sign.SignatureData signatureData = Sign.signMessage(hexMessage, credentials.getEcKeyPair());
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        byte byte_v = signatureData.getV();
        byte[] signMessage = new byte[r.length + s.length + 1];
        System.arraycopy(r, 0, signMessage, 0, r.length);
        System.arraycopy(s, 0, signMessage, r.length, s.length);
        signMessage[r.length + s.length] = byte_v;
        return signMessage;
    }

}
