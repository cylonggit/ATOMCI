from web3 import Web3
import multiprocessing
import datetime
import time
# import hashlib
from web3.middleware import geth_poa_middleware
from multiprocessing import Process, Manager
import mysql.connector
true = True
false = False
# ----------------- configurations ---------------
contractABI = {
    "abi": [
    {
        "constant":true,
        "inputs":[

        ],
        "name":"creator",
        "outputs":[
            {
                "name":"",
                "type":"address"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            }
        ],
        "name":"unLockAssetDebug",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_lockHash",
                "type":"bytes32"
            }
        ],
        "name":"incressAsset_lock_do",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_lock",
                "type":"bool"
            }
        ],
        "name":"setAsset",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_arr",
                "type":"string[32]"
            }
        ],
        "name":"strConcatByArray32",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[

        ],
        "name":"invokeID",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_a",
                "type":"string"
            }
        ],
        "name":"stringToUint",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_a",
                "type":"string"
            },
            {
                "name":"_b",
                "type":"uint256"
            }
        ],
        "name":"stringToUint",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_arr",
                "type":"string[7]"
            }
        ],
        "name":"strConcatByArray7",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"source",
                "type":"bytes"
            }
        ],
        "name":"bytesToString",
        "outputs":[
            {
                "name":"result",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_hashKey",
                "type":"string"
            }
        ],
        "name":"incressAsset_undo_unlock",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_lock",
                "type":"bool"
            }
        ],
        "name":"setContractLock",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_hashKey",
                "type":"string"
            }
        ],
        "name":"incressAsset_undo_unlock_contract",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_lockHash",
                "type":"bytes32"
            }
        ],
        "name":"incressAsset_lock_do_contract",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_invokeId",
                "type":"string"
            }
        ],
        "name":"getAsset_atomic",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            },
            {
                "name":"",
                "type":"bool"
            },
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_num",
                "type":"uint256"
            }
        ],
        "name":"setcountNumber",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetId",
                "type":"string"
            },
            {
                "name":"_invokeId",
                "type":"string"
            }
        ],
        "name":"getNumber_atomic",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_arr",
                "type":"string[40]"
            }
        ],
        "name":"strConcatByArray",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[

        ],
        "name":"incressCount",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"b32name",
                "type":"bytes32"
            }
        ],
        "name":"bytes32ToString",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_arr",
                "type":"string[11]"
            }
        ],
        "name":"strConcatByArray11",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            }
        ],
        "name":"getAsset",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            },
            {
                "name":"",
                "type":"bool"
            },
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[

        ],
        "name":"contractLock",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"source",
                "type":"string"
            }
        ],
        "name":"stringToBytes32",
        "outputs":[
            {
                "name":"result",
                "type":"bytes32"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            }
        ],
        "name":"incressAsset_getOldData",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_total",
                "type":"uint256"
            }
        ],
        "name":"createAsset",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_hashKey",
                "type":"string"
            }
        ],
        "name":"incressAsset_unlock",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"source",
                "type":"string"
            }
        ],
        "name":"stringToBytes",
        "outputs":[
            {
                "name":"result",
                "type":"bytes"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[

        ],
        "name":"countNum",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_assetId",
                "type":"string"
            }
        ],
        "name":"getNumber",
        "outputs":[
            {
                "name":"",
                "type":"uint256"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"i",
                "type":"uint256"
            }
        ],
        "name":"uintToString",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_server",
                "type":"address"
            }
        ],
        "name":"setServer",
        "outputs":[

        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"b",
                "type":"bytes"
            }
        ],
        "name":"bytesToInt",
        "outputs":[
            {
                "name":"result",
                "type":"int256"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "constant":false,
        "inputs":[
            {
                "name":"_assetID",
                "type":"string"
            },
            {
                "name":"_num",
                "type":"uint256"
            },
            {
                "name":"_invokeId",
                "type":"string"
            },
            {
                "name":"_hashKey",
                "type":"string"
            }
        ],
        "name":"incressAsset_unlock_contract",
        "outputs":[
            {
                "name":"",
                "type":"bool"
            }
        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[

        ],
        "name":"server",
        "outputs":[
            {
                "name":"",
                "type":"address"
            }
        ],
        "payable":false,
        "stateMutability":"view",
        "type":"function"
    },
    {
        "constant":true,
        "inputs":[
            {
                "name":"_a",
                "type":"string"
            },
            {
                "name":"_b",
                "type":"string"
            }
        ],
        "name":"strConcat",
        "outputs":[
            {
                "name":"",
                "type":"string"
            }
        ],
        "payable":false,
        "stateMutability":"pure",
        "type":"function"
    },
    {
        "inputs":[

        ],
        "payable":false,
        "stateMutability":"nonpayable",
        "type":"constructor"
    },
    {
        "anonymous":false,
        "inputs":[
            {
                "indexed":false,
                "name":"invokeID",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"invokeDetail",
                "type":"string"
            }
        ],
        "name":"crossEvent",
        "type":"event"
    },
    {
        "anonymous":false,
        "inputs":[
            {
                "indexed":false,
                "name":"invokeID",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"callBackDetail",
                "type":"string"
            }
        ],
        "name":"callBackEvent",
        "type":"event"
    },
    {
        "anonymous":false,
        "inputs":[
            {
                "indexed":false,
                "name":"invokeId",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"assetId",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"num",
                "type":"uint256"
            }
        ],
        "name":"GetNumber",
        "type":"event"
    },
    {
        "anonymous":false,
        "inputs":[
            {
                "indexed":false,
                "name":"invokeId",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"assetId",
                "type":"string"
            },
            {
                "indexed":false,
                "name":"total",
                "type":"uint256"
            }
        ],
        "name":"ReadAsset",
        "type":"event"
    }
]
}
# blockchain rpc
w3 = Web3(Web3.HTTPProvider("http://192.168.17.131:8545"))
w3.middleware_onion.inject(geth_poa_middleware, layer=0)
# contract
contractAddr = '0x6c6a303b4B5B92b4227Aa767ea6c9bAf4a09919a'
contractInstance = w3.eth.contract(address=contractAddr, abi=contractABI['abi'])
# database
mydb = mysql.connector.connect(
    host="10.20.36.229",
    port="3306",
    user="chen",
    passwd="chen",
    database="chen"
)
# ----------------- end configurations -------------

