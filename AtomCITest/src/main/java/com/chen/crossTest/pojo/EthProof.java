package com.chen.crossTest.pojo;

import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public class EthProof {
    private String transactionHash;
    private BigInteger transactionIndex;
    private String blockHash;
    private BigInteger blockNumber;
    private BigInteger cumulativeGasUsed;
    private BigInteger gasUsed;
    private String contractAddress;
    private String root;
    private String status;
    private String from;
    private String to;
    private List<Log> logs;
    //private String logsBloom;

    public EthProof(TransactionReceipt t) {
        this.transactionHash = t.getTransactionHash();
        this.transactionIndex = t.getTransactionIndex();
        this.blockHash = t.getBlockHash();
        this.blockNumber = t.getBlockNumber();
        this.cumulativeGasUsed = t.getCumulativeGasUsed();
        this.gasUsed = t.getGasUsed();
        this.contractAddress = t.getContractAddress();
        this.root = t.getRoot();
        this.status = t.getStatus();
        this.from = t.getFrom();
        this.to = t.getTo();
        this.logs = t.getLogs();
        //this.logsBloom = t.getLogsBloom();
    }


    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public BigInteger getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(BigInteger transactionIndex) {
        this.transactionIndex = transactionIndex;
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

    public BigInteger getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setCumulativeGasUsed(BigInteger cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }



    @Override
    public String toString() {
        return "EthProof{" +
                "transactionHash='" + transactionHash + '\'' +
                ", transactionIndex='" + transactionIndex + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", blockNumber='" + blockNumber + '\'' +
                ", cumulativeGasUsed='" + cumulativeGasUsed + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", root='" + root + '\'' +
                ", status='" + status + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", logs=" + logs +'\'' +
                '}';
    }
}
