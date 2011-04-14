package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSelectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
   private javax.swing.JLabel ivjRepeaterLabel = null;
   private java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   private com.cannontech.common.gui.util.AddRemovePanel ivjRepeatersAddRemovePanel = null;
   private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   private boolean rightListDragging = false;
   
   private JLabel errorMessageLabel = null;
   
public RepeaterSelectPanel() {
   super();
   initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC1(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.fireInputUpdate();
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.fireInputUpdate();
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC3:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mousePressed(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC4:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseReleased(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC5:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
   return getPreferredSize();
   }
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
   return new Dimension(350, 200);
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRepeaterLabel() {
   if (ivjRepeaterLabel == null) {
      try {
         ivjRepeaterLabel = new javax.swing.JLabel();
         ivjRepeaterLabel.setName("RepeaterLabel");
         ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 14));
         ivjRepeaterLabel.setText("Select the repeater(s) to include in this route:");
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjRepeaterLabel;
}
/**
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRepeatersAddRemovePanel() {
   if (ivjRepeatersAddRemovePanel == null) {
      try {
         ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
         ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjRepeatersAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	Integer routeID = ((com.cannontech.database.data.route.RouteBase) val).getRouteID();

	   java.util.Vector repeaterVector = ((com.cannontech.database.data.route.CCURoute)val).getRepeaterVector();
	   repeaterVector.removeAllElements();

	   for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
	   {
	      com.cannontech.database.db.route.RepeaterRoute rRoute = new com.cannontech.database.db.route.RepeaterRoute();
	      rRoute.setRouteID(routeID);
	      rRoute.setDeviceID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
	      rRoute.setVariableBits(new Integer(7));
	      rRoute.setRepeaterOrder(new Integer( i + 1 ) );
	      
	      repeaterVector.addElement( rRoute );   
	   }
	   RouteUsageHelper routeBoss = new RouteUsageHelper();
	   RouteRole role = routeBoss.assignRouteLocation((CCURoute)val,null, null);
	   if( role.getDuplicates().isEmpty() ) {
	       ((CCURoute)val).getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
	       ((CCURoute)val).getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));
	       
	       int rptVarBit = role.getVarbit();

	       for (int j = 0; j < repeaterVector.size(); j++) {
	           RepeaterRoute rpt = ((RepeaterRoute) repeaterVector.get(j));
	           if (rptVarBit + 1 <= 7) rptVarBit++;
	           if (j+1 == repeaterVector.size()) rptVarBit = 7;  // Last repeater's variable bit is always lucky 7.
	           rpt.setVariableBits(new Integer(rptVarBit));
	       }
	       
	   }else {  // All route combinations have been used,  suggest a suitable role combonation to reuse.
	       
	       RoleConflictDialog frame = new RoleConflictDialog(owner, role, (CCURoute)val, routeBoss);
	       frame.setLocationRelativeTo(this);
	       String choice = frame.getValue();
	       boolean finished = false;
	       int startingSpot = role.getFixedBit();
	       while(!finished) {
	           
	           if(choice == "Yes") {
	               finished = true;
	                ((CCURoute) val).getCarrierRoute().setCcuFixBits(new Integer(frame.getRole().getFixedBit()));
	                ((CCURoute) val).getCarrierRoute().setCcuVariableBits(new Integer(frame.getRole().getVarbit()));
	    
	                int rptVarBit = frame.getRole().getVarbit();
	    
	                for (int j = 0; j < repeaterVector.size(); j++) {
	                    RepeaterRoute rpt = ((RepeaterRoute) repeaterVector.get(j));
	                    if (rptVarBit + 1 <= 7) {
	                        rptVarBit++;
	                    }
	                    if (j + 1 == repeaterVector.size()) {
	                        rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
	                    }
	                    rpt.setVariableBits(new Integer(rptVarBit));
	                }
	           }else if(choice == "Cancel") {
	                   finished = true;
	                   return null;
	           }else {
	               finished = true;
	               return null;
	           }
	       }
	       
	   }
	   
	   return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

   /* Uncomment the following lines to print uncaught exceptions to stdout */
    com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
    com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
   // user code begin {1}
   // user code end
   getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
   try {
      // user code begin {1}
      // user code end
      setName("RepeaterSelectPanel");
      setLayout(new java.awt.GridBagLayout());
      setSize(370, 243);

      java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
      constraintsRepeaterLabel.gridx = 1; constraintsRepeaterLabel.gridy = 1;
      constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
      constraintsRepeaterLabel.insets = new java.awt.Insets(5, 0, 0, 0);
      add(getRepeaterLabel(), constraintsRepeaterLabel);

      java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
      constraintsRepeatersAddRemovePanel.gridx = 1; constraintsRepeatersAddRemovePanel.gridy = 2;
      constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
      constraintsRepeatersAddRemovePanel.weightx = 1.0;
      constraintsRepeatersAddRemovePanel.weighty = 1.0;
      add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);
      
      java.awt.GridBagConstraints constraintsRepeaterErrorLabel = new GridBagConstraints();
      constraintsRepeaterErrorLabel.gridx = 1;
      constraintsRepeaterErrorLabel.gridy = 3;
      constraintsRepeaterErrorLabel.gridwidth = 2;
      constraintsRepeaterErrorLabel.anchor = GridBagConstraints.WEST;
      constraintsRepeaterErrorLabel.insets = new java.awt.Insets(0, 2, 0, 2);
      add(getErrorMessageLabel(), constraintsRepeaterErrorLabel);
      
      initConnections();
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
   // user code begin {2}
   // user code end
}

private JLabel getErrorMessageLabel() {
    if (errorMessageLabel == null) {
        try {
            errorMessageLabel = new javax.swing.JLabel();
            errorMessageLabel.setName("ErrorMessageLabel");
            errorMessageLabel.setFont(new Font("Arial", 1, 10));
            errorMessageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return errorMessageLabel;
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
    ListModel rightListModel = getRepeatersAddRemovePanel().rightListGetModel();
    if( rightListModel.getSize() < 1 ) {
       setErrorString("One or more repeaters should be selected");
       getErrorMessageLabel().setText(getErrorString());
       return false;
    }
    
    for( int i = 0; i < rightListModel.getSize(); i++ ) {
                
       LiteYukonPAObject pao = (LiteYukonPAObject)rightListModel.getElementAt(i);
       
       if(pao.getPaoIdentifier().getPaoType() == PaoType.REPEATER_850) {
           if(i != rightListModel.getSize()-1) {
               setErrorString("When present, a Repeater 850 MUST be the last repeater in the repeater chain");
               getErrorMessageLabel().setText(getErrorString());
               return false;
           }
       }
    }

    getErrorMessageLabel().setText("");
    return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
   try {
      java.awt.Frame frame = new java.awt.Frame();
      RepeaterSelectPanel aRepeaterSelectPanel;
      aRepeaterSelectPanel = new RepeaterSelectPanel();
      frame.add("Center", aRepeaterSelectPanel);
      frame.setSize(aRepeaterSelectPanel.getSize());
      frame.addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
         };
      });
      frame.setVisible(true);
   } catch (Throwable exception) {
      System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
      com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
   }
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC2(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
   rightListItemIndex = -1;
   rightListDragging = false;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
   rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   rightListDragging = true;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
   int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

   if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
   {

      Object itemSelected = new Object();
      java.util.Vector destItems = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1 );

      for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
         destItems.addElement( getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i) );

      itemSelected = destItems.elementAt( rightListItemIndex );
      destItems.removeElementAt( rightListItemIndex );
      destItems.insertElementAt( itemSelected, indexSelected );
      getRepeatersAddRemovePanel().rightListSetListData(destItems);

      getRepeatersAddRemovePanel().revalidate();
      getRepeatersAddRemovePanel().repaint();

      // reset the values
      rightListItemIndex = -1;
      fireInputUpdate();
   }

   rightListDragging = false;

   return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC5(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC3(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC4(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
   IDatabaseCache cache =
               com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   java.util.Vector allRepeaters = null;
   synchronized(cache)
   {
      List<LiteYukonPAObject> allDevices = cache.getAllDevices();
      allRepeaters = new java.util.Vector();
      for (LiteYukonPAObject liteYukonPAObject : allDevices) {
         if( DeviceTypesFuncs.isRepeater(liteYukonPAObject.getPaoType().getDeviceTypeId())) {
            allRepeaters.add(liteYukonPAObject);
         }
      }
   }

   com.cannontech.common.gui.util.AddRemovePanel repeatersPanel = getRepeatersAddRemovePanel();
   repeatersPanel.setMode(repeatersPanel.TRANSFER_MODE);
   repeatersPanel.leftListRemoveAll();
   repeatersPanel.rightListRemoveAll();

   repeatersPanel.leftListSetListData(allRepeaters);
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getRepeatersAddRemovePanel().requestFocus();
        } 
    });    
}

}