# ----------------- General parameters -------------
invokeId="abcdefghijklmn"
assetId="asset001";
num=1;
hashKey="abc";
lockhash=Web3.keccak(text='abc');
#lockhash=keccak256("abc".encode('utf-8'));

# -----------------function services ---------------
def incressAsset_lock_do_contract(contract):
    txn = contract.functions.incressAsset_lock_do_contract(assetId,num,invokeId,lockhash).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def incressAsset_unlock_contract(contract):
    txn = contract.functions.incressAsset_unlock_contract(assetId,num,invokeId,hashKey).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def incressAsset_lock_do(contract):
    txn = contract.functions.incressAsset_lock_do(assetId,num,invokeId,lockhash).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def incressAsset_unlock(contract):
    txn = contract.functions.incressAsset_unlock(assetId,num,invokeId,hashKey).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def setAsset(contract):
    txn = contract.functions.setAsset(assetId,0,False).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def setContractLock(contract):
    txn = contract.functions.setContractLock(False).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def incressCountTx(contract):
    # txn = contract.functions.incressAsset("asset001", 1).buildTransaction(
    txn = contract.functions.incressCount().buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

def setCountNumTx(contract):
    # txn = contract.functions.incressAsset("asset001", 1).buildTransaction(
    txn = contract.functions.setcountNumber(0).buildTransaction(
        {
            'from': w3.eth.coinbase,
            'gasPrice': w3.eth.gasPrice,
            'gas': 3000000
        }
    )
    return txn

# use conbase send transaction
def sendTxn(txn):
    res = w3.eth.sendTransaction(txn).hex()

