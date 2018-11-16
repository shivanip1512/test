package com.cannontech.web.support;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.util.MatchStyle;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.support.SiteMapPage.OtherPermission;
import com.cannontech.web.support.SiteMapPage.PermissionLevel;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class SiteMapHelper {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ObjectFormattingService objectFormattingService;
    
    public Map<SiteMapCategory, List<SiteMapWrapper>> getSiteMap(YukonUserContext context) {
        Multimap<SiteMapCategory, SiteMapWrapper> map = ArrayListMultimap.create();

        for ( SiteMapPage page : SiteMapPage.values() ) {
            SiteMapWrapper wrapper = wrapPage(page, context.getYukonUser());
            if(wrapper != null){
                map.put(page.getCategory(), wrapper);
            }
        }

        Map<SiteMapCategory, Collection<SiteMapWrapper>> multiMap = map.asMap();

        List<SiteMapCategory> categories = Lists.newArrayList(multiMap.keySet());
        Collections.sort(categories);

        Map<SiteMapCategory, List<SiteMapWrapper>> realMap = Maps.newLinkedHashMap();
        for(SiteMapCategory category: categories){
            List<SiteMapWrapper> pages = Lists.newArrayList(multiMap.get(category));
            pages = objectFormattingService.sortDisplayableValues(pages, null, null, context);
            realMap.put(category, pages);
        }
        return realMap;
    }
    
    private SiteMapWrapper wrapPage(SiteMapPage page, LiteYukonUser user) {
        SiteMapWrapper wrapper = new SiteMapWrapper();
        wrapper.setPage(page);
        wrapper.setEnabled(false);

        PermissionLevel permissionLevel = getPermissionLevel(page, user);
        switch (permissionLevel) {
        case ACCESS:
            wrapper.setEnabled(true);
            break;
        case VIEW:
            wrapper.setEnabled(false);
            String permissionList = getPermissionDisplayList(page);
            wrapper.setRequiredPermissions(permissionList);
            break;
        case HIDE:
            return null;
        }
        return wrapper;
    }
    
    private String getPermissionDisplayList(SiteMapPage page) {
        String permissions = "";
        for(Object permission : page.getPermissions()){
            if ( permission instanceof YukonRole) {
                permissions += ((YukonRole) permission).name() + " ";
            } else if (permission instanceof YukonRoleProperty) {
                    permissions += ((YukonRoleProperty) permission).name() + " ";
            } else if (permission instanceof GlobalSettingType) {
                permissions += ((GlobalSettingType) permission).name() + " ";
            } else if (permission instanceof MasterConfigBoolean) {
                permissions += ((MasterConfigBoolean) permission).name() + " ";
            } else if (permission instanceof OtherPermission) {
                switch((OtherPermission) permission){
                case EC_OPERATOR:
                    permissions += "EC Operator ";
                    break;
                case HIDEABLE:
                    break;
                }
            } else {
                throw new IllegalArgumentException("Permission type not supported: " + permission);
            }
        }
        return permissions;
    }
    
    public PermissionLevel getPermissionLevel(SiteMapPage page, LiteYukonUser user) {
        Object[] permissions = page.getPermissions();
        MatchStyle matchStyle = page.getMatchStyle();

        if(permissions.length== 0) return PermissionLevel.ACCESS;

        PermissionLevel noPermission = PermissionLevel.VIEW;

        for(Object permission : permissions){
            boolean hasPermission = false;
            if ( permission instanceof YukonRole) {
                hasPermission = rolePropertyDao.checkRole((YukonRole) permission, user);
            } else if (permission instanceof YukonRoleProperty) {
                hasPermission = rolePropertyDao.checkProperty((YukonRoleProperty) permission, user);
            } else if (permission instanceof GlobalSettingType) {
                InputType<?> dataType = ((GlobalSettingType) permission).getType();
                if (dataType.getTypeClass() == Boolean.class) {
                    hasPermission = globalSettingDao.getBoolean((GlobalSettingType) permission);
                } else if (dataType.getTypeClass() == String.class) {
                    String globalSettingValue = globalSettingDao.getString((GlobalSettingType) permission);
                    if (!globalSettingValue.isEmpty()) {
                        hasPermission = true;
                    }
                }
            } else if (permission instanceof MasterConfigBoolean) {
                hasPermission = configurationSource.getBoolean((MasterConfigBoolean) permission);
            } else if (permission instanceof OtherPermission) {
                switch((OtherPermission) permission){
                case EC_OPERATOR:
                    hasPermission = ecDao.isEnergyCompanyOperator(user);
                    break;
                case HIDEABLE:
                    hasPermission = true;
                    noPermission = PermissionLevel.HIDE;
                    
                }
            } else {
                throw new IllegalArgumentException("Permission type not supported: " + permission);
            }
            //Short-circuit out if "any" and one is true, or "all" and one is false.
            if(hasPermission && matchStyle == MatchStyle.any) return PermissionLevel.ACCESS;
            if(! hasPermission && matchStyle == MatchStyle.all) return noPermission;
        }
        //Return true if "all" and no non-matches, false if "any" and no matches
        if(matchStyle == MatchStyle.all) return PermissionLevel.ACCESS;
        if(matchStyle == MatchStyle.any) return noPermission;
        throw new InvalidParameterException("MatchStyle must be either Matchstyle.any or Matchstyle.all");
    }
    
    public class SiteMapWrapper implements DisplayableEnum {
        private boolean enabled;
        private SiteMapPage page;
        private String requiredPermissions;

        public String getRequiredPermissions() {
            return requiredPermissions;
        }
        public void setRequiredPermissions(String requiredPermissions) {
            this.requiredPermissions = requiredPermissions;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public SiteMapPage getPage() {
            return page;
        }
        public void setPage(SiteMapPage page) {
            this.page = page;
        }
        @Override
        public String getFormatKey() {
            return page.getFormatKey();
        }
    }
}
