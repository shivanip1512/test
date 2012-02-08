package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.ManualThermostatSettingElementRequestMapper;
import com.cannontech.yukon.api.stars.model.ManualThermostatSetting;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class SendManualThermostatSettingEndpoint {

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(SendManualThermostatSettingEndpoint.class);
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendManualThermostatSettingRequest")
    public Element invoke(Element sendManualThermostatSetting, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendManualThermostatSetting, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendManualThermostatSetting);

        ManualThermostatSetting manualThermostatSetting = 
                requestTemplate.evaluateAsObject("/y:sendManualThermostatSettingRequest/y:manualThermostatSettingList/y:manualThermostatSetting", 
                                         new NodeToElementMapperWrapper<ManualThermostatSetting>(new ManualThermostatSettingElementRequestMapper()));
         Map<String, Integer> serialNumberToInventoryIdMap = getSerialNumberToInventoryIdMap(manualThermostatSetting, user);
         
        // Log run program attempt
        for (String serialNumber : manualThermostatSetting.getSerialNumbers()) {
            Temperature heatTemperature = manualThermostatSetting.getHeatTemperature() ;
            Temperature coolTemperature = manualThermostatSetting.getCoolTemperature(); 

            accountEventLogService.thermostatManualSetAttemptedByApi(user, serialNumber, 
                                                                     (heatTemperature != null) ? heatTemperature.toFahrenheit().getValue() : null, 
                                                                     (coolTemperature != null) ? coolTemperature.toFahrenheit().getValue() : null, 
                                                                     manualThermostatSetting.getThermostatMode().name(), 
                                                                     manualThermostatSetting.getFanState().name(), manualThermostatSetting.isHoldTemperature());
        }
        
        // init response
        Element resp = new Element("sendManualThermostatProgramResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        ThermostatManualEventResult result = null;
        try {
            
            // Check authorization
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);
            
            /* Send out run thermostat program commands
             * Once the account dependence is  removed from the setupAndExecute command we'll be able to remove the customerAccountDao hit
             * and further simplify this code. 
             */
            for (String serialNumber : manualThermostatSetting.getSerialNumbers()) {
                int thermostatId = serialNumberToInventoryIdMap.get(serialNumber);
                CustomerAccount account = customerAccountDao.getAccountByInventoryId( thermostatId);

                // Send out manual thermostat commands
                result = thermostatService.executeManualEvent(thermostatId, manualThermostatSetting.getHeatTemperature(), manualThermostatSetting.getCoolTemperature(),
                                                              manualThermostatSetting.getThermostatMode(), manualThermostatSetting.getFanState(), manualThermostatSetting.isHoldTemperature(),  
                                                              manualThermostatSetting.isAutoModeCommand(), account, user);

                if (result.isFailed() && manualThermostatSetting.getSerialNumbers().size() > 1) {
                    result = ThermostatManualEventResult.MULTIPLE_ERROR;
                }
            }
            
            if (result.isFailed()) {
                Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, "ManualCommandFailed", "The manual command sent did not work.");
                resp.addContent(fe);
                return resp;
            }

        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
            return resp;
        }
        
        resp.addContent(new Element("success", ns));
        return resp;
    }

    /**
     * This method gets a list of thermostat manual events that can be sent to the thermostat service to change the temperature,
     * fan state, and mode for a given thermostat.
     */
    private Map<String, Integer> getSerialNumberToInventoryIdMap(ManualThermostatSetting manualThermostatSetting, LiteYukonUser user) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);

        // Get all the serial numbers used in this web service call.
        Collection<String> serialNumbers = manualThermostatSetting.getSerialNumbers();

         return inventoryDao.getSerialNumberToInventoryIdMap(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());
    }
}