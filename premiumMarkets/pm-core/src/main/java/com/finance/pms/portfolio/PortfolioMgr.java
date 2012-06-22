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

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.finance.pms.admin.config.EventSignalConfig;
import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.datasources.web.Converter;
import com.finance.pms.events.pounderationrules.PonderationRule;
import com.finance.pms.events.pounderationrules.SilentPonderationRule;



// TODO: Auto-generated Javadoc
/**
 * The Class PortfolioMgr.
 * 
 * @author Guillaume Thoreton
 */
public class PortfolioMgr implements ApplicationContextAware {
	
	/** The LOGGER. */
	protected static MyLogger LOGGER = MyLogger.getLogger(PortfolioMgr.class);
	
	 /** The singleton. */
 	private static PortfolioMgr singleton;
	 
	 /** The portfolios. */
 	private List<Portfolio> portfolios;
 	private List<Portfolio> oldPortfolios;

	private PortfolioDAO portfolioDAO;
	private Converter currencyConverter;

	ApplicationContext applicationContext;
	
	/**
	 * Instantiates a new portfolio mgr.
	 * 
	 * @author Guillaume Thoreton
	 */
	public PortfolioMgr(PortfolioDAO portfolioDAO,Converter currencyConverter) {
		
		this.portfolioDAO = portfolioDAO;
		this.portfolios = portfolioDAO.loadVisiblePortfolios();
		this.currencyConverter=currencyConverter;
		resetOldPortfolioList();
		
		singleton = this;
	}
	
	//For Test ...
	public static void setInit(PortfolioMgr singleton) {
		PortfolioMgr.singleton = singleton;
		PortfolioMgr.singleton.oldPortfolios  = new ArrayList<Portfolio>();
		PortfolioMgr.singleton.portfolios   = new ArrayList<Portfolio>();
	}

	/**
	 * 
	 */
	private void resetOldPortfolioList() {
		
		this.oldPortfolios = new ArrayList<Portfolio>();
		for (Portfolio portfolio: portfolios) {
			copyPortfolioToList(portfolio,oldPortfolios);
		}
	}
	
	public AutoPortfolio getOrCreateAutoPortfolio(String analyseName, PonderationRule buyPonderationRule, PonderationRule sellPonderationRule, EventSignalConfig eventSignalConfig) {
		
		AutoPortfolio autoPortfolio =  new AutoPortfolio(analyseName, buyPonderationRule, sellPonderationRule, eventSignalConfig);
		
		int index = this.portfolios.indexOf(autoPortfolio);
		
		if (index == -1) {	
			
			this.portfolios.add(autoPortfolio);
			portfolioDAO.saveOrUpdatePortfolio(autoPortfolio);
			return autoPortfolio;
			
		} else {
			AutoPortfolio existingAutoPortfolio = (AutoPortfolio) this.portfolios.get(index);
			existingAutoPortfolio.setSellPonderationRule(sellPonderationRule);
			existingAutoPortfolio.setBuyPonderationRule(buyPonderationRule);
			existingAutoPortfolio.setEventSignalConfig(eventSignalConfig);
			return existingAutoPortfolio;
		}
	}


	public AbstractSharesList getPortfolio(String portfolioName) {
		
		int index = this.portfolios.indexOf(new Portfolio(portfolioName,new SilentPonderationRule(),new SilentPonderationRule(),null));
		
		if (index == -1) {		
			throw new IllegalArgumentException("Portfolio "+portfolioName+" doesn't exist");
		} else {
			return (AbstractSharesList) this.portfolios.get(index);
		}
	}
	
	/**
	 * Gets the single instance of PortfolioMgr.
	 * 
	 * @return single instance of PortfolioMgr
	 */
	public static PortfolioMgr getInstance() {
		if (singleton != null) {
			return singleton;
		} else {
			throw new RuntimeException("Portfolio manager should have bean injected by constructor");
		}
	}
	
	/**
	 * Adds the portfolio.
	 * 
	 * @param portfolio the p
	 * 
	 * @author Guillaume Thoreton
	 * @throws InvalidAlgorithmParameterException 
	 */
	public  void addPortfolio(Portfolio portfolio) throws InvalidAlgorithmParameterException {
		if (portfolios.contains(portfolio)) {
			throw new InvalidAlgorithmParameterException("Portfolio "+portfolio.getName()+" already exists. Please delete");
		}
		this.portfolios.add(portfolio);
	}
	

	public void removePortfolio(AbstractSharesList portfolioToRm) {
		this.portfolios.remove(portfolioToRm);
		this.portfolioDAO.delete(portfolioToRm);
	}
	
	public void hibStorePortfolio() {
		
		for (AbstractSharesList portfolio: this.portfolios) {
			LOGGER.debug("saving : "+portfolio.getName());
			try {			
				this.portfolioDAO.saveOrUpdatePortfolio(portfolio);
			} catch (Exception e) {
				LOGGER.error("Portfolio : "+portfolio,e);
			}
		}
		
		resetOldPortfolioList();
	}
	

