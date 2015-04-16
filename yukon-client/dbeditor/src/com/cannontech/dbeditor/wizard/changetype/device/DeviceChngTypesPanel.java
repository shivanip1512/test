package com.cannontech.dbeditor.wizard.changetype.device;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.device.service.DeviceUpdateService.PointToTemplate;
import com.cannontech.common.device.service.DeviceUpdateService.PointsToProcess;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;
import com.cannontech.spring.YukonSpringHook;

public class DeviceChngTypesPanel extends DataInputPanel implements ListSelectionListener {

    private DeviceBase currentDevice = null;

    private PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
    private DeviceUpdateService deviceUpdateService = YukonSpringHook.getBean(DeviceUpdateService.class);

    private JList deviceJList = null;
    private JPanel ivjJPanelNotes = null;
    private JScrollPane ivjJScrollPaneDevList = null;
    private JScrollPane ivjJScrollPaneNotes = null;
    private JTextPane ivjJTextPaneNotes = null;

    public boolean displayMctOptions = false;
    public boolean displayRfnOptions = false;

    /**
     * Constructor
     */
    public DeviceChngTypesPanel() {
        super();
        initialize();
    }

    /**
     * This method was created in VisualAge.
     * @return Dimension
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * This method was created in VisualAge.
     * @return Dimension
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.Object
     * @param val java.lang.Object
     */
    @Override
    public Object getValue(Object val) {
        PaoDefinition newDefinition = (PaoDefinition) getJListDevices().getSelectedValue();

        if (val == null) {
            return newDefinition.getType();
        }

        if (val instanceof ChangeDeviceTypeInfo) {
            return deviceUpdateService.changeDeviceType(currentDevice, newDefinition, (ChangeDeviceTypeInfo) val);
        } else {
            return deviceUpdateService.changeDeviceType(currentDevice, newDefinition, null);
        }
    }
    
    @Override
    public void setValue(Object val) {
    }

