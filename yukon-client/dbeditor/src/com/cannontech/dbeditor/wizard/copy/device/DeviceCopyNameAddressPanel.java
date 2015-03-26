package com.cannontech.dbeditor.wizard.copy.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.IonBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater921;
import com.cannontech.database.data.device.RfnMeterBase;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
 
public class DeviceCopyNameAddressPanel extends DataInputPanel implements ItemListener, CaretListener {
    
    private javax.swing.JTextField ivjAddressTextField = null;
    private javax.swing.JLabel ivjNameLabel = null;
    private javax.swing.JTextField ivjNameTextField = null;
    private javax.swing.JLabel ivjPhysicalAddressLabel = null;
    private javax.swing.JCheckBox ivjPointCopyCheckBox = null;
    private javax.swing.JLabel ivjJLabelMeterNumber = null;
    private javax.swing.JPanel ivjJPanelCopyDevice = null;
    private javax.swing.JTextField ivjJTextFieldMeterNumber = null;
       private DeviceBase deviceBase = null;
       private JLabel jLabelErrorMessage = null;
       private DlcAddressRangeService dlcAddressRangeService = 
           YukonSpringHook.getBean("dlcAddressRangeService", DlcAddressRangeService.class);
       
       private static final Logger log = YukonLogManager.getLogger(DeviceCopyNameAddressPanel.class);

