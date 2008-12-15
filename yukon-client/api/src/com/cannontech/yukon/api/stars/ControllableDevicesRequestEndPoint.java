package com.cannontech.yukon.api.stars;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Node;

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
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.helper.StarsControllableDeviceHelper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ControllableDevicesRequestEndPoint {
    private static Logger log = YukonLogManager.getLogger(ControllableDevicesRequestEndPoint.class);
    private StarsControllableDeviceHelper starsControllableDeviceHelper;

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
    static final String fieldInstallDateStr = "y:fieldInstallDate";
    static final String fieldRemoveDateStr = "y:fieldRemoveDate";

    private static ControllableDeviceDTOMapper deviceElementMapper = new ControllableDeviceDTOMapper();

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

    @PostConstruct
    public void initialize() throws JDOMException {
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "newControllableDevicesRequest")
    public Element invokeAddDevice(Element newControllableDevicesRequest, LiteYukonUser user) {
        // check request xml version
        XmlVersionUtils.verifyYukonMessageVersion(newControllableDevicesRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(newControllableDevicesRequest);
        List<StarsControllableDeviceDTO> devices = template.evaluate(newDeviceElementStr, deviceElementMapper);
        
        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.addDeviceToAccount(device, user);
            } catch (StarsClientRequestException e) {
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
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(updateControllableDevicesRequest);
        List<StarsControllableDeviceDTO> devices = template.evaluate(updateDeviceElementStr, deviceElementMapper);        

        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.updateDeviceOnAccount(device, user);
            } catch (StarsClientRequestException e) {
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
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(removeControllableDevicesRequest);
        List<StarsControllableDeviceDTO> devices = template.evaluate(removeDeviceElementStr, deviceElementMapper);        

        // run service
        for (StarsControllableDeviceDTO device : devices) {
            try {
                starsControllableDeviceHelper.removeDeviceFromAccount(device, user);
            } catch (StarsClientRequestException e) {
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
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
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
        String errorCode = "ProcessingError";
        ErrorCodeMapper errorCodeMapper = ErrorCodeMapper.valueOf(t);
        if (errorCodeMapper != null) {
            errorCode = errorCodeMapper.name();
        }
        return errorCode;
    }

    enum ErrorCodeMapper {
        AccountNotFound(StarsAccountNotFoundException.class), 
        DeviceAlreadyAssigned(StarsDeviceAlreadyAssignedException.class), 
        DeviceAlreadyExists(StarsDeviceAlreadyExistsException.class), 
        DeviceNotFoundOnAccount(StarsDeviceNotFoundOnAccountException.class), 
        SerialNumberAlreadyExists(StarsDeviceSerialNumberAlreadyExistsException.class), 
        InvalidArgument(StarsInvalidArgumentException.class), 
        InvalidDeviceType(StarsInvalidDeviceTypeException.class), 
        ClientRequestError(StarsClientRequestException.class);

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

    public static class ControllableDeviceDTOMapper implements
            ObjectMapper<Node, StarsControllableDeviceDTO> {

        @Override
        public StarsControllableDeviceDTO map(Node from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = XmlUtils.getXPathTemplateForNode(from);

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
