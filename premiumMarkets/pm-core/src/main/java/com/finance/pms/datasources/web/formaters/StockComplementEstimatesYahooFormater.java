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
package com.finance.pms.datasources.web.formaters;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.finance.pms.datasources.db.Validatable;
import com.finance.pms.datasources.web.MyUrl;
import com.finance.pms.screening.ScreeningSupplementedStock;

public class StockComplementEstimatesYahooFormater extends LineFormater {

	private static PatternProperties PATTERNS;
	
	private Pattern estEPS;
	
	public StockComplementEstimatesYahooFormater(String url, ScreeningSupplementedStock stockPart) {
		super(new MyUrl(url));
		params.add(stockPart);

		try {
			if (null == StockComplementEstimatesYahooFormater.PATTERNS)
				StockComplementEstimatesYahooFormater.PATTERNS = new PatternProperties("patterns.properties");
		} catch (IOException e) {
			LOGGER.debug("", e);
		}
		
		estEPS = Pattern.compile(StockComplementEstimatesYahooFormater.PATTERNS.getProperty("yahooEstEPS"));

	}

	@Override
	public List<Validatable> formatLine(String line) throws StopParseException {
		
		ScreeningSupplementedStock stockPart = (ScreeningSupplementedStock) params.get(0);
		LOGGER.trace(line);
		
		Matcher ePSGrowthMatcher;
		ePSGrowthMatcher = estEPS.matcher(line);
		if (ePSGrowthMatcher.find()) {
			String estEps = ePSGrowthMatcher.group(3);
			BigDecimal estEpsB;
			estEpsB = (estEps.equals("N/A"))? BigDecimal.ZERO : new BigDecimal(estEps).setScale(4, BigDecimal.ROUND_HALF_EVEN);
			
			stockPart.setYahooEstEPS(estEpsB);
		}
		
		if (!stockPart.isNOTSetYahooEstEPS()) {
			Validatable v = stockPart;
			v.setState(Validatable.VALID);
			throw new StopParseFoundException(v);
		}

		return null;
	}
	
	@Override
	public Boolean canHaveNoResultsFound() {
		return true;
	}

}
