package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.cannontech.common.gui.tree.*;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.model.LiteBaseTreeModel;

public class TreeViewPanel extends javax.swing.JPanel implements java.awt.ItemSelectable, javax.swing.event.TreeWillExpandListener, ItemListener
{
	//gui components
	private JTree tree;
	private JLabel sortByLabel;
	private JComboBox sortByComboBox;
	//The possible tree models
	private LiteBaseTreeModel[] treeModels;
	
	//Stores the current selection for each treeModel
	private java.util.Hashtable selectedItems = new java.util.Hashtable();

	//The currently selected item
	private Object selectedItem = null;
	//private LiteBaseTreeModel selectedModel = null;
	
	//List of item listeners
	protected Vector itemListeners = new Vector();
	//flag to indicate that the most recent selection was not acceptable
	protected boolean undoLastSelection = false;


	private static final TreeFindPanel FND_PANEL = new TreeFindPanel();
	private static OkCancelDialog dialog = null;


	private static java.util.Comparator nodeComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LitePoint)o1).getPaobjectID();
			int anotherVal = ((com.cannontech.database.data.lite.LitePoint)o2).getPaobjectID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};


/**
 * TreeViewPanel constructor comment.
 */
public TreeViewPanel() {
	super();
	initialize();
}

/**
 * This method was created in VisualAge.
 * @param l java.awt.event.ItemListener
 */
public void addItemListener(ItemListener l) {
	itemListeners.addElement(l);
}
/**
 * This method was created in VisualAge.
 * @param listener javax.swing.event.TreeSelectionListener
 */
public void addTreeSelectionListener(TreeSelectionListener listener) {
	getTree().addTreeSelectionListener( listener );
}
/**
 * This method was created in VisualAge.
 */
public void clearSelection() {
	getTree().getSelectionModel().clearSelection();
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:19:25 PM)
 */
private void expandRoot() 
{
	TreeNode rootNode = (TreeNode) getCurrentTreeModel().getRoot();


	Object[] pathToExpand = new Object[1];
	pathToExpand[0] = rootNode;
	getTree().expandPath( new TreePath(pathToExpand) );	
}
/**
 * This method was created in VisualAge.
 * @param e java.awt.event.ItemEvent
 */
protected void fireItemEvent(ItemEvent e) {
	for( int i = itemListeners.size() - 1; i >= 0; i-- )
		((ItemListener) itemListeners.elementAt(i)).itemStateChanged(e);
} 
/**
 * Insert the method's description here.
 * Creation date: (4/19/2002 12:54:50 PM)
 * @return com.cannontech.database.model.LiteBaseTreeModel
 */
