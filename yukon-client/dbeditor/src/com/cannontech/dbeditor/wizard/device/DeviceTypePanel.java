package com.cannontech.dbeditor.wizard.device;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.event.ListSelectionListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Multimap;

public class DeviceTypePanel extends DataInputPanel implements ListSelectionListener {
    private Multimap<String, PaoDefinition> deviceDisplayGroupMap = null;

    private javax.swing.JLabel ivjTypeLabel = null;

    private javax.swing.JList ivjDeviceCategoryList = null;
    private javax.swing.JScrollPane ivjDeviceCategoryScrollPane = null;
    private javax.swing.JList ivjDeviceTypeList = null;
    private javax.swing.JScrollPane ivjDeviceTypeScrollPane = null;
    private javax.swing.JPanel ivjListBoxPanel = null;

    public DeviceTypePanel() {
        super();
        initialize();
    }

    /**
     * connEtoC1:
     * (TypeList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent)
     * -->
     * DeviceTypePanel.typeList_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
     * @param arg1 javax.swing.event.ListSelectionEvent
     */
    private void connEtoC1(javax.swing.event.ListSelectionEvent arg1) {
        try {
            this.deviceCategoryList_ValueChanged();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Comment
     */
    public void deviceCategoryList_ValueChanged() {

        String selected = (String) getDeviceCategoryList().getSelectedValue();

        getDeviceTypeList().setListData(this.deviceDisplayGroupMap.get(selected).toArray());

        getDeviceTypeList().setSelectedIndex(0);

        invalidate();
        repaint();
    }

    /**
     * Return the DeviceCategoryList property value.
     * @return javax.swing.JList
     */
    private javax.swing.JList getDeviceCategoryList() {
        if (ivjDeviceCategoryList == null) {
            try {
                ivjDeviceCategoryList = new javax.swing.JList();
                ivjDeviceCategoryList.setName("DeviceCategoryList");
                ivjDeviceCategoryList.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDeviceCategoryList.setBounds(0, 0, 160, 120);
                ivjDeviceCategoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceCategoryList;
    }

    /**
     * Return the DeviceCategoryScrollPane property value.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getDeviceCategoryScrollPane() {
        if (ivjDeviceCategoryScrollPane == null) {
            try {
                ivjDeviceCategoryScrollPane = new javax.swing.JScrollPane();
                ivjDeviceCategoryScrollPane.setName("DeviceCategoryScrollPane");
                getDeviceCategoryScrollPane().setViewportView(getDeviceCategoryList());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceCategoryScrollPane;
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public PaoType getDeviceType() {
        return ((PaoDefinition) getDeviceTypeList().getSelectedValue()).getType();
    }

    /**
     * Return the DeviceTypeList property value.
     * @return javax.swing.JList
     */
    private javax.swing.JList getDeviceTypeList() {
        if (ivjDeviceTypeList == null) {
            try {
                ivjDeviceTypeList = new javax.swing.JList();
                ivjDeviceTypeList.setName("DeviceTypeList");
                ivjDeviceTypeList.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDeviceTypeList.setVisibleRowCount(12);
                ivjDeviceTypeList.setBounds(-9, 0, 169, 120);
                ivjDeviceTypeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceTypeList;
    }

    /**
     * Return the DeviceTypeScrollPane property value.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getDeviceTypeScrollPane() {
        if (ivjDeviceTypeScrollPane == null) {
            try {
                ivjDeviceTypeScrollPane = new javax.swing.JScrollPane();
                ivjDeviceTypeScrollPane.setName("DeviceTypeScrollPane");
                ivjDeviceTypeScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                getDeviceTypeScrollPane().setViewportView(getDeviceTypeList());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceTypeScrollPane;
    }

    /**
     * Return the ListBoxPanel property value.
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getListBoxPanel() {
        if (ivjListBoxPanel == null) {
            try {
                ivjListBoxPanel = new javax.swing.JPanel();
                ivjListBoxPanel.setName("ListBoxPanel");
                ivjListBoxPanel.setLayout(getListBoxPanelGridLayout());
                getListBoxPanel().add(getDeviceCategoryScrollPane(),
                                      getDeviceCategoryScrollPane().getName());
                getListBoxPanel().add(getDeviceTypeScrollPane(),
                                      getDeviceTypeScrollPane().getName());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjListBoxPanel;
    }

    /**
     * Return the ListBoxPanelGridLayout property value.
     * @return java.awt.GridLayout
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.GridLayout getListBoxPanelGridLayout() {
        java.awt.GridLayout ivjListBoxPanelGridLayout = null;
        try {
            /* Create part */
            ivjListBoxPanelGridLayout = new java.awt.GridLayout();
            ivjListBoxPanelGridLayout.setHgap(20);
            ivjListBoxPanelGridLayout.setColumns(2);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ;
        return ivjListBoxPanelGridLayout;
    }

    /**
     * This method was created in VisualAge.
     * @return java.awt.Dimension
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * This method was created in VisualAge.
     * @return java.awt.Dimension
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    /**
     * Return the TypeLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getTypeLabel() {
        if (ivjTypeLabel == null) {
            try {
                ivjTypeLabel = new javax.swing.JLabel();
                ivjTypeLabel.setName("TypeLabel");
                ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjTypeLabel.setText("Select the type of device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeLabel;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.Object
     * @param val java.lang.Object
     */
    @Override
    public Object getValue(Object val) {
        // Determine the correct type of device and return it

        PaoType type = ((PaoDefinition) getDeviceTypeList().getSelectedValue()).getType();
        DeviceBase returnDevice = DeviceFactory.createDevice(type);

        return returnDevice;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
        ;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getDeviceCategoryList().addListSelectionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {

        // Initialize the device type map
        PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
        this.deviceDisplayGroupMap = paoDefinitionService.getCreatablePaoDisplayGroupMap();
        
        // These strings are hard-coded in the paoDefinintion.xml file, we want them "createable", just not by DatabaseEditor
        this.deviceDisplayGroupMap.removeAll("Demand Response");
        this.deviceDisplayGroupMap.removeAll("Volt/Var");

        try {
            setName("DeviceTypePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
            constraintsTypeLabel.gridx = 1;
            constraintsTypeLabel.gridy = 1;
            constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsTypeLabel.ipadx = 4;
            constraintsTypeLabel.insets = new java.awt.Insets(17, 12, 4, 175);
            add(getTypeLabel(), constraintsTypeLabel);

            java.awt.GridBagConstraints constraintsListBoxPanel = new java.awt.GridBagConstraints();
            constraintsListBoxPanel.gridx = 1;
            constraintsListBoxPanel.gridy = 2;
            constraintsListBoxPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsListBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsListBoxPanel.weightx = 1.0;
            constraintsListBoxPanel.weighty = 1.0;
            constraintsListBoxPanel.ipadx = 264;
            constraintsListBoxPanel.ipady = 125;
            constraintsListBoxPanel.insets = new java.awt.Insets(4, 12, 9, 10);
            add(getListBoxPanel(), constraintsListBoxPanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // Manually add the device types to the TypeList for now

        getDeviceCategoryList().setListData(this.deviceDisplayGroupMap.keySet().toArray());
        getDeviceCategoryList().setSelectedIndex(0);
    }

    /**
     * isDataComplete method comment.
     */
    public boolean isDataComplete() {

        if (getDeviceTypeList().getSelectedValue() != null)
            return true;
        else
            return false;
    }

    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    @Override
    public boolean isInputValid() {
        if (getDeviceTypeList().getSelectedValue() == null) {
            setErrorString("A device type must be selected");
            return false;
        } else
            return true;

    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            Frame frame = new java.awt.Frame();
            DeviceTypePanel aDeviceTypePanel;
            aDeviceTypePanel = new DeviceTypePanel();
            frame.add("Center", aDeviceTypePanel);
            frame.setSize(aDeviceTypePanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
            com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
            ;
        }
    }

    /**
     * This method was created in VisualAge.
     * @param val java.lang.Object
     */
    @Override
    public void setValue(Object val) {
        return;
    }

    /**
     * Comment
     */
    public void typeList_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        fireInputUpdate();
    }

    /**
     * Method to handle events for the ListSelectionListener interface.
     * @param e javax.swing.event.ListSelectionEvent
     */
    @Override
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        if (e.getSource() == getDeviceCategoryList())
            connEtoC1(e);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts
        // in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDeviceCategoryScrollPane().requestFocus();
            }
        });
    }

}