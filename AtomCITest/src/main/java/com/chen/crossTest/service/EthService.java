package com.chen.crossTest.service;

import com.chen.crossTest.config.BaseChainEthConf;
import com.chen.crossTest.config.EthConnectConf;
import com.chen.crossTest.contract.CrossChainNFT_sol_CrossChainNFT;
import com.chen.crossTest.contract.EntryContract_sol_EntryContract;
import com.chen.crossTest.contract.ServContract_sol_ServContract;
import com.chen.crossTest.contract.EthBusiness_sol_EthBusiness;
import com.chen.crossTest.mybatis.LabLogMapper;
import com.chen.crossTest.pojo.*;
import com.chen.crossTest.pojo.proof.OperationProof;
import com.chen.crossTest.utils.Utils;
import com.google.gson.Gson;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EthService {

    static String entryABI="[{\"constant\":true,\"inputs\":[],\"name\":\"invokeFee\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"invokeId\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"client\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getBalance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"chainIds\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_invokeId\",\"type\":\"string\"},{\"name\":\"_num1\",\"type\":\"uint256\"}],\"name\":\"entryFunction\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getOperationNum\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_invokeId\",\"type\":\"string\"}],\"name\":\"forceSettle\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"operationNum\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_invokeId\",\"type\":\"string\"},{\"name\":\"_resultString\",\"type\":\"string\"}],\"name\":\"callback\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_invokeId\",\"type\":\"string\"}],\"name\":\"extend\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_community\",\"type\":\"address\"},{\"name\":\"_servContract\",\"type\":\"address\"},{\"name\":\"_serverAddr\",\"type\":\"address\"}],\"name\":\"setServiceInfo\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_invokeFee\",\"type\":\"uint256\"}],\"name\":\"setInvokeFee\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"parameters\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"operations\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"results\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"dAppProvider\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"state\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"service\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"servContract\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_community\",\"type\":\"address\"},{\"name\":\"_servContract\",\"type\":\"address\"},{\"name\":\"_serverAddr\",\"type\":\"address\"}],\"name\":\"initDebug\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"community\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"remoteFunctions\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"server\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"fallback\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_invokeId\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_resultStr\",\"type\":\"string\"}],\"name\":\"Result\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_invokeId\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_parameters\",\"type\":\"uint256\"}],\"name\":\"CrossChainEvent\",\"type\":\"event\"}]";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    //baseChain
    public static BaseChainEthConf becf = new BaseChainEthConf("ETH-CONF-001","/BaseChain.properties");
    static Web3j web3j = Web3j.build(new HttpService(becf.getGETHURL()));
    public static Credentials serverCre = Credentials.create(becf.getPriKeyServer());
    public static Credentials dAppCre = Credentials.create(becf.getPriKeyDApp());
    public static Credentials clientCre = Credentials.create(becf.getPriKeyClient());
    static Credentials judger1Cre = Credentials.create(becf.getPriKeyJudger1());
    static Credentials judger2Cre = Credentials.create(becf.getPriKeyJudger2());
    static Credentials judger3Cre = Credentials.create(becf.getPriKeyJudger3());
    static Credentials judger4Cre = Credentials.create(becf.getPriKeyJudger4());
    static Credentials judger5Cre = Credentials.create(becf.getPriKeyJudger5());
    static Map<String, Credentials> judgerCreList =new HashMap();
    static Credentials initManCre = Credentials.create(becf.getPriKeyInitMan());

    static ServContract_sol_ServContract servContract_server =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,serverCre, becf.GAS_PRICE, becf.GAS_LIMIT);
    static ServContract_sol_ServContract servNFTContract_server =  ServContract_sol_ServContract.load(becf.getNFTServiceAddr(),web3j,serverCre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_dApp =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,dAppCre, becf.GAS_PRICE, becf.GAS_LIMIT);
    static ServContract_sol_ServContract servContract_judger =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,judger1Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_judger2 =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,judger2Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_judger3 =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,judger3Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_judger4 =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,judger4Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_judger5 =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,judger5Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
