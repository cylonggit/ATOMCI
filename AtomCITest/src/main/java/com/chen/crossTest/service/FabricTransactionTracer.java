package com.chen.crossTest.service;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.math.BigInteger;

import static com.chen.crossTest.service.FabricService.channel;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

public class FabricTransactionTracer {

    public static void traceFabricTransaction(String transactionHash,String funcName) throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
        System.out.println("State tracing "+funcName+" sub_invocation in MyFabric-1 blockchain --------------");
        TransactionInfo tr = channel.queryTransactionByID(transactionHash);
        BlockInfo blockInfo = channel.queryBlockByTransactionID(tr.getTransactionID());
        System.out.println(tr.getProcessedTransaction().getAllFields());
        BlockInfo.EnvelopeInfo myEnvelopeInfo=null;
        for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE && envelopeInfo.getTransactionID().equals(tr.getTransactionID())){
                myEnvelopeInfo=envelopeInfo;
                break;
            }
        }
        myEnvelopeInfo.getCreator().getMspid();
        BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) myEnvelopeInfo;
        final String channelId = myEnvelopeInfo.getChannelId();
        out("  Transaction is in channel: %s", channelId);
        //out("  Transaction has %d actions", transactionEnvelopeInfo.getTransactionActionInfoCount());
        //assertEquals(1, transactionEnvelopeInfo.getTransactionActionInfoCount()); // for now there is only 1 action per transaction.
        out("  Transaction isValid: %b", transactionEnvelopeInfo.isValid());
        //assertTrue(transactionEnvelopeInfo.isValid());
        //out("  Transaction validation code %d", transactionEnvelopeInfo.getValidationCode());
        //assertEquals(0, transactionEnvelopeInfo.getValidationCode());
        int j = 0;
        for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
            ++j;
            out("   Transaction action %d has response status: %d", j, transactionActionInfo.getResponseStatus());
//            out("   Transaction action %d has response message bytes as string: %s", j,
//                    printableString(new String(transactionActionInfo.getResponseMessageBytes(), UTF_8)));
            out("   Transaction action %d has %d endorsements", j, transactionActionInfo.getEndorsementsCount());
            //assertEquals(2, transactionActionInfo.getEndorsementsCount());
            for (int n = 0; n < transactionActionInfo.getEndorsementsCount(); ++n) {
                BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(n);
                out("Endorser %d signature: %s", n, Hex.encodeHexString(endorserInfo.getSignature()));
                out("Endorser %d endorser: mspid %s \n certificate %s", n, endorserInfo.getMspid(), endorserInfo.getId());
            }
            out("   Transaction action %d has %d chaincode input arguments", j, transactionActionInfo.getChaincodeInputArgsCount());
            for (int z = 0; z < transactionActionInfo.getChaincodeInputArgsCount(); ++z) { // here to see the invokeId arguments
                out("     Transaction action %d has chaincode input argument %d is: %s", j, z,
                        printableString(new String(transactionActionInfo.getChaincodeInputArgs(z), UTF_8)));
            }

            out("   Transaction action %d proposal response status: %d", j,
                    transactionActionInfo.getProposalResponseStatus());
            out("   Transaction action %d proposal response payload: %s", j,
                    printableString(new String(transactionActionInfo.getProposalResponsePayload())));

            String chaincodeIDName = transactionActionInfo.getChaincodeIDName();
            String chaincodeIDVersion = transactionActionInfo.getChaincodeIDVersion();
            out("   Transaction action %d proposal chaincodeIDName: %s, chaincodeIDVersion: %s", j,
                    chaincodeIDName, chaincodeIDVersion);

            TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
            if (null != rwsetInfo) {
                out("   Transaction action %d has %d name space read write sets", j, rwsetInfo.getNsRwsetCount());

                for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
                    final String namespace = nsRwsetInfo.getNamespace();
                    KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

                    int rs = -1;
                    for (KvRwset.KVRead readList : rws.getReadsList()) {
                        rs++;

                        out("     Namespace %s read set %d key %s  version [%d:%d]", namespace, rs, readList.getKey(),
                                readList.getVersion().getBlockNum(), readList.getVersion().getTxNum());
//                        if ("bar".equals(channelId) && blockNumber == 2) {
//                            if ("example_cc_go".equals(namespace)) {
//                                if (rs == 0) {
//                                    assertEquals("a", readList.getKey());
//                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                    assertEquals(0, readList.getVersion().getTxNum());
//                                } else if (rs == 1) {
//                                    assertEquals("b", readList.getKey());
//                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                    assertEquals(0, readList.getVersion().getTxNum());
//                                } else {
//                                    fail(format("unexpected readset %d", rs));
//                                }
//
//                                TX_EXPECTED.remove("readset1");
//                            }
//                        }
                    }

                    rs = -1;
                    for (KvRwset.KVWrite writeList : rws.getWritesList()) {
                        rs++;
                        String valAsString = printableString(new String(writeList.getValue().toByteArray(), UTF_8));

                        out("     Namespace %s write set %d key %s has value '%s' ", namespace, rs,
                                writeList.getKey(),
                                valAsString);
//                        if ("bar".equals(channelId) && blockNumber == 2) {
//                            if (rs == 0) {
//                                assertEquals("a", writeList.getKey());
//                                assertEquals("400", valAsString);
//                            } else if (rs == 1) {
//                                assertEquals("b", writeList.getKey());
//                                assertEquals("400", valAsString);
//                            } else {
//                                fail(format("unexpected writeset %d", rs));
//                            }
//
//                            TX_EXPECTED.remove("writeset1");
//                        }
                    }
                }
            }

        }


        System.out.println("finish tracing "+funcName+" sub_invocation in MyFabric-1 blockchain --------------");
    }
    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }
    static String printableString(final String string) {
        int maxLogStringLength = 64;
        if (string == null || string.length() == 0) {
            return string;
        }

        String ret = string.replaceAll("[^\\p{Print}]", "?");

        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

        return ret;

    }


}
