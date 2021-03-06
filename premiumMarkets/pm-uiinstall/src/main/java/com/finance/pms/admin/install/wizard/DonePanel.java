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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

/**
 * The Class DonePanel.
 * 
 * @author Guillaume Thoreton
 */
public class DonePanel extends JPanel {
 

	private static final long serialVersionUID = 3412318352164432070L;
	
	
	private JLabel progressDescription;
    private JProgressBar progressSent;
    private JPanel contentPanel;
    private JLabel iconLabel;
    private JSeparator separator;
    private JLabel textLabel;
    private JPanel titlePanel;


	JLabel endInstallLabel;
	JTextPane endInstallTxt;
        
    public DonePanel() {
        
        super();

        contentPanel = new JPanel();
    	contentPanel.setLayout(new BorderLayout());
    	
    	endInstallLabel = new JLabel();
    	endInstallLabel.setBackground(new Color(192, 192, 192));
    	endInstallLabel.setFocusable(true);
    	endInstallLabel.setFont(endInstallLabel.getFont().deriveFont(14f));
	    endInstallLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		endInstallLabel.setText(
				"<html>"
				+ "The installation is now complete. Many thanks for your patience.<br />"
				+ "I will now try to launch the program for you.<br />"
				+ "This automatic launch may not work on some systems.<br /><b>PLEASE READ BELOW</b>"
				+ "</html>");
		contentPanel.add(endInstallLabel, BorderLayout.NORTH);
		
		endInstallTxt = new JTextPane();
		endInstallTxt.setBackground(new Color(192, 192, 192));
		endInstallTxt.setFocusable(true);
		endInstallTxt.setAlignmentY(Component.CENTER_ALIGNMENT);
	    endInstallTxt.setContentType("text/html");
	    contentPanel.add(endInstallTxt, BorderLayout.SOUTH);
	    
	    
        ImageIcon icon = getImageIcon();
        
        titlePanel = new javax.swing.JPanel();
        textLabel = new javax.swing.JLabel();
        iconLabel = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        titlePanel.setLayout(new java.awt.BorderLayout());
        titlePanel.setBackground(Color.gray);
        
        textLabel.setBackground(Color.gray);
        textLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        textLabel.setText("Installation completed.");
        textLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        textLabel.setOpaque(true);

        iconLabel.setBackground(Color.gray);
        if (icon != null) iconLabel.setIcon(icon);
        
        titlePanel.add(textLabel, BorderLayout.CENTER);
        titlePanel.add(iconLabel, BorderLayout.EAST);
        titlePanel.add(separator, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        JPanel secondaryPanel = new JPanel();
        secondaryPanel.add(contentPanel, BorderLayout.NORTH);
        add(secondaryPanel, BorderLayout.WEST);
        
    }  

    public void setProgressText(String s) {
        progressDescription.setText(s);
    }

    public void setProgressValue(int i) {
        progressSent.setValue(i);
    }
    
    private ImageIcon getImageIcon() {        
        return null;
    }
   
    
}
