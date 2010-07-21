package com.cannontech.web.group;

import com.cannontech.web.util.ExtTreeNode;

public interface NodeAttributeSettingCallback<T> {

    public void setAdditionalAttributes(ExtTreeNode node, T nodeAttribute);
}
