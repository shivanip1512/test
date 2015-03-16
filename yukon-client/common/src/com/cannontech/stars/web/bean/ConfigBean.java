package com.cannontech.stars.web.bean;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.db.hardware.StaticLoadGroupMapping;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ConfigBean 
{
    private List<StaticLoadGroupMapping> allStaticGroups;
    private final ConcurrentMap<Integer, List<StaticLoadGroupMapping>> staticGroupMap = new ConcurrentHashMap<Integer, List<StaticLoadGroupMapping>>();
    private int currentApplianceCategoryID;
    private LiteYukonUser currentUser = null;
    private boolean hasResetPermission;
    private boolean writeToFileAllowed;
    
    public ConfigBean()
    {
        super();
        String batchProcessType = YukonSpringHook.getBean(GlobalSettingDao.class).getString(GlobalSettingType.BATCHED_SWITCH_COMMAND_TOGGLE);
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
        List<StaticLoadGroupMapping> currentStaticGroups = staticGroupMap.get(currentApplianceCategoryID);
        if(currentStaticGroups == null) {
            currentStaticGroups = StaticLoadGroupMapping.getAllLoadGroupsForApplianceCat(getCurrentApplianceCategoryID());
            staticGroupMap.put(currentApplianceCategoryID, currentStaticGroups);
        }
        return currentStaticGroups;
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
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
        ConfigurationSource configSource = YukonSpringHook.getBean(ConfigurationSource.class);
        hasResetPermission = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, getCurrentUser())
                            && configSource.getBoolean(MasterConfigBooleanKeysEnum.SEND_INDIVIDUAL_SWITCH_CONFIG);
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
}
