package com.finance.pms.events.calculation;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.events.scoring.functions.HighLowSolver;
import com.finance.pms.talib.dataresults.StandardEventKey;
import com.finance.pms.talib.indicators.FormulatRes;
import com.finance.pms.talib.indicators.TalibIndicator;

public abstract class OscillatorDivergenceCalculator extends TalibIndicatorsCompositionCalculator {
	
	protected SortedMap<Integer,Double> higherLows;
	protected SortedMap<Integer,Double> lowerHighs;
	private HighLowSolver highLowSolver;

	public OscillatorDivergenceCalculator(Stock stock, Date startDate, Date endDate, Currency calculationCurrency) throws NotEnoughDataException {
		super(stock, startDate, endDate, calculationCurrency);
		
		highLowSolver = new HighLowSolver();
		higherLows = new TreeMap<Integer, Double>();
		lowerHighs = new TreeMap<Integer, Double>();
	}

	@Override
	protected FormulatRes eventFormulaCalculation(Integer calculatorIndex) throws InvalidAlgorithmParameterException {
		
		FormulatRes res = new FormulatRes((EventDefinition) getEventDefinition());
		res.setCurrentDate(this.getCalculatorQuotationData().getDate(calculatorIndex));
		
		Calendar currentDateCal = Calendar.getInstance();
		currentDateCal.setTime(res.getCurrentDate());
		Date lookBackPeriodStart = QuotationsFactories.getFactory().incrementDate(currentDateCal, -getDaysSpan()).getTime();
		int lookBackPeriodStartIdx = this.getCalculatorQuotationData().getClosestIndexForDate(0, lookBackPeriodStart);
		int idxSpan = calculatorIndex - lookBackPeriodStartIdx;
		
		if (idxSpan < 4) return res; //We need a least 3 days to draw higher low or lower high
		
		int mfiIdx = getIndicatorIndexFromCalculatorQuotationIndex(getOscillator(), calculatorIndex, getOscillatorQuotationStartDateIdx());
		Double[] mfiLookBackP = ArrayUtils.toObject(Arrays.copyOfRange(getOscillatorOutput(), mfiIdx - idxSpan, mfiIdx));
	
		{
			Boolean isPriceDown = this.getCalculatorQuotationData().get(calculatorIndex - idxSpan).getClose().compareTo(this.getCalculatorQuotationData().get(calculatorIndex).getClose()) > 0;
			Boolean isMfiUp = false;
			Boolean isMfiDownThreshold = false;
			if (isPriceDown) {

				isMfiDownThreshold = isOscBelowLowerThreshold(idxSpan, mfiIdx);
				
				if (isMfiDownThreshold) {
					
					ArrayList<Double> regline = new ArrayList<Double>();
					isMfiUp = highLowSolver.higherLow(mfiLookBackP,new Double[0],regline);
					
					if (isMfiUp) addReglineOutput(higherLows, calculatorIndex, regline, idxSpan);
				}
				
			}
			
			res.setBullishCrossOver(isPriceDown && isMfiDownThreshold && isMfiUp); 
			if (res.getBullishCrossOver()) {
				return res;
			}
			
		}
		{
			Boolean isPriceUp = this.getCalculatorQuotationData().get(calculatorIndex - idxSpan).getClose().compareTo(this.getCalculatorQuotationData().get(calculatorIndex).getClose()) < 0;
			Boolean isMfiUpThreshold = false;
			Boolean isMfiDown = false;
			if (isPriceUp) {
			
				isMfiUpThreshold = isOcsAboveUpperThreshold(idxSpan, mfiIdx);
	
				if (isMfiUpThreshold) {
					
					ArrayList<Double> regline = new ArrayList<Double>();
					isMfiDown = highLowSolver.lowerHigh(mfiLookBackP, new Double[0], regline);
					
					if (isMfiDown) addReglineOutput(lowerHighs, calculatorIndex, regline, idxSpan);
					
				}
			}
			
			res.setBearishCrossBellow(isPriceUp && isMfiDown && isMfiUpThreshold);
		
			return res;
		}
	}

