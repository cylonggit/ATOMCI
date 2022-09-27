package com.chen.crossTest.exp.detail;

import com.chen.crossTest.pojo.CallProof;
import com.chen.crossTest.pojo.NFTCrossArgs;
import com.chen.crossTest.service.EthService;
import com.chen.crossTest.service.EthTransactionTracer;
import com.chen.crossTest.service.FabricService;
import com.chen.crossTest.service.Parameters;
import com.chen.crossTest.utils.Utils;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CrossChainNFTTest {
    public CrossChainNFTTest() throws CertificateException, InvalidKeyException, IOException {
    }

    public void testEth2Fabric() throws Exception {
        /**
         Step 1:
            Operation 1: Function1(Inputs[0],Inputs[1],Inputs[2],Inputs[3],Inputs[4],Inputs[6]);
            Operation 2: Function2(Inputs[0],Inputs[1],Inputs[2],Inputs[3],Inputs[5],Inputs[6]);
         */
        // ------------ init ---------------------------------------
        initAll(2);
        String labType = "nft-cross-chain-e2f";
        Date testId = new Date();
        //按照cross definition 定义好相关operation的操作变量与返回值
        CompletableFuture<CallProof> operation1 = null;
        CompletableFuture<CallProof> operation2 = null;
        CompletableFuture<CallProof> unoperation1 = null;
        CompletableFuture<CallProof> unoperation2 = null;
        //  --------------------------------------strat  test -------------------------------
        System.out.println("****************** testId: " + testId.toString() + " testType: " + labType + " *********************");

        //------------------client invoke entry function-----------------------------
        System.out.println("---------------------------  client invoke entry function -------------------------");
        CallProof<TransactionReceipt> rp=EthService.NFTEntryFunctionAsync(testId, labType, 0,1); // e2f

        // ----------------server get the cross-chain information-----------------------------
        NFTCrossArgs args=EthTransactionTracer.traceNFTCrossArgs(rp.getTransaction());
        System.out.println("server get cross-chain innvocation args: "+args.toString());

        // ----------------server do cross-chain service-----------------------------
        System.out.println("---------------------------  server lock_do  -------------------------");


        operation1 = CompletableFuture.supplyAsync(
                () -> {
                    return EthService.crossChainTransfer_lock_do(testId, labType, 1, args);
                });

        operation2 = CompletableFuture.supplyAsync(
                () -> {
                    return FabricService.crossChainTransfer_lock_do(testId, labType, 2, args);
                });

        Utils.priteTime("waiting lock_do ...");
        //等待do结果
        CallProof cp1 = operation1.join();
        CallProof cp2 = operation2.join();
        if (cp1.getStatus().equals("SUCCESS") && cp2.getStatus().equals("SUCCESS")) { //do成功，准备执行unlock)
            System.out.println("---------------------------  server unlock  -------------------------");
            unoperation1 = CompletableFuture.supplyAsync(
                    () -> {
                        return EthService.crossChainTransfer_unlock(testId, labType, 1,args);
                    });
            unoperation2 = CompletableFuture.supplyAsync(
                    () -> {
                        return FabricService.crossChainTransfer_unlock(testId, labType, 2,args);
                    });

            System.out.println("waiting  unlock ....");
            CallProof un1 = unoperation1.join();
            CallProof un2 = unoperation2.join();
            System.out.println("finish all unlock!");

            System.out.println("---------------------------  server insert proofs  -------------------------");
            if (cp1.getStatus().equals("SUCCESS") && cp2.getStatus().equals("SUCCESS")  && un1.getStatus().equals("SUCCESS") && un2.getStatus().equals("SUCCESS")) {

                CompletableFuture<CallProof> insertt1 = null;
                CompletableFuture<CallProof> insertt2 = null;
                insertt1 = CompletableFuture.supplyAsync(
                        () -> {
                            // -----------------   server insert operation 1 proff --------------------------------------------
                            //Utils.priteTime("start insert proof of operation 1");
                            String templ = "{\"Operation\":1,\"chainId\":\"MyEthereum-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                            //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                            templ = templ.replace("$blockNum1$", cp1.getBlockNumber().toString());
                            templ = templ.replace("$tranHash1$", cp1.getTransaction());
                            templ = templ.replace("$blockNum2$", un1.getBlockNumber().toString());
                            templ = templ.replace("$tranHash2$", un1.getTransaction());
                            System.out.println(templ);
                            try {
                                return EthService.insertProofAsync(testId, labType, 1, templ, false, 0,EthService.becf.getNFTServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        });
                insertt2 = CompletableFuture.supplyAsync(
                        () -> {
                            // -----------------   server insert operation 2 proff --------------------------------------------
                           // Utils.priteTime("start insert proof of operation 2");
                            String templ = "{\"Operation\":2,\"chainId\":\"MyFabric-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                            //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                            templ = templ.replace("$blockNum1$", cp2.getBlockNumber().toString());
                            templ = templ.replace("$tranHash1$", cp2.getTransaction());
                            templ = templ.replace("$blockNum2$", un2.getBlockNumber().toString());
                            templ = templ.replace("$tranHash2$", un2.getTransaction());
                            System.out.println(templ);
                            try {
                                return EthService.insertProofAsync(testId, labType, 2, templ, false, 1,EthService.becf.getNFTServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        });
                CallProof in1 = insertt1.join();
                CallProof in2 = insertt2.join();
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
                            return EthService.auditAsync(testId, labType, 1, true, 0,EthService.becf.getNFTServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote2 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 2, true, 0,EthService.becf.getNFTServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote3 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 3, true, 0,EthService.becf.getNFTServiceAddr());
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
        }else{ //有执行不成功的
            if(cp1.getStatus().equals("SUCCESS")){  // undo_unlock
                System.out.println("todo---------------undo_unlock in operation 1");
            }else{
                System.out.println("todo---------------unlock in operation 1");
            }
            if(cp2.getStatus().equals("SUCCESS")){  // undo_unlock
                System.out.println("todo---------------undo_unlock in operation 2");
            }else{
                System.out.println("todo---------------unlock in operation 2");
            }
        }

    }
    public void initAll(int totalOperationNum) throws Exception {
        System.out.println("---------------------------  initing data -------------------------");
        //EthService.initBasechain(totalOperationNum);
        EthService.initBasechainNFTAsync(totalOperationNum);
        EthService.initNFTEthbusinessChain();
        FabricService.initNFTFabricBusinessChain(String.valueOf(1));
    }

    public void testFabric2Eth() throws Exception {
        /**
         Step 1:
         Operation 1: Function1(Inputs[0],Inputs[1],Inputs[2],Inputs[3],Inputs[4],Inputs[6]);
         Operation 2: Function2(Inputs[0],Inputs[1],Inputs[2],Inputs[3],Inputs[5],Inputs[6]);
         */
        // ------------ init ---------------------------------------
        EthService.initBasechainNFTAsync(2);
        FabricService.initNFTFabricBusinessChain(String.valueOf(2));
        String labType = "nft-cross-chain-f2e";
        Date testId = new Date();
        //按照cross definition 定义好相关operation的操作变量与返回值
        CompletableFuture<CallProof> operation1 = null;
        CompletableFuture<CallProof> operation2 = null;
        CompletableFuture<CallProof> unoperation1 = null;
        CompletableFuture<CallProof> unoperation2 = null;
        //  --------------------------------------strat  test -------------------------------
        System.out.println("****************** testId: " + testId.toString() + " testType: " + labType + " *********************");

        //------------------client invoke entry function-----------------------------
        System.out.println("---------------------------  client invoke entry function -------------------------");
        CallProof<TransactionReceipt> rp=EthService.NFTEntryFunctionAsync(testId, labType, 0,2); // e2f
        // ----------------server do cross-chain service-----------------------------

        System.out.println("---------------------------  server lock_do  -------------------------");
        //get the entry information
        NFTCrossArgs args=EthTransactionTracer.traceNFTCrossArgs(rp.getTransaction());

        System.out.println("server get cross-chain innvocation args: "+args.toString());

        operation1 = CompletableFuture.supplyAsync(
                () -> {
                    return EthService.crossChainTransfer_lock_do(testId, labType, 1, args);
                });

        operation2 = CompletableFuture.supplyAsync(
                () -> {
                    return FabricService.crossChainTransfer_lock_do(testId, labType, 2, args);
                });

        Utils.priteTime("waiting lock_do ...");
        //等待do结果
        CallProof cp1 = operation1.join();
        CallProof cp2 = operation2.join();
        if (cp1.getStatus().equals("SUCCESS") && cp2.getStatus().equals("SUCCESS")) { //do成功，准备执行unlock)
            System.out.println("---------------------------  server unlock  -------------------------");
            unoperation1 = CompletableFuture.supplyAsync(
                    () -> {
                        return EthService.crossChainTransfer_unlock(testId, labType, 1,args);
                    });
            unoperation2 = CompletableFuture.supplyAsync(
                    () -> {
                        return FabricService.crossChainTransfer_unlock(testId, labType, 2,args);
                    });

            System.out.println("waiting  unlock ....");
            CallProof un1 = unoperation1.join();
            CallProof un2 = unoperation2.join();
            System.out.println("finish all unlock!");

            System.out.println("---------------------------  server insert proofs  -------------------------");
            if (cp1.getStatus().equals("SUCCESS") && cp2.getStatus().equals("SUCCESS")  && un1.getStatus().equals("SUCCESS") && un2.getStatus().equals("SUCCESS")) {

                CompletableFuture<CallProof> insertt1 = null;
                CompletableFuture<CallProof> insertt2 = null;
                insertt1 = CompletableFuture.supplyAsync(
                        () -> {
                            // -----------------   server insert operation 1 proff --------------------------------------------
                            //Utils.priteTime("start insert proof of operation 1");
                            String templ = "{\"Operation\":1,\"chainId\":\"MyEthereum-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                            //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                            templ = templ.replace("$blockNum1$", cp1.getBlockNumber().toString());
                            templ = templ.replace("$tranHash1$", cp1.getTransaction());
                            templ = templ.replace("$blockNum2$", un1.getBlockNumber().toString());
                            templ = templ.replace("$tranHash2$", un1.getTransaction());
                            System.out.println(templ);
                            try {
                                return EthService.insertProofAsync(testId, labType, 1, templ, false, 0,EthService.becf.getNFTServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        });
                insertt2 = CompletableFuture.supplyAsync(
                        () -> {
                            // -----------------   server insert operation 2 proff --------------------------------------------
                            // Utils.priteTime("start insert proof of operation 2");
                            String templ = "{\"Operation\":2,\"chainId\":\"MyFabric-1\",\"lock_do\":{\"blockNum\":$blockNum1$,\"tranHash\":\"$tranHash1$\"},\"unlock\":{ \"blockNum\":$blockNum2$, \"tranHash\":\"$tranHash2$\" }}";
                            //[{"Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum1$,"tranHash":"$tranHash1$"},"unlock":{ "blockNum":$blockNum2$, "tranHash":"$tranHash2$" }},{ "Operation":1,"chainId":"MyEthereum-1","lock_do":{"blockNum":$blockNum3$,"tranHash":"$tranHash3$"},"unlock":{ "blockNum":$blockNum4$, "tranHash":"$tranHash4$"}}]
                            templ = templ.replace("$blockNum1$", cp2.getBlockNumber().toString());
                            templ = templ.replace("$tranHash1$", cp2.getTransaction());
                            templ = templ.replace("$blockNum2$", un2.getBlockNumber().toString());
                            templ = templ.replace("$tranHash2$", un2.getTransaction());
                            System.out.println(templ);
                            try {
                                return EthService.insertProofAsync(testId, labType, 2, templ, false, 1,EthService.becf.getNFTServiceAddr());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        });
                CallProof in1 = insertt1.join();
                CallProof in2 = insertt2.join();
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
                            return EthService.auditAsync(testId, labType, 1, true, 0,EthService.becf.getNFTServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote2 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 2, true, 0,EthService.becf.getNFTServiceAddr());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
            vote3 = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return EthService.auditAsync(testId, labType, 3, true, 0,EthService.becf.getNFTServiceAddr());
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
        }else{ //有执行不成功的
            if(cp1.getStatus().equals("SUCCESS")){  // undo_unlock
                System.out.println("todo---------------undo_unlock in operation 1");
            }else{
                System.out.println("todo---------------unlock in operation 1");
            }
            if(cp2.getStatus().equals("SUCCESS")){  // undo_unlock
                System.out.println("todo---------------undo_unlock in operation 2");
            }else{
                System.out.println("todo---------------unlock in operation 2");
            }
        }

    }

}
