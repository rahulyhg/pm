package com.finance.pms.portfolio;

import org.springframework.beans.factory.annotation.Autowired;

import com.finance.pms.admin.config.Config;
import com.finance.pms.admin.config.EventSignalConfig;
import com.finance.pms.datasources.shares.SharesListId;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.threads.ConfigThreadLocal;

public class ShareListDefaultMgr extends ShareListMgr {
	
	@Autowired
	PortfolioDAO portfolioDAO;
	
	@Override
	protected void removeForeignKeysUpdate(PortfolioShare toRemovePortfolioShare) {
			
		SharesList unknownShareList = portfolioDAO.loadShareList(SharesListId.UNKNOWN.name());
		Stock stock = toRemovePortfolioShare.getStock();
		PortfolioShare unknownCounterpartPortfolioShare = unknownShareList.getListShares().get(stock);
		if (unknownCounterpartPortfolioShare == null) {
			unknownShareList.addShare(stock);
			unknownCounterpartPortfolioShare = unknownShareList.getListShares().get(stock);
		}
		portfolioDAO.saveOrUpdatePortfolioShare(unknownCounterpartPortfolioShare);
	
	}
	
	@Override
	protected void addForeignKeysUpdate(PortfolioShare newPortfolioShare) {
		
		//We leave as it in Unknown the stock can be in several portfolios.
		
	}

	@Override
	public EventSignalConfig initPkgDependentConfig() {
		
		EventSignalConfig config;
		try {
			config = (EventSignalConfig) ConfigThreadLocal.get(Config.EVENT_SIGNAL_NAME);
		} catch (IllegalArgumentException e) {
			config = new EventSignalConfig();
			ConfigThreadLocal.set(Config.EVENT_SIGNAL_NAME, config);
		}
		
		return config;
	}



}
