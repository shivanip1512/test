package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;

public class NoViolationResponsePanel extends DataInputPanel implements ActionListener {
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableNoConstraints = null;
	
	private NoConstraintViolatedTableModel constTableModel = null;
    private boolean noConstraintsViolated = false;

public NoViolationResponsePanel() {
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

private NoConstraintViolatedTableModel getTableModelCons()
{
	if( constTableModel == null )
		constTableModel = new NoConstraintViolatedTableModel();
	
	return constTableModel;
}

private javax.swing.JTable getJTableConstraints() {
	if (ivjJTableNoConstraints == null) {
		try {
			ivjJTableNoConstraints = new javax.swing.JTable();
			ivjJTableNoConstraints.setName("JTableConstraints");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableNoConstraints.getTableHeader());
			ivjJTableNoConstraints.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableNoConstraints.setModel( getTableModelCons() );
			ivjJTableNoConstraints.setFont( new Font("Dialog", Font.PLAIN, 12) );
			ivjJTableNoConstraints.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS );
			ivjJTableNoConstraints.setShowGrid( false );
			ivjJTableNoConstraints.setIntercellSpacing( new Dimension(0,0) );
			ivjJTableNoConstraints.setDefaultRenderer(
				Object.class,
				new MultiLineConstraintRenderer() );

			//ivjJTableConstraints.sizeColumnsToFit( true );
			ivjJTableNoConstraints.setBackground( Color.BLACK );
			
			ivjJTableNoConstraints.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);					
			ivjJTableNoConstraints.setAutoCreateColumnsFromModel( true );
			ivjJTableNoConstraints.createDefaultColumnsFromModel();

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableNoConstraints;
}

private void initJTableCellComponents() {
	// Do any column specific initialization here
    getJTableConstraints().getColumnModel().getColumn(NoConstraintViolatedTableModel.COL_PROGRAM_NAME).setPreferredWidth(150);
    getJTableConstraints().getColumnModel().getColumn(NoConstraintViolatedTableModel.COL_NOVIOLATION).setPreferredWidth(200);
}

public Object getValue(Object obj) {
	ArrayList progList = new ArrayList( getTableModelCons().getRowCount() );
	for( int i = 0; i < getTableModelCons().getRowCount(); i++ )
	{
		ResponseProg prg = getTableModelCons().getRowAt( i );
		
        /*None violated so just send them like an Observe was set*/
		prg.getLmRequest().setConstraintFlag(ManualControlRequestMessage.CONSTRAINTS_FLAG_USE );

		progList.add( prg );
	}

	//ResponseProg[] progArr = new ResponseProg[ progList.size() ];
	return (ResponseProg[])progList.toArray( new ResponseProg[progList.size()] );
}

private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", exception );
}

private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("NoConstraintsViolatedPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(414, 231);
		add(getJScrollPaneTable(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
    
	initJTableCellComponents();
}

public void setValue(Object obj) {
	ResponseProg[] respProgs = (ResponseProg[])obj;
	
	for( int i = 0; i < respProgs.length; i++ ) {
		getTableModelCons().addRow( respProgs[i] );
	}
}

public boolean isNoConstraintsViolated() {
    return noConstraintsViolated;
}

public void setNoConstraintsViolated(boolean noConstraintsViolated) {
    this.noConstraintsViolated = noConstraintsViolated;
}

}
