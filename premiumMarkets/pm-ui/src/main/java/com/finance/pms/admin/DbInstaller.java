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
package com.finance.pms.admin;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;


// TODO: Auto-generated Javadoc
/**
 * The Class DbInstaller.
 * 
 * @author Guillaume Thoreton
 */
public class DbInstaller extends Observable {
	
	/** The LOGGER. */
	//protected static MyLogger LOGGER = MyLogger.getLogger(DbInstaller.class);
	
	private InputStream fis;
	
	/** The extract dir. */
	public static String extractDir = "export";
	
	
	/**
	 * Instantiates a new db installer.
	 * 
	 * @author Guillaume Thoreton
	 */
	public DbInstaller() {
		super();
	}


	/**
	 * Install db.
	 * 
	 * @param urlfrom the urlfrom
	 * @param dbZipFileName the db zip file name
	 * @param folderTo the folder to
	 * 
	 * @throws NoPreparedDbException the no prepared db exception
	 * 
	 * @author Guillaume Thoreton
	 */
	public void extractDB(URL urlfrom, String dbZipFileName, File folderTo) throws NoPreparedDbException {
		//extract db
		System.out.println("Extract db : "+ dbZipFileName +" to "+ folderTo + " and alternatively from "+ urlfrom);
		try {
			fis = this.getClass().getClassLoader().getResourceAsStream(dbZipFileName);
			if (null == fis) {
				System.out.println("No Data base found in package. Will download.");
				fis = downloadDb(urlfrom, dbZipFileName);
			} else {
				System.out.println("Data base found in package.");
			}
			
			fis.read(new byte[2]); //Reads the BZ odd bytes
			CBZip2InputStream bzipIS = new CBZip2InputStream(fis);
			TarInputStream tis = new TarInputStream(bzipIS);
			TarEntry tarEntry = tis.getNextEntry();

			while (tarEntry != null) {
				File destPath = new File(folderTo.getAbsolutePath() + File.separator + tarEntry.getName());
				System.out.println("Extracting " + destPath.getAbsoluteFile());
				if (destPath.exists()) {
					//TODO DB already installed => update??
				}
				if (!tarEntry.isDirectory()) {
					if (!destPath.getParentFile().exists()) {
						System.out.println("Creating directory " + destPath.getParent());
						destPath.getParentFile().mkdirs();
					}
					FileOutputStream fout = new FileOutputStream(destPath);
					tis.copyEntryContents(fout);
					fout.close();
					
					this.setChanged();
					this.notifyObservers("extracting "+destPath.getAbsolutePath());
					//displayTime("extracting "+destPath.getAbsolutePath());
					
				} else {
					System.out.println("Creating directory " + destPath.getAbsoluteFile());
					destPath.mkdirs();
				}
				tarEntry = tis.getNextEntry();
			}
			tis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("Extract db. Done!");
	}
	
	
	/**
	 * Download db.
	 * 
	 * @param urlFrom the url from
	 * @param dbZipFileName the db zip file name
	 * 
	 * @return the input stream
	 * 
	 * @throws IOException 	 * @throws NoPreparedDbException the no prepared db exception
	 * @throws NoPreparedDbException the no prepared db exception
	 * 
	 * @author Guillaume Thoreton
	 */
	private InputStream downloadDb(URL urlFrom, String dbZipFileName) throws NoPreparedDbException {
		ZipInputStream zI = null;
		try {
			//URL initialdb = new URL("http://surfnet.dl.sourceforge.net/sourceforge/pmsqueak/initialdb.jar");
			//URL initialdb = new URL("http://downloads.sourceforge.net/pmsqueak/initialdb-0.1.2.jar?use_mirror=osdn");	
			InputStream is = urlFrom.openStream();
			zI = new ZipInputStream(is);
			ZipEntry ze = zI.getNextEntry();
			while (!ze.getName().equals(dbZipFileName)) {
				ze = zI.getNextEntry();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			throw new NoPreparedDbException();
		}
		return zI;
	}
	
	/**
	 * Import db.
	 * 
	 * @param piggyMarketSqueakFolder the piggy market squeak folder
	 * @param extractDirName the extract dir name
	 * @param connection the connection
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @author Guillaume Thoreton
	 */
	public void importDB(File piggyMarketSqueakFolder, String extractDirName, Connection connection) throws SQLException, OtherException {
		
		dropConstraints(piggyMarketSqueakFolder,extractDirName,connection);
		 
		//insert data and create indexes
		importDerby(piggyMarketSqueakFolder,extractDirName, connection);
		
		//create keys and indexes 
		createIndexesAndKeys(piggyMarketSqueakFolder,extractDirName,connection);
		
		System.out.println("Import db. Done!");
		
	}
	
	

	
	/**
	 * Import derby.
	 * 
	 * @param piggyMarketSqueakFolder the piggy market squeak folder
	 * @param connection the connection
	 * @param extractDirName the extract dir name
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @author Guillaume Thoreton
	 * @throws OtherException 
	 */
	private void importDerby(File piggyMarketSqueakFolder, String extractDirName, Connection connection) throws SQLException, OtherException {
		
		String installPath = piggyMarketSqueakFolder.getAbsolutePath()+File.separator;
		
		File datDir = new File(installPath+extractDirName);
		File [] lsdat = datDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				String fname = pathname.getName();
				return fname.endsWith(".dat");
			}
			
		});
		
