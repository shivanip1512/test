package com.cannontech.common.pao.attribute.model;

import com.cannontech.common.i18n.Displayable;

/**
 * Class that represents an attribute of a device
 */
public interface Attribute extends Displayable {
    public String getKey();

    public String getDescription();
}
