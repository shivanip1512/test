package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class LMProgramDirectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, java.awt.event.ItemListener {
	public static final int MAX_GEAR_COUNT = Integer.MAX_VALUE; //there should not be a limit!
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JButton ivjJButtonDelete = null;
	private DirectModifyGearPanel ivjDirectModifyGearPanel = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectPanel() {
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
	if (e.getSource() == getJButtonDelete()) 
		connEtoC4(e);
	if (e.getSource() == getJButtonCreate()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxGear.item.itemStateChanged(java.awt.event.ItemEvent) --> LMProgramDirectPanel.jComboBoxGear_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxGear_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonDelete.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectPanel.jButtonDelete_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDelete_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectPanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCreate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the DirectModifyGearPanel property value.
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DirectModifyGearPanel getDirectModifyGearPanel() {
	if (ivjDirectModifyGearPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Gear Settings");
			ivjDirectModifyGearPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel();
			ivjDirectModifyGearPanel.setName("DirectModifyGearPanel");
			ivjDirectModifyGearPanel.setPreferredSize(new java.awt.Dimension(315, 194));
			ivjDirectModifyGearPanel.setBorder(ivjLocalBorder);
			// user code begin {1}

			ivjDirectModifyGearPanel.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDirectModifyGearPanel;
}
/**
 * Return the JButtonCreate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCreate() {
	if (ivjJButtonCreate == null) {
		try {
			ivjJButtonCreate = new javax.swing.JButton();
			ivjJButtonCreate.setName("JButtonCreate");
			ivjJButtonCreate.setMnemonic('C');
			ivjJButtonCreate.setText("Create...");
			// user code begin {1}

			ivjJButtonCreate.setMnemonic('a');
			ivjJButtonCreate.setText("Add");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreate;
}
/**
 * Return the JButtonDelete property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDelete() {
	if (ivjJButtonDelete == null) {
		try {
			ivjJButtonDelete = new javax.swing.JButton();
			ivjJButtonDelete.setName("JButtonDelete");
			ivjJButtonDelete.setMnemonic('d');
			ivjJButtonDelete.setText("Delete");
			ivjJButtonDelete.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonDelete.setPreferredSize(new java.awt.Dimension(81, 25));
			ivjJButtonDelete.setEnabled(true);
			ivjJButtonDelete.setMinimumSize(new java.awt.Dimension(81, 25));
			// user code begin {1}

			ivjJButtonDelete.setMnemonic('r');
			ivjJButtonDelete.setText("Remove");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDelete;
}
/**
 * Return the JComboBoxGear property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGear() {
	if (ivjJComboBoxGear == null) {
		try {
			ivjJComboBoxGear = new javax.swing.JComboBox();
			ivjJComboBoxGear.setName("JComboBoxGear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGear;
}
/**
 * Return the JLabelGear property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGear() {
	if (ivjJLabelGear == null) {
		try {
			ivjJLabelGear = new javax.swing.JLabel();
			ivjJLabelGear.setName("JLabelGear");
			ivjJLabelGear.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGear.setText("Gear:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGear;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setLayout(getJPanelButtonsFlowLayout());
			getJPanelButtons().add(getJButtonDelete(), getJButtonDelete().getName());
			getJPanelButtons().add(getJButtonCreate(), getJButtonCreate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}
/**
 * Return the JPanelButtonsFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelButtonsFlowLayout() {
	java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelButtonsFlowLayout = new java.awt.FlowLayout();
		ivjJPanelButtonsFlowLayout.setVgap(4);
		ivjJPanelButtonsFlowLayout.setHgap(10);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelButtonsFlowLayout;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//this stores any changes that are made to the current Gear
	if( getJComboBoxGear().getSelectedItem() != null )
		getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() );

	LMProgramDirect program = (LMProgramDirect)o;

	program.getLmProgramDirectGearVector().removeAllElements();
	for( int i = 0; i < getJComboBoxGear().getItemCount(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGear gear = (com.cannontech.database.db.device.lm.LMProgramDirectGear)getJComboBoxGear().getItemAt(i);
		gear.setGearNumber( new Integer(i+1) );

		program.getLmProgramDirectGearVector().add( gear );
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
 * Insert the method's description here.
 * Creation date: (3/13/2002 10:24:00 AM)
 * @return boolean
 */
public boolean hasLatchingGear() 
{
	for( int i = 0; i < getJComboBoxGear().getItemCount(); i++ )
	{
		if( getDirectModifyGearPanel().getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING)
			 ||
			 ((LMProgramDirectGear)getJComboBoxGear().getItemAt(i)).getControlMethod().equalsIgnoreCase(
			  	 			LMProgramDirectGear.CONTROL_LATCHING) )
		{
			return true;
		}
	}

	return false;	
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getDirectModifyGearPanel().addDataInputPanelListener( this );

	// user code end
	getJButtonDelete().addActionListener(this);
	getJButtonCreate().addActionListener(this);
	getJComboBoxGear().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramDirectPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(392, 401);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.gridwidth = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.ipady = -4;
		constraintsJPanelButtons.insets = new java.awt.Insets(1, 12, 3, 10);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
		constraintsJLabelGear.gridx = 1; constraintsJLabelGear.gridy = 1;
		constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGear.ipadx = 6;
		constraintsJLabelGear.ipady = 1;
		constraintsJLabelGear.insets = new java.awt.Insets(3, 10, 3, 0);
		add(getJLabelGear(), constraintsJLabelGear);

		java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
		constraintsJComboBoxGear.gridx = 2; constraintsJComboBoxGear.gridy = 1;
		constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxGear.weightx = 1.0;
		constraintsJComboBoxGear.ipadx = 100;
		constraintsJComboBoxGear.insets = new java.awt.Insets(2, 0, 1, 114);
		add(getJComboBoxGear(), constraintsJComboBoxGear);

		java.awt.GridBagConstraints constraintsDirectModifyGearPanel = new java.awt.GridBagConstraints();
		constraintsDirectModifyGearPanel.gridx = 1; constraintsDirectModifyGearPanel.gridy = 2;
		constraintsDirectModifyGearPanel.gridwidth = 2;
		constraintsDirectModifyGearPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDirectModifyGearPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDirectModifyGearPanel.ipadx = 378;
		constraintsDirectModifyGearPanel.ipady = 315;
		constraintsDirectModifyGearPanel.insets = new java.awt.Insets(2, 1, 1, 3);
		add(getDirectModifyGearPanel(), constraintsDirectModifyGearPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 */
public void inputUpdate(com.cannontech.common.gui.util.DataInputPanelEvent event)
{
	
	if( getJComboBoxGear().getSelectedItem() != null 
		 && !getDirectModifyGearPanel().getGearType().equalsIgnoreCase(
			 ((LMProgramDirectGear)getJComboBoxGear().getSelectedItem()).getControlMethod()) )
	{
		getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() );
	}

	fireInputUpdate();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJComboBoxGear().getSelectedItem() != null
		 && !getDirectModifyGearPanel().isInputValid() )
	{
		setErrorString( getDirectModifyGearPanel().getErrorString() );
		return false;
	}

	if( getJComboBoxGear().getItemCount() <= 0 )
	{
		setErrorString("Every Direct Program must contain 1 or more gears");
		return false;
	}

	if( getJComboBoxGear().getItemCount() > 1 
		 && hasLatchingGear() )
	{
		setErrorString("A latching gear can not exist alongside other gear types.");
		return false;
	}

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxGear()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//if there were any changes to the current selected gear, we must store them
	if( getJComboBoxGear().getSelectedItem() != null )
		getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() );


	//only allow the user to define at most MAX_GEAR_COUNT Gears
	if( getJComboBoxGear().getItemCount() >= MAX_GEAR_COUNT )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"A Direct Program is only allowed " + MAX_GEAR_COUNT + " or less gears to be defined for it.",
			"Gear Limit Exceeded",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );

		return;
	}
	else if( hasLatchingGear() )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"Only 1 latching gear is allowed for a Direct Program.",
			"Latching Gear Limit Exceeded",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );

		return;
	}


	DirectModifyGearPanel p = new DirectModifyGearPanel();
	com.cannontech.common.gui.util.OkCancelDialog d = new com.cannontech.common.gui.util.OkCancelDialog(
		com.cannontech.common.util.CtiUtilities.getParentFrame(this), "Gear Creation", true, p );

	d.setSize( 400, 385 );
	//d.pack();
	d.setLocationRelativeTo( this );
	d.show();

	if( d.getButtonPressed() == d.OK_PRESSED )
	{
		LMProgramDirectGear g = (LMProgramDirectGear)p.getValue(null);

		if( getJComboBoxGear().getItemCount() >= 1 
			 && g.getControlMethod().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING) )
		{
			javax.swing.JOptionPane.showMessageDialog(
				this, 
				"A latching gear can not exist alongside other gear types.",
				"Latching Gear Exception",
				javax.swing.JOptionPane.INFORMATION_MESSAGE );
		}
		else
		{
			//set the gear number for the newly created gear
			g.setGearNumber( new Integer(getJComboBoxGear().getItemCount()+1) );
			
			getJComboBoxGear().addItem( g );
			getJComboBoxGear().setSelectedItem( g );

			fireInputUpdate();

			//we may need to show/hide the DirectModifyGearPanel panel
			getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
		}
	}	


	d.dispose();	
	return;





	
