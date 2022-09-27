pragma solidity ^0.4.26;
pragma experimental ABIEncoderV2;
contract CrossCommunityInterface {
    enum PState {Offline, Ready, Serving, Good, Bad}
    function getNum(address _myAddress) public view returns (uint);
    function startService(uint _N, address _server, address _dApp, address _client, string _invokeId, string[] memory _chainIds) public  returns (address[] memory);
    function updateJudgerState(address _judger, PState _state) public;
    function updateServerState(address _server, PState _state) public;
    function getServerReputation(address _server) public view returns(uint,uint);
    function() payable public;
}
contract EntryInterface {
    function setServiceInfo(address _community,address _servContract,address _serverAddr) public;
    function getOperationNum() public view returns(uint);
    function getChainIds() public view returns(string[] memory);
    function callback(string memory _invokeId, string memory _resultString) public  returns(string memory);
    function() payable public;
}
contract ServContract {
    function() payable public {}
    enum State { Init, Ready, Serving, Voting, End}
    State public SCState;

    CrossCommunityInterface public community;
    EntryInterface public entry;
    address public entryAddr;
    uint public totalOperation=3; // This variable is set according to the cross-chain definition in the entry contract
    string[] public chainIds; // according to the cross-chain definition in the entry contract
    uint lostMax=1 ether;
    uint amplifier = 10; // amplifier parameter for deposit, should be more than 2

    // ----------------- cross-chain invocation information ----------------------
    string public invokeId;
    bool invocationResult;
    uint public startBlockNum;
    uint public judgerNumNeed=3;
    uint public judgerNumMin=2;
    uint public maxServiceTime=1000000; // after start, how many blocks should the server to finish his service, set 1000000 in debug mode
    uint public maxAuditTime=2000000; //after start, how many blocks should the judgers to finish their service, set 1000000 in debug mode
    uint public operationEndBNum; // the block number when the server finish his service
    uint public proofNum; // The amount of proofs submitted
    uint public validNum;
    uint public invalidNum;
    uint DNSNum=0; //the number of judgers who have denied to service

    //  server
    address public server;
    uint public serverBalance = 0;
    uint public servForfeit = amplifier*lostMax;  // forfeit should cover the max loss if the cross-chain invocataion comes out invalid, not consider the reputation of the server in debug mode here
    uint public servReward = 50 finney; //0.05 ether  //servReward should more than the gas cost, it is (net profit+ gas cost).
    uint public serverDeposit = servForfeit ; // the server's deposit should not be less than servForfeit

    // judgers
    enum JudgerState { Ready, Voted, Wrong, Right, OverTime, Out}
    struct JudgerInfo {
        bool voted;
        JudgerState state;  //wheterh it is a member judger committee
        bool result;
        uint balance; //the account balance of this judger
        string detail;
    }
    mapping(address => JudgerInfo) judgersInfo;
    address [] public judgerAddrs;
    address [] public votedJudgerAddrs;
    uint public judgerReward = 10 finney;  //the the judger's reward if judger audit the same as the final result
    uint public judgerForfeit = 200 finney; // the penalty if the judger audit wrong or deny to service
    uint public judgerDeposit; // value comes from the community contract when create the service contract

    //dApp provider
    address public dApp; // dApp provider's address
    uint public dAppBalance = 0;
    uint public dAppDeposit = 80 finney; // servReward + judgerNumNeed*judgerReward; // the dApp's deposit should cover the total Fee for the service providers.

    // client
    address public client; // current client's address
    uint public clientBalance = 0;
    uint public invokeFee= 80 finney; // the Fee pass on to the client
    uint public clientCompensation=100 finney;

    //this is the balance to reward the judgeres from the committee
    uint public shareBalance = 0;

    //  -----------  proof -----------------------------------
    enum ProofState {Fresh, Failed, Pass} //not be audited，audited but not passed， audited and passed
    struct Proof{
        string jsonStrProof;  //string like {"lock_do":{"chainId":1,"tranHash":"xxx","blockNum":456},"unlock":{"chainId":1,"tranHash":"xxx","blockNum":456}}
        ProofState state;
    }
    mapping (string=>mapping (uint => Proof)) private crossProofs; //invokeId->operationId->proofString. one corss-chain invocation

    //crosschain invocations return values
    struct ReturnResults{
        string data;
    }
    mapping(string=>ReturnResults) returnResults; //invokeId->return result

    //crosschain invocations history
    struct InvokeResult{
        bool result;
    }
    mapping(string => InvokeResult) private invocationHis;



    //---------------- events ----------------------------------
    //this is to log event that _who modified the SC state to _newstate at time stamp _time
    //event SCStateModified(address indexed _who, uint _time, State _newstate);
    //----------------- construct functions ------------------------
    // --------------------   constructor -- invoke by server in community contract --------------------------
    constructor(address _community, address _entry,address _server, address _dApp, uint _servDeposit, uint _judgerDeposit)
    public payable
    {
        server = _server;
        dApp = _dApp;
        community = CrossCommunityInterface(_community);
        entry=EntryInterface(_entry);
        entryAddr=_entry;
        serverBalance=_servDeposit; //服务者与dapp的押金都不放公共池，是独立记录
        judgerDeposit=_judgerDeposit;
        SCState=State.Init; //debug 时可直接ready
        //debug
        chainIds.push("MyEthereum-1");
        chainIds.push("MyFabric-1");
        // chainIds.push("Ethereum-2");
    }


    //---------------------- Interface for Entry contract--------------------------
    function getResult(string memory _invokeId)
    public view
    checkState(State.End)
    checkEntry()
    returns(string memory)
    {
        return returnResults[_invokeId].data;
    }

    // invoked by entry contract
    function startInvocation(string[] memory _chainIds, address _client,  uint _clientFee,string memory _invokeId)
    public
    checkState(State.Ready)
    checkEntry()
    {
        require(serverBalance>=serverDeposit);
        require(dAppBalance>=dAppDeposit);
        // get random judgers from community
        judgerAddrs=community.startService(judgerNumNeed, server, dApp,_client,_invokeId,_chainIds);
        invokeId=_invokeId;
        client=_client;
        SCState = State.Serving;
        shareBalance=judgerNumNeed*judgerDeposit; //put judger's deposit in shareBalance
        startBlockNum=block.number;
        clientBalance+=_clientFee;
    }

    function setTotalOperation(uint _totalOperation)
    public
    checkState(State.End)
    {
        totalOperation=_totalOperation;
    }

    // --------------------- inferface for dApp ------------------

    // dApp agree to the service contract and deposit in the contract
    function dAppAgree()
    public
    payable
    checkState(State.Init)
    checkDApp
    checkToken(dAppDeposit)
    {
        dAppBalance += msg.value;
        entry.setServiceInfo(address(community),address(this),server);
        totalOperation=entry.getOperationNum();
        chainIds=entry.getChainIds();
        SCState = State.Ready;
    }
    //---------------------- Interface for server --------------------------
    event OperationEnd(uint blocNum); // event for the server finishing his service

    //Insert a business unit operation proofs
    function insertProof(string memory _jsonStrProof,uint _operationId,bool ifFinish)
    public
    checkServer
    checkState(State.Serving)
    {   //proof of an operation on a business unit
        crossProofs[invokeId][_operationId]=Proof(_jsonStrProof,ProofState.Fresh);
        proofNum++;
        if(proofNum==totalOperation){
            SCState = State.Voting; //change to Voting state
            operationEndBNum=block.number;
            emit OperationEnd(block.number);
        }
    }

    //function setContractsInfo(address _community,address _entry) public

    //---------------------- Interface for judger --------------------------
    event VoteEnd(string invokeId,uint blockNum); // event for all judgers finished the audit

    function audit(string memory _invokeId,bool _result, string memory _detail)
    public
    checkState(State.Voting)
    checkInvokeId(_invokeId)
    checkJudger()
    checkHasNotAudit(msg.sender)
    checkBlockNumIn(startBlockNum+maxAuditTime)//合法投票范围内
    {
        judgersInfo[msg.sender]=JudgerInfo(true,JudgerState.Voted,_result,0,_detail); //记录投票信息
        votedJudgerAddrs.push(msg.sender);
        if(_result){
            validNum++;
        }else{
            invalidNum++;
        }
        if(votedJudgerAddrs.length>=judgerNumNeed){  //reach the number judgerNumNeed
            if(validNum>=judgerNumMin){ //pass
                invocationResult=true;
                SCState=State.End;
                //the token from client transfer to the dAppProvider, and the dAppProvider pay the service fee
                clientBalance=0;
                dAppBalance+=clientBalance;
                dAppBalance-=servReward;
                serverBalance+=servReward;
                for(uint i=0;i<votedJudgerAddrs.length;i++){
                    if(judgersInfo[votedJudgerAddrs[i]].result){ // good judger get reward
                        judgersInfo[votedJudgerAddrs[i]].balance+=(judgerReward+judgerDeposit);
                        dAppBalance-=judgerReward;
                        shareBalance-=judgerDeposit;
                        community.updateJudgerState(votedJudgerAddrs[i],CrossCommunityInterface.PState.Good);
                    }else{// bad judger is punished
                        judgersInfo[votedJudgerAddrs[i]].balance=judgerDeposit-judgerForfeit;
                        shareBalance-=(judgerDeposit-judgerForfeit);
                        community.updateJudgerState(votedJudgerAddrs[i],CrossCommunityInterface.PState.Bad);
                    }
                }
                //The deposit of judgers who have denied to audit remains in the shareBalance, along with the penalty for bad judgers, to compensate the DAPPProvider
                serverBalance+=shareBalance/2;
                // clientBalance+=shareBalance/3;
                dAppBalance+=shareBalance/2;
                shareBalance=0;
                community.updateServerState(server,CrossCommunityInterface.PState.Good);
            }
            if(validNum<judgerNumMin){ // not pass
                invocationResult=false;
                SCState=State.End;
                //server is punished
                serverBalance-=servForfeit;
                shareBalance+=servForfeit;

                for(uint j=0;j<votedJudgerAddrs.length;j++){
                    if(!judgersInfo[votedJudgerAddrs[j]].result){ // good judger get reward
                        judgersInfo[votedJudgerAddrs[j]].balance+=(judgerReward+judgerDeposit);
                        shareBalance-=judgerReward;
                        shareBalance-=judgerDeposit;
                        community.updateJudgerState(votedJudgerAddrs[i],CrossCommunityInterface.PState.Good);
                    }else{// bad judger is punished
                        judgersInfo[votedJudgerAddrs[j]].balance=judgerDeposit-judgerForfeit;
                        shareBalance-=(judgerDeposit-judgerForfeit);
                        judgersInfo[votedJudgerAddrs[i]].balance=judgerDeposit-judgerForfeit;
                        community.updateJudgerState(votedJudgerAddrs[i],CrossCommunityInterface.PState.Bad);
                    }
                }
                //The deposit of judgers who have denied to audit remains in the shareBalance, along with the penalty for bad judgers, to compensate the DAPPProvider and the client
                dAppBalance+=shareBalance/2; //because the amplifier is larger than 2, so the dApp provider will not surfer loss
                clientBalance+=shareBalance/2;
                shareBalance=0;
                community.updateServerState(server,CrossCommunityInterface.PState.Bad);
            }
            emit VoteEnd(invokeId,block.number);
            entry.callback(invokeId,"suceess");
        }
    }

    //---------------------- Interface for anyone --------------------------
    function getJudgerInfo(address  _judger)
    public view
    returns(bool,JudgerState,uint)
    {
        return (judgersInfo[_judger].voted,judgersInfo[_judger].state,judgersInfo[_judger].balance);
    }
    //查询当前的余额
    function getBalance() public view returns(uint){
        return this.balance;
    }

    //get a business unit operation proofs
    function getProofs(string memory _invokeId,uint _operationId)
    public view
    returns(string memory,uint,ProofState)
        ///checkState(State.Fresh)
    {
        return (crossProofs[_invokeId][_operationId].jsonStrProof,_operationId,
        crossProofs[_invokeId][_operationId].state);
    }

    //get balance of a judger
    function getJudgerbalance(address _judger)
    public view
    returns(uint)
    {
        return judgersInfo[_judger].balance;
    }

    // for server or dApp provider to increase his deposit
    function deposit()
    public
    payable
    returns (uint)
    {
        if(msg.sender==server){
            serverBalance+=msg.value;
            return serverBalance;
        }
        if(msg.sender==dApp){
            dAppBalance+=msg.value;
            return serverBalance;
        }
        return 0;
    }


    // for all to get back their balance from the contract
    function getBackMoney(string memory _invokeId)
    public
    checkBlockNumOut(startBlockNum+maxAuditTime)
    returns (bool)
    {
        if(msg.sender==server){
            require(serverBalance > 0);
            msg.sender.transfer(serverBalance);
            serverBalance = 0;
            return true;
        }
        if(msg.sender==dApp){
            require(dAppBalance > 0);
            msg.sender.transfer(dAppBalance);
            dAppBalance = 0;
            return true;
        }
        if(msg.sender==client){
            require(clientBalance > 0);
            msg.sender.transfer(clientBalance);
            clientBalance = 0;
            return true;
        }
        if(judgersInfo[msg.sender].balance>0){
            msg.sender.transfer(judgersInfo[msg.sender].balance);
            judgersInfo[msg.sender].balance = 0;
            return true;
        }
    }

    // extend the invocation if some judgers deny to audit
    function extend(string _invokeId)
    public
    checkInvokeId(_invokeId)
    checkBlockNumOut(startBlockNum+maxAuditTime)
    {
        // delete the judgers, but the deposits of judgers who have not audited left in the contract, and their state is still Serving, it will keep record when they turn ready in community.
        delete judgerAddrs;
        DNSNum=judgerNumNeed-votedJudgerAddrs.length;
        //apply for new judgers
        judgerAddrs=community.startService(DNSNum, server, dApp,client,invokeId,chainIds); // because the older judger's state is not ready, they will not be selected.
        //extend the auditTime
        maxAuditTime=maxAuditTime+1000;
    }

    // force to settle and end the invocation if the deposit of judgers who have denied to audit can cover the total loss
    function forceSettle(string _invokeId)
    public
    checkInvokeId(_invokeId)
    checkBlockNumOut(startBlockNum+maxAuditTime)
    {
        if(DNSNum*judgerDeposit>(lostMax+dAppDeposit+clientCompensation)){ //when the total deposit of judgers who have denied to audit over the total loss
            //compensation for server
            serverBalance+=servReward;
            shareBalance-=servReward;
            //compensation for good judgers
            for(uint j=0;j<votedJudgerAddrs.length;j++){
                judgersInfo[votedJudgerAddrs[j]].balance+=(judgerReward+judgerDeposit);
                shareBalance-=(judgerReward+judgerDeposit);
                community.updateJudgerState(votedJudgerAddrs[j],CrossCommunityInterface.PState.Bad);
            }
            //compensation for dApp providers
            dAppBalance+=lostMax;
            shareBalance-=lostMax;
            //compensation for client
            clientBalance+=shareBalance;
            shareBalance=0;
            SCState=State.End;
            community.updateServerState(server,CrossCommunityInterface.PState.Good);
        }
    }



    //-------------------- condition check functions----------------
    modifier checkHasNotAudit(address _judger){
        require(!judgersInfo[_judger].voted);
        _;
    }

    modifier checkInvokeId(string memory _invokeId){
        require(keccak256(_invokeId) == keccak256(invokeId));
        _;
    }
    modifier checkState(State _state){
        require(SCState == _state);
        _;
    }

    modifier checkEntry(){
        require(msg.sender == entryAddr);
        _;
    }
    modifier checkServer() {
        require(msg.sender == server);
        _;
    }

    modifier checkDApp() {
        require(msg.sender == dApp);
        _;
    }

    modifier checkToken(uint _token) {
        require(msg.value >= _token);
        _;
    }

    //check whether the sender is a legal judger member in the committee
    modifier checkJudger() {
        uint num=0;
        for(uint i=0; i<judgerAddrs.length; i++){
            if(msg.sender==judgerAddrs[i]){
                num++;
            }
        }
        require(num>0);
        _;
    }
    modifier checkBlockNumIn(uint _blockNum) {
        require(block.number <= _blockNum);
        _;
    }
    modifier checkBlockNumOut(uint _blockNum) {
        require(block.number >_blockNum);
        _;
    }

    // ------------------ debug functions -------------------------------
    function initDebug(uint _totalOperation)
    public
    payable // keep the contract's token = 10 ehter + 80 finney when debug
    {
        SCState=State.Ready;
        for(uint i = 0 ; i < judgerAddrs.length ; i++){
            delete judgersInfo[judgerAddrs[i]]; // clear all the selected judgers
        }
        delete judgerAddrs;
        delete votedJudgerAddrs;
        operationEndBNum=0;
        validNum=0;
        invalidNum=0;
        dAppBalance=80 finney;
        serverBalance=10 ether;
        proofNum=0;
        totalOperation=_totalOperation;
        msg.sender.transfer(this.balance-10 ether - 80 finney); // let server contract only left servDeposit+dAppDeposit
        shareBalance=0;
    }
}
