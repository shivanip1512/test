package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class LMProgramDirectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, java.awt.event.ItemListener {
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JButton ivjJButtonDelete = null;
	private DirectModifyGearPanel ivjDirectModifyGearPanel = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private PaoType programType;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramDirectPanel.this.getJButtonDelete()) 
				connEtoC4(e);
			if (e.getSource() == LMProgramDirectPanel.this.getJButtonCreate()) 
				connEtoC5(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == LMProgramDirectPanel.this.getJComboBoxGear()) 
				connEtoC1(e);
		};
	};

public LMProgramDirectPanel(PaoType programType) {
	super();
	this.programType = programType;
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
			ivjDirectModifyGearPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel(programType);
			ivjDirectModifyGearPanel.setName("DirectModifyGearPanel");
			ivjDirectModifyGearPanel.setPreferredSize(new java.awt.Dimension(336, 266));
			ivjDirectModifyGearPanel.setMinimumSize(new java.awt.Dimension(336, 266));
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
			ivjJButtonCreate.setMnemonic('a');
			ivjJButtonCreate.setText("Add");
			
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJPanelButtons.setPreferredSize(new java.awt.Dimension(336, 29));
			ivjJPanelButtons.setLayout(getJPanelButtonsFlowLayout());
			ivjJPanelButtons.setMinimumSize(new java.awt.Dimension(336, 29));
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
		if( getDirectModifyGearPanel().getGearType() == GearControlMethod.Latching 
			 || ((LMProgramDirectGear)getJComboBoxGear().getItemAt(i)).getControlMethod() 
			 == GearControlMethod.Latching )
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
	getJButtonDelete().addActionListener(ivjEventHandler);
	getJButtonCreate().addActionListener(ivjEventHandler);
	getJComboBoxGear().addItemListener(ivjEventHandler);
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
		setPreferredSize(new java.awt.Dimension(392, 354));
		setLayout(new java.awt.GridBagLayout());
		setSize(392, 354);
		setMinimumSize(new java.awt.Dimension(0, 0));
		setMaximumSize(new java.awt.Dimension(392, 354));

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.weightx = 1.0;
		constraintsJPanelButtons.weighty = 1.0;
		constraintsJPanelButtons.insets = new java.awt.Insets(3, 52, 17, 4);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
		constraintsJLabelGear.gridx = 1; constraintsJLabelGear.gridy = 1;
		constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGear.ipadx = 6;
		constraintsJLabelGear.ipady = 1;
		constraintsJLabelGear.insets = new java.awt.Insets(14, 11, 1, 339);
		add(getJLabelGear(), constraintsJLabelGear);

		java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
		constraintsJComboBoxGear.gridx = 1; constraintsJComboBoxGear.gridy = 1;
		constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxGear.weightx = 1.0;
		constraintsJComboBoxGear.ipadx = 100;
		constraintsJComboBoxGear.ipady = -3;
		constraintsJComboBoxGear.insets = new java.awt.Insets(13, 52, 2, 114);
		add(getJComboBoxGear(), constraintsJComboBoxGear);

		java.awt.GridBagConstraints constraintsDirectModifyGearPanel = new java.awt.GridBagConstraints();
		constraintsDirectModifyGearPanel.gridx = 1; constraintsDirectModifyGearPanel.gridy = 2;
		constraintsDirectModifyGearPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDirectModifyGearPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDirectModifyGearPanel.weightx = 1.0;
		constraintsDirectModifyGearPanel.weighty = 1.0;
		constraintsDirectModifyGearPanel.ipady = 100;
		constraintsDirectModifyGearPanel.insets = new java.awt.Insets(2, 5, 2, 4);
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
		 && getDirectModifyGearPanel().getGearType() !=
			 ((LMProgramDirectGear)getJComboBoxGear().getSelectedItem()).getControlMethod() )
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
	if( getJComboBoxGear().getItemCount() >= IlmDefines.MAX_GEAR_COUNT )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"A Direct Program is only allowed " + IlmDefines.MAX_GEAR_COUNT + 
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


	DirectModifyGearPanel p = new DirectModifyGearPanel(programType);

	OkCancelDialog d = new OkCancelDialog(SwingUtil.getParentFrame(this), "Gear Creation", true, p);

	d.setSize(500, 500);
	d.setLocationRelativeTo( this );
	d.setVisible(true);

	if( d.getButtonPressed() == d.OK_PRESSED )
	{
		LMProgramDirectGear g = (LMProgramDirectGear)p.getValue(null);

		if( getJComboBoxGear().getItemCount() >= 1 
			 && g.getControlMethod() == GearControlMethod.Latching )
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
}

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

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJComboBoxGear().requestFocus(); 
        } 
    });    
}

}