/*	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );
	
	DirectGearPanel panel = new DirectGearPanel()
	{
		public void disposePanel()
		{
			dialog.dispose();
		}
	};

	dialog.setTitle("Create Gear");
	dialog.setModal(true);
	dialog.setContentPane( panel );
	panel.setUsedGearNames( getJTableModel().getAllGearNames() );
	//dialog.setSize(460, 400);
	dialog.pack();
	dialog.setLocationRelativeTo(this);
	dialog.show();

	if( panel.getButtonPressed() == DirectGearPanel.PRESSED_OK )
	{
		//add the newly created gear to the JTable
		getJTableModel().addRow( panel.getNewGear() );

		//tell the listeners our panel has changed
		fireInputUpdate();

		//only allow up to MAX_GEAR_COUNT gears per program
		getJButtonCreate().setEnabled( getJTableModel().getRowCount() < MAX_GEAR_COUNT );		
	}

	return;
*/
}
/**
 * Comment
 */
public void jButtonDelete_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxGear().getSelectedItem() != null )
	{
		getJComboBoxGear().removeItemAt(
				getJComboBoxGear().getSelectedIndex() );

		//tell the listeners our panel has changed
		fireInputUpdate();

		//we may need to show/hide the DirectModifyGearPanel panel
		getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
	}
	else
		javax.swing.JOptionPane.showMessageDialog(
			this,
			"A gear must be selected in the Gear drop down box.",
			"Unable to Delete Gear",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );
	

	return;
}
/**
 * Comment
 */
