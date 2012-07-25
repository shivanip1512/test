package com.cannontech.common.i18n;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.SystemUserContext;

public class DisplayableEnumCellRenderer extends DefaultListCellRenderer {

    private YukonUserContextMessageSourceResolver resolver = null;

    public DisplayableEnumCellRenderer() {
        resolver = YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setText(resolver.getMessageSourceAccessor(new SystemUserContext()).getMessage(((DisplayableEnum) value).getFormatKey()));

        return this;
    }
}
