package com.cannontech.multispeak.dao.impl.v5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocations;
import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNames;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNamesResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfo;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfoResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchString;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfAccountID;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfContactInfoReferable;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfCustomer;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfCustomerID;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfDomain;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfMeterID;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfNetworkModelRef;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfServiceLocation;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfServiceLocationID;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.NetworkModelRef;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.commontypes.OtherContactInformation;
import com.cannontech.msp.beans.v5.commontypes.OtherContactItem;
import com.cannontech.msp.beans.v5.commontypes.SingleIdentifier;
import com.cannontech.msp.beans.v5.enumerations.ServiceKind;
import com.cannontech.msp.beans.v5.multispeak.ContactInfo;
import com.cannontech.msp.beans.v5.multispeak.ContactInfoReferable;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.Domain;
import com.cannontech.msp.beans.v5.multispeak.DomainMember;
import com.cannontech.msp.beans.v5.multispeak.DomainMembers;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.MspMeter;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.CBClient;
import com.cannontech.multispeak.client.core.v5.CDClient;
import com.cannontech.multispeak.client.core.v5.DRClient;
import com.cannontech.multispeak.client.core.v5.EAClient;
import com.cannontech.multispeak.client.core.v5.MDMClient;
import com.cannontech.multispeak.client.core.v5.MRClient;
import com.cannontech.multispeak.client.core.v5.OAClient;
import com.cannontech.multispeak.client.core.v5.ODClient;
import com.cannontech.multispeak.client.core.v5.SCADAClient;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.dao.v5.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MspObjectDaoImpl implements MspObjectDao {
    private static final Logger log = YukonLogManager.getLogger(MspObjectDaoImpl.class);

    @Autowired private CBClient cbClient;
    @Autowired private CDClient cdClient;
    @Autowired private DRClient drClient;
    @Autowired private EAClient eaClient;
    @Autowired private MDMClient mdmClient;
    @Autowired private OAClient oaClient;
    @Autowired private MRClient mrClient;
    @Autowired private ODClient odClient;
    @Autowired private SCADAClient scadaClient;
    @Autowired private NOTClient notClient;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private SystemLogHelper _systemLogHelper = null;

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null) {
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        }
        return _systemLogHelper;
    }

    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(new Date()));

        errorObject.setReferenceID(objectID);
        errorObject.setDisplayString(errorMessage);
        errorObject.setNounType(nounType);

        String description =
            "ErrorObject: (ObjId:" + errorObject.getReferenceID() + " Noun:" + errorObject.getNounType() + " Message:"
                + errorObject.getDisplayString() + ")";
        logMSPActivity(method, description, userName);

        return errorObject;
    }

    @Override
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects) {

        if (errorObjects != null && !errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName, String exceptionMessage) {
        ErrorObject errorObject =
            getErrorObject(objectID, notFoundObjectType + ": " + objectID + " - " + exceptionMessage + ".", nounType,
                method, userName);
        return errorObject;
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName) {
        return getNotFoundErrorObject(objectID, notFoundObjectType, nounType, method, userName,
            "Was NOT found in Yukon");
    }

    @Override
    public void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName,
            SystemLog.TYPE_MULTISPEAK);
        log.debug("MSP Activity (Method: " + method + "-" + description + " )");
    }

    @Override
    public ServiceLocation getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }

    @Override
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetServiceLocationsByMeterIDs getServiceLocationsByMeterIDs = new GetServiceLocationsByMeterIDs();
            ArrayOfMeterID arrayOfMeterID = new ArrayOfMeterID();
            List<MeterID> meterIds = arrayOfMeterID.getMeterID();
            MeterID meterID = new MeterID();
            meterID.setMeterName(meterNumber);
            meterID.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
            meterID.setServiceType(ServiceKind.ELECTRIC);
            meterID.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
            meterIds.add(meterID);

            getServiceLocationsByMeterIDs.setArrayOfMeterID(arrayOfMeterID);

            GetServiceLocationsByMeterIDsResponse getServiceLocationsByMeterIDsResponse =
                cbClient.getServiceLocationsByMeterIDs(mspVendor, endpointUrl, getServiceLocationsByMeterIDs);

            if (getServiceLocationsByMeterIDsResponse != null) {
                ArrayOfServiceLocation arrayOfServiceLocation =
                    getServiceLocationsByMeterIDsResponse.getArrayOfServiceLocation();
                if (arrayOfServiceLocation != null) {
                    List<ServiceLocation> responseServiceLocations = arrayOfServiceLocation.getServiceLocation();
                    if (CollectionUtils.isNotEmpty(responseServiceLocations)) {
                        mspServiceLocation = responseServiceLocations.get(0);
                    }
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getServiceLocationsByMeterIDsResponse ("
                + mspVendor.getCompanyName());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return mspServiceLocation;
    }

    @Override
    public List<MspMeter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation,
            MultispeakVendor mspVendor) {
        //
        List<MspMeter> meterDetails = new ArrayList<MspMeter>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMetersByServiceLocationIDs getMetersByServiceLocationIDs = new GetMetersByServiceLocationIDs();
            ArrayOfServiceLocationID arrayOfServiceLocationID = new ArrayOfServiceLocationID();
            List<ObjectID> objectIDs = arrayOfServiceLocationID.getServiceLocationID();

            ObjectID objectID = new ObjectID();
            objectID.setObjectGUID(mspServiceLocation.getObjectGUID());
            SingleIdentifier singleIdentifier = new SingleIdentifier();
            // TODO: Test during integration if SERVICE_LOCATION_ID is needed as IdentifierName and its valid
            // value
            singleIdentifier.setIdentifierName(MultispeakDefines.SERVICE_LOCATION_ID);
            singleIdentifier.setValue(mspServiceLocation.getObjectGUID());
            objectID.setPrimaryIdentifier(singleIdentifier);
            objectIDs.add(objectID);

            getMetersByServiceLocationIDs.setArrayOfServiceLocationID(arrayOfServiceLocationID);

            GetMetersByServiceLocationIDsResponse getMetersByServiceLocationIDsResponse =
                cbClient.getMetersByServiceLocationIDs(mspVendor, endpointUrl, getMetersByServiceLocationIDs);

            if (getMetersByServiceLocationIDsResponse != null) {
                Meters meters = getMetersByServiceLocationIDsResponse.getMeters();
                if (meters != null) {
                    meterDetails = multispeakFuncs.getMspMeters(meters);
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByServiceLocationIDs ("
                + mspVendor.getCompanyName());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meterDetails;
    }

    @Override
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException {
        if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            cbClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            cdClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.DR_Server_STR)) {
            drClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            eaClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            mdmClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            mrClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            oaClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.NOT_Server_STR) || service.equalsIgnoreCase(MultispeakDefines.NOT_Server_DR_STR)) {
            notClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            odClient.pingURL(mspVendor, endpointUrl);
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            scadaClient.pingURL(mspVendor, endpointUrl);
        } else {
            ErrorObject obj = new ErrorObject();
            obj.setReferenceID("-100");
            obj.setDetailedString("No server for " + service);
            obj.setNounType(null);
            obj.setEventTime(null);
            return new ErrorObject[] { obj };
        }
        List<ErrorObject> errorObjects = multispeakFuncs.getErrorObjectsFromResponse();
        ErrorObject[] objects = toErrorObject(errorObjects);
        return objects;
    }
    
    @Override
    public List<String> findMethods(String mspServer, MultispeakVendor mspVendor) {

        try {
            return getMethods(mspVendor, mspServer, null);
        } catch (MultispeakWebServiceClientException e) {
            log.error("Exception processing GetMethods (" + mspVendor.getCompanyName() + ") for Server: " + mspServer);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException {
        if (endpointUrl == null) {
            endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);
        }
        List<String> methods = new ArrayList<>();
        try {
            if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
                methods = cbClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
                methods = cdClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
                methods = eaClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.DR_Server_STR)) {
                methods = drClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
                methods = mdmClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
                methods = mrClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.NOT_Server_STR) || service.equalsIgnoreCase(MultispeakDefines.NOT_Server_DR_STR)) {
                methods = notClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
                methods = oaClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
                methods = odClient.getMethods(mspVendor, endpointUrl);
            } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
                methods = scadaClient.getMethods(mspVendor, endpointUrl);
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - GetMethods (" + mspVendor.getCompanyName() + ") ");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            throw e;
        }
        return methods;
    }

    @Override
    public List<MspMeter> getMetersByContactInfo(Map<String, String> facilityNameValues, MultispeakVendor mspVendor) {
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        List<MspMeter> meterList = new ArrayList<>();
        try {
            GetMetersByContactInfo getMetersByContactInfo = new GetMetersByContactInfo();
            ArrayOfContactInfoReferable arrayOfContactInfoReferable = new ArrayOfContactInfoReferable();
            List<ContactInfoReferable> contactInfoReferableList = arrayOfContactInfoReferable.getContactInfoReferable();

            ContactInfoReferable contactInfoReferable = new ContactInfoReferable();
            ContactInfo contactInfo = new ContactInfo();
            OtherContactInformation otherContactInformation = new OtherContactInformation();
            List<OtherContactItem> otherContactItems = otherContactInformation.getOtherContactItem();

            if (facilityNameValues != null) {
                facilityNameValues.forEach((facilityName, facilityValue) -> {
                    OtherContactItem otherContactItem = new OtherContactItem();
                    otherContactItem.setDetails(facilityValue);
                    otherContactItem.setInfoType(facilityName);
                    otherContactItems.add(otherContactItem);
                });
            }
            contactInfo.setOtherContactInformation(otherContactInformation);
            contactInfoReferable.setContactInfo(contactInfo);
            contactInfoReferableList.add(contactInfoReferable);

            getMetersByContactInfo.setArrayOfContactInfoReferable(arrayOfContactInfoReferable);

            GetMetersByContactInfoResponse response =
                cbClient.getMetersByContactInfo(mspVendor, endpointUrl, getMetersByContactInfo);
            if (response != null) {
                if (response.getMeters() != null) {
                    meterList = multispeakFuncs.getMspMeters(response.getMeters());
                }
            } else {
                log.error("Response not received for (" + mspVendor.getCompanyName() + ")");
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByContactInfo (" + mspVendor.getCompanyName()
                + ") ");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meterList;
    }

    @Override
    public List<MspMeter> getMetersByCustomerIDs(List<String> customerIDs, MultispeakVendor mspVendor) {
        List<MspMeter> meterList = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMetersByCustomerIDs getMetersByCustomerIDs = new GetMetersByCustomerIDs();
            ArrayOfCustomerID arrayOfCustomerID = new ArrayOfCustomerID();
            List<ObjectID> objectIDs = arrayOfCustomerID.getCustomerID();

            if (customerIDs != null) {
                customerIDs.forEach(customerID -> {
                    ObjectID objectID = new ObjectID();
                    objectID.setObjectGUID(customerID);
                    SingleIdentifier singleIdentifier = new SingleIdentifier();
                    singleIdentifier.setIdentifierName(MultispeakDefines.CUSTOMER_ID);
                    singleIdentifier.setValue(customerID);
                    objectID.setPrimaryIdentifier(singleIdentifier);
                    objectIDs.add(objectID);
                });
            }
            getMetersByCustomerIDs.setArrayOfCustomerID(arrayOfCustomerID);

            GetMetersByCustomerIDsResponse response =
                cbClient.getMetersByCustomerIDs(mspVendor, endpointUrl, getMetersByCustomerIDs);
            if (response != null) {
                if (response.getMeters() != null) {
                    meterList = multispeakFuncs.getMspMeters(response.getMeters());
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByCustomerIDs (" + mspVendor.getCompanyName()
                + ") ");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());

        }
        return meterList;
    }

    @Override
    public List<MspMeter> getMetersByAccountIDs(List<String> accoundIDs, MultispeakVendor mspVendor) {
        List<MspMeter> meterList = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMetersByAccountIDs getMetersByAccountIDs = new GetMetersByAccountIDs();
            ArrayOfAccountID arrayOfAccountID = new ArrayOfAccountID();
            List<ObjectID> objectIDs = arrayOfAccountID.getAccountID();

            if (accoundIDs != null) {
                accoundIDs.forEach(accountID -> {
                    ObjectID objectID = new ObjectID();
                    objectID.setObjectGUID(accountID);
                    SingleIdentifier singleIdentifier = new SingleIdentifier();
                    singleIdentifier.setIdentifierName(MultispeakDefines.ACCOUNT_ID);
                    singleIdentifier.setValue(accountID);
                    objectID.setPrimaryIdentifier(singleIdentifier);
                    objectIDs.add(objectID);
                });
            }
            getMetersByAccountIDs.setArrayOfAccountID(arrayOfAccountID);

            GetMetersByAccountIDsResponse response =
                cbClient.getMetersByAccountIDs(mspVendor, endpointUrl, getMetersByAccountIDs);

            if (response != null) {
                if (response.getMeters() != null) {
                    meterList = multispeakFuncs.getMspMeters(response.getMeters());
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByAccountIDs (" + mspVendor.getCompanyName()
                + ") ");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());

        }
        return meterList;
    }

    @Override
    public List<MspMeter> getMetersBySearchString(String searchString, MultispeakVendor mspVendor) {
        List<MspMeter> meterList = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            long start = System.currentTimeMillis();
            log.debug("Begin call to getMetersBySearchString for SearchString: %s" + searchString);

            GetMetersBySearchString getMetersBySearchString = new GetMetersBySearchString();
            getMetersBySearchString.setSearchString(searchString);
            GetMetersBySearchStringResponse response =
                cbClient.getMetersBySearchString(mspVendor, endpointUrl, getMetersBySearchString);
            log.debug("End call to getMetersBySearchString for SearchString:" + searchString + "  (took "
                + (System.currentTimeMillis() - start) + " millis)");
            if (response != null) {
                if (response.getMeters() != null) {
                    meterList = multispeakFuncs.getMspMeters(response.getMeters());
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersBySearchString (" + mspVendor.getCompanyName()
                + ") for SearchString: " + searchString);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meterList;
    }

    @Override
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws MultispeakWebServiceClientException {
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

    private String getMoreServiceLocations(MultispeakVendor mspVendor, String lastReceived,
            MultispeakGetAllServiceLocationsCallback callback) throws MultispeakWebServiceClientException {
        String lastSent = null;
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetAllServiceLocations getAllServiceLocations = new GetAllServiceLocations();
            getAllServiceLocations.setLastReceived(lastReceived);
            // get service locations
            GetAllServiceLocationsResponse response =
                cbClient.getAllServiceLocations(mspVendor, endpointUrl, getAllServiceLocations);
            if (response != null && response.getArrayOfServiceLocation() != null) {
                List<ServiceLocation> serviceLocations = response.getArrayOfServiceLocation().getServiceLocation();
                int serviceLocationCount = serviceLocations.size();
                int objectsRemaining = 0;
                String objectsRemainingStr = multispeakFuncs.getObjectRemainingValueFromHeader();
                if (!StringUtils.isBlank(objectsRemainingStr)) {
                    try {
                        objectsRemaining = Integer.valueOf(objectsRemainingStr);
                    } catch (NumberFormatException e) {
                        log.error("Non-integer value in header for ObjectsRemaining: " + objectsRemainingStr, e);
                    }
                }
                if (objectsRemaining != 0) {
                    lastSent = multispeakFuncs.getLastSentFromHeader();
                    log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                        + " ServiceLocations using lastReceived = " + lastReceived + ". Response: ObjectsRemaining = "
                        + objectsRemaining + ", LastSent = " + lastSent);
                } else {
                    log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                        + " ServiceLocations using LastSent = " + lastReceived + ". Response: ObjectsRemaining = "
                        + objectsRemaining);
                }

                // process service locations
                if (serviceLocationCount > 0) {
                    callback.processServiceLocations(serviceLocations);
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getAllServiceLocations (" + mspVendor.getCompanyName()
                + ") for LastReceived: " + lastReceived);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");

            throw e;
        }
        return lastSent;
    }

    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {
        List<String> domainNames = new ArrayList<>();
        domainNames.add("substationCode");
        return getMspSubstationName(mspVendor, domainNames);
    }
    
    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor, List<String> domainNames) {
        List<String> substationNames = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetDomainsByDomainNames getDomainsByDomainNames = new GetDomainsByDomainNames();
            ArrayOfString arrayOfDoaminNames = new ArrayOfString();
            List<String> domainNamesList = arrayOfDoaminNames.getTheString();

            if (domainNames != null) {
                domainNames.forEach(domainName -> {
                    domainNamesList.add(domainName);
                });
            }
            getDomainsByDomainNames.setArrayOfString(arrayOfDoaminNames);

            GetDomainsByDomainNamesResponse response =
                cbClient.getDomainsByDomainNames(mspVendor, endpointUrl, getDomainsByDomainNames);
            if (response != null) {
                ArrayOfDomain arrayOfDomain = response.getArrayOfDomain();
                if (arrayOfDomain != null) {
                    List<Domain> domains = arrayOfDomain.getDomain();

                    domains.forEach(domain -> {
                        DomainMembers domainMembers = domain.getDomainMembers();
                        if (domainMembers != null) {
                            List<DomainMember> domainMembersList = domainMembers.getDomainMember();
                            if (!domainMembersList.isEmpty()) {
                                domainMembersList.forEach(domainMember -> {
                                    substationNames.add(domainMember.getDescription());
                                });
                            }
                        }
                    });

                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMspSubstationName(" + mspVendor.getCompanyName());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return substationNames;
    }

    @Override
    public List<MspMeter> getMetersByNetworkModelRef(List<String> locations, MultispeakVendor mspVendor) {
        List<MspMeter> meterList = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMetersByNetworkModelRefs getMetersByNetworkModelRefs = new GetMetersByNetworkModelRefs();
            ArrayOfNetworkModelRef arrayOfNetworkModelRef = new ArrayOfNetworkModelRef();
            List<NetworkModelRef> networkModelRefList = arrayOfNetworkModelRef.getNetworkModelRef();

            if (locations != null) {
                locations.forEach(location -> {
                    NetworkModelRef networkModelRef = new NetworkModelRef();
                    networkModelRef.setNoun(new QName("http://www.multispeak.org/V5.0/commonTypes", "objectRef", "com"));
                    networkModelRef.setPrimaryIdentifierValue(location);
                    networkModelRef.setValue(location);
                    networkModelRef.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
                    networkModelRef.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
                    networkModelRefList.add(networkModelRef);
                });
            }
            getMetersByNetworkModelRefs.setArrayOfNetworkModelRef(arrayOfNetworkModelRef);

            GetMetersByNetworkModelRefsResponse response =
                cbClient.getMetersByNetworkModelRef(mspVendor, endpointUrl, getMetersByNetworkModelRefs);

            if (response != null) {
                Meters meters = response.getMeters();
                if (meters != null) {
                    meterList = multispeakFuncs.getMspMeters(meters);
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByNetworkModelRef (" + mspVendor.getCompanyName());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }

        return meterList;
    }

    @Override
    public List<Customer> getCustomersByMeterIDs(List<String> meterNumbers, MultispeakVendor mspVendor) {
       List<Customer> customers = new ArrayList<>();
       String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetCustomersByMeterIDs getCustomersByMeterIDs = new GetCustomersByMeterIDs();
            ArrayOfMeterID arrayOfMeterID = new ArrayOfMeterID();
            List<MeterID> meterIds = arrayOfMeterID.getMeterID();

            if (meterNumbers != null) {
                meterNumbers.forEach(meterNumber -> {
                    MeterID meterID = new MeterID();
                    meterID.setMeterName(meterNumber);
                    meterID.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
                    meterID.setServiceType(ServiceKind.ELECTRIC);
                    meterID.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
                    meterIds.add(meterID);
                });
            }
            getCustomersByMeterIDs.setArrayOfMeterID(arrayOfMeterID);

            GetCustomersByMeterIDsResponse getCustomersByMeterIDsResponse =
                cbClient.getCustomersByMeterIDs(mspVendor, endpointUrl, getCustomersByMeterIDs);
            
            if (getCustomersByMeterIDsResponse != null) {
                ArrayOfCustomer arrayOfCustomers = getCustomersByMeterIDsResponse.getArrayOfCustomer();
                if (arrayOfCustomers != null) {
                    customers = arrayOfCustomers.getCustomer();
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getCustomersByMeterIDs (" + mspVendor.getCompanyName());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return customers;
    }
    
    
    
    @Override
    public Customer getMspCustomer(SimpleMeter meter, MultispeakVendor mspVendor) {
        List<String> meterIds = new ArrayList<>();
        meterIds.add(meter.getMeterNumber());
        List<Customer> customers = getCustomersByMeterIDs(meterIds, mspVendor);
        return (customers.isEmpty() ? new Customer() : customers.get(0));
    }
}