//    static ServContract_sol_ServContract servContract_client =  ServContract_sol_ServContract.load(becf.getServiceAddr(),web3j,clientCre, BigInteger.valueOf(1L), BigInteger.valueOf(8000000L));
//    static Map<String, ServContract_sol_ServContract> judgerContractList =new HashMap();

    static EntryContract_sol_EntryContract entryContract_client =  EntryContract_sol_EntryContract.load(becf.getEntryAddr(),web3j,clientCre, BigInteger.valueOf(1L), BigInteger.valueOf(8000000L));


    //ethereum business contract
    public static EthConnectConf ecf = new EthConnectConf("ETH-CONF-001", "/EthBusiness.properties");
    static Credentials credentials = Credentials.create(ecf.getPRIVATEKEY());
    static Web3j web3j2 = Web3j.build(new HttpService(ecf.getGETHURL()));
    static EthBusiness_sol_EthBusiness ethBusinessContract = EthBusiness_sol_EthBusiness.load(ecf.getCONTRACT_ADDR(),web3j2,credentials, ecf.getGasPrice(), ecf.getGasLimit());
    static CrossChainNFT_sol_CrossChainNFT NFTContract = CrossChainNFT_sol_CrossChainNFT.load(ecf.getNFT_ADDR(),web3j2,credentials, ecf.getGasPrice(), ecf.getGasLimit());
    //mysql
    //mysql
    static SqlSession sqlSession =null;
    static LabLogMapper logMapper=null;
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSession = sessionFactory.openSession();
            logMapper = sqlSession.getMapper(LabLogMapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        judgerCreList.put("0x960082d1b215929f426517EE8b0c34F8b6d4dAF3".toLowerCase(),judger1Cre);
        judgerCreList.put("0xD4F7e685AEEB0C0F697F9DfCA2f36bb113FB9d39".toLowerCase(),judger2Cre);
        judgerCreList.put("0x897a792665E48D1cAAED5cea50f396b8A77d6650".toLowerCase(),judger3Cre);
        judgerCreList.put("0x3B9eD344D2885d33dD8C3D2411411dF94ab8E824".toLowerCase(),judger4Cre);
        judgerCreList.put("0xeF77D5819f56bB7d20Cf99B951E90E074A1577cd".toLowerCase(),judger5Cre);

    }
    public static void insertLog(LabLog log){
        logMapper.addLablog(log);
        sqlSession.commit();  //执行增删改是需手动提交数据
    }

    public static void changeConfig(String type){
        if(type.equals("dApp1")){
            servContract_server =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp1(),web3j,serverCre, becf.GAS_PRICE, becf.GAS_LIMIT);
            servContract_judger =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp1(),web3j,judger1Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
            entryContract_client =  EntryContract_sol_EntryContract.load(becf.getEntryAddrDApp1(),web3j,clientCre, BigInteger.valueOf(1L), BigInteger.valueOf(8000000L));
            becf.setServiceAddr(becf.getServiceAddrDApp1());
            becf.setEntryAddr(becf.getEntryAddrDApp1());
        } else if (type.equals("dApp2")) {
            servContract_server =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp2(),web3j,serverCre, becf.GAS_PRICE, becf.GAS_LIMIT);
            servContract_judger =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp2(),web3j,judger1Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
            entryContract_client =  EntryContract_sol_EntryContract.load(becf.getEntryAddrDApp2(),web3j,clientCre, BigInteger.valueOf(1L), BigInteger.valueOf(8000000L));
            becf.setServiceAddr(becf.getServiceAddrDApp2());
            becf.setEntryAddr(becf.getEntryAddrDApp2());
        }else if (type.equals("dApp3")) {
            servContract_server =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp3(),web3j,serverCre, becf.GAS_PRICE, becf.GAS_LIMIT);
            servContract_judger =  ServContract_sol_ServContract.load(becf.getServiceAddrDApp3(),web3j,judger1Cre, becf.GAS_PRICE, becf.GAS_LIMIT);
            entryContract_client =  EntryContract_sol_EntryContract.load(becf.getEntryAddrDApp3(),web3j,clientCre, BigInteger.valueOf(1L), BigInteger.valueOf(8000000L));
            becf.setServiceAddr(becf.getServiceAddrDApp3());
            becf.setEntryAddr(becf.getEntryAddrDApp3());
        }

    }

    //  -------------------------------------------------------------------------   business chain ------------------------------------------------------------------
    
