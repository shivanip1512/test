package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGearDefines;

public class LMProgramDirectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, java.awt.event.ItemListener {
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
			ivjDirectModifyGearPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel();
			ivjDirectModifyGearPanel.setName("DirectModifyGearPanel");
			ivjDirectModifyGearPanel.setPreferredSize(new java.awt.Dimension(315, 194));
			//ivjDirectModifyGearPanel.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
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
	{
		int index = getJComboBoxGear().getSelectedIndex();
		getJComboBoxGear().insertItemAt(getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() ), index);
		getJComboBoxGear().removeItemAt(index+1);
	}
		 
		
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
		setSize(392, 472);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 2;
		constraintsJPanelButtons.gridwidth = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.ipady = -4;
		constraintsJPanelButtons.insets = new java.awt.Insets(1, 12, 3, 10);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
		constraintsJLabelGear.gridx = 0; constraintsJLabelGear.gridy = 0;
		constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGear.ipadx = 6;
		constraintsJLabelGear.ipady = 1;
		constraintsJLabelGear.insets = new java.awt.Insets(3, 10, 3, 0);
		add(getJLabelGear(), constraintsJLabelGear);

		java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
		constraintsJComboBoxGear.gridx = 1; constraintsJComboBoxGear.gridy = 0;
		constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxGear.weightx = 1.0;
		constraintsJComboBoxGear.ipadx = 100;
		constraintsJComboBoxGear.insets = new java.awt.Insets(2, 0, 1, 114);
		add(getJComboBoxGear(), constraintsJComboBoxGear);

		java.awt.GridBagConstraints constraintsDirectModifyGearPanel = new java.awt.GridBagConstraints();
		constraintsDirectModifyGearPanel.gridx = 1; constraintsDirectModifyGearPanel.gridy = 1;
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
public void inputUpdate(PropertyPanelEvent event)
{
	
	if( getJComboBoxGear().getSelectedItem() != null 
		 && !getDirectModifyGearPanel().getGearType().equalsIgnoreCase(
			 ((LMProgramDirectGear)getJComboBoxGear().getSelectedItem()).getControlMethod()) )
	{
		int index = getJComboBoxGear().getSelectedIndex();
		getJComboBoxGear().insertItemAt(getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() ), index);
		getJComboBoxGear().removeItemAt(index+1);
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
	if( getJComboBoxGear().getItemCount() >= LMProgramDirectGearDefines.MAX_GEAR_COUNT )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"A Direct Program is only allowed " + LMProgramDirectGearDefines.MAX_GEAR_COUNT + 
			" or less gears to be defined for it.",
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
	D0CB838494G88G88G0DF36CACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD8D45715597DCCEA37C9EADB13B5D9531044B6EC42267A555D5A44742369241FA62625DD53380969C7DB5A5A0DC9CDE2F7CD63EEAE434F008C08CA8C71A722A8C2449194A38E78430F8808BA910218104803F940684C3C491BB7B07C08FB4E3D6F3EF98CEFG696772FD07776E39771EFB6E39671E1F7B6E101C9DD1A909669CC292D3087AF7A411106413042C78E15A1FB92EE33B49C2F4FFBB81
	B610839B126169033A5B5BCD16EC726659B4A8CF43F21B49729E3CEFA2EB4E2E3543CB04F116C25DD71645540EBF4ED3BEBE0E0516BD03D6F8E681CC87DC8C401A6DB87E2D0339AABE8E4AD96ABBC2221910588F304D0E2CDC9D3E728356EEA6BC23811CBB30CD5460B635BE8D4A58A68F1EE5BBD85BA6F826D36EEE35172B6D5A3FB1132CFEE633B993475251679023760DD31FF9645BD490C964257EC66443F13567F8176431F589DE2F64D5442EDE1B33D3F43A94C936753B868559E9F30AFD2EAE51667678E4
	29C796BCF6FB53D987FB2DF549E21762903C221B1013F9B807D3D9E914BE4C814ACBA86E49AC145BB4F81F8DB0BF0F4F63971915AA1FB32F4EA4718F25C71CC776CE46FF594E50BC16BD5BEFCE5809749753F541FE8A14E3G98AF9B06B8AF053BB8AF4EF55836834AC100999AFEDF866223219C8B30EC97677158DE4EE35B50C3A4E1FFC16A7A88BC2E5A45F84B4D8F71688866273ECFF5671C0C6B5F82F5C1005B81668244812C8258CAF5B67D4B8F61D9552954C89E0F642D72792A5DCE792C64945D70EE37C3
	0D0A3BA0F6C932139026170DD945867CF0C85BBDDAA791585CAE922EC7B1976EA54E8D5949A986F31B7FFC2EF91BCA23E4F7E8EE3D3BD1EE256BD1EE8EF847FE488F439F2378989D1E2D55CF28AEE05984F505FB381C5B32B8AFCD49F7126C528A33450017D9CF1518EF5F43F81833A7448B13F12102B20E837CCA00648213E59BC0A5C0FB8117F1731707A612F1836C8359AB381B831DBEC1D62A83AE37D314D5D97FFCF0BCD99B816B77728D77E3F209BE24DF23655F379B4AE5F9748EF3300049E3C6C1C8AEEC
	6C4DC311461EDBB0D6B73234B5DCC6770A29002DE112067FAD45AF5561595ADE21F8DA063A7A3DA68BEBFFBF5DDBB1FB43F564D10AB76D8D5711528C2EA34B216EE421494256772781340FF3209C8B308CE095C0F61149D28CF032082F71373FBCB049FD64502DAF6BFBD15E8CCFB12848C203572F885EAE31D510DDC227DB749335CD070C3CF50E6AA7527D5D85E31C963D22ACA8AE498B54850F884A64E7ED3C7286ECA30B3436456591E1G0F0FF03AFD72A6F83A853F72094FA9A8E228852B9F11D1A77A04D6
	208884F02D0BD0DE8235399D70BEDC44E575E69F6216C2F925A61737640D70BC834AAD3A6D76DAD79732A14883DCC70F50BDCBBE841F846058477766EBEDDC87EBB36FA1454F9FB554C104557B18BED90B43F5300652AE83FC8B00D24CFD6A622CB6F54C9B599B6190ED509F79E7E37C160E1174B750C7C8B140A1FDA6EFA108731B89731A8B90D74CE5DE664FE33AD8AD78C52EFD67E5D09C5199DA815636D4C15FA27609DE25D67409DEA73C785535AFD752C3FA59ADE9BAF31C6251CE55006278B5FC2D320161
	0106DE4BF9FD932D9794C5F6F5869431D9D4941737A7543F0572629FE5827D6ADC3B86F02EABE00EE4BF685FFEBE571715CDBA7DEA12242B81DF8B6E9CD5CF77D3FF3C9B5AD76E67EB736BF598E3F5E86595349C2A772DEB1B62FAA14D4959662FCEF90C4968949B63BB1ACDCBD7E54C7073A87E9660B59AA0CE1B77110CF01EF85F09465BBAE91E9CE346C80A2067C67DA74347A69297082C53AF840CF57A650031CE7FAEB03E4E3E92B052D9D677C6G67A575FBF5FB603F83E88F3BA42FD7249BD16BC2D80C7C
	DA8B4E4365F5A997DD4E9ED1E9F68D0AE053EC284BC1EAF35740FAD9ED5CCE7667F3BB39FB70AE92FBB947EC94CF4D7A4EA1335346F4B3E88B59C946531ABE548751ADFAC0D0B5D2402BB07C7FD2FB2E627DC99477DE713D58373031BA20A8123796EA9531D820F3313107C3143BA559A3BA8B2F88FD02CD68D7ECF4ADECD5347A943E97656D5FFFBC4FA828AF3E2227FC39AA0CE4D3E41A95C52DC50718BEEDA27F3EE1941FB568GEF8A1F0C720D72D94679FC3B7D9F991F90B7F4CA55D27034A84845AEC57458
	70DF338244EBFA85EF4FD8B69B20FEC2A675E4AFB704D38C67D2A309BC169ED2ED6F5DB4571985FA9383E032F15BBB580F755C762A8E26C6F23B19EAC3DD6181545B3E75B9BADDDCEEE3F9EC16D67F608475BF0BD84F786889E6639E943F8C0890BA2171BFA38E7078A738B12C2ED5F087C2F1D5DFB0831EEE1D5FE0F85700711E108650D7DC55687B87D28946C70CA65B43F385246913DD9EF033688855FC3CF6C81F0F7BE0EE183B4F0527890069433CCFF12EC6EBB93B84E9A4E470FCFDB974D98D10D3426834
	14B01930F651D65EEE8514C9E928DDDE293EDDFBA6EF578B78DBG22CAD93B65EA3B5D87315D57359C7B00067BAE457D998C7362E41EBB1C4D561F9374AEF9A349489E51718EB27A438743F32AE7294F1E8D781803DC57DE9D507B79AA3FDF6AF25110D6DD2F5903FADB45F08F8C2E03E737CB76C3FE659403FC4D9F9EC4DEB166D5516A3ABD3A6F98999B431F22A793BC0C2A69277B23C52A01303B9E5426C6B3652DAE311FF0B9DC2B531FB3ACDC3F531C91C18E316A7C63F5F248019C1E71F40F9DF3C723B313
	AE4A242DA7603285949786570282BE4D97D08FC758F944E3D416C8AB6F90BFF7F8F184F53B8D4ABE0069GAC874A34DA5536D100DB8640765FBFD56877DFF61949D28E50DE46F5D6319E04670D5BD6AE776C75A82B3F456EB319BF3869244A6312476EA415574DE623F313696B2DE6DF9913752C32104CD75F1F1F1A96151F4AE4718BCD968950E65BE1AE0B7C94A3D934C0BD1B4B1900EEAED3728DEC7CDBB379783DC0BBCA9BFFF33EFE7CE507D9DCCBBE62EB31B29377678D7A8FF59D66A4D5A06FD3F7997C0C
	67205E864446679946A488B99F29F67E23F09B703DEB28373500EF7B085B00DDC39BF4B660826CBAE089AC3696778DA5B15B2C16178E25F05BCCF84E34F2846567861A538122G96GAC534678ECF842331AD341F14EEA3EBF665CE34DC7211C40FAE4ECFE404EA54EDD47754EBB424EA552B57CE794DF2943B37D0827FB984BEDD03770880F61B7D943474FAB9FBDE6E5D819755BB9265F87E1650E30FECCE756B73F4F6D93AAC650666F077858EED763AE424F226F3A2E77FD41F226373361B9371C1F1D7E3282
	579C776C22F2FE26D0CD50BF959FB5D96A81BAG068122G9682449F656B764830116D9E3DEE118CF7A8CFE532F8E10AB27C7D04B2A476517D583CBFCDC9D3E9645A79BAFEADF3EABC5CB326DFB88F8E3B51BA7638379A2FA33786AA2FE773F99C50EB67EBF4C7A2622220BC574E5743F2ADD337D79D124B2BE87BF47EB50C19A459556372F25C8FA92ECB12E93244F02BC7A267F1EF0F9867F109570D73B84B75714FA656DF0F146731F304E7289F0D0579AD85E03807290DDA83656C8A3EEEAD57B196AD033283
	2097E01AD6A750BA74975195AC56DA24557DE18473E2EC9BEE9F0CG6972B1A6531E0D718E9DE3B279BC6FB3595C9CE1BBED3FF873FAAAE73FDF74538D07D109DA77C765DDE2107B1E93BCA26179D023B5DDEDF3CA11EA4AB47DFC33A857D8BFE93043F473396BBC56D9D941E31D84BB5A495D15E0EFGDAAA393CDF2888BF07F0C35DAD156AB732CAE339C69B609782AE8EA0C1D7271EF711487A5BCC0C75779241FD631782F217982E239DA4D72B8BD9AFF440A8C2B5EF217D95C16E914768380F76E7F5117A8F
	10C87B002DCFD626FEFD8EEB3AF5B9E50C5F83F9D80F29FE7D58D839B1FACF0D22D72A513B10BF5E370BB283FA0C7FEFA6221F8CEA653BE9F9C09B6FB7C3E8E75C420094G03B47F986A489AADAF2B07F28740F4005947180FE27AB72F9F6377EEC849C09C4DBE310B4CB80EFDFFF40D77DD0B34GD78260G709D67B15906BADC9326294CAD06643966A866A80DCDC2276846000B632F3551BE7AD8CC6B73BF3BF09F315490BC1C2BFBG2B55CF22EA4EFCB6F5D41CF81C4DE5ADBC19BF163F507B636C932C3E97
	1E43A7789E79657570B518F9A2A477689311E219368A5CF38BCF3018A5C543F7D1FC1C8E4FE2197F34EB31B654551F647675AD9A8F44C0B9D643D5D752EF8CD0CE50F08FD35CAAA827D5F15C798F91378D4A659AEE3F446DF51306BBCBF1BEA8CF53F0F7D35CACA84F55F0FDA91C971306FBBC057332CC43EDCD65E3A4E8B8D7BE4725D573DCABEF18FFBFEE835C0CEA2EE7259D986FE8F68E83ED3B7D346CF2D68BBDCDD4EB192D6B9DCED37BAF2A66366EEECA135418AC39GA5B57C0CB0D840F351186F5EC91A
	EE319BFE730EEF59E92E2FE1EB6A2E894563CE48C559188957781851D09FDF03380DA3FABDC37D1C877FECC80F56570E2AC75B023EA0D45FB72A9E795DB02AFF7428FA1451DE35BF1B5B33FB785C56BEBB1B387F35B062777C42DA75CE037AF4BC390B543F97B42B7E25BB02FF21BB134DFFDAAA0F53265572753A3F638C196A1D86639D4C46FA648BBE5642DA3E3E7352F1FDD7C359FA4AE459FD0A6B571F285F2B0432C343BDE666BA271C627B798B3A1FCCD00E57F0AE0A43F3A4BCA3E238D576B6351D116D0D
	0F001FAC30B152AB467BBEB65918A392B0BB19CD6D0247ABC2C2606A325621DC76D25CE25417D35CBEDF2A44B3BD851C31238E6F643C02CDD49ABF2AC43B6F9102AECF40C38F63991DDFD1BAB361BD2A0E53697E90F54FA70B5D22AC0BCEF6F42F363FCE6D5192F8DF8610FC1A6E8DA9F742B9B8A51417684523C927CAEBC862B1F853E9BE763FC814CF17B70C4F27E95BF6E8578B40F077D33B7595649FF0EC4ED9680688636BE06AF8FCAD23EDEE492F1D00F77E1D9EE18E74D9G102F2E6D92F8DF2C36E17AD0
	B0BC35BB8EE5D36C673961FE4C0775DE23F9FABD73E1099A7ECB0AAF5661191D1F2D5979F2281BDD4FF3265757210CC90349328D208C2089408DB03D0167D0DB4DA5E4021C36C572B5C100695625B4ECFEFF3AB6353B9FBF36F0FBBA7F713B49025BD6A4C6BA371C5D406C40C275396209DD944F66B74D0A73DB89F5E90DA6CBB1C0ADC09B002F114F2F4BDC4C6607C72CD50AA2F4755253DBBEB9BCD08D21B168D23F1D266A67B62DD1BD4BD31FB10DE36D936B773DAFF83FD8E833B42C5D8A03FEECED7750BBA6
	EB9B43575CC671E9E742577C8769FC4DF320AEF8066FD1C246461E6F913E5716C33B55G5B1A40F7B731F3C536571EB6CF3457D2884F13A006053E6DCD213D66EBE27C3D754EBB6FB0BE9C9F5358D2FF5ECA383DA910F5F56C7CD22BBBDE094F3D4E3D2A2EC4756B4F794A5795C4BC679BEED26F89294F052F6DD2E97C7CB3BD0D6CFB334D0991E8ACD27BAEED525DF52336EBA326D9AA0F359F62F907D76C4F534EA9EBDD20BEF8F925303C34B86F6061A2707D34DFF5F6285FAEE9B27D984F0BBE57479CF97B5E
	CCB40AB956FC7E01B96DAC633578EC0867344356E4B666B7A56AF764004878F9E4C3081F54144970435A7EDB8ADD171A3DE4072AE75401513FD5A74662787C8B912D4E69B7D6BE27ED0FFE1D543E30A2B1BF42BAF428F3192B1B536AB2EBB25BE35EEBC80D3D776B5E9F9C1E6AFD07495B74A7EE6A984CE67DF5DD641CBEB143B82737E4981FC9E5E60CFF37605D0C48F78B36E5686F96B01BFBA287E3659530AE4967CC16C200CAG47B96D0CB4E39721765640561EF2766019825EBED37878F7D8A34FF526F562
	BBEB77D9B12868144014F810BE47BFE44575D2A49FC5B2DCB4BD7711DDBD3D8AEB2A629F33225E3A45EE0AE631ED940D435DB02F61F3BC5EDDBA003899D00E52F0C787F98C9C2361FECA636A35D04EBD4FF1A334DDB114CFEA381F0C703E4E73BCAE5E37AE244FE53A77A753EF3EEE7F6A260E417C56DF8877DB73E14EAB4F739C675041D1E77CACA72DF3CB7DCD2AABD3F316E3097C7CED370367ACEFA7A24DF6A8ABG378398592348B66A8DBC6AD0757B7E4DD8BF87E844BA38CE1F485C874FD6614A582F3500
	F44812CF1415810625DF7ED5DA776215FDD547EE4D5C9309866C0CC61872794EAB10293588BD9AAF73F251EF47839FEB8172BE86FF8D5084607C1873F6DBF661B83CB16151BDA78BAE2F42683E3B85FDFD90E84C84D8G908710G3096A07B8264FDG65G8E00DE00E10099GF3G6683AC82D8FE01736059D4B48E8F6A41C20889CDA627F3A26F7DF3B9936F7D0BB974FBBC6C5916811FFA36581EC34F7D3B3B7D22E6E3041CEC8D5760F7C8FE971EC9B23F7603D4740F5D10CF333C65B93A6FD7415C56DE60B1
	55AB8323F33757112C1A23B886470B1BF81C41F314853A9C8571454DA6CB262EFE93F9790B48795DFFD13BD28EFDEA1BB9EFF78D62FCBBA5199E4232F8AFC67B5D068F5ACD870885D85A4C3E85B03EDF9C61FC2FECB6BEB765B19FBEC90B491253920A778A617DAFE6C27A4D5CF63CCD2616A325A7CE2DDF75387D4A003F7A165173E27134E4263622A5BC4E8ED2FCEFCBF81C1DE856FE0381F54BDA384E4F590CB4B53F4A363D2AEF0FEEC69F09776BDC025BB5C82F10B4C3C3EF8F2BFFBCB7325E7FB8F7E23DFF
	A657586745650E75F94F679A7B3C17F275BE0F610A36C4662BE44B44FC9D5DE24C5709ADE3792A5DE24CD7631631FC1D5F02BA5783F624CDEB572261AEE938CB14A662AE3A1CCA2F063FCC79C2FC3D0883F23CC8796A2738B695F7C5073B246224AD3847E04A3D12765DD059C26D064B6DE665A0ED637202095164F10D72687289BA3E7FDE43A15F4C1E3871E7D96A7EDFB400715328183C8E0CD361AEA7681254332BB82F24B82F34CC4BAF6E45666A7B1379A1BA7E413CF0BA21587ED069E1A0F2342C08697852
	C17E7B064ACF4007FE42FD729F3723DDB7BAF78CDD88EAF1A9EE31DAB5BF2A0F5EB6444FF0E3BE61BE3AF18847C80072DA001CD67EBDBEC37B1ED65CFA23FEBBFEB73E33D86A920D8B1CC0C3E96571558394B79D4A33DAF9FC95B0934D060C685EAD661B9FDF657DBF0C7162043F4709E9B57A263FE242FE615F7855BB9D91FF039389183D1DC66F16ACEA8D3743F7D07C5256F0BB6C9862F6B89E6AEAAF72754C9E757DA89A708BAF0EFFF64C7607AF9177EFDF0F60E33E75B62AABE8BF62AEF25F3A003669555A
	7C0C3641E0B19E5A306F4D311B50DF3B3CEE17D72CE13F1251E4738FA3A17D7A166E3DF568666B01F4D347E07BEBAF5D3B2BC0966B13B82E1862525ACC162FA6F1D95FC73F0F16E9327FBD7EFC1414167078C2A830924DDFD2534D705B0219A1BCB75D047D8E76752C5701D84C2BA4C5A1296CFCC9A1696CB049E63379E532F12C612B3A73BFDE0839DC1BE3A66686125ACBA9B61014FE9CB2083FD7427878E37F17E40418A314E432DE235BC02DFDFC5FA1FB526F9ECAB213E4A04A3CDF83C9C6978634E5B6C692
	F8BFBA82CA827F50DA1AA96DD4D5AEA88374D3098AC941AFB5F698ABF31493F87A0143FFD8726BD6FF92C9EDA06FE34C5AAC4AB444901DF885C27D7DD49DC97761DD980C9D0191BFFD247815813758C4F23AAF60BDE53FA2BA2BF87062D7483B744A5D813469876A85FF2F020E3D2209A4FB7117DEE4A32BF68B5E1E83AC1229E033296A2033C11DA0406FA64AAFD231E9D4B6BD31FE4F4E460485C944DCC75639A541D9A7F4C1FE50C05215DED97477CAEEE750F762A4A514560F6A034741C9C0EE2BB6FD1C2C2D
	85428F0D692B417BEE4FDC11D987CB931449815A92C3F41B5642D953AB48B014A8B7A8784BB5C95E2D557D5D87CA41F3275FEBB713C4F512389456208F1488C29C2F9F2FD18287F9E19C70AA0D106DDB0BCB6E7A673FFECA89053AAAE4879399F4BD50A3CA1E83D55D5DE0A2B482A0BFA03FDDA30FEDECE1EDB6863F71EE63736FF442FA5712A4CFC9897DDFCA7F9F447F2594D3CAB12587C1082B4838FF6175C12A00598AD392CD37F119ED10FB8B4A0B2FAEFE70254AD5ABE1F6F5A4055EF284E1E707DA1D8BF8
	BAC57936183B36E74C0C7F9A2E058FD50253CD6D96BCAE77GEED19A3E84911F979C25776BD4E612341D7694A8D60556C61FC3E1ADBBCBAC3A2EBF53DDF32BA56239F0DCBB7387896D6177BF8BB6223265FDEE32D4FE4E637A3E8D7BC964BC3D59A3385D0A603EDA65F5E24EDEBD2008213334FD9B113E1F37D1ABD95D410D487795DD1F7F83D0CB878826038FB38497GGACC0GGD0CB818294G94G88G88G0DF36CAC26038FB38497GGACC0GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBE97GGGG
**end of data**/
}

}
