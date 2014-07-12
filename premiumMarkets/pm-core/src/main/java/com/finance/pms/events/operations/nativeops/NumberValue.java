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
package com.finance.pms.events.operations.nativeops;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.operations.TargetStockInfo;
import com.finance.pms.events.operations.Value;

@XmlRootElement
public class NumberValue extends Value<Number> implements Cloneable  {
	
	protected static MyLogger LOGGER = MyLogger.getLogger(NumberValue.class);
	
	@XmlElement
	Double numberValue;

	@SuppressWarnings("unused")
	private NumberValue() {
		super();
	}

	public NumberValue(Double value) {
		super();
		this.numberValue = value;
	}
	
	public NumberValue(String value) {
		super();
		this.numberValue = Double.valueOf(value);
	}
	
	public Number getValue(TargetStockInfo targetStockInfo) {
		return numberValue;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() +" : value "+numberValue;
	}

	public Double getNumberValue() {
		return numberValue;
	}
	
	@Override
	public Object clone() {
		try {
			NumberValue clone = (NumberValue) super.clone();
			return clone;
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return null;
	}

	@Override
	public String getValueAsString() {
		return numberValue.toString();
	}

}
