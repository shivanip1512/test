package com.cannontech.stars.web.servlet;

import java.util.ArrayList;
import java.util.Timer;

import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.task.HourlyTimerTask;
import com.cannontech.stars.util.task.RefreshTimerTask;
import com.cannontech.stars.util.task.StarsTimerTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPServer extends JAXMServlet implements ReqRespListener, com.cannontech.database.cache.DBChangeListener {
	
	public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
	
	public static final int YUK_LIST_ENTRY_ID_EMAIL = 1;
	public static final int YUK_LIST_ENTRY_ID_PHONE = 2;
	public static final int YUK_LIST_ENTRY_ID_FAX = 4;
	public static final int YUK_LIST_ENTRY_ID_HOME_PHONE = 5;
	public static final int YUK_LIST_ENTRY_ID_WORK_PHONE = 6;
	
	public static final int YUK_WEB_CONFIG_ID_COOL = -1;
	public static final int YUK_WEB_CONFIG_ID_HEAT = -2;
	
	private static boolean clientLocal = true;
    
    // Instance of the SOAPServer object
    private static SOAPServer instance = null;
    
    // Timer object for periodical tasks
    private static Timer timer = new Timer();
    
    private StarsTimerTask[] timerTasks = {
    	new HourlyTimerTask(),
    	new RefreshTimerTask()
    };
	
    private PILConnectionServlet connToPIL = null;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	
	// Array of all the energy companies
	private static LiteStarsEnergyCompany[] energyCompanies = null;
    
    // List of web configurations
    private static ArrayList webConfigList = null;
	
    // List of stars yukon users
    private static ArrayList starsUserList = null;
    
    
    public static void refreshCache() {
    	if (energyCompanies != null) {
    		for (int i = 0; i < energyCompanies.length; i++)
    			energyCompanies[i].clear();
    	}
    	energyCompanies = null;
		webConfigList = null;
    	starsUserList = null;
    	
    	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
    	com.cannontech.common.constants.YukonListFuncs.releaseAllConstants();
    }

    public SOAPServer() {
        super();
    }

	public static boolean isClientLocal() {
		return clientLocal;
	}

	public static void setClientLocal(boolean clientLocal) {
		SOAPServer.clientLocal = clientLocal;
	}
    
    public static SOAPServer getInstance() {
    	return instance;
    }
    
    public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
	}
	
	public com.cannontech.message.porter.ClientConnection getPILConnection() {
		if (connToPIL == null) return null;
		return connToPIL.getConnection();
	}
	
	void initDispatchConnection() 
	{
		String host = null;
		int port;
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			host = bundle.getString("dispatch_machine");
			port = (new Integer(bundle.getString("dispatch_port"))).intValue();
		}
		catch ( java.util.MissingResourceException mre )
		{
			mre.printStackTrace();
			host = "127.0.0.1";
			port = 1510;
		}
		catch ( NumberFormatException nfe )
		{
			nfe.printStackTrace();
			port = 1510;
		}
	
		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
	
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("Yukon STARS");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );
		
		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);
	
		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);	
	}
    
	public static void runTimerTask(StarsTimerTask timerTask) {
		if (timerTask.isFixedRate()) {
			/* Run the first time after the initial delay,
			 * then run periodically at a fixed rate, e.g. at every midnight
			 */
/*			long initRunTime = System.currentTimeMillis() + timerTask.getInitialDelay();
			long startTime = timerTask.getNextScheduledTime().getTime();
			if (initRunTime < startTime) {
				try {
					StarsTimerTask initTask = (StarsTimerTask) timerTask.getClass().newInstance();
    				timer.schedule( initTask, timerTask.getInitialDelay() );
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}*/
    		timer.scheduleAtFixedRate( timerTask, timerTask.getNextScheduledTime(), timerTask.getTimerPeriod() );
		}
		else if (timerTask.getTimerPeriod() == 0) {
			/* Run just once after the initial delay,
			 * If initial delay set to 0, has the same effect as creating a new thread
			 */
			timer.schedule( timerTask, timerTask.getInitialDelay() );
		}
		else {
			/* Run the first time after the initial delay,
			 * then run periodically at a fixed delay, e.g. every 5 minutes
			 */
			timer.schedule( timerTask, timerTask.getInitialDelay(), timerTask.getTimerPeriod() );
		}
	}

    /*
     * Start implementation of ReqRespListener
     */
    public void init() throws javax.servlet.ServletException {
    	if (instance != null) return;
    	
		getAllWebConfigurations();
    	LiteStarsEnergyCompany[] companies = getAllEnergyCompanies();
    	if (companies != null) {
	    	for (int i = 0; i < companies.length; i++) {
	    		if (companies[i].getLiteID() == DEFAULT_ENERGY_COMPANY_ID
	    			|| companies[i].getUserID() != com.cannontech.user.UserUtils.USER_YUKON_ID)
		    		companies[i].init();
	    	}
    	}
    	
    	connToPIL = (com.cannontech.servlet.PILConnectionServlet)
    			getServletContext().getAttribute(com.cannontech.servlet.PILConnectionServlet.SERVLET_CONTEXT_ID);
    			
    	initDispatchConnection();
    	
    	for (int i = 0; i < timerTasks.length; i++)
    		runTimerTask( timerTasks[i] );
    	
    	instance = this;
    }

    public SOAPMessage onMessage(SOAPMessage message) {
        StarsOperation respOper = new StarsOperation();

        try {
        	StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( message );
        	if (reqOper == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_NODE_NOT_FOUND, "Invalid request format") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Thanks for waking me up :)" );
            respOper.setStarsSuccess( success );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	try {
	        	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	        			StarsConstants.FAILURE_CODE_RUNTIME_ERROR, "Server error: cannot process request") );
	        	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	catch (Exception e2) {
        		e2.printStackTrace();
        	}
        }

        return null;
    }
    
    /*
     * Start implementation of class functions
     */
    public static LiteStarsEnergyCompany[] getAllEnergyCompanies() {
    	if (energyCompanies == null) {
	    	java.sql.Connection conn = null;
	    	
    		try {
	    		conn = com.cannontech.database.PoolManager.getInstance().getConnection(
	    				com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		    	com.cannontech.database.db.company.EnergyCompany[] companies =
		    			com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies( conn );
		    	if (companies == null) return null;
		    	
		    	energyCompanies = new LiteStarsEnergyCompany[ companies.length ];
		    	for (int i = 0; i < energyCompanies.length; i++)
		    		energyCompanies[i] = new LiteStarsEnergyCompany( companies[i] );
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		finally {
    			try {
    				if (conn != null) conn.close();
    			}
    			catch (Exception e2) {
    				e2.printStackTrace();
    			}
    		}
	    	
	    	CTILogger.info( "All energy companies loaded" );
    	}
    	
    	return energyCompanies;
    }
    
    public static ArrayList getAllWebConfigurations() {
    	if (webConfigList == null) {
    		webConfigList = new ArrayList();
    		
    		com.cannontech.database.db.web.YukonWebConfiguration[] webConfigs =
    				com.cannontech.database.db.web.YukonWebConfiguration.getAllCustomerWebConfigurations();
    		for (int i = 0; i < webConfigs.length; i++) {
    			LiteWebConfiguration webConfig = new LiteWebConfiguration();
    			webConfig.setConfigID(webConfigs[i].getConfigurationID().intValue() );
    			webConfig.setLogoLocation( webConfigs[i].getLogoLocation() );
    			webConfig.setDescription( webConfigs[i].getDescription() );
    			webConfig.setAlternateDisplayName( webConfigs[i].getAlternateDisplayName() );
    			webConfig.setUrl( webConfigs[i].getURL() );
    			
    			webConfigList.add( webConfig );
    		}
	    	
	    	CTILogger.info( "All customer web configurations loaded" );
    	}
    	
    	return webConfigList;
    }
    
    public static ArrayList getAllStarsYukonUsers() {
    	if (starsUserList == null)
    		starsUserList = new ArrayList();
    	return starsUserList;
    }
    
    public static LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
    	LiteStarsEnergyCompany[] companies = getAllEnergyCompanies();
    	if (companies != null) {
    		for (int i = 0; i < companies.length; i++)
    			if (companies[i].getEnergyCompanyID().intValue() == energyCompanyID)
    				return companies[i];
    	}
    	
    	return null;
    }
    
    public static LiteStarsEnergyCompany getDefaultEnergyCompany() {
    	return getEnergyCompany( DEFAULT_ENERGY_COMPANY_ID );
    }
	
	public static LiteWebConfiguration getWebConfiguration(int configID) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = (LiteWebConfiguration) webConfigList.get(i);
				if (config.getConfigID() == configID)
					return config;
			}
		}
		
		return null;
	}
	
	public static void addWebConfiguration(LiteWebConfiguration config) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) { webConfigList.add(config); }
	}
	
	public static LiteWebConfiguration deleteWebConfiguration(int configID) {
		ArrayList webConfigList = getAllWebConfigurations();
		synchronized (webConfigList) {
			for (int i = 0; i < webConfigList.size(); i++) {
				LiteWebConfiguration config = (LiteWebConfiguration) webConfigList.get(i);
				if (config.getConfigID() == configID) {
					webConfigList.remove( i );
					return config;
				}
			}
		}
		
		return null;
	}
	
	public static StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				StarsYukonUser user = (StarsYukonUser) userList.get(i);
				if (user.getUserID() == yukonUser.getUserID())
					return new StarsYukonUser(user);
			}
		}
		
		StarsYukonUser user = new StarsYukonUser( yukonUser );
		addStarsYukonUser( user );
		return user;
	}
	
	public static void addStarsYukonUser(StarsYukonUser user) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) { userList.add( user ); }
	}
	
	public static void deleteStarsYukonUser(int userID) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				StarsYukonUser user = (StarsYukonUser) userList.get(i);
				if (user.getUserID() == userID) {
					userList.remove(i);
					return;
				}
			}
		}
	}
	
	public static void updateLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		ArrayList ctrlHist = liteCtrlHist.getLmControlHistory();
		
		int lastCtrlHistID = 0;
		if (ctrlHist.size() > 0)
			lastCtrlHistID = ((LiteLMControlHistory) ctrlHist.get(ctrlHist.size() - 1)).getLmCtrlHistID();
			
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = com.cannontech.stars.util.LMControlHistoryUtil.getLMControlHistory(
				new Integer(liteCtrlHist.getGroupID()), new Integer(lastCtrlHistID) );
		if (ctrlHists != null) {
			for (int i= 0; i < ctrlHists.length; i++)
				ctrlHist.add( (LiteLMControlHistory) StarsLiteFactory.createLite(ctrlHists[i]) );
		}
	}


	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
		if (msg.getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
			return;
		if (msg.getDatabase() != DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB)
			return;
		
		CTILogger.debug(" ## DBChangeMsg ##\n" + msg);
		
		LiteStarsCustAccountInformation liteAcctInfo = null;
		LiteStarsEnergyCompany energyCompany = null;
		
		LiteStarsEnergyCompany[] companies = getAllEnergyCompanies();
		for (int i = 0; i < companies.length; i++) {
			liteAcctInfo = companies[i].getCustAccountInformation( msg.getId(), false );
			if (liteAcctInfo != null) {
				energyCompany = companies[i];
				break;
			}
		}
		
		// If the customer account information is not loaded yet, we don't need to care about it
		if (liteAcctInfo == null) return;
		
		if( msg.getCategory().equalsIgnoreCase(DBChangeMsg.CAT_CUSTOMER_ACCOUNT) )
		{
			handleCustomerAccountChange( msg, energyCompany, liteAcctInfo );
		}
	}
	
	private void handleCustomerAccountChange( DBChangeMsg msg, LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo )
	{
		LiteBase lBase = null;
	
		switch( msg.getTypeOfChange() )
		{
			case DBChangeMsg.CHANGE_TYPE_ADD:
				// Don't need to do anything, since customer account information is load-on-demand
				break;
				
			case DBChangeMsg.CHANGE_TYPE_UPDATE:
				energyCompany.getAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() ).retrieve();
				liteAcctInfo.getCustomerAccount().retrieve();
				
				LiteCustomerContact litePrimContact = energyCompany.getCustomerContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
				litePrimContact.retrieve();
				ArrayList contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
				for (int i = 0; i < contacts.size(); i++)
					energyCompany.deleteCustomerContact( ((Integer) contacts.get(i)).intValue() );
				liteAcctInfo.getCustomer().retrieve();
				contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
				for (int i = 0; i < contacts.size(); i++) {
					LiteCustomerContact liteContact = new LiteCustomerContact( ((Integer) contacts.get(i)).intValue() );
					liteContact.retrieve();
					energyCompany.addCustomerContact( liteContact );
				}
				
				energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() ).retrieve();
				liteAcctInfo.getAccountSite().retrieve();
				
				liteAcctInfo.getSiteInformation().retrieve();
				
				energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				break;
				
			case DBChangeMsg.CHANGE_TYPE_DELETE:
				energyCompany.deleteCustAccountInformation( liteAcctInfo );
				energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
				break;
		}
	}
}