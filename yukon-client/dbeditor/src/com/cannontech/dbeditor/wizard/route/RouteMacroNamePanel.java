package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;

/**
 * This type was created in VisualAge.
 */
public class RouteMacroNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjEnterLabel = null;
	private javax.swing.JTextField ivjMacroNameTextBox = null;
public RouteMacroNamePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
@Override
public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getMacroNameTextBox()) 
		connEtoC1(e);
}
/**
 * connEtoC1:  (MacroNameTextBox.caret.caretUpdate(javax.swing.event.CaretEvent) --> RouteMacroNamePanel.macroNameTextBox_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		this.macroNameTextBox_CaretUpdate(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the EnterLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getEnterLabel() {
	if (ivjEnterLabel == null) {
		try {
			ivjEnterLabel = new javax.swing.JLabel();
			ivjEnterLabel.setName("EnterLabel");
			ivjEnterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEnterLabel.setText("Route Macro Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEnterLabel;
}
/**
 * Return the MacroNameTextBox property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getMacroNameTextBox() {
	if (ivjMacroNameTextBox == null) {
		try {
			ivjMacroNameTextBox = new javax.swing.JTextField();
			ivjMacroNameTextBox.setName("MacroNameTextBox");
			ivjMacroNameTextBox.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMacroNameTextBox.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
			ivjMacroNameTextBox.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMacroNameTextBox;
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) 
{
	String macroName = getMacroNameTextBox().getText().trim();

	//Create a Macro Route object
	val = com.cannontech.database.data.route.RouteFactory.createRoute(PaoType.ROUTE_MACRO);
	((com.cannontech.database.data.route.RouteBase) val).setRouteName(macroName);
	((com.cannontech.database.data.route.RouteBase) val).setDeviceID( new Integer(
							com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) );
	
	((com.cannontech.database.data.route.RouteBase) val).setDefaultRoute("N");
	
	return val;
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
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getMacroNameTextBox().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("RouteMacroNamePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(367, 200);

		GridBagConstraints constraintsEnterLabel = new GridBagConstraints();
		constraintsEnterLabel.gridx = 0; constraintsEnterLabel.gridy = 0;
		constraintsEnterLabel.anchor = GridBagConstraints.WEST;
		constraintsEnterLabel.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getEnterLabel(), constraintsEnterLabel);

		GridBagConstraints constraintsMacroNameTextBox = new GridBagConstraints();
		constraintsMacroNameTextBox.gridx = 0; constraintsMacroNameTextBox.gridy = 1;
		constraintsMacroNameTextBox.anchor = GridBagConstraints.WEST;
		constraintsMacroNameTextBox.fill = GridBagConstraints.HORIZONTAL;
		constraintsMacroNameTextBox.weightx = 1.0;
		constraintsMacroNameTextBox.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getMacroNameTextBox(), constraintsMacroNameTextBox);
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
public boolean isInputValid() {
	if( getMacroNameTextBox().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}
	else
		return true;
		
}
/**
 * Comment
 */
public void macroNameTextBox_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {

	fireInputUpdate();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		RouteMacroNamePanel aRouteMacroNamePanel;
		aRouteMacroNamePanel = new RouteMacroNamePanel();
		frame.add("Center", aRouteMacroNamePanel);
		frame.setSize(aRouteMacroNamePanel.getSize());
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
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {
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
            getMacroNameTextBox().requestFocus();
        } 
    });    
}
}
