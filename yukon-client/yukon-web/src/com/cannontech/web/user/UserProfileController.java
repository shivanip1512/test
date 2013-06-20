package com.cannontech.web.user;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.service.EventLogService;
import com.cannontech.common.events.service.EventLogUIService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.login.PasswordResetController;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.user.model.ChangePassword;
import com.cannontech.web.user.model.UserProfile;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.user.validator.ChangePasswordValidator;
import com.cannontech.web.user.validator.UserProfileValidator;
import com.cannontech.web.util.JsonHelper;

@Controller
@RequestMapping("/*")
public class UserProfileController {

    private static final boolean ENABLE_CHANGE_USERNAME = false;
    private static final int PAGE_EVENT_ROW_COUNT = 10;
    static final int PASSWORD_EXPIRE_DAYS_TO_WARN = 30;
    private static final int DEFAULT_RETRY_PASSWORD_IN_SECONDS = 10;
    private static final String MSGKEY_BASE_PROFILE = "yukon.web.modules.user.profile.";
    private static final String MSGKEY_PASSWORD_CHANGE_SUCCESS = MSGKEY_BASE_PROFILE +"changePassword.success";
    private static final String MSGKEY_MISMATCHED_USERS = MSGKEY_BASE_PROFILE +"changePassword.user.mismatch";
    private static final String MSGKEY_CHANGE_PASSWORD_SYSTEMERR = MSGKEY_BASE_PROFILE +"changePassword.error.system_save";
    private static final String MSGKEY_PREF_BAD_URL = "yukon.web.modules.user.preferences.url.bad_format";


    private static final Logger log = YukonLogManager.getLogger(UserProfileController.class);

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactService contactService;
    @Autowired private ContactNotificationService contactNotificationService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EventLogService eventLogService;
    @Autowired private EventLogUIService eventLogUIService;
    @Autowired private JsonHelper jsonHelper;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private PaoPermissionService paoPermissionService;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private RoleDao roleDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private UserPreferencesHelper prefHelper;
    @Autowired private UserProfileHelper profileHelper;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UserPreferenceService prefService;

    @Autowired private UserProfileValidator userValidator;
    @Autowired private ChangePasswordValidator passwordValidator;

    /**
     * Existing change password functionality requires: "k", "userGroupName", and more {@link PasswordResetController}
     * Ideally we'd create/destroy changePwdKey (k) when the dialog appears/disappears.  As-is, we never destroy it.
     * {@link PasswordResetService.invalidatePasswordKey(k)}
     * 
     * @param model
     * @param context
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String profile(ModelMap model, LiteYukonUser user, YukonUserContext context) {

        model.addAttribute("mode", PageEditMode.VIEW);
        profileHelper.setupUserAndNotifications(model, user, context);
        prefHelper.setupUserPreferences(model, user);
        prefHelper.buildPreferenceOptions(model);
        profileHelper.setupRoleGroups(model, user.getUserGroupId());
        profileHelper.setupActivityStream(model, user, 0, PAGE_EVENT_ROW_COUNT);
        profileHelper.setupPasswordData(model, user);

        return "profile.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activityStream/{startIndex}.html")
    public String loadActivityStream(@PathVariable Integer startIndex,
                                                   ModelMap model,
                                                   LiteYukonUser user,
                                                   YukonUserContext context) {

        startIndex = startIndex < 0 ? 0 : startIndex;
        profileHelper.setupActivityStream(model, user, startIndex, PAGE_EVENT_ROW_COUNT);
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
                                                        YukonUserContext context, 
                                                        FlashScope flash) {

        // validate user permissions: so far, just verify that the current user is editing themself.
        if (user.getUserID() != profile.getUserId()) {
            flash.setConfirm(new YukonMessageSourceResolvable(MSGKEY_BASE_PROFILE +"EDIT.FAIL.cannotEditOtherUser"));
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
                if (ENABLE_CHANGE_USERNAME) {
                    if (!user.getUsername().equals(profile.getUsername())) {
                        user.setUsername(profile.getUsername());
                        yukonUserDao.update(user);
                    }
                }
            } catch (IllegalArgumentException iae) {
                // TODO FIXME REVISE catch block to something meaningful...
                result.reject(MSGKEY_BASE_PROFILE +"EDIT.FAIL.databaseProblem");
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
     * Returns an empty {@link JSONObject} on success.
     * Failure may throw exception or return an empty {@link JSONObject}.
     */
    @RequestMapping (value="/updatePreference.json")
    public @ResponseBody JSONObject updatePreference(Integer userId,
                                                     String prefName,
                                                     String prefValue,
                                                     LiteYukonUser user,
                                                     YukonUserContext context,
                                                     HttpServletRequest request) {

        profileHelper.isUserAuthorized(user, userId);
        UserPreferenceName preference = UserPreferenceName.getName(prefName); // valueOf FAILS

        // TODO FIXME: HOW to validate that prefValue is one of the options for prefName?
        if (preference.getValueType() == InputTypeFactory.stringType()) {
            if (preference.toString().endsWith("_URL")) {
                MessageSourceAccessor messenger = resolver.getMessageSourceAccessor(context);
                if (StringUtils.isBlank(prefValue)) {   // Allow blanks
                    
                // If it is a full URL, error out if it isn't on this website.
                } else if (YukonValidationUtils.isBasicUrl(prefValue)) {
                    String currentPrefix = ServletUtil.getHostURL(request).toString();
                    if (prefValue.startsWith(currentPrefix)) { // Allow
                        // Save only the path information
                        prefValue = prefValue.substring(currentPrefix.length());
                    } else {
                        return jsonHelper.failToJSON("notificationValue", MSGKEY_PREF_BAD_URL, messenger);
                    }
                    
                // Save URL paths, ie. anything after the http://*/, eg. "/user/profile"
                } else if (YukonValidationUtils.isUrlPath(prefValue)) { // Allow
                } else {
                    return jsonHelper.failToJSON("notificationValue", MSGKEY_PREF_BAD_URL, messenger);
                }
            }
        }

        prefService.savePreference(user, preference, prefValue);

        return jsonHelper.succeed();
    }

