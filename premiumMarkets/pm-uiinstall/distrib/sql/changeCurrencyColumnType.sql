--alter table CURRENCYRATE alter column RATE set data type DECIMAL(20,2);
ALTER TABLE PORTFOLIO ADD COLUMN AVGBUYPRICEb DECIMAL(20,4) DEFAULT 0;
UPDATE PORTFOLIO SET AVGBUYPRICEb=AVGBUYPRICE;
ALTER TABLE PORTFOLIO DROP COLUMN AVGBUYPRICE;
RENAME COLUMN PORTFOLIO.AVGBUYPRICEb TO AVGBUYPRICE;
--ALTER TABLE PORTFOLIO CHANGE AVGBUYPRICEb AVGBUYPRICE DECIMAL(20,4) DEFAULT 0;

ALTER TABLE PORTFOLIO ADD COLUMN CASHINb DECIMAL(20,4) DEFAULT 0;
UPDATE PORTFOLIO SET CASHINb=CASHIN;
ALTER TABLE PORTFOLIO DROP COLUMN CASHIN;
RENAME COLUMN PORTFOLIO.CASHINb TO CASHIN;
--ALTER TABLE PORTFOLIO CHANGE CASHINb CASHIN DECIMAL(20,4) DEFAULT 0;

ALTER TABLE PORTFOLIO ADD COLUMN CASHOUTb DECIMAL(20,4) DEFAULT 0;
UPDATE PORTFOLIO SET CASHOUTb=CASHOUT;
ALTER TABLE PORTFOLIO DROP COLUMN CASHOUT;
RENAME COLUMN PORTFOLIO.CASHOUTb TO CASHOUT;
--ALTER TABLE PORTFOLIO CHANGE CASHOUTb CASHOUT DECIMAL(20,4) DEFAULT 0;

ALTER TABLE PORTFOLIO_NAME ADD COLUMN TOTALINAMOUNTEVERb DECIMAL(20,4) DEFAULT 0;
UPDATE PORTFOLIO_NAME SET TOTALINAMOUNTEVERb=TOTALINAMOUNTEVER;
ALTER TABLE PORTFOLIO_NAME DROP COLUMN TOTALINAMOUNTEVER;
RENAME COLUMN PORTFOLIO_NAME.TOTALINAMOUNTEVERb TO TOTALINAMOUNTEVER;
--ALTER TABLE PORTFOLIO_NAME CHANGE TOTALINAMOUNTEVERb TOTALINAMOUNTEVER DECIMAL(20,4) DEFAULT 0;

ALTER TABLE PORTFOLIO_NAME ADD COLUMN TOTALOUTAMOUNTEVERb DECIMAL(20,4) DEFAULT 0;
UPDATE PORTFOLIO_NAME SET TOTALOUTAMOUNTEVERb=TOTALOUTAMOUNTEVER;
ALTER TABLE PORTFOLIO_NAME DROP COLUMN TOTALOUTAMOUNTEVER;
RENAME COLUMN PORTFOLIO_NAME.TOTALOUTAMOUNTEVERb TO TOTALOUTAMOUNTEVER;
--ALTER TABLE PORTFOLIO_NAME CHANGE TOTALOUTAMOUNTEVERb TOTALOUTAMOUNTEVER DECIMAL(20,4) DEFAULT 0;

ALTER TABLE CURRENCYRATE ADD COLUMN RATEb DECIMAL(20,8) DEFAULT 1;
UPDATE CURRENCYRATE SET RATEb=RATE;
ALTER TABLE CURRENCYRATE DROP COLUMN RATE;
RENAME COLUMN CURRENCYRATE.RATEb TO RATE;
--ALTER TABLE CURRENCYRATE CHANGE RATEb RATE DECIMAL(20,8) DEFAULT 1;

