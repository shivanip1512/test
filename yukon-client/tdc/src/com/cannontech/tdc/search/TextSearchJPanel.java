package com.cannontech.tdc.search;

import javax.swing.JTable;

/**
 * Insert the type's description here.
 * Creation date: (7/6/2001 4:55:01 PM)
 * @author: 
 */
public class TextSearchJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	// references to the tables we need to search
	private JTable[] tablesToSearch = null;
	
	public static int PRESSED_CANCEL = 0;
	public static int PRESSED_SEARCH = 0;
	private int buttonPressed = PRESSED_CANCEL;
	private javax.swing.JCheckBox ivjJCheckBoxColumn = null;
	private javax.swing.JComboBox ivjJComboBoxColumnName = null;
	private javax.swing.JLabel ivjJLabelColumnName = null;
	private javax.swing.JLabel ivjJLabelSearchText = null;
	private javax.swing.JPanel ivjJPanelSearchOption = null;
	private javax.swing.JCheckBox ivjJCheckBoxCaseSensitive = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonSearch = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private javax.swing.JCheckBox ivjJCheckBoxSearchFromSelected = null;
	private javax.swing.JComboBox ivjJComboBoxSearchText = null;


/**
 * TextSearchJPanel constructor comment.
 */
public TextSearchJPanel() {
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
	if (e.getSource() == getJCheckBoxColumn()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonSearch()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JCheckBoxColumn.action.actionPerformed(java.awt.event.ActionEvent) --> TextSearchJPanel.jCheckBoxColumn_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxColumn_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonSearch.action.actionPerformed(java.awt.event.ActionEvent) --> TextSearchJPanel.jButtonSearch_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSearch_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> TextSearchJPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 5:12:28 PM)
 */
public void executeCancelButtonPressed() 
{
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 3:05:36 PM)
 */
public void findNextOccurence() 
{

	//be sure that a Find Next will search its way down
	getJCheckBoxSearchFromSelected().setSelected(true);

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{			
			jButtonSearch_ActionPerformed( new java.awt.event.ActionEvent(this,
					java.awt.event.ActionEvent.ACTION_PERFORMED, "FindNextOccurence") );
		}
		
	});
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 5:13:50 PM)
 * @return int
 */
