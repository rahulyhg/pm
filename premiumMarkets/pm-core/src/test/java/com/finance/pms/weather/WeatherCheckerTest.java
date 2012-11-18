/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock market technical analysis
 * major indicators, portfolio management and historical data charting.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock market technical analysis and indexes rotation,
 * With around 80% of forecasted trades above buy and hold, while back testing over DJI, 
 * FTSE, DAX and SBF, Back testing, 
 * Buy sell email notifications with automated markets and user defined portfolios scanning.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview and a free workable demo.
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
package com.finance.pms.weather;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.finance.pms.SpringContext;

public class WeatherCheckerTest extends TestCase {
	
	WeatherChecker testObject;
//	private SimpleDateFormat simpleDateFormat;
//	private Stock refStock;

	@Before
	public void setUp() throws Exception {
		
		SpringContext springContext = new SpringContext();
		springContext.setDataSource("/home/guil/Developpement/Quotes/pms/db.properties");
		springContext.loadBeans("/connexions.xml", "/swtclients.xml","/talibanalysisservices.xml");
		springContext.refresh();
		
//		simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//		refStock = AnalysisClient.REF_STOCK;
		
	}
	
	@Test
	public void test() {
		Assert.assertTrue(true);
	}

	//FIXME
//	@Test
//	public final void testPreCalculateEvents() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("29/08/2011"), simpleDateFormat.parse("01/09/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(4,preCalculateEvents.size());
//		
//	}
//	
//	@Test
//	public final void testPreCalculateEvents2() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("29/08/2011"), simpleDateFormat.parse("02/09/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(4,preCalculateEvents.size());
//		
//	}
//	
//	@Test
//	public final void testPreCalculateEvents3() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("01/08/2011"), simpleDateFormat.parse("05/08/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(2,preCalculateEvents.size());
//		
//	}
//	
//	@Test
//	public final void testPreCalculateEvents4() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("05/08/2011"), simpleDateFormat.parse("05/09/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(4,preCalculateEvents.size());
//		
//	}
//	
//	@Test
//	public final void testPreCalculateEvents5() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("05/07/2011"), simpleDateFormat.parse("01/08/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(4,preCalculateEvents.size());
//		
//	}
//	
//	@Test
//	public final void testPreCalculateEvents6() throws ParseException, NotEnoughDataException {
//		
//		refStock.setLastQuote(simpleDateFormat.parse("01/09/2011"));
//		
//		testObject = new WeatherChecker(refStock, simpleDateFormat.parse("28/09/2011"), simpleDateFormat.parse("01/12/2011"), Currency.EUR);
//	
//		Map<EventKey, EventValue> preCalculateEvents = testObject.preCalculatedEventData;
//		
//		System.out.println(preCalculateEvents);
//		assertEquals(2,preCalculateEvents.size());
//		
//	}
}
