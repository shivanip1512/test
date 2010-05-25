package com.cannontech.common.gui.util;

import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Sets;

/**
 * AddRemovePanel which does macro group specific validation
 */
public class MacroGroupAddRemovePanel extends AddRemovePanel {

    private DisplayablePao currentMacroGroup;
    
    public void setCurrentMacroGroup(DisplayablePao currentMacroGroup) {
        this.currentMacroGroup = currentMacroGroup;
    }
    
    @Override
    public boolean validateAddAction() {
        
        Set<PaoIdentifier> parentMacroGroups = 
            getAllParentMacroGroups(currentMacroGroup.getPaoIdentifier());
        
        Object[] selectedObjects = getLeftList().getSelectedValues();
        
        for(Object object : selectedObjects) {
            LiteYukonPAObject group = (LiteYukonPAObject) object;
            PaoIdentifier groupIdentifier = group.getPaoIdentifier();
            
            // If this group is a macro group and is in the parent list, we 
            // have a circular macro group reference
            if(PaoType.MACRO_GROUP.equals(groupIdentifier.getPaoType())
                    && parentMacroGroups.contains(groupIdentifier)) {

                JOptionPane.showMessageDialog(this, 
                                              group.getPaoName() 
                                              + " is a parent macro group of " 
                                              + currentMacroGroup.getName()
                                              + ". You cannot circularly nest macro groups.",
                                              "Circular reference", 
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Recursive helper method to get all of the macroGroups ancestor macroGroups
     * @param macroGroup - Macro group to get ancestors for
     * @return List of all ancestor macro groups
     */
    private Set<PaoIdentifier> getAllParentMacroGroups(PaoIdentifier macroGroup) {
        
        LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
        
        // Get parents
        Set<PaoIdentifier> parentMacroSet = Sets.newHashSet();
        List<PaoIdentifier> parentMacroGroups = loadGroupDao.getParentMacroGroups(macroGroup);
        parentMacroSet.addAll(parentMacroGroups);
        
        // Recursively get parent's parents
        for (PaoIdentifier paoIdentifier : parentMacroGroups) {
            parentMacroSet.addAll(getAllParentMacroGroups(paoIdentifier));
        }
        
        return parentMacroSet;
        
    }
    
}
