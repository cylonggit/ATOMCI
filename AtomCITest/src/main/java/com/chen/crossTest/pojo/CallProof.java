package com.chen.crossTest.pojo;



import java.math.BigInteger;

public class CallProof<T>{
    private T result;
    private ChainType chainType;
    private String transaction;
    private BigInteger transactionIndex;
    private String blockHash;
    private BigInteger blockNumber;
    private String root;
    private String contract;
    private String serviceID;
    private String event;
    private String channel;
    private String proof;
    private BigInteger logBlockNum;
    private String logTransaction;
    private String status;
    private String msg;
    private BigInteger gas;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public BigInteger getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(BigInteger transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ChainType getChainType() {
        return chainType;
    }

    public void setChainType(ChainType chainType) {
        this.chainType = chainType;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public BigInteger getLogBlockNum() {
        return logBlockNum;
    }

    public void setLogBlockNum(BigInteger logBlockNum) {
        this.logBlockNum = logBlockNum;
    }

    public String getLogTransaction() {
        return logTransaction;
    }

    public void setLogTransaction(String logTransaction) {
        this.logTransaction = logTransaction;
    }

    public BigInteger getGas() {
        return gas;
    }

    public void setGas(BigInteger gas) {
        this.gas = gas;
    }
}
