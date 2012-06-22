/**
 * Premium Markets is an automated financial technical analysis system. 
 * It implements a graphical environment for monitoring financial technical analysis
 * major indicators and for portfolio management.
 * In its advanced packaging, not provided under this license, it also includes :
 * Screening of financial web sites to pickup the best market shares, 
 * Forecast of share prices trend changes on the basis of financial technical analysis,
 * (with a rate of around 70% of forecasts being successful observed while back testing 
 * over DJI, FTSE, DAX and SBF),
 * Back testing and Email sending on buy and sell alerts triggered while scanning markets
 * and user defined portfolios.
 * Please refer to Premium Markets PRICE TREND FORECAST web portal at 
 * http://premiummarkets.elasticbeanstalk.com/ for a preview of more advanced features. 
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
package com.finance.pms.admin.install.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.nexes.wizard.WizardPanelDescriptor;


// TODO: Auto-generated Javadoc
/**
 * The Class LicencePanelDescriptor.
 * 
 * @author Guillaume Thoreton
 */
public class LicencePanelDescriptor extends WizardPanelDescriptor implements ActionListener {
    
    /** The Constant IDENTIFIER. */
    public static final String IDENTIFIER = "LICENCE_PANEL";
    
    /** The panel2. */
    LicencePanel panel2;
    
    /**
     * Instantiates a new licence panel descriptor.
     * 
     * @author Guillaume Thoreton
     */
    public LicencePanelDescriptor() {
        
        panel2 = new LicencePanel();
        panel2.addCheckBoxActionListener(this);
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel2);
        
    }
    
    /* (non-Javadoc)
     * @see com.nexes.wizard.WizardPanelDescriptor#getNextPanelDescriptor()
     */
    @Override
	public Object getNextPanelDescriptor() {
        return InstallFolderPanelDescriptor.IDENTIFIER;
    }
    
    /* (non-Javadoc)
     * @see com.nexes.wizard.WizardPanelDescriptor#getBackPanelDescriptor()
     */
    @Override
	public Object getBackPanelDescriptor() {
        return UpdateUrlPanelDescriptor.IDENTIFIER;
    }
    
    
    /* (non-Javadoc)
     * @see com.nexes.wizard.WizardPanelDescriptor#aboutToDisplayPanel()
     */
    @Override
	public void aboutToDisplayPanel() {
        setNextButtonAccordingToCheckBox();
        Install.selectNextButton();
    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        setNextButtonAccordingToCheckBox();
    }
            
    
    /**
     * Sets the next button according to check box.
     * 
     * @author Guillaume Thoreton
     */
    private void setNextButtonAccordingToCheckBox() {
    	getWizard().setNextFinishButtonEnabled(panel2.isCheckBoxSelected());
    	Install.selectNextButton();
    }
}
