--install replace.class
CALL SQLJ.install_jar('#INSTALLDIR#lib/ReplaceBin.jar','APP.ReplaceBin',0);
CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP.ReplaceBin');
create function Replace( SourceStringExpression varchar(50),  SearchStringExpression varchar(50),  ReplaceStringExpression varchar(50)) returns varchar(50) parameter style java no sql language java external name 'Replace.replace';


--update
ALTER TABLE "APP"."PORTFOLIO" DROP CONSTRAINT "FK_PORTFOLIO_TO_PORTFOLIO_NAME";
--update PORTFOLIO_NAME set name = replace (name, ':NSE', ':NSEINDIA') where name like 'NSE%';
update PORTFOLIO_NAME set name = replace (name, 'NSE', 'NSEINDIA') where name like 'NSE%';
--update PORTFOLIO set name = replace (name, ':NSE', ':NSEINDIA') where name like 'NSE%';
update PORTFOLIO set name = replace (name, 'NSE', 'NSEINDIA') where name like 'NSE%';
ALTER TABLE "APP"."PORTFOLIO" ADD CONSTRAINT "FK_PORTFOLIO_TO_PORTFOLIO_NAME" FOREIGN KEY ("NAME") REFERENCES "APP"."PORTFOLIO_NAME" ("NAME") ON DELETE NO ACTION ON UPDATE NO ACTION;
update SHARES set MARKETLISTPROVIDER = replace (MARKETLISTPROVIDER, 'NSE', 'NSEINDIA') where MARKETLISTPROVIDER like 'NSE%';


--| PORTFOLIO | CREATE TABLE `PORTFOLIO` (
--  `SYMBOL` varchar(20) NOT NULL,
--  `ISIN` varchar(20) NOT NULL,
--  `NAME` varchar(255) NOT NULL,
--  `QUANTITY` decimal(19,5) DEFAULT NULL,
--  `MONITOR` smallint(6) NOT NULL DEFAULT '0',
--  `BUYDATE` date DEFAULT '1970-01-01',
--  `TRANSACTIONCURRENCY` char(3) NOT NULL DEFAULT 'EUR',
--  `EXTERNALACCOUNT` varchar(255) DEFAULT NULL,
--  `AVGBUYPRICE` decimal(20,4) DEFAULT '0.0000',
--  `CASHIN` decimal(20,4) DEFAULT '0.0000',
--  `CASHOUT` decimal(20,4) DEFAULT '0.0000',
--  PRIMARY KEY (`SYMBOL`,`ISIN`,`NAME`),
--  KEY `FK_PORTFOLIO_TO_PORTFOLIO_NAME` (`NAME`),
--  KEY `FK_STOCK` (`SYMBOL`,`ISIN`),
--  CONSTRAINT `FK_PORTFOLIO_TO_PORTFOLIO_NAME` FOREIGN KEY (`NAME`) REFERENCES `PORTFOLIO_NAME` (`NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION,
--  CONSTRAINT `FK_PORTFOLIO_TO_SHARES` FOREIGN KEY (`SYMBOL`, `ISIN`) REFERENCES `SHARES` (`SYMBOL`, `ISIN`) ON DELETE NO ACTION ON UPDATE NO ACTION
--) ENGINE=InnoDB DEFAULT CHARSET=utf8 |

--| PERF_SUPPLEMENT | CREATE TABLE `PERF_SUPPLEMENT` (
--  `SYMBOL` varchar(20) NOT NULL,
--  `ISIN` varchar(20) NOT NULL,
--  `NAME` varchar(255) NOT NULL,
--  `PERFDATE` date NOT NULL,
--  `LATEST` smallint(6) NOT NULL DEFAULT '1',
--  `TRENDFORECAST` varchar(32) DEFAULT NULL,
--  `ISREVERSAL` smallint(6) NOT NULL DEFAULT '0',
--  `YEARLYPERF` decimal(10,4) DEFAULT NULL,
--  `MONTHLYPERF` decimal(10,4) DEFAULT NULL,
--  `WEEKLYPERF` decimal(10,4) DEFAULT NULL,
--  `DAILYPERF` decimal(10,4) DEFAULT NULL,
--  `EVENTDEFINITION` varchar(100) NOT NULL,
--  PRIMARY KEY (`SYMBOL`,`ISIN`,`NAME`,`PERFDATE`,`EVENTDEFINITION`),
--  KEY `FK_PERF_SUPPLEMENT_TO_PORTFOLIO` (`SYMBOL`,`ISIN`,`NAME`),
--  KEY `PERF_SUPPLEMENT_LATEST` (`LATEST`),
--  KEY `PERF_SUPPLEMENT_KEY` (`SYMBOL`,`ISIN`,`NAME`,`EVENTDEFINITION`),
--  CONSTRAINT `FK_PERF_SUPPLEMENT_TO_PORTFOLIO` FOREIGN KEY (`SYMBOL`, `ISIN`, `NAME`) REFERENCES `PORTFOLIO` (`SYMBOL`, `ISIN`, `NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION
--) ENGINE=InnoDB DEFAULT CHARSET=utf8 |

--| USERS | CREATE TABLE `USERS` (
--  `EMAIL` varchar(100) NOT NULL,
--  `SYMBOL` varchar(20) NOT NULL,
--  `ISIN` varchar(20) NOT NULL,
--  `NAME` varchar(255) NOT NULL,
--  `COMMENT` mediumtext,
--  `REGISTRATIONDATE` date NOT NULL,
--  `REGISTRATIONFORECAST` varchar(32) DEFAULT NULL,
--  `ISACTIVE` smallint(6) NOT NULL DEFAULT '1',
--  `EVENTDEFINITION` varchar(100) NOT NULL DEFAULT '',
--  `STOCKPARAMS` varchar(255) NOT NULL DEFAULT '',
--  PRIMARY KEY (`EMAIL`,`SYMBOL`,`ISIN`,`NAME`,`EVENTDEFINITION`,`STOCKPARAMS`),
--  KEY `FK_USER_TO_PORTFOLIO` (`SYMBOL`,`ISIN`,`NAME`),
--  CONSTRAINT `FK_USER_TO_PORTFOLIO` FOREIGN KEY (`SYMBOL`, `ISIN`, `NAME`) REFERENCES `PORTFOLIO` (`SYMBOL`, `ISIN`, `NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION
--) ENGINE=InnoDB DEFAULT CHARSET=utf8 |


