package com.cannontech.macs.schedule.wizard;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2001 12:41:47 PM)
 * @author: 
 */
public class SimpleSchedulePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.KeyListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
	private com.cannontech.database.db.DBPersistent currentNodeSelected = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjCTITreeViewPanel = null;
	private javax.swing.JComboBox ivjJComboBoxStartCommand = null;
	private javax.swing.JComboBox ivjJComboBoxStopCommand = null;
	private javax.swing.JLabel ivjJLabelStartCommand = null;
	private javax.swing.JLabel ivjJLabelStopCommand = null;
	private javax.swing.JPanel ivjJPanelCommands = null;
	public static final int LOAD_GROUP_INDEX = 0;
	public static final int DEVICE_INDEX = 1;
	private com.cannontech.database.model.DBTreeModel[] TREE_MODELS = 
	{
		new com.cannontech.database.model.LMGroupsModel(false),
		new com.cannontech.database.model.DeviceTreeModel(false)
	};
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldInterval = null;
	private javax.swing.JLabel ivjJLabelMinsFormat = null;
	private javax.swing.JLabel ivjJLabelRepeatInterval = null;
	private javax.swing.JLabel ivjJLabelReminder = null;
/**
 * SimpleSchedulePanel constructor comment.
 */
public SimpleSchedulePanel() {
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

	if( e.getSource() == getCTITreeViewPanel().getSortByComboBox() )
	{
		getCTITreeViewPanel().clearSelection();
	}

	// user code end
	if (e.getSource() == getJComboBoxStartCommand()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxStopCommand()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) 
{
	if( e.getSource() == getJComboBoxStartCommand().getEditor() )
		fireInputUpdate();

	if( e.getSource() == getJComboBoxStopCommand().getEditor() ) 
		fireInputUpdate();
}
/**
 * connEtoC1:  (JComboBoxStartCommand.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleSchedulePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JComboBoxStopCommand.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleSchedulePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * Return the CTITreeViewPanel property value.
 * @return com.cannontech.common.gui.util.TreeViewPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TreeViewPanel getCTITreeViewPanel() {
	if (ivjCTITreeViewPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Target");
			ivjCTITreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
			ivjCTITreeViewPanel.setName("CTITreeViewPanel");
			ivjCTITreeViewPanel.setBorder(ivjLocalBorder1);
			// user code begin {1}

			ivjCTITreeViewPanel.getTree().setRootVisible(false);
			
			//SimpleScheduleLoadGroupTreeModel s = new SimpleScheduleLoadGroupTreeModel();			
			for( int i = 0; i < TREE_MODELS.length; i++ )
			{
				getCTITreeViewPanel().getSortByComboBox().addItem( TREE_MODELS[i] );
				TREE_MODELS[i].update();
			}
			
			getCTITreeViewPanel().getTree().setModel( TREE_MODELS[LOAD_GROUP_INDEX] );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCTITreeViewPanel;
}
/**
 * Return the JComboBoxStartCommand property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxStartCommand() {
	if (ivjJComboBoxStartCommand == null) {
		try {
			ivjJComboBoxStartCommand = new javax.swing.JComboBox();
			ivjJComboBoxStartCommand.setName("JComboBoxStartCommand");
			ivjJComboBoxStartCommand.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxStartCommand;
}
/**
 * Return the JComboBoxStopCommand property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxStopCommand() {
	if (ivjJComboBoxStopCommand == null) {
		try {
			ivjJComboBoxStopCommand = new javax.swing.JComboBox();
			ivjJComboBoxStopCommand.setName("JComboBoxStopCommand");
			ivjJComboBoxStopCommand.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxStopCommand;
}
/**
 * Return the JCSpinFieldInterval property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldInterval() {
	if (ivjJCSpinFieldInterval == null) {
		try {
			ivjJCSpinFieldInterval = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldInterval.setName("JCSpinFieldInterval");
			// user code begin {1}

			ivjJCSpinFieldInterval.setDataProperties(new com.klg.jclass.field.DataProperties(
								new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), 
								new Integer(Integer.MAX_VALUE), null, true, null,
								new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###",
								false, false, false, null, new Integer(0)/*Default*/), 
								new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
								new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
								new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			ivjJCSpinFieldInterval.setValue( new Integer(0) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldInterval;
}
/**
 * Return the JLabelMinsFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinsFormat() {
	if (ivjJLabelMinsFormat == null) {
		try {
			ivjJLabelMinsFormat = new javax.swing.JLabel();
			ivjJLabelMinsFormat.setName("JLabelMinsFormat");
			ivjJLabelMinsFormat.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinsFormat.setText("(minutes)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinsFormat;
}
/**
 * Return the JLabelReminder property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelReminder() {
	if (ivjJLabelReminder == null) {
		try {
			ivjJLabelReminder = new javax.swing.JLabel();
			ivjJLabelReminder.setName("JLabelReminder");
			ivjJLabelReminder.setText("*Select a Target by single clicking on it above");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelReminder;
}
/**
 * Return the JLabelRepeatInterval property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRepeatInterval() {
	if (ivjJLabelRepeatInterval == null) {
		try {
			ivjJLabelRepeatInterval = new javax.swing.JLabel();
			ivjJLabelRepeatInterval.setName("JLabelRepeatInterval");
			ivjJLabelRepeatInterval.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRepeatInterval.setText("Repeat Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRepeatInterval;
}
/**
 * Return the JLabelStartCommand property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartCommand() {
	if (ivjJLabelStartCommand == null) {
		try {
			ivjJLabelStartCommand = new javax.swing.JLabel();
			ivjJLabelStartCommand.setName("JLabelStartCommand");
			ivjJLabelStartCommand.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartCommand.setText("Start:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartCommand;
}
/**
 * Return the JLabelStopCommand property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopCommand() {
	if (ivjJLabelStopCommand == null) {
		try {
			ivjJLabelStopCommand = new javax.swing.JLabel();
			ivjJLabelStopCommand.setName("JLabelStopCommand");
			ivjJLabelStopCommand.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopCommand.setText("Stop");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopCommand;
}
/**
 * Return the JPanelCommands property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCommands() {
	if (ivjJPanelCommands == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Commands");
			ivjJPanelCommands = new javax.swing.JPanel();
			ivjJPanelCommands.setName("JPanelCommands");
			ivjJPanelCommands.setBorder(ivjLocalBorder);
			ivjJPanelCommands.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelStartCommand = new java.awt.GridBagConstraints();
			constraintsJLabelStartCommand.gridx = 1; constraintsJLabelStartCommand.gridy = 1;
			constraintsJLabelStartCommand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartCommand.ipadx = 11;
			constraintsJLabelStartCommand.ipady = 1;
			constraintsJLabelStartCommand.insets = new java.awt.Insets(0, 11, 0, 193);
			getJPanelCommands().add(getJLabelStartCommand(), constraintsJLabelStartCommand);

			java.awt.GridBagConstraints constraintsJComboBoxStartCommand = new java.awt.GridBagConstraints();
			constraintsJComboBoxStartCommand.gridx = 1; constraintsJComboBoxStartCommand.gridy = 2;
			constraintsJComboBoxStartCommand.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxStartCommand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxStartCommand.weightx = 1.0;
			constraintsJComboBoxStartCommand.ipadx = 116;
			constraintsJComboBoxStartCommand.insets = new java.awt.Insets(1, 10, 2, 5);
			getJPanelCommands().add(getJComboBoxStartCommand(), constraintsJComboBoxStartCommand);

			java.awt.GridBagConstraints constraintsJLabelStopCommand = new java.awt.GridBagConstraints();
			constraintsJLabelStopCommand.gridx = 1; constraintsJLabelStopCommand.gridy = 3;
			constraintsJLabelStopCommand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopCommand.ipadx = 16;
			constraintsJLabelStopCommand.ipady = 1;
			constraintsJLabelStopCommand.insets = new java.awt.Insets(3, 11, 0, 193);
			getJPanelCommands().add(getJLabelStopCommand(), constraintsJLabelStopCommand);

			java.awt.GridBagConstraints constraintsJComboBoxStopCommand = new java.awt.GridBagConstraints();
			constraintsJComboBoxStopCommand.gridx = 1; constraintsJComboBoxStopCommand.gridy = 4;
			constraintsJComboBoxStopCommand.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxStopCommand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxStopCommand.weightx = 1.0;
			constraintsJComboBoxStopCommand.ipadx = 116;
			constraintsJComboBoxStopCommand.insets = new java.awt.Insets(1, 10, 13, 5);
			getJPanelCommands().add(getJComboBoxStopCommand(), constraintsJComboBoxStopCommand);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCommands;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.messaging.message.macs.ScheduleMessage command = (com.cannontech.messaging.message.macs.ScheduleMessage)val;

	//store the start command
	if( getJComboBoxStartCommand().getSelectedItem() != null )
		command.setStartCommand( getJComboBoxStartCommand().getSelectedItem().toString() );
	else
		command.setStartCommand("");

	//store the stop command
	if( getJComboBoxStopCommand().getSelectedItem() != null )
		command.setStopCommand( getJComboBoxStopCommand().getSelectedItem().toString() );
	else
		command.setStopCommand("");
			
	javax.swing.tree.DefaultMutableTreeNode node = getCTITreeViewPanel().getSelectedNode();
    if (node != null) {
        command.setTargetPAObjectId(((com.cannontech.database.data.lite.LiteYukonPAObject) 
                                     node.getUserObject()).getLiteID());
    }

	command.setRepeatInterval( ((Number)getJCSpinFieldInterval().getValue()).intValue() * 60 );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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

	getJComboBoxStartCommand().getEditor().getEditorComponent().addKeyListener( this );
	getJComboBoxStopCommand().getEditor().getEditorComponent().addKeyListener( this );

	getCTITreeViewPanel().getSortByComboBox().addActionListener( this );
	getCTITreeViewPanel().getTree().addMouseListener( this );
	getJCSpinFieldInterval().addValueListener(this);

	// user code end
	getJComboBoxStartCommand().addActionListener(this);
	getJComboBoxStopCommand().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimpleSchedulePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(301, 446);

		java.awt.GridBagConstraints constraintsJPanelCommands = new java.awt.GridBagConstraints();
		constraintsJPanelCommands.gridx = 1; constraintsJPanelCommands.gridy = 1;
		constraintsJPanelCommands.gridwidth = 3;
		constraintsJPanelCommands.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCommands.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCommands.weightx = 1.0;
		constraintsJPanelCommands.weighty = 1.0;
		constraintsJPanelCommands.ipadx = -10;
		constraintsJPanelCommands.ipady = -12;
		constraintsJPanelCommands.insets = new java.awt.Insets(5, 7, 6, 10);
		add(getJPanelCommands(), constraintsJPanelCommands);

		java.awt.GridBagConstraints constraintsCTITreeViewPanel = new java.awt.GridBagConstraints();
		constraintsCTITreeViewPanel.gridx = 1; constraintsCTITreeViewPanel.gridy = 3;
		constraintsCTITreeViewPanel.gridwidth = 3;
		constraintsCTITreeViewPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCTITreeViewPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCTITreeViewPanel.weightx = 1.0;
		constraintsCTITreeViewPanel.weighty = 1.0;
		constraintsCTITreeViewPanel.ipadx = 37;
		constraintsCTITreeViewPanel.ipady = 176;
		constraintsCTITreeViewPanel.insets = new java.awt.Insets(6, 7, 2, 10);
		add(getCTITreeViewPanel(), constraintsCTITreeViewPanel);

		java.awt.GridBagConstraints constraintsJLabelRepeatInterval = new java.awt.GridBagConstraints();
		constraintsJLabelRepeatInterval.gridx = 1; constraintsJLabelRepeatInterval.gridy = 2;
		constraintsJLabelRepeatInterval.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelRepeatInterval.ipadx = 10;
		constraintsJLabelRepeatInterval.insets = new java.awt.Insets(7, 7, 5, 0);
		add(getJLabelRepeatInterval(), constraintsJLabelRepeatInterval);

		java.awt.GridBagConstraints constraintsJCSpinFieldInterval = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldInterval.gridx = 2; constraintsJCSpinFieldInterval.gridy = 2;
		constraintsJCSpinFieldInterval.ipadx = 62;
		constraintsJCSpinFieldInterval.ipady = 19;
		constraintsJCSpinFieldInterval.insets = new java.awt.Insets(6, 1, 5, 2);
		add(getJCSpinFieldInterval(), constraintsJCSpinFieldInterval);

		java.awt.GridBagConstraints constraintsJLabelMinsFormat = new java.awt.GridBagConstraints();
		constraintsJLabelMinsFormat.gridx = 3; constraintsJLabelMinsFormat.gridy = 2;
		constraintsJLabelMinsFormat.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinsFormat.ipadx = 4;
		constraintsJLabelMinsFormat.ipady = -2;
		constraintsJLabelMinsFormat.insets = new java.awt.Insets(9, 2, 8, 24);
		add(getJLabelMinsFormat(), constraintsJLabelMinsFormat);

		java.awt.GridBagConstraints constraintsJLabelReminder = new java.awt.GridBagConstraints();
		constraintsJLabelReminder.gridx = 1; constraintsJLabelReminder.gridy = 4;
		constraintsJLabelReminder.gridwidth = 3;
		constraintsJLabelReminder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelReminder.ipadx = 8;
		constraintsJLabelReminder.insets = new java.awt.Insets(2, 7, 7, 10);
		add(getJLabelReminder(), constraintsJLabelReminder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	
	getCTITreeViewPanel().addItemListener( this );	
	getCTITreeViewPanel().refresh();

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	//if( getCTITreeViewPanel().getTree().getSelectionCount() <= 0 )
		//return false;

	return true;
}
/**
 * This method handles ItemEvents generated by the sortByComboBox
 * @param event java.awt.event.ItemEvent
 */
