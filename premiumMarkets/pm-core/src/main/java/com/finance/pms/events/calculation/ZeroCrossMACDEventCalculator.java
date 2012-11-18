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
package com.finance.pms.events.calculation;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.talib.dataresults.StandardEventKey;
import com.finance.pms.talib.indicators.FormulatRes;
import com.finance.pms.talib.indicators.MACD;
import com.finance.pms.talib.indicators.SMA;
import com.finance.pms.talib.indicators.TalibIndicator;

public class ZeroCrossMACDEventCalculator extends TalibIndicatorsCompositionCalculator {
	
	private MACD macd;
	private Integer macdQuotationStartDateIdx;
//	private SMA sma;
//	private Integer smaQuotationStartDateIdx;
	
	public ZeroCrossMACDEventCalculator(Stock stock, MACD macd, SMA sma, Date startDate, Date endDate, Currency calculationCurrency) throws NotEnoughDataException {
		super(stock, startDate, endDate, calculationCurrency);
		
//		this.sma = sma;
//		smaQuotationStartDateIdx = sma.getIndicatorQuotationData().getClosestIndexForDate(0, startDate);
//		Integer smaQuotationEndDateIdx = sma.getIndicatorQuotationData().getClosestIndexForDate(smaQuotationStartDateIdx, endDate);
//		isValidData(stock, sma, startDate, smaQuotationStartDateIdx, smaQuotationEndDateIdx);
		
		this.macd = macd;
		macdQuotationStartDateIdx = macd.getIndicatorQuotationData().getClosestIndexForDate(0, startDate);
		Integer macdQuotationEndDateIdx = macd.getIndicatorQuotationData().getClosestIndexForDate(macdQuotationStartDateIdx, endDate);
		isValidData(stock, macd, startDate, macdQuotationStartDateIdx, macdQuotationEndDateIdx);
	}
	
	@Override
	protected FormulatRes eventFormulaCalculation(Integer calculatorIndex) throws InvalidAlgorithmParameterException {
		
		FormulatRes res = new FormulatRes(EventDefinition.PMMACDZEROCROSS);
		res.setCurrentDate(this.getCalculatorQuotationData().getDate(calculatorIndex));
		
//		Integer smaIndicatorIndex = getIndicatorIndexFromCalculatorQuotationIndex(this.sma, calculatorIndex, smaQuotationStartDateIdx);
		Integer macdIndicatorIndex = getIndicatorIndexFromCalculatorQuotationIndex(this.macd, calculatorIndex, macdQuotationStartDateIdx);
		
		{
			//BULL : MACD above its signal line over 2 days cross over 0
//			boolean isPriceAboveSMA = this.getCalculatorQuotationData().get(calculatorIndex).getClose().doubleValue() > sma.getSma()[smaIndicatorIndex];
			boolean isMacdAboveSignal = (macd.getSignal()[macdIndicatorIndex] < macd.getMacd()[macdIndicatorIndex]);	// 1rst macd > 1rst signal
			boolean isMacdCrossingAboveZero = ( macd.getMacd()[macdIndicatorIndex-1] < 0) && (0 < macd.getMacd()[macdIndicatorIndex]); //masc crossed over 0
			res.setBullishCrossOver(isMacdAboveSignal && isMacdCrossingAboveZero); 
			if (res.getBullishCrossOver()) return res;
		}
		
		{
			//BEAR : MACD below its signal line over 2 days cross below 0
//			boolean isPriceBelowSMA = this.getCalculatorQuotationData().get(calculatorIndex).getClose().doubleValue() < sma.getSma()[smaIndicatorIndex];
			boolean isMacdBelowSignal = (macd.getSignal()[macdIndicatorIndex] >  macd.getMacd()[macdIndicatorIndex]); 	// 1rst macd < 1rst signal
			boolean isMacdCrossingBelowZero = ( macd.getMacd()[macdIndicatorIndex-1] > 0) && (0 > macd.getMacd()[macdIndicatorIndex]); //masc crossed bellow 0
			res.setBearishCrossBellow(isMacdBelowSignal && isMacdCrossingBelowZero); 		
		}
		
		return res;
		
	}
	
