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
package com.finance.pms.events.calculation.parametrizedindicators;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.events.operations.Operation;
import com.finance.pms.events.operations.conditional.EventConditionHolder;
import com.finance.pms.events.operations.nativeops.NativeOperationsBasic;

@XmlRootElement
public class NativeParametrizedIndicators {
	
	private static MyLogger LOGGER = MyLogger.getLogger(NativeOperationsBasic.class);
	private static final String XMLFILE = System.getProperty("installdir")+File.separator+"nativeIndicators.xml";
	
	
	@XmlElementWrapper(name = "calculators")
	@XmlElement(name = "calculator")
	private List<Operation> calculators;

	public NativeParametrizedIndicators() {
		super();
		calculators = new ArrayList<Operation>();
	}
	
	public static void initNativeIndicators() {
		
		NativeParametrizedIndicators nativeParametrizedIndicators = new NativeParametrizedIndicators();
		
		EventConditionHolder baseEventConditionHolder = new EventConditionHolder();
		nativeParametrizedIndicators.calculators.add(baseEventConditionHolder);
		
		try {
			JAXBContext context = JAXBContext.newInstance(NativeParametrizedIndicators.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(nativeParametrizedIndicators, System.out);
			m.marshal(nativeParametrizedIndicators, new File(XMLFILE));
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
	}
	
	public static NativeParametrizedIndicators loadNativeIndicators () {

		try {
			JAXBContext context = JAXBContext.newInstance(NativeParametrizedIndicators.class);
			Unmarshaller um = context.createUnmarshaller();
			NativeParametrizedIndicators nativeOps = (NativeParametrizedIndicators) um.unmarshal(new FileReader(XMLFILE));
			return nativeOps;
		} catch (Exception e) {
			LOGGER.error(e,e);
			throw new RuntimeException(e);
		}
	}

	public Map<String, Operation> getCalculators() {
		Map<String, Operation> ret = new HashMap<String, Operation>();
		for (Operation operation : calculators) {
			//TODO lower?
			ret.put(operation.getReference(), operation);
		}
		return ret;
	}


}
