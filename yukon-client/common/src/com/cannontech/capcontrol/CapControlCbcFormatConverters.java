package com.cannontech.capcontrol;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class CapControlCbcFormatConverters {

    public static String convertNeutralCurrent(Double value) {
        YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        int pvalue = value.intValue();
        String neutralCurrent;

        if ((pvalue & 0x08) == 0x08){
            neutralCurrent = accessor.getMessage("yukon.common.yes");
        } else {
            neutralCurrent = accessor.getMessage("yukon.common.no");
        }

        return neutralCurrent;
    }

    public static String convertToIpAddress(Double value) {
        long ipvalue = value.longValue();

        StringBuilder sb = new StringBuilder();
        int temp = (int) ((ipvalue >> 24) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 16) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 8) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) (ipvalue & 0xFF);
        sb.append(Integer.toString(temp, 10));

        return sb.toString();
    }

    public static String convertToFirmwareVersion(Double value) {
        //  The firmware version is encoded as up to 8 six-bit ASCII characters
        //  http://nemesis.lonestar.org/reference/telecom/codes/sixbit.html
        long encodedValue = value.longValue();

        StringBuilder sb = new StringBuilder();

        while( encodedValue != 0 ) {
            sb.append((char)(' ' + encodedValue % 0x40));
            encodedValue /= 0x40;
        }

        return sb.reverse().toString();
    }

    public static String convertLong(Double value) {
        return Long.toString(value.longValue());
    }

    public static String convertLastControlReason(Double value) {

        int rawState = value.intValue();
        YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);
        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);

        LiteState state = stateGroupDao.findLiteState(StateGroupUtils.STATEGROUP_LASTCONTROL_STATE, rawState);

        if (state == null) {
            return accessor.getMessage("yukon.web.modules.capcontrol.unknownState", rawState);
        }

        return state.getStateText();
    }

    public static String convertLastControlReasonColor(Double value) {
        YukonColorPalette yukonColor = getColorForState(StateGroupUtils.STATEGROUP_LASTCONTROL_STATE, value.intValue());
        return yukonColor.getHexValue();
    }

    public static String convertIgnoredControlReason(Double value) {

        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);
        YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);

        int rawState = value.intValue();

        LiteState state = stateGroupDao.findLiteState(StateGroupUtils.STATEGROUP_IGNORED_CONTROL, rawState);

        if (state == null) {
            return accessor.getMessage("yukon.web.modules.capcontrol.unknownState", rawState);
        }

        return state.getStateText();
    }

    public static String convertIgnoredControlReasonColor(Double value) {
        YukonColorPalette yukonColor = getColorForState(StateGroupUtils.STATEGROUP_IGNORED_CONTROL, value.intValue());
        return yukonColor.getHexValue();
    }

    private static YukonColorPalette getColorForState(int stateGroupId, int state) {
        StateGroupDao stateGroupDao = YukonSpringHook.getBean(StateGroupDao.class);
        LiteState liteState = stateGroupDao.findLiteState(stateGroupId, state);

        YukonColorPalette yukonColor = liteState == null ? 
                YukonColorPalette.WHITE : YukonColorPalette.getColor(liteState.getFgColor());

        return yukonColor;
    }
}
