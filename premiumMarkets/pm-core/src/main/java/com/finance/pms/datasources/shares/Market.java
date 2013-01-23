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
package com.finance.pms.datasources.shares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.finance.pms.admin.install.logging.MyLogger;

// TODO: Auto-generated Javadoc
/**
 * The Enum Market.
 * 
 * @author Guillaume Thoreton
 */
public enum Market implements Serializable {
	
	UNKNOWN ("UNKNOWN","unknown",Currency.EUR,BigDecimal.ONE,YahooMarketExtentions.NN, "UNKNOWN", "UNKNOWN"),
	
	EURONEXT ("EURONEXT","euronext",Currency.EUR,BigDecimal.ONE,YahooMarketExtentions.PAR, "EPA", "XPAR"),
	
	PARIS ("PARIS","paris",Currency.EUR,BigDecimal.ONE,YahooMarketExtentions.PAR, "EPA", "XPAR"),
	
	NASDAQ ("NASDAQ","nasdaq",Currency.USD,BigDecimal.ONE,YahooMarketExtentions.NASDAQ, "NASDAQ", "WMORN"),
	
	ASX ("ASX","asx",Currency.AUD,BigDecimal.ONE,YahooMarketExtentions.ASX, "UNKNOWN", "WMORN"),
	
	BSE ("BSE","bse",Currency.INR,BigDecimal.ONE,YahooMarketExtentions.BSE, "UNKNOWN", "WMORN"),
	
	NSE ("NSE","nse",Currency.INR,BigDecimal.ONE,YahooMarketExtentions.BSE, "UNKNOWN", "WMORN"),
	
	NYSE ("NYSE","nyse",Currency.USD,BigDecimal.ONE,YahooMarketExtentions.NYSE, "NSE", "WMORN"),
	
	AMEX  ("AMEX","amex",Currency.USD,BigDecimal.ONE,YahooMarketExtentions.AMEX, "UNKNOWN", "WMORN"),
	
	LSE ("LON","lse",Currency.GBP,new BigDecimal(100),YahooMarketExtentions.LON, "UNKNOWN", "WMORN"),
	
	TSX ("TSX","tsx",Currency.CAD,BigDecimal.ONE,YahooMarketExtentions.TSX, "UNKNOWN", "WMORN");
	
	
	private static MyLogger LOGGER = MyLogger.getLogger(Market.class);
	
	private String marketName;
	private String friendlyName;
	private Currency defaultCurrency;
	private BigDecimal defaultCurrencyFactor;
	private YahooMarketExtentions yahooExtension;
	private String googleExtension;
	private String investirExtension;

	/**
	 * Instantiates a new market.
	 * 
	 * @param marketName the market name
	 * @param friendlyName the friendly name
	 * 
	 * @author Guillaume Thoreton
	 */
	private Market(String marketName,String friendlyName,Currency defaultCurrency,BigDecimal defaultCurrencyFactor, YahooMarketExtentions marketExtentions, String googleMarketName, String investirExtension) {
		this.marketName = marketName;
		this.friendlyName = friendlyName;
		this.defaultCurrency = defaultCurrency;
		this.defaultCurrencyFactor = defaultCurrencyFactor;
		this.yahooExtension = marketExtentions;
		this.googleExtension = googleMarketName;
		this.investirExtension = investirExtension;
	}

	/**
	 * Gets the friendly name.
	 * 
	 * @return the friendly name
	 */
	public String getFriendlyName() {
		return friendlyName;
	}
	
	
	public String getMarketName() {
		return marketName;
	}

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	@SuppressWarnings("unused")
	private void setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public YahooMarketExtentions getYahooExtension() {
		return yahooExtension;
	}

	public String getGoogleExtension() {
		return googleExtension;
	}

	public String getInvestirExtension() {
		return investirExtension;
	}
	
	public static Set<String> getExtensionFor(MarketQuotationProviders provider) {
		Set<String> ret = new HashSet<String>();
		String cmdParam4Prov = provider.getCmdParam();
		
		try {
			for (Market market : values()) {
				String ext = Market.class.getDeclaredField(cmdParam4Prov+"Extension").get(market).toString();
				ret.add(ext);
			}
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		
		return ret;
	}

	public BigDecimal getDefaultCurrencyFactor() {
		return defaultCurrencyFactor;
	}

	@SuppressWarnings("unused")
	private void setDefaultCurrencyFactor(BigDecimal defaultCurrencyFactor) {
		this.defaultCurrencyFactor = defaultCurrencyFactor;
	}
	
	
	
}