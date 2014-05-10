
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


package com.finance.pms.events.scoring.functions;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.quotations.QuotationsFactories;

public class Normalizer<T> {
	
	private static MyLogger LOGGER = MyLogger.getLogger(Normalizer.class);
	
	private Class<T> genType;
	
	private Date start;
	private Date end;
	private double maxNorm;
	private double minNorm;

	private double max;
	private double min;
	private boolean keepZero;
	
	public Normalizer(Class<T> genType, Date start, Date end, double minNorm, double maxNorm, boolean keepZero) {
		
		this.genType = genType;
		
		this.start = start;
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);
		QuotationsFactories.getFactory().incrementDate(endCal, 1);
		this.end = endCal.getTime();

		this.minNorm = minNorm;
		this.maxNorm = maxNorm;
		
		this.keepZero = keepZero;
	}

	public Normalizer(Class<T> genType, Date start, Date end, double minNorm, double maxNorm) {
		this(genType, start, end, minNorm, maxNorm, false);
	}


	public SortedMap<Date, T> normalised(SortedMap<Date,T> data) {
		
		SortedMap<Date, T> ret = new TreeMap<Date, T>();
		
		SortedMap<Date, T> subD = data.subMap(start, end);
		calculateMinMax(subD);
		
		for (Date date : subD.keySet()) {
			double value = valueOf(subD.get(date));
			ret.put(date,  tOf(((value-min)/(max-min)) * (maxNorm - minNorm) + minNorm));
		}
		
		return ret;
		
	}

	private void calculateMinMax(SortedMap<Date, T> subD) {
		
		max = -Double.MAX_VALUE;
		min = Double.MAX_VALUE;
		
		for (Date date : subD.keySet()) {
			double value = valueOf(subD.get(date));
			if (value >= max) max = value;
			if (value <= min) min = value;
		}
		
		if (keepZero) {
			double biggestAbs = Math.max(Math.abs(max), Math.abs(min));
			max = (max > 0)? biggestAbs: 0;
			min = (min < 0)? -biggestAbs : 0;
		}
		
	}

	public double getNormalizedZero() {
		if (max == 0 && min == 0) throw new RuntimeException("Uninitialised normaliser", new Exception());
		return (-min/(max-min)) * (maxNorm - minNorm) + minNorm;
	}
	
	public double getNormalizedValue(Double value) {
		if (max == 0 && min == 0) throw new RuntimeException("Uninitialised normaliser", new Exception());
		return (value-min/(max-min)) * (maxNorm - minNorm) + minNorm;
	}
	
	@SuppressWarnings("unchecked")
	private T tOf(Double destValueAti) {
		
		if (genType.isAssignableFrom(Double.class)) {
			return (T) destValueAti;
		} else if (genType.isArray() && genType.getComponentType().equals(Double.TYPE)) {
			return (T) new double[]{destValueAti};
		} else throw new NotImplementedException();
		
 	}

	private Double valueOf(T t) {
		
		if (t instanceof Double) {
			return (Double) t;
		} else if (t.getClass().isArray() &&t.getClass().getComponentType().equals(Double.TYPE)){
			if (((double[]) t).length > 1) {
				LOGGER.warn("Normalised data contains element value size > 1 is not supported. Only the first series will be normalised.");
			}
			return ((double[]) t)[0];
		} else throw new NotImplementedException();
		
	}
	
}
