package com.chen.crossTest;

import com.chen.crossTest.exp.detail.AtomicReadEth_Writte_Both_dApp1;
import com.chen.crossTest.exp.detail.AtomicReadFabric_Writte_Both_dApp2;
import com.chen.crossTest.exp.detail.AtomicWritte_Both_dApp3;
import com.chen.crossTest.exp.detail.CrossChainNFTTest;
import jnr.ffi.annotations.Out;

public class TestNFTCrossChain {

    public static void main(String[] args) throws Exception {
        int testRound=50;
        for(int i=1;i<=testRound;i++) {
            System.out.println();
            System.out.println();
            System.out.println("--------------------------NFT crossChain transfer test, --- round: " + i + " --------------------------------------------------");
            CrossChainNFTTest test = new CrossChainNFTTest();
            System.out.println("---------------------------------------------------   test transfer NFT from eth to fabric -------------------------------");
            test.testEth2Fabric();
            System.out.println("\n");
            System.out.println("\n");
            System.out.println("---------------------------------------------------   test transfer NFT back from fabric to eth -------------------------------");
            test.testFabric2Eth();
        }
    }
}
