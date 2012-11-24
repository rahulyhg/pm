
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


package com.finance.pms.events.scoring.functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CurvesSplitsSolver implements CurvesConstantOperation {

	@Override
	public SortedMap<Date, double[]> operate(SortedMap<Date, double[]> data1, double constant) {
		
		SortedMap<Date, double[]> ret = new TreeMap<Date, double[]>(data1);
		CurvesMultiplication curvesSubstraction = new CurvesMultiplication();
		Date firstKey = data1.firstKey();
		
		List<Date> keysList = new ArrayList<Date>(data1.keySet());
		for (int j = 1; j < keysList.size(); j++) {
			double dj = data1.get(keysList.get(j))[0];
			double djm1 = data1.get(keysList.get(j-1))[0];
			double change = (dj - djm1)/djm1;
			if (Math.abs(change) > constant) {
				SortedMap<Date, double[]> solvedSub = curvesSubstraction.operate(ret.subMap(firstKey, keysList.get(j)), dj/djm1);
				ret.putAll(solvedSub);
			}
		}
		
		return ret;

	}

}