	protected Boolean isOcsAboveUpperThreshold(int idxSpan, int mfiIdx) {
		for (int i = mfiIdx - idxSpan; i < mfiIdx; i++) {
			if (getOscillatorOutput()[i] >= getOscillatorUpperThreshold()) {
				return true;
			}
		}
		return false;
	}

	protected Boolean isOscBelowLowerThreshold(int idxSpan, int mfiIdx) {
		for (int i = mfiIdx - idxSpan; i < mfiIdx; i++) {
			if (getOscillatorOutput()[i] <= getOscillatorLowerThreshold()) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract double getOscillatorLowerThreshold();
	protected abstract double getOscillatorUpperThreshold();

	protected abstract Integer getOscillatorQuotationStartDateIdx();

	protected abstract double[] getOscillatorOutput();

	protected abstract TalibIndicator getOscillator();	

	@Override
	protected String buildLine(int calculatorIndex, Map<EventKey, EventValue> edata, List<SortedMap<Date, double[]>> linearsExpects) {
		Date calculatorDate = this.getCalculatorQuotationData().get(calculatorIndex).getDate();
		EventValue bearishEventValue = edata.get(new StandardEventKey(calculatorDate,getEventDefinition(),EventType.BEARISH));
		EventValue bullishEventValue = edata.get(new StandardEventKey(calculatorDate,getEventDefinition(),EventType.BULLISH));
		BigDecimal calculatorClose = this.getCalculatorQuotationData().get(calculatorIndex).getClose();
		int mfiQuotationIndex = getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculatorIndex,getOscillatorQuotationStartDateIdx());
		double mfiV = getOscillatorOutput()[getIndicatorIndexFromCalculatorQuotationIndex(getOscillator(), calculatorIndex, getOscillatorQuotationStartDateIdx())];
		String thresholdString = printThresholdsCSV();
		String line =
			new SimpleDateFormat("yyyy-MM-dd").format(calculatorDate) + "," + calculatorClose + "," + 
			getOscillator().getIndicatorQuotationData().get(mfiQuotationIndex).getDate() + "," + getOscillator().getIndicatorQuotationData().get(mfiQuotationIndex).getClose() + "," + 
			((this.higherLows.get(mfiQuotationIndex)!=null)?mfiV:"") + "," + ((this.lowerHighs.get(mfiQuotationIndex)!=null)?mfiV:"") + "," + 
			thresholdString + "," + mfiV;
		
		if (bearishEventValue != null) {
			line = line + "," + calculatorClose +",0,";
		} else if (bullishEventValue != null){
			line = line + ",0," + calculatorClose +",";
		} else {
			line = line + ",0,0,";
		}
		
		line = addScoringLinesElement(line, calculatorDate, linearsExpects)+"\n";
		
		return line;
	}

	protected String printThresholdsCSV() {
		return getOscillatorLowerThreshold() + "," + getOscillatorUpperThreshold();
	}
	
	protected void addReglineOutput(SortedMap<Integer, Double> reglines, Integer calculatorIndex, ArrayList<Double> regline, Integer idxSpan) {

//		int overlapFromKey = calculatorIndex-idxSpan-1;
//		if (reglines.isEmpty() || (reglines.lastKey() < overlapFromKey)) {
//			reglines.put(calculatorIndex - idxSpan, regline.get(0));
//			reglines.put(calculatorIndex, regline.get(regline.size()-1));
//			for (int i = calculatorIndex - idxSpan+1; i < calculatorIndex; i++) {
//				reglines.put(i, Double.NaN);
//			}
//		}
		
		int gap = 0;
		int overlapFromKey = calculatorIndex-idxSpan;
		if (!reglines.isEmpty()) {
			while ( reglines.lastKey() >= overlapFromKey -1 && gap < regline.size()-1) {
				overlapFromKey++;
				gap++;
			}
		}
		if (gap < regline.size() -1) {
			reglines.put(overlapFromKey, regline.get(gap));
			reglines.put(calculatorIndex, regline.get(regline.size()-1));
			for (int i = overlapFromKey + 1; i < calculatorIndex; i++) {
				reglines.put(i, Double.NaN);
			}
		}
			
	}

}
