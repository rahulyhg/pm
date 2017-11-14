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
package com.finance.pms.datasources.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.finance.pms.admin.install.logging.MyLogger;
import com.finance.pms.datasources.db.DataSource;
import com.finance.pms.datasources.db.TableLocker;
import com.finance.pms.datasources.db.Validatable;
import com.finance.pms.datasources.shares.MarketQuotationProviders;
import com.finance.pms.datasources.shares.SharesListId;
import com.finance.pms.datasources.shares.Stock;
import com.finance.pms.datasources.shares.StockCategories;
import com.finance.pms.datasources.shares.StockList;
import com.finance.pms.datasources.shares.YahooMarketExtentions;
import com.finance.pms.datasources.web.formaters.DayQuoteYahooCrumbFormater;
import com.finance.pms.events.calculation.DateFactory;
import com.finance.pms.portfolio.PortfolioMgr;
import com.finance.pms.portfolio.SharesList;


/**
 * The MarketListProvider is implemented here solely to handle the UNKNOWN market list (stocks in no particular lists)
 */
public class ProvidersYahooCrumb extends Providers implements QuotationProvider, MarketListProvider {

    private static MyLogger LOGGER = MyLogger.getLogger(ProvidersYahooCrumb.class);

    protected ProvidersYahooCrumb() {
        super();
    }

    public ProvidersYahooCrumb(String pathToProps) {
        super();
        this.httpSource = new HttpSourceYahooCrumb(pathToProps, this);
    }

    @Override
    public void getQuotes(Stock stock, Date start, Date end) throws HttpException, SQLException {

        //TODO scrapeLast();

        if (stock.getSymbol() == null) throw new RuntimeException("Error : no Symbol for "+stock.toString());

        MyUrl url;
        try {
            url = resolveUrlFor(stock, start, end);
        } catch (InvalidAlgorithmParameterException e) {
            return;
        }

        TreeSet<Validatable> queries = initValidatableSet();
        queries.addAll(readPage(stock, url, start));

        LOGGER.guiInfo("Getting last quotes : Number of new quotations for "+stock.getSymbol()+" :"+queries.size());
        LOGGER.debug(queries);

        try {
            ArrayList<TableLocker> tablet2lock = new ArrayList<TableLocker>();
            tablet2lock.add(new TableLocker(DataSource.QUOTATIONS.TABLE_NAME,TableLocker.LockMode.NOLOCK));
            DataSource.getInstance().executeInsertOrUpdateQuotations(new ArrayList<Validatable>(queries), tablet2lock);
        } catch (SQLException e) {

            LOGGER.error("Yahoo quotations sql error trying : "+url.getUrl(), e);
            throw e;
        }

    }

    public List<Validatable> readPage(Stock stock, MyUrl url, Date  start) throws HttpException {
        DayQuoteYahooCrumbFormater dsf = new DayQuoteYahooCrumbFormater(url, stock, stock.getMarketValuation().getCurrency().name());
        return this.httpSource.readURL(dsf);
    }

