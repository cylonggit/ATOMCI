package com.chen.crossTest.service;

import com.alibaba.fastjson.JSONObject;
import com.chen.crossTest.config.FabricConnectConf;
import com.chen.crossTest.mybatis.LabLogMapper;
import com.chen.crossTest.pojo.*;
import com.chen.crossTest.utils.Utils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.TransactionImpl;
import org.hyperledger.fabric.protos.peer.ProposalPackage;
import org.hyperledger.fabric.protos.peer.ProposalResponsePackage;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

public class FabricService {
    public FabricService() throws CertificateException, InvalidKeyException, IOException {
    }
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    //fabric，business contract
    static  FabricConnectConf cf; //注册的fabric信息
    static  FabricConnectConf cf2; //注册的fabric信息
    static {
        try {
            cf = new FabricConnectConf("FABRIC-CONF-001", Paths.get("src", "main", "resources", "connection.json"), Paths.get("src", "main", "resources", "crypto-config",
                    "peerOrganizations", "org2.example.com", "users", "User1@org2.example.com", "msp"), "mychannel",
                    "/Fabric.properties"); // chainCodeName

            cf2 = new FabricConnectConf("org2user1", Paths.get("src", "main", "resources", "connection.json"), Paths.get("src", "main", "resources", "crypto-config",
                    "peerOrganizations", "org2.example.com", "users", "User1@org2.example.com", "msp"), "mychannel",
                    "/FabricNFT.properties"); // chainCodeName
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    // get the gatwey  network and contract
    static  Gateway gateway = cf.getBuilder().connect();
    static  Network network = gateway.getNetwork(cf.getChannelName());
    static  Contract fabricBusinessContract = network.getContract(cf.getChainCodeName());
    static  Channel channel = network.getChannel();

    static  Gateway gateway2 = cf2.getBuilder().connect();
    static  Network network2 = gateway2.getNetwork(cf2.getChannelName());
    static  Contract fabricBusinessContract2 = network2.getContract(cf2.getChainCodeName());
    static  Channel channel2 = network2.getChannel();

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
    }
    public static void insertLog(LabLog log){
        logMapper.addLablog(log);
        sqlSession.commit();  //执行增删改是需手动提交数据
    }
    
    
    public static CallProof  decressAsset_lock_do(Date testId,String labType,int operation,BigInteger num,ClientArgs args) {
        Utils.priteTime("start operation "+operation+" lock_do in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());
        TransactionImpl tr = fabricBusinessContract.createTransactionImpl("decressAsset_lock_do").setEndorsingPeers2(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(args.getAssetId(), String.valueOf(num), args.getInvokeId(), Parameters.lockHashStr);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" lock_do");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" lock_do in fabric");
        return callProof;
    }

    public static CallProof  crossChainTransfer_lock_do(Date testId, String labType, int operation, NFTCrossArgs args) {
        Utils.priteTime("start operation "+operation+" lock_do in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());
        TransactionImpl tr = fabricBusinessContract2.createTransactionImpl("crossChainTransfer_lock_do").setEndorsingPeers2(network2.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(String.valueOf(args.getType()),String.valueOf(args.getTokenId()), args.getEthAddr(),args.getMsp(),args.getFabricSign(),args.getInvokeId(), Parameters.lockHashStr);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel2.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" lock_do");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" lock_do in fabric");
        return callProof;
    }
    public static CallProof  decressAsset_unlock(Date testId,String labType,int operation,BigInteger num,ClientArgs args) {
        Utils.priteTime("start operation "+operation+" unlock in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());
        TransactionImpl tr = fabricBusinessContract.createTransactionImpl("decressAsset_unlock").setEndorsingPeers2(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(Parameters.assetId, Parameters.numString, Parameters.invokeId, Parameters.hashKey);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" unlock");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" lock_do in fabric");
        return callProof;
    }

    public static CallProof  crossChainTransfer_unlock(Date testId,String labType,int operation,NFTCrossArgs args) {
        Utils.priteTime("start operation "+operation+" unlock in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());
        TransactionImpl tr = fabricBusinessContract2.createTransactionImpl("crossChainTransfer_unlock").setEndorsingPeers2(network2.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(String.valueOf(args.getType()),String.valueOf(args.getTokenId()), args.getEthAddr(),args.getMsp(),args.getFabricSign(),args.getInvokeId(), Parameters.hashKey);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel2.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" unlock");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" lock_do in fabric");
        return callProof;
    }


    public static CallProof  decressAsset_undo_unlock(Date testId,String labType,int operation) {
        Utils.priteTime("start operation "+operation+" undo_unlock in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());
        TransactionImpl tr = fabricBusinessContract.createTransactionImpl("decressAsset_undo_unlock").setEndorsingPeers2(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(Parameters.assetId, Parameters.numString, Parameters.invokeId, Parameters.hashKey);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" undo_unlock");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" undo_unlock in fabric");
        return callProof;
    }

    public static CallProof ReadAssetAtomic(Date testId, String labType, int operation) {
        Utils.priteTime("start operation "+operation+" atomic read in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());

        TransactionImpl tr =  fabricBusinessContract.createTransactionImpl("ReadAsset_atomic").setEndorsingPeers2(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(Parameters.assetId,Parameters.invokeId);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" atomic read");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" atomic read in fabric");
        return callProof;
    }

    public static CallProof getNumberAtomic(Date testId, String labType, int operation) {
        Utils.priteTime("start operation "+operation+" atomic read in fabric");
        CallProof<byte[]> callProof = new CallProof<>();
        callProof.setChainType(ChainType.FABRIC);
        byte[] result = null;
        String  start2  =  sdf.format(new Date());

        TransactionImpl tr =  fabricBusinessContract.createTransactionImpl("getNumber_atomic").setEndorsingPeers2(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)));
        System.out.println("TransactionId:" + tr.getTransactionId());

