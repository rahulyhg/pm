-- ----------------------------------------------
-- DDL Statements for indexes
-- ----------------------------------------------

--EVENTS
-- PRIMARY KEY ("ANALYSENAME", "SYMBOL", "ISIN", "DATE", "EVENTDEF", "EVENTDEFEXTENSION");

----Test and admin : cleanEventsForAnalysisName EVENTS_ANAME
--Ignored CREATE INDEX "APP"."EVENTS_ANAME" ON "APP"."EVENTS" ("ANALYSENAME");

----UI : cleanEventsForAnalysisNameAndStock  EVENTS_STOCK_ANAME_DATE_DEF
CREATE INDEX "APP"."EVENTS_STOCK_ANAME_DATE_DEF" ON "APP"."EVENTS" ("ANALYSENAME", "SYMBOL", "ISIN", "DATE", "EVENTDEF");

----UI : cleanEventsForIndicators : EVENTS_ANAME_DATE_DEF
CREATE INDEX "APP"."EVENTS_ANAME_DATE_DEF" ON "APP"."EVENTS" ("ANALYSENAME", "DATE", "EVENTDEF");

----UI : loadEventsByDate : EVENTS_ANAME_DATE_DEF_TYPE
--Ignored

----UI : loadEventsByDate : EVENTS_STOCK_ANAME_DATE_DEF_TYPE
CREATE INDEX "APP"."EVENTS_STOCK_ANAME_DATE_DEF_TYPE" ON "APP"."EVENTS" ("ANALYSENAME", "SYMBOL", "ISIN", "DATE", "EVENTDEF", "EVENTTYPE");

----Not Used?
--CREATE INDEX "APP"."EVENTS_ANAME_DATE" ON "APP"."EVENTS" ("ANALYSENAME", "DATE");



--OTHER
CREATE INDEX "APP"."STOCK_DATE_ORIGIN" ON "APP"."QUOTATIONS" ("SYMBOL", "ISIN", "DATE");

CREATE INDEX "APP"."SYMBOL_ISIN_INDEX" ON "APP"."QUOTATIONS" ("SYMBOL", "ISIN");

CREATE INDEX "APP"."SHARES_SYMBOL" ON "APP"."SHARES" ("SYMBOL");



