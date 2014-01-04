
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


package com.finance.pms.events.calculation;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.SortedMap;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventInfo;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.events.calculation.houseIndicators.HouseAroon;
import com.finance.pms.events.quotations.NoQuotationsException;
import com.finance.pms.talib.dataresults.StandardEventKey;
import com.finance.pms.talib.indicators.FormulatRes;
import com.finance.pms.talib.indicators.SMA;
import com.finance.pms.talib.indicators.StochasticOscillator;
import com.finance.pms.talib.indicators.TalibException;
import com.finance.pms.talib.indicators.TalibIndicator;

public class StochasticDivergence_old extends TalibIndicatorsCompositionCalculator {
	
	private StochasticOscillator stochOsc;
	private Integer stochQuotationStartDateIdx;
	
	private SMA sma;
	private Integer smaQuotationStartDateIdx;
	
	public StochasticDivergence_old(EventInfo eventInfo, Stock stock, Date startDate, Date endDate, Currency calculationCurrency, String analyseName, Boolean persistTrainingEvents, Observer... observers) throws NotEnoughDataException, TalibException, NoQuotationsException {
//		fastKLookBackPeriod = 14;
//		slowKSmaPeriod = 3;
//		slowDSmaPeriod = 3;
		this(stock, new StochasticOscillator(stock, startDate, endDate, calculationCurrency, 14, 3, 3), new HouseAroon(stock,  startDate, endDate, calculationCurrency, 25) , startDate,endDate,calculationCurrency);
	}

	public StochasticDivergence_old(Stock stock, StochasticOscillator stochasticOscillator, HouseAroon aroon, Date startDate, Date endDate, Currency calculationCurrency) throws NotEnoughDataException {
		super(stock, startDate, endDate, calculationCurrency);
		
		this.stochOsc = stochasticOscillator;
		stochQuotationStartDateIdx = stochasticOscillator.getIndicatorQuotationData().getClosestIndexForDate(0, startDate);
		Integer stochQuotationEndDateIdx = stochasticOscillator.getIndicatorQuotationData().getClosestIndexForDate(stochQuotationStartDateIdx, endDate);
		isValidData(stock, stochasticOscillator, startDate, stochQuotationStartDateIdx, stochQuotationEndDateIdx);
		
		try {
			this.sma = new SMA(stock, 2, startDate, endDate, calculationCurrency, Math.max(20, getDaysSpan()), 0);
		} catch (TalibException e) {
			throw new NotEnoughDataException(stock, e.getMessage(),e);
		} catch (NoQuotationsException e) {
			throw new NotEnoughDataException(stock, e.getMessage(),e);
		}
		
		smaQuotationStartDateIdx = sma.getIndicatorQuotationData().getClosestIndexForDate(0, startDate);
		Integer smaQuotationEndDateIdx = sma.getIndicatorQuotationData().getClosestIndexForDate(smaQuotationStartDateIdx, endDate);
		isValidData(stock, sma, startDate, smaQuotationStartDateIdx, smaQuotationEndDateIdx);
	
	}

	@Override
	protected FormulatRes eventFormulaCalculation(Integer calculatorIndex) throws InvalidAlgorithmParameterException {
	
		FormulatRes res = new FormulatRes(getEventDefinition());
		res.setCurrentDate(this.getCalculatorQuotationData().getDate(calculatorIndex));
		
		int stochIdx = getIndicatorIndexFromCalculatorQuotationIndex(this.stochOsc, calculatorIndex, stochQuotationStartDateIdx);
		double[] stochLookBackP = Arrays.copyOfRange(this.stochOsc.getSlowK(), stochIdx - getDaysSpan(), stochIdx);
		double[] quotationLookBackP = Arrays.copyOfRange(this.getCalculatorQuotationData().getCloseValues(), calculatorIndex - getDaysSpan(), calculatorIndex);
		
		int smaIndex = getIndicatorIndexFromCalculatorQuotationIndex(this.sma, calculatorIndex, smaQuotationStartDateIdx);
		double[] quotationLookBackPThresh = Arrays.copyOfRange(this.sma.getSma(), smaIndex - getDaysSpan(), smaIndex);
		
		double[] lowThreshLookBackP = new double[getDaysSpan()];
		for (int i = 0; i < lowThreshLookBackP.length; i++) {
			lowThreshLookBackP[i] = this.stochOsc.getLowerThreshold();
			
		}
		
		double[] upperThreshLookBackP = new double[getDaysSpan()];
		for (int i = 0; i < upperThreshLookBackP.length; i++) {
			upperThreshLookBackP[i] = this.stochOsc.getUpperThreshold();
			
		}
		
		{
			Boolean isPriceDown = lowerLow(quotationLookBackP, quotationLookBackPThresh);
			Boolean isStochUp = higherLow(stochLookBackP, lowThreshLookBackP);
			res.setBullishCrossOver(isPriceDown && isStochUp); 
			
			if (res.getBullishCrossOver()) return res;

		}
		{
			Boolean isPriceUp = higherHigh(quotationLookBackP, quotationLookBackPThresh);
			Boolean isStochDown = lowerHigh(stochLookBackP, upperThreshLookBackP);
			res.setBearishCrossBellow(isPriceUp && isStochDown);
		
			return res;
		}
		
	}
	
