package org.hyperledger.fabric.gateway.impl;

import org.hyperledger.fabric.sdk.BlockEvent;

public class OrderResponse {
    byte[] result;
    BlockEvent.TransactionEvent transactionEvent;

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public BlockEvent.TransactionEvent getTransactionEvent() {
        return transactionEvent;
    }

    public void setTransactionEvent(BlockEvent.TransactionEvent transactionEvent) {
        this.transactionEvent = transactionEvent;
    }
}
