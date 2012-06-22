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
package com.finance.pms.screening;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.db.DataSource;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.AnalysisClient;
import com.finance.pms.events.EventDefinition;
import com.finance.pms.events.EventKey;
import com.finance.pms.events.EventSource;
import com.finance.pms.events.EventState;
import com.finance.pms.events.EventType;
import com.finance.pms.events.EventValue;
import com.finance.pms.events.SymbolEvents;
import com.finance.pms.events.calculation.MessageProperties;
import com.finance.pms.portfolio.IgnoredEventDateException;
import com.finance.pms.queue.SymbolEventsMessage;
import com.finance.pms.talib.dataresults.AlertEventValue;
import com.finance.pms.talib.dataresults.StandardEventKey;

public abstract class TrendSupplementStockExporter extends Exporter<NavigableSet<TrendSupplementedStock>> {

	private static MyLogger LOGGER = MyLogger.getLogger(TrendSupplementStockExporter.class);


	protected TrendSupplementStockExporter(String fileExtention, Queue eventQueue, JmsTemplate jmsTemplate) {
		super("perfsAndYield_" +fileExtention,
				"rank;id;sector;" +
				"div;yield;payout;Ryie;Rpout;%change;PastRat;" +
				"Yrec;Brec;Ypot;Bpot;RecRat;" +
				"Yeps;YestEps;Ype;YepsGrowth;Ypeg;YPegRat;" +
				"Beps;BestEps;Bpe;BepsGrowth;Bpeg;BPegRat;" +
				"Reps;RestEps;Rpe;RepsGrowth;Rpeg;RPegRat;" +
				"EstRat;NoDivRat;FulRat",
				eventQueue, jmsTemplate);
	}


	@Override
	public void exportToFile(NavigableSet<TrendSupplementedStock> element) throws IOException {
		
		writeFileHeader(fileName, header);
		bufferedWriter.write(SEPARATOR+"Dividende : being overridden in this order :  from yahoo then reuters then bourso\n");
		bufferedWriter.write(SEPARATOR+"BNA == EPS (annual Earning Per Share)\n");
		bufferedWriter.write(SEPARATOR+"Est BNA/EPS : Estimated end of next this year BNA or estimated EPS\n");
		bufferedWriter.write(SEPARATOR+"Pay out ratio (reuters) == dividend per share / earning per share. The best is a high div and a low ratio as the earnings support the dividend.\n");
		bufferedWriter.write(SEPARATOR+"Ideal Payout ratio is 50%. It shouldn't be outside the 40%, 60% limits.\n");
		bufferedWriter.write(SEPARATOR+"EPS growth == Estimated EPS - Current EPS\n");
		bufferedWriter.write(SEPARATOR+"PEG ratio == P/E ratio / EPS growth rate. It is considered a form of normalisation because higher growth rates should cause higher P/E ratios.\n");
		bufferedWriter.write(SEPARATOR+"Ideal figures : P/E is below 15 (ie not over priced) and EPS growth is above 20% and PEG : below 0.75 (ie 15/20)\n\n");
		bufferedWriter.write(header);
		bufferedWriter.newLine();
		
		
		Integer rank = 0;
		for (TrendSupplementedStock stockPerf : element) {
			rank++;
			String newLine = 
					rank.toString().concat(SEPARATOR)
					.concat(stockPerf.getName().concat(BLANK))
					.concat(stockPerf.getStock().getSymbol()).concat(BLANK)
					.concat(stockPerf.getStock().getIsin()).concat(SEPARATOR)
					.concat(stockPerf.getSectorHint()).concat(SEPARATOR)
					 //Past reating
					.concat(stockPerf.getDividend().toString()).concat(SEPARATOR)
					.concat(stockPerf.yield().toString()).concat(SEPARATOR)
					.concat(stockPerf.payoutRatio().toString()).concat(SEPARATOR)
					.concat(stockPerf.getReutersYield().toString()).concat(SEPARATOR)
					.concat(stockPerf.getReutersPayoutRatio().toString()).concat(SEPARATOR)
					.concat(stockPerf.quotePerfOverPeriod().toString()).concat(SEPARATOR)
					.concat(stockPerf.pastRating().toString()).concat(SEPARATOR)
					//Reco
					.concat(stockPerf.getYahooMeanRecommendations().toString()).concat(SEPARATOR)
					.concat(stockPerf.getBoursoMeanRecommendations().toString()).concat(SEPARATOR)
					.concat(stockPerf.getYahooPotentielPrice().toString()).concat(SEPARATOR)
					.concat(stockPerf.getBoursoPricePotentiel().toString()).concat(SEPARATOR)
					.concat(stockPerf.recRating().toString()).concat(SEPARATOR)
					
					//Actual PE EPSG PEG
					.concat(stockPerf.getYahooEPS().toString()).concat(SEPARATOR)
					.concat(stockPerf.getYahooEstEPS().toString()).concat(SEPARATOR)
					.concat(stockPerf.yahooPE().toString()).concat(SEPARATOR)
					.concat(stockPerf.yahooEPSG().toString()).concat(SEPARATOR)
					.concat(stockPerf.yahooPEG().toString()).concat(SEPARATOR)
					.concat(stockPerf.yahooPEGRating().toString()).concat(SEPARATOR)
					
					.concat(stockPerf.getBoursoBNA().toString()).concat(SEPARATOR)
					.concat(stockPerf.getBoursoEstBNA().toString()).concat(SEPARATOR)
					.concat(stockPerf.boursoPE().toString()).concat(SEPARATOR)
					.concat(stockPerf.boursoEPSG().toString()).concat(SEPARATOR)
					.concat(stockPerf.boursoPEG().toString()).concat(SEPARATOR)
					.concat(stockPerf.boursoPEGRating().toString()).concat(SEPARATOR)

					.concat(stockPerf.getReutersEPS().toString()).concat(SEPARATOR)
					.concat(stockPerf.getReutersEstEPS().toString()).concat(SEPARATOR)
					.concat(stockPerf.reutersPE().toString()).concat(SEPARATOR)
					.concat(stockPerf.reutersEPSG().toString()).concat(SEPARATOR)
					.concat(stockPerf.reutersPEG().toString()).concat(SEPARATOR)
					.concat(stockPerf.reutersPEGRating().toString()).concat(SEPARATOR)
					
					//Totals
					.concat(stockPerf.estimatedRating().toString()).concat(SEPARATOR)
					.concat(stockPerf.noDivFulRating().toString()).concat(SEPARATOR)
					.concat(stockPerf.fullRating().toString());
			
			bufferedWriter.write(newLine);
			bufferedWriter.newLine();
		}
		
		bufferedWriter.flush();
		
	}


