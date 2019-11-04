package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LmSetupFilterType implements DisplayableEnum {

    CONTROL_AREA("/dr/setup/controlArea/"),
    CONTROL_SCENARIO("/dr/setup/controlScenario/"),
    LOAD_GROUP("/dr/setup/loadGroup/"),
    LOAD_PROGRAM("/dr/setup/loadProgram/"),
    MACRO_LOAD_GROUP("/dr/setup/macroLoadGroup/"),
    PROGRAM_CONSTRAINT("/dr/setup/constraint/"),
    //TODO: We will add a url in the YUK to add support UI for view/edit gears from filter page. YUK to be created.
    GEAR("");

    private String viewUrl;

    private LmSetupFilterType(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup." + name();
    }

}
