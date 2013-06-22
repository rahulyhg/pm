package com.finance.pms.datasources.db;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.portfolio.PortfolioShare;

public class StripedCloseRealPrice extends StripedCloseFunction {
	
	private NumberFormat nf = new DecimalFormat("0.####");

	@Override
	public void targetShareData(PortfolioShare ps, Quotations stockQuotations) {
		
		this.stockQuotations = stockQuotations;
		
		Date startDate = getStartDate(stockQuotations);
		startDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(0,startDate);
		
		Date endDate = getEndDate(stockQuotations);
		endDateQuotationIndex = this.stockQuotations.getClosestIndexForDate(startDateQuotationIndex, endDate);

	}

	@Override
	public Number[] relativeCloses() {
		
		ArrayList<BigDecimal>  retA = new ArrayList<BigDecimal>();
		for (int i = startDateQuotationIndex; i <= this.endDateQuotationIndex; i++) {
			retA.add(this.stockQuotations.get(i).getClose());
		}
	
		return  retA.toArray(new BigDecimal[0]);
	}

	@Override
	public String lineToolTip() {
		return "real price";
	}

	@Override
	public String formatYValue(Number yValue) {
		return nf.format(yValue);
	}

}
