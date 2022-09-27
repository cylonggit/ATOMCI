package com.chen.crossTest;

import com.chen.crossTest.exp.detail.AtomicReadEth_Writte_Both_dApp1;
import com.chen.crossTest.exp.detail.AtomicReadFabric_Writte_Both_dApp2;
import com.chen.crossTest.exp.detail.AtomicWritte_Both_dApp3;
import com.chen.crossTest.service.EthService;

public class TestDAPPs {
    public static void main(String[] args) throws Exception {
        //int testRound=50;
        int testRound=1;

        EthService.changeConfig("dApp1");
        for(int i=1;i<=testRound;i++){
            System.out.println();
            System.out.println();
            System.out.println("--------------------------AtomicReadEth_Writte_both_Aysnc --- round: "+i+" --------------------------------------------------");
            AtomicReadEth_Writte_Both_dApp1 test1=new AtomicReadEth_Writte_Both_dApp1();
            test1.testAtomicReadEth_Writte_both_Aysnc();
        }
        EthService.changeConfig("dApp2");
        for(int i=1;i<=testRound;i++){
            System.out.println();
            System.out.println();
            System.out.println("--------------------------AtomicReadFabric_Writte_both_Aysnc --- round: "+i+" --------------------------------------------------");
            AtomicReadFabric_Writte_Both_dApp2 test1=new AtomicReadFabric_Writte_Both_dApp2();
            test1.testAtomicReadFabric_Writte_both_Aysnc();
        }
        EthService.changeConfig("dApp3");
        for(int i=1;i<=testRound;i++){
            System.out.println();
            System.out.println();
            System.out.println("--------------------------AtomicWritte_2_Aysnc --- round: "+i+" --------------------------------------------------");
            AtomicWritte_Both_dApp3 test1=new AtomicWritte_Both_dApp3();
            test1.testAtomicWritte_2();

        }
    }
}
