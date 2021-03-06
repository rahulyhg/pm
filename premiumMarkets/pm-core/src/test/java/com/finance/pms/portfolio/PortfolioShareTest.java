/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock markets technical analysis
 * major indicators, for portfolio management and historical data charting.
 * In its advanced packaging -not provided under this license- it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock markets technical analysis and indices rotation,
 * Back testing, Automated buy sell email notifications on trend signals calculated over
 * markets and user defined portfolios. 
 * With in mind beating the buy and hold strategy.
 * Type 'Premium Markets FORECAST' in your favourite search engine for a free workable demo.
 * 
 * Copyright (C) 2008-2014 Guillaume Thoreton
 * 
 * This file is part of Premium Markets.
 * 
 * Premium Markets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.finance.pms.portfolio;

import org.junit.Before;
import org.junit.Test;

public class PortfolioShareTest {
	
	PortfolioShare portfolioShare1;
	PortfolioShare portfolioShare2;
	AbstractSharesList portfolio;
	PortfolioShare portfolioShare3;
	
	
	@Before
	public void setUp() throws Exception {
		
		portfolio = new UserPortfolio("pTest", null);
		
		//portfolioShare2 = new PortfolioShare("stock1","2","19-09-2008","55","11",0F,"any");
		//FIXME portfolioShare2 = new PortfolioShare();
//		portfolioShare2.setStock(new Stock());
//		portfolioShare2.setCashin(55f);
//		portfolioShare2.setCashout(11f);
//		portfolioShare2.setQuantity(2f);
//		portfolioShare2.setLastDayCloseQuotation(10f);
		//"stock2",4f,new Date(),100f,22f,0F
		
		//portfolioShare1 = new PortfolioShare("stock2","4","19-09-2008","100","22",0F,"any");
		//FIXME portfolioShare1 = new PortfolioShare();
//		portfolioShare1.setStock(new Stock());
//		portfolioShare1.setCashin(100f);
//		portfolioShare1.setCashout(22f);
//		portfolioShare1.setQuantity(4f);
//		portfolioShare1.setLastDayCloseQuotation(20f);
		
		//portfolio.rawAddShare(portfolioShare2);
//		portfolio.addAmountToTotalAmountIn(new BigDecimal(55f),Currency.EUR, new Date());
//		portfolio.addAmountToTotalAmountOut(new BigDecimal(11f),Currency.EUR, new Date());
//		//portfolio.rawAddShare(portfolioShare1);
//		portfolio.addAmountToTotalAmountIn(new BigDecimal(100f),Currency.EUR, new Date());
//		portfolio.addAmountToTotalAmountOut(new BigDecimal(22f),Currency.EUR, new Date());
	
	}

	@Test
	public final void testApplyTransaction() throws Exception {
		
		//EasyMock.replay(portfolio);
		//EasyMock.replay(portfolioShare1);
		//EasyMock.replay(portfolioShare2);
		
//		Assert.assertEquals(new BigDecimal(155).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(33).setScale(2),portfolio.getTotalOutAmountEver());
//		
//		Transaction transaction = 
//			new Transaction(portfolioShare1.getCashin(),portfolioShare1.getCashout(),BigDecimal.ONE,new BigDecimal(30),TransactionType.AOUT, new Date());
//		
//		portfolioShare1.applyTransaction(transaction, true);
//		
//		Assert.assertEquals(new BigDecimal(155).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(63).setScale(2),portfolio.getTotalOutAmountEver());
//		
//		Transaction transaction1 = new Transaction(portfolioShare2.getCashin(),portfolioShare2.getCashout(),new BigDecimal(2),new BigDecimal(30),TransactionType.AIN, new Date());
//		
//		portfolioShare1.applyTransaction(transaction1, true);
//		
//		Assert.assertEquals(new BigDecimal(215).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(63).setScale(2),portfolio.getTotalOutAmountEver());
//		
//		Transaction transaction3 = new Transaction(portfolioShare2.getCashin(),portfolioShare2.getCashout(),new BigDecimal(4),new BigDecimal(60),TransactionType.AOUT, new Date());
//		
//		portfolioShare1.applyTransaction(transaction3, true);
//		
//		Assert.assertEquals(new BigDecimal(215).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(303).setScale(2),portfolio.getTotalOutAmountEver());
	}

//	@Test
//	public final void testResetTransaction() throws Exception {
//		
//		Assert.assertEquals(new BigDecimal(155).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(33).setScale(2),portfolio.getTotalOutAmountEver());
//		
//		Transaction transaction = new Transaction(portfolioShare1.getCashin(),portfolioShare1.getCashout(),BigDecimal.ONE,new BigDecimal(30),TransactionType.AOUT, new Date());
//		
//		//portfolioShare1.resetTransaction(transaction);
//		
//		Assert.assertEquals(new BigDecimal(85).setScale(2),portfolio.getTotalInAmountEver());
//		Assert.assertEquals(new BigDecimal(11).setScale(2),portfolio.getTotalOutAmountEver());
//		
//	}
	
	public void testRealizedProfit() {
		
		//portfolioShare3.getR
	}
}
