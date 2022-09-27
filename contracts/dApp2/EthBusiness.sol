pragma solidity ^0.4.26;
//pragma solidity ^0.7.1;
pragma experimental ABIEncoderV2;

/**
 * @title Owner
 * @dev Set & change owner
 */
contract EthBusiness {

    struct Asset{
        bool valid; // A field used to quickly determine whether an asset exists
        string assetID;
        uint total;
        bool ifLocked;
        bytes32 lockHash;
        string backup;
    }

    address public server;
    address public creator;
    mapping(string => Asset) assetList;
    uint public invokeID;
    event crossEvent(string  invokeID,string  invokeDetail);
    event callBackEvent(string  invokeID,string  callBackDetail);


    /**
     * @dev Set contract deployer as owner
     */
    constructor() public {
        invokeID=0;
        creator=msg.sender;
        //debug init asset
        string memory _assetID="asset001";
        assetList[_assetID].assetID="asset001";
        assetList[_assetID].total=100;
        assetList[_assetID].valid=true;
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        assetList[_assetID].backup="";
        _assetID="asset002";
        assetList[_assetID].assetID="asset002";
        assetList[_assetID].total=100;
        assetList[_assetID].valid=true;
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        assetList[_assetID].backup="";
        _assetID="asset003";
        assetList[_assetID].assetID="asset003";
        assetList[_assetID].total=100;
        assetList[_assetID].valid=true;
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        assetList[_assetID].backup="";
        server=0xEd869df2e1a1A17A21F3ce8ADF437F868D53167C;
    }

    function setServer(address _server) public{
        require(msg.sender==creator);
        server=_server;
    }




    function createAsset(string _assetID,uint _total) public returns (bool){
        require(!assetList[_assetID].valid);
        assetList[_assetID].assetID=_assetID;
        assetList[_assetID].total=_total;
        assetList[_assetID].valid=true;
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        assetList[_assetID].backup="";
        return true;
    }


    //------------- incressAsset asset business unit--------------------
    function incressAsset_lock_do(string _assetID,uint _num,string memory _invokeId,bytes32 _lockHash) public returns (bool){
        require(msg.sender==server && assetList[_assetID].valid && !assetList[_assetID].ifLocked);
        //lock
        //string memory para=strConcat(para,_assetID);
        //para=strConcat(para,",");
        //para=strConcat(para,uintToString(assetList[_assetID].total));
        string memory para=uintToString(assetList[_assetID].total);
        assetList[_assetID].backup=para;
        assetList[_assetID].ifLocked=true;
        assetList[_assetID].lockHash=_lockHash;
        //do
        assetList[_assetID].total +=_num;
    }

    function incressAsset_unlock(string _assetID,uint _num,string memory _invokeId,string memory _hashKey) public returns (bool){
        require(assetList[_assetID].valid && assetList[_assetID].ifLocked);
        require(keccak256(_hashKey)==assetList[_assetID].lockHash);
        // unlock
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        return true;
    }

    function incressAsset_undo_unlock(string _assetID,uint _num,string memory _invokeId,string memory _hashKey) public returns (bool){
        require(assetList[_assetID].valid && assetList[_assetID].ifLocked);
        require(keccak256(_hashKey)==assetList[_assetID].lockHash);
        // undo
        string memory oldDataStr= assetList[_assetID].backup;
        // string[] memory oldData= split(oldDataStr, ",");
        uint oldNum=stringToUint(oldDataStr);
        assetList[_assetID].total =oldNum;
        // unlock
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        return true;
    }
    //------------- read operate Number in fabric business unit--------------------
    event GetNumber(string invokeId, string assetId,uint num);

    //atomic read
    function getNumber_atomic(string _assetId,string memory _invokeId) public returns(uint){
        if(assetList[_assetId].total<100){
            emit GetNumber(_invokeId,_assetId,2);
            return 2;
        }else{
            emit GetNumber(_invokeId,_assetId,1);
            return 1;
        }
    }
    //original read
    function getNumber(string _assetId) public view returns(uint){
        if(assetList[_assetId].total<100){
            return 2;
        }else{
            return 1;
        }
    }



    // ------------------debug functions--------------------------------------------------------
    function setAsset(string _assetID,uint _num,bool _lock) public returns (bool){
        require(assetList[_assetID].valid);
        assetList[_assetID].total=_num;
        assetList[_assetID].ifLocked=_lock;
        return true;
    }

    function incressAsset_getOldData(string _assetID) public view returns(string memory){
        require(assetList[_assetID].valid && assetList[_assetID].ifLocked);
        return assetList[_assetID].backup;
    }

    function unLockAssetDebug(string memory _assetID) public returns (bool){
        require(assetList[_assetID].valid);
        assetList[_assetID].ifLocked=false;
        assetList[_assetID].lockHash="";
        assetList[_assetID].backup="";
        return true;
    }



    // ------------------ tools --------------------------------------------------------
    //字符串比较
    function compareStrings(string memory a, string memory b)pure internal returns (bool) {
        if (bytes(a).length != bytes(b).length) {
            return false;
        }
        for (uint i = 0; i < bytes(a).length; i ++) {
            if(bytes(a)[i] != bytes(b)[i]) {
                return false;
            }
        }
        return true;
    }

    //字符串拼接
    function strConcat(string memory _a, string memory _b) public pure returns (string memory){
        bytes memory _ba = bytes(_a);
        bytes memory _bb = bytes(_b);
        string memory ret = new string(_ba.length + _bb.length );
        bytes memory bret = bytes(ret);
        uint k = 0;
        for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
        for (uint j = 0; j < _bb.length; j++) bret[k++] = _bb[j];
        return string(ret);
    }
    //字符串拼接
    function strConcatByArray(string [40] memory _arr) public pure returns (string memory){
        uint n = _arr.length;
        string memory result =_arr[0];
        if (n>1){
            uint index=1;
            for(index=1; index<n; index++){
                bytes memory _ba = bytes(result);
                string memory _b = _arr[index];
                bytes memory _bb = bytes(_b);
                string memory ret = new string(_ba.length + _bb.length );
                bytes memory bret = bytes(ret);
                uint k = 0;
                for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
                for (uint j = 0; j < _bb.length; j++) bret[k++] = _bb[j];
                result= string(ret);
            }
        }
        return result;
    }
    //字符串拼接
    function strConcatByArray32(string [32] memory _arr) public pure returns (string memory){
        uint n = _arr.length;
        string memory result =_arr[0];
        if (n>1){
            uint index=1;
            for(index=1; index<n; index++){
                bytes memory _ba = bytes(result);
                string memory _b = _arr[index];
                bytes memory _bb = bytes(_b);
                string memory ret = new string(_ba.length + _bb.length );
                bytes memory bret = bytes(ret);
                uint k = 0;
                for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
                for (uint j = 0; j < _bb.length; j++) bret[k++] = _bb[j];
                result= string(ret);
            }
        }
        return result;
    }

    //字符串拼接
    function strConcatByArray11(string [11] memory _arr) pure public returns (string memory){
        uint n = _arr.length;
        string memory result =_arr[0];
        if (n>1){
            uint index=1;
            for(index=1; index<n; index++){
                bytes memory _ba = bytes(result);
                string memory _b = _arr[index];
                bytes memory _bb = bytes(_b);
                string memory ret = new string(_ba.length + _bb.length );
                bytes memory bret = bytes(ret);
                uint k = 0;
                for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
                for (uint j = 0; j < _bb.length; j++) bret[k++] = _bb[j];
                result= string(ret);
            }
        }
        return result;
    }

    //字符串拼接
    function strConcatByArray7(string [7] memory _arr) pure public returns (string memory){
        uint n = _arr.length;
        string memory result =_arr[0];
        if (n>1){
            uint index=1;
            for(index=1; index<n; index++){
                bytes memory _ba = bytes(result);
                string memory _b = _arr[index];
                bytes memory _bb = bytes(_b);
                string memory ret = new string(_ba.length + _bb.length );
                bytes memory bret = bytes(ret);
                uint k = 0;
                for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
                for (uint j = 0; j < _bb.length; j++) bret[k++] = _bb[j];
                result= string(ret);
            }
        }
        return result;
    }





    // function toBytesNickJohnson(uint256 x) pure public returns (bytes memory b) {
    //     b = new bytes(32);
    //     assembly { mstore(add(b, 32), x) }
    // }

    // function uintToStringV2(uint playChoice) pure public returns (string memory s) {
    //     bytes memory c = toBytesNickJohnson(playChoice);
    //     return string(c);
    // }

    //return int256
    function bytesToInt(bytes memory b) public pure returns (int result) {
        uint i = 0;
        uint tr = 0;
        result = 0;
        bool sign = false;
        if(b[i] == "-") {
            sign = true;
            i++;
        } else if(b[i] == "+") {
            i++;
        }
        while(uint8(b[b.length - tr - 1]) == 0x00) {
            tr++;
        }
        for (;i < b.length - tr; i++) {
            uint8 c = uint8(b[i]);
            if (c >= 48 && c <= 57) {
                result *= 10;
                result = result + int(c - 48);
            }
        }
        if(sign) {
            result *= -1;
        }
    }
    // string to uint256
    function stringToUint(string memory _a) public pure returns (uint) {
        return stringToUint(_a, 0);
    }

    // string to uint256 (parseFloat*10^_b)  放大10^b倍
    function stringToUint(string memory _a, uint _b) public pure returns (uint) {
        bytes memory bresult = bytes(_a);
        uint mint = 0;
        bool decimals = false;
        for (uint i=0; i<bresult.length; i++){
            if ((bresult[i] >= 48)&&(bresult[i] <= 57)){
                if (decimals){
                    if (_b == 0) break;
                    else _b--;
                }
                mint *= 10;
                mint += uint(bresult[i]) - 48;
            } else if (bresult[i] == 46) decimals = true;
        }
        if (_b > 0) mint *= 10**_b;
        return mint;
    }

    function uintToString(uint i) public pure returns (string){
        if (i == 0) return "0";
        uint j = i;
        uint len;
        while (j != 0){
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len - 1;
        while (i != 0){
            bstr[k--] = byte(48 + i % 10);
            i /= 10;
        }
        return string(bstr);
    }

    function stringToBytes(string memory source) public pure  returns (bytes result) {
        return bytes(source);
    }

    function stringToBytes32(string memory source) public pure  returns (bytes32 result) {
        assembly {
            result := mload(add(source, 32))
        }
    }

    function bytesToString(bytes source) public pure  returns (string memory result) {
        return string(source);
    }

    // 如果是固定大小字节数组转string，那么就需要先将字节数组转动态字节数组，再转字符串。
    // 但是，如果字符串不是占满32个字节。那么后面就会由0进行填充。所以我们需要将这些空字符去掉。
    function bytes32ToString(bytes32 b32name) public pure  returns(string){

        bytes memory bytesString = new bytes(32);

        // 定义一个变量记录字节数量
        uint charCount = 0;

        // 统计共有多少个字节数
        for(uint32 i = 0; i < 32; i++){

            byte char = byte(bytes32(uint(b32name) * 2 ** (8 * i)));  // 将b32name左移i位,参考下面算法
            // 获取到的始终是第0个字节。
            // 但为什么*2

            if(char != 0){
                bytesString[charCount] = char;
                charCount++;
            }
        }

        // 初始化一动态数组，长度为charCount
        bytes memory bytesStringTrimmed = new bytes(charCount);
        for(i = 0;i< charCount;i++){
            bytesStringTrimmed[i] = bytesString[i];
        }

        return string(bytesStringTrimmed);
    }


    function split(string memory src, string memory separator)
    internal
    pure
    returns (string[] memory splitArr) {
        bytes memory srcBytes = bytes(src);

        uint offset = 0;
        uint splitsCount = 1;
        int limit = -1;
        while (offset < srcBytes.length - 1) {
            limit = indexOf(src, separator, offset);
            if (limit == -1)
                break;
            else {
                splitsCount++;
                offset = uint(limit) + 1;
            }
        }

        splitArr = new string[](splitsCount);

        offset = 0;
        splitsCount = 0;
        while (offset < srcBytes.length - 1) {

            limit = indexOf(src, separator, offset);
            if (limit == - 1) {
                limit = int(srcBytes.length);
            }

            string memory tmp = new string(uint(limit) - offset);
            bytes memory tmpBytes = bytes(tmp);

            uint j = 0;
            for (uint i = offset; i < uint(limit); i++) {
                tmpBytes[j++] = srcBytes[i];
            }
            offset = uint(limit) + 1;
            splitArr[splitsCount++] = string(tmpBytes);
        }
        return splitArr;
    }

    function indexOf(string  memory src, string memory value, uint offset)
    internal
    pure
    returns (int) {
        bytes memory srcBytes = bytes(src);
        bytes memory valueBytes = bytes(value);

        assert(valueBytes.length == 1);

        for (uint i = offset; i < srcBytes.length; i++) {
            if (srcBytes[i] == valueBytes[0]) {
                return int(i);
            }
        }

        return -1;
    }



}