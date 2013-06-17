package com.cannontech.cbc.oneline;

import java.awt.Color;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.ExtraUpdatableTextElement;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.esub.element.StaticText;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;

public class OnelineDisplayManager {
    public static OnelineDisplayManager me;
    public static Hashtable<Integer, String> propPrefixMap = new Hashtable<Integer, String>();

    private OnelineDisplayManager() {
        propPrefixMap.put(PAOGroups.CAP_CONTROL_SUBBUS, "SubStat_");
        propPrefixMap.put(PAOGroups.CAP_CONTROL_FEEDER, "FeederStat_");
        propPrefixMap.put(PAOGroups.CAPBANK, "CapStat_");

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
            YukonUserContext userContext = parentOnelineObject.getDrawing().getLayoutParams().getYukonUserContext();
            if (userContext != null) {
                RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
                String rolePropertyValue = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.getForId(roleProperty), userContext.getYukonUser());
                Boolean isTarget = Boolean.valueOf(rolePropertyValue);
                if (isTarget.booleanValue())
                    return true;
            }
        }
        return false;
    }

    public String getDisplayValue(StreamableCapObject stream, int rolePropID, UpdatableStats stats, YukonUserContext userContext) {
        
        UpdaterHelper updaterHelper = YukonSpringHook.getBean("updaterHelper", UpdaterHelper.class);
        UpdaterHelper.UpdaterDataType dispCol = stats.getPropColumnMap().get(rolePropID);

        if (stream instanceof SubBus) {
            return updaterHelper.getOnelineSubBusValueAt((SubBus) stream, dispCol, userContext);
        } else if (stream instanceof Feeder) {
            return (String) updaterHelper.getFeederValueAt((Feeder) stream, dispCol, userContext);
        } else if (stream instanceof CapBankDevice) {
            return updaterHelper.getCapBankValueAt((CapBankDevice) stream, dispCol, userContext).toString();
        } else {
            YukonUserContextMessageSourceResolver resolver = YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
            return resolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.defaults.none");
        }    
    }

    public UpdatableTextList adjustPosition(List<UpdatableTextList> allStats, LxComponent prevComp, int pos, StreamableCapObject stream, YukonUserContext userContext) {
        YukonUserContextMessageSourceResolver resolver = YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
        UpdatableTextList temp = allStats.get(pos);

        UpdatableStats stats = temp.getStats();
        String labelName = getLabel(temp.getRolePropID(), stats);
        LxAbstractText label = OnelineUtil.createTextElement(labelName,
                                                             OnelineUtil.getStartPoint(prevComp),
                                                             null,
                                                             new Integer((int) prevComp.getHeight() + 10));
        String text = getDisplayValue(stream, temp.getRolePropID(), stats, userContext);
        String displayableText = new String(text);
        if(stats.getPropColumnMap().get(temp.getRolePropID()) == UpdaterHelper.UpdaterDataType.CB_CONTROLLER) {
            if(text.length() > 13) {
                displayableText = text.substring(0, 12) + "...";
            }
        }

        StaticText content = OnelineUtil.createTextElement(displayableText,
                                                           OnelineUtil.getStartPoint(label),
                                                           new Integer((int) label.getWidth() + 10),
                                                           null);
        
        //handle any extra elements
        if (temp instanceof ExtraUpdatableTextElement) {
            ExtraUpdatableTextElement extraElement = (ExtraUpdatableTextElement) temp;
            if (extraElement.conditionToAddIsTrue())
            {
                LxAbstractText t = createExtraElement(stream,
                                   (ExtraUpdatableTextElement) temp,
                                   labelName,
                                   content,
                                   resolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.modules.capcontrol.signalQuality"),
                                   Color.RED);
               temp.addExtraElement(t);
            }
            else
            {
                LxAbstractText t = createExtraElement(stream,
                                                      (ExtraUpdatableTextElement) temp,
                                                      labelName,
                                                      content,
                                                      "",
                                                      Color.BLACK);
                                  temp.addExtraElement(t);

            }
                
        }

        int type = OnelineUtil.getYukonType(stream);
        Integer paoID = stream.getCcId();
        content.setName(propPrefixMap.get(type) + paoID + "_" + labelName);
        if(labelName.equalsIgnoreCase("Updated:")) {
            content.setName(CommandPopups.VAR_CHANGE_POPUP+"_"+paoID);
            content.setLinkTo("javascript:void(0)");
        }else if(labelName.equalsIgnoreCase("CBC:")) {
            content.setName("CBCNAME_"+text);
            content.setLinkTo("javascript:void(0)");
        }
        temp.setFirstElement(label);
        temp.setLastElement(content);
        return temp;
    }


    public String getLabel(int rolePropID, UpdatableStats stats) {

        return stats.getPropLabelMap().get(rolePropID);

    }


    

    private LxAbstractText createExtraElement(StreamableCapObject stream,
            ExtraUpdatableTextElement extra, String labelName,
            LxComponent neighborComponent, String textContent, Color c) {
        
        // create an extra Element to display
        StaticText retTextElement = OnelineUtil.createColoredTextElement(textContent,
                                                                    OnelineUtil.getStartPoint(neighborComponent),
                                                                    new Integer((int) neighborComponent.getWidth() + 10),
                                                                    null,
                                                                   c);
        int type = OnelineUtil.getYukonType(stream);
        Integer paoID = stream.getCcId();
        String nameString = propPrefixMap.get(type) + paoID;
        retTextElement.setName(nameString + "_EXTRA_" + (Math.abs( extra.getRolePropID())));
        return retTextElement;
    }

}
