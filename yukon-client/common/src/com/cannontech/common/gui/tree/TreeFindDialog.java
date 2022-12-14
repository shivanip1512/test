package com.cannontech.common.gui.tree;

import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.OkCancelDialog;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TreeFindDialog extends OkCancelDialog
{
	private TreeFindPanel findPanel = null;
	private JTree findTree = null;
	private KeyEventDispatcher keyEvDisp = null;

	/**
	 * @param owner
	 */
	public TreeFindDialog( Frame owner, JTree srcTree )
	{
		super(owner, "Search", true );
		findPanel = new TreeFindPanel();
		setDisplayPanel( findPanel );
		setFindTree( srcTree );

		init();
	}
	
	private void init()
	{
		keyEvDisp = new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				//do the checks of the KeyEvent here
				if( e.getID() == KeyEvent.KEY_PRESSED
				 	&& e.getSource() == getFindTree() )
				{
					//do the checks of the keystrokes here
					if( e.getKeyCode() >= KeyEvent.VK_0
						 && e.getKeyCode() <= KeyEvent.VK_DIVIDE
						 && e.getModifiers() == 0
						 && !TreeFindDialog.this.isShowing() )
					{
						findPanel.setValue( new Character(e.getKeyChar()) );

						TreeFindDialog.this.setSize(250, 120);
						TreeFindDialog.this.setLocationRelativeTo( getFindTree().getParent() );
						TreeFindDialog.this.show();

						if( TreeFindDialog.this.getButtonPressed() == OkCancelDialog.OK_PRESSED )
						{

							Object value = findPanel.getValue(null);

							if( value != null )
							{
								boolean found = searchTreeString(value.toString());

								if( !found )
									JOptionPane.showMessageDialog(
										TreeFindDialog.this, "Unable to find your selected item", "Item Not Found",
										javax.swing.JOptionPane.INFORMATION_MESSAGE );
							}
						}

						TreeFindDialog.this.setVisible(false);
					}

				}
			
				//is this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		};
			
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( keyEvDisp );

	}


	/**
	 * Removes our keyboard listener
	 */
	public void removeListener()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher( keyEvDisp );

		CTILogger.info("   ***TreeFindDilog keyboard listener removed");
	}

	/**
	 * @return
	 */
	public JTree getFindTree()
	{
		return findTree;
	}

	/**
	 * @param tree
	 */
	public void setFindTree(JTree tree)
	{
		findTree = tree;
	}

	/**
	 * Searches the ALL visible tree nodes starting from the Treepath that is 
	 * currently selected. The search algorithm used is BreadthFirst. This order:
	 *      1
	 *    / | \
	 *   2  3  4
	 *  /    \   \
	 * 5     6    7
	 * |   / | \
	 * 8   9 10 11
	 *
	 * @return boolean
	 */
	public boolean searchTreeString(String val)
	{
		if( val == null || val.length() <= 0 || !(getFindTree().getModel().getRoot() instanceof DefaultMutableTreeNode) )
			return false;

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)getFindTree().getModel().getRoot();
		
		//could get chunky!!
		Enumeration childEnum = rootNode.breadthFirstEnumeration();		
		TreePath startPath = getFindTree().getSelectionPath();
		boolean start = false;
		
		while( childEnum.hasMoreElements() )
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)childEnum.nextElement();
			
			if( startPath != null && !start ) {

				TreePath tp = new TreePath( node.getPath() );
				if( startPath.equals(tp) ) {
					start = true;
				}
			}
			else {

				String nodeStr = 
						(node.getUserObject().toString().length() < val.length()
							? node.getUserObject().toString()
							: node.getUserObject().toString().substring(0, val.length()) );
	
				//we found the string
				if( nodeStr.toLowerCase().indexOf(val.toLowerCase()) >= 0 )
				{
					TreePath path = new TreePath( node.getPath() );
				
					getFindTree().getSelectionModel().setSelectionPath( path );
					getFindTree().scrollPathToVisible( path );
					return true;
				}
			}
		}

//		if( val.length() > 0 )
//			if( searchFirstLevelString( val.substring(0, val.length()-1) ) )
//				return true;

		return false;
	}

}
