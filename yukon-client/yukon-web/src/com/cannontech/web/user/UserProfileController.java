package com.cannontech.web.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.login.PasswordController;
import com.cannontech.web.smartNotifications.SmartNotificationFilter;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.user.model.ChangePassword;
import com.cannontech.web.user.model.UserProfile;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.user.validator.ChangePasswordValidatorFactory;
import com.cannontech.web.user.validator.ChangePasswordValidatorFactory.ChangePasswordValidator;
import com.cannontech.web.user.validator.UserProfileValidator;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/*")
public class UserProfileController {
    private static final Logger log = YukonLogManager.getLogger(UserProfileController.class);

    @Autowired private AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private UserPreferencesHelper prefHelper;
    @Autowired private UserProfileHelper profileHelper;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private UserProfileValidator userValidator;
    @Autowired private ChangePasswordValidatorFactory passwordValidatorFactory;

    private static final int pageEventRowCount = 10;
    private static final String baseKey = "yukon.web.modules.user.profile.";

    /**
     * Existing change password functionality requires: "k", "userGroupName", and more {@link PasswordController}
     * Ideally we'd create/destroy changePwdKey (k) when the dialog appears/disappears.  As-is, we never destroy it.
     * {@link PasswordResetService.invalidatePasswordKey(k)}
     * 
     * @param model
     * @param context
     * @return
     */
    @RequestMapping(value="profile", method = RequestMethod.GET)
    public String profile(ModelMap model, LiteYukonUser user, YukonUserContext context) {

        model.addAttribute("mode", PageEditMode.VIEW);
        profileHelper.setupUserAndNotifications(model, user, context);
        prefHelper.setupUserPreferences(model, user);
        profileHelper.setupPasswordData(model, user);
        model.addAttribute("filter", new SmartNotificationFilter());
        model.addAttribute("sendTime", userPreferenceService.getPreference(context.getYukonUser(), UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME));
        model.addAttribute("eventTypes", SmartNotificationEventType.values());

        return "profile.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activityStream/{startIndex}.html")
    public String loadActivityStream(@PathVariable Integer startIndex, ModelMap model, LiteYukonUser user) {

        startIndex = startIndex < 0 ? 0 : startIndex;
        profileHelper.setupActivityStream(model, user, startIndex, pageEventRowCount);
        return "activityStreamSection.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/profile/edit")
    public String editProfile(ModelMap model, LiteYukonUser user, YukonUserContext context) {

        model.addAttribute("mode", PageEditMode.EDIT);
        profileHelper.setupUserAndNotifications(model, user, context);
        return "profile.jsp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/profile/update")
    public String updateProfile(@ModelAttribute("userProfile") UserProfile profile, 
                                                        BindingResult result,
                                                        ModelMap model, 
                                                        LiteYukonUser user,
                                                        FlashScope flash) {

        // validate user permissions: so far, just verify that the current user is editing themself.
        if (user.getUserID() != profile.getUserId()) {
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey +"EDIT.FAIL.cannotEditOtherUser"));
            return "redirect:/user/profile";
        }
        profile.setUsername(user.getUsername());

        // Do all the validations, then if clear attempt committing all changes
        userValidator.validate(profile, result);
        if (!result.hasErrors()) {
            List<LiteContact> contacts = contactDao.getContactsByLoginId(user.getUserID());
            if (!contacts.isEmpty()) {
                profile.getContact().setContactId(contacts.get(0).getContactID());
            }
            try { // save all
                operatorAccountService.saveContactDto(profile.getContact(), user);
            } catch (IllegalArgumentException iae) {
                // TODO FIXME REVISE catch block to something meaningful...
                result.reject(baseKey +"EDIT.FAIL.databaseProblem");
                log.error("Problem while saving updates from user", iae);
            }
        }

        // Report any and all errors
        if (result.hasErrors()) {
            model.addAttribute("mode", PageEditMode.EDIT);
            model.addAttribute("userProfile", profile);
            model.addAttribute("notificationTypes", ContactNotificationType.values());
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return "profile.jsp";
        }

        return "redirect:/user/profile";
    }

    /**
     * Failure may throw exception or return success:true json
     */
    @RequestMapping(value="/updatePreference.json", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Boolean> updatePreference(Integer userId, UserPreferenceName prefName,
                                                 String prefValue, LiteYukonUser user) {
        profileHelper.isUserAuthorized(user, userId);
        userPreferenceService.savePreference(user, prefName, prefValue);
        return Collections.singletonMap("success", true);
    }

    @RequestMapping(method = RequestMethod.POST, value="/updatePreferences/all/default.json")
    public @ResponseBody Map<String, ?> resetAllPreferences(Integer userId, LiteYukonUser user) {

        profileHelper.isUserAuthorized(user, userId);
        userPreferenceService.deleteUserPreferencesByPreferenceType(user, PreferenceType.EDITABLE);

        List<Object> prefList = new ArrayList<>();
        for (UserPreferenceName pref : UserPreferenceName.getUserPreferencesByType(PreferenceType.EDITABLE)) {
            Map<String, Object> jsonPref = Maps.newHashMapWithExpectedSize(2);
            jsonPref.put("name", pref.name());
            jsonPref.put("defaultVal", pref.getDefaultValue());
            prefList.add(jsonPref);
        }

        return Collections.singletonMap("preferences", prefList);
    }

    /**
     * NOTE: Currently includes all validations.
     * NOTE: Assumes the new passwords cannot be empty/blank/spaces
     * 
     * @param changePassword        Form/Command object - note: we do not @Valid because of checking userIds and
     *                              custom passwordValidator setting.
     * @param bindingResult         SpringMVC binding result
     * @param context
     * @param req
     * @param resp
     * @return                      JSONObject [success: true|false, message: null | "changed!", 
     *                                          errors:[ JSONObject[field: "GLOBAL" | {fieldname}, 
     *                                                              message: "printable message", 
     *                                                              severity: "ERROR"]]]
     * @throws Exception
     * 
     * @see inspiration: {@link ChangeLoginController.updatePassword()}
     */
    @RequestMapping(value = "/updatePassword.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> updatePassword(@ModelAttribute ChangePassword changePassword,
                                                   BindingResult bindingResult,
                                                   LiteYukonUser user,
                                                   YukonUserContext context,
                                                   HttpServletRequest req,
                                                   HttpServletResponse resp) {

        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);

        Map<String, Object> result = new HashMap<>();
        if (user.getUserID() != changePassword.getUserId()) {
            bindingResult.reject(baseKey +"changePassword.error.user.mismatch");
        } else {
            UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
            AuthenticationCategory authCat = userAuthenticationInfo.getAuthenticationCategory();
            if (!authenticationService.supportsPasswordSet(authCat)) {
                bindingResult.reject(baseKey +"changePassword.error.user.notAllowed");
            }
        }
        if (bindingResult.hasErrors()) {
            result.putAll(getErrorJson(bindingResult, accessor));
            return result;
        }

        // Validate: skip the no-match message since that's already done within the UI.
        ChangePasswordValidator validator = passwordValidatorFactory.getValidator();
        validator.setAddMessageConfirmPasswordDoesntMatch(false);
        validator.validate(changePassword, bindingResult);
        result.put("secondsToWait", changePassword.getRetrySeconds());

        if (!bindingResult.hasErrors()) {
            try {
                authenticationService.setPassword(user, changePassword.getNewPassword(), context.getYukonUser()); // This cannot fail?
                Map<String, Object> json = new HashMap<>();
                json.put("success", true);
                json.put("message", accessor.getMessage(new YukonMessageSourceResolvable(baseKey +"changePassword.success")));
                json.put("new_date", dateFormattingService.format(new Date(), DateFormatEnum.DATE, context));
                return json;
            } catch (NoSuchMessageException|UnsupportedOperationException e) {
                log.info("Failed saving new password", e);
                bindingResult.reject(baseKey +"changePassword.error.system_save", new Object[]{e}, e.toString());
            }
        }

        result.putAll(getErrorJson(bindingResult, accessor));
        return result;
    }
    
    /**
     * Call this when a JSON-based action needs to fail and returns the list of errors as Map<String, Object>s.
     * @param errors  Errors or BindingResult
     * @param accessor MessageSourceAccessor to interpret message keys.
     * @return result a Map<String, Object>
     * 
     * @postcondition result['success'] = false
     * @postcondition result['errors'] = List<Object>[ 0+ Map<String, Object>[field:{String}, message:{String}, severity:"ERROR"]]
     */
    private Map<String, Object> getErrorJson(Errors errors, MessageSourceAccessor accessor) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", false);
        List<Object> errorList = new ArrayList<>();
        
        for (ObjectError err : errors.getGlobalErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", "GLOBAL");
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        for (FieldError err : errors.getFieldErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", err.getField());
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        result.put("errors", errorList);
        
        return result;
    }
    
    @RequestMapping(value = "/updateDisplayEventRangePreference.json", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Boolean> updateDisplayEventRangePreference(String prefValue, LiteYukonUser user) {
        // This preference does not require the check for the authorized user
        // No admin can set this preference for the other user.
        userPreferenceService.savePreference(user, UserPreferenceName.DISPLAY_EVENT_RANGE, prefValue);
        return Collections.singletonMap("success", true);
    }
}