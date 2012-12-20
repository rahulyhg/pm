--drop
ALTER TABLE  PERF_SUPPLEMENT drop  FOREIGN KEY `FK_PERF_SUPPLEMENT_TO_PORTFOLIO`;
ALTER TABLE  PORTFOLIO drop  FOREIGN KEY `FK_PORTFOLIO_TO_PORTFOLIO_NAME`;
ALTER TABLE  PORTFOLIO drop  FOREIGN KEY `FK_PORTFOLIO_TO_SHARES`;
ALTER TABLE TREND_SUPPLEMENT drop FOREIGN KEY  `FK_TREND_SUPPLEMENT_TO_SHARES`;
ALTER TABLE ALERTS drop FOREIGN KEY  `FK_ALERTS_TO_PORTFOLIO`;


ALTER TABLE CURRENCYRATE drop PRIMARY KEY;
ALTER TABLE EVENTS drop PRIMARY KEY;
ALTER TABLE PERF_SUPPLEMENT drop PRIMARY KEY;
ALTER TABLE PORTFOLIO drop PRIMARY KEY;
ALTER TABLE PORTFOLIO_NAME drop PRIMARY KEY;
ALTER TABLE QUOTATIONS drop PRIMARY KEY;
ALTER TABLE SHARES drop PRIMARY KEY;
ALTER TABLE TUNEDCONF drop PRIMARY KEY;


--primary
ALTER TABLE CURRENCYRATE CHARACTER SET utf8;
ALTER TABLE CURRENCYRATE ADD PRIMARY KEY (`FROMCURRENCY`,`TOCURRENCY`,`DATE`);
ALTER TABLE CURRENCYRATE CHARACTER SET utf8;
ALTER TABLE EVENTS  ADD PRIMARY KEY  (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTDEFID`,`EVENTDEFEXTENSION`);
ALTER TABLE  PERF_SUPPLEMENT  ADD PRIMARY KEY (`SYMBOL`,`ISIN`,`NAME`,`PERFDATE`);
ALTER TABLE  PORTFOLIO  ADD PRIMARY KEY (`SYMBOL`,`ISIN`,`NAME`);
ALTER TABLE  PORTFOLIO_NAME ADD PRIMARY KEY (`NAME`);
ALTER TABLE  QUOTATIONS ADD  PRIMARY KEY (`SYMBOL`,`ISIN`,`DATE`);
ALTER TABLE  SHARES ADD  PRIMARY KEY(`SYMBOL`,`ISIN`);
ALTER TABLE  TUNEDCONF ADD  PRIMARY KEY (`SYMBOL`,`ISIN`,`CONFIGFILE`);

--foreign
ALTER TABLE  PERF_SUPPLEMENT  ADD CONSTRAINT `FK_PERF_SUPPLEMENT_TO_PORTFOLIO` FOREIGN KEY (`SYMBOL`, `ISIN`, `NAME`) REFERENCES `PORTFOLIO` (`SYMBOL`, `ISIN`, `NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE  PORTFOLIO  ADD CONSTRAINT `FK_PORTFOLIO_TO_PORTFOLIO_NAME` FOREIGN KEY (`NAME`) REFERENCES `PORTFOLIO_NAME` (`NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE  PORTFOLIO  ADD CONSTRAINT `FK_PORTFOLIO_TO_SHARES` FOREIGN KEY (`SYMBOL`, `ISIN`) REFERENCES `SHARES` (`SYMBOL`, `ISIN`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE  TREND_SUPPLEMENT  ADD CONSTRAINT `FK_TREND_SUPPLEMENT_TO_SHARES` FOREIGN KEY (`SYMBOL`, `ISIN`) REFERENCES `SHARES` (`SYMBOL`, `ISIN`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE  ALERTS  ADD CONSTRAINT `FK_ALERTS_TO_PORTFOLIO` FOREIGN KEY (`SYMBOL`, `ISIN`, `NAME`) REFERENCES `PORTFOLIO` (`SYMBOL`, `ISIN`, `NAME`) ON DELETE NO ACTION ON UPDATE NO ACTION;





 

