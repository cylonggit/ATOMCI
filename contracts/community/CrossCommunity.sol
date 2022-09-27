pragma solidity ^0.4.26;
pragma experimental ABIEncoderV2;
import  "./ServContract.sol" ;
contract CrossCommunity {
    function() payable public {}
    // blockchains information
    mapping(string => string) chainList;

    uint serverBaseDeposit=200 finney;   //server deposit when register
    uint judgerBaseDeposit=200 finney;  //judger deposit when register. this will be transfered to service contract which selects the judger

    enum PState {Offline, Ready, Serving, Good, Bad}  //the state of the service provider: server or judger

    constructor() public payable
    {
        //debug init chains
        chainList["MyEthereum-1"]="RPC:192.168.17.131:8545,detail:xxx";
        chainList["MyFabric-1"]="RPC:10.17.x.x:x,detail:xxx";
        chainList["Ethereum-2"]="RPC:192.168.x.x:x,detail:xxx";
        //debug init 5 judgers
        address[] memory addrs= new address[](5);
        addrs[0]=0x960082d1b215929f426517EE8b0c34F8b6d4dAF3;
        addrs[1]=0xD4F7e685AEEB0C0F697F9DfCA2f36bb113FB9d39;
        addrs[2]=0x897a792665E48D1cAAED5cea50f396b8A77d6650;
        addrs[3]=0x3B9eD344D2885d33dD8C3D2411411dF94ab8E824;
        addrs[4]=0xeF77D5819f56bB7d20Cf99B951E90E074A1577cd;
        for(uint i=0;i<5;i++){
            judgerPool[addrs[i]].index = judgerAddrs.push(addrs[i]) - 1;
            judgerPool[addrs[i]].servTimes = 0;
            judgerPool[addrs[i]].badTimes = 0;
            judgerPool[addrs[i]].balance = 200 finney;
            judgerPool[addrs[i]].registered = true;
            judgerPool[addrs[i]].state = PState.Ready;
            // judgerPool[addrs[i]].hasVote=false;
            judgerPool[addrs[i]].servContract=0x0;
            judgerPool[addrs[i]].supportChains["MyEthereum-1"]=true;
            judgerPool[addrs[i]].supportChains["MyFabric-1"]=true;
            judgerPool[addrs[i]].supportChains["Ethereum-2"]=true;
            onlineJudgerNum++;
        }

        //debug init  a server
        address serverAddr=0xEd869df2e1a1A17A21F3ce8ADF437F868D53167C;
        serverAddrs.push(serverAddr);
        serverPool[serverAddr].index = 0;
        serverPool[serverAddr].servTimes = 0;
        serverPool[serverAddr].badTimes = 0;
        serverPool[serverAddr].balance = 200 finney;
        serverPool[serverAddr].registered = true;
        serverPool[serverAddr].state = PState.Ready;
        serverPool[serverAddr].servContract=0x0;
        serverPool[serverAddr].supportChains["MyEthereum-1"]=true;
        serverPool[serverAddr].supportChains["MyFabric-1"]=true;
        serverPool[serverAddr].supportChains["Ethereum-2"]=true;
        onlineServerNum++;

        //debug init a dApp Provider and entry contract
        DAppPool[address(0)]=DApp(0x65CE35093D037031DcE8F37B790D72a4E37Ae47C,address(0));

    }

    //  ---------------------- judge   ----------------
    struct Judger {
        mapping(string => bool) supportChains; //chainId => true/false
        PState state;
        uint servTimes;
        uint badTimes;
        uint balance;
        address servContract;
        bool registered;
        uint index;
    }

    mapping(address => Judger) judgerPool;
    address [] public judgerAddrs;
    uint public onlineJudgerNum = 0;

    // judger should subscribe to the event
    event JudgerSelected(address indexed _judgerAddr, uint _index, address _forWhom);


    modifier checkToken(uint _token) {
        require(msg.value == _token);
        _;
    }
    modifier checkTokenEqFinney(uint _token) {
        uint oneUnit=1 finney;
        require(msg.value == (_token*oneUnit));
        _;
    }

    modifier checkJudgerNotExist(address _register){
        require(!judgerPool[_register].registered);
        _;
    }

    modifier checkJudger(address _judger){
        require(judgerPool[_judger].registered);
        _;
    }

    modifier checkJudgerState(address _judger,PState _state){
        require(judgerPool[_judger].state==_state);
        _;
    }

    //  ---------------------- server   ----------------
    struct Server {
        mapping(string => bool) supportChains; //chainId => true/false
        PState state;
        uint servTimes;
        uint badTimes;
        uint balance;
        address servContract;
        bool registered;
        uint index;
    }
    mapping(address => Server) serverPool;
    address [] public serverAddrs;
    uint public onlineServerNum = 0;

    modifier checkServerNotExist(address _register){
        require(!serverPool[_register].registered);
        _;
    }
    modifier checkServer(address _server){
        require(serverPool[_server].registered);
        _;
    }

    //  ---------------------- dApp and entry contract   ----------------
    struct DApp{
        address dAppProvider;
        address entryAddr;
    }
    mapping (address => DApp) DAppPool;


    // -----------------------service Contract list ------------------------
    struct ServiceInfo {
        bool valid;
        bool hasCommittee;
    }
    event NewServContract(address _entryContract,address _servContract, uint _time);
    mapping(address => ServiceInfo) servContractPool;

    modifier checkServContract(address _sc){
        require(servContractPool[_sc].valid);
        _;
    }

    // --------------------- inferface for server ------------------
    //when the server create service contract, msg.value==_serverDeposit finney
    function createServContract(address _entry,address _dApp)//,address _dApp)
    public
    payable
    returns
    (address)
    {
        //address dAppManager=0x65CE35093D037031DcE8F37B790D72a4E37Ae47C; // debug
        address newServContract = new ServContract(address(this),_entry, msg.sender, _dApp,msg.value,judgerBaseDeposit);
        servContractPool[newServContract].valid = true;
        servContractPool[newServContract].hasCommittee=false;
        newServContract.transfer(msg.value);  //if has msg.value, transfer to service contract as deposit
        emit NewServContract( _entry, newServContract,now);
        serverPool[msg.sender].servContract=newServContract;
        serverPool[msg.sender].state = PState.Serving;
        return newServContract;
    }


    // --------------------- inferface for Normal User ------------------
    function judgerRegister(string[] memory _chainIds)
    public
    payable
    checkToken(judgerBaseDeposit)
    checkJudgerNotExist(msg.sender)
    {
        judgerPool[msg.sender].index = judgerAddrs.push(msg.sender) - 1;
        judgerPool[msg.sender].state = PState.Offline;
        judgerPool[msg.sender].servTimes = 0;
        judgerPool[msg.sender].badTimes = 0;
        judgerPool[msg.sender].balance = msg.value;
        judgerPool[msg.sender].registered = true;
        for(uint i=0;i<_chainIds.length;i++){
            judgerPool[msg.sender].supportChains[_chainIds[i]]=true;
        }
    }

    function serverRegister(string[] memory _chainIds)
    public
    payable
    checkToken(serverBaseDeposit)
    checkServerNotExist(msg.sender)
    {
        serverPool[msg.sender].index = serverAddrs.push(msg.sender) - 1;
        serverPool[msg.sender].state = PState.Offline;
        serverPool[msg.sender].servTimes = 0;
        serverPool[msg.sender].badTimes = 0;
        serverPool[msg.sender].balance = msg.value;
        serverPool[msg.sender].registered = true;
        for(uint i=0;i<_chainIds.length;i++){
            serverPool[msg.sender].supportChains[_chainIds[i]]=true;
        }
    }
    // --------------------- inferface for ServContract ------------------

    function startService(uint _N, address _server, address _dApp, address _client, string _invokeId, string[] memory _chainIds)
    public
        //checkServContract(msg.sender) //debug 关闭验证
    returns
    (address[] memory)
    {
        //require(onlineJudgerNum >= _N*10); // there should be more than 10 times of _N online judgeres, closed in debug mode
        address[] memory judgersSelected=new address[](_N);
        uint seed = (uint)(keccak256(_invokeId));
        for (uint i = 1; i < 7; i++){  //combine the hash values of the lastest 6 blocks to make the ramdom seed more secure
            seed += (uint)(blockhash(block.number - i));
        }
        uint jcounter = 0;
        while (jcounter < _N) {
            address sAddr = judgerAddrs[seed % judgerAddrs.length];
            if (judgerPool[sAddr].state == PState.Ready && sAddr != _server && sAddr != _dApp && sAddr != _client) //满足条件
            {
                bool ifSupport=true;
                // Check that Judger supports these chains
                for(uint j=0;j<_chainIds.length;j++){
                    if(!judgerPool[sAddr].supportChains[_chainIds[j]]){
                        ifSupport=false;
                        break;
                    }
                }
                if(ifSupport){
                    judgersSelected[jcounter]=sAddr;
                    judgerPool[sAddr].state = PState.Serving;
                    judgerPool[sAddr].servContract = msg.sender;
                    emit JudgerSelected(sAddr, judgerPool[sAddr].index, msg.sender);
                    onlineJudgerNum--;
                    msg.sender.transfer(judgerBaseDeposit);// transfer the judger's base deposit to service contract
                    //msg.sender.transfer(judgerPool[sAddr].deopsit);
                    judgerPool[sAddr].balance-=judgerBaseDeposit;
                    jcounter++;
                }
            }
            seed = (uint)(keccak256(uint(seed)));
        }
        servContractPool[msg.sender].hasCommittee = true;
        return judgersSelected;
    }

    //    function updateJudgerServTime(address _judger, bool _good)
    //    public
    //    checkJudger(_judger)
    //    checkServContract(msg.sender)
    //    {
    //        require(judgerPool[_judger].servContract == msg.sender);
    //        judgerPool[_judger].servTimes += 1;
    //        if(!_good){
    //            judgerPool[_judger].badTimes += 1;
    //        }
    //    }
    function getServerReputation(address _server) public view returns(uint,uint){
        return (serverPool[_server].servTimes,serverPool[_server].badTimes);
    }
    function updateJudgerState(address _judger, PState _state)
    public
    checkJudger(_judger)
    checkServContract(msg.sender)
    {
        require(judgerPool[_judger].servContract == msg.sender);
        judgerPool[_judger].state=_state;
    }
    function updateServerState(address _server, PState _state)
    public
    checkServer(_server)
    checkServContract(msg.sender)
    {
        require(serverPool[_server].servContract == msg.sender);
        serverPool[_server].state=_state;
    }

    // --------------------- inferface for Judger ------------------
    modifier checkJudgerDeposit() {
        uint need=judgerBaseDeposit;
        if(judgerPool[msg.sender].registered){
            need = need+(judgerPool[msg.sender].badTimes*100 finney)-judgerPool[msg.sender].balance;
        }
        require(msg.value >= need);
        _;
    }
    modifier checkServerDeposit() {
        uint need=serverBaseDeposit;
        if(serverPool[msg.sender].registered){
            need = need+(serverPool[msg.sender].badTimes*100 finney)-serverPool[msg.sender].balance;
        }
        require(msg.value >= need);
        _;
    }


    function judgerReady()
    public
    payable
    checkJudger(msg.sender)
    checkJudgerDeposit
    {
        //require(judgerPool[msg.sender].state != PState.Serving);
        require(judgerPool[msg.sender].badTimes < 10);
        //the record will changed according to the last service
        if(judgerPool[msg.sender].state == PState.Good){
            judgerPool[msg.sender].servTimes+=1;
        }
        if(judgerPool[msg.sender].state == PState.Bad  || judgerPool[msg.sender].state == PState.Serving){
            judgerPool[msg.sender].servTimes+=1;
            judgerPool[msg.sender].badTimes+=1;
        }
        judgerPool[msg.sender].state = PState.Ready;
        judgerPool[msg.sender].servContract=0x0;
        judgerPool[msg.sender].balance+=msg.value;
        onlineJudgerNum++;
    }
    function judgerOff()
    public
    checkJudger(msg.sender)
    checkJudgerState(msg.sender,PState.Ready)
    {
        //require(judgerPool[msg.sender].state != PState.Serving);
        judgerPool[msg.sender].state = PState.Offline;
        onlineJudgerNum--;
    }

    function getJudgerInfo(address _judger)
    public
    view
    returns
    (CrossCommunity.PState, address,uint, uint,uint)
    {
        return (judgerPool[_judger].state, judgerPool[_judger].servContract, judgerPool[_judger].balance,judgerPool[_judger].servTimes, judgerPool[_judger].badTimes);
    }

    // ------------- Interface for Server ------------------------------
    function serverReady()
    public
    checkServer(msg.sender)
    checkServerDeposit
    {
        //require(serverPool[msg.sender].state != PState.Serving);
        require(serverPool[msg.sender].badTimes < 10);
        //the record will changed according to the last service
        if(serverPool[msg.sender].state == PState.Good){
            serverPool[msg.sender].servTimes+=1;
        }
        if(serverPool[msg.sender].state == PState.Bad || serverPool[msg.sender].state == PState.Serving){
            serverPool[msg.sender].servTimes+=1;
            serverPool[msg.sender].badTimes+=1;
        }
        serverPool[msg.sender].state = PState.Ready;
        judgerPool[msg.sender].servContract=0x0;
        judgerPool[msg.sender].balance+=msg.value;
        onlineServerNum++;
    }

    function serverOff()
    public
    checkServer(msg.sender)
    {
        require(serverPool[msg.sender].state != PState.Serving);
        serverPool[msg.sender].state = PState.Offline;
        onlineServerNum--;
    }

    function getServerInfo(address _server)
    public
    view
    returns
    (CrossCommunity.PState, address,uint, uint,uint)
    {
        return (serverPool[_server].state, serverPool[_server].servContract, serverPool[_server].balance,serverPool[_server].servTimes, serverPool[_server].badTimes);
    }


    //  ---------------  debug functions -----------------------------------------
    function getBalance() public view returns(uint){
        return this.balance;
    }
    function debugWithdraw()
    public
    {
        if(address(this).balance > 0)
            msg.sender.transfer(address(this).balance);
    }
    function initDebug(address _servAddr)
    public
    payable //send 800 finney as the deposit of the judgers and server
    {
        for(uint j=0;j<judgerAddrs.length;j++){
            judgerPool[judgerAddrs[j]].state = PState.Ready;
            judgerPool[judgerAddrs[j]].balance=200 finney;
        }
        onlineJudgerNum=judgerAddrs.length;
        serverPool[serverAddrs[0]].state=PState.Ready;
        serverPool[serverAddrs[0]].balance=200 finney;
        serverPool[serverAddrs[0]].servContract=_servAddr;
        if(address(this).balance > 1200 finney)
            msg.sender.transfer(address(this).balance-(1200 finney));

    }

    function setServerState(address _addr,PState _state)
    public
    payable
    {
        serverPool[_addr].state=_state;
    }
    function setJudgerState(address _addr,PState _state)
    public
    payable
    {
        judgerPool[_addr].state=_state;
    }

    function addJudgers(address _addr) public payable returns (uint){
        judgerPool[_addr].index = judgerAddrs.push(_addr) - 1;
        judgerPool[_addr].servTimes = 0;
        judgerPool[_addr].badTimes = 0;
        judgerPool[_addr].balance = 200 finney;
        judgerPool[_addr].registered = true;
        judgerPool[_addr].state = PState.Ready;
        // judgerPool[addrs[i]].hasVote=false;
        judgerPool[_addr].servContract=0x0;
        judgerPool[_addr].supportChains["MyEthereum-1"]=true;
        judgerPool[_addr].supportChains["MyFabric-1"]=true;
        judgerPool[_addr].supportChains["Ethereum-2"]=true;
        onlineJudgerNum++;
        return judgerPool[_addr].index;
    }
}
