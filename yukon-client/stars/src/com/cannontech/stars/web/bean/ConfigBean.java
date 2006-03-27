package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.StaticLoadGroupMapping;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;



public class ConfigBean 
{
    private List<StaticLoadGroupMapping> currentStaticGroups;
    private List<StaticLoadGroupMapping> allStaticGroups;
    private boolean hasStaticLoadGroupMapping;
    private int currentApplianceCategoryID;
    
    public ConfigBean()
    {
        super();
        
        hasStaticLoadGroupMapping = VersionTools.staticLoadGroupMappingExists();
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
    
    public boolean getHasStaticLoadGroupMapping() 
    {
       return hasStaticLoadGroupMapping;
    }
    
    public void setHasStaticLoadGroupMapping(boolean hasStaticLoadGroupMapping) 
    {
        this.hasStaticLoadGroupMapping = hasStaticLoadGroupMapping;
    }

    public int getCurrentApplianceCategoryID() {
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
    
}
