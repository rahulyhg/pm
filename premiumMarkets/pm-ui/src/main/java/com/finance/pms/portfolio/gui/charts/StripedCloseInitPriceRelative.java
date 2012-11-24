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
package com.finance.pms.portfolio.gui.charts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.db.StripedCloseFunction;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.portfolio.PortfolioShare;


// TODO: Auto-generated Javadoc
/**
 * The Class StripedCloseInitPriceRelative.
 * 
 * @author Guillaume Thoreton
 */
public class StripedCloseInitPriceRelative extends StripedCloseFunction {

	protected static MyLogger LOGGER = MyLogger.getLogger(StripedCloseInitPriceRelative.class);

	/** The buy price. */
	private BigDecimal buyPrice;
	@SuppressWarnings("unused") //debug
	private PortfolioShare portfolioShare;

	public StripedCloseInitPriceRelative() {	
		super();
	}

	@Override
	public void targetShareData(PortfolioShare portfolioShare, Quotations stockQuotations) {

		this.portfolioShare = portfolioShare;
		this.stockQuotations = stockQuotations;

		if (arbitraryStartDate != null && arbitraryEndDate != null) {
			Date startDate = getStartDate(stockQuotations);
			startDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(0,startDate);
			Date endDate = getEndDate(stockQuotations);
			endDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(startDateQuotationIndex, endDate);
		}  else {
			startDateQuotationIndex = 0;
			endDateQuotationIndex = stockQuotations.size();
		}

		this.buyPrice = portfolioShare.getAvgBuyPrice();

	}


	@Override
	public BigDecimal[] relativeCloses() {
		
		ArrayList<BigDecimal>  retA = new ArrayList<BigDecimal>();

		for (int i = startDateQuotationIndex; i <= endDateQuotationIndex; i++) {
			
			BigDecimal value = (buyPrice.compareTo(BigDecimal.ZERO) == 0)? 
					BigDecimal.ZERO : (stockQuotations.get(i).getClose().divide(buyPrice,10,BigDecimal.ROUND_DOWN)).subtract(BigDecimal.ONE);
			retA.add(value);
		}

		return  retA.toArray(new BigDecimal[0]);
	}



}
