
/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock markets technical analysis
 * major indicators, for portfolio management and historical data charting.
 * In its advanced packaging -not provided under this license- it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock markets technical analysis and indices rotation,
 * Back testing, Automated buy sell email notifications on trend signals calculated over
 * markets and user defined portfolios. 
 * With in mind beating the buy and hold strategy.
 * Type 'Premium Markets FORECAST' in your favourite search engine for a free workable demo.
 * 
 * Copyright (C) 2008-2014 Guillaume Thoreton
 * 
 * This file is part of Premium Markets.
 * 
 * Premium Markets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package com.finance.pms.events.scoring.functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MomentumSmoother extends Smoother {

	private int period;

	public MomentumSmoother(int period) {
		this.period = period;
	}

	@Override
	public SortedMap<Date, double[]> smooth(SortedMap<Date, double[]> data, Boolean fixLag) {

		SortedMap<Date, double[]> ret = new TreeMap<Date, double[]>();

		List<double[]> values = new ArrayList<double[]>(data.values());
		List<Date> keys = new ArrayList<Date>(data.keySet());
		
		for (int i = period; i < values.size(); i++) {

			double currentValue = values.get(i)[0];
			double previousValue = values.get(i-period)[0];
			double value = (currentValue - previousValue)/previousValue;
			
			//DecimalFormat twoDForm = new DecimalFormat("#.##");
			//ret.put(keys.get(i), new double[]{Double.valueOf(twoDForm.format(value))});
			ret.put(keys.get(i), new double[]{value});

		}

		return ret;
	}

}
