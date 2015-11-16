package com.cannontech.web.common.events;

import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.JsTreeNode;

public class HighlightSelectedEventLogNodeAttributeSettingCallback 
                   implements NodeAttributeSettingCallback<String> {

	private String jsTreeSelectedNodePath;
    private String selectedEventType;
	
	public HighlightSelectedEventLogNodeAttributeSettingCallback(String eventType) {
        this.selectedEventType = eventType;
	}
	
	@Override
	public void setAdditionalAttributes(JsTreeNode node, String eventType) {
        if (this.selectedEventType.equals(eventType)) {
            
            jsTreeSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getjsTreeSelectedNodePath() {
		return jsTreeSelectedNodePath;
	}
}
