/**
 * Premium Markets is an automated financial technical analysis system. 
 * It implements a graphical environment for monitoring financial technical analysis
 * major indicators and for portfolio management.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pickup the best market shares, 
 * Forecast of share prices trend changes on the basis of financial technical analysis,
 * (with a rate of around 70% of forecasts being successful observed while back testing 
 * over DJI, FTSE, DAX and SBF),
 * Back testing and Email sending on buy and sell alerts triggered while scanning markets
 * and user defined portfolios.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview of more advanced features. 
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

// TODO: Auto-generated Javadoc
/**
 * The Enum Market.
 * 
 * @author Guillaume Thoreton
 */
public enum Market implements Serializable{
	
	UNKNOWN ("UNKNOWN","unknown",Currency.EUR,YahooMarketExtentions.NN, "UNKNOWN", "UNKNOWN"),
	
	EURONEXT ("EURONEXT","euronext",Currency.EUR,YahooMarketExtentions.PAR, "EPA", "XPAR"),
	
	PARIS ("PARIS","paris",Currency.EUR,YahooMarketExtentions.PAR, "EPA", "XPAR"),
	
	NASDAQ ("NASDAQ","nasdaq",Currency.USD,YahooMarketExtentions.NASDAQ, "NASDAQ", "WMORN"),
	
	ASX ("ASX","asx",Currency.AUD,YahooMarketExtentions.ASX, "UNKNOWN", "WMORN"),
	
	BSE ("BSE","bse",Currency.INR,YahooMarketExtentions.BSE, "UNKNOWN", "WMORN"),
	
	NYSE ("NYSE","nyse",Currency.USD,YahooMarketExtentions.NYSE, "NSE", "WMORN"),
	
	AMEX  ("AMEX","amex",Currency.USD,YahooMarketExtentions.AMEX, "UNKNOWN", "WMORN"),
	
	LSE ("LON","lse",Currency.GBP,YahooMarketExtentions.LON, "UNKNOWN", "WMORN"),
	
	TSX ("TSX","tsx",Currency.CAD,YahooMarketExtentions.TSX, "UNKNOWN", "WMORN");
	
	private String marketName;
	private String friendlyName;
	private YahooMarketExtentions marketExtention;
	private Currency currency;
	private String googleMarketName;
	private String investirExtension;


	/**
	 * Instantiates a new market.
	 * 
	 * @param marketName the market name
	 * @param friendlyName the friendly name
	 * 
	 * @author Guillaume Thoreton
	 */
	private Market(String marketName,String friendlyName,Currency currency,YahooMarketExtentions marketExtentions, String googleMarketName, String investirExtension) {
		this.marketName = marketName;
		this.friendlyName = friendlyName;
		this.currency = currency;
		this.marketExtention = marketExtentions;
		this.googleMarketName = googleMarketName;
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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public YahooMarketExtentions getMarketExtention() {
		return marketExtention;
	}

	public String getGoogleMarketName() {
		return googleMarketName;
	}

	public String getInvestirExtension() {
		return investirExtension;
	}
	
	
	
}