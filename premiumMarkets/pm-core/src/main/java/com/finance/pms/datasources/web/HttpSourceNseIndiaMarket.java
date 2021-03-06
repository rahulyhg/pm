/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock markets technical analysis
 * major indicators, for portfolio management and historical data charting.
 * In its advanced packaging -not provided under this license- it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock markets technical analysis and indices rotation,
 * Back testing, Automated buy sell email notifications on trend signals calculated over
 * markets and user defined portfolios. 
 * With in mind beating the buy and hold strategy.
 * Type 'Premium Markets FORECAST' in your favourite search engine for a free workable demo.
 * 
 * Copyright (C) 2008-2014 Guillaume Thoreton
 * 
 * This file is part of Premium Markets.
 * 
 * Premium Markets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.finance.pms.datasources.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.finance.pms.datasources.shares.StockCategories;

public class HttpSourceNseIndiaMarket extends HttpSourceMarket {	
	
	public HttpSourceNseIndiaMarket(String pathToprops, Providers beanFactory) {
		super(pathToprops, beanFactory);
	}

	@Override
	public String getCategoryStockListURL(StockCategories marche, String ...params) {
			
		String urlRet = "http://www.nseindia.com/content/indices/ind_%slist.csv";
		urlRet = String.format(urlRet,params[0].toLowerCase().replace(" ", ""));
		
		return urlRet;
		
	}

	@Override
	public String getStockInfoPageURL(String isin) throws UnsupportedEncodingException {
		return "http://in.finance.yahoo.com/lookup?s="+ URLEncoder.encode(isin,"UTF-8");
	}
	
	@Override
	protected HttpUriRequest getRequestMethod(MyUrl url) throws UnsupportedEncodingException {
	// Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
	// Accept-Encoding gzip, deflate
	// Accept-Language en-US,en;q=0.5
	// Connection keep-alive
	// Host www.nseindia.com
	// User-Agent Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:18.0) Gecko/20100101 Firefox/18.0
	HttpUriRequest getMethod = new HttpGet(url.getUrl());
	getMethod.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	getMethod.setHeader("Accept-Encoding", "gzip, deflate");
	getMethod.setHeader("Accept-Language", "en-US,en;q=0.5");
	getMethod.setHeader("Connection", "keep-alive");
	getMethod.setHeader("Host", "www.nseindia.com");
	getMethod.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:18.0) Gecko/20100101 Firefox/18.0");
	return getMethod;
	}

}
