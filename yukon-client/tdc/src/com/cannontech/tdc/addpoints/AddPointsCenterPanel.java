package com.cannontech.tdc.addpoints;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 4:54:40 PM)
 * @author: 
 */
import java.awt.Cursor;
import java.awt.Frame;
import java.util.List;

import javax.swing.ListSelectionModel;

import com.cannontech.common.gui.dnd.DAndDDevicePointTable;
import com.cannontech.common.gui.tree.TreeFindDialog;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class AddPointsCenterPanel extends javax.swing.JPanel implements javax.swing.event.TableModelListener, com.cannontech.common.gui.dnd.DragAndDropListener
{
	private TreeFindDialog fndDialog = null;
	
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
	private DAndDDevicePointTable ivjRightTable = null;
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
			
			ivjLeftTree.setToggleClickCount( -1 );
			
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
 * @return com.cannontech.common.gui.dnd.DAndDDevicePointTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DAndDDevicePointTable getRightTable() {
	if (ivjRightTable == null) {
		try {
			ivjRightTable = new DAndDDevicePointTable();
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

	fndDialog = new TreeFindDialog(SwingUtil.getParentFrame(this), getLeftTree());

	getRightTableModel().addTableModelListener(this);

	getRightTable().addDragAndDropListener(this);

	// user code end
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getRightTable().addMouseListener(ivjEventHandler);
}

private void initialize() {
	try {
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
		Frame f = SwingUtil.getParentFrame(this);

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
                        int paoId = ((LiteYukonPAObject)userObject).getYukonID();
                        List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(paoId);
                        
						getRightTable().addDevice(points);
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
		Frame f = SwingUtil.getParentFrame(this);

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
}
