package com.cannontech.tdc.addpoints;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 4:54:40 PM)
 * @author: 
 */
import java.awt.Cursor;

import javax.swing.ListSelectionModel;

import com.cannontech.common.gui.tree.TreeFindDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class AddPointsCenterPanel extends javax.swing.JPanel implements javax.swing.event.TableModelListener, com.cannontech.common.gui.dnd.DragAndDropListener
{
	private TreeFindDialog fndDialog = null;
	
	private long currentDisplayNumber = com.cannontech.tdc.data.Display.UNKNOWN_DISPLAY_NUMBER;
	private java.util.Vector selectedPoints = null;
	private javax.swing.JScrollPane ivjJScrollPaneLeftTree = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	// Constants
	public final int DEVICE_NAME = 2;
	public final int POINT_NAME_AND_ID = 3;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private AddPointsLeftTree ivjLeftTree = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjJLabelSelected = null;
	private com.cannontech.common.gui.dnd.DragAndDropTable ivjRightTable = null;
	private javax.swing.JScrollPane ivjJScrollPaneRightTable = null;
	private javax.swing.JPanel ivjJPanelRight = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddPointsCenterPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == AddPointsCenterPanel.this.getJButtonRemove()) 
				connEtoC1(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddPointsCenterPanel.this.getRightTable()) 
				connEtoC3(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
/**
 * AddPointsCenterPanel constructor comment.
 */
public AddPointsCenterPanel() {
	super();
	initialize();
}

public void drop_actionPerformed(java.util.EventObject newEvent)
{
	//just act like a user pressed the add button
	//fireAddButtonAction_actionPerformed( new java.util.EventObject(this) );
	
	System.out.println("   drop_actionPerformed() occured int CenterPanel");
}


/**
 * connEtoC1:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> AddPointsCenterPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> AddPointsCenterPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (RightTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddPointsCenterPanel.rightTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.rightTable_MouseClicked(arg1);
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
			ivjJButtonAdd.setMnemonic('A');
			ivjJButtonAdd.setText("Add >>");
			ivjJButtonAdd.setBounds(21, 76, 85, 27);
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
			ivjJButtonRemove.setMnemonic('R');
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setBounds(21, 111, 85, 27);
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
 * Return the JLabelSelected property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSelected() {
	if (ivjJLabelSelected == null) {
		try {
			ivjJLabelSelected = new javax.swing.JLabel();
			ivjJLabelSelected.setName("JLabelSelected");
			ivjJLabelSelected.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjJLabelSelected.setText("Selected");
			ivjJLabelSelected.setMaximumSize(new java.awt.Dimension(148, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSelected;
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
			ivjJPanel2.setLayout(null);
			getJPanel2().add(getJButtonRemove(), getJButtonRemove().getName());
			getJPanel2().add(getJButtonAdd(), getJButtonAdd().getName());
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
 * Return the JPanelRight property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelRight() {
	if (ivjJPanelRight == null) {
		try {
			ivjJPanelRight = new javax.swing.JPanel();
			ivjJPanelRight.setName("JPanelRight");
			ivjJPanelRight.setPreferredSize(new java.awt.Dimension(246, 256));
			ivjJPanelRight.setLayout(new java.awt.GridBagLayout());
			ivjJPanelRight.setMinimumSize(new java.awt.Dimension(240, 240));

			java.awt.GridBagConstraints constraintsJLabelSelected = new java.awt.GridBagConstraints();
			constraintsJLabelSelected.gridx = 1; constraintsJLabelSelected.gridy = 1;
			constraintsJLabelSelected.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJLabelSelected.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJLabelSelected.weightx = 0.0;
			constraintsJLabelSelected.weighty = 0.0;
			constraintsJLabelSelected.ipadx = 133;
			constraintsJLabelSelected.insets = new java.awt.Insets(1, 1, 1, 50);
			getJPanelRight().add(getJLabelSelected(), constraintsJLabelSelected);

			java.awt.GridBagConstraints constraintsJScrollPaneRightTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneRightTable.gridx = 1; constraintsJScrollPaneRightTable.gridy = 2;
			constraintsJScrollPaneRightTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneRightTable.weightx = 1.0;
			constraintsJScrollPaneRightTable.weighty = 1.0;
			constraintsJScrollPaneRightTable.ipadx = 223;
			constraintsJScrollPaneRightTable.ipady = 209;
			constraintsJScrollPaneRightTable.insets = new java.awt.Insets(1, 1, 2, 0);
			getJPanelRight().add(getJScrollPaneRightTable(), constraintsJScrollPaneRightTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelRight;
}
/**
 * Return the JScrollPaneLeftTree property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneLeftTree() {
	if (ivjJScrollPaneLeftTree == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitlePosition(1);
			ivjLocalBorder.setTitleJustification(1);
			ivjLocalBorder.setTitle("Available");
			ivjJScrollPaneLeftTree = new javax.swing.JScrollPane();
			ivjJScrollPaneLeftTree.setName("JScrollPaneLeftTree");
			ivjJScrollPaneLeftTree.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneLeftTree.setBorder(ivjLocalBorder);
			ivjJScrollPaneLeftTree.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneLeftTree.setPreferredSize(new java.awt.Dimension(240, 240));
			ivjJScrollPaneLeftTree.setMinimumSize(new java.awt.Dimension(240, 240));
			getJScrollPaneLeftTree().setViewportView(getLeftTree());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneLeftTree;
}
/**
 * Return the JScrollPaneRightTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneRightTable() {
	if (ivjJScrollPaneRightTable == null) {
		try {
			ivjJScrollPaneRightTable = new javax.swing.JScrollPane();
			ivjJScrollPaneRightTable.setName("JScrollPaneRightTable");
			ivjJScrollPaneRightTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneRightTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneRightTable.setPreferredSize(new java.awt.Dimension(240, 240));
			getJScrollPaneRightTable().setViewportView(getRightTable());
			// user code begin {1}

			getRightTable().setViewDropTarget( ivjJScrollPaneRightTable.getViewport() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneRightTable;
}
/**
 * Return the PointsLeftTree property value.
 * @return com.cannontech.tdc.addpoints.AddPointsTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private AddPointsLeftTree getLeftTree() {
	if (ivjLeftTree == null) {
		try {
			ivjLeftTree = new com.cannontech.tdc.addpoints.AddPointsLeftTree();
			ivjLeftTree.setName("LeftTree");
			ivjLeftTree.setBounds(0, 0, 78, 72);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftTree;
}
/**
 * Return the RightTable property value.
 * @return com.cannontech.common.gui.dnd.DragAndDropTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.dnd.DragAndDropTable getRightTable() {
	if (ivjRightTable == null) {
		try {
			ivjRightTable = new com.cannontech.common.gui.dnd.DragAndDropTable();
			ivjRightTable.setName("RightTable");
			getJScrollPaneRightTable().setColumnHeaderView(ivjRightTable.getTableHeader());
			ivjRightTable.setToolTipText("Dbl Click to create seperators");
			ivjRightTable.setBounds(0, 0, 450, 400);
			// user code begin {1}

			ivjRightTable.setToolTipText("Dbl-Click to create seperators, click-and-drag to reorder");
			
			ivjRightTable.setModel( new RightTableModel() );
			ivjRightTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			//ivjRightTable.setShowVerticalLines( false );
			ivjRightTable.setShowHorizontalLines( false );
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightTable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2002 2:51:06 PM)
 * @return com.cannontech.tdc.addpoints.RightTableModel
 */
private RightTableModel getRightTableModel() 
{
	return (RightTableModel)getRightTable().getModel();
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 2:29:57 PM)
 */
public int getRightTablePointCount() 
{
	return getRightTable().getRowCount();
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 2:29:57 PM)
 */
public long getRightTablePointID( int i )
{
	return ((RightTableModel)getRightTable().getModel()).getPointID( i );
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION AddPointsCenterPanel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	fndDialog = new TreeFindDialog(
			CtiUtilities.getParentFrame(this), getLeftTree() );

	getRightTableModel().addTableModelListener(this);

	getRightTable().addDragAndDropListener(this);

	// user code end
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getRightTable().addMouseListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		if( selectedPoints == null )
			selectedPoints = new java.util.Vector( 25 );
			
		// user code end
		setName("AddPointsCenterPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(639, 283);

		java.awt.GridBagConstraints constraintsJScrollPaneLeftTree = new java.awt.GridBagConstraints();
		constraintsJScrollPaneLeftTree.gridx = 1; constraintsJScrollPaneLeftTree.gridy = 1;
		constraintsJScrollPaneLeftTree.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneLeftTree.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneLeftTree.weightx = 1.0;
		constraintsJScrollPaneLeftTree.weighty = 1.0;
		constraintsJScrollPaneLeftTree.insets = new java.awt.Insets(10, 8, 16, 1);
		add(getJScrollPaneLeftTree(), constraintsJScrollPaneLeftTree);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 2; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanel2.weightx = 0.0;
		constraintsJPanel2.weighty = 0.0;
		constraintsJPanel2.ipadx = 118;
		constraintsJPanel2.ipady = 241;
		constraintsJPanel2.insets = new java.awt.Insets(26, 2, 16, 2);
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsJPanelRight = new java.awt.GridBagConstraints();
		constraintsJPanelRight.gridx = 3; constraintsJPanelRight.gridy = 1;
		constraintsJPanelRight.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelRight.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJPanelRight.weightx = 1.0;
		constraintsJPanelRight.weighty = 1.0;
		constraintsJPanelRight.insets = new java.awt.Insets(11, 3, 16, 15);
		add(getJPanelRight(), constraintsJPanelRight);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getRightTableModel().exceededMaxRows() )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, "Unable to add the selected point since displays can only have " 
				+ com.cannontech.tdc.utils.TDCDefines.MAX_ROWS + " points." );

		return;
	}

	javax.swing.tree.TreePath[] path = getLeftTree().getSelectionPaths();

	if( path != null && path[0].getPath().length != 1 )
	{
		Cursor original = getCursor();
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame( this );
		
		try
		{
			f.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );

			for( int i = 0; i < path.length; i++ )
			{
				if( path[i].getLastPathComponent() instanceof DBTreeNode )
				{
					Object userObject = 
							((DBTreeNode)path[i].getLastPathComponent()).getUserObject();

					// handle the selected device
					if( userObject instanceof LiteYukonPAObject )
					{
						getRightTable().addDevice(
							PAOFuncs.getLitePointsForPAObject( 
									((LiteYukonPAObject)userObject).getYukonID()) );
					} 
					else if( userObject instanceof LitePoint )
					{
						LitePoint litePt = (LitePoint)userObject;
						
						getRightTable().addPoint( litePt );
					}
				}
			}
		}
		finally
		{
			f.setCursor( original );
		}
	}


	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getRightTable().getSelectedRow() >= 0 && 
		 getRightTable().getSelectedRow() < getRightTable().getRowCount() )
	{
		Cursor original = getCursor();
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame( this );

		try
		{
			RightTableModel tableModel = ((RightTableModel)getRightTable().getModel());
			
			// set the selected index to the point right above the one(s) removed
			int smallestLocation = getRightTable().getSelectedRow();
				
			for( int i = (getRightTable().getSelectedRows().length - 1); i >= 0; i-- )
				tableModel.removeRow( getRightTable().getSelectedRows()[i] );

			
			if( getRightTable().getSelectedRows().length > 0 )
				smallestLocation = getRightTable().getSelectedRows()[0];
			
			if( smallestLocation > 0 )
				getRightTable().setRowSelectionInterval( smallestLocation - 1, smallestLocation - 1 );
			else if( getRightTable().getRowCount() > 0 )  // if we have at least 1 row to select and smallestLocation <= 0
				getRightTable().setRowSelectionInterval( smallestLocation, smallestLocation );
			
			getRightTable().revalidate();
			getRightTable().repaint();
		}
		finally
		{
			f.setCursor( original );
		}
		
	}


	if( getRightTableModel().getRowCount() > 0 )
		getJLabelSelected().setText("Selected (" + getRightTableModel().getRowCount() + " Rows)");
	else
		getJLabelSelected().setText("Selected");
		
	return;
}
/**
 * Comment
 */
