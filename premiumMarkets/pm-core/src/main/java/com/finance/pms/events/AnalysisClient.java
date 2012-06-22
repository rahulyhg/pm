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
package com.finance.pms.events;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.finance.pms.MainPMScmd;
import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.admin.install.logging.PopupMessageRunnable;
import com.finance.pms.datasources.shares.Market;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.datasources.web.ScraperMetrics;
import com.finance.pms.events.calculation.MessageProperties;
import com.finance.pms.portfolio.PortfolioMgr;
import com.finance.pms.queue.AbstractAnalysisClientRunnableMessage;
import com.finance.pms.queue.ExportAutoPortfolioMessage;
import com.finance.pms.queue.InnerQueue;
import com.finance.pms.queue.SignalProcessorMessage;
import com.finance.pms.queue.SingleEventMessage;
import com.finance.pms.queue.SymbolEventsMessage;



// TODO: Auto-generated Javadoc
/**
 * The Class AnalysisClient.
 * 
 * @author Guillaume Thoreton
 */
public class AnalysisClient  implements MessageListener, ApplicationContextAware {

	/** The LOGGER. */
	protected static MyLogger LOGGER = MyLogger.getLogger(AnalysisClient.class);
	public static Stock ANY_STOCK = new Stock("ANYSTOCK","ANYSTOCK");
	static {
		ANY_STOCK.setName("ANY STOCK");
		ANY_STOCK.setMarket(Market.EURONEXT);
	}
	
	ApplicationContext applicationContext;
	
	private MailSender mailSender;
    private SimpleMailMessage templateMessage;
    private InnerQueue eventQueue;
    private ExecutorService analysisExecutor;
    
    @Autowired
	private ScraperMetrics scrapperMetrics;
    
    
    
