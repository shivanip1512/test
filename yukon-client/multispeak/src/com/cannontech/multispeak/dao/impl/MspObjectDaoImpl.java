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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    private SystemLogHelper _systemLogHelper = null;
    private static final Logger log = YukonLogManager.getLogger(MspObjectDaoImpl.class);

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    @Override
	public Customer getMspCustomer(YukonMeter meter, MultispeakVendor mspVendor) {
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }                
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            log.error("TargetService: " + endpointURL + " - getCustomerByMeterNo(" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: "+e.getMessage());
            log.info("A default(empty) is being used for Customer");
        }
        return mspCustomer;
    }
    @Override
    public ServiceLocation getMspServiceLocation(YukonMeter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                LogHelper.debug(log, "Calling %s CB_Server.getServiceLocationByMeterNo for meterNumber: %s", mspVendor.getCompanyName(), meterNumber);
                mspServiceLocation =  port.getServiceLocationByMeterNo(meterNumber);
            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            log.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: "+e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
    }
    @Override
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(YukonMeter meter, MultispeakVendor mspVendor) {
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
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            log.error("TargetService: " + endpointURL + " - getMeterByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: "+e.getMessage());
            log.info("A default(empty) is being used for Meter");
       }
       return mspMeter;
    }

    // GET ALL MSP METERS
    @Override
    public void getAllMspMeters(MultispeakVendor mspVendor, SimpleCallback<List<com.cannontech.multispeak.deploy.service.Meter>> callback) throws Exception {
    
    	boolean firstGet = true;
		String lastReceived = null;
		
		while (firstGet || lastReceived != null) {
			
			log.info("Calling getMoreMspMeters, lastReceived = " + lastReceived);
			lastReceived = getMoreMspMeters(mspVendor, lastReceived, callback);
			firstGet = false;
		}
    }
    
    private String getMoreMspMeters(MultispeakVendor mspVendor, String lastReceived, SimpleCallback<List<com.cannontech.multispeak.deploy.service.Meter>> callback) throws Exception {
    	
    	String lastSent = null;
        
        try {
        	
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
            	
                com.cannontech.multispeak.deploy.service.Meter[] meters = port.getAllMeters(lastReceived);
                
                List<com.cannontech.multispeak.deploy.service.Meter> mspMeters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>(meters.length);
                if( meters != null) {
                    mspMeters = Arrays.asList(meters);
                }

                int objectsRemaining = 0;
                String objectsRemainingStr = getAttributeValue(port, "objectsRemaining");
                if (!StringUtils.isBlank(objectsRemainingStr)) {
                	try {
                		objectsRemaining = Integer.valueOf(objectsRemainingStr);
                	} catch (NumberFormatException e) {
                		log.error("Non-integer value in header for objectsRemaining: " + objectsRemainingStr, e);
                	}
                }
                
                if (objectsRemaining != 0) { 
        			lastSent = getAttributeValue(port, "lastSent");
        			log.info("getMoreMspMeters responded, received " + meters.length + " meters using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining + ", lastSent = " + lastSent);
        		} else {
        			log.info("getMoreMspMeters responded, received " + meters.length + " meters using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining);
        		}
                
                // pass to callback
                callback.handle(mspMeters);
                
            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            }
            
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            log.error("TargetService: " + endpointURL + " - getAllMeters (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            log.error("RemoteExceptionDetail: "+e.getMessage());
            log.info("A default(empty) is being used for Meter");
       }
       
       return lastSent;
    }
    
    // GET ALL MSP SERVICE LOCATIONS
    @Override
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback) throws RemoteException {
    	
    	boolean firstGet = true;
		String lastReceived = null;
		
		while (firstGet || lastReceived != null) {
			
			// kill before gathering more substations if callback is canceled
			if (callback.isCanceled()) {
				log.info("MultispeakGetAllServiceLocationsCallback in canceled state, aborting next call to getMoreServiceLocations");
				return;
			}
			
			log.info("Calling getMoreServiceLocations, lastReceived = " + lastReceived);
			lastReceived = getMoreServiceLocations(mspVendor, lastReceived, callback);
			firstGet = false;
		}
		
		callback.finish();
    }
    
    private String getMoreServiceLocations(MultispeakVendor mspVendor, String lastReceived, MultispeakGetAllServiceLocationsCallback callback) throws RemoteException {
    	
    	String lastSent = null;
        
        try {
        	
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
            	
            	// get service locations
                com.cannontech.multispeak.deploy.service.ServiceLocation[] serviceLocations = port.getAllServiceLocations(lastReceived);
                
                int serviceLocationCount = 0;
                if(serviceLocations != null) {
                	serviceLocationCount = serviceLocations.length;
                }
                
                // objectsRemaining
                int objectsRemaining = 0;
                String objectsRemainingStr = getAttributeValue(port, "objectsRemaining");
                if (!StringUtils.isBlank(objectsRemainingStr)) {
                	try {
                		objectsRemaining = Integer.valueOf(objectsRemainingStr);
                	} catch (NumberFormatException e) {
                		log.error("Non-integer value in header for objectsRemaining: " + objectsRemainingStr, e);
                	}
                }
                
                if (objectsRemaining != 0) { 
        			lastSent = getAttributeValue(port, "lastSent");
        			log.info("getMoreServiceLocations responded, received " + serviceLocationCount + " ServiceLocations using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining + ", lastSent = " + lastSent);
        		} else {
        			log.info("getMoreServiceLocations responded, received " + serviceLocationCount + " ServiceLocations using lastReceived = " + lastReceived + ". Response: objectsRemaining = " + objectsRemaining);
        		}
                
                
                // process service locations
                if(serviceLocationCount > 0) {
                	List<com.cannontech.multispeak.deploy.service.ServiceLocation> mspServiceLocations = Arrays.asList(serviceLocations);
                    callback.processServiceLocations(mspServiceLocations);
                } 
                
            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            }
            
        } catch (RemoteException e) {
        	
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            log.error("TargetService: " + endpointURL + " - getAllServiceLocations (" + mspVendor.getCompanyName() + ") for LastReceived: " + lastReceived);
            log.error("RemoteExceptionDetail: "+e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");
            
            throw e;
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
                LogHelper.debug(log, "Begin call to getMeterByServLoc for ServLoc: %s", serviceLocation);
                com.cannontech.multispeak.deploy.service.Meter[] mspMeters = port.getMeterByServLoc(serviceLocation);
                LogHelper.debug(log, "End call to getMeterByServLoc for ServLoc:" + serviceLocation + "  (took " + (System.currentTimeMillis() - start) + " millis)");
                if( mspMeters!= null) {
                	meters = Arrays.asList(mspMeters);
                }
            } else {
            	log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for ServLoc: " + serviceLocation);
            }
        } catch (RemoteException e) {
        	log.error("TargetService: " + endpointURL + " - getMeterByServLoc (" + mspVendor.getCompanyName() + ") for ServLoc: " + serviceLocation);
        	log.error("RemoteExceptionDetail: "+e.getMessage());
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
        LogHelper.debug(log, "MSP Activity (Method: %s - %s)", method, description);
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for DomainMember 'substationCode'");
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointURL + " - getDomainMembers(" + mspVendor.getCompanyName() + ") for DomainMember 'substationCode'");
            log.error("RemoteExceptionDetail: "+e.getMessage());
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
            else if(mspServer.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
                MDM_ServerSoap_PortType port = MultispeakPortFactory.getMDM_ServerPort(mspVendor);
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
            log.error("Exception processing getMethods (" + mspVendor.getCompanyName() + ") for Server: " + mspServer);
            log.error("RemoteExceptionDetail: "+e.getMessage());
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for EALocation: " + eaLocation);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointURL + " - getMetersByEALocation (" + mspVendor.getCompanyName() + ") for EALocation: " + eaLocation);
            log.error("RemoteExceptionDetail: "+e.getMessage());
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for FacilityId: " + facilityId);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointURL + " - getMetersByFacilityID (" + mspVendor.getCompanyName() + ") for FacilityId: " + facilityId);
            log.error("RemoteExceptionDetail: "+e.getMessage());
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for Account Number: " + accountNumber);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointURL + " - getMeterByAccountNumber (" + mspVendor.getCompanyName() + ") for Account Number: " + accountNumber);
            log.error("RemoteExceptionDetail: "+e.getMessage());
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
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for CustId: " + custId);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointURL + " - getMeterByCustID (" + mspVendor.getCompanyName() + ") for CustId: " + custId);
            log.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return meters;
    }
    
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws RemoteException
    {
        ErrorObject[] objects = new ErrorObject[]{};
        if(service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            OD_ServerSoap_BindingStub port = MultispeakPortFactory.getOD_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            MDM_ServerSoap_PortType port = MultispeakPortFactory.getMDM_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            MR_ServerSoap_BindingStub port = MultispeakPortFactory.getMR_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            LM_ServerSoap_BindingStub port = MultispeakPortFactory.getLM_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            CD_ServerSoap_BindingStub port = MultispeakPortFactory.getCD_ServerPort(mspVendor);
            objects = port.pingURL();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            objects = port.pingURL();
        } 
        else if( service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor);
            objects = port.pingURL();
        }
        else {
            ErrorObject obj = new ErrorObject("-100", "No server for " + service, null, null);
            return new ErrorObject[]{obj};
        }
        return objects;
    }
    
    public String[] getMethods(MultispeakVendor mspVendor, String service) throws RemoteException
    {
        String[] objects = new String[]{};
        if(service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            OD_ServerSoap_BindingStub port = MultispeakPortFactory.getOD_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            MDM_ServerSoap_PortType port = MultispeakPortFactory.getMDM_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            MR_ServerSoap_BindingStub port = MultispeakPortFactory.getMR_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            LM_ServerSoap_BindingStub port = MultispeakPortFactory.getLM_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            CD_ServerSoap_BindingStub port = MultispeakPortFactory.getCD_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            objects = port.getMethods();
        }
        else if(service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor);
            objects = port.getMethods();
        }
        else {
            return new String[]{"No server for " + service};
        }
        return objects;
    }
}