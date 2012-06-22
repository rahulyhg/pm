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
package com.finance.pms.datasources.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.portfolio.PortfolioShare;


public class StripedCloseAbsoluteRelative extends StripedCloseFunction {

	protected static MyLogger LOGGER = MyLogger.getLogger(StripedCloseAbsoluteRelative.class);

	public StripedCloseAbsoluteRelative(Date arbitraryStartDate, Date arbitraryEndDate) {
		super(arbitraryEndDate);
		this.arbitraryStartDate = arbitraryStartDate;
		LOGGER.debug("The arbitrary date is : "+arbitraryStartDate);
	}


	@Override
	public BigDecimal[] relatedClose() {
		BigDecimal[] ret = new BigDecimal[0];
		ArrayList<BigDecimal>  retA = new ArrayList<BigDecimal>();

		BigDecimal realCloseRoot = stockQuotations.get(startDateQuotationIndex).getClose();

		for (int i = startDateQuotationIndex; i <= this.endDateQuotationIndex; i++) {
			BigDecimal relatedCloseValue = this.stockQuotations.get(i).getClose().subtract(realCloseRoot).divide(realCloseRoot,10,BigDecimal.ROUND_DOWN);
			retA.add(relatedCloseValue);
		}

		return  retA.toArray(ret);
	}

	@Override
	public void targetShareData(PortfolioShare ps, Quotations stockQuotations) {

		this.stockQuotations = stockQuotations;
		
		Date startDate = getStartDate(stockQuotations);
		startDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(0,startDate);
		
		Date endDate = getEndDate(stockQuotations);
		endDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(startDateQuotationIndex, endDate);

	}


}
