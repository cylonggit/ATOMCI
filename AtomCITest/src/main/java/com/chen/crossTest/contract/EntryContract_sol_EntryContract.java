package com.chen.crossTest.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class EntryContract_sol_EntryContract extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b5060038054600160a060020a0319163317905567011c37937e0800006006556040805161032081019091526102f08082526200137860208301398051620000619160079160209091019062000230565b5060e06040519081016040528060ab81526020016200166860ab91398051620000939160089160209091019062000230565b506003600955600c8054600181018083556000839052604080518082019091528381527f4d79457468657265756d2d31000000000000000000000000000000000000000060209091019081529092620000ff926000805160206200135883398151915201919062000230565b5050600c80546001810180835560009290925260408051808201909152600a8082527f4d794661627269632d3100000000000000000000000000000000000000000000602090920191825262000168926000805160206200135883398151915201919062000230565b5050600c80546001810180835560009290925260408051808201909152600a8082527f457468657265756d2d32000000000000000000000000000000000000000000006020909201918252620001d1926000805160206200135883398151915201919062000230565b505060408051808201909152600f8082527f5b737472696e672c737472696e675d000000000000000000000000000000000060209092019182526200021991600a9162000230565b506005805460a060020a60ff0219169055620002d5565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200027357805160ff1916838001178555620002a3565b82800160010185558215620002a3579182015b82811115620002a357825182559160200191906001019062000286565b50620002b1929150620002b5565b5090565b620002d291905b80821115620002b15760008155600101620002bc565b90565b61107380620002e56000396000f3006080604052600436106101485763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166304daeabf811461014a5780630c62195c14610175578063109e94cf1461019757806312065fe0146101b95780631d776323146101ce57806321d93090146101f05780632a01ff75146102105780634fe13ae7146102255780635130a863146102455780635537ae511461025a5780635c8a10531461027a5780635debbe371461029a5780636e633390146102ba57806389035730146102da5780638b33b4b2146102ef57806395c7936414610304578063a48bdb7c14610317578063b5432c661461032c578063c19d93fb14610341578063d598d4c914610363578063d809750b14610378578063db96e57c1461039a578063dc1fb5a5146103ba578063e4d5677b146103cf578063fd922a42146103e4575b005b34801561015657600080fd5b5061015f6103f9565b60405161016c9190610f41565b60405180910390f35b34801561018157600080fd5b5061018a6103ff565b60405161016c9190610f0b565b3480156101a357600080fd5b506101ac61048d565b60405161016c9190610e81565b3480156101c557600080fd5b5061015f61049c565b3480156101da57600080fd5b506101e36104a1565b60405161016c9190610e95565b3480156101fc57600080fd5b5061018a61020b366004610cc4565b610579565b34801561021c57600080fd5b5061015f6105ed565b34801561023157600080fd5b506101ac610240366004610c1e565b6105f3565b34801561025157600080fd5b5061015f610674565b34801561026657600080fd5b5061018a610275366004610c5b565b61067a565b34801561028657600080fd5b506101ac610295366004610c1e565b610709565b3480156102a657600080fd5b506101486102b5366004610bd1565b610753565b3480156102c657600080fd5b506101486102d5366004610cc4565b6107f4565b3480156102e657600080fd5b5061018a610810565b3480156102fb57600080fd5b5061018a61086b565b610148610312366004610c5b565b6108c6565b34801561032357600080fd5b5061018a610a3c565b34801561033857600080fd5b506101ac610a97565b34801561034d57600080fd5b50610356610aa6565b60405161016c9190610efd565b34801561036f57600080fd5b506101ac610ac7565b34801561038457600080fd5b5061038d610ad6565b60405161016c9190610eef565b3480156103a657600080fd5b506101486103b5366004610bd1565b61076a565b3480156103c657600080fd5b506101ac610ae5565b3480156103db57600080fd5b5061018a610af4565b3480156103f057600080fd5b506101ac610b4f565b60065481565b600d805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b820191906000526020600020905b81548152906001019060200180831161046857829003601f168201915b505050505081565b600554600160a060020a031681565b303190565b6060600c805480602002602001604051908101604052809291908181526020016000905b828210156105705760008481526020908190208301805460408051601f600260001961010060018716150201909416939093049283018590048502810185019091528181529283018282801561055c5780601f106105315761010080835404028352916020019161055c565b820191906000526020600020905b81548152906001019060200180831161053f57829003601f168201915b5050505050815260200190600101906104c5565b50505050905090565b600c80548290811061058757fe5b600091825260209182902001805460408051601f60026000196101006001871615020190941693909304928301859004850281018501909152818152935090918301828280156104855780601f1061045a57610100808354040283529160200191610485565b60095490565b6001546040517f4fe13ae7000000000000000000000000000000000000000000000000000000008152600091600160a060020a031690634fe13ae79061063d908590600401610f0b565b600060405180830381600087803b15801561065757600080fd5b505af115801561066b573d6000803e3d6000fd5b50505050919050565b60095481565b600254606090600160a060020a0316331461069457600080fd5b6005805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790556040517f2d929c6e3a086fde10d26d0370d8e63cd191d663efc5651fa39bbbe5eac97c0e906106fb9085908590610f1c565b60405180910390a192915050565b6001546040517f5c8a1053000000000000000000000000000000000000000000000000000000008152600091600160a060020a031690635c8a10539061063d908590600401610f0b565b600354600160a060020a0316331461076a57600080fd5b60008054600160a060020a0394851673ffffffffffffffffffffffffffffffffffffffff19918216179091556001805493851693821684179055600280548216909317909255600480549190931691161790556005805474ff0000000000000000000000000000000000000000191674010000000000000000000000000000000000000000179055565b600354600160a060020a0316331461080b57600080fd5b600655565b600a805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b6008805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b60018060055474010000000000000000000000000000000000000000900460ff1660028111156108f257fe5b146108fc57600080fd5b6006543481111561090c57600080fd5b6001546040517fb71952a8000000000000000000000000000000000000000000000000000000008152600160a060020a039091169063b71952a89061095c90600c90339034908990600401610ea6565b600060405180830381600087803b15801561097657600080fd5b505af115801561098a573d6000803e3d6000fd5b50506005805474ff00000000000000000000000000000000000000001916740200000000000000000000000000000000000000001790555050600154604051600160a060020a03909116903480156108fc02916000818181858888f193505050501580156109fc573d6000803e3d6000fd5b507f9dfcc0fda8675f8b627e400d94e14a5d33b5a67048169acb14d4eec4ce7f7e978385604051610a2e929190610f1c565b60405180910390a150505050565b600b805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b600354600160a060020a031681565b60055474010000000000000000000000000000000000000000900460ff1681565b600254600160a060020a031681565b600154600160a060020a031681565b600054600160a060020a031681565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b600454600160a060020a031681565b6000610b6a8235610fbe565b9392505050565b6000601f82018313610b8257600080fd5b8135610b95610b9082610f76565b610f4f565b91508082526020830160208301858383011115610bb157600080fd5b610bbc838284610ff3565b50505092915050565b6000610b6a8235610fda565b600080600060608486031215610be657600080fd5b6000610bf28686610b5e565b9350506020610c0386828701610b5e565b9250506040610c1486828701610b5e565b9150509250925092565b600060208284031215610c3057600080fd5b813567ffffffffffffffff811115610c4757600080fd5b610c5384828501610b71565b949350505050565b60008060408385031215610c6e57600080fd5b823567ffffffffffffffff811115610c8557600080fd5b610c9185828601610b71565b925050602083013567ffffffffffffffff811115610cae57600080fd5b610cba85828601610b71565b9150509250929050565b600060208284031215610cd657600080fd5b6000610c538484610bc5565b610ceb81610fbe565b82525050565b6000610cfc82610fb0565b80845260208401935083602082028501610d1585610f9e565b60005b84811015610d4c578383038852610d30838351610dc4565b9250610d3b82610f9e565b602098909801979150600101610d18565b50909695505050505050565b6000610d6382610fb4565b80845260208401935083602082028501610d7c85610fa4565b60005b84811015610d4c578383038852610d968383610df9565b9250610da182610fb8565b602098909801979150600101610d7f565b610ceb81610fdd565b610ceb81610fe8565b6000610dcf82610fb0565b808452610de3816020860160208601610fff565b610dec8161102f565b9093016020019392505050565b600081546001811660008114610e165760018114610e3457610e70565b60028204607f16855260ff1982166020860152604085019250610e70565b60028204808652602086019550610e4a85610fa4565b60005b82811015610e6957815488820152600190910190602001610e4d565b8701945050505b505092915050565b610ceb81610fda565b60208101610e8f8284610ce2565b92915050565b60208082528101610b6a8184610cf1565b60808082528101610eb78187610d58565b9050610ec66020830186610ce2565b610ed36040830185610e78565b8181036060830152610ee58184610dc4565b9695505050505050565b60208101610e8f8284610db2565b60208101610e8f8284610dbb565b60208082528101610b6a8184610dc4565b60408082528101610f2d8185610dc4565b90508181036020830152610c538184610dc4565b60208101610e8f8284610e78565b60405181810167ffffffffffffffff81118282101715610f6e57600080fd5b604052919050565b600067ffffffffffffffff821115610f8d57600080fd5b506020601f91909101601f19160190565b60200190565b60009081526020902090565b5190565b5490565b60010190565b600160a060020a031690565b600060038210610fd657fe5b5090565b90565b6000610e8f82610fbe565b6000610e8f82610fca565b82818337506000910152565b60005b8381101561101a578181015183820152602001611002565b83811115611029576000848401525b50505050565b601f01601f1916905600a265627a7a7230582094337361e6376155171d651c5bbd0963f6d10f00779378e039468f132d819b5e6c6578706572696d656e74616cf50037df6966c971051c3d54ec59162606531493a51404a002842f56009d7e5cf4a8c75b7b2266756e6374696f6e4964223a2246756e6374696f6e31222c22636861696e4964223a224d79457468657265756d2d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a22676574526174654f66466162726963222c227061747465726e223a2261746f6d696352656164222c22706172616d6574657273223a5b2275696e74222c22737472696e67225d2c2272657475726e56616c756573223a5b2275696e74225d7d2c7b2266756e6374696f6e4964223a2246756e6374696f6e32222c22636861696e4964223a224d79457468657265756d2d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a22696e63726573734173736574222c227061747465726e223a2261746f6d69635772697465222c22706172616d6574657273223a5b22737472696e67222c2275696e74222c22737472696e67225d2c2272657475726e56616c756573223a5b22626f6f6c225d7d2c7b2266756e6374696f6e4964223a2246756e6374696f6e33222c22636861696e4964223a224d794661627269632d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a22646563726573734173736574222c227061747465726e223a2261746f6d69635772697465222c22706172616d6574657273223a5b22737472696e67222c22696e74222c22737472696e67225d2c2272657475726e56616c756573223a5b22426f6f6c65616e225d7d2c7b2266756e6374696f6e4964223a2246756e6374696f6e34222c22636861696e4964223a224d794661627269632d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a22476574526174654f66457468222c227061747465726e223a2261746f6d696352656164222c22706172616d6574657273223a5b22696e74222c22737472696e67225d2c2272657475726e56616c756573223a5b22696e74225d7d5d5374657020313a204f7065726174696f6e20313a207265203d2046756e6374696f6e3128496e707574735b315d2c496e707574735b325d293b205374657020323a204f7065726174696f6e20323a2046756e6374696f6e3228496e707574735b305d2c496e707574735b315d2c496e707574735b325d293b204f7065726174696f6e20333a2046756e6374696f6e3328496e707574735b305d2c72655b305d2c496e707574735b325d293b";

    protected EntryContract_sol_EntryContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EntryContract_sol_EntryContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<ResultEventResponse> getResultEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Result", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ResultEventResponse> responses = new ArrayList<ResultEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ResultEventResponse typedResponse = new ResultEventResponse();
            typedResponse._invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._resultStr = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ResultEventResponse> resultEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Result", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ResultEventResponse>() {
            @Override
            public ResultEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ResultEventResponse typedResponse = new ResultEventResponse();
                typedResponse._invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._resultStr = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<CrossChainEventEventResponse> getCrossChainEventEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("CrossChainEvent", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<CrossChainEventEventResponse> responses = new ArrayList<CrossChainEventEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            CrossChainEventEventResponse typedResponse = new CrossChainEventEventResponse();
            typedResponse._invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.assetId = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CrossChainEventEventResponse> crossChainEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("CrossChainEvent", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, CrossChainEventEventResponse>() {
            @Override
            public CrossChainEventEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                CrossChainEventEventResponse typedResponse = new CrossChainEventEventResponse();
                typedResponse._invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.assetId = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<BigInteger> invokeFee() {
        Function function = new Function("invokeFee", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> invokeId() {
        Function function = new Function("invokeId", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> client() {
        Function function = new Function("client", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> getBalance() {
        Function function = new Function("getBalance", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List> getChainIds() {
        Function function = new Function("getChainIds", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return executeRemoteCallSingleValueReturn(function, List.class);
    }

    public RemoteCall<String> chainIds(BigInteger param0) {
        Function function = new Function("chainIds", 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> getOperationNum() {
        Function function = new Function("getOperationNum", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> forceSettle(String _invokeId) {
        Function function = new Function(
                "forceSettle", 
                Arrays.<Type>asList(new Utf8String(_invokeId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> operationNum() {
        Function function = new Function("operationNum", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> callback(String _invokeId, String _resultString) {
        Function function = new Function(
                "callback", 
                Arrays.<Type>asList(new Utf8String(_invokeId),
                new Utf8String(_resultString)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> extend(String _invokeId) {
        Function function = new Function(
                "extend", 
                Arrays.<Type>asList(new Utf8String(_invokeId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setServiceInfo(String _community, String _servContract, String _serverAddr) {
        Function function = new Function(
                "setServiceInfo", 
                Arrays.<Type>asList(new Address(_community),
                new Address(_servContract),
                new Address(_serverAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setInvokeFee(BigInteger _invokeFee) {
        Function function = new Function(
                "setInvokeFee", 
                Arrays.<Type>asList(new Uint256(_invokeFee)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> parameters() {
        Function function = new Function("parameters", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> operations() {
        Function function = new Function("operations", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> entryFunction(String _assetId, String _invokeId, BigInteger weiValue) {
        Function function = new Function(
                "entryFunction", 
                Arrays.<Type>asList(new Utf8String(_assetId),
                new Utf8String(_invokeId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> results() {
        Function function = new Function("results", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> dAppProvider() {
        Function function = new Function("dAppProvider", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> state() {
        Function function = new Function("state", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> service() {
        Function function = new Function("service", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> servContract() {
        Function function = new Function("servContract", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> initDebug(String _community, String _servContract, String _serverAddr) {
        Function function = new Function(
                "initDebug", 
                Arrays.<Type>asList(new Address(_community),
                new Address(_servContract),
                new Address(_serverAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> community() {
        Function function = new Function("community", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> remoteFunctions() {
        Function function = new Function("remoteFunctions", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> server() {
        Function function = new Function("server", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<EntryContract_sol_EntryContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EntryContract_sol_EntryContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<EntryContract_sol_EntryContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EntryContract_sol_EntryContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static EntryContract_sol_EntryContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EntryContract_sol_EntryContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EntryContract_sol_EntryContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EntryContract_sol_EntryContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ResultEventResponse {
        public String _invokeId;

        public String _resultStr;
    }

    public static class CrossChainEventEventResponse {
        public String _invokeId;

        public String assetId;
    }
}
