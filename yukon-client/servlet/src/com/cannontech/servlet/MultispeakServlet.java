/*
 * Created on Aug 23, 2006
 */
package com.cannontech.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.multispeak.client.MultispeakBean;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.service.CB_MR;
import com.cannontech.multispeak.service.CB_MRLocator;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.CB_MRSoap_PortType;
import com.cannontech.multispeak.service.CD_CBSoap_BindingStub;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.service.MR_OASoap_BindingStub;
import com.cannontech.multispeak.service.MspObject;
import com.cannontech.multispeak.service.OA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.service.OD_OASoap_BindingStub;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.util.ServletUtil;

/**
 * Servlet to create, update, delete multispeak interfaces.
 * Provides methods to implement pingURL and getMethods web services.
 * @author stacey
 */
public class MultispeakServlet extends HttpServlet
{
	static final String LF = System.getProperty("line.separator");

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		String action = req.getParameter("ACTION");
		String retPage = handleMSPAction(action, req);
		resp.sendRedirect( retPage);
		return;
	}
	
	private String handleMSPAction(String action, HttpServletRequest req)
	{
		java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
			String ele = enum1.nextElement().toString();
			 CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
		  }

        HttpSession session = req.getSession(false);
        session.removeAttribute(ServletUtil.ATT_ERROR_MESSAGE);
        String redirect = req.getParameter("REDIRECT");
        if (redirect == null)
            redirect = req.getContextPath() + req.getRequestURL();

        MultispeakBean mspBean = (MultispeakBean)session.getAttribute("multispeakBean");
        if( mspBean == null) {
            session.setAttribute("multispeakBean", new MultispeakBean());
            mspBean = (MultispeakBean)session.getAttribute("multispeakBean");
        }
        
        if( action.equalsIgnoreCase("CB_MR")) {
            String command = req.getParameter("command");
            MultispeakVendor mspVendor;
            try {
                mspVendor = MultispeakFuncs.getMultispeakVendor("SEDC", null);
                if( mspVendor == null)
                    mspVendor = MultispeakFuncs.getMultispeakVendor("NISC", null);
                if( mspVendor == null) {
                    session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* No Multispeak Interface to CIS is defined.");
                    return redirect;
                }

                String deviceIDStr = req.getParameter("deviceID");
                Integer deviceID = null; 
                if( deviceIDStr != null) {
                    deviceID = new Integer(deviceIDStr);
                    MspObject object = handleCB_MR(command, deviceID.intValue(), mspVendor, session);
                    if( object != null) {
                        if( object instanceof Customer)
    //                        session.setAttribute("CustomerDetail", MultispeakFuncs.customerToString((Customer)object)));
                        session.setAttribute("CustomerDetail", MultispeakFuncs.customerToString((Customer)object).replaceAll("/r/n", "<BR>"));
                    }
                }
                return redirect;
            } catch (RemoteException e) {
                CTILogger.error(e);
            }
        }
        
//		load all parameters from req
        String vendorIDStr = req.getParameter("vendorID");
        Integer vendorID = null;
        if( vendorIDStr != null && vendorIDStr.length() > 0)
            vendorID = Integer.valueOf(vendorIDStr);
		String companyName = req.getParameter("mspCompanyName");
        String appName = req.getParameter("mspAppName");
		String uniqueKey = req.getParameter("mspUniqueKey");
		String password = req.getParameter("mspPassword");
		String username = req.getParameter("mspUserName");
        String outPassword = req.getParameter("outPassword");
        String outUsername = req.getParameter("outUserName");
		String mspURL = req.getParameter("mspURL");
		String[] mspInterfaces = req.getParameterValues("mspInterface");
		String[] mspEndpoints = req.getParameterValues("mspEndpoint");
