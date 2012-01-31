package com.cannontech.yukon.api.stars.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.FailedThermostatCommandException;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.ManualThermostatSettingElementRequestMapper;
import com.cannontech.yukon.api.stars.model.ManualThermostatSetting;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Sets;

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
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendManualThermostatSettingRequest")
    public Element invoke(Element sendManualThermostatSetting, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendManualThermostatSetting, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendManualThermostatSetting);

        List<ManualThermostatSetting> manualThermostatSettings = 
                requestTemplate.evaluate("//y:manualThermostatSettingList/y:manualThermostatSetting", 
                                         new NodeToElementMapperWrapper<ManualThermostatSetting>(new ManualThermostatSettingElementRequestMapper()));
         Map<String, Integer> serialNumberToInventoryIdMap = getSerialNumberToInventoryIdMap(manualThermostatSettings, user);
         
        // Log run program attempt
        for (ManualThermostatSetting manualThermostatSetting : manualThermostatSettings) {
            for (String serialNumber : manualThermostatSetting.getSerialNumbers()) {
                accountEventLogService.thermostatManualSetAttemptedByApi(user, serialNumber, manualThermostatSetting.getTemperature().toFahrenheit().toIntValue(),
                                                                         manualThermostatSetting.getThermostatMode().toString(), manualThermostatSetting.getFanState().toString(),
                                                                         manualThermostatSetting.isHoldTemperature());
            }
        }
        
        // init response
        Element resp = new Element("runThermostatProgramResponse", ns);
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
            for (ManualThermostatSetting manualThermostatSetting : manualThermostatSettings) {
                for (String serialNumber : manualThermostatSetting.getSerialNumbers()) {
                    int thermostatId = serialNumberToInventoryIdMap.get(serialNumber);
                    CustomerAccount account = customerAccountDao.getAccountByInventoryId( thermostatId);
                
                    // Send out manual thermostat commands
                    result = thermostatService.executeManualEvent(thermostatId, manualThermostatSetting.getTemperature(), manualThermostatSetting.getThermostatMode(),
                                                                  manualThermostatSetting.getFanState(), manualThermostatSetting.isHoldTemperature(),  account, user);
                    if (result.isFailed() && manualThermostatSetting.getSerialNumbers().size() > 1) {
                        result = ThermostatManualEventResult.MULTIPLE_ERROR;
                        break;
                    }
                }
            }
            
            if (result.isFailed()) {
                throw new FailedThermostatCommandException("Manual Thermostat Command Failed:"+result.toString());
            }

        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (FailedThermostatCommandException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, e, "ManualCommandFailed", "The manual command sent did not work.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendManualThermostatSetting, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        return resp;
    }

    /**
     * This method gets a list of thermostat manual events that can be sent to the thermostat service to change the temperature,
     * fan state, and mode for a given thermostat.
     */
    private Map<String, Integer> getSerialNumberToInventoryIdMap(List<ManualThermostatSetting> manualThermostatSettings, LiteYukonUser user) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);

        // Get all the serial numbers used in this web service call.
        final Set<String> serialNumbers = Sets.newHashSet();
        for (ManualThermostatSetting manualThermostatSetting : manualThermostatSettings) {
            serialNumbers.addAll(manualThermostatSetting.getSerialNumbers());
        }

         return inventoryDao.getSerialNumberToInventoryIdMap(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());
    }
}