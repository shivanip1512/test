package com.cannontech.web.i18n;

import org.springframework.web.servlet.tags.ThemeTag;

public class MsgTag extends ThemeTag {
    public void setKey(String key) {
        setCode(key);
    }
}
