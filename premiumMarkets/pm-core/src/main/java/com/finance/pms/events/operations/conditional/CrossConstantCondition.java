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
package com.finance.pms.events.operations.conditional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.TargetStockInfo;
import com.finance.pms.events.operations.Value;
import com.finance.pms.events.operations.nativeops.DoubleMapValue;
import com.finance.pms.events.operations.nativeops.NumberValue;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.events.scoring.functions.LeftShifter;

/**
 * 
 * @author Guillaume Thoreton
 * Additional constraints :
 * 'over'
 * does not make sense : 'for'. As the condition is an event in time not a status in time.
 * 'spanning'
 */

@XmlSeeAlso({CrossUpConstantCondition.class, CrossDownConstantCondition.class, DownRatioCondition.class, UpRatioCondition.class})
public abstract class CrossConstantCondition extends Condition<Double> {
	
	@SuppressWarnings("unused")
	private CrossConstantCondition() {
		super();
	}
	
	public CrossConstantCondition(String reference, String description, Operation... operands) {
		super(reference, description, new ArrayList<Operation>(Arrays.asList(operands)));
	}

	@Override
	public BooleanMapValue calculate(TargetStockInfo targetStock, @SuppressWarnings("rawtypes") List<? extends Value> inputs) {
		
		Double constant = ((NumberValue) inputs.get(0)).getValue(targetStock).doubleValue();
		Integer spanningShift = ((NumberValue) inputs.get(1)).getValue(targetStock).intValue();
		Integer overPeriod = ((NumberValue) inputs.get(2)).getValue(targetStock).intValue();
		Integer forPeriod = ((NumberValue) inputs.get(3)).getValue(targetStock).intValue();
		SortedMap<Date, Double> data = ((DoubleMapValue) inputs.get(4)).getValue(targetStock);
		
		if (spanningShift == 0) spanningShift = 1;
		LeftShifter<Double> rightShifter = new LeftShifter<Double>(-spanningShift.intValue(), false, false);
		SortedMap<Date, Double> rightShiftedData = rightShifter.shift(data);
		
		BooleanMapValue outputs = new  BooleanMapValue();
		BooleanMapValue underLyingRealOuts = new BooleanMapValue();

		for (Date date : data.keySet()) {
			Double current = data.get(date);
			Double previous = rightShiftedData.get(date);
			if (previous != null && !previous.isNaN()) {
				@SuppressWarnings("unchecked")
				Boolean conditionCheck = conditionCheck(previous, current, constant);
				if (conditionCheck != null) {
					
					if ((overPeriod == 0 || outputs.getValue(targetStock).get(date) == null)) {
						
						underLyingRealOuts.getValue(targetStock).put(date, conditionCheck);
						
						if (conditionCheck && forPeriod > 0) {
							
							Calendar startForPeriodCal = Calendar.getInstance();
							startForPeriodCal.setTime(date);
							QuotationsFactories.getFactory().incrementDate(startForPeriodCal, -forPeriod-1);
							Date startForPeriod = startForPeriodCal.getTime();
							
							SortedMap<Date, Boolean> forPeriodData = underLyingRealOuts.getValue(targetStock).subMap(startForPeriod, date);
							if (startForPeriod.before(data.firstKey())) {
								conditionCheck = null;
							} else {
								for (Boolean previousValue : forPeriodData.values()) {
									conditionCheck = conditionCheck && previousValue;
									if (!previousValue) break;
								}
							}
						}
						
						if (conditionCheck != null) outputs.getValue(targetStock).put(date, conditionCheck);
						
					}
					
					if (conditionCheck != null && conditionCheck && overPeriod > 0) {
						Calendar endOverPeriodCal = Calendar.getInstance();
						endOverPeriodCal.setTime(date);
						QuotationsFactories.getFactory().incrementDate(endOverPeriodCal, +overPeriod+1);
						Date endOverPeriod = (endOverPeriodCal.after(data.lastKey()))?data.lastKey():endOverPeriodCal.getTime();
						SortedMap<Date, Double> overPeriodData = data.subMap(date, endOverPeriod);
						for (Date overPeriodDate : overPeriodData.keySet()) {
							outputs.getValue(targetStock).put(overPeriodDate, conditionCheck);
						}
					}
					
				}
			}
		}
		
		return outputs;
	}

	public int mainInputPosition() {
		return 4;
	}
	
	@Override
	public int operationStartDateShift() {
		int maxDateShift = getOperands().get(mainInputPosition()).operationStartDateShift();
		for (int i = 1; i < mainInputPosition(); i++) {
			maxDateShift = maxDateShift + getOperands().get(i).operationStartDateShift();
		}
		return maxDateShift;
	}
}
