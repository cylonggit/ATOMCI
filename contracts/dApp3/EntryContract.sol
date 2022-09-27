pragma solidity ^0.4.26;
pragma experimental ABIEncoderV2;
contract ServContractInterface {
    function startInvocation(string[] memory _chainIds, address _client,  uint _clientFee,string memory _invokeId) public;
    function extend(string _invokeId) public;
    function forceSettle(string _invokeId) public;
    function initAllDebug(uint _totalOperation) public;
    function() public payable;
}
contract EntryContract{
    function() payable public {}
    address  public community;
    ServContractInterface public servContract;
    address public service; //service contract address
    address public dAppProvider;
    address public server;
    address public client;
    enum CrossState {Init,Free,Processing}
    CrossState public state;
    uint public invokeFee;

    string public remoteFunctions;
    string public operations;
    uint public operationNum;
    string public parameters;  //cross-chain parameters type, a json string like:  ["string", "int"]
    string public results;  //cross-chain result type
    string[] public chainIds;
    string public invokeId;

    event Result(string _invokeId,string _resultStr);

    modifier checkState(CrossState _state){
        require(state == _state);
        _;
    }
    modifier checkClient(){
        require(msg.sender == client);
        _;
    }
    modifier checkDapp(){
        require(msg.sender == dAppProvider);
        _;
    }
    modifier checkServer(){
        require(msg.sender == server);
        _;
    }
    modifier checkToken(uint _token) {
        require(msg.value >= _token);
        _;
    }
    modifier checkService(){
        require(msg.sender == service);
        _;
    }
    event CrossChainEvent(string  _invokeId,string assetId); ///the server should subscribe this event

    // --------------------   constructor  --------------------------
    constructor() public{
        dAppProvider = msg.sender;
        invokeFee=80 finney;
        //remoteFunctions definition
        remoteFunctions="[{\"functionId\":\"Function1\",\"chainId\":\"MyEthereum-1\",\"contractId\":\"xxxx...\",\"lang\":\"solidity 4.26\",\"businessUnit\":\"getNumber\",\"pattern\":\"atomicRead\",\"parameters\":[\"string\"],\"returnValues\":[\"uint\"]},{\"functionId\":\"Function2\",\"chainId\":\"MyEthereum-1\",\"contractId\":\"xxxx...\",\"lang\":\"solidity 4.26\",\"businessUnit\":\"incressAsset\",\"pattern\":\"atomicWrite\",\"parameters\":[\"string\",\"uint\"],\"returnValues\":[\"bool\"]},{\"functionId\":\"Function3\",\"chainId\":\"MyFabric-1\",\"contractId\":\"xxxx...\",\"lang\":\"java 1.8\",\"businessUnit\":\"decressAsset\",\"pattern\":\"atomicWrite\",\"parameters\":[\"string\",\"int\"],\"returnValues\":[\"Boolean\"]},{\"functionId\":\"Function4\",\"chainId\":\"MyFabric-1\",\"contractId\":\"xxxx...\",\"lang\":\"java 1.8\",\"businessUnit\":\"getNumber\",\"pattern\":\"atomicRead\",\"parameters\":[\"string\"],\"returnValues\":[\"int\"]}]";
        //dApp operations definition
        operations="Step 1: Operation 1: Function2(Inputs[0], Inputs[1]); Operation 2: Function3(Inputs[0], Inputs[1]);";
        operationNum=2;
        chainIds.push("MyEthereum-1");
        chainIds.push("MyFabric-1");
        chainIds.push("Ethereum-2");
        parameters="[string,string]";
        state=CrossState.Init;
    }
    function getCrossChainLogic() public view returns(string,string){
        return(remoteFunctions,operations);
    }
    // the dAppProvider can set the invokeFee to pass the Fee on to the client
    function setInvokeFee(uint _invokeFee)
    public
    checkDapp()
    {
        invokeFee=_invokeFee;
    }
    // the dAppProvider set the service community and contract information
    function setServiceInfo(address _community,address _servContract,address _serverAddr)
    public
    checkDapp()
    {
        community=_community;
        servContract=ServContractInterface(_servContract);
        service=_servContract;
        server=_serverAddr;
        state=CrossState.Free;
    }

    function getOperationNum() public view returns(uint){
        return operationNum;
    }
    function getChainIds() public view returns(string[] memory){
        return chainIds;
    }
    // cross-chain invocation entryFunction, the server should subscribe this event
    function entryFunction(string memory _assetId,string _invokeId)
    public payable
    checkState(CrossState.Free)
    checkToken(invokeFee) // dappManager can pass the Fee on to the client.  closed in debug mode.
    {
        servContract.startInvocation(chainIds,msg.sender,msg.value,_invokeId);
        state=CrossState.Processing;
        servContract.transfer(msg.value); //transfer the token of client to servContract
        emit CrossChainEvent(_invokeId,_assetId);
    }

    // the server callBack; client should subcribe this event
    function callback(string memory _invokeId, string memory _resultString)
    public
    checkService()
    returns(string memory)
    {
        state=CrossState.Free;
        emit Result(_invokeId,_resultString);
    }

    function extend(string _invokeId)
    public
    returns(address)
    {
        servContract.extend(_invokeId);
    }
    function forceSettle(string _invokeId)
    public
    returns(address)
    {
        servContract.forceSettle(_invokeId);
    }

    //--------------------------- debug functions-----------------------------
    function getBalance() public view returns(uint){
        return this.balance;
    }
    function initDebug(address _community,address _servContract,address _serverAddr)
    public
    {
        community=_community;
        servContract=ServContractInterface(_servContract);
        service=_servContract;
        server=_serverAddr;
        state=CrossState.Free;

    }
}
