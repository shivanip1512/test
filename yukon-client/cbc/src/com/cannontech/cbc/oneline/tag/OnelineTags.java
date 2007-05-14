package com.cannontech.cbc.oneline.tag;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.yukon.conns.ConnPool;

public class OnelineTags {
    public static final String TAGGRP_CB_OPERATIONAL_STATE = "Cap Bank Operational State";
    public static final String TAGGRP_ENABLEMENT = "Enablement State";
    public static final String TAGGRP_OVUV_ENABLEMENT = "OVUV Enablement State";

    public static final String TAGTYPE_CAP = "CAPTYPE";

    public static final String[] CAP_TAGS = { TAGGRP_CB_OPERATIONAL_STATE,
            TAGGRP_ENABLEMENT };
    public static final String TAGTYPE_FDR = "FEEDERTYPE";
    public static final String TAGTYPE_SUB = "SUBTAGTYPE";

    private String type;

    // all tag states for cap
    private TagGroup[] capTagStates = {
            new TagGroup(TAGGRP_CB_OPERATIONAL_STATE, new String[] {
                    CapBank.STANDALONE_OPSTATE, CapBank.SWITCHED_OPSTATE }),
            new TagGroup(TAGGRP_ENABLEMENT, new String[] {
                    CapBank.ENABLE_OPSTATE,
                    CapBank.DISABLE_OPSTATE }),
            new TagGroup(TAGGRP_OVUV_ENABLEMENT, new String[] {
                            CapBank.ENABLE_OVUV_OPSTATE,
                            CapBank.DISABLE_OVUV_OPSTATE })
    };

    private TagGroup[] feederTagStates = { new TagGroup(TAGGRP_ENABLEMENT,
                                                        new String[] {
                                                                CapControlFeeder.ENABLE_OPSTATE,
                                                                CapControlFeeder.DISABLE_OPSTATE }) };
    private TagGroup[] subTagStates = { new TagGroup(TAGGRP_ENABLEMENT,
                                                     new String[] {
                                                             CapControlSubBus.ENABLE_OPSTATE,
                                                             CapControlSubBus.DISABLE_OPSTATE }) };

    public OnelineTags(String t) {
        type = t;
    }

    public String getTagName(String tagDesc) {

        if (type.equals(TAGTYPE_CAP)) {
            return getTagStateName(tagDesc, capTagStates);
        } else if (type.equals(TAGTYPE_FDR)) {
            return getTagStateName(tagDesc, feederTagStates);

        } else if (type.equals(TAGTYPE_SUB)) {
            return getTagStateName(tagDesc, subTagStates);

        }
        return null;

    }

    private String getTagStateName(String tagDesc, TagGroup[] states) {
        for (int i = 0; i < states.length; i++) {
            TagGroup tagState = states[i];
            for (int j = 0; j < tagState.getTagNames().length; j++) {
                String state = tagState.getTagNames()[j];
                if (state.equalsIgnoreCase(tagDesc)) {
                    return tagState.getTagGroupName();
                }
            }
        }
        return null;
    }

    public static CBCTagHandler createTagHandler(List<LitePoint> points) {
        ClientConnection defDispatchConn = (ClientConnection) ConnPool.getInstance()
                                                                      .getDefDispatchConn();
        Multi multi = new Multi();
        Registration reg = new Registration();
        reg.setAppName(CtiUtilities.getApplicationName());
        reg.setAppIsUnique(0);
        reg.setAppKnownPort(0);
        reg.setAppExpirationDelay(300); // 5 minutes
        reg.setPriority(15);

        PointRegistration pReg = new PointRegistration();
        pReg.setRegFlags(PointRegistration.REG_ADD_POINTS);

        List<Integer> pointIDs = new ArrayList<Integer>();
        for (LitePoint point : points) {
            pointIDs.add(new Integer(point.getPointID()));
        }
        pReg.setPointList(pointIDs);
        multi.getVector().addElement(reg);
        multi.getVector().addElement(pReg);
        defDispatchConn = (ClientConnection) ConnPool.getInstance()
                                                     .getDefDispatchConn();
        defDispatchConn.setRegistrationMsg(multi);

        CBCTagHandler handler = new CBCTagHandler(defDispatchConn);
        return handler;
    }
}
