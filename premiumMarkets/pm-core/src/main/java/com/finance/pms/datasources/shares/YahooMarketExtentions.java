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
package com.finance.pms.datasources.shares;

import java.io.Serializable;

/**
 * The Enum YahooMarketExtentions.
 * 
 * @author Guillaume Thoreton
 */
public enum  YahooMarketExtentions implements Serializable {

	PAR ("PA"),
	AMS ("AS"),
	BRU ("BR"),
	LIS ("LS"),
	LON ("L"),
	ASX ("AX"),
	BSE ("BO"),
	NYSE (SymbolNameResolver.UNKNOWNEXTENSION),
	AMEX (SymbolNameResolver.UNKNOWNEXTENSION),
	NASDAQ (SymbolNameResolver.UNKNOWNEXTENSION),
	NN (SymbolNameResolver.UNKNOWNEXTENSION),
	TSX ("TO"),
	EURONEXT ("NX"),
	
	TW ("TW"), NZ("NZ"), VI("VI"), MI("MI"), ST("ST"), CO("CO"), TA("TA"), HK("HK"), HA("HA"), DU("DU"), 
	HM("HM"), F("F"), SG("SG"), DE("DE"), MU("MU"), BE("BE"), KQ("KQ"), SI("SI"), 
	JK("JK"), MC("MC"), MA("MA"), BR("BR"), SW("SW"), VX("VX"), AS("AS"), TO("TO"), LS("LS"), OL("OL");
	
	private String marketExtension;

	private YahooMarketExtentions(String specificMarketExtension) {
		this.marketExtension = specificMarketExtension;
	}

	public String getSpecificMarketExtension() {
		return marketExtension;
	}

	public static String getMarketExtension(String extensionClue) {
		return YahooMarketExtentions.valueOf(extensionClue).getSpecificMarketExtension();
	}
	
	public static String getExtensionClue(String extension) {
		YahooMarketExtentions eValues[] = YahooMarketExtentions.values();
		for (int i = 0; i < eValues.length; i++) {
			if (eValues[i].marketExtension.equals(extension))
				return eValues[i].toString();
		}
		return SymbolNameResolver.UNKNOWNEXTENSIONCLUE;
	}

}
