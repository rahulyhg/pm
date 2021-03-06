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
package com.finance.pms.events.calculation;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.AnalysisClient;
import com.finance.pms.events.EmailFilterEventSource;
import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.portfolio.AutoPortfolioAnalyser;
import com.finance.pms.portfolio.AutoPortfolioWays;
import com.finance.pms.portfolio.PortfolioMgr;
import com.finance.pms.portfolio.TransactionHistory;
import com.finance.pms.portfolio.TransactionRecord;
import com.finance.pms.queue.BuySellSignalCalculatorMessage;
import com.finance.pms.queue.SingleEventMessage;
import com.finance.pms.threads.ConfigThreadLocal;

public class BuySellSignalCalculatorMessageRunnable implements Runnable {
	
	protected static MyLogger LOGGER = MyLogger.getLogger(BuySellSignalCalculatorMessageRunnable.class);
	
	private BuySellSignalCalculatorMessage message;
	private JmsTemplate jmsTemplate;
	private Queue eventQueue;

	public BuySellSignalCalculatorMessageRunnable(Message message, JmsTemplate jmsTemplate, Queue eventQueue) {
		super();
		this.message = (BuySellSignalCalculatorMessage) message;
		this.jmsTemplate = jmsTemplate;
		this.eventQueue = eventQueue;
	}

	public void run() {

		try {
			LOGGER.debug("Processing " + message.getSignalProcessingName());

			for (String configName : message.getPassedThroughConfigs().keySet()) {
				ConfigThreadLocal.set(configName, message.getPassedThroughConfigs().get(configName));
			}

			LOGGER.info("Processing signals " +message.getMessageTxt()+", " + message.getSignalProcessingName() + " on the " + message.getStartDate() + " with " + message.getAdditionalEventListNames());

			AutoPortfolioWays portfolio = (AutoPortfolioWays) PortfolioMgr.getInstance().getPortfolio(message.getSignalProcessingName());
			
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(message.getStartDate());
			while (currentDate.getTime().before(message.getEndDate()) || currentDate.getTime().compareTo(message.getEndDate()) == 0) {
				
				TransactionHistory calculationTransactions = portfolio.calculate(currentDate.getTime(), message.getAdditionalEventListNames());
				sendTransactionHistory(calculationTransactions);
				
				//QuotationsFactories.getFactory().incrementDate(currentDate, 1);
				currentDate.add(Calendar.DAY_OF_YEAR, 1);
				
			}
			
			AutoPortfolioAnalyser logAnalyser = new AutoPortfolioAnalyser(portfolio);
			String logAnalysisMsg = logAnalyser.message();
			if (!logAnalysisMsg.isEmpty()) sendTransactionSummary(portfolio, logAnalysisMsg);
			
			LOGGER.info("Processor message completed : " +message.getMessageTxt()+", " +message.getSignalProcessingName()+" on the " + message.getStartDate()+ " with "+ Arrays.toString(message.getAdditionalEventListNames()));
		} catch (Exception e) {
			
			LOGGER.error("Error in "+this.toString(),e);
			
		} finally {
			
			 synchronized (message) {
				 message.setProcessed(true);
				 message.notifyAll();
			}
			
		}

	}

	private void sendTransactionSummary(final AutoPortfolioWays portfolio, final String logAnalysisMsg) {
		
		if (AnalysisClient.getEmailMsgQeueingFilter().contains(EmailFilterEventSource.Metrics)) {
			jmsTemplate.send(eventQueue, new MessageCreator() {

				public Message createMessage(Session session) throws JMSException {

					EventValue eventValue = new EventValue(message.getEndDate(), EventDefinition.UNKNOWN99, EventType.INFO, logAnalysisMsg, portfolio.getName());
					SingleEventMessage infoMessage = new SingleEventMessage(portfolio.getName(), message.getEndDate(), AnalysisClient.ANY_STOCK, eventValue, ConfigThreadLocal.getAll());
					infoMessage.setObjectProperty(MessageProperties.ANALYSE_SOURCE.getKey(), EmailFilterEventSource.Summary); //Source (event calculator)
					infoMessage.setObjectProperty(MessageProperties.TREND.getKey(), eventValue.getEventType().name()); //Bearish Bullish Other Info
					infoMessage.setObjectProperty(MessageProperties.SEND_EMAIL.getKey(), Boolean.TRUE);

					return infoMessage;
				}
			});
		}
	}
	
	private void sendTransactionHistory(TransactionHistory transactionHistory) {
		
		for (final TransactionRecord record : transactionHistory) {
			
			if (AnalysisClient.getEmailMsgQeueingFilter().contains(record.getSource())) {
				jmsTemplate.send(eventQueue, new MessageCreator() {

					public Message createMessage(Session session) throws JMSException {

						EventType eventType = EventType.INFO;
						if (record.getMovement().equals("buy")) {
							eventType = EventType.BULLISH;
						} 
						else if (record.getMovement().equals("sell")) {
							eventType = EventType.BEARISH;
						}

						String message = record.toString()+"\n\n"+ BuySellSignalCalculatorMessageRunnable.messageLinks("*", record.getStock());
						EventValue eventValue = new EventValue(record.getEventList().getLastDate(), EventDefinition.UNKNOWN99, eventType, message, record.getPortfolioName());
						SingleEventMessage infoMessage = new SingleEventMessage(record.getPortfolioName(), record.getDate(), record.getStock(), eventValue, ConfigThreadLocal.getAll());			
						infoMessage.setObjectProperty(MessageProperties.ANALYSE_SOURCE.getKey(), record.getSource()); //Source (event calculator)
						infoMessage.setObjectProperty(MessageProperties.TREND.getKey(), eventValue.getEventType().name()); //Bearish Bullish Other Info
						infoMessage.setObjectProperty(MessageProperties.SEND_EMAIL.getKey(), Boolean.TRUE);

						return infoMessage;
						
					}
				});
			}
		}
		
	}


	@Override
	public String toString() {
		return this.getClass().getName()+" processing "+this.message.getSignalProcessingName() + " at "+ message.getStartDate();
	}
	
	public static String messageLinks(String analysisName, Stock stock) {
		return 
				"Generated files :\n"+
				"file:// "+System.getProperty("installdir") + File.separator + "autoPortfolioLogs"+ File.separator +analysisName+stock.getSymbol()+ "_*_BuyAndSellRecords*.csv\n" +
				"file:// "+System.getProperty("installdir") + File.separator + "tmp" + File.separator + "nr_"+stock.getSymbol()+"_cf"+analysisName+"*.csv\n";
	}

}
