package com.finance.pms.events.operations.conditional;

import java.util.ArrayList;

import com.finance.pms.events.operations.Operation;


public class EqualDoubleMapCondition extends CmpDoubleMapCondition {
	

	private EqualDoubleMapCondition() {
		super("historical equality", "True when the first time series value is equal the second one.");
	}
	
	public EqualDoubleMapCondition(ArrayList<Operation> operands, String outputSelector) {
		this();
		setOperands(operands);
	}

	@Override
	public Boolean conditionCheck(@SuppressWarnings("unchecked") Comparable<Double>... ops) {
		return ops[0].compareTo((Double) ops[1])  == 0;
	}

}
