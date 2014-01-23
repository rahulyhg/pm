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
package com.finance.pms.events.operations.conditional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.finance.pms.events.operations.nativeops.DoubleMapValue;

public class MultiSelectorsValue extends DoubleMapValue {
	
	private Map<String, DoubleMapValue> selectorOutputs;
	private String calculationSelector;
	
	public MultiSelectorsValue(String calculationSelector) {
		super();
		this.calculationSelector = calculationSelector;
		this.selectorOutputs = new HashMap<String, DoubleMapValue>();
	}

	public MultiSelectorsValue(Map<String, DoubleMapValue> selectorOutputs, String calculationSelector) {
		super(selectorOutputs.get(calculationSelector).getValue(null));
		this.selectorOutputs = selectorOutputs;
		this.calculationSelector = calculationSelector;
	}
	
	public DoubleMapValue getValue(String selector) {
		return selectorOutputs.get(selector);
	}
	
	public Set<String> getSelectors() {
		return selectorOutputs.keySet();
	}

	@Override
	public String toString() {
		String ret = this.getClass().getSimpleName() + " with calculation selector : "+calculationSelector;
		for (String selector : selectorOutputs.keySet()) {
			SortedMap<Date, Double> selectorOutput = selectorOutputs.get(selector).getValue(null);
			ret = ret +";" + selector + " : size is "+selectorOutput.size() + ((selectorOutput.size() > 0)?", first key "+selectorOutput.firstKey()+ ", last key "+selectorOutput.lastKey():"");
		}
		return ret;
	}


	@Override
	public Object clone() {
		try {
			MultiSelectorsValue clone = (MultiSelectorsValue) super.clone();
			clone.selectorOutputs = new HashMap<String, DoubleMapValue>();
			for (String selector : selectorOutputs.keySet()) {
				clone.selectorOutputs.put(selector, (DoubleMapValue) selectorOutputs.get(selector).clone());
			}
			return clone;
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return null;
	}

	public String getCalculationSelector() {
		return calculationSelector;
	}
	

}