package com.cannontech.capcontrol;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class CapControlRegulatorFormatConverters {

    public static String convertEatonRegulatorControlMode(Double value) {

        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);
        YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);

        int rawState = value.intValue();

        LiteState state = stateGroupDao.findLiteState(StateGroupUtils.STATEGROUP_EATON_REGULATOR_CONTROL_MODE, rawState);

        if (state == null) {
            return accessor.getMessage("yukon.web.modules.capcontrol.unknownState", rawState);
        }

        return state.getStateText();
    }
    
    public static String convertEatonRegulatorControlModeColor(Double value) {
        YukonColorPalette yukonColor = getColorForState(StateGroupUtils.STATEGROUP_EATON_REGULATOR_CONTROL_MODE, value.intValue());
        return yukonColor.getHexValue();
    }

    public static String convertBeckwithRegulatorControlModeColor(Double value) {
        YukonColorPalette yukonColor = getColorForState(StateGroupUtils.STATEGROUP_BECKWITH_REGULATOR_CONTROL_MODE, value.intValue());
        return yukonColor.getHexValue();
    }
    
    public static String convertBeckwithRegulatorControlMode(Double value) {

        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);
        YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);

        int rawState = value.intValue();

        LiteState state = stateGroupDao.findLiteState(StateGroupUtils.STATEGROUP_BECKWITH_REGULATOR_CONTROL_MODE, rawState);

        if (state == null) {
            return accessor.getMessage("yukon.web.modules.capcontrol.unknownState", rawState);
        }

        return state.getStateText();
    }

    private static YukonColorPalette getColorForState(int stateGroupId, int state) {
        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);
        LiteState liteState = stateGroupDao.findLiteState(stateGroupId, state);

        YukonColorPalette yukonColor = liteState == null ? 
                YukonColorPalette.WHITE : YukonColorPalette.getColor(liteState.getFgColor());

        return yukonColor;
    }
}
