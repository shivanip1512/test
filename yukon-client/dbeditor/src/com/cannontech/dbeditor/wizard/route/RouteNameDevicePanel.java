package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.util.List;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class RouteNameDevicePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjRouteNameLabel = null;
	private javax.swing.JTextField ivjRouteNameTextField = null;
	private javax.swing.JLabel ivjSignalTransmitterLabel = null;
	private javax.swing.JComboBox<LiteYukonPAObject> ivjSignalTransmitterComboBox = null;
public RouteNameDevicePanel() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean allowRebroadcast() {

	if( getSignalTransmitterComboBox().getSelectedItem() == null )
		return false;
	else
		return DeviceTypesFuncs.allowRebroadcast( ((LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getPaoType().getDeviceTypeId());
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
@Override
public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getRouteNameTextField()) 
		connEtoC1(e);
}

/**
 * 
 * @param actionEvent
 */
public void comboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}

/**
 * connEtoC1:  (RouteNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> RouteType2Panel.textField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		this.textField_CaretUpdate(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (SignalTransmitterComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> RouteType2Panel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
	return new Dimension(350, 200 );
}

/**
 * Return the RouteNameLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRouteNameLabel() {
	if (ivjRouteNameLabel == null) {
		try {
			ivjRouteNameLabel = new javax.swing.JLabel();
			ivjRouteNameLabel.setName("RouteNameLabel");
			ivjRouteNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteNameLabel.setText("Route Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteNameLabel;
}

/**
 * Return the RouteNameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getRouteNameTextField() {
	if (ivjRouteNameTextField == null) {
		try {
			ivjRouteNameTextField = new javax.swing.JTextField();
			ivjRouteNameTextField.setName("RouteNameTextField");
			ivjRouteNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjRouteNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
	        ivjRouteNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteNameTextField;
}

/**
 * Return the SignalTransmitterComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox<LiteYukonPAObject> getSignalTransmitterComboBox() {
	if (ivjSignalTransmitterComboBox == null) {
		try {
			ivjSignalTransmitterComboBox = new javax.swing.JComboBox<LiteYukonPAObject>();
			ivjSignalTransmitterComboBox.setName("SignalTransmitterComboBox");
         
         IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
         synchronized(cache)
         {
            List<LiteYukonPAObject> allDevices = cache.getAllDevices();
            for (LiteYukonPAObject litePAO : allDevices) {
                        
               if( litePAO.getPaoType().getPaoClass() == PaoClass.TRANSMITTER
                   && !DeviceTypesFuncs.isRepeater(litePAO.getPaoType().getDeviceTypeId()) 
                   && litePAO.getPaoType() != PaoType.DIGIGATEWAY)
               {
                  getSignalTransmitterComboBox().addItem(litePAO);
               }

            }
            
         }
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterComboBox;
}
/**
 * Return the SignalTransmitterLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSignalTransmitterLabel() {
	if (ivjSignalTransmitterLabel == null) {
		try {
			ivjSignalTransmitterLabel = new javax.swing.JLabel();
			ivjSignalTransmitterLabel.setName("SignalTransmitterLabel");
			ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterLabel.setText("Signal Transmitter:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) {

	//Determine type of route and its name
	//create that type with the name and return it

	String routeName = getRouteNameTextField().getText().trim();

	Integer deviceID = new Integer(((LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getYukonID());

	PaoType paoType = ((LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getPaoType();

	if( DeviceTypesFuncs.isCCU(paoType.getDeviceTypeId()) || DeviceTypesFuncs.isRepeater(paoType.getDeviceTypeId()) )
	{
		val = RouteFactory.createRoute(PaoType.ROUTE_CCU);
	}
	else if( DeviceTypesFuncs.isTCU(paoType.getDeviceTypeId()) )
	{
		val = RouteFactory.createRoute(PaoType.ROUTE_TCU);
	}
	else if( DeviceTypesFuncs.isLCU(paoType.getDeviceTypeId()) )
	{
		val = RouteFactory.createRoute(PaoType.ROUTE_LCU);
	}
	else if( paoType == PaoType.TAPTERMINAL )
	{		
		val = RouteFactory.createRoute(PaoType.ROUTE_TAP_PAGING);
	}
	else if( paoType == PaoType.WCTP_TERMINAL )
	{		
		val = RouteFactory.createRoute(PaoType.ROUTE_WCTP_TERMINAL);
	}
	else if( paoType == PaoType.SNPP_TERMINAL )
	{		
		val = RouteFactory.createRoute(PaoType.ROUTE_SNPP_TERMINAL);
	}
	else if( paoType == PaoType.TNPP_TERMINAL )
	{
	    val = RouteFactory.createRoute(PaoType.ROUTE_TNPP_TERMINAL);
	}
	else if( paoType == PaoType.RTC )
	{
		val = RouteFactory.createRoute(PaoType.ROUTE_RTC);
	}
	else if( paoType == PaoType.SERIES_5_LMI )
	{
		val = RouteFactory.createRoute(PaoType.ROUTE_SERIES_5_LMI);
	}
	else if (paoType == PaoType.RDS_TERMINAL)
	{
	    val = RouteFactory.createRoute(PaoType.ROUTE_RDS_TERMINAL);
	}
	else //?
		throw new Error("RouteType2::getValue() - Unknown transmitter type");

	((RouteBase) val).setDeviceID(deviceID);	
	((RouteBase) val).setDefaultRoute("Y");

	((RouteBase) val).setRouteName( routeName );

	return val;
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

/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {
	getRouteNameTextField().addCaretListener(this);
	getSignalTransmitterComboBox().addItemListener(this);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("RouteType2Panel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 211);

		java.awt.GridBagConstraints constraintsRouteNameLabel = new java.awt.GridBagConstraints();
		constraintsRouteNameLabel.gridx = 0; constraintsRouteNameLabel.gridy = 0;
		constraintsRouteNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRouteNameLabel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getRouteNameLabel(), constraintsRouteNameLabel);

		java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
		constraintsRouteNameTextField.gridx = 0; constraintsRouteNameTextField.gridy = 1;
		constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRouteNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRouteNameTextField.weightx = 1.0;
		constraintsRouteNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getRouteNameTextField(), constraintsRouteNameTextField);

		java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
		constraintsSignalTransmitterLabel.gridx = 0; constraintsSignalTransmitterLabel.gridy = 2;
		constraintsSignalTransmitterLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSignalTransmitterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

		java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
		constraintsSignalTransmitterComboBox.gridx = 0; constraintsSignalTransmitterComboBox.gridy = 3;
		constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSignalTransmitterComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSignalTransmitterComboBox.weightx = 1.0;
		constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
@Override
public boolean isInputValid() 
{	
	if( getRouteNameTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
@Override
public void itemStateChanged(java.awt.event.ItemEvent e) {
	if (e.getSource() == getSignalTransmitterComboBox()) 
		connEtoC2(e);
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		RouteNameDevicePanel aRouteType2Panel;
		aRouteType2Panel = new RouteNameDevicePanel();
		frame.add("Center", aRouteType2Panel);
		frame.setSize(aRouteType2Panel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean noRepeaters() {
	boolean noRepeaters = true;

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		List<LiteYukonPAObject> devices = cache.getAllDevices();
		for (LiteYukonPAObject liteYukonPAObject : devices) {
			if(DeviceTypesFuncs.isRepeater(liteYukonPAObject.getPaoType().getDeviceTypeId()))
			{
				noRepeaters = false;
				break;
			}
		}
	}
	return noRepeaters;
}

@Override
public void setValue(Object val) 
{
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
            getRouteNameTextField().requestFocus(); 
        } 
    });    
}

/**
 * 
 * @param caretEvent
 */
public void textField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {

	fireInputUpdate();
}
}
