package com.cannontech.dbeditor.editor.route;

import static javax.swing.JOptionPane.showMessageDialog;

import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RegenerateRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.dbeditor.editor.regenerate.RouteRole;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSetupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener {
   private javax.swing.JButton ivjAdvancedSetupButton = null;
   private JLabel idiotLabel = null;
   private JLabel ccuOrderLabel = null;
   private AdvancedRepeaterSetupEditorPanel advancedRepeaterSetupEditorPanel = null;
   private Object objectToEdit = null;
   private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   private java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   private boolean rightListDragging = false;
   private com.cannontech.common.gui.util.RepeaterAddRemovePanel ivjRepeatersAddRemovePanel = null;
   private boolean addOrRemoveHasBeenDone = false;
   private boolean changeUpdated = true;
   private boolean dbRegenerate = false;
   private boolean advancedSettingsDone = false;
   private AdvancedRouteSetupDialog frame = null;

/*
  * Constructor
 WARNING: THIS METHOD WILL BE REGENERATED.
  */
public RepeaterSetupEditorPanel() {
   super();
   initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
   if (e.getSource() == getAdvancedSetupButton()) 
      connEtoC3(e);
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
    if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
        connEtoC4(newEvent);
    }
}
/**
 * Comment
 */
public void advancedSetupButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

    java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   
    frame = new AdvancedRouteSetupDialog(owner,(CCURoute)this.objectToEdit);
    frame.setLocationRelativeTo(this);
    String choice = frame.getValue();
   
    if(choice.equalsIgnoreCase("OK")) {
        advancedSettingsDone = true;
        dbRegenerate = true;
        fireInputDataPanelEvent( new PropertyPanelEvent(this,PropertyPanelEvent.EVENT_FORCE_APPLY));
        fireInputUpdate();
        setValue(this.objectToEdit);
    }
   
