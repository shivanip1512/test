package com.cannontech.web.api.admin.theme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;

@RestController
public class ThemeApiController {

    @Autowired private ThemeDao themeDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @GetMapping("/admin/config/currentTheme")
    public ResponseEntity<Object> currentTheme(YukonUserContext context) {

        List<Theme> themes = themeDao.getThemes();
        for (Theme theme : themes) {
            if (theme.isCurrentTheme()) {
                return new ResponseEntity<>(theme, HttpStatus.OK);
            }
        }

        final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        final String THEME_NOT_FOUND_MSG = "yukon.web.theme.error.NOT_FOUND";
        throw new NotFoundException(messageSourceAccessor.getMessage(THEME_NOT_FOUND_MSG));
    }

}
