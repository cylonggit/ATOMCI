package com.chen.crossTest.exp.detail;

import com.chen.crossTest.pojo.CallProof;
import com.chen.crossTest.pojo.ClientArgs;
import com.chen.crossTest.service.EthService;
import com.chen.crossTest.service.EthTransactionTracer;
import com.chen.crossTest.service.FabricService;
import com.chen.crossTest.utils.Utils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class AtomicReadEthWritteFabric {
    public AtomicReadEthWritteFabric() throws CertificateException, InvalidKeyException, IOException {
    }


    public void testAtomicReadEthWriteFabric() throws Exception {
        /**
         * Step 1:
         * Operation 1: num=getAsset(clientInput(2))
         * Step 2:
         * Operation 2: decressAsset(num[0])
         */
        // ------------ init ---------------------------------------
        initAll(2);
        String labType = "atomic-ethRead-fabricWrite";
        Date testId = new Date();
        //按照cross definition 定义好相关operation的操作变量与返回值
        CompletableFuture<CallProof> operation1 = null;
        CompletableFuture<CallProof> operation2 = null;
        CompletableFuture<CallProof> unoperation2 = null;
        //  --------------------------------------strat  test -------------------------------
        System.out.println("****************** testId: " + testId.toString() + " testType: "+labType+" *********************");

        //------------------client invoke entry function-----------------------------
        System.out.println("---------------------------  client invoke entry function -------------------------");
        CallProof<TransactionReceipt> rp=EthService.entryFunctionAsync(testId, labType,0);

        //------------------ server get the cross-chain invoke information ---------------------------------
        ClientArgs args =EthTransactionTracer.traceCrossArgs(rp.getTransaction());
        System.out.println("The entry function's parameters are: "+args.toString());

        // ----------------server do cross-chain service-----------------------------
        System.out.println("---------------------------  server read  -------------------------");
        operation1 = CompletableFuture.supplyAsync(
                () -> {
                    return EthService.getNumberAtomic(testId, labType, 1,args);
                });
        //等待结果
        CallProof cp1 = operation1.join();

        if (cp1.getStatus().equals("SUCCESS")) { //read成功，准备执行do
            //get the read result
            BigInteger readResult= EthTransactionTracer.traceAtomicReadResult(cp1.getTransaction());
            System.out.println("read result is: "+String.valueOf(readResult));

            System.out.println("---------------------------  server lock_do  -------------------------");
            operation2 = CompletableFuture.supplyAsync(
                    () -> {
                        return FabricService.decressAsset_lock_do(testId, labType, 2,readResult,args);
                    });

            Utils.priteTime("waiting lock_do ...");
            //等待do结果
            CallProof cp2 = operation2.join();
            if (cp2.getStatus().equals("SUCCESS")) { //do成功，准备执行unlock)
                System.out.println("---------------------------  server unlock  -------------------------");
                unoperation2 = CompletableFuture.supplyAsync(
                        () -> {
                            return FabricService.decressAsset_unlock(testId, labType, 2,readResult,args);
                        });

                System.out.println("waiting  unlock ....");
                CallProof un2 = unoperation2.join();
                System.out.println("finish all unlock!");

                System.out.println("---------------------------  server insert proofs  -------------------------");
                if (cp1.getStatus().equals("SUCCESS") && cp2.getStatus().equals("SUCCESS") && un2.getStatus().equals("SUCCESS")) {

                    CompletableFuture<CallProof> insertt1 = null;
                    CompletableFuture<CallProof> insertt2 = null;
                    insertt1 = CompletableFuture.supplyAsync(
                            () -> {
                                // -----------------   server insert operation 1 proff --------------------------------------------
                                String templ = "{\"Operation\":1,\"chainId\":\"MyEthereum-1\",\"read\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"}}";
                                //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                                templ = templ.replace("$blockNum1$", cp1.getBlockNumber().toString());
                                templ = templ.replace("$tranHash1$", cp1.getTransaction());
                                System.out.println(templ);
                                try {
                                    return EthService.insertProofAsync(testId, labType, 1, templ, false,0,EthService.becf.getServiceAddr());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                    insertt2 = CompletableFuture.supplyAsync(
                            () -> {
                                // -----------------   server insert operation 2 proff --------------------------------------------
                                Utils.priteTime("start insert proof of operation 2");
                                String templ = "{\"Operation\":2,\"chainId\":\"MyEthereum-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                                //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                                templ = templ.replace("$blockNum1$", cp2.getBlockNumber().toString());
                                templ = templ.replace("$tranHash1$", cp2.getTransaction());
                                templ = templ.replace("$blockNum2$", un2.getBlockNumber().toString());
                                templ = templ.replace("$tranHash2$", un2.getTransaction());
                                System.out.println(templ);
                                try {
                                    return EthService.insertProofAsync(testId, labType, 2, templ, false,1,EthService.becf.getServiceAddr());
                                }  catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                    CallProof in1 = insertt1.join();
                    CallProof in2 = insertt2.join();
                    System.out.println("finish all insert proofs!");

                }
            }

            // -----------------   judgers audit --------------------------------------------
            System.out.println("---------------------------  judgers audit -------------------------");
            CompletableFuture<CallProof> vote1 = null;
            CompletableFuture<CallProof> vote2 = null;
            CompletableFuture<CallProof> vote3 = null;
            vote1 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 1, true,0,EthService.becf.getServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote2 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 2, true,0,EthService.becf.getServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote3 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 3, true,0,EthService.becf.getServiceAddr());
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

        }

    }


    public void initAll(int totalOperationNum) throws Exception {
        System.out.println("---------------------------  initing data -------------------------");
        //EthService.initBasechain(totalOperationNum);
        EthService.initBasechainAsync(totalOperationNum);
        EthService.initEthbusinessChain();
        FabricService.initFabricBusinessChain();
    }




}
