package com.cannontech.common.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

import com.cannontech.common.gui.util.CtiTreeCellRenderer;

/**
 * @author rneuharth
 *
 * TreeRenderer that has a jcheckbox and label
 * 
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


    check.setBackground(java.awt.Color.white);
    label.setForeground(java.awt.Color.black);
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

	 //only allow edits for the non reserved roles
	 check.setEnabled( !((CheckNode)value).isSystemReserved() );
	 label.setEnabled( !((CheckNode)value).isSystemReserved() );

    label.setFont(tree.getFont());
    label.setText(stringValue);
    label.setSelected(isSelected);
    label.setFocus(hasFocus);

	 label.setIcon(null);
	
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
    int height = 0;  //use the smallest componenets height
    
    
    if( d_check.height < d_label.height ) 
    {
      height = d_check.height;
      y_check = (d_label.height - d_check.height)/2;
    }
    else
    {
      height = d_label.height;
      y_label = (d_check.height - d_label.height)/2;
    }

    check.setLocation(0, y_check);
    check.setBounds(0, y_check, d_check.width, height);
    
    label.setLocation(d_check.width, y_label);    
    label.setBounds(
    		d_check.width,
    		y_label,
    		d_label.width,
    		height);
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
        	Color fg = null, bg = null;
        	 
        	
         if( isSelected ) 
         {
            bg = java.awt.Color.blue;
            fg = java.awt.Color.white;
         } 
         else 
         {
            bg = java.awt.Color.white;
            fg = java.awt.Color.black;
         }
         
         
			if( !isEnabled() ) 
			{
				fg = java.awt.Color.LIGHT_GRAY;				
			}
          
          Dimension d = getPreferredSize();


	    g.setFont(getFont());
	    
	    //bg stuff
	    g.setColor(bg);
	    g.fillRect(0, 0, d.width, d.height);
	    
	    //fg stuff
	    g.setColor(fg);
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
