package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.LMGroupMCT;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupMCTEditorPanel extends DataInputPanel implements ActionListener, ItemListener, CaretListener {
    private JCheckBox ivjRelay1CheckBox = null;
    private JCheckBox ivjRelay2CheckBox = null;
    private JCheckBox ivjRelay3CheckBox = null;
    private javax.swing.JPanel ivjRelayUsagePanel = null;
    private JCheckBox ivjRelay4CheckBox = null;
    private JComboBox<String> ivjJComboBoxLevel = null;
    private javax.swing.JLabel ivjJLabelAddress = null;
    private javax.swing.JLabel ivjJLabelAddressLevel = null;
    private javax.swing.JLabel ivjJLabelMCTAddress = null;
    private javax.swing.JPanel ivjJPanelAddress = null;
    private javax.swing.JTextField ivjJTextFieldAddress = null;
    private JComboBox<LiteYukonPAObject> ivjJComboBoxMCTAddress = null;

    // just fill in some reasonable random values
    private static final LiteYukonPAObject NONE_PAO = new LiteYukonPAObject(CtiUtilities.NONE_ZERO_ID,
                                                                            CtiUtilities.STRING_NONE,
                                                                            PaoType.MCT370,
                                                                            CtiUtilities.STRING_NONE,
                                                                            CtiUtilities.STRING_NONE);

    public LMGroupMCTEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxMCTAddress()) {
                this.fireInputUpdate();
            }
            
            if (e.getSource() == getJComboBoxLevel()) {
                this.jComboBoxLevel_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        try {
            if (e.getSource() == getJTextFieldAddress()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }


    private JComboBox<String> getJComboBoxLevel() {
        if (ivjJComboBoxLevel == null) {
            try {
                ivjJComboBoxLevel = new JComboBox<String>();
                ivjJComboBoxLevel.setName("JComboBoxLevel");

                ivjJComboBoxLevel.addItem(IlmDefines.LEVEL_BRONZE);
                ivjJComboBoxLevel.addItem(IlmDefines.LEVEL_LEAD);
                ivjJComboBoxLevel.addItem(IlmDefines.LEVEL_MCT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxLevel;
    }

    private JComboBox<LiteYukonPAObject> getJComboBoxMCTAddress() {
        if (ivjJComboBoxMCTAddress == null) {
            try {
                ivjJComboBoxMCTAddress = new JComboBox<LiteYukonPAObject>();
                ivjJComboBoxMCTAddress.setName("JComboBoxMCTAddress");
                ivjJComboBoxMCTAddress.setToolTipText("Select the MCT device you want to use");
                ivjJComboBoxMCTAddress.setEnabled(false);

                // put some dummy that means zero id
                ivjJComboBoxMCTAddress.addItem(NONE_PAO);

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> mcts = cache.getAllDevices();
                    Collections.sort(mcts, LiteComparators.liteStringComparator);

                    for (LiteYukonPAObject dev : mcts) {
                        if (DeviceTypesFuncs.isMCT(dev.getPaoType().getDeviceTypeId())) {
                            ivjJComboBoxMCTAddress.addItem(dev);
                        }
                    }
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxMCTAddress;
    }

    private javax.swing.JLabel getJLabelAddress() {
        if (ivjJLabelAddress == null) {
            try {
                ivjJLabelAddress = new javax.swing.JLabel();
                ivjJLabelAddress.setName("JLabelAddress");
                ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelAddress.setText("Address:");
                ivjJLabelAddress.setEnabled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAddress;
    }

    private javax.swing.JLabel getJLabelAddressLevel() {
        if (ivjJLabelAddressLevel == null) {
            try {
                ivjJLabelAddressLevel = new javax.swing.JLabel();
                ivjJLabelAddressLevel.setName("JLabelAddressLevel");
                ivjJLabelAddressLevel.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelAddressLevel.setText("Address Level:");
                ivjJLabelAddressLevel.setEnabled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAddressLevel;
    }

    private javax.swing.JLabel getJLabelMCTAddress() {
        if (ivjJLabelMCTAddress == null) {
            try {
                ivjJLabelMCTAddress = new javax.swing.JLabel();
                ivjJLabelMCTAddress.setName("JLabelMCTAddress");
                ivjJLabelMCTAddress.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelMCTAddress.setText("MCT Address:");
                ivjJLabelMCTAddress.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelMCTAddress;
    }

    private javax.swing.JPanel getJPanelAddress() {
        if (ivjJPanelAddress == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
                ivjLocalBorder.setTitle("Address");
                ivjJPanelAddress = new javax.swing.JPanel();
                ivjJPanelAddress.setName("JPanelAddress");
                ivjJPanelAddress.setBorder(ivjLocalBorder);
                ivjJPanelAddress.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
                constraintsJLabelAddress.gridx = 1;
                constraintsJLabelAddress.gridy = 2;
                constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelAddress.ipadx = 13;
                constraintsJLabelAddress.ipady = -2;
                constraintsJLabelAddress.insets = new java.awt.Insets(6, 10, 6, 29);
                getJPanelAddress().add(getJLabelAddress(), constraintsJLabelAddress);

                java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
                constraintsJTextFieldAddress.gridx = 2;
                constraintsJTextFieldAddress.gridy = 2;
                constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldAddress.weightx = 1.0;
                constraintsJTextFieldAddress.ipadx = 192;
                constraintsJTextFieldAddress.insets = new java.awt.Insets(3, 2, 3, 23);
                getJPanelAddress().add(getJTextFieldAddress(), constraintsJTextFieldAddress);

                java.awt.GridBagConstraints constraintsJLabelAddressLevel = new java.awt.GridBagConstraints();
                constraintsJLabelAddressLevel.gridx = 1;
                constraintsJLabelAddressLevel.gridy = 1;
                constraintsJLabelAddressLevel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelAddressLevel.ipadx = 8;
                constraintsJLabelAddressLevel.insets = new java.awt.Insets(8, 10, 6, 2);
                getJPanelAddress().add(getJLabelAddressLevel(), constraintsJLabelAddressLevel);

                java.awt.GridBagConstraints constraintsJComboBoxLevel = new java.awt.GridBagConstraints();
                constraintsJComboBoxLevel.gridx = 2;
                constraintsJComboBoxLevel.gridy = 1;
                constraintsJComboBoxLevel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxLevel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJComboBoxLevel.weightx = 1.0;
                constraintsJComboBoxLevel.ipadx = 70;
                constraintsJComboBoxLevel.insets = new java.awt.Insets(5, 2, 2, 23);
                getJPanelAddress().add(getJComboBoxLevel(), constraintsJComboBoxLevel);

                java.awt.GridBagConstraints constraintsJLabelMCTAddress = new java.awt.GridBagConstraints();
                constraintsJLabelMCTAddress.gridx = 1;
                constraintsJLabelMCTAddress.gridy = 3;
                constraintsJLabelMCTAddress.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelMCTAddress.ipadx = 12;
                constraintsJLabelMCTAddress.insets = new java.awt.Insets(6, 10, 29, 2);
                getJPanelAddress().add(getJLabelMCTAddress(), constraintsJLabelMCTAddress);

                java.awt.GridBagConstraints constraintsJComboBoxMCTAddress = new java.awt.GridBagConstraints();
                constraintsJComboBoxMCTAddress.gridx = 2;
                constraintsJComboBoxMCTAddress.gridy = 3;
                constraintsJComboBoxMCTAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJComboBoxMCTAddress.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJComboBoxMCTAddress.weightx = 1.0;
                constraintsJComboBoxMCTAddress.ipadx = 70;
                constraintsJComboBoxMCTAddress.insets = new java.awt.Insets(3, 2, 25, 23);
                getJPanelAddress().add(getJComboBoxMCTAddress(), constraintsJComboBoxMCTAddress);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelAddress;
    }

    private javax.swing.JTextField getJTextFieldAddress() {
        if (ivjJTextFieldAddress == null) {
            try {
                ivjJTextFieldAddress = new javax.swing.JTextField();
                ivjJTextFieldAddress.setName("JTextFieldAddress");
                ivjJTextFieldAddress.setToolTipText("Address of the Group");
                ivjJTextFieldAddress.setText("0");
                ivjJTextFieldAddress.setEnabled(true);

                ivjJTextFieldAddress.setDocument(new LongRangeDocument(0, 9999999999l));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldAddress;
    }

    private JCheckBox getRelay1CheckBox() {
        if (ivjRelay1CheckBox == null) {
            try {
                ivjRelay1CheckBox = new JCheckBox();
                ivjRelay1CheckBox.setName("Relay1CheckBox");
                ivjRelay1CheckBox.setText("Relay 1");

                ivjRelay1CheckBox.setSelected(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRelay1CheckBox;
    }

    private JCheckBox getRelay2CheckBox() {
        if (ivjRelay2CheckBox == null) {
            try {
                ivjRelay2CheckBox = new JCheckBox();
                ivjRelay2CheckBox.setName("Relay2CheckBox");
                ivjRelay2CheckBox.setText("Relay 2");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRelay2CheckBox;
    }

    private JCheckBox getRelay3CheckBox() {
        if (ivjRelay3CheckBox == null) {
            try {
                ivjRelay3CheckBox = new JCheckBox();
                ivjRelay3CheckBox.setName("Relay3CheckBox");
                ivjRelay3CheckBox.setText("Relay 3");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRelay3CheckBox;
    }

    private JCheckBox getRelay4CheckBox() {
        if (ivjRelay4CheckBox == null) {
            try {
                ivjRelay4CheckBox = new JCheckBox();
                ivjRelay4CheckBox.setName("Relay4CheckBox");
                ivjRelay4CheckBox.setText("Relay 4");
                ivjRelay4CheckBox.setActionCommand("Relay 4");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRelay4CheckBox;
    }

    private javax.swing.JPanel getRelayUsagePanel() {
        if (ivjRelayUsagePanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
                ivjLocalBorder1.setTitle("Relay Usage");
                ivjRelayUsagePanel = new javax.swing.JPanel();
                ivjRelayUsagePanel.setName("RelayUsagePanel");
                ivjRelayUsagePanel.setBorder(ivjLocalBorder1);
                ivjRelayUsagePanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsRelay1CheckBox = new java.awt.GridBagConstraints();
                constraintsRelay1CheckBox.gridx = 1;
                constraintsRelay1CheckBox.gridy = 1;
                constraintsRelay1CheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRelay1CheckBox.insets = new java.awt.Insets(32, 37, 0, 37);
                getRelayUsagePanel().add(getRelay1CheckBox(), constraintsRelay1CheckBox);

                java.awt.GridBagConstraints constraintsRelay2CheckBox = new java.awt.GridBagConstraints();
                constraintsRelay2CheckBox.gridx = 1;
                constraintsRelay2CheckBox.gridy = 2;
                constraintsRelay2CheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRelay2CheckBox.insets = new java.awt.Insets(0, 37, 0, 37);
                getRelayUsagePanel().add(getRelay2CheckBox(), constraintsRelay2CheckBox);

                java.awt.GridBagConstraints constraintsRelay3CheckBox = new java.awt.GridBagConstraints();
                constraintsRelay3CheckBox.gridx = 1;
                constraintsRelay3CheckBox.gridy = 3;
                constraintsRelay3CheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRelay3CheckBox.insets = new java.awt.Insets(0, 37, 1, 37);
                getRelayUsagePanel().add(getRelay3CheckBox(), constraintsRelay3CheckBox);

                java.awt.GridBagConstraints constraintsRelay4CheckBox = new java.awt.GridBagConstraints();
                constraintsRelay4CheckBox.gridx = 1;
                constraintsRelay4CheckBox.gridy = 4;
                constraintsRelay4CheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRelay4CheckBox.insets = new java.awt.Insets(2, 37, 33, 37);
                getRelayUsagePanel().add(getRelay4CheckBox(), constraintsRelay4CheckBox);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRelayUsagePanel;
    }

    @Override
    public Object getValue(Object o) {
        LMGroupMCT group = null;

        if (o instanceof MultiDBPersistent) {
            group = (LMGroupMCT) MultiDBPersistent.getFirstObjectOfType(LMGroupMCT.class, (MultiDBPersistent) o);
        } else if (o instanceof SmartMultiDBPersistent) {
            group = (LMGroupMCT) ((SmartMultiDBPersistent) o).getOwnerDBPersistent();
        }

        if (o instanceof LMGroupMCT || group != null) {
            if (group == null) {
                group = (LMGroupMCT) o;
            }

            group.getLmGroupMCT().setLevel(getJComboBoxLevel().getSelectedItem().toString().substring(0, 1));

            if (getJTextFieldAddress().getText() != null && getJTextFieldAddress().getText().length() > 0) {
                group.getLmGroupMCT().setAddress(new Integer(getJTextFieldAddress().getText()));
            }

            StringBuffer relayUsage = new StringBuffer();
            if (getRelay1CheckBox().isSelected()) {
                relayUsage.append('1');
            }

            if (getRelay2CheckBox().isSelected()) {
                relayUsage.append('2');
            }

            if (getRelay3CheckBox().isSelected()) {
                relayUsage.append('3');
            }

            if (getRelay4CheckBox().isSelected()) {
                relayUsage.append('4');
            }

            group.getLmGroupMCT().setRelayUsage(relayUsage.toString());
            group.getLmGroupMCT().setMctDeviceID(new Integer(((LiteYukonPAObject) getJComboBoxMCTAddress().getSelectedItem()).getYukonID()));
        }
        return o;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getRelay1CheckBox().addItemListener(this);
        getRelay2CheckBox().addItemListener(this);
        getRelay3CheckBox().addItemListener(this);
        getRelay4CheckBox().addItemListener(this);
        getJTextFieldAddress().addCaretListener(this);
        getJComboBoxMCTAddress().addActionListener(this);
        getJComboBoxLevel().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMGroupVersacomEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(342, 371);

            java.awt.GridBagConstraints constraintsJPanelAddress = new java.awt.GridBagConstraints();
            constraintsJPanelAddress.gridx = 1;
            constraintsJPanelAddress.gridy = 1;
            constraintsJPanelAddress.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelAddress.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelAddress.weightx = 1.0;
            constraintsJPanelAddress.weighty = 1.0;
            constraintsJPanelAddress.ipadx = -10;
            constraintsJPanelAddress.ipady = -16;
            constraintsJPanelAddress.insets = new java.awt.Insets(4, 10, 8, 10);
            add(getJPanelAddress(), constraintsJPanelAddress);

            java.awt.GridBagConstraints constraintsRelayUsagePanel = new java.awt.GridBagConstraints();
            constraintsRelayUsagePanel.gridx = 1;
            constraintsRelayUsagePanel.gridy = 2;
            constraintsRelayUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsRelayUsagePanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRelayUsagePanel.weightx = 1.0;
            constraintsRelayUsagePanel.weighty = 1.0;
            constraintsRelayUsagePanel.ipadx = 172;
            constraintsRelayUsagePanel.ipady = -57;
            constraintsRelayUsagePanel.insets = new java.awt.Insets(8, 10, 103, 10);
            add(getRelayUsagePanel(), constraintsRelayUsagePanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getJTextFieldAddress().isEnabled())
            if (getJTextFieldAddress().getText() == null || getJTextFieldAddress().getText().length() <= 0) {
                setErrorString("A value for the Address text field must be filled in");
                return false;
            }

        if (getJComboBoxMCTAddress().isEnabled())
            if (getJComboBoxMCTAddress().getSelectedItem().equals(NONE_PAO)) {
                setErrorString("A real MCT must be selected in the MCT address combo box");
                return false;
            }

        return true;
    }

    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        
        try {
            if (e.getSource() == getRelay1CheckBox() ||
                    e.getSource() == getRelay2CheckBox() ||
                    e.getSource() == getRelay3CheckBox() ||
                    e.getSource() == getRelay4CheckBox()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void jComboBoxLevel_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        boolean isMCTAddy = getJComboBoxLevel().getSelectedItem().equals(IlmDefines.LEVEL_MCT);

        getJLabelAddress().setEnabled(!isMCTAddy);
        getJTextFieldAddress().setEnabled(!isMCTAddy);

        getJLabelMCTAddress().setEnabled(isMCTAddy);
        getJComboBoxMCTAddress().setEnabled(isMCTAddy);

        fireInputUpdate();
    }

    public void setRelay(String r) {

        if (r.charAt(0) == '1' && !getRelay1CheckBox().isSelected()) {
            getRelay1CheckBox().setSelected(true);
        }

        if (r.charAt(1) == '2' && !getRelay2CheckBox().isSelected()) {
            getRelay2CheckBox().setSelected(true);
        }

        if (r.charAt(2) == '3' && !getRelay3CheckBox().isSelected()) {
            getRelay3CheckBox().setSelected(true);
        }

        if (r.charAt(3) == '4' && !getRelay4CheckBox().isSelected()) {
            getRelay4CheckBox().setSelected(true);
        }
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof LMGroupMCT) {
            LMGroupMCT group = (LMGroupMCT) o;

            for (int i = 0; i < getJComboBoxLevel().getItemCount(); i++)
                if (group.getLmGroupMCT().getLevel().substring(0, 1).equals(getJComboBoxLevel().getItemAt(i).toString().substring(0, 1))) {
                    getJComboBoxLevel().setSelectedIndex(i);
                    break;
                }

            getJTextFieldAddress().setText(group.getLmGroupMCT().getAddress().toString());

            String relayUsage = group.getLmGroupMCT().getRelayUsage();
            getRelay1CheckBox().setSelected((relayUsage.indexOf('1') >= 0));
            getRelay2CheckBox().setSelected((relayUsage.indexOf('2') >= 0));
            getRelay3CheckBox().setSelected((relayUsage.indexOf('3') >= 0));
            getRelay4CheckBox().setSelected((relayUsage.indexOf('4') >= 0));

            for (int i = 0; i < getJComboBoxMCTAddress().getItemCount(); i++) {
                LiteYukonPAObject mct = getJComboBoxMCTAddress().getItemAt(i);

                if (mct.getYukonID() == group.getLmGroupMCT().getMctDeviceID().intValue()) {
                    getJComboBoxMCTAddress().setSelectedIndex(i);
                    break;
                }
            }

        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJPanelAddress().requestFocus();
            }
        });
    }
}