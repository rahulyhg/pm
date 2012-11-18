-- ============================

-- This file was created using Derby's dblook utility.
-- Timestamp: 2012-11-11 16:17:57.718
-- Source database is: /home/guil/Developpement/newEclipse/premiumMarkets/pm-gwt/src/main/webapp/premiummarkets
-- Connection URL is: jdbc:derby:/home/guil/Developpement/newEclipse/premiumMarkets/pm-gwt/src/main/webapp/premiummarkets
-- appendLogs: false

-- ----------------------------------------------
-- DDL Statements for tables
-- ----------------------------------------------

CREATE TABLE QUOTATIONS (DATE DATE NOT NULL, SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, CURRENCY CHAR(3), VOLUME BIGINT DEFAULT 0, OPENVALUE DECIMAL(20,4), CLOSEVALUE DECIMAL(20,4), HIGH DECIMAL(20,4), LOW DECIMAL(20,4)) CHARACTER SET utf8;

CREATE TABLE ALERTS (SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, NAME VARCHAR(255) NOT NULL, ALERTTYPE VARCHAR(255) NOT NULL, THRESHOLDTYPE VARCHAR(255) NOT NULL, VALUE NUMERIC(19,2) NOT NULL, OPTIONALMESSAGE LONG VARCHAR) CHARACTER SET utf8;

CREATE TABLE PORTFOLIO_NAME (NAME VARCHAR(255) NOT NULL, TYPE VARCHAR(32) DEFAULT 'default', TOTALINAMOUNTEVER NUMERIC(19,2) DEFAULT 0, TOTALOUTAMOUNTEVER NUMERIC(19,2) DEFAULT 0, BUYPONDERATIONRULE BLOB(2147483647), SELLPONDERATIONRULE BLOB(2147483647), PORTFOLIOCURRENCY CHAR(3)) CHARACTER SET utf8;

CREATE TABLE USERS (EMAIL VARCHAR(100) NOT NULL, SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, COMMENT VARCHAR(30000))  CHARACTER SET utf8;

CREATE TABLE WEATHER (DATE DATE NOT NULL, MAXTEMP SMALLINT, AVGTEMP SMALLINT, MINTEMP SMALLINT, PRECIPITATION NUMERIC(19,5), WIND SMALLINT) CHARACTER SET utf8;

CREATE TABLE PORTFOLIO (SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, QUANTITY NUMERIC(19,5), CASHIN NUMERIC(19,2), CASHOUT NUMERIC(19,2), NAME VARCHAR(255) NOT NULL, MONITOR SMALLINT NOT NULL DEFAULT 0, BUYDATE DATE DEFAULT '1970-01-01', AVGBUYPRICE DECIMAL(9,2) DEFAULT 0, TRANSACTIONCURRENCY CHAR(3) NOT NULL DEFAULT 'EUR', ACCOUNT VARCHAR(255)) CHARACTER SET utf8;

CREATE TABLE TREND_SUPPLEMENT (SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, BOURSOMEANRECOMMENDATIONS NUMERIC(19,2), BOURSOTARGETPRICE NUMERIC(19,2), YAHOOMEANRECOMMENDATIONS NUMERIC(19,2), YAHOOTARGETPRICE NUMERIC(19,2), DIVIDEND DECIMAL(10,2), REUTERSYIELD DECIMAL(10,2), YAHOOEPS NUMERIC(19,2), YAHOOESTEPS NUMERIC(19,2), REUTERSEPS NUMERIC(19,2), REUTERSESTEPS NUMERIC(19,2), REUTERSPAYOUTRATIO NUMERIC(19,2), BOURSOBNA NUMERIC(19,2), BOURSOESTBNA NUMERIC(19,2), TRENDDATE DATE NOT NULL DEFAULT '1970-01-01') CHARACTER SET utf8;

CREATE TABLE CURRENCYRATE (FROMCURRENCY INTEGER NOT NULL, TOCURRENCY INTEGER NOT NULL, DATE DATE NOT NULL, RATE DECIMAL(20,4) DEFAULT 1) CHARACTER SET utf8;

CREATE TABLE TRANSACTIONS (ID INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1), SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, ACCOUNT VARCHAR(256), DATE DATE NOT NULL, QUANTITY NUMERIC(19,5), PRICE NUMERIC(19,2), CURRENCY CHAR(3) NOT NULL DEFAULT 'EUR') CHARACTER SET utf8;

CREATE TABLE SHARES (SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, NAME VARCHAR(100) NOT NULL, LASTQUOTE DATE NOT NULL, REMOVABLE SMALLINT NOT NULL DEFAULT 0, QUOTATIONPROVIDER VARCHAR(32), MARKETLISTPROVIDER VARCHAR(32), CATEGORY VARCHAR(255), SECTOR_HINT VARCHAR(64), TRADING_MODE VARCHAR(64) NOT NULL DEFAULT 'UNKNOWN', CAPITALISATION BIGINT NOT NULL DEFAULT 0) CHARACTER SET utf8;

CREATE TABLE TUNEDCONF (SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, CONFIGFILE VARCHAR(50) NOT NULL, CONFIGSTR VARCHAR(255) NOT NULL, LASTTUNING DATE NOT NULL, LASTCALCULATION DATE NOT NULL, STALED SMALLINT NOT NULL DEFAULT 0) CHARACTER SET utf8;

