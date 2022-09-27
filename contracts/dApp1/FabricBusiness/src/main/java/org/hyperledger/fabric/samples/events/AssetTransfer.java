package org.hyperledger.fabric.samples.events;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.samples.events.notuse.Sha256Utils.sha3;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.nio.charset.Charset;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Map;
import org.web3j.crypto.*;

@Contract(
        name = "asset-transfer",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer sample",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Fabric Development Team",
                        url = "https://hyperledger.example.com")))
@Default
public final class AssetTransfer implements ContractInterface {
    private static String creator="x509::CN=User1@org1.example.com, OU=client, L=San Francisco, ST=California, C=US::CN=ca.org1.example.com, O=org1.example.com, L=San Francisco, ST=California, C=US";
    private static String server;
    static final String IMPLICIT_COLLECTION_NAME_PREFIX = "_implicit_org_";
    static final String PRIVATE_PROPS_KEY = "asset_properties";

    private enum AssetTransferErrors {
        INCOMPLETE_INPUT,
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS,
        DATA_ERROR,
        PARAMETER_ERROR,
        READLOG_NOT_FOUND
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String SetServer(final Context ctx, final String serverId) {
        require(ctx.getClientIdentity().getId().equals(creator),"Caller is not dApp manager!");
        this.server=serverId;
        return this.server;
    }




    //----   business unit of decreasing asset----------------------------------------------
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean decressAsset_lock_do (final Context ctx,final String assetID, final int num, final String invokeId, final String lockHash) throws SignatureException {
        require((ctx.getClientIdentity().getId()).equals(server),"The operater is not the server!");
        ChaincodeStub stub = ctx.getStub();
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = "Empty input: assetID";
        }
        System.out.printf("lockAsset: verify asset %s exists\n", assetID);
        Asset thisAsset = getAssetState(ctx, assetID);
        if(thisAsset.getIfLock()){
            errorMessage = "Asset has been locked by others";
        }
        if(thisAsset.getTotal()<num){
            errorMessage = "Asset check false";
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }
        //lock
        String backup =assetID+","+num;
        thisAsset.setIfLock(true);
        thisAsset.setLockHash(lockHash);
        thisAsset.setBackup(backup);

        //do
        int total =thisAsset.getTotal()-num;
        if (total >= 0) {
            thisAsset.setTotal(total);
        }else{
            errorMessage = "Asset is less then "+num;
            throw new ChaincodeException(errorMessage, AssetTransferErrors.DATA_ERROR.toString());
        }

        System.out.printf(" lock and do Asset: ID %s by hash: %s\n", assetID, lockHash);
        savePrivateData(ctx, assetID); // save private data if any
        byte[] assetJSON = thisAsset.serialize();
        stub.putState(assetID, assetJSON);
        stub.setEvent("decressAsset_lock_do", assetJSON); //publish Event

        return true;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean decressAsset_unlock (final Context ctx,final String assetID,final int num, final String invokeId,final String hashKey) throws SignatureException {
        ChaincodeStub stub = ctx.getStub();
        //check
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = "Empty input: assetID";
        }
        Asset thisAsset = getAssetState(ctx, assetID);
        if(thisAsset.getIfLock()){
            if(!thisAsset.getLockHash().equals(new String(Hash.sha3(hashKey.getBytes()),Charset.forName("ISO-8859-1")))){
                errorMessage = "Asset is locked by others";
            }
        }else{
            errorMessage = "Can't call undo_unlock when asset is not locked!";
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }

        // unlock
        thisAsset.setIfLock(false);
        thisAsset.setLockHash(null);
        thisAsset.setBackup(null);

        savePrivateData(ctx, assetID); // save private data if any
        byte[] assetJSON = thisAsset.serialize();

        stub.putState(assetID, assetJSON);
        stub.setEvent("decressAsset_unlock", assetJSON); //publish Event
        return true;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void decressAsset_undo_unlock (final Context ctx,final String assetID,final int num,final String invokeId,String hashKey) throws SignatureException {
        ChaincodeStub stub = ctx.getStub();
        //ckeck
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = String.format("Empty input: assetID");
        }
        Asset asset = getAssetState(ctx, assetID);
        if(asset.getIfLock()){
            if(!asset.getLockHash().equals(new String(Hash.sha3(hashKey.getBytes()),Charset.forName("ISO-8859-1")))){
                errorMessage = "Asset is locked by others";
            }
        }else{
                errorMessage = "Can't call undo_unlock when asset is not locked!";
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }
        // undo
        String parameterOld =asset.getBackup();
        String[] parameterArray=parameterOld.split(",");
        String assetIDOld = parameterArray[0];
        int numOld=Integer.parseInt(parameterArray[1]);
        if (!(assetID.equals(assetIDOld)) || !(numOld==num)) {
            errorMessage="parameters not matched";
            throw new ChaincodeException(errorMessage, AssetTransferErrors.PARAMETER_ERROR.toString());
        }
        int total =asset.getTotal()+num;
        if (total >= 0) {
            asset.setTotal(total);
        }else{
            errorMessage = "Asset is less then "+num;
            throw new ChaincodeException(errorMessage, AssetTransferErrors.DATA_ERROR.toString());
        }
        //unlock
        asset.setIfLock(false);
        asset.setLockHash(null);
        asset.setBackup(null);

