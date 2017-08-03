package com.cannontech.web.input.type;

import com.cannontech.common.i18n.Displayable;

public interface InputOptionProvider extends Displayable {
    public boolean isEnabled();
    public String getValue();
}
