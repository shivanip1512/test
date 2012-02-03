package com.cannontech.dbeditor.wizard.device;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IPCMeter;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;

public class DeviceTcpTerminalPanel extends DataInputPanel implements ActionListener, CaretListener{
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Font DIALOG_FONT = new Font("dialog", 0, 14);
    private static final Font SS_FONT = new Font("sansserif", 0, 14);
    
    private JComboBox baudRateComboBox = null;
    private JLabel baudRateLabel = null;
    private JLabel ipAddressLabel = null;
    private JTextField ipAddressTextField = null;
    private JTextField portTextField = null;
    private JLabel portNumberLabel = null;
    
    public DeviceTcpTerminalPanel() {
        setName("DeviceTcpTerminalPanel");
        setFont(new java.awt.Font("dialog", 0, 14));
        setLayout(new java.awt.GridBagLayout());
        setSize(350, 300);

        ipAddressLabel = new JLabel();
        ipAddressLabel.setName("ipAddressLabel");
        ipAddressLabel.setFont(DIALOG_FONT);
        ipAddressLabel.setText("IP Address:");
        GridBagConstraints constraintsipAddressLabel = new GridBagConstraints();
        constraintsipAddressLabel.gridx = 0; constraintsipAddressLabel.gridy = 0;
        constraintsipAddressLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsipAddressLabel.anchor = GridBagConstraints.WEST;
        add(ipAddressLabel, constraintsipAddressLabel);
        
        ipAddressTextField = new JTextField();                
        ipAddressTextField.setName("ipAddressTextField");                 
        ipAddressTextField.setFont(SS_FONT);
        ipAddressTextField.setText("");                                   
        ipAddressTextField.setColumns(12);
        GridBagConstraints constraintsipAddressTextField = new GridBagConstraints();
        constraintsipAddressTextField.gridx = 1; constraintsipAddressTextField.gridy = 0;
        constraintsipAddressTextField.anchor = GridBagConstraints.WEST;
        constraintsipAddressTextField.insets = new Insets(5, 10, 5, 0);                                
        add(ipAddressTextField, constraintsipAddressTextField);
        
        portNumberLabel = new javax.swing.JLabel();
        portNumberLabel.setName("SPortNumberLabel");
        portNumberLabel.setFont(DIALOG_FONT);
        portNumberLabel.setText("Port Number:");
        GridBagConstraints constraintsPortNumberLabel = new GridBagConstraints();
        constraintsPortNumberLabel.gridx = 0; constraintsPortNumberLabel.gridy = 1;
        constraintsPortNumberLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsPortNumberLabel.anchor = GridBagConstraints.WEST;
        add(portNumberLabel, constraintsPortNumberLabel);
        
        portTextField = new javax.swing.JTextField();
        portTextField.setName("PortTextField");
        portTextField.setFont(SS_FONT);
        portTextField.setColumns(4);
        portTextField.setDocument(new LongRangeDocument(0, 999999));
        GridBagConstraints constraintsPortTextField = new GridBagConstraints();
        constraintsPortTextField.gridx = 1; constraintsPortTextField.gridy = 1;
        constraintsPortTextField.anchor = GridBagConstraints.WEST;
        constraintsPortTextField.insets = new Insets(5, 10, 5, 0);
        add(portTextField, constraintsPortTextField);
        
        baudRateLabel = new javax.swing.JLabel();
        baudRateLabel.setName("BaudRateLabel");
        baudRateLabel.setFont(DIALOG_FONT);
        baudRateLabel.setText("Baud Rate:");
        GridBagConstraints constraintsBaudRateLabel = new GridBagConstraints();
        constraintsBaudRateLabel.gridx = 0; constraintsBaudRateLabel.gridy = 2;
        constraintsBaudRateLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsBaudRateLabel.anchor = GridBagConstraints.WEST;
        add(baudRateLabel, constraintsBaudRateLabel);
        
        baudRateComboBox = new JComboBox();
        baudRateComboBox.setName("BaudRateComboBox");
        baudRateComboBox.setFont(DIALOG_FONT);
        baudRateComboBox.setMaximumRowCount(5);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_300);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_1200);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_2400);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_4800);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_9600);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_14400);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_28800);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_38400);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_57600);
        baudRateComboBox.addItem(com.cannontech.common.version.DBEditorDefines.BAUD_115200);
        baudRateComboBox.setSelectedItem(com.cannontech.common.version.DBEditorDefines.BAUD_1200);
        GridBagConstraints constraintsBaudRateComboBox = new GridBagConstraints();
        constraintsBaudRateComboBox.gridx = 1; constraintsBaudRateComboBox.gridy = 2;
        constraintsBaudRateComboBox.fill = GridBagConstraints.HORIZONTAL;
        constraintsBaudRateComboBox.anchor = GridBagConstraints.WEST;
        constraintsBaudRateComboBox.insets = new Insets(5, 10, 5, 0);
        add(baudRateComboBox, constraintsBaudRateComboBox);
        
        //set the panel as a listener for its own events
        ipAddressTextField.addCaretListener(this);
        portTextField.addCaretListener(this);
        baudRateComboBox.addActionListener(this);
    }
    
    public void setValue(Object val) {
    }
    
    /**
     * Takes an object (which should be an IPCMeter) and populates it with the comm channel
     * settings from this panel.
     * 
     * Additionally, this method turns the IPCMeter object into a SmartMultiDBPersistent
     * that includes all the default points.
     */
    public Object getValue(Object val) 
    {
        String ipAddress = ipAddressTextField.getText();
        Integer portNumber = new Integer(portTextField.getText());
        Integer baudRate = new Integer((String) baudRateComboBox.getSelectedItem());
        
        IPCMeter ipc = (IPCMeter) val;
        ipc.getPortTerminalServer().setIpAddress(ipAddress);
        ipc.getPortTerminalServer().setSocketPortNumber(portNumber);
        ipc.getPortSettings().setBaudRate(baudRate);
        ipc.getPortSettings().setLineSettings("8N1");
        
        PaoDao paoDao = DaoFactory.getPaoDao();
        ipc.setDeviceID(paoDao.getNextPaoId());

        SmartMultiDBPersistent smartDB = createSmartDBPersistent(ipc);
        
        return smartDB;
    }
    
    /**
     * Validates the panel input. If the input is invalid, an error string is populated that
     * can be retrieved with <code>getErrorString()</code>.
     * @return true if the input is valid, otherwise false.
     */
    public boolean isInputValid() {
        String ipAddr = ipAddressTextField.getText();
        
        if(ipAddr == null){
            setErrorString("The IP Address text field must be filled in");
            return false;
        }
        
        if(IP_PATTERN.matcher(ipAddr).matches()) {
            String[] substrings = ipAddr.split("\\.");
            for(String substring : substrings) {
                int value = new Integer(substring);
                if(value < 0 || value > 255) {
                    setErrorString("The IP address value is invalid.");
                    return false;
                }
            }
        } else {
            setErrorString("The IP address value is invalid.");
            return false;
        }
        
        try {
            //check that the port string can be converted into a number
            new Integer(portTextField.getText());
        } catch(NumberFormatException n ) {
            setErrorString("The port field should only contain numbers.");
            return false;
        }

        return true;
    }
    
    /*
     * Returns a SmartMultiDBPersistent for the deviceBase.
     * Includes all points and the deviceBase as the OwnerDBPersistent.
     */
    private SmartMultiDBPersistent createSmartDBPersistent(DeviceBase deviceBase)
    {
        if( deviceBase == null )
            return null;
    
        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(deviceBase);
    
        PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
        DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);
    
        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }
    
        return smartDB; 
    }
    
    //Listener methods:
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == baudRateComboBox) 
            fireInputUpdate();
    }
    
    public void caretUpdate(CaretEvent event) {
        if (event.getSource() == ipAddressTextField || event.getSource() == portTextField) 
            fireInputUpdate();
    }
    
    public void setFirstFocus() {
        SwingUtilities.invokeLater(new Runnable(){ 
            public void run() { 
                ipAddressTextField.requestFocus(); 
            } 
        });    
    }
}