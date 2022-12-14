package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.List;

import com.cannontech.amr.device.DayOfWeek;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;
import com.google.common.collect.Lists;

public class WeekDaysType extends BaseEnumeratedType<String> {

    private String renderer = "weekDaysType.jsp";
    private String setting;

    public WeekDaysType(String setting) {
        super.setRenderer(renderer);
        this.setting = setting;
    }

    public List<InputOptionProvider> getOptionList() {
        List<InputOptionProvider> optionList = Lists.newArrayList();
        GlobalSettingDao globalSettingDao = YukonSpringHook.getBean(GlobalSettingDao.class);
        GlobalSettingType globalSettingType = GlobalSettingType.valueOf(setting);
        GlobalSetting setting = globalSettingDao.getSetting(globalSettingType);
        String daysOfWeek = (String) setting.getValue();
        int index = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (daysOfWeek.charAt(index) == 'Y') {
                optionList.add(new InputOption(day.name(), true));
            } else {
                optionList.add(new InputOption(day.name(), false));
            }
            index++;
        }

        return optionList;
    }

    public Class<String> getTypeClass() {
        return String.class;
    }

    public PropertyEditor getPropertyEditor() {

        PropertyEditor attrPropEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String typeStr) throws IllegalArgumentException {
                setValue(typeStr);
            }

            @Override
            public String getAsText() {
                return (String) getValue();
            }
        };
        return attrPropEditor;
    }

}
