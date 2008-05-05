package com.cannontech.web.i18n;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.servlet.tags.ThemeTag;

import com.cannontech.common.i18n.DisplayableEnum;

public class MsgTag extends ThemeTag {
    
    public void setKey(Object key) {
        if (key instanceof MessageSourceResolvable) {
            setCode(null);
            setMessage(key);
        } else if (key instanceof DisplayableEnum) {
            DisplayableEnum displayableEnum = (DisplayableEnum) key;
            setCode(displayableEnum.getFormatKey());
            setMessage(null);
        } else if (key instanceof String) {
            setCode((String) key);
            setMessage(null);
        } else {
            throw new IllegalArgumentException("Expected a String or MessageSourceResolvable, got a " + key.getClass().getName());
        }
    }
    
    public void setArgument(Object argument) {
        setArguments(new Object[]{argument});
    }
}
