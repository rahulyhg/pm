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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

import com.finance.pms.datasources.shares.Market;
import com.finance.pms.datasources.web.formaters.DayQuoteFormater;
import com.finance.pms.datasources.web.formaters.LineFormater;

public class ScraperMetrics {
	
	protected Queue eventQueue;
	protected JmsTemplate jmsTemplate;
	
	private Map<Class<? extends LineFormater>, Map<MetricsResType, List<ScraperMetricsElement>>> failures;
	
	private ScraperMetrics(JmsTemplate jmsTemplate, Queue eventQueue) {
		this.eventQueue = eventQueue;
		this.jmsTemplate = jmsTemplate;
		failures = new HashMap<Class<? extends LineFormater>, Map<MetricsResType, List<ScraperMetricsElement>>>(); 
	}

	public void addRecord(LineFormater formater, MetricsResType failure) {
		Class<? extends LineFormater> clazz = formater.getClass();
		MyUrl url = formater.getMyUrl();
		Object[] params = formater.getParams().toArray();
		
		List<ScraperMetricsElement> scrapMetForClassAndFailure = getRecordListForClassAndMetricsType(failure, clazz); 
		scrapMetForClassAndFailure.add(new ScraperMetricsElement(clazz, url, params));
		
	}

	/**
	 * @param failure
	 * @param clazz
	 * @return
	 */
	private List<ScraperMetricsElement> getRecordListForClassAndMetricsType(MetricsResType failure, Class<? extends LineFormater> clazz) {
		
		Map<MetricsResType,List<ScraperMetricsElement>> scrapperMetElesForClass = failures.get(clazz);
		if (scrapperMetElesForClass == null) {
			scrapperMetElesForClass = new HashMap<MetricsResType, List<ScraperMetricsElement>>();
			failures.put(clazz, scrapperMetElesForClass);
		} 
		List<ScraperMetricsElement> scapMetForClassAndFailure = scrapperMetElesForClass.get(failure);
		if (scapMetForClassAndFailure == null) {
			scapMetForClassAndFailure = new ArrayList<ScraperMetricsElement>();
			scrapperMetElesForClass.put(failure, scapMetForClassAndFailure);
		}
		return scapMetForClassAndFailure;
	}
	
	public void addRecord(LineFormater formater, String message) {
		Class<? extends LineFormater> clazz = formater.getClass();
		MyUrl url = formater.getMyUrl();
		List<Object> params2 = formater.getParams();
		params2.add(message);
		Object[] params = params2.toArray();
		List<ScraperMetricsElement> scrapMetForClassAndFailure = getRecordListForClassAndMetricsType(MetricsResType.HTTPERROR, clazz); 
		scrapMetForClassAndFailure.add(new ScraperMetricsElement(clazz, url, params));
		
	}

