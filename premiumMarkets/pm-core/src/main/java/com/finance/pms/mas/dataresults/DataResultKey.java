/**
 * Premium Markets is an automated financial technical analysis system. 
 * It implements a graphical environment for monitoring financial technical analysis
 * major indicators and for portfolio management.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pickup the best market shares, 
 * Forecast of share prices trend changes on the basis of financial technical analysis,
 * (with a rate of around 70% of forecasts being successful observed while back testing 
 * over DJI, FTSE, DAX and SBF),
 * Back testing and Email sending on buy and sell alerts triggered while scanning markets
 * and user defined portfolios.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview of more advanced features. 
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
package com.finance.pms.mas.dataresults;

import java.util.Date;

import com.finance.pms.events.EventKey;



// TODO: Auto-generated Javadoc
/**
 * The Class DataResultKey.
 * 
 * @author Guillaume Thoreton
 */
public class DataResultKey implements EventKey {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7449740441898110511L;
	
	/** The date. */
	private Date date;
	
	/** The eventdef. */
	private Integer eventDefId;
	
	/**
	 * Instantiates a new data result key.
	 * 
	 * @param date the date
	 * @param eventDefId the eventdef
	 * 
	 * @author Guillaume Thoreton
	 */
	public DataResultKey(Date date, Integer eventDefId){
		this.date = date;
		this.eventDefId = eventDefId;
	}
	

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	
	public Date getDate() {
		return date;
	}


	/**
	 * Gets the eventdef.
	 * 
	 * @return the eventdef
	 */
	
	public Comparable<Integer> getEventDefId() {
		return eventDefId;
	}
	
	@SuppressWarnings("rawtypes")
	
	public Comparable getEvenType() {
		// FIXME
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	
	public Comparable getEventDefExtra() {
		return null;
	}	

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((eventDefId == null) ? 0 : eventDefId.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataResultKey other = (DataResultKey) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (eventDefId == null) {
			if (other.eventDefId != null)
				return false;
		} else if (!eventDefId.equals(other.eventDefId))
			return false;
		return true;
	}

	public String toString() {
		return "DataResultKey [date=" + date + ", eventdef=" + eventDefId + "]";
	}
	
}