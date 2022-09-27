package com.chen.crossTest.pojo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FabricProof {
    BigInteger blockNum;
    String blockHash;
    String proposals;
    List<String> endorsements;

    public String getProposals() {
        return proposals;
    }

    public void setProposals(String proposals) {
        this.proposals = proposals;
    }

    public List<String> getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(List<String> endorsements) {
        this.endorsements = endorsements;
    }

    public FabricProof() {

        endorsements = new ArrayList<String>();
    }

    public BigInteger getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(BigInteger blockNum) {
        this.blockNum = blockNum;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
}
