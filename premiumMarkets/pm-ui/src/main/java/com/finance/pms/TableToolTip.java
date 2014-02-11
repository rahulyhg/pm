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
package com.finance.pms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class TableToolTip implements Listener {


	private Label lbContent;
	private int toolTipHashCode;
	private Shell tooltipShell;

	public TableToolTip() {
		super();
	}


	@Override
	public void handleEvent(Event event) {

		switch (event.type) {
		case SWT.MouseHover: 
			{
				buildAndShowToolTip(event);
			}
		}

	}

	protected abstract void buildAndShowToolTip(Event event);
	
	
	public Shell showTooltip(int toolTipHashCode, Shell parent, int x, int y, String txt) {
		
		this.toolTipHashCode = toolTipHashCode;
		
		tooltipShell = new Shell(parent, SWT.TOOL | SWT.ON_TOP);
		tooltipShell.setLayout(new GridLayout());
		tooltipShell.setBackground(tooltipShell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tooltipShell.setBackgroundMode(SWT.INHERIT_FORCE);

		lbContent = new Label(tooltipShell, SWT.NONE);
		lbContent.setFont(MainGui.CONTENTFONT);
		lbContent.setForeground(tooltipShell.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		lbContent.setText(txt);

		Point lbContentSize = lbContent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int width = lbContentSize.x + 10;
		int height = lbContentSize.y + 10;

		tooltipShell.setBounds(x + 5, y , width, height);
		tooltipShell.setVisible(true);
		
		return tooltipShell;
		
	}
	
	public void updateTooltip(int toolTipHashCode, String additionalText) {
		
		if (!tooltipShell.isDisposed() && tooltipShell.isVisible() && this.toolTipHashCode == toolTipHashCode) {
			try {
				String oldText = lbContent.getText();
				lbContent.setText(oldText + additionalText);
				
				tooltipShell.layout();
				tooltipShell.pack();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
	}

}
