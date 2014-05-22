package com.cannontech.web.dev;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.taglib.MessageScopeHelper;

@Controller
public class I18nController {

    @Autowired private ObjectFormattingService objectFormattingService;
    
    @RequestMapping("i18n")
    public String i18n(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "i18n/i18n.jsp";
    }
    
    @RequestMapping("i18n/scopes")
    public String i18nScopes(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "i18n/i18n_scopes.jsp";
    }
    
    public enum Colors implements DisplayableEnum {
        NONE,
        RED,
        ORANGE,
        YELLOW,
        BLUE,
        GREEN,
        INDIGO,
        VIOLET,
        OTHER,
        ;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dev.i18nDemo.colors." + name();
        }
    }

    public static class ScopePeeker {
        private HttpServletRequest request;

        private ScopePeeker(HttpServletRequest request) {
            this.request = request;
        }

        public String getScope() {
            return MessageScopeHelper.forRequest(request).toString();
        }
    }

    private void setupModel(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        
        model.addAttribute("scopePeeker", new ScopePeeker(request));

        model.addAttribute("numberZero", 0);
        model.addAttribute("numberOne", 1);
        model.addAttribute("numberTwo", 2);
        model.addAttribute("numberFive", 5);
        model.addAttribute("numberOneThird", 1.0 / 3.0);
        model.addAttribute("numberOneHalf", 1.0 / 2.0);
        model.addAttribute("numberTwoThirds", 2.0 / 3.0);
        model.addAttribute("numberFiveNinths", 5.0 / 9.0);
        model.addAttribute("largeDecimal", 9523155.235);

        List<BuiltInAttribute> atts =
            objectFormattingService.sortDisplayableValues(BuiltInAttribute.values(), null, null, userContext);
        model.addAttribute("builtInAttributes", atts);
        List<Colors> colors =
            objectFormattingService.sortDisplayableValues(Colors.values(), Colors.NONE, Colors.OTHER, userContext);
        model.addAttribute("colors", colors);
        model.addAttribute("thisNotThat", "This<br>Not That");
    }
    
}