        savePrivateData(ctx, assetID);
        byte[] assetJSON = asset.serialize();
        stub.putState(assetID, assetJSON);
        stub.setEvent("decressAsset_undo_unlock", assetJSON); //publish Event
    }
    //------------------------------business unit of readding rate ------------------------------
    //original read
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public int getNumber(final Context ctx, final String assetID) {
        if (assetID == null || assetID.equals("")) {
            String errorMessage  = String.format("Empty input: assetID");
            throw new ChaincodeException(errorMessage, AssetTransferErrors.PARAMETER_ERROR.toString());
        }
        Asset asset = getAssetState(ctx, assetID);
        if(asset.getTotal()<100){
            return 2;
        }else {
            return 1;
        }
    }
    // atomic read
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public int getNumber_atomic(final Context ctx, final String assetID, final String invokeId) {
        //input validations
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage  = String.format("Empty input: assetID");
        }
        if (invokeId == null || invokeId.equals("")) {
            errorMessage = String.format("Empty input: invokeId");
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }

        int num=0;
        Asset asset = getAssetState(ctx, assetID);
        if(asset.getTotal()<100){
            num= 2;
        }else {
            num= 1;
        }
        // record the read value
        ChaincodeStub stub = ctx.getStub();
        ReadLog log = new ReadLog(invokeId,assetID,num);
        savePrivateData(ctx, invokeId);
        byte[] logJSON = log.serialize();
        stub.putState(invokeId, logJSON);
        stub.setEvent("ReadLogEvent", logJSON);
        return num;
    }
    private ReadLog getReadLog(final Context ctx, final String invokeId) {
        byte[] logJSON = ctx.getStub().getState(invokeId);
        if (logJSON == null || logJSON.length == 0) {
            String errorMessage = String.format("readRateInfo %s does not exist", invokeId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.READLOG_NOT_FOUND.toString());
        }
        try {
            ReadLog log = ReadLog.deserialize(logJSON);
            return log;
        } catch (Exception e) {
            throw new ChaincodeException("Deserialize error: " + e.getMessage(), AssetTransferErrors.DATA_ERROR.toString());
        }
    }

