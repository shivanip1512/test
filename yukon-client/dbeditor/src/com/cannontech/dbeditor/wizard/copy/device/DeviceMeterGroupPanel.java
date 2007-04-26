package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceMeterGroup;
 
public class DeviceMeterGroupPanel extends com.cannontech.common.gui.util.DataInputPanel {
    
    private javax.swing.JLabel ivjAreaCodeGroupLabel = null;
    private javax.swing.JLabel ivjCycleGroupLabel = null;
    private javax.swing.JComboBox ivjAreaCodeGroupComboBox = null;
    private javax.swing.JComboBox ivjCycleGroupComboBox = null;
    private javax.swing.JComboBox ivjJComboBoxBillingGroup = null;
    private javax.swing.JLabel ivjJLabelBillingGroup = null;
    
    public DeviceMeterGroupPanel() {
        super();
        initialize();
    }
    
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
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
            add(getAreaCodeGroupComboBox(), constraintsAreaCodeGroupComboBox);

            java.awt.GridBagConstraints constraintsJLabelBillingGroup = new java.awt.GridBagConstraints();
            constraintsJLabelBillingGroup.gridx = 1; constraintsJLabelBillingGroup.gridy = 4;
            constraintsJLabelBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelBillingGroup.ipadx = 9;
            constraintsJLabelBillingGroup.ipady = 2;
            constraintsJLabelBillingGroup.insets = new java.awt.Insets(7, 16, 23, 0);
            add(getJLabelBillingGroup(), constraintsJLabelBillingGroup);

            java.awt.GridBagConstraints constraintsJComboBoxBillingGroup = new java.awt.GridBagConstraints();
            constraintsJComboBoxBillingGroup.gridx = 2; constraintsJComboBoxBillingGroup.gridy = 4;
            constraintsJComboBoxBillingGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxBillingGroup.weightx = 1.0;
            constraintsJComboBoxBillingGroup.insets = new java.awt.Insets(3, 1, 20, 21);
            add(getJComboBoxBillingGroup(), constraintsJComboBoxBillingGroup);

            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
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
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getCycleGroupComboBox() {
        if (ivjCycleGroupComboBox == null) {
            try {
                ivjCycleGroupComboBox = new javax.swing.JComboBox();
                ivjCycleGroupComboBox.setName("CycleGroupComboBox");
                ivjCycleGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
                ivjCycleGroupComboBox.setEditable(true);
                ivjCycleGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
                // user code begin {1}
             
             try
             {
                String availableCycleGroups[] = DeviceMeterGroup.getDeviceCollectionGroups();
                for(int i=0;i<availableCycleGroups.length;i++)
                   ivjCycleGroupComboBox.addItem( availableCycleGroups[i] );
             }
             catch(java.sql.SQLException e)
             {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
             }
             
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getAreaCodeGroupComboBox() {
        if (ivjAreaCodeGroupComboBox == null) {
            try {
                ivjAreaCodeGroupComboBox = new javax.swing.JComboBox();
                ivjAreaCodeGroupComboBox.setName("AreaCodeGroupComboBox");
                ivjAreaCodeGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
                ivjAreaCodeGroupComboBox.setEditable(true);
                ivjAreaCodeGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
                // user code begin {1}
             
             try
             {
                String availableAreaCodeGroups[] = DeviceMeterGroup.getDeviceTestCollectionGroups();
                for(int i=0;i<availableAreaCodeGroups.length;i++)
                   ivjAreaCodeGroupComboBox.addItem( availableAreaCodeGroups[i] );
             }
             catch(java.sql.SQLException e)
             {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
             }
             
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAreaCodeGroupComboBox;
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
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getJComboBoxBillingGroup() {
        if (ivjJComboBoxBillingGroup == null) {
            try {
                ivjJComboBoxBillingGroup = new javax.swing.JComboBox();
                ivjJComboBoxBillingGroup.setName("JComboBoxBillingGroup");
                ivjJComboBoxBillingGroup.setPreferredSize(new java.awt.Dimension(200, 25));
                ivjJComboBoxBillingGroup.setEditable(true);
                ivjJComboBoxBillingGroup.setMinimumSize(new java.awt.Dimension(200, 25));
                // user code begin {1}
             
             try
             {
                String avBillGrps[] = DeviceMeterGroup.getDeviceBillingGroups();
                for( int i = 0; i < avBillGrps.length; i++ )
                   ivjJComboBoxBillingGroup.addItem( avBillGrps[i] );
             }
             catch(java.sql.SQLException e)
             {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
             }
             
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
    
    
    public Object getValue(Object val) {
        
        DeviceMeterGroup dmg = null;
        
        if (val instanceof MultiDBPersistent)
        {
            if ((DBPersistent) ((MultiDBPersistent) val).getDBPersistentVector().get(0) instanceof MCTBase)
            {
                dmg = ((MCTBase) ((DBPersistent) ((MultiDBPersistent) val).getDBPersistentVector().get(0))).getDeviceMeterGroup();
            }
        }
        else
        {
            dmg = ((MCTBase) val).getDeviceMeterGroup();
        }

        String cycleGroup = getCycleGroupComboBox().getSelectedItem().toString();
        String areaCodeGroup = getAreaCodeGroupComboBox().getSelectedItem().toString();
        
        String billingGroup = getJComboBoxBillingGroup().getSelectedItem().toString();
        
        if( cycleGroup != null && cycleGroup.length() > 0 )
        {
            dmg.setCollectionGroup( cycleGroup );
        } 
        if( areaCodeGroup != null && areaCodeGroup.length() > 0 )
        {
            dmg.setTestCollectionGroup( areaCodeGroup );
        }
        if( billingGroup != null && billingGroup.length() > 0 )
        {
            dmg.setBillingGroup( billingGroup );
        }
        return val;
        
    }

    
    public void setValue(Object o) 
    {
        DeviceMeterGroup dmg = null;
        
        if (o instanceof MultiDBPersistent)
        {
            if ((DBPersistent) ((MultiDBPersistent) o).getDBPersistentVector().get(0) instanceof MCTBase)
            {
                dmg = ((MCTBase) ((DBPersistent) ((MultiDBPersistent) o).getDBPersistentVector().get(0))).getDeviceMeterGroup();
            }
        }
        else
        {
            dmg = ((MCTBase) o).getDeviceMeterGroup();
        }
        
        getCycleGroupComboBox().setSelectedItem(dmg.getCollectionGroup());
        getAreaCodeGroupComboBox().setSelectedItem(dmg.getTestCollectionGroup());
        getJComboBoxBillingGroup().setSelectedItem(dmg.getBillingGroup());
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