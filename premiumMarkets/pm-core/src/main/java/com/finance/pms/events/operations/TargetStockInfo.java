package com.finance.pms.events.operations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.calculation.WarningException;
import com.finance.pms.events.calculation.parametrizedindicators.ChartedOutputGroup;
import com.finance.pms.events.calculation.parametrizedindicators.ChartedOutputGroup.OutputDescr;
import com.finance.pms.events.calculation.parametrizedindicators.ChartedOutputGroup.Type;
import com.finance.pms.events.calculation.parametrizedindicators.OutputReference;
import com.finance.pms.events.operations.conditional.ChartableCondition;
import com.finance.pms.events.operations.conditional.MultiSelectorsValue;
import com.finance.pms.events.operations.nativeops.DoubleMapValue;
import com.finance.pms.events.operations.nativeops.LeafOperation;
import com.finance.pms.events.operations.nativeops.StockOperation;

public class TargetStockInfo {
	
	public class Output {
		
		OutputReference outputReference;
		Value<?> outputData;
		OutputDescr chartedDescription;
		
		public Output(OutputReference outputReference) {
			super();
			this.outputReference = outputReference;
		}
		
		public Output(OutputReference outputReference, Value<?> outputData) {
			super();
			this.outputReference = outputReference;
			this.outputData = outputData;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((outputReference == null) ? 0 : outputReference.hashCode());
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
			Output other = (Output) obj;
			if (outputReference == null) {
				if (other.outputReference != null)
					return false;
			} else if (!outputReference.equals(other.outputReference))
				return false;
			return true;
		}

		public Value<?> getOutputData() {
			return outputData;
		}

		public OutputDescr getChartedDescription() {
			return chartedDescription;
		}

		private void setChartedDescription(OutputDescr chartedDescription) {
			this.chartedDescription = chartedDescription;
		}

		@Override
		public String toString() {
			return "Output [outputReference=" + outputReference + ", chartedDescription=" + chartedDescription + "]";
		}

		public OutputReference getOutputReference() {
			return outputReference;
		}

	}
	
	Stock stock;
	Date startDate;
	Date endDate;
	private String analysisName;
	
	List<Output> calculatedOutputsCache;
	
	List<Output> gatheredChartableOutputs;
	List<ChartedOutputGroup> chartedOutputGroups;
	
	public TargetStockInfo(String analysisName, Stock stock, Date startDate, Date endDate) throws WarningException {
		super();
		this.analysisName = analysisName;
		this.stock = stock;
		
		Date lastQuote = stock.getLastQuote();
		if (lastQuote.before(startDate)) throw new WarningException("No enough quotations to calculate : "+stock.toString());
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.calculatedOutputsCache = new ArrayList<TargetStockInfo.Output>();
		this.gatheredChartableOutputs = new ArrayList<TargetStockInfo.Output>();
		this.chartedOutputGroups = new ArrayList<ChartedOutputGroup>();
	}
	
