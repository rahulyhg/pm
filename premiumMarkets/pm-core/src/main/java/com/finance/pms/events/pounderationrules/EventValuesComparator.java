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
package com.finance.pms.events.pounderationrules;

import java.util.Comparator;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.EventInfo;
import com.finance.pms.events.EventValue;


/**
 * The Class SymbolEventComparator.
 * 
 * @author Guillaume Thoreton
 */
public class EventValuesComparator implements Comparator<EventValue> {

	protected static MyLogger LOGGER = MyLogger.getLogger(EventValuesComparator.class);

	String method;
	Object[] params;
	private Boolean isAsc;
	
	Class<? extends Object>[] paramsClass;

	
	/**
	 * Instantiates a new symbol event comparator.
	 * 
	 * @param method the method
	 * @param params the params
	 * 
	 * @author Guillaume Thoreton
	 */
	public EventValuesComparator(Boolean isAsc, String method, Object... params) {
		
			this.method = method;
			this.params = params;
			this.paramsClass = new Class<?>[params.length];
			for (int i = 0; i < params.length; i++) {
				this.paramsClass[i] = PonderationRule.class;
			}
			this.isAsc = isAsc;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(EventValue o1, EventValue o2) {
		int retour = 0;
		try {
			if (this.method.equals("getEventDef")) {
				EventInfo o1Value = (EventInfo) o1.getClass().getMethod(this.method, this.paramsClass).invoke(o1, params);
				EventInfo o2Value = (EventInfo) o2.getClass().getMethod(this.method,this.paramsClass).invoke(o2, params);
				retour = o2Value.compareTo(o1Value);
				retour = (isAsc)?-retour:retour;
			} else {
				Comparable o1Value = (Comparable) o1.getClass().getMethod(this.method, this.paramsClass).invoke(o1, params);
				Comparable o2Value = (Comparable) o2.getClass().getMethod(this.method,this.paramsClass).invoke(o2, params);
				retour = o2Value.compareTo(o1Value);
				retour = (isAsc)?-retour:retour;
			}
		} catch (Exception e) {
			LOGGER.error("",e);
		} 
		if (retour == 0) {
			retour = (o2).getEventDef().compareTo((o1).getEventDef());
		}
		if (retour == 0) {
			retour = (o2).getDate().compareTo((o1).getDate());
		}

		return retour;
	}

}