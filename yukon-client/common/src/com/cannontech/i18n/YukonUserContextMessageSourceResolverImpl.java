package com.cannontech.i18n;

import org.springframework.context.MessageSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.user.YukonUserContext;

public class YukonUserContextMessageSourceResolverImpl implements
    YukonUserContextMessageSourceResolver {
    private ThemeSource themeSource;

    @Override
    public MessageSource getMessageSource(YukonUserContext userContext) {
        String themeName = userContext.getThemeName();
        
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
    
}
