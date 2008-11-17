package com.cannontech.multispeak.deploy.service.impl;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.CB_CDLocator;
import com.cannontech.multispeak.deploy.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_CDSoap_PortType;
import com.cannontech.multispeak.deploy.service.CB_MRLocator;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_PortType;
import com.cannontech.multispeak.deploy.service.CD_CBLocator;
import com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType;
import com.cannontech.multispeak.deploy.service.EA_MRLocator;
import com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_CBLocator;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_EALocator;
import com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_EASoap_PortType;
import com.cannontech.multispeak.deploy.service.OA_ODLocator;
import com.cannontech.multispeak.deploy.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OA_ODSoap_PortType;
import com.cannontech.multispeak.deploy.service.OD_OALocator;
import com.cannontech.multispeak.deploy.service.OD_OASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_OASoap_PortType;

public class MultispeakPortFactory {
	
	/**
	 * Returns a new CB_MR port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CB_MRSoap_BindingStub getCB_MRPort(MultispeakVendor mspVendor) {

		CB_MRLocator service = new CB_MRLocator();
        service.setCB_MRSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR));
        
        CB_MRSoap_PortType port = null;
        try {
            port = service.getCB_MRSoap();
//            ((CB_MRSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CB_MRSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CB_MRSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CB_MRSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (CB_MRSoap_BindingStub)port;
	}

	/**
	 * Returns a new MR_CB port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static MR_CBSoap_BindingStub getMR_CBPort(MultispeakVendor mspVendor) {

		MR_CBLocator service = new MR_CBLocator();
        service.setMR_CBSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_CB_STR));
        
        MR_CBSoap_PortType port = null;
        try {
            port = service.getMR_CBSoap();
//            ((MR_CBSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((MR_CBSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((MR_CBSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((MR_CBSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("MR_CB service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (MR_CBSoap_BindingStub)port;
	}

	/**
	 * Returns a new CB_CD port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CB_CDSoap_BindingStub getCB_CDPort(MultispeakVendor mspVendor) {

		CB_CDLocator service = new CB_CDLocator();
        service.setCB_CDSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CB_CD_STR));
        
        CB_CDSoap_PortType port = null; 
        try {
            port = service.getCB_CDSoap();
//            ((CB_CDSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CB_CDSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CB_CDSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CB_CDSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CB_CD service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (CB_CDSoap_BindingStub)port;
	}

	/**
	 * Returns a new CD_CB port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CD_CBSoap_BindingStub getCD_CBPort(MultispeakVendor mspVendor) {

		CD_CBLocator service = new CD_CBLocator();
        service.setCD_CBSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CD_CB_STR));
        
        CD_CBSoap_PortType port = null;
        try {
            port = service.getCD_CBSoap();
//            ((CD_CBSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CD_CBSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CD_CBSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CD_CBSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CD_CB service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }        
        return (CD_CBSoap_BindingStub)port;
	}

	/**
	 * Returns a new OA_OD port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OA_ODSoap_BindingStub getOA_ODPort(MultispeakVendor mspVendor) {

		OA_ODLocator service = new OA_ODLocator();
        service.setOA_ODSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OA_OD_STR));
        
        OA_ODSoap_PortType port = null;
        try { 
            port = service.getOA_ODSoap();
//            ((OA_ODSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((OA_ODSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((OA_ODSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((OA_ODSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("OA_OD service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (OA_ODSoap_BindingStub)port;
	}

	/**
	 * Returns a new OD_OA port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OD_OASoap_BindingStub getOD_OAPort(MultispeakVendor mspVendor) {

		OD_OALocator service = new OD_OALocator();
        service.setOD_OASoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OD_OA_STR));
        
        OD_OASoap_PortType port = null;
        try {
            port = service.getOD_OASoap();
//            ((OD_OASoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((OD_OASoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((OD_OASoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((OD_OASoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("OD_OA service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (OD_OASoap_BindingStub)port;
	}

	/**
	 * Returns a new MR_EA port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static MR_EASoap_BindingStub getMR_EAPort(MultispeakVendor mspVendor) {

		MR_EALocator service = new MR_EALocator();
        service.setMR_EASoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_EA_STR));
        
        MR_EASoap_PortType port = null;
        try {
            port = service.getMR_EASoap();
//            ((MR_EASoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((MR_EASoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((MR_EASoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((MR_EASoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("MR_EA service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (MR_EASoap_BindingStub)port;
	}

	/**
	 * Returns a new EA_MR port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static EA_MRSoap_BindingStub getEA_MRPort(MultispeakVendor mspVendor) {

		EA_MRLocator service = new EA_MRLocator();
        service.setEA_MRSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.EA_MR_STR));
        
        EA_MRSoap_PortType port = null;
        try {
            port = service.getEA_MRSoap();
//            ((EA_MRSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((EA_MRSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((EA_MRSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((EA_MRSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("EA_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (EA_MRSoap_BindingStub)port;
	}
}