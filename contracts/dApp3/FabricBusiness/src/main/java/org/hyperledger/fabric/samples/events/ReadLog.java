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
public final class ReadLog {
    @Property()
    private final String invokeId;

    @Property()
    private String assetId;

    @Property()
    private int num;


    public ReadLog(final String invokeId, final String assetId, final int value) {

        this.assetId = assetId;
        this.num = value;
        this.invokeId=invokeId;

    }

    public String getInvokeId() {
        return invokeId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    // Serialize asset without private properties
    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String privateProps) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("ID", invokeId);
        tMap.put("assetId", assetId);
        tMap.put("num", Integer.toString(num));
        if (privateProps != null && privateProps.length() > 0) {
            tMap.put("asset_properties", new JSONObject(privateProps));
        }

        return new JSONObject(tMap).toString();
    }

    public static ReadLog deserialize(final byte[] assetJSON) {
        return deserialize(new String(assetJSON, UTF_8));
    }

    public static ReadLog deserialize(final String assetJSON) {

        JSONObject json = new JSONObject(assetJSON);
        Map<String, Object> tMap = json.toMap();
        final String id = (String) tMap.get("ID");
        final String assetId = (String) tMap.get("assetId");
        int num = 0;
        if (tMap.containsKey("num")) {
            num = Integer.parseInt((String) tMap.get("num"));
        }
        return new ReadLog(id,assetId,num);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        ReadLog other = (ReadLog) obj;

        return Objects.deepEquals(
                        new String[]{getInvokeId()},
                        new String[]{other.getInvokeId()})
                &&
                Objects.deepEquals(
                        new String[]{getAssetId()},
                        new String[]{other.getAssetId()})
                &&
                Objects.deepEquals(
                        new int[]{getNum()},
                        new int[]{other.getNum()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInvokeId(), getAssetId(), getNum());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + " [invokeId=" + invokeId + ", assetId=" + assetId + ",num="+ num +"]";
    }

}
