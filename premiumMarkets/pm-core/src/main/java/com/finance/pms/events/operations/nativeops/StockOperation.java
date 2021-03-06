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
package com.finance.pms.events.operations.nativeops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.TargetStockInfo;
import com.finance.pms.events.quotations.QuotationDataType;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.events.quotations.Quotations.ValidityFilter;
import com.finance.pms.events.quotations.QuotationsFactories;


@XmlRootElement
public class StockOperation extends DoubleMapOperation {
	

	protected static MyLogger LOGGER = MyLogger.getLogger(StockOperation.class);

	public StockOperation() {
		super("stock", "Time series of real stock historical data (close, low, high and volume))");
		setAvailableOutputSelectors(new ArrayList<String>( Arrays.asList(new String[]{"close","open","high","low","volume"})));
	}
	
	public StockOperation(ArrayList<Operation> operands, String outputSelector) {
		this();
		this.setOperands(operands);
		this.setOutputSelector(outputSelector);
	}

	@Override
	public DoubleMapValue run(TargetStockInfo targetStock) {
		
		String outputSelector = getOutputSelector();
		QuotationDataType targetStockInputType = QuotationDataType.CLOSE;
		if (outputSelector != null) {
			targetStockInputType = QuotationDataType.valueOf(outputSelector.toUpperCase());
		}
		 
		SortedMap<Date, Double> buildSMapFromQuotations = new TreeMap<Date, Double>();
		try {
			switch(targetStockInputType) {
			case CLOSE :
				{
					Quotations quotationsInstance = QuotationsFactories.getFactory().getQuotationsInstance(targetStock.getStock(), targetStock.getStartDate(), targetStock.getEndDate(), true, null, 1, ValidityFilter.CLOSE);
					buildSMapFromQuotations = QuotationsFactories.getFactory().buildExactSMapFromQuotations(quotationsInstance, targetStockInputType, quotationsInstance.getFirstDateShiftedIdx(), quotationsInstance.getLastDateIdx());
					break;
				}
			case HIGH :
			case LOW :
			case OPEN :
				{
					Quotations quotationsInstance = QuotationsFactories.getFactory().getQuotationsInstance(targetStock.getStock(), targetStock.getStartDate(), targetStock.getEndDate(), true, null, 1, ValidityFilter.OHLC);
					buildSMapFromQuotations = QuotationsFactories.getFactory().buildExactSMapFromQuotations(quotationsInstance, targetStockInputType, quotationsInstance.getFirstDateShiftedIdx(), quotationsInstance.getLastDateIdx());
					break;
				}
			case VOLUME :
				{
					Quotations quotationsInstance = QuotationsFactories.getFactory().getQuotationsInstance(targetStock.getStock(), targetStock.getStartDate(), targetStock.getEndDate(), true, null, 1, ValidityFilter.VOLUME);
					buildSMapFromQuotations = QuotationsFactories.getFactory().buildExactSMapFromQuotations(quotationsInstance, targetStockInputType, quotationsInstance.getFirstDateShiftedIdx(), quotationsInstance.getLastDateIdx());
					break;
				}
			default :
				{
					Quotations quotationsInstance = QuotationsFactories.getFactory().getQuotationsInstance(targetStock.getStock(), targetStock.getStartDate(), targetStock.getEndDate(), true, null, 1, ValidityFilter.OHLCV);
					buildSMapFromQuotations = QuotationsFactories.getFactory().buildExactSMapFromQuotations(quotationsInstance, targetStockInputType, quotationsInstance.getFirstDateShiftedIdx(), quotationsInstance.getLastDateIdx());
				}
			}

		} catch (Exception e) {
			LOGGER.error(e,e);
		} 
		
		return new DoubleMapValue(buildSMapFromQuotations);
	}

	@Override
	public int operationStartDateShift() {
		return 0;
	}
	
}
