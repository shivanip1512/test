package com.cannontech.web.picker;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public abstract class BasePicker<T> implements Picker<T>, BeanNameAware {
    private String beanName = null;
    protected MessageSourceResolvable dialogTitle = null;

    @Override
    public MessageSourceResolvable getDialogTitle() {
        if (dialogTitle == null) {
            dialogTitle = new YukonMessageSourceResolvable("yukon.web.picker." + beanName + ".dialogTitle");
        }
        return dialogTitle;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setDialogTitle(MessageSourceResolvable dialogTitle) {
        this.dialogTitle = dialogTitle;
    }
}
