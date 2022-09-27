package org.hyperledger.fabric.samples.events.notuse;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Result {
    @Property()
    String status;
    @Property()
    byte[] results;

    public Result(final String status, final byte[] results) {
        this.status = status;
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getResults() {
        return results;
    }

    public void setResults(byte[] results) {
        this.results = results;
    }
}
