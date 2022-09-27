/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.events.notuse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;


import org.json.JSONObject;

@DataType()
public final class LimitInfo {

    @Property()
    private final String identityID;

    @Property()
    private int limitValue;

    @Property()
    private int ifApprove;

    public LimitInfo(final String identityID, final int limitValue) {

        this.identityID = identityID;
        this.limitValue = limitValue;
        this.ifApprove = 0;
    }
    public LimitInfo(final String identityID, final int limitValue,final int ifApprove) {

        this.identityID = identityID;
        this.limitValue = limitValue;
        this.ifApprove = ifApprove;
    }

    public String getIdentityID() {
        return identityID;
    }

    public int getLimitValue() {
        return limitValue;
    }

    //public void setIdentityID(final String identityID) {
    //   this.identityID = identityID;
    //}
    public void setLimitValue(final int limitValue) {
        this.limitValue = limitValue;
    }

    public int getIfApprove() {
        return ifApprove;
    }

    public void setIfApprove(int ifApprove) {
        this.ifApprove = ifApprove;
    }

    // Serialize asset without private properties
    public byte[] serialize() {
        return serialize(null).getBytes(UTF_8);
    }

    public String serialize(final String privateProps) {
        Map<String, Object> tMap = new HashMap();
        tMap.put("identityID", identityID);
        tMap.put("limitValue",  limitValue);
        tMap.put("ifApprove",  ifApprove);
        if (privateProps != null && privateProps.length() > 0) {
            tMap.put("limitInfo_properties", new JSONObject(privateProps));
        }
        return new JSONObject(tMap).toString();
    }

    public static LimitInfo deserialize(final byte[] assetJSON) {
        return deserialize(new String(assetJSON, UTF_8));
    }

    public static LimitInfo deserialize(final String assetJSON) {

        JSONObject json = new JSONObject(assetJSON);
        Map<String, Object> tMap = json.toMap();

        final String identityID = (String) tMap.get("identityID");
        int limitValue = (int) tMap.get("limitValue");
        int ifApprove = (int) tMap.get("ifApprove");
        return new LimitInfo(identityID, limitValue,ifApprove);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        LimitInfo other = (LimitInfo) obj;

        return Objects.deepEquals(
                new String[]{getIdentityID()},
                new String[]{other.getIdentityID()})
                &&
                Objects.deepEquals(
                        new int[]{getLimitValue(),getIfApprove()},
                        new int[]{other.getLimitValue(),other.getIfApprove()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentityID(), getLimitValue(), getIfApprove());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + " [limitValue=" + limitValue + ", limitValue=" + limitValue + ", ifApprove=" + ifApprove + "]";
    }

}
