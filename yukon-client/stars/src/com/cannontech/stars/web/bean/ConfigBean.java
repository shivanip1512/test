package com.cannontech.stars.web.bean;

import java.util.List;

import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.stars.hardware.StaticLoadGroupMapping;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.util.StarsUtils;

public class ConfigBean 
{
    private List<StaticLoadGroupMapping> currentStaticGroups;
    private List<StaticLoadGroupMapping> allStaticGroups;
    private int currentApplianceCategoryID;
    private LiteYukonUser currentUser = null;
    private boolean hasResetPermission;
    private boolean writeToFileAllowed;
    
    public ConfigBean()
    {
        super();
        
        String batchProcessType = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.BATCHED_SWITCH_COMMAND_TOGGLE );
        if(batchProcessType != null)
        {
            writeToFileAllowed = batchProcessType.compareTo(StarsUtils.BATCH_SWITCH_COMMAND_MANUAL) == 0 
                && VersionTools.staticLoadGroupMappingExists();
        }
        else
            writeToFileAllowed = false;
    }

    public List<StaticLoadGroupMapping> getCurrentStaticGroups() 
    {
        if(currentStaticGroups == null)
            currentStaticGroups = StaticLoadGroupMapping.getAllLoadGroupsForApplianceCat(getCurrentApplianceCategoryID());
        return currentStaticGroups;
    }
    
    public void setCurrentStaticGroups(List<StaticLoadGroupMapping> staticGroups) 
    {
        this.currentStaticGroups = staticGroups;
    }
    
    public int getCurrentApplianceCategoryID() 
    {
        return currentApplianceCategoryID;
    }

    public void setCurrentApplianceCategoryID(int currentApplianceCategoryID) {
        this.currentApplianceCategoryID = currentApplianceCategoryID;
    }

    public List<StaticLoadGroupMapping> getAllStaticGroups() {
        if(allStaticGroups == null)
            allStaticGroups = StaticLoadGroupMapping.getAllStaticLoadGroups();
        return allStaticGroups;
    }

    public void setAllStaticGroups(List<StaticLoadGroupMapping> allStaticGroups) {
        this.allStaticGroups = allStaticGroups;
    }

    public boolean isHasResetPermission() {
        /*Let's take some precautions that this person should be able to reset static load group mappings
         * 
         */
        hasResetPermission = DaoFactory.getAuthDao().checkRoleProperty(getCurrentUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS)
                            && DaoFactory.getAuthDao().checkRoleProperty(getCurrentUser(), InventoryRole.SN_CONFIG_RANGE);
        return hasResetPermission;
    }

    public void setHasResetPermission(boolean hasResetPermission) 
    {
        this.hasResetPermission = hasResetPermission;
    }

    public LiteYukonUser getCurrentUser() 
    {
        return currentUser;
    }

    public void setCurrentUser(LiteYukonUser currentUser) 
    {
        this.currentUser = currentUser;
    }
    
    public boolean isWriteToFileAllowed()
    {
        return writeToFileAllowed;
    }

    public void setWriteToFileAllowed(boolean writeToFileAllowed) 
    {
        this.writeToFileAllowed = writeToFileAllowed;
    }
}