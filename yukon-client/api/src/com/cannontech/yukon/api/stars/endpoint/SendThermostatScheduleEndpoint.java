package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
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
    @Autowired private EnergyCompanyDao ecDao;
    
    private final Namespace ns = YukonXml.getYukonNamespace();
    private final Logger log = YukonLogManager.getLogger(SendThermostatScheduleEndpoint.class);
    
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
            accountEventLogService.thermostatScheduleSendAttempted(user, serialNumber, scheduleName, EventSource.API);
        }
        
        // init response
        Element resp = new Element("sendThermostatScheduleResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // run service
        try {
            
            // Check authorization
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, user);
            
            // Get the inventoryIds from the serial numbers supplied.
            int yukonEnergyCompanyId = ecDao.getEnergyCompanyByOperator(user).getId();
            List<Integer> inventoryIds = inventoryDao.getInventoryIds(serialNumbers, yukonEnergyCompanyId);
            if(inventoryIds.isEmpty()){
                if(serialNumbers.isEmpty()){
                    Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, new Exception(), "SerialNumberDoesNotExist", "At least one Serial Number is required.");
                    resp.addContent(fe);
                }else if(serialNumbers.size() == 1){
                    Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, new Exception(), "SerialNumberDoesNotExist", "Serial Number supplied does not exist.");
                    resp.addContent(fe);
                }else{
                    Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, new Exception(), "SerialNumberDoesNotExist", "Serial Numbers supplied do not exist.");
                    resp.addContent(fe);
                }
            }else{
            
                // Send out thermostat schedule
                Map<Integer, CustomerAccount> inventoryIdsToAccountMap = customerAccountDao.getInventoryIdsToAccountMap(inventoryIds);
                for (Integer inventoryId : inventoryIds) {
                    CustomerAccount customerAccount = inventoryIdsToAccountMap.get(inventoryId);
                    
                    AccountThermostatSchedule ats = 
                            accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(customerAccount.getAccountId(), scheduleName);
                    thermostatService.sendSchedule(customerAccount, ats, Collections.singleton(inventoryId), ats.getThermostatScheduleMode(), yukonEnergyCompanyId, user);
                }
                // build response
                resp.addContent(new Element("success", ns));
            }
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
        } catch (EmptyResultDataAccessException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "ScheduleNameDoesNotExist", "The schedule name supplied does not exist.");
            resp.addContent(fe);
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendThermostatSchedule, e, "OtherException", "An exception has been caught."); 
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        return resp;
    }
}