public void jDisplayChanged_ActionPerformed( long displayNumber ) 
{
	RightTableModel tableModel = ((RightTableModel)getRightTable().getModel());

	tableModel.removeAllRows();
		
	// Set up the column names and Display Title
	tableModel.setCurrentDisplayNumber( displayNumber );
	tableModel.makeTable();

	getRightTable().resizeTable();
		
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 9:05:01 AM)
 * @return java.lang.String
 */
public static String parsePointID( String point )
{

	int i = 1;
	String temp = new String();
	
	while( point.charAt( i ) != ' ' )
	{
		temp += point.charAt( i++ );
	}
	
	return temp;
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 9:05:01 AM)
 * @return java.lang.String
 */
public static String parsePointName( String point, int beginning ) 
{
	return point.substring( beginning, point.length() );
}
/**
 * Comment
 */
public void rightTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{
	if( mouseEvent.getClickCount() == 2 && getRightTable().getSelectedRow() >= 0 )
	{
		RightTableModel model = (RightTableModel)getRightTable().getModel();
			
		if( model.isRowSelectedBlank( getRightTable().getSelectedRow() ) )
			model.removeRow( getRightTable().getSelectedRow() );
		else
			model.addBlankRow( getRightTable().getSelectedRow() );

		getRightTable().clearSelection();
	}
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/00 5:06:37 PM)
 * @param i int
 */