	public String getMetrics(Boolean full) {
		
		StringBuffer stringBuffer = new StringBuffer();
		for (Class<? extends LineFormater> clazz : failures.keySet()) {
			stringBuffer.append("Formater "+ clazz.getSimpleName());
			Map<MetricsResType, List<ScraperMetricsElement>> recordsForClass = failures.get(clazz);
			List<ScraperMetricsElement> failuresForClass = recordsForClass.get(MetricsResType.FAILURE);
			List<ScraperMetricsElement> emptyForClass = recordsForClass.get(MetricsResType.EMPTY);
			List<ScraperMetricsElement> successForClass = recordsForClass.get(MetricsResType.SUCCESS);
			List<ScraperMetricsElement> httpErrorsForClass = recordsForClass.get(MetricsResType.HTTPERROR);
			
			Double percentPotentialFail;
			Double percentHttpErrors;
			Double percentPatternEmpty;
			Double percentPatternFailures;
			int nbFailures = (failuresForClass == null)?0:failuresForClass.size();
			int nbEmpty = (emptyForClass == null)?0:emptyForClass.size();
			int nbSuccess = (successForClass == null)?0:successForClass.size();
			int nbHttpErrors = (httpErrorsForClass == null)?0:httpErrorsForClass.size();
			int total = nbEmpty + nbSuccess + nbHttpErrors + nbFailures;
			if (total > 0) {
				percentPotentialFail = (new Double(nbHttpErrors+nbFailures) / new Double(total))*100;
				percentHttpErrors = (new Double(nbHttpErrors) / new Double(total))*100;
				percentPatternEmpty = (new Double(nbEmpty) / new Double(total))*100;
				percentPatternFailures = (new Double(nbFailures) / new Double(total))*100;
			} else {
				percentPotentialFail = 0d;
				percentHttpErrors = 0d;
				percentPatternEmpty =0d;
				percentPatternFailures =0d;
			}
			
			List<MarketMet> httpErrorsMarkets = extractMarkets(httpErrorsForClass);
			List<MarketMet> failuresMarkets = extractMarkets(failuresForClass);
			
			stringBuffer.append(String.format(" as a percentage of %.2f %% of potential failure. " +
								"Including %.2f %% of HttpErrors (%s), " +
								"%.2f %% of Unexpected Failures (%s). " +
								"There also were %.2f %% of Empty results.\n", percentPotentialFail,percentHttpErrors,httpErrorsMarkets, percentPatternFailures,failuresMarkets, percentPatternEmpty));
			if (full) {
				stringBuffer.append("Failing http request : "+httpErrorsForClass);
				stringBuffer.append("\n");
				stringBuffer.append("Potential failures scrapings : "+failuresForClass);
				stringBuffer.append("\n");
				stringBuffer.append("Empty scrapings : "+emptyForClass);
				stringBuffer.append("\n");
				stringBuffer.append("Successful scrapings : "+successForClass);
				stringBuffer.append("\n");
				stringBuffer.append("\n");
			}
		}
		return stringBuffer.toString();
	}
	
	public String quotationMetricsMessage() {
		
		String retMess = "";
		for (Class<? extends LineFormater> lineFormaterClass : failures.keySet()) {
			if (DayQuoteFormater.class.isAssignableFrom(lineFormaterClass) || lineFormaterClass.getName().contains("DayQuote")) {
				Map<MetricsResType, List<ScraperMetricsElement>> dayQuoteFormaterFailures = failures.get(lineFormaterClass);
				List<ScraperMetricsElement> empty = dayQuoteFormaterFailures.get(MetricsResType.EMPTY);
				List<ScraperMetricsElement> http = dayQuoteFormaterFailures.get(MetricsResType.HTTPERROR);
				List<ScraperMetricsElement> otherErrors = dayQuoteFormaterFailures.get(MetricsResType.FAILURE);

				if (otherErrors != null) {
					for (ScraperMetricsElement scraperMetricsElement : otherErrors) {
						retMess = retMess + "Broken "+scraperMetricsElement.toString()+".\n";
					}
				}

				if (http != null) {
					for (ScraperMetricsElement scraperMetricsElement : http) {
						retMess = retMess + "Dead "+scraperMetricsElement.toString() + ".\n";
					}
				}

				if (empty != null) {
					for (ScraperMetricsElement scraperMetricsElement : empty) {
						retMess = retMess + "Empty "+scraperMetricsElement.toString() + ".\n";
					}
				}
			}
		}
		
		return retMess;
	}

	/**
	 * @param metricElements
	 * @return
	 */
	private List<MarketMet> extractMarkets(List<ScraperMetricsElement> metricElements) {
		List<MarketMet> markets = new ArrayList<MarketMet>();
		if (metricElements == null) return markets;
		for (ScraperMetricsElement scraperMetricsElement : metricElements) {
			Object[] params = scraperMetricsElement.getParams();
			for (Object object : params) {
				try {
					Market market = (Market) object.getClass().getMethod("getMarket", (Class<?> ) null).invoke(object, (Class<?> ) null);
					MarketMet o = new MarketMet(market);
					if (markets.contains(o)) {
						MarketMet marketMet = markets.get(markets.indexOf(o));
						marketMet.inc();
					} else {
						markets.add(o);
					}
					break;
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				} catch (IllegalAccessException e) {
				} catch (IllegalArgumentException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
		return markets;
	}
	
	public class MarketMet {
		Market market;
		Integer nb;
		public MarketMet(Market market) {
			this.market = market;
			nb = 1;
		}
		public void inc() {
			nb ++;
		}
		@Override
		public String toString() {
			return market + " " + nb ;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((market == null) ? 0 : market.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			return market.equals(((MarketMet)obj).market);
		}
		
	}
}
