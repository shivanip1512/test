package com.cannontech.common.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.cannontech.common.gui.util.CtiTreeCellRenderer;
import com.cannontech.common.gui.util.SimpleLabel;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CheckRenderer extends JPanel implements TreeCellRenderer
{

  protected JCheckBox check;
  protected TreeLabel label;

  protected static final CtiTreeCellRenderer defaultRenderer = 
  			new CtiTreeCellRenderer();
  
  public CheckRenderer() 
  {
    setLayout(null);
    add(check = new JCheckBox());
    add(label = new TreeLabel());
    check.setBackground(UIManager.getColor("Tree.textBackground"));
    label.setForeground(UIManager.getColor("Tree.textForeground"));
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
               boolean isSelected, boolean expanded,
               boolean leaf, int row, boolean hasFocus) 
  {

	if( !(value instanceof CheckNode) )  	
		return defaultRenderer.getTreeCellRendererComponent( 
					  tree,
					  value,
                 isSelected,
                 expanded,
                 leaf,
                 row,
                 hasFocus);
  	
    String  stringValue = tree.convertValueToText(value, isSelected,
			expanded, leaf, row, hasFocus);

    setEnabled(tree.isEnabled());
    
    
	 check.setSelected( ((CheckNode)value).isSelected() );    	

    label.setFont(tree.getFont());
    label.setText(stringValue);
    label.setSelected(isSelected);
    label.setFocus(hasFocus);

	 label.setIcon(null);
/*	
    if (leaf) {
      label.setIcon(UIManager.getIcon("Tree.leafIcon"));
    } else if (expanded) {
      label.setIcon(UIManager.getIcon("Tree.openIcon"));
    } else {
      //label.setIcon(UIManager.getIcon("Tree.closedIcon"));
      label.setIcon(null);
    }
*/
    return this;
  }
  
  public Dimension getPreferredSize() 
  {
    Dimension d_check = check.getPreferredSize();
    Dimension d_label = label.getPreferredSize();
    return new Dimension(
    		d_check.width + d_label.width,
      		(d_check.height < d_label.height ?
       		d_label.height : d_check.height) );
  }
  
  public void doLayout() 
  {
    Dimension d_check = check.getPreferredSize();
    Dimension d_label = label.getPreferredSize();
    int y_check = 0;
    int y_label = 0;
    
    if (d_check.height < d_label.height) 
    {
      y_check = (d_label.height - d_check.height)/2;
    } else {
      y_label = (d_check.height - d_label.height)/2;
    }
    check.setLocation(0,y_check);
    check.setBounds(0,y_check,d_check.width,d_check.height);
    label.setLocation(d_check.width,y_label);    
    label.setBounds(
    		d_check.width,
    		y_label,
    		d_label.width,
    		d_label.height);    
  }
   
  
  public void setBackground(Color color) 
  {
    if (color instanceof ColorUIResource)
      color = null;
    super.setBackground(color);
  }
  
    
  public class TreeLabel extends JLabel 
  {
    boolean isSelected;
    boolean hasFocus;
    
    public TreeLabel() {
    	super();
    }
        
    public void setBackground(Color color) 
    {
		if(color instanceof ColorUIResource)
		    color = null;
		super.setBackground(color);
    } 
         
    public void paint(Graphics g) 
    {
      String str;
      if ((str = getText()) != null) 
      {
        if (0 < str.length()) 
        {
        	
          if (isSelected) 
          {
            g.setColor(UIManager.getColor("Tree.selectionBackground"));
          } 
          else 
          {
            g.setColor(UIManager.getColor("Tree.textBackground"));
          }
          
          Dimension d = getPreferredSize();


	    g.setFont(getFont());
//	    g.setColor(background);
	    g.fillRect(0, 0, d.width, d.height);
	    g.setColor(getForeground());
	    g.drawString(str, 3, d.height - 5);

/*
          int imageOffset = 0;
          Icon currentI = getIcon();
          if (currentI != null) {
            imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
          }
          
          g.fillRect(imageOffset, 0, 
          	d.width -1 - imageOffset, 
          	d.height);
          
          if (hasFocus) 
          {
            g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
            g.drawRect(imageOffset, 0, 
            		d.width -1 - imageOffset, 
            		d.height -1);     
         }
*/
        }
      }
      
      
//      super.paint(g);
    }
  
    public Dimension getPreferredSize() 
    {
      Dimension retDimension = super.getPreferredSize();
      if (retDimension != null) {
        retDimension = new Dimension(
        		 retDimension.width + 3,
				 retDimension.height + 3);
      }
      return retDimension;
    }
    
    public void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }
    
    public void setFocus(boolean hasFocus) {
      this.hasFocus = hasFocus;
    }
  }
  
}
