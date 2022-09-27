pragma solidity ^0.4.26;
//pragma experimental ABIEncoderV2;

library Strings {
 
    /// @dev 比较2个字符类型是否相等
    function isEqual(string memory strLeft, string memory strRight) internal pure returns(bool) {
        bytes32 hashLeft = keccak256(abi.encode(strLeft));
        bytes32 hashRight = keccak256(abi.encode(strRight));
        return ( hashLeft == hashRight );
    }
}

/**
 * Utility library of inline functions on addresses
 */
library Address {

    /**
     * Returns whether the target address is a contract
     * @dev This function will return false if invoked during the constructor of a contract,
     * as the code is not actually created until after the constructor finishes.
     * @param account address of the account to check
     * @return whether the target address is a contract
     */
    function isContract(address account) internal view returns (bool) {
        uint256 size;
        // XXX Currently there is no better way to check if there is a contract in an address
        // than to check the size of the code at that address.
        // See https://ethereum.stackexchange.com/a/14016/36603
        // for more details about how this works.
        // TODO Check this again before the Serenity release, because all addresses will be
        // contracts then.
        // solium-disable-next-line security/no-inline-assembly
        assembly { size := extcodesize(account) }
        return size > 0;
    }

}


library LibAddress{
    
    /*
     *@dev 
     *@param
     *@return
     */
    function isContract(address account) internal view returns(bool) {
        uint256 size;
        assembly { size := extcodesize(account) }  
        return size > 0;
    }

    function isEmptyAddress(address addr) internal pure returns(bool){
        return addr == address(0);
    }


    function addressToBytes(address addr) internal pure returns (bytes memory){
        bytes20 addrBytes = bytes20(uint160(addr));
        bytes memory rtn = new bytes(20);
        for(uint8 i=0;i<20;i++){
            rtn[i] = addrBytes[i];
        }
        return rtn;
    }
 
    
    function bytesToAddress(bytes memory addrBytes) internal pure returns (address){
        require(addrBytes.length == 20);
        //Convert binary to uint160
        uint160 intVal = 0;

        for(uint8 i=0;i<20;i++){
            intVal <<= 8;
            intVal += uint8(addrBytes[i]);
        }
        return address(intVal);
    }


    function addressToString(address addr) internal pure returns(string memory){
        //Convert addr to bytes
        bytes20 value = bytes20(uint160(addr));
        bytes memory strBytes = new bytes(42);
        //Encode hex prefix
        strBytes[0] = '0';
        strBytes[1] = 'x';
        //Encode bytes usig hex encoding
        for(uint i=0;i<20;i++){
            uint8 byteValue = uint8(value[i]);
            strBytes[2 + (i<<1)] = encode((byteValue >> 4) & 0x0f);
            strBytes[3 + (i<<1)] = encode(byteValue & 0x0f);
        }
        return string(strBytes);
    }

    function stringToAddress(string memory data) internal pure returns(address){
        bytes memory strBytes = bytes(data);
        require(strBytes.length >= 39 && strBytes.length <= 42, "Not hex string");
        //Skip prefix
        uint start = 0;
        uint bytesBegin = 0;
        if(strBytes[1] == 'x' || strBytes[1] == 'X'){
            start = 2;
        }
        //Special case: 0xabc. should be 0x0abc
        uint160 addrValue = 0;
        uint effectPayloadLen = strBytes.length - start;
        if(effectPayloadLen == 39){
            addrValue += decode(strBytes[start++]);
            bytesBegin++;
        }
        //Main loop
        for(uint i=bytesBegin;i < 20; i++){
            addrValue <<= 8;
            uint8 tmp1 = decode(strBytes[start]);
            uint8 tmp2 = decode(strBytes[start+1]);
            uint8 combined = (tmp1 << 4) + tmp2;
            addrValue += combined;
            start+=2;
        }
        
        return address(addrValue);
    }


    //-----------HELPER METHOD--------------//

    //num represents a number from 0-15 and returns ascii representing [0-9A-Fa-f]
    function encode(uint8 num)  internal pure returns(bytes1){
        //0-9 -> 0-9
        if(num >= 0 && num <= 9){
            return bytes1(num + 48);
        }
        //10-15 -> a-f
        return bytes1(num + 87);
    }
        
    //asc represents one of the char:[0-9A-Fa-f] and returns consperronding value from 0-15
    function decode(bytes1 asc) internal pure returns(uint8){
        uint8 val = uint8(asc);
        //0-9
        if(val >= 48 && val <= 57){
            return val - 48;
        }
        //A-F
        if(val >= 65 && val <= 70){
            return val - 55;
        }
        //a-f
        return val - 87;
    }

}