//    //------------------------------business unit of readding asset ------------------------------
//    //original read
  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public Asset ReadAsset(final Context ctx, final String assetID) {
      System.out.printf("ReadAsset: ID %s\n", assetID);
      Asset asset = getAssetState(ctx, assetID);
      byte[] assetJSON = asset.serialize();
      ChaincodeStub stub = ctx.getStub();
      stub.setEvent("ReadAsset", assetJSON); //publish Event
      return asset;
  }
    // atomic read
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset ReadAsset_atomic(final Context ctx, final String assetID, final String invokeId) {
        System.out.printf("ReadAsset: ID %s\n", assetID);
        //input validations
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = String.format("Empty input: assetID");
        }
        if (invokeId == null || invokeId.equals("")) {
            errorMessage = String.format("Empty input: invokeId");
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }
        Asset asset = getAssetState(ctx, assetID);
        //String assetID=invokeId;
        ChaincodeStub stub = ctx.getStub();
        AssetReadLog log = new AssetReadLog(invokeId,assetID, asset.getTotal());
        savePrivateData(ctx, invokeId);
        byte[] assetLogJSON = log.serialize();
        stub.putState(invokeId, assetLogJSON);
        stub.setEvent("AssetReadLog", assetLogJSON);
        return asset;
    }
    private AssetReadLog getAssetLog(final Context ctx, final String invokeId) {
        byte[] assetlogJSON = ctx.getStub().getState(invokeId);
        if (assetlogJSON == null || assetlogJSON.length == 0) {
            String errorMessage = String.format("assetInfo %s does not exist", invokeId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        try {
            AssetReadLog assetLog = AssetReadLog.deserialize(assetlogJSON);
            return assetLog;
        } catch (Exception e) {
            throw new ChaincodeException("Deserialize error: " + e.getMessage(), AssetTransferErrors.DATA_ERROR.toString());
        }
    }


    // -------------------- debug functions ------------------------------------
    //-------------------   Create  Asset   -----------------------
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset CreateAsset(final Context ctx, final String assetID, final int total) {
        ChaincodeStub stub = ctx.getStub();
        //input validations
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = String.format("Empty input: assetID");
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }
        // Check if asset already exists
        byte[] assetJSON = ctx.getStub().getState(assetID);
        if (assetJSON != null && assetJSON.length > 0) {
            errorMessage = String.format("Asset %s already exists", assetID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        Asset asset = new Asset(assetID, total,false,null,null);
        savePrivateData(ctx, assetID);
        assetJSON = asset.serialize();
        stub.putState(assetID, assetJSON);
        stub.setEvent("CreateAsset", assetJSON);
        return asset;
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String ReadLog(final Context ctx, final String invokeId) {
        System.out.printf("ReadAssetLog: ID %s\n", invokeId);
        ReadLog log = getReadLog(ctx, invokeId);
        String privData = readPrivateData(ctx, invokeId);
        byte[] logSON = log.serialize();
        String result =log.serialize(privData);
        //record read result
        ChaincodeStub stub = ctx.getStub();
        stub.setEvent("TraceReadRateLog", logSON); //publish Event
        return result;
    }
//    @Transaction(intent = Transaction.TYPE.EVALUATE)
//    public String ReadAssetLog(final Context ctx, final String assetID,final String invokeId) {
//        System.out.printf("ReadAssetLog: ID %s\n", invokeId);
//        AssetReadLog assetLog = getAssetLog(ctx, invokeId);
//        String privData = readPrivateData(ctx, invokeId);
//        byte[] assetJSON = assetLog.serialize();
//        String result =assetLog.serialize(privData);
//        //record read result
//        ChaincodeStub stub = ctx.getStub();
//        stub.setEvent("ReadAssetBLog", assetJSON); //publish Event
//        return result;
//    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public AssetReadLog CreateAssetReadLog(final Context ctx, final String assetID, final int total,final String invokeId) {
       // String assetID=assetID1+invokeId;
        ChaincodeStub stub = ctx.getStub();
        //input validations
        String errorMessage = null;
        if (invokeId == null || invokeId.equals("")) {
            errorMessage = String.format("Empty input: invokeId");
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }
        // Check if assetlog already exists
        byte[] assetlogJSON = ctx.getStub().getState(invokeId);
        if (assetlogJSON != null && assetlogJSON.length > 0) {
            errorMessage = String.format("AssetLog %s already exists", invokeId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        AssetReadLog assetlog = new AssetReadLog(invokeId,assetID, total);
        savePrivateData(ctx, invokeId);
        assetlogJSON = assetlog.serialize();
        stub.putState(invokeId, assetlogJSON);
        stub.setEvent("CreateAssetReadLog", assetlogJSON);
        return assetlog;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)  //for debug
    public Asset resetAssetDebug(final Context ctx, final String assetID, final int num) {
        server="x509::CN=User1@org2.example.com, OU=client, L=San Francisco, ST=California, C=US::CN=ca.org2.example.com, O=org2.example.com, L=San Francisco, ST=California, C=US";
        ChaincodeStub stub = ctx.getStub();
        //input validations
        String errorMessage = null;
        if (assetID == null || assetID.equals("")) {
            errorMessage = String.format("Empty input: assetID");
        }
        if (errorMessage != null) {
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.INCOMPLETE_INPUT.toString());
        }

        // Check if asset already exists
        Asset asset=null;
        byte[] assetJSON = ctx.getStub().getState(assetID);
        if (assetJSON != null && assetJSON.length > 0) { //asset exists
             asset = getAssetState(ctx, assetID);
            // reads from the Statedb. Check if asset already exists
            if (num >= 0) {
                asset.setTotal(num);
                asset.setIfLock(false);
                asset.setLockHash(null);
                asset.setBackup(null);
            }else{
                errorMessage = "parameter error: "+num;
                throw new ChaincodeException(errorMessage, AssetTransferErrors.DATA_ERROR.toString());
            }
            savePrivateData(ctx, assetID);
            assetJSON = asset.serialize();
            System.out.printf("UpdateAsset Put: ID %s Data %s\n", assetID, new String(assetJSON));
            stub.putState(assetID, assetJSON);
        }else { // asset not exists
             asset = new Asset(assetID, num, false, null, null);
            savePrivateData(ctx, assetID);
            assetJSON = asset.serialize();
            stub.putState(assetID, assetJSON);
        }
        stub.setEvent("ResetAssetDebug", assetJSON);
        return asset;
    }


    // ---------------------- tools ----------------------
    private String readPrivateData(final Context ctx, final String assetKey) {
        String peerMSPID = ctx.getStub().getMspId();
        String clientMSPID = ctx.getClientIdentity().getMSPID();
        String implicitCollectionName = getCollectionName(ctx);
        String privData = null;
        //only if ClientOrgMatchesPeerOrg
        if (peerMSPID.equals(clientMSPID)) {
            System.out.printf(" ReadPrivateData from collection %s, ID %s\n", implicitCollectionName, assetKey);
            byte[] propJSON = ctx.getStub().getPrivateData(implicitCollectionName, assetKey);

            if (propJSON != null && propJSON.length > 0) {
                privData = new String(propJSON, UTF_8);
            }
        }
        return privData;
    }

    private void savePrivateData(final Context ctx, final String assetKey) {
        String peerMSPID = ctx.getStub().getMspId();
        String clientMSPID = ctx.getClientIdentity().getMSPID();
        String implicitCollectionName = getCollectionName(ctx);

        if (peerMSPID.equals(clientMSPID)) {
            Map<String, byte[]> transientMap = ctx.getStub().getTransient();
            if (transientMap != null && transientMap.containsKey(PRIVATE_PROPS_KEY)) {
                byte[] transientAssetJSON = transientMap.get(PRIVATE_PROPS_KEY);

                System.out.printf("Asset's PrivateData Put in collection %s, ID %s\n", implicitCollectionName, assetKey);
                ctx.getStub().putPrivateData(implicitCollectionName, assetKey, transientAssetJSON);
            }
        }
    }

    private void removePrivateData(final Context ctx, final String assetKey) {
        String peerMSPID = ctx.getStub().getMspId();
        String clientMSPID = ctx.getClientIdentity().getMSPID();
        String implicitCollectionName = getCollectionName(ctx);

        if (peerMSPID.equals(clientMSPID)) {
            System.out.printf("PrivateData Delete from collection %s, ID %s\n", implicitCollectionName, assetKey);
            ctx.getStub().delPrivateData(implicitCollectionName, assetKey);
        }
    }

    // Return the implicit collection name, to use for private property persistance
    private String getCollectionName(final Context ctx) {
        // Get the MSP ID of submitting client identity
        String clientMSPID = ctx.getClientIdentity().getMSPID();
        String collectionName = IMPLICIT_COLLECTION_NAME_PREFIX + clientMSPID;
        return collectionName;
    }

    private Asset getAssetState(final Context ctx, final String identityID) {
        byte[] assetBJSON = ctx.getStub().getState(identityID);
        if (assetBJSON == null || assetBJSON.length == 0) {
            String errorMessage = String.format("assetInfo %s does not exist", identityID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        try {
            Asset asset = Asset.deserialize(assetBJSON);
            return asset;
        } catch (Exception e) {
            throw new ChaincodeException("Deserialize error: " + e.getMessage(), AssetTransferErrors.DATA_ERROR.toString());
        }
    }


    private Sign.SignatureData getSignData(final String r, final String  s, final String  v) {
        byte[] ss = s.getBytes(Charset.forName("ISO-8859-1"));
        byte[] rr = r.getBytes(Charset.forName("ISO-8859-1"));
        byte vv = v.getBytes(Charset.forName("ISO-8859-1"))[0];
        Sign.SignatureData signatureData = new Sign.SignatureData(vv, rr, ss);
        return signatureData;
    }
    private String getAddrss(String plainText,String signStr) throws SignatureException {
        byte[] sign=signStr.getBytes(Charset.forName("ISO-8859-1"));
        byte[] r =new byte[32];
        byte[] s =new byte[32];
        byte v=sign[sign.length-1];
        System.arraycopy(sign, 0, r, 0, 32);
        System.arraycopy(sign, 32, s, 0, 32);
        Sign.SignatureData signatureData =new Sign.SignatureData(v,r,s);
        String pubKey = Sign.signedMessageToKey(plainText.getBytes(), signatureData).toString(16);
        String signerAddress = Keys.getAddress(pubKey);
        return signerAddress;
    }

    private boolean checkIdentity(String plainText,String signStr,ArrayList<String>  list) throws SignatureException {
       return list.contains(getAddrss(plainText,signStr));
    }
    public void require(boolean condition, String message) {
        if(!condition) {
            System.out.println(message);
            throw new ChaincodeException(message);
        }
    }

}
