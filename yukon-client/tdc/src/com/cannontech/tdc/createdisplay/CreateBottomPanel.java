package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/24/00 4:54:37 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.clientutils.CommonUtils;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.DataBaseInteraction;

public class CreateBottomPanel extends javax.swing.JPanel {
	private Vector columnTypeNumber = null;
	private Vector columnData = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelColumnNumber = null;
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JLabel ivjJLabelType = null;
	private javax.swing.JLabel ivjJLabelWidth = null;
	private javax.swing.JPanel ivjJPanelContentPane = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private javax.swing.JComboBox ivjJComboBoxColumnNumber = null;
	private javax.swing.JTextField ivjJTextFieldTitle = null;
	private javax.swing.JOptionPane warningMsg = null;
	private TableColumnListener columnEventHandler = new TableColumnListener( this );
	private SpinnerValueListener spinnerEventHandler = new SpinnerValueListener( this );
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxType = null;
	private javax.swing.JLabel ivjJLabelCurrentLayout = null;
	public static final int COLUMN_TITLE_WIDTH = 30;
	// constants
	public static final int STARTING_COLUMN_WIDTH = 75;
	private javax.swing.JButton ivjJButtonClear = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.BoxLayout ivjJPanelContentPaneBoxLayout = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == CreateBottomPanel.this.getJComboBoxColumnNumber()) 
				connEtoC1(e);
			if (e.getSource() == CreateBottomPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == CreateBottomPanel.this.getJButtonRemove()) 
				connEtoC3(e);
			if (e.getSource() == CreateBottomPanel.this.getJComboBoxType()) 
				connEtoC5(e);
			if (e.getSource() == CreateBottomPanel.this.getJButtonClear()) 
				connEtoC6(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == CreateBottomPanel.this.getJTextFieldTitle()) 
				connEtoC4(e);
		};
	};
/**
 * BTpn constructor comment.
 */
public CreateBottomPanel() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @param e javax.swing.event.TableColumnModelEvent
 */
public void columnAdded ( javax.swing.event.TableColumnModelEvent e ) 
{

}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 11:11:55 AM)
 * @return int
 */
public int columnCount() 
{
	return columnData.size();
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 11:11:55 AM)
 * @return int
 */
public String columnFieldData( int i, int field ) 
{
	ColumnData column = ((ColumnData)columnData.elementAt(i));

	switch( field )
	{
		case ColumnData.COLUMN_NUMBER:
		return column.getColumnNum().toString();

		case ColumnData.COLUMN_TITLE:
		return column.getColumnTitle();

		case ColumnData.COLUMN_TYPE:
		return column.getColumnType().toString();

		case ColumnData.COLUMN_TYPE_NUMBER:
		return column.getColumnTypeNumber().toString();
		
		case ColumnData.COLUMN_WIDTH:
		return column.getColumnWidth().toString();

		default:
		return "";
			
	}
}
/**
 * This method was created in VisualAge.
 * @param e javax.swing.event.ChangeEvent
 */
public void columnMarginChanged ( javax.swing.event.ChangeEvent e ) {
}
/**
 * connEtoC1:  (JComboBoxColumnNumber.action.actionPerformed(java.awt.event.ActionEvent) --> CreateBottomPanel.jComboBoxColumnNumber_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxColumnNumber_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> CreateBottomPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> CreateBottomPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JTextFieldTitle.caret.caretUpdate(javax.swing.event.CaretEvent) --> CreateBottomPanel.jTextFieldTitle_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldTitle_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxType.action.actionPerformed(java.awt.event.ActionEvent) --> CreateBottomPanel.jComboBoxType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxType_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JButtonClear.action.actionPerformed(java.awt.event.ActionEvent) --> CreateBottomPanel.jButtonClear_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonClear_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @param number int
 */
private void createColumn(String title, int prefWidth )
{	
	javax.swing.table.TableColumn tableColumn = new javax.swing.table.TableColumn();
	tableColumn.setHeaderValue( title );
	tableColumn.setPreferredWidth( prefWidth );
	tableColumn.setWidth(75);
	tableColumn.setMaxWidth(255);
	tableColumn.setMinWidth(30);

	getJComboBoxType().repaint();	
	getScrollPaneTable().addColumn(tableColumn);
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 11:11:55 AM)
 * @return int
 */
