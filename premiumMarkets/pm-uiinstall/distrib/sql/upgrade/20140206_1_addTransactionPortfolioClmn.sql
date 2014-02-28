--CREATE TABLE "APP"."TRANSACTIONS" (
--"ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1), 
--"SYMBOL" VARCHAR(20) NOT NULL, "ISIN" VARCHAR(20) NOT NULL, 
--"ACCOUNT" VARCHAR(256), "DATE" DATE NOT NULL, "QUANTITY" NUMERIC(19,5), "PRICE" NUMERIC(19,2), "CURRENCY" CHAR(3) NOT NULL DEFAULT 'EUR');

--CREATE TABLE "APP"."PORTFOLIO" (
--"SYMBOL" VARCHAR(20) NOT NULL, "ISIN" VARCHAR(20) NOT NULL, "QUANTITY" NUMERIC(19,5), "CASHIN" DECIMAL(20,4), "CASHOUT" DECIMAL(20,4), 
--"NAME" VARCHAR(255) NOT NULL, "MONITOR" SMALLINT NOT NULL DEFAULT 0, "BUYDATE" DATE DEFAULT '01/01/1970', "AVGBUYPRICE" DECIMAL(20,4) DEFAULT 0, "TRANSACTIONCURRENCY" CHAR(3) NOT NULL DEFAULT 'EUR', "ACCOUNT" VARCHAR(255));

--CREATE TABLE "APP"."PORTFOLIO_NAME" ("NAME" VARCHAR(255) NOT NULL, "TYPE" VARCHAR(32) DEFAULT 'default', 
--"TOTALINAMOUNTEVER" DECIMAL(20,4) DEFAULT 0, "TOTALOUTAMOUNTEVER" DECIMAL(20,4) DEFAULT 0, "BUYPONDERATIONRULE" BLOB(2147483647), "SELLPONDERATIONRULE" BLOB(2147483647), "PORTFOLIOCURRENCY" CHAR(3));



--Alter transaction
ALTER TABLE TRANSACTIONS ADD COLUMN PRICEb DECIMAL(20,4);
UPDATE TRANSACTIONS SET PRICEb=PRICE;
ALTER TABLE TRANSACTIONS DROP COLUMN PRICE;
RENAME COLUMN TRANSACTIONS.PRICEb TO PRICE;

RENAME COLUMN TRANSACTIONS.ACCOUNT TO EXTERNALACCOUNT;
ALTER TABLE TRANSACTIONS ADD COLUMN PORTFOLIO VARCHAR(255);

--ALTER TABLE TRANSACTIONS ALTER COLUMN EXTERNALACCOUNT SET DATA TYPE VARCHAR(255);
ALTER TABLE TRANSACTIONS ADD COLUMN EXTERNALACCOUNTb VARCHAR(255);
UPDATE TRANSACTIONS SET EXTERNALACCOUNTb=EXTERNALACCOUNT;
ALTER TABLE TRANSACTIONS DROP COLUMN EXTERNALACCOUNT;
RENAME COLUMN TRANSACTIONS.EXTERNALACCOUNTb TO EXTERNALACCOUNT;

--mv portfolios into transactions
RENAME COLUMN PORTFOLIO.ACCOUNT TO EXTERNALACCOUNT;
UPDATE PORTFOLIO SET PORTFOLIO.AVGBUYPRICE = (PORTFOLIO.CASHIN / PORTFOLIO.QUANTITY ) WHERE PORTFOLIO.AVGBUYPRICE = 0;
INSERT INTO TRANSACTIONS (SYMBOL, ISIN, DATE, QUANTITY, CURRENCY, PRICE, PORTFOLIO, EXTERNALACCOUNT) SELECT PORTFOLIO.SYMBOL, PORTFOLIO.ISIN, PORTFOLIO.BUYDATE, PORTFOLIO.QUANTITY, PORTFOLIO.TRANSACTIONCURRENCY, PORTFOLIO.AVGBUYPRICE, PORTFOLIO.NAME, PORTFOLIO.EXTERNALACCOUNT FROM PORTFOLIO join PORTFOLIO_NAME on PORTFOLIO.NAME = PORTFOLIO_NAME.NAME where PORTFOLIO_NAME.TYPE in ('AutoPortfolio','TuningAutoPortfolio','default') and PORTFOLIO.EXTERNALACCOUNT is NULL;
	
ALTER TABLE "APP"."TRANSACTIONS" ADD CONSTRAINT "FK_TRANSACTION_TO_PORTFOLIO_NAME" FOREIGN KEY ("PORTFOLIO") REFERENCES "APP"."PORTFOLIO_NAME" ("NAME") ON DELETE NO ACTION ON UPDATE NO ACTION;

--CREATE TABLE "APP"."TRANSACTIONS" ("ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1), "SYMBOL" VARCHAR(20) NOT NULL, "ISIN" VARCHAR(20) NOT NULL, "ACCOUNT" VARCHAR(255), "DATE" DATE NOT NULL, "QUANTITY" NUMERIC(19,5), "PRICE" DECIMAL(20,4), "CURRENCY" CHAR(3) NOT NULL DEFAULT 'EUR');

ALTER TABLE TRANSACTIONS ADD COLUMN IDb BIGINT NOT NULL DEFAULT 0;
UPDATE TRANSACTIONS SET IDb=ID;
ALTER TABLE TRANSACTIONS DROP COLUMN ID;
RENAME COLUMN TRANSACTIONS.IDb TO ID;

ALTER TABLE TRANSACTIONS ADD CONSTRAINT TRANSACTIONS_PKEY PRIMARY KEY ("ID");

