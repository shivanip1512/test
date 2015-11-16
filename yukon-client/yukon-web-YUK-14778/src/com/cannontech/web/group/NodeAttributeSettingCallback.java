package com.cannontech.web.group;

import com.cannontech.web.util.JsTreeNode;

public interface NodeAttributeSettingCallback<T> {

    public void setAdditionalAttributes(JsTreeNode node, T nodeAttribute);
}