public void editInitialize( Long templateNumber )
{
	if ( columnCount() > 1 )
	{
		getJComboBoxColumnNumber().setSelectedIndex( 0 );
		removeAllColumns();
	}

	String query = new String
		("select t.templatenum, t.title, t.typenum, t.ordering, t.width, c.name " + 
		 " from templatecolumns t, columntype c where t.templatenum = ? " +
		 " and t.typenum = c.typenum " +
		 " order by ordering");
	Object[] objs = new Object[1];
	objs[0] = templateNumber;
	Object[][] values = DataBaseInteraction.queryResults( query, objs );

	if ( values.length > 0 )
	{
		getJTextFieldTitle().setText( values[0][1].toString() );
		getJComboBoxType().setSelectedItem( values[0][5] );

		// tell the GUI to add a new column with the above data
		jButtonAdd_ActionPerformed( null );
		getScrollPaneTable().getColumnModel().getColumn( 0 ).setPreferredWidth( new Integer( values[0][4].toString() ).intValue() );
				
		// add in the other rows now
		for( int i = 1; i < values.length; i++ )
		{	
			Integer width = new Integer( values[i][4].toString() );
				
			getJTextFieldTitle().setText( CommonUtils.createString( values[i][1] ) );
			getJComboBoxType().setSelectedItem( values[i][5] );

			// let this event tell the other components to change it values accordingly
			jTextFieldTitle_CaretUpdate( null );
			
			// tell the GUI to add a new column with the above data
			jButtonAdd_ActionPerformed( null );
			getScrollPaneTable().getColumnModel().getColumn( 0 ).setPreferredWidth( width.intValue() );
			
		}
	}

	return;
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
			ivjJButtonAdd.setMnemonic('A');
			ivjJButtonAdd.setText("Add");
			ivjJButtonAdd.setBounds(373, 6, 85, 27);
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
 * Return the JButtonClear property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonClear() {
	if (ivjJButtonClear == null) {
		try {
			ivjJButtonClear = new javax.swing.JButton();
			ivjJButtonClear.setName("JButtonClear");
			ivjJButtonClear.setMnemonic('C');
			ivjJButtonClear.setText("Clear");
			ivjJButtonClear.setBounds(373, 79, 85, 27);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonClear;
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
			ivjJButtonRemove.setMnemonic('R');
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setBounds(373, 41, 85, 27);
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
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColumnNumber() {
	if (ivjJComboBoxColumnNumber == null) {
		try {
			ivjJComboBoxColumnNumber = new javax.swing.JComboBox();
			ivjJComboBoxColumnNumber.setName("JComboBoxColumnNumber");
			ivjJComboBoxColumnNumber.setBackground(java.awt.Color.white);
			ivjJComboBoxColumnNumber.setBounds(133, 6, 130, 21);
			// user code begin {1}
			
			ivjJComboBoxColumnNumber.addItem("1");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColumnNumber;
}
/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxType() {
	if (ivjJComboBoxType == null) {
		try {
			ivjJComboBoxType = new javax.swing.JComboBox();
			ivjJComboBoxType.setName("JComboBoxType");
			ivjJComboBoxType.setBackground(java.awt.Color.white);
			ivjJComboBoxType.setBounds(133, 68, 130, 21);
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxType;
}
/**
 * Return the JLabelColumnNumber property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelColumnNumber() {
	if (ivjJLabelColumnNumber == null) {
		try {
			ivjJLabelColumnNumber = new javax.swing.JLabel();
			ivjJLabelColumnNumber.setName("JLabelColumnNumber");
			ivjJLabelColumnNumber.setText("Column Number");
			ivjJLabelColumnNumber.setBounds(6, 6, 95, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelColumnNumber;
}
/**
 * Return the JLabelSummary property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurrentLayout() {
	if (ivjJLabelCurrentLayout == null) {
		try {
			ivjJLabelCurrentLayout = new javax.swing.JLabel();
			ivjJLabelCurrentLayout.setName("JLabelCurrentLayout");
			ivjJLabelCurrentLayout.setText("Current Layout");
			ivjJLabelCurrentLayout.setBounds(6, 5, 89, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurrentLayout;
}
/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setText("Title");
			ivjJLabelTitle.setBounds(6, 38, 45, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelType() {
	if (ivjJLabelType == null) {
		try {
			ivjJLabelType = new javax.swing.JLabel();
			ivjJLabelType.setName("JLabelType");
			ivjJLabelType.setText("Type");
			ivjJLabelType.setBounds(6, 69, 45, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelType;
}
/**
 * Return the JLabelWidth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWidth() {
	if (ivjJLabelWidth == null) {
		try {
			ivjJLabelWidth = new javax.swing.JLabel();
			ivjJLabelWidth.setName("JLabelWidth");
			ivjJLabelWidth.setText("Column Width");
			ivjJLabelWidth.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelWidth;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(1000, 100));
			ivjJPanel1.setLayout(null);
			ivjJPanel1.setMaximumSize(new java.awt.Dimension(10000, 10000));
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
			getJPanel1().add(getJButtonAdd(), getJButtonAdd().getName());
			getJPanel1().add(getJButtonRemove(), getJButtonRemove().getName());
			getJPanel1().add(getJButtonClear(), getJButtonClear().getName());
			getJPanel1().add(getJComboBoxType(), getJComboBoxType().getName());
			getJPanel1().add(getJTextFieldTitle(), getJTextFieldTitle().getName());
			getJPanel1().add(getJComboBoxColumnNumber(), getJComboBoxColumnNumber().getName());
			getJPanel1().add(getJLabelColumnNumber(), getJLabelColumnNumber().getName());
			getJPanel1().add(getJLabelTitle(), getJLabelTitle().getName());
			getJPanel1().add(getJLabelType(), getJLabelType().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(1000, 100));
			ivjJPanel2.setLayout(null);
			ivjJPanel2.setMaximumSize(new java.awt.Dimension(10000, 10000));
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
			getJPanel2().add(getJScrollPane2(), getJScrollPane2().getName());
			getJPanel2().add(getJLabelCurrentLayout(), getJLabelCurrentLayout().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanelContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelContentPane() {
	if (ivjJPanelContentPane == null) {
		try {
			ivjJPanelContentPane = new javax.swing.JPanel();
			ivjJPanelContentPane.setName("JPanelContentPane");
			ivjJPanelContentPane.setPreferredSize(new java.awt.Dimension(1000, 236));
			ivjJPanelContentPane.setLayout(getJPanelContentPaneBoxLayout());
			ivjJPanelContentPane.setBounds(0, 0, 479, 236);
			ivjJPanelContentPane.setMinimumSize(new java.awt.Dimension(79, 17));
			getJPanelContentPane().add(getJLabelWidth(), getJLabelWidth().getName());
			ivjJPanelContentPane.add(getJPanel1());
			ivjJPanelContentPane.add(getJPanel2());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelContentPane;
}
/**
 * Return the JPanelContentPaneBoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getJPanelContentPaneBoxLayout() {
	javax.swing.BoxLayout ivjJPanelContentPaneBoxLayout = null;
	try {
		/* Create part */
		ivjJPanelContentPaneBoxLayout = new javax.swing.BoxLayout(getJPanelContentPane(), javax.swing.BoxLayout.Y_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelContentPaneBoxLayout;
}
/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane2.setBounds(4, 25, 449, 58);
			ivjJScrollPane2.setVisible(true);
			getJScrollPane2().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTitle() {
	if (ivjJTextFieldTitle == null) {
		try {
			ivjJTextFieldTitle = new javax.swing.JTextField();
			ivjJTextFieldTitle.setName("JTextFieldTitle");
			ivjJTextFieldTitle.setBounds(133, 36, 130, 21);
			ivjJTextFieldTitle.setColumns(0);
			// user code begin {1}
			
			ivjJTextFieldTitle.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument( COLUMN_TITLE_WIDTH ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTitle;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane2().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane2().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setAutoscrolls(false);
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjScrollPaneTable.setColumnSelectionAllowed(true);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(false);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setRowSelectionAllowed(false);
			// user code begin {1}
			
/*			javax.swing.table.TableColumn defaultTableColumn = new javax.swing.table.TableColumn();
			defaultTableColumn.setHeaderValue("DEFAULT");
			defaultTableColumn.setMaxWidth(255);
			defaultTableColumn.setMinWidth(30);
			defaultTableColumn.setWidth(75);
			defaultTableColumn.setPreferredWidth(75);
			
			ivjScrollPaneTable.addColumn( defaultTableColumn );
*/			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION IN CreateBottomPanel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * This method was created in VisualAge.
 */
private void initColumnData() 
{
	// prepare the default values for the next possible column
	getJTextFieldTitle().setText("");
	getJComboBoxType().setSelectedIndex( 0 );	
}
/**
 * This method was created in VisualAge.
 */
public void initColumnType() 
{

	if ( columnTypeNumber == null )
		columnTypeNumber = new Vector( 20 );
			
	String query = new String
		("select  Name, TypeNum " +
		 "from columntype " + 
		 "order by typenum");
	Object[][] values = DataBaseInteraction.queryResults( query, null );


	if ( values.length > 0 )
	{
		
		for( int i = 0; i < values.length; i++ )
		{
			getJComboBoxType().addItem( values[i][0].toString() );
			columnTypeNumber.addElement( values[i][1] );
		}
	}

	getJComboBoxType().repaint();
	getJComboBoxType().revalidate();	
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	
	getScrollPaneTable().getColumnModel().addColumnModelListener( columnEventHandler );
	
	// user code end
	getJComboBoxColumnNumber().addActionListener(ivjEventHandler);
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getJTextFieldTitle().addCaretListener(ivjEventHandler);
	getJComboBoxType().addActionListener(ivjEventHandler);
	getJButtonClear().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		
		if ( columnData == null )
			columnData = new Vector( 25 );

		//storeColumnData();
		
		// user code end
		setName("JPanelCreateBottom");
		setLayout(null);
		setSize(479, 236);
		add(getJPanelContentPane(), getJPanelContentPane().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 1:07:56 PM)
 * @return boolean
 */
public boolean isColumnDataNULL() 
{

	ColumnData column = null;
	
	for ( int i = 0; i < columnData.size(); i++ )
	{
		column = ((ColumnData)columnData.elementAt( i ));
		
		if ( column.getColumnNum() == null ||
			  column.getColumnTitle().equals("") ||
			  column.getColumnType() == null || 
			  column.getColumnTypeNumber() == null ||
			  column.getColumnWidth() == null )
			return true;
	}

	
	return false;
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	
	// make sure all fields are filled in
	if ( getJComboBoxType().getSelectedItem() != null && 
		 (!getJTextFieldTitle().getText().equals("")) )
	{
		// store the newest columns data
		storeColumnData();

		// create the seen column in the table
		createColumn( getJTextFieldTitle().getText(), STARTING_COLUMN_WIDTH );
		
		// add a new number to the ColumnNumber combo box
		int i;
		i = getJComboBoxColumnNumber().getItemCount();
		getJComboBoxColumnNumber().addItem( Integer.toString(++i) );

		// prepare the fields for a possible new column
		getJComboBoxColumnNumber().setSelectedItem( Integer.toString( i ) );
		initColumnData();
		
		getJScrollPane2().revalidate();
		getJScrollPane2().repaint();

		getJComboBoxColumnNumber().repaint();
		getJTextFieldTitle().grabFocus();
	}
	else
		warningMsg.showMessageDialog(this, "All column fields must be completed",
								"Message Box", warningMsg.WARNING_MESSAGE);
	
	return;
}
/**
 * Comment
 */
public void jButtonClear_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
		
	removeAllColumns();

	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( getJComboBoxColumnNumber().getItemCount() >= 1 &&
		 getJComboBoxColumnNumber().getSelectedIndex() != getJComboBoxColumnNumber().getItemCount()-1 )
	{
		columnData.removeElementAt( getJComboBoxColumnNumber().getSelectedIndex() );
		
		getScrollPaneTable().removeColumn(
				getScrollPaneTable().getColumnModel().getColumn( 
				getJComboBoxColumnNumber().getSelectedIndex() ) );
			
		getJComboBoxColumnNumber().removeItemAt( getJComboBoxColumnNumber().getSelectedIndex() );

		// repopulate the combo box and columnData	
		int j = getJComboBoxColumnNumber().getItemCount() - 1;
		getJComboBoxColumnNumber().removeAllItems();		
					
		for (int i = 0; i <= j; i++)
		{
			getJComboBoxColumnNumber().addItem( Integer.toString( i + 1 ) );
			
			if( i != j )
				((ColumnData)columnData.elementAt( i )).setColumnNumber( i + 1 );
		}

		getJComboBoxColumnNumber().repaint();
		getJScrollPane2().repaint();	
	}
	
	return;
}
/**
 * Comment
 */
public void jComboBoxColumnNumber_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( getJComboBoxColumnNumber().getSelectedIndex() >= 0 )
	{
		// be sure the vector element != null
		if( getJComboBoxColumnNumber().getSelectedIndex() >= columnCount() )
		{
			initColumnData();	
			return;
		}
		
		ColumnData columnElement = (ColumnData)columnData.elementAt( 
								getJComboBoxColumnNumber().getSelectedIndex() );
		
		getJTextFieldTitle().setText( columnElement.getColumnTitle() );
		getJComboBoxType().setSelectedItem( columnElement.getColumnType() );
		
		getJComboBoxType().repaint();		
		getJTextFieldTitle().repaint();
	}
	
	return;
}
/**
 * Comment
 */
public void jComboBoxType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if ( getJComboBoxColumnNumber().getSelectedIndex() >= 0 &&
		  getJComboBoxColumnNumber().getSelectedIndex() < columnCount() )  // be sure the vector element != null
	{	
		// change the column type text
		((ColumnData)columnData.elementAt
				( getJComboBoxColumnNumber().getSelectedIndex() )).setColumnType( getJComboBoxType().getSelectedItem() );

		// change the column type number
		((ColumnData)columnData.elementAt
				( getJComboBoxColumnNumber().getSelectedIndex() )).setColumnTypeNumber( 
					columnTypeNumber.elementAt( getJComboBoxType().getSelectedIndex() ) );
	}

				
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 2:38:51 PM)
 * @param e com.klg.jclass.util.value.JCValueEvent
 */
public void jCSpinFieldWidth_ValueChange(com.klg.jclass.util.value.JCValueEvent e) 
{

	if ( getJComboBoxColumnNumber().getSelectedIndex() >= 0 &&
	  getJComboBoxColumnNumber().getSelectedIndex() < columnCount() )  // be sure the vector element != null
	{
		((ColumnData)columnData.elementAt( getJComboBoxColumnNumber().getSelectedIndex() ))
				.setColumnWidth( e.getNewValue() );

		getScrollPaneTable().getColumnModel().getColumn( getJComboBoxColumnNumber().getSelectedIndex() )
			.setWidth( new Integer( e.getNewValue().toString() ).intValue() );

		getScrollPaneTable().getColumnModel().getColumn( getJComboBoxColumnNumber().getSelectedIndex() )
			.setPreferredWidth( new Integer( e.getNewValue().toString() ).intValue() );
			
		getScrollPaneTable().revalidate();
		getScrollPaneTable().repaint();
		getJScrollPane2().revalidate();
		getJScrollPane2().repaint();
		
	}
	
	return;	
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 3:57:05 PM)
 */
public void jTableModel_ColumnAdded( javax.swing.event.TableColumnModelEvent e )
{

	// This is here to force the table to resize all the columns
	// so they take up the whole space of the table.
	// The table does not actually get resized because of the layout
	getScrollPaneTable().setBounds( 1, 1, 1, 1 );

}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 3:57:05 PM)
 */
public void jTableModel_ColumnMoved( javax.swing.event.TableColumnModelEvent e )
{
	swap( columnData, e.getFromIndex(), e.getToIndex());

	ColumnData column = null;
	
	if ( e.getToIndex() == getJComboBoxColumnNumber().getSelectedIndex() )
		column = ((ColumnData)columnData.elementAt( e.getToIndex() ) );
	else if ( e.getFromIndex() == getJComboBoxColumnNumber().getSelectedIndex() )
		column = ((ColumnData)columnData.elementAt( e.getFromIndex() ) );

	// if a moved column affected the current columns displayed fields
	if ( column != null )
	{
		getJTextFieldTitle().setText( column.getColumnTitle() );	
		getJComboBoxType().setSelectedItem( column.getColumnType() );
	}
		
	getJComboBoxType().repaint();	
}
/**
 * Comment
 */
public void jTextFieldTitle_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {

	if ( getJComboBoxColumnNumber().getSelectedIndex() >= 0 &&
		  getJComboBoxColumnNumber().getSelectedIndex() < columnCount() )  // be sure the vector element != null
	{	
		javax.swing.table.TableColumn modifyColumn = 
				getScrollPaneTable().getColumnModel().getColumn( getJComboBoxColumnNumber().getSelectedIndex() );
	
		modifyColumn.setHeaderValue( getJTextFieldTitle().getText() );
	
		((ColumnData)columnData.elementAt
				( getJComboBoxColumnNumber().getSelectedIndex() )).setColumnTitle( getJTextFieldTitle().getText() );

		getJScrollPane2().revalidate();	
		getJScrollPane2().repaint();
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
		CreateBottomPanel aCreateBottomPanel;
		aCreateBottomPanel = new CreateBottomPanel();
		frame.setContentPane(aCreateBottomPanel);
		frame.setSize(aCreateBottomPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeAllColumns() 
{
	int count = columnCount();

	if( getJComboBoxColumnNumber().getItemCount() == 1 )
		getJComboBoxColumnNumber().setSelectedIndex( 0 );
	else
		getJComboBoxColumnNumber().setSelectedIndex( 1 );
		
	for ( int i = 0; i < count; i++ )
	{
		jButtonRemove_ActionPerformed( null );
	}
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param number int
 */
public void setColumnData(Vector columns)
{
	removeAllColumns();

	for( int i = 0; i < columns.size(); i++ )
	{
		ColumnData currentColumn = (ColumnData)columns.elementAt( i );
			
		Integer width = new Integer( currentColumn.getColumnWidth().toString() );				
		getJTextFieldTitle().setText( currentColumn.getColumnTitle() );

		getJComboBoxType().setSelectedItem( currentColumn.getColumnType() );

		// let this event tell the other components to change it values accordingly
		jTextFieldTitle_CaretUpdate( null );
			
		// tell the GUI to add a new column with the above data
		jButtonAdd_ActionPerformed( null );
		getScrollPaneTable().getColumnModel().getColumn( 0 ).setPreferredWidth( width.intValue() );
	}
}
/**
 * This method was created in VisualAge.
 * @param number int
 */
private void storeColumnData()
{
	columnData.addElement( new ColumnData(
				getJComboBoxColumnNumber().getSelectedItem(),
				getJComboBoxType().getSelectedItem(),
				new Integer( STARTING_COLUMN_WIDTH ),
				getJTextFieldTitle().getText(),
				columnTypeNumber.elementAt( getJComboBoxType().getSelectedIndex() ) ) );

	return;
}
/**
 * This method was created in VisualAge.
 * @param source int
 * @param dest int
 */
public void swap( Vector vector, int source, int dest) {

	Object temp = new Object();

	if ( source != dest &&
		 source >= 0 && source < vector.size() &&
		 dest >= 0 && dest < vector.size() )
	{

		temp = vector.elementAt( source );
		vector.setElementAt( vector.elementAt( dest ), source );
		vector.setElementAt( temp, dest );
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D44535A081A966E984AC91C122020D9095A5A895DE0BB1EDE30BAD3E524F3425AD2FAF3E721E2874B52D5172DEAF79597C1010C482C4C8ED14GC1629AFE1498A266E79392DC49120410AA96166C8D593059DDF66F123F855E1919FBE66F5E3D3BC0ACFC5FE1F74E1CB3F3664C19B3E74E4CC6A87ADBD4467A54A2C1D81FA1487F8E2D9784DB27A04CFCF54F7B089BA3466984453FF7G8A85F7
	EEBAFCC6C0DD5162B8DD01A0983320BC9B4A255F0E53ED056F1B04E7A5E7BBFC8952CFB354FDB8660D61507D4C59017DE4527214AD39703991A09AF064B3D9A47D4F5BD2AC631749F8047529021058C3E83E3F25C446D5C15984B095A03A0751AF054FEC5A73786AAA196E741DD1C2728FF7276611F694EDA2141EE73C867933D4F8F47D1A73242FD7291C04478B65D8GB278B4217ED352F60D694279A35D0EFEFD3759EEF758A5313BD7AFD93A755DAE51AC09962B5BE9B38FD5D7B750E23DC3129C7DA633DD34
	757612368FED4D22ED89C2A51413A9EE598652DF8CFC0F87D84971D3F2893E4A8A638568356298BF59DDA30F21EB64D161748B76347CA0E39CE3E5E30B367A46B85573C666DBF4BEDEACA3FACF023A2C3EB8DD89C0AD40D900B1G5B299D2C59719EFC561D139A9C7D7D8EFB1D53D9EF3338BEF7D8C49BFC2F2E069A9957A6F6BBDC96C1E0F37D47AD959A72048246173675C67918CE228F603C4734BDA82CDC70D9EA2606CED6C6D726C676B1DD4C6F73690475DD93346F65FD7EF6695F77A9C15937F102A3ADC3
	236F581F6DC9CD125BC9D174DDFE014CF5AD1D6BE2783EDE368F06BFC671B6851E59683FD11BA1E5AF542D3A0036513591E5B126CF978A7699B5759099D717EA30B199CE5B5432EC48A3EDCF3431BE7338AC5BA8BEC601E7325CCDE5A46565D0D7539F27E376754AAE32E623219C8F3092A09920489E272B84B0585146BEAC7B60BAED4CE4F6C9759E2B4DA23A84467BCA5996789487A51739456E164C76EE31536C321A4F59C437A0533CD6C6F4F04E0FEF9D357DEE686328E897DDE6496A30C36B66FED192DDEE
	C6133133105010058C359D56FE91BA68F78A58EE5E4ECDE4EE4CEE692453820B5DC7416AB76FA4F3F2495C89ADC283701D69E5FBB9511785466F858867FA08206DFDA63A40DBD4D79B9D0E8B9EE78799093052CE745CCEFDC794FCDFE4C79DEF5DC3F0250EB8DD15835B79414EB7956D982C5DC4FC33EB8857D893757FC6875A4CABDDE8B3063CB9C2454F0EE95A8C811B0359CA18C33DF6CC34CDA297ABA7EE688A31E60D8E6D75F4E33E6277A17BC87B9A6DEF64F3B57EBDEA73A0EF8240EA2E6354DD254CE66B
	4DEE912D34558586A6DAFC9AE7F29666954B7AA4B2E54872935DEF60E3A925B402B3CED78EE08298840882C8F022CEE7651E8D514E5799EB20EE1F22FDE9751377CF6C0B69B2E597593F44CB22DDB208CE51EE01AFEE396EAF3BC83F727A6EF1F01E0C5D846F84FF50G8B504D719BF613B5EB16A417751CC7925BC5C9325A4F7B6AB76DA6FD39BD671CBED702FDED5BCD466A98GFF40697F361B2C25EE075DAE52C965ACF27DBBF4AD0BB6319F04EEF0F86C92436FAEA76B6102B8A47B123D65A4C03AE436F908
	DFE27144FCB3512753E56D87D3A12ECD283DC856F95F56E2050E56427C133828886A4AAF621A9D5801EB36EC78BEE169191D29DAFBCE649CC36A440BEC2D4D3868DB33CC2612BD5A323FCBFD0A0CF727D11CDEA4FAB8C6A399E5E8036B64BBFBC9DBCB200F65G2BAF62BA39F78F29C7DFA4AF1086074D46544975FC5BFB85E4DE440B9E91DCB8F1FA1FF15DCDFE6FCDD5DD272CC7D6BF7DBD22C71B42DFA638089EBB73B0E69420DC8CD065E23A9CF4B1DDB03AED9B112E489D272BGB03A99DD38DBC977EBCE37
	8270EB94F4391212EEF2BE52F5G5E8690AEB13A84BF3A1385C8D7600139757864EB76A869328B11AE827051G899EC617F50951B1FB7A439EE2B7B4BE6DB3DFB28F6A5D83E07B5555479471678A3A0790FA53A50CB3056DF43F07F2944022CBE8E34F67E11CB975C214306A01EDC1E36965329C4917FCB6D6BA8E634C2C77C91F8787E04F9FC03B583CD7395755395D0EEEAB5DE665B90D5F2B34C106FBF6EF8EFC76D8DDEE29851C44A04E7FCF7692F949BEAC23653864AA0DD748D83B205F880072C1022F4FA9
	D2481EG7835GC5D047EC7DBF7376939FC28DD44FBECC222B47616A97AD65C433FA730024277ECADFC72B9B4977BDB88F17CE9860338F2219F30EFA47A0D83B275FFE5C53FFCEF41D29D3B5E78C5E5E61BD1DEC9E6E5FD4AB4B3A6C1169C24D55CD29DA717E447C3F26BA87197EA387FD7360FDDC1736669FFF4EE4BAE95808BAC9821A12A154C958F125CEBA0618CE3CC32813399B3F1ECE5E28781868245E83CE43DEE7315C20A2781CE666237C91A0DB82177FBBA6AD7905E1BC3BD51557787AEF937B9D1744
	9B9401753F1877DF84ED9B0731FFEB16327F30E156FF4CB06A6F5D829263F61BDD2224277F331031DC31D065DE9BC82D2CBCA67B458CAAFB876C02CDD651E66930CAB671CC032295E328E6386CD78AD07625A0D78A177D35D3DA6B21F284F5D71DD92334670EA16768F4373F907BB7C05B5E916C7F5BA73566AEF684F5F7F76157333D29A307FDF35FE0934DB73A8C651868678167430491DF9C3BF2A4B026E567A6097A5735A36A335D950A973C6A331DEEBB1E6D8A20CE72E22CBFCDCF7CFBA2141381F2AF4769
	4AG8CG164B68D7CFD59C902EF116EA31439147EE36357B85C1F826F91F748BF10BDCAEFC1F681046AD824606637354FA98F359FF6CB3D4E5B97F934077362A3CD44547666F3C0D4822312F21C54AF1536F3D6A38A9915ADB87D0F0A5CE47763F219C5C7F4A2F60BEF739C9394FAD495F1F3AC1433F92B8FB0549B7660A4F3FD670FCCADDA5592717C25DCA00E400022BF13A03GA6G69AA4E51BD6FFFF22DB922165761E8GDCB344B58D6A78136B34E563686662D4G1FFAAE26DE551A0B7C76373467022DAE
	28E3FADEFCBCCB6EAB66AA1EDF7272C7A76B76GBE352CC9AAD9198CFFB4218C6B388CCF0FD2061FDCD30603E3347435333CC4DBDF5C1BB3D9233250AE0DE3D05630025149BAB900CFADEB5898ADFD693BD00699DC062A4251495094402716E105268CEBCF621C2D01FA1677D8CBE3563231909F0FC55CF3EF915CE9A8CBE3D156616D644C95494BE134CCFCGAB67EE8F15AFC0A07C5722518256474E6B6AE334398966BF2274414F4B5F52EB1F179F56EB1F1797688B999E5AD0E65730FE31BE74F97A993D56F9
	7A2FF40C4FEE4927ED3B5D221BBAB403D9B24BF53F13B68B9A676A6366FED1564FA6E1C9DE36CC53A8B99A7641F7666797EED77BF912F7DA847336ECAC7A79BD1709FD907E2431F84E1976B959D7CBEE811F8CE03C856D62CACE303C2F85E886EF893487666F93761138A59C6A23811682A482245C02FE7E6F9FDCF3AFEE9C8C3197FFD699FC1E5BAA3567392B121C515C8E0F2BDBD44F65194AE2DE675BD1DAA4311F281D5174D2FE496CBAAF8658020372333AE07C0315416C054D69180D4AB95D4F574D52636A
	B9A89E87E771F1CC77356382670035E748D736F700377706C95DDE873451AB37638D5ADE0ADFFB87F9FBF332C25DCBC4285A1BBD8EE35745854A76BE646D9D688A565EA260DD96D4BEC721323D0FF8FBFFBC29EEEF953411AC371395962C3D4CE212173219079C9E89767CB032CE5EAE4675D5826503G3DG5EB0364E583E7658G0911FADC66FE185EF62758ADB847935E243710F7A5502FA56D813E9240B49EF572E28F3191B917C4EE50086B3113F54F566E4B5BF16DAE9A0FEB775EA19AC3C3B9EBC21C2EF4
	822EDD7DE59CF75584B6C663845FD84F42F7E9027F58D96CFD60B84DF9CCE03177A60EEF2478C58ABC0B49E7D25FC14A2B21AE6CD65C076E2271E469C4581BA6A2AE298E77A663C474854B7785DF4BAF6C53DE4B3F5A276D337FE3DFE81F7D523EE0EB104977820D4D1CA0DB981739341560A2219C4DF1E3DBF19C319CD7F882F993B96E648964DD43F1CF1CC05E940EDB12053812EFA06EF75984D78B658E0EBB5605BA6D65380ACC6475F2DCEEA73D8F01F2AC471D243884A8A7F25CD31D481B84B8B61FCD45F8
	471BB5A9CED7BB896D7A7C3BBB85459D2F75DC3F5EF329DFEFBE87A7C30BD52A2EBE6E31590C72F2116D757662AC392D41C9E82FCBE87B73211CG30EA921EB586F761D9A3664169906BFD2619D3DB5531BFB5E592335B125BFCE70DE8B8EB30BEF336E11FDDD09FF19B41D5EDD31E2349DA7BB9906809FF2275FDFE7544C660082C281F61D7CF647DEE1A327E47FE75998AFEB6369F6D44312D7BD134E0DB78F150FCE122BC26F5722729F307D0B6694F19ECCD7F4FF6DC538743D9FDF338EF8D1F066F4EF02DB5
	7D66B051F9D8B8DB3B199C7FB6454FD060198D4C26BE0514E3212E78DF50DE9AE96C6005F2A4472D22769C8365259C372B1D6012203C0E63AE6493DC416DF13A725B51C7FDFC3C0C589414BBG1CG9381E683AC85C882C83F8356449DC87F99252F10F34C51F2EE186411C98E7BA050F5GC81C7E014FA3E4BD10B6347614F499BFD16ECF0B26C646F7481FCB65CFE790FA3E578529E7327D104B36A988FF21425E720350E03C1CA1FF66A97A88A5DB30FBB1368F193BC8BE791C397B42F91743E33740186F2039
	C46A53274277A8G362F7D398E77357488188700AA00AE0041885C57DE21FE355FAEC230E76DE6EDE5D09FBA8EA01253BDBDCC4EA592B87C688872EE46C8D223727E99590A7D2CA1F5F742DE87D08BF0960035D9CDEDF18E40984031B69C553EB61EBB01EDC4C07DEC00F800A4007449B81E1FE6A1CD99602AG3AG8681A2A7231C7F17CD745BCD5357EED9B71BF3506FAC1A0C728CD1FF1DG65953C7D06CCECBF99F0D9D34006811A81FA27E07BBBBA8375D5CDF13792DB65F436CEFFFDCDBE6C9B6B731D58CF
	A45047GAC83D88310BB95570731F8F4EFD13E98A55F149B66E3FE6A763708BF2A184A7C54FA0E1FC671D58ABC73DFF1FC8FB3C0DD74D44C93FAEA08CEF26F821D835482F4818C82CC3D8B6311707D1542B54EA69D8E279182961BA244E0634B5FB63AB7BACFE7627E907B547D90E33D3EBED81EAD7AAE797ECB7EDC5D310362657B24A3E4FCA9D0D774CD30DDG930085407BCD9C5F0A7D95ECFC0D30EB57C912393B175EA46160BA2D62008FCD82D8663F33CB5077C7FC93FD7A534374FD9C1497F15C17DE0CA9
	D6F21C77B27AFEE19A5ADEB15DFB0A20DC81D04B71E5945FB10D653AA50E1FBB047983422356CB0582D72BD1FFFD76798C6FC3AB8F1121223F51BE581AADADA1BE5A6992FBC417CB3434DB07C9FC4F56381D6AA992641FBD8D5778EBA7824FA19B5BF0ED2F18464EA154AF4ABAE3FC1F34939B6AB78FDA7BBD7D34975647C93AB727C74277C8241DF21952DA6DAA5A29F44F2E85BAA327DDBBCC7606E37C3E8A47755BE3F9BEBC3BC762E3FEF1A457D7C76F9964B32F1372A86FCFB8CF4CC7E44FBB66FF37A35795
	6D592C64D35EA3F27EE752693D3C51FC0E1C2514842CBE433E0157AB5B1E32B547072782313B18F79A14B15EE0A453750CC83C2BF933D12BFEBE2FCFBB2AD59F9F097B54AFEB85AE7B4ABAC24BEFDEC0FC07CBBF50EB15C466AF228614EFB92BF28EE93EF1A01098A95B067C3964569DF2EC7ECEBD66B44B6E46B8796F756AB8F92F200CF3EF2BD74749F9FE757FDA2F0E13B37C626CD76B55F17286391E0D3B2415FC6E2E5BAD7B451FFC299CE74174F7D2514F2B47F97AEEB63E5E3BFDE782B64E6F34725CAD9F
	671FDA5563BC64B70EF2D368F1FEE1D20FF31EDFFD38C9BD4E17D21563BCF1020E33ED37BC97FFB801B22E60B26EED8BAD63D7EDEA995B7D6A6FE88BAD632DA7034948E4FA1D675DF32723CC7B32C35BC7F736DA26963F7A3B32C35B478F32C35BC7C58E19FB32BF33B573DC3D406B4AB2032D99B69EEBA60E474047F3DFE668717CB4D3BD1EEF7855E7E4059E4F212C5063B951C96D20816D6035CE14514BE57C60D4E8BBB01FD24BB84B2FFE4A2950F6703DD3416C006D09D3C9E8A1FF1F29781ED0FCBD797C51
	826B635FEFEA9FAC9E3AD293BC7FB43EC6BB7FF4DB0DF67E694E1A50F782F75784FFE397D523FCE3476239E83A674402ADAC87C886483A874E4377E0AC277F64DD0146F29AF1DC2365BC49E513173F9276DFF8A478D80B0FDC7B3D70BBC76839103EEFA36DA3FE7791B2DF1243C9118CD7F1046848E5BD5FABB1D2993F7F8831F11B58C351AC4E78184616D5B0AE43BD98E7A48E8746BD2F8EE35CA381DDB8C0ACC042BD6CBE1B1DFBDC7C2EFB956013810AE6C00C8450B1834FE573E81CA5C1F94C8C6CEF6C073E
	F8601455A2754AEBF471B0999B4B496E13F15B5DBE5CFEB9BEB8B52C5E5F09CCB150BE3B1BC9EE563A03DF8A75ABE6E08CF022112CAF56470A936437A2F6F120147BB40395E618BC18AE5FD7D9D9D9D18A7F13BC93BB73FC5406E71EB550DE59CC4C3739A97EB414A5007019581754646B6BCCFED9683ED8E7509D631DF65447BB7CF9E9C8DEA6267C8E2FA748BBBC2AF566833B9A50874E1E09F7D6AD0D08DB49F155C791D7F4AF1E6F8C4D84E704F26F3D38C6CA6B08CF6FB47749A7B9F26CF13868AA81244965
	F00AAEE908E1696BC959776C2AD71E65A6C2FBB36E8DFD2EE0B2FCCA36BC5956FB6AF6866B9BD668B1D0C16B39BE31DB6AB01F67BE6F19D6E53F73214F78EB744B78D2CEA8799201E74DF5717D1F5F7D0DB08B564B2C6B6133E7AA794A0127663A7856F5AA79CC40E339869F53657C06603E6B4906EB7B2E25542EC470B9A42AE7386FB5909F636869F10B5CC77E30210063DA5CA6075BCA6E72185D4FBA66337B054357DC1F7B8FFC64DB1FDBC7F0FD8E42F8674F423CFADB33769B9EB8574859F4E66FE7C67077
	D12BE7615A0E26ED16C641D99F409405EBA3520B39285EA80CA33E6F8D1D83CF712A6388426F2BFF6FF2689C78694B6AB8827965D86DB31F6E468E3D9DDCF707BE8617F1E88F3FC39846F78FD1FCFC95ADF88F1698053A5812CAF4D1F09F1C37GEA6F433CC1CD5B68F2D547EE180F6524FEC77DED57FD6ADC55DA0A375C274ED58DF13F6D043A95DC6E2F9A347A2F3ADF3E4B3BBFF04DB03E630DDAFCE7E5FAA9A85F27C73578A64A74B3B47858FAE9E9A65FF94C43DC28DCE7EAA6B6E1354392B25B2C437499F3
	BB905A4F333C50A7748CBA9F5AA6F1B34355D3DC8260085FE638DF34A25DBC0E7BCDAB529D62387C36C03A2236C03A4D59017DEE4B8E6CF7FCE6A05D244CC03ACD27827BADBE256C1769782FA32333453D236433F03EA40D798BEE43A5DEE213A9772BEDF8A74527BF203661C738EFAB00BA69813461195ED16609073578929F10F39970A94C56FADB352F85790A647AF285DD8DFCB729785838A6D15FE1194D46154171F7D17C20824F46DB120A6FF54321EE6DEC9C6F4BED821765CF0A6FD75BEE7ED9A9722676
	41ECDAEA407B6C0EB9185B7DD98366FB4FF21C5808F4E19C777DC6240B60384E23C8974BF1CF9EC53A25F3F00FB46FA27A167D888D4E993EE4B04F07EF9649D9034BBAC2774B24B9AC3EE7BAFA576B1B133D0A6F31A3B7FFFE1E3B29FD30B14F9DC6FDA6F3FDFAF8BEDEF8103F4D2038B2A8D7F1DCF38B66770D9C37EA846FF64FF25CBB1771EEB70C63D6D4E3FEBF72C1DC8B912914974A719CF78F45A5C1B96BDB08FBB7D52997C2BF1A71478761E3FD9C766BE334EF8CB30260D9FCF370F3E2CF6C8C3A26814F
	FA65B04E9A0053GDE00995F4273572963BE7ADFB5A27D9228DF8E309620A0BACED7990D74432DBE7A9F9FC5FA9354DBG42GE68344C763F9F67288399BB48FDA5D0CBF5AC4781322E9FE526FBD389A647C3B17686E1255EDA53F9BE36766E7F8DCD971901E25AFD23B29053291001D5333F97B5E07FC7793D39FE277B90C6F9835239840AD62ED7DAF352355D0CEFE8847F1EF9A3DBBE86FF6B9ECB6F2625E4F70915B493D391C5F773848139FB9DEE6676D67D215E3AC1D4B7AB64267593918137FE5367A4CCE
	E88667E21C7926814F1861F3B10EBEE6D04751BB84E59C6CB52863E87F5C71A30600B85A2FFE15C19DC7A3BF13A92D89E50A67B29DEE8AADD3FF13DA264F7D6AE7B529E57A522F7E792650B2A5733378BAAED3C5F3E8197E512C1669B63F7A086650B245B58713095D9DA4FC25FCF3D33AB7DD734DCD4AD79F249E1C476C33EB1E6F6EE0C55F3ACCE6E73F701236FB4CB6B749E13135D4C077F6B9CFB3F2E3F91AFDDE0C1542213F78F9F8FE31D37CBAA897454469AAE2F05D9FE8777515D0FDE3F91ACFCF7AF8DF
	DFDF9A12D71927B95FC85EA2EB64E9FC2B104FFDC69A1E458CB1B816AFB248D83CD01E8A90934357937F0DC9FC8C7A7F2D86E52C378E70858FDFCF9EA12BC949D786BCD55745773A5FB94788BCE72F414772BAEF5177949250C6G4482AC83D8851072B07F1BA026F2A1F82E0939A71A13F5192DF60949F3E814B1F2638D7331187637544EF29FD14776AFD1FC71A36A58DE6276DA86F5939F41F94BC9437E57GCEF854DF07C5D0AEFFD4AB567EB1350D1AC7553F79CA24F863236A5FFC1D4D405FFC1D063A250F
	F23B51FC1BDEB61F75D9B5BFF0CED92C22B760D96D3B9CF74000E7359D9C7737A6247B12634A1A82698A1A8369B6B7AB69645F0EF7864FDF6D693CF67EEAFF27F66E3D2AB3B0777EC927F66E3D21D319FBE7FADC2D0803FF2F78BE6C3D79B171141B5A079C2FD27FE484FBF04E47F8F5086242209C43F1AB52783903637E12CE63D5A8274CC73BDB11C1622A5C47307CEBDAAE67651E8C6518885DBFDFEFD7B37E9971ECE83D69F370ACE1FC8C75F1C78E6A286BB10C4F2A52485F453AA43AA4EB375946B608FA33
	4B6430D93B076438311D6A1664CB3C1CAFA20D7855DE074BBA6C30CB011C78A6A60672125F85AEFA8C6FF8DECA4358EE9D608A9E07B899408460FC9C5B5F1291983F3E1F01B95488CEF77A8CD9173E0D0D7D1D8496C3666B14B1E494701033CBBCFCA6BE0E7B736327481AE1F2BD180374EB21BEFDC11C2E8C40GE0D9007D7525117BEB654F12953B2A3E33312D2325214EF8263D212D55E82C2FEBBBF33C9110721B16340FC270B6373635FC51FA3C430F3B2E9D9AE8B4B49AF03FAECC47BDB8EC8146EAAD696A
	D84DA3A8E32D11F4F52CF6402F7E61F4F52CB64EAFB77C1BF4F52C16ADA86F418DF48DC9AE0F48620F3E11A077C4F4F258B80E6A48B8C8CC37E481469D6559B49F9E8B31G00A996630EE5BC6E680D45B127EF889D1FFE34C1BD668DFE753D9B54E3DE60D7BFFDC33078142D61AD694A7DAB8A640A0D3D1E784528A3EBBF81E8D74A74EBC37025AF8CDED78CF5958B35766899D40F358B55B1429C0AB7AED447887AEC0C913A20EE49C274054F7B6D5125CF40D9700950E3E4FE667DF45CD3099DB15C7EF45CD3BD
	1C4ECEED20995ABC7B847A28EFD31C8465880E4B25B95B59D0CE60383FD35CCAA827BF0938E87EDB0AE20EFB163E0329017269A7F1DC7753A3867BBECF717DE0764D5FB3BF3F29FD30B12F4EC17DAF603817F314B18DDB3763F5E4CD18BD1243CD5D903E051E2943BC0173C974FB4B693C935AB651ED9D96490BDA74717F456943B87D0ECC929331B7566D227CD7F16AECB64700485F239F49C4FF9E4B791C195B64FE58DF63E1EF7F5CCDAEC7BFFD440B3C611958E77C13382FFC173F4DCE845C9A005CC530C6G
	0C0B307DD5D9A46F60F28C8488455AFDA38B5B35F11ED81D728C49F0C2A6F93B61674B29F753571D6CE8BD5356585E72C563195626A624BF1909BE12159F48D436497C5C0BFCCCE396211F5B142576F3F77A5D7BB5E6053E779B88F863644F7FD0409BA764E7F296E6AB65E4B67C5F19B7FAF6E1FFFBF3D14E0B50F84C4BC206A42455D9AC1220E3AFCDA5A103BEABF53B040D7ECF40564F59BA2E600E831ED4B8B08ABA904359EBEE91B2685FEDB08A9B4E51A0D9F4812DA49450BFF6718170FEC0F98F9B0554EE
	37A4EC77613B891E5536BB4D5DA210E4DB4C2E8B34050B4295A17DCBA13B47F57E9C6DC092D23FEC7A923A25A5D22DB38AB9D22FC8FF5CB7A8C942D6DF53A6F2A614AB8E8F12BF26A77C857EB969BFB7E1CED361DC421B01E7793A5BFF7EC24C99C3D12A105AA268FAE9D29C86BCC0326634D572E2F7B5ED8F8E943B8B88E009548996DEDA2324242651AA89F9ECC11532573420F87AF8D6925249353AA4644A55B4CB2FECB7F41F1230D1CE6215725F1031BC4E1E2B4AB720A9630B033EF56DDA4C76965BE2DF9E
	071D5D7B72483A0886FC7CE2BC2BBF15CB7E98EC3037996D7DE61B8D22528BF5F68BF927D1BFA4093EF7DC0EBC523E9BE964CAD6F7097EB63BCF41737FD0CB8788F82EE5D27B9CGG14D6GGD0CB818294G94G88G88GF5F954ACF82EE5D27B9CGG14D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB59DGGGG
**end of data**/
}
}
