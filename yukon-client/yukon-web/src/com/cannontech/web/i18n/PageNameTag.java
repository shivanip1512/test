package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable(value="pageNameTagPrototype", autowire=Autowire.BY_NAME)
public class PageNameTag extends YukonTagSupport {
    @Autowired private UserPageService userPageService;

    private UserPage userPage;

    @Override
    public void doTag() throws JspException, IOException {
        String pageName = userPageService.getLocalizePageName(userPage, getUserContext());
        pageName = HtmlUtils.htmlEscape(pageName);

        getJspContext().getOut().print(pageName);
    }

    public void setUserPage(UserPage userPage) {
        this.userPage = userPage;
    }
}
