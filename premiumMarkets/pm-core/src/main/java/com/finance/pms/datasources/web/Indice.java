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
package com.finance.pms.datasources.web;

import java.util.Set;
import java.util.TreeSet;

import com.finance.pms.datasources.shares.Market;

public class Indice implements Comparable<Indice> {
	
	private String name;
	private Market market;
	
	public Indice(String indice, Market market) {
		super();
		this.name = indice;
		this.market = market;
	}
	
	public Indice(String indice, String market) {
		super();
		this.name = indice;
		this.market = Market.valueOf(market);
	}
	
	public static String formatName(Set<Indice> indices) {
		String extention = "";
		for (Indice indice : indices) {
			extention = extention+","+indice.getName()+":"+indice.getMarket();
		}
		return extention;
	}

	public static String formatToString(Set<Indice> indices) {
		String txt = "";
		for (Indice indice : indices) {
			txt = txt+indice.toString();
		}
		return txt;
	}
	
	
	public static Set<Indice> parseString(String yahooIndices) {
		Set<Indice> listIndice = new TreeSet<Indice>();
		if (yahooIndices.isEmpty()) return listIndice;
		
		String[] indices = yahooIndices.split(",");
		for (String indice : indices) {
			String[] indiceElems = indice.split(":");
			if (indiceElems.length == 2) {
				listIndice.add(new Indice(indiceElems[0],indiceElems[1]));
			}
		}
		return listIndice;
	}
	
	/**
	 * @param providersYahooIndex
	 * @param indices
	 */
	public static void addIndicesToProvider(Providers providersYahooIndex, String... indices) {
		for (int i=0; i < indices.length; i= i+2) {
			providersYahooIndex.addIndice(new Indice(indices[i],indices[i+1]));
		}
	}

	public String getName() {
		return name;
	}

	public Market getMarket() {
		return market;
	}

	@Override
	public String toString() {
		return this.getName()+":"+this.getMarket()+",";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Indice other = (Indice) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int compareTo(Indice o) {
		return name.compareTo(o.name);
	}
	
	
}
