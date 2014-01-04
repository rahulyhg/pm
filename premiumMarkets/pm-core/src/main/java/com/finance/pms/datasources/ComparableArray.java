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
package com.finance.pms.datasources;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ComparableArray<T extends Comparable<T>> extends ArrayList<T> implements Comparable<ArrayList<T>> {

	private static final long serialVersionUID = -363699288012620328L;

	public ComparableArray(Collection<T> values) {
		super(values);
	}

	public ComparableArray() {
		super();
	}

	@Override
	//XXX
	public int compareTo(ArrayList<T> o) {
		//return this.hashCode() - o.hashCode();
		int size = new Integer(this.size()).compareTo(new Integer(o.size()));
		if (size != 0) {
			return size;
		} else {
			boolean equals = this.equals(o);
			if (equals) {
				return 0;
			} else {
				ArrayList<T> left = new ArrayList<T>(this);
				ArrayList<T> right = new ArrayList<T>(o);
				Collections.sort(left);
				Collections.sort(right);
				for (int i = 0; i < left.size(); i++) {
					int compareTo = left.get(i).compareTo(right.get(i));
					if (compareTo != 0) return compareTo;
				}
				throw new InvalidParameterException();
			}
		}
	}
}