    public AnalysisClient() {
		super();
		//this.analysisExecutor = Executors.newCachedThreadPool();
		//new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		this.analysisExecutor = new ThreadPoolExecutor(0, 800, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}



	public void onMessage(Message message) {

    	try {

    		if (message instanceof SignalProcessorMessage) { //Retrieve events from DB and process Auto portfolios
    			
    			SignalProcessorMessage processorMessage = (SignalProcessorMessage)((SignalProcessorMessage) message).getObject();
    			LOGGER.debug("New processor message received : " + processorMessage.getMessageTxt());
    			runSynchTask(
    					processorMessage.getSignalProcessingName(),
    					new SignalProcessorMessageRunnable(processorMessage, (JmsTemplate) applicationContext.getBean("jmsTemplate"), (InnerQueue) applicationContext.getBean("eventqueue")));

 
    		} else if (message instanceof ExportAutoPortfolioMessage) { //
    			
    			ExportAutoPortfolioMessage m = (ExportAutoPortfolioMessage) message;
    			LOGGER.info("New export message received : " + m.getAnalyseName());
    			runSynchTask(m.getAnalyseName(), new ExportAutoPortfolioRunnable(m));
    			
    			
    		} else if (message instanceof AbstractAnalysisClientRunnableMessage) { //Runnable Messages : screener, indicator and alerts on threshold
    			
    			AbstractAnalysisClientRunnableMessage screenerMessage = (AbstractAnalysisClientRunnableMessage) message;
    			LOGGER.info("New runnable message received : "+screenerMessage.getAnalysisName()+" for "+screenerMessage.getClass().getName());
    			runSynchTask(screenerMessage.getAnalysisName(), screenerMessage);
    			
    		} else if (message instanceof ObjectMessage) { //SymbolEvent : send email

    			SymbolEvents symbolEventMessage = extractSymbolEventsObject(message);
    			String eventListName = extractEventListName(message);
    			
    			EventType eType = EventType.valueOf((String) message.getObjectProperty(MessageProperties.TREND.getKey()));
    			EventSource source = (EventSource) message.getObjectProperty(MessageProperties.ANALYSE_SOURCE.getKey());
    			
    			LOGGER.debug("Event list for " + symbolEventMessage.getSymbolName() + " (" + symbolEventMessage.getSymbol() + ") : \n" + symbolEventMessage.toString());

    			//Email
    			Boolean sendEmail = (Boolean) message.getObjectProperty(MessageProperties.SEND_EMAIL.getKey());
    			
    			if (sendEmail) {
    				sendEmail(symbolEventMessage, eType, source, eventListName);
				}

    		} else {
    			throw new IllegalArgumentException("Message must be of type Inner or Processor Message");
    		}
    		
    	} catch (JMSException ex) {
    		throw new RuntimeException(ex);
    	}
    }



	/**
	 * @param symbolEvents
	 * @param eventType
	 * @param source
	 * @param fromUI
	 * @param eventListName 
	 */
    private void sendEmail(final SymbolEvents symbolEvents, EventType eventType, EventSource source, String eventListName) {
    	
    	LOGGER.debug(
    			"Email/Popup potential message preview : "+eventType.name()+" from "+source+" in "+ eventListName + " : " 
    			+ symbolEvents.getSymbolName()+" ("+symbolEvents.getSymbol()+"), "+symbolEvents.toEMail());
 
    	Boolean sendMailEnabled = new Boolean(MainPMScmd.getPrefs().get("mail.infoalert.activated","false"));
    	
    	//SCREENING
    	//All screening on monitored from user portfolios
    	Boolean isValidScreeningEvent = source.equals(EventSource.PMUserScreening) && symbolEvents.isMonitored();
    	//Screening validity metrics info message
    	Boolean isValidScreeningMessage = source.equals(EventSource.PMScreening) && symbolEvents.getStock().equals(ANY_STOCK) && eventType.equals(EventType.INFO);
    	
    	//WEATHER
    	//Only REFSTOCK sends a Weather msg
    	//Non monitored share won't send weather messages
    	Boolean isValidWeatherEvent = source.equals(EventSource.PMWeather) && symbolEvents.getStock().equals(ANY_STOCK);
    	
    	//ALERTS
    	Boolean isMonitoredForPortfolio = PortfolioMgr.getInstance().isMonitoredForPortofolio(symbolEvents.getStock(), eventListName);	
    	//All alerts on monitored from user portfolios
    	Boolean isValidAlertEvent = source.equals(EventSource.PMUserAlert) && isMonitoredForPortfolio;
 
    	//BUY SELL
    	//All Buy and Sell signals from auto portfolios and monitored user portfolios
		Boolean isValidAutoBuySellSignal = source.equals(EventSource.PMAutoBuySell) || ( source.equals(EventSource.PMUserBuySell)  && isMonitoredForPortfolio );
		
		Boolean isValidEventSource = isValidAlertEvent || isValidScreeningEvent || isValidAutoBuySellSignal || isValidScreeningMessage || isValidWeatherEvent;	
		if 	(sendMailEnabled && isValidEventSource ) {
			LOGGER.info(
	    			"Email/Popup potential message preview : "+eventType.name()+" from "+source+" in "+ eventListName + " : " 
	    			+ symbolEvents.getSymbolName()+" ("+symbolEvents.getSymbol()+"), "+symbolEvents.toEMail());
			
			this.sendMailEvent(symbolEvents, eventType, source, eventListName);
			
		} 
    }


	/**
	 * @param symbolEventMessage
	 */
	void openPopup( String reportHeader, String reportContent, String title) {
		
		PopupMessageRunnable messageBox = new PopupMessageRunnable(reportHeader,reportContent,title, true);
		
		Thread thread = new Thread(messageBox);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @param m
	 */
    private void runSynchTask(String analysisName, Runnable runnable) {

    		LOGGER.debug("Excuting executor :"+runnable.getClass().getName()+" for "+ analysisName);
    		analysisExecutor.execute(runnable);

    }

	/**
	 * @param message
	 * @return
	 * @throws JMSException
	 */
	private SymbolEvents extractSymbolEventsObject(Message message) throws JMSException {
	
		if (message instanceof SingleEventMessage) {
			EventMessageObject eventMessageObject = (EventMessageObject)((ObjectMessage)message).getObject();
			return  new SymbolEvents(eventMessageObject);
		} else if (message instanceof SymbolEventsMessage) {
			return (SymbolEvents) ((ObjectMessage) message).getObject();
		}
		
		throw new JMSException("Unrecognised message :"+message);
	}
	

	private String extractEventListName(Message message) throws JMSException {
		if (message instanceof SingleEventMessage) {
			EventMessageObject eventMessageObject = (EventMessageObject)((ObjectMessage)message).getObject();
			return eventMessageObject.getEventListName();
		} 
		return null;
	}
	
	/**
	 * Send mail event.
	 * 
	 * @param event the event
	 * @param eventType the et
	 * @param monitorLevel the ml
	 * @param source the source
	 * 
	 * @author Guillaume Thoreton
	 * @param eventListName 
	 */
	private void sendMailEvent(SymbolEvents event, EventType eventType, EventSource source, String eventListName) {
		
		if (this.templateMessage.getTo() == null || this.templateMessage.getTo().length == 0 || this.templateMessage.getTo()[0] == null || this.templateMessage.getTo()[0].equals("")) {
			LOGGER.warn("No recipicent set for this message. Event sending aborted!");
			return;
		}
		
		SimpleMailMessage mail = new SimpleMailMessage(this.templateMessage);
		String stockName = event.getSymbolName()+" ("+event.getSymbol()+")";
		String eMailTxt = event.toEMail();
		String notaBene = "This message was created on the "+new Date();
		String subject = "NONE";
	
		switch (eventType) {
			case BEARISH:
				subject = source + " : SELL "+stockName+" in "+ eventListName;
				break;
			case BULLISH:
				subject = source + " : BUY "+stockName+" in "+ eventListName;
				break;
			default:
				String addEventType = inferAdditionalEventTypeForOtherNInfoEventTypes(source, eMailTxt);
				subject = source + " : "+stockName+" "+ addEventType +" in "+ eventListName;
		}
		
		mail.setSubject(subject);
		mail.setText(eMailTxt+"\n\n\n"+notaBene);
		mail.setSentDate(event.getLastDate());
        
		//this.openPopup(subject, eMailTxt, "Info/Alert");
        try {
            this.mailSender.send(mail);
        }
        catch(MailException ex) {
            LOGGER.error("Can't send email :"+subject+";"+eMailTxt+"\n\n\n"+notaBene, ex);         
        }
	}

	/**
	 * @param event 
	 * @param eMailTxt
	 * @return
	 */
	private String inferAdditionalEventTypeForOtherNInfoEventTypes(EventSource eventSource, String eMailTxt) {

		String bearish = "SELL";
		String bullish = "BUY";

		String evenTypeAdd = EventType.INFO.name();
		
		//PMTAEvents,PMScreening,PMUserScreening,PMAlert,PMUserAlert,PMAutoBuySell,PMUserBuySell,PMWeather 
		switch(eventSource) {
		case PMTAEvents:
		case PMScreening :
		case PMAlert:
		case PMAutoBuySell:
			break;
		case PMUserScreening:
			if (eMailTxt.contains("rank is Down")) {
				evenTypeAdd = bearish;
			} else if (eMailTxt.contains("rank is Up")) {
				evenTypeAdd=bullish;
			} else if (eMailTxt.contains("Screener Alert")) {
				evenTypeAdd = "SCREENING";
			}
			break;
		case PMUserAlert:
			if ((eMailTxt.contains("Below channel") || eMailTxt.contains("take profit limit"))) {
				evenTypeAdd = bearish;
			} else if (eMailTxt.contains("Above channel")) {
				evenTypeAdd = bullish;
			}
			break;
		case PMUserBuySell:
			if (eMailTxt.contains("movement : sell")) {
				evenTypeAdd = bearish;
			} else if (eMailTxt.contains("movement : buy")) {
				evenTypeAdd = bullish;
			}
			break;
		case PMWeather:
			break;
		}

		return evenTypeAdd;
	}


	/**
     * Sets the mail sender.
     * 
     * @param mailSender the new mail sender
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sets the template message.
     * 
     * @param templateMessage the new template message
     */
    public void setTemplateMessage(SimpleMailMessage templateMessage) {
    	this.templateMessage = templateMessage;
    }
	
	public void close() {
		
		try {
			LOGGER.info("Waiting for queue to be empty and processed.");
			int cpt = 0;
			while (!eventQueue.isEmptyAndProcessed() && cpt++ < 60) {
				LOGGER.info("Number of task left :"+eventQueue.messagesInProcess());
				LOGGER.info("Task left :"+eventQueue.toString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.error("Error while waiting for event queue starvation.",e);
				}
			}
			LOGGER.info("Queue is now empty.");

			LOGGER.info("Waiting for all runnable messages to stop.");
			analysisExecutor.shutdown();
			boolean awaitTermination = analysisExecutor.awaitTermination(2, TimeUnit.DAYS);
			if (!awaitTermination) {
				List<Runnable> shutdownNow = analysisExecutor.shutdownNow();
				LOGGER.error(shutdownNow,new Exception());
			}
			LOGGER.info("All runnable messages are stoped.");

			LOGGER.info("Saving auto portfolios.");
			try {
				PortfolioMgr.getInstance().hibStorePortfolio();
			} catch (Exception e) {
				LOGGER.error("Error while closing AnalysisClient",e);
			}
			LOGGER.info("Auto portfolios are saved.");
			
			LOGGER.info("Sending metrics.");
			String metrics = scrapperMetrics.getMetrics(false);
			try {
				if (!metrics.isEmpty()) {
					SimpleMailMessage mail = new SimpleMailMessage(this.templateMessage);
					mail.setSubject("Scraper metrics");
					mail.setText(metrics);

					this.mailSender.send(mail);
				}
			} catch (Exception e) {
				LOGGER.warn("Can't send metrics. Please set up your email account in Settings ...? Here they are : "+metrics);
			}
			LOGGER.info("Metrics sent.");
			
			
			LOGGER.info("AnalysisClient is closed.");
			
		} catch (Exception e) {
			System.out.println("Error closing AnalysisClient : "+e);
			e.printStackTrace();
		}
	}

	public void setEventQueue(InnerQueue eventQueue) {
		this.eventQueue = eventQueue;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext= applicationContext;
	}

}
