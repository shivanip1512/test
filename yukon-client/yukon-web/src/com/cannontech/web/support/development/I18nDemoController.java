package com.cannontech.web.support.development;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.taglib.MessageScopeHelper;

@Controller
@RequestMapping("/development/i18nDemo/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class I18nDemoController {
    public static class ScopePeeker {
        private HttpServletRequest request;

        private ScopePeeker(HttpServletRequest request) {
            this.request = request;
        }

        public String getScope() {
            return MessageScopeHelper.forRequest(request).toString();
        }
    }

    @RequestMapping("main")
    public void main(ModelMap model, HttpServletRequest request) {
        model.addAttribute("scopePeeker", new ScopePeeker(request));
    }
}