/**
 * @title SafeMath
 * @dev Math operations with safety checks that revert on error
 */
library SafeMath {

    /**
    * @dev Multiplies two numbers, reverts on overflow.
    */
    function mul(uint256 a, uint256 b) internal pure returns (uint256) {
        // Gas optimization: this is cheaper than requiring 'a' not being zero, but the
        // benefit is lost if 'b' is also tested.
        // See: https://github.com/OpenZeppelin/openzeppelin-solidity/pull/522
        if (a == 0) {
            return 0;
        }

        uint256 c = a * b;
        require(c / a == b);

        return c;
    }

    /**
    * @dev Integer division of two numbers truncating the quotient, reverts on division by zero.
    */
    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b > 0); // Solidity only automatically asserts when dividing by 0
        uint256 c = a / b;
        // assert(a == b * c + a % b); // There is no case in which this doesn't hold

        return c;
    }

    /**
    * @dev Subtracts two numbers, reverts on overflow (i.e. if subtrahend is greater than minuend).
    */
    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b <= a);
        uint256 c = a - b;

        return c;
    }

    /**
    * @dev Adds two numbers, reverts on overflow.
    */
    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        require(c >= a);

        return c;
    }

    /**
    * @dev Divides two numbers and returns the remainder (unsigned integer modulo),
    * reverts when dividing by zero.
    */
    function mod(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b != 0);
        return a % b;
    }
}

/**
 * @title IERC165
 * @dev https://github.com/ethereum/EIPs/blob/master/EIPS/eip-165.md
 */
interface IERC165 {

    /**
     * @notice Query if a contract implements an interface
     * @param interfaceId The interface identifier, as specified in ERC-165
     * @dev Interface identification is specified in ERC-165. This function
     * uses less than 30,000 gas.
     */
    function supportsInterface(bytes4 interfaceId)
    external
    view
    returns (bool);
}
/**
 * @title ERC165
 * @author Matt Condon (@shrugs)
 * @dev Implements ERC165 using a lookup table.
 */
contract ERC165 is IERC165 {

    bytes4 private constant _InterfaceId_ERC165 = 0x01ffc9a7;
    /**
     * 0x01ffc9a7 ===
     *   bytes4(keccak256('supportsInterface(bytes4)'))
     */

    /**
     * @dev a mapping of interface id to whether or not it's supported
     */
    mapping(bytes4 => bool) private _supportedInterfaces;

    /**
     * @dev A contract implementing SupportsInterfaceWithLookup
     * implement ERC165 itself
     */
    constructor()
    public
    {
        _registerInterface(_InterfaceId_ERC165);
    }

    /**
     * @dev implement supportsInterface(bytes4) using a lookup table
     */
    function supportsInterface(bytes4 interfaceId)
    external
    view
    returns (bool)
    {
        return _supportedInterfaces[interfaceId];
    }

    /**
     * @dev internal method for registering an interface
     */
    function _registerInterface(bytes4 interfaceId)
    internal
    {
        require(interfaceId != 0xffffffff);
        _supportedInterfaces[interfaceId] = true;
    }
}
/**
 * @title ERC721 Non-Fungible Token Standard basic interface
 * @dev see https://github.com/ethereum/EIPs/blob/master/EIPS/eip-721.md
 */
contract IERC721 is IERC165 {

    event Transfer(
        address indexed from,
        address indexed to,
        uint256 indexed tokenId
    );
    event Approval(
        address indexed owner,
        address indexed approved,
        uint256 indexed tokenId
    );
    event ApprovalForAll(
        address indexed owner,
        address indexed operator,
        bool approved
    );

    function balanceOf(address owner) public view returns (uint256 balance);
    function ownerOf(uint256 tokenId) public view returns (address owner);

    function approve(address to, uint256 tokenId) public;
    function getApproved(uint256 tokenId)
    public view returns (address operator);

    function setApprovalForAll(address operator, bool _approved) public;
    function isApprovedForAll(address owner, address operator)
    public view returns (bool);

    function transferFrom(address from, address to, uint256 tokenId) public;
    function safeTransferFrom(address from, address to, uint256 tokenId)
    public;

    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId,
        bytes data
    )
    public;
}
/**
 * @title ERC721 token receiver interface
 * @dev Interface for any contract that wants to support safeTransfers
 * from ERC721 asset contracts.
 */
