package com.cannontech.yukon.api.stars.endpoint;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.pao.model.GPS;
import com.cannontech.common.pao.service.impl.LocationServiceImpl;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressNotUpdatableException;
import com.cannontech.stars.dr.hardware.exception.DeviceVendorUserIdNotUpdatableException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.dr.route.exception.StarsRouteNotFoundException;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ControllableDevicesRequestEndPoint {

    private HardwareEventLogService hardwareEventLogService;
    private StarsControllableDeviceHelper starsControllableDeviceHelper;
    @Autowired private RolePropertyDao rolePropertyDao;    

    private Namespace ns = YukonXml.getYukonNamespace();

    // Request elements
    static final String newDevicesReqStr = "/y:newControllableDevicesRequest";
    static final String updateDevicesReqStr = "/y:updateControllableDevicesRequest";
    static final String removeDevicesReqStr = "/y:removeControllableDevicesRequest";
    static final String controllableDeviceListStr = "/y:controllableDeviceList";
    static final String controllableDeviceStr = "/y:controllableDevice";
    static final String newDeviceElementStr;
    static final String updateDeviceElementStr;
    static final String removeDeviceElementStr;
    static final String accountNumberStr = "y:accountNumber";
    static final String serialNumberStr = "y:serialNumber";
    static final String deviceTypeStr = "y:deviceType";
    static final String deviceLabelStr = "y:deviceLabel";
    static final String serviceCompanyNameStr = "y:serviceCompanyName";
    static final String macAddressStr = "y:macAddress";
    static final String deviceVendorUserIdStr = "y:deviceVendorUserId";
    static final String routeStr = "y:routeName";
    static final String fieldInstallDateStr = "y:fieldInstallDate";
    static final String fieldRemoveDateStr = "y:fieldRemoveDate";
    static final String gpsStr = "//y:gps";
    static final String parentLatitudeStr = "y:gps/y:latitude";
    static final String parentLongitudeStr = "y:gps/y:longitude";
    static final String latitudeStr = "y:latitude";
    static final String longitudeStr = "y:longitude";

    private static ControllableDeviceDTOMapper deviceElementMapper = new ControllableDeviceDTOMapper();
    private static RemoveControllableDeviceDTOMapper removeDeviceElementMapper = new RemoveControllableDeviceDTOMapper();
    
    // Response elements
    static final String newDevicesRespStr = "newControllableDevicesResponse";
    static final String updateDevicesRespStr = "updateControllableDevicesResponse";
    static final String removeDevicesRespStr = "removeControllableDevicesResponse";
    static final String controllableDeviceResultListStr = "controllableDeviceResultList";
    static final String controllableDeviceResultStr = "controllableDeviceResult";
    static final String accountNumberRespStr = "accountNumber";
    static final String serialNumberRespStr = "serialNumber";
    static final String successStr = "success";

    static {
        newDeviceElementStr = newDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
        updateDeviceElementStr = updateDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
        removeDeviceElementStr = removeDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "newControllableDevicesRequest")
    public Element invokeAddDevice(Element newControllableDevicesRequest, LiteYukonUser user) {
        // check request xml version
        XmlVersionUtils.verifyYukonMessageVersion(newControllableDevicesRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(newControllableDevicesRequest);
        List<LmDeviceDto> devices = template.evaluate(newDeviceElementStr, deviceElementMapper);

        // Log hardware addition attempts
        for (LmDeviceDto device : devices) {
            hardwareEventLogService.hardwareAdditionAttempted(user,
                                                              device.getAccountNumber(),
                                                              device.getSerialNumber(),
                                                              EventSource.API);
        }
        
        // check authorization
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, user);
        
        // run service
        for (LmDeviceDto device : devices) {
            try {
                if (device.getThrowable() == null) {
                    if (starsControllableDeviceHelper.isOperationAllowedForDevice(device, user)) {
                        starsControllableDeviceHelper.addDeviceToAccount(device, user);
                    } else {
                        throw new StarsClientRequestException("This operation is not supported for this device type");
                    }
                }
            } catch (StarsClientRequestException | ProcessingException e) {
                // store error and continue to process all devices
                device.setThrowable(e);
            }
        }

        // build response
        Element resp = buildResponse(newControllableDevicesRequest,
                                     newDevicesRespStr,
                                     devices);
        return resp;
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "updateControllableDevicesRequest")
    public Element invokeUpdateDevice(Element updateControllableDevicesRequest, LiteYukonUser user) {
        // check request xml version
        XmlVersionUtils.verifyYukonMessageVersion(updateControllableDevicesRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(updateControllableDevicesRequest);
        List<LmDeviceDto> devices = template.evaluate(updateDeviceElementStr, deviceElementMapper);        

        // check authorization
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);

        
        // run service
        for (LmDeviceDto device : devices) {
            try {
                hardwareEventLogService.hardwareUpdateAttempted(user,
                                                                device.getAccountNumber(),
                                                                device.getSerialNumber(),
                                                                EventSource.API);
                if (device.getThrowable() == null) {
                    if (starsControllableDeviceHelper.isOperationAllowedForDevice(device, user)) {
                        starsControllableDeviceHelper.updateDeviceOnAccount(device, user);
                    } else {
                        throw new StarsClientRequestException("This operation is not supported for this device type");
                    }
                }
            } catch (StarsClientRequestException | ProcessingException e) {
                // store error and continue to process all devices
                device.setThrowable(e);
            }
        }

        // build response
        Element resp = buildResponse(updateControllableDevicesRequest,
                                     updateDevicesRespStr,
                                     devices);
        return resp;
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "removeControllableDevicesRequest")
    public Element invokeRemoveDevice(Element removeControllableDevicesRequest, LiteYukonUser user) {
        // check request xml version
        XmlVersionUtils.verifyYukonMessageVersion(removeControllableDevicesRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(removeControllableDevicesRequest);
        List<LmDeviceDto> devices = template.evaluate(removeDeviceElementStr, removeDeviceElementMapper);        

        // check authorization
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user);


        // run service
        for (LmDeviceDto device : devices) {
            try {
                hardwareEventLogService.hardwareRemovalAttempted(user,
                                                                 device.getAccountNumber(),
                                                                 device.getSerialNumber(),
                                                                 EventSource.API);
                    if (starsControllableDeviceHelper.isOperationAllowedForDevice(device, user)) {
                        starsControllableDeviceHelper.removeDeviceFromAccount(device, user);
                    } else {
                        throw new StarsClientRequestException("This operation is not supported for this device type");
                    }
            } catch (StarsClientRequestException | ProcessingException e) {
                // store error and continue to process all devices
                device.setThrowable(e);
            }
        }

        // build response
        Element resp = buildResponse(removeControllableDevicesRequest,
                                     removeDevicesRespStr,
                                     devices);
        return resp;
    }

    // builds response with the given response element name
    private Element buildResponse(Element req, String respStr,
            List<LmDeviceDto> devices) {

        Element resp = new Element(respStr, ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element deviceResultList = new Element(controllableDeviceResultListStr, ns);
        for (LmDeviceDto device : devices) {

            Element deviceResult = new Element(controllableDeviceResultStr, ns);

            deviceResult.addContent(XmlUtils.createStringElement(accountNumberRespStr,
                                                                 ns,
                                                                 device.getAccountNumber()));
            deviceResult.addContent(XmlUtils.createStringElement(serialNumberRespStr,
                                                                 ns,
                                                                 device.getSerialNumber()));
            if (device.getThrowable() == null) {
                deviceResult.addContent(XmlUtils.createStringElement(successStr, ns, ""));
            } else {
                // build failure element
                Throwable t = device.getThrowable();
                Element fe = XMLFailureGenerator.generateFailure(req,
                                                                 t,
                                                                 getErrorCode(t),
                                                                 t.getMessage());
                deviceResult.addContent(fe);
            }

            deviceResultList.addContent(deviceResult);
        }
        resp.addContent(deviceResultList);

        return resp;
    }

    // Convert the Exception to an errorCode string
    private String getErrorCode(Throwable t) {
        String errorCode = "ProcessingError";
        ErrorCodeMapper errorCodeMapper = ErrorCodeMapper.valueOf(t);
        if (errorCodeMapper != null) {
            errorCode = errorCodeMapper.name();
        }
        return errorCode;
    }

    enum ErrorCodeMapper {
        AccountNotFound(StarsAccountNotFoundException.class),
        RouteNotFound(StarsRouteNotFoundException.class),
        DeviceAlreadyAssigned(StarsDeviceAlreadyAssignedException.class), 
        DeviceAlreadyExists(StarsDeviceAlreadyExistsException.class), 
        DeviceNotFoundOnAccount(StarsDeviceNotFoundOnAccountException.class), 
        SerialNumberAlreadyExists(StarsDeviceSerialNumberAlreadyExistsException.class), 
        MacAddressAlreadyExists(DeviceMacAddressAlreadyExistsException.class),
        MacAddressNotUpdatable(DeviceMacAddressNotUpdatableException.class),
        VendorUserIdNotUpdatable(DeviceVendorUserIdNotUpdatableException.class),
        InvalidArgument(StarsInvalidArgumentException.class), 
        InvalidDeviceType(StarsInvalidDeviceTypeException.class), 
        ClientRequestError(StarsClientRequestException.class),
        InvalidLocationDetails(ProcessingException.class);

        private Class<? extends Throwable> throwableClass;

        private ErrorCodeMapper(Class<? extends Throwable> throwableClass) {
            this.throwableClass = throwableClass;
        }

        public Class<? extends Throwable> getThrowableClass() {
            return throwableClass;
        }

        public static ErrorCodeMapper valueOf(Throwable throwable) {
            ErrorCodeMapper desiredErrorCodeMapper = null;
            for (ErrorCodeMapper errorCodeMapper : ErrorCodeMapper.values()) {
                if (errorCodeMapper.getThrowableClass()
                                   .isInstance(throwable)) {
                    desiredErrorCodeMapper = errorCodeMapper;
                    break;
                }
            }
            return desiredErrorCodeMapper;
        }
    }

    public static class RemoveControllableDeviceDTOMapper implements ObjectMapper<Node, LmDeviceDto> {

        @Override
        public LmDeviceDto map(Node from) throws ObjectMappingException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);
            LmDeviceDto device = new LmDeviceDto();
            buildDTOMapper(template, device, from);
            return device;
        }

    }

    public static class ControllableDeviceDTOMapper implements ObjectMapper<Node, LmDeviceDto> {

        @Override
        public LmDeviceDto map(Node from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);
            GPS gpsLocation = new GPS();
            LmDeviceDto device = new LmDeviceDto();
            device = buildDTOMapper(template, device, from);

            try {
                gpsLocation = validateGPSFields(template);

                if (gpsLocation != null) {
                    device.setGps(gpsLocation);
                }
            } catch (StarsClientRequestException e) {
                // store error and continue to map all devices.
                device.setThrowable(e);
            }

            return device;

        }
    }

    private static LmDeviceDto buildDTOMapper(SimpleXPathTemplate template, LmDeviceDto device, Node from) {

        device.setAccountNumber(template.evaluateAsString(accountNumberStr));
        device.setSerialNumber(template.evaluateAsString(serialNumberStr));
        device.setDeviceType(template.evaluateAsString(deviceTypeStr));
        device.setDeviceLabel(template.evaluateAsString(deviceLabelStr));
        device.setServiceCompanyName(template.evaluateAsString(serviceCompanyNameStr));
        device.setFieldInstallDate(template.evaluateAsDate(fieldInstallDateStr));
        device.setFieldRemoveDate(template.evaluateAsDate(fieldRemoveDateStr));
        device.setMacAddress(template.evaluateAsString(macAddressStr));
        device.setDeviceVendorUserId(template.evaluateAsInt(deviceVendorUserIdStr));
        device.setInventoryRoute(template.evaluateAsString(routeStr));
        
        return device;
        
    }
    /**
     * validateGPSFields
     * This method is for validating fields of GPS tag in XML.
     * If Parent tag(<api:gps>) is parent in request then Latitude and longitude fields are compulsory.
     * If GPS field is present and both latitude and longitude fields values are blank then it should save
     * successfully.
     * In request in case latitude or longitude is missing it that case there will be error message latitude
     * or longitude field is missing.
     */
    private static GPS validateGPSFields(SimpleXPathTemplate template) {
        GPS gps = new GPS();
        Node GPS = template.evaluateAsNode(gpsStr);
        Node parentLatitude = template.evaluateAsNode(parentLatitudeStr);
        Node parentLongitude = template.evaluateAsNode(parentLongitudeStr);
        Node latitudeNode = template.evaluateAsNode(latitudeStr);
        Node longitudeNode = template.evaluateAsNode(longitudeStr);

        if ((latitudeNode != null || longitudeNode != null) && GPS == null) {
            throw new StarsClientRequestException("Latitude and longitute fields parent GPS field is missing");
        } else if (GPS != null) {

            if (parentLatitude == null && parentLongitude == null) {
                throw new StarsClientRequestException(
                    "Latitude and longitute  Fields are required if parent GPS field present.");
            } else if (parentLatitude == null && parentLongitude != null) {
                throw new StarsClientRequestException("Latitude Field is required in GPS parent field");
            } else if (parentLatitude != null && parentLongitude == null) {
                throw new StarsClientRequestException("Longitude Field is required in GPS parent field");
            } else {
                String latitude = template.evaluateAsString(parentLatitudeStr);
                String longitude = template.evaluateAsString(parentLongitudeStr);
                try {
                    /*
                     * Added this check because if we keep empty latitude and longitude values that is
                     * allowed,
                     * eg.<gps>
                     *      <latitude></latitude>
                     *      <longitude></longitude>
                     * </gps>
                     * In case of empty latitude and longitude device should be successfully enrolled.
                     */
                    if (StringUtils.isNotEmpty(latitude) || StringUtils.isNotEmpty(longitude)) {
                        gps = LocationServiceImpl.getValidLocationFormat(latitude, longitude);
                    }
                } catch (IllegalArgumentException e) {
                    throw new StarsInvalidArgumentException(e.getMessage());
                }
            }
        }
        return gps;

    }

    @Autowired
    public void setStarsControllableDeviceHandler(
            StarsControllableDeviceHelper starsControllableDeviceHandler) {
        this.starsControllableDeviceHelper = starsControllableDeviceHandler;
    }
    
    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
}
