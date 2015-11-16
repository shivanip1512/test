package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;


/**
 * If at least one property is true then the body of the tag is evaluated, otherwise it is skipped.
 */
public class CheckEnergyCompanySettingTag extends TagSupport {
    private String value;
    private int energyCompanyId;

    @Override
    public int doStartTag() throws JspException {

        EnergyCompanySettingDao energyCompanySettingDao = YukonSpringHook.getBean(EnergyCompanySettingDao.class);

        boolean inverted = false;
        if (value.startsWith("!")) {
            value = value.substring(1);
            inverted = true;
        }

        boolean isSet = false;
        try {
            EnergyCompanySettingType type = EnergyCompanySettingType.valueOf(EnergyCompanySettingType.class, value);
            isSet = energyCompanySettingDao.getBoolean(type, energyCompanyId);
        } catch (IllegalArgumentException ignore) {
            throw new IllegalArgumentException("Can't use '" + value + "', check that it is a valid EnergyCompanySetting");
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