public int getButtonPressed() {
	return buttonPressed;
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonSearch property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSearch() {
	if (ivjJButtonSearch == null) {
		try {
			ivjJButtonSearch = new javax.swing.JButton();
			ivjJButtonSearch.setName("JButtonSearch");
			ivjJButtonSearch.setMnemonic('s');
			ivjJButtonSearch.setText("Search");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSearch;
}
/**
 * Return the JCheckBoxCaseSensitive property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCaseSensitive() {
	if (ivjJCheckBoxCaseSensitive == null) {
		try {
			ivjJCheckBoxCaseSensitive = new javax.swing.JCheckBox();
			ivjJCheckBoxCaseSensitive.setName("JCheckBoxCaseSensitive");
			ivjJCheckBoxCaseSensitive.setMnemonic('c');
			ivjJCheckBoxCaseSensitive.setText("Case Sensitive");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCaseSensitive;
}
/**
 * Return the JCheckBoxColumn property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxColumn() {
	if (ivjJCheckBoxColumn == null) {
		try {
			ivjJCheckBoxColumn = new javax.swing.JCheckBox();
			ivjJCheckBoxColumn.setName("JCheckBoxColumn");
			ivjJCheckBoxColumn.setMnemonic('s');
			ivjJCheckBoxColumn.setText("Search By Column");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxColumn;
}
/**
 * Return the JCheckBoxSearchFromSelected property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSearchFromSelected() {
	if (ivjJCheckBoxSearchFromSelected == null) {
		try {
			ivjJCheckBoxSearchFromSelected = new javax.swing.JCheckBox();
			ivjJCheckBoxSearchFromSelected.setName("JCheckBoxSearchFromSelected");
			ivjJCheckBoxSearchFromSelected.setSelected(true);
			ivjJCheckBoxSearchFromSelected.setMnemonic('d');
			ivjJCheckBoxSearchFromSelected.setText("Search From Selected Row Down");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSearchFromSelected;
}
/**
 * Return the JComboBoxColumnName property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColumnName() {
	if (ivjJComboBoxColumnName == null) {
		try {
			ivjJComboBoxColumnName = new javax.swing.JComboBox();
			ivjJComboBoxColumnName.setName("JComboBoxColumnName");
			ivjJComboBoxColumnName.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColumnName;
}
/**
 * Return the JComboBoxSearchText property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSearchText() {
	if (ivjJComboBoxSearchText == null) {
		try {
			ivjJComboBoxSearchText = new javax.swing.JComboBox();
			ivjJComboBoxSearchText.setName("JComboBoxSearchText");
			ivjJComboBoxSearchText.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSearchText;
}
/**
 * Return the JLabelColumnName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelColumnName() {
	if (ivjJLabelColumnName == null) {
		try {
			ivjJLabelColumnName = new javax.swing.JLabel();
			ivjJLabelColumnName.setName("JLabelColumnName");
			ivjJLabelColumnName.setText("Column Name:");
			ivjJLabelColumnName.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelColumnName;
}
/**
 * Return the JLabelSearchText property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSearchText() {
	if (ivjJLabelSearchText == null) {
		try {
			ivjJLabelSearchText = new javax.swing.JLabel();
			ivjJLabelSearchText.setName("JLabelSearchText");
			ivjJLabelSearchText.setText("Search Text:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSearchText;
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
			ivjJPanelButtons.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonSearch = new java.awt.GridBagConstraints();
			constraintsJButtonSearch.gridx = 1; constraintsJButtonSearch.gridy = 1;
			constraintsJButtonSearch.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsJButtonSearch.ipadx = 10;
			constraintsJButtonSearch.insets = new java.awt.Insets(2, 184, 5, 5);
			getJPanelButtons().add(getJButtonSearch(), constraintsJButtonSearch);

			java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
			constraintsJButtonCancel.gridx = 2; constraintsJButtonCancel.gridy = 1;
			constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsJButtonCancel.ipadx = 12;
			constraintsJButtonCancel.insets = new java.awt.Insets(2, 6, 5, 4);
			getJPanelButtons().add(getJButtonCancel(), constraintsJButtonCancel);
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
 * Return the JPanelSearchOption property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSearchOption() {
	if (ivjJPanelSearchOption == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Search Options");
			ivjJPanelSearchOption = new javax.swing.JPanel();
			ivjJPanelSearchOption.setName("JPanelSearchOption");
			ivjJPanelSearchOption.setBorder(ivjLocalBorder);
			ivjJPanelSearchOption.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxColumn = new java.awt.GridBagConstraints();
			constraintsJCheckBoxColumn.gridx = 1; constraintsJCheckBoxColumn.gridy = 1;
			constraintsJCheckBoxColumn.gridwidth = 2;
			constraintsJCheckBoxColumn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxColumn.ipadx = 60;
			constraintsJCheckBoxColumn.insets = new java.awt.Insets(5, 10, 1, 170);
			getJPanelSearchOption().add(getJCheckBoxColumn(), constraintsJCheckBoxColumn);

			java.awt.GridBagConstraints constraintsJLabelColumnName = new java.awt.GridBagConstraints();
			constraintsJLabelColumnName.gridx = 1; constraintsJLabelColumnName.gridy = 2;
			constraintsJLabelColumnName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelColumnName.ipadx = 8;
			constraintsJLabelColumnName.insets = new java.awt.Insets(6, 10, 12, 0);
			getJPanelSearchOption().add(getJLabelColumnName(), constraintsJLabelColumnName);

			java.awt.GridBagConstraints constraintsJComboBoxColumnName = new java.awt.GridBagConstraints();
			constraintsJComboBoxColumnName.gridx = 2; constraintsJComboBoxColumnName.gridy = 2;
			constraintsJComboBoxColumnName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxColumnName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxColumnName.weightx = 1.0;
			constraintsJComboBoxColumnName.ipadx = 72;
			constraintsJComboBoxColumnName.insets = new java.awt.Insets(2, 0, 7, 71);
			getJPanelSearchOption().add(getJComboBoxColumnName(), constraintsJComboBoxColumnName);

			java.awt.GridBagConstraints constraintsJCheckBoxCaseSensitive = new java.awt.GridBagConstraints();
			constraintsJCheckBoxCaseSensitive.gridx = 1; constraintsJCheckBoxCaseSensitive.gridy = 3;
			constraintsJCheckBoxCaseSensitive.gridwidth = 2;
			constraintsJCheckBoxCaseSensitive.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxCaseSensitive.ipadx = 10;
			constraintsJCheckBoxCaseSensitive.insets = new java.awt.Insets(8, 10, 3, 240);
			getJPanelSearchOption().add(getJCheckBoxCaseSensitive(), constraintsJCheckBoxCaseSensitive);

			java.awt.GridBagConstraints constraintsJCheckBoxSearchFromSelected = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSearchFromSelected.gridx = 1; constraintsJCheckBoxSearchFromSelected.gridy = 4;
			constraintsJCheckBoxSearchFromSelected.gridwidth = 2;
			constraintsJCheckBoxSearchFromSelected.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSearchFromSelected.ipadx = 10;
			constraintsJCheckBoxSearchFromSelected.insets = new java.awt.Insets(4, 10, 9, 135);
			getJPanelSearchOption().add(getJCheckBoxSearchFromSelected(), constraintsJCheckBoxSearchFromSelected);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSearchOption;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 5:19:46 PM)
 * @return java.lang.String
 */
public String getSearchText() 
{
	if( getJComboBoxSearchText().getSelectedItem() == null )
		return null;
	else
		return getJComboBoxSearchText().getSelectedItem().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 10:34:01 AM)
 * @return JTable
 */
public JTable[] getTablesToSearch() {
	return tablesToSearch;
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
	// user code end
	getJCheckBoxColumn().addActionListener(this);
	getJButtonSearch().addActionListener(this);
	getJButtonCancel().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TextSearchJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(388, 234);

		java.awt.GridBagConstraints constraintsJLabelSearchText = new java.awt.GridBagConstraints();
		constraintsJLabelSearchText.gridx = 1; constraintsJLabelSearchText.gridy = 1;
		constraintsJLabelSearchText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSearchText.ipadx = 15;
		constraintsJLabelSearchText.insets = new java.awt.Insets(8, 9, 14, 2);
		add(getJLabelSearchText(), constraintsJLabelSearchText);

		java.awt.GridBagConstraints constraintsJPanelSearchOption = new java.awt.GridBagConstraints();
		constraintsJPanelSearchOption.gridx = 1; constraintsJPanelSearchOption.gridy = 2;
		constraintsJPanelSearchOption.gridwidth = 2;
		constraintsJPanelSearchOption.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelSearchOption.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelSearchOption.weightx = 1.0;
		constraintsJPanelSearchOption.weighty = 1.0;
		constraintsJPanelSearchOption.ipadx = -10;
		constraintsJPanelSearchOption.ipady = -7;
		constraintsJPanelSearchOption.insets = new java.awt.Insets(9, 9, 1, 10);
		add(getJPanelSearchOption(), constraintsJPanelSearchOption);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.gridwidth = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.weightx = 1.0;
		constraintsJPanelButtons.weighty = 1.0;
		constraintsJPanelButtons.insets = new java.awt.Insets(2, 9, 9, 10);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJComboBoxSearchText = new java.awt.GridBagConstraints();
		constraintsJComboBoxSearchText.gridx = 2; constraintsJComboBoxSearchText.gridy = 1;
		constraintsJComboBoxSearchText.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSearchText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxSearchText.weightx = 1.0;
		constraintsJComboBoxSearchText.ipadx = 151;
		constraintsJComboBoxSearchText.insets = new java.awt.Insets(4, 2, 9, 11);
		add(getJComboBoxSearchText(), constraintsJComboBoxSearchText);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}	
	
	getJComboBoxSearchText().requestFocus();	
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 1:56:59 PM)
 * @return boolean
 */
private boolean isTextEnteredEqual( String tableText )
{
	if( getJCheckBoxCaseSensitive().isSelected() )
		return tableText.indexOf(this.getSearchText()) != -1;
	else
		return tableText.toUpperCase().indexOf(this.getSearchText().toUpperCase()) != -1;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{


	buttonPressed = PRESSED_CANCEL;
	executeCancelButtonPressed();
	
	return;
}
/**
 * Comment
 */
public void jButtonSearch_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getSearchText() == null )
		return;

	boolean found = false;
	//only add new items to the editable JComboBox
	for( int i = 0; i < getJComboBoxSearchText().getModel().getSize(); i++ )
		if( getJComboBoxSearchText().getModel().getElementAt(i).toString().equals(getJComboBoxSearchText().getSelectedItem().toString()) )
		{
			found = true;
			break;
		}

	if( !found )
		getJComboBoxSearchText().addItem( getJComboBoxSearchText().getSelectedItem().toString() );


	found = false;
	for( int k = 0; k < getTablesToSearch().length && !found; k++ )
	{
		JTable currTable = getTablesToSearch()[k];
		
		//determine what row we should start on, if we have the last row
		//  selected, start at row ZERO
		int begin = 0;

		if( getJCheckBoxSearchFromSelected().isSelected()
			 && currTable.getSelectedRow() < (currTable.getRowCount()-1) )
		{
			begin = currTable.getSelectedRow() + 1; //search 1 beyond the selected row
		}
	
		for( int i = begin; i < currTable.getRowCount() && !found; i++ )
		{
			for( int j = 0; j < currTable.getColumnCount() && !found; j++ )
			{
				//only search column specific for the first table
				if( k == 0 && getJCheckBoxColumn().isSelected() && getJComboBoxColumnName().getSelectedIndex() != j )
					continue;
					
				if( isTextEnteredEqual(currTable.getModel().getValueAt(i, j).toString()) )
				{
					found = true;
					currTable.setRowSelectionInterval( i, i );
	
					//scroll to a location that makes the new selected row appear
					currTable.scrollRectToVisible( new java.awt.Rectangle(
						0,
						currTable.getRowHeight() * (i+1) - currTable.getRowHeight(),  //just an estimate that works!!
						100,
						100) );	
				}
			}
			
		}

	}


	if( found )
	{
		buttonPressed = PRESSED_SEARCH;
		executeCancelButtonPressed();
	}
	else
	{
		String message = "Unable to find the text : " + getSearchText();
		if( getJCheckBoxColumn().isSelected() )
			message += "\nin column : " + getJComboBoxColumnName().getSelectedItem().toString();
			
		javax.swing.JOptionPane.showMessageDialog( this, message, 
				"Text Not Found", javax.swing.JOptionPane.PLAIN_MESSAGE );
	}
	
	return;
}
/**
 * Comment
 */
public void jCheckBoxColumn_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJComboBoxColumnName().setEnabled( getJCheckBoxColumn().isSelected() );
	getJLabelColumnName().setEnabled( getJCheckBoxColumn().isSelected() );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TextSearchJPanel aTextSearchJPanel;
		aTextSearchJPanel = new TextSearchJPanel();
		frame.setContentPane(aTextSearchJPanel);
		frame.setSize(aTextSearchJPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 5:05:32 PM)
 * @param columns java.lang.String[]
 */
public void setColumnNames(String[] columns) 
{	
	if( columns != null )
	{
		boolean needChange = false;
		
		//make sure we have a different set of new columns
		if( getJComboBoxColumnName().getItemCount() == columns.length )
		{
			for( int i = 0; i < columns.length; i++ )
				if( getJComboBoxColumnName().getItemAt(i).toString().equals( columns[i] ) )
					continue;
				else
				{
					needChange = true;
					break;
				}
		}
		else
			needChange = true;

		if( needChange 
			 || getJComboBoxColumnName().getItemCount() == 0 )
		{
			getJCheckBoxColumn().setSelected(false);
			getJComboBoxColumnName().removeAllItems();
			
			for( int i = 0; i < columns.length; i++ )
				getJComboBoxColumnName().addItem( columns[i] );
		}

	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 10:34:01 AM)
 * @param newTableToSearch JTable
 */
public void setTablesToSearch(JTable newTablesToSearch[]) {
	tablesToSearch = newTablesToSearch;	
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8EF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4145715987F08E2C4A5CA0CDA1218AC27250D5B32DB1B13663833F4E39B4EB1696A56E659461E3ACDCE8FCDEDE3FAC862A60F81860828D8D1D041D0B4EBCC90D1D186C2FE78B102128DAACBC963A68E4C878C8E730DB35FB0430F32777D5CB75F8CB320E6DD4E39677B5E3D6F3D7B5E7DFB773EEFA085EF45E5259B8B88C94FA2622FBA1D104D6F93F251F06EDB080BD08CA6227B7BB340CEF2CF
	E1A6BC230136A44EE04AA733CCB950DE8E6D14E586D3A13C6FA2F3CD65B5709206CFA65056DBAB8FCF4CE763AE64134D5A3FBB1E874F6DG7100234F4D8A651FFD22C86013859EA15DC84853BD34CF5909E201B3C33B8DA08AA02E0777DF894FDC46F9FA2DD9743BBCAF0EEC7E49E163B6BA0FEECE0422DEBE36CEBCCB4923699BFBA92F73A77244988D5A89GF47399E4CDEBA56D5FF6357743EEF520225B62F0288ECD696E2B502C5D95EE45626A6E2B2DEDD7FC5AA576FE2E4D62D06CD77A681C8BCE6430B988
	F9875A298CB75BC779AC0777478156CA7C45A10A2F3499CC4DGD7EC38378F8E570935F70EBCCA367C4C1131A34C5EFCB63E27B81BFFEFB11ED73377B2BD9C5ACD651D82B452EFB095G1881BA81FCG7B197E5FABF8971E75DDDA23BAB020BA6A1D4E863B557529EAD56C70DEDB8B94013B28F42BAEABA1DC4797CE1407D84FC44047FDF54B6338CC62CF223E93AEBEC2D23EF75E189DC2A6A971154668FEAE0B78FE3FCCB86F58303CD777875AE3A06F3F90BD6F5499EAC6D6885E096B0E98D344BC1BF43CCB2F
	D2DD3F41ECA09F5E5305FDF0FCAE33812B8E4FEDF39E6BCF5B9A5056DFC55B685C0EEBE94DDCCC720F370614C32C215ED8E767EBE837872F651F984F88BB6719AD5772D3060F556179DABA991E36D7G2DE040E06276F534107A2A8F5AB1G89GC9G9B8108837AB950463402E3B7E9E3ED9617566031592D0A0B7031C7F76F01A7381A4B5262F0EB96C73752E1F159ACDDF645CDC41F533B695A3A82463D4FEC3F9BF81CD59C0A4B2259D4874CEE99D0B4456566FD1AF66F24FDDC8A23365B8694E0B060A4B8EF
	6B6EDD7034DB5C5AFFBB2D96CD71776074765DD4A703168E1891A600F7AE173F96D1F9D5417EDB81AECBB92CE1731DD7DC90ADEAEBDBD5752A4759CEF7C2929CD44E0F3258610477C8874A38E3AF45ED02F60E0A7318767CC9B7CF132D1BAE5F629AC29F4B9A227138D8C51BF93193ED26E95B4324FC5D3910B643E22E4AED25CD8D761D9DECCE3AAE5EDE646B8C63CB45EAE8DF3A75D831A2AC0F0C2FB97FF6292B24FDD4D7BED8F38CC02214F17D1ED2EE338D963702D67A198B8CCC317AA54E5719BC046788DD
	D716DC7FFA06CF06B9B78394BBD1F6DF8EF586757F3AFB99AFBBAF6321DF4F7FA58FAE236AC2FAAEA9030AC3EBD21C0A438AAFEEC17B30105A1E705B9ED50EE9E2F8A778F9A3B816DB624F95D2DF34E81A4B56655114CB0A26599C3DFE7AC5464B6D69F27AC384727A3C106ED175021F4B7EDF96D29F69D69D8E05A9CB8E9174AB4CC7953BB2G0BEED4BD8E0D63FB0A281DDFD506C40C68AF22894F20456E2171066787ABACDB699EDC3681B0819A32C80C1345D5195FB0DF839C4DF37A60B96AC4DF749EC4DFBC
	B47CGD975555B46D0E7C96C43CD469427B8CB1CFEDF64EB7AE2EF6835DBD82C90F8B7DF6BABBD79708C4ED5386D7F47FEF6A6DDB3188E8154DDC35B7F373D140E71C598FD23EA37F3D1CA997FE49F1D5B2DDC73A89016E9A0BBAF65764C3EBF8551BA048CB97D677B288C6D3A98D86422B25CBA0C79DFBB2C478E90F50D4BB1799A17836F37C67633C2FB54656F3744256FB7F5847BED047696DD3FE2B76F47657452912ABB167375DB86AD3E8A37976C2F36D61F5BD51E4C917D2DEE4C614E1D26B2DC826DC400
	E4B76A7919ED184345DC0DA39B9E5A97B6BF5DA8561123694E692918431DAD21BC1B01562921FE5A776B4F11FA37DB6D3631A3CC48B623D8EF879C17D37CA6BCFBECAE3756820E6AC3BDECAF266B25E71CC00BB33FEAB8CF6C558EFC178354F9A81EE49468561E8278CC403F831041663BFF788475E3E6A8957C512638FAD45700E2FD07CA36426255AAD84C2828E764B3747D886A21642B8F61595F5827F4DFEDD0FDE0F51E8147D775C19335061F697DA39D1837BD386BE3314E646FACA6F5E33B0C217268489D
	A546E99EAE7B840FDF87235FB5E524FE79DAB617C71A14C79A74299CC4F97C28DAAF0FBE40C783A48EE2AD31270C5AC1FF03C753D487F741DB5C8D671FB0027CD7435C440B7CE78E0562EF76A27FFE1B0EFFA3CD1F6C37481F1FA727CE86674E2DDE7FB9FF45BB7E4C67B965934CBFB4AF4FA9F3A57EA9060F566179BE5B981E369300166F43BC68DF8ED07F54209D8D908F308AE083G99C23FD8D8FC12CC12E736C09D67F2D86C17828E924C77CA288DC36C976D3692AAC3967B891E2B354341F2A89E8A5C3BB9
	284D47B50F9BF7A028FDB9E89C575F079D7BC347A57490F176CC498ABEFBE82E26411C11GBCFED5E7640875448DE11C3A7E09BECEBD3E6304F1EB18B835DE2CAFED486FA36532560CA825F156BCECB035835881A28192GD683248E230E1EAD79E0B29DB143EBD79B81578C67C3E37099AEE57A7AED6AA2EFE4B2DD640D0452450ECBFBC36B02FB9715A153454A41D03AB884F31AC7D097DB2AD197EDA31897FEF75C3AEEEEBF2BA65DCFE450FE789A1E981276GF4DE033432B3EEBD34D3A52E709445650FC21C
	9F4535A61D26E7FFAB347BGA28116CB5AB30C16B44A4F869A0FB6CADA4769F0B93D0CABE2CE3976514035AF9F8DDFDFF09E6A24BC02736E607CB8F702315C0E1FBB90BE777D75015039EF6A0150396F9F8E6C64F818C3DF81A37D55839367464637C26546A5EC1FBF5E33034D6DF6ABEE66D8CD964DA2E84FEB3BC9089C793F20C896B25AC52E8D670ABEE7B4357138341D9FE4216DE4DE47DC2851C8E3F79D345B816C57D167F5994176B98DE831574519FBFD3CBEF9CC49AE23E75BAA2027GEC86483BE1B015
	5F40D8927B5624717E0CEF02F8BF2BB43C8E671506566122D21A4739D50F2BDB8956533252A2C973C72D96CD9920A265FD7E060D57AC2EDEE51C1E3F4346F3DA38715FAF8DE78BDCDF27C6747ABAA17535E990F357569B282F6D55D4DF53203D84A071866AAB5D9C2C2FE420ED38A16EF2EF0C5797673DB90077C9690F7FB894BCDF4E98645FE3FC1E4A31F073F51F2435175DB224FAB4C2D60C51B58F1F44585A846D4BG53GE24774397C5FFA69995663328C00E8AEB915EE9295C147D61C4231E950BF8FF065
	G2DG76884CDF53765058F52E55522558F93AC6EBA7CE1B23511A06D7841C76191397C41CFE1EEC13F4FEA47875F26FF51A331F83CB6DD2615476CFCDF89E179510C72E0D60E7C6DA8466F93FBC2D2767CD61742AA92833A73274B2EE867C65A9FE39BA27044B4F3E4FFCF65A941E07ED15789FB17CF29D1E67E7AD8CCF5BC9C0FB67AEBCA3AE315CA28A5AF192E7E4B1A2915AC992571D05E74953F7219F6FA88B6F07FB4AC27BE1F1D968D8DAD6B6F1ACFD37AC1C7FF0B93EBC98EC2B25D37D322B1A9ACE0EF7
	B1DFEA1E4A6515A571F3987E328E4F65D8D00D793C93E8EB2722FC0A4CA8C773B444DDB6239CDBA56E07E614E32744BDBB04E363A56E40B00EFDDC624606F16CEA407175BCF8926F300A269BCCED5331FEB8FE0566F5B26721D5C2ED6DD9174D5AE069EDE51E49634BEFD85EC047C7CE4778D2D4C567DC856D75G1B26E34E697BCF4CB9939ED9CCDAA34E072C0DB7341FB0664C6072AE1F614FB963A16764BC5BCFA14FBE20474DE0FAB82577936AFB6B20C385BCB9BDB100CEED81CACF9D7D5781F43A5E5C8CBD
	3DA8408F33F463795E1EFA9B77366529F8E27F3B1A30F57F8BE2CFF9B3451DEB47C1F2E856EB5962BE22A74CFD840BCCFC7FF55598531BE6223EFA6D616A8591F944F9D0A4470E4EC43BD8477D955A2BA56E31EC198FCADCD9AE5ACFFEA4461F9F520F07648A34C781E281D68224C87AD30CCE6FF3B26F06DC98208940FAB752BFB3CE143351F969B3A5F23C7F0D8ADE74BDB6845D1F9776CD48836F14370620E591FFCE986EAE154779FE9687879C8AA4A4366E183BD9ED46F04F83CC3B9B4F99351A5EB3EA9058
	565FCD3F29FE031A91291B454E382C26C9560979ABFD9643988A87051DD5427B10F8677A4A73228E274DC29D4E70218E63E7218EA219CD24C13BE0B66296B05C2838BB6338FD5513655793811DBF941E72746B6A645762916E7BG667CDCAF75E654CBA57066FAB03C6B5743DA734D68818175D0A2E47FB14C39DB3CF3FE99AC6EBE8950AA7931E3DB70DBB8BC913F2CC978DD1E4D6FCA705B5DCE92BF6E4EE6B260637E7EB6477D6E164771B372B76C1C681B4D4F48F409FF0961B59D1E1FD5872A702C0AG5A46
	59F8C774C2B90D194DD190D7GC681E2G92811E0E42DCE3D5997D36BFE14D502EBADBA199316B52873E3F4FCF5D5E376BFF4846B3A1713187494A992F24073BE3599845FDA0F38EFF3E50FE1061797EC6AA687E2A0056862081C48144812C1C037B4BB9D84E77F746EA536AB54D525D47EE01F1F39DB6456BC75364186F2D3EBA545E1245BAB64C996F33FCDCC948F1881BE1DC4EBD01E3F97CCB4D2239FD176AE249994F6DB3CC181F974318AAGBBC044BD7C9E017779D5B676093D07677FAB6019A4F8F0BF2A
	491D4CEF33F47E1482E3B75D4363A7EDE74EB518FE7B469BEF3C8440573AEEA74DCF4F855EC90BFD3C7F363FDE6084DA2BF15A76AF82F2FEBF996B1BCF76844CEBF1C3BE6AF05BB45B2002F22AB931DF5F07578D1F385401CB8A7D9C23D8C57DB0DB8B2E9F284C7267E27D705805607A01528FC9FA52D828FBFD33243FEA8EC5EF1D0B67596B1974F958FDD858FC1CD77F7B9C731B55A13FF3D138B2176BCE934F47E79E94F32C79C2BFC77E62BF9B53434C11A0462E1C6B4F935FE26B02DA13787FF84C7DA98D81
	0247AF7350B70B3FF5BFE97A59AB69874370C895F357C57BF93C503FA51B5B65A9AF5A25964D6DB28A1EF1517C7E0B5B65D25FE4F6192D334B15B0B6B95AEF179B227DF1104B5CFD0C551B7AEF8B129673D79D0D5F7BCBDA2799F54565B105DE93885A3EE1C66BC65AF2DA468BDF5FD465FFFFC577FE286A6B1C55B7F736FCF0C7F97058797B7270F5E8DAF968BA74757250F5E86651096F6C720E067F1E3D6B287EFBB60F7DDA95753FD430011CF986D3B94047G6D73B06EBFF0340C30389FA2661F3176527BA8
	7A6B998D79CF2B883F575995137F66A62602FECF6CD2413C86687C08DFDCC1752529CE066438EFD4D09939EC3DFD9A6FAA708FD7D05F34ABBD8C4D73524EEA2CD71D73B0D7F54BDAF7D4622A2A9117ACF115AC27AD1EEFB0D54E473A76BD937E0E2A89F0237331CEFA3E04A65C21FE63576B31D5F8B41B3D36365D26591586F1C4093A49ED425F77A54E47BAB7BB1B72C805F66682484996A06F2DB91827D4814E3EG6F7EAF65E05D3AFC814EB10575CD0176E6003C9803E9FC7C5A74D1A89C77536C53C42E5120
	615D6C219864797B8BD86FDD1638574630DF0C449D2F21B82A2F64984C2B92CFD3DC663D86D3693DE8EF270F3D874F8ECB3F4820E83A213A184581324D25BA9517B644316C4B3F3063C72B09547F683137434D81D6FB8E4A574F3A7A6110EB3774CABFF81665649F43BAAE83C4G4482A4GA4812C87C885A08B8D26A200CA00A600CEGBB40A8C0B4407205380F4263C7A658073868E04E633258287B089C74FF3C285B15B0572605A83F073D285BF79621EE7F19753D82ED9FC07422DB556DD335BA19D505775B
	29D5137BEDD4957BEDG78DB7B10D35CA94F2B2239185A5363D6E4FCD8D815AFF1AD6EB615E6A52A032F67457769FA7819B66B36EAA4BCE36305AC5265FC672BEF2586CADC641F0FFD9B077631F47D99F9564777EB532898F89D76258FFDF86DA22CE90F1BA64B6DB7AE8A756D6D23C94785FF0B6363FC130E538309855FE3B551880BB58531413552B406AF0D8D2E1536E5231D1683ADBA96773BB36776EA3C37EEF3DC67AD0F637BFA1945454D63767BBA43137B0277BBD3465CBC20B96F437DBE2905623F76BE
	D1E741F34BFD21747B478B932DBBFFB164500BC30D5BCA02471921DF2B68FBE5F138FA6AF48D9D27696811703EC434D70498477D758727693B4CB7F8489534D56C4E5266G973658ED436C27C2172023231753DFB4070FA9AF1BA70FA92F1BC3678219B563F3015C1A5039400E9AFDAE40F185B55427FBA186F74AFEFBA56EF309BB406624383F582CDA1F449F2AF9D3601B954AD0FE83E06B72B2DC27409D5361BE97382A9A9A97E14BFD2A8B47565450FB3B9E1B5D4E5B9F30BEB68704C9A90F8636C61B5322DB
	77D9097BDC6A3A5BFBFB3EF46DB647FD731647F1DFBB4BFE0B1E34B858875B987E694541BE78F3F9B626G6D637B712E6BB07D698789FF4E2D21BF7D92F15B63E37723AE0F423FD52F5A350D4959EEF3A80D7CD75D846FB767727FEB81BEAB6FC7FF1FC29FE25F33F46F3B326FFC2DD27E7F4063359C7F1EB2F37D6FF3F26EBC6FC76EA88F2E53DFE8725B617D185FFF64115F8EA56ECB165716AEB1182A16A0EE5BC56C57ACF183E358AFD6624A88FEAFCB10389E02FCD7C95C13C4569A9277A90BD745CBA147DC
	0A38058C57846DCE09FB0E47B5E80FCADCD3AD4E9739946D75D31FDF079D3A77673CF7DE1FE9F714877767C7D85D25510A0A60BD4C9FBEA272FDF7BDFD67B9E00767E66E6F4265146BC126BB09BE8774DC207BD49C7450320A3B78288B1827EFDC0A77B307AECE16FB6A7926AD8D4CBD731699CC73087E3EFF619872A8829A471DA7788D20FC9946C5F303FF6F3FA037F2C7943C773AE57EEF8EFC6FD6E6FFEEF1D94857B0D20379EF73B23C3F7BF64D786FD6C98C77E300CE596F0FB5015F2D3EE8742F7D0B5A49
	561EBB415A7BF4EB975FE268F4E29F8FDD2063FAACF6B7351F70E7483F1F710F797618FE8C47F511407B2D1C337E7E73EB704C616DFF2DE1FD2D58773DE65A177F5F64065D5F0305A67C16E4E9A41F5F3D15B68C1572BBDE0D18B88ADE785D1B5BC5F648DF593630ECE14360495A0CDFBD14E1A419AD444433279612C9D320961245B22496128159134FA924C23319EFD2893A2F119B528FD60A27B1GCB33124CGAC7E4E164E12AB3139BA2CD1A3F9E2AB7417A9353253F65149C8C59EB014E268470CCC0DEC97
	C379EFE25CB09797C6A93D712E85BA3D5FAE15975C9AA9918368EDF6A9DEE717DED43D25CD2A57A15964095551BF1344E68B4C5900BDE6083E1B46AD14E2E7B21970FB5FA3E37A3B653469C5E16F27B744095FBD45796FFD79DD4474A2EAFF158FC0DE7FG5EA72CABBCCA425FE9DC9A30586D1A45FE355EE125779B8DC31A623F931CD5C467F7E39FC194B5FA91DDF73FEE4C7F82D0CB8788466E78F03B94GG9CBCGGD0CB818294G94G88G88G8EF954AC466E78F03B94GG9CBCGG8CGGGGGG
	GGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7594GGGG
**end of data**/
}
}
