package com.cannontech.web.i18n;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.servlet.tags.ThemeTag;

public class MsgTag extends ThemeTag {
    public void setKey(Object key) {
        if (key instanceof MessageSourceResolvable) {
            setMessage(key);
        } else if (key instanceof String) {
            setCode((String) key);
        } else {
            throw new IllegalArgumentException("Expected a String or MessageSourceResolvable, got a " + key.getClass().getName());
        }
    }
}
