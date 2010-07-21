package com.cannontech.web.common.events;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.ExtTreeNode;

public class HighlightSelectedEventLogNodeAttributeSettingCallback 
                   implements NodeAttributeSettingCallback<EventCategory> {

	private EventCategory selectedEventCategory;
	private String extSelectedNodePath;
	
	public HighlightSelectedEventLogNodeAttributeSettingCallback(EventCategory selectedEventCategory) {
		this.selectedEventCategory = selectedEventCategory;
	}
	
	@Override
	public void setAdditionalAttributes(ExtTreeNode node, EventCategory eventCategory) {
        if (this.selectedEventCategory != null && 
            this.selectedEventCategory.equals(eventCategory)) {
            node.setAttribute("cls", "highlightNode");
            extSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getExtSelectedNodePath() {
		return extSelectedNodePath;
	}
}
