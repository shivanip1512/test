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

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.client.MultispeakBean;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.CD_CBSoap_BindingStub;
import com.cannontech.multispeak.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.service.MR_OASoap_BindingStub;
import com.cannontech.multispeak.service.MultiSpeakMsgHeader;
import com.cannontech.multispeak.service.OA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.service.OD_OASoap_BindingStub;
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
        
//		load all parameters from req
        String vendorIDStr = req.getParameter("vendorID");
        Integer vendorID = null;
        if( vendorIDStr != null && vendorIDStr.length() > 0)
            vendorID = Integer.valueOf(vendorIDStr);
		String companyName = req.getParameter("mspVendor");
		String uniqueKey = req.getParameter("mspUniqueKey");
		String password = req.getParameter("mspPassword");
		String username = req.getParameter("mspUserName");
		String mspURL = req.getParameter("mspURL");
		String[] mspInterfaces = req.getParameterValues("mspInterface");
		String[] mspEndpoints = req.getParameterValues("mspEndpoint");
//        load action parameters from req
        String mspService = req.getParameter("actionService");
        String mspEndpoint = req.getParameter("actionEndpoint");
        
        if( !mspURL.endsWith("/"))
            mspURL += "/";
        String serviceURL = mspURL + mspEndpoint;
        
        mspBean.setSelectedCompanyName(companyName);
        MultispeakVendor mspVendor = new MultispeakVendor(vendorID,companyName, username, password, uniqueKey, 0, mspURL);

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
		}
        else if( action.equalsIgnoreCase("Create")) {
            redirect = req.getContextPath() + createMultispeakInterface(session, mspVendor);
        }
        else if( action.equalsIgnoreCase("Delete")) {
            deleteMultispeakInterface(session, mspVendor);
            mspBean.setSelectedCompanyName("Cannon");   //set to default
        }        
        else if( action.equalsIgnoreCase("pingURL")) {
            pingURL(session, mspService, serviceURL);
        }        
		else if( action.equalsIgnoreCase("getMethods")) {
            getMethods(session, mspService, serviceURL);
		}
		return redirect;
	}
    
    /**
     * Inserts the mspVendor into the database.
     * @param session The request session.
     * @param mspVendor The multispeakVendor to create.
     */
    private String createMultispeakInterface(HttpSession session, MultispeakVendor mspVendor) {
        try{
            if( MultispeakFuncs.getMultispeakDao().getMultispeakVendor(mspVendor.getCompanyName())!= null){
                session.setAttribute(ServletUtil.ATT_ERROR_MESSAGE, "* A vendor with the name '" + mspVendor.getCompanyName() + "' already exists.  Please enter a different company name.");
                return "/msp_setup_new.jsp";
            }
        }catch(NotFoundException nfe)
        {
            MultispeakFuncs.getMultispeakDao().addMultispeakVendor(mspVendor);
            return "/msp_setup.jsp?vendor="+ mspVendor.getCompanyName();
        }
        return "/msp_setup.jsp";
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
    
    /**
     * Implements the pingURL webservice
     * @param session The request session. 
     * @param mspService The multispeak webservice to invoke.
     * @param serviceURL The url for the mspService
     */
    private void pingURL(HttpSession session, String mspService, String serviceURL){
        try {
            ArrayOfErrorObject objects = pingURL(serviceURL, mspService);
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
     * @param mspService The multispeak webservice to invoke.
     * @param serviceURL The url for the mspService
     */
    private void getMethods(HttpSession session, String mspService, String serviceURL){
        try {
            ArrayOfString objects = getMethods(serviceURL, mspService);
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
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public static ArrayOfErrorObject pingURL(String serviceURL, String service) throws RemoteException
    {
        ArrayOfErrorObject objects = new ArrayOfErrorObject();
        MultiSpeakMsgHeader msHeader = new YukonMultispeakMsgHeader();
        SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", msHeader);
        URL instanceURL = null;
        try {
            instanceURL = new URL(serviceURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
     * @param serviceURL The url of for the webservice to be called.
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public static ArrayOfString getMethods(String serviceURL, String service) throws RemoteException
    {
        ArrayOfString objects = new ArrayOfString();
        MultiSpeakMsgHeader msHeader = new YukonMultispeakMsgHeader();
        SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", msHeader);
        URL instanceURL = null;
        try {
            instanceURL = new URL(serviceURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
