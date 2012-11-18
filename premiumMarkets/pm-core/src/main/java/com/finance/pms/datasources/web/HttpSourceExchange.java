/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock market technical analysis
 * major indicators, portfolio management and historical data charting.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock market technical analysis and indexes rotation,
 * With around 80% of forecasted trades above buy and hold, while back testing over DJI, 
 * FTSE, DAX and SBF, Back testing, 
 * Buy sell email notifications with automated markets and user defined portfolios scanning.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview and a free workable demo.
 * 
 * Copyright (C) 2008-2012 Guillaume Thoreton
 * 
 * This file is part of Premium Markets.
 * 
 * Premium Markets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.finance.pms.datasources.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.finance.pms.datasources.shares.Currency;


public class HttpSourceExchange extends HttpSourceYahoo {
	
	public HttpSourceExchange(String pathToprops, MyBeanFactoryAware beanFactory) {
		super(pathToprops, beanFactory);		
	}
	
	public String getUrl(Currency currency) {
		//String url = "http://www.xe.com/ucc/convert.cgi?Amount=1&From=%S&To=%S&image.x=51&image.y=10&image=Submit";
		//String url = "http://uk.finance.yahoo.com/currencies/converter/#from=%S;to=EUR;amt=1"
		String url = "http://www.gocurrency.com/v2/dorate.php?inV=1&from=%S&to=%S&Calculate=Convert";
		return String.format(url,currency.toString(),"EUR");
	}
	
	public String getOandaHistoryUrl(Currency fromCurrency,Currency toCurrency, Date start,Date end) {
		String url="http://www.oanda.com/transactionCurrency/historical-rates?date_fmt=us&date=%s&date1=%s&exch=%S&expr=%S&margin_fixed=0&format=CSV&redirected=1";
		return String.format(url,new SimpleDateFormat("MM/dd/yy").format(end),new SimpleDateFormat("MM/dd/yy").format(start),fromCurrency.toString(),toCurrency.toString());
	}
	
	public String getImfHistoryUrl(Date date) {
		String url="http://www.imf.org/external/np/fin/data/rms_mth.aspx?SelectDate=%s&reportType=REP&tsvflag=Y";
		return String.format(url,new SimpleDateFormat("yyyy-MM-dd").format(date));
	}
	
}
