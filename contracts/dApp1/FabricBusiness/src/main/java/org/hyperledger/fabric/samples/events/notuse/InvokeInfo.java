/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.events.notuse;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public final class InvokeInfo {

    @Property()
    private int nowID;

    @Property()
    private int startID;

    @Property()
    private int endID;

    @Property()
    private Boolean ifLock;

    @Property()
    private byte[] lockHash;

    public InvokeInfo(final int nowID, final int startID, final int endID,Boolean ifLock,byte[] lockHash) {

        this.nowID = nowID;
        this.startID = startID;
        this.endID = endID;
        this.ifLock = ifLock;
        this.lockHash = lockHash;
    }

    public int getNowID() {
        return nowID;
    }

    public void setNowID(int nowID) {
        this.nowID = nowID;
    }

    public int getStartID() {
        return startID;
    }

    public void setStartID(int startID) {
        this.startID = startID;
    }

    public int getEndID() {
        return endID;
    }

    public void setEndID(int endID) {
        this.endID = endID;
    }

    public Boolean isIfLock() {
        return ifLock;
    }

    public void setIfLock(Boolean ifLock) {
        this.ifLock = ifLock;
    }

    public byte[] getLockHash() {
        return lockHash;
    }

    public void setLockHash(byte[] lockHash) {
        this.lockHash = lockHash;
    }

    // Serialize asset without private properties
    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String privateProps) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("nowID", Integer.toString(nowID));
        tMap.put("startID", Integer.toString(startID));
        tMap.put("endID", Integer.toString(endID));
        tMap.put("ifLock", ifLock.toString());
        if (lockHash != null && lockHash.length > 0) {
            tMap.put("lockHash", lockHash.toString());
        }
        if (privateProps != null && privateProps.length() > 0) {
            tMap.put("asset_properties", new JSONObject(privateProps));
        }

        return new JSONObject(tMap).toString();
    }

    public static InvokeInfo deserialize(final byte[] assetJSON) {
        return deserialize(new String(assetJSON, UTF_8));
    }

    public static InvokeInfo deserialize(final String assetJSON) {

        JSONObject json = new JSONObject(assetJSON);
        Map<String, Object> tMap = json.toMap();
        final int nowID = Integer.parseInt((String) tMap.get("nowID"));
        final int startID = Integer.parseInt((String) tMap.get("startID"));
        final int endID = Integer.parseInt((String) tMap.get("endID"));

        final Boolean ifLock = Boolean.valueOf(tMap.get("ifLock").toString());
        byte[] lockHash = null;
        if (tMap.containsKey("lockHash")) {
            lockHash = ((String) tMap.get("lockHash")).getBytes();
        }
        return new InvokeInfo(nowID,startID,endID,ifLock,lockHash);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        InvokeInfo other = (InvokeInfo) obj;

        return
                //Objects.deepEquals(
                //new String[]{getStartID()},
                //new String[]{other.getAssetID()})
                //&&
                Objects.deepEquals(
                        new int[]{getStartID(),getEndID(),getNowID()},
                        new int[]{other.getStartID(),other.getEndID(),other.getNowID()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNowID(),getStartID(), getEndID());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + " [nowID=" + nowID + ", startID=" + startID + ", endID="+endID+"]";
    }

}
