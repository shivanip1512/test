package com.cannontech.multispeak.service.impl;

import javax.xml.rpc.ServiceException;

import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.CB_CDLocator;
import com.cannontech.multispeak.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.service.CB_CDSoap_PortType;
import com.cannontech.multispeak.service.CB_MRLocator;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.CB_MRSoap_PortType;
import com.cannontech.multispeak.service.CD_CBLocator;
import com.cannontech.multispeak.service.CD_CBSoap_BindingStub;
import com.cannontech.multispeak.service.CD_CBSoap_PortType;
import com.cannontech.multispeak.service.EA_MRLocator;
import com.cannontech.multispeak.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.EA_MRSoap_PortType;
import com.cannontech.multispeak.service.MR_CBLocator;
import com.cannontech.multispeak.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.service.MR_EALocator;
import com.cannontech.multispeak.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.service.MR_EASoap_PortType;
import com.cannontech.multispeak.service.MR_OALocator;
import com.cannontech.multispeak.service.MR_OASoap_BindingStub;
import com.cannontech.multispeak.service.MR_OASoap_PortType;
import com.cannontech.multispeak.service.OA_MRLocator;
import com.cannontech.multispeak.service.OA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.OA_MRSoap_PortType;
import com.cannontech.multispeak.service.OA_ODLocator;
import com.cannontech.multispeak.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.service.OA_ODSoap_PortType;
import com.cannontech.multispeak.service.OD_OALocator;
import com.cannontech.multispeak.service.OD_OASoap_BindingStub;
import com.cannontech.multispeak.service.OD_OASoap_PortType;

public class MultispeakPortFactory {
	
	/**
	 * Returns a new CB_MR port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CB_MRSoap_BindingStub getCB_MRPort(MultispeakVendor mspVendor) throws ServiceException {

		CB_MRLocator service = new CB_MRLocator();
        service.setCB_MRSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR));
        
        CB_MRSoap_PortType port = service.getCB_MRSoap();
        ((CB_MRSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((CB_MRSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((CB_MRSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((CB_MRSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (CB_MRSoap_BindingStub)port;
	}

	/**
	 * Returns a new MR_CB port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static MR_CBSoap_BindingStub getMR_CBPort(MultispeakVendor mspVendor) throws ServiceException {

		MR_CBLocator service = new MR_CBLocator();
        service.setMR_CBSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_CB_STR));
        
        MR_CBSoap_PortType port = service.getMR_CBSoap();
        ((MR_CBSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((MR_CBSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((MR_CBSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((MR_CBSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (MR_CBSoap_BindingStub)port;
	}

	/**
	 * Returns a new CB_CD port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CB_CDSoap_BindingStub getCB_CDPort(MultispeakVendor mspVendor) throws ServiceException {

		CB_CDLocator service = new CB_CDLocator();
        service.setCB_CDSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.CB_CD_STR));
        
        CB_CDSoap_PortType port = service.getCB_CDSoap();
        ((CB_CDSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((CB_CDSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((CB_CDSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((CB_CDSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (CB_CDSoap_BindingStub)port;
	}

	/**
	 * Returns a new CD_CB port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static CD_CBSoap_BindingStub getCD_CBPort(MultispeakVendor mspVendor) throws ServiceException {

		CD_CBLocator service = new CD_CBLocator();
        service.setCD_CBSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_CB_STR));
        
        CD_CBSoap_PortType port = service.getCD_CBSoap();
        ((CD_CBSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((CD_CBSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((CD_CBSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((CD_CBSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (CD_CBSoap_BindingStub)port;
	}

	/**
	 * Returns a new OA_OD port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OA_ODSoap_BindingStub getOA_ODPort(MultispeakVendor mspVendor) throws ServiceException {

		OA_ODLocator service = new OA_ODLocator();
        service.setOA_ODSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OA_OD_STR));
        
        OA_ODSoap_PortType port = service.getOA_ODSoap();
        ((OA_ODSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((OA_ODSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((OA_ODSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((OA_ODSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (OA_ODSoap_BindingStub)port;
	}

	/**
	 * Returns a new OD_OA port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OD_OASoap_BindingStub getOD_OAPort(MultispeakVendor mspVendor) throws ServiceException {

		OD_OALocator service = new OD_OALocator();
        service.setOD_OASoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OD_OA_STR));
        
        OD_OASoap_PortType port = service.getOD_OASoap();
        ((OD_OASoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((OD_OASoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((OD_OASoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((OD_OASoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (OD_OASoap_BindingStub)port;
	}

	/**
	 * Returns a new MR_EA port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static MR_EASoap_BindingStub getMR_EAPort(MultispeakVendor mspVendor) throws ServiceException {

		MR_EALocator service = new MR_EALocator();
        service.setMR_EASoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_EA_STR));
        
        MR_EASoap_PortType port = service.getMR_EASoap();
        ((MR_EASoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((MR_EASoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((MR_EASoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((MR_EASoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (MR_EASoap_BindingStub)port;
	}

	/**
	 * Returns a new EA_MR port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static EA_MRSoap_BindingStub getEA_MRPort(MultispeakVendor mspVendor) throws ServiceException {

		EA_MRLocator service = new EA_MRLocator();
        service.setEA_MRSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.EA_MR_STR));
        
        EA_MRSoap_PortType port = service.getEA_MRSoap();
        ((EA_MRSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((EA_MRSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((EA_MRSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((EA_MRSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (EA_MRSoap_BindingStub)port;
	}

	/**
	 * Returns a new MR_OA port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static MR_OASoap_BindingStub getMR_OAPort(MultispeakVendor mspVendor) throws ServiceException {

		MR_OALocator service = new MR_OALocator();
        service.setMR_OASoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.MR_OA_STR));
        
        MR_OASoap_PortType port = service.getMR_OASoap();
        ((MR_OASoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((MR_OASoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((MR_OASoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((MR_OASoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (MR_OASoap_BindingStub)port;
	}

	/**
	 * Returns a new OA_MR port instance. 
	 * @param mspVendor
	 * @return
	 * @throws ServiceException
	 */
	public static OA_MRSoap_BindingStub getOA_MRPort(MultispeakVendor mspVendor) throws ServiceException {

		OA_MRLocator service = new OA_MRLocator();
        service.setOA_MRSoapEndpointAddress(mspVendor.getEndpointURL(MultispeakDefines.OA_MR_STR));
        
        OA_MRSoap_PortType port = service.getOA_MRSoap();
        ((OA_MRSoap_BindingStub)port).setUsername(mspVendor.getOutUserName());
        ((OA_MRSoap_BindingStub)port).setPassword(mspVendor.getOutPassword());
        ((OA_MRSoap_BindingStub)port).setHeader(mspVendor.getHeader());
        ((OA_MRSoap_BindingStub)port).setTimeout(mspVendor.getTimeout());
        
        return (OA_MRSoap_BindingStub)port;
	}
}