package com.cannontech.common.editor;

/**
 * Insert the type's description here.
 * Creation date: (5/20/2002 1:40:07 PM)
 * @author: 
 */
public interface IMultiPanelEditor {
/**
 * Insert the method's description here.
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
Object[] createNewPanel(int panelIndex);
}
