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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.jfree.report.util.StringUtil;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.DuplicateException;
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
import com.cannontech.multispeak.dao.impl.MultispeakDaoImpl;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.deploy.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_OASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MspObject;
import com.cannontech.multispeak.deploy.service.OA_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_OASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

/**
 * Servlet to create, update, delete multispeak interfaces.
 * Provides methods to implement pingURL and getMethods web services.
 * @author stacey
 */
public class MultispeakServlet extends HttpServlet
{
	static final String LF = System.getProperty("line.separator");

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException 
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
            MultispeakDaoImpl multispeakDao = (MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao");
            MultispeakFuncs multispeakFuncs = (MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs");
            
            String command = req.getParameter("command");
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());

            String deviceIDStr = req.getParameter("deviceID");
            Integer deviceID = null; 
            if( deviceIDStr != null) {
                deviceID = new Integer(deviceIDStr);
                MspObject object = handleCB_MR(command, deviceID.intValue(), mspVendor, session);
                if( object != null) {
                    if( object instanceof Customer)
                    	session.setAttribute("CustomerDetail", multispeakFuncs.customerToString((Customer)object).replaceAll("/r/n", "<BR>"));
                    else if( object instanceof ServiceLocation)
                    	session.setAttribute("ServLocDetail", multispeakFuncs.serviceLocationToString((ServiceLocation)object).replaceAll("/r/n", "<BR>"));
                }
            }
            return redirect;
        }
        
//		load all parameters from req
        String vendorIdStr = req.getParameter("mspVendor");
        Integer vendorId = null;
        if( StringUtils.isNotBlank(vendorIdStr) && StringUtils.isNumeric(vendorIdStr)) {
            vendorId = Integer.valueOf(vendorIdStr);
        }

        if( action.equalsIgnoreCase("ChangeVendor")) {
            MultispeakDaoImpl multispeakDao = (MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao");
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
            mspBean.setSelectedMspVendor(mspVendor);
            return redirect;
        }
        
        String companyName = req.getParameter("mspCompanyName");
        String appName = req.getParameter("mspAppName");
		String password = req.getParameter("mspPassword");
		String username = req.getParameter("mspUserName");
        
        String maxReturnRecordsStr = req.getParameter("mspMaxReturnRecords");
        int maxReturnRecords = 10000;
        if( StringUtils.isNotBlank(maxReturnRecordsStr) && StringUtils.isNumeric(maxReturnRecordsStr))
            maxReturnRecords = Integer.parseInt(maxReturnRecordsStr);
        
        String requestMessageTimeoutStr = req.getParameter("mspRequestMessageTimeout");
        long requestMessageTimeout = 120000;
        if( StringUtils.isNotBlank(requestMessageTimeoutStr) && StringUtils.isNumeric(requestMessageTimeoutStr))
            requestMessageTimeout = Long.parseLong(requestMessageTimeoutStr);

        String maxInitiateRequestObjectsStr = req.getParameter("mspMaxInitiateRequestObjects");
        long maxInitiateRequestObjects = 15;
        if( StringUtils.isNotBlank(maxInitiateRequestObjectsStr) && StringUtils.isNumeric(maxInitiateRequestObjectsStr))
            maxInitiateRequestObjects = Long.parseLong(maxInitiateRequestObjectsStr);

        String templateNameDefault = req.getParameter("mspTemplateNameDefault");

        String outPassword = req.getParameter("outPassword");
        String outUsername = req.getParameter("outUserName");
		String mspURL = req.getParameter("mspURL");
		String[] mspInterfaces = req.getParameterValues("mspInterface");
		String[] mspEndpoints = req.getParameterValues("mspEndpoint");
        
        if( !mspURL.endsWith("/"))
            mspURL += "/";
        
        MultispeakVendor mspVendor = new MultispeakVendor(vendorId,companyName, appName, 
                                                          username, password, outUsername, outPassword, 
                                                          maxReturnRecords, requestMessageTimeout,
                                                          maxInitiateRequestObjects, templateNameDefault, 
                                                          mspURL);

        List<MultispeakInterface> mspInterfaceList = new ArrayList<MultispeakInterface>();
        if( mspInterfaces != null) {
            for (int i = 0; i < mspInterfaces.length; i++ )
            {
                MultispeakInterface mspInterface = new MultispeakInterface(vendorId, mspInterfaces[i], mspEndpoints[i]);
                mspInterfaceList.add(mspInterface);
            }
        }
        mspVendor.setMspInterfaces(mspInterfaceList);
        mspBean.setSelectedMspVendor(mspVendor);

        //Validate the request parameters before continuing on.
        String errorMessage = isValidMspRequest(req);
        if (errorMessage != null) {
            session.setAttribute( MultispeakDefines.MSP_RESULT_MSG, "");
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, errorMessage);
            return redirect;
        }

