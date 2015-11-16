package com.cannontech.web.dr.cc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.GroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.model.Exclusion;
import com.cannontech.web.dr.cc.service.CiCustomerVerificationService;

public class CiInitEventModelValidator extends SimpleValidator<CiInitEventModel> {
    @Autowired private CiCustomerVerificationService customerVerificationService;
    @Autowired private GroupService groupService;
    @Autowired private ProgramParameterDao programParameterDao;
    @Autowired private ProgramDao programDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private AfterInitValidator afterInitValidator;
    private AfterPricingValidator afterPricingValidator;
    private AfterGroupSelectionValidator afterGroupSelectionValidator;
    private AfterCustomerVerificationValidator afterCustomerVerificationValidator;
    private PreCreateValidator preCreateValidator;
    
    private static final String keyBase = "yukon.web.modules.dr.cc.init.error.";
    
    private CiInitEventModelValidator() {
        super(CiInitEventModel.class);
    }
    
    @Override
    protected void doValidation(CiInitEventModel target, Errors errors) {
        throw new MethodNotImplementedException("Use a subclass to perform validation.");
    }
    
    private final class AfterInitValidator extends CiInitEventModelValidator {
        @Override
        protected void doValidation(CiInitEventModel event, Errors errors) {
            Rejector rejector = new FieldSpecificRejector(errors);
            validateInitValues(event, rejector);
        }
    };
    
    private final class AfterPricingValidator extends CiInitEventModelValidator {
        @Override
        protected void doValidation(CiInitEventModel event, Errors errors) {
            Rejector rejector = new FieldSpecificRejector(errors);
            validatePrices(event, rejector);
        }
    };
    
    private final class AfterGroupSelectionValidator extends CiInitEventModelValidator {
        @Override
        protected void doValidation(CiInitEventModel event, Errors errors) {
            if (event.getSelectedGroupIds() == null) {
                errors.reject(key("noGroupsSelected"));
            }
        }
    };
    
    private final class AfterCustomerVerificationValidator extends CiInitEventModelValidator {
        @Override
        protected void doValidation(CiInitEventModel event, Errors errors) {
            if (event.getSelectedCustomerIds() == null) {
                errors.reject(key("noCustomersSelected"));
            }
        }
    };
    
    private final class PreCreateValidator extends CiInitEventModelValidator {
        @Override
        protected void doValidation(CiInitEventModel event, Errors errors) {
            // If accounting event, no validation required
            if (event.getEventType().isAccounting()) {
                return;
            }
            
            // Re-verify times
            Rejector rejector = new GeneralRejector(errors);
            validateInitValues(event, rejector);
            
            // Re-compute exclusions
            Map<Integer, List<Exclusion>> exclusions = new HashMap<>();
            List<Group> selectedGroups = groupService.getGroupsById(event.getSelectedGroupIds());
            List<GroupCustomerNotif> customerNotifs = customerVerificationService.getVerifiedCustomerList(event, selectedGroups, exclusions);
            
            if (!event.isEventExtension()) {
                // Check to make sure that none of the selected customers are now excluded
                for (Integer selectedCustomerId : event.getSelectedCustomerIds()) {
                    for (GroupCustomerNotif notif : customerNotifs) {
                        if (notif.getCustomer().getId() == selectedCustomerId) {
                            for (Exclusion exclusion : exclusions.get(notif.getId())) {
                                if (exclusion.isForceExcluded()) {
                                    String customerName = notif.getCustomer().getCompanyName();
                                    MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
                                    String reason = accessor.getMessage(exclusion.getMessage());
                                    errors.reject(key("customerExcluded"), new Object[] {customerName, reason}, "");
                                }
                            }
                        }
                    }
                }
            }
            
            // Re-verify prices (if econ event)
            if (event.getEventType().isEconomic()) {
                validatePrices(event, rejector);
            }
        }
    }

    public CiInitEventModelValidator getAfterInitValidator() {
        if (afterInitValidator == null) {
            afterInitValidator = new AfterInitValidator();
        }
        return afterInitValidator;
    }

    public CiInitEventModelValidator getAfterPricingValidator() {
        if (afterPricingValidator == null) {
            afterPricingValidator = new AfterPricingValidator();
        }
        return afterPricingValidator;
    }

    public CiInitEventModelValidator getAfterGroupSelectionValidator() {
        if (afterGroupSelectionValidator == null) {
            afterGroupSelectionValidator = new AfterGroupSelectionValidator();
        }
        return afterGroupSelectionValidator;
    }

