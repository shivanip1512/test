package com.cannontech.multispeak.deploy.service.impl;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
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
import com.cannontech.multispeak.deploy.service.SCADA_ServerLocator;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType;

public class MultispeakPortFactory {

	/**
	 * Get a new CB_Server port instance using the specified endpoint URL. 
	 */
	public static CB_ServerSoap_BindingStub getCB_ServerPort(MultispeakVendor mspVendor,
	                                                         String endpointUrl) {

		CB_ServerLocator service = new CB_ServerLocator();
        service.setCB_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static CB_ServerSoap_BindingStub getCB_CDPort(MultispeakVendor mspVendor, String endpointUrl) {

		CB_ServerLocator service = new CB_ServerLocator();
        service.setCB_ServerSoapEndpointAddress(endpointUrl);
        
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
    public static MR_ServerSoap_BindingStub getMR_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

        MR_ServerLocator service = new MR_ServerLocator();
        service.setMR_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static CD_ServerSoap_BindingStub getCD_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

		CD_ServerLocator service = new CD_ServerLocator();
        service.setCD_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static OA_ServerSoap_BindingStub getOA_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

		OA_ServerLocator service = new OA_ServerLocator();
        service.setOA_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static OD_ServerSoap_BindingStub getOD_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

		OD_ServerLocator service = new OD_ServerLocator();
        service.setOD_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static EA_ServerSoap_BindingStub getEA_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

		EA_ServerLocator service = new EA_ServerLocator();
        service.setEA_ServerSoapEndpointAddress(endpointUrl);
        
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
	public static LM_ServerSoap_BindingStub getLM_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

		LM_ServerLocator service = new LM_ServerLocator();
        service.setLM_ServerSoapEndpointAddress(endpointUrl);
        
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
     * Get a new MDM_Server port instance using the specified endpointUrlL.
     */
    public static MDM_ServerSoap_PortType getMDM_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {
        MDM_ServerLocator service = new MDM_ServerLocator();
        service.setMDM_ServerSoapEndpointAddress(endpointUrl);

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
    
    /**
     * Returns a new SCADA_Server port instance for the endpointStr specified. 
     */
    public static SCADA_ServerSoap_BindingStub getSCADA_ServerPort(MultispeakVendor mspVendor, String endpointUrl) {

        SCADA_ServerLocator service = new SCADA_ServerLocator();
        service.setSCADA_ServerSoapEndpointAddress(endpointUrl);
        
        SCADA_ServerSoap_PortType port = null;
        try {
            port = service.getSCADA_ServerSoap();
//            ((SCADA_ServerSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
//            ((SCADA_ServerSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
            ((SCADA_ServerSoap_BindingStub)port).setHeader(mspVendor.getHeader());
            ((SCADA_ServerSoap_BindingStub)port).setTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
        } catch (ServiceException e) {
            CTILogger.error("SCADA_Server service is not defined for company(" + mspVendor.getCompanyName()+ ") - method failed.");
            CTILogger.error("ServiceException Detail: " + e);
        }
        
        return (SCADA_ServerSoap_BindingStub)port;
    }

}