-- ----------------------------------------------
-- DDL Statements for indexes
-- ----------------------------------------------

--ALTER TABLE "APP"."EVENTS" ADD CONSTRAINT "EVENTS_PKEY" UNIQUE ( "ANALYSENAME", "SYMBOL", "ISIN", "DATE", "EVENTDEF", "EVENTDEFEXTENSION");
CREATE INDEX "EVENTS_ANAME" on "EVENTS" ("ANALYSENAME");
CREATE INDEX "EVENTS_ANAME_DATE" on "EVENTS" ("ANALYSENAME","DATE");
CREATE INDEX "EVENTS_STOCK_ANAME_DATE_DEF" on "EVENTS" ("ANALYSENAME","SYMBOL","ISIN","DATE","EVENTDEF");
CREATE INDEX "EVENTS_STOCK_ANAME_DATE_DEF_TYPE" on "EVENTS" ("ANALYSENAME","SYMBOL","ISIN","DATE","EVENTDEF","EVENTTYPE");


--Other tables
CREATE INDEX "APP"."TS_STOCK_DATE" ON "APP"."TREND_SUPPLEMENT" ("SYMBOL", "ISIN", "TRENDDATE");
CREATE INDEX "APP"."SYMBOL_ISIN_INDEX" ON "APP"."QUOTATIONS" ("SYMBOL", "ISIN");
CREATE INDEX "APP"."SHARES_SYMBOL" ON "APP"."SHARES" ("SYMBOL");
-- table will be empty CREATE INDEX "PERF_SUPPLEMENT" ON  "PERF_SUPPLEMENT_LATEST" ("LATEST");