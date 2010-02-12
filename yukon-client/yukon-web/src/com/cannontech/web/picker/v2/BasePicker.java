package com.cannontech.web.picker.v2;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public abstract class BasePicker<T> implements Picker<T>, BeanNameAware {
    private String beanName = null;
    protected MessageSourceResolvable dialogTitle = null;

    public MessageSourceResolvable getDialogTitle() {
        if (dialogTitle == null) {
            setDialogTitle("yukon.web.modules.picker." + beanName + ".dialogTitle");
        }
        return dialogTitle;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setDialogTitle(String dialogTitleKey, Object...arguments) {
        this.dialogTitle =
            new YukonMessageSourceResolvable(dialogTitleKey, arguments);
    }

    public void setDialogTitle(MessageSourceResolvable dialogTitle) {
        this.dialogTitle = dialogTitle;
    }
}
