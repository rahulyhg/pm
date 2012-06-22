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
package com.finance.pms.events.calculation;

import java.security.InvalidAlgorithmParameterException;
import java.util.Collection;
import java.util.Date;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.db.DataSource;
import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.ShareDAO;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.portfolio.PortfolioDAO;


// TODO: Auto-generated Javadoc
/**
 * The Class IndicatorCalculationService.
 * 
 * @author Guillaume Thoreton
 */
public abstract class IndicatorsCalculationService {
	
	private static MyLogger LOGGER = MyLogger.getLogger(IndicatorsCalculationService.class);
	
	protected Queue eventQueue;
	protected JmsTemplate jmsTemplate;
	
	protected ShareDAO shareDAO;
	protected PortfolioDAO portfolioDAO;
	private Collection<Stock> symbolsCache;
	protected Set<Observer> observers;

	public IndicatorsCalculationService() {
		super();
		observers = new ConcurrentSkipListSet<Observer>();
	}

	/**
	 * Load all stocks from db.
	 * 
	 * @return the collection< stock>
	 * 
	 * @author Guillaume Thoreton
	 */
	protected Collection<Stock> loadAllStocksFromDB() {
		return DataSource.getInstance().loadStocksForCurrentShareList();
	}

	/**
	 * Full analyze.
	 * @param datedeb the datedeb
	 * @param datefin the datefin
	 * @param calculationCurrency 
	 * @param periodType the period type
	 * @param passNumber 
	 * @param limitedCache 
	 * 
	 * @author Guillaume Thoreton
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void fullAnalyze(Date datedeb, Date datefin, Currency calculationCurrency, String analyseName, String periodType, Boolean keepCache, Integer passNumber) throws InvalidAlgorithmParameterException {
		if ((this.symbolsCache == null) || !keepCache) this.symbolsCache = loadAllStocksFromDB();
		try {
			analyze(this.symbolsCache, datedeb, datefin, calculationCurrency, analyseName, periodType, keepCache, passNumber, false, true);
		} catch (IncompleteDataSetException e) {
			LOGGER.warn(e);
		}
	}
	

	/**
	 * A bit of theory :)
	 * 
	 * What is the datedeb?
	 * This is a reference for date when the event calculation will start.
	 * In case of a daily batch :
	 * - this date is the latest event date in the data base.
	 * In case of a call from the UI :
	 * - this date is the date display in the event window.
	 * 
	 * Which events will be emailed?
	 * Only the new events should be email.
	 * It means only in theory that the ones that are later then the previous latest event.
	 * --> But in practice all the calculated events will be resent. <-- TODO : filter
	 * The difference is particularly visible when the update comes from the UI with a random date.
	 * 
	 * Note : 
	 * - It is important to notice that, the calculations of indicators starts 
	 * 	before (~ 100 quotations) the calculation of the events them selves.
	 * - The amount before the start reference date the calculation (daysbackwardday) depends on the indicators.
	 * It will be forced to 10 days in the case of the PMS indicator (and was 30 days for MAS indicators).
	 * - The events are calculated using d-1, d and d+1. This means that the latest calculated event will be on today-1.
	 * In the case of the daily calculation, the next iteration will then start on today-1 (lets call it �)
	 * The first calculation for the next daily iteration will then be on � - daysbackwardday + 1.
	 * @param symbols the symbols
	 * @param dateDeb the datedeb
	 * @param dateFin the datefin
	 * @param calculationCurrency 
	 * @param periodType the period type
	 * @param passNumber 
	 * @param export 
	 * @param limitedCache 
	 * 
	 * @author Guillaume Thoreton
	 * @param persistEvents 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws IncompleteDataSetException 
	 */
	public void analyze(Collection<Stock> symbols, Date dateDeb, Date dateFin, Currency calculationCurrency, String eventListName, 
						String periodType, Boolean kc, Integer passNumber, Boolean export, Boolean persistEvents)	throws InvalidAlgorithmParameterException, IncompleteDataSetException {
		this.analyseSymbolCollection(symbols, dateDeb, dateFin, calculationCurrency, eventListName, periodType, kc, passNumber, export, persistEvents);		
	}
	
	/**
	 * Part analyze.
	 * @param symbols the symbols
	 * @param dateDeb the datedeb
	 * @param dateFin the datefin
	 * @param calculationCurrency 
	 * @param periodType the period type
	 * @param passNumer 
	 * @param export 
	 * @param limitedCache 
	 * @author Guillaume Thoreton
	 * @param persistEvents 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws IncompleteDataSetException 
	 */
	public void partialAnalyze(Collection<Stock> symbols, Date dateDeb, Date dateFin, Currency calculationCurrency, String eventListName, 
								String periodType, Boolean keepCache, Integer passNumer, Boolean export, Boolean persistEvents) throws InvalidAlgorithmParameterException, IncompleteDataSetException {
		this.analyze(symbols, dateDeb, dateFin, calculationCurrency, eventListName,periodType , keepCache, passNumer, export, persistEvents);
	}
	
	public void setShareDAO(ShareDAO shareDAO) {
		this.shareDAO = shareDAO;
	}
	
	public void setPortfolioDAO(PortfolioDAO portfolioDAO) {
		this.portfolioDAO = portfolioDAO;
	}

	/**
	 * Analyse symbol collection.
	 * @param symbols the symbols
	 * @param datedeb the datedeb
	 * @param datefin the datefin
	 * @param calculationCurrency TODO
	 * @param periodType the period type
	 * @author Guillaume Thoreton
	 * @throws InvalidAlgorithmParameterException 
	 * @throws IncompleteDataSetException 
	 */
	protected abstract void analyseSymbolCollection(Collection<Stock> symbols, Date datedeb, Date datefin, Currency calculationCurrency, String eventListName, 
													String periodType, Boolean keepCache, Integer passNumber, Boolean export, Boolean persistEvents) throws InvalidAlgorithmParameterException, IncompleteDataSetException;

	
	public void runIndicatorsCalculation(Collection<Stock> shareList, String eventListName, Date startDate, Date endDate, Currency calculationCurrency, 
											String periodType, Integer passNumber, Boolean export, Boolean persistEvents) throws InvalidAlgorithmParameterException {
		if (shareList.size() > 0) {
			try {
				partialAnalyze(shareList, startDate, endDate, calculationCurrency, eventListName, periodType, true, passNumber, export, persistEvents);
			} catch (IncompleteDataSetException e) {
				LOGGER.warn(e);
			}
		} else {
			fullAnalyze(startDate, endDate, calculationCurrency, eventListName, periodType, true, passNumber);
		}
		
	}
	
	
	//XXX observers should be passed as params to service methods as their life cycle is shorter than the singleton
	public void addObservers(Collection<Observer> observers) {
		if (observers != null) {
			this.observers.addAll(observers);
		} 
	}

	public void removeObserver(Observer otfObserver) {
		this.observers.remove(otfObserver);
	}
	
	
}
	