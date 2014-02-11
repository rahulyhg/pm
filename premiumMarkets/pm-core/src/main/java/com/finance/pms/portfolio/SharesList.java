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
package com.finance.pms.portfolio;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;

/**
 * @author Guillaume Thoreton
 * Used for market share list storage
 *
 */
@Entity
@DiscriminatorValue("ShareList")
public class SharesList extends AbstractSharesList {
	
	@SuppressWarnings("unused")
	private SharesList() {
		super();
	}

	public SharesList(String name) {
		super(name);
	}
	
	public void addShares(Collection<Stock> pssToAdd) {
		for (Stock stock : pssToAdd) {
			addShare(stock);
		}
	}
	
	public void addShare(Stock stock) {
		//PortfolioShare newPortfolioShare = new PortfolioShare(this, stock, BigDecimal.ONE, EventSignalConfig.getNewDate(), BigDecimal.ONE, BigDecimal.ZERO, MonitorLevel.NONE, stock.getMarketValuation().getCurrency());
		PortfolioShare newPortfolioShare = new PortfolioShare(this, stock, MonitorLevel.NONE, stock.getMarketValuation().getCurrency());
		addShareToList(newPortfolioShare);
		ShareListMgr.getInstance().addForeignKeysUpdate(newPortfolioShare);
	}
	
	public void removeShare(PortfolioShare portfolioShare) {
		ShareListMgr.getInstance().removeForeignKeysUpdate(portfolioShare);
		removeShareFromList(portfolioShare);
	}
	
	public void removeShares(Set<PortfolioShare> pssToRemove) {
		for (PortfolioShare ps : pssToRemove) {
			removeShare(ps);
		}
	}
	
	public Set<Stock> toStocksSet() {
		Set<Stock> retSet = new HashSet<Stock>();
		for (PortfolioShare portfolioShare : this.getListShares().values()) {
			retSet.add(portfolioShare.getStock());
		}
		return retSet;
	}
	
	public SortedSet<Stock> toSortedStocksSet() {
		SortedSet<Stock> retSet = new TreeSet<Stock>(new Comparator<Stock>() {
			public int compare(Stock o1, Stock o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		
		for (PortfolioShare portfolioShare : this.getListShares().values()) {
			retSet.add(portfolioShare.getStock());
		}
		
		return retSet;
	}

	@Override
	public Date getLastTransactionFor(Stock stock) {
		return new Date(0);
	}

	@Override
	public BigDecimal getCashInFor(PortfolioShare portfolioShare, Date currentStartDate, Date currentEndDate, Currency currency) {
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal getCashOutFor(PortfolioShare portfolioShare, Date currentStartDate, Date currentEndDate, Currency currency) {
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getQuantityFor(PortfolioShare portfolioShare,Date currentStartDate, Date currentEndDate) {
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal getBasisFor(PortfolioShare portfolioShare, Date currentStartDate, Date currentEndDate, Currency currency) {
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal getPriceAvgBuyFor(PortfolioShare portfolioShare, Date currentStartDate, Date currentEndDate, Currency currency) {
		return BigDecimal.ONE;
	}

	@Override
	public InOutWeighted getWeightedInvestedFor(PortfolioShare portfolioShare, Date currentEndDate, Currency currency) {
		return new InOutWeighted(BigDecimal.ONE, BigDecimal.ZERO, currentEndDate);
	}

}