contract IERC721Receiver {
    /**
     * @notice Handle the receipt of an NFT
     * @dev The ERC721 smart contract calls this function on the recipient
     * after a `safeTransfer`. This function MUST return the function selector,
     * otherwise the caller will revert the transaction. The selector to be
     * returned can be obtained as `this.onERC721Received.selector`. This
     * function MAY throw to revert and reject the transfer.
     * Note: the ERC721 contract address is always the message sender.
     * @param operator The address which called `safeTransferFrom` function
     * @param from The address which previously owned the token
     * @param tokenId The NFT identifier which is being transferred
     * @param data Additional data with no specified format
     * @return `bytes4(keccak256("onERC721Received(address,address,uint256,bytes)"))`
     */
    function onERC721Received(
        address operator,
        address from,
        uint256 tokenId,
        bytes data
    )
    public
    returns(bytes4);
}


/**
 * @title ERC721 Non-Fungible Token Standard basic implementation
 * @dev see https://github.com/ethereum/EIPs/blob/master/EIPS/eip-721.md
 */
contract ERC721 is ERC165, IERC721 {

    using SafeMath for uint256;
    using Address for address;

    // Equals to `bytes4(keccak256("onERC721Received(address,address,uint256,bytes)"))`
    // which can be also obtained as `IERC721Receiver(0).onERC721Received.selector`
    bytes4 private constant _ERC721_RECEIVED = 0x150b7a02;

    // Mapping from token ID to owner
    mapping (uint256 => address)  _tokenOwner;

    // Mapping from token ID to approved address
    mapping (uint256 => address)  _tokenApprovals;

    // Mapping from owner to number of owned token
    mapping (address => uint256)  _ownedTokensCount;

    // Mapping from owner to operator approvals
    mapping (address => mapping (address => bool))  _operatorApprovals;

    bytes4 private constant _InterfaceId_ERC721 = 0x80ac58cd;
    /*
     * 0x80ac58cd ===
     *   bytes4(keccak256('balanceOf(address)')) ^
     *   bytes4(keccak256('ownerOf(uint256)')) ^
     *   bytes4(keccak256('approve(address,uint256)')) ^
     *   bytes4(keccak256('getApproved(uint256)')) ^
     *   bytes4(keccak256('setApprovalForAll(address,bool)')) ^
     *   bytes4(keccak256('isApprovedForAll(address,address)')) ^
     *   bytes4(keccak256('transferFrom(address,address,uint256)')) ^
     *   bytes4(keccak256('safeTransferFrom(address,address,uint256)')) ^
     *   bytes4(keccak256('safeTransferFrom(address,address,uint256,bytes)'))
     */

    constructor()
    public
    {
        // register the supported interfaces to conform to ERC721 via ERC165
        _registerInterface(_InterfaceId_ERC721);
    }

    /**
     * @dev Gets the balance of the specified address
     * @param owner address to query the balance of
     * @return uint256 representing the amount owned by the passed address
     */
    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return _ownedTokensCount[owner];
    }

    /**
     * @dev Gets the owner of the specified token ID
     * @param tokenId uint256 ID of the token to query the owner of
     * @return owner address currently marked as the owner of the given token ID
     */
    function ownerOf(uint256 tokenId) public view returns (address) {
        address owner = _tokenOwner[tokenId];
        require(owner != address(0));
        return owner;
    }

    /**
     * @dev Approves another address to transfer the given token ID
     * The zero address indicates there is no approved address.
     * There can only be one approved address per token at a given time.
     * Can only be called by the token owner or an approved operator.
     * @param to address to be approved for the given token ID
     * @param tokenId uint256 ID of the token to be approved
     */
    function approve(address to, uint256 tokenId) public {
        address owner = ownerOf(tokenId);
        require(to != owner);
        require(msg.sender == owner || isApprovedForAll(owner, msg.sender));

        _tokenApprovals[tokenId] = to;
        emit Approval(owner, to, tokenId);
    }

    /**
     * @dev Gets the approved address for a token ID, or zero if no address set
     * Reverts if the token ID does not exist.
     * @param tokenId uint256 ID of the token to query the approval of
     * @return address currently approved for the given token ID
     */
    function getApproved(uint256 tokenId) public view returns (address) {
        require(_exists(tokenId));
        return _tokenApprovals[tokenId];
    }

    /**
     * @dev Sets or unsets the approval of a given operator
     * An operator is allowed to transfer all tokens of the sender on their behalf
     * @param to operator address to set the approval
     * @param approved representing the status of the approval to be set
     */
    function setApprovalForAll(address to, bool approved) public {
        require(to != msg.sender);
        _operatorApprovals[msg.sender][to] = approved;
        emit ApprovalForAll(msg.sender, to, approved);
    }

    /**
     * @dev Tells whether an operator is approved by a given owner
     * @param owner owner address which you want to query the approval of
     * @param operator operator address which you want to query the approval of
     * @return bool whether the given operator is approved by the given owner
     */
    function isApprovedForAll(
        address owner,
        address operator
    )
    public
    view
    returns (bool)
    {
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev Transfers the ownership of a given token ID to another address
     * Usage of this method is discouraged, use `safeTransferFrom` whenever possible
     * Requires the msg sender to be the owner, approved, or operator
     * @param from current owner of the token
     * @param to address to receive the ownership of the given token ID
     * @param tokenId uint256 ID of the token to be transferred
    */
    function transferFrom(
        address from,
        address to,
        uint256 tokenId
    )
    public
    {
        require(_isApprovedOrOwner(msg.sender, tokenId));
        require(to != address(0));

        _clearApproval(from, tokenId);
        _removeTokenFrom(from, tokenId);
        _addTokenTo(to, tokenId);

        emit Transfer(from, to, tokenId);
    }

    /**
     * @dev Safely transfers the ownership of a given token ID to another address
     * If the target address is a contract, it must implement `onERC721Received`,
     * which is called upon a safe transfer, and return the magic value
     * `bytes4(keccak256("onERC721Received(address,address,uint256,bytes)"))`; otherwise,
     * the transfer is reverted.
     *
     * Requires the msg sender to be the owner, approved, or operator
     * @param from current owner of the token
     * @param to address to receive the ownership of the given token ID
     * @param tokenId uint256 ID of the token to be transferred
    */
    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId
    )
    public
    {
        // solium-disable-next-line arg-overflow
        safeTransferFrom(from, to, tokenId, "");
    }

    /**
     * @dev Safely transfers the ownership of a given token ID to another address
     * If the target address is a contract, it must implement `onERC721Received`,
     * which is called upon a safe transfer, and return the magic value
     * `bytes4(keccak256("onERC721Received(address,address,uint256,bytes)"))`; otherwise,
     * the transfer is reverted.
     * Requires the msg sender to be the owner, approved, or operator
     * @param from current owner of the token
     * @param to address to receive the ownership of the given token ID
     * @param tokenId uint256 ID of the token to be transferred
     * @param _data bytes data to send along with a safe transfer check
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId,
        bytes _data
    )
    public
    {
        transferFrom(from, to, tokenId);
        // solium-disable-next-line arg-overflow
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    /**
     * @dev Returns whether the specified token exists
     * @param tokenId uint256 ID of the token to query the existence of
     * @return whether the token exists
     */
    function _exists(uint256 tokenId) internal view returns (bool) {
        address owner = _tokenOwner[tokenId];
        return owner != address(0);
    }

    /**
     * @dev Returns whether the given spender can transfer a given token ID
     * @param spender address of the spender to query
     * @param tokenId uint256 ID of the token to be transferred
     * @return bool whether the msg.sender is approved for the given token ID,
     *  is an operator of the owner, or is the owner of the token
     */
    function _isApprovedOrOwner(
        address spender,
        uint256 tokenId
    )
    internal
    view
    returns (bool)
    {
        address owner = ownerOf(tokenId);
        // Disable solium check because of
        // https://github.com/duaraghav8/Solium/issues/175
        // solium-disable-next-line operator-whitespace
        return (
        spender == owner ||
        getApproved(tokenId) == spender ||
        isApprovedForAll(owner, spender)
        );
    }

    /**
     * @dev Internal function to mint a new token
     * Reverts if the given token ID already exists
     * @param to The address that will own the minted token
     * @param tokenId uint256 ID of the token to be minted by the msg.sender
     */
    function _mint(address to, uint256 tokenId) internal {
        require(to != address(0));
        // require(!_exists(tokenId));
        _addTokenTo(to, tokenId);
        emit Transfer(address(0), to, tokenId);
    }

    /**
     * @dev Internal function to burn a specific token
     * Reverts if the token does not exist
     * @param tokenId uint256 ID of the token being burned by the msg.sender
     */
    function _burn(address owner, uint256 tokenId) internal {
        _clearApproval(owner, tokenId);
        _removeTokenFrom(owner, tokenId);
        emit Transfer(owner, address(0), tokenId);
    }

    /**
     * @dev Internal function to add a token ID to the list of a given address
     * Note that this function is left internal to make ERC721Enumerable possible, but is not
     * intended to be called by custom derived contracts: in particular, it emits no Transfer event.
     * @param to address representing the new owner of the given token ID
     * @param tokenId uint256 ID of the token to be added to the tokens list of the given address
     */
    function _addTokenTo(address to, uint256 tokenId) internal {
        require(_tokenOwner[tokenId] == address(0));
        _tokenOwner[tokenId] = to;
        _ownedTokensCount[to] = _ownedTokensCount[to].add(1);
    }

    /**
     * @dev Internal function to remove a token ID from the list of a given address
     * Note that this function is left internal to make ERC721Enumerable possible, but is not
     * intended to be called by custom derived contracts: in particular, it emits no Transfer event,
     * and doesn't clear approvals.
     * @param from address representing the previous owner of the given token ID
     * @param tokenId uint256 ID of the token to be removed from the tokens list of the given address
     */
    function _removeTokenFrom(address from, uint256 tokenId) internal {
        require(ownerOf(tokenId) == from);
        _ownedTokensCount[from] = _ownedTokensCount[from].sub(1);
        _tokenOwner[tokenId] = address(0);
    }

    /**
     * @dev Internal function to invoke `onERC721Received` on a target address
     * The call is not executed if the target address is not a contract
     * @param from address representing the previous owner of the given token ID
     * @param to target address that will receive the tokens
     * @param tokenId uint256 ID of the token to be transferred
     * @param _data bytes optional data to send along with the call
     * @return whether the call correctly returned the expected magic value
     */
    function _checkOnERC721Received(
        address from,
        address to,
        uint256 tokenId,
        bytes _data
    )
    internal
    returns (bool)
    {
        if (!to.isContract()) {
            return true;
        }
        bytes4 retval = IERC721Receiver(to).onERC721Received(
            msg.sender, from, tokenId, _data);
        return (retval == _ERC721_RECEIVED);
    }

    /**
     * @dev Private function to clear current approval of a given token ID
     * Reverts if the given address is not indeed the owner of the token
     * @param owner owner of the token
     * @param tokenId uint256 ID of the token to be transferred
     */
    function _clearApproval(address owner, uint256 tokenId) private {
        require(ownerOf(tokenId) == owner);
        if (_tokenApprovals[tokenId] != address(0)) {
            _tokenApprovals[tokenId] = address(0);
        }
    }

}