        if( action.equalsIgnoreCase("Save")) {
            saveMspVendor(req, mspVendor);
		}
        else if( action.equalsIgnoreCase("Create")) {
            try {
                redirect = req.getContextPath() + "/msp_setup.jsp?vendor="+ createMultispeakInterface(session, mspVendor);
            } catch( DuplicateException e) {
                session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* " + e.getMessage());
            }
        }
        else if( action.equalsIgnoreCase("Delete")) {
            deleteMultispeakInterface(session, mspVendor);
            mspBean.setSelectedMspVendor(null); //clear out, a default one will load when needed
        }
        else if( action.equalsIgnoreCase("pingURL")) {
            String mspService = req.getParameter("actionService");
                pingURL(session, mspVendor, mspService);
        }        
		else if( action.equalsIgnoreCase("getMethods")) {
            String mspService = req.getParameter("actionService");
            getMethods(session, mspVendor, mspService);
		}
		return redirect;
	}

    private void saveMspVendor(HttpServletRequest req, MultispeakVendor mspVendor) {
        MultispeakDaoImpl multispeakDao = (MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao");
        MultispeakFuncs multispeakFuncs = (MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs");
        
        multispeakDao.updateMultispeakVendor(mspVendor);
        
        int oldMspPrimaryCIS = multispeakFuncs.getPrimaryCIS();
        int mspPrimaryCIS = oldMspPrimaryCIS;
        if (req.getParameter("mspPrimaryCIS") != null)
            mspPrimaryCIS = Integer.valueOf(req.getParameter("mspPrimaryCIS")).intValue();
        
        int oldMspPaoNameAlias = multispeakFuncs.getPaoNameAlias();
        int mspPaoNameAlias = oldMspPaoNameAlias;
        if (req.getParameter("mspPaoNameAlias") != null)
            mspPaoNameAlias = Integer.valueOf(req.getParameter("mspPaoNameAlias")).intValue();
        
        //Update the role property values if they have changed.
        if ( mspPrimaryCIS != oldMspPrimaryCIS || 
             mspPaoNameAlias != oldMspPaoNameAlias ){
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

    /**
     * Validates that the request object has data in required fields.
     * Returns a String error message if the request is not valid.
     * Returns null if the request is a valid object (has data in all necessary fields).
     * @param mspVendor
     * @return
     */
    private String isValidMspRequest(HttpServletRequest req) {
        
        String param = req.getParameter("mspCompanyName");
        if( StringUtils.isBlank(req.getParameter("mspCompanyName")) )
            return "* Invalid Company Name.  Must have length greater than zero.";
        
        param = req.getParameter("mspMaxInitiateRequestObjects");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param))
            return "* Invalid MaxInitiateRequestObjects.  Must be a numeric value.";
        
        param = req.getParameter("mspRequestMessageTimeout");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param))
            return "* Invalid RequestMessageTimeout.  Must be a numeric value.";
        
        param = req.getParameter("mspMaxReturnRecords");
        if( StringUtils.isBlank(param) || !StringUtils.isNumeric(param))
            return "* Invalid MaxReturnRecords.  Must be a numeric value.";
        
        param = req.getParameter("mspTemplateNameDefault");
        if( StringUtils.isBlank(param))
            return "* Invalid TemplateNameDefault.  Must have length greater than zero.";
        
        param = req.getParameter("mspURL");
        try {
            URL url = new URL(param);
        } catch (MalformedURLException e) {
            return "* Invalid URL format.  (Ex. http://127.0.0.1:8080/)";
        }
        return null;
    }
    
    /**
     * Inserts the mspVendor into the database.
     * @param session The request session.
     * @param mspVendor The multispeakVendor to create.
     */
    private int createMultispeakInterface(HttpSession session, MultispeakVendor mspVendor) throws DuplicateException {
        ((MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao")).addMultispeakVendor(mspVendor);
            return mspVendor.getVendorID().intValue();
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
            ((MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao")).deleteMultispeakVendor(mspVendor.getVendorID().intValue());
    }

    private MspObject handleCB_MR(String command, int deviceID, MultispeakVendor mspVendor, HttpSession session) {
        MspObject mspObject = null;
        
        LiteDeviceMeterNumber liteDevMeterNum = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceID);
        if (liteDevMeterNum == null) {
            session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* MeterNumber not found for Device (ID:" + deviceID + ")");
            return mspObject;
        }
        
        String meterNumber = liteDevMeterNum.getMeterNumber();
        MultispeakInterface mspInterface = mspVendor.getMspInterfaceMap().get(MultispeakDefines.CB_MR_STR);
        String endpointURL = "";
        if( mspInterface != null)
            endpointURL = mspVendor.getUrl() + mspInterface.getMspEndpoint();
        
        try {
        	CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            if( command.equalsIgnoreCase("getServiceLocationByMeterNo")) {
                mspObject = port.getServiceLocationByMeterNo(meterNumber);
            } else if ( command.equalsIgnoreCase("getCustomerByMeterNo")) {
                mspObject = port.getCustomerByMeterNo(meterNumber);
            }
            
//            port.getServiceLocationByAccountNumber()
                            
//            ArrayOfMeter mspMeters = port.getMeterByServLoc(serviceLocationStr);

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
     */
    private void pingURL(HttpSession session, MultispeakVendor mspVendor, String mspService){
        try {
            ErrorObject[] objects = pingURL(mspVendor, mspService);
            if( objects != null && objects != null  && objects.length > 0){
                String result = "";
                for (int i = 0; i < objects.length; i++) {
                    result += objects[i].getObjectID() + " - " + objects[i].getErrorString();
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
     */
    private void getMethods(HttpSession session, MultispeakVendor mspVendor, String mspService){
        try {
            String[] objects = getMethods(mspVendor, mspService);
            if( objects != null && objects != null)
            {
                String resultStr = mspService + " available methods:\n";
                if( objects.length > 0)
                {
                    resultStr += " * " + objects[0] + "\n";
                    for (int i = 1; i < objects.length; i++)
                        resultStr += " * " + objects[i] + "\n";
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
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws ServiceException 
     * @throws RemoteException 
     * @throws ServiceException 
     */
    public static ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws RemoteException
    {
        ErrorObject[] objects = new ErrorObject[]{};
        if( service.equalsIgnoreCase(MultispeakDefines.OD_OA_STR)) {
            OD_OASoap_BindingStub port = MultispeakPortFactory.getOD_OAPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_OD_STR)) {
            OA_ODSoap_BindingStub port = MultispeakPortFactory.getOA_ODPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_EA_STR)) {
            MR_EASoap_BindingStub port = MultispeakPortFactory.getMR_EAPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.EA_MR_STR)) {
            EA_MRSoap_BindingStub port = MultispeakPortFactory.getEA_MRPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_CB_STR)) {
            MR_CBSoap_BindingStub port = MultispeakPortFactory.getMR_CBPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_MR_STR)) {
        	CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CD_CB_STR)) {
            CD_CBSoap_BindingStub port = MultispeakPortFactory.getCD_CBPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_CDSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_OA_STR)) {
            MR_OASoap_BindingStub port = MultispeakPortFactory.getMR_OAPort(mspVendor);
            objects = port.pingURL();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_MR_STR)) {
            OA_MRSoap_BindingStub port = MultispeakPortFactory.getOA_MRPort(mspVendor);
            objects = port.pingURL();
        }
        return objects;
    }
    
    /**
     * Utility to implement the getMethods method for the service.
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public static String[] getMethods(MultispeakVendor mspVendor, String service) throws RemoteException
    {
        String[] objects = new String[]{};
        if( service.equalsIgnoreCase(MultispeakDefines.OD_OA_STR)) {
            OD_OASoap_BindingStub port = MultispeakPortFactory.getOD_OAPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_OD_STR)) {
            OA_ODSoap_BindingStub port = MultispeakPortFactory.getOA_ODPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_EA_STR)) {
            MR_EASoap_BindingStub port = MultispeakPortFactory.getMR_EAPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.EA_MR_STR)) {
            EA_MRSoap_BindingStub port = MultispeakPortFactory.getEA_MRPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_CB_STR)) {
            MR_CBSoap_BindingStub port = MultispeakPortFactory.getMR_CBPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_MR_STR)) {
        	CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CD_CB_STR)) {
            CD_CBSoap_BindingStub port = MultispeakPortFactory.getCD_CBPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_CDSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.MR_OA_STR)) {
            MR_OASoap_BindingStub port = MultispeakPortFactory.getMR_OAPort(mspVendor);
            objects = port.getMethods();
        }
        else if( service.equalsIgnoreCase(MultispeakDefines.OA_MR_STR)) {
            OA_MRSoap_BindingStub port = MultispeakPortFactory.getOA_MRPort(mspVendor);
            objects = port.getMethods();
        }
        return objects;
    }   
}