//    public static CallProof incressAsset_lock_do(Date testId,String labType,int operation) {
//       // System.out.println("--------------------- operation "+operation+" lock_do in ethereum-------------------------");
//        Utils.priteTime("start operation "+operation+" lock_do in ethereum");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            String  start  =  sdf.format(new Date());
//            TransactionReceipt result = ethBusinessContract.incressAsset_lock_do(Parameters.assetId,Parameters.num,Parameters.invokeId,Parameters.lockHash).send();
//            String  end  =  sdf.format(new Date());
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in operation: "+operation+" lock_do is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" lock_do");
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call to ETH fail in operation "+operation+" lock_do");
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish operation "+operation+" lock_do in ethereum");
//        return callProof;
//    }

    public static CallProof<TransactionReceipt> incressAsset_lock_do(Date testId, String labType,int operation,BigInteger num,ClientArgs args) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation " + operation + " lock_do in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "incressAsset_lock_do",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(args.getAssetId()),
                            new org.web3j.abi.datatypes.generated.Uint256(num),
                            new org.web3j.abi.datatypes.Utf8String(args.getInvokeId()),
                            new org.web3j.abi.datatypes.generated.Bytes32(Parameters.lockHash)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);  

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" lock_do");
                    }
                    // wait about 12 blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new LabLog(testId, labType, ethProof.getTransactionHash(), start, end, ethProof.getGasUsed(), ethProof.getBlockNumber(), "operation : " + operation + " lock_do");
                    insertLog(log1);
                    Utils.priteTime("finish operation " + operation + " lock_do in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CallProof<TransactionReceipt> crossChainTransfer_lock_do(Date testId, String labType, int operation, NFTCrossArgs args) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation " + operation + " lock_do in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "crossChainTransfer_lock_do",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(args.getType()),
                            new org.web3j.abi.datatypes.generated.Uint256(args.getTokenId()),
                            new org.web3j.abi.datatypes.Address(args.getEthAddr()),
                            new org.web3j.abi.datatypes.Utf8String(args.getMsp()),
                            new org.web3j.abi.datatypes.DynamicBytes(args.get_ethSign()),
                            new org.web3j.abi.datatypes.Utf8String(args.getInvokeId()),
                            new org.web3j.abi.datatypes.generated.Bytes32(Parameters.lockHash)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getNFT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" lock_do");
                    }
                    // wait about 12 blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new LabLog(testId, labType, ethProof.getTransactionHash(), start, end, ethProof.getGasUsed(), ethProof.getBlockNumber(), "operation : " + operation + " lock_do");
                    insertLog(log1);
                    Utils.priteTime("finish operation " + operation + " lock_do in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    public static CallProof incressAsset_unlock(Date testId,String labType,int operation) {
//       // System.out.println("--------------------- operation "+operation+" unlock in ethereum-------------------------");
//        Utils.priteTime("start operation "+operation+" unlock in ethereum");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            String  start1  =  sdf.format(new Date());
//            TransactionReceipt result = ethBusinessContract.incressAsset_unlock(Parameters.assetId,Parameters.num,Parameters.invokeId,Parameters.hashKey).send();
//            String  end1  =  sdf.format(new Date());
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in operation: "+operation+" unlock is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,end1, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" unlock");
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call to ETH fail in operation "+operation+" unlock");
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish operation "+operation+" unlock in ethereum");
//        return callProof;
//    }

    public static CallProof<TransactionReceipt> incressAsset_unlock(Date testId, String labType,int operation,BigInteger num,ClientArgs args) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation "+operation+" unlock in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "incressAsset_unlock",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(args.getAssetId()),
                            new org.web3j.abi.datatypes.generated.Uint256(num),
                            new org.web3j.abi.datatypes.Utf8String(args.getInvokeId()),
                            new org.web3j.abi.datatypes.Utf8String(Parameters.hashKey)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);  

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" unlock");
                    }
                    // wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" unlock");
                    insertLog(log1);
                    Utils.priteTime("finish operation "+operation+" unlock in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CallProof<TransactionReceipt> crossChainTransfer_unlock(Date testId, String labType,int operation,NFTCrossArgs args) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation "+operation+" unlock in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "crossChainTransfer_unlock",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(args.getType()),
                            new org.web3j.abi.datatypes.generated.Uint256(args.getTokenId()),
                            new org.web3j.abi.datatypes.Address(args.getEthAddr()),
                            new org.web3j.abi.datatypes.Utf8String(args.getMsp()),
                            new org.web3j.abi.datatypes.DynamicBytes(args.get_ethSign()),
                            new org.web3j.abi.datatypes.Utf8String(args.getInvokeId()),
                            new org.web3j.abi.datatypes.Utf8String(Parameters.hashKey)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getNFT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" unlock");
                    }
                    // wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" unlock");
                    insertLog(log1);
                    Utils.priteTime("finish operation "+operation+" unlock in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//
//    public static CallProof incressAsset_undo_unlock(Date testId,String labType,int operation) {
//       // System.out.println("--------------------- operation "+operation+" undo_unlock in ethereum-------------------------");
//        Utils.priteTime("start operation "+operation+" undo_unlock in ethereum");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            String  start1  =  sdf.format(new Date());
//            TransactionReceipt result = ethBusinessContract.incressAsset_undo_unlock(Parameters.assetId,Parameters.num,Parameters.invokeId,Parameters.hashKey).send();
//            String  end1  =  sdf.format(new Date());
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in operation: "+operation+" undo_unlock is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,end1, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" undo_unlock");
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call to ETH fail in operation "+operation+" undo_unlock");
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish operation "+operation+" undo_unlock in ethereum");
//        return callProof;
//    }

    public static CallProof<TransactionReceipt> incressAsset_undo_unlock(Date testId, String labType,int operation) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation "+operation+" undo_unlock in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "incressAsset_undo_unlock",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(Parameters.assetId),
                            new org.web3j.abi.datatypes.generated.Uint256(Parameters.num),
                            new org.web3j.abi.datatypes.Utf8String(Parameters.invokeId),
                            new org.web3j.abi.datatypes.Utf8String(Parameters.hashKey)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);  

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" undo_unlock");
                    }
                    // wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" undo_unlock");
                    insertLog(log1);
                    Utils.priteTime("finish operation "+operation+" undo_unlock in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




//
//
//    public static CallProof getAssetAtomic(Date testId,String labType,int operation) {
//        Utils.priteTime("start operation "+operation+" atomic read in ethereum");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            String  start1  =  sdf.format(new Date());
//            TransactionReceipt result = ethBusinessContract.getAsset_atomic(Parameters.assetId,Parameters.invokeId).send();
//            String  end1  =  sdf.format(new Date());
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in operation: "+operation+" atomic read is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,end1, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" atomic read");
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call to ETH fail in operation "+operation+" atomic read");
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish operation "+operation+" atomic read in ethereum");
//        return callProof;
//    }


    public static CallProof<TransactionReceipt> getAssetAtomic(Date testId, String labType,int operation) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation "+operation+" atomic read in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "getAsset_atomic",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(Parameters.assetId),
                            new org.web3j.abi.datatypes.Utf8String(Parameters.invokeId)),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);  

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" atomic read");
                    }
                    // wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" atomic read");
                    insertLog(log1);
                    Utils.priteTime("finish operation "+operation+" atomic read in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CallProof<TransactionReceipt> getNumberAtomic(Date testId, String labType, int operation,ClientArgs args) {
        try {
            int nonceAdd = 0;
            Utils.priteTime("start operation "+operation+" atomic read in ethereum");
            //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
            EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                    serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            nonce = nonce.add(BigInteger.valueOf(nonceAdd));
            System.out.println("nonce:" + nonce);
            BigInteger gasPrice = BigInteger.valueOf(1L);
            BigInteger gasLimit = BigInteger.valueOf(8000000L);
            String start = sdf.format(new Date());
            // create our transaction
            Function function = new Function(
                    "getNumber_atomic",
                    Arrays.<Type>asList(new Utf8String(args.getAssetId()),
                            new Utf8String(args.getInvokeId())),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, serverCre);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
            //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
            // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
            String transactionHash = ethSendTransaction.getTransactionHash();
            CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
            EthGetTransactionReceipt re = cf.join();
            System.out.println("tranHash: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("transaction failed,info:" + message);
                // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
            } else {
                String hash = ethSendTransaction.getTransactionHash();
                EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
                System.out.println("Async send operation " + operation + "  success");
            }
            //观察100秒
            TransactionReceipt rp = null;
            CallProof<TransactionReceipt> callProof = new CallProof<>();
            int b = 0;
            while (b < 500) {
                rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
                if (rp != null) {
                    System.out.println("the transaction has on chain!");
                    System.out.println("gas used in operation " + operation + " is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    EthProof ethProof = new EthProof(rp);
                    callProof.setResult(rp);
                    callProof.setChainType(ChainType.ETH);
                    callProof.setBlockNumber(ethProof.getBlockNumber());
                    callProof.setBlockHash(ethProof.getBlockHash());
                    callProof.setTransaction(ethProof.getTransactionHash());
                    callProof.setContract(ethProof.getContractAddress());
                    callProof.setTransactionIndex(ethProof.getTransactionIndex());
                    callProof.setRoot(ethProof.getRoot());
                    callProof.setStatus("SUCCESS");
                    if (ethProof.getStatus().equals("0x0")) {
                        callProof.setStatus("FAIL");
                        throw new Exception("call to ETH fail in operation "+operation+" atomic read");
                    }
                    // wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction
                    Utils.priteTime("start to wait about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    Thread.sleep(ecf.BLOCKINTERVAL * ecf.BLOCKSNEED);
                    Utils.priteTime("finish of waiting about "+ecf.BLOCKSNEED+" blocks to confirm the transaction");
                    String end = sdf.format(new Date());
                    LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start,end, ethProof.getGasUsed(),ethProof.getBlockNumber(),"operation : "+operation+" atomic read");
                    insertLog(log1);
                    Utils.priteTime("finish operation "+operation+" atomic read in ethereum");
                    return callProof;
                }
                Thread.sleep(100);
                b++;
            }
            System.out.println("the transaction has not on chain over 100 seconds!");
            throw new Exception("the transaction has not on chain over 100 seconds!");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initEthbusinessChain() throws Exception {
        //初始化链上数据
//        //ethurem
        System.out.println("initing ethereum business chain data...");
//        TransactionReceipt rp = ethBusinessContract.setAsset(Parameters.assetId, BigInteger.valueOf(100),false).send();
//        System.out.println(rp);
        int nonceAdd = 0;
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce = nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:" + nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        String start = sdf.format(new Date());
        // create our transaction
        Function function = new Function(
                "setAsset",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(Parameters.assetId),
                        new org.web3j.abi.datatypes.generated.Uint256(Parameters.initNum),
                        new org.web3j.abi.datatypes.Bool(false)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);  

        RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getCONTRACT_ADDR(), encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re = cf.join();
        System.out.println("tranHash: " + transactionHash);
        if (ethSendTransaction.hasError()) {
            String message = ethSendTransaction.getError().getMessage();
            System.out.println("init asset transaction failed,info:" + message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        } else {
            String hash = ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send init asset transaction success");
        }
        //观察100秒
        TransactionReceipt rp = null;
        CallProof<TransactionReceipt> callProof = new CallProof<>();
        int b = 0;
        while (b < 500) {
            rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
            if (rp != null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in init asset transaction is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")) {
                    callProof.setStatus("FAIL");
                    throw new Exception("call to ETH fail in init asset transaction ");
                }
                Tuple3<BigInteger, Boolean, String> re2 = ethBusinessContract.getAsset("asset001").send();
                System.out.println("init asset001 in ethereum business contract: "+re2.getValue1()+" tokens. and stat is: "+re2.getValue2());
                System.out.println("finish initing ethereum business chain data.");
                System.out.println("\n");
                return;
            }
            Thread.sleep(100);
            b++;
        }
        System.out.println("the init transaction has not on chain over 100 seconds!");
        throw new Exception("the init transaction has not on chain over 100 seconds!");

    }



    public static void initNFTEthbusinessChain() throws Exception {
        //初始化链上数据
//        //ethurem
        System.out.println("initing nft ethereum business chain data...");
//        TransactionReceipt rp = ethBusinessContract.setAsset(Parameters.assetId, BigInteger.valueOf(100),false).send();
//        System.out.println(rp);
        int nonceAdd = 0;
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        EthGetTransactionCount ethGetTransactionCount = web3j2.ethGetTransactionCount(
                dAppCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce = nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:" + nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        String start = sdf.format(new Date());
        // create our transaction
        Function function = new Function(
                "initNFT",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(Parameters.nftTokenID)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ecf.getNFT_ADDR(), encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, dAppCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf
        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf = web3j2.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re = cf.join();
        System.out.println("tranHash: " + transactionHash);
        if (ethSendTransaction.hasError()) {
            String message = ethSendTransaction.getError().getMessage();
            System.out.println("init mint NFT transaction failed,info:" + message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        } else {
            String hash = ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j2.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send init nft asset transaction success");
        }
        //观察100秒
        TransactionReceipt rp = null;
        CallProof<TransactionReceipt> callProof = new CallProof<>();
        int b = 0;
        while (b < 500) {
            rp = web3j2.ethGetTransactionReceipt(transactionHash).send().getResult();
            if (rp != null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in init nft transaction is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")) {
                    callProof.setStatus("FAIL");
                    throw new Exception("call to ETH fail in init asset transaction ");
                }
                String owner = NFTContract.ownerOf(Parameters.nftTokenID).send();
                System.out.println("init nft in ethereum business contract, tokenID: "+Parameters.nftTokenID+"'s owner is: "+owner);
                System.out.println("finish initing nft ethereum business chain data.");
                System.out.println("\n");
                return;
            }
            Thread.sleep(100);
            b++;
        }
        System.out.println("the init transaction has not on chain over 100 seconds!");
        throw new Exception("the init transaction has not on chain over 100 seconds!");

    }




    //  -------------------------------------------------------------------------   base chain ------------------------------------------------------------------
//    public static CallProof entryFunction(Date testId,String labType) {
//        //System.out.println("---------------------------  client invoke entry function -------------------------");
//        Utils.priteTime("start entry function in base chain");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(80));
//            String  start1  =  sdf.format(new Date());
//            TransactionReceipt result = entryContract_client.entryFunction(Parameters.assetId,Parameters.invokeId,sendWei).send();
//            String  end1  =  sdf.format(new Date());
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in entry function is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,end1, ethProof.getGasUsed(),ethProof.getBlockNumber(),"entry-function");
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call base chain fail in entry-function");
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish entry function in base chain");
//        return callProof;
//    }

    public static CallProof<TransactionReceipt> entryFunctionAsync(Date testId, String labType,int nonceAdd) throws Exception {
        Utils.priteTime("start entry function in base chain");
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                clientCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(80));
        String  start  =  sdf.format(new Date());
        // create our transaction
        Function function = new Function(
                "entryFunction",
                Arrays.<Type>asList(new Utf8String(Parameters.assetId),
                        new Utf8String(Parameters.invokeId)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);  
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getEntryAddr(),sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,clientCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send entry function  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in entry function is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail entry function in base chain");
                }
                LabLog log1 = new  LabLog(testId,labType,transactionHash,start,sdf.format(new Date()), rp.getGasUsed(),rp.getBlockNumber(),"entry-function");
                insertLog(log1);

                Utils.priteTime("finish entry function in base chain");
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }



    public static CallProof<TransactionReceipt> NFTEntryFunctionAsync(Date testId, String labType,int nonceAdd,int crossChainType) throws Exception {
        Utils.priteTime("start entry function in base chain");
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                clientCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(80));
        String  start  =  sdf.format(new Date());
        // create our transaction
        //BigInteger _type, BigInteger _tokenId, String _ethAddr, String fabricMSPID, byte[] _ethSign, String _fabricSign, String _invokeId, BigInteger weiValue
        Function function = new Function(
                "entryFunction",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(java.math.BigInteger.valueOf(crossChainType)),
                        new org.web3j.abi.datatypes.generated.Uint256(Parameters.nftTokenID),
                        new org.web3j.abi.datatypes.Address(Parameters.dAppAddr),
                        new org.web3j.abi.datatypes.Utf8String(Parameters.ownerMsp),
                        new org.web3j.abi.datatypes.DynamicBytes(Parameters.getEthSign(dAppCre,Parameters.ownerLock)),
                        new org.web3j.abi.datatypes.Utf8String(Parameters.getFabricSign(Parameters.ownerLock)),
                        new org.web3j.abi.datatypes.Utf8String(Parameters.invokeId)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getNFTEntryAddr(),sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,clientCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send entry function  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in entry function is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail entry function in base chain");
                }
                LabLog log1 = new  LabLog(testId,labType,transactionHash,start,sdf.format(new Date()), rp.getGasUsed(),rp.getBlockNumber(),"entry-function");
                insertLog(log1);

                Utils.priteTime("finish entry function in base chain");
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }



    public static CallProof insertProof(Date testId,String labType,int operation,String proof,boolean ifFinish) {
        Utils.priteTime("start insert proof of operation "+operation+" in base chain");
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        try {
            BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(80));
            String  start1  =  sdf.format(new Date());
            TransactionReceipt result = servContract_server.insertProof(proof,BigInteger.valueOf(operation),ifFinish).send();
            String  end1  =  sdf.format(new Date());
            EthProof ethProof = new EthProof(result);
            System.out.println("gas used in insert Proof is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            callProof.setResult(result);
            callProof.setChainType(ChainType.ETH);
            callProof.setBlockNumber(ethProof.getBlockNumber());
            callProof.setBlockHash(ethProof.getBlockHash());
            callProof.setTransaction(ethProof.getTransactionHash());
            callProof.setContract(ethProof.getContractAddress());
            callProof.setTransactionIndex(ethProof.getTransactionIndex());
            callProof.setRoot(ethProof.getRoot());
            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,end1, ethProof.getGasUsed(),ethProof.getBlockNumber(),"insert operation: "+operation+" proof");
            insertLog(log1);
            callProof.setStatus("SUCCESS");
            if (ethProof.getStatus().equals("0x0")){
                callProof.setStatus("FAIL");
                throw new Exception("call to  base chain fail in insert proof of operation "+operation);
            }
        }catch (Exception e){
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        Utils.priteTime("finish insert proof of operation "+operation+" in base chain");
        return callProof;
    }

    public static CallProof<TransactionReceipt> insertProofAsync(Date testId, String labType, int operation, String proof, boolean ifFinish, int nonceAdd,String serviceAddr) throws Exception {
        Utils.priteTime("start insert proof of operation "+operation+" in base chain");
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                serverCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = becf.GAS_PRICE;
        BigInteger gasLimit = becf.GAS_LIMIT;
        String  start  =  sdf.format(new Date());
        // create our transaction
        Function function = new Function(
                "insertProof",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(proof),
                        new org.web3j.abi.datatypes.generated.Uint256(BigInteger.valueOf(operation)),
                        new org.web3j.abi.datatypes.Bool(ifFinish)),
                Collections.<TypeReference<?>>emptyList());

        //0x88460889
        String encodedFunction = FunctionEncoder.encode(function);  

        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,serviceAddr,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,serverCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send insert proof of operation "+operation+"  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in insert Proof is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail insert proof of operation "+operation+" in base chain");
                }
                LabLog log1 = new  LabLog(testId,labType,transactionHash,start,sdf.format(new Date()), rp.getGasUsed(),rp.getBlockNumber(),"insert operation: "+operation+" proof");
                insertLog(log1);

                Utils.priteTime("finish insert proof of operation "+operation+" in base chain");
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }


