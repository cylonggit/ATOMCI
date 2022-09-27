package com.chen.crossTest.service;

import com.chen.crossTest.contract.EthBusiness_sol_EthBusiness;
import com.chen.crossTest.pojo.ClientArgs;
import com.chen.crossTest.pojo.NFTCrossArgs;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.chen.crossTest.service.EthService.*;

public class EthTransactionTracer {
    public static String invokeId;
    public static List<Type> decodeInputData(String inputData,
                                             String methodName,
                                             List<TypeReference<?>> outputParameters) {
        Function function = new Function(methodName,
                Collections.<Type>emptyList(),
                outputParameters
        );
        List<Type> result = FunctionReturnDecoder.decode(
                inputData.substring(10),
                function.getOutputParameters());
        return result;
    }


    public static void traceEntryFunction(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        System.out.println("start tracing entryFunction in base blockchain--------------");
        TransactionReceipt rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            Transaction tr=web3j.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            System.out.println("from: "+tr.getFrom());
            System.out.println("to: "+tr.getTo());
            System.out.println("blockNum: "+tr.getBlockNumber());
            System.out.println("send value: "+tr.getValue());
            //System.out.println("input: "+tr.getInput());
            String inputData = tr.getInput();
            String method = inputData.substring(0, 10);
            System.out.println("methodCode: "+method);

            List<Type> result = decodeInputData(inputData, "entryFunction",
                    Arrays.asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Uint256>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                System.out.println("parameters are as follows:");
                invokeId = result.get(0).getValue().toString();
                System.out.println("invokeId: "+invokeId);
                String num = result.get(1).getValue().toString();
                System.out.println("num: "+num);
            }
        }else {
            System.out.println("transaction not found!");
        }
        System.out.println("finish tracing entryFunction in base blockchain--------------");
    }
    public static void traceLock_do(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        System.out.println("start tracing lock_do sub_invocation in MyEthereum-1 blockchain  --------------");
        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            Transaction tr=web3j2.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            System.out.println("from: "+tr.getFrom());
            System.out.println("to: "+tr.getTo());
            System.out.println("blockNum: "+tr.getBlockNumber());
            System.out.println("send value: "+tr.getValue());
            //System.out.println("input: "+tr.getInput());
            String inputData = tr.getInput();
            String method = inputData.substring(0, 10);
            System.out.println("methodCode: "+method);

//            Function function = new Function(
//                    "incressAsset_lock_do",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetID),
//                            new org.web3j.abi.datatypes.generated.Uint256(_num),
//                            new org.web3j.abi.datatypes.Utf8String(_invokeId),
//                            new org.web3j.abi.datatypes.generated.Bytes32(_lockHash)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "incressAsset_lock_do",
                    Arrays.asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Uint256>() {},
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Bytes32>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                System.out.println("parameters are as follows:");
                System.out.println("assetId: "+result.get(0).getValue().toString());
                System.out.println("num: "+result.get(1).getValue().toString());
                System.out.println("invokeId: "+result.get(2).getValue().toString());
                //assertEquals(result.get(2).getValue().toString().equals(invokeId));
                System.out.println("lockHash: "+result.get(3).getValue().toString());
            }
        }else {
            System.out.println("transaction not found! audit will not be passed!");
        }
        System.out.println("finish tracing lock_do sub_invocation in MyEthereum-1 blockchain --------------");
    }

    public static void traceUnlock(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        System.out.println("start tracing unlock sub_invocation in MyEthereum-1 blockchain --------------");
        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            Transaction tr=web3j2.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            System.out.println("from: "+tr.getFrom());
            System.out.println("to: "+tr.getTo());
            System.out.println("blockNum: "+tr.getBlockNumber());
            System.out.println("send value: "+tr.getValue());
            //System.out.println("input: "+tr.getInput());
            String inputData = tr.getInput();
            String method = inputData.substring(0, 10);
            System.out.println("methodCode: "+method);

//            Function function = new Function(
//                    "incressAsset_unlock",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetID),
//                            new org.web3j.abi.datatypes.generated.Uint256(_num),
//                            new org.web3j.abi.datatypes.Utf8String(_invokeId),
//                            new org.web3j.abi.datatypes.Utf8String(_hashKey)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "incressAsset_unlock",
                    Arrays.asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Uint256>() {},
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Utf8String>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                System.out.println("parameters are as follows:");
                System.out.println("assetId: "+result.get(0).getValue().toString());
                System.out.println("num: "+result.get(1).getValue().toString());
                System.out.println("invokeId: "+result.get(2).getValue().toString());
                //assertEquals(result.get(2).getValue().toString().equals(invokeId));
                System.out.println("hashKey: "+result.get(3).getValue().toString());
            }
        }else {
            System.out.println("transaction not found! audit will not be passed!");
        }
        System.out.println("finish tracing unlock sub_invocation in MyEthereum-1 blockchain --------------");
    }


    public static void traceAtomicRead(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        System.out.println("start tracing atomicRead sub_invocation in MyEthereum-1 blockchain --------------");
        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            Transaction tr=web3j2.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            System.out.println("from: "+tr.getFrom());
            System.out.println("to: "+tr.getTo());
            System.out.println("blockNum: "+tr.getBlockNumber());
            System.out.println("send value: "+tr.getValue());
            //System.out.println("input: "+tr.getInput());
            String inputData = tr.getInput();
            String method = inputData.substring(0, 10);
            System.out.println("methodCode: "+method);
//            Function function = new Function(
//                    "getRateOfFabric_atomic",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_num),
//                            new org.web3j.abi.datatypes.Utf8String(_invokeId)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "getRateOfFabric_atomic",
                    Arrays.asList(
                            new TypeReference<Uint256>() {},
                            new TypeReference<Utf8String>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                System.out.println("parameters are as follows:");
                System.out.println("num: "+result.get(0).getValue().toString());
                System.out.println("invokeId: "+result.get(1).getValue().toString());
                //assertEquals(result.get(2).getValue().toString().equals(invokeId));
            }
            System.out.println("logs are as follows:");
            List<EthBusiness_sol_EthBusiness.GetNumberEventResponse> b=ethBusinessContract.getGetNumberEvents(rp);
            System.out.println("Atomic read event detail: invokeId: "+b.get(0).invokeId+"; assetId: "+b.get(0).assetId+"; operate num: "+b.get(0).num);
            System.out.println("finish tracing atomicRead sub_invocation in MyEthereum-1 blockchain --------------");

        }else {
            System.out.println("transaction not found!");
        }
    }

    public static BigInteger traceAtomicReadResult(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            List<EthBusiness_sol_EthBusiness.GetNumberEventResponse> b=ethBusinessContract.getGetNumberEvents(rp);
            System.out.println("Atomic read event detail: invokeId: "+b.get(0).invokeId+"; assetId: "+b.get(0).assetId+"; operate num: "+b.get(0).num);
            System.out.println("read result is: "+b.get(0).num);
            return b.get(0).num;
        }else {
            System.out.println("transaction not found!");
            return null;
        }
    }

    public static void traceAtomicReadAsset(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        System.out.println("start tracing atomicRead sub_invocation in MyEthereum-1 blockchain --------------");
        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        if(rp!=null){
            Transaction tr=web3j2.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            System.out.println("from: "+tr.getFrom());
            System.out.println("to: "+tr.getTo());
            System.out.println("blockNum: "+tr.getBlockNumber());
            System.out.println("send value: "+tr.getValue());
            //System.out.println("input: "+tr.getInput());
            String inputData = tr.getInput();
            String method = inputData.substring(0, 10);
            System.out.println("methodCode: "+method);
//            Function function = new Function(
//                    "getAsset_atomic",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetID),
//                            new org.web3j.abi.datatypes.Utf8String(_invokeId)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "getAsset_atomic",
                    Arrays.asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Utf8String>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                System.out.println("parameters are as follows:");
                System.out.println("assetId: "+result.get(0).getValue().toString());
                System.out.println("invokeId: "+result.get(1).getValue().toString());
                //assertEquals(result.get(2).getValue().toString().equals(invokeId));
            }
            System.out.println("logs are as follows:");
            List<EthBusiness_sol_EthBusiness.ReadAssetEventResponse> b=ethBusinessContract.getReadAssetEvents(rp);
            System.out.println("Atomic read event detail: invokeId: "+b.get(0).invokeId+"; assetId: "+b.get(0).assetId+"; total: "+b.get(0).total);
            System.out.println("finish tracing atomicRead sub_invocation in MyEthereum-1 blockchain --------------");

        }else {
            System.out.println("transaction not found!");
        }


    }

    public static void getAtomicReadEventDetail(String transactionHash) throws IOException {
        //event definition
        Event event = new Event("ReadAsset",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));

        TransactionReceipt rp=web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
        Log log=rp.getLogs().get(0);
        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            System.out.println("no event found!");
        }
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        EventValues ev= new EventValues(indexedValues, nonIndexedValues);
        System.out.println("Atomic read detail: invokeId: "+ev.getNonIndexedValues().get(0).getValue()+" assetId: "+ev.getNonIndexedValues().get(1).getValue()+" total: "+ev.getNonIndexedValues().get(2).getValue());
    }

    public static ClientArgs traceCrossArgs(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        TransactionReceipt rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
        ClientArgs args=new ClientArgs();
        if(rp!=null){
            Transaction tr=web3j.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            args.setFrom(tr.getFrom());
            args.setTo(tr.getTo());
            args.setSendValue(tr.getValue());

            String inputData = tr.getInput();
//            Function function = new Function(
//                    "entryFunction",
//                    Arrays.<Type>asList(new Utf8String(_assetId),
//                            new Utf8String(_invokeId)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "entryFunction",
                    Arrays.asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Utf8String>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                args.setAssetId(result.get(0).getValue().toString());
                args.setInvokeId(result.get(1).getValue().toString());
            }
            return args;
        }else {
            System.out.println("transaction not found!");
            return null;
        }
    }

    public static NFTCrossArgs traceNFTCrossArgs(String transactionHash) throws Exception {
        //String transactionHash="0x1045e3d0c7100e39f3f104958a6f5b9bd7f1bd737866edc8fb375540d3d12ed2";
        TransactionReceipt rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
        NFTCrossArgs args=new NFTCrossArgs();
        if(rp!=null){
            Transaction tr=web3j.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
            args.setFrom(tr.getFrom());
            args.setTo(tr.getTo());
            args.setSendValue(tr.getValue());

            String inputData = tr.getInput();
//            String method = inputData.substring(0, 10);
//            System.out.println("methodCode: "+method);
//            Function function = new Function(
//                    "entryFunction",
//                    Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_type),
//                            new org.web3j.abi.datatypes.generated.Uint256(_tokenId),
//                            new org.web3j.abi.datatypes.Address(_ethAddr),
//                            new org.web3j.abi.datatypes.Utf8String(fabricMSPID),
//                            new org.web3j.abi.datatypes.DynamicBytes(_ethSign),
//                            new org.web3j.abi.datatypes.Utf8String(_fabricSign),
//                            new org.web3j.abi.datatypes.Utf8String(_invokeId)),
//                    Collections.<TypeReference<?>>emptyList());

            List<Type> result = decodeInputData(inputData, "entryFunction",
                    Arrays.asList(
                            new TypeReference<Int256>() {},
                            new TypeReference<Uint256>() {},
                            new TypeReference<Address>() {},
                            new TypeReference<Utf8String>() {},
                            new TypeReference<DynamicBytes>() {},
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Utf8String>() {}
                    )
            );
            if (result != null && result.size() > 0) {
                args.setType(new BigInteger(result.get(0).getValue().toString()));
                args.setTokenId(new BigInteger(result.get(1).getValue().toString()));
                args.setEthAddr(result.get(2).getValue().toString());
                args.setMsp(result.get(3).getValue().toString());
                args.set_ethSign((byte[]) result.get(4).getValue());
                args.setFabricSign(result.get(5).getValue().toString());
                args.setInvokeId(result.get(6).getValue().toString());
            }
            return args;
        }else {
            System.out.println("transaction not found!");
            return null;
        }
    }

}
