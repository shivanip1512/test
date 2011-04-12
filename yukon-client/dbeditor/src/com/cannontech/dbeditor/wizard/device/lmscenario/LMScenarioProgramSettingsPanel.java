package com.cannontech.dbeditor.wizard.device.lmscenario;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.KeyStroke;

import com.cannontech.common.gui.table.MultiJComboCellEditor;
import com.cannontech.common.gui.table.MultiJComboCellRenderer;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TreeFindPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2004 12:15:45 PM)
 * @author: 
 */
public class LMScenarioProgramSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjProgramsScrollPane = null;
	private javax.swing.JTable ivjProgramsTable = null;
	private LMControlScenarioProgramTableModel tableModel = null;
	private javax.swing.JList ivjAvailableList = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameJTextField = null;
	private Vector allGears = null;
	private static OkCancelDialog dialog = null;
	private static final TreeFindPanel FND_PANEL = new TreeFindPanel();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonRemove()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getNameJTextField()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getProgramsTable()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	private javax.swing.JLabel ivjJLabelSearchInstructions = null;
/**
 * LMScenarioProgramSettingsPanel constructor comment.
 */
public LMScenarioProgramSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (ProgramsTable.mouse.mousePressed(java.awt.event.MouseEvent) --> LMScenarioProgramSettingsPanel.programsTable_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programsTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DefaultGearJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (StartDelayJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (DurationJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * Return the AvailableList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getAvailableList() {
	if (ivjAvailableList == null) {
		try {
			ivjAvailableList = new javax.swing.JList();
			ivjAvailableList.setName("AvailableList");
			ivjAvailableList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAvailableList;
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
			ivjJButtonAdd.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonAdd.setText("Assign to Scenario");
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
			ivjJButtonRemove.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonRemove.setText("Remove from Scenario");
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
 * Return the JLabelSearchInstructions property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSearchInstructions() {
	if (ivjJLabelSearchInstructions == null) {
		try {
			ivjJLabelSearchInstructions = new javax.swing.JLabel();
			ivjJLabelSearchInstructions.setName("JLabelSearchInstructions");
			ivjJLabelSearchInstructions.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSearchInstructions.setText("Click the table and press Alt + S to search");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSearchInstructions;
}
/**
 * Return the JScrollPaneAvailable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAvailable() {
	if (ivjJScrollPaneAvailable == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder1.setTitle("Available Programs (must belong to a Control Area to be listed)");
			ivjJScrollPaneAvailable = new javax.swing.JScrollPane();
			ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
			ivjJScrollPaneAvailable.setPreferredSize(new java.awt.Dimension(404, 130));
			ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAvailable.setBorder(ivjLocalBorder1);
			ivjJScrollPaneAvailable.setMinimumSize(new java.awt.Dimension(404, 130));
			getJScrollPaneAvailable().setViewportView(getAvailableList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAvailable;
}
/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjNameJLabel.setText("Scenario Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}
/**
 * Return the NameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameJTextField() {
	if (ivjNameJTextField == null) {
		try {
			ivjNameJTextField = new javax.swing.JTextField();
			ivjNameJTextField.setName("NameJTextField");
			// user code begin {1}
			ivjNameJTextField.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJTextField;
}
/**
 * Return the ProgramsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProgramsScrollPane() {
	if (ivjProgramsScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Assigned Programs");
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setBorder(ivjLocalBorder);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(404, 155));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(404, 155));
			getProgramsScrollPane().setViewportView(getProgramsTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramsScrollPane;
}
/**
 * Return the ProgramsTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getProgramsTable() 
{
	if (ivjProgramsTable == null) 
	{
		try {
			ivjProgramsTable = new javax.swing.JTable();
			ivjProgramsTable.setName("ProgramsTable");
			getProgramsScrollPane().setColumnHeaderView(ivjProgramsTable.getTableHeader());
			
			// user code begin {1}
			ivjProgramsTable.setAutoCreateColumnsFromModel(true);
			ivjProgramsTable.setModel( getTableModel() );
			ivjProgramsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjProgramsTable.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjProgramsTable.setBounds(0, 0, 385, 5000);
			ivjProgramsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjProgramsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			ivjProgramsTable.setGridColor( java.awt.Color.black );
			ivjProgramsTable.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjProgramsTable.setRowHeight(20);
			
			//Do any column specific initialization here, with the exception of the gear column.
			javax.swing.table.TableColumn nameColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.PROGRAMLITEPAO_COLUMN);
			javax.swing.table.TableColumn startOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTOFFSET_COLUMN);
			javax.swing.table.TableColumn stopOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STOPOFFSET_COLUMN);
				
			nameColumn.setPreferredWidth(100);
			startOffsetColumn.setPreferredWidth(60);

			//create our editor for the time fields
			JTextFieldTimeEntry field = new JTextFieldTimeEntry();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);
			startOffsetColumn.setCellEditor( ed );
			stopOffsetColumn.setCellEditor( ed );
			
			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			startOffsetColumn.setCellRenderer(rend);
			stopOffsetColumn.setCellRenderer(rend);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProgramsTable;
}
private LMControlScenarioProgramTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new LMControlScenarioProgramTableModel();
		
	return tableModel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	//make sure cells get saved even though they might be currently being edited
	if( getProgramsTable().isEditing() )
		getProgramsTable().getCellEditor().stopCellEditing();
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	scen.setScenarioName(getNameJTextField().getText());
		
	Vector assignedPrograms = new Vector();
	
	for(int j = 0; j < getProgramsTable().getRowCount(); j++)
	{
		LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();
						
		//program name needs to be converted to id for storage
		LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(j);

		newScenarioProgram.setProgramID(new Integer(thePAO.getLiteID()));
		
		newScenarioProgram.setStartOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStartOffsetAt(j)));
		
		newScenarioProgram.setStopOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStopOffsetAt(j)));
		
		newScenarioProgram.setStartGear(new Integer(((LiteGear)getTableModel().getStartGearAt(j)).getGearNumber()));
		
		assignedPrograms.addElement(newScenarioProgram);
	}
		
	scen.setAllThePrograms(assignedPrograms);
	
	return scen;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	dialog = new OkCancelDialog(
		CtiUtilities.getParentFrame(this),
		"Search",
		true, FND_PANEL );
	
	final AbstractAction searchAction = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( LMScenarioProgramSettingsPanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getTableModel().getRowCount();
						for(int j = 0; j < numberOfRows; j++)
						{
							String programName = ((LiteBase)getTableModel().getValueAt(j, 0)).toString();
							if(programName.compareTo(value.toString()) == 0)
							{
								getProgramsTable().setRowSelectionInterval(j, j);
								getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getProgramsTable().getRowHeight() * (j+1) - getProgramsTable().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(programName.indexOf(value.toString()) > -1 && programName.indexOf(value.toString()) < 2)
							{
								getProgramsTable().setRowSelectionInterval(j, j);
								getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getProgramsTable().getRowHeight() * (j+1) - getProgramsTable().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								LMScenarioProgramSettingsPanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
				dialog.setVisible(false);
			}
		}
	};
	
	final AbstractAction searchActionJList = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( LMScenarioProgramSettingsPanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getAvailableList().getModel().getSize();
						for(int j = 0; j < numberOfRows; j++)
						{
							String programName = ((LiteBase)getAvailableList().getModel().getElementAt(j)).toString();
							if(programName.compareTo(value.toString()) == 0)
							{
								getAvailableList().setSelectedIndex(j);
								getAvailableList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getAvailableList().getHeight() * (j+1) - getAvailableList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(programName.indexOf(value.toString()) > -1 && programName.indexOf(value.toString()) < 2)
							{
								getAvailableList().setSelectedIndex(j);
								getAvailableList().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getAvailableList().getHeight() * (j+1) - getAvailableList().getHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								LMScenarioProgramSettingsPanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
				dialog.setVisible(false);
			}
		}
	};
	
	//do the secret magic key combo: ALT + S
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK, true);
	getProgramsTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getProgramsTable().getActionMap().put("FindAction", searchAction);
	getAvailableList().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getAvailableList().getActionMap().put("FindAction", searchActionJList);
	
	// user code end
	getProgramsTable().addMouseListener(ivjEventHandler);
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getNameJTextField().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMScenarioProgramSettingsPanel");
		setPreferredSize(new java.awt.Dimension(420, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 374);
		setMinimumSize(new java.awt.Dimension(420, 360));
		setMaximumSize(new java.awt.Dimension(420, 360));

		java.awt.GridBagConstraints constraintsProgramsScrollPane = new java.awt.GridBagConstraints();
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 4;
		constraintsProgramsScrollPane.gridwidth = 3;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(1, 8, 1, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 1; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.gridwidth = 2;
		constraintsJButtonAdd.ipadx = 44;
		constraintsJButtonAdd.insets = new java.awt.Insets(2, 12, 0, 7);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.ipadx = 20;
		constraintsJButtonRemove.insets = new java.awt.Insets(2, 7, 0, 24);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(4, 8, 2, 8);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

		java.awt.GridBagConstraints constraintsNameJTextField = new java.awt.GridBagConstraints();
		constraintsNameJTextField.gridx = 2; constraintsNameJTextField.gridy = 1;
		constraintsNameJTextField.gridwidth = 2;
		constraintsNameJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameJTextField.weightx = 1.0;
		constraintsNameJTextField.ipadx = 195;
		constraintsNameJTextField.insets = new java.awt.Insets(7, 3, 4, 98);
		add(getNameJTextField(), constraintsNameJTextField);

		java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
		constraintsNameJLabel.gridx = 1; constraintsNameJLabel.gridy = 1;
		constraintsNameJLabel.ipadx = 9;
		constraintsNameJLabel.insets = new java.awt.Insets(11, 16, 6, 2);
		add(getNameJLabel(), constraintsNameJLabel);

		java.awt.GridBagConstraints constraintsJLabelSearchInstructions = new java.awt.GridBagConstraints();
		constraintsJLabelSearchInstructions.gridx = 1; constraintsJLabelSearchInstructions.gridy = 5;
		constraintsJLabelSearchInstructions.gridwidth = 3;
		constraintsJLabelSearchInstructions.ipadx = 50;
		constraintsJLabelSearchInstructions.ipady = 3;
		constraintsJLabelSearchInstructions.insets = new java.awt.Insets(2, 12, 7, 152);
		add(getJLabelSearchInstructions(), constraintsJLabelSearchInstructions);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	if( getNameJTextField().getText() == null || getNameJTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	Object[] availablePrograms = getAvailableList().getSelectedValues();
	
	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
		
	for(int h = 0; h < availablePrograms.length; h++)
	{
		Integer programID = new Integer(((LiteYukonPAObject)availablePrograms[h]).getLiteID());
		LiteYukonPAObject thePAO = DaoFactory.getPaoDao().getLiteYukonPAO(programID.intValue());
		
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		//do the gears, man
		LiteGear startingGear = null;
		for(int d = 0; d < allGears.size(); d++)
		{
			if( ((LiteGear)allGears.elementAt(d)).getOwnerID() == programID.intValue())
			{
				//add gear to the combobox model for the table
				comboModel.addElement(allGears.elementAt(d));
				
				if( ((LiteGear)allGears.elementAt(d)).getGearNumber() == 1 )
				{
					//it's a newly added program; just take the first gear for the startgear
					startingGear = (LiteGear)allGears.elementAt(d);
					comboModel.setSelectedItem(startingGear);
				}
			}
		}
		//add the new row
		getTableModel().addRowValue( thePAO, "0:00", "0:00", startingGear);
			
		((MultiJComboCellEditor)getProgramsTable().getCellEditor(getTableModel().getRowCount() + 1, 3)).addModel(comboModel);	
			
		//autoscroll to show new additions
		getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
			0,
			getProgramsTable().getRowHeight() * (getProgramsTable().getRowCount() - 4 )- getProgramsTable().getRowHeight(),  //just an estimate that works!!
			100,
			100) );	
		
		//update the available programs list
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(programID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
	
	repaint();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int[] selectedRows = getProgramsTable().getSelectedRows();
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize() + selectedRows.length);
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int u = selectedRows.length - 1; u >= 0; u--)
	{
		LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(selectedRows[u]);
				
		allAvailable.addElement(thePAO);
		//renderer is also automatically updated since it uses the same vector of combo box models
		((MultiJComboCellEditor)getProgramsTable().getCellEditor(u, 3)).removeModel(selectedRows[u]);
		getTableModel().removeRowValue(selectedRows[u]);
	}
	
	getAvailableList().setListData(allAvailable);
	repaint();
		
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMScenarioProgramSettingsPanel aLMScenarioProgramSettingsPanel;
		aLMScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
		frame.setContentPane(aLMScenarioProgramSettingsPanel);
		frame.setSize(aLMScenarioProgramSettingsPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}
public void populateAvailableList()
{
	Vector availablePrograms = new java.util.Vector();
	
	if(allGears == null)
		allGears = new Vector();
	
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List<LiteYukonPAObject> progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		allGears.addAll(cache.getAllGears());
		Vector programsInAControlArea = LMControlAreaProgram.getAllProgramsInControlAreas();
		try
		{
			for (LiteYukonPAObject program: progs) {
				Integer progID = new Integer(program.getLiteID());
				
				for( int j = 0; j < programsInAControlArea.size(); j++)
				{
					if(progID.compareTo((Integer)programsInAControlArea.elementAt(j)) == 0)
					{
						if(DeviceTypesFuncs.isLMProgramDirect(program.getPaoType().getDeviceTypeId() ))
						{
							availablePrograms.addElement(program);
							programsInAControlArea.removeElementAt(j);
						}
					}
				}				
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace(); //something is up
		}
	}
	getAvailableList().setListData(availablePrograms);

}
/**
 * Comment
 */
public void programsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
	fireInputUpdate();
	return;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	
	LMScenario scen = (LMScenario)o;

	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	getNameJTextField().setText(scen.getScenarioName());
	
	populateAvailableList();
	
	Vector assignedPrograms = scen.getAllThePrograms();
	javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
	startGearColumn.setPreferredWidth(100);
	Vector models = new Vector();
	
	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int j = 0; j < assignedPrograms.size(); j++)
	{
		LMControlScenarioProgram lightProgram = (LMControlScenarioProgram)assignedPrograms.elementAt(j);
		Integer progID = lightProgram.getProgramID();
		LiteYukonPAObject thePAO = DaoFactory.getPaoDao().getLiteYukonPAO(progID.intValue());
		LiteGear startingGear = null;
				
		//do the gears, man
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		for( int y = 0; y < allGears.size(); y++ )
		{
			if( ((LiteGear)allGears.elementAt(y)).getOwnerID() == progID.intValue())
			{
				//add gear to the combobox model for the table
				comboModel.addElement(allGears.elementAt(y));
				
				if(((LiteGear)allGears.elementAt(y)).getGearNumber() == lightProgram.getStartGear().intValue() )
				{
					//find the startgear
					startingGear = (LiteGear)allGears.elementAt(y);
					comboModel.setSelectedItem(startingGear);
				}
			}
		}
		models.addElement(comboModel);
		
		//add the new row
		getTableModel().addRowValue( thePAO, JTextFieldTimeEntry.getTimeTextForField(lightProgram.getStartOffset()), JTextFieldTimeEntry.getTimeTextForField(lightProgram.getStopOffset()),
			startingGear);
				
		//make sure that the available programs list is not showing these assigned programs
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(progID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
	
	//set up the combo box renderers and editors for the gear column
	startGearColumn.setCellEditor(new MultiJComboCellEditor(models));
	startGearColumn.setCellRenderer(new MultiJComboCellRenderer(models));
	
		
}

}
