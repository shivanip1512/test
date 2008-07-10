package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (8/3/00 11:51:58 AM)
 * @author: 
 */
public interface SpecialTDCChild {
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// Takes a JComponent and adds an ActionListener to it
void addActionListenerToJComponent( javax.swing.JComponent component );/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
void executeRefreshButton();/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:51:45 AM)
 * @return javax.swing.table.TableModel
 */
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// This method is used to export a set of data to a file
void exportDataSet();
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
java.awt.Font getFont();
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 * @return javax.swing.JButton[]
 */

// this array of JButtons should represent the buttons that
//   get inserted into the toolbar of TDC
javax.swing.JButton[] getJButtons();
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:56:13 AM)
 * @return java.lang.String
 */

// Overide this method so TDC knows what the JLabel should display
// for the JCombo box that normally displays "Display:"
String getJComboLabel();
javax.swing.JTable[] getJTables();/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 9:18:52 AM)
 * @return javax.swing.JPanel
 */
// This method must be called in order to see the
//   SpecailTDCChild(), the panel returned is the one
//   used to replace TDC's JTable
   
javax.swing.JPanel getMainJPanel();
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:56:13 AM)
 * @return java.lang.String
 */

// Overide this method so TDC knows what the name of the special child is
String getName();

//a method used to fully init this object
void initChild();

/**
 * Insert the method's description here.
 * Creation date: (8/14/00 4:26:01 PM)
 * @return java.lang.String
 */
String getVersion();
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
void print();
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
void printPreview();
// Takes a JComponent and an removes the specified ActionListener
void removeActionListenerFromJComponent( javax.swing.JComponent component );/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 12:55:52 PM)
 * @param soundToggle boolean
 */
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
void setTableFont( java.awt.Font font );

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
// This method should handle the option of having
//   the vertical and horizontal grid lines on a JTable set or not,
//   if a JTable is present
public void setGridLines(boolean hGridLines, boolean vGridLines );
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
void setInitialTitle();
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 * @return javax.swing.JButton[]
 */
// this method should be used to set the array of JButtons
// in the toolbar of TDC
void setJButtons(javax.swing.JButton[] buttons);
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 1:32:55 PM)
 * @param colors java.awt.Color[]
 * @param colors java.awt.Color 
 */
void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor );


/**
 * Sets the display to use sound or not. 
 *   muteToggle    = TRUE sound is completely turned off 
 */
void setAlarmMute( boolean muteToggle );

/**
 * TRUE sound is turned off only for current alarms (new alarms play sound)
 */
void silenceAlarms();



/** 
 * Returns a boolean telling any callser that the ComboBox for this
 * display needs to go through the typical initialization process.  
 * @return boolean
 */
boolean needsComboIniting();
}
