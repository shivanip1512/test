package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ClientRights;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.spring.YukonSpringHook;

public class SwitchTypePanel extends DataInputPanel {
    private javax.swing.JLabel ivjSelectLabel = null;
    private javax.swing.JList<PaoType> ivjSwitchList = null;
    private javax.swing.JScrollPane ivjSwitchListScrollPane = null;

    // hex value representing some privileges of the user on this machine
    public static final long SHOW_PROTOCOL = 
            Long.parseLong(ClientSession.getInstance().getRolePropertyValue(YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV), 16);

    private static List<PaoType> GROUP_LIST = new LinkedList<PaoType>(Arrays.asList(PaoType.LM_GROUP_DIGI_SEP,
        PaoType.LM_GROUP_ECOBEE, PaoType.LM_GROUP_EMETCON, PaoType.LM_GROUP_EXPRESSCOMM, PaoType.LM_GROUP_GOLAY,
        PaoType.LM_GROUP_MCT, PaoType.LM_GROUP_POINT, PaoType.LM_GROUP_RFN_EXPRESSCOMM, PaoType.LM_GROUP_RIPPLE,
        PaoType.LM_GROUP_VERSACOM));

    private static List<PaoType> GROUP_LIST_SA = new LinkedList<PaoType>(Arrays.asList(PaoType.LM_GROUP_DIGI_SEP,
        PaoType.LM_GROUP_ECOBEE, PaoType.LM_GROUP_EMETCON, PaoType.LM_GROUP_EXPRESSCOMM, PaoType.LM_GROUP_GOLAY,
        PaoType.LM_GROUP_MCT, PaoType.LM_GROUP_POINT, PaoType.LM_GROUP_RFN_EXPRESSCOMM, PaoType.LM_GROUP_RIPPLE,
        PaoType.LM_GROUP_SA205, PaoType.LM_GROUP_SA305, PaoType.LM_GROUP_SADIGITAL, PaoType.LM_GROUP_VERSACOM));

    public SwitchTypePanel() {
        super();
        initialize();
    }

    private void initSwitchList() {
        try {
            getSwitchList().setListData(this.getGroupList());
            getSwitchList().setSelectionInterval(0, 0);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private PaoType[] getGroupList() {
        ConfigurationSource masterConfigSource =
            YukonSpringHook.getBean("configurationSource", ConfigurationSource.class);
        boolean honeywellEnabled = masterConfigSource.getBoolean(MasterConfigBoolean.HONEYWELL_SUPPORT_ENABLED, false);
        if (honeywellEnabled && !GROUP_LIST.contains(PaoType.LM_GROUP_HONEYWELL)) {
            GROUP_LIST.add(PaoType.LM_GROUP_HONEYWELL);
            Collections.sort(GROUP_LIST);
            GROUP_LIST_SA.add(PaoType.LM_GROUP_HONEYWELL);
            Collections.sort(GROUP_LIST);
        }
        // normally we cannot show SA protocol groups, this checks the
        // specific property.
        if ((SHOW_PROTOCOL & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0) {
            return SwitchTypePanel.GROUP_LIST_SA.toArray(new PaoType[GROUP_LIST_SA.size()]);
        }
        return SwitchTypePanel.GROUP_LIST.toArray(new PaoType[GROUP_LIST.size()]);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private javax.swing.JLabel getSelectLabel() {
        if (ivjSelectLabel == null) {
            try {
                ivjSelectLabel = new javax.swing.JLabel();
                ivjSelectLabel.setName("SelectLabel");
                ivjSelectLabel.setText("Select the type of switch:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSelectLabel;
    }

    private javax.swing.JList<PaoType> getSwitchList() {
        if (ivjSwitchList == null) {
            try {
                ivjSwitchList = new javax.swing.JList<PaoType>();
                ivjSwitchList.setName("SwitchList");
                ivjSwitchList.setBounds(0, 0, 300, 122);
                ivjSwitchList.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                ivjSwitchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                ivjSwitchList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList list,
                            Object value, int index, boolean isSelected,
                            boolean cellHasFocus) {

                        JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                                                   value,
                                                                                   index,
                                                                                   isSelected,
                                                                                   cellHasFocus);
                        PaoType paoType = (PaoType) value;
                        label.setText(paoType.getDbString());
                        return label;
                    }
                });

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSwitchList;
    }

    private javax.swing.JScrollPane getSwitchListScrollPane() {
        if (ivjSwitchListScrollPane == null) {
            try {
                ivjSwitchListScrollPane = new javax.swing.JScrollPane();
                ivjSwitchListScrollPane.setName("SwitchListScrollPane");
                ivjSwitchListScrollPane.setPreferredSize(new java.awt.Dimension(300, 153));
                ivjSwitchListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                getSwitchListScrollPane().setViewportView(getSwitchList());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSwitchListScrollPane;
    }

    public PaoType getTypeOfSwitchSelected() {

        // normally we cannot show SA protocol groups, this checks the
        // specific property.
        return getSwitchList().getSelectedValue();
    }

    @Override
    public Object getValue(Object o) {
        return LMFactory.createLoadManagement(getTypeOfSwitchSelected());
    }

    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(),
                                                   exception);
        ;
    }

    private void initialize() {
        try {
            setName("SwitchTypePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
            constraintsSelectLabel.gridx = 1;
            constraintsSelectLabel.gridy = 1;
            constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSelectLabel.ipady = 1;
            constraintsSelectLabel.insets = new java.awt.Insets(13, 25, 2, 183);
            add(getSelectLabel(), constraintsSelectLabel);

            java.awt.GridBagConstraints constraintsSwitchListScrollPane = new java.awt.GridBagConstraints();
            constraintsSwitchListScrollPane.gridx = 1;
            constraintsSwitchListScrollPane.gridy = 2;
            constraintsSwitchListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsSwitchListScrollPane.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSwitchListScrollPane.weightx = 1.0;
            constraintsSwitchListScrollPane.weighty = 1.0;
            constraintsSwitchListScrollPane.ipadx = 278;
            constraintsSwitchListScrollPane.ipady = 131;
            constraintsSwitchListScrollPane.insets = new java.awt.Insets(2, 25, 15, 25);
            add(getSwitchListScrollPane(), constraintsSwitchListScrollPane);
            
            initSwitchList();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void setValue(Object o) {
        // nothing to set
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts
        // in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getSwitchListScrollPane().requestFocus();
            }
        });
    }

}