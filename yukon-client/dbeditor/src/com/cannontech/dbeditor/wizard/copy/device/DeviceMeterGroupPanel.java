package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.spring.YukonSpringHook;
 
public class DeviceMeterGroupPanel extends com.cannontech.common.gui.util.DataInputPanel {
    
    private javax.swing.JLabel ivjAreaCodeGroupLabel = null;
    private javax.swing.JLabel ivjCycleGroupLabel = null;
    private javax.swing.JComboBox alternateGroupComboBox = null;
    private javax.swing.JComboBox ivjCycleGroupComboBox = null;
    private javax.swing.JComboBox ivjJComboBoxBillingGroup = null;
    private JComboBox customGroup1ComboBox = null;
    private JComboBox customGroup2ComboBox = null;
    private JComboBox customGroup3ComboBox = null;
    private javax.swing.JLabel ivjJLabelBillingGroup = null;
    private JLabel customGroup1Label = null;
    private JLabel customGroup2Label = null;
    private JLabel customGroup3Label = null;
    
    FixedDeviceGroupingHack hacker = (FixedDeviceGroupingHack) YukonSpringHook.getBean("fixedDeviceGroupingHack"); 
    
    public DeviceMeterGroupPanel() {
        super();
        initialize();
    }
    
    private void initialize() {
        try {
            setName("DeviceMeterGroupPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);
            
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
            ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
            ivjLocalBorder1.setTitle("Data Collection");
            setBorder(ivjLocalBorder1);

            java.awt.GridBagConstraints constraintsCycleGroupLabel = new java.awt.GridBagConstraints();
            constraintsCycleGroupLabel.gridx = 1; constraintsCycleGroupLabel.gridy = 2;
            constraintsCycleGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCycleGroupLabel.ipadx = 9;
            constraintsCycleGroupLabel.ipady = 2;
            constraintsCycleGroupLabel.insets = new java.awt.Insets(7, 15, 6, 1);
            add(getCycleGroupLabel(), constraintsCycleGroupLabel);

            java.awt.GridBagConstraints constraintsAreaCodeGroupLabel = new java.awt.GridBagConstraints();
            constraintsAreaCodeGroupLabel.gridx = 1; constraintsAreaCodeGroupLabel.gridy = 3;
            constraintsAreaCodeGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsAreaCodeGroupLabel.ipadx = 35;
            constraintsAreaCodeGroupLabel.ipady = 2;
            constraintsAreaCodeGroupLabel.insets = new java.awt.Insets(7, 15, 6, 1);
            add(getAreaCodeGroupLabel(), constraintsAreaCodeGroupLabel);

            java.awt.GridBagConstraints constraintsCycleGroupComboBox = new java.awt.GridBagConstraints();
            constraintsCycleGroupComboBox.gridx = 2; constraintsCycleGroupComboBox.gridy = 2;
            constraintsCycleGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCycleGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCycleGroupComboBox.weightx = 1.0;
            constraintsCycleGroupComboBox.insets = new java.awt.Insets(3, 0, 3, 22);
            add(getCycleGroupComboBox(), constraintsCycleGroupComboBox);

            java.awt.GridBagConstraints constraintsAreaCodeGroupComboBox = new java.awt.GridBagConstraints();
            constraintsAreaCodeGroupComboBox.gridx = 2; constraintsAreaCodeGroupComboBox.gridy = 3;
            constraintsAreaCodeGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsAreaCodeGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsAreaCodeGroupComboBox.weightx = 1.0;
            constraintsAreaCodeGroupComboBox.insets = new java.awt.Insets(3, 0, 3, 22);
            add(getAlternateGroupComboBox(), constraintsAreaCodeGroupComboBox);

            java.awt.GridBagConstraints constraintsJLabelBillingGroup = new java.awt.GridBagConstraints();
            constraintsJLabelBillingGroup.gridx = 1; constraintsJLabelBillingGroup.gridy = 4;
            constraintsJLabelBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelBillingGroup.ipadx = 9;
            constraintsJLabelBillingGroup.ipady = 2;
            constraintsJLabelBillingGroup.insets = new java.awt.Insets(7, 16, 3, 0);
            add(getJLabelBillingGroup(), constraintsJLabelBillingGroup);

            java.awt.GridBagConstraints constraintsJComboBoxBillingGroup = new java.awt.GridBagConstraints();
            constraintsJComboBoxBillingGroup.gridx = 2; constraintsJComboBoxBillingGroup.gridy = 4;
            constraintsJComboBoxBillingGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxBillingGroup.weightx = 1.0;
            constraintsJComboBoxBillingGroup.insets = new java.awt.Insets(3, 1, 3, 21);
            add(getJComboBoxBillingGroup(), constraintsJComboBoxBillingGroup);
            
            java.awt.GridBagConstraints constraintsCustomGroup1Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup1Label.gridx = 1; constraintsCustomGroup1Label.gridy = 5;
            constraintsCustomGroup1Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup1Label.ipadx = 9;
            constraintsCustomGroup1Label.ipady = 2;
            constraintsCustomGroup1Label.insets = new java.awt.Insets(7, 16, 3, 0);
            add(getCustomGroup1Label(), constraintsCustomGroup1Label);

            java.awt.GridBagConstraints constraintsCustomGroup1ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup1ComboBox.gridx = 2; constraintsCustomGroup1ComboBox.gridy = 5;
            constraintsCustomGroup1ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup1ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup1ComboBox.weightx = 1.0;
            constraintsCustomGroup1ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            add(getCustomGroup1ComboBox(), constraintsCustomGroup1ComboBox);
            
            java.awt.GridBagConstraints constraintsCustomGroup2Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup2Label.gridx = 1; constraintsCustomGroup2Label.gridy = 6;
            constraintsCustomGroup2Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup2Label.ipadx = 9;
            constraintsCustomGroup2Label.ipady = 2;
            constraintsCustomGroup2Label.insets = new java.awt.Insets(7, 16, 3, 0);
            add(getCustomGroup2Label(), constraintsCustomGroup2Label);

            java.awt.GridBagConstraints constraintsCustomGroup2ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup2ComboBox.gridx = 2; constraintsCustomGroup2ComboBox.gridy = 6;
            constraintsCustomGroup2ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup2ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup2ComboBox.weightx = 1.0;
            constraintsCustomGroup2ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            add(getCustomGroup2ComboBox(), constraintsCustomGroup2ComboBox);
            
            java.awt.GridBagConstraints constraintsCustomGroup3Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup3Label.gridx = 1; constraintsCustomGroup3Label.gridy = 7;
            constraintsCustomGroup3Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup3Label.ipadx = 9;
            constraintsCustomGroup3Label.ipady = 2;
            constraintsCustomGroup3Label.insets = new java.awt.Insets(7, 16, 3, 0);
            add(getCustomGroup3Label(), constraintsCustomGroup3Label);

            java.awt.GridBagConstraints constraintsCustomGroup3ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup3ComboBox.gridx = 2; constraintsCustomGroup3ComboBox.gridy = 7;
            constraintsCustomGroup3ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup3ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup3ComboBox.weightx = 1.0;
            constraintsCustomGroup3ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            add(getCustomGroup3ComboBox(), constraintsCustomGroup3ComboBox);

            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    private JLabel getCustomGroup1Label() {
        if (customGroup1Label == null) {
            try {
                customGroup1Label = new javax.swing.JLabel();
                customGroup1Label.setName("CustomGroup1Label");
                customGroup1Label.setText("Custom Group 1:");
                customGroup1Label.setMaximumSize(new Dimension(114, 16));
                customGroup1Label.setPreferredSize(new Dimension(114, 16));
                customGroup1Label.setFont(new Font("dialog", 0, 14));
                customGroup1Label.setMinimumSize(new Dimension(114, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup1Label;
    }

    private JLabel getCustomGroup2Label() {
        if (customGroup2Label == null) {
            try {
                customGroup2Label = new javax.swing.JLabel();
                customGroup2Label.setName("CustomGroup2Label");
                customGroup2Label.setText("Custom Group 2:");
                customGroup2Label.setMaximumSize(new Dimension(114, 16));
                customGroup2Label.setPreferredSize(new Dimension(114, 16));
                customGroup2Label.setFont(new Font("dialog", 0, 14));
                customGroup2Label.setMinimumSize(new Dimension(114, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup2Label;
    }

    private JLabel getCustomGroup3Label() {
        if (customGroup3Label == null) {
            try {
                customGroup3Label = new javax.swing.JLabel();
                customGroup3Label.setName("CustomGroup3Label");
                customGroup3Label.setText("Custom Group 3:");
                customGroup3Label.setMaximumSize(new Dimension(114, 16));
                customGroup3Label.setPreferredSize(new Dimension(114, 16));
                customGroup3Label.setFont(new Font("dialog", 0, 14));
                customGroup3Label.setMinimumSize(new Dimension(114, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup3Label;
    }
    
    /**
     * Return the CycleGroupLabel property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getCycleGroupLabel() {
        if (ivjCycleGroupLabel == null) {
            try {
                ivjCycleGroupLabel = new javax.swing.JLabel();
                ivjCycleGroupLabel.setName("CycleGroupLabel");
                ivjCycleGroupLabel.setText("Data Collection Group:");
                ivjCycleGroupLabel.setMaximumSize(new java.awt.Dimension(200, 16));
                ivjCycleGroupLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjCycleGroupLabel.setPreferredSize(new java.awt.Dimension(140, 16));
                ivjCycleGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjCycleGroupLabel.setMinimumSize(new java.awt.Dimension(140, 16));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCycleGroupLabel;
    }
    
    /**
     * Return the CycleGroupComboBox property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getCycleGroupComboBox() {
        if (ivjCycleGroupComboBox == null) {
            try {
                ivjCycleGroupComboBox = new javax.swing.JComboBox();
                ivjCycleGroupComboBox.setName("CycleGroupComboBox");
                ivjCycleGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
                ivjCycleGroupComboBox.setEditable(true);
                ivjCycleGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
                List<String> availableCollectionGroups = hacker.getGroups(FixedDeviceGroups.COLLECTIONGROUP);
                Iterator<String> iter = availableCollectionGroups.iterator();
                while(iter.hasNext()) {
                    ivjCycleGroupComboBox.addItem(iter.next());
                }
             
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCycleGroupComboBox;
    }
    
    /**
     * Return the AreaCodeGroupLabel property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getAreaCodeGroupLabel() {
        if (ivjAreaCodeGroupLabel == null) {
            try {
                ivjAreaCodeGroupLabel = new javax.swing.JLabel();
                ivjAreaCodeGroupLabel.setName("AreaCodeGroupLabel");
                ivjAreaCodeGroupLabel.setText("Alternate Group:");
                ivjAreaCodeGroupLabel.setMaximumSize(new java.awt.Dimension(114, 16));
                ivjAreaCodeGroupLabel.setPreferredSize(new java.awt.Dimension(114, 16));
                ivjAreaCodeGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAreaCodeGroupLabel.setMinimumSize(new java.awt.Dimension(114, 16));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAreaCodeGroupLabel;
    }
    
    /**
     * Return the AreaCodeGroupComboBox property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getAlternateGroupComboBox() {
        if (alternateGroupComboBox == null) {
            try {
                alternateGroupComboBox = new javax.swing.JComboBox();
                alternateGroupComboBox.setName("AreaCodeGroupComboBox");
                alternateGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
                alternateGroupComboBox.setEditable(true);
                alternateGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
                List<String> availableAlternateGroups = hacker.getGroups(FixedDeviceGroups.TESTCOLLECTIONGROUP);
                Iterator<String> iter = availableAlternateGroups.iterator();
                while(iter.hasNext()) {
                    alternateGroupComboBox.addItem(iter.next());
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return alternateGroupComboBox;
    }
    
    private JComboBox getCustomGroup1ComboBox() {
        if (customGroup1ComboBox == null) {
            try {
                customGroup1ComboBox = new JComboBox();
                customGroup1ComboBox.setName("CustomGroup1ComboBox");
                customGroup1ComboBox.setPreferredSize(new Dimension(200, 25));
                customGroup1ComboBox.setEditable(true);
                customGroup1ComboBox.setMinimumSize(new Dimension(200, 25));

                List<String> availableCustom1Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM1GROUP);
                Iterator<String> iter = availableCustom1Groups.iterator();
                while(iter.hasNext()) {
                    customGroup1ComboBox.addItem(iter.next());
                }

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup1ComboBox;
    }

    private JComboBox getCustomGroup2ComboBox() {
        if (customGroup2ComboBox == null) {
            try {
                customGroup2ComboBox = new JComboBox();
                customGroup2ComboBox.setName("CustomGroup2ComboBox");
                customGroup2ComboBox.setPreferredSize(new Dimension(200, 25));
                customGroup2ComboBox.setEditable(true);
                customGroup2ComboBox.setMinimumSize(new Dimension(200, 25));
                List<String> availableCustom2Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM2GROUP);
                Iterator<String> iter = availableCustom2Groups.iterator();
                while(iter.hasNext()) {
                    customGroup2ComboBox.addItem(iter.next());
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup2ComboBox;
    }

    private JComboBox getCustomGroup3ComboBox() {
        if (customGroup3ComboBox == null) {
            try {
                customGroup3ComboBox = new JComboBox();
                customGroup3ComboBox.setName("CustomGroup3ComboBox");
                customGroup3ComboBox.setPreferredSize(new Dimension(200, 25));
                customGroup3ComboBox.setEditable(true);
                customGroup3ComboBox.setMinimumSize(new Dimension(200, 25));
                List<String> availableCustom3Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM3GROUP);
                Iterator<String> iter = availableCustom3Groups.iterator();
                while(iter.hasNext()) {
                    customGroup3ComboBox.addItem(iter.next());
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return customGroup3ComboBox;
    }
    
    /**
     * Return the JLabelBillingGroup property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelBillingGroup() {
        if (ivjJLabelBillingGroup == null) {
            try {
                ivjJLabelBillingGroup = new javax.swing.JLabel();
                ivjJLabelBillingGroup.setName("JLabelBillingGroup");
                ivjJLabelBillingGroup.setText("Billing Group:");
                ivjJLabelBillingGroup.setMaximumSize(new java.awt.Dimension(200, 16));
                ivjJLabelBillingGroup.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelBillingGroup.setPreferredSize(new java.awt.Dimension(140, 16));
                ivjJLabelBillingGroup.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelBillingGroup.setMinimumSize(new java.awt.Dimension(140, 16));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelBillingGroup;
    }
    
    /**
     * Return the JComboBoxBillingGroup property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getJComboBoxBillingGroup() {
        if (ivjJComboBoxBillingGroup == null) {
            try {
                ivjJComboBoxBillingGroup = new javax.swing.JComboBox();
                ivjJComboBoxBillingGroup.setName("JComboBoxBillingGroup");
                ivjJComboBoxBillingGroup.setPreferredSize(new java.awt.Dimension(200, 25));
                ivjJComboBoxBillingGroup.setEditable(true);
                ivjJComboBoxBillingGroup.setMinimumSize(new java.awt.Dimension(200, 25));
                List<String> availableBillingsGroups = hacker.getGroups(FixedDeviceGroups.BILLINGGROUP);
                Iterator<String> iter = availableBillingsGroups.iterator();
                while(iter.hasNext()) {
                    ivjJComboBoxBillingGroup.addItem(iter.next());
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxBillingGroup;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
    
    
    @SuppressWarnings("deprecation")
    public Object getValue(Object val) {
        
        LiteYukonPAObject liteYuk = null;
        
        if (val instanceof MultiDBPersistent)
        {
            if ((DBPersistent) ((MultiDBPersistent) val).getDBPersistentVector().get(0) instanceof MCTBase)
            {
                liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) ((DBPersistent) ((MultiDBPersistent) val).getDBPersistentVector().get(0))).getPAObjectID());
            }
        }
        else
        {
            liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) val).getPAObjectID());
        }

        String cycleGroup = getCycleGroupComboBox().getSelectedItem().toString();
        String alternateGroup = getAlternateGroupComboBox().getSelectedItem().toString();
        String billingGroup = getJComboBoxBillingGroup().getSelectedItem().toString();
        String customGroup1 = getCustomGroup1ComboBox().getSelectedItem().toString();
        String customGroup2 = getCustomGroup2ComboBox().getSelectedItem().toString();
        String customGroup3 = getCustomGroup3ComboBox().getSelectedItem().toString();
        
        YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
        hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, yukonDevice, billingGroup);
        hacker.setGroup(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice, cycleGroup);
        hacker.setGroup(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice, alternateGroup);
        hacker.setGroup(FixedDeviceGroups.CUSTOM1GROUP, yukonDevice, customGroup1);
        hacker.setGroup(FixedDeviceGroups.CUSTOM2GROUP, yukonDevice, customGroup2);
        hacker.setGroup(FixedDeviceGroups.CUSTOM3GROUP, yukonDevice, customGroup3);
        return val;
        
    }

    
    @SuppressWarnings("deprecation")
    public void setValue(Object o) 
    {
//        DeviceMeterGroup dmg = null;
        LiteYukonPAObject liteYuk = null;
        
        if (o instanceof MultiDBPersistent)
        {
            if ((DBPersistent) ((MultiDBPersistent) o).getDBPersistentVector().get(0) instanceof MCTBase)
            {
                
                liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) ((DBPersistent) ((MultiDBPersistent) o).getDBPersistentVector().get(0))).getPAObjectID());
            }
        }
        else
        {
            liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) o).getPAObjectID());
        }
        
        YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
        String billingGroup = hacker.getGroupForDevice(FixedDeviceGroups.BILLINGGROUP, yukonDevice);
        String alternateGroup = hacker.getGroupForDevice(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice);
        String collectionGroup = hacker.getGroupForDevice(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice);
        String customGroup1 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM1GROUP, yukonDevice);
        String customGroup2 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM2GROUP, yukonDevice);
        String customGroup3 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM3GROUP, yukonDevice);
        getCycleGroupComboBox().setSelectedItem( collectionGroup );
        getAlternateGroupComboBox().setSelectedItem( alternateGroup );
        getJComboBoxBillingGroup().setSelectedItem( billingGroup );
        getCustomGroup1ComboBox().setSelectedItem(customGroup1);
        getCustomGroup2ComboBox().setSelectedItem(customGroup2);
        getCustomGroup3ComboBox().setSelectedItem(customGroup3);
    }
    
    public void setFirstFocus() 
    {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            public void run() 
                { 
                getCycleGroupComboBox().requestFocus(); 
            } 
        });    
    }

}