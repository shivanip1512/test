package com.cannontech.common.gui.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
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


int cnt = ((JPanel)tree.getCellRenderer()).getComponentCount();
for( int i = 0; i < cnt; i++ )
	 System.out.println("   cmp=("+i+") " + 
	 	((JPanel)tree.getCellRenderer()).getComponent(i).getLocation() );
	 
//System.out.println("   y=" + y );
System.out.println("   x=" + 
		((JPanel)tree.getCellRenderer()).getComponentAt(
			e.getPoint()) );
		
System.out.println("   y=" + 
		((JPanel)tree.getCellRenderer()).getComponentCount() );

System.out.println("   l=" + 
		((JPanel)tree.getCellRenderer()).getLocation() );
		
System.out.println("   pt=" + e.getPoint() );

System.out.println();


		if( tree.getCellRenderer() instanceof JPanel )
			((JPanel)tree.getCellRenderer()).getLocation();

      if( path != null
      	 && path.getLastPathComponent() instanceof CheckNode ) 
      {
        CheckNode node = (CheckNode)path.getLastPathComponent();

                
         boolean doSelect = !node.isSelected();
         
			selectNode( node, doSelect, row );

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
