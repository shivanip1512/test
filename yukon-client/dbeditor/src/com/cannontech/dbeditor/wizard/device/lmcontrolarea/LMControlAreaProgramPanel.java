package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.Vector;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMProgram;

public class LMControlAreaProgramPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JComboBox ivjJComboBoxLMProgram = null;
	private javax.swing.JLabel ivjJLabelPriority = null;
	private javax.swing.JLabel ivjJLabelProgram = null;
	private javax.swing.JLabel ivjJLabelStopOrder = null;
	private javax.swing.JPanel ivjJPanelLMProgram = null;
	private javax.swing.JScrollPane ivjJScrollPaneJTable = null;
	private javax.swing.JLabel ivjJLabelSelectedPrograms = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldStopOrder = null;
	private javax.swing.JTable ivjJTableProgram = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMControlAreaProgramPanel() {
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
	if (e.getSource() == getJButtonAdd()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaProgramPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaProgramPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JComboBoxLMProgram property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxLMProgram() {
	if (ivjJComboBoxLMProgram == null) {
		try {
			ivjJComboBoxLMProgram = new javax.swing.JComboBox();
			ivjJComboBoxLMProgram.setName("JComboBoxLMProgram");
			ivjJComboBoxLMProgram.setToolTipText("The program you want to add to this control area");
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List l = cache.getAllLMPrograms();
				Vector unassignedPrgIDs = LMProgram.getUnassignedPrograms();

				// fills our JComboBox with LiteDevices!!
				for( int i = 0; i < l.size(); i++ )
				{
					LiteYukonPAObject lite = (LiteYukonPAObject)l.get(i);
					 
					if( unassignedPrgIDs.contains( new Integer(lite.getYukonID()) ) )
						ivjJComboBoxLMProgram.addItem( lite );
				}
			}
			
			if( ivjJComboBoxLMProgram.getItemCount() <= 0 )
				ivjJComboBoxLMProgram.addItem( "  (No Unassigned Programs Available)" );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxLMProgram;
}
/**
 * Return the JCSpinFieldUserOrder1 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldPriority() {
	if (ivjJCSpinFieldPriority == null) {
		try {
			ivjJCSpinFieldPriority = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldPriority.setName("JCSpinFieldPriority");
			ivjJCSpinFieldPriority.setToolTipText("Search priority");
			// user code begin {1}

			ivjJCSpinFieldPriority.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, 
						new Integer(1), "###0.###;-###0.###", false/*allow_null*/,
						false, false, null, new Integer(1)/*default value*/), 
						new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(0)), 
							new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldPriority;
}
/**
 * Return the JCSpinFieldUserOrder2 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldStopOrder() {
	if (ivjJCSpinFieldStopOrder == null) {
		try {
			ivjJCSpinFieldStopOrder = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldStopOrder.setName("JCSpinFieldStopOrder");
			ivjJCSpinFieldStopOrder.setToolTipText("Order number used when stopping");
			// user code begin {1}

			ivjJCSpinFieldStopOrder.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, 
						new Integer(1), "###0.###;-###0.###", false/*allow_null*/,
						false, false, null, new Integer(1)/*default value*/), 
						new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(0)), 
							new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldStopOrder;
}
/**
 * Return the JLabelPriority property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPriority() {
	if (ivjJLabelPriority == null) {
		try {
			ivjJLabelPriority = new javax.swing.JLabel();
			ivjJLabelPriority.setName("JLabelPriority");
			ivjJLabelPriority.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPriority.setText("Assigned Start Priority:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPriority;
}
/**
 * Return the JLabelProgram property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgram() {
	if (ivjJLabelProgram == null) {
		try {
			ivjJLabelProgram = new javax.swing.JLabel();
			ivjJLabelProgram.setName("JLabelProgram");
			ivjJLabelProgram.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgram.setText("Program:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgram;
}
/**
 * Return the JLabelSelectedPrograms property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSelectedPrograms() {
	if (ivjJLabelSelectedPrograms == null) {
		try {
			ivjJLabelSelectedPrograms = new javax.swing.JLabel();
			ivjJLabelSelectedPrograms.setName("JLabelSelectedPrograms");
			ivjJLabelSelectedPrograms.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelSelectedPrograms.setText("Assigned Programs");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSelectedPrograms;
}
/**
 * Return the JLabelStopOrder property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopOrder() {
	if (ivjJLabelStopOrder == null) {
		try {
			ivjJLabelStopOrder = new javax.swing.JLabel();
			ivjJLabelStopOrder.setName("JLabelStopOrder");
			ivjJLabelStopOrder.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopOrder.setText("Assigned Stop Priority:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopOrder;
}
/**
 * Return the JPanelLMProgram property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLMProgram() {
	if (ivjJPanelLMProgram == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Program Assignment");
			ivjJPanelLMProgram = new javax.swing.JPanel();
			ivjJPanelLMProgram.setName("JPanelLMProgram");
			ivjJPanelLMProgram.setBorder(ivjLocalBorder);
			ivjJPanelLMProgram.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJComboBoxLMProgram = new java.awt.GridBagConstraints();
			constraintsJComboBoxLMProgram.gridx = 2; constraintsJComboBoxLMProgram.gridy = 1;
			constraintsJComboBoxLMProgram.gridwidth = 3;
			constraintsJComboBoxLMProgram.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxLMProgram.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxLMProgram.weightx = 1.0;
			constraintsJComboBoxLMProgram.ipadx = 122;
			constraintsJComboBoxLMProgram.insets = new java.awt.Insets(5, 1, 3, 28);
			getJPanelLMProgram().add(getJComboBoxLMProgram(), constraintsJComboBoxLMProgram);

			java.awt.GridBagConstraints constraintsJLabelProgram = new java.awt.GridBagConstraints();
			constraintsJLabelProgram.gridx = 1; constraintsJLabelProgram.gridy = 1;
			constraintsJLabelProgram.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelProgram.ipadx = 10;
			constraintsJLabelProgram.insets = new java.awt.Insets(5, 17, 6, 1);
			getJPanelLMProgram().add(getJLabelProgram(), constraintsJLabelProgram);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 4; constraintsJButtonAdd.gridy = 3;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsJButtonAdd.ipadx = 11;
			constraintsJButtonAdd.insets = new java.awt.Insets(2, 20, 9, 26);
			getJPanelLMProgram().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJLabelStopOrder = new java.awt.GridBagConstraints();
			constraintsJLabelStopOrder.gridx = 1; constraintsJLabelStopOrder.gridy = 3;
			constraintsJLabelStopOrder.gridwidth = 2;
			constraintsJLabelStopOrder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopOrder.ipadx = 3;
			constraintsJLabelStopOrder.insets = new java.awt.Insets(7, 17, 10, 0);
			getJPanelLMProgram().add(getJLabelStopOrder(), constraintsJLabelStopOrder);

			java.awt.GridBagConstraints constraintsJLabelPriority = new java.awt.GridBagConstraints();
			constraintsJLabelPriority.gridx = 1; constraintsJLabelPriority.gridy = 2;
			constraintsJLabelPriority.gridwidth = 2;
			constraintsJLabelPriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPriority.ipadx = 2;
			constraintsJLabelPriority.insets = new java.awt.Insets(6, 17, 1, 0);
			getJPanelLMProgram().add(getJLabelPriority(), constraintsJLabelPriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldPriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldPriority.gridx = 3; constraintsJCSpinFieldPriority.gridy = 2;
			constraintsJCSpinFieldPriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldPriority.ipadx = 54;
			constraintsJCSpinFieldPriority.ipady = 19;
			constraintsJCSpinFieldPriority.insets = new java.awt.Insets(4, 1, 2, 25);
			getJPanelLMProgram().add(getJCSpinFieldPriority(), constraintsJCSpinFieldPriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldStopOrder = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldStopOrder.gridx = 3; constraintsJCSpinFieldStopOrder.gridy = 3;
			constraintsJCSpinFieldStopOrder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldStopOrder.ipadx = 54;
			constraintsJCSpinFieldStopOrder.ipady = 19;
			constraintsJCSpinFieldStopOrder.insets = new java.awt.Insets(5, 1, 11, 25);
			getJPanelLMProgram().add(getJCSpinFieldStopOrder(), constraintsJCSpinFieldStopOrder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLMProgram;
}
/**
 * Return the JScrollPaneJTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJTable() {
	if (ivjJScrollPaneJTable == null) {
		try {
			ivjJScrollPaneJTable = new javax.swing.JScrollPane();
			ivjJScrollPaneJTable.setName("JScrollPaneJTable");
			ivjJScrollPaneJTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneJTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneJTable().setViewportView(getJTableProgram());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJTable;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 5:55:39 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.ControlAreaProgramTableModel
 */
