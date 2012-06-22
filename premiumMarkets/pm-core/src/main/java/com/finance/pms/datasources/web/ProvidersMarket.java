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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpException;

import com.finance.pms.datasources.shares.Market;
import com.finance.pms.datasources.shares.MarketQuotationProviders;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.datasources.shares.StockCategories;
import com.finance.pms.datasources.web.formaters.LineFormater;

public abstract class ProvidersMarket extends ProvidersList {


	@Override
	public Set<Indice> getIndices() {
		return new TreeSet<Indice>();
	}

	@Override
	public void addIndice(Indice indice) {
		// Nothing
	}

	protected String getUrl() {
		return httpSource.getCategoryStockListURL(StockCategories.DEFAULT_CATEGORY);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Set<Stock> fetchStockList(MarketQuotationProviders marketQuotationsProviders) {
		
		Set<Stock> listFromWeb = new TreeSet<Stock>(getNewStockComparator());
		
		LineFormater lsf = this.getFormater(getUrl(),this.getMarket(),marketQuotationsProviders);
		@SuppressWarnings("rawtypes")
		List ltmp = new ArrayList();
		try {
			ltmp = ((HttpSourceMarket)this.httpSource).readURL(lsf);
		} catch (HttpException e) {
			LOGGER.error("",e);
		}
		
		listFromWeb.addAll(ltmp);
		
		return listFromWeb;
	}

	protected abstract Market getMarket();
	
	

}
