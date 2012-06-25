package com.cannontech.multispeak.deploy.service.impl;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.CB_ServerLocator;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.CD_ServerLocator;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.EA_ServerLocator;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.LM_ServerLocator;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.MDM_ServerLocator;
import com.cannontech.multispeak.deploy.service.MDM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_ServerLocator;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.OA_ServerLocator;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.OD_ServerLocator;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_PortType;

public class MultispeakPortFactory {
    /**
     * Get a new CB_Server port instance using the endpoint URL configured in Yukon.
     */
    public static CB_ServerSoap_BindingStub getCB_ServerPort(MultispeakVendor mspVendor) {
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        return getCB_ServerPort(mspVendor, endpointURL);
    }

	/**
	 * Get a new CB_Server port instance using the specified endpoint URL. 
	 */
	public static CB_ServerSoap_BindingStub getCB_ServerPort(MultispeakVendor mspVendor,
	                                                         String endpointURL) {

		CB_ServerLocator service = new CB_ServerLocator();
        service.setCB_ServerSoapEndpointAddress(endpointURL);
        
        CB_ServerSoap_PortType port = null;
        try {
            port = service.getCB_ServerSoap();
//            ((CB_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CB_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CB_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CB_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CB_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (CB_ServerSoap_BindingStub)port;
	}

	/**
	 * Returns a new CB_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CB_ServerSoap_BindingStub getCB_CDPort(MultispeakVendor mspVendor) {

		CB_ServerLocator service = new CB_ServerLocator();
		String endpoint = mspVendor.getEndpointURL(MultispeakDefines.CB_CD_STR);
		if (endpoint == "") {
			//Couldn't find an endpoint for CB_CD...lets try the new way!
			CTILogger.info("CB_CD service is not defined for company(" + mspVendor.getCompanyName()+ ") - Will try CB_Server instead.");
			endpoint = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
		}
        service.setCB_ServerSoapEndpointAddress(endpoint);
        
        CB_ServerSoap_PortType port = null;
        try {
            port = service.getCB_ServerSoap();
//            ((CB_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CB_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CB_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CB_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CB_CD/CB_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (CB_ServerSoap_BindingStub)port;
	}
	
    /**
     * Returns a new MR_Server port instance for the endpointStr specified. 
     * @param mspVendor
     * @param endpointStr
     * @return
     * @throws ServiceException
     */
    public static MR_ServerSoap_BindingStub getMR_ServerPort(MultispeakVendor mspVendor) {

        MR_ServerLocator service = new MR_ServerLocator();
        service.setMR_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_Server_STR));
        
        MR_ServerSoap_PortType port = null;
        try {
            port = service.getMR_ServerSoap();
//            ((MR_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((MR_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((MR_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((MR_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("MR_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (MR_ServerSoap_BindingStub)port;
    }

	/**
	 * Returns a new CD_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CD_ServerSoap_BindingStub getCD_ServerPort(MultispeakVendor mspVendor) {

		CD_ServerLocator service = new CD_ServerLocator();
        service.setCD_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CD_Server_STR));
        
        CD_ServerSoap_PortType port = null;
        try {
            port = service.getCD_ServerSoap();
//            ((CD_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((CD_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((CD_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((CD_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("CD_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }        
        return (CD_ServerSoap_BindingStub)port;
	}

	/**
	 * Returns a new OA_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OA_ServerSoap_BindingStub getOA_ServerPort(MultispeakVendor mspVendor) {

		OA_ServerLocator service = new OA_ServerLocator();
        service.setOA_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OA_Server_STR));
        
        OA_ServerSoap_PortType port = null;
        try { 
            port = service.getOA_ServerSoap();
//            ((OA_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((OA_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((OA_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((OA_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("OA_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (OA_ServerSoap_BindingStub)port;
	}

	/**
	 * Returns a new OD_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OD_ServerSoap_BindingStub getOD_ServerPort(MultispeakVendor mspVendor) {

		OD_ServerLocator service = new OD_ServerLocator();
        service.setOD_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OD_Server_STR));
        
        OD_ServerSoap_PortType port = null;
        try {
            port = service.getOD_ServerSoap();
//            ((OD_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((OD_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((OD_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((OD_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("OD_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (OD_ServerSoap_BindingStub)port;
	}

	/**
	 * Returns a new EA_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static EA_ServerSoap_BindingStub getEA_ServerPort(MultispeakVendor mspVendor) {

		EA_ServerLocator service = new EA_ServerLocator();
        service.setEA_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.EA_Server_STR));
        
        EA_ServerSoap_PortType port = null;
        try {
            port = service.getEA_ServerSoap();
//            ((EA_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((EA_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((EA_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((EA_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("EA_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (EA_ServerSoap_BindingStub)port;
	}
	
	/**
	 * Returns a new LM_Server port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static LM_ServerSoap_BindingStub getLM_ServerPort(MultispeakVendor mspVendor) {

		LM_ServerLocator service = new LM_ServerLocator();
        service.setLM_ServerSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.LM_Server_STR));
        
        LM_ServerSoap_PortType port = null;
        try {
            port = service.getLM_ServerSoap();
//            ((LM_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((LM_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((LM_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((LM_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("LM_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        return (LM_ServerSoap_BindingStub)port;
	}

    /**
     * Get a new MDM_Server port instance using the endpoint URL configured in Yukon.
     */
    public static MDM_ServerSoap_PortType getMDM_ServerPort(MultispeakVendor mspVendor) {
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.MDM_Server_STR);
        return getMDM_ServerPort(mspVendor, endpointURL);
   }

    /**
     * Get a new MDM_Server port instance using the specified endpoint URL.
     */
    public static MDM_ServerSoap_PortType getMDM_ServerPort(MultispeakVendor mspVendor,
                                                            String endpointURL) {
        MDM_ServerLocator service = new MDM_ServerLocator();
        service.setMDM_ServerSoapEndpointAddress(endpointURL);

        MDM_ServerSoap_PortType port = null;
        try {
            port = service.getMDM_ServerSoap();
            MDM_ServerSoap_BindingStub bindingStub = (MDM_ServerSoap_BindingStub) port;
            // bindingStub.setUsername(mspVendor.getOutUserName());
            // bindingStub.setPassword(mspVendor.getOutPassword());
            bindingStub.setHeader(mspVendor.getHeader());
            bindingStub.setTimeout((int) mspVendor.getRequestMessageTimeout());
        } catch (ServiceException e) {
            CTILogger.error("MDM_Server service is not defined for company("
                            + mspVendor.getCompanyName() + ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }

        return port;
    }
}