private ControlAreaProgramTableModel getJTableModel() 
{
	return (ControlAreaProgramTableModel)getJTableProgram().getModel();
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableProgram() {
	if (ivjJTableProgram == null) {
		try {
			ivjJTableProgram = new javax.swing.JTable();
			ivjJTableProgram.setName("JTableProgram");
			getJScrollPaneJTable().setColumnHeaderView(ivjJTableProgram.getTableHeader());
			ivjJTableProgram.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableProgram.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableProgram.setModel( new ControlAreaProgramTableModel() );

			//create our editor for the Integer fields
			javax.swing.JTextField field = new javax.swing.JTextField();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
			
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			field.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 99999) );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);

			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend =
						new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );

			
			ivjJTableProgram.setDefaultEditor( Integer.class, ed );
			ivjJTableProgram.setDefaultRenderer( Integer.class, rend );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 4:44:33 PM)
 * @return java.lang.Integer
 */

//as of 2.41 we do not use incrementing priorities; user changes priorities manually
/*private Integer getNextStartOrder() 
{
	int j = 1;
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{

		if( j == getJTableModel().getRowAt(i).getProgramList().getDefaultPriority().intValue() )
		{
			j++;
			i = -1; //we must start i to -1 because we will increment to zero
						// in the loop above
			continue;
		}

	}
	
	return new Integer(j);
}*/
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 4:44:33 PM)
 * @return java.lang.Integer
 */
