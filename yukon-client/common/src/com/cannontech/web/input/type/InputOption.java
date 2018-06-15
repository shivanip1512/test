package com.cannontech.web.input.type;

import java.util.Objects;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Class which represents an input option to be used with a drop down box
 */

public class InputOption implements InputOptionProvider {
    private String value = null;
    private boolean enabled = true;
    private MessageSourceResolvable messageSourceResolvable;

    public InputOption(){};

    public InputOption(String value, String text) {
        this.value = value;
        setText(text);
    }

    public InputOption(String value, boolean enabled) {
        this.value = value;
        setEnabled(enabled);
    }

    public InputOption(String value, String text, boolean enabled) {
        this(value, text);
        setEnabled(enabled);
    }

    public InputOption(Enum<?> enumEntry) {
        this.value = enumEntry.name();
        setEnum(enumEntry);
    }

    @Override
    public MessageSourceResolvable getMessage() {
        if (messageSourceResolvable == null) {
            return YukonMessageSourceResolvable.createDefaultWithoutCode(value);
        }
        return messageSourceResolvable;
    }

    public void setText(String text) {
        messageSourceResolvable = YukonMessageSourceResolvable.createDefaultWithoutCode(text);
    }

    public void setEnum(Enum<?> e) {
        if (e instanceof Displayable) {
            messageSourceResolvable = ((Displayable) e).getMessage();
        } else if (e instanceof DisplayableEnum) {
            messageSourceResolvable = new YukonMessageSourceResolvable(((DisplayableEnum) e).getFormatKey());
        } else {
            setText(e.name());
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof InputOption) {
            InputOption other = (InputOption) obj;
        
            return enabled == other.enabled 
                && Objects.equals(value, other.value)
                && Objects.equals(messageSourceResolvable, other.messageSourceResolvable);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + value + ":" + messageSourceResolvable.getDefaultMessage() + (enabled == false ? "]:disabled" : "]");
    }
}
