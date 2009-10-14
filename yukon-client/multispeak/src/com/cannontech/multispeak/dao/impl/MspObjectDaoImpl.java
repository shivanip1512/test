package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Name;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.lang.math.NumberUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    private SystemLogHelper _systemLogHelper = null;

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    @Override
	public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        Customer mspCustomer = new Customer();
        try {    
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspCustomer = port.getCustomerByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }                
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getCustomerByMeterNo(" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Customer");
        }
        return mspCustomer;
    }
    @Override
    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspServiceLocation =  port.getServiceLocationByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
    }
    @Override
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(Meter meter, MultispeakVendor mspVendor) {
        return getMspMeter(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor) {
        com.cannontech.multispeak.deploy.service.Meter mspMeter = new com.cannontech.multispeak.deploy.service.Meter();
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspMeter =  port.getMeterByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getMeterByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Meter");
       }
       return mspMeter;
    }

    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getAllMspMeters(MultispeakVendor mspVendor) {
    
    	List<com.cannontech.multispeak.deploy.service.Meter> allMeters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
    
    	boolean firstGet = true;
		String lastReceived = null;
		
		while (firstGet || lastReceived != null) {
			
			CTILogger.info("Calling getMoreMspMeters, lastReceived = " + lastReceived);
			lastReceived = getMoreMspMeters(allMeters, lastReceived, mspVendor);
			firstGet = false;
		}
    
		return allMeters;
	}
    
    private String getMoreMspMeters(List<com.cannontech.multispeak.deploy.service.Meter> allMeters, String lastReceived, MultispeakVendor mspVendor) {
    	
    	String lastSent = null;
        
        try {
        	
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
            	
                com.cannontech.multispeak.deploy.service.Meter[] meters = port.getAllMeters(lastReceived);
                
                List<com.cannontech.multispeak.deploy.service.Meter> mspMeters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
                if( meters != null) {
                    mspMeters = Arrays.asList(meters);
                }
                allMeters.addAll(mspMeters);
                
                int objectsRemaining = 0;
                String objectsRemainingStr = getAttributeValue(port, "objectsRemaining");
                if (NumberUtils.isDigits(objectsRemainingStr)) {
                	objectsRemaining = Integer.valueOf(objectsRemainingStr);
                }
                
                if (objectsRemaining != 0) {
        			lastSent = getAttributeValue(port, "lastSent");
        			CTILogger.info("getMoreMspMeters responded, received " + meters.length + " meters using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining + ", lastSent = " + lastSent);
        		} else {
        			CTILogger.info("getMoreMspMeters responded, received " + meters.length + " meters using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining);
        		}
                
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            }
            
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getAllMeters (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Meter");
       }
       
       return lastSent;
    }
    
    @SuppressWarnings("unchecked")
    private String getAttributeValue(Stub port, String name) {
    	
    	String value = null;
    	SOAPHeaderElement[] responseHeaders = port.getResponseHeaders();
        for (SOAPHeaderElement headerElement : responseHeaders) {
        	Iterator<Name> attributeNamesItr = headerElement.getAllAttributes();
        	while(attributeNamesItr.hasNext()) {
				Name attributeName = attributeNamesItr.next();
				if (attributeName.getLocalName().equalsIgnoreCase(name)) {
					value = headerElement.getAttributeValue(attributeName);
					if (!org.apache.commons.lang.StringUtils.isBlank(value)) {
						break;
					}
				}
			}
        }
        return value;
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor) {
    	return getMspMetersByServiceLocation(mspServiceLocation.getObjectID(), mspVendor);    	
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByServiceLocation(String serviceLocation, MultispeakVendor mspVendor) {
        
    	List<com.cannontech.multispeak.deploy.service.Meter> meters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
            	long start = System.currentTimeMillis();
                CTILogger.debug("Begin call to getMeterByServLoc for ServLoc:" + serviceLocation);
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMeterByServLoc(serviceLocation);
                CTILogger.debug("End call to getMeterByServLoc for ServLoc:" + serviceLocation + "  (took " + (System.currentTimeMillis() - start) + " millis)");
                if( mspMeters!= null) {
                	meters = Arrays.asList(mspMeters);
                }
            } else {
            	CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for ServLoc: " + serviceLocation);
            }
        } catch (RemoteException e) {
        	CTILogger.error("TargetService: " + endpointURL + " - getMeterByServLoc (" + mspVendor.getCompanyName() + ") for ServLoc: " + serviceLocation);
        	CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
    
    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method, String userName){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setEventTime(new GregorianCalendar());
        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);
        
        String description = "ErrorObject: (ObjId:" + errorObject.getObjectID() +
        					" Noun:" + errorObject.getNounType() +
        					" Message:" + errorObject.getErrorString() +")";
        logMSPActivity(method, description, userName);
        return errorObject;
    }
    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType, String method, String userName) {
        ErrorObject errorObject = getErrorObject(objectID, 
                                         notFoundObjectType + ": " + objectID + " - Was NOT found in Yukon.",
                                         nounType,
                                         method,
                                         userName);
        return errorObject;
    }
    @Override
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects) {

        if( !errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    @Override
    public void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName, SystemLog.TYPE_MULTISPEAK);
        CTILogger.debug("MSP Activity (Method: " + method +  " - " + description + ")");
    }
    
    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        List<String> substationNames = new ArrayList<String>();
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                DomainMember [] domainMembers = port.getDomainMembers("substationCode");
                if(domainMembers != null) {
                    for (DomainMember domainMember : domainMembers) {
                        substationNames.add(domainMember.getDescription());
                    }
                }
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for DomainMember 'substationCode'");
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - getDomainMembers(" + mspVendor.getCompanyName() + ") for DomainMember 'substationCode'");
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return substationNames;
    }

    @Override
    public List<String> getMspMethods(String mspServer, MultispeakVendor mspVendor) {
        
        String[] objects = new String[]{};
        try {
            if(mspServer.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
                OD_ServerSoap_BindingStub port = MultispeakPortFactory.getOD_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
                OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
                MR_ServerSoap_BindingStub port = MultispeakPortFactory.getMR_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
                EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
                LM_ServerSoap_BindingStub port = MultispeakPortFactory.getLM_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
                CD_ServerSoap_BindingStub port = MultispeakPortFactory.getCD_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
                CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
                objects = port.getMethods();
            }
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
                CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor);
                objects = port.getMethods();
            }
        } catch (RemoteException e) {
            CTILogger.error("Exception processing getMethods (" + mspVendor.getCompanyName() + ") for Server: " + mspServer);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        
        if (objects == null) {
        	return Collections.emptyList();
        }
        
        return Arrays.asList(objects);
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByEALocation(String eaLocation, MultispeakVendor mspVendor) {
        
        List<com.cannontech.multispeak.deploy.service.Meter> meters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMetersByEALocation(eaLocation);
                if( mspMeters!= null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for EALocation: " + eaLocation);
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - getMetersByEALocation (" + mspVendor.getCompanyName() + ") for EALocation: " + eaLocation);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByFacilityId(String facilityId, MultispeakVendor mspVendor) {
        
        List<com.cannontech.multispeak.deploy.service.Meter> meters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMetersByFacilityID(facilityId);
                if( mspMeters!= null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for FacilityId: " + facilityId);
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - getMetersByFacilityID (" + mspVendor.getCompanyName() + ") for FacilityId: " + facilityId);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByAccountNumber(String accountNumber, MultispeakVendor mspVendor) {
        
        List<com.cannontech.multispeak.deploy.service.Meter> meters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMeterByAccountNumber(accountNumber);
                if( mspMeters!= null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for Account Number: " + accountNumber);
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - getMeterByAccountNumber (" + mspVendor.getCompanyName() + ") for Account Number: " + accountNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
    
    @Override
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByCustId(String custId, MultispeakVendor mspVendor) {
        
        List<com.cannontech.multispeak.deploy.service.Meter> meters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        //lookup meter by eaLocation
        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMeterByCustID(custId);
                if( mspMeters!= null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for CustId: " + custId);
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - getMeterByCustID (" + mspVendor.getCompanyName() + ") for CustId: " + custId);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
}