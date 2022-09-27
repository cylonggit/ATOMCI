package com.chen.crossTest.exp.detail;

import com.chen.crossTest.pojo.CallProof;
import com.chen.crossTest.pojo.ClientArgs;
import com.chen.crossTest.service.*;
import com.chen.crossTest.utils.Utils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


public class AtomicReadFabric_Writte_Both_dApp2 {
    public AtomicReadFabric_Writte_Both_dApp2() throws CertificateException, InvalidKeyException, IOException {
    }

    public void testAtomicReadFabric_Writte_both_Aysnc() throws Exception {
        /**
         Step 1:
             Operation 1: re = Function4(Inputs[0]);
         Step 2:
             Operation 2: Function2(Inputs[0],re[0]);
             Operation 3: Function3(Inputs[0],re[0]);
         */
        // ------------ init ---------------------------------------
        initAll(3);
        String labType = "atomic-ReadFabric_write_2";
        Date testId = new Date();
        //按照cross definition 定义好相关operation的操作变量与返回值
        CompletableFuture<CallProof> operation1 = null;
        CompletableFuture<CallProof> operation2 = null;
        CompletableFuture<CallProof> operation3 = null;
        CompletableFuture<CallProof> unoperation2 = null;
        CompletableFuture<CallProof> unoperation3 = null;
        //  --------------------------------------strat  test -------------------------------
        System.out.println("****************** testId: " + testId.toString() + " testType: " + labType + " *********************");

        //------------------client invoke entry function-----------------------------
        System.out.println("---------------------------  client invoke entry function -------------------------");
        CallProof<TransactionReceipt> rp=EthService.entryFunctionAsync(testId, labType, 0);

        //------------------ server get the cross-chain invoke information ---------------------------------
        ClientArgs args =EthTransactionTracer.traceCrossArgs(rp.getTransaction());
        System.out.println("The entry function's parameters are: "+args.toString());

        // ----------------- server read fabric business --------------------------------
        System.out.println("---------------------------  server read  -------------------------");
        operation1 = CompletableFuture.supplyAsync(
                () -> {
                    return FabricService.getNumberAtomic(testId, labType, 1);
                });
        //等待结果
        CallProof<byte[]> cp1 = operation1.join();

        if (cp1.getStatus().equals("SUCCESS")) {
            //get the read result
            BigInteger readResult=new BigInteger(new String(cp1.getResult(), "UTF8"));
            System.out.println("read result is: "+ String.valueOf(readResult));
            // ----------------server do cross-chain service-----------------------------
            System.out.println("---------------------------  server lock_do  -------------------------");
            operation2 = CompletableFuture.supplyAsync(
                    () -> {
                        return EthService.incressAsset_lock_do(testId, labType, 2,readResult,args);
                    });

            operation3 = CompletableFuture.supplyAsync(
                    () -> {
                        return FabricService.decressAsset_lock_do(testId, labType, 3, readResult,args);
                    });

            Utils.priteTime("waiting lock_do ...");
            //等待do结果
            CallProof cp2 = operation2.join();
            CallProof cp3 = operation3.join();
            if (cp2.getStatus().equals("SUCCESS") && cp3.getStatus().equals("SUCCESS")) { //do成功，准备执行unlock)
                System.out.println("---------------------------  server unlock  -------------------------");
                unoperation2 = CompletableFuture.supplyAsync(
                        () -> {
                            return EthService.incressAsset_unlock(testId, labType, 2,readResult,args);
                        });
                unoperation3 = CompletableFuture.supplyAsync(
                        () -> {
                            return FabricService.decressAsset_unlock(testId, labType, 3,readResult,args);
                        });

                System.out.println("waiting  unlock ....");
                CallProof un2 = unoperation2.join();
                CallProof un3 = unoperation3.join();
                System.out.println("finish all unlock!");

                System.out.println("---------------------------  server insert proofs  -------------------------");
                if (cp2.getStatus().equals("SUCCESS") && cp3.getStatus().equals("SUCCESS") && un2.getStatus().equals("SUCCESS") && un3.getStatus().equals("SUCCESS")) {
                    CompletableFuture<CallProof> insertt1 = null;
                    CompletableFuture<CallProof> insertt2 = null;
                    CompletableFuture<CallProof> insertt3 = null;

                    insertt1 = CompletableFuture.supplyAsync(
                            () -> {
                                // -----------------   server insert operation 1 proff --------------------------------------------
                                String templ = "{\"Operation\":1,\"chainId\":\"MyFabric-1\",\"read\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"}}";
                                //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                                templ = templ.replace("$blockNum1$", cp1.getBlockNumber().toString());
                                templ = templ.replace("$tranHash1$", cp1.getTransaction());
                                System.out.println(templ);
                                try {
                                    return EthService.insertProofAsync(testId, labType, 1, templ, false, 0,EthService.becf.getServiceAddr());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                    insertt2 = CompletableFuture.supplyAsync(
                            () -> {
                                // -----------------   server insert operation 1 proff --------------------------------------------
                                //Utils.priteTime("start insert proof of operation 2");
                                String templ = "{\"Operation\":2,\"chainId\":\"MyEthereum-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                                //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                                templ = templ.replace("$blockNum1$", cp2.getBlockNumber().toString());
                                templ = templ.replace("$tranHash1$", cp2.getTransaction());
                                templ = templ.replace("$blockNum2$", un2.getBlockNumber().toString());
                                templ = templ.replace("$tranHash2$", un2.getTransaction());
                                System.out.println(templ);
                                try {
                                    return EthService.insertProofAsync(testId, labType, 2, templ, false, 1,EthService.becf.getServiceAddr());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                    insertt3 = CompletableFuture.supplyAsync(
                            () -> {
                                // -----------------   server insert operation 2 proff --------------------------------------------
                                //Utils.priteTime("start insert proof of operation 3");
                                String templ = "{\"Operation\":3,\"chainId\":\"MyFabric-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                                //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                                templ = templ.replace("$blockNum1$", cp3.getBlockNumber().toString());
                                templ = templ.replace("$tranHash1$", cp3.getTransaction());
                                templ = templ.replace("$blockNum2$", un3.getBlockNumber().toString());
                                templ = templ.replace("$tranHash2$", un3.getTransaction());
                                System.out.println(templ);
                                try {
                                    return EthService.insertProofAsync(testId, labType, 3, templ, false, 2,EthService.becf.getServiceAddr());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                    CallProof in1 = insertt1.join();
                    CallProof in2 = insertt2.join();
                    CallProof in3 = insertt3.join();
                    System.out.println("finish all insert proofs!");

                }


                // -----------------   judgers audit --------------------------------------------
                System.out.println("---------------------------  judgers audit -------------------------");
                CompletableFuture<CallProof> vote1 = null;
                CompletableFuture<CallProof> vote2 = null;
                CompletableFuture<CallProof> vote3 = null;
                vote1 = CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return EthService.auditAsync(testId, labType, 1, true, 0,EthService.becf.getServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                vote2 = CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return EthService.auditAsync(testId, labType, 2, true, 0,EthService.becf.getServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                vote3 = CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return EthService.auditAsync(testId, labType, 3, true, 0,EthService.becf.getServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });

                System.out.println("start waiting audit ....");
                if (vote1 != null) {
                    System.out.println("audit 1 result: " + vote1.join().getStatus());
                }
                if (vote2 != null) {
                    System.out.println("audit 2 result: " + vote2.join().getStatus());
                }
                if (vote3 != null) {
                    System.out.println("audit 3 result: " + vote3.join().getStatus());
                }
                Utils.priteTime("finish all audits!");
            } else { //有执行不成功的
                if (cp2.getStatus().equals("SUCCESS")) {  // undo_unlock
                    System.out.println("todo---------------undo_unlock in operation 2");
                } else {
                    System.out.println("todo---------------unlock in operation 2");
                }
                if (cp3.getStatus().equals("SUCCESS")) {  // undo_unlock
                    System.out.println("todo---------------undo_unlock in operation 3");
                } else {
                    System.out.println("todo---------------unlock in operation 3");
                }
            }
        }
    }
    public void initAll(int totalOperationNum) throws Exception {
        System.out.println("---------------------------  initing data -------------------------");
        EthService.initBasechainAsync(totalOperationNum);
        EthService.initEthbusinessChain();
        FabricService.initFabricBusinessChain();
    }
}
