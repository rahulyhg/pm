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
package com.finance.pms.datasources.files;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.finance.pms.datasources.shares.Currency;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.calculation.DateFactory;
import com.finance.pms.portfolio.Portfolio;
import com.finance.pms.portfolio.Transaction.TransactionType;

@Entity
@Table(name="TRANSACTIONS")
public class TransactionElement implements Comparable<TransactionElement>, Serializable {
	
	private static final long serialVersionUID = -257553176773712060L;
	
	private Portfolio portfolio;
	private String externalAccount;

	private Long id;
	private Stock stock;
	
	private Date date;
	private BigDecimal quantity;
	private BigDecimal price;
	private Currency currency;
	
	@SuppressWarnings("unused")
	private TransactionElement() {
		//Hib
	}
	
	public TransactionElement(Stock stock, Portfolio portfolio, String externalAccount, Date date, BigDecimal price, BigDecimal quantity, Currency currency) {
		super();
		this.stock = stock;
		this.portfolio= portfolio;
		this.externalAccount = externalAccount;
		this.date = date;
		this.price = price;
		this.quantity = quantity;
		this.currency = currency;
		
		this.id = DateFactory.milliSecStamp();
	}

	@Override
	public String toString() {
		return "TransactionElement [symbol=" + stock + ", accountName=" + externalAccount + ", portfolio=" + ((portfolio != null)?portfolio.getName():null) +  ", date=" + date + ", quantity="+ quantity + ", price=" + price + "]";
	}

	public int compareTo(TransactionElement o) {
		return new TransactionComparator().compare(this, o);
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumns({ @JoinColumn(name = "isin", referencedColumnName = "isin"), @JoinColumn(name = "symbol", referencedColumnName = "symbol") })
	public Stock getStock() {
		return stock;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}
	
	public BigDecimal getPrice() {
		return price;
	}

	public StringBuffer printTestElement() {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
		StringBuffer reportPrint = new StringBuffer();
		reportPrint.append("elements.add(new TransactionElement(stock").append(", \""+externalAccount+"\"");
		reportPrint.append(", simpleDateFormat.parse(\"").append(simpleDateFormat.format(getDate())).append("\")");
		reportPrint.append(", new BigDecimal(").append(getPrice()).append(")");
		reportPrint.append(", new BigDecimal(").append(getQuantity()).append(")");
		reportPrint.append(", Currency.").append(getCurrency());
		reportPrint.append("));\n");
		
		return reportPrint;
	}

	public String getExternalAccount() {
		return externalAccount;
	}

	@SuppressWarnings("unused")
	private void setExternalAccount(String accountName) {
		this.externalAccount = accountName;
	}

	@SuppressWarnings("unused")
	private void setSymbol(Stock stock) {
		this.stock = stock;
	}

	@SuppressWarnings("unused")
	private void setDate(Date date) {
		this.date = date;
	}

	@SuppressWarnings("unused")
	private void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	@SuppressWarnings("unused")
	private void setPrice(BigDecimal price) {
		this.price = price;
	}

	//@Id  @GeneratedValue
	@Id
	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalAccount == null) ? 0 : externalAccount.hashCode());
		result = prime * result + ((portfolio == null) ? 0 : portfolio.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
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
		TransactionElement other = (TransactionElement) obj;
		if (externalAccount == null) {
			if (other.externalAccount != null)
				return false;
		} else if (!externalAccount.equals(other.externalAccount))
			return false;
		if (portfolio == null) {
			if (other.portfolio != null)
				return false;
		} else if (!portfolio.equals(other.portfolio))
			return false;
		if (currency != other.currency)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (date.compareTo(other.date) != 0)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	@Enumerated(EnumType.STRING)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@ManyToOne
	@ForeignKey(name="FK_TRANSACTION_TO_PORTFOLIO_NAME")
	@JoinColumn(name = "portfolio")
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public TransactionType transactionType() {
		
		if (this.getQuantity().compareTo(BigDecimal.ZERO) > 0 && this.getPrice().compareTo(BigDecimal.ZERO) != 0 ) {
			return TransactionType.AIN;
		} else if (this.getQuantity().compareTo(BigDecimal.ZERO) < 0 && this.getPrice().compareTo(BigDecimal.ZERO) != 0) {
			return TransactionType.AOUT;
		} else {
			return TransactionType.NULL;
		}
		
	}

	public BigDecimal amount() {
		return this.getPrice().multiply(this.getQuantity());
	}
	
}
