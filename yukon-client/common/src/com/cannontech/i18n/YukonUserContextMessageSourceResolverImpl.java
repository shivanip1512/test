package com.cannontech.i18n;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.user.YukonUserContext;

public class YukonUserContextMessageSourceResolverImpl implements
    YukonUserContextMessageSourceResolver {
    private ThemeSource themeSource;
    private AuthDao authDao;
    private String defaultThemeName = "";


    @Override
    public MessageSource getMessageSource(YukonUserContext userContext) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
        String themeName = authDao.getRolePropertyValue(yukonUser, WebClientRole.THEME_NAME);
        if (StringUtils.isBlank(themeName)) {
            themeName = defaultThemeName;
        }
        themeName = themeName.trim();
        
        Theme theme = themeSource.getTheme(themeName);
        
        MessageSource messageSource = theme.getMessageSource();

        return messageSource;
    }

    @Override
    public MessageSourceAccessor getMessageSourceAccessor(YukonUserContext userContext) {
        MessageSource messageSource = getMessageSource(userContext);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource, userContext.getLocale());
        return messageSourceAccessor;
    }

    public void setThemeSource(ThemeSource themeSource) {
        this.themeSource = themeSource;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

}
