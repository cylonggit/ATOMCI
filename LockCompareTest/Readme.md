# Contract Availability under Different Locks Test Project
1. This project is based on Python version 3.8.
2. Some package needs to be installed before running this project.
```python
pip install Web3
pip install py-solc
pip install mysql-connector
```

## Configuration
Before you can start running tests, you need to complete the configuration first.


1. All the configuration is at the front of the code in file lockTest.py.

2. database initialization

We use MySQL database to record the experiment results. The database is initialized as follows:
```sql
CREATE DATABASE /*!32312 IF NOT EXISTS*/`chen` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `chen`;
DROP TABLE IF EXISTS `locktest`;
CREATE TABLE `locktest` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID-ID',
  `testType` VARCHAR(50),   
  `partitions` INT(11) DEFAULT 0 ,
  `transactionCount` INT(11) DEFAULT 0 ,
  `crossPeriod` INT(11) DEFAULT 0 ,
  `invokePeriod` INT(11) DEFAULT 0 ,
  `tranNumAll` INT(11) DEFAULT 0 ,
  `tranNumSeccuss` INT(11) DEFAULT 0 ,
  `startTime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'startTime',
  `endTime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'endTime',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='lockTestList';
```

## Experiment and Results

1. Running the lockTest.py can run the lock-compare experiment in the paper.

2. Use the following SQL code to get the results. Due to network, hardware and other reasons, they may be slightly different from the results in the paper.

```sql
-- Get the results of locking the contract
SELECT invokePeriod, tranNumSeccuss FROM locktest WHERE testType='Contract' ORDER BY invokePeriod;

-- Get the results of locking the resource
SELECT invokePeriod, tranNumSeccuss FROM locktest WHERE testType='Resource' ORDER BY invokePeriod;
```
