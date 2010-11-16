package com.cannontech.web.common.events;

import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.ExtTreeNode;

public class HighlightSelectedEventLogNodeAttributeSettingCallback 
                   implements NodeAttributeSettingCallback<String> {

	private String extSelectedNodePath;
    private String selectedEventType;
	
	public HighlightSelectedEventLogNodeAttributeSettingCallback(String eventType) {
        this.selectedEventType = eventType;
	}
	
	@Override
	public void setAdditionalAttributes(ExtTreeNode node, String eventType) {
        if (this.selectedEventType.equals(eventType)) {
            node.setAttribute("cls", "highlightNode");
            extSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getExtSelectedNodePath() {
		return extSelectedNodePath;
	}
}
