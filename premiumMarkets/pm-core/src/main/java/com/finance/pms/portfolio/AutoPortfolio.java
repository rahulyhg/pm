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
package com.finance.pms.portfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.finance.pms.IndicatorCalculationServiceMain;
import com.finance.pms.admin.config.EventSignalConfig;
import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.events.EventsResources;
import com.finance.pms.events.SymbolEvents;
import com.finance.pms.events.pounderationrules.PonderationRule;
import com.finance.pms.threads.ConfigThreadLocal;
import com.finance.pms.threads.ObserverMsg;

/**
 * @author Guillaume Thoreton
 * AutoPortfolio based on Screening events and used for virtual portfolios buy daily buy and sell.
 * TODO These portfolios are multi currencies. The calculation is made using the stock currency.
 * TODO The total amounts will need conversion in a target currency.
 *
 */
@Entity
@DiscriminatorValue("AutoPortfolio")
public class AutoPortfolio extends Portfolio implements AutoPortfolioWays {

	protected static MyLogger LOGGER = MyLogger.getLogger(AutoPortfolio.class);
	
	public static final String NAME  = "AutoPortfolio";

	private AutoPortfolioDelegate autoPortfolioDelegate;
	
	private String[] additionalPortfolioEventListNames = new String[0];
	
	private EventSignalConfig eventSignalConfig;

	@SuppressWarnings("unused")
	private AutoPortfolio() {
		//Hibernate
 		autoPortfolioDelegate = new AutoPortfolioDelegate(this);
	}
	
	public AutoPortfolio(AutoPortfolio portfolio) {
		super(portfolio);	
		autoPortfolioDelegate = new AutoPortfolioDelegate(this);
		additionalPortfolioEventListNames = portfolio.additionalPortfolioEventListNames;
		eventSignalConfig = portfolio.eventSignalConfig;
	}

	public AutoPortfolio(String name, PonderationRule buyPonderationRule, PonderationRule sellPonderationRule, EventSignalConfig eventSignalConfig) {
		super(name,buyPonderationRule,sellPonderationRule,null);
		this.eventSignalConfig = eventSignalConfig;
		autoPortfolioDelegate = new AutoPortfolioDelegate(this);
	}
	
	public BigDecimal withdrawCash(Date currentDate, Currency transactionCurrency) throws NoCashAvailableException {

		BigDecimal ret = BigDecimal.ZERO;

		if (this.getNotNullTotalInAmountEver().compareTo(AutoPortfolioDelegate.DEFAULT_INITIAL_CASH) <= 0) {
			ret = AutoPortfolioDelegate.DEFAULT_TRANSACTION_AMOUNT;
		} else {
			ret = getNotNullTotalOutAmountEver().subtract(this.getTotalInAmountEver()).add(AutoPortfolioDelegate.DEFAULT_INITIAL_CASH);
		}

		if (ret.compareTo(BigDecimal.ZERO) <= 0) {
			throw new NoCashAvailableException("No cash left : out " + getTotalOutAmountEver()+" in "+ getTotalInAmountEver());
		}

		return PortfolioMgr.getInstance().getCurrencyConverter().convert(Currency.EUR, transactionCurrency, ret, currentDate);
	}

	@Transient
	public BigDecimal getAvailableCash() {
		return getTotalOutAmountEver();
	}

	public void notifyObservers(ObserverMsg string) {
		super.notifyObservers(string);
	}
	
	@Override
	public void setChanged() {
		super.setChanged();
	}

	public TransactionHistory calculate(Date endDate, String... additionalEventListNames) {
		
		ConfigThreadLocal.set(EventSignalConfig.EVENT_SIGNAL_NAME, this.eventSignalConfig);
		
		List<SymbolEvents> listEvents = loadEventsForCalculation(endDate);
		return autoPortfolioDelegate.calculate(listEvents, endDate, getNonNullBuyPonderationRule(), getNonNullSellPonderationRule());
	}
	
	/**
	 * @param currentDate
	 * @param eventListNames
	 * @return
	 */
	
	public List<SymbolEvents> loadEventsForCalculation(Date currentDate) {
		
		List<SymbolEvents> fullListEvents = new ArrayList<SymbolEvents>();
		fullListEvents = loadEvents(currentDate, eventSignalConfig, this.additionalPortfolioEventListNames, this.getName());

		return fullListEvents;
	}

	/**
	 * @param currentDate
	 * @param eventSignalConfig
	 * @return
	 */
	private List<SymbolEvents> loadEvents(Date currentDate, EventSignalConfig eventSignalConfig, String[] additionalPortfolioEventListNames, String... otherNames) {
		
		Date dateStart = eventLoadStartDate(currentDate, eventSignalConfig.getBackwardDaySpan());
		String[] fullEventListNames = Arrays.copyOf(additionalPortfolioEventListNames, additionalPortfolioEventListNames.length+otherNames.length);
		
		for (int i = 0; i < otherNames.length; i++) {
			fullEventListNames[additionalPortfolioEventListNames.length + i] = otherNames[i];
		}
		
		return EventsResources.getInstance().getListEventsFor(dateStart, currentDate, true, fullEventListNames);
	}

	/**
	 * @param currentDate
	 * @param backwardDaySpan
	 * @return
	 */
	private Date eventLoadStartDate(Date currentDate, Integer backwardDaySpan) {
		//Get start date
//		Date dateStart;
//		if (!MainGui.isGuiRunning) { //not in GUI
//			dateStart = IndicatorCalculationServiceMain.getDateMoinsNJours(currentDate, backwardDaySpan);
//		} else { //&& MainGui.isGuiRunning) { //in GUI
//			dateStart =	MainGui.analyseStartDateCalculation();
//		}
//		return dateStart;
		//FIXME set backwardDaySpan from UI
		return IndicatorCalculationServiceMain.getDateMoinsNJours(currentDate, backwardDaySpan);
	}


	@Override
	public void resetManualAlerts(PortfolioShare portfolioShare, AbstractSharesList sourcePortfolio) {
		//nothing as there should be no manual alerts in an Auto Portfolio
	}

	public void exportAutoportfolioContent(Date date) {
		//FIXME not in use and won't work
		//IndicatorTuning.exportPortfolioContentToTotalFile(this,date);
	}

	@Transient
	public TransactionHistory getTransactionHistory() {
		return autoPortfolioDelegate.getTransactionHistory();
	}
	
	public void setAdditionalPortfolioEventListNames(String[] additionalEventListNames) {
		this.additionalPortfolioEventListNames = additionalEventListNames;
	}

	public void setEventSignalConfig(EventSignalConfig eventSignalConfig) {
		this.eventSignalConfig = eventSignalConfig;
	}

	public void log(TransactionRecord transactionRecord) {
		this.autoPortfolioDelegate.log(transactionRecord);
	}
	
}
