package org.hyperledger.fabric.samples.events.notuse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CrossEvent {
    private String invokeID;
    private String invokeType;
    private String verifyType;
    private String identityType;
    private String identityID;
    private String account;
    private String addrID;
    private String chainID;
    private String chainType;
    private String chainIP;
    private String chainPort;
    private String contract;
    private String method;
    private String methodType;
    private String args;
    private String argsType;
    private String returnType;
    private String callBackID;
    private String sourceMethod;
    private String cryptoHashKey;
    private String sourceArgs;
    private String sourceArgsType;

    public CrossEvent(String invokeID, String invokeType, String verifyType, String identityType, String identityID, String account, String addrID, String chainID, String chainType, String chainIP, String chainPort, String contract, String method, String methodType, String args, String argsType, String returnType, String callBackID, String sourceMethod, String cryptoHashKey, String sourceArgs, String sourceArgsType) {
        this.invokeID = invokeID;
        this.invokeType = invokeType;
        this.verifyType = verifyType;
        this.identityType = identityType;
        this.identityID = identityID;
        this.account = account;
        this.addrID = addrID;
        this.chainID = chainID;
        this.chainType = chainType;
        this.chainIP = chainIP;
        this.chainPort = chainPort;
        this.contract = contract;
        this.method = method;
        this.methodType = methodType;
        this.args = args;
        this.argsType = argsType;
        this.returnType = returnType;
        this.callBackID = callBackID;
        this.sourceMethod = sourceMethod;
        this.cryptoHashKey = cryptoHashKey;
        this.sourceArgs = sourceArgs;
        this.sourceArgsType = sourceArgsType;
    }

    public String getSourceArgs() {
        return sourceArgs;
    }

    public void setSourceArgs(String sourceArgs) {
        this.sourceArgs = sourceArgs;
    }

    public String getSourceArgsType() {
        return sourceArgsType;
    }

    public void setSourceArgsType(String sourceArgsType) {
        this.sourceArgsType = sourceArgsType;
    }

    public String getCryptoHashKey() {
        return cryptoHashKey;
    }

    public void setCryptoHashKey(String cryptoHashKey) {
        this.cryptoHashKey = cryptoHashKey;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String sign) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("invokeID", invokeID);
        tMap.put("invokeType",  invokeType);
        tMap.put("verifyType",  verifyType);
        tMap.put("identityType",  identityType);
        tMap.put("identityID",  identityID);
        tMap.put("account",  account);
        tMap.put("addrID",  addrID);
        tMap.put("chainID",  chainID);
        tMap.put("chainType",  chainType);
        tMap.put("chainIP",  chainIP);
        tMap.put("chainPort",  chainPort);
        tMap.put("contract",  contract);
        tMap.put("method",  method);
        tMap.put("methodType",  methodType);
        tMap.put("args",  args);
        tMap.put("argsType",  argsType);
        tMap.put("returnType",  returnType);
        tMap.put("callBackID",  callBackID);
        tMap.put("sourceMethod",  sourceMethod);
        tMap.put("cryptoHashKey",  cryptoHashKey);
        tMap.put("sourceArgs",  sourceArgs);
        tMap.put("sourceArgsType",  sourceArgsType);
        //tMap.put("identityType", Integer.toString(size));
        if (sign != null && sign.length() > 0) {
            tMap.put("sign", new JSONObject(sign));
        }
        return new JSONObject(tMap).toString();
    }

    public String getArgsType() {
        return argsType;
    }

    public void setArgsType(String argsType) {
        this.argsType = argsType;
    }

    public String getAddrID() {
        return addrID;
    }

    public void setAddrID(String addrID) {
        this.addrID = addrID;
    }

    public String getInvokeID() {
        return invokeID;
    }

    public void setInvokeID(String invokeID) {
        this.invokeID = invokeID;
    }

    public String getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(String invokeType) {
        this.invokeType = invokeType;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getChainIP() {
        return chainIP;
    }

    public void setChainIP(String chainIP) {
        this.chainIP = chainIP;
    }

    public String getChainPort() {
        return chainPort;
    }

    public void setChainPort(String chainPort) {
        this.chainPort = chainPort;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getCallBackID() {
        return callBackID;
    }

    public void setCallBackID(String callBackID) {
        this.callBackID = callBackID;
    }
}
