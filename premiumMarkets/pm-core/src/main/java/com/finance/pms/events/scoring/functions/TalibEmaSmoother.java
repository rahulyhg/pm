
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

import java.util.Arrays;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import com.finance.pms.talib.indicators.TalibCoreService;
import com.tictactec.ta.lib.MInteger;

public class TalibEmaSmoother extends Smoother {
	
	private int period;
	

	public TalibEmaSmoother(int period) {
		super();
		this.period = period;
	}


	@Override
	public SortedMap<Date, double[]> smooth(SortedMap<Date, double[]> data , Boolean fixLag) {
		
		int lag = 0;
		if (fixLag) {
			lag = (period - 1)/2;
		} 
		
		MInteger outBegIdx = new MInteger();
		MInteger outNBElement = new MInteger();
		int endIdx = data.size()-1;
		int startIdx = 0;
		
		double[] ema = new double[data.size() - period +1];
		
		double[] inReal = new double[data.size()];
		int i=0;
		for (double[] dv : data.values()) {
			inReal[i] = dv[0];
			i++;
		}
		
		//RetCode rc;
		if (period == 1) {
			ema = Arrays.copyOfRange(inReal, startIdx, endIdx);
			outBegIdx = new MInteger();
			outBegIdx.value = startIdx;
			outNBElement = new MInteger();
			outNBElement.value = endIdx - outBegIdx.value;
			//rc = RetCode.Success;
		} else {
			//rc = 
			TalibCoreService.getCore().ema(startIdx, endIdx, inReal, period, outBegIdx, outNBElement, ema);
		}
		
		SortedMap<Date, double[]> ret = new TreeMap<Date, double[]>();
		int j = 0;
		for (Date date : data.keySet()){
			if (j >= period - lag && (j- (period - lag)) < ema.length) {
				ret.put(date, new double[] {ema[j - (period - lag)]});
			}
			j++;
		}
		
		
		return ret;
	}

}