		for (int i = 0; i < lsdat.length; i++) {
			
			try {
				String name = lsdat[i].getName();
				String fnNoExt = name.substring(0,name.length()-4);
				
				System.out.println(new Date() + " : processing "+lsdat[i].getAbsolutePath());
				
				PreparedStatement psQuotation = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
				psQuotation.setString(1, null);
				psQuotation.setString(2, fnNoExt.toUpperCase());
				psQuotation.setString(3, lsdat[i].getAbsolutePath());
				psQuotation.setString(4, ",");
				psQuotation.setString(5, null);
				psQuotation.setString(6, null);
				psQuotation.setString(7, "1");
				
				psQuotation.execute();
				System.out.println(new Date() + " : "+lsdat[i].getAbsolutePath() +". Done!");
				
				this.setChanged();
				this.notifyObservers("setting Quotations data");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	private void dropConstraints(File piggyMarketSqueakFolder, String extractDirName, Connection connection) throws SQLException,OtherException {
		String installPath = piggyMarketSqueakFolder.getAbsolutePath() + File.separator;
		try {
			File keysFile = new File(installPath + extractDirName + File.separator + "DROP.sql");
			String statement;
			FileReader in = new FileReader(keysFile);
			BufferedReader fr = new BufferedReader(in);
			while ((statement = fr.readLine()) != null) {
				if (!statement.isEmpty() && statement.length() > 2 && !statement.substring(0,2).equals("--")) {
					try {
						PreparedStatement ps = connection.prepareStatement(statement.substring(0, statement.length() - 1));
						ps.execute();
					} catch (java.sql.SQLException e) {
						fr.close();
						throw new OtherException(e);
					}
					System.out.println(statement + " Done!");
				}
			}
			fr.close();
			System.out.println("Drop constraints. Done!");
		} catch (FileNotFoundException e) {
			System.out.println("No constraints to drop.");
			throw new OtherException(e);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new OtherException(e);
		}
		this.setChanged();
		this.notifyObservers("droping constraints");
		
	}
	
	/**
	 * Creates the indexes and keys.
	 * 
	 * @param piggyMarketSqueakFolder
	 *            the piggy market squeak folder
	 * @param connection
	 *            the connection
	 * @param extractDirName
	 *            the extract dir name
	 * 
	 * @throws SQLException
	 *             the SQL exception
	 * 
	 * @author Guillaume Thoreton
	 * @throws OtherException 
	 */
	private void createIndexesAndKeys(File piggyMarketSqueakFolder, String extractDirName, Connection connection) throws SQLException, OtherException {

		String installPath = piggyMarketSqueakFolder.getAbsolutePath() + File.separator;

		try {
			File keysFile = new File(installPath + extractDirName + File.separator + "KEYS.sql");

			BufferedReader fr = new BufferedReader(new FileReader(keysFile));
			String statement;
			while ((statement = fr.readLine()) != null) {
				if (!statement.equals("") && !statement.substring(0, 2).equals("--")) {
					try {
						PreparedStatement ps = connection.prepareStatement(statement.substring(0, statement.length() - 1));
						ps.execute();
					} catch (java.sql.SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(statement + " Done!");
				}
			}
			fr.close();
			System.out.println("Keys. Done!");

		} catch (FileNotFoundException e) {
			System.out.println("No Keys update available.");
			throw new OtherException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new OtherException(e);
		}

		this.setChanged();
		this.notifyObservers("setting keys");
		
		try {

			File indexesFile = new File(installPath + extractDirName + File.separator + "INDEXES.sql");

			BufferedReader fr = new BufferedReader(new FileReader(indexesFile));
			String statement;
			while ((statement = fr.readLine()) != null) {
				if (!statement.equals("") && !statement.substring(0, 2).equals("--")) {
					PreparedStatement ps = connection.prepareStatement(statement.substring(0, statement.length() - 1));
					ps.execute();
					System.out.println(statement + " Done!");
				}
			}
			fr.close();
			System.out.println("Indexes. Done!");

		} catch (FileNotFoundException e) {
			System.out.println("No Indexes update available.");
			throw new OtherException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new OtherException(e);
		}

		this.setChanged();
		this.notifyObservers("setting indexes");

	}


	/**
	 * Gets the fis.
	 * 
	 * @return the fis
	 */
	public InputStream getFis() {
		return fis;
	}

}
