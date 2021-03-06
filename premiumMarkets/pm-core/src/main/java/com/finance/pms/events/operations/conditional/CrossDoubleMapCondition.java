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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.TargetStockInfo;
import com.finance.pms.events.operations.Value;
import com.finance.pms.events.operations.nativeops.DoubleMapOperation;
import com.finance.pms.events.operations.nativeops.DoubleMapValue;
import com.finance.pms.events.operations.nativeops.NumberOperation;
import com.finance.pms.events.operations.nativeops.NumberValue;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.events.scoring.functions.LeftShifter;

/**
 * 
 * @author Guillaume Thoreton
 * Additional constraints :
 * not implemented : 'over'
 * does not make sense : 'for'. As the condition is an event in time not a status in time.
 * 'spanning'
 */

@XmlSeeAlso({CrossUpDoubleMapCondition.class, CrossDownDoubleMapCondition.class})
public abstract class CrossDoubleMapCondition extends Condition<Double> implements OnSignalCondition {
	

	@SuppressWarnings("unused")
	private CrossDoubleMapCondition() {
		super();
	}

	public CrossDoubleMapCondition(String reference, String description) {
		super(reference, description, new NumberOperation("dates comparison span"),  new NumberOperation("time period over which it happens"), new DoubleMapOperation("'"+reference+ "' left operand (data)"), new DoubleMapOperation("'"+reference+ "' right operand (signal)"));
	}
	
	public CrossDoubleMapCondition(String reference, String description, ArrayList<Operation> operands) {
		this(reference, description);
		this.setOperands(operands);
	}

	@Override
	public BooleanMapValue calculate(TargetStockInfo targetStock, @SuppressWarnings("rawtypes") List<? extends Value> inputs) {
		
		Integer spanningShift = ((NumberValue) inputs.get(0)).getValue(targetStock).intValue();
		Integer overPeriod = ((NumberValue) inputs.get(1)).getValue(targetStock).intValue();
		SortedMap<Date, Double> firstOp = ((DoubleMapValue) inputs.get(2)).getValue(targetStock);
		SortedMap<Date, Double> secondOp = ((DoubleMapValue) inputs.get(3)).getValue(targetStock);
		
		SortedSet<Date> fullKeySet = new TreeSet<Date>();
		fullKeySet.addAll(firstOp.keySet());
		fullKeySet.addAll(secondOp.keySet());
		
		if (spanningShift == 0) spanningShift = 1;
		LeftShifter<Double> rightShifter = new LeftShifter<Double>(-spanningShift.intValue(), false, false);
		SortedMap<Date, Double> rightShiftedFirstOp = rightShifter.shift(firstOp);
		SortedMap<Date, Double> rightShiftedSecondOp = rightShifter.shift(secondOp);
		
		BooleanMapValue outputs = new  BooleanMapValue();

		for (Date date : fullKeySet) {
			Double firstV = firstOp.get(date);
			Double secondV = secondOp.get(date);
			Double previousFirstOp = rightShiftedFirstOp.get(date);
			Double previousSecondOp = rightShiftedSecondOp.get(date);
			if (previousFirstOp != null && !previousFirstOp.isNaN() && previousSecondOp != null && !previousSecondOp.isNaN())  {
				@SuppressWarnings("unchecked")
				Boolean conditionCheck = conditionCheck(previousFirstOp, firstV, previousSecondOp, secondV);
				
				if (conditionCheck != null) {
				
					if (overPeriod == 0 || outputs.getValue(targetStock).get(date) == null) {
						outputs.getValue(targetStock).put(date, conditionCheck);
					}
					
					if (conditionCheck && overPeriod > 0) {
						Calendar endOverPeriodCal = Calendar.getInstance();
						endOverPeriodCal.setTime(date);
						QuotationsFactories.getFactory().incrementDate(endOverPeriodCal, +overPeriod+1);
						Date endOverPeriod = (endOverPeriodCal.after(fullKeySet.last()))?fullKeySet.last():endOverPeriodCal.getTime();
						SortedSet<Date> overPeriodData = fullKeySet.subSet(date, endOverPeriod);
						for (Date overPeriodDate : overPeriodData) {
							outputs.getValue(targetStock).put(overPeriodDate, conditionCheck);
						}
					}
				}
			}
		}
		
		return outputs;
	}

	@Override
	public int mainInputPosition() {
		return 2;
	}

	@Override
	public int inputSignalPosition() {
		return 3;
	}
	
	@Override
	public int operationStartDateShift() {
		int maxDateShift = Math.max(getOperands().get(mainInputPosition()).operationStartDateShift(),getOperands().get(inputSignalPosition()).operationStartDateShift());
		for (int i = 0; i < mainInputPosition(); i++) {
			maxDateShift = maxDateShift + getOperands().get(i).operationStartDateShift();
		}
		return maxDateShift;
	}
}