public void itemStateChanged(java.awt.event.ItemEvent event) 
{
	try
	{
		if( event.getStateChange() == java.awt.event.ItemEvent.SELECTED )
		{
			//javax.swing.tree.DefaultMutableTreeNode node = getCTITreeViewPanel().getSelectedNode();			
			//com.cannontech.clientutils.CTILogger.info( ((com.cannontech.database.data.lite.LiteDevice)node.getUserObject()).getDeviceName() );

		}
		else if( event.getStateChange() == java.awt.event.ItemEvent.DESELECTED )
		{
			//com.cannontech.clientutils.CTILogger.info("DESELECTED TREE NODE");
			//getCTITreeViewPanel().undoLastSelection(true);
		}

	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 5:17:32 PM)
 * @param event java.awt.event.KeyEvent
 */
public void keyPressed(java.awt.event.KeyEvent event) 
{
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 5:17:32 PM)
 * @param event java.awt.event.KeyEvent
 */
public void keyReleased(java.awt.event.KeyEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 5:17:32 PM)
 * @param event java.awt.event.KeyEvent
 */
public void keyTyped(java.awt.event.KeyEvent event) {}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleSchedulePanel aSimpleSchedulePanel;
		aSimpleSchedulePanel = new SimpleSchedulePanel();
		frame.setContentPane(aSimpleSchedulePanel);
		frame.setSize(aSimpleSchedulePanel.getSize());
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
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseClicked(java.awt.event.MouseEvent e) 
{
	if ( e.getSource() == getCTITreeViewPanel().getTree() )
		fireInputUpdate();
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseEntered(java.awt.event.MouseEvent e) {
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseExited(java.awt.event.MouseEvent e) {
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mousePressed(java.awt.event.MouseEvent e) {
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseReleased(java.awt.event.MouseEvent e) {
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	com.cannontech.messaging.message.macs.ScheduleMessage sched = (com.cannontech.messaging.message.macs.ScheduleMessage)o;
	boolean set = false;

	// check to see if we have to add the startCommand or just set it selected
	for( int i = 0; i < getJComboBoxStartCommand().getItemCount(); i++ )
	{
		if( sched.getStartCommand().equalsIgnoreCase(getJComboBoxStartCommand().getItemAt(i).toString()) )
		{
			getJComboBoxStartCommand().setSelectedIndex( i );
			set = true;
			break;
		}
	}
	if( !set )
	{
		getJComboBoxStartCommand().insertItemAt( sched.getStartCommand(), 0 );
		getJComboBoxStartCommand().setSelectedIndex(0);
	}
	
	// check to see if we have to add the stopCommand or just set it selected
	set = false;
	for( int i = 0; i < getJComboBoxStopCommand().getItemCount(); i++ )
	{
		if( sched.getStopCommand().equalsIgnoreCase(getJComboBoxStopCommand().getItemAt(i).toString()) )
		{
			getJComboBoxStopCommand().setSelectedIndex( i );
			set = true;
			break;
		}
	}
	if( !set )
	{
		getJComboBoxStopCommand().insertItemAt( sched.getStopCommand(), 0 );
		getJComboBoxStopCommand().setSelectedIndex(0);
	}

	getJCSpinFieldInterval().setValue( new Integer( sched.getRepeatInterval() / 60 ) );

	
	// Set the target of our simpleCommandSchedule if its in the JTree
	for( int i = 0; i < TREE_MODELS.length; i++ )
	{
		for( int j = 0; j < TREE_MODELS[i].getChildCount(TREE_MODELS[i].getRoot()); j++ )
		{
			javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode)TREE_MODELS[i].getChild( TREE_MODELS[i].getRoot(), j );
			int index = TREE_MODELS[i].getIndexOfChild( TREE_MODELS[i].getRoot(), node );
			
			if( node != null )
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)
				      node.getUserObject()).getLiteID() == sched.getTargetPAObjectId()) {
					getCTITreeViewPanel().selectLiteObject( (com.cannontech.database.data.lite.LiteYukonPAObject)node.getUserObject() );
					getCTITreeViewPanel().getTree().setSelectionRow( index );
					break;
				}
			}					
		}
	}


}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 1:25:55 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent event) 
{
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 1:26:30 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent event) {}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5AF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF49457F57C8C984B9830G19CF8EB66062D82DF1CA9A52D28E2E15965B64540D6DE34566385466A44AA9EE5593DAE30746CFDA7DG819292A0E3E10B0FC1B8D4C818841081238F82090DD11090CB10C1235D590FB43B336C4C22959250FB5FE7F6345A15G1736BA67EA665D776E3B775DF7FFEFE61694FEBAA3B3AD3D101034CC427F8E2791B262A4A11B1AE7A68A5C9CB9A90B187EF6G6413CA
	BD832E73212FF9F6D2D69E891D4E017692E8A7CFCF4A5A8A77856466E9F284EEE270C9BECC484497D7AD98184F3FDD94FC32E97BADE7AEDCB7G3C8CB83C96BB117FFFB80BB8FEA5478BC8CBA7A44705E372DD459C5786B4A100A400E4A79B1F8A57B514738315C778385671B3484A9777262F47F9CCF38AE8F4B1DA1DDFCB48B3E945EE64D5694A65B40950379CG97EFA1EBF69F076B3146CE57893B6AAD33CBBE1F6A53E53B3B4CAB5935B24D6E169DC1C5AE6B725C10820E4A4AA60F572F48CD9C5DA879E485
	6893A80FE12E9CBAA72C875A3BBD08DB580DFC17C2FBA5C02EC7609F3801F8B75C8F81C87008359E5FFB0C2F4556738CD97D3A4F32B146DA67FA589A13BD61354687BF487E086E0B2D8575DF581114F584408A60879887B09760E3EA8FC7EC1F43352ADD2FD13DDE55D76577D7AB0E406FD5072C40FDE5A574F05CF9592E869C0430BDBF695A9FC51E010051FDFD57F4CCA7F33E907B1FF87EC932744F4F26E7C7514952B965690BBB18AE16F504F542F80F0A491BF476354F3E3CAF92B36F95A3D5CBE6945E73DE
	5B17DE4867A96D8C7336F562DE9F24FBFD866E5338FDB07CB10A1FE642B3DBCD22B6036DC49C27885B30ED9032D8B326103C8AEBD4BDA47CCDD53A2EB0996214C8D95E27BC67AB0CE726A1CBC68F62171870CC160768F8ECAF033EDAEFD29633AF49063EBB875AC9GCB81D682947A12324A81EAFD4246065A8E5C210DB5CA813DBA68D19CF200B0DA29E58BDC65109E106AFD1AAE796CF20B9470C86D0A2C91BE262385F5505E07CE2536EF879E27E51F9C10F40F6A0359A52F2C4B810D0D893564631800CCFB1B
	BDDE9998F87DC44CBB2425G2E0A2469977C8EC91743A3D87F68965C136BD28B4C889340BD534B58CB28AF872C3F97A04E500365A24EF7D68EC040282C342AEAE7505F0CABA1CBFD28676F5058B1836E677B040E477C91F1A5AA7830AA66293C3849B4CF2D470E62CB01EE61E3C7EEE0FC362A42E67E45A6EC26F67DEC327F350628B60320284CD6C628113ED3CD67C439D87B45EE5BDD7AF1A4D85568BE970E956FFCEB9E16817A18FED7DD463DCA84D996832498BA8EDEACE1B6DBADE9323052F381B0B0599156
	B81373439BB9DC1F96C37635BD08A37E242CD23F5059789BB65338EF332E58BA7BAE650173E75E879ECCB71DB64CC772F55927574AFE5967009B0D7785ECE8F35CDF1D2AC19322F8BF78F78DB814E660EF595087A5DD8FF85A033A5CA46B3A47678A770F243C34E03BBF9C9A842F07ED38C6358B7C5B98BF41063EE1D7FDBE19EE12C1427B6329EF4A0A6C8521EB5420CFE778A917503EBB65EE9E9B3EF3898B1F6B129244B8436A043952BADCC34063052D47D0C5667951EF3F6FAAB2692891F0D86F0C066BB43F
	70412EDD4287F75FF81CAC3A7AD9FA349C12B03BB6FD351F79C55E35308FB2199E79E3F459A7529841719A13B5CD45D8932DD2E1F6DF718D4ED58EBCEA81DA2F893B4F3F0C7DA236F0032FD19505295350732FAF67613E485702B204E48CE2E78D5DE536EE0A68EB61FAE47D6BDBD10F0AA97E6586E8AE6C9135208E724481A4DEE33ADC41F5414611DEB14E86EDFF207FB826036C2B186F2355F52E2027AC28FBB0FACABA982ABF28D365309A6ED3F78E1FE3DCC054EAC7BBD1DF0B213D8CE0D5C06C6BBF2C9735
	DAFC678C32EC663618F5E91E46E4BB24057735E4382855FA2EA0CF9D7AC6E8E2AFC6371A73C5152629F68FCDD5DC0F073FB16FBB439D7BE6ADDC1D1E00265703E306044E2B3FC1F9B117F1B44F6DF20F486D894077E5G370E78D736961AE44F81DCB9C0B300054E372467A07AADB50AB2F6E91483CEB560159D259D52F529CC6A524BE80CA82B225D2770FE1F581B24FD1B181CB255BECB30257BDAC44D75C441C94EFB491FCDA147EE9724C72B01C7EF5C1EBED767B5309E56EB6F7732ACAB2E7CAA1B2DF1DF2F
	D8636EA058FAD02C716A09E8EB0C8F0AF83D5B9DD9AF4E8906F35602E07F7C456A28E7292D3C9CE4F554FA833F0862D71A70CC3E2FA89E5BC557E17F2F0B5C3F368D63CAAA34F33B408E810E81348274F6899BF9711BAF48A035D5BD58FC40A7A9CDFD022828F12ED8D85C636D91D7B2C55CA3A227F476C46AA1212B6F5A67C53499DDF7BF3A3208F6F2849D338D17723170DB3424D0195685B92132328156582ED62BA19EF7D7F71A63EEEA972B1B8AC310F38118AF2FAD923EFCB2A4FC76E61D59E797ECBC183E
	AE064F06C2CC3E04D05836769B672BF9924621E468DB89105B8DFC812CGEE0038EE31C79B2E9E9FEC0F28F3B42BB500BB83F131A6B2FF99BABD50FBEFFBF13C9FDD64DEA4F67725E37A1BF4C268AF897AD91D7AA50D0F2B211DF7C36032BA90F7885A569B42FFFEC077478F6DB800B9G0B81D2GB2FA44183F25E3506FCAGD78B60G9881B04B98F32A338D2EC8A3E443B944FD5C0D58F5E4B41843477B39BCFD7554C6F92DB70DDF97E11FF743AB1BDF51475618704CAE8A5AE257D15B5A2257D1A5ED516B28
	BDED798C8FF318CFD1223F2CED60BA6BF7ED516A2C6DD4769736EC24F3EB1A2CD1C34524497BFE2EEFA6D16A2D2F61204557DDC01E68D943471C52551A8A434FFF392FA83A1F1BF271A1A5076BB129C7646212BA0C172B21DD581B14353FD758494622011EAD5442B8DBEFDFDBE1FE7C259D730082FD2381E6G4C87C86E957E3B24ED5098FBAAB4C00CDDA8455E679FC9517779A589EDD0D303813B9C3917BF110A0C3EF0242857E5AF2A1D0DF913526BD240A577330537A9BD6B0BC57F4FD2ACFBE1FB6A6CB56F
	69C143FF261E98E88FD601CEF3EF765D83B65FF9B7565B0A542D86F570691B384F9E37086FA550BE825086205FB457F84FF621FFBA61E08F62B579E5BBF15FC25A6D9D0276D5981F82108378D200B337C48D33F0A776B3EB432D15FC8ECD483334896B5B1A667A6600AC37F864AEBA0A757D45EF50DE9A2CD23B2C1C078D15D80A0524497AFFF8886B36061AA63F47D7671195076866F33FF2A84F207F3D472755C155A4692C6FCF8BB7186606C5510DE3796E23AD66FCB777964BF74B603A721628E91E3DEA2EE9
	F2EF33B16537C57FFBDD667EDA5E9FB27AC71D0BC69FE774AFBB922DFF46ED6103C795737E4F837C625B617DDEFABBD64D94CFFDBA75B62B0D56987847A9BEF7C8984FEA26BD940F6DD2680B9BA272501EA21107D28D5C123308BBB9946A57214227EB6C31FD32419E5DA79B6D51E3EF0BFD6058FB459E4B1718FC5FBBA1E4F68F95B2BF3FD948BC9F002DF93CDBBC3FDE82380CE16279F5C5DB31B90EE265DDD9F9BA60F1D4CBAEAB75A896CBFF6A964FA90E8D9331B45B03F36A500E8318B5CC54C821BD22C6CA
	FCF28A318EB99B75DC332C79E07A02E17CF96B30F00DB487EAA4463326C370AC9E8E7A9F0E38EB9DE69BC23B3FE9E175886B1F59279FF5874786D37FEBFD7AD15E4B69667E6CCEF3FF26091E2D6D254F445AD63FB407A85FBF9A734CB6EDB8DB53A2FEEDEC59C5F60F79D5B6537B4A765D44FC3E54BD2603E51F3841F47107AD2216DAB5DC687F58B6FA8E9F81F18920ED045853DFBAE364C69A9958FCA9D7C5EE8C99F437FE3747ACD32762AA6B30233516B9B192E191ED04A5D163150544BB0B39A3C45D3C701C
	18BB4540EDBDAA64AFF9C058E9A34D4FB6E8AB866EA58B6246C1FB1601EB4894B63E788191AB2654E19CAC9A89319D209520F724687B935AB7EDA4FB8E30E0A4BB67333E7DF56239A00E47EB6B4841EB40A2BE6648A0E3F94DE970D8E3428F46C3BC579B2CCE4C9B05BAB9DED4BB48DC197C0A309464CE8558457199B047AFC878F9E281F90746B959615171210A1B7278602C58DB9B36A96E05930371DDE3621BB82AAF5FC5A63E0C470A4D024712D1E20FA677BBBB5FC92D1CCFE65FA35DDBF7CD47724BCFE97C
	DBBA0A6517B4837FB645271870AC862F7708983C927AEC2345D9FD070C7E3AG5A49G29GB98FC23D81D07B2048B9AB6D65E410BA32D975DBA1A9A926B44256F726635E5E1BBD17ADE25D3C3FFC024C9F79FEDA2C332E6DC16697A1FECDE95EC5717C2CCB63519C68CB82C881A0E3207E8658BFC62C6F18E3BFDB5FA9B041AADD176CEE7AE4CAAC8E43E1980D459ADBDB4B96735ACE0E696B1F4DE3E2558C012B747D6918483D6B21F8FDCC645ED5EF93FBB7847A160F91367A6ED5B37FB38F7118422F7E077A73
	E7F4DF04865A1391B1691EBFE726CB60639279F5D1CC3A622351783D4A4727C421E3F14125E0ED5D2E86E8D142EA6BDB0A28A5D783DDE19C1C5B815A6258B3B0C6773625FFCD7E3EC5500562D8CDBE8E2E33B89D0BAFD266E071C53CA7416B8220FDB90E4597ECAF077BF7BF7C7043DFGB0B94EB66133C216791AF4AC3FD831AE565736934F204653213EC37818001CEF1AC375C776E71497761D4398426A5EB76BA26BDEDCEFEA1C280B77DA2375E7BC1C1475A91D1F907ABE087E25FE559F877969E17C5662BD
	AF5EB35F7D27DC613B45CFCFA5352F3F1F360BC4775D5607398DBD9C2E41D2BAD6E7333565DB715D069E884AEC5E99DD66EFA90E2CBD9C75DD8442F2BE5FAAFEDDB0EA9709369EA67B9CAC1539BEADDB84CFE63B7F68A1069D27186E8BBC5F769D5F6039E17FFD6541E25F67F26C73C4059C7DBCF1D80EFE1EB8AE8F7C2C66149C7B1D18D5B63F93E331BB64C75BAC9F8BBE8E6087988110B0D6446DB7641D04466DA8B17B1443054F98704D3BAE787F35B376DA131C033F2F7F31935FD1342B3A2EFAF1FE01FF45
	097B851EC7118C176CC49D85BCAE374E06F27CDBCE34BFC5F6D2B42BB13DDBC4ADBAEF2C28BB5F2B96F5E772D8916FC3D74376B754F4FFE65B7D37C5693E72E0EB4EE843B517BE827179912107D3B50833C23B5540FD3746B8F79A380610404DB7F0EF1C9373650CB36AFEC94CD7EC601E1584ED29017B021E99C2509EED60920E0AF1716344593655AB3EF7D90E387162BC73736DF8F69B789DDE33C7D764EA1E5E7819E616D71CD51A470B33D61185F944C1FB9640027102770E8CD1973C8A385C89623C634990
	E7575A89E20E49F4ECAF34638192A75CE9FC97B1F29D0A48E55CF45AF836E9703CE29538559344D97C6FAF1B63F55E149D69222E095C7FC30F32B8FD7251F09EE0B1600406B9D9877CB800F9GCBG16812C82480DCF4AAA81B884E085F0G04G62GE6G4C85C882C80EB7EABD9F6ECB0B54412BD8AC7954G0D9A00648F9FE86088C89E8C9DBC964D52F1FDABE01E1C09E22E613E5F8CB0D7E3C0754B813D1BE1691BE4716E4A9FBB8E0D728F9E07467959F949A5B7F77B79F34F09FE2CF9D427D3130DF8B745
	1FE7606A35C6D5736073D2D68FCDB21E4915CEE4F554C938B6CFE46FB3D8BD3421E630FAA81B046BA185E807CC8C57C371F0FF962ED21A482D9F37E1EEF5029F307357FB6D743BA19817B8D154EE2DB5B47F0282185D392E23A5B1F91E9BD41EBE67CE18F3A91787EDAE956EB773FBDE6F05861AAFB346FCE20E55F0FF932EFB09B8CFDA9AF1BDB913C01FGCC7EB1DDE17997CB83714B9EC47E5AC94C2F7E5328AF9B0E7655552FC3E1DE4F9C3D1333F2F68CDE0D40AB1F5F33785F6E9D6C9C57BAA95A7BBB7F20
	F41FC451C3BEF90A5205A68D7EFC84A18E4631B3D49C0D15B3A6C51E2DA6D2FC6224483355A79691C367C3DF71E411EB77E65C5BF97E3777C8673BEBBA362EF519A87F1049116BAD24783849116B1DEE640C8468DBED2CF76BE9B37F36847EADB23F0ECE08F53633DB4DF4537838397C1A94050E4553273B614263543940E7A4C69C05CA2EC13AAE1DEB6F106DFA3364B2E84EEB31E368A5ED70985A2EC52F655CDA7FDA4E2BC52F65B44DDC4B716F4568F7CCAE48A3B6E35CCD83F749408D23F3A26E2247213B8D
	7CE8AA97624F4848D06047D2393AA84E46F10F1AF017B8AEC143BCGCBF62B81C1BBDD23F15723A82C7DB89D6371C1DAB07471A415516317CCF2BFED6042F2BF25639E9AB5BECBEB3CEF2EDEC86705B4A3A91E9B74131FA69868F3B15BDB38C554CA49E097F90F095AA1EB2B28F94EBCA66A155AEAEA57B886E06EE3F7DB2FEC6941963BFF7EB6A1A2879D2D41B303CF6EAAB1BE76290304D43A5381B22B2E1D7BCB4A7717D49C02A2A176D977E7267BCB69617B8F3242FCEA25BB61F3F01F205D1E931E67A4AD1F
	58FB6AC5856B29CE950FCF2EE1DF8593D1AFAF223562E2504D3247C40DBAA39DF1AB211DB3C578768494C93C67B45D6F365C7FF343717F859E791961B5EDB35D4F4C387F3C975CD79EECCF4BE83DDD887B39FB0A58670FAFA06E88344F9838A19744792459405D308A5AD9864E36D6504EB7F0BFDCABE8138C3BC95AF2EFB97FA70352C54F796B36A2DD6154BB4B7925D3C54E1FCFE345312911B970B90ABFB3B5B287B6D40B9C588CFD73270A5A73F5AFBA1E0E27B274D5A26A3AFF25FED6BC8D4E075344589F50
	E7CFF4AC91FEEB47504D7D7F54693E356EE53A07B620F798F39C3C5037FE9B090F38F97DB679421DBC0B9410EAFA47026B0A1F46FEB7E5B156B0135A418C40A7CE933569D339E1FEAF2F3D13DABF963F25B0678E5E476CE9A70D69A900A7530D7759D5549621DDBADD585842EAE26C7F5F196E9B2B6F3F6FFA6EAB8F2663620DE11B08CFC79DC54989D5D01CA81092AAF86A61347F1E9F265DF861CE739623AD4254456FB3360467312E3D3BF976EE8D539E5D9A5DBF7EC0777A906C69496942365F296A6F9F8DC5
	E11936960767DDD4F527FAE13F855CFE4D879B1878AE495482E423C1D3CF8B1AE5573F28343CB35312CEB26AC996AB706AC986D6E975A41396F175448285DE88FFB003599B3D16BEC8562E11DBA8957D0C2EBFB60B9B8E6D51C99E7B0426C4FCA4D32913B525DE0F8F2AA2ED1FCE8EFC5EA4634767A5D2C9B37DE229243D3BC403DAC811CB6C0A475E8937A52A2F4483A35A556B3241A65D44A68B96383E0FD2FE7478EFFF36642D964DC2326A49C798DF1A64GAD37E487FE5E46FF53D3C75678D58FF4829ED62F
	51CB2626F7AB321594BA4F63B76E1AAEBB2AC4A12769E43395B99D40E7D3874EC81A1B3E0D7D52CAB2FC786BA33209F5AB124FF500D5F5DFD29526D535D13951668948DBC06545D94A0CD98A7EEA63E7BB9A164F371074BA32D6D1A5C71DE4079A3F1E2C515D81D9F32B0AA3647F6AA41DA8278B4D89E32185267B58D8BEAE362C992225A1F4E7E8FA5C7A1475F52032D0009C20A3B11C1699A39CB5EEA9G2C64C03D0E3F26D2833B0D3E211F68FBFEFCFAEBEBBAC9630B44ADD89B726BA44BA9F9BDCAB75C54D2
	37201B3381B6980E94BE8D3F968749CAA2A493DD860332C7DFA81F785487FF248C4224BA79142992C88F38E455FB204A6904724E18G748A53EFB72647B1E591E3B60546EFEEF86D43F63003DAE2711617537F95747FC17CDFC1B19594D3F190141BCA867C0B6C8F09B5D363B19C8D373F2C8EF6D0FF635D971EF873D86A8AD8DD9D4924DFDE43A66405C7FD9D74364B01110993372F0BDF76906E119FCDA50473EE8A99761E496D9D7F327BE05338B7E4D1ACFD26D945CC04A6AE0A4D6DEFB4084EB3368EDB85F4
	68E67A3F682E9501D45BA8EDB132276DDDE179F33814985C5698D871E48C3DB8DDA7B962BBBD100B3E6703E81101AF6BCCCBEB230468C6E9B1161651F7E9E207325891100908B47F33EBEE636D32D0D4E17FCF76C148F4B83C8F0473FD4E10697F1524A722CBEA8136ACBFFC9BCB4056817CD7F1FF042F8B09F73B7BEE1B1F692FFA20A866BB5804597C5D6E6C48E77A95C1DA9B82BE63C96337367AFF11584F001A3C1222681252D965F36073206AEEDDB6FD839E4479B5B106F772F712C1143B43C4735FD0CB87
	887CAF452CFB96GG2CC0GGD0CB818294G94G88G88G5AF854AC7CAF452CFB96GG2CC0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3596GGGG
**end of data**/
}
}
