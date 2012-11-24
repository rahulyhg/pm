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
package com.finance.pms.talib.dataresults;

import java.util.Date;

import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventType;



// TODO: Auto-generated Javadoc
/**
 * The Class IndicatorEventKey.
 * 
 * @author Guillaume Thoreton
 */
public class StandardEventKey implements EventKey {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6078591017314398119L;
	
	private Date date;
	private EventDefinition eventdef;
	private EventType eventType;
	
	/**
	 * Instantiates a new indicator event key.
	 * @param eventDate the event date
	 * @param eventDef the event def
	 * 
	 * @author Guillaume Thoreton
	 */
	public StandardEventKey(Date eventDate, EventDefinition eventDef, EventType eventType) {
		super();
		this.date = eventDate;
		this.eventdef = eventDef;
		this.eventType = eventType;
	}
	
	
	public StandardEventKey(Date date,Integer eventdefId, String eventType) {
		super();
		this.date = date;
		this.eventdef = EventDefinition.valueOf(eventdefId);
		this.eventType = EventType.valueOf(eventType.charAt(0));
	}


	public Date getDate() {
		return date;
	}

	
	public Comparable<?> getEvenType() {
		return eventType;
	}

	public Comparable<?> getEventDefId() {
		return eventdef;
	}
	
	@SuppressWarnings("rawtypes")
	public Comparable getEventDefExtra() {
		return eventdef;
	}

	
	
	public String toString() {
		return "StandardEventKey [date=" + date + ", eventdef=" + eventdef + ", eventType=" + eventType + "]";
	}



	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((eventdef == null) ? 0 : eventdef.hashCode());
		return result;
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandardEventKey other = (StandardEventKey) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (eventdef == null) {
			if (other.eventdef != null)
				return false;
		} else if (!eventdef.equals(other.eventdef))
			return false;
		return true;
	}	

}
