package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.WebSecurityChecker;
import com.cannontech.web.security.annotation.CheckRole;

@CheckRole(YukonRole.RESIDENTIAL_CUSTOMER)
@Controller
public class EnrollmentController extends AbstractConsumerController {
    private static final String KEY_PROGRAMID = "programId";
    private static final String KEY_INVENTORYID = "inventoryId";
    private static final String KEY_APPLIANCECATEGORYID = "applianceCategoryId";
    private static final String KEY_ENROLL = "enroll";
    private DisplayableEnrollmentDao displayableEnrollmentDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private WebSecurityChecker webSecurityChecker;
    
    @RequestMapping(value = "/consumer/enrollment", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        webSecurityChecker.checkRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT);
        
        List<DisplayableEnrollment> enrollments = 
            displayableEnrollmentDao.find(customerAccount.getAccountId());
        map.addAttribute("enrollments", enrollments);
        
        return "consumer/enrollment/enrollment.jsp";
    }
    
    @RequestMapping(value = "/consumer/enrollmentDetail", method = RequestMethod.GET)
    public String viewDetail(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		YukonUserContext yukonUserContext, ModelMap map) {
    	webSecurityChecker.checkRoleProperty(YukonRoleProperty.RESIDENTIAL_ENROLLMENT_PER_DEVICE);
        
    	List<DisplayableEnrollment> enrollments = 
    		displayableEnrollmentDao.find(customerAccount.getAccountId());
    	map.addAttribute("enrollments", enrollments);
    	
    	return "consumer/enrollment/enrollmentDetail.jsp";
    }
    
    @RequestMapping(value = "/consumer/enrollmentUpdate", method = RequestMethod.POST)
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            String json, String enrollPage, YukonUserContext yukonUserContext, HttpSession session, 
            ModelMap map) {
        webSecurityChecker.checkRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT,
                                             YukonRoleProperty.RESIDENTIAL_ENROLLMENT_PER_DEVICE);
        
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        
        JSONObject jsonObj = new JSONObject(json);
        List<ProgramEnrollment> requestList = new ArrayList<ProgramEnrollment>();
        List<Integer> reqInventoryIds = new ArrayList<Integer>();
        List<Integer> reqAppCategoryIds = new ArrayList<Integer>();
        
        @SuppressWarnings("unchecked") Iterator<String> iterator = jsonObj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject innerJSONObject = jsonObj.getJSONObject(key);

            @SuppressWarnings("unchecked") Iterator<String> iterator2 = innerJSONObject.keys();
            while (iterator2.hasNext()) {
                String key2 = iterator2.next();
                JSONObject value = innerJSONObject.getJSONObject(key2);
                int programId = value.getInt(KEY_PROGRAMID);
                int inventoryId = value.getInt(KEY_INVENTORYID);
                int applianceCategoryId = value.getInt(KEY_APPLIANCECATEGORYID);
                boolean enroll = value.getBoolean(KEY_ENROLL);

                // Can't check programId's during enrollment.
                reqInventoryIds.add(inventoryId);
                reqAppCategoryIds.add(applianceCategoryId);
                
                /* ProgramSignUpAction only cares about programs you are enrolling into,
                   this will be changing. */
                if (enroll) {
	                ProgramEnrollment enrollmentRequest = new ProgramEnrollment();
	                enrollmentRequest.setProgramId(programId);
	                enrollmentRequest.setInventoryId(inventoryId);
	                enrollmentRequest.setApplianceCategoryId(applianceCategoryId);
	                enrollmentRequest.setEnroll(enroll);
                    requestList.add(enrollmentRequest);
                }    
            }
        }
        // set the group, relay at one go
        setExistingLMGroupAndRelay(requestList, customerAccount.getAccountId());
        
        // verify all ids with accountCheckerService in one go
        accountCheckerService.checkInventory(user, reqInventoryIds.toArray(new Integer[reqInventoryIds.size()]));
        accountCheckerService.checkApplianceCategory(user, reqAppCategoryIds.toArray(new Integer[reqAppCategoryIds.size()]));
        
        ProgramEnrollmentResultEnum enrollmentResultEnum = 
            programEnrollmentService.applyEnrollmentRequests(customerAccount, requestList, yukonUserContext.getYukonUser());
        map.addAttribute("enrollmentResult", enrollmentResultEnum);
        
        
        EventUtils.logSTARSEvent(user.getUserID(),
                                 EventUtils.EVENT_CATEGORY_ACCOUNT,
                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                 customerAccount.getAccountId(),
                                 session);
        
        map.addAttribute("backUrl", "/spring/stars/consumer/" + enrollPage);
        
        return "consumer/enrollment/enrollmentResult.jsp";
    }

    private void setExistingLMGroupAndRelay(List<ProgramEnrollment> enrollRequestList, int accountId){
        List<LMHardwareControlGroup> lmHardwareControlGroupList = 
            lmHardwareControlGroupDao.getCurrentEnrollmentByAccountId(accountId);

        for (ProgramEnrollment enrollRequest : enrollRequestList) {
            if (enrollRequest.isEnroll()) {
                for (LMHardwareControlGroup existingEntry : lmHardwareControlGroupList) {
                    if(existingEntry.getInventoryId() == enrollRequest.getInventoryId() && existingEntry.getProgramId() == enrollRequest.getProgramId()){
                        enrollRequest.setLmGroupId(existingEntry.getLmGroupId());
                        enrollRequest.setRelay(existingEntry.getRelay());
                        break;
                    }
                }
            }
        }
    }

    @Autowired
    public void setDisplayableEnrollmentDao(
            DisplayableEnrollmentDao displayableEnrollmentDao) {
        this.displayableEnrollmentDao = displayableEnrollmentDao;
    }
    
    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setWebSecurityChecker(WebSecurityChecker webSecurityChecker) {
        this.webSecurityChecker = webSecurityChecker;
    }
    
}
