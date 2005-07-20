package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.util.CtiUtilities;

public class AnalogBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JComboBox ivjArchiveIntervalComboBox = null;
	private javax.swing.JLabel ivjArchiveIntervalLabel = null;
	private javax.swing.JComboBox ivjArchiveTypeComboBox = null;
	private javax.swing.JLabel ivjArchiveTypeLabel = null;
	private javax.swing.JLabel ivjJLabelDecimalPositons = null;
	private com.klg.jclass.field.JCSpinField ivjDecimalPlacesSpinner = null;
	private javax.swing.JPanel ivjJPanelArchive = null;
	private javax.swing.JPanel ivjJPanelHolder = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public AnalogBasePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getArchiveTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getUnitOfMeasureComboBox()) 
		connEtoC4(e);
	if (e.getSource() == getArchiveIntervalComboBox()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireInputUpdate();

	if( ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("None") ||
			((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Change") ||
			((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Update") )
	{
		getArchiveIntervalLabel().setEnabled(false);
		getArchiveIntervalComboBox().setEnabled(false);
	}
	else
	{
		getArchiveIntervalLabel().setEnabled(true);
		getArchiveIntervalComboBox().setEnabled(true);
		getArchiveIntervalComboBox().setSelectedItem("5 minute");
		
		if("On Timer Or Update".equalsIgnoreCase(((String)getArchiveTypeComboBox().getSelectedItem())))
			getArchiveIntervalComboBox().setSelectedItem("Daily");
	}

	return;
}
/**
 * connEtoC1:  (UnitOfMeasureComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.archiveTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (ArchiveIntervalComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Return the ArchiveIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getArchiveIntervalComboBox() {
	if (ivjArchiveIntervalComboBox == null) {
		try {
			ivjArchiveIntervalComboBox = new javax.swing.JComboBox();
			ivjArchiveIntervalComboBox.setName("ArchiveIntervalComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalComboBox;
}
/**
 * Return the ArchiveIntervalLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getArchiveIntervalLabel() {
	if (ivjArchiveIntervalLabel == null) {
		try {
			ivjArchiveIntervalLabel = new javax.swing.JLabel();
			ivjArchiveIntervalLabel.setName("ArchiveIntervalLabel");
			ivjArchiveIntervalLabel.setText("Interval:");
			ivjArchiveIntervalLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveIntervalLabel.setMinimumSize(new java.awt.Dimension(78, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalLabel;
}
/**
 * Return the ArchiveTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getArchiveTypeComboBox() {
	if (ivjArchiveTypeComboBox == null) {
		try {
			ivjArchiveTypeComboBox = new javax.swing.JComboBox();
			ivjArchiveTypeComboBox.setName("ArchiveTypeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeComboBox;
}
/**
 * Return the ArchiveTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getArchiveTypeLabel() {
	if (ivjArchiveTypeLabel == null) {
		try {
			ivjArchiveTypeLabel = new javax.swing.JLabel();
			ivjArchiveTypeLabel.setName("ArchiveTypeLabel");
			ivjArchiveTypeLabel.setText("Data Type:");
			ivjArchiveTypeLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveTypeLabel.setMinimumSize(new java.awt.Dimension(78, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeLabel;
}
/**
 * Return the LogFrequencyHourSpinner1 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDecimalPlacesSpinner() {
	if (ivjDecimalPlacesSpinner == null) {
		try {
			ivjDecimalPlacesSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDecimalPlacesSpinner.setName("DecimalPlacesSpinner");
			ivjDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjDecimalPlacesSpinner.setBackground(java.awt.Color.white);
			ivjDecimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjDecimalPlacesSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(20,22));
			ivjDecimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(20,22));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDecimalPlacesSpinner;
}
/**
 * Return the JLabelDecimalPositons property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDecimalPositons() {
	if (ivjJLabelDecimalPositons == null) {
		try {
			ivjJLabelDecimalPositons = new javax.swing.JLabel();
			ivjJLabelDecimalPositons.setName("JLabelDecimalPositons");
			ivjJLabelDecimalPositons.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDecimalPositons.setText("Decimal Digits:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDecimalPositons;
}
/**
 * Return the JPanelArchive property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelArchive() {
	if (ivjJPanelArchive == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Archive");
			ivjJPanelArchive = new javax.swing.JPanel();
			ivjJPanelArchive.setName("JPanelArchive");
			ivjJPanelArchive.setBorder(ivjLocalBorder);
			ivjJPanelArchive.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
			constraintsArchiveTypeLabel.gridx = 1; constraintsArchiveTypeLabel.gridy = 1;
			constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeLabel.ipadx = -5;
			constraintsArchiveTypeLabel.insets = new java.awt.Insets(3, 14, 6, 1);
			getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalLabel.gridx = 1; constraintsArchiveIntervalLabel.gridy = 2;
			constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalLabel.ipadx = -5;
			constraintsArchiveIntervalLabel.insets = new java.awt.Insets(5, 14, 10, 1);
			getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalComboBox.gridx = 2; constraintsArchiveIntervalComboBox.gridy = 2;
			constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalComboBox.weightx = 1.0;
			constraintsArchiveIntervalComboBox.ipadx = 10;
			constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 6, 8);
			getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

			java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveTypeComboBox.gridx = 2; constraintsArchiveTypeComboBox.gridy = 1;
			constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeComboBox.weightx = 1.0;
			constraintsArchiveTypeComboBox.ipadx = 10;
			constraintsArchiveTypeComboBox.insets = new java.awt.Insets(0, 2, 2, 8);
			getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelArchive;
}
/**
 * Return the JPanelHolder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHolder() {
	if (ivjJPanelHolder == null) {
		try {
			ivjJPanelHolder = new javax.swing.JPanel();
			ivjJPanelHolder.setName("JPanelHolder");
			ivjJPanelHolder.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureLabel.gridx = 0; constraintsUnitOfMeasureLabel.gridy = 0;
			constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(5, 0, 5, 2);
			getJPanelHolder().add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

			java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureComboBox.gridx = 1; constraintsUnitOfMeasureComboBox.gridy = 0;
			constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUnitOfMeasureComboBox.weightx = 1.0;
			constraintsUnitOfMeasureComboBox.ipadx = 162;
			constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(1, 2, 1, 21);
			getJPanelHolder().add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

			java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
			constraintsJLabelDecimalPositons.gridx = 0; constraintsJLabelDecimalPositons.gridy = 1;
			constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDecimalPositons.insets = new java.awt.Insets(3, 0, 7, 12);
			getJPanelHolder().add(getJLabelDecimalPositons(), constraintsJLabelDecimalPositons);

			java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
			constraintsDecimalPlacesSpinner.gridx = 1; constraintsDecimalPlacesSpinner.gridy = 1;
			constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDecimalPlacesSpinner.ipadx = 20;
			constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(2, 2, 5, 100);
			getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHolder;
}
/**
 * Return the UnitOfMeasureComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setPreferredSize(new java.awt.Dimension(126, 24));
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMinimumSize(new java.awt.Dimension(90, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setText("Unit of Measure:");
			ivjUnitOfMeasureLabel.setMaximumSize(new java.awt.Dimension(103, 16));
			ivjUnitOfMeasureLabel.setPreferredSize(new java.awt.Dimension(103, 16));
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setMinimumSize(new java.awt.Dimension(103, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Assume that commonObject is an instance of com.cannontech.database.data.point.AnalogPoint
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

	int uOfMeasureID = ((com.cannontech.database.data.lite.LiteUnitMeasure)getUnitOfMeasureComboBox().getSelectedItem()).getUomID();
	
	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
	if(getArchiveTypeComboBox().getSelectedItem().toString().compareTo("On Timer Or Update") == 0)
		point.getPoint().setArchiveType("time|update");
	else
		point.getPoint().setArchiveType((String)getArchiveTypeComboBox().getSelectedItem());
	point.getPoint().setArchiveInterval(CtiUtilities.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));
	
	point.getPointUnit().setDecimalPlaces( new Integer(((Number)getDecimalPlacesSpinner().getValue()).intValue() ) );

	return point;
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

	getDecimalPlacesSpinner().addValueListener(this);
	
	// user code end
	getArchiveTypeComboBox().addActionListener(this);
	getUnitOfMeasureComboBox().addActionListener(this);
	getArchiveIntervalComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AnalogBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(386, 177);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
		constraintsJPanelArchive.gridx = 1; constraintsJPanelArchive.gridy = 2;
		constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelArchive.weightx = 1.0;
		constraintsJPanelArchive.weighty = 1.0;
		constraintsJPanelArchive.ipadx = 136;
		constraintsJPanelArchive.ipady = 8;
		constraintsJPanelArchive.insets = new java.awt.Insets(6, 4, 18, 2);
		add(getJPanelArchive(), constraintsJPanelArchive);

		java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
		constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 1;
		constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHolder.weightx = 1.0;
		constraintsJPanelHolder.weighty = 1.0;
		constraintsJPanelHolder.insets = new java.awt.Insets(2, 4, 6, 2);
		add(getJPanelHolder(), constraintsJPanelHolder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//Put a border around the Analog section of panel
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Analog Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);

	//Load the unit of measure combo box with default possible values
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allUnitMeasures = cache.getAllUnitMeasures();
		for(int i=0; i<allUnitMeasures.size(); i++)
      {
			getUnitOfMeasureComboBox().addItem( allUnitMeasures.get(i) );         
      }

	}

	//Load the Archive Type combo box with default possible values
	getArchiveTypeComboBox().addItem("None");
	getArchiveTypeComboBox().addItem("On Change");
	getArchiveTypeComboBox().addItem("On Timer");
	getArchiveTypeComboBox().addItem("On Update");
	getArchiveTypeComboBox().addItem("On Timer Or Update");

	//Load the Archive Interval combo box with default possible values
	getArchiveIntervalComboBox().addItem("1 second");
	getArchiveIntervalComboBox().addItem("2 second");
	getArchiveIntervalComboBox().addItem("5 second");
	getArchiveIntervalComboBox().addItem("10 second");
	getArchiveIntervalComboBox().addItem("15 second");
	getArchiveIntervalComboBox().addItem("30 second");
	getArchiveIntervalComboBox().addItem("1 minute");
	getArchiveIntervalComboBox().addItem("2 minute");
	getArchiveIntervalComboBox().addItem("3 minute");
	getArchiveIntervalComboBox().addItem("5 minute");
	getArchiveIntervalComboBox().addItem("10 minute");
	getArchiveIntervalComboBox().addItem("15 minute");
	getArchiveIntervalComboBox().addItem("30 minute");
	getArchiveIntervalComboBox().addItem("1 hour");
	getArchiveIntervalComboBox().addItem("2 hour");
	getArchiveIntervalComboBox().addItem("6 hour");
	getArchiveIntervalComboBox().addItem("12 hour");
	//getArchiveIntervalComboBox().addItem("1 day");
	getArchiveIntervalComboBox().addItem("Daily");
	getArchiveIntervalComboBox().addItem("Weekly");
	getArchiveIntervalComboBox().addItem("Monthly");
	getArchiveIntervalComboBox().setSelectedItem("5 minute");
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{	
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
		AnalogBasePanel aAnalogBasePanel;
		aAnalogBasePanel = new AnalogBasePanel();
		frame.add("Center", aAnalogBasePanel);
		frame.setSize(aAnalogBasePanel.getSize());
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
public void setValue(Object val) {
	//Assume that defaultObject is an AnalogPoint
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

	int uOfMeasureID = point.getPointUnit().getUomID().intValue();
	String archiveType = point.getPoint().getArchiveType();
	if(archiveType.compareTo("time|update") == 0)
		archiveType = "On Timer Or Update";
	Integer archiveInteger = point.getPoint().getArchiveInterval();
	

	getDecimalPlacesSpinner().setValue( point.getPointUnit().getDecimalPlaces() );
	getArchiveIntervalLabel().setEnabled(false);
	getArchiveIntervalComboBox().setEnabled(false);
	for(int i=0;i<getUnitOfMeasureComboBox().getModel().getSize();i++)
	{
		if( ((com.cannontech.database.data.lite.LiteUnitMeasure)getUnitOfMeasureComboBox().getItemAt(i)).getUomID()
			 == uOfMeasureID )
		{
			getUnitOfMeasureComboBox().setSelectedIndex(i);
			break;
		}
	}

	for(int i=0;i<getArchiveTypeComboBox().getModel().getSize();i++)
	{
		if( ((String)getArchiveTypeComboBox().getItemAt(i)).equalsIgnoreCase(archiveType) )
		{
			getArchiveTypeComboBox().setSelectedIndex(i);
			if( getArchiveIntervalComboBox().isEnabled() )
				CtiUtilities.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(),archiveInteger.intValue());
			break;
		}
	}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getDecimalPlacesSpinner()) 
		this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5BF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715EA08B111C4CCC8C20C29A4E2EBF679225D1A6F338D5BE5DB72B5E96C46ECEDC39B37CBF7EDEADB535A2D5B3506ED9E432F469F149F91D1A7A0C6DB0A40A8A2E284E6A0E0A6CA82AAD61BB0B08F98FD4C9B5FBC18415FBD675EF76F1B99E6785BF53F72FD47776EB9775CFB6EB9671EFB6E3DEF94F277C5272798F285A1A5DD507E0E2588026B34A0AC7EF25D6B8C37D0CC4890FC7E5E83
	58AE8C9952603994E8750B92B232055D6DD9D0DE86657DDFCC4858896FB9C20AE42D0717907D6C87DA31796D392377734D8256CFA6A96FEC5C8C4F2DG4B81074F22DE6C7FF0E30106DF276199249884A11954B95DD824613A206C85D88A302C1756DF854F0D2467FBEBEB34FA17E7C78B6B3E7D3EE18B366353A683CB9F656D571EC6E1D1CAF6BF7665A6F2A2CFB4509281F07029C24F76CAF85635DD6D7B20C79EA86F31B91D32D395FB7A4BC53BC31515F2176CF02A35358DCE1BA477B55A5CE21B4DA9CA4093
	CE5AFD21B10B34A3889DD0AEF0A06EEC9576359C4A2B81848743671B90FF995ED500F0879B5F49776BB479BBEFAC9212DFF726EE8BB13E98879D57B207BE3E48418D193B08ADD28BD167D9D792B24AG6A816CGE1GB1G3B098FEC5A7587F8B6F42BE6F9E0C0F6B638DC0D12DD79D8360B923C43B83BD58D772958A3ABF6C120F69EEEAC8BA24FE8C0796E1BB09F5549C29333F96C27DF94D6FE65A3C3E6901D2CDCD8E1083BC2F531620A2E935AF7FC483E57DD7177C97F3EAF883EFD2F19AE27268769FB4977
	8E99322EEABEFFD56F5BFA95ED7DCD6283F5701E22798745FF0760A3FC7054BF4F91BC16230116A6B15F685C4AE431264D9132AB2DC17590750D86C33FC4E518A2854A72G713BD809761949E5F90C6093FC70D416A2024772CA20558FA4E4D07F5ADC0473B5864A71G09GEB81321D60EFG75CE66E36D3B8E0F53475AEC0A5AB868106C22A2D05E4D057970943D2AE2EBF53AD51B33C7BCEFD39C36EEC9F48BDA1D3CC2142D5B0F2F1078FE8F7471216894951B6A101D503AEDC0D4C545CD6B1494EE47BA0AC8
	289D0E8191BA98F0892C5DBF966640D3323955F3AE3BCD95759A14EEAAC41B8C594EC30B50G3CD33D9C5F037A6A0271FB81E6F0BDBCC75A7BC8D4A0E05456DAE5796A202B83C7A2A4BAD14F2F1358B1975E17B8190E0F6EC5DC111C10F1C4E66D7C24F007CFBB96C78F0AEFD306599CFB398AE3F20B4CFC66171D4CE7ACDB9688E55FBB9D54E7906CB275151BF2605C79AEE993652265CBD51DE34C5B96B9781C9AFF2CD0476CA3F5126DEF65361A3F8FED9583324683A4F19D9BF79B294FE208E7DEFAD6818793
	6D3A46291C91A63616E0F9A317FFAE412F07B60D2E040C969753DD3129B3C07F0CEF32E399293B93248FDF7D24EB347F0BBE280E720BF0DD9207C427EA91DD22538EAFEE0D36278847234D5BDE197314903C8B663999A6161B638F9561DC34292A62689ED445F6D1D59D4EBE1DDEC97AF28FF63B7490417A2AA94231499E18673C7E49A21CA3BD3253A992E3F1960DFE0A4CD1D19287C0E833BC68D4A93EE98F7A79D5F1D80B912DFBB0699932C90398EFE80E305876AE0EC1F18C008BE04892D638F07E7E1267B8
	88AE40E12E33901EF1AEB697BDA5ECAE6E3F7E34907FF929A158DA923540E2A83AC667CF45B5FDAED219EA7786173D1E448A8D6F2632EE3B08FA8B4CD6286F2FB4E2BBED503E9D6066B5667BF1FB114E620B6674E6D91228AA390E1F5D170DB6912F8D0A9016B110FD447536F85F0EG5AF9CD07143EF49F6AD0720901179554612BA61683C602BC31G4B2ED1BDEED6289EE83DDD352C5EC2A847ABFA3DA43FFA5F617584B764DD6E1175281EB2BF475CA0D85E57B768A89FD49D98E9EDAAB833EBD0A5CA2479DE
	27B9CB1B3BB5EE1657BDE2C63D3A20BC83A05A4DEC7F5A961657C5DE0D96126697064CDB173A29ECAB5D3A6D0D77303C6EC0B1D993D4D8E7D5E6B31351F7EDE9F03B659E87D956B4FD7F1B5157B7A86E2746CD706CF5A8EE3595A62F175966ADA34A0B6B1E0656720075A41E213E6A21DFAFC076A06273BC39BE32AF86FCA2C0B2C0AAE96FA7472BF0EE9367A9270FB6D16915158151FE700AED48D6EE732865A40E14B790F2933E9FE2367974DCA372ABBD7D0EA131E358A5C29E51ADB74A5E4F9B829A3306EE6D
	442173AC1FFBA627DE13F559F3F3043ABBB906E079750CEDFB8C1D03DA8EB5285B61665FE5242E796C6DCC2A1346DA2613E5D0A7ED0869E4D1F520CE6A0056B54476998907F6D05D0B4423E802F190E5246D7E16372B82CFACEFF76951E03ADEB54456163F18D90E3BFE084A3C59232F43C6F8BF6271DF17E96E17E9417EDABCB477CB67789D84FF5987CF65FB424C64F381ED050765AB4F17E08C2C71A6E4F4GC8GE1GB1G89DE662B036F1904B17241D618FB8A842DF63F004F7232E274AB0851DA79F0B14A
	C1E23440563F30630139FE1257FF5C4981E54A7770883E3D81E56330BF9F35E71F341B458F74C1EF395B836BD7ED6DE9662B5A9AF15A623BC6548C53DC6FB2BCDDGB426D8BDEC8FB9E318450EDB4D3E31A3EEDB15615D9031A3DE13AFE9D87759B23EA77C3C94632171BA78A3C087008BA092E0B1400A6B4CC6DFADBEB9160D48346A104D00EB01B8EE8EDCEB39CEDBEAA7E70B73A3784A834A6B2E7B73D19DBFF1B4180EB75F00BC6C8653711B55BCEE5FE0F3A7F912F2EEBAB1161C2E9B7EFCD406DF9D65F686
	BA5D8328E4BD090772CA0E5BCBF0EB211CF613615E36A06EA0146BB92E2899F1F6A85F3C49463405540B00F2AC479590DC229613A8EE1EB9B057467A134D8FA3EE8653535291FD24CF327D2D42485C1F7A6D57CBC26724AF16844FC9DFA9891E133ED6321D62218D5F1DA9237F20E4741C7547A541F256BDC47617733711365DEE51CDA692A6979A6D27EA1E90A4F7BD831BD7ED5CB9428B47B7EAF51AD459DCA930B8741BC3687BC16210CF4EB20D589FED12F01365AC9BBE46F81E8C655CDB10D75CE2FE7109E7
	34739A4B2D11BEC2634C1B87F01D6A843A8A908E908310F00B45974F7EB15700A66FA8EB0023B4341DDD25416DBCD40A7916DB9ED4FA44C0DB5EAAAD60B4BD12352A62G2A1D5619CE78D51B52A70E70058789BF250562FF2CB4143FD01B5E29753569919EA7DEA3F1826D3B6A963357FA8F5A2B68B62C67G565B4CDE3F28E67632834EFBBBA06EC0B9BAGC77BB61D706D7BA81F336B0E0EE67FA537C75A1F36B7530C3AF2A9E22F28406E215DF19DFDD73803324710F87692702E384DF24423E65CE7C936E1F9
	D0053C06547B9BB30B6396A8DF868883083AC363B97577AFFBF09D6CD5EC83204AF61758A304CFA1F9CB9363DD8F75B7832E8C408AA0CDE1FD1A1A1197989D830383D4C252EF4A07A85F8A2817446BFE359947C3E7D1834DC479D807F6EE663496995DC2BB6B34786AAEEBEAC2C651D4DDDF95D3C76A0E66DC4EB3581EE5AA4D39B6F27C8D026F724153DCCCA0F8AC5F84DA52D4369E74FB589AE11946665DAF8F041EB77FF9A0783CF97BC0707818727E6871F1637B217C1D6A677BA3FCABF21A2E130553C269C7
	7262F86326856AE718609327856A67779E261FF5C06B3C076947F08C759307E50EDBC2F436864A499C377D9C53E3F69843AD9AC4DC97143D9C77EEB52B978E3815E1F8DF3261CF8277139C33911ECE51E3645B940B8376E3F8F8F850D4D1D1E62C2C28BAC4733DE80F6F9DC74D26E3C14F89900C77D2BD9D511EF17715E8637EE7B3BB17120096F3AF1BB315DD188372B58285292D7DD0F1589BEDFDD6B283E95CA9A46B84722F3C17451D96329F481D8E738940B21D65275E83ACBF0D7D629C41BA652320FB5B24
	0EAA4365695A1EEA3A1E1FAE047C1476D953447A0C837A3A69086BED725D7320AEFBD2E80EC569D1FE746D1A2DF57A377D68C8CBCF7525A77B51D1C7DBB4FEBA36D7CA595812DFD9A8C84F9F8F39EF2F3B0F0E29CBFB361DAF9176BF70F6A67505428A5D97BC6622503EG1ED0F1087A5497088D22203D587B181F9D4DC35CF2A82F6238428266FBD9B358DA201E417319CEA82BG11GCBB9EDAA21A5C2F97D8CFAAE02E7AD1416F18673B42CCB66640C7167011DE354D5F97F7BC89F6C4CF3A26712E31D79123D
	6B7D2813BB9E4B9875D2FD20GF84AGCAB4DABD3C67E9F626BAFE0B2C431100DFF8BF533BF40C69FD597DECEC0A97F5DB74G6457GDDGE18FB05A9DC20BF9001ED9A540F3952725F9031DF7A3FFB079314DA20DD697220EAF50BE76846DA3147EB77A68FBBC39BA756DBFD407DE7B7FC87415B6935EE7323575979EBCF36D151DAA2D73AFA48657813D85005A72603951EC192A590959528BFC61B3F5DBC643FBD30ADEA7C7D8648D3E76A712389E8B7517723E0B2B4772A3DFDFDAB5535F0F564F547D084613
	D7AF7946766CB96F99D2046071A4AD1C5AB5BBDC0F155A5DDBA54AE981FCD7B8DB5BCF157F911E67EDD734EC3A095CD813F49A10ED0A6C9295F518E249E92C6693096651F23C503E7466A47976D9A64AC75777B83276CC89276BFB8A473FC87061BEF83A7E1D313075AF8AE849616C0C6AE4956606DD8F42BE99A092A096E099406A8719AEE714E3309EF5FF52A13B2C10C8C9BE298F9D5F4926495DF17FE3A6DBE716FC6D99E169745F2504BA63C9FED03BE7FE08BED7F71490BC9D5FF2930E4F8A34FE00F00098
	0025G4B9FE263DBDEDEC647076E50202A361EFEF2B24C86F75EA1FAF4B4A656F45EBCEF41BC3DDBD6C822C77374E57CCCEB95341F8CF0E4964C5DD974EC1272D973C66677BDF90C2FEB964D6F3D708C5778685C2BAC98EB5E7B4E3D9860DDBA0B4EBDACAF03776DA9B4A720F29853B07EB668E749D6DB37A8D15AAEE72ECEA3A710C3B649079EE15AB9124E4F77E81DFF326778374F69747C7679B33E39C622B65664D96CFC7726A798BDBBA2A1E39FE99F7691827BDBFBEAA48EF2B512EB3C00DBB84D2FFE3C19
	79D5515FBEA9D8DE7FDDCAC9883F5A9FC17DC90D50634D6AAB4919F4ECE955A3ED383B1A59B0B6025AB0811E099174EC095A701EE3E35950975682EF5AEC5D06853375F9CD65B85B0CE767671C8E75EC6F47224DBD28D0ABB23A23B9AF104E2D406A0411BA27891FC56CF18C5824B6598D1151296F4767B523AD98D53275086EF6178376768A3555D73C013642B16F1F4DEC99E58AC62F6174E2BF3AF5B625CF194D768E254784AE4B339E34F508FC31322A3A4CD8F56410E67B02BA1DAF7DDCF1A0DFB3AC34870B
	6D8E3C92AC06CC93369D40AACC4C3FCE1C545FBF7BC06F6F06E9EFE8B90FD7D0B10376C55B8ABF05A59A47AC7578CE6355E79525C208358D2255E9686BEC779531C76D3075F11E6D1528D73E7F24CB1EC6AB28C45D43722F029E9C5749D5D9BBD4F47651987E8DF4D9ED3DFA45673D43B256B7897F9B20FD8C5C55BE68781B2BC21F83FCDC957C9C203DAA78B940452A514FC1EDD5216F6E7B2BFC6F6E295DBFE9C05BC442BCD88C3082E0B5C072EC36F67D368AFD9A3F9A9A39EEB5597B707C8E3F94D279B92DA9
	74D813CCE3FFDF74B3935E13F64B2AAA8FE07B8C7FAB935ACB15DD84C9F16BCC28A34551572F522A9AFE0389FDDD92FB891A66A76BAC6C9B2F020793B25A9EE67B731F6E01A9B246DDF407C31544C6ED99564E896B1A58B9610C0759FEFDDAAE76918F65C40035G346F19F9ACB74AFAA4A1237E91F6AF725DBC367F76BE425AC849473A0B217C92C052A363DD0B581CBEE49684FEBE5D9B62FC1AC4E2AA431AB358866EA156F3393E62E1384B11AC0F5BE94109998F6515G4223A0AFC09900852093C082188290
	853098A081A091E08DC0B2C076E3902F9FE3FEA5D4235EC3654F5A8189F1AE4566C0775272CCF3B553E58D3465FA0C49BC331A69F261E3CC17DFA5F513203C9EA07B71096A72B7CD8237774F3DCCA7879FE7FD3EEAE2E77B610FB3FF3ADDFD40571F2ECAFD65D7FAF0861777BAC4490E1714382EB5E30135FD51446E47929EE77A395890FA9EF5B50CBD0F9C8D649B8470FFCC0CA84E5900B9055C5B6B9679FCF5B7E4F3DC2B1BAC4B901DE9EC5CB0665EA17971E0F7CFD9E3666401773C142F017095C40D6F8C24
	8C6A51BD49EF493C2B0F8A5C2B3CC3705628403D4A0CDC66C31DC00B0BE2E7823373A637471AB7C93E44897351F13DC14E400DCF840EF7954117BD91B85E4CFCBEE700B67789B65EDF1F89367689F3287EF367843743C1405785505A20EC1F93749E52932C8F2FD6B7BCC49F5100DF92C0FB894AAB83F05AB78A1650F3254AB276DCB9E1893E66B4D8C62EB94D1660EB0E55623B66D05CA7960C89FD904BBAF93D739CF71163AE11B691F741E1D77BB95EC664C2FC0B089DB2FC9F114BC3F01D9ACE7241DD54F057
	487E91065CAFAB7CDE4EC24EFF9C12C44B57C99D079326BF57479DA22343E57311BB0C63AEF25B36D6CF4E6FAFCC12AFE242FCF4DE7C69987A7F5AB90173251A601367844E17FF60EBCC5613302EBD49664B2F021E67ADFFD27B4D441341FDF9BD60B74F75279921FCE4EE3079324264BB46962D4EE56D691ABB328F4A7799678B8B28938565D80078GBC3D7F0E4F59C26CAFB91C2219FE41AD30B358CE12632CG3EB5F359996C26DC32CEBE1510117B947F1683D3C8CD0E74BC7D7D215C3B1F3FFF697F210F9F
	6F544774F31F77AD79F73F6F83F7358FEA531C33E8D3A3583362A9E6E7AF4155C1390363169890578F65B00E7BF98D39B30172C20E9B5E454E6963B8EE9D494FB6FF81F240AFB05C8FC91ED98365960EBBC5722C8EA8CB9C370D70CE0172DC0EFBEE88F10B219C4FF1DBAF30FED7F05C9B240F7D73E04D1A477C55F4CC37E11D4F7B0B55F75F1EBF3A2BFDE8F72843B866B698EF57BC2607AF911C8A65F00E4B29E6F732519C37025F49AE6138B5DC2FAFF1DC14175D4996C4B35CAFC89EDB8665BA0EFBB41F4882
	65AE0E5B104F5A13B8EE107046E119633E2692D9201C76B4433D4A6FFD8BB86E9829578F65A9CF33B3D0398957D495F7EEF88603C6A6E7E877111896897522F95D77F253D9DD011D3364FF08AC5A9D4BD9DCB3D5C8A2A86D0353BAED56D9F237E173865CEDFC094C1F655047CA5E4FAB0650F7B52B485C4AFA86626AB32CFE4AF17F3B1A1D4731CEBD502D4F30733E08DD93B96FAB7339F743756DA63473DF6F3C730EFE5E776E3160F7B5A564AEE986540F64727D5A632F975BC73ADE4EFB036B45C97CA69E5AD8
	465BB9B04A9D56A9E26F75D0B7EDBE2B7F62103FDE5EA031208268F57319DECAAFCC64ACBBD0AF527CC03D7C07A738DE16DF473E3DD0BF0C4BF7F01869055679004C3D45C00F63F53EBE9CDCBF5FA7F5D7C23D55G94E7A6BA480DC948B046B0DC8A5123954A1DB1EC4C291D230D5977DC97A15C675E8E71B3E274BC087671AD52C7A46023E3185C06A37E3AAFB8C2FE6B8A74C4DEA7B65F5FAF422C58A415F17B4E31E4CC9DC5462C850167536126503E33004414DC60B1AEE0323D2612DC5F56F335CF11871DF6
	D677E7A406D821DE6782264FB7C73D0B8C1CDB0132FA211DC3FE321ED1037BD0AF69BB8C6AC7ACE06751E5E77539747B9C7C7DEF10F365865848C845E546CA6DD8D90BDDFFEE53F91FB50CF2C6FC2C869871A006725D7AC4677B571A71737D20DD679B58B55EB3F04A1BF9CE60F2371E575B795CB37E7E258BBADFDA7578795E6A5479668F0D1F2F562E7399AECCEC3CFF917571CE69555BF97268787BFF2ECF671BB70C77B345F881E314FB0D5A95CCEDB12B7BDA3FDEB72BFA34FB0D8A936D04722DF368FCAF0F
	FA9F624FF7C9527912A6281B5B833AEE1EF66A6DBCBC81397F5B25735D1FBF317ECB07747EDBBCFABB0B54D1ECF3946FD92AF89B4707F53EEF115F25F09E33AC49CA39275F210A346E656BFAFFF7EE68FCD5D563690F7E3F823F3E1D8D09CE6CDBC23ADB91F60C2C5F70508FDE0F7D5C12EB908C2DC2C6BFB99FE89552BDF85060C5DD018F2614392F8937857E87ADED63AD3512230124A1D3ED6A3F4FCFB588E9508CBDAAE99552703C83DAD3E82B292A6CA2ED9AB4A0DBC55EB69EC65F06363778D96345270F3E
	396CC7675D29C2C62B308B9716F6D1A177C4229D3F5356FE705BACECA47F9BG06CD6858CD9E69EEF5D8922DC2EE6F277863B737AA5A9B584D08DB957248CF148E63A17E61961B3BDF45D31F93D6A14D09BFCD96F6D032E4F3769D2657D1A70874A98DDDC45E2D1AAEF2083C58CAB9EFA56705ED256F1DDE35B4D5B0B48B1BA459E6EF3675283252AAECD47B95515DAFCBF62F6BD4BDE9A80B6454987BD321395DFC78B858728ED8A63950D73DCF05EFD93D25991467D5044324A62E23653C065D5CEFD320ABD1E9
	D571275632321F53266ED58FFC67431D178DC20AB6C8D4FD1657258A993D368107B48CAF547709D1B62BA02DE2F33A7197DDA019B1C0B2C6629D14BF72F245234FEE78B369C0E7D505FDD41540FA38CF14878EB77476BA1CA2EFG748A4D6F614DE31D7200BABB3C3373CEFF6F1DEE70830B10BAD0D1C17E2DA47FD6613F1584D3C9B015D5205C354228FF01F4AF9BB3F19E6E63E87E72E630207A4337DEFE660D3A35EBE0F44DC2BA79099398A1DB2FF5E6F020DBD42647BE3A675D482419E8A3973A0A975B4D26
	4D330F23507B4907C8FE5E7FE7DEA743E70698B896C3F91AC649702B4B3086D54854BEF8D005CDB8BF0CF83EDF8B16E49FC9407BB66D9E5DE8F174B9D4F7ADE9AAA3403529E81BC678E89A22370227D87CBCA5031EAD12E2A172604BF6D24CA17F66CF58A048D5C09B64FABC68659A430B09B3C1CA018391C28CA4158D043A7C5D115764753393CFE2479C45DF7118CE6AE35A64B326F4FF39D5A1BBB06B702B50972474D7B54C96FD186CBF63B10C9866A465365CC5395BF439B335FA64EE1B4B0DB79ED5906036
	7A279EA98BF65E13BD4BB4E890321AF1C110DC7D36D6AD50D905F73B49AAAEAAD097CC2B87F4428B2B26218716545DBA3E8771145A6E327560423A51EED32E1296D0148B17040D3DCADFB769C0958C171AAFC1372404640BD6E1135A0F9F90608A278ABB75265B704AD7A350B5D9004D78BBAE7267E7B4B498E663867F4153EF318E5D717D76F37D3D85A13F6B8FDBC44F46239685FE7BB910CA4E40813FF6913B1B7DFB837E50A05477B16D90F8A555A6DDEDF05A71DB1946E1D5543F4FD0D231FDB72B2391A9ED
	B895653E624373BFD0CB87885916223D2699GG18CAGGD0CB818294G94G88G88G5BF854AC5916223D2699GG18CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6099GGGG
**end of data**/
}
}
