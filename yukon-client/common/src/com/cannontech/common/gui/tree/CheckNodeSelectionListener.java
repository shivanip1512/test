package com.cannontech.common.gui.tree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
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


		//only do events if the actual check box is selected
		if( !isCheckBoxSelected(e.getPoint(), tree.getRowBounds(row)) )
			return;

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

     	super.mouseClicked( e );
    }
    


	/**
	 * takes a mouse clicks point and the rows visible rectangle attribute to know
	 */
	private boolean isCheckBoxSelected( Point pt, Rectangle visRowRect_ )
	{
		if( visRowRect_ == null || pt == null )
			return false;


		JPanel rend = (JPanel)tree.getCellRenderer();		
		int checkW = 0;
		for( int i = 0; i < rend.getComponentCount(); i++ )
			if( rend.getComponent(i) instanceof JCheckBox )
			{
				checkW = rend.getComponent(i).getWidth();
				break;
			}


		return 
			pt.getX() >= visRowRect_.getLocation().getX()
			&& pt.getX() <= (checkW + visRowRect_.getLocation().getX());		
	}

	private void selectNode( CheckNode node, boolean selected, int row )
	{		
		node.setSelected( selected );

		((DefaultTreeModel)tree.getModel()).nodeChanged(node);
		
   }

	
}
