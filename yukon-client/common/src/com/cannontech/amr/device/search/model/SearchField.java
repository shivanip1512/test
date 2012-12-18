package com.cannontech.amr.device.search.model;

import com.cannontech.common.i18n.DisplayableEnum;

public interface SearchField extends DisplayableEnum {
    String getFieldName();
    String getQueryField();
    boolean isVisible();
}
