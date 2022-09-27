/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.events;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public final class Asset {

    @Property()
    private final String assetID;

    @Property()
    private int total;

    @Property()
    private Boolean ifLock;

    @Property()
    private String lockHash;

    @Property()
    private String backup;

    public Asset(final String assetID, final int value, final Boolean ifLock, final String lockHash, final String backup) {

        this.assetID = assetID;
        this.total = value;
        this.ifLock = ifLock;
        this.lockHash = lockHash;
        this.backup = backup;
    }


    public Boolean getIfLock() {
        return ifLock;
    }

    public void setIfLock(Boolean ifLock) {
        this.ifLock = ifLock;
    }

    public String getLockHash() {
        return lockHash;
    }

    public void setLockHash(String lockHash) {
        this.lockHash = lockHash;
    }

    public String getAssetID() {
        return assetID;
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }

    // Serialize asset without private properties
    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String privateProps) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("ID", assetID);
        tMap.put("total", Integer.toString(total));
        tMap.put("ifLock", ifLock.toString());
        if (lockHash != null && lockHash.length() > 0) {
            tMap.put("lockHash", lockHash);
        }
        if (backup != null && backup.length() > 0) {
            tMap.put("backup", backup);
        }
        if (privateProps != null && privateProps.length() > 0) {
            tMap.put("asset_properties", new JSONObject(privateProps));
        }

        return new JSONObject(tMap).toString();
    }

    public static Asset deserialize(final byte[] assetJSON) {
        return deserialize(new String(assetJSON, UTF_8));
    }

    public static Asset deserialize(final String assetJSON) {

        JSONObject json = new JSONObject(assetJSON);
        Map<String, Object> tMap = json.toMap();
        final String id = (String) tMap.get("ID");

        int total = 0;
        if (tMap.containsKey("total")) {
            total = Integer.parseInt((String) tMap.get("total"));
        }
        final Boolean ifLock = Boolean.valueOf( (String)tMap.get("ifLock"));
        String lockHash = null;
        if (tMap.containsKey("lockHash")) {
            lockHash = (String) tMap.get("lockHash");
        }
        String backup = null;
        if (tMap.containsKey("backup")) {
            backup = (String) tMap.get("backup");
        }
//        String readValue = null;
//        if (tMap.containsKey("readValue")) {
//            readValue = (String) tMap.get("readValue");
//        }
        return new Asset(id,total,ifLock,lockHash,backup);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[]{getAssetID()},
                new String[]{other.getAssetID()})
                &&
                Objects.deepEquals(
                        new int[]{getTotal()},
                        new int[]{other.getTotal()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetID(), getTotal());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + " [assetID=" + assetID + ", total=" + total + "]";
    }

}