    public CiInitEventModelValidator getAfterCustomerVerificationValidator() {
        if (afterCustomerVerificationValidator == null) {
            afterCustomerVerificationValidator = new AfterCustomerVerificationValidator();
        }
        return afterCustomerVerificationValidator;
    }
    
    public CiInitEventModelValidator getPreCreateValidator() {
        if (preCreateValidator == null) {
            preCreateValidator = new PreCreateValidator();
        }
        return preCreateValidator;
    }
    
    private static String key(String postfix) {
        return keyBase + postfix;
    }
    
    private int getMinimumDurationMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.MINIMUM_EVENT_DURATION_MINUTES);
    }
    
    private int getMinMinutesBetweenNotifAndStart(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.MINIMUM_NOTIFICATION_MINUTES);
    }
    
    private void validatePrices(CiInitEventModel event, Rejector rejector) {
        int numberOfWindows = event.getNumberOfWindows();
        List<BigDecimal> prices = event.getWindowPrices();
        while (prices.size() < numberOfWindows) {
            prices.add(null);
        }
        for (int i = 0; i < numberOfWindows; i++) {
            if (prices.get(i) == null) {
                rejector.reject("windowPrices[" + i + "]", key("windowPrice"));
            } else if (prices.get(i).signum() == -1) {
                rejector.reject("windowPrices[" + i + "]", key("windowPrice"));
            }
        }
        event.setWindowPrices(prices);
    }
    
    private void validateInitValues(CiInitEventModel event, Rejector rejector) {
        // No validation required for accounting events
        
        // Validation for notification and economic events
        if (event.getEventType().isNotification() || event.getEventType().isEconomic()) {
            
            // Is start time before notification?
            DateTime startTime = event.getStartTime();
            DateTime notifTime = event.getNotificationTime();
            if (startTime.isBefore(notifTime)) {
                rejector.reject("startTime", key("startTime"));
            }
            
            // Is there enough time between notification and start?
            Program program = programDao.getForId(event.getProgramId());
            int minMinutesBetweenNotifAndStart = getMinMinutesBetweenNotifAndStart(program);
            int minutesBetweenNotifAndStart = Minutes.minutesBetween(notifTime, startTime).getMinutes();
            if (minutesBetweenNotifAndStart < minMinutesBetweenNotifAndStart) {
                rejector.reject("notificationTime", key("notificationBeforeStart"), minMinutesBetweenNotifAndStart);
            }
        }
        
        // Validation for notif events only
        if (event.getEventType().isNotification()) {
            Program program = programDao.getForId(event.getProgramId());
            int minDuration = getMinimumDurationMinutes(program);
            if (event.getDuration() < minDuration) {
                rejector.reject("duration", key("duration"), minDuration);
            }
        }
        
        // Validation for economic events only
        if (event.getEventType().isEconomic()) {
            
            // Is the notification time in the past?
            if (event.getNotificationTime().isBeforeNow()) {
                rejector.reject("notificationTime", key("notificationPast"));
            }
            
            // Is there at least one pricing window?
            if (event.getNumberOfWindows() < 1) {
                rejector.reject("numberOfWindows", key("numberOfWindows"));
            }
        }
    }
    
    /**
     * Abstract class that provides the ability to reject values via the provided Errors object.
     */
    private abstract class Rejector {
        protected Errors errors;
        
        private Rejector(Errors errors) {
            this.errors = errors;
        }
        
        public abstract void reject(String field, String errorCode);
        public abstract void reject(String field, String errorCode, Object arg);
    }
    
    /**
     * Rejector that specifies the field being rejected, to be used on pages where the field being validated is
     * present.
     */
    private class FieldSpecificRejector extends Rejector {
        
        public FieldSpecificRejector(Errors errors) {
            super(errors);
        }
        
        @Override
        public void reject(String field, String errorCode) {
            errors.rejectValue(field, errorCode);
        }
        
        @Override
        public void reject(String field, String errorCode, Object arg) {
            errors.rejectValue(field, errorCode, new Object[] {arg}, "");
        }
    }
    
    /**
     * Rejector that ignores the field being rejected, to be used when validating fields that were populated on previous 
     * pages. All errors are treated as global.
     */
    private class GeneralRejector extends Rejector {
        
        public GeneralRejector(Errors errors) {
            super(errors);
        }
        
        @Override
        public void reject(String field, String errorCode) {
            errors.reject(errorCode);
        }
        
        @Override
        public void reject(String field, String errorCode, Object arg) {
            errors.reject(errorCode, new Object[] {arg}, "");
        }
    }
}
