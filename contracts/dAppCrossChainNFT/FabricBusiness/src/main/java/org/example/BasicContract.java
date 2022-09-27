/*
SPDX-License-Identifier: Apache-2.0
*/
package org.example;

import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.example.constant.ContractConstant;
import org.example.event.ApprovalEvent;
import org.example.event.ApprovalForAllEvent;
import org.example.event.CrossTransferEvent;
import org.example.event.TransferEvent;
import org.example.ledgerapi.State;
import org.example.state.Balance.Balance;
import org.example.state.NFT.NFT;
import org.example.state.NFT.NFTList;
import org.example.state.OperatorApproval.OperatorApproval;
import org.example.utils.Base64;
import org.example.utils.RSAEncrypt;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.web3j.crypto.Hash;

@Contract(
        name = "NFT",
        info = @Info(
                title = "NFT",
                description = "The hyperlegendary NFT",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "NFT Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public class BasicContract implements ContractInterface, IERC721 {

    // use the classname for the logger, this way you can refactor
    private final static Logger LOG = Logger.getLogger(BasicContract.class.getName());
    private static String creator="x509::CN=User1@org1.example.com, OU=client, L=San Francisco, ST=California, C=US::CN=ca.org1.example.com, O=org1.example.com, L=San Francisco, ST=California, C=US";

    private static String server;

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new ERC721Context(stub);
    }

    public BasicContract() {

    }

    @Transaction
    public void instantiate(ERC721Context ctx) {
        // No implementation required with this example
        // It could be where data migration is performed, if necessary
        LOG.info("No data migration to perform");
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String SetServer(final Context ctx, final String serverId) {
        require(ctx.getClientIdentity().getId().equals(creator),"Caller is not dApp manager!");
        this.server=serverId;
        return this.server;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String msgSender(ERC721Context ctx) {
        return ctx.getClientIdentity().getId();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public int balanceOf(ERC721Context ctx, String owner) {
        require(owner != Utils.address(0), "balance query for the zero address");
        Balance ownerBalance = ctx.balanceList.getBalance(owner);
        if(null == ownerBalance) {
            return 0;
        }

        return ownerBalance.getBalance();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String ownerOf(ERC721Context ctx, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();
        require(owner != Utils.address(0), "balance query for the zero address");
        return owner;
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void safeTransferFrom(ERC721Context ctx, String from, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        require(to != Utils.address(0), "transfer to the zero address");
        require(_isApprovedOrOwner(ctx, msgSender, tokenId), "transfer caller is not owner nor approved");

        _transferFrom(ctx, from, to, tokenId, "");
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void transferFrom(ERC721Context ctx, String from, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        require(to != Utils.address(0), "transfer to the zero address");
        require(_isApprovedOrOwner(ctx, msgSender, tokenId), "transfer caller is not owner nor approved");

        _transferFrom(ctx, from, to, tokenId, "");
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void approve(ERC721Context ctx, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        NFT nft = ctx.nftList.getNFT(tokenId);
        String owner = nft.getOwner();
        
        require(to != owner, "approval to current owner");

        require(
            msgSender.equals(owner) || isApprovedForAll(ctx, owner, msgSender),
            "approve caller is not owner nor approved for all"
        );
       
        _approve(ctx, to, tokenId);
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void setApprovalForAll(ERC721Context ctx, String operator, boolean approved) {
        String msgSender = ctx.getClientIdentity().getId();
        require(msgSender != operator, "approve to caller");

        OperatorApproval operatorApproval = OperatorApproval.createInstance(msgSender, operator, approved);
        ctx.operatorApprovalList.updateOperatorApproval(operatorApproval);

        ApprovalForAllEvent event = new ApprovalForAllEvent().setOwner(msgSender).setOperator(operator).setApproved(approved);
        ctx.getStub().setEvent("Approval", event.serialize());
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getApproved(ERC721Context ctx, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        return nft.getTokenApproval();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isApprovedForAll(ERC721Context ctx, String owner, String operator) {
        String key = OperatorApproval.makeKey(owner, operator);
        OperatorApproval operatorApproval = ctx.operatorApprovalList.getOperatorApproval(key);
        if(null == operatorApproval) {
            return false;
        }

        return operatorApproval.isApproved();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public NFT mint(ERC721Context ctx, String to, String tokenId,String publicKey) {
        require(to != Utils.address(0), "mint to the zero address");
        require(
            !_exists(ctx, tokenId),
            String.format("token %s already minted", tokenId)
        );

        NFT nft = NFT.createInstance(tokenId, to, to, "",publicKey,"","false","","");
        ctx.nftList.addNFT(nft);

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null == toBalance) {
            Balance newBalance = Balance.createInstance(to, 1);
            ctx.balanceList.addBalance(newBalance);
        } else {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }

        TransferEvent event = new TransferEvent().setFrom(Utils.address(0)).setTo(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());

        return nft;
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public NFT mintWithLock(ERC721Context ctx, String to, String tokenId,String publicKey,String ownerLock) {
        require(to != Utils.address(0), "mint to the zero address");
        require(
                !_exists(ctx, tokenId),
                String.format("token %s already minted", tokenId)
        );

        NFT nft = NFT.createInstance(tokenId, to, to, "",publicKey,ownerLock,"false","","");
        ctx.nftList.addNFT(nft);

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null == toBalance) {
            Balance newBalance = Balance.createInstance(to, 1);
            ctx.balanceList.addBalance(newBalance);
        } else {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }

        TransferEvent event = new TransferEvent().setFrom(Utils.address(0)).setTo(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());

        return nft;
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void burn(ERC721Context ctx, String tokenId) {
        require(
                _exists(ctx, tokenId),
                String.format("token %s does not exist!", tokenId)
        );
        require(_isOwner(ctx,msgSender(ctx),tokenId),"sender is not the owner of the token!");
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();

        _approve(ctx, Utils.address(0), tokenId);

        Balance ownerBalance = ctx.balanceList.getBalance(owner);
        ownerBalance.setBalance(ownerBalance.getBalance() - 1);
        ctx.balanceList.updateBalance(ownerBalance);
        
        nft.setOwner(Utils.address(0));
        ctx.nftList.updateNFT(nft);

        TransferEvent event = new TransferEvent().setFrom(owner).setTo(Utils.address(0)).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean setOwnerLock(ERC721Context ctx, String tokenId,String publicKey,String ownerLock) {
        require(
                _exists(ctx, tokenId),
                String.format("token %s does not exist!", tokenId)
        );
        NFT nft = _getNft(ctx, tokenId);
        String msgSender = ctx.getClientIdentity().getId();
        require(_isOwner(ctx, msgSender, tokenId), "caller is not owner!");
        nft.setPublicKey(publicKey);
        nft.setOwnerLock(ownerLock);
        ctx.nftList.updateNFT(nft);
        return true;
    }



    //cross-chain transfer business unit
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public NFT crossChainTransfer_lock_do(ERC721Context ctx, int type,String tokenId,String ethAddr,String msp,String signature,String invokeId,String lockHash) throws Exception {
        require((ctx.getClientIdentity().getId()).equals(server),"The operater is not the server!");

        CrossTransferEvent event = new CrossTransferEvent();
        NFT NFTnow=null;
        if(type==1){ //eth -> fabric
            require(!_exists(ctx, tokenId), String.format("token %s  exists", tokenId));
            // lock and do
            NFTnow = _crossChainMint(ctx, msp, tokenId, "", "true", lockHash);
            event.setFrom(ethAddr).setTo(msp).setTokenId(tokenId).setInvokeId(invokeId);
        }
        if(type==2){ // fabric -> eth
            require(_exists(ctx, tokenId), String.format("Token %s does not exist", tokenId));
            NFT nft = _getNft(ctx, tokenId);
            require(nft.getIfLock().equals("false"), "The nft is locked!");
            //verify the owner
            require(StringUtils.isNotBlank(nft.getOwnerLock()) && StringUtils.isNotBlank(nft.getPublicKey()),"the owner does not set the crossChain transfer verify infomation!" );
            byte[] signbytes=null;
            try {
                signbytes = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(nft.getPublicKey()), Base64.decode(signature));
            }catch (Exception e){
                require(signbytes!=null,"Can't decode the signature with the nft's publicKey!");
            }
            String signstr=new String(signbytes);
            require(signstr.equals(nft.getOwnerLock()),"The signature is not signed by the owner!");
            //lock and do
            NFTnow=_crossChainBurn(ctx,tokenId,"true",lockHash);
            event.setTo(ethAddr).setFrom(msp).setTokenId(tokenId).setInvokeId(invokeId);
        }

        ctx.getStub().setEvent("CrossChainTransfer", event.serialize());
        return NFTnow;
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public NFT crossChainTransfer_unlock(ERC721Context ctx, int type,String tokenId,String ethAddr,String msp,String signature,String invokeId,String hashKey) throws Exception {
        require(_exists(ctx,tokenId),"The nft does not exist!");
        NFT nft = _getNft(ctx, tokenId);
        require(nft.getIfLock().equals("true"),"The nft is not locked!");
        require(nft.getLockHash().equals(new String(Hash.sha3(hashKey.getBytes()), Charset.forName("ISO-8859-1"))),"the nft is locked by others!");
        //unlock
        //nft.setBackup(null); //backup owner
        nft.setIfLock("false");
        //nft.setLockHash(null);

        ctx.nftList.updateNFT(nft);
        return nft;
        //TransferEvent event = new TransferEvent().setFrom(owner).setTo(Utils.address(0)).setTokenId(tokenId);
        //ctx.getStub().setEvent("Transfer", event.serialize());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void crossChainTransfer_undo_unlock(ERC721Context ctx, int type,String tokenId,String ethAddr,String msp,String signature,String invokeId,String hashKey) throws Exception {
        NFT nft = _getNft(ctx, tokenId);
        require(nft.getIfLock().equals("true"),"The nft is not locked!");
        nft.getLockHash().equals(new String(Hash.sha3(hashKey.getBytes()), Charset.forName("ISO-8859-1")));

        if(type==1){ //eth -> fabric
            require(_exists(ctx, tokenId), String.format("token %s does not exist", tokenId));
            //undo
            _approve(ctx, Utils.address(0), tokenId);
            String owner =nft.getOwner();
            Balance ownerBalance = ctx.balanceList.getBalance(owner);
            ownerBalance.setBalance(ownerBalance.getBalance() - 1);
            ctx.balanceList.updateBalance(ownerBalance);
            nft.setOwner(Utils.address(0));
            //unlock
            nft.setBackup(null);
            nft.setIfLock("false");
            nft.setLockHash(null);
            ctx.nftList.updateNFT(nft);

        }
        if(type==2){ // fabric -> eth
            require(!_exists(ctx, tokenId), String.format("token %s exists", tokenId));
            //undo
            nft.setOwner(nft.getBackup()); //recover the data
            String owner = nft.getOwner();
            _approve(ctx, owner, tokenId);
            Balance ownerBalance = ctx.balanceList.getBalance(owner);
            ownerBalance.setBalance(ownerBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(ownerBalance);
            //unlock
            nft.setBackup(null);
            nft.setIfLock("false");
            nft.setLockHash(null);
            ctx.nftList.updateNFT(nft);
        }
    }


    // tool functions
    private NFT _crossChainBurn(ERC721Context ctx, String tokenId,String ifLock,String lockHash) {
        require((ctx.getClientIdentity().getId()).equals(server),"The operater is not the server!");
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();
        //lock
        nft.setBackup(nft.getOwner()); //backup owner
        nft.setIfLock(ifLock);
        nft.setLockHash(lockHash);
        //do
        Balance ownerBalance = ctx.balanceList.getBalance(owner);
        ownerBalance.setBalance(ownerBalance.getBalance() - 1);
        ctx.balanceList.updateBalance(ownerBalance);

        nft.setOwner(Utils.address(0));
        nft.setTokenApproval(Utils.address(0));
        nft.setOwnerLock(""); // avoid retry transfer
        ctx.nftList.updateNFT(nft);

        TransferEvent event = new TransferEvent().setFrom(owner).setTo(Utils.address(0)).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());
        return nft;
    }

    private NFT _crossChainMint(ERC721Context ctx, String to, String tokenId,String publicKey,String ifLock,String lockHash) {
        require(to != Utils.address(0), "mint to the zero address");
        require(
                !_exists(ctx, tokenId), // token is using and owner is not 0
                String.format("token %s already minted", tokenId)
        );
//        if(!_existsIncludeZero(ctx, tokenId)){
//            ctx.nftList.deleteNFT(tokenId);
//        }
        NFT nft=null;
        if(!_existsIncludeZero(ctx, tokenId)) { // no history record
            nft = NFT.createInstance(tokenId, to, to, "", publicKey, "", ifLock, lockHash, Utils.address(0));
            ctx.nftList.addNFT(nft);

        }else { // remint history record
            nft = _getNft(ctx, tokenId);
            nft.setOwner(to);
            nft.setPublicKey(publicKey);
            nft.setIfLock(ifLock);
            nft.setLockHash(lockHash);
            nft.setBackup(nft.getOwner());
            nft.setOwnerLock("");
            ctx.nftList.updateNFT(nft);
        }

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null == toBalance) {
            Balance newBalance = Balance.createInstance(to, 1);
            ctx.balanceList.addBalance(newBalance);
        } else {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }
        return nft;
    }


    private NFT _getNft(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        require(
            null != nft,
            String.format("token %s does not exist", tokenId)
        );

        return nft;
    }

    private void _transferFrom(ERC721Context ctx, String from, String to, String tokenId, String data) {
        NFT nft = _getNft(ctx, tokenId);
        require(
            from.equals(nft.getOwner()),
            String.format("%s is not owner of token %s", from, tokenId)
        );

        // Clear approvals from the previous owner
        _approve(ctx, "", tokenId);

        Balance fromBalance = ctx.balanceList.getBalance(from);
        if(null != fromBalance) {
            fromBalance.setBalance(fromBalance.getBalance() - 1);
            ctx.balanceList.updateBalance(fromBalance);
        }

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null != toBalance) {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }

        nft.setOwner(to);
        ctx.nftList.updateNFT(nft);

        TransferEvent event = new TransferEvent().setFrom(from).setTo(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());
    }

    private void _approve(ERC721Context ctx, String to, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        nft.setTokenApproval(to);
        ctx.nftList.updateNFT(nft);

        ApprovalEvent event = new ApprovalEvent().setOwner(nft.getOwner()).setApproved(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Approval", event.serialize());
    }

    private boolean _isApprovedOrOwner(ERC721Context ctx, String spender, String tokenId) {
        require(_exists(ctx, tokenId), "operator query for nonexistent token");
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();
        return (spender.equals(owner) || getApproved(ctx, tokenId).equals(spender) || isApprovedForAll(ctx, owner, spender));
    }
    private boolean _isOwner(ERC721Context ctx, String spender, String tokenId) {
        require(_exists(ctx, tokenId), "operator query for nonexistent token");
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();
        return (spender.equals(owner));
    }

    private boolean _exists(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        return null != nft && Utils.address(0) != nft.getOwner();
    }
    private boolean _existsIncludeZero(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        return null != nft;
    }

    public void require(boolean condition, String message) {
        if(!condition) {
            System.out.println(message);
            throw new ChaincodeException(message);
        }
    }




    //  debug functions
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public NFT getNFT(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        require(
                null != nft,
                String.format("token %s does not exist", tokenId)
        );
        return nft;
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String initNFTDebug(ERC721Context ctx, String tokenId,int crossType,String pubKey,String ownerLock) {
        String result="initNFT: ";
        server=ctx.getClientIdentity().getId();
        if(crossType==1) {
            if (_existsIncludeZero(ctx, tokenId)) {
                NFT nft = _getNft(ctx, tokenId);
                String owner = nft.getOwner();
                if (!owner.equals(Utils.address(0))) {
                    try {
                        Balance toBalance = ctx.balanceList.getBalance(owner);
                        if (null == toBalance) {
                            Balance newBalance = Balance.createInstance(owner, 0);
                            ctx.balanceList.addBalance(newBalance);
                        } else {
                            toBalance.setBalance(0);
                            ctx.balanceList.updateBalance(toBalance);
                        }
                    } catch (Exception e) {
                        result = result + "reset balance failed. ";
                    }
                }
                try {
                    ctx.nftList.deleteNFT(nft.getKey());
                } catch (Exception e) {
                    result = result + "delete failed.";
                }
            }
        }
        if(crossType==2){
            NFT nft = _getNft(ctx, tokenId);
            nft.setOwnerLock(ownerLock);
            nft.setPublicKey(pubKey);
            ctx.nftList.updateNFT(nft);
        }
        return result;
    }

}
