package com.cannontech.web.admin.encryption;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
@RequestMapping("/encryption/*")
public class EncryptionController {
    private EncryptedRouteDao encryptedRouteDao;
    private final static String baseKey = "yukon.web.modules.adminSetup.encryption";

    private Validator detailsValidator =
        new SimpleValidator<EncryptedRoute>(EncryptedRoute.class) {
            @Override
            public void doValidation(EncryptedRoute encryptedRoute, Errors errors) {

                Pattern lengthPattern = Pattern.compile("^[0-9A-Fa-f]{40}$");
                YukonValidationUtils.regexCheck(errors,
                                                "value", encryptedRoute.getValue(),
                                                lengthPattern, baseKey + ".errorMsg.format");

            }
        };

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                       BindingResult bindingResult, FlashScope flashScope) {

        List<EncryptedRoute> encryptedRoutes = encryptedRouteDao.getAllEncryptedRoutes();
        model.addAttribute("encryptedRoutes", encryptedRoutes);
        model.addAttribute("showRouteError", null);

        return "encryption/view.jsp";
    }

    @RequestMapping("delete")
    public String delete(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                         BindingResult bindingResult, FlashScope flashScope) {

        encryptedRoute.setValue(null);
        encryptedRouteDao.saveEncryptedRoute(encryptedRoute);

        return "redirect:view";
    }

    @RequestMapping("save")
    public String add(HttpServletRequest request, ModelMap model, EncryptedRoute encryptedRoute,
                       BindingResult bindingResult, FlashScope flashScope) {

        detailsValidator.validate(encryptedRoute, bindingResult);

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            List<EncryptedRoute> encryptedRoutes = encryptedRouteDao.getAllEncryptedRoutes();
            model.addAttribute("encryptedRoutes", encryptedRoutes);
            model.addAttribute("showRouteError", encryptedRoute.getPaobjectId());

            return "encryption/view.jsp";
        }
        encryptedRouteDao.saveEncryptedRoute(encryptedRoute);
        return "redirect:view";
    }

    @Autowired
    public void setEncryptedRouteDao(EncryptedRouteDao encryptedRouteDao) {
        this.encryptedRouteDao = encryptedRouteDao;
    }

}