public void jComboBoxGear_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	if( itemEvent != null )
	{
		if( itemEvent.getStateChange() == itemEvent.DESELECTED )
			getDirectModifyGearPanel().getValue( itemEvent.getItem() );


		if( itemEvent.getStateChange() == itemEvent.SELECTED )
		{
			getDirectModifyGearPanel().setValue( itemEvent.getItem() );
		}
		
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
public void setValue(Object o) 
{
	LMProgramDirect program = (LMProgramDirect)o;

	for( int i = 0; i < program.getLmProgramDirectGearVector().size(); i++ )
	{
		getJComboBoxGear().addItem(
				(com.cannontech.database.db.device.lm.LMProgramDirectGear)program.getLmProgramDirectGearVector().get(i) );
	}


	if( getJComboBoxGear().getItemCount() > 0 )
		getJComboBoxGear().setSelectedIndex( 0 );

	//we may need to show/hide the DirectModifyGearPanel panel
	getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G84F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BD494D716BA71C3A2B118E0A4EEF2E20C13E54E3A93E7C24E3A09BB4730E60E2E4E0C59B51BE4564C12D9B6E14F180CB3B199B3E1CF9E8DB408BF9494D48C9D440883360018704BAA029F14A41DE8101945E8C197F4E1F7D7DBDD8D4DC759FB5F2BD7DDB45D001E13751CEB553B6F3D7B6E3D6F7E5E2B0664964ECBCFB165921212CE34FF1F269092D5CB48E32FC73C4EF11344F8B3B17C7B88E087
	316DCE03E79474D5BF9AEF4EA66B52B2213DG5A51F3634DF9701EC3FE1716D885AFE156D982FD33DE1971133157F9B7072F13C15B13D23740F39BC0AC607039C14475E3D273B57CAA8D4FA145C4C8E2970EF9AA35C043D5C35B86B08FA02E0B0DDF854FCDF4652935555A3876D9734806FFBAE45A0AF48CB4B914F53339B66DE9A1CF26EC6C463556D2BEF1CE84341782207029E473F083BC6BED573B7F27D3F6D9BB8537DBF62BE227435AA78D880A5DEA97FB25CE516AF4F99439DB91DC3535AD1F5B58EB1324
	081D2ACDF00BCE2021BAF01DE22A77D42489EDA2A16EE7FEDCFBB134D783A4697868FE44B741FBBB00AAF1F9BFBBD42F4953B678A4C9FE491D3ABD0C3C119213B3CE8A489B6D7BE3469E3AB71F6C42BDD88FFD39BD7166920096GA7C0A440BEEA931F657E891E8D9DEA236CF2496E860F6714532EFCAE5BC5A73C5756C20F067BDA6C1495BBA1EC5FFF1DDA96021F31004DDBFF5B7318CEE20FF19BD8787593E46DD397CC99A1F432B6365C34200769E2C9CFC0A7EC6D43E96156DE5BB352C6C72EFD199857DEB7
	CDCECD8F31F65C0B07CD6BB5BAE95783EBD7DD473DEE20FBDD8C6FA91AFDB07C850A779B704CDE7F055A8C36A320EF5DF5EE9BED5BB8AFADE9F3C9F6C5CBC8BD4444B718ECCE460343994CCB66G520EF632B5B3F4DEF2A9FE21814FF819CBF94476B268ABF1451B19FD1D48C37F0D04F6AC40D2001781B682643AE11C1B5B58345C239334B11B202827FC1253AEAA044DBD31EBB7BCC53F2A884DEE2FAA38BB45D6C111048E2768A55A18F33B10370E9173BE27365F89EB1C955D22A228126C866A02CBD4C5454B
	46346E5A01E3941176DE12DCA2AC607290CE775BDDB970F48ADE759B0FDDD0454088565F358B7724D7E8850AC0G5E19DE1C79282FF3A07FB5G0F2E0757A83D8B228281834208ACDF77F9AE21A4E4299B75FC12460EA9707E301B6B583187F1E9F23C39C066F472F76FB450E912BA11FDC16967BEF6369FE3F4394CED662DB6EEB3CDDB3FCF4ADEBC9F52E6E85C14B51B1103FD67ABCA9379E26DD77CED3769479C4A65503EB6BAD67C7E0E57C81D4098265F577662DEC581CF8B81D669BA3E385B42EC76146095
	3915FE29000109760046991F5B8638FFEC52EC076177D2FCA2505C6209B7D7F9386E6675874B45670E47733E890CE1EB7C7D28B5D0A769931CBF91BEF2C8F39E66A531D7F42BCD22C7F45B61452B755972D09E4DEF3BE4FDCEAB45FB404F9B41313CBA3EBD8FFDD1D0D5C56A702962C5D1D5A5F7F7205FCE57727ABABC019041573A1E07F249FD60677AF8CF9E7AC8276CF60BF43374A9DA3FC2FDD4F40AAEE03AD17639D5067767231DDF977B3598B1140FC5D02F6074E13CE1B543A2E1B34A20C8AEB0818CD9E4
	29877D77F2DA3EC1C76D00435AA79A1E319E6E0BFD8738AF968FBCC616DEBDE88A15CBE23E5FE44A3C417C2460C6409799CF2EBD21F97752D821613D299477233FC021C7C215AC4C7E7796A02DFAD843866038416D7F37FB301F47984D709BE5271329D357739BFB33F1DF449BBE91C2B386338B3A6E5E5A3BB3282FD553A36BFFF7AF6A51E90803A795546367833CAE1C847C44GAC3E41F439D153859B37E3100F0B047682A5B0EE19E29C773CBEEE0BB75EDC8C5064E563A6F959B8362F99D731BE88D58BF67B
	A42BCF15B05A8AAA98344747F448EA3E67E85CC69A4B3D3C36FBB38375CA54F8F3BEC0394A777F052D3C360B3EBE0FA4BC3EB7ECADFBCEE53CB955407EDB6E62351DFDBF2E39907A1628FC4FEE6CB36617862FD76E14E8EA5374EDA9B05A87439DAC4802E71724F855E6F0E0BF5F1B52826497F31F0656EA01DE9A53D0565530EE268FEAF69F621F3819EB607DA4609CG11G4C8EDF9D28C47F2646E3E58F1B28F4490ACB341774883D02D568D32DB416D89BE877E9FCBF4C77A6FBBB6A2D6714CFD5E5F7934820
	0AD79B0248340427D3F7381569FFFE4EC90D47D5BF1CCB6A07F3CC21EA6B086D7BCDC9BE26776C5E007E07FEECCEDD776D7B99CC973503DC976DB0A63A176B62D0FBA8DD2C6A65E70E8BBE03AC0DD8FE5C2EACEC7D3774759300F6FE9FDF7F1FEF9957377531FA425F4777621BA12C47A4D5F4D9713F0BAAB05068905C5D233722997A350DE03CAFC82DC35E21126B10CF497E332220DCED8EA6924C3CCE85D8577253CFC7556211FD013AE0DE5F689A015528C2A64A9C5747EA54CDBA5ECD712B8DF8262337299F
	E2FB9D74597C3C2EFA7BC30C55F150DE8D1084C07A634D9600137D5C1F1EAAB8C64629DB1BA1BEA8EE41F9F1C4E252FCC85A0F7A06DC2235DD7BD107B417901E2707860275505EBFD2F6FFD01B4D3BE7543C8F035A8F874DE37B377D629E9E63F0137CD6EF9F64595A5A734C9A210F653258CCE3AE0B6BE7B674B2BC57813038776ACD0CA768BFE983BC3E5DBCE30CEFCB36D71AB60709EF0E8146DF44C0403F4A74336BA3960C594B21AF91A099A0FFB05EDC8F5086B0B44877688F7BBF9BEF0F2865DD129B81F7
	8EF2CDE3F0CD206B7464601D6D459723660D5A0B411173187E3EED4F54E8448D725AF0439D72E09A9A0F07F503216C215B39AF34BDF08F57F85DB44C6FB55206602CC74FAB2F523CD78FED1B0EFB460CB8955A11C3DC266ECC2C9196C1FB95C0A2G39497BD45A0771317826161B753E77B25A28C90DFFDEC0C03AFC0D090CE7EB6C4C44B5A6D2FB73B9932D2391180DA6FF983ECECEAD8CDDA7EFAA8CDDA7EFAF5C4170C043F8DA667DF905E357510505216A687DD42E953B37D35ADE2F6825CE03450E5677063A
	0B0428273F00833526930817831B34B127D53931CA3731378F670736B1C38D151049EBA847CDDEC37DF688E377BCE8AF86D82D5B452FEF861FED1220AFD933136CDB21773FB8843E9AF0ADG578CFDAC7E9497E18ED5819F85908B3094A061960FBBCF970E1B9BCE7B47488DFFED89EF93BF34043609B88B560ADE5927F40A417B7E2CA5DF6F8BC4B8CC3C38C5EC4CB2BADF9514EEF1145D2C2473D9DF3879EBAC61EC0B6DFF5F20F17F0F6BBE56599E9C1B48F03CB9F718693CE4F8743EB0FA25C3C6FA95BA3DED
	43E35DA156072537A18D4FF2CE21DF762904AC99C69B5B1C4E731C8D5A8E00A8008543AC5FB17B7DD19F6630AEB8BE013897BDE2A7091904F3DFB07339E9504E872886E88370CF6235560EB328F3E669AC458674353186EB6F73ADC2076844C20A63EF3651B9469ACB1F735FC568076C988756A6F575E3B73F03E6755FD7D70C75DF42A4A6CB5AE430E11A5B5FB8E64C6D7913D93F831EC313390F7DE7E6300E23A787749ABBB9DCFD56F993FDF649E4D607256BF8B7452FB660D9FD764E902FE193216F5C77F89E
	C93BC54FB75856F11FD35C9AE8A76A389FD35CFAE8E75E45F19F7B90D7G6DEA9D772F8A4FCBADBA6EDD0A73C0FB0A0EBB61C5DC8C34975D45FD3D2CA83C2F9EAB8A6D2BB5C521637709223163775922F0BE46789BE867FAD926737C13F62E1717F5DC6195AEEF220EBBB84CF119D360ACCD75FFAC1DFF97E983DC5494EE739557307E52E3B696733535E79549FECA68EE219E446276E0FA26B6FF79949E37FFC0E912296093G65D3F9ED687F1857068B1F18CBDAA6DD88F976CD38D4E9BAB7D5BB7BCE8D540631
	D09B32B57FE066EB46C2FF42D4AADB06516651D78E40FFD624C77BAF0F68C79DE3DE8B745FBD229F795DB1E27E33A37AD1C7073479CC36158739EC49ABE30973691A30677A12E9CC26966DE9EBBDC00A6FF9BFC34B15DDE1F2A50D92CC7E08AB3CEE1CB20D6F57EF361DA237799DABCC90E1EB94D671B516CC637B3A17FE1358G6DAD5321D61B4E6DAA19666EFAE85BF45C131B392D2953F9CC1168F9E1993493F41CCAF1F8470377B78C777ED01BB6AED4FCCF88031FA830B5F2E98D916E1EF82275DDB8E031FA
	9F0DCDAE378885025439A58275D2C9F1AB50CE26739CE13F05F70EAA8491DB84FEF37D5B43C427316296669E176017DCBE57C5E9C0A40C4EEB14CEB43C4F0B60F4BE73214DF99431CBD49451CEC793EDFCAB0D092B607DE500343B699D1512BF2E8CDB3D28AF510D57F3F60DD631171F85DA6E66EB37FAA91F12BB08CFB79D5B8E639CG8C77B80DD75311FF40B119D763E7C842787A644AD8FC3DCC47548FB70DB1A6DD630F4382183398600056378A5ED7E87E496C21A67D4E3EE71E3D43F913EEFB9E4B2303E6
	74C14BBDAC0F266878A994DFE64033787E249E5F2B216F61FB787D47D32528E3B2A35EDCGD08550826084081841735E2BC565E41C9A77126CE9014468B424B2A6DF32794E3E73BE174163E85CB3734962E96F25043BBFF8F806761DD7FBA6DDBAC071CC3EB3C7D03EF550179999EFAE83E882E88370C4F2794A8F14B179CE5BA535C1D505CE873D99654235CAE2DFG0D059F136D5EABC65926C43275E3346742485171094DAB2E6273164218B5C16312C24CE3FBBB13465D644860BD1FC3711977866F39454F77
	BC977A7C77F29F4D59B43A7E7D53A66EEBEBE15C860002191033E7B2DE182F7DF473D83E162A4947FD6E9C4CED1F9970B54FCC465FFB9FFC7081634376952DEF0DF73904471B0F95C39F3B9B557B1AEF6173107D10E6AB737A0C3F8B294E7AB464379404A1CD1EA86D39E47A810D46AF7E57C8A3FB6EC72614B0B416EBF3574C8C54A4ACF67DBBDE2772186F43FB973758E7516FC01BA4B09F7CC0DCD2DDD1E629BC8E511D4D5BDE911837D21948BC467372AB46DA43D21A96325658F86528A973BE46EB59FD811E
	5774A4E730B56B3CB46FA8BE117153D79960A72BFDA27C30314F3453FDE9BCC48AE99BEC1270FF6BCF0C46F1795F41A334A653EBDB38CC85FF73D724692577D28E045907EB1AAC0B8CB2A501CC4C47A6535FACE9F724067727536F74DB7244E37A4F3F53B5D84CDAD29A7EAC720F25214FA2ABCAC31FC5FED1BA76DD51CB25613F397E2A54784D15455CAD4731C6CE02FDC90B0AB717G54835822F83CF51695919AEFC3445A5376EE3C3740DFF828FC7DAF0F0417357548783F8B39FA844F579DB20492975267F8
	71886E17AAFBA81261FA0E200E94295B2132217CBB729134DB2758C5512C36DDC06BEFA748B594456B5DDFD6A0AE8A5A73F45C09CADE83AF54F1F5342ECE06F67EAC0EE335D99934CF6A38AF5B78DC7BACDE97C77B83767C08617D63BB7E1D4444ED7B44F73A864BDB19D9BCEF45014C6BE671334D314A915FAC58D9740CD36EEB51D219F6E631E571BB44627B7919E55BE624598EED95A092A0D4BC8A9F2362BEA13AFD8B1F525FD4820D2577F31BCE31164233D568512AA3ACA5E405DAB5A0ED0A6C9195351FE1
	6957EF0DD6BB269DFE66379E8CC783BC63BC087CE5C78F9C55AE895DBAAF5E9A4C5B8940C7B2006501F8F3B5C08B007D817DB7AC95A5E3704614C7FDCE91A4374A683ED40B395E8FB4228196832C86C884C886480E06F39FC0B5008D4081B084908530GA08EE0B9405AE84EC3ED5561B1F850AE9482CC68BA49BD9E5E77779D9F5F770B0F53EF5D6033177ABD5A7D6561631890642EAE2F2847986B71EC9D576C3549DE896FBDD9DEFB768A6647AEB8C733F34B8B546F5703EC49513C267ADD4548335BD3184634
	3A2935F23C3A49880B8DE7947401325971662D067E9C72F5D578735DF59AD72AE1CE53EC4E1B4B0772F6488A3DE0E2755EFDBEDE6FF9E0DC8440D200B53359F79746F7D75BF8FC2713C03D378E6612878375DEAE3C3FE302EA42245F57E65D599929608E67FDB366BC8B70D7756048DA1B55535F2331625C0341F5768C0A3F74E0F01D1D3B19472BEB50376CC1EE73DD1FA2CDBD2FB237675F00BF459C8947E4D5921C52G7DF946C598686E56FEEFD4935E6E6F2A995F6EA3EBC2673C7BEBC6673CB9B521F35EA3
	B5461C47F03F298D4F576FEB47676B7D5A50FC25540D664BDC971A2FADF523795AD107B6578DF124CD9F37DB477DD9476D23B491F7D9322B8E9D7FD1DD1606BFA762029CFF087255C7F1ED9A2E4C007B33062B28C39F83119D324267FED2C7630664F4EAB1100E115C90E2F4FD1C20BCCA9E4140F7130EC33ED9BC3162351E667FBF2F407AE9C4CDFE8602D3C9119DECC96EAEAA331417D9AA2A7473C5FBA35169506B920D4E851FA518CE2036BFD6F19C0854D49DE6B67EF7BE7EBB6412B97166EBF3F8CEFEE3BF
	467531FFABF5C9D21D62A9AD7CE8B97A839F4F51B1F3F80EFE78B27DB6G6DA444455CEE0E1EFF9A5FD95D6424B5C0BE50A8096135D49C4555C33BA50657D291F82D20450BD906773D1B3F7BDA6A587F439A7F609BAFF6DE0B8975BB0215634E8B7EDD811B17560B7306E2A6F6B79B8563D8EC4DEE433DDF90939CF377D07C22186018BB7FB20F39CB204F72904FF5F7E5214F75F68B9E168F9F22FB0EBEBFC59F33120EF168E3DE23E370FBDAB40CE15F391766E00E155CCE49ADB632DF0F6B36352F1756FFB0F6
	49C35C360AAFA3EEB534939F6236353BADE0CF050677584B5F7D3EBF771D2E417438A80B6E135E5E1885291ED41473F6E5AF6ADDC56F4778C4F80D125DCF6BA750D3323EA7F5175379D8427D7979B3C4FF971A093ECE7E650946BF7677CE7F15B39BB6FC611BA4DDA5E6F64F2412CD6CD249EA35FA9532F3F4GEC18794ACB8B2FB6651A082919189DB458B71374BE4C9AFE7C5BG2C133F725EA03708A9CCCBA15BF53A4DB46AA7749E2BCD7D0F47D3CDA48D0832AC58CC52B01581ED052D118AD9102E005A45FF
	98B5CD1436D953815ABE662B94152463A71BDAD8EB6B88E5BC7F587157D77D2A551BCA4C4DE48F562E97C505169A229DFF4E217D2D42993249A3C33D0DB5A4B062250FF42F5A6F94DBC8EE5757781BDD2FAA5A9BF89163D549AE7A333E23985B0F1E933C8E9593FCDD8BC9F363DFD5101D2C5BA9383B0F320A260EC953F00DCA0376C3005F9C4AAFD2316AD4F21E5DFE702373090BD309698C49F24A027D0C50896704E632C9F5A82257A1BB6DFE4F091314D0E69F1A91466CD4A037CF979F0535DE02B2C4E77A3A
	7F11482DC9DB4F40567895F2140E44D25D2A0F30B7BA8485169215E6957FCAC4D60A753E499F2A9F7F6CECDE3B0924E8C262D6EC71FB401A3A8417646CC7332226C7B7A99B0C8BCAA03797FF42891CD902B863DD7A825687D6144F7E419F7FC29788CCD5C9A1D3A5CCBD5AAD4A2E238DDDDD900EF48220D7A03FDFA70FE32CC1E3F67AE76DBA7F62879DE087CDA455D5DECE7F2F207FD7627F9594D3C1B19515205C75E44CFF417DFEAEB3B59E5D66F17B2DF0B69754DF3E39E27E3F552FDF87521DA16974D70F30
	895901D1DF78DC9D22B2ED616C7D1B2393E6609EF950D47CC8B757BF426E3334C5D3F5CF0B87432A5347709C01E3CDAA590A3EE16177D9F4D456A8CBC46C3651A605681D7A46E2EB979D1AC77F5FFB5BEA42D985F46E073AF4077072065DFB9E9EB65EBFEF1C1A9F7E8E7BD16D8E7B51403DA7BB331F4D235F76G1F74A8BF034C59FD04043FD33868921CCED5F0DEEFF05B71FE61D43FAA866E7DDA72103E170F51BA35BB857A779BBD06B97F87D0CB8788A0373FB71C95GG38BAGGD0CB818294G94G88G
	88G84F954ACA0373FB71C95GG38BAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5695GGGG
**end of data**/
}
}
