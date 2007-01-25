package com.cannontech.cbc.oneline.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.state.StateGroupUtils;

public class OnelineUtil {
    public static final int SUB_ST_EN = 0;
    public static final int SUB_ST_DIS = 1;
    public static final int SUB_ST_PENDING = 2;
    public static final int SUB_ST_EN_ALBUS = 3;

    public static final int VERIFY_EN = 0;
    public static final int VERIFY_DIS = 1;

    public static final double SUB_IMG_WIDTH = 30;
    public static final double CAP_IMG_WIDTH = 20;
    public static final int SUB_IMG_HEIGHT = 60;
    
    public static final String YUKON_LOGO_WHITE = "YukonLogoWhite.gif";
    public static final String CAP_CONTROL_LOGO = "CapControlLogo.gif";


    public static final String ONELN_STATE_GROUP = "1LNSUBSTATE";
    public static final String ONELN_VERIFY_GROUP = "1LNVERIFY";

    public static LiteStateGroup getOnelineStateGroup(String groupName) {
        LiteStateGroup[] groups = DaoFactory.getStateDao().getAllStateGroups();
        for (int i = 0; i < groups.length; i++) {
            LiteStateGroup group = groups[i];
            if (group.getStateGroupName().equalsIgnoreCase(groupName))
                return group;

        }
        return null;
    }

    public static List stringToList(String s) {
        String[] temp = StringUtils.split(s, ':');
        List<String> l = new ArrayList(0);
        for (int i = 0; i < temp.length; i++) {
            String string = temp[i];
            if (string.equalsIgnoreCase("null")) {
                l.add(null);
            } else {
                l.add(string);
            }
        }
        return l;
    }

    public static List<LiteYukonImage> getAdditionalImages() {
        StateDao stateDao = DaoFactory.getStateDao();
        //we need images from state groups: CapBankStatus, 1LNSUBSTATE, 1LNVERIFY
        List<LiteYukonImage> additionalImages = new ArrayList<LiteYukonImage>(0);
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        for (int i = 0; i < allStateGroups.length; i++) {
            LiteStateGroup group = allStateGroups[i];
            String stateGroupName = group.getStateGroupName();
            int stateGroupID = group.getStateGroupID();
            if (stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_STATE_GROUP) || stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_VERIFY_GROUP) || stateGroupID == StateGroupUtils.STATEGROUPID_CAPBANK) {
                LiteState[] liteStates = stateDao.getLiteStates(stateGroupID);
                for (int j = 0; j < liteStates.length; j++) {
                    LiteState state = liteStates[j];
                    int imageID = state.getImageID();
                    LiteYukonImage liteYukonImage = DaoFactory.getYukonImageDao()
                                                              .getLiteYukonImage(imageID);
                    additionalImages.add(liteYukonImage);
                }

            }

        }
        return additionalImages;
    
    }


}