	/**
	 * @param eventListName
	 * @param screenedStock
	 * @param previousRank 
	 * @param eventType 
	 * @param eventDate 
	 * @param string 
	 * @param ""
	 */
	protected SymbolEvents constructEvent(
			String eventListName, TrendSupplementedStock screenedStock, EventDefinition eventDefinition, Integer rank, Integer previousRank, EventType eventType, String message, Date eventDate) {
		
		Map<EventKey, EventValue> eventMap = new HashMap<EventKey, EventValue>();
		
		EventKey key = new StandardEventKey(eventDate, eventDefinition, eventType);
		message = message + ". Rank is : --" + rank +"--";
		if (previousRank != null) message = message + " and was "+previousRank;
		EventValue value = new AlertEventValue(eventDate, eventType, eventDefinition, message, eventListName);
		eventMap.put(key, value);
		SymbolEvents symbolEvents = new SymbolEvents(screenedStock.getStock(), eventMap, EventDefinition.getEventDefList(), EventState.STATE_TERMINATED);
		return symbolEvents;
	}


	/**
	 * @param stock
	 * @param analysisName 
	 * @param eventTypes 
	 * @param eventType 
	 * @return
	 * @throws IgnoredEventDateException 
	 */
	protected Integer extractPreviousRank(Stock stock, String analysisName, Date endDate, EventType... eventTypes) throws IgnoredEventDateException {
		EventValue previousEventValue = DataSource.getInstance().getLastTrendEventFor(stock, analysisName, endDate, eventTypes);
		
		if (previousEventValue == null) return null;
		Calendar currentDateCal = Calendar.getInstance();
		currentDateCal.setTime(endDate);
		currentDateCal.set(Calendar.HOUR_OF_DAY, 0);
		currentDateCal.set(Calendar.MINUTE, 0);
		currentDateCal.set(Calendar.SECOND, 0);
		currentDateCal.set(Calendar.MILLISECOND,0);
		if (previousEventValue.getDate().equals(currentDateCal.getTime())) throw new IgnoredEventDateException(previousEventValue);
		
		Integer previousRank = extractPreviousRankFromMessage(previousEventValue);
		return previousRank;
	}


	/**
	 * @param previousEventValue
	 */
	private Integer extractPreviousRankFromMessage(EventValue previousEventValue) {
		
		Pattern pattern = Pattern.compile("Rank is : --([0-9]+)--");
		Matcher matcher = pattern.matcher(previousEventValue.getMessage());
		if (matcher.find()) {
			return new Integer(matcher.group(1));
		} else {
			LOGGER.error("Invalid trend event : "+previousEventValue, new Throwable());
			return 0;
		}
	}


	public void sendValidityMetricsMsg(final FullRatingOrdinator ordinator, final String eventListName) {
		
			jmsTemplate.send(eventQueue, new MessageCreator() {

				public Message createMessage(Session session) throws JMSException {
					
					SymbolEvents symbolEvents = new SymbolEvents(AnalysisClient.ANY_STOCK);
					StandardEventKey eventKey = new StandardEventKey(new Date(), EventDefinition.SCREENER, EventType.INFO);
					String messageTxt = String.format(
							"Percentage of unfound trends %.2f %%.\n" +
							"Percentage of not credible trends %.2f %%.\n" +
							"Percentage of staled trends %.2f %%.\n" +
							"Percentage of under performing %.2f %%.\n" +
							"Percentage of invalid trends %.2f %%.\n" +
							"Unfound trends :\n %s \nNot credible trends :\n %s \nStaled trends :\n %s \nUnder perf :\n %s \nInvalid trends :\n %s\n",
							ordinator.getNotToBefoundPerCentage()*100,ordinator.getNotCrediblePerCentage()*100,ordinator.getStaledPerCentage()*100,ordinator.getIgnoredPerCentage()*100,ordinator.getInvalidPerCentage()*100,
							ordinator.getNotToBeFoundTrends(),ordinator.getNotCredibleTrends(),ordinator.getStaledTrends(),ordinator.getIgnoredTrends(),ordinator.getInvalidTrends());
					symbolEvents.addEventResultElement(eventKey, new AlertEventValue(eventKey, messageTxt, eventListName), EventDefinition.SCREENER.getEventDef());
					
					SymbolEventsMessage message = new SymbolEventsMessage(symbolEvents);
					message.setObjectProperty(MessageProperties.ANALYSE_SOURCE.getKey(), EventSource.PMScreening);
					message.setObjectProperty(MessageProperties.SEND_EMAIL.getKey(), Boolean.TRUE);
					message.setObjectProperty(MessageProperties.TREND.getKey(), EventType.INFO.name());
					
					return message;
				}
			});
		}
		
	
}
