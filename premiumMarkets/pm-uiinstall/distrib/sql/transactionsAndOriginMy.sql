

ALTER TABLE PORTFOLIO CHANGE ACCOUNT EXTERNALACCOUNT  VARCHAR(255);

UPDATE SHARES set REMOVABLE = 1;

ALTER TABLE QUOTATIONS DROP PRIMARY KEY;
ALTER TABLE QUOTATIONS ADD COLUMN ORIGIN SMALLINT(6) NOT NULL DEFAULT '0';

ALTER TABLE QUOTATIONS ADD CONSTRAINT QUOTATIONS_SYMBOL_ISIN_DATE_ORIGIN PRIMARY KEY (`SYMBOL`, `ISIN`, `DATE`, `ORIGIN`);
CREATE INDEX STOCK_DATE_ORIGIN ON QUOTATIONS (`SYMBOL`, `ISIN`, `DATE`);