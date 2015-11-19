package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.point.SystemPoint;
import com.cannontech.spring.YukonSpringHook;

public class PointBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjBasePanel = null;
	private javax.swing.JLabel ivjDeviceNameLabel = null;
	private javax.swing.JLabel ivjLogicalGrpLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjPointNameTextField = null;
	private javax.swing.JLabel ivjPointNumberLabel = null;
	private javax.swing.JPanel ivjSpecificPanel = null;
	private javax.swing.JCheckBox ivjOutOfServiceCheckBox = null;
	private javax.swing.JComboBox ivjLogGroupComboBox = null;
	private DataInputPanel currentSpecificPanel = null;
	private javax.swing.JLabel ivjPointNumberActualLabel = null;
	private javax.swing.JLabel ivjDeviceNameActualLabel = null;
	private javax.swing.JLabel ivjPointTypeActualLabel = null;
	private javax.swing.JLabel ivjPointTypeLabel = null;
	
	private Logger log = YukonLogManager.getLogger(PointBaseEditorPanel.class);
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointBaseEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if (e.getSource() == getLogGroupComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getOutOfServiceCheckBox()) 
		connEtoC4(e);
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPointNameTextField()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PointNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (GroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (OutOfServiceCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the BasePanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getBasePanel() {
	if (ivjBasePanel == null) {
		try {
			ivjBasePanel = new javax.swing.JPanel();
			ivjBasePanel.setName("BasePanel");
			ivjBasePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsDeviceNameLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameLabel.gridx = 1; constraintsDeviceNameLabel.gridy = 5;
			constraintsDeviceNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getDeviceNameLabel(), constraintsDeviceNameLabel);

			java.awt.GridBagConstraints constraintsPointNameTextField = new java.awt.GridBagConstraints();
			constraintsPointNameTextField.gridx = 2; constraintsPointNameTextField.gridy = 1;
			constraintsPointNameTextField.gridwidth = 2;
			constraintsPointNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPointNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNameTextField.weightx = 1.0;
			constraintsPointNameTextField.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getPointNameTextField(), constraintsPointNameTextField);
			
			java.awt.GridBagConstraints constraintsDeviceNameActualLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameActualLabel.gridx = 2; constraintsDeviceNameActualLabel.gridy = 5;
			constraintsDeviceNameActualLabel.gridwidth = 2;
			constraintsDeviceNameActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameActualLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getDeviceNameActualLabel(), constraintsDeviceNameActualLabel);

			java.awt.GridBagConstraints constraintsGroupLabel = new java.awt.GridBagConstraints();
			constraintsGroupLabel.gridx = 1; constraintsGroupLabel.gridy = 2;
			constraintsGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getLogicalGrpLabel(), constraintsGroupLabel);

			java.awt.GridBagConstraints constraintsGroupComboBox = new java.awt.GridBagConstraints();
			constraintsGroupComboBox.gridx = 2; constraintsGroupComboBox.gridy = 2;
			constraintsGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupComboBox.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getLogGroupComboBox(), constraintsGroupComboBox);

			java.awt.GridBagConstraints constraintsPointNumberLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberLabel.gridx = 1; constraintsPointNumberLabel.gridy = 3;
			constraintsPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getPointNumberLabel(), constraintsPointNumberLabel);

			java.awt.GridBagConstraints constraintsPointNumberActualLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberActualLabel.gridx = 2; constraintsPointNumberActualLabel.gridy = 3;
			constraintsPointNumberActualLabel.gridwidth = 2;
			constraintsPointNumberActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberActualLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getPointNumberActualLabel(), constraintsPointNumberActualLabel);

			java.awt.GridBagConstraints constraintsPointTypeLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeLabel.gridx = 1; constraintsPointTypeLabel.gridy = 4;
			constraintsPointTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getPointTypeLabel(), constraintsPointTypeLabel);

			java.awt.GridBagConstraints constraintsPointTypeActualLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeActualLabel.gridx = 2; constraintsPointTypeActualLabel.gridy = 4;
			constraintsPointTypeActualLabel.gridwidth = 2;
			constraintsPointTypeActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeActualLabel.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getPointTypeActualLabel(), constraintsPointTypeActualLabel);

			java.awt.GridBagConstraints constraintsOutOfServiceCheckBox = new java.awt.GridBagConstraints();
			constraintsOutOfServiceCheckBox.gridx = 3; constraintsOutOfServiceCheckBox.gridy = 2;
			constraintsOutOfServiceCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsOutOfServiceCheckBox.insets = new java.awt.Insets(2,2,2,2);
			getBasePanel().add(getOutOfServiceCheckBox(), constraintsOutOfServiceCheckBox);

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjBasePanel.setBorder(ivjLocalBorder);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBasePanel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDeviceNameActualLabel() {
	if (ivjDeviceNameActualLabel == null) {
		try {
			ivjDeviceNameActualLabel = new javax.swing.JLabel();
			ivjDeviceNameActualLabel.setName("DeviceNameActualLabel");
			ivjDeviceNameActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjDeviceNameActualLabel.setText("(Unknown Device)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceNameActualLabel;
}
/**
 * Return the DeviceNameLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDeviceNameLabel() {
	if (ivjDeviceNameLabel == null) {
		try {
			ivjDeviceNameLabel = new javax.swing.JLabel();
			ivjDeviceNameLabel.setName("DeviceNameLabel");
			ivjDeviceNameLabel.setText("Assigned Device:");
			ivjDeviceNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceNameLabel;
}
/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getLogGroupComboBox() 
{
	if (ivjLogGroupComboBox == null) 	
	{
		try 
		{
			ivjLogGroupComboBox = new javax.swing.JComboBox();
			ivjLogGroupComboBox.setName("LogGroupComboBox");
			ivjLogGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLogGroupComboBox.setEditable(false);

            for (PointLogicalGroups group : PointLogicalGroups.values()) {
                ivjLogGroupComboBox.addItem(group.getDbValue());
            }

		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjLogGroupComboBox;
}

/**
 * Return the GroupLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLogicalGrpLabel() 
{
	if (ivjLogicalGrpLabel == null) 
	{
		try 
		{
			ivjLogicalGrpLabel = new javax.swing.JLabel();
			ivjLogicalGrpLabel.setName("LogGroupLabel");
			ivjLogicalGrpLabel.setText("Timing Group:");
			ivjLogicalGrpLabel.setFont(new java.awt.Font("dialog", 0, 14));

		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjLogicalGrpLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
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
			ivjNameLabel.setText("Point Name:");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the OutOfServiceCheckBox property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getOutOfServiceCheckBox() {
	if (ivjOutOfServiceCheckBox == null) {
		try {
			ivjOutOfServiceCheckBox = new javax.swing.JCheckBox();
			ivjOutOfServiceCheckBox.setName("OutOfServiceCheckBox");
			ivjOutOfServiceCheckBox.setActionCommand("Out of Service");
			ivjOutOfServiceCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjOutOfServiceCheckBox.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjOutOfServiceCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));
			ivjOutOfServiceCheckBox.setText("Disable Point");
			ivjOutOfServiceCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjOutOfServiceCheckBox.setBorderPainted(false);
			ivjOutOfServiceCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOutOfServiceCheckBox.setContentAreaFilled(true);
			ivjOutOfServiceCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutOfServiceCheckBox;
}

/**
 * Return the PointNameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getPointNameTextField() {
	if (ivjPointNameTextField == null) {
		try {
			ivjPointNameTextField = new javax.swing.JTextField();
			ivjPointNameTextField.setName("PointNameTextField");
			ivjPointNameTextField.setColumns(12);
			ivjPointNameTextField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_POINT_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointNameTextField;
}
/**
 * Return the PointNumberActualLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPointNumberActualLabel() {
	if (ivjPointNumberActualLabel == null) {
		try {
			ivjPointNumberActualLabel = new javax.swing.JLabel();
			ivjPointNumberActualLabel.setName("PointNumberActualLabel");
			ivjPointNumberActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjPointNumberActualLabel.setText("(Unknown)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointNumberActualLabel;
}

/**
 * Return the PointNumberLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPointNumberLabel() {
	if (ivjPointNumberLabel == null) {
		try {
			ivjPointNumberLabel = new javax.swing.JLabel();
			ivjPointNumberLabel.setName("PointNumberLabel");
			ivjPointNumberLabel.setText("Point ID:");
			ivjPointNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointNumberLabel;
}
/**
 * Return the PointTypeActualLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPointTypeActualLabel() {
	if (ivjPointTypeActualLabel == null) {
		try {
			ivjPointTypeActualLabel = new javax.swing.JLabel();
			ivjPointTypeActualLabel.setName("PointTypeActualLabel");
			ivjPointTypeActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjPointTypeActualLabel.setText("(Unknown)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointTypeActualLabel;
}

/**
 * Return the PointTypeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPointTypeLabel() {
	if (ivjPointTypeLabel == null) {
		try {
			ivjPointTypeLabel = new javax.swing.JLabel();
			ivjPointTypeLabel.setName("PointTypeLabel");
			ivjPointTypeLabel.setText("Point Type:");
			ivjPointTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPointTypeLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 350 );
}
/**
 * Return the SpecificPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSpecificPanel() {
	if (ivjSpecificPanel == null) {
		try {
			ivjSpecificPanel = new javax.swing.JPanel();
			ivjSpecificPanel.setName("SpecificPanel");
			ivjSpecificPanel.setToolTipText("");
			ivjSpecificPanel.setLayout(new java.awt.BorderLayout());
			ivjSpecificPanel.setAlignmentY(0.5F);
			ivjSpecificPanel.setAlignmentX(0.5F);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSpecificPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Consider commonObject as a com.cannontech.database.data.point.PointBase
	PointBase point = (PointBase) val;

	point.getPoint().setPointName( getPointNameTextField().getText() );

	point.getPoint().setLogicalGroup( 
			getLogGroupComboBox().getSelectedItem().toString() );

	point.getPoint().setServiceFlag( new Character(
		(getOutOfServiceCheckBox().isSelected() ? 'Y' : 'N') ) );


	//Get the value out of whatever panel happens to be
	//the 'specific panel' - was set in setDefaultValue
	try {
        this.currentSpecificPanel.getValue(val);
    } catch (EditorInputValidationException e) {
        log.error( e.getMessage(), e );
    }

	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception 
{
	getPointNameTextField().addCaretListener(this);
	getLogGroupComboBox().addActionListener(this);
	getOutOfServiceCheckBox().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("PointBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsBasePanel = new java.awt.GridBagConstraints();
		constraintsBasePanel.gridx = 0; constraintsBasePanel.gridy = 0;
		constraintsBasePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsBasePanel.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getBasePanel(), constraintsBasePanel);

		java.awt.GridBagConstraints constraintsSpecificPanel = new java.awt.GridBagConstraints();
		constraintsSpecificPanel.gridx = 0; constraintsSpecificPanel.gridy = 1;
		constraintsSpecificPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSpecificPanel.weightx = 1.0;
		constraintsSpecificPanel.weighty = 1.0;
		add(getSpecificPanel(), constraintsSpecificPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * This method was created in VisualAge.
 * @param event PropertyPanelEvent
 */
public void inputUpdate(PropertyPanelEvent event) {
	//Just pass it on to our listeners
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{

	if( getPointNameTextField().getText() == null
		 || getPointNameTextField().getText().length() <= 0
		 || getPointNameTextField().getText().charAt(0) == ' ' )
	{
		setErrorString("The Point Name text field must be filled in");
		return false;
	}


	//be sure we check the Specific panel for the right input values!!!
	if( !((DataInputPanel)this.currentSpecificPanel).isInputValid() )
	{
		setErrorString( ((DataInputPanel)this.currentSpecificPanel).getErrorString() );
		return false;
	}
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PointBaseEditorPanel aPointBaseEditorPanel;
		aPointBaseEditorPanel = new PointBaseEditorPanel();
		frame.add("Center", aPointBaseEditorPanel);
		frame.setSize(aPointBaseEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	//Consider defaultObject a com.cannontech.database.data.point.PointBase
	PointBase point = (PointBase) val;

	Integer pointID = point.getPoint().getPointID();
	String name = point.getPoint().getPointName();
	String group = point.getPoint().getLogicalGroup();
	
	int pointDeviceID = point.getPoint().getPaoID().intValue();

	getPointNumberActualLabel().setText( "# " + pointID );
	getPointNameTextField().setText(name);
	getOutOfServiceCheckBox().setSelected( 
				(point.getPoint().getServiceFlag().toString().equalsIgnoreCase("Y") ? true : false) );
	
	if( PointLogicalGroups.isValidLogicalGroup(group) )
		getLogGroupComboBox().setSelectedItem( group );
		//CtiUtilities.setSelectedInComboBox( getLogGroupComboBox(), group );
	else
		getLogGroupComboBox().setSelectedItem(PointLogicalGroups.DEFAULT.getDbValue());



	//Load the device
	LiteYukonPAObject litePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO( pointDeviceID );
	getDeviceNameActualLabel().setText( litePAO.getPaoName() );


	getPointTypeActualLabel().setText( point.getPoint().getPointType() );
	
	//Depending on type change the specific panel to suite
	
	DataInputPanel sp = null;
	
	if( val instanceof CalcStatusPoint )
	{
		sp = new CalcStatusBasePanel();
	}
	else
	if( val instanceof StatusPoint )
	{
		sp = new PointStatusBasePanel();
	}
	else
	if( val instanceof AccumulatorPoint )
	{
		sp = new AccumulatorBasePanel();
	}
	else
	if( val instanceof AnalogPoint )
	{
		sp = new AnalogBasePanel();
	}
	else
	if( val instanceof CalculatedPoint )
	{
		sp = new CalcBasePanel();
	}
	else if (val instanceof SystemPoint) {
	    sp = new SystemPointBasePanel();
	}
	
	
	if( sp != null )
	{
		sp.addDataInputPanelListener(this);
		sp.setValue(val);
		
		java.awt.GridBagConstraints g = null;
		
		if( getLayout() instanceof java.awt.GridBagLayout )
			g = ((java.awt.GridBagLayout)getLayout()).getConstraints(getSpecificPanel());
		else
			throw new IllegalArgumentException( "***" + this.getClass().getName() + " does not have a GridBagLayout!!!");
			
		//just use the SpecificPanel object as a place and GridBagConstraint holder
		remove( getSpecificPanel() );
		this.currentSpecificPanel = sp;
		add( sp, g );
		
	}

}
}
