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

package com.finance.pms;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/* 1.4 example used by DialogDemo.java. */
public class CustomDialog extends JDialog
implements ActionListener,
PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private JOptionPane optionPane;

	private String btnString1 = "Ok";
	private String btnString2 = "No Way!";

	/** Creates the reusable dialog. 
	 * @param autoClose */
	public CustomDialog(Frame aFrame, String report,String error,String title, boolean autoClose) {
		super(aFrame, true);
		setTitle(title);

		//Create an array of the text and components to be displayed.
		Object[] array = {report,"___ Report content ___",error};

		//Create an array specifying the number of dialog buttons and their text.
		Object[] options = {btnString1, btnString2};

		//Create the JOptionPane.
		optionPane = new JOptionPane(array,JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION,null,options,options[0]);

		//Make this dialog display it.
		setContentPane(optionPane);

		//Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window,
				 * we're going to change the JOptionPane's
				 * value property.
				 */
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		//Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
		
		if (autoClose) autoCloseTiming();

	}

	/**
	 * 
	 */
	public void autoCloseTiming() {
		SwingWorker<Boolean, Boolean> swingWorker = new SwingWorker<Boolean, Boolean>() {

			@Override
			protected Boolean doInBackground() throws Exception {
				while((!isVisible() || !isEnabled()) && (!isCancelled() || !isDone())) {
					Thread.sleep(5000);
				}
				this.dispose();
				return true;
			}
			
			public void dispose() throws InterruptedException {
				while (isVisible()) {
					Thread.sleep(10000);
					setVisible(false);
				}
			}
			
		};
		
		swingWorker.execute();
	}

	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(btnString1);
	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) ||
						JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				//ignore reset
				return;
			}
			
			clearAndHide();
			
			synchronized (this) {
				this.notifyAll();
			}

		}
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);		
	}
	

	public JOptionPane getOptionPane() {
		return optionPane;
	}
}
