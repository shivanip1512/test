package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.servlet.PILConnectionServlet;

import com.cannontech.stars.util.*;
import com.cannontech.stars.util.timertask.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.stars.xml.util.XMLUtil;

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
    
    // Instance of the SOAPServer object
    private static SOAPServer instance = null;
    
    // Timer object for periodical tasks
    private static Timer timer = new Timer();
    
    private static StarsTimerTask[] timerTasks = {
    	new DailyTimerTask(),
    	new LMControlHistoryTimerTask()
    };
	
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
    }

    public SOAPServer() {
        super();
    }
    
    public static SOAPServer getInstance() {
    	return instance;
    }
    
    /*
     * Start implementation of DBChangeListener
     */
    public com.cannontech.message.util.ClientConnection getClientConnection() {
		return connToDispatch;
	}

	public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase lBase)
	{
		if (((DBChangeMsg)msg).getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
			return;
		
		com.cannontech.clientutils.CTILogger.debug(" ## DBChangeMsg ##\n" + msg);
		
		if( msg.getDatabase() == msg.CHANGE_CUSTOMER_CONTACT_DB )
		{
			com.cannontech.database.data.lite.LiteCustomerContact obj = (com.cannontech.database.data.lite.LiteCustomerContact) lBase;
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
	    		if (companies[i].getEnergyCompanyID().intValue() < 0) continue;	// Don't initialize the default company now
	    		companies[i].init();
	    	}
    	}
    	
    	initDispatchConnection();    	
    	startTimers();
    	
		ServerUtils.setPILConnectionServlet( (com.cannontech.servlet.PILConnectionServlet)
    			getServletContext().getAttribute(com.cannontech.servlet.PILConnectionServlet.SERVLET_CONTEXT_ID) );
    	instance = this;
    }

	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getParameter( "RefreshCache" ) != null) {
			refreshCache();
			resp.sendRedirect( "/refresh_cache.html" );
		}
		else
			super.service(req, resp);
	}

    public SOAPMessage onMessage(SOAPMessage message) {
        StarsOperation respOper = new StarsOperation();

        try {
        	StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( message );
        	if (reqOper == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
	        	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
		//dbChangeListener = new DBChangeMessageListener();
		//dbChangeListener.start();
	}
    
    void startTimers() {
    	for (int i = 0; i < timerTasks.length; i++) {
    		if (timerTasks[i].isFixedRate()) {
    			long initRunTime = System.currentTimeMillis() + timerTasks[i].getInitialDelay();
    			long startTime = timerTasks[i].getNextScheduledTime().getTime();
    			if (initRunTime < startTime) {
    				try {
    					StarsTimerTask initTask = (StarsTimerTask) timerTasks[i].getClass().newInstance();
	    				timer.schedule( initTask, timerTasks[i].getInitialDelay() );
    				}
    				catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    			
	    		timer.scheduleAtFixedRate( timerTasks[i], timerTasks[i].getNextScheduledTime(), timerTasks[i].getTimerPeriod() );
    		}
    		else
    			timer.schedule( timerTasks[i], timerTasks[i].getInitialDelay(), timerTasks[i].getTimerPeriod() );
    	}
    }
    
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
    		
    		com.cannontech.database.db.stars.CustomerWebConfiguration[] webConfigs =
    				com.cannontech.database.db.stars.CustomerWebConfiguration.getAllCustomerWebConfigurations();
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
    
    public static ArrayList getAllLMPrograms(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
		return company.getAllLMPrograms();
    }
    
    public static ArrayList getAllApplianceCategories(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllApplianceCategories();
    }

	public static Hashtable getAllSelectionLists(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllSelectionLists();
	}
	
	public static ArrayList getAllServiceCompanies(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllServiceCompanies();
	}
	
	public static LiteStarsThermostatSettings getDefaultThermostatSettings(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getDefaultThermostatSettings();
	}
	
	public static ArrayList getAllCustomerContacts(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllCustomerContacts();
	}
	
	public static ArrayList getAllCustomerAddresses(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllCustomerAddresses();
	}
	
	public static ArrayList getAllLMHardwares(int energyCompanyID) {
    	LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
    	if (company == null) return null;
    	
    	return company.getAllLMHardwares();
	}
	
	public static ArrayList getAllWorkOrders(int energyCompanyID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getAllWorkOrders();
	}
	
	public static ArrayList getAllCustAccountInformation(int energyCompanyID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getAllCustAccountInformation();
	}
	
	public static ArrayList getAllLMControlHistory(int energyCompanyID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getAllLMControlHistory();
	}
	
	
	public static LiteWebConfiguration getWebConfiguration(int configID) {
		ArrayList webConfigList = getAllWebConfigurations();
		for (int i = 0; i < webConfigList.size(); i++) {
			LiteWebConfiguration config = (LiteWebConfiguration) webConfigList.get(i);
			if (config.getConfigID() == configID)
				return config;
		}
		
		return null;
	}
	
	public static StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
		ArrayList userList = getAllStarsYukonUsers();
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				StarsYukonUser user = (StarsYukonUser) userList.get(i);
				if (user.getUserID() == yukonUser.getUserID())
					return user;
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
	
	public static LiteInterviewQuestion[] getInterviewQuestions(int energyCompanyID, int questionType) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getInterviewQuestions( questionType );
	}
	
	
	public static LiteApplianceCategory getApplianceCategory(int energyCompanyID, int applianceCategoryID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getApplianceCategory( applianceCategoryID );
	}
	
	public static LiteServiceCompany getServiceCompany(int energyCompanyID, int serviceCompanyID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getServiceCompany( serviceCompanyID );
	}
	
	public static LiteCustomerContact getCustomerContact(int energyCompanyID, int contactID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getCustomerContact( contactID );
	}
	
	public static LiteCustomerAddress getCustomerAddress(int energyCompanyID, int addressID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getCustomerAddress( addressID );
	}
	
	public static LiteCustomerAddress addCustomerAddress(int energyCompanyID, com.cannontech.database.db.customer.CustomerAddress addr) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.addCustomerAddress( addr );
	}
	
	public static LiteLMProgram getLMProgram(int energyCompanyID, int programID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getLMProgram( programID );
	}
	
	public static LiteLMHardwareBase getLMHardware(int energyCompanyID, int inventoryID, boolean autoLoad) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getLMHardware( inventoryID, autoLoad );
	}
	
	public static LiteLMHardwareBase addLMHardware(int energyCompanyID, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.addLMHardware( hw );
	}
	
	public static LiteStarsLMControlHistory getLMControlHistory(int energyCompanyID, int groupID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getLMControlHistory( groupID );
	}
	
	public static LiteWorkOrderBase getWorkOrderBase(int energyCompanyID, int orderID) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getWorkOrderBase( orderID );
	}
	
	public static LiteWorkOrderBase addWorkOrderBase(int energyCompanyID, com.cannontech.database.db.stars.report.WorkOrderBase order) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.addWorkOrderBase( order );
	}
	
	public static LiteStarsCustAccountInformation getCustAccountInformation(int energyCompanyID, int accountID, boolean autoLoad) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.getCustAccountInformation( accountID, autoLoad );
	}
	
	public static LiteStarsCustAccountInformation addCustAccountInformation(int energyCompanyID, com.cannontech.database.data.stars.customer.CustomerAccount account) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.addCustAccountInformation( account );
	}
	
	public static LiteStarsCustAccountInformation searchByAccountNumber(int energyCompanyID, String accountNo) {
		LiteStarsEnergyCompany company = getEnergyCompany( energyCompanyID );
		if (company == null) return null;
		
		return company.searchByAccountNumber( accountNo );
	}
	
	public static void updateLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		ArrayList ctrlHist = liteCtrlHist.getLmControlHistory();
		if (ctrlHist == null) {
			ctrlHist = new ArrayList();
			liteCtrlHist.setLmControlHistory( ctrlHist );
		}
		
		int lastCtrlHistID = 0;
		if (ctrlHist.size() > 0)
			lastCtrlHistID = ((LiteLMControlHistory) ctrlHist.get(ctrlHist.size() - 1)).getLmCtrlHistID();
			
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = com.cannontech.database.db.stars.LMControlHistory.getLMControlHistory(
				new Integer(liteCtrlHist.getGroupID()), new Integer(lastCtrlHistID) );
		if (ctrlHists != null) {
			for (int i= 0; i < ctrlHists.length; i++)
				ctrlHist.add( (LiteLMControlHistory) StarsLiteFactory.createLite(ctrlHists[i]) );
		}
	}

}