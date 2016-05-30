package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class SendDefaultThermostatScheduleEndpoint {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private EnergyCompanyDao ecDao;
    
    private final Namespace ns = YukonXml.getYukonNamespace();
    private final Logger log = YukonLogManager.getLogger(SendDefaultThermostatScheduleEndpoint.class);
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendDefaultThermostatScheduleRequest")
    public Element invoke(Element sendDefaultThermostatSchedule, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendDefaultThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendDefaultThermostatSchedule);
        List<String> serialNumbers = requestTemplate.evaluateAsStringList("//y:serialNumber");

        // Log run program attempt
        for (String serialNumber : serialNumbers) {
            accountEventLogService.thermostatScheduleSendDefaultAttempted(user, serialNumber, EventSource.API);
        }
        
        // init response
        Element resp = new Element("sendDefaultThermostatScheduleResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, user);
            
            // Get the inventoryIds from the serial numbers supplied.
            int yukonEnergyCompanyId = ecDao.getEnergyCompanyByOperator(user).getId();
            List<Integer> inventoryIds = inventoryDao.getInventoryIds(serialNumbers, yukonEnergyCompanyId);
            
            // Send out default thermostat schedule
            Map<Integer, CustomerAccount> inventoryIdsToAccountMap = customerAccountDao.getInventoryIdsToAccountMap(inventoryIds);
            for (Integer inventoryId : inventoryIds) {
                CustomerAccount customerAccount = inventoryIdsToAccountMap.get(inventoryId);
                Thermostat thermostat = inventoryDao.getThermostatById(inventoryId);
                String defaultScheduleName = "Default "+thermostat.getSchedulableThermostatType();
                
                AccountThermostatSchedule defaultSchedule  = 
                    thermostatService.getAccountThermostatScheduleTemplate(customerAccount.getAccountId(), thermostat.getSchedulableThermostatType());
                defaultSchedule.setScheduleName(defaultScheduleName);

                // Check if the default schedule for the type already exists.  If it does use the schedule id so a new one isn't created.
                AccountThermostatSchedule existingDefaultSchedule = 
                    accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerAccount.getAccountId(), defaultScheduleName);
                if (existingDefaultSchedule != null) {
                    defaultSchedule.setAccountThermostatScheduleId(existingDefaultSchedule.getAccountThermostatScheduleId());
                }

                // Save the default schedule and send it out.
                accountThermostatScheduleDao.save(defaultSchedule);
                thermostatService.sendSchedule(customerAccount, defaultSchedule, Collections.singleton(inventoryId), defaultSchedule.getThermostatScheduleMode(), yukonEnergyCompanyId, user);
            }
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendDefaultThermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendDefaultThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
}