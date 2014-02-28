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
package com.finance.pms.portfolio.gui.charts;

import java.util.Date;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.portfolio.gui.SlidingPortfolioShare;
import com.tictactec.ta.lib.MInteger;

/**
 * The Interface StripedCloseFunction.
 * 
 * @author Guillaume Thoreton
 */
public abstract class StripedCloseFunction {
	
	protected static MyLogger LOGGER = MyLogger.getLogger(StripedCloseFunction.class);
	
	protected Date arbitraryStartDate;
	protected Date arbitraryEndDate;

	public StripedCloseFunction() {
		super();
	}

	public StripedCloseFunction(Date arbitraryEndDate) {
		super();
		this.arbitraryEndDate = arbitraryEndDate;
	}

	public abstract Number[] targetShareData(SlidingPortfolioShare ps, Quotations stockQuotations, MInteger startDateQuotationIndex, MInteger endDateQuotationIndex);
	
	public void updateEndDate(Date date) {
		this.arbitraryEndDate = date;
	
	}

	public void updateStartDate(Date date) {
		this.arbitraryStartDate = date;
	
	}

	protected Date getStartDate(Quotations stockQuotations) {
		
		Date startDate = this.arbitraryStartDate;
		startDate = (startDate.before(stockQuotations.getDate(0)))?stockQuotations.getDate(0):startDate;
		LOGGER.debug("The start date is : "+startDate);
		
		return startDate;
	}

	protected Date getEndDate(Quotations stockQuotations) {
		
		Date endDate = this.arbitraryEndDate;
		Integer lastQuoteI = stockQuotations.size()-1;
		endDate = (endDate.after(stockQuotations.getDate(lastQuoteI)))?stockQuotations.getDate(lastQuoteI):endDate;
		LOGGER.debug("The end date is : "+endDate);
		
		return endDate;
	}
	
	public Date getArbitraryStartDateForChart() {
		return arbitraryStartDate;
	}
	
	public abstract Date getArbitraryStartDateForCalculation();

	public Date getArbitraryEndDate() {
		return arbitraryEndDate;
	}
	
	public abstract String lineToolTip();
	
	public abstract String formatYValue(Number yValue);
	
	public abstract Boolean isRelative();
	
}