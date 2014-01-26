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
package com.finance.pms.portfolio.gui.charts;

import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.finance.pms.ActionDialog;
import com.finance.pms.ActionDialogAction;
import com.finance.pms.CursorFactory;
import com.finance.pms.MainGui;
import com.finance.pms.MainPMScmd;
import com.finance.pms.PopupMenu;
import com.finance.pms.admin.config.EventSignalConfig;
import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.db.DataSource;
import com.finance.pms.datasources.db.StripedCloseLogRoc;
import com.finance.pms.datasources.db.StripedCloseRelativeToReferee;
import com.finance.pms.datasources.db.StripedCloseRelativeToStart;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.events.quotations.NoQuotationsException;
import com.finance.pms.events.quotations.Quotations;
import com.finance.pms.events.quotations.QuotationsFactories;
import com.finance.pms.portfolio.gui.ActionDialogForm;
import com.finance.pms.portfolio.gui.SlidingPortfolioShare;

public class ChartPerfDisplay extends ChartDisplayStrategy {
	
	private static final String NOT_DEFINED = "Not Defined";

	private static MyLogger LOGGER = MyLogger.getLogger(ChartPerfDisplay.class);
	
	private Quotations refereeQuotations;
	private boolean isShutDown = false;

	private Button closeFunctionBut;
	private String cmpModeToolTipRoot;
	

	public ChartPerfDisplay(ChartsComposite chartTarget) {
		super();
		this.chartTarget = chartTarget;
		populatePopups(chartTarget.getPopusGroup());
		this.chartTarget.getMainChartWraper().initMainPlot(ChartMain.PERCENTAGE_FORMAT, "No data available. Check that the portfolio stocks and sliding date ranges. There may be no quotations available.");
	}

	@Override
	public void highLight(Integer idx, Stock selectedShare, Boolean recalculationGranted) {

		try {

			this.chartTarget.getShell().setEnabled(false);
			this.chartTarget.getParent().getParent().setCursor(CursorFactory.getCursor(SWT.CURSOR_WAIT));

			chartTarget.getMainChartWraper().setMainYAxisLabel("");
			if ( idx == null || selectedShare == null ) {
				return;
			}

			chartTarget.setHighligtedId(idx);
			chartTarget.getHightlitedEventModel().setViewParamRoot(selectedShare);

			if (chartTarget.isDisposed() || !chartTarget.isVisible()) {
				return;
			}

			Boolean isShowing = checkIfShowing(selectedShare);
			if (!isShowing) {
				for (SlidingPortfolioShare slidingPortfolioShare : chartTarget.getCurrentTabShareList()) {
					if (slidingPortfolioShare.getStock().equals(selectedShare)) {
						slidingPortfolioShare.setDisplayOnChart(true);
						break;
					}
				}
			}

			chartTarget.getStripedCloseFunction().updateStartDate(chartTarget.getSlidingStartDate());
			chartTarget.getStripedCloseFunction().updateEndDate(chartTarget.getSlidingEndDate());
			chartTarget.getMainChartWraper().updateLineDataSet(chartTarget.getCurrentTabShareList(), chartTarget.getStripedCloseFunction(), getIsApplyColor(), chartTarget.getPlotChart());

			chartTarget.getMainChartWraper().highLightSerie(chartTarget.getHighligtedId(), 3);

		} finally {

			this.chartTarget.getShell().setEnabled(true);
			this.chartTarget.getParent().getParent().setCursor(CursorFactory.getCursor(SWT.CURSOR_ARROW));

		}
	}

	@Override
	public void initRefreshAction() {
		//Nothing
	}

	@Override
	public void endRefreshAction(List<Exception> exceptions) {
		//Nothing
	}

