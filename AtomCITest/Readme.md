# Latency and Gas Consumption Experiments and Cross-chian NFT Test Project

1. The application is based on Fabric version V2.2.0 and Ethereum version 1.9.x
2. The application is based on a custom Fabric SDK: Fabric -gateway-java-chen. Before running the application, please first package the SDK (in the dependent tools folder in the upper directory) into your local Maven repository.


## Configuration
1. The Ethereum business chain configuration file is Ethbusiness.properties. The basechain configuration file is BaseChian.properties. 

2. Configuration for Fabric: (1) Member MSP is specified in folder crypto-config and the network is specified in connection.json. (2) the chain-code name is specified in Fabric.Properties (if test the NFT example, the chain-code name is specified in FabricNFT.properties).

3. We use MySQL database to record the experiment results, please specify the database configuration in dbconfig.properties.
   The database is initialized as follows:
```sql
   CREATE DATABASE /*!32312 IF NOT EXISTS*/`chen` /*!40100 DEFAULT CHARACTER SET utf8 */;
   USE `chen`;
   DROP TABLE IF EXISTS `lablog`;
   CREATE TABLE `lablog` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `testId` DATETIME, #The identifier of an experiment
   `labType` VARCHAR(50) NOT NULL COMMENT 'type',   
   `tran` VARCHAR(100) DEFAULT NULL COMMENT 'transactionId',
   `startTime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'start time',
   `endTime` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'end time',
   `gas` INT(11)  DEFAULT 0 COMMENT 'gas',
   `blockNum` INT(11)  DEFAULT 0 COMMENT 'block number',
   `remark` VARCHAR(255)  DEFAULT NULL COMMENT 'remark',
   PRIMARY KEY (`id`)
   ) ENGINE=INNODB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='resultList';

```
## Run Experiments and NFT Example

1. Running the TestDAPPs.java can run the cross-chain delay and gas consumption experiments in the paper.

2. Running the TestNFTCrossChain.java can run the cross-chain NFT dApp example (transfer the NFT from one blockchain to another).

3. Use the following SQL code to get the experiment results. Due to network, hardware and other reasons, they may be slightly different from the results in the paper.

```sql
-- ----------------------- DApp 1: atomic-ReadEth_write_2
-- total test rounds
SELECT COUNT(DISTINCT testId) FROM lablog a WHERE labType='atomic-ReadEth_write_2';
-- sub-invocation time
SELECT a.`remark`, AVG((UNIX_TIMESTAMP(endTime) - UNIX_TIMESTAMP(startTime))) timeuse FROM lablog a WHERE labType='atomic-ReadEth_write_2' GROUP BY remark;
-- total time
SELECT AVG(b.totalTime) FROM (SELECT (MAX(UNIX_TIMESTAMP(endTime))-MIN(UNIX_TIMESTAMP(startTime))) totalTime FROM lablog a WHERE labType='atomic-ReadEth_write_2' GROUP BY a.testId) b;
-- sub-invocation gas
SELECT a.`remark`, AVG(gas) gas FROM lablog a WHERE labType='atomic-ReadEth_write_2' GROUP BY remark;

-- ----------------------- DApp 2:  atomic-ReadFabric_write_2
-- total test rounds
SELECT COUNT(DISTINCT testId) FROM lablog a WHERE labType='atomic-ReadFabric_write_2';
-- sub-invocation time
SELECT a.`remark`, AVG((UNIX_TIMESTAMP(endTime) - UNIX_TIMESTAMP(startTime))) timeuse FROM lablog a WHERE labType='atomic-ReadFabric_write_2' GROUP BY remark;
-- total time
SELECT AVG(b.totalTime) FROM (SELECT (MAX(UNIX_TIMESTAMP(endTime))-MIN(UNIX_TIMESTAMP(startTime))) totalTime FROM lablog a WHERE labType='atomic-ReadFabric_write_2' GROUP BY a.testId) b;
-- sub-invocation gas
SELECT a.`remark`, AVG(gas) gas FROM lablog a WHERE labType='atomic-ReadFabric_write_2' GROUP BY remark;

-- ----------------------- DApp 3:  atomic-write_2
-- total test rounds
SELECT COUNT(DISTINCT testId) FROM lablog a WHERE labType='atomic-write_2';
-- sub-invocation time
SELECT a.`remark`, AVG((UNIX_TIMESTAMP(endTime) - UNIX_TIMESTAMP(startTime))) timeuse FROM lablog a WHERE labType='atomic-write_2' GROUP BY remark;
-- total time
SELECT AVG(b.totalTime) FROM (SELECT (MAX(UNIX_TIMESTAMP(endTime))-MIN(UNIX_TIMESTAMP(startTime))) totalTime FROM lablog a WHERE labType='atomic-write_2' GROUP BY a.testId) b;
-- sub-invocation gas
SELECT a.`remark`, AVG(gas) gas FROM lablog a WHERE labType='atomic-write_2' GROUP BY remark;

```






