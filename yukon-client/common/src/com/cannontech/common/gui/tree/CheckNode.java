package com.cannontech.common.gui.tree;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CheckNode extends DefaultMutableTreeNode 
{
//  public final static int SINGLE_SELECTION = 0;
//  public final static int DIG_IN_SELECTION = 4;
//  protected int selectionMode;
  
  protected boolean isSelected;

  public CheckNode() {
    this(null);
  }

  public CheckNode(Object userObject) {
    this(userObject, true, false);
  }

  public CheckNode(Object userObject, boolean allowsChildren
                                    , boolean isSelected) {
    super(userObject, allowsChildren);
    this.isSelected = isSelected;
//    setSelectionMode(DIG_IN_SELECTION);
  }

/*
  public void setSelectionMode(int mode) {
    selectionMode = mode;
  }

  public int getSelectionMode() {
    return selectionMode;
  }
*/

  public void setSelected(boolean isSelected) 
  {
    this.isSelected = isSelected;
    
//    if( (selectionMode == DIG_IN_SELECTION)
//         && (children != null)) 
    if( children != null ) 
	 {
      Enumeration enum = children.elements();      
      while (enum.hasMoreElements()) 
      {
        CheckNode node = (CheckNode)enum.nextElement();
        node.setSelected(isSelected);
      }
    }

  }
  
  public boolean isSelected() {
    return isSelected;
  }


  // If you want to change "isSelected" by CellEditor,
  /*
  public void setUserObject(Object obj) {
    if (obj instanceof Boolean) {
      setSelected(((Boolean)obj).booleanValue());
    } else {
      super.setUserObject(obj);
    }
  }
  */
}
