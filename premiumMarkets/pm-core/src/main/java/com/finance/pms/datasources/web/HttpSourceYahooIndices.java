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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.shares.StockCategories;
import com.finance.pms.threads.MyHttpClient;
import com.finance.pms.threads.SimpleHttpClient;

public class HttpSourceYahooIndices extends HttpSourceMarket {
	
	private static MyLogger LOGGER = MyLogger.getLogger(HttpSourceYahooIndices.class);
	
	
	private final String PARIS="1rP";
	private final String LONDON="1u";
	
	////http://uk.old.finance.yahoo.com/d/quotes.csv?s=%40%5ENDX&f=sl1d1t1c1ohgv&e=.csv
	//private final String YAHOO_UK = "http://uk.old.finance.yahoo.com/d/quotes.csv?s=";
	
	//http://download.finance.yahoo.com/d/quotes.csv?s=%5EFTSE&f=sl1d1t1c1ohgv&e=.csv
	//private final String YAHOO_UK = "http://download.finance.yahoo.com/d/quotes.csv?s=";
	
	//http://finance.yahoo.com/q/cp?s=^FTSE
	private final String YAHOO_UK = "http://finance.yahoo.com/q/cp?s=";
	
	//private final String CSV_PARAM = "&f=sl1d1t1c1ohgv&e=.csv";
	private final String CSV_PARAM = "&c=%s";

	//private final String OTHER="1mC";
	
	//http://uk.old.finance.yahoo.com/d/quotes.csv?s=@%5EFCHI&f=sl1d1t1c1ohgv&e=.csv
	//	private final String CACsParam = "@^FCHI";
	//	private final String FTSEsParam = "@^FTLC";
	
	
	public HttpSourceYahooIndices(String pathToprops, Providers beanFactory) {
		super(pathToprops, beanFactory);
	}

	@Override
	protected MyHttpClient myHttpConnect() throws HttpException, IOException {
		return new SimpleHttpClient();
	}

	@Override
	public String getCategoryStockListURL(StockCategories stockCategory, String...params) {

		String urlRet = YAHOO_UK;

		try {
			urlRet = urlRet + URLEncoder.encode(params[0],"UTF-8") + String.format(CSV_PARAM, params[1]);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}

		return urlRet;
	}

	@Override
	public String getStockInfoPageURL(String isin) throws UnsupportedEncodingException {
		return "http://uk.finance.yahoo.com/q?s="+ URLEncoder.encode(isin,"UTF-8");
	}
	
	public String getStockInfoPageProfilURL(String isin) throws UnsupportedEncodingException {
		return "http://uk.finance.yahoo.com/q/pr?s="+ URLEncoder.encode(isin,"UTF-8");
	}
	
	public String getStockInfoPageOpinionsURL(String isin) throws UnsupportedEncodingException {
		return "http://uk.finance.yahoo.com/q/ao?s="+ URLEncoder.encode(isin,"UTF-8");
	}
	
	public String getStockInfoPageEstimatesURL(String symbol) throws UnsupportedEncodingException {
		return "http://finance.yahoo.com/q/ae?s="+ URLEncoder.encode(symbol.replaceAll("\\..*", ""),"UTF-8");
	}
	
	public String getStockInfoPageUKEstimatesURL(String symbol) throws UnsupportedEncodingException {
		return "http://uk.finance.yahoo.com/q/ae?s="+ URLEncoder.encode(symbol,"UTF-8");
	}

	public String getStockInfoPageReutersFinancialsURL(String symbol) throws UnsupportedEncodingException {
		return "http://www.reuters.com/finance/stocks/financialHighlights?symbol="+ URLEncoder.encode(symbol,"UTF-8");
	}
	
	public String getStockInfoPageReutersOverViewURL(String symbol) throws UnsupportedEncodingException {
		return "http://www.reuters.com/finance/stocks/overview?symbol="+ URLEncoder.encode(symbol,"UTF-8");
	}
	
	

	@Override
	protected HttpMethodBase getRequestMethod(MyUrl url) throws UnsupportedEncodingException {
		GetMethod getMethod = new GetMethod(url.getUrl());
		//getMethod.setFollowRedirects(false);
		return getMethod;
	}
	
	
	public String getStockInfoPageBOResumeURL(String symbol) throws UnsupportedEncodingException {
		//return "http://www.boursorama.com/cours.phtml?code=" + URLEncoder.encode(isin,"UTF-8") + "&choix_bourse=pays%3D33&categorie=";
		//return "http://www.boursorama.com/cours.phtml?symbole="+boParams(URLEncoder.encode(symbol,"UTF-8"));
		return "http://www.boursorama.com/bourse/profil/resume_societe.phtml?symbole="+boParams(URLEncoder.encode(symbol,"UTF-8"));
	}
	
	public String  getStockInfoPageBOEstimatesURL(String symbol) throws UnsupportedEncodingException  {
		return "http://www.boursorama.com/bourse/actions/conseils/consensus/consensus_previsions.phtml?symbole="+boParams(URLEncoder.encode(symbol,"UTF-8"));
	}
	
	public String getStockInfoPageBOpinionsURL(String symbol) throws UnsupportedEncodingException {
		
		String eUrl = URLEncoder.encode(symbol,"UTF-8");
		String param = boParams(eUrl);
		//return "http://www.boursorama.com/infos/consensus/consensus_analystes.phtml?symbole="+param;
		return "http://www.boursorama.com/bourse/actions/conseils/consensus/consensus_analystes.phtml?symbole="+param;
	}
	
	/**
	 * @param eUrl
	 * @return
	 */
	private String boParams(String eUrl) {
		String param = "";
		if (eUrl.substring(eUrl.length() - 3).equals(".PA")) {
			param = PARIS + eUrl.substring(0, eUrl.length() - 3);
		} else
			if (eUrl.substring(eUrl.length() - 2).equals(".L")) {
				param = LONDON + eUrl;
			} else {
				param = eUrl;
			}
		return param;
	}

	
	
}