//    public static CallProof audit(Date testId,String labType,int judgerNum,boolean ifValid) {
//        // System.out.println("---------------------------  client invoke entry function -------------------------");
//        Utils.priteTime("start judger "+judgerNum+" audit in base chain");
//        CallProof<TransactionReceipt> callProof=new CallProof<>();
//        try {
//            BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(80));
//            String  start1  =  sdf.format(new Date());
//            TransactionReceipt result=null;
//            if(judgerNum==1)
//                result = servContract_judger1.audit(Parameters.invokeId,ifValid,Parameters.invokeId).send();
//            if(judgerNum==2)
//                result = servContract_judger2.audit(Parameters.invokeId,ifValid,Parameters.invokeId).send();
//            if(judgerNum==3)
//                result = servContract_judger3.audit(Parameters.invokeId,ifValid,Parameters.invokeId).send();
//            Date end1 =new Date();
//            EthProof ethProof = new EthProof(result);
//            System.out.println("gas used in audit "+judgerNum+" is: " + ethProof.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            callProof.setResult(result);
//            callProof.setChainType(ChainType.ETH);
//            callProof.setBlockNumber(ethProof.getBlockNumber());
//            callProof.setBlockHash(ethProof.getBlockHash());
//            callProof.setTransaction(ethProof.getTransactionHash());
//            callProof.setContract(ethProof.getContractAddress());
//            callProof.setTransactionIndex(ethProof.getTransactionIndex());
//            callProof.setRoot(ethProof.getRoot());
//            LabLog log1 = new  LabLog(testId,labType,ethProof.getTransactionHash(),start1,sdf.format(new Date()), ethProof.getGasUsed(),ethProof.getBlockNumber(),"audit: "+judgerNum);
//            insertLog(log1);
//            callProof.setStatus("SUCCESS");
//            if (ethProof.getStatus().equals("0x0")){
//                callProof.setStatus("FAIL");
//                throw new Exception("call to  base chain fail in audit: "+judgerNum);
//            }
//        }catch (Exception e){
//            callProof.setStatus("FAIL");
//            callProof.setMsg(e.getMessage());
//            e.printStackTrace();
//        }
//        Utils.priteTime("finish judger "+judgerNum+" audit in base chain");
//        return callProof;
//    }

    public static CallProof<TransactionReceipt> auditAsync(Date testId,String labType,int judgerNum,boolean ifValid,int nonceAdd,String serviceAddr) throws Exception {
        Utils.priteTime("start judger "+judgerNum+" audit in base chain");
        Credentials credentials =null;
        if(serviceAddr.equals(becf.getServiceAddr())) {
            if (judgerNum == 1)
                credentials = judgerCreList.get(servContract_server.judgerAddrs(BigInteger.valueOf(0)).send().toLowerCase());
            if (judgerNum == 2)
                credentials = judgerCreList.get(servContract_server.judgerAddrs(BigInteger.valueOf(1)).send().toLowerCase());
            if (judgerNum == 3)
                credentials = judgerCreList.get(servContract_server.judgerAddrs(BigInteger.valueOf(2)).send().toLowerCase());
        }
        if(serviceAddr.equals(becf.getNFTServiceAddr())) {
            if (judgerNum == 1)
                credentials = judgerCreList.get(servNFTContract_server.judgerAddrs(BigInteger.valueOf(0)).send().toLowerCase());
            if (judgerNum == 2)
                credentials = judgerCreList.get(servNFTContract_server.judgerAddrs(BigInteger.valueOf(1)).send().toLowerCase());
            if (judgerNum == 3)
                credentials = judgerCreList.get(servNFTContract_server.judgerAddrs(BigInteger.valueOf(2)).send().toLowerCase());
        }
        //auditor trace all the sub-invocations that the server has done
        traceSubInvocations(labType);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = becf.GAS_PRICE;
        BigInteger gasLimit = becf.GAS_LIMIT;
        String  start  =  sdf.format(new Date());
        // create our transaction
        Function function = new Function(
                "audit",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(Parameters.invokeId),
                        new org.web3j.abi.datatypes.Bool(ifValid),
                        new org.web3j.abi.datatypes.Utf8String(Parameters.invokeId)),
                Collections.<TypeReference<?>>emptyList());

        //0x88460889
        String encodedFunction = FunctionEncoder.encode(function);  

        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,serviceAddr,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send audit "+judgerNum+"  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in audit "+judgerNum+" is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail judger "+judgerNum+" audit in base chain");
                }
                LabLog log1 = new  LabLog(testId,labType,transactionHash,start, sdf.format(new Date()), rp.getGasUsed(),rp.getBlockNumber(),"audit: "+judgerNum);
                insertLog(log1);

                Utils.priteTime("finish judger "+judgerNum+" audit in base chain");
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }

    public static void traceSubInvocations(String labType) throws Exception {
        // when an auditor trace the sub-transactions, the most important thing is to see whether the transaction is exists. Then the auditor should see the parameter invokedId and the transaction's final result.
        Gson gson = new Gson();
        if(labType.equals("atomic-ReadEth_write_2")){
            OperationProof opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(1)).send().getValue1(), OperationProof.class);
            EthTransactionTracer.traceAtomicRead(opp.getRead().getTranHash());

            opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(2)).send().getValue1(), OperationProof.class);
            EthTransactionTracer.traceLock_do(opp.getLock_do().getTranHash());
            EthTransactionTracer.traceUnlock(opp.getUnlock().getTranHash());

            opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(3)).send().getValue1(), OperationProof.class);
            FabricTransactionTracer.traceFabricTransaction(opp.getLock_do().getTranHash(),"lock_do");
            FabricTransactionTracer.traceFabricTransaction(opp.getUnlock().getTranHash(),"unlock");
        }
        if(labType.equals("atomic-ReadFabric_write_2")){
            OperationProof opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(1)).send().getValue1(), OperationProof.class);
            FabricTransactionTracer.traceFabricTransaction(opp.getRead().getTranHash(),"atomic read");

            opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(2)).send().getValue1(), OperationProof.class);
            EthTransactionTracer.traceLock_do(opp.getLock_do().getTranHash());
            EthTransactionTracer.traceUnlock(opp.getUnlock().getTranHash());

            opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(3)).send().getValue1(), OperationProof.class);
            FabricTransactionTracer.traceFabricTransaction(opp.getLock_do().getTranHash(),"lock_do");
            FabricTransactionTracer.traceFabricTransaction(opp.getUnlock().getTranHash(),"unlock");

        }
        if(labType.equals("atomic-write_2")){
            OperationProof opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(1)).send().getValue1(), OperationProof.class);
            EthTransactionTracer.traceLock_do(opp.getLock_do().getTranHash());
            EthTransactionTracer.traceUnlock(opp.getUnlock().getTranHash());

            opp = gson.fromJson(servContract_judger.getProofs(Parameters.invokeId,BigInteger.valueOf(2)).send().getValue1(), OperationProof.class);
            FabricTransactionTracer.traceFabricTransaction(opp.getLock_do().getTranHash(),"lock_do");
            FabricTransactionTracer.traceFabricTransaction(opp.getUnlock().getTranHash(),"unlock");
        }
    }


    public static TransactionReceipt getReceiptFramBasechain(String tranHash) throws IOException {
        return web3j.ethGetTransactionReceipt(tranHash).send().getResult();
    }

    public static BigInteger getProofNum() throws Exception {
        return servContract_server.proofNum().send();
    }

