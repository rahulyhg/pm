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
package com.finance.pms.events.operations.nativeops;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.TargetStockInfo;
import com.finance.pms.events.operations.Value;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.talib.indicators.SMA;

@XmlRootElement
public class PMSMAOperation extends PMDataFreeOperation {
	
	protected static MyLogger LOGGER = MyLogger.getLogger(PMSMAOperation.class);
	
	public PMSMAOperation() {
		super("sma_", "House made SMA on close historical data", new NumberOperation("number","smaPeriod","Sma period", new NumberValue(200.0)));
	}
	
	public PMSMAOperation(ArrayList<Operation> operands, String outputSelector) {
		this();
		this.setOperands(operands);
		this.setOutputSelector(outputSelector);
	}
	
	
	@Override
	public DoubleMapValue calculate(TargetStockInfo targetStock, @SuppressWarnings("rawtypes") List<? extends Value> inputs) {

		//Param check
		Integer period = ((NumberValue) inputs.get(0)).getValue(targetStock).intValue();

		DoubleMapValue ret = new DoubleMapValue();
		try {
//			SMA sma = new SMA(targetStock.getStock(), period, targetStock.getStartDate(), targetStock.getEndDate(), null);
			SMA sma = new SMA(period);
			Quotations quotations = QuotationsFactories.getFactory().getQuotationsInstance(
					targetStock.getStock(), targetStock.getStartDate(), targetStock.getEndDate(), 
					true, targetStock.getStock().getMarketValuation().getCurrency(), 
					sma.getStartShift(), sma.quotationValidity());
			sma.calculateIndicator(quotations);
			
			
			return doubleArrayMapToDoubleMap(quotations, targetStock, sma, sma.getOutputData());

		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return ret;
	}

}
