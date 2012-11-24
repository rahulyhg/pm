/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock market technical analysis
 * major indicators, portfolio management and historical data charting.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock market technical analysis and indexes rotation,
 * Around 80% of predicted trades more profitable than buy and hold, leading to 4 times 
 * more profit, while back testing over NYSE, NASDAQ, EURONEXT and LSE, Back testing, 
 * Automated buy sell email notifications on trend change signals calculated over markets 
 * and user defined portfolios. See Premium Markets FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com for documentation and a free workable demo.
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.mas.RestartServerException;
import com.finance.pms.threads.MyHttpClient;
import com.finance.pms.threads.PoolSemaphore;
import com.finance.pms.threads.SourceClient;
import com.finance.pms.threads.SourceConnector;

public abstract class HttpSourceMarket extends HttpSource implements SourceConnector {
	
	/** The LOGGER. */
	private static MyLogger LOGGER = MyLogger.getLogger(HttpSourceNASDAQ.class);
	
	/** The thread pool. */
	private PoolSemaphore threadPool;
	
	
	public HttpSourceMarket(String pathToprops, MyBeanFactoryAware beanFactory) {
		super(pathToprops, beanFactory);		
		LOGGER.debug("Number of Http Threads :" + this.nbHttpThreads);
		threadPool = new PoolSemaphore(this.nbHttpThreads, this, false);
	}

	@Override
	protected HttpMethodBase getRequestMethod(MyUrl url) throws UnsupportedEncodingException {
		return new GetMethod(url.getUrl());
	}

	@Override
	public MyUrl getStockQuotationURL(String ticker, String startYear, String startMonth, String startDay, String endYear,
			String endMonth, String endDay) {
		throw new RuntimeException("Fetching stock information is not implemented for market only http source");
	}

	@Override
	public PoolSemaphore getThreadPool() {
		return this.threadPool;
	}

	@Override
	public MyHttpClient httpConnect() { //throws IOException, HttpException {
		MyHttpClient myHttpClient = null;
		try {
			myHttpClient = this.myHttpConnect();
		} catch (HttpException e) {
			LOGGER.error("Can't open http connection",e);
		} catch (IOException e) {
			LOGGER.error("Can't open http connection",e);
		}
		return myHttpClient;
	}
	

	protected abstract MyHttpClient myHttpConnect() throws HttpException, IOException;

	@Override
	public void stopThreads() {
		LOGGER.info("That's all ... Bye");
	}

	
	public SourceClient connect(int connectionId) throws RestartServerException {
		SourceClient retour;
		retour = this.httpConnect();
		return retour;
	}


	
	public int crashResart(int connectionId) {
		//silent
		return 0;
	}


	
	public void shutdownSource(SourceClient sourceClient, int connectionId) {
		//silent
	}
}
