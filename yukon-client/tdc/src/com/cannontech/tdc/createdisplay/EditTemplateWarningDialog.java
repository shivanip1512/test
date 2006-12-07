package com.cannontech.tdc.createdisplay;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.OkCancelPanel;

public class EditTemplateWarningDialog extends JDialog {

    private OkCancelPanel okCancelPanel = null;
    private JPanel contentPane = null;
    private JLabel message = null;
    PrivateEventHandler eventHandler = new PrivateEventHandler();
    public static String WARNING = "Changes to templates affect their associated displays";
    private JDialog configDialog = null;

    class PrivateEventHandler implements
            com.cannontech.common.gui.util.OkCancelPanelListener,
            java.awt.event.ActionListener {

        public void actionPerformed(ActionEvent e) {

        }

        public void JButtonCancelAction_actionPerformed(EventObject newEvent) {
            dispose();
        }


        public void JButtonOkAction_actionPerformed(EventObject newEvent) {
            dispose();
            configDialog.setVisible(true);
        };
    };

    public EditTemplateWarningDialog() {

    }

    public EditTemplateWarningDialog(Frame o, JDialog child) {
        super(o);
        configDialog = child;
        configThis();
        getMessage().setText(WARNING);
        setContentPane(getJDialogContentPane());
        initConnections();
    }

    private void initConnections() {
        getOkCancelPanel().addOkCancelPanelListener(eventHandler);
    }

    private void configThis() {
        setName("Warning");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(470, 100);
        setModal(true);
        setTitle("Warning");
    }

    private Container getJDialogContentPane() {
        if (contentPane == null) {
            contentPane = new javax.swing.JPanel();
            contentPane.setName("JDialogContentPane");
            contentPane.setPreferredSize(new java.awt.Dimension(470, 100));
            contentPane.setLayout(new java.awt.GridBagLayout());
            contentPane.setMaximumSize(new java.awt.Dimension(470, 100));
            contentPane.setMinimumSize(new java.awt.Dimension(470, 100));

            GridBagConstraints configMessage = configMessage();
            getJDialogContentPane().add(getMessage(), configMessage);

            GridBagConstraints configOkCancel = configOkCancelPanel();
            getJDialogContentPane().add(getOkCancelPanel(), configOkCancel);

        }
        return contentPane;
    }

    private GridBagConstraints configMessage() {
        GridBagConstraints configMessage = new java.awt.GridBagConstraints();
        configMessage.gridx = 0;
        configMessage.gridy = 5;
        configMessage.fill = java.awt.GridBagConstraints.CENTER;
        configMessage.anchor = java.awt.GridBagConstraints.WEST;
        configMessage.weightx = 0.0;
        configMessage.weighty = 0.0;
        configMessage.insets = new java.awt.Insets(0, 0, 0, 0);

        return configMessage;

    }

    private java.awt.GridBagConstraints configOkCancelPanel() {
        GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
        constraintsOkCancelPanel.gridx = 0;
        constraintsOkCancelPanel.gridy = 7;
        constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.CENTER;
        constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsOkCancelPanel.weightx = 0.0;
        constraintsOkCancelPanel.weighty = 0.0;
        constraintsOkCancelPanel.insets = new java.awt.Insets(0, 0, 0, 0);
        return constraintsOkCancelPanel;
    }

    public OkCancelPanel getOkCancelPanel() {

        if (okCancelPanel == null) {
            try {
                okCancelPanel = new OkCancelPanel();
                okCancelPanel.setName("OkCancelPanel");
            } catch (Throwable e) {
                CTILogger.error(e);
            }
        }
        return okCancelPanel;
    }

    public JLabel getMessage() {
        if (message == null) {
            try {
                message = new JLabel();
                message.setName("WarningMessage");
            } catch (Throwable e) {
                CTILogger.error(e);
            }
        }
        return message;
    }
    
}