    @Override
    public void retrieveAndCompleteStockInfo(Stock stock, StockList stockList) {

        //No check available for Yahoo
        if (!stock.isFieldSet("isin") || !stock.isFieldSet("symbol") || !stock.isFieldSet("name")) {
            LOGGER.warn("No completion check on symbol, isin, name is available for the Yahoo provider. Please provide the full info (symbol, isin, name) for each stock : "+stock);

        } else {
            List<Validatable> listReq = new ArrayList<Validatable>();

            if (!stockList.contains(stock)) { // not already in base

                //check for last former quotation
                Date pastLastQuotationDate = DataSource.getInstance().getLastQuotationDateFromQuotations(stock, false);
                stock.setLastQuote(pastLastQuotationDate);

                LOGGER.info("New ticker : "+stock.toString()+" and will be added with last quote : "+ pastLastQuotationDate);

                listReq.add(stock);
                stockList.add(stock);

            } else { // already in base : update name
                stockList.get(stockList.indexOf(stock)).setName(stock.getName());
            }

            try {
                DataSource.getInstance().getShareDAO().saveOrUpdateStocks(listReq);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public String getStockRefName(Stock ticker){
        return ticker.getSymbol();
    }

    public MyUrl resolveUrlFor(Stock stock, Date start, Date end) throws InvalidAlgorithmParameterException {

        MyUrl url;

        //Date and symbol set up
        Date today = DateFactory.midnithDate(new Date());
        if (start.after(end) || ( start.compareTo(end) == 0 && end.compareTo(today) == 0 ) ) throw new InvalidAlgorithmParameterException();

        String symbol = stock.getSymbol();
        if (symbol.endsWith("."+YahooMarketExtentions.EURONEXT.getSpecificMarketExtension())) {
            String regex = "\\."+YahooMarketExtentions.EURONEXT.getSpecificMarketExtension()+"$";
            String replacement = "."+YahooMarketExtentions.PAR.getSpecificMarketExtension();
            symbol = symbol.replaceAll(regex, replacement);
        }
        if ('^' != symbol.charAt(0) && stock.getCategory().equals(StockCategories.INDICES_OTHER)) symbol = "^"+symbol;

        //Crumb
        HttpGet httpGet = new HttpGet(((HttpSourceYahooCrumb)this.httpSource).getYahooQuoteCrumbURL());
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(30000);
        requestBuilder = requestBuilder.setSocketTimeout(30000);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(30000);
        requestBuilder = requestBuilder.setCookieSpec(CookieSpecs.DEFAULT);

        HttpClientBuilder builder = HttpClientBuilder.create();     
        builder.setDefaultRequestConfig(requestBuilder.build());
        CloseableHttpClient closeableHttpClient = builder.build();

        try {
            CloseableHttpResponse crumbResponse = closeableHttpClient.execute(httpGet);
            Header responseCookies = crumbResponse.getFirstHeader("Set-Cookie");
            String cookie = responseCookies.getValue().split(";")[0];

            String crumb = getCrumb(crumbResponse.getEntity().getContent());
            
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(start);
            ZonedDateTime zonedStart = ZonedDateTime.of(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH)+1, calendarStart.get(Calendar.DAY_OF_MONTH),0 , 0, 0, 0, TimeZone.getTimeZone("EST").toZoneId());

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(end);
            ZonedDateTime zonedEnd = ZonedDateTime.of(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH)+1, calendarEnd.get(Calendar.DAY_OF_MONTH),0 , 0, 0, 0, TimeZone.getTimeZone("EST").toZoneId());

            //Call
            url =  ((HttpSourceYahooCrumb)this.httpSource)
                    .getYahooQuoteURL(symbol, zonedStart.toEpochSecond(), zonedEnd.toEpochSecond(), cookie, crumb);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return url;
    }

    String getCrumb(InputStream html) {

        String crumb = null;

        try {
            //"CrumbStore":{"crumb":"9Gm6dOkKT.K"}
            //Illegal character : Exr3SkLUf\u002Fm
            Pattern pattern = Pattern.compile("CrumbStore\":\\{\"crumb\":\"([^\"]+)\"\\}");

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(html, StandardCharsets.UTF_8))) {
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    Matcher lineMatch = pattern.matcher(line);
                    if (lineMatch.find()) {
                        crumb = lineMatch.group(1);
                        System.out.println(crumb);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return crumb;
    }

    @Override
    public StockList retrieveStockListFromWeb(MarketQuotationProviders marketQuotationsProviders, StockList stocksInDB) throws HttpException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateStockListFromWeb(MarketQuotationProviders marketQuotationsProviders) throws HttpException {
        // TODO Auto-generated method stub

    }

    @Override
    public MarketQuotationProviders defaultMarketQuotationProviders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addIndices(SortedSet<Indice> indices, Boolean replace) {
        // TODO Auto-generated method stub

    }

    @Override
    public SortedSet<Indice> getIndices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SharesListId getSharesListIdEnum() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void retrieveStockListFromBase(StockList dbStockList) {
        // TODO Auto-generated method stub

    }

    @Override
    public Stock supplement(Stock stock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SharesList loadSharesListForThisListProvider() {
        SharesList shareList = PortfolioMgr.getInstance().getPortfolioDAO().loadShareList(SharesListId.UNKNOWN.name());
        if (shareList == null) shareList = new SharesList(SharesListId.UNKNOWN.name());
        return shareList;
    }

    @Override
    public StockList retreiveStockListFromFile(String pathToFileList, StockList dbStockList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void retrieveScreeningInfo(Collection<Stock> shareListInDB) {
        // TODO Auto-generated method stub

    }

}