contract CrossChainNFT is ERC721 {
    using Strings for string;
        // Token name
    string public name;

    // Token symbol
    string public symbol;
    enum CrossChainType {E2F,F2E}
    //enum ChainNow{E,F}

    event CrossChainTransfer(
        CrossChainType crossChaintype,
        string indexed from,
        string indexed to,
        uint256 indexed tokenId

    );
    event Mint(
        address indexed from,
        address indexed to,
        uint256 indexed tokenId
    );

    struct NFT{
        uint tokenId;
        bool valid; 
        string chain; //which blockchain the NFT is in now. "E" and "F"
        string ownerLock; 
        bool ifLocked;
        bytes32 lockHash;
        string backup;
    }
    mapping(uint256 => NFT) NFTs;  //all the NFT in the cross-chain dApp

    //Contract creator
    address public creator;
    // cross-chain server
    address public server;
    function setServer(address _server) public{
        require(msg.sender==creator);
        server=_server;
    }


    function mint(uint256 _tokenId, address _to) public
    {
        //require(msg.sender==_creator);
        require(!_exists(_tokenId));
        require(_to != address(0));
        require(!NFTs[_tokenId].valid); // the token is not in all the blockchains
        _mint( _to,_tokenId);
        NFTs[_tokenId]=NFT(_tokenId,true,"MyEthereum-1","",false,'',""); 
        emit Mint(msg.sender,_to,_tokenId);
    }


    //---------- cross-chain transfer business union -----------------------------------------------

    function _crossChainBurn(uint256 tokenId) internal  {
        address owner = ERC721.ownerOf(tokenId);
        // Update ownership in case tokenId was transferred by `_beforeTokenTransfer` hook
        owner = ERC721.ownerOf(tokenId);
        // Clear approvals
        delete _tokenApprovals[tokenId];

        _ownedTokensCount[owner] -= 1;

        delete _tokenOwner[tokenId];
    }

     function _crossChainMint(uint256 _tokenId, address _to) internal 
    {
        //require(!_exists(_tokenId));
        require(_to != address(0));
        _mint( _to,_tokenId);

    }

    function setOwnerLock(uint256 _tokenId, string memory _lock) public
    {
        require(NFTs[_tokenId].valid); 
        require(_exists(_tokenId));
        require(ownerOf(_tokenId)==msg.sender);
        NFTs[_tokenId].ownerLock=_lock; 
    }

    function crossChainTransfer_lock_do(int _type,uint256 _tokenId, address _addr, string memory _msp,  bytes _signature,  string memory _invokeId, bytes32 _lockHash) public {
        require(msg.sender==server);
        require(NFTs[_tokenId].valid);  
        require(NFTs[_tokenId].ifLocked==false);
        // ensure the owner call the entry funcction
       
        if(_type==1){ //eth -> fabric
            require(!NFTs[_tokenId].ownerLock.isEqual(""));
            require(verifySign(NFTs[_tokenId].ownerLock,_signature,ownerOf(_tokenId)));
            require(_exists(_tokenId));
            //lock
            NFTs[_tokenId].ifLocked=true;
            NFTs[_tokenId].lockHash=_lockHash;
            NFTs[_tokenId].backup=LibAddress.addressToString(ownerOf(_tokenId));
            //do
            _crossChainBurn(_tokenId); 
            NFTs[_tokenId].chain="F";
        }
        if(_type==2){ // fabric -> eth
            require(!_exists(_tokenId));
            //lock
            NFTs[_tokenId].ifLocked=true;
            NFTs[_tokenId].lockHash=_lockHash;
            NFTs[_tokenId].backup=NFTs[_tokenId].chain;
            //do
            _crossChainMint(_tokenId,_addr); 
            NFTs[_tokenId].chain="E";
        }   
        NFTs[_tokenId].ownerLock="";   //avoid retry transfer 
    }

    function crossChainTransfer_unlock(int _type, uint256 _tokenId,address _addr, string memory _msp,  bytes _signature, string memory _invokeId,string memory _hashKey) public{
        require(NFTs[_tokenId].valid && NFTs[_tokenId].ifLocked);
        require(keccak256(abi.encodePacked(_hashKey))==NFTs[_tokenId].lockHash);
        // unlock
        NFTs[_tokenId].ifLocked=false;
    }

    function crossChainTransfer_undo_unlock(int _type, uint256 _tokenId,address _addr, string memory _msp, bytes _signature,  string memory _invokeId,string memory _hashKey) public {
        require(NFTs[_tokenId].valid && NFTs[_tokenId].ifLocked);
        require(keccak256(abi.encodePacked(_hashKey))==NFTs[_tokenId].lockHash);
        if(_type==2){
            //require(keccak256(abi.encodePacked(NFTs[_tokenId].chain))==keccak256(abi.encodePacked('E')));
            require(_exists(_tokenId));
            //undo
            _crossChainBurn(_tokenId); 
            NFTs[_tokenId].chain="F";
            // unlock
            NFTs[_tokenId].ifLocked=false;
        }
        if(_type==1){
            //require(keccak256(abi.encodePacked(NFTs[_tokenId].chain))==keccak256(abi.encodePacked('F')));
            require(!_exists(_tokenId));   
            //undo
            address oldOwner=LibAddress.stringToAddress(NFTs[_tokenId].backup);
            _crossChainMint(_tokenId,oldOwner); 
            NFTs[_tokenId].chain="E";
            // unlock
            NFTs[_tokenId].ifLocked=false;
        }  

    }

    /**
   * @dev Constructor function
   */
    constructor (string memory _name, string memory _symbol) public {
        name = _name;
        symbol = _symbol;
        //debug init data
        creator=msg.sender;
        server=0xEd869df2e1a1A17A21F3ce8ADF437F868D53167C;
        _mint( 0x65CE35093D037031DcE8F37B790D72a4E37Ae47C,12345678);
        NFTs[12345678]=NFT(12345678,true,"MyEthereum-1","myownerlock",false,'',''); 
        _mint( 0x65CE35093D037031DcE8F37B790D72a4E37Ae47C,1);
        NFTs[1]=NFT(1,true,"MyEthereum-1","",false,'',''); 
        _mint( 0xd33cc2d4c8d85298e1fB36B26e0d0Bdb34572271,2);
        NFTs[1]=NFT(2,true,"MyEthereum-1","myownerlock",false,'',''); 

    }
    function setName(string memory newName) public
    {
       name=newName;
    }


    // -------------------------------------------- verify owner --------------------------------------
    function recover(bytes32 _msgHash, bytes _signature) internal pure returns (address){
        bytes32 r;
        bytes32 s;
        uint8 v;
    
        // Check the signature length
        if (_signature.length != 65) {
          return (address(0));
        }
    
        // Divide the signature in r, s and v variables with inline assembly.
        assembly {
          r := mload(add(_signature, 0x20))
          s := mload(add(_signature, 0x40))
          v := byte(0, mload(add(_signature, 0x60)))
        }
    
        // Version of signature should be 27 or 28, but 0 and 1 are also possible versions
        if (v < 27) {
          v += 27;
        }
    
        // If the version is correct return the signer address
        if (v != 27 && v != 28) {
          return (address(0));
        } else {
          // solium-disable-next-line arg-overflow
          return ecrecover(_msgHash, v, r, s);
  
        }
    }

        
    function verifySign(string memory _plainText, bytes _signature, address _addr) internal pure returns (bool){
        return recover(keccak256(abi.encodePacked(_plainText)),_signature) ==_addr;       
    }

    // -----------------------  debug functions ----------------------


    function initNFT(uint256 _tokenId) public {
        if(_exists(_tokenId)){
            _crossChainBurn(_tokenId);           
        }
        _mint(0x65CE35093D037031DcE8F37B790D72a4E37Ae47C,_tokenId);
        NFTs[_tokenId]=NFT(_tokenId,true,"MyEthereum-1","myownerlock",false,'','');   
    }
    function verifySignPublic(string memory _plainText, bytes _signature, address _addr) public  pure returns (bool){
         return recover(keccak256(abi.encodePacked(_plainText)),_signature) ==_addr;    
    }
    function verifySignPublicString(string memory _plainText, string memory _signature, address _addr) public  pure returns (bool){
         return recover(keccak256(abi.encodePacked(_plainText)),bytes(_signature)) ==_addr;    
    }
    function getNFT(uint256 _tokenId) public  view returns (address,string,string,bool){
        address owner = _tokenOwner[_tokenId];
        return (owner, NFTs[_tokenId].chain, NFTs[_tokenId].ownerLock, NFTs[_tokenId].ifLocked);      
    }
    function ifExists(uint256 _tokenId) public view returns (bool) {
        return _exists(_tokenId);
    }
    function StringToAddrTest(string memory _in) public pure returns(address){
        return LibAddress.stringToAddress(_in); 
    }
    function AddrToStringTest(address _in) public pure returns(string){
        return LibAddress.addressToString(_in); 
    }

    function StringToBytesTest(string memory _in) public pure returns(bytes){
        return bytes(_in); 
    }
    function StringToBytes32Test(string memory _in) public pure returns(bytes32){
        return stringToBytes32(_in); 
    }
    function BytesToStringTest(bytes _in) public pure returns(string){
        return string(_in); 
    }

  function stringToBytes32(string memory source) internal pure  returns (bytes32 result) {
    assembly {
        result := mload(add(source, 32))
    } 
  }

}