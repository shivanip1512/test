package com.cannontech.common.gui.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CheckNodeSelectionListener extends MouseAdapter 
{
    private JTree tree = null;
    
    public CheckNodeSelectionListener(JTree tree) 
    {
    	super();
      this.tree = tree;
    }
    
    public void mouseClicked( MouseEvent e ) 
    {
      int x = e.getX();
      int y = e.getY();
      int row = tree.getRowForLocation(x, y);
      TreePath  path = tree.getPathForRow(row);
      //TreePath  path = tree.getSelectionPath();

      if( path != null
      	 && path.getLastPathComponent() instanceof CheckNode ) 
      {
        CheckNode node = (CheckNode)path.getLastPathComponent();
                
         boolean doSelect = !node.isSelected();
         
			selectNode( node, doSelect, row );

			for( int i = 0; i < node.getChildCount(); i++ )
			{
				if( node.getChildAt(i) instanceof CheckNode )
					selectNode( 
						(CheckNode)node.getChildAt(i),
						doSelect,
						row );
			}

			// I need revalidate if node is root.  but why?
			if (row == 0) 
			{
			  tree.revalidate();
			  tree.repaint();
			}        
      }
      else
      	super.mouseClicked( e );
    }
    

	private void selectNode( CheckNode node, boolean selected, int row )
	{		
		node.setSelected( selected );

		((DefaultTreeModel)tree.getModel()).nodeChanged(node);
		
    }
    
}
