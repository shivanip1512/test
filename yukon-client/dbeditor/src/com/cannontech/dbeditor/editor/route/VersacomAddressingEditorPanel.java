package com.cannontech.dbeditor.editor.route;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.roles.application.DBEditorRole;

/**
 * This type was created in VisualAge.
 */
public class VersacomAddressingEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ComponentListener, com.klg.jclass.util.value.JCValueListener
{
	private com.klg.jclass.field.JCSpinField ivjSectionAddressSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjUtilityIDSpinner = null;
	private javax.swing.JLabel ivjSectionAddressLabel = null;
	private javax.swing.JLabel ivjUtilityIDLabel = null;
	private com.cannontech.common.gui.util.BitTogglePanel ivjClassAddressPanel = null;
	private com.cannontech.common.gui.util.BitTogglePanel ivjDivisionAddressPanel = null;
	private com.cannontech.common.gui.util.TitleBorder ivjClassAddressPanelTitleBorder = null;
	private com.cannontech.common.gui.util.TitleBorder ivjDivisionAddressPanelTitleBorder = null;
	private javax.swing.JTextField ivjJTextFieldUtilRange = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public VersacomAddressingEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ComponentListener interface.
 * @param e java.awt.event.ComponentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void componentHidden(java.awt.event.ComponentEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getClassAddressPanel()) 
		connEtoC3();
	if (e.getSource() == getDivisionAddressPanel()) 
		connEtoC4();
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ComponentListener interface.
 * @param e java.awt.event.ComponentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void componentMoved(java.awt.event.ComponentEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getClassAddressPanel()) 
		connEtoC3();
	if (e.getSource() == getDivisionAddressPanel()) 
		connEtoC4();
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ComponentListener interface.
 * @param e java.awt.event.ComponentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void componentResized(java.awt.event.ComponentEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getClassAddressPanel()) 
		connEtoC3();
	if (e.getSource() == getDivisionAddressPanel()) 
		connEtoC4();
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ComponentListener interface.
 * @param e java.awt.event.ComponentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void componentShown(java.awt.event.ComponentEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getClassAddressPanel()) 
		connEtoC3();
	if (e.getSource() == getDivisionAddressPanel()) 
		connEtoC4();
	// user code begin {2}
	// user code end
}
/**
 * connEtoC3:  (ClassAddressPanel.component. --> VersacomAddressingEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
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
 * connEtoC4:  (DivisionAddressPanel.component. --> VersacomAddressingEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
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
 * Return the ClassAddressPanel property value.
 * @return com.cannontech.common.gui.util.BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.BitTogglePanel getClassAddressPanel() {
	if (ivjClassAddressPanel == null) {
		try {
			ivjClassAddressPanel = new com.cannontech.common.gui.util.BitTogglePanel();
			ivjClassAddressPanel.setName("ClassAddressPanel");
			ivjClassAddressPanel.setBorder(getClassAddressPanelTitleBorder());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClassAddressPanel;
}
/**
 * Return the ClassAddressPanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getClassAddressPanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjClassAddressPanelTitleBorder = null;
	try {
		/* Create part */
		ivjClassAddressPanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjClassAddressPanelTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjClassAddressPanelTitleBorder.setTitle("Class Addresses");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjClassAddressPanelTitleBorder;
}
/**
 * Return the DivisionAddressPanel property value.
 * @return com.cannontech.common.gui.util.BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.BitTogglePanel getDivisionAddressPanel() {
	if (ivjDivisionAddressPanel == null) {
		try {
			ivjDivisionAddressPanel = new com.cannontech.common.gui.util.BitTogglePanel();
			ivjDivisionAddressPanel.setName("DivisionAddressPanel");
			ivjDivisionAddressPanel.setBorder(getDivisionAddressPanelTitleBorder());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDivisionAddressPanel;
}
/**
 * Return the DivisionAddressPanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getDivisionAddressPanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjDivisionAddressPanelTitleBorder = null;
	try {
		/* Create part */
		ivjDivisionAddressPanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjDivisionAddressPanelTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjDivisionAddressPanelTitleBorder.setTitle("Division Addresses");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjDivisionAddressPanelTitleBorder;
}
/**
 * Return the JTextFieldUtilRange property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUtilRange() {
	if (ivjJTextFieldUtilRange == null) {
		try {
			ivjJTextFieldUtilRange = new javax.swing.JTextField();
			ivjJTextFieldUtilRange.setName("JTextFieldUtilRange");
			ivjJTextFieldUtilRange.setDisabledTextColor(new java.awt.Color(0,0,0));
			ivjJTextFieldUtilRange.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJTextFieldUtilRange.setText("(Util range:)");
			ivjJTextFieldUtilRange.setEnabled(false);
			// user code begin {1}

			ivjJTextFieldUtilRange.setText(
				"(Util range: " + 
				ClientSession.getInstance().getRolePropertyValue(
				DBEditorRole.UTILITY_ID_RANGE,
				"1-" + CtiUtilities.MAX_UTILITY_ID ) +")" );

			ivjJTextFieldUtilRange.setToolTipText( ivjJTextFieldUtilRange.getText() );
			ivjJTextFieldUtilRange.setBackground( getBackground() );
			ivjJTextFieldUtilRange.setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUtilRange;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSectionAddressLabel() {
	if (ivjSectionAddressLabel == null) {
		try {
			ivjSectionAddressLabel = new javax.swing.JLabel();
			ivjSectionAddressLabel.setName("SectionAddressLabel");
			ivjSectionAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSectionAddressLabel.setText("Section Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddressLabel;
}
/**
 * Return the SectionAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getSectionAddressSpinner() {
	if (ivjSectionAddressSpinner == null) {
		try {
			ivjSectionAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjSectionAddressSpinner.setName("SectionAddressSpinner");
			ivjSectionAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjSectionAddressSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSectionAddressSpinner.setBackground(java.awt.Color.white);
			ivjSectionAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjSectionAddressSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(256), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddressSpinner;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUtilityIDLabel() {
	if (ivjUtilityIDLabel == null) {
		try {
			ivjUtilityIDLabel = new javax.swing.JLabel();
			ivjUtilityIDLabel.setName("UtilityIDLabel");
			ivjUtilityIDLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUtilityIDLabel.setText("Utility ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUtilityIDLabel;
}
/**
 * Return the UtilityIDSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getUtilityIDSpinner() {
	if (ivjUtilityIDSpinner == null) {
		try {
			ivjUtilityIDSpinner = new com.klg.jclass.field.JCSpinField();
			ivjUtilityIDSpinner.setName("UtilityIDSpinner");
			ivjUtilityIDSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjUtilityIDSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUtilityIDSpinner.setBackground(java.awt.Color.white);
			ivjUtilityIDSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}


			ivjUtilityIDSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
						new Integer(0), new Integer(com.cannontech.common.util.CtiUtilities.MAX_UTILITY_ID), 
						null, true, null, 
						new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
						null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(
								true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUtilityIDSpinner;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) {

	//Make sure o is of the assumed type
	if( o instanceof com.cannontech.database.data.route.VersacomRoute )
	{
		com.cannontech.database.data.route.VersacomRoute vr = (com.cannontech.database.data.route.VersacomRoute) o;

		Integer utilityID = new Integer( 
				((Number)getUtilityIDSpinner().getValue()).intValue() );

		Integer sectionAddress = new Integer( 
				((Number)getSectionAddressSpinner().getValue()).intValue() );

		Integer classAddressInteger = new Integer( getClassAddressPanel().getIntegerValue().intValue() );
		Integer divisionAddressInteger = new Integer( getDivisionAddressPanel().getIntegerValue().intValue() );

		vr.getVersacomRoute().setUtilityID( utilityID );
		vr.getVersacomRoute().setSectionAddress( sectionAddress);
		vr.getVersacomRoute().setClassAddress( classAddressInteger );
		vr.getVersacomRoute().setDivisionAddress( divisionAddressInteger );
	}
	
	return o;
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
	
	getUtilityIDSpinner().addValueListener(this);
	getSectionAddressSpinner().addValueListener(this);

	// user code end
	getClassAddressPanel().addComponentListener(this);
	getDivisionAddressPanel().addComponentListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomAddressingEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(333, 367);

		java.awt.GridBagConstraints constraintsUtilityIDLabel = new java.awt.GridBagConstraints();
		constraintsUtilityIDLabel.gridx = 1; constraintsUtilityIDLabel.gridy = 1;
		constraintsUtilityIDLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUtilityIDLabel.insets = new java.awt.Insets(15, 11, 3, 63);
		add(getUtilityIDLabel(), constraintsUtilityIDLabel);

		java.awt.GridBagConstraints constraintsUtilityIDSpinner = new java.awt.GridBagConstraints();
		constraintsUtilityIDSpinner.gridx = 2; constraintsUtilityIDSpinner.gridy = 1;
		constraintsUtilityIDSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUtilityIDSpinner.insets = new java.awt.Insets(14, 10, 1, 145);
		add(getUtilityIDSpinner(), constraintsUtilityIDSpinner);

		java.awt.GridBagConstraints constraintsSectionAddressSpinner = new java.awt.GridBagConstraints();
		constraintsSectionAddressSpinner.gridx = 2; constraintsSectionAddressSpinner.gridy = 3;
		constraintsSectionAddressSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSectionAddressSpinner.insets = new java.awt.Insets(3, 11, 7, 144);
		add(getSectionAddressSpinner(), constraintsSectionAddressSpinner);

		java.awt.GridBagConstraints constraintsSectionAddressLabel = new java.awt.GridBagConstraints();
		constraintsSectionAddressLabel.gridx = 1; constraintsSectionAddressLabel.gridy = 3;
		constraintsSectionAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSectionAddressLabel.insets = new java.awt.Insets(4, 12, 9, 9);
		add(getSectionAddressLabel(), constraintsSectionAddressLabel);

		java.awt.GridBagConstraints constraintsClassAddressPanel = new java.awt.GridBagConstraints();
		constraintsClassAddressPanel.gridx = 1; constraintsClassAddressPanel.gridy = 4;
		constraintsClassAddressPanel.gridwidth = 2;
		constraintsClassAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsClassAddressPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsClassAddressPanel.weightx = 1.0;
		constraintsClassAddressPanel.weighty = 1.0;
		constraintsClassAddressPanel.ipadx = 28;
		constraintsClassAddressPanel.ipady = 1;
		constraintsClassAddressPanel.insets = new java.awt.Insets(8, 12, 5, 108);
		add(getClassAddressPanel(), constraintsClassAddressPanel);

		java.awt.GridBagConstraints constraintsDivisionAddressPanel = new java.awt.GridBagConstraints();
		constraintsDivisionAddressPanel.gridx = 1; constraintsDivisionAddressPanel.gridy = 5;
		constraintsDivisionAddressPanel.gridwidth = 2;
		constraintsDivisionAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDivisionAddressPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDivisionAddressPanel.weightx = 1.0;
		constraintsDivisionAddressPanel.weighty = 1.0;
		constraintsDivisionAddressPanel.ipadx = 28;
		constraintsDivisionAddressPanel.ipady = 1;
		constraintsDivisionAddressPanel.insets = new java.awt.Insets(5, 12, 47, 108);
		add(getDivisionAddressPanel(), constraintsDivisionAddressPanel);

		java.awt.GridBagConstraints constraintsJTextFieldUtilRange = new java.awt.GridBagConstraints();
		constraintsJTextFieldUtilRange.gridx = 1; constraintsJTextFieldUtilRange.gridy = 2;
		constraintsJTextFieldUtilRange.gridwidth = 2;
		constraintsJTextFieldUtilRange.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldUtilRange.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldUtilRange.weightx = 1.0;
		constraintsJTextFieldUtilRange.ipadx = 313;
		constraintsJTextFieldUtilRange.ipady = 1;
		constraintsJTextFieldUtilRange.insets = new java.awt.Insets(1, 11, 3, 5);
		add(getJTextFieldUtilRange(), constraintsJTextFieldUtilRange);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{

	String idRange =
	ClientSession.getInstance().getRolePropertyValue(
		DBEditorRole.UTILITY_ID_RANGE,
		"1-" + CtiUtilities.MAX_UTILITY_ID );

	int res = java.util.Arrays.binarySearch( 
				com.cannontech.common.util.CtiUtilities.decodeRangeIDString( idRange, com.cannontech.common.util.CtiUtilities.MAX_UTILITY_ID ),
				((Number)getUtilityIDSpinner().getValue()).intValue() );

	if( res < 0 )
	{
		setErrorString("An invalid Utility ID was entered, the valid Utility ID range is: " + idRange );
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		VersacomAddressingEditorPanel aVersacomAddressingEditorPanel;
		aVersacomAddressingEditorPanel = new VersacomAddressingEditorPanel();
		frame.setContentPane(aVersacomAddressingEditorPanel);
		frame.setSize(aVersacomAddressingEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) {

	//Make sure it is the assumed type
	if( o instanceof com.cannontech.database.data.route.VersacomRoute )
	{
		com.cannontech.database.data.route.VersacomRoute vr = (com.cannontech.database.data.route.VersacomRoute) o;
		
		Integer utilityID = vr.getVersacomRoute().getUtilityID();
		Integer sectionAddress = vr.getVersacomRoute().getSectionAddress();
		Integer classAddress = vr.getVersacomRoute().getClassAddress();
		Integer divisionAddress = vr.getVersacomRoute().getDivisionAddress();

		getUtilityIDSpinner().setValue( utilityID );
		getSectionAddressSpinner().setValue( sectionAddress );
		getClassAddressPanel().setIntegerValue( classAddress );
		getDivisionAddressPanel().setIntegerValue( divisionAddress );
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8FDCD4D53A24E624D828E4266E0BED4DFCAF5A35176D7315EDC5762232DDDFDBDBDB3C973DEC17DAEBE9633D2C7855E1E08646FF10226226097F8AB7C294CCA3AC91C4B1AA1E2209D8F01939438C8CF347BBF7E4900177FD675CF36FE51881752D3FDF68473D67BB67BB5F1F731D6FFC675C83495B98EBCEB7659112EEA66A4F1EF4C24AAA89F9677B9727F1DC0498EFA1061FCDG6F11578319700C
	063A441B63AD39E47D09ECA8CF07725CE9711635703E0A4C37AF5B83AFE178AC7C141047FE7543DF0666F375C94EA70B16A3B31741F38540CC4061B3D5C47E311979AAFE3E0A6710EEA2A44901ED6E4EDC2F6276C2399EE09A40AC87EB1FGCFAB65FCE579DE355D697131A475110FCC4B319FC31F9C8A5B98ED1D7AACA03FC8CFEDC3DE8B291CC8B38A4AF381D0798C724901AF61D9D15F517605CD6AAC36899E0F64D1C41B33D8343B94C9AE16A53FA216171F90E51FGADAA6DF6D97479DC1E3603343EDE7008
	EE62F5A20FA2F3B66D937401F29A451D6BC13E3361FDBE40C28DDFF99E717BDD711613GDE9757F55FC7952AAEA7FBFEC1C6BD6549488923EB240B69B84B256B9A63FFABEB9D9D17079B517E0B20AE37BD5E329560B00093A09260FD6A8F8BCE7E8D1E152DCA155459A9F9AA3D5E83EE3B7C35E4975D70DEDE8EB5AA6E28E813E4BBA1EC4CDF4EAC8AA14FD0406896DFB49D33494CD2BE7EF1C7E71044BB3EB2E50530C9624C92D3ECBB33451CF65DA60C77CEF3B85ECF378F744F013C3FA3C65E4AE8A9439C0277
	6CA7371B96297D108E1DF7C9870E752B54878A60BDDD758F06DFCC710A814FFCB5026231BC8A6A12BB38EF1CDC41E5294B1CC2F2F7560534436478CAD31D1B49E0F7874BF2A7753BE8B763E955E4092778B8831E49F20862311CGF5C51D719666DF0FB7615C0D06F29C40BC00C400B400FC8F3473F09FDB57386382FD2CDE101583FE175BAE4A0451BE5E38861EE2C01105DA0FCF91BCB67104A03B04D63768A3EA1BE79B51862D836812286F5B0047A151A34A026212BC503B50A9AAB00BD91B179A5F43B632C8
	EB9BDC1DA2B06874925E6FEB0D2B606996FC4AF12FDDD0C43D852BFF3391EDF2D64038G9D40BB334B3B2751DEF5203F9DC0516C201C427E3E92E588976565F512546177B620A6E41E876DFC18460EC8F80F75F09B3F7DBD623225F8CB01447B397554CAC3BF55AE9B0AAF485DFC0E2576E0FCAE13384F3CFA127BCC75721BC95113C7C27A8CC20B44FCA5A0854F1DF7E81FA897AB0F6EB999E6AE15C921675245470A9B43724878FF763FC29BABD7B30ED5AC48BC97A0D133719FCE95B01FBDA078C46E255F4A60
	E022DD37B813736E733970B42BF22FD37D0755A5503AD468F73DB75ED28D6076F29BFEFD5E289F273F90395F3FE09BBA867048D079DC882D75825AB1DBA5B6617AA41E95BDCA356895BDF6F87129F5AFB4E1BF6A7CF5C89A4DCB946F0579DE85934C27617F5C04F3D2D09459558A0B6EB1D1D1E01D556B5F203CFC7ED62F9EAAB82FF71AD0B7298B663B56BE63B44E951B647108F450B492355E7CBD0E3F6896BBC168AA496FD198FE6977686F9DE2379AABF23E47C4682C6076E35CE1F943ACE1A96AA03BBA4195
	B0F41145DE1C474D66FC030DE2G07794FFCF8A6F9791C6C5A42676456F3BFA577B5EFB305DAD3A65FDCED5AFF064D2706B37A1CE4B2119FC24BEE79A1DB477B182CE74AB640F3688C064F091BDA681A847CA281E24F70B9B162872C67F1C71D8CD5125B4DCC2B59DCEAC61F7709E77CA204EB8CF0DFE9F6BC5B3CB2286E04EAD3D65F570CB6F59BE2E3110CB6CDBB4F7344B9A04F820054B34C2E4BE4E697566E173D3CDDG4A51F238F66DFD3C5DECA84F87D8A407EAD7524F5B157962ADF53E416D18FD331AB1
	378815C33679DD45FE450511DAD0E092F87D8AB5B74B979F26B19F7BF07BF8DE78C2B65AFFBA146782BC6163BE736F4BF9DE985391CB12FE765730B970A29F13ADD751FD26E0A44F8B978948339E6ADA94BE36EFB69B5726CA1FCF32396832280E4B4C9623CFB15CEDADCB6069F049BE2596A6FD000F619DADA8AF2E1BAADA4DA33675AED3F5D500EF9CG1BAF234B0C73E52142563F757EF8CB89C0866DB32AF7891D471DDE49839294F33B4F5B3E1249A0D24B32053AF07B891E3B5D34EA3F5A6F7C1FCFA1957D
	2BCC21F269481C8D2643FEE6AB2FDF375979FF36E4AC7A616DACA67FF31A7CF321CD2A1F4B7F574D2164AFBA9BEF598F40E8A776F15A13000B86486DC27CBDCA1E01B661AC23CDBB4B75FE2B2F1C471DE27A3B4AA9F85AC4FBA1FAD9073B2D385D067113FA98EBD0FC240ACE2903980F37F35BB776EB7C01EFF497677F8FFBC271CF6862EB542F87654C09DD7A7A1E52B5F82DE739E41B9579E586D8AE1923613BA83E4800E7723DC4FD9E4B8DD037A040731F9E9B5A362E9B76ADG91G138166GBC5D4DFD77F3
	21148C13DF5642DC14BD027B5800050367F9825A9AE23DDA7EEF414CEDCE78BA7AF1EF309DD23B876A1EFBEEE01951FDB6082EB828DC96C4477C7A0D3E616349811752A0353539C5C3BC79D5990FA7F567F8BC19F79CEDBB8A4A53GE61F63367D3D9DE35BFCA8A783109E18F3G7B81CE83C474F07B76376E9B4E3E54599A24AA409D0658DD953C5EEA76F871926D787A30F60C6D99C8476CF1F4B337471C9EEE0F648A34C78A14334FC3DEFD1E7B7A8DFD17A65BAD036802E55BFBBE549877FD7A21F10C8D3359
	61925D767272A3D5473CAECF8D9638BE298A6A03EBE47DF93EAE745698570539B93BCCD89F723CC015E366F9BD4695795F4AE2FAB477DF1A7E1D03680275CFBE9FEAEC1E5C43F5A13D30FF24FB209769FA38974AF59AEED696623CD00E6C65E355E9C55F0D03F28240C2006C3EF8CBA140E1GE79FEF57CB5BC57631359B577919FDACFF405CA0C1EB7767ECEF88FBE9B13D17759D2E1E83724ED65B21DCABD499C33555E5DCE19D0AF716EA2361F8E3AE40F3F61E63075BA7E11CE5F3FB14ADFC6EBE5696BAF79F
	EF8B1D3BDFEFFB0F61218F63CE1E575FE89BBA373F4596AA375FC075F8F8CD8E6D5B6793FDB458E0B225567DD1D9CDC264785F40E6DF3541AA525AEBD55B9CD4242AD24DF79FA8B37AEEF25098270598F3360EBD3B89E3C8EEBFDB4752B57CC20ADFEF4033756D11637C6C31906A227AF9ECFD419E5E7EAF5BC35BBF450EE36D137C32CD8C36719B76FC2DCE0F42350A580966D0778DA252AB025CA68E9A23ACB1DF2B8BC71FAB069BC7E66B75FDC6DB17E9367EEE335156A97D83ED1B99914E562777216D72A302
	EDADD27C5608E0DB3FDA41EDDD82F5B191BC17DAB0C02E1D1ADCEFAADC2E4550F659953A9C8570DE86F0780A01323175B34E013953E9288BGC4834C84C838020F6DECF1583C64E0A0E4DE42641D54EF14F717A66FB3FB383C4940AB4DA0DB6E08F84BD6003DA3864A4B7ACBB3639E56ADF4CBFE85564391E82BD5961E3B56C1598E908590BB423837393D8B630FC396BAE1180FF9C59B099909348FE5F1DA8265BC00B20086GE5A437793D47317EB864ABAE253B363ACEE8450DA62BA33BF20DF5385641561D55
	E5B4621CBE465CCF5D2E2A8DB4FD7ED433EAD09B460055BFF8907D1446A835166FF3D97D9B9BF17C2ADDE7DDBE3D835A0455B717E07FC79AE08652B598A5BD0A59B7E13973EC2BB1F7CE9C49F2E7B20A6756CD25216A73347A297B0C750523D8FD7DA83E6EBDC3F3B9BB14839A6E17FDFCAD1CB60A2F9F4F63E799FEAEEFF8DFF0BC5C79595F83980F17AEAB8F26330766F00BC0DFF2A53743B28B374356AB399DE81A2A4A55E1F8CF2D387CF6D83E6F725BA1570FBAAB20EF34E607CFCA69B7BEA84F56F065EB90
	17G65A48D1735075BABD5436D227DD50F06FCFF3496339C6157233B9D2157237B9D217301079CC367833FF104DBC718FC2F6D617AFAC7F3199D3B393E23B4DC7EB62E6FE48DB75F4F750D53F0F525DC5F3451FCDF7E39052F9539D7C11C3F0AFF935C5902F9A34D4D5B0533C231502514179F12DD7683C2DB9D0D20AC079DC97D9069A32E627B0B5BE91FF3203CGA079AA1E2787BE64F9FA5C0CA924AE622B1067B7C98D3BCCA412455F2D11FA1EBE9372F4467315AC4E33856AA7C7A26E7DECE3AC4178751C09
	65612C7E6B8175E8776D44D85FBF209E651DBA007E768175E683BD536D51EDDC3734C7E792771D1F05BD1BCAD2F54A3C1ABD6BCFECA1DB473C1D456C7E3AA35FE877C0312F8BFC8236C2B430B37D7FE44DD667DD857441F093CAB9CE391A0F61364275217B52A2BA23FDFC9F5F0744DD4D572D574AF02DEC15E46A30EC2D4C5E4D573B0550AE8D20EC8C784C98361F089903ED9E6B2B9EE29EE72876B22B6FADC023GECD14B11707E197A4E6438F273E0B926E967CD09E3189C29704C9E4B76B4CC0EAD166164D8
	EA1023GE84B466AF29C9E9BACC776DE9CCF2FACBAC4D9966D47DC67B0370E9A0B3CBE23F32D815EDB46F27B756E4673F30764D1D81B93F4FC9640FB2256E647B6BC53EB95EC9DED32647758F9FFB69A3F16C545DB722338BE9F6C99CA9F7E6D044B3B9768903628BED8972567080C372D947507044655696F245AB05ECDD41713505E8E40F01B76A2AE9A4A53B45CC49A0792207C0406BBC463D066B8F89F4765FE41BF1C5C99C348DDB2CE171B716821F62CG7C61F1FC5D2B34DC5A3778EFAF11AE722269D83E
	BEB20BFED798971C47C7D1FC443860BC7EDF34589C85F5A963783955CF1C687F8D57405C8708840885388FA0719A3E0EF95BCA48B039F703642D03054EEDD81A18FEFF493A34BB8A77E77198B87B2E1B481C51EF2607BB07C939C6BD1F3F16BD139B36D0BC532F249D755B8FF52781A2GA68344814C3B166BB7272D0869079FF0AA95C530B96949BCD76E04CB6C52511834B35DD22CC65D1ED679AFD21FA4FA709E0C51DDDFEA244BD35B952A4F0A30F4CF6DC33AFA355E2EBE836AB38A1E31C134EC6CCBF67331
	1F8D750B22F9CC7F6386DCF706BE97EDF0A9EE71009AB255332640EEFE8E38F5BCDF27B368197CE9A8ABGD1G9B29ED8949A67CA7654BD038696371DE14738C51467E79E5FC6C575FBA15D4BF75E6BA1F4341F6C99E4F745DBBC1DF5B135B5354EF8C73B6F33D3DD0BFF382DF5F16ED6172A7CD6072B71A693EF8E23C25846070440B157FBF50BC043D9FD5886131F1F36987707408DD85BC75A92EF6019B61AE29F0F7C9D1412EDD5B395E31DD46FBD9FB176C89799D9321E5A253D7D11FF32F5A22720E6856F9
	972F599C4C3B868638F0335DA53825364DC58540FFE759F6AEF7C52FCE7B6E271823E8F4D512DB120B3B1CAEC59CEC83CE5F5C273F0F68577B7A6202EC4068EEA6BADD536E8B272B4C506916ED3BB83DEF4954E9677943694D5ABEE256F5FC45225345AA97A2AB1B47D763C3FD1FE0F83F47F27977D63F3B2CBCD8BC1D618C3F6F394D99FA5FF307B3743E67DF1DC31FF35E6F8CFF07A141E93C43405603FB32F0FC63E05EA4GAC82A0B171163C983E961CF0216F60AD2A416B40C1FB9B1E83624DA905737F2CBD
	3C2E7B5B073FEFD55BCE73B6C9D124CE6C1F633FED2779376425C806EBE8C79B492EB627421A2A78D36DB8975D22032255BD6A92441500DE7BE3781E6D370DB4370432DD436DBD4577F6DE8D7773D2CEFB1F06FBF38D27FDC2431DDA4BE913B45C2BB47FAF39AE5ED2FF9D470D4B27E7AFD0D6B45C8DB574FE9614A7EB38F7EAF8FF53B55C87BD08CB05F276A41E23FD3A7B52F22D6ACB246B25F46BA7DD58DD2C92E827FE1F25EBE27524609CAD02626BA78567E856BE3EDE1D063A7BA6713D6EEF1A0C6BC26E14
	CDA6CE974CFB6B75CC46126B75751079FD0B95F3E7BB60A3G26834C85D8G108C108610BBB95E3295208220814089F09EA09AA096E0B6C042E4BECFEEBB0339438921DD4D86595D97BAD3G29EE6E69E4118597CE9755160FEC3E34B1F87A92695E5B02F40913AFEC6CD2A67331FB0C668319B7840F5D6FA8BE77066031EBB67331DB8FF591B7707C7A5F68BCDC88E5B2055B6EFA6563A1ECD7AFCBDED1D63A9916DEBFD0677903D961E34D7C2C61E34D13742CC3049851506DD53FBFBC13050B1864F078C4AD26
	BD17152B61EAFD75124F050742EC1D7BE049403DE899552FGF4AB1A427722AFB7E29B8504E16B649EB48DE1FB38EFCF8D37179EEA8FF7FACA709E6EA99A2B1C00D7G983FD34BF5FEE7568E47EF283D6E74A941FBDD7397389E59DDBE342C9D8FBFE80A204933718B14E796504D556CB1BDFF204D6E2271F0A15427E8EDE6558C34D96F8ADD075507065341CC8C678FD3F59D90D786E51F49F87E303B87F596BDD485D56E3A9EFE8E36FFAA0F3DB1B44FEF0032DB430D6DC5DC8414E326EA5F05E95E275EE5B53C
	6774DD7EFC26703272E0F378EFB86D5439F1A6FB9B89B3F7A1C3B882395F372D6D22CDE9905AB49A459AFE5E76D8071F37232C21F304B15641B942355650B942F5D6E30E40F0D32C689FED902BCFEA6DEE54F00D9AEEBA6D93F15F396C0AD3437F93158B7107C5E448713328DCDD94F7D2455DE940B52A383B2C98FBC0E5A7CEA0063B57CA670A4B6DE66587E89B1787C211E60F84AA234BAB9864FECC43215C4CD78B37F07F0D557C7779CC444D06F202663F3FB56BBE1BE8F8974D175FFF7BAEAB8FE61BC71760
	3B16C333A5057FCD4012BCEADF8871025BF50EDED3BC868DBDEDAC8E7D9A431D9AB79EEA3C38FD55EBF81DD0FD3FF7355E4FAB9739AF7D2FB5FABF477275FEC6662F8B6E0786634212A27837DD23295B28530C2B59941AF7A5CCBAB7305E897FF12BC6426F7525823DBFDB8FBA0CC3F07B747B91376E46792260498976CD785A70G0D27CF005FA5CD65F17F26DEB3EF2B49BB01FEF7E4B47FCBFDB8EFDA3C25E09A27D91CB918664E4D3A0D56EC57656B6B7DE8A8DB9737CAEE3B2AB5981C514B9F6B74CFE4DE8C
	BD7B3B38CEFF9E98A06E95E27649A4C7232B2513B869ECE9F9468BBF4BB0114CDAE2E1C12D16E4E2E42AA5E69A38EAC9868435GDE64C1C5B1FB408F4D593EB3042EAEE407810BBCD68E5EF3D7DE737B27621A2B73CC4484EC1CA26B575C05810C760CBE358BE8178F506B411F6E7E437C7FBC614BA016DA328E176CE322CCE706E847DBB26A1F2754902BD70234968F3BC1F89FFD18FDCA37DB2CA3F90E23F8A55B270876CABE67FC8AD9DD071CF660315A0E43024F29E00C7F3C0EE4FA708FE948CAD66D96BCED
	BB58847C1C4A19DE59C2F52679G483B0A4ACB2F28EA3D2C3ABBE75B26A38B67E490D38DD9621684FB0DE003E52916D89527AC7A1CB0C8816F177BE9C759DD7C8BCD86F4773E26BEAADB0C890DA6F4C760A7D14B131757187082B859C1DBE206D22C3530D7B98599D809F22D02FF98A449DB353AABBED0BEFC6C505A53A6122EAA0943179570AA446290BADD6EEEF8E1DEC487F62982524A0247671064CE102CA0C8B2DE25B1A81E78F049F57F7856A94AC0A7D548C6E6CAA05D51A6CA1DBBAA9D8E17C754BAG3B
	C2779B346E31CDF1D01B1501712B0FBC79EEAB78C1B54968ACA9213FF7525F3B7077CE0A59C9B1BBF701F1D34810BF4175812EB3F59EED1E607097574088AA4F3C7270CD4FD624AC826DEA0819DE2005C14855DBFD636FEC956551F157EDD89A13B49647480B2E9240FEF38250630A01D1B17D6635A3F32FAD751BC8F68D0E005B6B94EA551E6B4852D6BA75C4995AAAA4D737A02585B7B55940075F5771B644335AE3DE41061EEC358BF2876D81E55850C42C8E392D15B2D00829292689585292D6EF2CA3CB94A7
	1E2720CBA9E42D5EF5BD6E98558AB689483B706325BFDA9060F7792229A7A3B6C31B5603CB66C1D389315641F382514BB54F44D2AEED3A127EDEFD51630CD4EBA96DBAEDF8BEE2233FBE0CE61805E3E6B59EE59D280BC2162AF7FC8AEA2B4BE120549BBB856A0D9BC0ADAF449685B2DE2EA95F7E63D33EB420A94ABF1D621F8C677C78A4AD57A545851D7F61E626E170G8C034724E732526300B052E7F2692B8704BF8BCB0F1994C87377D5EB3FDABEB0F8G28608A4921E72F5C17F051DA49EFF469481063F401
	1AE2298AFF9D3CBCA376CD6047ACDDC3001D67ED6FB71E67AD3EB2BF6CF71E02996C5C2AECC670F95E4B5D742C9470D1B37839D2F977A7A47C5E74D82760F6AB023B2352E347FD6A01EEC5B47C2DE0B7766F63ED54CAD677BF5DA8F73B01667FGD0CB8788174976535F96GGDCC1GGD0CB818294G94G88G88GD9F954AC174976535F96GGDCC1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9997GGGG
**end of data**/
}
}
