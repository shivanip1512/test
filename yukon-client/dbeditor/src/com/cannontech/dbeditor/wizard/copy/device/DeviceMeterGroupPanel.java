package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.device.DeviceGroupMember;
import com.cannontech.spring.YukonSpringHook;
 
@SuppressWarnings("deprecation")
public class DeviceMeterGroupPanel extends DataInputPanel {
    
    private javax.swing.JLabel ivjAreaCodeGroupLabel = null;
    private javax.swing.JLabel ivjCycleGroupLabel = null;
    private JTextArea customGroupsDirectionsLabel = null;
    private javax.swing.JComboBox<String> alternateGroupComboBox = null;
    private javax.swing.JComboBox<String> ivjCycleGroupComboBox = null;
    private javax.swing.JComboBox<String> ivjJComboBoxBillingGroup = null;
    private javax.swing.JLabel ivjJLabelBillingGroup = null;
    
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
            
            java.awt.GridBagConstraints constraintsCustomGroupsDirectionsLabel = new java.awt.GridBagConstraints();
            constraintsCustomGroupsDirectionsLabel.gridx = 1; constraintsCustomGroupsDirectionsLabel.gridy = 5;
            constraintsCustomGroupsDirectionsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroupsDirectionsLabel.anchor = java.awt.GridBagConstraints.CENTER;
            constraintsCustomGroupsDirectionsLabel.gridwidth  = 2;
            constraintsCustomGroupsDirectionsLabel.ipadx = 2;
            constraintsCustomGroupsDirectionsLabel.ipady = 2;
            constraintsCustomGroupsDirectionsLabel.insets = new java.awt.Insets(7, 10, 3, 10);
            add(getCustomGroupsDirectionsLabel(), constraintsCustomGroupsDirectionsLabel);

            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
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
    private javax.swing.JComboBox<String> getCycleGroupComboBox() {
        if (ivjCycleGroupComboBox == null) {
            try {
                ivjCycleGroupComboBox = new javax.swing.JComboBox<String>();
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
    private javax.swing.JComboBox<String> getAlternateGroupComboBox() {
        if (alternateGroupComboBox == null) {
            try {
                alternateGroupComboBox = new javax.swing.JComboBox<String>();
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
    private javax.swing.JComboBox<String> getJComboBoxBillingGroup() {
        if (ivjJComboBoxBillingGroup == null) {
            try {
                ivjJComboBoxBillingGroup = new javax.swing.JComboBox<String>();
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
    
    private JTextArea getCustomGroupsDirectionsLabel() {
        if (customGroupsDirectionsLabel == null) {
          try {
              customGroupsDirectionsLabel = new javax.swing.JTextArea();
              customGroupsDirectionsLabel.setName("CustomGroupsDirections");
              customGroupsDirectionsLabel.setText("* To further manage the groupings for this device, please log into the Yukon Web operations client. Once there, enter the Metering section and proceed to the Device Details page to update the device's assigned groups.");
              customGroupsDirectionsLabel.setFont(new Font("dialog", 0, 13));
              customGroupsDirectionsLabel.setLineWrap( true );
              customGroupsDirectionsLabel.setWrapStyleWord( true );
              customGroupsDirectionsLabel.setBackground( null );
          } catch (java.lang.Throwable ivjExc) {
              handleException(ivjExc);
          }
        }
        return customGroupsDirectionsLabel;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
         com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
         com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
    
    
    @Override
    public Object getValue(Object val) {
        DeviceBase device = null;
        
        SimpleDevice yd = null;
        if (val instanceof MultiDBPersistent) {
            if (((MultiDBPersistent) val).getDBPersistentVector().get(0) instanceof DeviceBase) {
                device = (DeviceBase) (((MultiDBPersistent) val).getDBPersistentVector().get(0));
            }
        } else {
            device = (DeviceBase) val;
        }
        yd = new SimpleDevice(device.getPAObjectID(), device.getPaoType());
        String cycleGroup = (String)getCycleGroupComboBox().getSelectedItem();
        String alternateGroup = (String)getAlternateGroupComboBox().getSelectedItem();
        String billingGroup = (String)getJComboBoxBillingGroup().getSelectedItem();
        
        DeviceGroupMember dgm = new DeviceGroupMember(yd, billingGroup, cycleGroup, alternateGroup, null, null, null);
        ((MultiDBPersistent) val).getDBPersistentVector().add(dgm);
        
        return val;
        
    }
    
    @Override
    public void setValue(Object o) {
        DeviceBase device = null;
        SimpleDevice yd = null;
        
        if (o instanceof MultiDBPersistent) {
            if (((MultiDBPersistent) o).getDBPersistentVector().get(0) instanceof DeviceBase) {
                device = (DeviceBase) (((MultiDBPersistent) o).getDBPersistentVector().get(0));
            }
        } else {
            device = (DeviceBase) o;
        }
        
        yd = new SimpleDevice(device.getPAObjectID(), device.getPaoType());
        String billingGroup = hacker.getGroupForDevice(FixedDeviceGroups.BILLINGGROUP, yd);
        String alternateGroup = hacker.getGroupForDevice(FixedDeviceGroups.TESTCOLLECTIONGROUP, yd);
        String collectionGroup = hacker.getGroupForDevice(FixedDeviceGroups.COLLECTIONGROUP, yd);
        getCycleGroupComboBox().setSelectedItem( collectionGroup );
        getAlternateGroupComboBox().setSelectedItem( alternateGroup );
        getJComboBoxBillingGroup().setSelectedItem( billingGroup );
    }
    
    @Override
    public void setFirstFocus() 
    {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            @Override
            public void run() 
                { 
                getCycleGroupComboBox().requestFocus(); 
            } 
        });    
    }

}