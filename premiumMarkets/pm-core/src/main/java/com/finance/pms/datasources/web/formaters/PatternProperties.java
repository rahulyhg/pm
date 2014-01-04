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
package com.finance.pms.datasources.web.formaters;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class PatternProperties.
 * 
 * @author Guillaume Thoreton
 */
public class PatternProperties extends Properties {
	
	

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4212322995153221739L;

	/**
	 * Instantiates a new pattern properties.
	 * 
	 * @param projectRelqtiveFilePath the project relqtive file path
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @author Guillaume Thoreton
	 */
	public PatternProperties(String projectRelqtiveFilePath)  throws IOException {
		super();
		this.load(projectRelqtiveFilePath);
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
	 */
	@Override
	public String getProperty(String key, String uParam) {
		Object[] parmA= new String[1];
		parmA[0]=uParam;
		return this.getProperty(key, parmA);
	}



	/**
	 * Gets the property.
	 * 
	 * @param key the key
	 * @param variables the variables
	 * 
	 * @return the property
	 */
	public String getProperty(String key, Object ...variables) {
		String prop = super.getProperty(key);
		if (null != prop) {
			//MessageFormat mf = new MessageFormat(prop);
			prop = MessageFormat.format(prop,variables);
		}
		return prop;
	}

	/**
	 * Load.
	 * 
	 * @param projectRelativeFilePath the project relqtive file path
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @author Guillaume Thoreton
	 */
	public void load(String projectRelativeFilePath) throws IOException {
		this.load(this.getClass().getClassLoader().getResourceAsStream(projectRelativeFilePath));
	}

	
	

}
