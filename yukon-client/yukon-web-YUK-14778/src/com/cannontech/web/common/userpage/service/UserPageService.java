package com.cannontech.web.common.userpage.service;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.user.YukonUserContext;

public interface UserPageService {
    String getLocalizedPageTitle(UserPage userPage, YukonUserContext userContext);
}
