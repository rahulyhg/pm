/**
 * Premium Markets is an automated stock market analysis system.
 * It implements a graphical environment for monitoring stock markets technical analysis
 * major indicators, for portfolio management and historical data charting.
 * In its advanced packaging -not provided under this license- it also includes :
 * Screening of financial web sites to pick up the best market shares, 
 * Price trend prediction based on stock markets technical analysis and indices rotation,
 * Back testing, Automated buy sell email notifications on trend signals calculated over
 * markets and user defined portfolios. 
 * With in mind beating the buy and hold strategy.
 * Type 'Premium Markets FORECAST' in your favourite search engine for a free workable demo.
 * 
 * Copyright (C) 2008-2014 Guillaume Thoreton
 * 
 * This file is part of Premium Markets.
 * 
 * Premium Markets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.finance.pms.admin.install.wizard;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;


/**
 * The Class IntroPanel.
 * 
 * @author Guillaume Thoreton
 */
public class UpdateUrl extends JPanel {
 
	private static final long serialVersionUID = -6101824066434845051L;
	
    private JPanel contentPanel;
    
    JPanel jPanel1;
    private JLabel iconLabel;
    private ImageIcon icon;
    
    JLabel jLabel1;

    JLabel jLabel2;
    
    JLabel label3;
    JLabel label4;
    JLabel label5;
    
    JLabel label6;
    JButton buttonOk;
    JButton buttonNo;
    
	MyObs observable;
	String versionNumber;
    
	
    public UpdateUrl() {
    	
    	observable = new MyObs();
        
        iconLabel = new JLabel();
        contentPanel = getContentPanel();
        contentPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        icon = getImageIcon();

        setLayout(new java.awt.BorderLayout());

        if (icon != null)
            iconLabel.setIcon(icon);
        
        iconLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        
        add(iconLabel, BorderLayout.WEST);
        
        JPanel secondaryPanel = new JPanel();
        secondaryPanel.add(contentPanel, BorderLayout.NORTH);
        add(secondaryPanel, BorderLayout.CENTER);
    }
    
    private JPanel getContentPanel() {
    	
    	JPanel contentPanel1 = new JPanel();
    	contentPanel1.setLayout(new java.awt.BorderLayout());
    	
        jPanel1 = new JPanel();
        jPanel1.setLayout(new java.awt.GridLayout(0, 1));
        
        jLabel1 = new JLabel();
        jLabel1.setText("Checking for updates ...");
        jPanel1.add(jLabel1);
        
        jLabel2 = new JLabel();
        jPanel1.add(jLabel2);
        label3 = new JLabel();
        label3.setText("");
        jPanel1.add(label3);
        label4 = new JLabel();
        label4.setText("");
        jPanel1.add(label4);
        label5 = new JLabel();
        jPanel1.add(label5);
        
		label6 = new JLabel();
        label6.setText("Download latest?");
        label6.setVisible(false);
        jPanel1.add(label6);

        buttonOk = new JButton("Ok");
        buttonOk.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				Runnable runnable = new Runnable() {
					public void run() {
						runLatest();
					}
				};
				
				Thread thread = new Thread(runnable);
				thread.start();
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ed) {
					ed.printStackTrace();
				}
				
				System.exit(0);
				
			}
        	
        });
        buttonOk.setVisible(false);
        
        buttonNo = new JButton("No");
        buttonNo.addMouseListener(new MouseAdapter() {
        	@Override
			public void mouseClicked(MouseEvent e) {
        		observable.setChanged();
        		observable.notifyObservers();
        	}
        });
        buttonNo.setVisible(false);
        
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new java.awt.GridLayout(0, 2));
        jPanel2.add(buttonOk);
        jPanel2.add(buttonNo);
        
        jPanel1.add(jPanel2);
        
        
        
        contentPanel1.add(jPanel1,BorderLayout.SOUTH);

        return contentPanel1;
        
    }

	public void downLoadLatest(final String vn, Observer observer) {
	     
		versionNumber = vn;
    	observable.addObserver(observer);
    	
    	buttonNo.setVisible(true);
    	buttonOk.setVisible(true);
    	label6.setVisible(true);

	}
    
	private void runLatest() {
		
		 try {
			 
			 String siteUrl = "none.com";
			 try {
				 Properties pbuild = new Properties();
				 pbuild.load(this.getClass().getResourceAsStream("/pmsbuild.properties"));
				 siteUrl = pbuild.getProperty("site.url");
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
				
			URI uri = new URI("http://"+siteUrl+"/html/swtui.html#Download");
			ProgressPanel.open(uri);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		 
	}
    
    private ImageIcon getImageIcon() {
    	URL resource = this.getClass().getClassLoader().getResource(Install.iconFile+".png");
    	if (resource != null){
    		return new ImageIcon(resource);
    	} else {
    		return new ImageIcon();
    	}
    }
    
    class ActivatedHyperlinkListener implements HyperlinkListener {

    	  JEditorPane editorPane;

    	  public ActivatedHyperlinkListener(JEditorPane editorPane) {
    	    this.editorPane = editorPane;
    	  }

    	  public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
    	    HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
    	    final URL url = hyperlinkEvent.getURL();
    	    if (type == HyperlinkEvent.EventType.ENTERED) {
    	      System.out.println("URL: " + url);
    	    } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
    	      System.out.println("Activated");
    	      Document doc = editorPane.getDocument();
    	      try {
    	        editorPane.setPage(url);
    	      } catch (IOException ioException) {
    	        System.out.println("Error following link");
    	        editorPane.setDocument(doc);
    	      }
    	    }
    	  }
    	}
    
    class MyObs extends Observable {

		@Override
		public synchronized void setChanged() {
			super.setChanged();
		}
    	
    }
 
}