//    public static void initBasechain(BigInteger totalOperationNum) throws Exception {
//        // init base chain
//        System.out.println("initing base chain....");
//        System.out.println("before initing, the service contract state is: "+servContract_server.SCState().send().toString());
//        TransactionReceipt bp= entryContract_client.initAllContract(totalOperationNum,Parameters.fenny.multiply(BigInteger.valueOf(2000))).send();
//        if (bp.getStatus().equals("0x0")){
//            throw new Exception("fail in initing base chain!");
//        }
//       // TransactionReceipt bp2=servContract_server.setTotalOperation(totalOperationNum).send();
//        System.out.println("after initing, the service contract state is: "+servContract_server.SCState().send().toString());
//        System.out.println("after initing, the service contract totalOperationNum is: "+servContract_server.totalOperation().send().toString());
//        System.out.println("finish initing base chain");
//        System.out.println();
//    }

    public static void initBasechainAsync(int totalOperation) throws Exception {
        System.out.println("initing base chain....");
        initCommunityAsync(becf.getServiceAddr());
        initServiceAsync(totalOperation,becf.getServiceAddr());
        //initServiceAsync2(totalOperation);
        initEntryAsync(becf.getCommunityAddr(),becf.getServiceAddr(),serverCre.getAddress());
        System.out.println("finish initing base chain");
    }
    public static void initBasechainNFTAsync(int totalOperation) throws Exception {
        System.out.println("initing base chain....");
        initCommunityAsync(becf.getNFTServiceAddr());
        initNFTServiceAsync(totalOperation,becf.getNFTServiceAddr());
        //initServiceAsync2(totalOperation);
        initNFTEntryAsync(becf.getCommunityAddr(),becf.getNFTServiceAddr(),serverCre.getAddress());
        System.out.println("finish initing base chain");
    }

    public static CallProof<TransactionReceipt> initCommunityAsync(String _servAddr) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing community....");
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(1600));

        // create our transaction
