package com.cannontech.common.dr.setup;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(value={ "routeName"}, allowGetters= true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LoadGroupRipple extends LoadGroupBase<LMGroupRipple> implements LoadGroupRoute {
    private Integer routeId;
    private String routeName;
    private Integer shedTime;
    private String control;
    private String restore;

    private RippleGroup group;
    private RippleGroupAreaCode areaCode;
    public static final Integer SHOW_SPECIAL_RIPPLE = 0x10000000;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getShedTime() {
        return shedTime;
    }

    public void setShedTime(Integer shedTime) {
        this.shedTime = shedTime;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getRestore() {
        return restore;
    }

    public void setRestore(String restore) {
        this.restore = restore;
    }

    public RippleGroup getGroup() {
        return group;
    }

    public void setGroup(RippleGroup group) {
        this.group = group;
    }

    public RippleGroupAreaCode getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(RippleGroupAreaCode areaCode) {
        this.areaCode = areaCode;
    }
    
    public static boolean isSpecialRippleEnabled(LiteYukonUser user) {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        long specialRipple = Long.parseLong(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV, user), 16);
        return (specialRipple & SHOW_SPECIAL_RIPPLE) != 0;
    }

    @Override
    public void buildModel(LMGroupRipple lmGroupRipple) {
        // Set parent fields
        super.buildModel(lmGroupRipple);
        
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        if (isSpecialRippleEnabled(user)) {
            RippleGroup rippleGroup = RippleGroup
                    .getRippleGroupValue(lmGroupRipple.getLmGroupRipple().getControl().subSequence(0, 10).toString());
            RippleGroupAreaCode rippleGroupAreaCode = RippleGroupAreaCode
                    .getRippleAreaCodeValue(lmGroupRipple.getLmGroupRipple().getControl().subSequence(10, 16).toString());
            setGroup(rippleGroup);
            setAreaCode(rippleGroupAreaCode);
            setControl(lmGroupRipple.getLmGroupRipple().getControl().subSequence(16, 50).toString());
            setRestore(lmGroupRipple.getLmGroupRipple().getRestore().substring(16, 50).toString());
        } else {
            setControl(lmGroupRipple.getLmGroupRipple().getControl());
            setRestore(lmGroupRipple.getLmGroupRipple().getRestore());
        }

        // Set Ripple fields
        setRouteId(lmGroupRipple.getRouteID());
        setShedTime(lmGroupRipple.getLmGroupRipple().getShedTime());
    }

    @Override
    public void buildDBPersistent(LMGroupRipple group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set LMGroupRipple fields.
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();

        com.cannontech.database.db.device.lm.LMGroupRipple lmGroupRipple = group.getLmGroupRipple();
        lmGroupRipple.setRouteID(getRouteId());
        lmGroupRipple.setShedTime(getShedTime());
        if (isSpecialRippleEnabled(user) && (getGroup() != null && getAreaCode() != null)) {
            String rippleGroup = (String) getGroup().getDatabaseRepresentation();
            String rippleGroupAreaCode = (String) getAreaCode().getDatabaseRepresentation();
            lmGroupRipple.setControl(new StringBuffer(rippleGroup).append(rippleGroupAreaCode).append(getControl()).toString());
            lmGroupRipple.setRestore(new StringBuffer(rippleGroup).append(rippleGroupAreaCode).append(getRestore()).toString());
        } else {
            lmGroupRipple.setControl(getControl());
            lmGroupRipple.setRestore(getRestore());
        }

        group.setLmGroupRipple(lmGroupRipple);
    }

}