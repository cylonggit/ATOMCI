/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example.state.NFT;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.commons.lang3.StringUtils;
import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class NFT extends State {

    @Property()
    private String tokenId;

    @Property()
    private String owner;

    @Property()
    private String tokenApproval;

    @Property()
    private String data;
    @Property()
    private String publicKey;

    @Property()
    private String ownerLock;

    @Property()
    private String ifLock;

    @Property()
    private String lockHash;

    @Property()
    private String backup;


    public NFT() {
        super();
    }

    public NFT setKey() {
        this.key = this.tokenId;
        return this;
    }

    public String getTokenId() {
        return tokenId;
    }

    public NFT setTokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public NFT setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getTokenApproval() {
        return tokenApproval;
    }

    public NFT setTokenApproval(String tokenApproval) {
        this.tokenApproval = tokenApproval;
        return this;
    }

    public String getData() {
        return data;
    }

    public NFT setData(String data) {
        this.data = data;
        return this;
    }
    public String getPublicKey() {
        return publicKey;
    }

    public NFT setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String getOwnerLock() {
        return ownerLock;
    }

    public NFT setOwnerLock(String ownerLock) {
        this.ownerLock = ownerLock;
        return this;
    }

    public String getIfLock() {
        return ifLock;
    }

    public NFT setIfLock(String ifLock) {
        this.ifLock = ifLock;
        return this;
    }

    public String getLockHash() {
        return lockHash;
    }

    public NFT setLockHash(String lockHash) {
        this.lockHash = lockHash;
        return this;
    }

    public String getBackup() {
        return backup;
    }

    public NFT setBackup(String backup) {
        this.backup = backup;
        return this;
    }

    /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */
    public static NFT deserialize(byte[] _data) {
        JSONObject json = new JSONObject(new String(_data, UTF_8));
        String tokenId = json.getString("tokenId");
        String owner = json.getString("owner");
        String tokenApproval = json.getString("tokenApproval");
        String data = json.getString("data");
        String publicKey = json.getString("publicKey");
        String ownerLock = json.getString("ownerLock");
        String ifLock = json.getString("ifLock");
        String lockHash = json.getString("lockHash");
        String backup = json.getString("backup");
//        String lockHash = null;
//        if (StringUtils.isNotBlank(json.getString("lockHash")) ) {
//            lockHash = json.getString("lockHash");
//        }
//        String backup = null;
//        if (StringUtils.isNotBlank(json.getString("backup"))) {
//            backup = json.getString("backup");
//        }
        return createInstance(tokenId, owner, tokenApproval, data,publicKey,ownerLock,ifLock,lockHash,backup);
    }

    public static byte[] serialize(NFT paper) {
        return State.serialize(paper);
    }

    /**
     * Factory method to create a commercial paper object
     */
    public static NFT createInstance(String tokenId, String owner, String tokenApproval, String data,String publicKey,String ownerLock, String ifLock,  String lockHash,  String backup) {
        return new NFT()
            .setTokenId(tokenId)
            .setOwner(owner)
            .setTokenApproval(tokenApproval)
            .setData(data)
            .setPublicKey(publicKey)
            .setOwnerLock(ownerLock)
            .setIfLock(ifLock)
            .setLockHash(lockHash)
            .setBackup(backup)
            .setKey();
    }
}
