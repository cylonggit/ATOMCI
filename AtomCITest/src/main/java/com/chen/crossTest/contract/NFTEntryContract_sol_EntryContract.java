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
import org.web3j.abi.datatypes.generated.Int256;
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
public class NFTEntryContract_sol_EntryContract extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b5060038054600160a060020a0319163317905567011c37937e0800006006556040805161020081019091526101cb80825262001495602083013980516200006191600791602090910190620001ef565b5060e06040519081016040528060b181526020016200166060b1913980516200009391600891602090910190620001ef565b506002600955600c8054600181018083556000839052604080518082019091528381527f4d79457468657265756d2d31000000000000000000000000000000000000000060209091019081529092620000ff9260008051602062001475833981519152019190620001ef565b5050600c80546001810180835560009290925260408051808201909152600a8082527f4d794661627269632d31000000000000000000000000000000000000000000006020909201918252620001689260008051602062001475833981519152019190620001ef565b505060408051606081018252602d8082527f5b696e742c75696e742c616464726573732c737472696e672c62797465732c73602083019081527f7472696e672c737472696e675d000000000000000000000000000000000000009290930191909152620001d891600a91620001ef565b506005805460a060020a60ff021916905562000294565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200023257805160ff191683800117855562000262565b8280016001018555821562000262579182015b828111156200026257825182559160200191906001019062000245565b506200027092915062000274565b5090565b6200029191905b808211156200027057600081556001016200027b565b90565b6111d180620002a46000396000f3006080604052600436106101485763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166304daeabf811461014a5780630c62195c14610175578063109e94cf1461019757806312065fe0146101b95780631d776323146101ce57806321d93090146101f05780632a01ff75146102105780634fe13ae7146102255780635130a863146102455780635537ae511461025a5780635c8a10531461027a5780635debbe371461029a5780636e633390146102ba57806389035730146102da5780638b33b4b2146102ef578063a48bdb7c14610304578063b5432c6614610319578063c19d93fb1461032e578063d598d4c914610350578063d809750b14610365578063d8b38ad914610387578063db96e57c1461039a578063dc1fb5a5146103ba578063e4d5677b146103cf578063fd922a42146103e4575b005b34801561015657600080fd5b5061015f6103f9565b60405161016c919061109f565b60405180910390f35b34801561018157600080fd5b5061018a6103ff565b60405161016c9190611012565b3480156101a357600080fd5b506101ac61048d565b60405161016c9190610f88565b3480156101c557600080fd5b5061015f61049c565b3480156101da57600080fd5b506101e36104a1565b60405161016c9190610f9c565b3480156101fc57600080fd5b5061018a61020b366004610dcb565b610579565b34801561021c57600080fd5b5061015f6105ed565b34801561023157600080fd5b506101ac610240366004610d25565b6105f3565b34801561025157600080fd5b5061015f610674565b34801561026657600080fd5b5061018a610275366004610d62565b61067a565b34801561028657600080fd5b506101ac610295366004610d25565b610709565b3480156102a657600080fd5b506101486102b5366004610bdc565b610753565b3480156102c657600080fd5b506101486102d5366004610dcb565b6107f4565b3480156102e657600080fd5b5061018a610810565b3480156102fb57600080fd5b5061018a61086b565b34801561031057600080fd5b5061018a6108c6565b34801561032557600080fd5b506101ac610921565b34801561033a57600080fd5b50610343610930565b60405161016c9190611004565b34801561035c57600080fd5b506101ac610951565b34801561037157600080fd5b5061037a610960565b60405161016c9190610ff6565b610148610395366004610c29565b61096f565b3480156103a657600080fd5b506101486103b5366004610bdc565b61076a565b3480156103c657600080fd5b506101ac610af0565b3480156103db57600080fd5b5061018a610aff565b3480156103f057600080fd5b506101ac610b5a565b60065481565b600d805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b820191906000526020600020905b81548152906001019060200180831161046857829003601f168201915b505050505081565b600554600160a060020a031681565b303190565b6060600c805480602002602001604051908101604052809291908181526020016000905b828210156105705760008481526020908190208301805460408051601f600260001961010060018716150201909416939093049283018590048502810185019091528181529283018282801561055c5780601f106105315761010080835404028352916020019161055c565b820191906000526020600020905b81548152906001019060200180831161053f57829003601f168201915b5050505050815260200190600101906104c5565b50505050905090565b600c80548290811061058757fe5b600091825260209182902001805460408051601f60026000196101006001871615020190941693909304928301859004850281018501909152818152935090918301828280156104855780601f1061045a57610100808354040283529160200191610485565b60095490565b6001546040517f4fe13ae7000000000000000000000000000000000000000000000000000000008152600091600160a060020a031690634fe13ae79061063d908590600401611012565b600060405180830381600087803b15801561065757600080fd5b505af115801561066b573d6000803e3d6000fd5b50505050919050565b60095481565b600254606090600160a060020a0316331461069457600080fd5b6005805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790556040517f2d929c6e3a086fde10d26d0370d8e63cd191d663efc5651fa39bbbe5eac97c0e906106fb9085908590611023565b60405180910390a192915050565b6001546040517f5c8a1053000000000000000000000000000000000000000000000000000000008152600091600160a060020a031690635c8a10539061063d908590600401611012565b600354600160a060020a0316331461076a57600080fd5b60008054600160a060020a0394851673ffffffffffffffffffffffffffffffffffffffff19918216179091556001805493851693821684179055600280548216909317909255600480549190931691161790556005805474ff0000000000000000000000000000000000000000191674010000000000000000000000000000000000000000179055565b600354600160a060020a0316331461080b57600080fd5b600655565b600a805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b6008805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b600b805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b600354600160a060020a031681565b60055474010000000000000000000000000000000000000000900460ff1681565b600254600160a060020a031681565b600154600160a060020a031681565b60018060055474010000000000000000000000000000000000000000900460ff16600281111561099b57fe5b146109a557600080fd5b600654348111156109b557600080fd5b6001546040517fb71952a8000000000000000000000000000000000000000000000000000000008152600160a060020a039091169063b71952a890610a0590600c90339034908990600401610fad565b600060405180830381600087803b158015610a1f57600080fd5b505af1158015610a33573d6000803e3d6000fd5b50506005805474ff00000000000000000000000000000000000000001916740200000000000000000000000000000000000000001790555050600154604051600160a060020a03909116903480156108fc02916000818181858888f19350505050158015610aa5573d6000803e3d6000fd5b507fe2e57fe23773ae336cd05eb74952d23acc2be31644234f890046f0b8bfc8fa6483898b8a8a604051610add959493929190611048565b60405180910390a1505050505050505050565b600054600160a060020a031681565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104855780601f1061045a57610100808354040283529160200191610485565b600454600160a060020a031681565b6000610b75823561111c565b9392505050565b6000601f82018313610b8d57600080fd5b8135610ba0610b9b826110d4565b6110ad565b91508082526020830160208301858383011115610bbc57600080fd5b610bc7838284611151565b50505092915050565b6000610b758235611138565b600080600060608486031215610bf157600080fd5b6000610bfd8686610b69565b9350506020610c0e86828701610b69565b9250506040610c1f86828701610b69565b9150509250925092565b600080600080600080600060e0888a031215610c4457600080fd5b6000610c508a8a610bd0565b9750506020610c618a828b01610bd0565b9650506040610c728a828b01610b69565b955050606088013567ffffffffffffffff811115610c8f57600080fd5b610c9b8a828b01610b7c565b945050608088013567ffffffffffffffff811115610cb857600080fd5b610cc48a828b01610b7c565b93505060a088013567ffffffffffffffff811115610ce157600080fd5b610ced8a828b01610b7c565b92505060c088013567ffffffffffffffff811115610d0a57600080fd5b610d168a828b01610b7c565b91505092959891949750929550565b600060208284031215610d3757600080fd5b813567ffffffffffffffff811115610d4e57600080fd5b610d5a84828501610b7c565b949350505050565b60008060408385031215610d7557600080fd5b823567ffffffffffffffff811115610d8c57600080fd5b610d9885828601610b7c565b925050602083013567ffffffffffffffff811115610db557600080fd5b610dc185828601610b7c565b9150509250929050565b600060208284031215610ddd57600080fd5b6000610d5a8484610bd0565b610df28161111c565b82525050565b6000610e038261110e565b80845260208401935083602082028501610e1c856110fc565b60005b84811015610e53578383038852610e37838351610ed4565b9250610e42826110fc565b602098909801979150600101610e1f565b50909695505050505050565b6000610e6a82611112565b80845260208401935083602082028501610e8385611102565b60005b84811015610e53578383038852610e9d8383610f09565b9250610ea882611116565b602098909801979150600101610e86565b610df28161113b565b610df281611146565b610df281611138565b6000610edf8261110e565b808452610ef381602086016020860161115d565b610efc8161118d565b9093016020019392505050565b600081546001811660008114610f265760018114610f4457610f80565b60028204607f16855260ff1982166020860152604085019250610f80565b60028204808652602086019550610f5a85611102565b60005b82811015610f7957815488820152600190910190602001610f5d565b8701945050505b505092915050565b60208101610f968284610de9565b92915050565b60208082528101610b758184610df8565b60808082528101610fbe8187610e5f565b9050610fcd6020830186610de9565b610fda6040830185610ecb565b8181036060830152610fec8184610ed4565b9695505050505050565b60208101610f968284610eb9565b60208101610f968284610ec2565b60208082528101610b758184610ed4565b604080825281016110348185610ed4565b90508181036020830152610d5a8184610ed4565b60a080825281016110598188610ed4565b90506110686020830187610ecb565b6110756040830186610ecb565b6110826060830185610de9565b81810360808301526110948184610ed4565b979650505050505050565b60208101610f968284610ecb565b60405181810167ffffffffffffffff811182821017156110cc57600080fd5b604052919050565b600067ffffffffffffffff8211156110eb57600080fd5b506020601f91909101601f19160190565b60200190565b60009081526020902090565b5190565b5490565b60010190565b600160a060020a031690565b60006003821061113457fe5b5090565b90565b6000610f968261111c565b6000610f9682611128565b82818337506000910152565b60005b83811015611178578181015183820152602001611160565b83811115611187576000848401525b50505050565b601f01601f1916905600a265627a7a72305820399d95bcc7c7380df8e5b7cb113d05eaf0597c71cd3c446df4babc961ef8eb396c6578706572696d656e74616cf50037df6966c971051c3d54ec59162606531493a51404a002842f56009d7e5cf4a8c75b7b2266756e6374696f6e4964223a2246756e6374696f6e31222c22636861696e4964223a224d79457468657265756d2d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a2263726f7373436861696e5472616e73666572222c227061747465726e223a2261746f6d69635772697465222c22706172616d6574657273223a5b22696e74222c2275696e74323536222c2261646472657373222c22737472696e67222c226279746573222c22737472696e67225d2c2272657475726e56616c756573223a5b2275696e74225d7d2c7b2266756e6374696f6e4964223a2246756e6374696f6e32222c22636861696e4964223a224d794661627269632d31222c22636f6e74726163744964223a22787878782e2e2e222c22627573696e657373556e6974223a2263726f7373436861696e5472616e73666572222c227061747465726e223a2261746f6d69635772697465222c22706172616d6574657273223a5b22696e74222c22537472696e67222c22537472696e67222c22537472696e67222c22537472696e67222c22537472696e67222c22537472696e67225d2c2272657475726e56616c756573223a5b22626f6f6c225d7d5d5374657020313a204f7065726174696f6e20313a2046756e6374696f6e3128496e707574735b305d2c496e707574735b315d2c496e707574735b325d2c496e707574735b335d2c496e707574735b345d2c496e707574735b365d293b204f7065726174696f6e20323a2046756e6374696f6e3228496e707574735b305d2c496e707574735b315d2c496e707574735b325d2c496e707574735b335d2c496e707574735b355d2c496e707574735b365d293b";

    protected NFTEntryContract_sol_EntryContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NFTEntryContract_sol_EntryContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Int256>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<CrossChainEventEventResponse> responses = new ArrayList<CrossChainEventEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            CrossChainEventEventResponse typedResponse = new CrossChainEventEventResponse();
            typedResponse.invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.transferType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.ethAddr = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.msp = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CrossChainEventEventResponse> crossChainEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("CrossChainEvent", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Int256>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, CrossChainEventEventResponse>() {
            @Override
            public CrossChainEventEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                CrossChainEventEventResponse typedResponse = new CrossChainEventEventResponse();
                typedResponse.invokeId = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.transferType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.ethAddr = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.msp = (String) eventValues.getNonIndexedValues().get(4).getValue();
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_invokeId)), 
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_invokeId), 
                new org.web3j.abi.datatypes.Utf8String(_resultString)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> extend(String _invokeId) {
        Function function = new Function(
                "extend", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_invokeId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setServiceInfo(String _community, String _servContract, String _serverAddr) {
        Function function = new Function(
                "setServiceInfo", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_community), 
                new org.web3j.abi.datatypes.Address(_servContract), 
                new org.web3j.abi.datatypes.Address(_serverAddr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setInvokeFee(BigInteger _invokeFee) {
        Function function = new Function(
                "setInvokeFee", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_invokeFee)), 
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

    public RemoteCall<TransactionReceipt> entryFunction(BigInteger _type, BigInteger _tokenId, String _ethAddr, String fabricMSPID, byte[] _ethSign, String _fabricSign, String _invokeId, BigInteger weiValue) {
        Function function = new Function(
                "entryFunction", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_type), 
                new org.web3j.abi.datatypes.generated.Uint256(_tokenId), 
                new org.web3j.abi.datatypes.Address(_ethAddr), 
                new org.web3j.abi.datatypes.Utf8String(fabricMSPID), 
                new org.web3j.abi.datatypes.DynamicBytes(_ethSign), 
                new org.web3j.abi.datatypes.Utf8String(_fabricSign), 
                new org.web3j.abi.datatypes.Utf8String(_invokeId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> initDebug(String _community, String _servContract, String _serverAddr) {
        Function function = new Function(
                "initDebug", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_community), 
                new org.web3j.abi.datatypes.Address(_servContract), 
                new org.web3j.abi.datatypes.Address(_serverAddr)), 
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

    public static RemoteCall<NFTEntryContract_sol_EntryContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NFTEntryContract_sol_EntryContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NFTEntryContract_sol_EntryContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NFTEntryContract_sol_EntryContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static NFTEntryContract_sol_EntryContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NFTEntryContract_sol_EntryContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static NFTEntryContract_sol_EntryContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NFTEntryContract_sol_EntryContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ResultEventResponse {
        public String _invokeId;

        public String _resultStr;
    }

    public static class CrossChainEventEventResponse {
        public String invokeId;

        public BigInteger tokenId;

        public BigInteger transferType;

        public String ethAddr;

        public String msp;
    }
}