//        load action parameters from req
        String mspService = req.getParameter("actionService");
        String mspEndpoint = req.getParameter("actionEndpoint");
        int mspPrimaryCIS = 0;
        if (req.getParameter("mspPrimaryCIS") != null)
        	mspPrimaryCIS = Integer.valueOf(req.getParameter("mspPrimaryCIS")).intValue();
        int mspPaoNameAlias = 0;
        if (req.getParameter("mspPaoNameAlias") != null)
        	mspPaoNameAlias = Integer.valueOf(req.getParameter("mspPaoNameAlias")).intValue();
        
        if( !mspURL.endsWith("/"))
            mspURL += "/";
        String serviceURL = mspURL + mspEndpoint;
        
        if( vendorID != null)
            mspBean.setSelectedVendorID(vendorID.intValue());
        MultispeakVendor mspVendor = new MultispeakVendor(vendorID,companyName, appName, 
                                                          username, password, outUsername, outPassword, 
                                                          uniqueKey, 0, mspURL);

        List<MultispeakInterface> mspInterfaceList = new ArrayList<MultispeakInterface>();
        if( mspInterfaces != null) {
            for (int i = 0; i < mspInterfaces.length; i++ )
            {
                MultispeakInterface mspInterface = new MultispeakInterface(vendorID, mspInterfaces[i], mspEndpoints[i]);
                mspInterfaceList.add(mspInterface);
            }
        }
        mspVendor.setMspInterfaces(mspInterfaceList);
        mspBean.setSelectedMspVendor(mspVendor);

        if( companyName.length() <= 0){
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* Invalid company name length, at least one character required.");
            return redirect;
        }
        
        if( action.equalsIgnoreCase("Save")) {
            MultispeakFuncs.getMultispeakDao().updateMultispeakVendor(mspVendor);
            if ( mspPrimaryCIS != mspBean.getPrimaryCIS() || mspPaoNameAlias != mspBean.getPaoNameAlias()){
    			try
    			{
    				boolean breakTime = false;
    				LiteYukonGroup yukGrp = DaoFactory.getAuthDao().getGroup( YukonGroupRoleDefs.GRP_YUKON );
    				YukonGroup yukGrpPersist = (YukonGroup)LiteFactory.createDBPersistent( yukGrp );
    				//fill out the DB Persistent with data
    				yukGrpPersist = (YukonGroup)Transaction.createTransaction( Transaction.RETRIEVE, yukGrpPersist ).execute();

    				for( int j = 0; j < yukGrpPersist.getYukonGroupRoles().size(); j++ ){
    					YukonGroupRole grpRole = (YukonGroupRole)yukGrpPersist.getYukonGroupRoles().get(j);
    					if( MultispeakRole.MSP_PRIMARY_CB_VENDORID == grpRole.getRolePropertyID().intValue() ) {
							grpRole.setValue(String.valueOf(mspPrimaryCIS));
							if( !breakTime )
								breakTime = true;
							else
								break;							
    					} else if( MultispeakRole.MSP_PAONAME_ALIAS == grpRole.getRolePropertyID().intValue() ) {
							grpRole.setValue(String.valueOf(mspPaoNameAlias));
							if( !breakTime )
								breakTime = true;
							else
								break;
    					}
    				}
					//update any changed values in the DB
					DaoFactory.getDbPersistentDao().performDBChange(yukGrpPersist, Transaction.UPDATE);
    			}
    			catch (Exception e)
    			{
    				CTILogger.error( "Unable to connect to DISPATCH.  MSPPrimaryCIS role not saved", e );					
    			}
            }
		}
        else if( action.equalsIgnoreCase("Create")) {
            redirect = req.getContextPath() + "/msp_setup.jsp?vendor="+ createMultispeakInterface(session, mspVendor);
        }
        else if( action.equalsIgnoreCase("Delete")) {
            deleteMultispeakInterface(session, mspVendor);
            mspBean.setSelectedVendorID(1);   //set to default
        }        
        else if( action.equalsIgnoreCase("pingURL")) {
            pingURL(session, mspVendor, mspService, serviceURL);
        }        
		else if( action.equalsIgnoreCase("getMethods")) {
            getMethods(session, mspVendor, mspService, serviceURL);
		}
		return redirect;
	}
    
    /**
     * Inserts the mspVendor into the database.
     * @param session The request session.
     * @param mspVendor The multispeakVendor to create.
     */
    private int createMultispeakInterface(HttpSession session, MultispeakVendor mspVendor) {
        /*try{
            if( MultispeakFuncs.getMultispeakDao().getMultispeakVendor(mspVendor.getCompanyName())!= null){
                session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* A vendor with the name '" + mspVendor.getCompanyName() + "' and app name '" + mspVendor.getAppName() + "' already exists.  Please enter a different company name.");
                return "/msp_setup_new.jsp";
            }
        }catch(NotFoundException nfe) {*/
            MultispeakFuncs.getMultispeakDao().addMultispeakVendor(mspVendor);
            return mspVendor.getVendorID().intValue();
        /*}
        return "/msp_setup.jsp";*/
    }
    
    /**
     * Deletes the mspVendor from the database.
     * @param session The request session.
     * @param mspVendor The multispeakVendor to delete.
     */
    private void deleteMultispeakInterface (HttpSession session, MultispeakVendor mspVendor) {
        String companyName = mspVendor.getCompanyName();
        if( companyName.equalsIgnoreCase("cannon"))
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* The default interface '" + companyName + "' cannot be deleted.");
        else
            MultispeakFuncs.getMultispeakDao().deleteMultispeakVendor(mspVendor.getVendorID().intValue());
    }

    private MspObject handleCB_MR(String command, int deviceID, MultispeakVendor mspVendor, HttpSession session) {
        MspObject mspObject = null;
        
        LiteDeviceMeterNumber liteDevMeterNum = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceID);
        if (liteDevMeterNum == null) {
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* MeterNumber not found for Device (ID:" + deviceID + ")");
            return mspObject;
        }
        
        String meterNumber = liteDevMeterNum.getMeterNumber();
        MultispeakInterface mspInterface = (MultispeakInterface)mspVendor.getMspInterfaceMap().get(MultispeakDefines.CB_MR_STR);
        String endpointURL = "";
        if( mspInterface != null)
            endpointURL = mspVendor.getUrl() + mspInterface.getMspEndpoint();
        
        try {
            CB_MR service = new CB_MRLocator();
            ((CB_MRLocator)service).setCB_MRSoapEndpointAddress(endpointURL);                
            CB_MRSoap_PortType port = service.getCB_MRSoap();
            ((CB_MRSoap_BindingStub)port).setHeader(MultispeakFuncs.getHeader(mspVendor));
            ((CB_MRSoap_BindingStub)port).setTimeout(MultispeakVendor.TIMEOUT);

            if( command.equalsIgnoreCase("getServiceLocationByMeterNo")) {
                port.getServiceLocationByMeterNo(meterNumber);
            } else if ( command.equalsIgnoreCase("getCustomerByMeterNo")) {
                mspObject = port.getCustomerByMeterNo(meterNumber);
            }
            
//            port.getServiceLocationByAccountNumber()
                            
//            ArrayOfMeter mspMeters = port.getMeterByServLoc(serviceLocationStr);

        } catch (ServiceException e) {
        	CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getXXXByMeterNo failed.");
			CTILogger.error("ServiceExceptionDetail: " + e.getMessage());
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "CB_MR service is not defined for company name: " + mspVendor.getCompanyName()+ ".  Method cancelled.");
        } catch (RemoteException e) {
        	CTILogger.error("TargetService: " + endpointURL + " - getXXXByMeterNo (" + mspVendor.getCompanyName() + ")");
			CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "CB_MR service is not defined for company name: " + mspVendor.getCompanyName()+ ".  Method cancelled.");
        }
        return mspObject;
    }
    
    /**
     * Implements the pingURL webservice
     * @param session The request session. 
     * @param mspVendor The multispeak vendor to invoke. 
     * @param mspService The multispeak webservice to invoke.
     * @param serviceURL The url for the mspService
     */
    private void pingURL(HttpSession session, MultispeakVendor mspVendor, String mspService, String serviceURL){
        try {
            ArrayOfErrorObject objects = pingURL(mspVendor, serviceURL, mspService);
            if( objects != null && objects.getErrorObject() != null  && objects.getErrorObject().length > 0){
                String result = "";
                for (int i = 0; i < objects.getErrorObject().length; i++) {
                    result += objects.getErrorObject(i).getObjectID() + " - " + objects.getErrorObject(i).getErrorString();
                }
                session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, result);
                session.setAttribute("resultColor", "red");
            }
            else {
                session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, "* " + mspService + " pingURL Successful");
                session.setAttribute("resultColor", "blue");
            }
        }catch (RemoteException re) {
            session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
            session.setAttribute("resultColor", "red");
        }
    }
    
    /**
     * Implements the getMethod webservice
     * @param session The request session. 
     * @param mspVendor The multispeak vendor to invoke. 
     * @param mspService The multispeak webservice to invoke.
     * @param serviceURL The url for the mspService
     */
    private void getMethods(HttpSession session, MultispeakVendor mspVendor, String mspService, String serviceURL){
        try {
            ArrayOfString objects = getMethods(mspVendor, serviceURL, mspService);
            if( objects != null && objects.getString() != null)
            {
                String resultStr = mspService + " available methods:\n";
                if( objects.getString().length > 0)
                {
                    resultStr += " * " + objects.getString(0) + "\n";
                    for (int i = 1; i < objects.getString().length; i++)
                        resultStr += " * " + objects.getString(i) + "\n";
                }
                session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, resultStr);
                session.setAttribute("resultColor", "blue");
            }
            else
            {
                session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, "* No methods reported for " + mspService +" getMethods:\n" + mspService + " is not supported.");
                session.setAttribute("resultColor", "red");
            }                
        }catch (RemoteException re) {
            session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, re.getMessage());
            session.setAttribute("resultColor", "red");
        }

    }
    
    /**
     * Utility to implement the pingURL method for the service.
     * @param serviceURL The url of for the webservice to be called.
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public static ArrayOfErrorObject pingURL(MultispeakVendor mspVendor, String serviceURL, String service) throws RemoteException
    {
        ArrayOfErrorObject objects = new ArrayOfErrorObject();
        SOAPHeaderElement header = MultispeakFuncs.getHeader(mspVendor);
        URL instanceURL = null;
        try {
            instanceURL = new URL(serviceURL);
        } catch (MalformedURLException e) {
            CTILogger.error(e);
        }
        
        if( service.equalsIgnoreCase(MultispeakDefines.OD_OA_STR)) {
            OD_OASoap_BindingStub instance = new OD_OASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_OD_STR)) {
            OA_ODSoap_BindingStub instance = new OA_ODSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_EA_STR)) {
            MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.EA_MR_STR)) {
            EA_MRSoap_BindingStub instance = new EA_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_CB_STR)) {
            MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_MR_STR)) {
            CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CD_CB_STR)) {
            CD_CBSoap_BindingStub instance = new CD_CBSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_CDSoap_BindingStub instance = new CB_CDSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_OA_STR)) {
            MR_OASoap_BindingStub instance = new MR_OASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_MR_STR)) {
            OA_MRSoap_BindingStub instance = new OA_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.pingURL();
        }
        return objects;
    }
    
    /**
     * Utility to implement the getMethods method for the service.
     * @param mspVendor The multispeak vendor to invoke. 
     * @param serviceURL The url of for the webservice to be called.
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public static ArrayOfString getMethods(MultispeakVendor mspVendor, String serviceURL, String service) throws RemoteException
    {
        ArrayOfString objects = new ArrayOfString();
        SOAPHeaderElement header = MultispeakFuncs.getHeader(mspVendor);
        URL instanceURL = null;
        try {
            instanceURL = new URL(serviceURL);
        } catch (MalformedURLException e) {
            CTILogger.error(e);
        }
        if( service.equalsIgnoreCase(MultispeakDefines.OD_OA_STR)) {
            OD_OASoap_BindingStub instance = new OD_OASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_OD_STR)) {
            OA_ODSoap_BindingStub instance = new OA_ODSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_EA_STR)) {
            MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.EA_MR_STR)) {
            EA_MRSoap_BindingStub instance = new EA_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_CB_STR)) {
            MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_MR_STR)) {
            CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CD_CB_STR)) {
            CD_CBSoap_BindingStub instance = new CD_CBSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_CDSoap_BindingStub instance = new CB_CDSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_OA_STR)) {
            MR_OASoap_BindingStub instance = new MR_OASoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_MR_STR)) {
            OA_MRSoap_BindingStub instance = new OA_MRSoap_BindingStub(instanceURL, new Service());
            instance.setHeader(header);
            objects = instance.getMethods();
        }
        return objects;
    }   
}
