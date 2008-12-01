package com.cannontech.yukon.api.stars;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceNotFoundOnAccountException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsInvalidDeviceTypeException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.helper.StarsControllableDeviceHelper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ControllableDevicesRequestEndPoint {
    private static Logger log = YukonLogManager.getLogger(ControllableDevicesRequestEndPoint.class);
    private StarsControllableDeviceHelper starsControllableDeviceHelper;

    private Namespace ns = YukonXml.getYukonNamespace();

    // Request elements
    static final String REQ_XML_VERSION_1_0 = "1.0";
    static final String newDevicesReqStr = "/y:newControllableDevicesRequest";
    static final String updateDevicesReqStr = "/y:updateControllableDevicesRequest";
    static final String removeDevicesReqStr = "/y:removeControllableDevicesRequest";
    static final String reqVersionStr = "//@version";
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
    static final String fieldInstallDateStr = "y:fieldInstallDate";
    static final String fieldRemoveDateStr = "y:fieldRemoveDate";

    private static NodeToElementMapperWrapper<StarsControllableDeviceDTO> deviceElementMapper;

    // Response elements
    static final String newDevicesRespStr = "newControllableDevicesResponse";
    static final String updateDevicesRespStr = "updateControllableDevicesResponse";
    static final String removeDevicesRespStr = "removeControllableDevicesResponse";
    static final String versionRespAttrStr = "version";    
    static final String controllableDeviceResultListStr = "controllableDeviceResultList";
    static final String controllableDeviceResultStr = "controllableDeviceResult";
    static final String accountNumberRespStr = "accountNumber";
    static final String serialNumberRespStr = "serialNumber";
    static final String successStr = "success";

    static {
        newDeviceElementStr = newDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
        updateDeviceElementStr = updateDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
        removeDeviceElementStr = removeDevicesReqStr + controllableDeviceListStr + controllableDeviceStr;
        
        deviceElementMapper = new NodeToElementMapperWrapper<StarsControllableDeviceDTO>(new ControllableDeviceDTOMapper());
    }

    @PostConstruct
    public void initialize() throws JDOMException {
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "newControllableDevicesRequest")
    public Element invokeAddDevice(Element newControllableDevicesRequest, LiteYukonUser user) {
        List<StarsControllableDeviceDTO> devices = null;

        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(newControllableDevicesRequest);

        // check request xml version
        String version = template.evaluateAsString(reqVersionStr);
        if (!StringUtils.isBlank(version) && version.equals(REQ_XML_VERSION_1_0)) {
            devices = template.evaluate(newDeviceElementStr,
                                        deviceElementMapper);
        } else {
            throw new RuntimeException("Request XML version is not specified or not valid");
        }

        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.addDeviceToAccount(device, user);
            } catch (RuntimeException e) {
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

        List<StarsControllableDeviceDTO> devices = null;
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(updateControllableDevicesRequest);

        // check request xml version
        String version = template.evaluateAsString(reqVersionStr);
        if (!StringUtils.isBlank(version) && version.equals(REQ_XML_VERSION_1_0)) {
            devices = template.evaluate(updateDeviceElementStr,
                                        deviceElementMapper);
        } else {
            throw new RuntimeException("Request XML version is not specified or not valid");
        }

        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.updateDeviceOnAccount(device, user);
            } catch (RuntimeException e) {
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

        List<StarsControllableDeviceDTO> devices = null;
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(removeControllableDevicesRequest);

        // check request xml version
        String version = template.evaluateAsString(reqVersionStr);
        if (!StringUtils.isBlank(version) && version.equals(REQ_XML_VERSION_1_0)) {
            devices = template.evaluate(removeDeviceElementStr,
                                        deviceElementMapper);
        } else {
            throw new RuntimeException("Request XML version is not specified or not valid");
        }
        
        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.removeDeviceFromAccount(device, user);
            } catch (RuntimeException e) {
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
            List<StarsControllableDeviceDTO> devices) {

        Element resp = new Element(respStr, ns);
        Attribute versionAttr = new Attribute(versionRespAttrStr, REQ_XML_VERSION_1_0);
        resp.setAttribute(versionAttr);
        
        Element deviceResultList = new Element(controllableDeviceResultListStr, ns);
        for (StarsControllableDeviceDTO device : devices) {

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
        String errorCode = ErrorCodeMapper.valueOf(t).name();
        return errorCode;
    }

    enum ErrorCodeMapper {
        AccountNotFound(StarsAccountNotFoundException.class.getName()), 
        DeviceAlreadyAssigned(StarsDeviceAlreadyAssignedException.class.getName()), 
        DeviceAlreadyExists(StarsDeviceAlreadyExistsException.class.getName()), 
        DeviceNotFoundOnAccount(StarsDeviceNotFoundOnAccountException.class.getName()), 
        SerialNumberAlreadyExists(StarsDeviceSerialNumberAlreadyExistsException.class.getName()), 
        InvalidArgument(StarsInvalidArgumentException.class.getName()), 
        InvalidDeviceType(StarsInvalidDeviceTypeException.class.getName()), 
        ProcessingError(Throwable.class.getName());

        private String throwableClassName;

        private ErrorCodeMapper(String throwableClassName) {
            this.throwableClassName = throwableClassName;
        }

        public String getThrowableClassName() {
            return throwableClassName;
        }

        public static ErrorCodeMapper valueOf(Throwable throwable) {
            String throwableClassName = throwable.getClass().getName();
            if (throwableClassName.equals(AccountNotFound.getThrowableClassName())) {
                return AccountNotFound;
            } else if (throwableClassName.equals(DeviceAlreadyAssigned.getThrowableClassName())) {
                return DeviceAlreadyAssigned;
            } else if (throwableClassName.equals(DeviceAlreadyExists.getThrowableClassName())) {
                return DeviceAlreadyExists;
            } else if (throwableClassName.equals(DeviceNotFoundOnAccount.getThrowableClassName())) {
                return DeviceNotFoundOnAccount;
            } else if (throwableClassName.equals(SerialNumberAlreadyExists.getThrowableClassName())) {
                return SerialNumberAlreadyExists;
            } else if (throwableClassName.equals(InvalidArgument.getThrowableClassName())) {
                return InvalidArgument;
            } else if (throwableClassName.equals(InvalidDeviceType.getThrowableClassName())) {
                return InvalidDeviceType;                
            } else {
                return ProcessingError;
            }
        }
    }

    public static class ControllableDeviceDTOMapper implements
            ObjectMapper<Element, StarsControllableDeviceDTO> {

        @Override
        public StarsControllableDeviceDTO map(Element from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(from);

            StarsControllableDeviceDTO device = new StarsControllableDeviceDTO();
            device.setAccountNumber(template.evaluateAsString(accountNumberStr));
            device.setSerialNumber(template.evaluateAsString(serialNumberStr));
            device.setDeviceType(template.evaluateAsString(deviceTypeStr));
            device.setDeviceLabel(template.evaluateAsString(deviceLabelStr));
            device.setServiceCompanyName(template.evaluateAsString(serviceCompanyNameStr));
            device.setFieldInstallDate(template.evaluateAsDate(fieldInstallDateStr));
            device.setFieldRemoveDate(template.evaluateAsDate(fieldRemoveDateStr));

            return device;
        }
    }

    @Autowired
    public void setStarsControllableDeviceHandler(
            StarsControllableDeviceHelper starsControllableDeviceHandler) {
        this.starsControllableDeviceHelper = starsControllableDeviceHandler;
    }
}