//        Function function = new Function(
//                "initDebug",
//                Arrays.<Type>asList(),
//                Collections.<TypeReference<?>>emptyList());
        Function function = new Function(
                "initDebug",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_servAddr)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);  
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getCommunityAddr(),sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send initAllContract  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in init community is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail init community  in base chain");
                }
                Utils.priteTime("finish  init community  in base chain");
                System.out.println("finish initing community in base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }

    public static CallProof<TransactionReceipt> addJudgerDebug(String _addr) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing community....");
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(200));
        Function function = new Function(
                "addJudgers",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getCommunityAddr(),sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send initAllContract  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in init community is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail init community  in base chain");
                }
                Utils.priteTime("finish  init community  in base chain");
                System.out.println("finish initing community in base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }



    public static CallProof<TransactionReceipt> initServiceAsync(int totalOperation,String service) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing service contract....");
        System.out.println("before initing, the service contract state is: "+servContract_server.SCState().send().toString());
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(10080));

        // create our transaction
        Function function = new Function(
                "initDebug",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(BigInteger.valueOf(totalOperation))),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);  
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,service,sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send init service contract  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in  init service contract is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail  init service contract in base chain");
                }

                Utils.priteTime("finish  init service contract in base chain");
                System.out.println("after initing, the service contract state is: "+servContract_server.SCState().send().toString());
                System.out.println("after initing, the service contract totalOperationNum is: "+servContract_server.totalOperation().send().toString());
                System.out.println("finish initing service contract base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }
    public static CallProof<TransactionReceipt> initNFTServiceAsync(int totalOperation,String service) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing service contract....");
        System.out.println("before initing, the service contract state is: "+servNFTContract_server.SCState().send().toString());
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(10080));

        // create our transaction
        Function function = new Function(
                "initDebug",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(BigInteger.valueOf(totalOperation))),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,service,sendWei,encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send init service contract  success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in  init service contract is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail  init service contract in base chain");
                }

                Utils.priteTime("finish  init service contract in base chain");
                System.out.println("after initing, the service contract state is: "+servNFTContract_server.SCState().send().toString());
                System.out.println("after initing, the service contract totalOperationNum is: "+servNFTContract_server.totalOperation().send().toString());
                System.out.println("finish initing service contract base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }

    public static CallProof<TransactionReceipt> initEntryAsync(String _community,String _servContract,String _serverAddr) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing entry contract....");
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        //BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(0));
        // create our transaction
        Function function = new Function(
                "initDebug",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_community),
                        new org.web3j.abi.datatypes.Address(_servContract),
                        new org.web3j.abi.datatypes.Address(_serverAddr)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);  
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getEntryAddr(),encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send initing entry contract success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in  initing entry contract is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail initing entry contract in base chain");
                }

                Utils.priteTime("finish initing entry contract in base chain");
                System.out.println("finish initing entry contract on base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }
    public static CallProof<TransactionReceipt> initNFTEntryAsync(String _community,String _servContract,String _serverAddr) throws Exception {
        //Credentials credentials = WalletUtils.loadCredentials(password,keyStore);
        System.out.println("initing entry contract....");
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                initManCre.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        //nonce=nonce.add(BigInteger.valueOf(nonceAdd));
        System.out.println("nonce:"+nonce);
        BigInteger gasPrice = BigInteger.valueOf(1L);
        BigInteger gasLimit = BigInteger.valueOf(8000000L);
        //BigInteger sendWei =Parameters.fenny.multiply(BigInteger.valueOf(0));
        // create our transaction
        Function function = new Function(
                "initDebug",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_community),
                        new org.web3j.abi.datatypes.Address(_servContract),
                        new org.web3j.abi.datatypes.Address(_serverAddr)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        //public static RawTransaction createTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data)
        RawTransaction transaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,becf.getNFTEntryAddr(),encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,initManCre);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        //EthSendTransaction ethSendTransaction = web3j2.ethSendRawTransaction(hexValue).send();
        // fullhash=0x085848e4cb97e1b3b38ac89aef3f9ffe128cf23facb52deef37aef76347dd131 recipient=0x4Fb44a868336eE79EdEC9c2163eE38438B8E6edf

        String transactionHash = ethSendTransaction.getTransactionHash();
        CompletableFuture<EthGetTransactionReceipt> cf=web3j.ethGetTransactionReceipt(transactionHash).sendAsync();
        EthGetTransactionReceipt re=cf.join();
        System.out.println("tranHash: "+transactionHash);
        if(ethSendTransaction.hasError())
        {
            String message=ethSendTransaction.getError().getMessage();
            System.out.println("transaction failed,info:"+message);
            // Utils.writeFile("F:/testErr.txt","transaction failed,info:"+message);
        }
        else
        {
            String hash=ethSendTransaction.getTransactionHash();
            EthGetTransactionReceipt send = web3j.ethGetTransactionReceipt(hash).send();
            System.out.println("Async send initing entry contract success");
        }
        //观察100秒
        TransactionReceipt rp=null;
        CallProof<TransactionReceipt> callProof=new CallProof<>();
        int b=0;
        while(b<500) {
            rp=web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if(rp!=null) {
                System.out.println("the transaction has on chain!");
                System.out.println("gas used in  initing entry contract is: " + rp.getGasUsed().toString() + "      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                EthProof ethProof = new EthProof(rp);
                callProof.setResult(rp);
                callProof.setChainType(ChainType.ETH);
                callProof.setBlockNumber(ethProof.getBlockNumber());
                callProof.setBlockHash(ethProof.getBlockHash());
                callProof.setTransaction(ethProof.getTransactionHash());
                callProof.setContract(ethProof.getContractAddress());
                callProof.setTransactionIndex(ethProof.getTransactionIndex());
                callProof.setRoot(ethProof.getRoot());
                callProof.setStatus("SUCCESS");
                if (ethProof.getStatus().equals("0x0")){
                    callProof.setStatus("FAIL");
                    throw new Exception("fail initing entry contract in base chain");
                }

                Utils.priteTime("finish initing entry contract in base chain");
                System.out.println("finish initing entry contract on base chain");
                System.out.println();
                return callProof;
            }
            Thread.sleep(200);
            b++;
        }
        System.out.println("the transaction has not on chain over 100 seconds!");
        throw new Exception("the transaction has not on chain over 100 seconds!");
    }




}
