package com.cannontech.common.gui.util;

import javax.swing.JOptionPane;

public class RepeaterAddRemovePanel extends AddRemovePanel{

    /**
     * Return true if there is room to assign a repeater to this route
     * @return boolean
     */
    @Override
    public boolean validateAddAction() {
        int leftSelectedSize = getLeftList().getSelectedIndices().length;
        int rightListSize = getRightList().getModel().getSize();
        
        if( (leftSelectedSize + rightListSize) > 7 ) {
            displayFullRouteError();
            return false;
        }
        
        return true;
    }
    
    /**
     * Error pop up telling the user that the selected repeaters cannot be added to
     * the route because it already has 7 assigned
     */
    public void displayFullRouteError() {
        JOptionPane.showMessageDialog(this, "Only 7 repeaters can exist on a route!"
                                      ,"Assigned List Full", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}
