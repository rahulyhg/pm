ALTER TABLE EVENTS  ADD PRIMARY KEY  (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTDEFID`,`EVENTDEFEXTENSION`);

ALTER TABLE EVENTS  ADD INDEX `EVENTS_ANAME` (`ANALYSENAME`);
ALTER TABLE EVENTS  ADD INDEX `EVENTS_ANAME_DATE` (`ANALYSENAME`,`DATE`);
ALTER TABLE EVENTS  ADD INDEX `EVENTS_ANAME_DATE_ID` (`ANALYSENAME`,`DATE`,`EVENTDEFID`);
ALTER TABLE EVENTS  ADD INDEX `EVENTS_STOCK_ANAME_TYPE_ID` (`SYMBOL`,`ISIN`,`ANALYSENAME`,`EVENTTYPE`,`EVENTDEFID`);
ALTER TABLE EVENTS  ADD INDEX `EVENTS_STOCK_ANAME_DATE_TYPE` (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTTYPE`);
ALTER TABLE EVENTS  ADD INDEX `EVENTS_STOCK_ANAME_DATE_ID` (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTDEFID`);