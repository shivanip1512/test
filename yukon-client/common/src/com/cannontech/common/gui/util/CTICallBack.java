package com.cannontech.common.gui.util;

/**
 * @author rneuharth
 * Aug 26, 2002 at 3:50:37 PM
 * 
 * The purpose of this interface is to have GUI containers implement it
 * and then have inner GUI components call the call back function into
 * the GUI container.
 * 
 * Example:  
 *    JDialog dialogD implements CTICallBack
 * and a list on dialogD has a popup menu. We can pass
 * dialogD as a CTICallBack object into the popup menu.
 * This is better than passing the entire JDialog class signature
 * into the popup menu.
 */
public interface CTICallBack
{

   void ctiCallBackAction( java.beans.PropertyChangeEvent pEvent );

}