private LiteBaseTreeModel getCurrentTreeModel() 
{
	return (LiteBaseTreeModel)getTree().getModel();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 */
public Object getSelectedItem() {
	TreePath path = getTree().getSelectionPath();
	
	if( path != null )
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node.getUserObject();
	}
	else
		return null;
	
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.tree.DefaultMutableTreeNode
 */
public DefaultMutableTreeNode getSelectedNode() {
	TreePath path = getTree().getSelectionPath();
	
	if( path != null )
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.tree.DefaultMutableTreeNode
 */
public DefaultMutableTreeNode[] getSelectedNodes() 
{
	TreePath[] path = getTree().getSelectionPaths();	
	
	if( path != null )
	{
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[path.length];
		
		for( int i = 0; i < path.length; i++ )
		{
			nodes[i] = (DefaultMutableTreeNode) path[i].getLastPathComponent();
		}
		
		return nodes;
	}
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 */
public Object[] getSelectedObjects() {
	Object[] items = new Object[1];
	items[0] = getSelectedNode();
	return items;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.tree.TreeModel
 */
public TreeModel getSelectedTreeModel() {
	return (TreeModel) getSortBySelection();
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JComboBox
 */
public JComboBox getSortByComboBox() 
{

	if( sortByComboBox == null )
	{
		sortByComboBox = new JComboBox();
		sortByComboBox.setName("JComboBoxSortBy");
		sortByComboBox.setMaximumRowCount( 13 );
	}

	return sortByComboBox;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JLabel
 */
private JLabel getSortByLabel() {
	
	if( sortByLabel == null )
	{
		sortByLabel = new JLabel("Sort By: ");
		sortByLabel.setFont( new java.awt.Font("dialog", 0, 14) );
	}
		
	return sortByLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getSortBySelection() {
	return getSortByComboBox().getSelectedItem();
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JTree
 */
public JTree getTree() 
{
	if( tree == null )
	{		
		tree = new JTree( 
			new com.cannontech.database.model.NullDBTreeModel() );

		//this removes the automatic expand/collapse from our tree
		tree.setUI(new BasicTreeUI() 
		{
			protected boolean isToggleEvent(MouseEvent event) 
			{
				return false;
		 	}
		});
		
		//do this so we can see the tool tips for each tree node
		ToolTipManager.sharedInstance().registerComponent(tree);
	}

	return tree;
}
/**
 * This method was created in VisualAge.
 * @return LiteBaseTreeModel[]
 */
public LiteBaseTreeModel[] getTreeModels() {
	return treeModels;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:23:32 PM)
 */
private void initConnections() 
{
	getSortByComboBox().addItemListener(this);

	getTree().addTreeWillExpandListener(this);

/*
	registerKeyboardAction( this,
								javax.swing.KeyStroke.getKeyStroke( 
								java.awt.event.KeyEvent.VK_F,
								java.awt.event.KeyEvent.CTRL_MASK),
								WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	for( int i = java.awt.event.KeyEvent.VK_A; i <= java.awt.event.KeyEvent.VK_Z; i++ )
	{
		registerKeyboardAction( this,
								javax.swing.KeyStroke.getKeyStroke( 
								i,
								0),
								WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		registerKeyboardAction( this,
								javax.swing.KeyStroke.getKeyStroke( 
								i,
								java.awt.event.KeyEvent.SHIFT_MASK),
								WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);		
	}
*/

	dialog = new OkCancelDialog(
			CtiUtilities.getParentFrame(TreeViewPanel.this),
			"Search",
			true, FND_PANEL );


	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				//do the checks of the KeyEvent here
				if( e.getID() == KeyEvent.KEY_PRESSED && e.getSource() == getTree() )
				{
					//do the checks of the keystrokes here
					if( e.getKeyCode() >= KeyEvent.VK_0
						 && e.getKeyCode() <= KeyEvent.VK_DIVIDE
						 && e.getModifiers() == 0
						 && !dialog.isShowing() )
					{
						FND_PANEL.setValue( new Character(e.getKeyChar()) );
						
						dialog.setSize(250, 120);
						dialog.setLocationRelativeTo( TreeViewPanel.this );
						dialog.show();
		
						if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
						{
			
							Object value = FND_PANEL.getValue(null);
							
							if( value != null )
							{
								boolean found = searchFirstLevelString(value.toString());
	
								if( !found )
									javax.swing.JOptionPane.showMessageDialog(
										TreeViewPanel.this, "Unable to find your selected item", "Item Not Found",
										javax.swing.JOptionPane.INFORMATION_MESSAGE );
							}
						}
		
						dialog.setVisible(false);
					}
	
				}
				
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});
	

}

/**
 * This method was created in VisualAge.
 */
private void initialize() {

	setLayout( new java.awt.BorderLayout() );

	JPanel sortByPanel = new JPanel();

	/**** LAYOUT ADDED FOR sortByPanel 3-16-2001 ********/
	sortByPanel.setLayout( new java.awt.GridBagLayout() );
	java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
	constraintsJLabelName.gridx = 1; 
	constraintsJLabelName.gridy = 1;
	constraintsJLabelName.anchor = java.awt.GridBagConstraints.NORTHWEST;
	constraintsJLabelName.ipadx = 3;
	constraintsJLabelName.ipady = -2;
	constraintsJLabelName.insets = new java.awt.Insets(3, 3, 3, 3);
	sortByPanel.add(getSortByLabel(), constraintsJLabelName);

	java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
	constraintsJTextFieldName.gridx = 2;
	constraintsJTextFieldName.gridy = 1;
	constraintsJTextFieldName.gridwidth = 2;
	constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
	constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.NORTHWEST;
	constraintsJTextFieldName.weightx = 1.0;
	constraintsJTextFieldName.ipadx = 10;
	constraintsJTextFieldName.insets = new java.awt.Insets(3, 3, 3, 0);
	sortByPanel.add(getSortByComboBox(), constraintsJTextFieldName);
	/**** END OF NEW LAYOUT ADDITION FOR sortByPanel ********/
	

	getTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION );
	getTree().setCellRenderer( new com.cannontech.common.gui.util.CtiTreeCellRenderer() );

	javax.swing.JScrollPane treeScrollPane = new javax.swing.JScrollPane(getTree());
	
	add( sortByPanel, "North" );
	add( treeScrollPane, "Center" );

	//try to make every component have the same Fly over help
	for( int i = 0; i < getComponentCount(); i++ )
		((javax.swing.JComponent)getComponent(i)).setToolTipText("Press CTRL+F to search the tree");
		
	initConnections();
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ItemEvent
 */
public void itemStateChanged(ItemEvent event) {

	if( event.getStateChange() == ItemEvent.SELECTED )
	{
//temp code
java.util.Date timerStop = null;
java.util.Date timerStart = new java.util.Date();
//temp code

		//Store the current selection before we switch models
		Object value = getSelectedItem();

		//Destroy the previous model so the garbage collector can munch on it
		LiteBaseTreeModel previousModel = getCurrentTreeModel();
		((DefaultMutableTreeNode)previousModel.getRoot()).removeAllChildren();

		if( value != null )
			selectedItems.put(  getCurrentTreeModel(), getSelectedItem() );
		
		LiteBaseTreeModel model = 
			(LiteBaseTreeModel) getSortByComboBox().getSelectedItem();

		try
		{
			model.update();  //time hog on large DB's possibly!!
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}		

		getTree().setModel(model);

		//Make sure that the tree is expanded at least one level
		expandRoot();

		//If there was a previous selection make sure it is selected
		Object itemToRestore = selectedItems.get( model );

		if( itemToRestore != null )
		{
			TreePath selectPath = com.cannontech.common.util.CtiUtilities.getTreePath( getTree(), itemToRestore );
			getTree().getSelectionModel().setSelectionPath( selectPath );
		}
		
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for TreeViewPanel.itemStateChanged(ItemEvent) setModel" );
//temp code
		
	}
}
/**
 * This method was created in VisualAge.
 */
public void refresh() 
{
	getCurrentTreeModel().update();
}
/**
 * This method was created in VisualAge.
 * @param l java.awt.event.ItemListener
 */
public void removeItemListener(ItemListener l)  {
	itemListeners.removeElement(l);
}
/**
 * This method was created in VisualAge.
 * @param l javax.swing.event.TreeExpansionListener
 */
public void removeTreeExpansionListener(TreeExpansionListener l) {
	getTree().removeTreeExpansionListener(l);
}
/**
 * This method was created in VisualAge.
 * @param e TreeSelectionEvent
 */
public void removeTreeSelectionListener(TreeSelectionListener l) {
	getTree().removeTreeSelectionListener(l);
}
/**
 * This method will search all of the available tree models to find a match
 * to str and then select that Object.
 * @param str java.lang.String
 */
private boolean searchByString(String str)
{
	if( str == null )
	{
		getTree().getSelectionModel().setSelectionPath( null );
		return true;
	} 
	
	TreePath rootPath = new TreePath( getCurrentTreeModel().getRoot() );
	
	if( searchForString( str, rootPath ) )
		return true;
		
	return false;	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param val java.lang.Object
 */
public boolean searchFirstLevelString(String val)
{
	if( val == null || val.length() <= 0 )
		return false;

	TreeNode rootNode = (TreeNode)getCurrentTreeModel().getRoot();
	
	for( int i = 0; i < rootNode.getChildCount(); i++ )
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(i);

		String nodeStr = 
				(node.getUserObject().toString().length() < val.length()
					? node.getUserObject().toString()
					: node.getUserObject().toString().substring(0, val.length()) );

		//we found the string
		if( nodeStr.toLowerCase().indexOf(val.toLowerCase()) >= 0 )
		{
			TreePath path = new TreePath( node.getPath() );
			
			getTree().getSelectionModel().setSelectionPath( path );
			getTree().scrollPathToVisible( path );
			return true;
		}
	}

	if( val.length() > 0 )
		if( searchFirstLevelString( val.substring(0, val.length()-1) ) )
			return true;

	return false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param val java.lang.Object
 */
private boolean searchForString(String val, TreePath path) 
{
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

	val = val.toLowerCase();
	
	//if( node.getUserObject().toString().toLowerCase().equals( val ) )
	if( node.getUserObject().toString().toLowerCase().indexOf(val) >= 0
		 && node != getCurrentTreeModel().getRoot() )
	{
		getTree().getSelectionModel().setSelectionPath( path );
		getTree().scrollPathToVisible( path );
		return true;
	}
	else
	if( node.getChildCount() == 0 )
	{
		return false;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			Object nextPathObjs[] = new Object[path.getPath().length +1];

			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );

			nextPathObjs[path.getPath().length] = node.getChildAt(i);
			
			TreePath nextPath = new TreePath(nextPathObjs);
			
			if( searchForString( val, nextPath) )
				return true;

		}

		return false;
	}
	
		
}
/**
 * This method will search all of the available tree models to find a match
 * to str and then select that Object.
 * @param str java.lang.String
 */
public boolean selectByString(String str)
{
	if( str == null )
	{
		getTree().getSelectionModel().setSelectionPath( null );
		return true;
	} 
	
	TreePath rootPath = new TreePath( getCurrentTreeModel().getRoot() );
	
	if( selectString( str, rootPath ) )
		return true;
		
	return false;	
}
/**
 * This method was created in VisualAge.
 */
public boolean selectLiteBase(TreePath path, LiteBase lBase ) 
{
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
	
	if( node.getUserObject() instanceof com.cannontech.database.data.lite.LiteBase 
		 && node.getUserObject().equals(lBase) )
	{
		getTree().setSelectionPath( path );
		getTree().scrollPathToVisible( path );
		return true;
	}
	else
	if( node.getChildCount() == 0  )
	{
		return false;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			Object nextPathObjs[] = new Object[path.getPath().length +1];

			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );

			nextPathObjs[path.getPath().length] = node.getChildAt(i);
			
			TreePath nextPath = new TreePath(nextPathObjs);
			
			if( selectLiteBase(nextPath, lBase) )
				return true;

		}

		return false;
	}
	
		
}
/**
 * This method will select objects looking in current tree model first
 */
public void selectLiteObject(com.cannontech.database.data.lite.LiteBase liteObj) {

	if( liteObj == null )
		getTree().getSelectionModel().setSelectionPath( null );
	else
	{
		TreePath rootPath = new TreePath( getCurrentTreeModel().getRoot() );
		
		if( selectLiteBase(rootPath, liteObj) )
		{
			invalidate();
			repaint();
		}
		else
		{
			for( int i = 0; i < getSortByComboBox().getModel().getSize(); i++ )
			{
				com.cannontech.database.model.DBTreeModel model = (com.cannontech.database.model.DBTreeModel) getSortByComboBox().getItemAt(i);

				model.update();
				
				getTree().setModel(model);
				
				rootPath = new TreePath( getCurrentTreeModel().getRoot() );
				
				if( selectLiteBase(rootPath, liteObj) )
				{
					getSortByComboBox().setSelectedItem( getSortByComboBox().getModel().getElementAt(i));
					invalidate();
					repaint();
					break;
				}
			} 
		}
	}
}
/**
 * This method will select objects looking in current tree model first
 */
public void selectObject(com.cannontech.database.db.DBPersistent obj) {

	if( obj == null )
		getTree().getSelectionModel().setSelectionPath( null );
	else
	{
		com.cannontech.database.data.lite.LiteBase liteBase = com.cannontech.database.data.lite.LiteFactory.createLite(obj);
		TreePath rootPath = new TreePath( getCurrentTreeModel().getRoot() );
		
		if( selectLiteBase(rootPath, liteBase) )
		{
			invalidate();
			repaint();
		}
		else
		{
			for( int i = 0; i < getSortByComboBox().getModel().getSize(); i++ )
			{
				com.cannontech.database.model.DBTreeModel model = (com.cannontech.database.model.DBTreeModel) getSortByComboBox().getItemAt(i);

				model.update();
				
				getTree().setModel(model);
				
				rootPath = new TreePath( getCurrentTreeModel().getRoot() );
				
				if( selectLiteBase(rootPath, liteBase) )
				{
					getSortByComboBox().setSelectedIndex( i );
					invalidate();
					repaint();
					break;
				}
			} 
		}
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param val java.lang.Object
 */
private boolean selectObject(Object val, TreePath path) {

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

	if( node.getUserObject().equals( val ) )
	{
		getTree().getSelectionModel().setSelectionPath( path );
		getTree().scrollPathToVisible( path );
		return true;
	}
	else
	if( node.getChildCount() == 0 )
	{
		return false;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			Object nextPathObjs[] = new Object[path.getPath().length +1];

			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );

			nextPathObjs[path.getPath().length] = node.getChildAt(i);
			
			TreePath nextPath = new TreePath(nextPathObjs);
			
			if( selectObject( val, nextPath) )
				return true;	
		}

		return false;
	}
	
		
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param val java.lang.Object
 */
private boolean selectString(String val, TreePath path) {

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

	val = val.toLowerCase();
	
	if( node.getUserObject().toString().toLowerCase().equals( val ) )
	{
		getTree().getSelectionModel().setSelectionPath( path );
		getTree().scrollPathToVisible( path );
		return true;
	}
	else
	if( node.getChildCount() == 0 )
	{
		return false;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			Object nextPathObjs[] = new Object[path.getPath().length +1];

			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );

			nextPathObjs[path.getPath().length] = node.getChildAt(i);
			
			TreePath nextPath = new TreePath(nextPathObjs);
			
			if( selectString( val, nextPath) )
				return true;

		}

		return false;
	}
	
		
}
/**
 * This method was created in VisualAge.
 * @param int
 */
public void setSelectedSortByIndex(int indx)
{
	getSortByComboBox().setSelectedIndex( indx );
}
/**
 * This method was created in VisualAge.
 * @param models javax.swing.tree.TreeModel[]
 */
public void setTreeModels(LiteBaseTreeModel[] models) 
{
	treeModels = models;

	if( getSortByComboBox().getModel().getSize() > 0 )
		getSortByComboBox().removeAllItems();
	
	for( int i = 0; i < treeModels.length; i++ )
	{
		getSortByComboBox().addItem( treeModels[i] );
	}
	
	if( treeModels.length > 0 )
	{
		getTree().setModel( treeModels[0] );
		getSortByComboBox().setSelectedItem( treeModels[0] );	
	}
	else
	{
		getTree().setModel( new com.cannontech.database.model.NullDBTreeModel() );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:55:28 PM)
 * @param changeType int
 */
public void treeObjectDelete( LiteBase lb )
{
	getCurrentTreeModel().removeTreeObject( lb );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:55:28 PM)
 * @param changeType int
 * @return boolean true if the LiteBase object is found in the current model
 */
public boolean treeObjectInsert( LiteBase lb )
{
	if( lb == null )
		return false;
	
	LiteBaseTreeModel model = getCurrentTreeModel();

	return getCurrentTreeModel().insertTreeObject( lb );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:55:28 PM)
 * @param changeType int
 */
public void treeObjectUpdated( LiteBase lb )
{
	getCurrentTreeModel().updateTreeObject( lb );
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:19:42 PM)
 * @param event javax.swing.event.TreeExpansionEvent
 */
public void treeWillCollapse(TreeExpansionEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:19:42 PM)
 * @param event javax.swing.event.TreeExpansionEvent
 */
public void treeWillExpand(TreeExpansionEvent event) 
{

	//getCurrentTreeModel().getRoot()
	TreePath rootPath = new TreePath( getCurrentTreeModel().getRoot() );

	if( !event.getPath().equals(rootPath) )
	{
		getCurrentTreeModel().treePathWillExpand( event.getPath() );
	}

}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
public void undoLastSelection(boolean val) {
	this.undoLastSelection = val;
}
}
