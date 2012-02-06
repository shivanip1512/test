package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class SendThermostatScheduleEndpoint {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(SendThermostatScheduleEndpoint.class);
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendThermostatScheduleRequest")
    public Element invoke(Element sendThermostatSchedule, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendThermostatSchedule);
        List<String> serialNumbers = requestTemplate.evaluateAsStringList("//y:serialNumber");
        String scheduleName = requestTemplate.evaluateAsString("//y:scheduleName");

        // Log run program attempt
        for (String serialNumber : serialNumbers) {
            accountEventLogService.thermostatScheduleSendAttemptedByApi(user, serialNumber, scheduleName);
        }
        
        // init response
        Element resp = new Element("sendThermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, user);
            
            // Get the inventoryIds from the serial numbers supplied.
            YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            List<Integer> inventoryIds = inventoryDao.getInventoryIds(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());
            
            // Send out thermostat schedule
            Map<Integer, CustomerAccount> inventoryIdsToAccountMap = customerAccountDao.getInventoryIdsToAccountMap(inventoryIds);
            for (Integer inventoryId : inventoryIds) {
                CustomerAccount customerAccount = inventoryIdsToAccountMap.get(inventoryId);
                
                AccountThermostatSchedule ats = 
                        accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(customerAccount.getAccountId(), scheduleName);
                thermostatService.sendSchedule(customerAccount, ats, Collections.singleton(inventoryId), ats.getThermostatScheduleMode(), user);
            }
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (EmptyResultDataAccessException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "ScheduleNameDoesNotExist", "The schedule name supplied does not exist.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
}