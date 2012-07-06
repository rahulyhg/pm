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
package com.finance.pms.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


// TODO: Auto-generated Javadoc
/**
 * The Class DerbyExport.
 * 
 * @author Guillaume Thoreton
 */
public class DerbyExport {

	
	/** The dat dir name. */
	//final String datDirName = "distrib/export_nasdaq-yahoo";
	//final String datDirName = "distrib/export";
	//final String datDirName = "my_export";
	//final String datDirName = "distrib/export_euronext-yahoo";
	//final String datDirName = "/home/guil/tmp";
	//final String datDirName = "/home/guil/Documents/Comptes/Gestion/PMS";
	final String datDirName = "/home/guil/tmp";
	
	/** The db name. */
	//final String dbName = "piggymarketsqueak_euronext_yahoo";
	//final String dbName = "piggymarketsqueak_initialdb";
	final String dbName = "premiummarkets";
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @author Guillaume Thoreton
	 */
	public static void main(String[] args) {
		DerbyExport de=new DerbyExport();
		de.exportDB();
	}
	
	
	/**
	 * Export db.
	 * 
	 * @author Guillaume Thoreton
	 */
	public void exportDB() {
		
		try {
			
			PreparedStatement psAlertsAlerts=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psAlertsAlerts.setString(1,null);
			psAlertsAlerts.setString(2,"ALERTS");
			psAlertsAlerts.setString(3,datDirName+"/ALERTS.dat");
			psAlertsAlerts.setString(4,";");
			psAlertsAlerts.setString(5,null);
			psAlertsAlerts.setString(6,null);
			psAlertsAlerts.execute();
			
			PreparedStatement psCurrency=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psCurrency.setString(1,null);
			psCurrency.setString(2,"CURRENCYRATE");
			psCurrency.setString(3,datDirName+"/CURRENCYRATE.dat");
			psCurrency.setString(4,";");
			psCurrency.setString(5,null);
			psCurrency.setString(6,null);
			psCurrency.execute();
			
			PreparedStatement psEvents=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psEvents.setString(1,null);
			psEvents.setString(2,"EVENTS");
			psEvents.setString(3,datDirName+"/EVENTS.dat");
			psEvents.setString(4,";");
			psEvents.setString(5,null);
			psEvents.setString(6,null);
			psEvents.execute();
			
			PreparedStatement psPort=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psPort.setString(1,null);
			psPort.setString(2,"PORTFOLIO");
			psPort.setString(3,datDirName+"/PORTFOLIO.dat");
			psPort.setString(4,";");
			psPort.setString(5,null);
			psPort.setString(6,null);
			psPort.execute();
			
			PreparedStatement psPortName=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psPortName.setString(1,null);
			psPortName.setString(2,"PORTFOLIO_NAME");
			psPortName.setString(3,datDirName+"/PORTFOLIO_NAME.dat");
			psPortName.setString(4,";");
			psPortName.setString(5,null);
			psPortName.setString(6,null);
			psPortName.execute();
			
			PreparedStatement psShares=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psShares.setString(1,null);
			psShares.setString(2,"SHARES");
			psShares.setString(3,datDirName+"/SHARES.dat");
			psShares.setString(4,";");
			psShares.setString(5,null);
			psShares.setString(6,null);
			psShares.execute();
			
			
			PreparedStatement psQuotation=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psQuotation.setString(1,null);
			psQuotation.setString(2,"QUOTATIONS");
			psQuotation.setString(3,datDirName+"/QUOTATIONS.dat");
			psQuotation.setString(4,";");
			psQuotation.setString(5,null);
			psQuotation.setString(6,null);
			psQuotation.execute();
			
			PreparedStatement psTrend=this.connect(true).prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
			psTrend.setString(1,null);
			psTrend.setString(2,"TREND_SUPPLEMENT");
			psTrend.setString(3,datDirName+"/TREND_SUPPLEMENT.dat");
			psTrend.setString(4,";");
			psTrend.setString(5,null);
			psTrend.setString(6,null);
			psTrend.execute();


			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Connect.
	 * 
	 * @param autocommit the autocommit
	 * 
	 * @return the connection
	 * 
	 * @author Guillaume Thoreton
	 */
	private Connection connect(boolean autocommit) {
		String connectionURL;
		Connection conn = null;
			try {
				// Resolve the className
				try {
					Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				} catch (java.lang.ExceptionInInitializerError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Set up the connection
				connectionURL = "jdbc:" +  "derby";
				//connectionURL = connectionURL + ":" + "/opt/USERDATA/derby/";
				connectionURL = connectionURL + ":" + "/home/guil/Developpement/Quotes/";
				//connectionURL = connectionURL + ":" + "/home/guil/Developpement/newEclipse/premiumMarkets/pm-uiinstall/distrib/derby/";
				connectionURL = connectionURL + dbName;
				conn = DriverManager.getConnection(connectionURL);
				conn.setAutoCommit(autocommit);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return conn;
	}
}