    class IvjEventHandler implements ItemListener, CaretListener {
        @Override
        public void caretUpdate(javax.swing.event.CaretEvent e) 
        {
            if (e.getSource() == DeviceCopyNameAddressPanel.this.getNameTextField()) 
                connEtoC1(e);
            if (e.getSource() == DeviceCopyNameAddressPanel.this.getAddressTextField()) 
                connEtoC2(e);
            if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldMeterNumber()) 
                connEtoC4(e);
            if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldPhoneNumber()) 
                connEtoC5(e);
        };
    
        @Override
        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == DeviceCopyNameAddressPanel.this.getPointCopyCheckBox()) 
                connEtoC3(e);
        };
    };

    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JLabel ivjJLabelPhoneNumber = null;
    private JTextField ivjJTextFieldPhoneNumber = null;
    
    /**
     * Constructor
     */
    public DeviceCopyNameAddressPanel() {
        super();
        initialize();
    }
    
    /**
     * Method to handle events for the CaretListener interface.
     * @param e javax.swing.event.CaretEvent
     */
    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        if (e.getSource() == getNameTextField()) 
            connEtoC1(e);
        if (e.getSource() == getAddressTextField()) 
            connEtoC2(e);
    }
    
    /**
     * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
     * @param arg1 javax.swing.event.CaretEvent
     */
    private void connEtoC1(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
     * @param arg1 javax.swing.event.CaretEvent
     */
    private void connEtoC2(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * connEtoC3:  (PointCopyCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceCopyNameAddressPanel.pointCopyCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private void connEtoC3(java.awt.event.ItemEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * connEtoC4:  (JTextFieldMeterNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
     * @param arg1 javax.swing.event.CaretEvent
     */
    private void connEtoC4(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
     * @param arg1 javax.swing.event.CaretEvent
     */
    private void connEtoC5(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public boolean copyPointsIsChecked() {
        return getPointCopyCheckBox().isSelected();
    }
    
    /**
     * Return the AddressTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getAddressTextField() {
        if (ivjAddressTextField == null) {
            try {
                ivjAddressTextField = new javax.swing.JTextField();
                ivjAddressTextField.setName("AddressTextField");
                ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjAddressTextField.setColumns(6);
                ivjAddressTextField.setDocument( new LongRangeDocument(-9999999999L, 9999999999L) );
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddressTextField;
    }
    
    /**
     * Return the JLabelMeterNumber property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelMeterNumber() {
        if (ivjJLabelMeterNumber == null) {
            try {
                ivjJLabelMeterNumber = new javax.swing.JLabel();
                ivjJLabelMeterNumber.setName("JLabelMeterNumber");
                ivjJLabelMeterNumber.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelMeterNumber.setText("Meter Number:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelMeterNumber;
    }
    
    /**
     * Return the JLabelPhoneNumber property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelPhoneNumber() {
        if (ivjJLabelPhoneNumber == null) {
            try {
                ivjJLabelPhoneNumber = new javax.swing.JLabel();
                ivjJLabelPhoneNumber.setName("JLabelPhoneNumber");
                ivjJLabelPhoneNumber.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelPhoneNumber.setText("Phone Number:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelPhoneNumber;
    }

    private javax.swing.JPanel getJPanelCopyDevice() {
        if (ivjJPanelCopyDevice == null) {
            try {
                ivjJPanelCopyDevice = new javax.swing.JPanel();
                ivjJPanelCopyDevice.setName("JPanelCopyDevice");
                ivjJPanelCopyDevice.setLayout(new java.awt.GridBagLayout());
    
                java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
                constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
                constraintsNameLabel.ipadx = 17;
                constraintsNameLabel.insets = new java.awt.Insets(28, 38, 7, 0);
                getJPanelCopyDevice().add(getNameLabel(), constraintsNameLabel);
    
                java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
                constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
                constraintsPhysicalAddressLabel.ipadx = 48;
                constraintsPhysicalAddressLabel.insets = new java.awt.Insets(7, 38, 5, 0);
                getJPanelCopyDevice().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);
    
                java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
                constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
                constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsNameTextField.weightx = 1.0;
                constraintsNameTextField.ipadx = 186;
                constraintsNameTextField.insets = new java.awt.Insets(26, 0, 5, 18);
                getJPanelCopyDevice().add(getNameTextField(), constraintsNameTextField);
    
                java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
                constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
                constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsAddressTextField.weightx = 1.0;
                constraintsAddressTextField.ipadx = 186;
                constraintsAddressTextField.insets = new java.awt.Insets(5, 0, 3, 18);
                getJPanelCopyDevice().add(getAddressTextField(), constraintsAddressTextField);
    
                java.awt.GridBagConstraints constraintsPointCopyCheckBox = new java.awt.GridBagConstraints();
                constraintsPointCopyCheckBox.gridx = 1; constraintsPointCopyCheckBox.gridy = 5;
                constraintsPointCopyCheckBox.ipadx = 12;
                constraintsPointCopyCheckBox.insets = new java.awt.Insets(4, 38, 28, 0);
                getJPanelCopyDevice().add(getPointCopyCheckBox(), constraintsPointCopyCheckBox);
    
                java.awt.GridBagConstraints constraintsJLabelMeterNumber = new java.awt.GridBagConstraints();
                constraintsJLabelMeterNumber.gridx = 1; constraintsJLabelMeterNumber.gridy = 3;
                constraintsJLabelMeterNumber.ipadx = 11;
                constraintsJLabelMeterNumber.insets = new java.awt.Insets(6, 38, 6, 0);
                getJPanelCopyDevice().add(getJLabelMeterNumber(), constraintsJLabelMeterNumber);
    
                java.awt.GridBagConstraints constraintsJTextFieldMeterNumber = new java.awt.GridBagConstraints();
                constraintsJTextFieldMeterNumber.gridx = 2; constraintsJTextFieldMeterNumber.gridy = 3;
                constraintsJTextFieldMeterNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldMeterNumber.weightx = 1.0;
                constraintsJTextFieldMeterNumber.ipadx = 186;
                constraintsJTextFieldMeterNumber.insets = new java.awt.Insets(4, 0, 4, 18);
                getJPanelCopyDevice().add(getJTextFieldMeterNumber(), constraintsJTextFieldMeterNumber);
    
                java.awt.GridBagConstraints constraintsJLabelPhoneNumber = new java.awt.GridBagConstraints();
                constraintsJLabelPhoneNumber.gridx = 1; constraintsJLabelPhoneNumber.gridy = 4;
                constraintsJLabelPhoneNumber.ipadx = 6;
                constraintsJLabelPhoneNumber.insets = new java.awt.Insets(6, 38, 5, 0);
                getJPanelCopyDevice().add(getJLabelPhoneNumber(), constraintsJLabelPhoneNumber);
    
                java.awt.GridBagConstraints constraintsJTextFieldPhoneNumber = new java.awt.GridBagConstraints();
                constraintsJTextFieldPhoneNumber.gridx = 2; constraintsJTextFieldPhoneNumber.gridy = 4;
                constraintsJTextFieldPhoneNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldPhoneNumber.weightx = 1.0;
                constraintsJTextFieldPhoneNumber.ipadx = 186;
                constraintsJTextFieldPhoneNumber.insets = new java.awt.Insets(4, 0, 3, 18);
                getJPanelCopyDevice().add(getJTextFieldPhoneNumber(), constraintsJTextFieldPhoneNumber);
                    
                 java.awt.GridBagConstraints cpg = new java.awt.GridBagConstraints();
                 cpg.gridx = 1;
                 cpg.gridy = 5;
                 cpg.anchor = java.awt.GridBagConstraints.WEST;
                 cpg.fill = java.awt.GridBagConstraints.HORIZONTAL;
                 cpg.gridwidth = 2;
                 cpg.insets = new java.awt.Insets(26, 48, 10, 15);
                 getJPanelCopyDevice().add(getJLabelErrorMessage(), cpg);
                    
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelCopyDevice;
    }
    
    /**
     * Return the JTextFieldMeterNumber property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getJTextFieldMeterNumber() {
        if (ivjJTextFieldMeterNumber == null) {
            try {
                ivjJTextFieldMeterNumber = new javax.swing.JTextField();
                ivjJTextFieldMeterNumber.setName("JTextFieldMeterNumber");
                ivjJTextFieldMeterNumber.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjJTextFieldMeterNumber.setColumns(6);
                ivjJTextFieldMeterNumber.setDocument(
                    new TextFieldDocument(TextFieldDocument.MAX_METER_NUMBER_LENGTH));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldMeterNumber;
    }
    
    /**
     * Return the JTextFieldPhoneNumber property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getJTextFieldPhoneNumber() {
        if (ivjJTextFieldPhoneNumber == null) {
            try {
                ivjJTextFieldPhoneNumber = new javax.swing.JTextField();
                ivjJTextFieldPhoneNumber.setName("JTextFieldPhoneNumber");
                ivjJTextFieldPhoneNumber.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjJTextFieldPhoneNumber.setColumns(6);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldPhoneNumber;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    /**
     * Return the NameLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new javax.swing.JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjNameLabel.setText("Device Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameLabel;
    }
    
    /**
     * Return the NameTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getNameTextField() {
        if (ivjNameTextField == null) {
            try {
                ivjNameTextField = new javax.swing.JTextField();
                ivjNameTextField.setName("NameTextField");
                ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjNameTextField.setColumns(12);
                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                    PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }
    
    /**
     * Return the PhysicalAddressLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getPhysicalAddressLabel() {
        if (ivjPhysicalAddressLabel == null) {
            try {
                ivjPhysicalAddressLabel = new javax.swing.JLabel();
                ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
                ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPhysicalAddressLabel.setText("Address:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPhysicalAddressLabel;
    }
    
    /**
     * Return the PointCopyCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getPointCopyCheckBox() {
        if (ivjPointCopyCheckBox == null) {
            try {
                ivjPointCopyCheckBox = new javax.swing.JCheckBox();
                ivjPointCopyCheckBox.setName("PointCopyCheckBox");
                ivjPointCopyCheckBox.setText("Copy Points");
                ivjPointCopyCheckBox.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointCopyCheckBox;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension( 350, 200);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(Object val) {
        deviceBase = ((DeviceBase) val);
        int previousDeviceID = deviceBase.getDevice().getDeviceID().intValue();
        RouteBase newRoute = null;
    
        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);   
        deviceBase.setDeviceID(paoDao.getNextPaoId());
    
        boolean hasRoute = false;
        boolean hasPoints = false;
    
        String nameString = getNameTextField().getText();
        deviceBase.setPAOName(nameString);
    
        MultiDBPersistent objectsToAdd = new MultiDBPersistent();
        objectsToAdd.getDBPersistentVector().add(deviceBase);
    
        //Search for the correct sub-type and set the address
        if( getAddressTextField().isVisible() ) {
            
            if (val instanceof IDLCBase) {
                ((IDLCBase) val).getDeviceIDLCRemote().setAddress(new Integer(getAddressTextField().getText()));
            } else if (val instanceof DNPBase) {
                ((DNPBase) val).getDeviceAddress().setSlaveAddress(new Integer(getAddressTextField().getText()));
            } else if (val instanceof CarrierBase) {
                CarrierBase carrierBase = (CarrierBase)val;
                Integer addressHolder = new Integer(getAddressTextField().getText());
                if(val instanceof Repeater900) {
                      addressHolder = new Integer(addressHolder.intValue() + Repeater900.ADDRESS_OFFSET);
                } else if(val instanceof Repeater921) {
                     addressHolder = new Integer(addressHolder.intValue() + Repeater921.ADDRESS_OFFSET);
                }
                carrierBase.getDeviceCarrierSettings().setAddress(addressHolder);

                if( deviceBase.getPaoType().isMct()) {
                    checkMCTAddresses( new Integer(getAddressTextField().getText()).intValue() );
                    checkMeterNumber(getJTextFieldMeterNumber().getText());
                }
            } else if (val instanceof Series5Base) {
                 ((Series5Base) val).getSeries5().setSlaveAddress( new Integer(getAddressTextField().getText()) );
            } else if (val instanceof RTCBase) {
                ((RTCBase) val).getDeviceRTC().setRTCAddress( new Integer( getAddressTextField().getText()));
            } else if (val instanceof CCU721) {
                ((CCU721) val).getDeviceAddress().setSlaveAddress(new Integer(getAddressTextField().getText()));
            } else { //didn't find it
                throw new Error("Unable to determine device type when attempting to set the address");
            }
        }
    
        //Search for the correct sub-type and set the meter fields
        if( getJTextFieldMeterNumber().isVisible() ) {
            if( val instanceof MCTBase ) {
                 ((MCTBase ) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
            } else if( val instanceof IEDMeter ) {
                 ((IEDMeter) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
            } else if( val instanceof RfnMeterBase ) {
                ((RfnMeterBase) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
            } else { //didn't find it
                throw new Error("Unable to determine device type when attempting to set the Meter Number");
            }
        }
        
        if (deviceBase.getPaoType().getPaoClass() == PaoClass.TRANSMITTER){
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            synchronized (cache) {
                List<LiteYukonPAObject> routes = cache.getAllRoutes();
                DBPersistent oldRoute = null;
                PaoType paoType = null;
    
                for (LiteYukonPAObject route: routes) {
                    oldRoute = LiteFactory.createDBPersistent(route);
                    try {
                        Transaction<?> t = Transaction.createTransaction(Transaction.RETRIEVE, oldRoute);
                        t.execute();
                    } catch (Exception e) {
                        log.error( e.getMessage(), e );
                    }
                    
                    if (oldRoute instanceof RouteBase) {
                        if (((RouteBase) oldRoute).getDeviceID().intValue() == previousDeviceID) {
                            paoType = route.getPaoType();
                            newRoute = RouteFactory.createRoute(paoType);
    
                            hasRoute = true;
                            break;
                        }
                    }
                }
                
                if (hasRoute)  {
                    newRoute.setRouteID(paoDao.getNextPaoId());
                    newRoute.setRouteName(nameString);
                    newRoute.setDeviceID( ((RouteBase) oldRoute).getDeviceID() );
                    newRoute.setDefaultRoute( ((RouteBase) oldRoute).getDefaultRoute() );
                    newRoute.setDeviceID(deviceBase.getDevice().getDeviceID());
                    
                    if(paoType == PaoType.ROUTE_CCU) {
                        ((CCURoute) newRoute).setCarrierRoute(((CCURoute) oldRoute).getCarrierRoute());
                        ((CCURoute) newRoute).getCarrierRoute().setRouteID(newRoute.getRouteID());
                    }
    
                }
            }
    
        }
    
        if (getPointCopyCheckBox().isSelected()) {
            Vector<PointBase> devicePoints = new Vector();
            
            PointBase pointBase = null;
            List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(previousDeviceID);
            for (LitePoint point : points) {
                pointBase = (PointBase) LiteFactory.createDBPersistent(point);
                try {
                    Transaction<?> t = Transaction.createTransaction(Transaction.RETRIEVE, pointBase);
                    t.execute();
                    devicePoints.addElement(pointBase);
                } catch (TransactionException e) {
                    log.error( e.getMessage(), e );
                }
            }    
            
            //int startingPointID = DaoFactory.getPointDao().getNextPointId();
            Integer newDeviceID = deviceBase.getDevice().getDeviceID();

            for (PointBase devicePoint: devicePoints) {
                devicePoint.setPointID(YukonSpringHook.getBean(PointDao.class).getNextPointId());
                devicePoint.getPoint().setPaoID(newDeviceID);
                objectsToAdd.getDBPersistentVector().add(devicePoint);
            }
            hasPoints = true;                
        }
        
        //user can input new phone number; otherwise they may later control/scan the wrong device
        if( val instanceof RemoteBase) {
             if(getJTextFieldPhoneNumber().isVisible()) {
                  ((RemoteBase)val).getDeviceDialupSettings().setPhoneNumber(getJTextFieldPhoneNumber().getText());
             }
        }
        
        if(hasRoute) {
            objectsToAdd.getDBPersistentVector().add(newRoute);
        }
        return objectsToAdd;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
    
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        log.info("--------- UNCAUGHT EXCEPTION ---------");
        log.error( exception.getMessage(), exception );;
    }
    
    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getPointCopyCheckBox().addItemListener(ivjEventHandler);
        getNameTextField().addCaretListener(ivjEventHandler);
        getAddressTextField().addCaretListener(ivjEventHandler);
        getJTextFieldMeterNumber().addCaretListener(ivjEventHandler);
        getJTextFieldPhoneNumber().addCaretListener(ivjEventHandler);
    }
    
    
    private javax.swing.JLabel getJLabelErrorMessage()
    {
       if( jLabelErrorMessage == null )
       {
           jLabelErrorMessage = new javax.swing.JLabel();
           jLabelErrorMessage.setFont(new java.awt.Font("dialog.bold", 1, 10));
           jLabelErrorMessage.setBackground(Color.cyan);
          
           jLabelErrorMessage.setMaximumSize(new java.awt.Dimension(250, 60));
           jLabelErrorMessage.setPreferredSize(new java.awt.Dimension(220, 60));
           jLabelErrorMessage.setMinimumSize(new java.awt.Dimension(220, 60));            
       }
       
       return jLabelErrorMessage;
    }
    
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DeviceNameAddressPanel");
            setLayout(new java.awt.GridLayout());
            setSize(350, 200);
            add(getJPanelCopyDevice(), getJPanelCopyDevice().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        getJTextFieldPhoneNumber().setVisible(false);
        getJLabelPhoneNumber().setVisible(false);    
    }
    
    @Override
    public boolean isInputValid() {
        if( getNameTextField().getText() == null   ||
                getNameTextField().getText().length() < 1 )
        {
            setErrorString("The Name text field must be filled in");
            return false;
        }
    
        if (getAddressTextField().isVisible()) {
            if (getAddressTextField().getText() == null || getAddressTextField().getText().length() < 1) {
                setErrorString("The Address text field must be filled in");
                return false;
            }
            
            try {
                int address = Integer.parseInt(getAddressTextField().getText());
                PaoType paoType = deviceBase.getPaoType();
                if (!dlcAddressRangeService.isValidEnforcedAddress(paoType, address)) {
                    
                    String rangeString = dlcAddressRangeService.rangeStringEnforced(paoType);
                    setErrorString("Invalid address. Device address range: " + rangeString);
                    getJLabelErrorMessage().setText("(" + getErrorString() + ")");
                    getJLabelErrorMessage().setToolTipText("(" + getErrorString() + ")");
                    
                    return false;
                }
            } catch (NumberFormatException e) {
                // If this happens, we assume they know what they are
                // doing and we accept any string as input
            }
            
        }
        
        String deviceName = getNameTextField().getText();
        if( !isUniquePao(deviceName, deviceBase.getPaoType())) {
            setErrorString("Name '" + deviceName + "' is already in use.");
             getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
             getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
            return false;
        } 

        //No errors, clear out
           getJLabelErrorMessage().setText( "" );
           getJLabelErrorMessage().setToolTipText( "" );
        return true;
    }
    
    /**
     * Method to handle events for the ItemListener interface.
     * @param e java.awt.event.ItemEvent
     */
    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        if (e.getSource() == getPointCopyCheckBox()) 
            connEtoC3(e);
    }
    
    @Override
    public void setValue(Object val ) {
        deviceBase = (DeviceBase)val;
        PaoClass paoClass = deviceBase.getPaoType().getPaoClass();

        //handle all device Address fields
        boolean showAddress = (val instanceof IEDBase)
                 || (paoClass == PaoClass.GROUP)
                 || (paoClass == PaoClass.VIRTUAL)
                 || (paoClass == PaoClass.GRID)
                 || (paoClass == PaoClass.RFMESH);
    
        getAddressTextField().setVisible( !showAddress );
        getPhysicalAddressLabel().setVisible( !showAddress );
        
        //handle all meter fields
        boolean showMeterNumber = (val instanceof MCTBase) 
            || (val instanceof IEDMeter)
            || (val instanceof RfnMeterBase);
        
        getJTextFieldMeterNumber().setVisible( showMeterNumber );
        getJLabelMeterNumber().setVisible( showMeterNumber );
    
        setPanelState( deviceBase );
    
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized(cache) {
            List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceBase.getDevice().getDeviceID());
            if(points.size() > 0) {
                getPointCopyCheckBox().setEnabled(true);
                getPointCopyCheckBox().setSelected(true);                
            }
        }
    }

    private void setPanelState( DeviceBase val ) {
      Integer addressHolder;
      
      if( val instanceof CarrierBase )
      {
           addressHolder = new Integer(((CarrierBase)val).getDeviceCarrierSettings().getAddress().toString());
         if(val instanceof Repeater900)
             addressHolder = new Integer(addressHolder.intValue() - Repeater900.ADDRESS_OFFSET);
         else if(val instanceof Repeater921)
             addressHolder = new Integer(addressHolder.intValue() - Repeater921.ADDRESS_OFFSET);
         getAddressTextField().setText( addressHolder.toString() );
      }
      if( val instanceof IDLCBase )
         getAddressTextField().setText( ((IDLCBase)val).getDeviceIDLCRemote().getAddress().toString() );
   
         if( val instanceof DNPBase )
              getAddressTextField().setText( ((DNPBase)val).getDeviceAddress().getSlaveAddress().toString() );
   
      if( val instanceof MCTBase )
         getJTextFieldMeterNumber().setText( ((MCTBase)val).getDeviceMeterGroup().getMeterNumber().toString() );
      
      if( val instanceof RfnMeterBase )
          getJTextFieldMeterNumber().setText( ((RfnMeterBase)val).getDeviceMeterGroup().getMeterNumber().toString() );
      
      if( val instanceof IEDMeter )
         getJTextFieldMeterNumber().setText( ((IEDMeter)val).getDeviceMeterGroup().getMeterNumber().toString() );

      if( val instanceof IonBase )
      {
         getPhysicalAddressLabel().setText("Slave Address:");
         
         getAddressTextField().setText( 
            ((IonBase)val).getDeviceAddress().getSlaveAddress().toString() );            
      }
      
      if( val instanceof Series5Base )
      {
           getAddressTextField().setText( ((Series5Base)val).getSeries5().getSlaveAddress().toString());
      }
      
      if( val instanceof RTCBase )
      {
           getAddressTextField().setText( ((RTCBase)val).getDeviceRTC().getRTCAddress().toString());
      }
   
      //user can input new phone number; otherwise they may later control/scan the wrong device
      if( val instanceof RemoteBase)
        {
            if(((RemoteBase)val).hasPhoneNumber())
            {
                getJTextFieldPhoneNumber().setVisible(true);
                  getJLabelPhoneNumber().setVisible(true);
                  getJTextFieldPhoneNumber().setText(((RemoteBase)val).getDeviceDialupSettings().getPhoneNumber());
             }
            else
            {
                  getJTextFieldPhoneNumber().setVisible(false);
                  getJLabelPhoneNumber().setVisible(false);
            } 
        }
        
      if (val instanceof CCU721) {
          getPhysicalAddressLabel().setText("Slave Address:");
          getAddressTextField().setText(((CCU721)val).getDeviceAddress().getSlaveAddress().toString() );
      }
      
      getNameTextField().setText( val.getPAOName() + "(copy)" );      
   }
   
   /**
    * Helper method to check that an address for an mct is unique
    * @param address - Address to check
    */
    private void checkMCTAddresses(int address) {

        String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);
        if (devices.length > 0) {
            String devStr = new String();
            for (int i = 0; i < devices.length; i++) {
                devStr += "          " + devices[i] + "\n";
            }

            String message = "The address '"
                + address
                + "' is already used by the following devices,\n"
                + "are you sure you want to use it again?\n";
            
            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                            message,
                                                                            "Address Already Used",
                                                                            devices);
            if (res == JOptionPane.NO_OPTION) {
                throw new CancelInsertException("Device was not inserted");
            }
        }
    }
   
   /**
     * Helper method to check meternumber uniqueness
     * @param meterNumber - Meternumber to check
     */
   private void checkMeterNumber(String meterNumber) {
        List<String> devices = DeviceMeterGroup.checkMeterNumber(meterNumber, null);

        if (devices.size() > 0) {

            String message = "The meter number '"
                + meterNumber
                + "' is already used by the following devices,\n"
                + "are you sure you want to use it again?\n";

            int response = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                                 message,
                                                                                 "Meter Number Already Used",
                                                                                 devices);
            if (response == javax.swing.JOptionPane.NO_OPTION) {
                throw new CancelInsertException("Device was not inserted");
            }
        }
    }

   
}