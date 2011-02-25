package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.OptOutHelper;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers.OptOutRequestElementMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class OptOutRequestEndpoint {
    private AccountEventLogService accountEventLogService;
    private OptOutService optOutService;
    private CustomerAccountDao customerAccountDao;
    private LMHardwareBaseDao lmHardwareBaseDao; 
    private RolePropertyDao rolePropertyDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;

    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "optOutRequest")
    public Element invoke(Element optOutRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(optOutRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(optOutRequest);
        OptOutHelper optOutHelper = template.evaluateAsObject("//y:optOutRequest", 
                                                              new NodeToElementMapperWrapper<OptOutHelper>(new OptOutRequestElementMapper()));

        Element resp = new Element("optOutResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element fe = null;
        try {
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            CustomerAccount customerAccount = 
                customerAccountDao.getByAccountNumberForDescendentsOfEnergyCompany(optOutHelper.getAccountNumber(), 
                                                                                   energyCompany);
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getBySerialNumber(optOutHelper.getSerialNumber());
            
            accountEventLogService.optOutAttemptedThroughApi(user, customerAccount.getAccountNumber(),
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             optOutHelper.getStartDate());
            
            OptOutRequest request = new OptOutRequest();
            List<Integer>inventoryIds = Collections.singletonList(lmHardwareBase.getInventoryId());
            request.setInventoryIdList(inventoryIds);  
            request.setDurationInHours(optOutHelper.getDuration().toPeriod().toStandardHours().getHours());
          
            if (optOutHelper.getStartDate() != null) {
                request.setStartDate(optOutHelper.getStartDate());
            }
           
            optOutService.optOutWithValidation(customerAccount, request, user, optOutHelper.getOptOutCounts());
            
            resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        } catch (OptOutException e) {
            fe = XMLFailureGenerator.generateFailure(optOutRequest,e, e.getErrorCode(), e.getMessage());
        } catch(NotAuthorizedException e) {
            fe = XMLFailureGenerator.generateFailure(optOutRequest, e, "UserNotAuthorized", "Insufficent privileges to perform this operation.");
        } catch (NotFoundException e){
            fe = XMLFailureGenerator.generateFailure(optOutRequest, e, "NotFound", e.getMessage());
        } finally {
            if (fe != null) {
                resp.addContent(fe);
            }
        }

        return resp;  
    }

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
}