CREATE TABLE EVENTS (DATE TIMESTAMP NOT NULL, SYMBOL VARCHAR(20) NOT NULL, ISIN VARCHAR(20) NOT NULL, EVENTDEF CHAR(100), EVENTTYPE CHAR(1) NOT NULL, ACCURACY SMALLINT, EVENTDEFID SMALLINT NOT NULL, EVENTDEFEXTENSION VARCHAR(100) NOT NULL DEFAULT '', ANALYSENAME VARCHAR(256) NOT NULL DEFAULT 'AutoPortfolio', MESSAGE LONG VARCHAR) CHARACTER SET utf8;
ALTER TABLE EVENTS MODIFY DATE TIMESTAMP NOT NULL DEFAULT 0;
ALTER TABLE EVENTS ALTER COLUMN DATE DROP DEFAULT;

-- ----------------------------------------------
-- DDL Statements for indexes
-- ----------------------------------------------

CREATE INDEX SYMBOL_ISIN_INDEX ON QUOTATIONS (SYMBOL, ISIN);

CREATE INDEX EVENTS_ANAME ON EVENTS (ANALYSENAME);
CREATE INDEX EVENTS_ANAME_DATE ON EVENTS (ANALYSENAME, DATE);
CREATE INDEX EVENTS_ANAME_DATE_ID ON EVENTS (ANALYSENAME, DATE, EVENTDEFID);
CREATE INDEX EVENTS_STOCK_ANAME_TYPE_ID ON EVENTS (SYMBOL, ISIN, ANALYSENAME, EVENTTYPE, EVENTDEFID);
CREATE INDEX EVENTS_STOCK_ANAME_DATE_TYPE ON EVENTS (SYMBOL, ISIN, ANALYSENAME, DATE, EVENTTYPE);
CREATE INDEX EVENTS_STOCK_ANAME_DATE_ID ON EVENTS (SYMBOL, ISIN, ANALYSENAME, DATE, EVENTDEFID);

CREATE INDEX SHARES_SYMBOL ON SHARES (SYMBOL);

-- ----------------------------------------------
-- DDL Statements for keys
-- ----------------------------------------------

-- primary/unique
ALTER TABLE QUOTATIONS ADD CONSTRAINT QUOTATIONS_SYMBOL_ISIN_DATE PRIMARY KEY (SYMBOL, ISIN, DATE);
ALTER TABLE TREND_SUPPLEMENT ADD CONSTRAINT TREND_SUPPLEMENT_PKEY UNIQUE (TRENDDATE, SYMBOL, ISIN);
ALTER TABLE EVENTS ADD CONSTRAINT EVENTS_PKEY UNIQUE (SYMBOL, ISIN, ANALYSENAME, DATE, EVENTDEFID, EVENTDEFEXTENSION);
ALTER TABLE SHARES ADD CONSTRAINT SHARES_SYMBOL_ISIN UNIQUE (SYMBOL, ISIN);
ALTER TABLE PORTFOLIO_NAME ADD CONSTRAINT PORTFOLIO_NAME_PKEY PRIMARY KEY (NAME);
ALTER TABLE TUNEDCONF ADD CONSTRAINT TUNEDCONF_SYMBOL_ISIN_CFG UNIQUE (SYMBOL, ISIN, CONFIGFILE);
ALTER TABLE ALERTS ADD CONSTRAINT ALERTS_ID_PKEY PRIMARY KEY (SYMBOL, ISIN, NAME, THRESHOLDTYPE, ALERTTYPE);
ALTER TABLE PORTFOLIO ADD CONSTRAINT PORTFOLIO_PKEY PRIMARY KEY (SYMBOL, ISIN, NAME);
ALTER TABLE CURRENCYRATE ADD CONSTRAINT CURRENCYRATE_PKEY PRIMARY KEY (FROMCURRENCY, TOCURRENCY, DATE);

-- foreign
ALTER TABLE ALERTS ADD CONSTRAINT FK_ALERTS_TO_PORTFOLIO FOREIGN KEY (SYMBOL, ISIN, NAME) REFERENCES PORTFOLIO (SYMBOL, ISIN, NAME) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE PORTFOLIO ADD CONSTRAINT FK_PORTFOLIO_TO_PORTFOLIO_NAME FOREIGN KEY (NAME) REFERENCES PORTFOLIO_NAME (NAME) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE PORTFOLIO ADD CONSTRAINT FK_PORTFOLIO_TO_SHARES FOREIGN KEY (SYMBOL, ISIN) REFERENCES SHARES (SYMBOL, ISIN) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE TREND_SUPPLEMENT ADD CONSTRAINT FK_TREND_SUPPLEMENT_TO_SHARES FOREIGN KEY (SYMBOL, ISIN) REFERENCES SHARES (SYMBOL, ISIN) ON DELETE NO ACTION ON UPDATE NO ACTION;

--ALTER TABLE PORTFOLIO DROP FOREIGN KEY FK_PORTFOLIO_TO_PORTFOLIO_NAME;
--ALTER TABLE PORTFOLIO DROP FOREIGN KEY FK_PORTFOLIO_TO_SHARES;
--ALTER TABLE TREND_SUPPLEMENT DROP FOREIGN KEY FK_TREND_SUPPLEMENT_TO_SHARES;
--ALTER TABLE ALERTS DROP FOREIGN KEY FK_ALERTS_TO_PORTFOLIO;	



