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
package com.finance.pms.events.calculation;

import java.util.Date;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.quotations.NoQuotationsException;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.events.quotations.QuotationsFactories;


public abstract class Indicator {

	protected Stock stock;
	private Quotations quotations;

	protected Indicator(Stock stock, Date firstDate, Date lastDate, Currency calculationCurrency, Integer firstIdxShift, Integer lastIdxShift) throws NoQuotationsException {
		super();
		this.stock = stock;
		this.quotations  = QuotationsFactories.getFactory().getQuotationsInstance(stock, firstDate, lastDate, true, calculationCurrency, firstIdxShift, lastIdxShift);
	}
	
	protected Indicator(Quotations quotations) {
		super();
		this.stock = quotations.getStock();
		this.quotations  = quotations;
	}
	
	public Quotations getIndicatorQuotationData() {
		return this.quotations;
	}
	
	protected String getStockName() {
		return this.stock.getName();
	}
	
	protected Integer startIdx() {
		return this.quotations.getFirstDateShiftedIdx();
	}
	
	protected Integer endIdx() {
		return  this.quotations.getLastDateIdx();
	}
	
	protected Boolean hasQuotations() {
		return this.quotations.hasQuotations();
	}
	
	protected Stock getStock() {
		return stock;
	}
	
	public abstract void exportToCSV();
	
	

}