private Integer getNextStopOrder() 
{
	int j = 1;
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{

		if( j == getJTableModel().getRowAt(i).getProgramList().getStopPriority().intValue() )
		{
			j++;
			i = -1; //we must start i to -1 because we will increment to zero
						// in the loop above
			continue;
		}

	}
	
	return new Integer(j);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea) o;

	controlArea.getLmControlAreaProgramVector().removeAllElements();

	for (int i = 0; i < getJTableModel().getRowCount(); i++)
	{
		ControlAreaProgramTableModel.ProgramRow row = getJTableModel().getRowAt(i);

		row.getProgramList().setDeviceID(controlArea.getPAObjectID());

		controlArea.getLmControlAreaProgramVector().add(row.getProgramList());
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonAdd().addActionListener(this);
	getJButtonRemove().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaProgramPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(389, 348);

		java.awt.GridBagConstraints constraintsJScrollPaneJTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneJTable.gridx = 1; constraintsJScrollPaneJTable.gridy = 3;
		constraintsJScrollPaneJTable.gridwidth = 2;
		constraintsJScrollPaneJTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneJTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneJTable.weightx = 1.0;
		constraintsJScrollPaneJTable.weighty = 1.0;
		constraintsJScrollPaneJTable.ipadx = 341;
		constraintsJScrollPaneJTable.ipady = 145;
		constraintsJScrollPaneJTable.insets = new java.awt.Insets(2, 10, 9, 16);
		add(getJScrollPaneJTable(), constraintsJScrollPaneJTable);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 2;
		constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(8, 40, 1, 16);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJPanelLMProgram = new java.awt.GridBagConstraints();
		constraintsJPanelLMProgram.gridx = 1; constraintsJPanelLMProgram.gridy = 1;
		constraintsJPanelLMProgram.gridwidth = 2;
		constraintsJPanelLMProgram.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLMProgram.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLMProgram.weightx = 1.0;
		constraintsJPanelLMProgram.weighty = 1.0;
		constraintsJPanelLMProgram.ipadx = -10;
		constraintsJPanelLMProgram.ipady = -10;
		constraintsJPanelLMProgram.insets = new java.awt.Insets(15, 10, 8, 16);
		add(getJPanelLMProgram(), constraintsJPanelLMProgram);

		java.awt.GridBagConstraints constraintsJLabelSelectedPrograms = new java.awt.GridBagConstraints();
		constraintsJLabelSelectedPrograms.gridx = 1; constraintsJLabelSelectedPrograms.gridy = 2;
		constraintsJLabelSelectedPrograms.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSelectedPrograms.ipadx = 15;
		constraintsJLabelSelectedPrograms.ipady = 8;
		constraintsJLabelSelectedPrograms.insets = new java.awt.Insets(8, 10, 2, 63);
		add(getJLabelSelectedPrograms(), constraintsJLabelSelectedPrograms);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxLMProgram().getSelectedItem() == null
	    || !(getJComboBoxLMProgram().getSelectedItem() instanceof LiteBase) )
	{
		return;
	}

	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();

	com.cannontech.database.db.device.lm.LMControlAreaProgram programList = new com.cannontech.database.db.device.lm.LMControlAreaProgram();
	programList.setStartPriority( new Integer( ((Number)getJCSpinFieldPriority().getValue()).intValue() ) );
	programList.setStopPriority( new Integer( ((Number)getJCSpinFieldStopOrder().getValue()).intValue() ) );

	// this is set to the LMPrograms deviceID
	programList.setLmProgramDeviceID( new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem()).getYukonID() ) );
	
 	/*if( getJTableModel().addRow( programList, (com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem() ) )
 	{
	 	//as of 2.41 we no longer increment priorities
	 	//user manually changes them; otherwise, they remain at the same value
	 	getJCSpinFieldStopOrder().setValue( getNextStopOrder() );
	 	getJCSpinFieldPriority().setValue( getNextStartOrder() );
	}
 	else*/
	if( ! getJTableModel().addRow( programList, (com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem() ) )
 		javax.swing.JOptionPane.showMessageDialog( this, "That Program is already in the list.", "Duplicate Program", javax.swing.JOptionPane.INFORMATION_MESSAGE );
 	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();
	
	if( getJTableProgram().getSelectedRow() >= 0 )
	{
		LiteYukonPAObject[] lite = new LiteYukonPAObject[getJTableProgram().getSelectedRows().length];
		int[] selRows = getJTableProgram().getSelectedRows();
		
		for( int i = (selRows.length-1); i >= 0; i-- )
		{
			lite[i] = getJTableModel().removeRow( selRows[i] );
	
			boolean alreadyFound = false;
			for( int j = 0; j < getJComboBoxLMProgram().getItemCount(); j++ )
			{
				if( getJComboBoxLMProgram().getItemAt(j).equals(lite[i]) )
				{
					alreadyFound = true;
					break;
				}				
			}
				
			if( !alreadyFound )
				getJComboBoxLMProgram().addItem( lite[i] );
		}		
	}

	getJTableProgram().clearSelection();
	fireInputUpdate();
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMControlAreaProgramPanel aLMControlAreaProgramPanel;
		aLMControlAreaProgramPanel = new LMControlAreaProgramPanel();
		frame.setContentPane(aLMControlAreaProgramPanel);
		frame.setSize(aLMControlAreaProgramPanel.getSize());
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
	if( o == null )
		return;

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea)o;

	for( int i = 0; i < controlArea.getLmControlAreaProgramVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMControlAreaProgram programList = (com.cannontech.database.db.device.lm.LMControlAreaProgram)controlArea.getLmControlAreaProgramVector().elementAt(i);
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;

		//find the LMProgram that this list item points at
		liteDevice = PAOFuncs.getLiteYukonPAO( programList.getLmProgramDeviceID().intValue() );
		
		if( liteDevice == null )
			throw new RuntimeException("Unable to find the LMProgram with deviceID " 
				+ programList.getLmProgramDeviceID().intValue() + 
				" for the LMControlAreaList in LMControlArea '" 
				+ controlArea.getPAOName() + "'" );
			

		getJTableModel().addRow( programList, liteDevice );
	}


	//getJCSpinFieldStopOrder().setValue( new Integer(getJTableModel().getRowCount()+1) );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8FDC1C4515B1C62591ABAA1A285822A19595B5C6ECE3CCABA1310DB53D469ED6FAF2DEFA172B39BB6CD1CB5B2839BA7C0704A490A0218AE6CDE2A44AA191228910B47C8DC4CCE832A1584B19AC6C87EC326C2E3B9F30848877E666FB73FD7B6DAE84AC7713DF5EFE5F3CB7EF5E4C1B19B7EF7EFDA179BBC25312127389C9CAA34A5FA7C904147491F2F6752B4790B7C30ACEA71A3FF7GF2493327D3
	61998434D54FC62767107B73B2A19C8661442722533743FB9E79637E06737062C7CE9150F2965A1B4717F367DBA8A70305FBF233613999A09CF07419A8D179E339058AFE2502C7C8CAA6A43E1B4639A337C8419D00F09BC0B84012EE9EBF861ED9CC72B5D58714F8E7EF8AA509BFFEAFF993CDC713A6C2E58F676DD01E8672F0926925321631FCD21EA0202DG2005CFA1E159D431B5ED97FB8EF7597B4A3ACCB61B5DA6CBDD3DE50316CBA627394CAC8DD83A24B2EBDF97601DF62B49A9192A2ADABF2B6741DA88
	36B96DBDCED3DF1B49A6D98999E172B6EE4DE4699352G61CC8B452599E99E22A09C8B10A070AB993E955E4DGA396AC772177EA14F2F58CBFCCE6BDEFCB5962275C4196DE5EA596355C417D2FE57C0955518BC534AE56812D68C2F4FAB90091C0868886F80B350DFFFE6BFDF856F64A75763EBE3B2D566128331A1D1F594D12955E2B2A002260CECADDF6271990DE7FCF67167A484FF840791E1FB49F57C9F885360508135FA1F10FFE1A1C61C3A7F1616549919738AE16DFD0F542E567EC75A7FB4D854F366AA9
	7B8C514A162F35272479109D795C5E64F5CABA5997D559B597E9DD8F313AAE0577A425FDF07C558CBFC303676DF6A34353F0905092AFE25B68580CF9E9CD3D03646CEB752907395135499DD61E8707D51F17A327E95AA1D6AEB3CD6465D306DF2041733C7C0C65050617832D24AFBA1D372FB5EF53FE9C8861F000A800D800F5G79B608E743B676C15107D758465ACCCE392E5FE2B5CBCE42F95794ED032764161D26961BCBB6593A24F61353E26A34CAAE2244F939086A2053036F9556763BC046D149A6B9CD32
	45EE0354CDFD12ACB9DDBC4E6BC539B48E74E4CAB5DA7AA4905067A018EECAF19EBC2DA617FC5AE1B649129A0353B79653BA99B035C30A10G3CF33D649A283E1A217C67819CC28F573274BE151CE0AF4014586D977B9DC6DA9292E523FAFE0F590ED9709EE2C39DEFFA0762D26D50076D184E770B37EA52E930F4516C1B1CC35847460C54D6175B314D7C3A835BCC432679247439E3BE5B8C33B7F6254D5875FD679BACCD1AAF9E4EB5F6CC329FEB215C6E3B3FF95A0A773F120C148968DC3F43BBE9DD4D057CC4
	8244889D2FABB670B6DBE7F2C958CACFB82101C9E6D563BC1FCD27B395FD26083C7F056162A13DFC876AAC1615A7698A72B69560B2FE43E450745326C186574D2F5F266311B4A059648649A1594C7062D2E83FFB1B36B9253FF65B854F6B8C6F007EDD8F9D4AA570C9BBE89FB44932535259AFCB27A4D93658FAD4FA568EAA4B555F69D0CD834A4A59C14BE89F047EAD62976C20FD8346EC1B44AAC930A8746D2CEFCAD6298FB2DDEF6F37499CDFEC206D7B22B4245806F78C54899AB0D97B291D61BE438253C6DA
	8627258F2A1E1AAA926A207D767056C20D0EEAGC7FD9F991E818E6C03033B308F6E3EF4B709BA77EE322FB1E46E7C0664358E5EB756BB54BE487334759D5FF9AFE4B6C2413BD2982E251F366FF15C95257DC76E22E996FD897E81C07317587E035F21F434B1CA432F37DB2DDC2DC25FD76D4C2175A3FD59AF01E926466CD32143001DDBF534F6C51F1C9E34136A532A310386A79B93CF23FFF89E726386187BA557E96C170ACED83C236D984F8A61D9GE1CE9EEF0D1347637516F10E0E7B3EFC3D1EFECBD93FEC
	21D654A4C303F5744BCCBD5C178BE7761826D16AC21FAD3A006A4B8C619100E097566F331B50E78B3E98CAD64FFB532F2F9A6162F9DB65D26B57F0B57AECFF5CCDE56648516986996BE263AE6D38D16BF2593BACEC48D27438F817367EB96E49DD9B6059EDF13A64966820EE5479D33BE8FE6918262015B1BE62B40E7115A0578A105ACF71CF8C64EB729E867815G89G3C1D6535FFC47BAFEB94E57C51A6B93B6D4EBE49DCF241B4E0AAB38D4AE54CD61455B2F2A3FD5F0BF573DA69C1F8DE286B17E5708C4C66
	F3353AB4DA7DA7D23D371D200FF6CF5E91A50FAB9F3A03540C65A57B7219833694A73776F33D4B7D2A7EC796262744FF71FA865705239DF59185F1567523AE7EEB2BDE97F4BCAC9FC0DDBC7F95F5F12024C65545C9294FBEA0CDD29DFC8CF8504B4FED98D0475B0E816F31177BFF9FEF2365368EF07FAFCB606B99BEC00367FADA417A828D0781ADF390FD169B76D0FBE805F0GC0A840A200D800F5035826BF5BDDC1A67089DB200FBAEDA66BA98F630F3E593BD467E02F15F0463BD4074CDE939C8B971E566BA1
	5F6DD976F2DD1873C5F9713D2D8B9BF5FC3C7E361C7A935A99DACB6EB257A00CE5D5D547F80D820D0F97DBE56DF8E1F573F694844FB0GEEFBDE9948D47233440DB6E634C9EBE316EC79A8F9239F9B132864AFFFC8ED6325E2DED8D7C26D2691E88E00G00B00095GEBGB2AFE19D5D746E21096A08B5BC233D9EF04DE0476B75632E5069C07B546AE216971F3EAECAAEF972F17DD566217E0EG1D7B574B19BDF7C3B8D060EE4C27388590DEF1897D341AEDF4CC0B07F076B04CE3G9A06115642E83D43FC3C21
	36B0D0507E2EE0AA7EEA36123EA88F3C47DF726E23DC46CBD312112674D3EDDA3EFCD0DE6F376D716F5F5D354737FFB7EF0FEF7F6E3EBD399C8FE9E8E7F7C8FFE44F787E5F63FBFC79FF45AC7FCFEF5B4252F639A497EB08F490D7E8AF498544079FF89CA600CA5973483F1D4ED262B44A767AFD229F7FF6EF216FFE2C718D0E49583EA20651B7309E2476F08D045783640FE03B7843003E8D17826D48882FE36308F7DD73FE7A74DEBA5E7482FD96C088C0A4C04C88764F936FCDE8C39B5D63585056927F75FC2A
	44F7BDFFDEC27D9A173D5F59A5696B72FCC92120291620C5167A285AF99C8B6317CD4E9E492BADB898BF2779639FAA7157DEF81DBE582E2D530FC43FB918272F03C450E366A857FD5128F79D7074DE58CA6781D653103DDF06796FA8334D425FAF07F0B30083E056285667FCE4105A66EE7037A1FB279CD297F1DF263C5F49C75EB590FF9DC0A1608FGF4DCC6BFA22C1A16715829AEF05C2D344D9D635EB16629BF156A701856CF9034335934CEF9CBED7DCCF179955A2B5B69DA4A31D6D327E4BDA5D1CFDEB2AB
	B1DCB8B60DEE530ECDD1177958948FCFB206FE50F025568FAA9C63F1EA46303D1F10353AEE833CF9CC552FFB4C1F7F11407ACF609877B33688BC292278C88D1E7B9FCE961F06630056B183ED7A66C15AB7E3A03CD660CEB25CFA88E7DF053861C1347D05D7E11FFAF62F7FBE913B57F71FF8F12FEF5B37E66F7836AFFE2F3F364C73571607656895F93EBD8F4BB1A2F06682ACC7G6038EE5E4C43F56FD840654C44F56FFD670B34360DFA12D5D5C71D96F31D29271535F2EE5FBA73F0CD23F9A65A37A016668C88
	4F85D8B0937D92779E74CBA23EF387E91D71294F394FEA63C749ABE6723A0F1F297AA5616017F019AB73D1E649555169672F2638577235ED0D3663471389A903A7271796E869D41F547E2B74AFBC68B43F9BD33474A08FBA5551FB8ABFAF5BB36FE25956BF93CE2C0F9D74BB2F8B3F1A17E92572ECEB5FC5F64FFEBD036B3D273118789CD354DECE50F7F9EC9B0EAD2933D0771BF3286EEBA0EC84304E427A7CED371F310A75F21E5E42CACCEF56ACECB35DEC7CDA8261D582372BG7D23C4013BFE1B68A357E07F
	7E7DC15A06FAA19CG90863084A096A091A07FDA0C17DEC563D5C258F8AD1F474B708C8608840891715C879DBE74C95339D27F26D749830683143C5077D86599F60DAF7FF20ECF59DB2ED0AE36C92DEFC56BE513723ED1A34BFBEC69E1FD2C4F86965CE669CA3D0E6A7B93E6131681A4DE0B634102BC3AB6A50361E93E0E6E4D55D1D705C43078B78FB60C1337AC227AFA94820107C2315206C32F63FB04741D4B3F4C72742FD4BE0439BC69A3D55EF041F87252880EFF1E725EC299815CE6A4897D9B3C46650900
	7315CD112FE752FCFC8C6A4C277DAFA7000FC1C982FF11610BB4F8EEFFEF93763784E80181B8B7EE2E207DB58142595F08CE5F8DD083F096407D8D9C03E63EDFCEA6706B0CF6C7AB8CD2D64D30424B77EC7E54765716E6201D0BDCFC8FD9F46D2BC97E6616015F607DA8C2F92EB56EE2F8E54F311216AF8EE86BG0AE6C727D782B4831C1F0D657B717B253CFC0DE60BDCAB4B262EDE36F20505EB37C803AA1ABACF3CEC0B37E159E4C8ABE036E7FF8E196D4F2F1849EC6702597A3D37C0060F1A2D5FFB4B4F413D
	37D5B47F57231D7ADE25D637F32FD77420BC232E7767C30E4A54076C34BB59604FFD48E031869083FC71G3B83418F84E88360F1DA8BB00E9C487D4CC0F80686AAF2D9FF3CE75BF87D917BA17649C5403B92E01792DE8D6FAF3F71469B94F8DE777651B55BE35072BA6DF5F6376AC2AAE5D9361F791EECF491A405E64D20EB0E4744DA9E621B19CF25781D325D41D613955AA6E933A6BD0B5DE9110710EF5D19BC1617D38E0B2D49A2D94DA282275F7FE63EA7DDA44E7D54C3D55A31BDBE104F5773EFC0BF7655E1
	3D9FCB69A5023E7E38AFFA0D2007FA7941145EA668FB76EB69669BB8FD2E202F9A70C50F38816D7E71C17AFC4F741E52C7C3873567A18EEC7844679E82EB0BB7A8E7A91467126BF6A9E97C74FF34E9645C71CEF2121FB4323F49F98B3F297AEABBD83E8849A46A9F9FA3FABE216FDC46AF33511694BDF0A7E9F87E5524DDFEE434AAE907E8E42C3D30BE03177F56BCD6FE67FB0A0C97E95695BB5E1B2F3EFF29F98F497BAAFB06D7B606BCB62DB2380D7CCB057FF9C8C7056FF94897953E67A1660A7157D82C957E
	7758DC955ABDB6EE639FBAC8959E83F516G10FFA37499001A9B513EA7D56EA44C3E7B306D0D669E3AB6C0F772E5B1CF2A74DF567F281CF87FBF31125A1DCEBB181DBE1ABE62DF2B247585B601A1B96E0DCA2AA3272527D766D195FC5AFE5A262DD2B7C3F35F77D9667BF6C039C6EEC4FFF875F60A8B0270A2012BAEC49FF9650DB8D68464D01F2DA88843B733708191FEB3C75B06E83C3FFDBB2D701071375EF366E57FBBB637B88B79378621BE82C7F00EBAC360DE9EC19D8586619CF5090C63EC9C6072EF42B9
	4DCB45FB49C47BF5C60BEC156A14A1D519B7257663FEDD6BCDB8EFFAE8184A1885619000C8G3E4EE19A512F412F84DA82C06A4D18BF39GFD38A24099EF463579A585B8CF8D3C99657CBB0B3B9C42F1G09G5E36B2614F3EF05C3655D6930263F40F75AD62F3CE072332D2DE398A4B1BFD8B662DFC8467FA8D82D7F182F1A382773C1893C65E02FA3F7C719E2D5EAFDAFB4AAEF4D1CBD256CDC7D71A81B154224E7FF7BF66A1DE241DB108385D4168777E3A1A3593883B0351CE5C367F83F8361BAEA89EAEF587
	6DCEE6A9G895E05C3F24AC39C4BF60E957B13C80797255F466DFF57DF9AE0ED0E017ACEF4DE103AE4232907207D5AF1187ACCF3A19F11G31G89G2937023F8AD08E50G508160G88G8881D8G309CA086E0AD407ADB319C911F6C9D279C4A628AB3FACE13051ABDA56F5FBD685F6EFD7F6044F66F0787593EAF58AB6310C3D99FFD66A075B56D5D5DAEC95857D587F3842E4555E6F7D9683AAA9FE357EE23733F81D034254BE4650B91F5A6E71B5DEA699AD26604A94C667DB7C04EED681B7CE1BB55E5AF38
	E31720EFFAF362FCB20F59C63A0F54863C5CE7BDD9B8114F1AC1D41FB5987842EFD3FD56A5707EC0B2A13B12045F79A7661332C5D255A7657DFCD90E361F2FGDE3AF7938FCFB2874B52B5C2EDF3B7140367798F2CEF9482FD779C1C831FB8C25B9C4FFF6A68C47977B587C69F48B8075B06DC02F6BADA1EE84EF6FE0E2F350BD5937269774AB8DFD6BF659B19F3E5E3CE90446373251DA35446054D514F01BFE07885F374F36048E1341FCB00E6180B36F7E4C4FFDEE69650A2G2266228DB9530876A9F6AE5A58
	D7AF33B9786D60EFG9C399D4752CB85D3DB8B8818A25F52C973F1BDFD417651BBEE576B2F07614D376B7517A0469F8750E2C4F9072A287C83F7782E3FD69FF84E37FFC41B6F73CABCD9F9867865BBF0C24B972244DB20BC177B650B7318AF47AA716215A739539FDF67FE4A176F03CE2144AFDF76A0BB6763072F4D871E5B475FD653F7619F732186FD48EA6A6BDAECE0B2CDD64BA5F66C6694C43475F0FA45E17FB63C6A7044B63C7E30EF5F356530376FFA7C30EF5F55F8D86B3BAA7BF407298D6A013127C344
	7BC260BE9738AE16A6451D311865DE013730FCD1FC33C485A25E4E72B548F09D8ACE5660BED7F0C30769B884C506A182F9AF9FE6F656E235AA6BDCAC0E458643125047F5AC0F9607C9136FC001237966B6FB55F63A882AF10F4AB4AB69E56D0DA70DAD75352D67CE551FBC515ADAD7FB72DCED6B195A4FCF897E778BBF9E073F794449163F1EB8EE74CC615439630D0D8D0DE07CA7675BB9EBE9080F8997067D2C5733510B5B3E57F3708E0179CE34FD473632B35B900E84D8F9A70ECBB9C709A8530C91FAD743A6
	8D9A44A121A69854CAF656C223336EACB56C03FF1554D7613C956C4E875FAF0F4A5B446A42EA31C9757C54B4C19F5F407C6AB5A0F37D1D684FFFBC4C4E290540989902F629E404083E38CF733EF8F87A6764FFBF2DB2783D15B9A3F85E30BC045FDBE906675990450E066058B5BF857D5F40902CBFD7BA1B9FC0B88EA0D1686CEC2356C6E65F856364DD2A1D2A3C4B5F1A662DE36C0C79DDFC4C489378FB995E2D417331648DB6F652F0B8509A3E05724D5B54FA6A533C7FFECA67CCA6D7E7DB36CD278C5ECE0358
	9CE9B514F75D37306DBED905F35154EFA32EF5846731C6017BE9934EE3FB85EEDD93724A82F7628472AE9738706D489BABF0D9E21DE00D407DD64C1FABC39197A7F81B856E1060B58A5C901BFB05C1F80940056F97671984EE57A046AB3CDB14D76CFD6F3E9B5B829977EE0EFC373F367883E61B826E567BB5BFE278603B75FECD5BD674EBC20116FA8F7A611F745331C826B3FBEA37884EA1CEB07B538B71E491773361B40CCB501E353790A1A3EA84E50481CF5CBD58B7B3C75136664F433E7959A83B0785E1B3
	4008406733BE938C6188006573D0CF7727687594B7CF55CD42BCFFFAFA09590054B07D794AC406B704694FD78E2563794A9A200506E15D3918EF1CBA9F6AF3BE626631F6D98361B601ABEE96EDF5BE667D4E315D44F36FF679FCF57FF675FC7D9E6E03ACBE798E58A500A3G6781E6G0482C4812C86A077E27C3431A09FED07260B7B39813AFD585EEB3C75255F2BC508556D73D220799F4F8EA40AFD6BD21FF95BC238DD246F7ABB933E564C74FB3988F47C5C2843E579090B10CBF06C3E370A724AF63B55E8F1
	9841E7D45A74903B7F49671DBF2F1AE86F95674E74E938D71DF732F35D907E9D516E758EB11BF784702D7722BF917229AAEF4B51AB59DB72B74F9D01B48BBC6439191D8CGFC301077C62BAA4FB029F5813D3C65CA9BD46595B1DB388A70F1C25E4E632ABC630472524611179D4E574DF06FB00F846D77DD070DA7D419CF6E9FCF2636FC3EE49602CC3A36C14F287034FB5B54343B46BD8B1021E99B3E525E2DC91B5B0F6D39E8BF4A43318FAD275B034A787D934DFB7B566977976C53AAC3D907BDCDC4FD3EBFFC
	257EB44F5F8FC726361E90B3C53ECD237E780C613EEDA29FEF1632712EB7DC3F9F7E8C433B43757B6127C6F11DBEGE87161389E94404647367B7477C8EFE1F873FD7AFB24EFDE46FB95EE20455D07635452949C3B0E5C0F38A3E958764C77E35BFBB95D7F396C5798ADG62C65C4F7750D78A3E317469BF17BDF7CAB22E74DCB6EF0F339B3D4F915C5D28BFC790FF3FFA0E00734526F81FE1DD5904FC6481FE3620901E258FF0BEEECFAA1BA7F3362081F8BB9ED057E92D8F7033852F0B338583DFD2BE3E57FF5A
	A571E3GCA59B6E6687672A102D333177F2BB17DDEBE4D277B815CCBDFFEC63F97CF6901025EB2263F3BC36921021EB5680BBFD250EFFD5397FD05201F76499FA7682D237A3DFECACF90745F247A2227C6A07D7CA1DF7442885F7BCC5C16196975A8452EC5516609E7B81A687E2C8F3BE68633E66F8143D62E98B64EBBA82969B4762A6904EC1FDCBA3D96B51D962B1A4E2AC926E36DD3533951263EAF24DB614AB96BB2719D0AD25051910073GB3GC2A350466C9E457B56F46F0F77639F0D625DE965BC958B2B
	744FC7277F5E3373AB4918E81F174B7866E56FBB4B37B3DC9468E7ED841657F0F97A6FAC9F3CBC1DF716393D7B4F94EC8FB98FF2FBD7824F1A87F95D73B6F3A68D6398815F8B90G908E3062C15C3BA84B501F0F277CEB013E7EC1F1E7324E6B4EA44449FF886995073C4E8D813D64A11C237E2291570FEA9E423D6844A61C4F4A000B90F1EB0EE2BD2EF988672D970E32B3098FC727D78234BEBC593D685574C82C323FDBCCB0AF6607D1666BE7F0EFA6C460E60CA1AEC6600E8CA2AEFB81620A4F097DE9018B94
	71C6846E6AD1149BA9F0A5E9E22FD960669C92FB4D0FA04E180BBAB97B881EA31F192BBFC7FED312769CF82487BDD761D7693F7220E7F97197FA5C8DCD5370733B4A4FDC46354360C778BCBA62913E6F65F92779D09D460BFF047BB2298BF9BC3C534C5B4376D4EC8F8DC09FD908E5BF170AE50FDC086D618696B79E42290FC62797BD0AF1C753B0EE65232827875246BFEF7F0FE9FABDF91E375F122E57D32D47F97B5AF43D1E703CBD4FD3F78636856B2318FF833B7F918661A5GB1A27F2F54E15CF8013BF088
	F13BA351063D4856D84FC6DE59FE27C3703D56B4B53FBC73685478EAA74D477D6D97D9FF1F9529DFFFFB096103A2756BEF0E2358F6C200C69E43726EA4D43E79B15FFAF27B40AB6B069D5AFC87AA71C215E724DF3EEB467465DD81F163147889FE799A8735FC295FD54EA9AA4F526F7A635BF9CE4BF7C409E7D41E3DFE7936FB4C33C614F8C14AB34CAFDFG63DB6403CEE125DF3E7FCE23FC2B7D70A57A658BB9C479B23F671B2F4887DE71E9A67DCDA6E5CC927339F2C83B77FBB85F482A1B6ABDF81EEEF79D26
	9B8AE9460AF43BD3261AAEAFE7F16A547A6587D364B3CF1A0F775B24B4F64EE1113EBFEFE07822C57A7EFC0B988BCA009638887B734C3429657BE28672A5C0DA290FFB361B42473D5B914FDFB03353250F6B57953E4570B50F6B579576D4613AC2AB5096BCAE6E4B56F95D95DF8C7E51E2AE37E6313FF6ECBF64F16FD009E7D61EEE9FFCFC9E703BC66F7D4D371BF0FFB3F0B166EB8D5BEBD556A2B46F83C727FF6D6B46E915414BFC1079FAF397EB774AC2892E43ACD80C386D9D4CE7FC825E1FC05C37B2A9EE77
	935A3D32B7B21137D2448BE1FE62AC884F9538FF9DC3DE75EE66B1A1B7C244BBBB08F2578B5C8775481B33C41CBF2DC75EA201BBF88EF92D82D75B013CB3846E1E8EF1CED5604AC611B7CE609CC711B7C1601EBD0A3C6B856E5B0C37757B5169666FA3AE7DB246F388DCC39A46D37772660BB530A8916F51C398CF5D4B4BBF0C71F2FEA0FC174BD37B5EC22657182CBFC3F67E873E782C5EFD760A64DD7262534BF37B14472ED5AA6FB91A775BD3273FAFAE1CD6993CCEE7B2BFB8884A9E79832C679798EE850493
	846EE35147D21F443A3F234A736CF9988B9B853D34EA7A4F1EB7D6CD7F59734D75E8CB5CCF0AFB5E429E2877230705BD8894712EBDA47A34407DF103685382F7EE236853CF629ED28ABDF22E2CDD35917D1E8FDFBB7D9E39123DBA0456A818FFC529FBF5F214FACF124B396B10679955A34CCE4D00F801D1689F2D9F43FB22BC8F5B32A633FFF7DE1387F6E6B3CA5D3F6369BFBA66FDDF7547E3F8DFF5C5942E9B1B46ADBB6EC32579119B8B6F8709F69F6F9AE67757GBEA18A6BE08F3D3E2268FADE7DE44E636A
	659916C62767FA486BE4E3C5A9608FACC5F9AD32AA6F710EAB592774A72FF7293AEF48651DE1E3048C7899C21E35DF1537F65CFDD82DACDF7296C01AF8E71D5763F7C7FD6FD35EB8204AAC3EBC19FB3EDEEDE7293A174853DB12B651BD65714A3042AB3D8F8ECF143F716AA0C6139E57493EB17D37E0B8FEDE1D7EFBA29CFFFB46446766A3B3705C7C6A2558C7DF493832F3731BB3715C3CA19A7B55E71913599FB7831FBBDA5D0F890477DFAA676679DA66F3D57A3371F47FECAE1587205CC72F477B604BC5BEEE
	9E373F79EB9BF887BCA6DA35EDDC8677E9D683BEBE9A5B7F42C135AD8EEC7CAA7D3BE819FA5E7E4A56C1F1BF61B9377A3EEF18083A3FB9F3F27B9567C6D45EDF0E51A73D5349E9B3C7D599ECFBC4093723FEF2B2821354F47EB1D3CDE7FF4764525938C1CD27A7DBCD47FCF4F2694C5E242633E8337A7E4296757D0FB9EA7A4FE4FC35733C6FE67E7F1D67EDE4679E78FA6326EABCCFD6328C57AB4F56D0451D07309BA0E819384FDB4F6ADE5CE7F565293A78E9012A0BD7B6CC6E9C6FAB1AF31BDB2B471B7307AF
	73B7F7DF45723CE419FE4D61978C3FE219FE4D213386579CE200F6E0B97ACBFB2B51DF8AD90E7E664FEB547C7D13663D37E67AFD752B26D5865FB7BAD20DF6B48A4A9C831088107F944CFB1F42F5696EEADCEBB7BE05EB6D4155636FC97C30FA7CBD09F555636FC93CDF63EFCFC2D96799372D78827E7F89047C65A760C0C43CCC52E412D2EBB64BA41DFF054A65A4DBC413EDE1677AD78FD4D4257C4B3C14E4125AC25279BD079612CAAFAB3410B4F61721052448F6075B2114016679CD00E4571764B2C9D6F2DD
	82C04DC52640D2FDEED030DBE412DB6BF2D9FAEC1259001F3492B133B4B113A10F1BBC3A55D3F76F7F554A975BDDA9A43D057C09FA5827A4A73B6CA119698765142FFBB711AC075D82C440C381DC6C116612072CD2AB496FBEC93FF26B12A5F3ADDEA3F1492400FD2D73C3FAE37543E6132BD726E77C2BDBC92A0DFE071CEC65E42B495673A13FD3D24D3410D4FB1EF6922671A448EF9E4BAFCD25CC2412774416F75FB936E6D18AC9EEA29B2CF613394954A55B1DADA4CB6EF5CA2EDE3B556CF67C7988CBA813B9
	505469CB05645E92452705AD238E204874C577DD011B56EEEA8235391D64C39613CEA84AC48CF3FD2F498922A4E70BCC3F2BEEF76E96342B5E16777C6468763349A4C9A9A42D028DEE873410EED31F45BA84AF8DEC40E0153399027C5A08E8B9D499E5CDD09E799FDEFE7A1E97EA9262A131A612463E6C89D94AD1E39D6F6F6B141C57C65CDA3CB1F87575B4478EDADCCCB9CBF61AECAE3ABA8C099BF4E5C61248FA59ADCF175FFA5FEBFFE5D9D7D9E53203D79230FE58A3597BBE2C6D6E3658A411G5498A4DFAC
	1227F14AF4F1363AEFAAB8765C9B1D50429AC8CADFF9B97B5D47FEBF223F7B98E69F436C7B882AAD010C7B27273B314C2CD932C081EDC3EE7A3655ADBAC23252917ECC5B8434BF252798F857217774E5D2EE6C158C8E05B0E46FB78C1AEC32C1369BCCE6B3FD483D9617C179BFB18C74BF4510C92612CE15CC360BDEC83F45E1408FF6G25D0C3B1B9E5ADE94BA949646C6A85299C85CAE7A7FF8CB6D64306FE97F08D76CAB6038B92F550EF975092E5FAF5950A4D76EE73941DA3DA9E8D65511F6D742710B13D49
	FE0BA72DF74A35C3276FD2D55FA90A7122DB3AE9BE73CC7C64B945BB4FFF7B1CD628B9258744F000799A6634CA4D6986A52774AF636B1753DA37473005B9CD77680314C2270129823BD103CD73B74465E97AABFE82DCA69BCE3BA4836BAF5015B632EE0721ACCDA74BBBED334090E530EBFA960C1A789DC86FD1B3C549AC91AA7DDAA97A242A68B4C5AED5D5320772AA94CA7257AF7FFF75B44B58GD23FFEB9B529B94DC2FD122FE5676B75EC935B151CA6FBE81A02096648A32E28830F32E25CF48F2C6ACBEA3B
	7AC7C46B21FECD87A7595B99B8CEF8DF615FEE5ABB267DBE543AEB8A7DFEE3AA7FE7FCAED972B3755BCD7C7B9AAB5AE9FAE6404F7899FEA323301D2E03797BCE4729BE1355AA1B2C97EBEDE67A4D0E3AA1D9D23FCF7413F61A3E8B63A8C4CE0BE15F7B3D206179BFD0CB87887DB3919D56A1GGD0EDGGD0CB818294G94G88G88GD8F954AC7DB3919D56A1GGD0EDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG90A2GGGG
	
**end of data**/
}
}
