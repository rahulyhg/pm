CREATE TABLE `EVENTSCACHE` (
  `DATE` datetime NOT NULL,
  `SYMBOL` varchar(20) NOT NULL,
  `ISIN` varchar(20) NOT NULL,
  `EVENTDEF` char(100) DEFAULT NULL,
  `EVENTTYPE` char(1) NOT NULL,
  `ACCURACY` smallint(6) DEFAULT NULL,
  `EVENTDEFID` smallint(6) NOT NULL,
  `EVENTDEFEXTENSION` varchar(100) NOT NULL DEFAULT '',
  `ANALYSENAME` varchar(256) NOT NULL,
  `MESSAGE` mediumtext,
  PRIMARY KEY (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTDEFID`,`EVENTDEFEXTENSION`),
  KEY `EVENTS_ANAME_DATE` (`ANALYSENAME`,`DATE`),
  KEY `EVENTS_STOCK_ANAME_DATE_TYPE` (`SYMBOL`,`ISIN`,`ANALYSENAME`,`DATE`,`EVENTTYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;


--update where + EVENTS.SYMBOL_FIELD + " = ? AND "+ EVENTS.ISIN_FIELD + " = ? AND " 
--					+ EVENTS.DATE_FIELD + " = ? AND " + EVENTS.EVENTDEFID_FIELD + " = ? AND " + EVENTS.EVENTDEFEXTENSION_FIELD + " = ? AND "
--					+ EVENTS.ANALYSE_NAME + " = ?";
					
--delete  " WHERE "+EVENTS.ANALYSE_NAME+" = ? AND "+EVENTS.DATE_FIELD+" >= ? AND "+EVENTS.DATE_FIELD+" <= ?"

--select + " WHERE "
--				+ EVENTS.DATE_FIELD + " >= ? AND " 
--				+ EVENTS.DATE_FIELD + " <= ? AND " 
--				+ EVENTS.ANALYSE_NAME + " in "+eventListConstraint+" AND " 
--				+ EVENTS.EVENTTYPE_FIELD + " <> '"+EventType.INFO.getEventTypeChar()+"' AND " 
--				+ EVENTS.SYMBOL_FIELD + " = ? AND "
--				+ EVENTS.ISIN_FIELD+" = ?")
					
					