	public Stock getStock() {
		return stock;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public String getAnalysisName() {
		return analysisName;
	}
	
	public Value<?> checkAlreadyCalculated(Operation operation) {
		if (operation.getFormula() == null && !(operation instanceof StockOperation)) return null;
		int indexOf = calculatedOutputsCache.indexOf(new Output(new OutputReference(operation)));
		if (indexOf == -1) {
			return null;
		}
		return calculatedOutputsCache.get(indexOf).outputData ;
	}
	
	public void addOutput(Operation operation, Value<?> output) {
		
		Value<?> alreadyCalculated = checkAlreadyCalculated(operation);
		if (alreadyCalculated != null) {
			if (getIndexOfChartableOutput(operation) == -1) {
				this.gatheredChartableOutputs.add(new Output(new OutputReference(operation), alreadyCalculated));
			}
			return;
		}
		
		//this.calculatedOutputsCache.add(new Output(new OutputReference(operation), output));
		if (output instanceof MultiSelectorsValue) {
			for (String selector : ((MultiSelectorsValue) output).getSelectors()) {
				String tamperedFormula = operation.getFormula().replaceAll(":.*\\(", ":"+selector+"(");
				OutputReference outputReference = new OutputReference(operation.getReference(), selector, tamperedFormula, operation.getReferenceAsOperand(), (operation instanceof LeafOperation), operation.getOperationReference());
				this.calculatedOutputsCache.add(new Output(outputReference, ((MultiSelectorsValue) output).getValue(selector)));
			}
			this.gatheredChartableOutputs.add(new Output(new OutputReference(operation), ((MultiSelectorsValue) output).getValue(((MultiSelectorsValue) output).getCalculationSelector())));
		} else {
			this.calculatedOutputsCache.add(new Output(new OutputReference(operation), output));
			this.gatheredChartableOutputs.add(new Output(new OutputReference(operation), output));
		}
		

	}
	
	public void addExtraneousChartableOutput(Operation operation, DoubleMapValue output, String multiOutputDiscriminator) {
		this.gatheredChartableOutputs.add(new Output(new OutputReference(operation, multiOutputDiscriminator), output));
	}
	
	public ChartedOutputGroup setMain(Operation operation) {

		Integer indexOfOutput = getIndexOfChartableOutput(operation);
		if (indexOfOutput != -1) {
			
			Output output = gatheredChartableOutputs.get(indexOfOutput);
			OutputDescr chartedDesrc = output.getChartedDescription();
			if (chartedDesrc != null) {
				chartedDesrc.maskType(Type.MAIN);
			} else {
				ChartedOutputGroup chartedOutputGroup = new ChartedOutputGroup(operation, indexOfOutput);
				chartedOutputGroups.add(chartedOutputGroup);
				chartedDesrc = chartedOutputGroup.getThisDescription();
				output.setChartedDescription(chartedDesrc);
			}
			return chartedDesrc.getContainer();
			
		} else {
			throw new RuntimeException("No historical output found. The main output must be a DoubleOperation: "+operation.getClass()+" for "+operation);
		}
		
	}
	
	public Integer getIndexOfChartableOutput(Operation operation) {
		return gatheredChartableOutputs.indexOf(new Output(new OutputReference(operation)));
	}
	
	public Integer getIndexOfChartableOutput(OutputReference outputRef) {
		return gatheredChartableOutputs.indexOf(new Output(outputRef));
	}

	public List<ChartedOutputGroup> getChartedOutputGroups() {
		return chartedOutputGroups;
	}

	public void addChartInfoForSignal(ChartedOutputGroup mainChartedGrp, Operation operation) {
		
		Integer indexOfOutput = getIndexOfChartableOutput(operation);
		if (indexOfOutput != -1) {
			Output output = gatheredChartableOutputs.get(indexOfOutput);
			OutputDescr chartedDescr = output.getChartedDescription();
			if (chartedDescr != null) {
				//Merge mainChartedGrp and existingChartedGrp if <> + maskType
				ChartedOutputGroup existingChartedGrp = chartedDescr.getContainer();
				if (existingChartedGrp.equals(mainChartedGrp)) {
					chartedDescr.maskType(Type.SIGNAL);
				} else {
					OutputDescr existingChartedDesrc = existingChartedGrp.getThisDescription();
					existingChartedDesrc.maskType(Type.BOTH);
					mainChartedGrp.mvComponentInThisGrp(existingChartedGrp.getThisReference(), existingChartedDesrc);
					for (OutputReference oldContentRef : existingChartedGrp.getComponents().keySet()) {
						OutputDescr oldOutputDescr = existingChartedGrp.getComponents().get(oldContentRef);
						mainChartedGrp.mvComponentInThisGrp(oldContentRef, oldOutputDescr);
					}
					this.chartedOutputGroups.remove(this.chartedOutputGroups.indexOf(existingChartedGrp));
				}
			} else {
				chartedDescr = mainChartedGrp.addSignal(operation, indexOfOutput);
				output.setChartedDescription(chartedDescr);
			}
		
		} else {
			throw new RuntimeException("Output not found for "+operation);
		}
	}
	
	public void addChartInfoForAdditonalOutputs(Operation operand, Map<String, Type> outputTypes) {
		
		Integer indexOfMain = getIndexOfChartableOutput(operand.getOperands().get(((ChartableCondition)operand).mainInputPosition()));
		Output output = gatheredChartableOutputs.get(indexOfMain);
		OutputDescr chartedDesrc = output.getChartedDescription();
		if (chartedDesrc != null) {
			ChartedOutputGroup mainChartedGroup = chartedDesrc.getContainer();
			for (String outputKey : outputTypes.keySet()) {
				Integer indexOfOutput = getIndexOfChartableOutput(new OutputReference(operand, outputKey));
				mainChartedGroup.addAdditonalOutput(outputKey, operand, indexOfOutput, outputTypes.get(outputKey));
			}
		} else {
			throw new RuntimeException("Multi Output Main group not found not found for "+operand);
		}
		
	}

	@Override
	public String toString() {
		return "TargetStockInfo [stock=" + stock + ", startDate=" + startDate + ", endDate=" + endDate + ", analysisName=" + analysisName + "]";
	}

	public List<Output> getGatheredChartableOutputs() {
		return gatheredChartableOutputs;
	}

	public void printOutputs() {
		
		Set<Date> allKeys = new TreeSet<Date>();
		String header = this.stock.getFriendlyName().replaceAll(",", " ") + ",";
		
		for (Output output : gatheredChartableOutputs) {
			Value<?> outputData = output.getOutputData();
			if (outputData instanceof DoubleMapValue) {
				header = header + output.getOutputReference().getReference()+",";
				Set<Date> keySet = ((DoubleMapValue)outputData).getValue(this).keySet();
				allKeys.addAll(keySet);
			}
		}
		System.out.println(header);
	
		for (Date date : allKeys) {
			String line = date + ",";
			for (Output output : gatheredChartableOutputs) {
				Value<?> outputData = output.getOutputData();
				if (outputData instanceof DoubleMapValue) {
					line = line + ((DoubleMapValue)outputData).getValue(this).get(date) + ",";
				}
			}
			System.out.println(line);
		}
		
	}
}
