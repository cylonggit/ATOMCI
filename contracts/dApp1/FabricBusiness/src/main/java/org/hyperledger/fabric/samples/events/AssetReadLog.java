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
public final class AssetReadLog {
    @Property()
    private final String invokeId;

    @Property()
    private String assetID;

    @Property()
    private int total;


    public AssetReadLog(final String invokeId, final String assetID, final int value) {

        this.assetID = assetID;
        this.total = value;
        this.invokeId=invokeId;

    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getInvokeId() {
        return invokeId;
    }


    // Serialize asset without private properties
    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String privateProps) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("ID", invokeId);
        tMap.put("assetID", assetID);
        tMap.put("total", Integer.toString(total));
        if (privateProps != null && privateProps.length() > 0) {
            tMap.put("asset_properties", new JSONObject(privateProps));
        }

        return new JSONObject(tMap).toString();
    }

    public static AssetReadLog deserialize(final byte[] assetJSON) {
        return deserialize(new String(assetJSON, UTF_8));
    }

    public static AssetReadLog deserialize(final String assetJSON) {

        JSONObject json = new JSONObject(assetJSON);
        Map<String, Object> tMap = json.toMap();
        final String id = (String) tMap.get("ID");
        String assetID = null;
        if (tMap.containsKey("assetID")) {
            assetID = (String) tMap.get("assetID");
        }
        int total = 0;
        if (tMap.containsKey("total")) {
            total = Integer.parseInt((String) tMap.get("total"));
        }
        return new AssetReadLog(id,assetID,total);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        AssetReadLog other = (AssetReadLog) obj;

        return Objects.deepEquals(
                        new String[]{getInvokeId()},
                        new String[]{other.getInvokeId()})
                &&
                Objects.deepEquals(
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
                + " [invokeId=" + invokeId + ", assetID=" + assetID + ",total="+total+"]";
    }

}
