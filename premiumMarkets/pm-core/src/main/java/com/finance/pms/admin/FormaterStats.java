/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock market technical analysis
 * major indicators, portfolio management and historical data charting.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock market technical analysis and indexes rotation,
 * With around 80% of forecasted trades above buy and hold, while back testing over DJI, 
 * FTSE, DAX and SBF, Back testing, 
 * Buy sell email notifications with automated markets and user defined portfolios scanning.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview and a free workable demo.
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

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;

//@Aspect
public class FormaterStats {
	
	private FormaterStats() {
		super();
		System.out.println(" Instancating aspect object : "+ this);
	}

	//@Around("formateLineMethods()")
    @SuppressWarnings("rawtypes")
	public Object checkFailure(ProceedingJoinPoint formater) throws Throwable {
		System.out.println("In Formating.");
		Object result = null;
        try {
        	result = formater.proceed();
			return result;
        } finally {
        	System.out.println("Out Formating.");
        	if (result != null && ((Set)result).size() == 0) {
        		recordFailureFor(formater.getTarget(), formater.getArgs());
        	}
        }
    }

	//@Pointcut("execution(* com.finance.pms.datasources.web.HttpSource.readURL(com.finance.pms.datasources.web.formaters.LineFormater, com.finance.pms.threads.MyHttpClient))")
    public void formateLineMethods(){}
	

    private void recordFailureFor(Object target, Object[] args) {
		System.out.println("Formating failed for : "+target.getClass().getSimpleName()+", "+args);
	}
}