# Periodically make cross-chain invocations to lock contract
def lockContract(_crossPeriod,_invokePeriod,finished):
    # parameters
    crossPeriod = _crossPeriod  # The duration of a cross-chain transaction
    invokePeriod=_invokePeriod # The period in which cross-chain transactions are invoked
    # global finished
    
    print("init data.....")
    tx = setContractLock(contractInstance)
    sendTxn(tx)
    time.sleep(3)
    print("finish init data.....")
    # keep until the main thread is finished
    while(len(finished)==0):
        start = datetime.datetime.now()
        print("-------------------------- lock_do ---------------------------------")
        print("startTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        data = contractInstance.functions.contractLock().call()
        print("before lock: the contractLock is: ", data)
        tx = incressAsset_lock_do_contract(contractInstance)
        sendTxn(tx)
        end = datetime.datetime.now()
        print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        data = contractInstance.functions.contractLock().call()
        # keep lock the contract in crossPeriod time
        while ((end - start).seconds < crossPeriod):
            time.sleep(1)
            if(data==False):
                data = contractInstance.functions.contractLock().call()
                print("after lock: the contractLock is: ", data)
                print("finising lock transaction time has used:", (end - start).seconds," seconds")
            end = datetime.datetime.now()
            print("nowTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        print("-------------------------- unlock ---------------------------------")
        startUnlock = datetime.datetime.now()
        print("do unlock: the asset001  ")
        tx = incressAsset_unlock_contract(contractInstance)
        sendTxn(tx)
        end = datetime.datetime.now()
        print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        print("send unlock transaction time used:", (end - start).seconds)
        data = contractInstance.functions.contractLock().call()
        # wait until invokePeriod time has passed
        while((end - start).seconds<invokePeriod):
            time.sleep(1)
            if (data==True):
                data = contractInstance.functions.contractLock().call()
                print("after unlock: the contractLock is: ", data)
                print("finising unlock transaction time has used:", (end - startUnlock).seconds," seconds")
            end = datetime.datetime.now()
            print("nowTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))

    print("main thret has finished, quit!")

# Periodically make cross-chain invocations to lock resources
def lockResource(_crossPeriod,_invokePeriod,finished):
    # parameters
    crossPeriod = _crossPeriod  # The duration of a cross-chain transaction
    invokePeriod=_invokePeriod # The period in which cross-chain transactions are invoked
    # global finished
    
    print("init data.....")
    tx = setContractLock(contractInstance)
    sendTxn(tx)
    time.sleep(3)
    print("finish init data.....")
    # keep until the main thread is finished
    while(len(finished)==0):
        start = datetime.datetime.now()
        print("-------------------------- lock_do ---------------------------------")
        print("startTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        data = contractInstance.functions.getAsset(assetId).call()
        print("before lock: the asset is: ", data)
        tx = incressAsset_lock_do(contractInstance)
        sendTxn(tx)
        end = datetime.datetime.now()
        print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        data = contractInstance.functions.getAsset(assetId).call()
        # keep lock the contract in crossPeriod time
        while ((end - start).seconds < crossPeriod):
            time.sleep(1)
            if(data[1]==False):
                data = contractInstance.functions.getAsset(assetId).call()
                print("after lock: the asset is: ", data)
                print("finising lock transaction time has used:", (end - start).seconds," seconds")
            end = datetime.datetime.now()
            print("nowTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        print("-------------------------- unlock ---------------------------------")
        startUnlock = datetime.datetime.now()
        print("do unlock: the asset001  ")
        tx = incressAsset_unlock(contractInstance)
        sendTxn(tx)
        end = datetime.datetime.now()
        print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
        print("send unlock transaction time used:", (end - start).seconds)
        data = contractInstance.functions.getAsset(assetId).call()
        # wait until invokePeriod time has passed
        while((end - start).seconds<invokePeriod):
            time.sleep(1)
            if (data[1]==True):
                data = contractInstance.functions.getAsset(assetId).call()
                print("after unlock: the asset is: ", data)
                print("finising unlock transaction time has used:", (end - startUnlock).seconds," seconds")
            end = datetime.datetime.now()
            print("nowTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))

    print("main thret has finished, quit!")


def testLockContract(partitions,TransactionCount,crossPeriod,invokePeriod):
    print(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  new test %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
    # partitions: total rounds
    # TransactionCount: The number of transactions sent in each round
    # crossPeriod: The duration of a cross-chain transaction
    # invokePeriod: The period in which cross-chain transactions are invoked
    print("test parameters: partitions-", partitions, ", TransactionCount-", TransactionCount, ", crossPeriod-",
          crossPeriod, ", invokePeriod-", invokePeriod)

    # initialization  The variable countNum records the number of successful transactions
    print("init countNum to 0 ....")
    sendTxn(setCountNumTx(contractInstance))
    time.sleep(3)
    data = contractInstance.functions.countNum().call()
    print("befor test: the countNum is: ", data)

    # Open a thread to lock the contract periodically
    manager = Manager()
    finished = manager.list()
    p = multiprocessing.Process(target=lockContract,
                                args=(crossPeriod, invokePeriod, finished)
                                , )
    p.start()

    print("startTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
    start = datetime.datetime.now()
    for l in range(partitions):
        print("start round: ", l)
        startRound = datetime.datetime.now()
        for i in range(TransactionCount):
            tx = incressCountTx(contractInstance)
            sendTxn(tx)
        endRound = datetime.datetime.now()
        # each round will be done in 1 second
        while ((endRound - startRound).seconds < 1):
            endRound = datetime.datetime.now()
            # print("endRound:",endRound)
            # print("startRound:",startRound)
            # print("round:",l,", ",(endRound - startRound).microseconds)

    end = datetime.datetime.now()
    print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
    print("the time used:", (end - start).seconds)
    # wait until all the transactions are finished
    time.sleep(5)
    data = contractInstance.functions.countNum().call()
    print("after: the countNum is: ", data)
    # Notifies the child thread of completion
    finished.append(1);
    p.join()
    # Record the experimental data
    startstr = start.strftime("%Y-%m-%d %H:%M:%S")
    endstr = end.strftime("%Y-%m-%d %H:%M:%S")
    mycursor = mydb.cursor()
    sql = "INSERT INTO locktest(testType,partitions,transactionCount, crossPeriod, invokePeriod, tranNumAll, tranNumSeccuss,startTime,endTime) \
              VALUES (%s, %s, %s, %s, %s,%s,%s,%s,%s)"
    val = ("Contract",partitions, TransactionCount, crossPeriod, invokePeriod, partitions * TransactionCount, data, startstr, endstr)
    try:
        # execute sql
        mycursor.execute(sql, val)
        # commit
        mydb.commit()
    except:
        mydb.rollback()
    # close
    mycursor.close()



def testLockResource(partitions,TransactionCount,crossPeriod,invokePeriod):
    print(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  new test %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
    # partitions: total rounds
    # TransactionCount: The number of transactions sent in each round
    # crossPeriod: The duration of a cross-chain transaction
    # invokePeriod: The period in which cross-chain transactions are invoked
    print("test parameters: partitions-", partitions, ", TransactionCount-", TransactionCount, ", crossPeriod-",
          crossPeriod, ", invokePeriod-", invokePeriod)

    # Initialization
    print("init countNum to 0 ....")
    sendTxn(setCountNumTx(contractInstance))
    time.sleep(3)
    data = contractInstance.functions.countNum().call()
    print("befor: the countNum is: ", data)

    # Open a thread to lock the contract periodically
    manager = Manager()
    finished = manager.list()
    p = multiprocessing.Process(target=lockResource,
                                args=(crossPeriod, invokePeriod, finished)
                                , )
    p.start()

    processCount = 2
    print("startTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
    start = datetime.datetime.now()
    for l in range(partitions):
        print("start round: ", l)
        # processList = []
        startRound = datetime.datetime.now()
        # ---------multi-thread
        # for i in range(processCount):
        #
        #     p = multiprocessing.Process(target=sendincressCountTxs,
        #                                 args=(i * int(TransactionCount // processCount),
        #                                       (i + 1) * int(TransactionCount // processCount))
        #                                 , )
        #     processList.append(p)
        #     p.start()
        #
        # for p in processList:
        #     p.join()
        # ---------single-thread
        for i in range(TransactionCount):
            tx = incressCountTx(contractInstance)
            sendTxn(tx)
        endRound = datetime.datetime.now()
        # each round will be done in 1 second
        while ((endRound - startRound).seconds < 1):
            endRound = datetime.datetime.now()
            # print("endRound:",endRound)
            # print("startRound:",startRound)
            # print("round:",l,", ",(endRound - startRound).microseconds)

    end = datetime.datetime.now()
    print("endTime: ", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())))
    print("the time used:", (end - start).seconds)
    # wait until all the transactions are finished
    time.sleep(5)
    data = contractInstance.functions.countNum().call()
    print("after: the countNum is: ", data)
    # Notifies the child thread of completion
    finished.append(1);
    p.join()
    # Record the experimental data
    startstr = start.strftime("%Y-%m-%d %H:%M:%S")
    endstr = end.strftime("%Y-%m-%d %H:%M:%S")

    mycursor = mydb.cursor()
    sql = "INSERT INTO locktest(testType,partitions,transactionCount, crossPeriod, invokePeriod, tranNumAll, tranNumSeccuss,startTime,endTime) \
              VALUES (%s, %s, %s, %s, %s,%s,%s,%s,%s)"
    val = ("Resource",partitions, TransactionCount, crossPeriod, invokePeriod, partitions * TransactionCount, data, startstr, endstr)
    try:
        mycursor.execute(sql, val)
        mydb.commit()
    except:
        mydb.rollback()
    mycursor.close()

def main():
    testnum = 1
    # # -----------
    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 30)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 30)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 40)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 40)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 50)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 50)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 60)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 60)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 70)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 70)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 80)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 80)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 90)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 90)
    testnum += 1

    print("*************************************** test: ", testnum, " *************************")
    testLockContract(200, 10, 30, 100)
    testnum += 1
    print("*************************************** test: ", testnum, " *************************")
    testLockResource(200, 10, 30, 100)
    testnum += 1

    mydb.close()
    
if __name__ == '__main__':
    main()

