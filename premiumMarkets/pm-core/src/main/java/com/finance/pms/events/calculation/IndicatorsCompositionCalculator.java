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
package com.finance.pms.events.calculation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.talib.dataresults.StandardEventKey;
import com.finance.pms.talib.dataresults.StandardEventValue;
import com.finance.pms.talib.indicators.FormulatRes;
import com.finance.pms.talib.indicators.TalibIndicator;

public abstract class IndicatorsCompositionCalculator extends EventCompostionCalculator {
	
	private static MyLogger LOGGER = MyLogger.getLogger(IndicatorsCompositionCalculator.class);
	
	public IndicatorsCompositionCalculator(Stock stock, Date startDate, Date endDate, Currency calculationCurrency) throws NotEnoughDataException {
		super(stock, startDate, endDate, calculationCurrency);
	}

	public IndicatorsCompositionCalculator(Stock stock, Date startDate, Date endDate, Currency transactionCurrency, int calculatorIndexShift) throws NotEnoughDataException {
		super(stock, startDate, endDate, transactionCurrency, calculatorIndexShift);
	}

	@Override
	public Map<EventKey, EventValue> calculateEventsFor(String eventListName) {
		
		Map<EventKey, EventValue> edata = new HashMap<EventKey, EventValue>();
		
		FormulatRes res;
		try {
			for (int quotationIndex = calculationStartIdx; quotationIndex <= calculationEndIdx ; quotationIndex++) {
				res = eventFormulaCalculation(quotationIndex);
				res.setCurrentDate(this.getCalculatorQuotationData().getDate(quotationIndex));
				
				if (res.formulaTrend() != 0) {
					Date current = res.getCurrentDate();
					EventType eventType = EventType.valueOf(res.formulaTrend() + 1);
					StandardEventKey iek = new StandardEventKey(current, res.getEventDefinition(), eventType);
					EventValue iev = new StandardEventValue(current, res.getEventDefinition(), eventType, eventListName);
					edata.put(iek, iev);
				}
			}
			
		} catch (InvalidAlgorithmParameterException e) {
			LOGGER.error("",e);
		} finally {
			if (LOGGER.isDebugEnabled()) {
				exportToCSV(edata, eventListName);
			}
		}
		
		return edata;
		
	}

	protected abstract FormulatRes eventFormulaCalculation(Integer quotationIdx) throws InvalidAlgorithmParameterException;
	
	
	/**
	 * @param stock
	 * @param indicator
	 * @param startDate
	 * @param indicatorQuotationEndDateIdx
	 * @throws NotEnoughDataException
	 */
	protected void isValidData(Stock stock, TalibIndicator indicator, Date startDate, Integer indicatorQuotationStartDateIdx, Integer indicatorQuotationEndDateIdx) 
																																							throws NotEnoughDataException {
		if (indicator.getOutBegDate() == null) {
			throw new NotEnoughDataException(null, null, indicator.toString(),new Throwable());
		}
		if ((indicatorQuotationEndDateIdx - indicatorQuotationStartDateIdx) > indicator.getOutNBElement().value 
				|| indicator.getOutBegDate().after(startDate) 
				|| !isInDataRange(indicator,getIndicatorIndexFromCalculatorQuotationIndex(indicator, calculationStartIdx, indicatorQuotationStartDateIdx)) 
				|| !isInDataRange(indicator,getIndicatorIndexFromCalculatorQuotationIndex(indicator, calculationEndIdx, indicatorQuotationStartDateIdx))) {
			String message = "Not enought quotations for calculating indicator "+ indicator.getClass().getSimpleName() +" for calculator "+ this.getClass().getSimpleName() + " for " + stock.getName() + "!\n "
					+ " \t\tCalculation start date requested from "+ new SimpleDateFormat("yyyy-MM-dd").format(startDate)+ ". Indicator first date availabe : "+indicator.getOutBegDate()+".\n"
					+ " \t\tIndicator indexes may be out of range : from "+ this.getIndicatorIndexFromCalculatorQuotationIndex(indicator, calculationStartIdx, indicatorQuotationStartDateIdx)+ " to "+ this.getIndicatorIndexFromCalculatorQuotationIndex(indicator, calculationEndIdx, indicatorQuotationStartDateIdx)+".\n"
					+ " \t\tNb indicator quotations requested : "+(indicatorQuotationEndDateIdx - indicatorQuotationStartDateIdx)+" And calculated "+ indicator.getOutNBElement().value+".\n"
					+ " \t\tAvailable quotations for indicator init : "+  getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculationStartIdx, indicatorQuotationStartDateIdx)+".\n"
					+ " \t\tIf you really want that one calculated, quotation data investigation is needed as there may be some gaps in the quotations data leading to insufficient amount of quotations for the indicator calculation.";
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(indicator.getOutBegDate());
			QuotationsFactories.getFactory().incrementDate(calendar, getDaysSpan());
			throw new NotEnoughDataException(calendar.getTime(), stock.getLastQuote(), message,new Throwable());
		}
	}
	
	
	protected abstract Boolean isInDataRange(TalibIndicator indicator, Integer indicatorIndex);

	/**
	 * @param calculatorQuotationIndex
	 * @param indicatorQuotationStartIdx 
	 * @return
	 */
	protected Integer getIndicatorIndexFromCalculatorQuotationIndex(TalibIndicator indicator, Integer calculatorQuotationIndex, Integer indicatorQuotationStartIdx) {
		int indicatorQuotationIndex = getIndicatorQuotationIndexFromCalculatorQuotationIndex(calculatorQuotationIndex, indicatorQuotationStartIdx);
		Integer indicatorIndex = indicatorQuotationIndex - indicator.getOutBegIdx().value;
		return indicatorIndex;
	}
	
	/**
	 * @param calculatorQuotastrionIndex
	 * @return
	 */
	protected int getIndicatorQuotationIndexFromCalculatorQuotationIndex(Integer calculatorQuotationIndex, Integer indicatorQuotationStartIdx) {
		int deltaFromCalulatorStartIdx = calculatorQuotationIndex - calculationStartIdx;
		int indicatorQuotationIndex = deltaFromCalulatorStartIdx + indicatorQuotationStartIdx;
		return indicatorQuotationIndex;
	}
	
	/**
	 * Export mac dto csv.
	 * 
	 * @param filename the filename
	 * 
	 * @author Guillaume Thoreton
	 * @param edata 
	 * @param eventListName 
	 */
	public void exportToCSV(Map<EventKey, EventValue> edata, String eventListName) {
		
		
		File export = 
				new File(
						System.getProperty("installdir") + File.separator + "tmp" + File.separator +
						this.stock.getName().replaceAll("[/\\*\\.\\?,;><|\\!\\(\\) ]", "_") + "_"+ this.getClass().getSimpleName() + "_" + eventListName +".csv");

		FileWriter fos = null;
		try {
			fos = new FileWriter(export);
			String header = getHeader();
			fos.write(header);
			for (int i = calculationStartIdx; i <= calculationEndIdx; i++) {
				String line = buildLine(i, edata);
				fos.write(line);
			}
			fos.flush();
			
		} catch (Exception e) {
			LOGGER.error("", e);
		}  finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
		}
	}

	protected  abstract String getHeader();

	protected abstract String buildLine(int calculatorIndex, Map<EventKey, EventValue> edata);
	
	

}
