package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (9/20/2001 2:58:05 PM)
 * @author: 
 */
public interface AdvancedPropertiesGUI 
{
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 2:58:21 PM)
 */
javax.swing.JPanel getMainJPanel();
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:55:11 PM)
 * @return java.lang.Object
 */
java.lang.Object getOriginalObject();
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 2:58:21 PM)
 * @param obj java.lang.Object
 */
Object getValue(Object obj);
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:55:11 PM)
 * @return java.lang.Object
 */
void setOriginalObject( Object value );
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 2:58:21 PM)
 * @param obj java.lang.Object
 */
void setValue(Object obj);
}
