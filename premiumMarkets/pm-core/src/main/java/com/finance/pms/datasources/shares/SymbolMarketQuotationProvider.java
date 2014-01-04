/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock market technical analysis
 * major indicators, portfolio management and historical data charting.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock market technical analysis and indexes rotation,
 * With in mind beating buy and hold, Back testing, 
 * Automated buy sell email notifications on trend change signals calculated over markets 
 * and user defined portfolios. See Premium Markets FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com for documentation and a free workable demo.
 * 
 * Copyright (C) 2008-2014 Guillaume Thoreton
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
import java.security.InvalidAlgorithmParameterException;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;


/**
 * The Class SymbolMarketQuotationProvider.
 * 
 * @author Guillaume Thoreton
 */
@Embeddable
@AccessType("field") 
public class SymbolMarketQuotationProvider implements Serializable {
	
	private static final long serialVersionUID = 2005494963121200376L;

	@Enumerated(EnumType.STRING)
	private MarketQuotationProviders marketQuotationProvider;

	@Transient
	private String extentionClue;
	
	
	
	/**
	 * Instantiates a new symbol market quotation provider.
	 * 
	 * @author Guillaume Thoreton
	 */
	public SymbolMarketQuotationProvider() {
		super();
		this.marketQuotationProvider = MarketQuotationProviders.DEFAULT;
		this.extentionClue=SymbolNameResolver.UNKNOWNEXTENSIONCLUE;
	}

	/**
	 * Instantiates a new symbol market quotation provider.
	 * 
	 * @param marketQuotationsProviders the market quotations providers
	 * @param extentionClue the extention clue
	 * 
	 * @author Guillaume Thoreton
	 */
	public SymbolMarketQuotationProvider(MarketQuotationProviders marketQuotationsProviders,String extentionClue) {
		super();
		this.marketQuotationProvider = marketQuotationsProviders;
		this.extentionClue=(null == extentionClue ||extentionClue.equals(""))? SymbolNameResolver.UNKNOWNEXTENSIONCLUE : extentionClue;
	}
	
	/**
	 * Instantiates a new symbol market quotation provider.
	 * 
	 * @param marketQuotationsProviders the market quotations providers
	 * @param symbolWExtension the symbol w extension
	 * 
	 * @author Guillaume Thoreton
	 */
	public SymbolMarketQuotationProvider(String marketQuotationsProviders, String symbolWExtension) {
		super();
		this.marketQuotationProvider = MarketQuotationProviders.valueOf(marketQuotationsProviders);
		this.extentionClue = this.marketQuotationProvider.getSymbolNameResolver().getExtensionClueFromSymbol(symbolWExtension);
	}



	/**
	 * Gets the full symbol.
	 * 
	 * @param symbol the symbol
	 * 
	 * @return the full symbol
	 * 
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 */
	public String getFullSymbol(String symbol) throws InvalidAlgorithmParameterException {
		return marketQuotationProvider.getSymbolNameResolver().getFullSymbol(symbol, this.extentionClue);
	}
	
	/**
	 * Gets the cmd param.
	 * 
	 * @return the cmd param
	 */
	public String getCmdParam() {
		return marketQuotationProvider.getCmdParam();
	}

	/**
	 * Gets the market quotation providers list.
	 * 
	 * @return the market quotation providers list
	 */
	public MarketQuotationProviders getMarketQuotationProvider() {
		return marketQuotationProvider;
	}

	@Override
	public String toString() {
		return this.marketQuotationProvider.toString();
	}
	
}