//   //This makes sure that the user applies their changes before bringing up the advanced setup dialogue
//      StringBuffer message = new StringBuffer("Advanced Setup may not accurately reflect current status \n" + 
//   											"unless your latest changes are applied.  Do you want to \n" + 
//   											"permanently apply your changes now?");
//   if(addOrRemoveHasBeenDone && !changeUpdated)
//   	{
//   		int optional = 
//   				javax.swing.JOptionPane.showConfirmDialog(
//   						this, 
//   						message,
//                     "Changes not applied.",
//                     JOptionPane.YES_NO_OPTION,
//                     JOptionPane.WARNING_MESSAGE);
//               
//     
//        addOrRemoveHasBeenDone = false;
//        if(optional == JOptionPane.YES_OPTION)
//        {
//			/*start of the chain that leads eventually out to the DatabaseEditor class
//			 * in order to simulate an apply button click so that the db is updated
//			 */
//		
//			fireInputDataPanelEvent( new PropertyPanelEvent(
//						this, 
//						PropertyPanelEvent.EVENT_FORCE_APPLY));
//       }
//       else {
//       	dbRegenerate = false;
//   		}
//         	       	
//   }
//      	
//   getValue(this.objectToEdit);
//   getAdvancedRepeaterSetupEditorPanel().setValue(this.objectToEdit);
//   	
//   
//   com.cannontech.common.gui.util.BooleanDialog b = new com.cannontech.common.gui.util.BooleanDialog(getAdvancedRepeaterSetupEditorPanel(), owner);
//   b.yesButtonSetText("Ok");
//   b.noButtonSetText("Cancel");
//   b.setTitle("Advanced Repeater Setup");
//   b.setLocationRelativeTo(this);
//   b.setSize( new java.awt.Dimension(445, 485) );
//
//   if ( b.getValue() )
//   {
//   		if(getAdvancedRepeaterSetupEditorPanel().isDataCorrect(this.objectToEdit))
//   		{
//   			getAdvancedRepeaterSetupEditorPanel().getValue(this.objectToEdit);
//			message = new StringBuffer("Changes made in the Advanced Setup tab will immediately be downloaded to the field device.  \n" +
//                    "Click 'Yes' to save and download these changes. Click 'No' to return to the main window.");
//			int optional = javax.swing.JOptionPane.showConfirmDialog(
//							this, 
//							message,
//						 "Apply new changes?",
//						 JOptionPane.YES_NO_OPTION,
//						 JOptionPane.QUESTION_MESSAGE);
//			if(optional == JOptionPane.YES_OPTION)
//				fireInputDataPanelEvent( new PropertyPanelEvent(
//							this, 
//							PropertyPanelEvent.EVENT_FORCE_APPLY));
//			fireInputUpdate();
//			setValue(this.objectToEdit);
//   		}
//      	else
//   		{
//			message = new StringBuffer("Advanced Setup has detected duplicate variable bits.  Duplicate variable bits are not permitted\n" +
//										"on the same route.  Setup has been forced to return to your previous variable bit values. \n" + 
//										"Please re-edit Advanced Setup appropriately and avoid duplication of variable bits.");
//			JOptionPane.showMessageDialog(getAdvancedRepeaterSetupEditorPanel(), message, "CONFLICTING BIT VALUES", JOptionPane.ERROR_MESSAGE); 
//			getAdvancedSetupButton().doClick(); 
//   		}
//   }
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (AdvancedSetupButton.action.actionPerformed(java.awt.event.ActionEvent) --> RepeaterSetupEditorPanel.advancedSetupButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
   try {
      // user code begin {1}
      // user code end
      this.advancedSetupButton_ActionPerformed(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
	if(ivjExc instanceof java.lang.NullPointerException )
	{
		StringBuffer error = new StringBuffer("Advanced Setup has detected a bit field value that is invalid. \n" + 
		"The variable bit fields accept values only between 0 and 7. \n " +
		"Previous values have been restored.");
		javax.swing.JOptionPane.showMessageDialog(this, error, "BIT VALUE OUT OF RANGE", javax.swing.JOptionPane.ERROR_MESSAGE);
		getAdvancedSetupButton().doClick();
	}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC4(java.util.EventObject arg1) {
   try {
      this.fireInputUpdate();
      getAdvancedSetupButton().setEnabled(false);
      getIdiotLabel().setVisible(true);
      addOrRemoveHasBeenDone = true;
      changeUpdated = false;
      dbRegenerate = true;
   
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
}
/**
 * connEtoC5:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC5(java.util.EventObject arg1) {
   try {
       
      this.fireInputUpdate();
      getAdvancedSetupButton().setEnabled(false);
      getIdiotLabel().setVisible(true);
      addOrRemoveHasBeenDone = true;
      changeUpdated = false;
      dbRegenerate = true;
     
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
}
/**
 * connEtoC6:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
private void connEtoC6(java.util.EventObject arg1) {
   try {
      this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected AdvancedRepeaterSetupEditorPanel getAdvancedRepeaterSetupEditorPanel() {
   if( advancedRepeaterSetupEditorPanel == null )
      advancedRepeaterSetupEditorPanel = new AdvancedRepeaterSetupEditorPanel();
      
   return advancedRepeaterSetupEditorPanel;
}
/**
 * Return the AdvancedSetupButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAdvancedSetupButton() {
   if (ivjAdvancedSetupButton == null) {
      try {
         ivjAdvancedSetupButton = new javax.swing.JButton();
         ivjAdvancedSetupButton.setName("AdvancedSetupButton");
         ivjAdvancedSetupButton.setText("Advanced Setup...");
         ivjAdvancedSetupButton.setMaximumSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setActionCommand("Advanced Setup >>");
         ivjAdvancedSetupButton.setPreferredSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setFont(new java.awt.Font("dialog", 0, 14));
         ivjAdvancedSetupButton.setMinimumSize(new java.awt.Dimension(159, 27));
         ivjAdvancedSetupButton.setEnabled(false);
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjAdvancedSetupButton;
}

private JLabel getIdiotLabel() {
    if ( idiotLabel == null ) {
        idiotLabel = new JLabel();
        idiotLabel.setText("* Apply changes to enable advanced settings options.");
        idiotLabel.setVisible(false);
    }
    return idiotLabel;
}

private JLabel getCcuOrderLabel() {
    if ( ccuOrderLabel == null ) {
        ccuOrderLabel = new JLabel();
        ccuOrderLabel.setText("Repeater Order: Closest to CCU on top.");
        ccuOrderLabel.setFont(new java.awt.Font("Arial", 1, 12));
    }
    return ccuOrderLabel;
}

/**
 * Return the RepeaterAddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
private com.cannontech.common.gui.util.RepeaterAddRemovePanel getRepeatersAddRemovePanel() {
   if (ivjRepeatersAddRemovePanel == null) {
      try {
         ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.RepeaterAddRemovePanel();
         ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
         ivjRepeatersAddRemovePanel.setMode(com.cannontech.common.gui.util.RepeaterAddRemovePanel.TRANSFER_MODE);
         ivjRepeatersAddRemovePanel.setRightListMax( new Integer(7) );
      } catch (java.lang.Throwable ivjExc) {
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
@SuppressWarnings({ "cast", "unchecked" })
public Object getValue(Object val) {
	com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;
	
	   //Build up an assigned repeaterRoute Vector
	   java.util.Vector repeaterRouteVector = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() );
	   Integer deviceID = null;
	   for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
	   {
	      deviceID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

	      com.cannontech.database.db.route.RepeaterRoute rr = new com.cannontech.database.db.route.RepeaterRoute(
	                                                                           route.getRouteID(),
	                                                                           deviceID,
	                                                                           new Integer(7),
	                                                                           new Integer(i+1) );
	      
	      repeaterRouteVector.addElement(rr);
	   }

	   if ( !route.getRepeaterVector().isEmpty() )
	   {
	      for( int i = 0; i < repeaterRouteVector.size(); i++ )
	      {
	         for ( int j = 0; j < route.getRepeaterVector().size(); j++ )
	         {
	            if ( ((RepeaterRoute)route.getRepeaterVector().elementAt(j)).getDeviceID().equals(((RepeaterRoute)repeaterRouteVector.elementAt(i)).getDeviceID()) )
	            {
	               ((RepeaterRoute)repeaterRouteVector.elementAt(i)).setVariableBits(((RepeaterRoute)route.getRepeaterVector().elementAt(j)).getVariableBits() );
	               break;
	            }
	         }
	      }
	   }
	   if(getRepeatersAddRemovePanel().getRightList().getModel().getSize() == 0) {
	       dbRegenerate = false;
	       ((CCURoute)val).getCarrierRoute().setCcuFixBits(new Integer(31));
	       ((CCURoute)val).getCarrierRoute().setCcuVariableBits(new Integer(7));
	   }

	   route.setRepeaterVector(repeaterRouteVector);
	   if (dbRegenerate) {
	       if(!advancedSettingsDone) {
	           String userLocked = route.getCarrierRoute().getUserLocked();
	           if (userLocked.equalsIgnoreCase("N")) {
	       	
	               Vector changingRoutes = new Vector(1);
	               changingRoutes.add((CCURoute)val);
	               RegenerateRoute routeBoss = new RegenerateRoute();
	               routeBoss.removeChanginRoutes(changingRoutes);
	               
	               RouteRole role = routeBoss.assignRouteLocation((CCURoute)val,null, null);
	             if( role.getDuplicates().isEmpty() ) {
	                   ((CCURoute)val).getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
	                   ((CCURoute)val).getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));
	                   
	                   int rptVarBit = role.getVarbit();
	    
	                   for (int j = 0; j < repeaterRouteVector.size(); j++) {
	                       RepeaterRoute rpt = ((RepeaterRoute) repeaterRouteVector.get(j));
	                       if (rptVarBit + 1 <= 7) rptVarBit++;
	                       if (j+1 == repeaterRouteVector.size()) rptVarBit = 7;  // Last repeater's variable bit is always lucky 7.
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
	                
	                            for (int j = 0; j < repeaterRouteVector.size(); j++) {
	                                RepeaterRoute rpt = ((RepeaterRoute) repeaterRouteVector.get(j));
	                                if (rptVarBit + 1 <= 7) {
	                                    rptVarBit++;
	                                }
	                                if (j + 1 == repeaterRouteVector.size()) {
	                                    rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
	                                }
	                                rpt.setVariableBits(new Integer(rptVarBit));
	                            }
	                       }else if(choice == "Cancel") {
	                           return null;
	                       }else {
	                           finished = true;
	                           return null;
	                       }
	                   }
	               }
	               this.objectToEdit = val;
	           }
	           dbRegenerate = false;
	       }else {
	           // advanced settings panel has done the checking work.
	           int unMaskedFixedBit = frame.getUnMaskedFixedBit();
	           int varBit = frame.getVariableBit();
	           ((CCURoute)val).getCarrierRoute().setCcuFixBits(new Integer(unMaskedFixedBit));
	           ((CCURoute)val).getCarrierRoute().setCcuVariableBits(new Integer(varBit));
	           
	           for (int j = 0; j < repeaterRouteVector.size(); j++) {
	               RepeaterRoute rr = ((RepeaterRoute) repeaterRouteVector.get(j));
	               if (j+1 == repeaterRouteVector.size()) {
	                   varBit = 7;  // Last repeater's variable bit is always lucky 7.
	               }else {
	                   varBit = new Integer(frame.repeaterTextFieldArray[j].getText()).intValue();
	               }
	               
	               rr.setVariableBits(new Integer(varBit));
	           }
	           if( frame.getLockCheckBox().isSelected() ) {
	               ((CCURoute)val).getCarrierRoute().setUserLocked( "Y" );
	           }else {
	               ((CCURoute)val).getCarrierRoute().setUserLocked( "N" );
	           }
	           setValue(val);
	           this.objectToEdit = val;
	           advancedSettingsDone = false;
	           dbRegenerate = false;
	       }
	    }
	   
		changeUpdated = true;
	    if(getRepeatersAddRemovePanel().getRightList().getModel().getSize() > 0)
	    {
	        getAdvancedSetupButton().setEnabled(true);
	        
	    }
	    getIdiotLabel().setVisible(false);
	    
	    for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
        {
           LiteYukonPAObject liteYukonPAObject = (LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i);
           if(liteYukonPAObject.getPaoIdentifier().getPaoType() == PaoType.REPEATER_850) {
               showMessageDialog( this, 
                                  "This route contains a Repeater 850, it is recommended that " +
                                   "this repeater has no more than 10 devices connected.", 
                                  "Information", 
                                  javax.swing.JOptionPane.INFORMATION_MESSAGE );
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
    com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
   // user code begin {1}
   // user code end
   getAdvancedSetupButton().addActionListener(this);
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
      setName("RepeaterSetupEditorPanel");
      setLayout(new java.awt.GridBagLayout());
      setSize(382, 274);
      
      java.awt.GridBagConstraints constraintsCcuOrderLabel = new java.awt.GridBagConstraints();
      constraintsCcuOrderLabel.gridx = 0; constraintsCcuOrderLabel.gridy = 0;
      constraintsCcuOrderLabel.anchor = java.awt.GridBagConstraints.WEST;
      constraintsCcuOrderLabel.insets = new java.awt.Insets(5, 5, 5, 0);
      add(getCcuOrderLabel(), constraintsCcuOrderLabel);

      java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
      constraintsRepeatersAddRemovePanel.gridx = 0; constraintsRepeatersAddRemovePanel.gridy = 1;
      constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
      add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);

      java.awt.GridBagConstraints constraintsAdvancedSetupButton = new java.awt.GridBagConstraints();
      constraintsAdvancedSetupButton.gridx = 0; constraintsAdvancedSetupButton.gridy = 2;
      constraintsAdvancedSetupButton.anchor = java.awt.GridBagConstraints.EAST;
      constraintsAdvancedSetupButton.insets = new java.awt.Insets(10, 0, 0, 0);
      add(getAdvancedSetupButton(), constraintsAdvancedSetupButton);
      
      java.awt.GridBagConstraints constraintsIdiotLabel = new java.awt.GridBagConstraints();
      constraintsIdiotLabel.gridx = 0; constraintsIdiotLabel.gridy = 3;
      constraintsIdiotLabel.anchor = java.awt.GridBagConstraints.WEST;
      constraintsIdiotLabel.insets = new java.awt.Insets(5, 0, 0, 0);
      add(getIdiotLabel(), constraintsIdiotLabel);
      
      initConnections();
   } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
   }
   // user code begin {2}
   // user code end
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
      java.awt.Frame frame;
      try {
         Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
         frame = (java.awt.Frame)aFrameClass.newInstance();
      } catch (java.lang.Throwable ivjExc) {
         frame = new java.awt.Frame();
      }
      RepeaterSetupEditorPanel aRepeaterSetupEditorPanel;
      aRepeaterSetupEditorPanel = new RepeaterSetupEditorPanel();
      frame.add("Center", aRepeaterSetupEditorPanel);
      frame.setSize(aRepeaterSetupEditorPanel.getSize());
      frame.setVisible(true);
   } catch (Throwable exception) {
      System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
      com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
   }
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
    if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
        connEtoC5(newEvent);
    }
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC6(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC1(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC2(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected void setAdvancedRepeaterSetupEditorPanel(AdvancedRepeaterSetupEditorPanel newValue) {
   this.advancedRepeaterSetupEditorPanel = newValue;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
   this.objectToEdit = val;
   
   com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;     

   java.util.Vector repeaterRoutes = route.getRepeaterVector();
   java.util.Vector assignedRepeaters = new java.util.Vector();
   java.util.Vector availableRepeaters = new java.util.Vector();
   IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   synchronized(cache)
   {
      List<LiteYukonPAObject> devices = cache.getAllDevices();
      int repeaterRouteDeviceID;
      for(int i=0;i<repeaterRoutes.size();i++)
      {
    	  for (LiteYukonPAObject liteDevice : devices) {
            if( DeviceTypesFuncs.isRepeater(liteDevice.getPaoType().getDeviceTypeId()) )
            {
               repeaterRouteDeviceID = ((RepeaterRoute)repeaterRoutes.get(i)).getDeviceID().intValue();
               if( repeaterRouteDeviceID == liteDevice.getYukonID() )
               {
                  assignedRepeaters.addElement(liteDevice);
                  break;
               }
            }
         }
      }
      boolean alreadyAssigned = false;
      for (LiteYukonPAObject liteDevice : devices) {
         alreadyAssigned = false;
         if( DeviceTypesFuncs.isRepeater(liteDevice.getPaoType().getDeviceTypeId()) )
         {
            for(int j=0;j<assignedRepeaters.size();j++)
            {
               if( ((LiteYukonPAObject)assignedRepeaters.get(j)).getYukonID() ==
                     liteDevice.getYukonID() )
                  alreadyAssigned = true;
            }
            if( !alreadyAssigned )
            {
               availableRepeaters.addElement(liteDevice);
            }
         }
      }
   }

   getRepeatersAddRemovePanel().rightListSetListData( assignedRepeaters );
   getRepeatersAddRemovePanel().leftListSetListData( availableRepeaters );
   if(route.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y")) {
       getRepeatersAddRemovePanel().getAddButton().setEnabled(false);
       getRepeatersAddRemovePanel().getRemoveButton().setEnabled(false);
   }else {
       getRepeatersAddRemovePanel().getAddButton().setEnabled(true);
       getRepeatersAddRemovePanel().getRemoveButton().setEnabled(true);
   }
   if( assignedRepeaters.size() > 0 ) {
      getAdvancedSetupButton().setEnabled(true);
   }
}

    public boolean isInputValid() {
        for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
        {
           if(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getPaoIdentifier().getPaoType() == PaoType.REPEATER_850) {
               if(i != getRepeatersAddRemovePanel().rightListGetModel().getSize()-1) {
                   setErrorString("When present, a repeater 850 MUST be the last repeater in the repeater chain");
                   return false;
               }
           }
        }
        return true;
        
    }
}