public void setDisplayNumber(long i) 
{
	currentDisplayNumber = i;
}
	/**
	 * This fine grain notification tells listeners the exact range
	 * of cells, rows, or columns that changed.
	 */
public void tableChanged(javax.swing.event.TableModelEvent e)
{

	//update our text count of points in our list
	if( getRightTableModel().getRowCount() > 0 )
		getJLabelSelected().setText("Selected (" + getRightTableModel().getRowCount() + " Rows)");
	else
		getJLabelSelected().setText("Selected");

}

/**
 * Removes any resources used by this Panel
 */
public void dispose()
{
	fndDialog.removeListener();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D447352422D2E3930DE808FF452889E9E8352D6D33F9FEAF164F76A50D7D1EEDDE935B7A9A5A7ADA5A4F34FEF976452676E57895628F28A8822B22C14516ACBF625F0A4A4F02E0C8C458A0C9C8F5E1AF30FA77475D8BAC0A7A4E1939B3FB396C02586817433DF3E6664C19F34E1CF3E666EEC8663E48647804CCC26213097A6FC4BCA1619788399D5D72FF9C97A24524904D3F7DG5B48FF1FCE
	0267A428ABDF901312C13C5BD321BC8F4A85DF09C94902779D649F07FCE6F889B2CEA354657CAC49B97CB8EF6E6463EC246537ABB743F3ABC094607039CE4271332AF62B7865AA1EC3FC82A1ABBB31CDE915C1451D04F2A3C0B8C0D4A7EB3F941EE9F464312613EA3B364911E45D8F8EA7ECC1BA9A1A9C723AD8DF337A4CA38B6257F461D81FD6EDD67B8CC0F991GCEBE117C69339370ACEF3C5EF5364365B0F6D81DCE17D311BA3A0D0A2D43E835595CAE3BD3711ACC15B6DBA3FD2D16205E53E8F5CAB2696EC6
	5AF32BD2A9ADC20A213C0E62E61463F851703E8CE01540371FC17CC9BB4C97205B4E67F866F039BA0756DB8BC85B2B4E4474A0F38C313339C5597DF38C6FF9EBE3B6554709AC147BEA28CB3D9613E2G28G388A908210CB6D606C2E3F413332DD29F6B99CAEE7255BDDA55BBC9F38EC128C6FB055F6C545B5C99DAE0F0D9026EBEBD5E1GFE0683564FB56AFECCA6D1C7395E231B1EA22B3E79FE4246GB2D995D51C90F10D49E261B53FCC5858D1C147DEF1ED30DD8E9E7BB2510E5DB04E15189CE06CC5BFB912
	30DA2533DEB3F641F554F5BA5575EEF80FD76D036133A9DE5660190D4E21B6036581280B3D4EED23F5AB6725A1E9BA49A8E988A80708184A84334CF8E813753C3CC2478C1359189B85AF3F2278C88D1E7152CD71D8DE8CF586C7CC8A332F0AECDC33A1D01E893088E0B9409A00D4A734F3F29B9B3F2B689EED2C516AD12AFA6C32CD72905637A2EB97BCA51F623156BB3D0A5559A135D8BDF6EB3BACF9095A26BE8BF9EB9F546FC3EA7B9DB046F949A9F92C0A5D65846AD607848B564B5ADC4E5A06EDBC922D3558
	9D928C60F093CE77CA568E540D552BFC6436D995495F0255F7E521CEFA2DADC0918840BB130BA38765D58E7337G58049CFEC5693DAFF940DD18CC8DAE57759E3785E7C296B9D14E4FD35F610377C9CEAEE3FBAE6256E33D0B53495E3DDDC347EC6FC0762D1EFE3E46F61EC13F1C6F62B67307D6EEB366AD73C961CFAE843499DA6FD2ED4625DFBB7BA8CD640B151FAEEFD57B643B822F1DFF865898CB4598091FB37D2DC2D72F9AD0D721B007C80025C246165DF94CE62B2CDE09DB69C58F9818E473CB1C71396A
	8CDF2B98DB13857FF1943F92E892B744DEB7171D730CF6DE497740EB5628E5B7FE50989B6F215F3D7041014968C2B646A529970220D9F2CBCE9B3CF8553ACB59E8FB6A3A6DF409BE17A95E8D6B3C9A9616D760BF4B4635E8D5940F3D3DC7111AA5C531BB3B7C75929D4B5B536E763B88BE161C0D3C3B7AE01D0B76B732F10DF4381CCE09AACBF4D16B3DF40DCA3264G262BDDBDCE05617BF3504E2FCB7D2A0F381D0309CF2FD56EC1FF4372040556CDB8870F5D81A600AE0BC43A29DF95F98EC2A360B05FD16099
	6666EB316FDD3E9673EF4EA1CB2F9CCC8894CBA2661B93625CECDDEDF07B57A26349199B18F7057A8A956FE53C5E3E1C09F69CA0E1E1761F3197E999EE44249483545D60763FB6976B390FD18D3F5AA54BCC1CC24E7FE548C03DC8B7FAA4F04D684C5E9732DBED582E2BEBD165486A5798D00E32468F66F9D00E39E7F9AEF895787181C45CE032DCF9C31585ED771AE8A7C3B994E01E0735DB6D51361BFB0E37AB74462418813A3D2C5DBCAFEB47E475BFC7D0FFB47F3BE66D357A0C5EBE30C113698215974B6FAA
	2AD35576F1DE1E474935A843BCA5A665A4C02342F57D23ADBC0F8B3F9EC9E26766844DD53B954647C4452F6B3CB1BC0F7BE49F0E399C6AD6A9DCBFDDFB343124526BF5F558E998D3E53BEB2F569698AEF76FBB706C34FB3CCABDACD69F57437E3D48AF46B9952D46FD73594D6ADC57423885BD609FFB907FE2D32606772B008B830886C824741EBDDB0AEB199A0A11BD9AA5CF274B6310EC85A8D92335CFB1D23FE12C2455B578FE046B612D42D3703CD65523A810AD58ECD7AAF5B49A0293B9FD2405703C6D499D
	95AA0F4B3FB61D145F5D1190A80F8ECB5F13105A4B64DE5C6B177F40B7D292577C63AF9B19ACD24FF1D98CC01BC53DDC96D39B8349E2CDAF17056B5CBFA70B138565FED9B4C98ED72FB4CAF1B07E178B7EC99F58A9G73D9EBCFE9FD169B7051GABGD67771B99C2DF88F1E8ED70FD7B2523F55323D637AD07EBF40BA4E3E4AFFD26AB1F8FA6CDD5D8ADD62D7BE509250F36E27407448E2D9CD35BEEF5F50674FB9B2FCC3738F164F4652F5196F536739EBA83E5C274FF3BD35BC4F35C05DE29F4F41FE1707B2B2
	74035CG9A813A8142G227A79FAFCE66FD1B2C20EDB0F2E57E9151B8785B11EEB6EC3D9C35CD14B9D7BD0FEB46E88B9DCB82B1743327E41F30F5515D93F56A17D766A4AC9B78777E33ACBEF4E8E6C8F19B5C29D332117F375F18F73C48350AC86E0FE13B4272A7C545D647E71F62D56BFAEC9AFCD589444BF06287CC55E742F4FC2314F1DF2G7DFBAC54EDG483C856382D8G5CG6137380EDE5FF7E6A49DD15B33382A81D787F129DA1FBF8819EEBDF7FF3AB08C6927574522DB037BB179ADEF6472DBFE0B67
	11BF3ECF9EE2C764A1E9A010BDCC68FE0F84BE9F696A319BEDCE9BECC6BC562ECA274D6CF129911D5BC8C73356C68CC03F8EC086889960B6D2E06231B5E2004756385338A6D7C0B98E005C865FCB77CAEDB48E6F06F221401D2AC1DCA3143BEFF3997D062E6909D00E86D8AA70EFD07C4A5B4CDFA3AF8C6F2BED5549827B62F3696D41B2093EBD7AFD8F9BE37210B192B575FA390FF68CE66BEF64854F4D7F1C97B8B7FFBBAFF0EE1E3CFF9B43838D6D8E1D57EF59BFFC6E3EF3FF205CFD8F1D770B3B52A9ED2FD7
	725245E736AAD6356E374ACE92A0077F90B6712A4CF610074E2629EDEA94D7F529D8AF1B8C5C16B65C6636740B0B287332BBB1A98DG36BBDC67494D7A75B0GF5136EB07D4E3BB3D44F4C6F6464E36CDF8C757F893086A069EECCCA7EDD6EEF1E5DBFE2CC28718D9393E69D882E43F987826B70AB87B047743AFABC9D12DECFDFBF30DB5479BDDB3DA2B9D0242C4DF3343FE275F4C9C3747CDD5A1F55856B7F038341EC01692B661CD6DFA5E2CD7C28D12F83B3483175AE133DFCF7288E983D348AAD3D23047B2A
	2E9A3421G312B3943833B984C688977CD4FDD8E943FC2E04C703B5CB74D20718BE593FD175B5369D3E8CF99A1109B83D404F0FBFA4F04F3E98D896C9F64GF816173451B33F10901617448B3CC47193B5F8162F7C61B4BF470E003A8DA1BCDF73D6631ED536763BFA9408978FE15B8955FC0ED7216C830884D874901BAB1359577BB07EF6FA2C8E90D533DB6AA0D1DF403E0DB53CEFA6148BG6AG3A81423E40477C54087B910DF49B24CE456211A4D5CFBD17D0B6ECA7D52A622EEF5DA4F0CD18A12A7AB8F8DE
	9FAB104735B0D65298AE638C035EDE8CE37CF2AD9E93CC46CD0DA8B37398A64BE4017F0C62DBB5F8A6635597F95EACC35D0AB1BC569828BD140502ACC2B96ED08B625A20AC8B5C78969E13FC215CAF9C488F3E2E0F6487DE57C57901FD73317C61FD73197CE06B117197574867B1D370DCF2094FE32140CD3B446731A4142F09B7AEA38E790A8D65EBE2E396BD679E8BB998C05DD82E2F48E69E732E82CE81888318B9F678983AB0C07D32GB8264385F4BD2E9C2BBFE37F8E45470D551F31D71E6267F66B212EED
	9C1F2B1966A1CB213CD2601EB571792F9E4767540E692B6A3F2EEB5E571D5647774F935898DB4A9F6498CCBE5F28667EA5F5BC7837715CCF14DC45FCDB78D65C3F19CC67BDF6DB15352B01FA9DE69FEF519C8D7B7B46F37BA8B0A34D05D0FE81A0F6BC5F8B788E713DC074D353C9C3487B814FCAE2AD25896B47B37D9B427CFB01A8588B30B1FBEB78182DD0BFA98CF173EA34FBF454770984C20C70E4752F8C2A477502FBD6FFFDEA2D369E79FDBBC9DBEF9AD40FB2FACF6D4F667652C1BE378DAFC59179DB2702
	1E832DD46734C1FDB6363CCB72A77CC51D5BE767F98CAA7BA21767A359A84F88A8AF84F8610B5CDE8F537CA7965B891CE38F37615D93388D3752755F8D65D0016B20B8BCCF4333B7067B7D4551589B525A2A523DF71BCEEC9C4D98BCB73E573B8196F35E26F3F3B8A5C8486C9D5193D0AEB554170F8118B90147333ACBF88E2C00A3DEBB816F3E677E9DB54962616C969E1F4A9E869F87508D9086908540E86E227EF2BE408B00E3B43EDF68277155CB1C46CA28DF8330F9A26CE5G9AA6F2193FF91947B8F9A213
	E348C43FBC4361BDEA62E079B2FFF70B7A60A59375B1EE9C452F18280FF1DB32F80CDB8DF5F5DF627692C365928D65958217EBC15CEAA82F9738F60ACBFD84FC7EA39C3704624CD0EE9538440F282DC1B9D4605CB739CDC6BC42677D2DD3A87BB8D1FE1E16715C16154F1F9A0D1DA01D517B572B239AE33477D3EA4EE4427CC7F139E40B5DED81D3A34CCE0E9B7DF692E362F6C29E0DC94984A88330GA80FF2F9AC9EF2BEF6EFF37C7EFD76FBFB547D187D3DC5FDFF78237A7CB631065EBBBF2A4FEF0DE69EFF22
	21AEE392BF0F4BAE227B90A8CF820882D88A908B30E1924F31DE4EAFA6A36C3DACAEF783A4E132A6EDE2733BD5F3FF5FA8FCF7A377730B1EFB12AC9E776778E067D999135573E67599E7F91762597C220BF1FED721EEGA092E0B140F2005513797CF28F96327955586CCA2522D8BB3A69C9BF1FDC0BDD6A7323B119D7F316735C2E57832D4D0F452464BDB6727AA88B50268EF0ED0F8572BF8974EE4D7D18BE8F5BCC71A10F69733070EC1E07CD023AF50FF19FFC56B0F49F7311012F0756A9B1A96EA96A9EFD8A
	BB47D9BB855B046EB18FB317E4F5AC8ECB204F8A001D2AFD2F06775761796BF83ECE3F3693E36E856D99BC61F54FBA5E7157D55AEC9C5F560C4F431507D55B086C53FE37F4721D93816F7D90CE0633B9B5284FA5635FD5E97C4727DA9A995377A7448721912A761D946E4F11D88E312C0567C74B42F9FE74C64B107C08E8731B7C4B7A7C6809C175AD1775795133036A43AE6B73233F2B75CCD66FDD2232EABA2C1E5D3CFC097338C170985FB4BC0FC71B74BC3EB628DEEA9A1E47471B027148625456D69E3B4A26
	7258F5AC95F1F5D0EE9338C5E9BC1E05885C1E8A442D00F25CD46E339F4AC25C86A86F1E46F193A92EFC9A3B03E1389DD9A345A16C4F5F115E687DF96108E3E84FA16FAF567D34993FE16815BC0A3D43AA33E302AA2B2751A55BBB7A894B195651FC83F36736E95C8714DA5087343BBCF4D39BB58D5B55DBB09F18A95A74DB50EFF63BBC761BAE27B214BAD169CF25B9C9A5409A5177E50A6B85EF69F6F994C67F7714FE61633C4D1B5BF07D3BBDD2276471C836E67BCDC950AC24B94DC9E8EBFE1C7B1F71B70773
	BF7C1B8F6EFF5C50AF74F166FF309C816F4F03714F957E67F9977A18A6F107464FB86344F9793C4779BA49BB2DDFA77B9335F65EF4DA3FCE62872D0350537AF5B2F6D07FEF1F562F935E1F599229A162BDCD73DEE6FE70FB628F9E68986A59CD51B0E7B7C5C14EEE0A021C5D948DFFEEFE26A878B7AF66A26DB7AFACEF38D30A3AD98536309E40908179B800B902678CBFAEBAC0E84E90A0DF2831F56119B0FEE12770713FD59CFC2ECB0AC77EAE6FFB45E8376DAE880D8E244F71AF95233E94171BA299EEC5B14A
	085E933326AA7E15E234F1D96A24E866CFC369BE78AA4CABB4027B58A9F5080B00F234400D5BC67DAE14578BDC6CB66E47B7BFA176A0FD5CE767895C73D7384FF6BF41FDF1AA7DB6A8D4143753F204A8CF4F7EBC6C8F69873771EFFEAEE3847361AC9F3BED60F977E260E56D937C5C276B40C122397F3337BB0CBD3D8E2335DDB1CA10051ACC9F7648F2031A2E91F6BE719C25D5B0BDA625822095E09F55AB6C97897F3776DCA09C5B8B0515E10965BF3F5C4C73EB4333B3087955BF473FCB8267BF6E696A1D5DF4
	FF7E93F7ED03FADFBCF00CFED389F519B34477B4A5986FDB2C57541D822655AE8FDD9D00EC74385C903A7A9916FE45242E393E63C4586AD7CB8E862381AB6C023557FA317D1A5481CE3CCB687345167B5B032D3CB45AFEACDE1D20F9CC994C3BE2860FEF8D29C3736C0ED41EE74BB358FDC1A8BCA3E6303BD29667DECA9BAE4EEDA403736C0550F719AAEFAC2F1C615F6FB1BEDE39027BA5075DE9F774B8E814E5BC57527CE9B534DFABF87ED2BA0EF121415AAE494D92FE15A6095C3BA88FE32966AEC29BBA897B
	EE675F3234F14CB0134D2BE2A6273F4902F7D31A2BA323061E31252649D2DFDD59F025393A69E2C3C3D5E553154A064B159FB70B39DC31786D760C244D5B0BEF45A7A48631DB4BCCE62F36197E5C1F510B369C9D061F3A0BCD751FDC7C509208A3561FFCC4D795DDD387119D95AF5D4C0327D36A4B931F62186D90997003660252326242BC78F3E4F4EB368EBF5FD26D74F51356CE43E1DED1B3074FD159DA9AD79ABC76FC29F46458B32D14FE7F86B143526FD66FA6E714221D3BBABB3D1208F111259982D76FED
	F4F96DF82749E4B3637C48B96B374F731CF509301D2F54E9F3D6361E3EDB47575386E817B9ABA6258160AAGFE03407A4D5AF6EFF96CF73671BC56B70B1FB7766EC05CE2A8AF1BA572503E1172D0AD2C05FEC933E3D25ED54B3BE77B57A79B638FB4A666833EF8B667B96C9D44714BBE36362AF334EB2BEDB61B634C59BCCE0E99F2F7F7EFBEEF72287B31F387639EFA67B0DBFF6EF40262174C561FBBAD95716F8528AB7CB2BFF7BAEEC2F9CE02F2B4408A00B8008DG1911B1A985G65G0DG5DGA1G61GD1
	GCBG168344822C0B64B16707E5781DDC301823DE2850244CE335E3DA265A6CC347F0EEA2EFE461C82D8BBB063287972A582D327DA67DA434999ABA3B585AACAD772F7D3F258E3B760F435ABFF67C881349432962AEE88E7849B97C8E7065D4F147B3075FC94427D1F9C379850058B9230D799F1D21A57A3E631C1F5F5F6F9A16DF55D7295F72F686791617C68C467B1F32787D657AB9DC47CB4FE1CED77CA457511B478F8C23A37CCE0CC9171F93A41D26675E509F77CD8C17C6F1ED00433D981B4B183A7B0DAF
	CC9F3F2B63E7E6BE203B70C91E237DF64F91927C9B2D9E45AE1BCC963BA2CBD52AFBD27712A575DC96F1CFF2BD3E1CC13FEB1E9B13F29220F1AEDF73AB365DEFACE2BABDD46957A9A8739EF4BABA7B292F725B0FDC6D9F6BC6763D5BCF6B1932A0F28C76DBC06E8319BCEF66705F81F64F65FE2F8CBFC9D1FD59994D7B778D8FFEEF7C5A839DC37DBD08E9185F0318C60E591F18826F972F1A066E97A5D3607D62F513F63F48F0EE936A348B7CE92BE857A3F09F8B5CCDCA93F1176DB625DB60C3A8DF082F1368E7
	AABCC721FC75D1DC2B0AFBD8037BD845CDB2E1BEGD306504E7BCEB5211F69344BB2ABBFC15B581D100688F94C26BC5A5DD68D5FF3856EE3215B49297797CF3FFC1F7DD60E3A9F0B37BDB4AEC44E554761BB949FBDD79F07B72609FDA854996671F574D616F67C10F9AC1F8CD71FD170DCB2EFF00E49E4376C9C4A582B7AEB364E2D07B69391AF295A995E605B6247F3352B737FE35DC873DF219B1F757B6B087D745F6132FE476B315F9A20C9668F261BB9FFE8AEDDGB8A657735B68F76E737572EE24783A79FA
	F97FB4034B5B82F50B67F3F95F4D990EEF43D3C3F9D06F516876073D07EA5E3724BDF8FFF768010E41627A365D68EB0AE17E95CF71F323FF2767D096A85B842E301E1FAF39856E4EG7D46884AAB8158772ECBF7B01B136DCE291A7DF2D50C75812531865AA6BD4DE9ACCB275FC0C339CC6052B3681D84143B856ED30A9B0072242739CEBF586657C70B667D350C872F1B758FF48C75FB359CAE7319CF733C25E0B77539904759FE66AC555FD228DFAE5A7C22EE70F79F0FE24ACF58FE2A3AFEB47BA992857B02A8
	767FDB20F720703EA9DEFB5EB1F5001EF7D8FD3A730E2FD25BA8037695D11CAFDBBDAEA085B337DA975D8B30368354AEE4E86793EDFF1E3E0537659BDA51FEDDBA4FCB22C57BC3995BF97B8BBDDE455E895BDD560975294860FD1608BE1E0CE45E0770DC6B9B743CDF7D3EA5C77CBF9F20CFACGF9A6A6A58F2062997EBDCCCD839132F82E6EB05165E6B84F0283B6703F2EAEE33BCB369DA84CAB017FE057B43AFC507B8191BCCE7B50BFE6ED73BE7D187EFC30246CA40CF57C085A6F2F0D7EFE7F12BE9ADE5573
	248C54FB0920F1D91455FDD48E3F4B397A8C3F4B59142B3F4B19CC34F7B1E7F2073F4BF1656A6FF2E68D2A1F13BB7CDD4E4AC1FEBF82780AFEE678339B36767E184377C03384EEDD8E5F83619C587F03247D54C698B47AF5122C10944823734E1E0597F6412E103D66F6B90F7ED0ACCFF16551DF69CBF9DE48ABBDD65801F93D9E12AE44DFCF7326585E232644DF4FCDCCA0C975A405651175A4891341FA12CCF345FA1288F9248FFF630F67BE89AA64G356DC30D2072111F3E9AFD451C19C09200E03744A8A477
	E11AC0E92CC5705EA0F728F692D489623FF80ACDD6917FAB5AE4281A44CCF993EF232113BC04EA06EA53C8681D41ED94124A4FBE9512D659EB354BB4217610AD03AC73FBF30E7DE679AFDA3C09A4251EE42383EC16BC74FCC032616FAD547F01C1ADC9233B9AF4C5A0B7AFFDA4FB15FED9EAA0191DCD78C3DEAF0CD549CF9E3C8A59C9FF42D7049B4B22BA2B37DBC186CEB710A4A77E2F9648F6D6ADDB1DDDC5EC23FC1A8AAE3E72AA9553D6AAAA8F59C179C5AAC6C1E5473F269F5CFFE17562C412D0CB5E11DDD6
	DB2D3583D4DFCF5214EE0F646D06E566F31F2B2004D2699D8E7A53A4A017AB260F13B56237D502696B3ED9933744ED2985ED79BC240836C4FFEF94ADEC555DD68F8CA5F96A95E6ED79226E213D4A219F1E4FEACBA0716AA4D1A35BFDEE305CCE2B43AE77430B197A812A2BDDD0E43B81E130A88CE3AD4CC779596BAFBE79737235EB00D8ADC926BF768316B27C2DBE6CF134CB1EF151D377EC8A0FFD98B9F663F4B965B458CDB93D78ABD0A01E271BB32F922C9B273CD8BC751937BE212C7B3BAAE49FD392F4AD6A
	12DC0E224A4ECE48C084815098105FA348E39B232E4DF65F641D97FE72F6BBD81819A4BA0A0B695F927A37947F16D0CC89451414025A5612E17F696BFDFC4E54ACE9E18F5A108F5FF2FCE235700C36D43B86699A8A27250CA14607584C21D604685DC217D84A45BF7B70CF9E6DF4007EBDB86A19E02F433A9998FD6ADD41113B5A3B0275E3F7877DCEA8E4013A6FD9603F63E2E7D82B4A695DA56057AC6067D80565F8C69D6CEE2A59E115E545AADF2FF45A701E2A2ADF117C77503FACC77ADE5EC62DE4F5F16548
	77B5CD1F7F87D0CB87880FBD82F1EC9AGG3CCAGGD0CB818294G94G88G88GF5F954AC0FBD82F1EC9AGG3CCAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG269AGGGG
**end of data**/
}
}
