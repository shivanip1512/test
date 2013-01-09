package com.cannontech.dbeditor.wizard.season;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.season.DateOfSeason;

/**
 * This type was created in VisualAge.
 */
public class SeasonScheduleBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
	private javax.swing.JDialog seasonCreationDialog = null;
	private javax.swing.JLabel ivjJLabelAssignedSeasons = null;
	private javax.swing.JLabel ivjJLabelScheduleName = null;
	private javax.swing.JPanel ivjJPanelSeason = null;
	private javax.swing.JTable ivjJTableSeasons = null;
	private javax.swing.JTextField ivjJTextFieldSeasonScName = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JButton ivjJButtonEdit = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	
	private final String SEASON_SPLIT_FIRST_HALF = "&1&";
	private final String SEASON_SPLIT_SECOND_HALF = "&2&";  

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SeasonScheduleBasePanel() {
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
	if (e.getSource() == getJButtonCreate()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonEdit()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldSeasonScName()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTableSeasons.mouse.mousePressed(java.awt.event.MouseEvent) --> SeasonScheduleBasePanel.jTableSeasons_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableSeasons_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonScheduleBasePanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JButtonEdit.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonScheduleBasePanel.jButtonEdit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonEdit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonScheduleBasePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JTextFieldSeasonScName.caret.caretUpdate(javax.swing.event.CaretEvent) --> SeasonScheduleBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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

private javax.swing.JDialog getSeasonCreationDialog() 
{
	if( seasonCreationDialog == null )
	{
		seasonCreationDialog = new JDialog(SwingUtil.getParentFrame(this), true);

		SeasonDateCreationPanel panel = new SeasonDateCreationPanel()
		{
			public void disposePanel()
			{
				seasonCreationDialog.setVisible(false);
				super.disposePanel();
			}

		};

		seasonCreationDialog.setContentPane( panel );
	}
		
	return seasonCreationDialog;
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
			ivjJButtonCreate.setMnemonic(67);
			ivjJButtonCreate.setText("Create...");
			ivjJButtonCreate.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonCreate.setActionCommand("Create...");
			ivjJButtonCreate.setMinimumSize(new java.awt.Dimension(81, 25));
			// user code begin {1}
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
 * Return the JButtonEdit property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonEdit() {
	if (ivjJButtonEdit == null) {
		try {
			ivjJButtonEdit = new javax.swing.JButton();
			ivjJButtonEdit.setName("JButtonEdit");
			ivjJButtonEdit.setMnemonic(68);
			ivjJButtonEdit.setText("Edit...");
			ivjJButtonEdit.setMaximumSize(new java.awt.Dimension(65, 25));
			ivjJButtonEdit.setActionCommand("Edit...");
			ivjJButtonEdit.setMinimumSize(new java.awt.Dimension(65, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonEdit;
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
			ivjJButtonRemove.setMnemonic(86);
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonRemove.setActionCommand("Remove");
			ivjJButtonRemove.setMinimumSize(new java.awt.Dimension(81, 25));
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
 * Return the JLabelAssignedSeasons property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssignedSeasons() {
	if (ivjJLabelAssignedSeasons == null) {
		try {
			ivjJLabelAssignedSeasons = new javax.swing.JLabel();
			ivjJLabelAssignedSeasons.setName("JLabelAssignedSeasons");
			ivjJLabelAssignedSeasons.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelAssignedSeasons.setText("Assigned Seasons:");
			ivjJLabelAssignedSeasons.setMaximumSize(new java.awt.Dimension(106, 16));
			ivjJLabelAssignedSeasons.setMinimumSize(new java.awt.Dimension(106, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssignedSeasons;
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelScheduleName() {
	if (ivjJLabelScheduleName == null) {
		try {
			ivjJLabelScheduleName = new javax.swing.JLabel();
			ivjJLabelScheduleName.setName("JLabelScheduleName");
			ivjJLabelScheduleName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelScheduleName.setText("Schedule Name:");
			ivjJLabelScheduleName.setMaximumSize(new java.awt.Dimension(103, 19));
			ivjJLabelScheduleName.setMinimumSize(new java.awt.Dimension(103, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelScheduleName;
}
/**
 * Return the JPanelSeason property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSeason() {
	if (ivjJPanelSeason == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitlePosition(2);
			ivjLocalBorder.setTitleJustification(4);
			ivjLocalBorder.setTitle("Season Dates");
			ivjJPanelSeason = new javax.swing.JPanel();
			ivjJPanelSeason.setName("JPanelSeason");
			ivjJPanelSeason.setBorder(ivjLocalBorder);
			ivjJPanelSeason.setLayout(new java.awt.GridBagLayout());
			ivjJPanelSeason.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjJPanelSeason.setMinimumSize(new java.awt.Dimension(350, 330));

			java.awt.GridBagConstraints constraintsJLabelAssignedSeasons = new java.awt.GridBagConstraints();
			constraintsJLabelAssignedSeasons.gridx = 0; constraintsJLabelAssignedSeasons.gridy = 1;
			constraintsJLabelAssignedSeasons.gridwidth = 2;
			constraintsJLabelAssignedSeasons.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAssignedSeasons.ipadx = 20;
			constraintsJLabelAssignedSeasons.ipady = 1;
			constraintsJLabelAssignedSeasons.insets = new java.awt.Insets(8, 10, 0, 63);
			getJPanelSeason().add(getJLabelAssignedSeasons(), constraintsJLabelAssignedSeasons);

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 0;
			constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonRemove.ipadx = 4;
			constraintsJButtonRemove.insets = new java.awt.Insets(3, 7, 8, 40);
			getJPanelSeason().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJButtonEdit = new java.awt.GridBagConstraints();
			constraintsJButtonEdit.gridx = 1; constraintsJButtonEdit.gridy = 0;
			constraintsJButtonEdit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonEdit.ipadx = 20;
			constraintsJButtonEdit.insets = new java.awt.Insets(3, 4, 8, 6);
			getJPanelSeason().add(getJButtonEdit(), constraintsJButtonEdit);

			java.awt.GridBagConstraints constraintsJButtonCreate = new java.awt.GridBagConstraints();
			constraintsJButtonCreate.gridx = 0; constraintsJButtonCreate.gridy = 0;
			constraintsJButtonCreate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonCreate.ipadx = 4;
			constraintsJButtonCreate.insets = new java.awt.Insets(3, 16, 8, 3);
			getJPanelSeason().add(getJButtonCreate(), constraintsJButtonCreate);

			java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneTable.gridx = 0; constraintsJScrollPaneTable.gridy = 2;
			constraintsJScrollPaneTable.gridwidth = 3;
			constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneTable.weightx = 1.0;
			constraintsJScrollPaneTable.weighty = 1.0;
			constraintsJScrollPaneTable.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSeason().add(getJScrollPaneTable(), constraintsJScrollPaneTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSeason;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneTable().setViewportView(getJTableSeasons());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
/**
 * Return the JTableSeasons property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableSeasons() {
	if (ivjJTableSeasons == null) {
		try {
			ivjJTableSeasons = new javax.swing.JTable();
			ivjJTableSeasons.setName("JTableSeasons");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableSeasons.getTableHeader());
			getJScrollPaneTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableSeasons.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableSeasons.setModel( getJTableModel() );
			ivjJTableSeasons.setDefaultRenderer( Object.class, new SeasonScheduleCellRenderer() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableSeasons;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 3:57:28 PM)
 * @return com.cannontech.dbeditor.wizard.seasonschedule.SeasonDatesTableModel
 */
private SeasonDatesTableModel getJTableModel() 
{
	if( !(getJTableSeasons().getModel() instanceof SeasonDatesTableModel) )
		return new SeasonDatesTableModel();
	else
		return (SeasonDatesTableModel)getJTableSeasons().getModel();
}
/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSeasonScName() {
	if (ivjJTextFieldSeasonScName == null) {
		try {
			ivjJTextFieldSeasonScName = new javax.swing.JTextField();
			ivjJTextFieldSeasonScName.setName("JTextFieldSeasonScName");
			ivjJTextFieldSeasonScName.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldSeasonScName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjJTextFieldSeasonScName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(40));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSeasonScName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.season.SeasonSchedule sSched = null;
	if( val != null )
		sSched = (com.cannontech.database.data.season.SeasonSchedule)val;
	else
		sSched = new com.cannontech.database.data.season.SeasonSchedule(
					com.cannontech.database.db.season.SeasonSchedule.getNextSeasonScheduleID() );

	sSched.setScheduleName( getJTextFieldSeasonScName().getText() )	;

	sSched.getSeasonDatesVector().removeAllElements();
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		DateOfSeason d = getJTableModel().getRowAt(i);
		d.setSeasonScheduleID( sSched.getScheduleID() );
		
		//this is where the gaminess starts...
		//the server can't handle the december to january jump, so it must be split into two seasons
		int startMonth = d.getSeasonStartMonth().intValue();
		int endMonth = d.getSeasonEndMonth().intValue();
		if(endMonth < startMonth)
		//if(startMonth == 12 && endMonth < startMonth)
		{
			DateOfSeason firstHalf = new DateOfSeason();
			DateOfSeason secondHalf = new DateOfSeason();
			firstHalf.setSeasonScheduleID(sSched.getScheduleID());
			secondHalf.setSeasonScheduleID(sSched.getScheduleID());
			secondHalf.setSeasonName(SEASON_SPLIT_SECOND_HALF + d.getSeasonName().replaceAll(SEASON_SPLIT_FIRST_HALF, ""));
			//set start date to January 1st.
			secondHalf.setSeasonStartMonth(new Integer(1));
			secondHalf.setSeasonStartDay(new Integer(1));
			//set end date to the end date of the original
			secondHalf.setSeasonEndMonth(d.getSeasonEndMonth());
			secondHalf.setSeasonEndDay(d.getSeasonEndDay());
			
			//adjust the first half to end on December 31st.
			firstHalf.setSeasonStartMonth(d.getSeasonStartMonth());
			firstHalf.setSeasonStartDay(d.getSeasonStartDay());
			firstHalf.setSeasonEndMonth(new Integer(12));
			firstHalf.setSeasonEndDay(new Integer(31));
			firstHalf.setSeasonName(SEASON_SPLIT_FIRST_HALF + d.getSeasonName().replaceAll(SEASON_SPLIT_FIRST_HALF, ""));
			
			sSched.getSeasonDatesVector().add(firstHalf);
			sSched.getSeasonDatesVector().add(secondHalf);
		}
		else		
			sSched.getSeasonDatesVector().add( getJTableModel().getRowAt(i) );
	}

	return sSched;
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
	getJTableSeasons().addMouseListener(this);
	getJButtonCreate().addActionListener(this);
	getJButtonEdit().addActionListener(this);
	getJButtonRemove().addActionListener(this);
	getJTextFieldSeasonScName().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SeasonScheduleBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 444);

		java.awt.GridBagConstraints constraintsJTextFieldSeasonScName = new java.awt.GridBagConstraints();
		constraintsJTextFieldSeasonScName.gridx = 2; constraintsJTextFieldSeasonScName.gridy = 1;
		constraintsJTextFieldSeasonScName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSeasonScName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldSeasonScName.weightx = 1.0;
		constraintsJTextFieldSeasonScName.ipadx = 79;
		constraintsJTextFieldSeasonScName.insets = new java.awt.Insets(15, 0, 3, 17);
		add(getJTextFieldSeasonScName(), constraintsJTextFieldSeasonScName);

		java.awt.GridBagConstraints constraintsJLabelScheduleName = new java.awt.GridBagConstraints();
		constraintsJLabelScheduleName.gridx = 1; constraintsJLabelScheduleName.gridy = 1;
		constraintsJLabelScheduleName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelScheduleName.ipadx = 6;
		constraintsJLabelScheduleName.insets = new java.awt.Insets(16, 8, 4, 0);
		add(getJLabelScheduleName(), constraintsJLabelScheduleName);

		java.awt.GridBagConstraints constraintsJPanelSeason = new java.awt.GridBagConstraints();
		constraintsJPanelSeason.gridx = 1; constraintsJPanelSeason.gridy = 2;
		constraintsJPanelSeason.gridwidth = 2;
		constraintsJPanelSeason.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelSeason.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelSeason.weightx = 1.0;
		constraintsJPanelSeason.weighty = 1.0;
		constraintsJPanelSeason.ipadx = 351;
		constraintsJPanelSeason.ipady = 330;
		constraintsJPanelSeason.insets = new java.awt.Insets(4, 5, 12, 7);
		add(getJPanelSeason(), constraintsJPanelSeason);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getJTableSeasons().setFocusable(false);
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	com.cannontech.common.gui.util.OkCancelPanel o
			= new com.cannontech.common.gui.util.OkCancelPanel();
	
	if( getJTextFieldSeasonScName().getText() == null
		 || ! (getJTextFieldSeasonScName().getText().length() > 0) )
	{
	    setErrorString("Schedule Name cannot be blank.");
        return false;
	}
	
	// check for duplicate season names
	// important because DateOfSeason table has key index on SeasonScheduleID,SeasonName
	Map<String, Integer> seasonNames = new HashMap<String, Integer>(); 
	for( int i = 0; i < getJTableModel().getRowCount(); i++ ) {
	    
	    String thisSeasonName = getJTableModel().getRowAt(i).getSeasonName();
	    if (seasonNames.keySet().contains(thisSeasonName)) {
	        
	        Integer conflictRow = seasonNames.get(thisSeasonName);
	        setErrorString("Season name '" + thisSeasonName + "' for row " + (i + 1) + " conflicts with the season name for row " + (conflictRow + 1) + ".");
	        return false;
	        
	    } else {
	        seasonNames.put(thisSeasonName, i);
	    }
	}

	SeasonScheduleChecker overlapChecker = new SeasonScheduleChecker();
    for( int i = 0; i < getJTableModel().getRowCount(); i++ ) {
        DateOfSeason season = getJTableModel().getRowAt(i);
        try{
            overlapChecker.addSeason(season);
        } catch (OverlappingSeasonException e){
            setErrorString("Improperly defined season.  " + e.getMessage());
            return false;
        }
    }
	return true;

}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	SeasonDateCreationPanel panel = 
				(SeasonDateCreationPanel)getSeasonCreationDialog().getContentPane();

	getSeasonCreationDialog().setTitle("Season Date Creation");
	panel.resetValues();

	getSeasonCreationDialog().pack();
	getSeasonCreationDialog().setLocationRelativeTo(this);
	getSeasonCreationDialog().show();

	if( panel.getResponse() == SeasonDateCreationPanel.PRESSED_OK )
	{
		getJTableModel().addRow( panel.getDateOfSeason() );

		fireInputUpdate();
	}

	return;
}
/**
 * Comment
 */
public void jButtonEdit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int row = getJTableSeasons().getSelectedRow();

	if( row < 0 || row >= getJTableModel().getRowCount() )
		return;

	SeasonDateCreationPanel panel = 
				(SeasonDateCreationPanel)getSeasonCreationDialog().getContentPane();

	getSeasonCreationDialog().setContentPane( panel );
	getSeasonCreationDialog().setTitle("Edit Season Date");
	panel.setDateOfSeason( 
			getJTableModel().getRowAt(row) );
	
	getSeasonCreationDialog().pack();
	getSeasonCreationDialog().setLocationRelativeTo(this);
	getSeasonCreationDialog().show();

	
	if( panel.getResponse() == SeasonDateCreationPanel.PRESSED_OK )
	{
		getJTableModel().setDateOfSeasonRow( panel.getDateOfSeason(), row );

		fireInputUpdate();
	}

	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int row = getJTableSeasons().getSelectedRow();

	if( row >= 0 && row < getJTableModel().getRowCount() )
		getJTableModel().removeRow(row);

	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jTableSeasons_MousePressed(java.awt.event.MouseEvent event) 
{

	int rowLocation = getJTableSeasons().rowAtPoint( event.getPoint() );
	
	getJTableSeasons().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );

	//If there was a double click open a new edit window
	if (event.getClickCount() == 2)
	{
		//if( event.isShiftDown() )
			//showDebugInfo();
		//else
			getJButtonEdit().doClick();
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
		SeasonScheduleBasePanel aSeasonScheduleBasePanel;
		aSeasonScheduleBasePanel = new SeasonScheduleBasePanel();
		frame.setContentPane(aSeasonScheduleBasePanel);
		frame.setSize(aSeasonScheduleBasePanel.getSize());
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableSeasons()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.season.SeasonSchedule sSched = null;
	DateOfSeason firstHalf = null;
	DateOfSeason secondHalf = null;
	
	if( val != null )
	{
		sSched = (com.cannontech.database.data.season.SeasonSchedule)val;

		getJTextFieldSeasonScName().setText( sSched.getScheduleName() );

		for( int i = 0; i < sSched.getSeasonDatesVector().size(); i++ )
		{		
			DateOfSeason theSeason = (DateOfSeason)sSched.getSeasonDatesVector().get(i);
			
			//more gaminess here...
			//the server can't handle the december to january jump, so it was split into two seasons
			//now they must become one again
			String sName = theSeason.getSeasonName();
			if(sName.startsWith(SEASON_SPLIT_FIRST_HALF))
				firstHalf = theSeason;

			else if(sName.startsWith(SEASON_SPLIT_SECOND_HALF))
				secondHalf = theSeason;

			else
				getJTableModel().addRow(theSeason);
		}
		
		if(firstHalf != null && secondHalf != null)
		{
			//make the name acceptable
			firstHalf.setSeasonName(firstHalf.getSeasonName().replaceFirst(SEASON_SPLIT_FIRST_HALF, ""));
			//get the proper end date
			firstHalf.setSeasonEndMonth(secondHalf.getSeasonEndMonth());
			firstHalf.setSeasonEndDay(secondHalf.getSeasonEndDay());
			
			getJTableModel().addRow(firstHalf);
		}
	}


}
}
