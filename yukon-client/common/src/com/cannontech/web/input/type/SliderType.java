package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.List;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;
import com.google.common.collect.Lists;

public class SliderType extends BaseEnumeratedType<String> {

    private String renderer = "slider.jsp";
    private String setting;
    private int hours = 24; // Default will be 24 hours slider

    public SliderType(String setting, int hours) {
        super.setRenderer(renderer);
        this.setting = setting;
        this.hours = hours;
    }

    public List<InputOptionProvider> getOptionList() {
        List<InputOptionProvider> optionList = Lists.newArrayList();
        GlobalSettingDao globalSettingDao = YukonSpringHook.getBean(GlobalSettingDao.class);
        GlobalSettingType globalSettingType = GlobalSettingType.valueOf(setting);
        GlobalSetting setting = globalSettingDao.getSetting(globalSettingType);
        String startStopTime = (String) setting.getValue();
        String[] timeRanges = startStopTime.split(",");
        optionList.add(new InputOption(timeRanges[0], this.setting));
        optionList.add(new InputOption(timeRanges[1], this.setting));

        return optionList;
    }

    public Class<String> getTypeClass() {
        return String.class;
    }

    public int getHours() {
        return hours;
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