	@Override
	protected Boolean isInDataRange(TalibIndicator indicator, Integer indicatorIndex) {
		if (indicator instanceof HouseAroon) return this.isInDataRange((HouseAroon)indicator, indicatorIndex);
		if (indicator instanceof StochasticOscillator) return this.isInDataRange((StochasticOscillator)indicator, indicatorIndex);
		if (indicator instanceof SMA) return this.isInDataRange((SMA)indicator, indicatorIndex);
		throw new RuntimeException("Booo",new Throwable());
	}

	private Boolean isInDataRange(StochasticOscillator indicator, Integer indicatorIndex) {
		return (getDaysSpan() < indicatorIndex) && (indicatorIndex < this.stochOsc.getSlowK().length);
		
	}
	
	private boolean isInDataRange(SMA sma, Integer index) {
		return (getDaysSpan() < index && index < sma.getSma().length);
	}
	
	private boolean isInDataRange(HouseAroon aroon, Integer index) {
		return (0 < index && index < aroon.getOutAroonUp().length);
	}

	@Override
	protected String getHeader(List<Integer> scoringSmas) {
		String head = "CALCULATOR DATE, CALCULATOR QUOTE, STOCH DATE, LOW TH, UP TH, STOCH SLOW K, STOCH SLOW D , bearish, bullish";
		head = addScoringHeader(head, scoringSmas);
		return head+"\n";	
	}

	@Override
	protected String buildLine(int calculatorIndex, Map<EventKey, EventValue> edata, List<SortedMap<Date, double[]>> linearsExpects) {
		
		Date calculatorDate = this.getCalculatorQuotationData().get(calculatorIndex).getDate();
		EventValue bearishEventValue = edata.get(new StandardEventKey(calculatorDate, getEventDefinition(), EventType.BEARISH));
		EventValue bullishEventValue = edata.get(new StandardEventKey(calculatorDate, getEventDefinition(), EventType.BULLISH));
		BigDecimal calculatorClose = this.getCalculatorQuotationData().get(calculatorIndex).getClose();
		
		int stochIndex = getIndicatorIndexFromCalculatorQuotationIndex(this.stochOsc, calculatorIndex, stochQuotationStartDateIdx);
		int stochQuotationIndex = getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculatorIndex, stochQuotationStartDateIdx);
		
		String line =
			new SimpleDateFormat("yyyy-MM-dd").format(calculatorDate) + "," +calculatorClose + ","  
			+ this.stochOsc.getIndicatorQuotationData().get(stochQuotationIndex).getDate() + ","
			+ this.stochOsc.getLowerThreshold() + ","
			+ this.stochOsc.getUpperThreshold() + ","
			+ this.stochOsc.getSlowK()[stochIndex] + ","
			+ this.stochOsc.getSlowD()[stochIndex];
		
		if (bearishEventValue != null) {
			line = line + ","+calculatorClose+",0,";
		} else if (bullishEventValue != null) {
			line = line + ",0,"+calculatorClose+",";
		} else {
			line = line + ",0,0,";
		}
		
		line = addScoringLinesElement(line, calculatorDate, linearsExpects)+"\n";
		
		return line;
	}
	
	@Override
	protected double[] buildOneOutput(int calculatorIndex) {
			
		int stochIndex = getIndicatorIndexFromCalculatorQuotationIndex(this.stochOsc, calculatorIndex, stochQuotationStartDateIdx);
		return new double[]
				{
				this.stochOsc.getSlowK()[stochIndex],
				this.stochOsc.getSlowD()[stochIndex],
				this.stochOsc.getLowerThreshold(),
				this.stochOsc.getUpperThreshold(),
				};
	}

	@Override
	protected int getDaysSpan() {
		return 60;
	}

	@Override
	public EventDefinition getEventDefinition() {
		return EventDefinition.PMSSTOCHDIVERGENCEOLD;
	}

}
