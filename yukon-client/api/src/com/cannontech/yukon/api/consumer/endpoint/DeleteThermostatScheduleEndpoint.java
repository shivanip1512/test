package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class DeleteThermostatScheduleEndpoint {

    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="deleteThermostatScheduleRequest")
    public Element invoke(Element deleteThermostatSchedule, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(deleteThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(deleteThermostatSchedule);

        // init response
        Element resp = new Element("deleteThermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        try {
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, userContext.getYukonUser());
            
            // Get all of the thermostat schedule ids and check if the user is allowed to delete them.
            List<Integer> thermostatScheduleIds = requestTemplate.evaluateAsIntegerList("//y:thermostatScheduleId");
            accountCheckerService.checkThermostatSchedule(userContext.getYukonUser(), thermostatScheduleIds.toArray(new Integer[thermostatScheduleIds.size()]));

            // Log thermostat schedule save attempt
            for (int thermostatScheduleId : thermostatScheduleIds) {
                AccountThermostatSchedule accountThermostatSchedule = accountThermostatScheduleDao.getById(thermostatScheduleId);
                CustomerAccount customerAccount = customerAccountDao.getById(accountThermostatSchedule.getAccountId());
                accountEventLogService.thermostatScheduleDeleteAttempted(userContext.getYukonUser(), customerAccount.getAccountNumber(),
                                                                         accountThermostatSchedule.getScheduleName(), EventSource.API);
            }
            
            accountThermostatScheduleDao.deleteByThermostatScheduleIds(thermostatScheduleIds);
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(deleteThermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(deleteThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(new Element("success", ns));

        return resp;
    }
}