    @Override
    public void valueChanged(ListSelectionEvent ev) {
        // make sure we have the last event in a sequence of events.
        if (!ev.getValueIsAdjusting()) {            
            if (ev.getSource() == getJListDevices()) {
                getJTextPaneNotes().setText("generating type differences...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PaoDefinition paoDefinition = (PaoDefinition) getJListDevices().getSelectedValue();
                        
                        if (((PaoDefinition) getJListDevices().getSelectedValue()).equals(paoDefinition)) {

                            displayMctOptions = false;
                            displayRfnOptions = false;
                            PaoType currentDeviceType = currentDevice.getPaoType();
                            PaoType newType = paoDefinition.getType();
                            StringBuilder requiredText = new  StringBuilder();
                            
                            if (currentDeviceType.isMct() && newType.isRfMeter()) {
                                displayRfnOptions = true;
                                requiredText.append("\n-- serial number\n");
                                requiredText.append("-- model\n");
                                requiredText.append("-- manufacturer");
                            }
                            if (currentDeviceType.isRfMeter() && newType.isMct()) {
                                displayMctOptions = true;
                                requiredText.append("\n-- physical address\n");
                                requiredText.append("-- route");
                            }
                            
                            StringBuilder text = new StringBuilder();
                            text.append("Change from " + currentDeviceType + " to " + paoDefinition.getDisplayName());
                            if(requiredText.length() > 0){
                                text.append("\n\nRequired:");
                                text.append(requiredText);
                            }
                            text.append(generatePointChangeText(paoDefinition));
                            getJTextPaneNotes().setText(text.toString());

                            fireInputUpdate();

                        }
                    }
                }).start();
            }
        }
    }

    /**
     * Returns the currentDevice.
     * @return DeviceBase
     */
    public DeviceBase getCurrentDevice() {
        return currentDevice;
    }

    /**
     * Sets the currentDevice.
     * @param currentDevice The currentDevice to set
     */
    public void setCurrentDevice(DeviceBase currentDevice) {
        this.currentDevice = currentDevice;

        setList(currentDevice);

    }

    /**
     * Initialize the UI.
     */
    private void initialize() {
        try {
            setName("DeviceNameAddressPanel");
            setLayout(new GridBagLayout());
            setSize(355, 371);

            JLabel deviceLabel = new JLabel("Select the new device type:");
            deviceLabel.setFont(new Font("dialog", 0, 14));
            add(deviceLabel, new GridBagConstraints(1,
                                                    1,
                                                    1,
                                                    1,
                                                    1.0,
                                                    0.0,
                                                    GridBagConstraints.WEST,
                                                    GridBagConstraints.NONE,
                                                    new Insets(0, 10, 5, 0),
                                                    0,
                                                    0));

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              getJScrollPaneDevList(),
                                              getJPanelNotes());
            split.setDividerSize(10);
            add(split, new GridBagConstraints(1,
                                              2,
                                              1,
                                              1,
                                              1.0,
                                              1.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.BOTH,
                                              new Insets(0, 10, 10, 10),
                                              0,
                                              0));

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        getJListDevices().addListSelectionListener(this);

    }

    /**
     * Helper method to set the change list to the list of devices that the
     * given device can change into
     * @param device - Device to change
     */
    private void setList(DeviceBase device) {

        // Get a list of all of the device definitions that the device can
        // change to and sort the list by device type
        List<PaoDefinition> deviceList = new ArrayList<PaoDefinition>();
        DeviceDao deviceDao = YukonSpringHook.getBean(DeviceDao.class);
        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        deviceList.addAll(paoDefinitionService.getChangeablePaos(yukonDevice));
        Collections.sort(deviceList);

        // Set the JList data
        getJListDevices().setListData(deviceList.toArray());
        getJListDevices().setSelectedIndex(0);

    }

    /**
     * Helper method to generate text explaining what will be changed on the
     * current device to change it's type to the selected type
     * @param paoDefinition - Selected definition to change the current
     *            device to
     * @return A string explaining change details
     */
    private String generatePointChangeText(PaoDefinition paoDefinition) {

        StringBuffer buffer = new StringBuffer();
        
        DeviceBase device = getCurrentDevice();
        
        PointsToProcess pointsToProcess = deviceUpdateService.getPointsToProccess(device, paoDefinition.getType());

        // Add text for point additions
        Set<PointTemplate> addTemplates = pointsToProcess.getPointsToAdd();
        
        buffer.append("\n\nPoints to add:\n");
        if (addTemplates.size() == 0) {
            buffer.append("--none\n");
        } else {
            for (PointTemplate template : addTemplates) {
                buffer.append("-- " + template.getName() + "\n");
            }
        }
        buffer.append("\n");

        // Add text for point deletions
        Set<PointBase> removeTemplates = pointsToProcess.getPointsToDelete();
        buffer.append("Points to remove:\n");
        buffer.append(this.generateRemoveChangeText(paoDefinition, removeTemplates));

        // Add text for point transfers
        Collection<PointToTemplate> transferTemplates = pointsToProcess.getPointsToTransfer().values();
        buffer.append("Points to transfer:\n");
        if (transferTemplates.size() == 0) {
            buffer.append("--none\n");
        } else {
            buffer.append(this.generatePointTransferChangeText(paoDefinition, transferTemplates));
        }

        return buffer.toString();
    }

    /**
     * Helper method to generate notes for each point that will be removed when
     * the device type is changed
     * @param paoDefinition - Selected definition to change the current
     *            device to
     * @param points - Templates of points to remove
     * @return A String with remove information
     */
    private String generateRemoveChangeText(PaoDefinition paoDefinition, Set<PointBase> points) {

        StringBuffer buffer = new StringBuffer();

        if (points.size() == 0) {
            buffer.append("--none\n");
        } else {
            for (PointBase point : points) {
                buffer.append("-- #" + point.getPoint().getPointOffset() + " " + point.getPoint().getPointName() + "\n");
            }
        }
        buffer.append("\n");

        return buffer.toString();

    }

    /**
     * Helper method to generate notes for what will change about each point
     * that will be transferred
     * @param paoDefinition - Selected definition to change the current
     *            device to
     * @param transferTemplates - Templates for points to transfer
     * @return A String with change information
     */
    private String generatePointTransferChangeText(PaoDefinition paoDefinition,
            Collection<PointToTemplate> transferTemplates) {

        StringBuffer buffer = new StringBuffer();

        
        for (PointToTemplate pointToTemplate : transferTemplates) {
    
            Point point = pointToTemplate.getPoint().getPoint();
            buffer.append("-- #" + point.getPointOffset() + " " + point.getPointName()+ "\n");

            if (!pointToTemplate.getTemplate().getName().equals(point.getPointName())) {
                buffer.append("    point name will be updated to: " + pointToTemplate.getTemplate().getName()
                        + "\n");
            }
            
            if (pointToTemplate.getTemplate().getOffset() != point.getPointOffset()) {
                buffer.append("    offset will be updated to: " + pointToTemplate.getTemplate().getOffset() + "\n");
            }

            PointBase pointBase = pointToTemplate.getPoint();
            Double currentMultiplier = 1.0;
            if (pointBase instanceof AccumulatorPoint) {
                AccumulatorPoint accPoint = (AccumulatorPoint) pointBase;
                currentMultiplier = accPoint.getPointAccumulator().getMultiplier();
            } else if (pointBase instanceof AnalogPoint) {
                AnalogPoint analogPoint = (AnalogPoint) pointBase;
                currentMultiplier = analogPoint.getPointAnalog().getMultiplier();
            }
        
            if (pointToTemplate.getTemplate().getMultiplier() != currentMultiplier) {
                buffer.append("    multiplier will be updated to: " + pointToTemplate.getTemplate().getMultiplier()
                        + "\n");
            }
        }

        return buffer.toString();
    }

    /**
     * Return the JListDevices property value.
     * @return JList
     */
    private JList getJListDevices() {
        if (deviceJList == null) {
            try {
                deviceJList = new JList();
                deviceJList.setName("JListDevices");
                deviceJList.setBounds(0, 0, 300, 50);
                deviceJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return deviceJList;
    }

    /**
     * Return the JPanelNotes property value.
     * @return JPanel
     */
    private JPanel getJPanelNotes() {
        if (ivjJPanelNotes == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitle("Notes");
                ivjJPanelNotes = new javax.swing.JPanel();
                ivjJPanelNotes.setName("JPanelNotes");
                ivjJPanelNotes.setBorder(ivjLocalBorder);
                ivjJPanelNotes.setLayout(new java.awt.BorderLayout());
                getJPanelNotes().add(getJScrollPaneNotes(), "Center");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelNotes;
    }

    /**
     * Return the JScrollPane1 property value.
     * @return JScrollPane
     */
    private JScrollPane getJScrollPaneDevList() {
        if (ivjJScrollPaneDevList == null) {
            try {
                ivjJScrollPaneDevList = new JScrollPane();
                ivjJScrollPaneDevList.setName("JScrollPaneDevList");
                getJScrollPaneDevList().setViewportView(getJListDevices());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPaneDevList;
    }

    /**
     * Return the JScrollPaneNotes property value.
     * @return JScrollPane
     */
    private JScrollPane getJScrollPaneNotes() {
        if (ivjJScrollPaneNotes == null) {
            try {
                ivjJScrollPaneNotes = new javax.swing.JScrollPane();
                ivjJScrollPaneNotes.setName("JScrollPaneNotes");
                getJScrollPaneNotes().setViewportView(getJTextPaneNotes());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPaneNotes;
    }

    /**
     * Return the JTextPaneNotes property value.
     * @return JTextPane
     */
    private JTextPane getJTextPaneNotes() {
        if (ivjJTextPaneNotes == null) {
            try {
                ivjJTextPaneNotes = new JTextPane();
                ivjJTextPaneNotes.setName("JTextPaneNotes");
                ivjJTextPaneNotes.setBackground(new java.awt.Color(204, 204, 204));
                ivjJTextPaneNotes.setBounds(0, 0, 183, 196);
                ivjJTextPaneNotes.setEditable(false);
                ivjJTextPaneNotes.setBackground(getJScrollPaneDevList().getBackground());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextPaneNotes;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
        ;
    }
}