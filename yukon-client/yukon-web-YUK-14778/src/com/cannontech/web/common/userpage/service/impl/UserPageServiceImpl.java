package com.cannontech.web.common.userpage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.cannontech.web.layout.PageDetailProducer;
import com.cannontech.web.layout.PageDetailProducer.PageContext;
import com.cannontech.web.menu.Module;
import com.cannontech.web.menu.ModuleBuilder;
import com.cannontech.web.menu.PageInfo;

public class UserPageServiceImpl implements UserPageService {
    @Autowired private ModuleBuilder moduleBuilder;
    @Autowired private PageDetailProducer pageDetailProducer;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String getLocalizedPageTitle(UserPage userPage, YukonUserContext userContext) {
        Module moduleBase = moduleBuilder.getModule(userPage.getModule().getName());

        String pageTitle = null;
        PageInfo pageInfo = moduleBase.getPageInfo(userPage.getName());
        if (pageInfo == null) {
            // This shouldn't _normally_ happen but can in a development environment when switching workspaces
            // where one workspace has a new page the other doesn't.  (The new page will be in the user history
            // and trigger this.)
            pageTitle = "unknown page";
        } else {
            PageContext pageContext = new PageContext();
            pageContext.pageInfo = pageInfo;
            pageContext.labelArguments = userPage.getArguments();

            pageDetailProducer.fillInPageLabels(pageContext, messageSourceResolver.getMessageSourceAccessor(userContext));

            pageTitle = pageDetailProducer.getPagePart("pageTitle", pageContext,
                messageSourceResolver.getMessageSourceAccessor(userContext));
        }

        return pageTitle;
    }
}
