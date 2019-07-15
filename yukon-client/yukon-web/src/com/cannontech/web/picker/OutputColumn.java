package com.cannontech.web.picker;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class OutputColumn {
    private String field;
    @JsonDeserialize(as = YukonMessageSourceResolvable.class)
    private MessageSourceResolvable title;
    private int maxCharsDisplayed = 0;
    
    public OutputColumn() {
        
    }

    public OutputColumn(String field, MessageSourceResolvable title) {
        this.field = field;
        this.title = title;
    }

    public OutputColumn(String field, String titleKey) {
        this(field, new YukonMessageSourceResolvable(titleKey));
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @JsonDeserialize(as = YukonMessageSourceResolvable.class)
    public MessageSourceResolvable getTitle() {
        return title;
    }

    public void setTitle(MessageSourceResolvable title) {
        this.title = title;
    }

    public int getMaxCharsDisplayed() {
        return maxCharsDisplayed;
    }

    public void setMaxCharsDisplayed(int maxCharsDisplayed) {
        this.maxCharsDisplayed = maxCharsDisplayed;
    }
}
