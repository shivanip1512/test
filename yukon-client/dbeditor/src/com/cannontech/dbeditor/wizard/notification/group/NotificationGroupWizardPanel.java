package com.cannontech.dbeditor.wizard.notification.group;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.dbeditor.editor.notification.group.GroupNotificationEditorPanel;

public class NotificationGroupWizardPanel extends WizardPanel {
    private GroupNotificationEditorPanel groupNotificationEditorPanel;
    private GroupPagerSetup groupPagerSetup;

    public NotificationGroupWizardPanel() {
        super();
    }

    @Override
    public Dimension getActualSize() {
        setPreferredSize(new Dimension(410, 480));

        return getPreferredSize();
    }

    public GroupNotificationEditorPanel getGroupNotificationEditorPanel() {
        if (groupNotificationEditorPanel == null) {
            groupNotificationEditorPanel = new GroupNotificationEditorPanel();
        }

        return groupNotificationEditorPanel;
    }

    public GroupPagerSetup getGroupPagerSetup() {
        if (groupPagerSetup == null) {
            groupPagerSetup = new GroupPagerSetup();
        }

        return groupPagerSetup;
    }

    @Override
    protected String getHeaderText() {
        return "Notification Group Setup";
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            return getGroupNotificationEditorPanel();
        } else if (currentInputPanel == getGroupNotificationEditorPanel()) {
            return getGroupPagerSetup();
        } else {
            throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
        }
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return (currentPanel == getGroupNotificationEditorPanel());
    }
}