    /**
     * 
     * @param userId
     * @param context
     * @return              JSONObject [
     *                              'preferences' : [
     *                                      'name' : String,
     *                                      'prefType' : 'EnumType' or YUPN,
     *                                      'defaultVal' : String ] ]
     */
    @RequestMapping(method = RequestMethod.POST, value="/updatePreferences/all/default.json")
    public @ResponseBody JSONObject resetAllPreferences(Integer userId, LiteYukonUser user, YukonUserContext context) {

        profileHelper.isUserAuthorized(user, userId);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);

        prefService.deleteAllSavedPreferencesForUser(user);

        JSONObject data = new JSONObject();
        JSONArray prefList = prefHelper.setupPreferenceDefaults(accessor);
        data.put("preferences", prefList);
        return data;
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
    public @ResponseBody JSONObject updatePassword(@ModelAttribute(value="changePassword") ChangePassword changePassword,
                                                   BindingResult bindingResult,
                                                   LiteYukonUser user,
                                                   YukonUserContext context,
                                                   HttpServletRequest req,
                                                   HttpServletResponse resp) {

        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(context);

        JSONObject result = new JSONObject();
        if (user.getUserID() != changePassword.getUserId().intValue()) {
            bindingResult.reject(MSGKEY_MISMATCHED_USERS);
            result.put("secondsToWait", DEFAULT_RETRY_PASSWORD_IN_SECONDS); // Not currently using this value...
            return jsonHelper.failToJSON(result, bindingResult, messageSourceAccessor);
        }

        // Validate: skip the no-match message since that's already done within the UI.
        passwordValidator.setAddMessageConfirmPasswordDoesntMatch(false);
        passwordValidator.validate(changePassword, bindingResult);
        result.put("secondsToWait", changePassword.getRetrySeconds() == null
                ? DEFAULT_RETRY_PASSWORD_IN_SECONDS : changePassword.getRetrySeconds());

        if (!bindingResult.hasErrors()) {
            try {
                authenticationService.setPassword(user, changePassword.getNewPassword()); // This cannot fail?
                JSONObject json = jsonHelper.succeed(messageSourceAccessor.getMessage(MSGKEY_PASSWORD_CHANGE_SUCCESS));
                json.put("new_date", dateFormattingService.format(new Date(), DateFormatEnum.DATE, context));
                return json;
            } catch (NoSuchMessageException|UnsupportedOperationException e) {
                log.info("Failed saving new password", e);
                bindingResult.reject(MSGKEY_CHANGE_PASSWORD_SYSTEMERR, new Object[]{e}, e.toString());
            }
        }

        return jsonHelper.failToJSON(result, bindingResult, messageSourceAccessor);
    }

}