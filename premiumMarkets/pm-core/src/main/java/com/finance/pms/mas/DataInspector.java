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
package com.finance.pms.mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.finance.pms.admin.install.logging.MyLogger;



// TODO: Auto-generated Javadoc
/**
 * The Class DataInspector.
 * 
 * @author Guillaume Thoreton
 */
public class DataInspector {
	
	/** The LOGGER. */
	protected static MyLogger LOGGER = MyLogger.getLogger(DataInspector.class);
	
	//private StringBuffer response;
	/** The ack number. */
	private int ackNumber;
	
	/** The message. */
	private StringBuffer message;


	/**
	 * Instantiates a new data inspector.
	 * 
	 * @param br the br
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RestartServerException the restart server exception
	 * 
	 * @author Guillaume Thoreton
	 */
	public DataInspector(BufferedReader br) throws IOException,RestartServerException {
		try {
			message = new StringBuffer();
			String line;
			String ack = new String();
			//while (!br.ready());
			for(int count=0; ((line = br.readLine()) != null); count++) {
			 	if (count == 0) { //extraction de l'aquitement
					StringTokenizer tResponse = new StringTokenizer(line,MasSource.MESSAGE_FIELD_SEPARATOR);
					ack = tResponse.nextToken();
					ackNumber = (new Integer(ack)).intValue();
					String suite = line.substring(ack.length()+MasSource.MESSAGE_FIELD_SEPARATOR.length());
					message.append(suite);
			 	} else {
			 		message.append("\n");
			 		message.append(line);
			 	}
			}
			//System.out.println("message "+message);
			//supression du caract�re de contr�le
			if (ack.length() > 0 && message.charAt(message.length()-1) == MasConnection.EOM.charAt(0)) {
				message.deleteCharAt(message.length()-1);
			} else {
				throw new RestartServerException("No reponse: Probably Mas Server crash");
			}
		} catch (Exception e) {
			LOGGER.debug("",e);
			throw new RestartServerException();
		} 
	}
	
	/**
	 * Gets the ack number.
	 * 
	 * @return the ack number
	 */
	public int getAckNumber() {
		return ackNumber;
	}

	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message.toString();
	}
}
