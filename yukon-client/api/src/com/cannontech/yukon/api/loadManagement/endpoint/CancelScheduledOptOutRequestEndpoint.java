package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.CancelOptOutHelper;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.exception.CancelOptOutException;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers.CancelOptOutRequestMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CancelScheduledOptOutRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private OptOutService optOutService;
    private CustomerAccountDao customerAccountDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private RolePropertyDao rolePropertyDao;
    private OptOutEventDao optOutEventDao;

    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "cancelScheduledOptOutRequest")
    public Element invoke(Element newOptOutCancelRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(newOptOutCancelRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(newOptOutCancelRequest);
        CancelOptOutHelper cancelOptOutHelper = template.evaluateAsObject("//y:cancelScheduledOptOutRequest", 
                                                              new NodeToElementMapperWrapper<CancelOptOutHelper>(new CancelOptOutRequestMapper()));

        Element resp = new Element("cancelScheduledOptOutResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element fe = null;
        
        CustomerAccount customerAccount = null;
        LMHardwareBase lmHardwareBase = null;
        
        try {
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompanyByUser(user);
            customerAccount = 
                customerAccountDao.getByAccountNumberForDescendentsOfEnergyCompany(cancelOptOutHelper.getAccountNumber(), 
                                                                                   energyCompany);
            lmHardwareBase = lmHardwareBaseDao.getBySerialNumber(cancelOptOutHelper.getSerialNumber());
            
            List<OptOutEventDto> events =
                optOutEventDao.getCurrentOptOuts(customerAccount.getAccountId(), lmHardwareBase.getInventoryId());
            
            LinkedList<Integer> eventIdList = new LinkedList<Integer>();
            
            boolean scheduledFound = false;
            
            for (OptOutEventDto event: events) {
                if (event.getState() == OptOutEventState.SCHEDULED) {
                    scheduledFound = true;
                    eventIdList.add(event.getEventId());
                    accountEventLogService.optOutCancelAttemptedThroughApi(user, customerAccount.getAccountNumber(), 
                                                                           lmHardwareBase.getManufacturerSerialNumber(),
                                                                           new Instant(event.getStartDate()),
                                                                           new Instant(event.getStopDate()));
                }
            }
            if (scheduledFound) {
                optOutService.cancelOptOut(eventIdList, user);
                resp.addContent(XmlUtils.createStringElement("success", ns, ""));
            } else {
                throw new CancelOptOutException("No scheduled opt outs found for device " + lmHardwareBase.getManufacturerSerialNumber());
            }
         
        } catch (NotAuthorizedException e) {
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, "UserNotAuthorized", e.getMessage());
        } catch (NotFoundException e) {
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, "NotFound", e.getMessage());
        } catch (OptOutException e) {
            fe = XMLFailureGenerator.generateFailure(newOptOutCancelRequest, e, e.getErrorCode(), e.getMessage());
        } finally {
            if (fe!=null) {
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
}
