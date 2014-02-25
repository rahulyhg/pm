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
package com.finance.pms.talib.indicators;

import java.text.SimpleDateFormat;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.quotations.QuotationUnit;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.events.quotations.Quotations.ValidityFilter;
import com.tictactec.ta.lib.RetCode;

/**
 * The Class MACD.
 * 
 * @author Guillaume Thoreton
 */
public class MACD extends TalibIndicator {

	protected static MyLogger LOGGER = MyLogger.getLogger(MACD.class);
	
	public Integer fastPeriod;
	public Integer slowPeriod;
	public Integer signalPeriod;
	
	
	private double[] macd;
	private double[] signal;
	private double[] history;

	public MACD(Integer fastPeriod, Integer slowPeriod, Integer signalPeriod) {
		super(fastPeriod, slowPeriod, signalPeriod);
		this.fastPeriod = fastPeriod;
		this.signalPeriod = signalPeriod;
		this.slowPeriod = slowPeriod;
	}


	@Override
	protected RetCode talibCall(Integer startIdx, Integer endIdx, double[][] inData, Number...indicatorParams) {
		RetCode rc = TalibCoreService.getCore().macd(startIdx, endIdx, inData[0], (Integer)indicatorParams[0], (Integer) indicatorParams[1],(Integer) indicatorParams[2], outBegIdx, outNBElement, macd, signal, history);
		return rc;
	}


	@Override
	protected void initResArray(int length) {
		macd = new double[length];
		signal = new double[length];
		history = new double[length];
	}

	@Override
	protected String getLine(Integer indicator, QuotationUnit qU) {
		String line =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
					qU.getDate()) + "," +
					qU.getClose() + "," +
					macd[indicator] + "," + signal[indicator]  + "," + history[indicator]  + "\n";
		return line;
	}

	@Override
	protected String getHeader() {
		String header = "DATE,QUOTE,MACD,SIGNAL,HISTORY\n";
		return header;
	}
	
	public double[] getMacd() {
		return macd;
	}

	public double[] getSignal() {
		return signal;
	}

	public double[] getHistory() {
		return history;
	}

	@Override
	protected double[][] getInputData(Quotations quotations) {
		double[] closeValues = quotations.getCloseValues();
		double[][] ret = new double[1][closeValues.length];
		ret[0]= closeValues;
		return 	ret;
	}

	@Override
	public double[] getOutputData() {
		return macd;
	}


	@Override
	public Integer getStartShift() {
		return Math.max(slowPeriod, fastPeriod) + signalPeriod +1;
	}


	@Override
	public ValidityFilter quotationValidity() {
		return ValidityFilter.CLOSE;
	}

}
