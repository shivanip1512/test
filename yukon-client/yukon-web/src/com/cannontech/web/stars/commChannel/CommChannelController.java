package com.cannontech.web.stars.commChannel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/device/commChannel")
public class CommChannelController {

    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash) {
        return "/commChannel/list.jsp";
    }
}
