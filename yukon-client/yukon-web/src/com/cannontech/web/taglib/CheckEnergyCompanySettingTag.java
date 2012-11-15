package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;


/**
 * If at least one property is true then the body of the tag is evaluated, otherwise it is skipped.
 * @see CheckProperty
 */
public class CheckEnergyCompanySettingTag extends TagSupport {
    private String value;
    private int energyCompanyId;

    @Override
    public int doStartTag() throws JspException {
        
        EnergyCompanyRolePropertyDao ecRoleDao = YukonSpringHook.getBean(EnergyCompanyRolePropertyDao.class);
        StarsDatabaseCache starsDatabaseCache = YukonSpringHook.getBean(StarsDatabaseCache.class);
        
        boolean inverted = false;
        if (value.startsWith("!")) {
            value = value.substring(1);
            inverted = true;
        }
        
        boolean isSet = false;
        try {
            YukonRoleProperty role = YukonRoleProperty.valueOf(YukonRoleProperty.class, value);
            YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
            isSet = ecRoleDao.checkProperty(role, yukonEnergyCompany);
        } catch (IllegalArgumentException ignore) {
            throw new IllegalArgumentException("Can't use '" + value + "', check that it is a valid EnergyCompanyRole");

        }
        
        if (inverted) {
            if (isSet) {
                return SKIP_BODY;
            } else {
                return EVAL_BODY_INCLUDE;
            }
        } else if (isSet) {
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

}