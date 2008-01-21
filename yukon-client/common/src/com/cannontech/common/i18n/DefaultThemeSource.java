package com.cannontech.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.SimpleTheme;

public class DefaultThemeSource implements ThemeSource {

    private SimpleTheme simpleTheme;

    @Override
    public Theme getTheme(String themeName) {
        return simpleTheme;
    }

    public void setMessageSource(MessageSource messageSource) {
        simpleTheme = new SimpleTheme("default", messageSource);
    }
    

}
