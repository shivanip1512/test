/*
 * Created on Nov 14, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.gui;

import javax.swing.JTree;

import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.common.gui.util.TreeViewPanel;

/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CheckBoxJTreePanel extends TreeViewPanel{
	private JTree tree;
	
	
	public javax.swing.JTree getTree() {
		if (tree == null) {
			try {
				tree = new javax.swing.JTree();
				tree.setName("JTreeCheckBoxes");
				tree.setBounds(0, 0, 165, 243);
				// user code begin {1}
			
				//DefaultMutableTreeNode root = 
				//	new DefaultMutableTreeNode("Role Categories");

				//tree.setModel( new CTITreeModel(root) );			
				tree.setCellRenderer( new CheckRenderer() );
				//tree.setRootVisible( false );

				//DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
				/*synchronized( cache )
				{
					List roles = cache.getAllYukonRoles();
					Collections.sort( roles, LiteComparators.liteRoleCategoryComparator );
					String tmpCat = null;
					DefaultMutableTreeNode categoryParent = null;
				
					for( int i = 0; i < roles.size(); i++ )
					{
						LiteYukonRole role = (LiteYukonRole)roles.get(i);

						if( !role.getCategory().equalsIgnoreCase(tmpCat) )
						{
							tmpCat = role.getCategory();
							categoryParent = new DefaultMutableTreeNode(tmpCat);
							root.add( categoryParent );
						}
						
						categoryParent.add( new LiteBaseNode(role) );					
					}
				
				}*/
			
				//expand the root
				//tree.expandPath( new TreePath(root.getPath()) );

				//tree.addMouseListener( getNodeListener() );

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				//handleException(ivjExc);
			}
		}
		return tree;
	}

}