	@Override
	public Boolean isInDataRange(TalibIndicator indicator, Integer indicatorIndex) {
		if (indicator instanceof SMA) return this.isInDataRange((SMA)indicator, indicatorIndex);
		if (indicator instanceof MACD) return this.isInDataRange((MACD)indicator, indicatorIndex);
		throw new RuntimeException("Booo",new Throwable());
	}
	
	private boolean isInDataRange(SMA sma, Integer index) {
		return (0 < index && index < sma.getSma().length);
	}

	private Boolean isInDataRange(MACD macd, Integer index) {
		return (getDaysSpan() < index && index < macd.getMacd().length);
	}

	@Override
	protected String getHeader(List<Integer> scoringSmas) {
		//return "CALCULATOR DATE, CALCULATOR QUOTE, SMA DATE, SMA QUOTE, SMA "+sma.getPeriod()+", MACD DATE, MACD QUOTE, MACD, SIGNAL,bearish, bullish\n";
		String head = "CALCULATOR DATE, CALCULATOR QUOTE, MACD DATE, MACD QUOTE, MACD, SIGNAL,bearish, bullish";
		head = addScoringHeader(head, scoringSmas);
		return head+"\n";	
	}

	@Override
	protected String buildLine(int calculatorIndex, Map<EventKey, EventValue> edata, List<SortedMap<Date, double[]>> linearsExpects) {
		Date calculatorDate = this.getCalculatorQuotationData().get(calculatorIndex).getDate();
		EventValue bearishEventValue = edata.get(new StandardEventKey(calculatorDate,EventDefinition.PMMACDZEROCROSS, EventType.BEARISH));
		EventValue bullishEventValue = edata.get(new StandardEventKey(calculatorDate,EventDefinition.PMMACDZEROCROSS, EventType.BULLISH));
		BigDecimal calculatorClose = this.getCalculatorQuotationData().get(calculatorIndex).getClose();
//		int smaQuotationIndex = getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculatorIndex,smaQuotationStartDateIdx);
		int macdQuotationIndex = getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculatorIndex,macdQuotationStartDateIdx);
		String line =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calculatorDate) + "," +calculatorClose + "," 
//			+ this.sma.getIndicatorQuotationData().get(smaQuotationIndex).getDate() + "," +this.sma.getIndicatorQuotationData().get(smaQuotationIndex).getClose() + "," 
//			+ this.sma.getSma()[getIndicatorIndexFromCalculatorQuotationIndex(this.sma, calculatorIndex, smaQuotationStartDateIdx)] +","
			+ this.macd.getIndicatorQuotationData().get(macdQuotationIndex).getDate()+ "," +this.macd.getIndicatorQuotationData().get(macdQuotationIndex).getClose() + ","
			+ this.macd.getMacd()[getIndicatorIndexFromCalculatorQuotationIndex(this.macd, calculatorIndex, macdQuotationStartDateIdx)]*100+"," 
			+ this.macd.getSignal()[getIndicatorIndexFromCalculatorQuotationIndex(this.macd, calculatorIndex, macdQuotationStartDateIdx)]*100;
		
		if (bearishEventValue != null) {
			line = line + ","+calculatorClose+",0,";
		} else if (bullishEventValue != null){
				line = line + ",0,"+calculatorClose+",";
		} else {
			line = line + ",0,0,";
		}
		
		line = addScoringLinesElement(line, calculatorDate, linearsExpects)+"\n";
		
		return line;
	}

	@Override
	protected int getDaysSpan() {
		return 10;
	}

	@Override
	public EventDefinition getEventDefinition() {
		return EventDefinition.PMMACDZEROCROSS;
	}
	
	
}
