package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import com.cannontech.common.events.model.EventLogColumnTypeEnum;

public class EventLogColumnTypePropertyEditor extends PropertyEditorSupport {

    public void setAsText(String eventLogColumnTypeStr) throws IllegalArgumentException {
        setValue(EventLogColumnTypeEnum.valueOf(eventLogColumnTypeStr));
    }

    public String getAsText() {
        EventLogColumnTypeEnum eventLogColumnType = (EventLogColumnTypeEnum) getValue();
        if (eventLogColumnType != null) {
            return eventLogColumnType.toString();
        }
        return null;
    }

}