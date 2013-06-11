package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.CheckBoxTableHeader;
import com.cannontech.common.gui.util.CheckBoxTableRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.roles.loadcontrol.DirectLoadcontrolRole;
import com.cannontech.spring.YukonSpringHook;

/**
 * Shows the response from the server for LMProgram constraints
 * 
 */
public class ConstraintResponsePanel extends DataInputPanel implements ActionListener, ItemListener
{
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableConstraints = null;
	
	private ConstraintTableModel constTableModel = null;

/**
 * ConstraintOverridePanel constructor comment.
 */
public ConstraintResponsePanel() {
	super();
	initialize();
}

public void actionPerformed( ActionEvent e ) {
	
	//check boxes inside table cells
	if( e.getSource() instanceof JCheckBox ) {		
		//only tell our table to repaint itself
		getTableModelCons().fireTableDataChanged();
	}
	
}

public void itemStateChanged(ItemEvent e) {

  //check boxe inside the tableHeader
  if( e.getSource() instanceof CheckBoxTableHeader ) {
	  boolean checked = ((CheckBoxTableHeader)e.getSource()).isSelected();
	
	  for( int i = 0; i < getTableModelCons().getRowCount(); i++ ) {
		  ResponseProg prg = getTableModelCons().getRowAt( i );		
		  prg.setOverride( new Boolean(checked) );
	  }
	
	  getTableModelCons().fireTableDataChanged();
  }

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
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneTable().setViewportView(getJTableConstraints());
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

private ConstraintTableModel getTableModelCons()
{
	if( constTableModel == null )
		constTableModel = new ConstraintTableModel();
	
	return constTableModel;
}

/**
 * Return the JTableConstraints property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableConstraints() {
	if (ivjJTableConstraints == null) {
		try {
			ivjJTableConstraints = new javax.swing.JTable();
			ivjJTableConstraints.setName("JTableConstraints");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableConstraints.getTableHeader());
			ivjJTableConstraints.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableConstraints.setModel( getTableModelCons() );
			ivjJTableConstraints.setFont( new Font("Dialog", Font.PLAIN, 12) );
			ivjJTableConstraints.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS );
			ivjJTableConstraints.setShowGrid( false );
			ivjJTableConstraints.setIntercellSpacing( new Dimension(0,0) );
			ivjJTableConstraints.setDefaultRenderer(
				Object.class,
				new MultiLineConstraintRenderer() );

			//ivjJTableConstraints.sizeColumnsToFit( true );
			ivjJTableConstraints.setBackground( Color.BLACK );
			
			ivjJTableConstraints.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);					
			ivjJTableConstraints.setAutoCreateColumnsFromModel( true );
			ivjJTableConstraints.createDefaultColumnsFromModel();

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableConstraints;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */ 
private void initJTableCellComponents() 
{
	// Do any column specific initialization here
    getJTableConstraints().getColumnModel().getColumn(
    		ConstraintTableModel.COL_OVERRIDE).setMinWidth(70);
    getJTableConstraints().getColumnModel().getColumn(
    		ConstraintTableModel.COL_OVERRIDE).setMaxWidth(70);
    getJTableConstraints().getColumnModel().getColumn(
    		ConstraintTableModel.COL_PROGRAM_NAME).setPreferredWidth(150);
    getJTableConstraints().getColumnModel().getColumn(
    		ConstraintTableModel.COL_VIOLATION).setPreferredWidth(530);

    
    TableColumn overColumn = getJTableConstraints().getColumnModel().getColumn(ConstraintTableModel.COL_OVERRIDE);

	// Create and add the column renderers	
	CheckBoxTableRenderer bxRender = new CheckBoxTableRenderer();
	bxRender.setVerticalAlignment(JCheckBox.NORTH);
	bxRender.setHorizontalAlignment(JCheckBox.CENTER);
	bxRender.setBackGroundColor( Color.BLACK );

	overColumn.setCellRenderer(bxRender);

	// Create and add the column CellEditors
	JCheckBox chkBox = new JCheckBox();
	chkBox.setVerticalAlignment(JCheckBox.NORTH);
	chkBox.setHorizontalAlignment(JCheckBox.CENTER);
	chkBox.setBackground( Color.BLACK );
	chkBox.addActionListener( this );
	
	overColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );

	//assign our table header for the Override column
	CheckBoxTableHeader hdrBxRender = new CheckBoxTableHeader( this );

	//hdrBxRender.setVerticalAlignment(JCheckBox.NORTH);
	//hdrBxRender.setHorizontalAlignment(JCheckBox.LEFT);
	hdrBxRender.setText("Ignore Constraints");
	
	//force all check boxes to be checked/unchecked based on the state of the
	// header checkBox
	hdrBxRender.addActionListener( this );

	overColumn.setHeaderRenderer( hdrBxRender );


	//allow the override check box if the user has the roleproperty defined
	boolean canOverride = 
		YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(
			YukonRoleProperty.getForId(DirectLoadcontrolRole.ALLOW_OVERRIDE_CONSTRAINT),
	         ClientSession.getInstance().getUser());

	chkBox.setEnabled( canOverride );
	bxRender.setEnabled( canOverride );
	hdrBxRender.setEnabled( canOverride );
	
	if( !canOverride ) {
		bxRender.setToolTipText(
			"RoleProperty settings prohibit any constraint overrides");
		chkBox.setToolTipText(
			"RoleProperty settings prohibit any constraint overrides");
		hdrBxRender.setToolTipText(
			"RoleProperty settings prohibit any constraint overrides");
	}

}


/**
 * Returns the programs to override
 * 
 * @return java.lang.Object
 * @param obj java.lang.Object
 */
public Object getValue(Object obj)
{
	ArrayList progList = new ArrayList( getTableModelCons().getRowCount() );
	for( int i = 0; i < getTableModelCons().getRowCount(); i++ )
	{
		ResponseProg prg = getTableModelCons().getRowAt( i );
		
		prg.getLmRequest().setConstraintFlag(
			prg.getOverride().booleanValue()
			? ManualControlRequestMessage.CONSTRAINTS_FLAG_OVERRIDE
			: ManualControlRequestMessage.CONSTRAINTS_FLAG_USE );

		progList.add( prg );
	}

	//ResponseProg[] progArr = new ResponseProg[ progList.size() ];
	return (ResponseProg[])progList.toArray( new ResponseProg[progList.size()] );
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception)
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", exception );
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ConstraintOverridePanel");
		setLayout(new java.awt.BorderLayout());
		setSize(414, 231);
		add(getJScrollPaneTable(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
    
	initJTableCellComponents();

	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 1:08:46 PM)
 * @param obj java.lang.Object
 */
public void setValue(Object obj) 
{
	ResponseProg[] respProgs = (ResponseProg[])obj;
	
	for( int i = 0; i < respProgs.length; i++ )
	{
		//we only care about the violators!!!!
		if( respProgs[i].getStatus() != ServerResponseMessage.STATUS_UNINIT )
			getTableModelCons().addRow( respProgs[i] );
	}

	
	//allow the override check box if the user has the roleproperty defined
//	boolean canOverride = 
//		DaoFactory.getAuthDao().checkRoleProperty(
//			ClientSession.getInstance().getUser(),
//			DirectLoadcontrolRole.ALLOW_OVERRIDE_CONSTRAINT);

}

}