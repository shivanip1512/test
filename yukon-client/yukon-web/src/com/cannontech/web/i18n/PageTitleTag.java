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

@Configurable(value="pageTitleTagPrototype", autowire=Autowire.BY_NAME)
public class PageTitleTag extends YukonTagSupport {
    @Autowired private UserPageService userPageService;

    private UserPage userPage;

    @Override
    public void doTag() throws JspException, IOException {
        String pageTitle = userPageService.getLocalizedPageTitle(userPage, getUserContext());
        pageTitle = HtmlUtils.htmlEscape(pageTitle);

        getJspContext().getOut().print(pageTitle);
    }

    public void setUserPage(UserPage userPage) {
        this.userPage = userPage;
    }
}
