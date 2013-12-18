package com.finance.pms.events.calculation.parametrizedindicators;

import java.util.HashMap;
import java.util.Map;

import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.Value;
import com.finance.pms.events.operations.nativeops.NumberValue;
import com.finance.pms.portfolio.InfoObject;

public class ChartedOutputGroup {

	public enum Type {MAIN, SIGNAL, BOTH, CONSTANT, MULTI, MULTISIGNAL, INVISIBLE};
	
	public class OutputDescr implements InfoObject, Comparable<OutputDescr>{
		
		OutputReference outputReference;
		ChartedOutputGroup container;
		Type type;
		Integer outputIndex;
		Value<?> value;
		
		Boolean displayOnChart;
		
		public OutputDescr(OutputReference outputReference, ChartedOutputGroup container, Type type, Integer outputIndex, Value<?> value) {
			super();
			this.outputReference = outputReference;
			this.container = container;
			this.type = type;
			this.outputIndex = outputIndex;
			this.value = value;
			
			this.displayOnChart = true;
		}

		public Type getType() {
			return type;
		}

		public Integer getOutputIndex() {
			return outputIndex;
		}

		public Value<?> getValue() {
			return value;
		}

		public void setOutputIndex(Integer outputIndex) {
			this.outputIndex = outputIndex;
		}

		public String fullQualifiedName() {
			String discriminentReference = outputReference.getReference();
			if (outputReference.getIsLeaf()) {
				discriminentReference = value.getValueAsString();
			}
			
			String famillyName = outputReference.getOperationReference()+ ((outputReference.getOutputSelector() != null)?":"+outputReference.getOutputSelector():"");
			return discriminentReference + " (" + famillyName + ") displayed as " + outputReference.getReferenceAsOperand();
		}

		@Override
		public String toString() {
			return "OutputDescr [type=" + type + ", outputIndex=" + outputIndex + ", value=" + value + ", displayed="+displayOnChart+"]";
		}

		public ChartedOutputGroup getContainer() {
			return container;
		}

		public void maskType(Type newType) {
			
			switch (newType) {
			case MAIN :
				if (this.type.equals(Type.SIGNAL) || this.type.equals(Type.BOTH)) this.type = Type.BOTH;
				break;
			case SIGNAL :
				if (this.type.equals(Type.MAIN)  || this.type.equals(Type.BOTH)) this.type = Type.BOTH;
				break;
			default :
				this.type = newType;
			}
			
		}

		private void setContainer(ChartedOutputGroup container) {
			this.container = container;
		}

		public Boolean getDisplayOnChart() {
			return displayOnChart;
		}

		public void setDisplayOnChart(Boolean displayOnChart) {
			this.displayOnChart = displayOnChart;
		}

		@Override
		public String info() {
			return fullQualifiedName();
		}

		@Override
		public String tootTip() {
			//return fullQualifiedName();
			//return fullQualifiedName() + ((outputReference.getFormula() != null)?" "+outputReference.getFormula():"");
			return (outputReference.getFormula() != null)?outputReference.getFormula():fullQualifiedName();
		}

		@Override
		public int compareTo(OutputDescr o) {
//			if (outputName != null && o.outputName != null && !outputName.equals(o.outputName)) {
//				return outputName.compareTo(o.outputName);
//			} else {
//				return outputIndex.compareTo(o.outputIndex);
//			}
			return this.fullQualifiedName().compareTo(o.fullQualifiedName());
		}
	}
	
	OutputReference thisReference;
	OutputDescr thisDescription;
	Map<OutputReference, OutputDescr> components;
	
	//Non displayed group
	public ChartedOutputGroup(OutputReference outputReference, int outputIndex) {
		thisDescription = new OutputDescr(outputReference, this, Type.INVISIBLE, outputIndex, null);
		thisReference = outputReference;
		components = new HashMap<OutputReference, ChartedOutputGroup.OutputDescr>();
	}
	
	
	//Adding a main
	public ChartedOutputGroup(Operation operation, int outputIndex) {
		
//		String outputName = operation.getReference();
//		if (operation.getOutputSelector() != null) outputName = operation.getOutputSelector() + " ("+outputName+")";
		OutputReference outputReference = new OutputReference(operation);
		thisDescription = new OutputDescr(outputReference, this, Type.MAIN, outputIndex, null);
		thisReference = outputReference;
		components = new HashMap<OutputReference, ChartedOutputGroup.OutputDescr>();
	}
	
	public OutputDescr addSignal(Operation operation, int outputIndex) {
//		String outputName = operation.getReference();
//		if (operation.getOutputSelector() != null) outputName = operation.getOutputSelector() + " ("+outputName+")";
		OutputReference outputReference = new OutputReference(operation);
		OutputDescr outputDescr = new OutputDescr(outputReference, this, Type.SIGNAL, outputIndex, null);
		this.components.put(outputReference, outputDescr);
		return outputDescr;
	}

	public void addConstant(String parentReference, Operation operation, NumberValue doubleValue) {
//		String outputNameOverride = doubleValue.getNumberValue() +" ("+parentReference+" "+operation.getReferenceAsOperand()+")";
		//OutputReference outputReference = new OutputReference(operation);
		String referenceAsOperandOverride = parentReference+" "+operation.getReferenceAsOperand();
		OutputReference outputReference = new OutputReference(operation.getReference(), null, null, referenceAsOperandOverride, true, operation.getOperationReference()); 
		this.components.put(outputReference, new OutputDescr(outputReference, this, Type.CONSTANT, null, doubleValue));
	}
	
	public void addAdditonalOutput(String outputKey, Operation operation, int outputIndex, Type type) {
//		String outputName = outputKey + " ("+operation.getReference()+")";
//		if (operation.getOutputSelector() != null) outputName = operation.getOutputSelector() + " ("+outputName+")";
		OutputReference outputReference = new OutputReference(operation, outputKey);
		this.components.put(outputReference, new OutputDescr(outputReference, this, type, outputIndex, null));
	}
	
	public OutputDescr mvComponentInThisGrp(OutputReference outputRef, OutputDescr outputDescr) {
		outputDescr.setContainer(this);
		this.components.put(outputRef,outputDescr);
		return outputDescr;
	}

	public Map<OutputReference, OutputDescr> getComponents() {
		return components;
	}

	public OutputDescr getThisDescription() {
		return thisDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((thisReference == null) ? 0 : thisReference.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartedOutputGroup other = (ChartedOutputGroup) obj;
		if (thisReference == null) {
			if (other.thisReference != null)
				return false;
		} else if (!thisReference.equals(other.thisReference))
			return false;
		return true;
	}

	public OutputReference getThisReference() {
		return thisReference;
	}


	@Override
	public String toString() {
		return "ChartedOutputGroup [\n\t thisReference=" + thisReference + ", \n\t thisDescription=" + thisDescription + ", \n\t components=" + components + "]";
	}

}