	@Override
	public void populatePopups(final Composite popupButtonsGroup) {
		
		cleanPopupButtonsGroup(popupButtonsGroup);
		
		cmpModeToolTipRoot = "This is to set the comparison mode between several stocks.\nUse the 'Hide / Show stock' button to display several stock on the chart.\n";
		
		{
			closeFunctionBut = new Button(popupButtonsGroup, SWT.PUSH);
			closeFunctionBut.setFont(MainGui.DEFAULTFONT);
			closeFunctionBut.setText("Comparison mode ...");
			
			final Set<TransfoInfo> transfos = new HashSet<TransfoInfo>(Arrays.asList(new TransfoInfo[]{
					
					new TransfoInfo("Change to Buy price", new ActionDialogAction() {

						@Override
						public void action(Control targetControl) {
							chartTarget.setStripedCloseFunction( new StripedCloseRelativeToBuyPrice());
							chartTarget.updateCharts( true, true, false);
						}
					}),
					new TransfoInfo("Change to Period start", new ActionDialogAction() {

						@Override
						public void action(Control targetControl) {
							chartTarget.setStripedCloseFunction( new StripedCloseRelativeToStart(chartTarget.getSlidingStartDate(), chartTarget.getSlidingEndDate()));
							chartTarget.updateCharts( true, true, false);
						}
					}),
					new TransfoInfo("Change to Previous day (log ROC)", new ActionDialogAction() {

						@Override
						public void action(Control targetControl) {

							final ActionDialogForm actionDialogForm = new ActionDialogForm(chartTarget.getShell(), "Ok", null, "Root at zero");
							ActionDialogAction actionDialogAction = new ActionDialogAction() {
								@Override
								public void action(Control targetControl) {
									actionDialogForm.values[0] = Boolean.valueOf(((Button)actionDialogForm.controls[0]).getSelection()).toString();
									chartTarget.setStripedCloseFunction( new StripedCloseLogRoc(actionDialogForm.values[0].equals(Boolean.TRUE.toString())));
									chartTarget.updateCharts( true, true, false);
								}
							};
							Button zeroBut =  new Button(actionDialogForm.getParent(), SWT.CHECK | SWT.LEAD);
							zeroBut.setText("Root at zero");
							zeroBut.setFont(MainGui.DEFAULTFONT);
							zeroBut.setSelection(true);
							actionDialogForm.setControl(zeroBut);
							actionDialogForm.setAction(actionDialogAction);
							actionDialogForm.open();
							
						}
					}),
					new TransfoInfo("Change to Referee", new ActionDialogAction() {

						@Override
						public void action(Control targetControl) {

							String refereeRef = MainPMScmd.getPrefs().get("charts.referee", NOT_DEFINED+"||-||"+NOT_DEFINED);
							
							String symbolSplit = NOT_DEFINED;
							String isinSplit = NOT_DEFINED;
							try {
								symbolSplit = refereeRef.split("\\|\\|-\\|\\|")[0];
								isinSplit = refereeRef.split("\\|\\|-\\|\\|")[1];
							} catch (Exception e) {
								LOGGER.warn("Invalid previous referee : "+refereeRef);
							}
							
							final String previousSymbol = symbolSplit;
							final String previousIsin = isinSplit;
							final Stock previousReferee = relativeToRefereeSetting(previousSymbol, previousIsin);
							if (previousReferee != null) chartTarget.updateCharts( true, true, false);
							
							ActionDialogAction actionDialogAction = new ActionDialogAction() {
								@Override
								public void action(Control targetControl) {
									chartTarget.getParent().getParent().setCursor(CursorFactory.getCursor(SWT.CURSOR_WAIT));
									try {
										selectNewReferee(previousReferee);
									} finally {
										chartTarget.getParent().getParent().setCursor(CursorFactory.getCursor(SWT.CURSOR_ARROW));
									}
								}
							};
							
							ActionDialog actionDialogForm = new ActionDialog(
									chartTarget.getShell(), 
									"Select a new referee ...", null, "Current referee : "+((previousReferee!=null)?previousReferee.getFriendlyName():NOT_DEFINED), "Select a new referee ...", actionDialogAction);

							actionDialogForm.open();

						}
					})}));

			closeFunctionBut.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {		
					selectComparisonMode(closeFunctionBut, transfos);
				}

				private void selectComparisonMode(final Button closeFunctionBut, final Set<TransfoInfo> transfos) {
					
					final Set<TransfoInfo> selectTransfo = new HashSet<TransfoInfo>();
					for (TransfoInfo transfoInfo : transfos) {
						if (chartTarget.getStripedCloseFunction().lineToolTip().toLowerCase().contains(transfoInfo.info().toLowerCase())) {
							selectTransfo.add(transfoInfo);
						}
					}
					
					ActionDialogAction actionDialogAction = new ActionDialogAction() {
						
						@Override
						public void action(Control targetControl) {
							for (TransfoInfo selctTransUnic : selectTransfo) {
								selctTransUnic.action.action(null);
							}
							
						}
					};
					PopupMenu<TransfoInfo> popupMenu = new PopupMenu<TransfoInfo>(chartTarget, closeFunctionBut, transfos, selectTransfo, true, false, SWT.RADIO, actionDialogAction);
					popupMenu.open();
						
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					selectComparisonMode(closeFunctionBut, transfos);
				}
			});

		}

		{
			final Button hideStock = new Button(popupButtonsGroup, SWT.PUSH);
			hideStock.setFont(MainGui.DEFAULTFONT);
			hideStock.setText("Hide / Show stock ...");
			hideStock.setToolTipText("This will hide and show stocks on the chart for comparison purpose.\nNote that the selected line in the portfolio can't be hidden");
			
			hideStock.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {		
					hideShowShares(hideStock);
				}

				private void hideShowShares(final Button hideStock) {
					
					final Set<SlidingPortfolioShare> displayedShares = new HashSet<SlidingPortfolioShare>();
					final List<SlidingPortfolioShare> currentTabShareList = chartTarget.getCurrentTabShareList();
					
					SlidingPortfolioShare selectedPS = chartTarget.getCurrentLineSelection();
					final TreeSet<SlidingPortfolioShare> availShares = new TreeSet<SlidingPortfolioShare>(currentTabShareList);
					if (selectedPS != null) availShares.remove(selectedPS);
					
					if (availShares.size() > 0) {
		
						for (SlidingPortfolioShare slidingPortfolioShare : availShares) {
							if (slidingPortfolioShare.getDisplayOnChart()) {
								displayedShares.add(slidingPortfolioShare);
							} 
						}
						
						ActionDialogAction action = new ActionDialogAction() {
							
							@Override
							public void action(Control targetControl) {
								if (!isShutDown) {
									for (SlidingPortfolioShare slidingPortfolioShare : availShares) {
										if (displayedShares.contains(slidingPortfolioShare)) {
											slidingPortfolioShare.setDisplayOnChart(true);
										} else {
											slidingPortfolioShare.setDisplayOnChart(false);
										}
									}
									chartTarget.updateCharts(true, true, false);
								}
							}
						};
						PopupMenu<SlidingPortfolioShare> popupMenu =  new PopupMenu<SlidingPortfolioShare>(chartTarget, hideStock, availShares , displayedShares, true, true, SWT.CHECK, action);
						popupMenu.open();
						
					}

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					hideShowShares(hideStock);
				}
			});

		}
		
		popupButtonsGroup.layout();
		
	}
	
	Stock relativeToRefereeSetting(String previousSymbol, String previousIsin) {

		if (previousSymbol != null && !NOT_DEFINED.equals(previousSymbol)) {//already set

			if (refereeQuotations != null && refereeQuotations.size() != 0) {//Initialised

				try {
					chartTarget.setStripedCloseFunction(new StripedCloseRelativeToReferee(refereeQuotations, chartTarget.getSlidingStartDate(), chartTarget.getSlidingEndDate()));
				} catch (InvalidAlgorithmParameterException e) {
					LOGGER.error("",e);
				}

			} else {//Set but not initialised
				
				try {
					Stock stock = DataSource.getInstance().getShareDAO().loadStockBy(previousSymbol, previousIsin);
					loadRefereeQuotations(stock);
				} catch (Exception e) {
					LOGGER.warn("Unable to initialise referee "+previousSymbol+ " " + previousIsin);
					chartTarget.setStripedCloseFunction(new StripedCloseRelativeToBuyPrice());
				}
			}

		} 

		return  (refereeQuotations != null)?refereeQuotations.getStock():null;

	}	
	
	void loadRefereeQuotations(Stock stock) throws InvalidAlgorithmParameterException {
		try {
			if (null == stock) throw new InvalidAlgorithmParameterException("Referee can't be null");
			refereeQuotations  = QuotationsFactories.getFactory().getQuotationsInstance(stock,ChartsComposite.DEFAULT_START_DATE, EventSignalConfig.getNewDate(),true,stock.getMarketValuation().getCurrency(),0,0);
			chartTarget.setStripedCloseFunction(new StripedCloseRelativeToReferee(refereeQuotations, chartTarget.getSlidingStartDate(), chartTarget.getSlidingEndDate()));
		} catch (NoQuotationsException e) {
			throw new RuntimeException(e);
		}
	}
	

	private void selectNewReferee(Stock previousReferee) {
		//Open selection window
		NewRefereeDialog.showUI(chartTarget.getShell(), chartTarget, this);
	}
	
	@Override
	public void resetChart() {
		
		for (SlidingPortfolioShare sShare : chartTarget.getCurrentTabShareList()) {
			sShare.setDisplayOnChart(true);
		}
		
		//TODO keep object an state
		chartTarget.setStripedCloseFunction(new StripedCloseRelativeToStart(chartTarget.getSlidingStartDate(), chartTarget.getSlidingEndDate()));
		
		lightResetChart();
		
	}
	

	@Override
	public void lightResetChart() {
		
		chartTarget.getMainChartWraper().resetBarChart();
		chartTarget.getMainChartWraper().resetIndicChart();
		
		chartTarget.getMainChartWraper().updateLineDataSet(chartTarget.getCurrentTabShareList(), chartTarget.getStripedCloseFunction(), getIsApplyColor(), chartTarget.getPlotChart());
		
	}

	@Override
	public Boolean getIsApplyColor() {
		return true;
	}

	@Override
	public int retreivePreviousSelection() {
		
		if (chartTarget.getHightlitedEventModel().getViewParamRoot() != null) {
			
			Stock stock = chartTarget.getHightlitedEventModel().getViewParamRoot();
			int cpt = 0;
			for (SlidingPortfolioShare slidingPortfolioShare : chartTarget.getCurrentTabShareList()) {
				if (stock.equals(slidingPortfolioShare.getStock())) return cpt;
				cpt++;
			}
		} 
		
		return -1;
		
	}

	@Override
	public void exportBarChartPng() {
		// NotImplemented
	}

	@Override
	public void refreshView(List<Exception> exceptions) {
		// Nothing
		
	}

	@Override
	public void shutDownDisplay() {

		for (SlidingPortfolioShare slidingPortfolioShare : chartTarget.getCurrentTabShareList()) {
			slidingPortfolioShare.setDisplayOnChart(false);
		}
		
		chartTarget.getMainChartWraper().resetBarChart();
		chartTarget.getMainChartWraper().resetIndicChart();
		
		isShutDown = true;
		
	}

	@Override
	public void updateButtonsToolTips() {
		
		if (!isShutDown) {
			closeFunctionBut.setText("Comparison mode ...");
			closeFunctionBut.setToolTipText(cmpModeToolTipRoot +"The current comparison mode is '"+chartTarget.getStripedCloseFunction().lineToolTip()+"'");
			chartTarget.chartBoutonsGroup.setSize(chartTarget.chartBoutonsGroup.getBounds().width, chartTarget.chartBoutonsGroup.getBounds().height);
			chartTarget.chartBoutonsGroup.layout();
		}
		
	}


}
