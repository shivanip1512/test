package com.cannontech.yukon.api.stars.endpoint;

import java.util.List;

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
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.FailedThermostatCommandException;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class RunThermostatProgramEndpoint {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(RunThermostatProgramEndpoint.class);
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="runThermostatProgramRequest")
    public Element invoke(Element runThermostatProgram, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(runThermostatProgram, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(runThermostatProgram);
        List<String> serialNumbers = requestTemplate.evaluateAsStringList("//y:serialNumber");

        // Log run program attempt
        for (String serialNumber : serialNumbers) {
            accountEventLogService.thermostatRunProgramAttemptedByApi(user, serialNumber);
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
            
            // Send out run thermostat program commands
            YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            List<Integer> thermostatIds = inventoryDao.getInventoryIds(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());

            // Send out run program commands
            for (int thermostatId : thermostatIds) {
                result = thermostatService.runProgram(thermostatId, user);
                
                if (result.isFailed() && thermostatIds.size() > 1) {
                    result = ThermostatManualEventResult.MULTIPLE_ERROR;
                    break;
                }
            }

            if (result.isFailed()) {
                throw new FailedThermostatCommandException("Run Program Command Failed:"+result.toString());
            }
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(runThermostatProgram, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (FailedThermostatCommandException e) {
            Element fe = XMLFailureGenerator.generateFailure(runThermostatProgram, e, "RunProgramFailed", "The run program failed to send out.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(runThermostatProgram, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
}