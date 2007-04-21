package com.cannontech.cbc.oneline;

import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.element.StaticText;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;

public class OnelineDisplayManager {
    public static OnelineDisplayManager me;
    public static Hashtable<Integer, String> propPrefixMap = new Hashtable<Integer, String>();

    private OnelineDisplayManager() {
        propPrefixMap.put(CBCOnelineSettingsRole.SUB_ROLEID, "SubStat_");
        propPrefixMap.put(CBCOnelineSettingsRole.FDR_ROLEID, "FeederStat_");
        propPrefixMap.put(CBCOnelineSettingsRole.CAP_ROLEID, "CapStat_");

    }

    public static synchronized OnelineDisplayManager getInstance() {
        if (me == null) {
            me = new OnelineDisplayManager();

        }
        return me;
    }

    public boolean isVisible(int roleProperty, UpdatableStats stats) {
        OnelineObject parentOnelineObject = stats.getParentOnelineObject();
        if (parentOnelineObject != null) {
            LiteYukonUser user = parentOnelineObject.getDrawing()
                                                    .getLayoutParams()
                                                    .getUser();
            if (user != null) {
                AuthDao authDao = DaoFactory.getAuthDao();
                String rolePropertyValue = authDao.getRolePropertyValue(user,
                                                                        roleProperty);
                Boolean isTarget = Boolean.valueOf(rolePropertyValue);
                if (isTarget.booleanValue())
                    return true;
            }
        }
        return false;
    }

    public String getDisplayValue(StreamableCapObject stream, int rolePropID,
            UpdatableStats stats) {
        int roleID = getRoleID(rolePropID);
        CBCDisplay oldWebDisplay = new CBCDisplay();
        if (roleID == CBCOnelineSettingsRole.SUB_ROLEID) {
            Integer dispCol = stats.getPropColumnMap().get(rolePropID);
            return (String) oldWebDisplay.getSubBusValueAt((SubBus) stream,
                                                           dispCol);
        }
        else if (roleID == CBCOnelineSettingsRole.FDR_ROLEID) {
            Integer dispCol = stats.getPropColumnMap().get(rolePropID);
            return (String) oldWebDisplay.getFeederValueAt((Feeder) stream,
                                                           dispCol);
        }
        else if (roleID == CBCOnelineSettingsRole.CAP_ROLEID) {
            Integer dispCol = stats.getPropColumnMap().get(rolePropID);
            return oldWebDisplay.getCapBankValueAt((CapBankDevice) stream,
                                                           dispCol.intValue()).toString();
        }
        return "(none)";

    }

    public UpdatableTextList adjustPosition(List<UpdatableTextList> allStats,
            LxComponent prevComp, int pos, StreamableCapObject stream) {
        UpdatableTextList temp = allStats.get(pos);

        UpdatableStats stats = temp.getStats();
        String labelName = getLabel(temp.getRolePropID(), stats);
        LxAbstractText label = OnelineUtil.createTextElement(labelName,
                                                             OnelineUtil.getStartPoint(prevComp),
                                                             null,
                                                             new Integer((int) prevComp.getHeight() + 10));
        String text = getDisplayValue(stream, temp.getRolePropID(), stats);

        StaticText content = OnelineUtil.createTextElement(text,
                                                           OnelineUtil.getStartPoint(label),
                                                           new Integer((int) label.getWidth() + 10),
                                                           null);
        content.setName(propPrefixMap.get(getRoleID(temp.getRolePropID())) + stream.getCcId() + "_" + labelName);

        temp.setFirstElement(label);
        temp.setLastElement(content);
        return temp;
    }

    public String getLabel(int rolePropID, UpdatableStats stats) {

        return stats.getPropLabelMap().get(rolePropID);

    }

    private int getRoleID(int rolePropID) {
        int roleID = (rolePropID / 100);
        return roleID;
    }

}