	/**
	 * Gets the portfolios.
	 * 
	 * @return the portfolios
	 */
	public List<Portfolio> getVisiblePortfolios() {
		
		List<Portfolio> visiblePortfolios = new ArrayList<Portfolio>();
		for (Portfolio portfolio : portfolios) {
			if (!(portfolio instanceof Hidden)) {
				visiblePortfolios.add(portfolio);
			}
		}
				
		return visiblePortfolios;
	}


	public void setPortfolioDAO(PortfolioDAO portfolioDAO) {
		this.portfolioDAO = portfolioDAO;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	private PortfolioShare getPortfolioShareForSymbolInPortfolio(String stockSymbol,String portfolioName) {
		
		for (AbstractSharesList portfolio : this.portfolios) {
			if (portfolio.getName().equals(portfolioName)) {
				for(PortfolioShare portfolioShare : portfolio.getListShares().values()) {
					LOGGER.debug("Nb of shares for "+portfolio.getName()+" not empty : "+portfolioShare.getStock());
					if (portfolioShare.getStock().getSymbol().equals(stockSymbol)) {
						LOGGER.debug("Share matching : "+stockSymbol+" with "+portfolioShare.getStock().getName());
						return portfolioShare;
					}					
				}
			}
		}
		return null;
		
	}
	
	public Boolean isMonitored(Stock stock) {

		for (AbstractSharesList portfolio : this.portfolios) {
			for(PortfolioShare portfolioShare : portfolio.getListShares().values()) {
				if (portfolioShare.getStock().lenientEquals(stock)) {
					if (!portfolioShare.getMonitorLevel().equals(MonitorLevel.NONE)) {
						return true;
					} else {
						break;
					}
				}					
			}
		}
		return false;
	}
	
	public List<PortfolioShare> getRecordedPortfolioShareForStockInAllPortfolios(Stock stock) {

		List<PortfolioShare> portfolioShareList = new ArrayList<PortfolioShare>();
		for (AbstractSharesList portfolio : this.portfolios) {
			
			for(PortfolioShare portfolioShare : portfolio.getListShares().values()) {
				if (portfolioShare.getStock().equals(stock)) {
					portfolioShareList.add(portfolioShare);
				}					
			}
		}
		return portfolioShareList;
	}
	
	public List<PortfolioShare> getRecordedPortfolioShareForStockInPortfolio(Stock stock,String portfolioName) {
		List<PortfolioShare> portfolioShareList = new ArrayList<PortfolioShare>();
		PortfolioShare portfolioShare = this.getPortfolioShareForSymbolInPortfolio(stock.getSymbol(), portfolioName);
		if (null != portfolioShare) portfolioShareList.add(portfolioShare);
		return portfolioShareList;
	}

	public List<Portfolio> cancelModifications() {
		this.portfolios = new ArrayList<Portfolio>();	
		for (Portfolio portfolio: oldPortfolios) {
			copyPortfolioToList(portfolio, portfolios);
		}
		return this.portfolios;
	}

	/**
	 * @param portfolio
	 * @throws InstantiationError
	 */
	private void copyPortfolioToList(Portfolio portfolio, List<Portfolio> list) throws InstantiationError {
		if (portfolio instanceof AutoPortfolio) {
			list.add(new AutoPortfolio((AutoPortfolio)portfolio));
		} else 
		if (portfolio instanceof UserPortfolio) {
			list.add(new UserPortfolio((UserPortfolio)portfolio));
		}
		else {
			throw new InstantiationError();
		}
	}
	
	
	public Converter getCurrencyConverter() {
		return currencyConverter;
	}

	public PortfolioDAO getPortfolioDAO() {
		return this.portfolioDAO;	
	}

	public List<AutoPortfolio> getAutoPortfolios() {
		List<AutoPortfolio> retVal = new ArrayList<AutoPortfolio>();
		for (Portfolio portfolio : this.portfolios) {
			if (portfolio instanceof AutoPortfolio) {
				retVal.add((AutoPortfolio)portfolio);
			}
		}
		return retVal;
 		
	}

	public List<String> getUserPortfolioNames() {
		return portfolioDAO.loadUserPortfolioNames();
	}

	public boolean isMonitoredForPortofolio(Stock stock, String eventListName) {
		try {
			AbstractSharesList portfolio = this.getPortfolio(eventListName);
			PortfolioShare shareForStock = portfolio.getShareForStock(stock);
			return (shareForStock != null) && shareForStock.getMonitorLevel().equals(MonitorLevel.ANY);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public List<UserPortfolio> getUserPortfolios() {
		List<UserPortfolio> userPortfolios = new ArrayList<UserPortfolio>();
		for (Portfolio portfolio : portfolios) {
			if (portfolio instanceof UserPortfolio) {
				userPortfolios.add((UserPortfolio) portfolio);
			}
		}
		return userPortfolios;
	}

	
}
