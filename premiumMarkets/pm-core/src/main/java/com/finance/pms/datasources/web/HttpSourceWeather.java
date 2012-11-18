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
package com.finance.pms.datasources.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import com.finance.pms.datasources.db.Validatable;
import com.finance.pms.datasources.web.formaters.LineFormater;

public class HttpSourceWeather extends HttpSourceYahoo {


	public HttpSourceWeather(String pathToprops, MyBeanFactoryAware beanFactory) {
		super(pathToprops, beanFactory);
	}

	public String getYearHistory(Date year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(year);
		calendar.add(Calendar.YEAR, 1);
		String url = "http://www.wunderground.com/history/airport/KNYC/%s/1/1/CustomHistory.html?dayend=1&monthend=1&yearend=%s&req_city=NA&req_state=NA&req_statename=NA";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		return String.format(url, simpleDateFormat.format(year), simpleDateFormat.format(calendar.getTime()));
	}
	
	public String getMonthHistory(Date date) {
		String url = "http://www.wunderground.com/history/airport/KNYC/%s/%s/1/MonthlyHistory.html";
		return String.format(url,new SimpleDateFormat("yyyy").format(date), new SimpleDateFormat("MM").format(date));
	
	}


	@Override
	public List<Validatable> readURL(LineFormater formater) throws HttpException {
		return super.readURL(formater);
	}

}
