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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.finance.pms.admin.install.logging.MyLogger;

/**
 * The Class ErrorDialog.
 * 
 * @author Guillaume Thoreton
 */
public class UserDialog extends Dialog {
	

	protected static MyLogger LOGGER = MyLogger.getLogger(UserDialog.class);


	private Text errorTxt;
	private Text addMsgTxt;
	private String erreur;
	private String addMessage;
	protected Button valideButton1;

	public UserDialog(Shell parent, String erreur, String addMessage) {
		//super(new Shell(new Shell(parent,SWT.ON_TOP), SWT.SHEET | SWT.RESIZE));
		//super(new Shell(new Shell(parent, SWT.ON_TOP), SWT.SHELL_TRIM | SWT.RESIZE));
		super(new Shell(parent, SWT.SHELL_TRIM));
		this.getParent().setText("Premium Markets - Warning");
		this.erreur = erreur;
		this.addMessage = addMessage;
	}
	
//	public UserDialog(Shell parent, int style, String erreur, String addMessage) {
//		super(new Shell(new Shell(parent,SWT.ON_TOP), style));
//		this.getParent().setText("Premium Markets - Warning");
//		this.erreur = erreur;
//		this.addMessage = addMessage;
//	}
	
	protected UserDialog(Shell parent, String title, String erreur, String addMessage) {
		//super(new Shell(new Shell(parent,SWT.ON_TOP), style));
		super(new Shell(parent, SWT.SHELL_TRIM));
		this.getParent().setText(title);
		this.erreur = erreur;
		this.addMessage = addMessage;
	
	}
	
//	public void open() {
//		open(true);
//	}

	public void open() {
		try {

			GridLayout dialogShellLayout = new GridLayout();
			this.getParent().setLayout(dialogShellLayout);
			this.getParent().setBackground(MainGui.pOPUP_BG);
			
			if (erreur != null) {
				errorTxt = new Text(getParent(), SWT.WRAP);
				GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
				errorTxt.setLayoutData(layoutData);
				errorTxt.setFont(MainGui.DEFAULTFONT);
				errorTxt.setBackground(MainGui.pOPUP_BG);
				errorTxt.setText(cleanMsg(this.erreur, true));
				errorTxt.setEditable(false);
				
			}
			if (addMessage != null) {
				addMsgTxt = new Text(getParent(), SWT.WRAP| SWT.V_SCROLL);
				addMsgTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
				addMsgTxt.setFont(MainGui.DEFAULTFONT);
				addMsgTxt.setBackground(new Color(getParent().getDisplay(),(int) (MainGui.pOPUP_BG.getRed()*1.05),(int)(MainGui.pOPUP_BG.getGreen()*1.05),(int) (MainGui.pOPUP_BG.getBlue()*1.05)));
				addMsgTxt.setEditable(false);
				addMsgTxt.setCapture(false);
				addMsgTxt.setText(cleanMsg(this.addMessage, false));
			}
			{
				valideButton1 = new Button(getParent(), SWT.PUSH | SWT.CENTER);
				GridData validerbutton1LData = new GridData(SWT.CENTER, SWT.BOTTOM, false, false);
				valideButton1.setLayoutData(validerbutton1LData);
				validationButtonTxtAndAction();
			}
			
//			Listener listener = new Listener() {
//				
//				@Override
//				public void handleEvent(Event event) {
//					if (focusOnAckButton) {
//						valideButton1.setCapture(true);
//						valideButton1.forceFocus();
//					}
//				}
//			};
//			this.getParent().addListener(SWT.FocusIn,listener);
//			this.getParent().addListener(SWT.FOCUSED,listener);
//			this.getParent().addListener(SWT.Activate,listener);
			
			layout();
			
			Point pt = getParent().getDisplay().getCursorLocation();
			int x = Math.max(0, Math.min(getParent().getDisplay().getBounds().width-getParent().getSize().x, pt.x-getParent().getSize().x-10));
			int y = Math.max(0, Math.min(getParent().getDisplay().getBounds().height-getParent().getSize().y, pt.y+10));
			getParent().setLocation(x, y);
			
			getParent().open();
			
			getParent().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						getParent().setActive();
					} catch (Exception e) {
						LOGGER.warn(e, e);
					}
				}
			});
			
			
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}

	protected void layout() {
		
		if (errorTxt != null) {
			errorTxt.pack();
			if (errorTxt.getSize().y < 100) ((GridData)errorTxt.getLayoutData()).heightHint = 100;
		}
	
		if (addMsgTxt != null) {
			//Point computeSize = addMsgTxt.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			//addMsgTxt.setSize(addMsgTxt.getSize().x, Math.max(computeSize.y, 200));
			addMsgTxt.pack();
			addMsgTxt.setSize(addMsgTxt.getSize().x+10, Math.max(addMsgTxt.getSize().y, 200));
		}
		
		
		getParent().layout();
		getParent().pack();
		
	}
	
	public void updateDialog(String title, String erreur, String addMessage) {
		
		this.getParent().setText(title);
		this.erreur = erreur;
		this.addMessage = addMessage;
		
		if (erreur != null && errorTxt != null)  {
			errorTxt.setText(cleanMsg(this.erreur, true));
		}
		if (addMessage != null && addMsgTxt != null) {
			addMsgTxt.setText(cleanMsg(this.addMessage, false));
		}
		valideButton1.setText("Ok");
		
		layout();
		
		getParent().setActive();
		valideButton1.setFocus();
		
	}

	private String cleanMsg(String message, Boolean noCR) {
	
		String cleanMessage = message;
		if (!noCR) cleanMessage = message.replaceAll("\\. ", ".\n");
		cleanMessage = cleanMessage.replaceAll("[A-Za-z\\.]+Exception: ", "");
		cleanMessage = cleanMessage.replaceAll("\\[", "").replaceAll("\\]", "");
		
		return cleanMessage;
	}

	protected void validationButtonTxtAndAction() {
		
		valideButton1.setText("Ok");
		valideButton1.setFont(MainGui.DEFAULTFONT);
		valideButton1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent evt) {
				validerbutton1MouseDown();
			}
		});
		valideButton1.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("Key released : "+e.keyCode+","+e.character+","+e.doit);
				if ((e.keyCode == SWT.CR || e.keyCode == SWT.SPACE)) {
					validerbutton1MouseDown();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("Key pressed : "+e.keyCode+","+e.character+","+e.doit);
			}
		
		});
			
	}

	protected void validerbutton1MouseDown() {
		getParent().dispose();
	}

	public boolean sameDialog(String erreur, String addMessage) {
		
		if (this.addMessage == null) {
			if (addMessage != null)
				return false;
		} else if (!this.addMessage.equals(addMessage))
			return false;
		if (this.erreur == null) {
			if (erreur != null)
				return false;
		} else if (!this.erreur.equals(erreur))
			return false;
		return true;
	}

}