        //取回endorser的模型运行回复
        Collection<ProposalResponse> proposalResponses = tr.endorseTransaction(Parameters.assetId,Parameters.invokeId);
        Collection<ProposalResponse> validResponses = null;
        try {
            //验证背书
            validResponses = tr.validatePeerResponses(proposalResponses);
            //向order提交已被背书的交易
            result = tr.commitTransaction(validResponses);
            System.out.println("result: " + new String(result, "UTF8"));
            BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionId());
            System.out.println(blockInfo.getBlockNumber());
            callProof.setTransaction(tr.getTransactionId());
            callProof.setResult(result);
            callProof.setBlockNumber(BigInteger.valueOf(blockInfo.getBlockNumber()));
            callProof.setBlockHash(new String(blockInfo.getDataHash(), Charset.forName("ISO-8859-1")));
            //proof
            FabricProof fabricProof = new FabricProof();
            fabricProof.setBlockNum(callProof.getBlockNumber());
            fabricProof.setBlockHash(callProof.getBlockHash());
            for (ProposalResponse p : validResponses) {
                String chaincodeID = p.getChaincodeID().getName();
                System.out.println(chaincodeID);
                String transactionID = p.getTransactionID();

                if (fabricProof.getProposals() == null || fabricProof.getProposals().isEmpty()) {
                    ProposalPackage.Proposal pro = p.getProposal();
                    byte[] proposal = p.getProposal().toByteArray();
                    String proposalStr = new String(proposal, Charset.forName("ISO-8859-1"));
                    System.out.println("proposalStr length:" + proposalStr.length());
                    fabricProof.setProposals(proposalStr);
                }
                ProposalResponsePackage.Endorsement en = p.getProposalResponse().getEndorsement();
                byte[] endorsementBytes = p.getProposalResponse().getEndorsement().toByteArray();
                String endorStr = new String(endorsementBytes, Charset.forName("ISO-8859-1"));
                fabricProof.getEndorsements().add(endorStr);
                System.out.println("endorStr length:" + endorStr.length());
            }
            //JSONObject jsonObject = (JSONObject) JSONObject.toJSON(fabricProof);
            String proofString = JSONObject.toJSONString(fabricProof);
//                            System.out.println("before gzip,the proof length:"+proofString.length());
//                            String gzipProof= ZipUtil.gzip(proofString);
//                            System.out.println("after gzip,the proof length::"+gzipProof.length());
//                            callProof.setProof(gzipProof);
            callProof.setProof(proofString);
            callProof.setStatus("SUCCESS");
        } catch (Exception e) {
            callProof.setStatus("FAIL");
            callProof.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String  end2  =  sdf.format(new Date());
        LabLog log1 = new LabLog(testId,labType, callProof.getTransaction(), start2, end2, null, callProof.getBlockNumber(), "operation "+operation+" atomic read");
        insertLog(log1);
        Utils.priteTime("finish operation "+operation+" atomic read in fabric");
        return callProof;
    }

    public static void initFabricBusinessChain() throws Exception {
        System.out.println("initing fabric chain data...");
        fabricBusinessContract.submitTransaction("resetAssetDebug",Parameters.assetId, String.valueOf(Parameters.initNum));
        System.out.println("we reset the asset001 to 1000");
        byte[] result5 = fabricBusinessContract.evaluateTransaction("ReadAsset","asset001");
        System.out.println("read fabric asset: " + new String(result5, "UTF8"));
        System.out.println("finish initing fabric chain data.");
        System.out.println("\n");
    }

    public static byte[] initNFTFabricBusinessChain(String type) throws Exception {
        System.out.println("initing fabric chain data...");
        byte[] result=fabricBusinessContract2.submitTransaction("initNFTDebug",String.valueOf(Parameters.nftTokenID),type,Parameters.pubKey,Parameters.ownerLock);
        System.out.println("we initing the nft");
        try {
            byte[] result5 = fabricBusinessContract2.evaluateTransaction("getNFT", String.valueOf(Parameters.nftTokenID));
            System.out.println("getNFT: " + new String(result5, "UTF8"));
        }catch (Exception e){
            System.out.println("nft has been deleted");
        }
        System.out.println("finish initing fabric chain data.");
        System.out.println("\n");
        return result;
    }


}
