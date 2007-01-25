package com.cannontech.cbc.oneline.elements;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.cap.CapControlPanel;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.FeederControlPanel;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.SubControlPanel;
import com.cannontech.cbc.oneline.states.OnelineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.oneline.view.OnelineCommandTable;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

@SuppressWarnings("serial")
public class PanelDynamicTextElement extends DynamicText implements
        OnelineLxElement {
    private OnelineCommandTable parentTable;
    private OnelineState dynamicState;
    private String stateGroupName;

    private boolean updateText = true;
    private boolean updateColor = false;

    public PanelDynamicTextElement(OnelineCommandTable table,
            OnelineState state, String name) {
        parentTable = table;
        dynamicState = state;
        stateGroupName = name;
        if (dynamicState != null)
            dynamicState.setGroupName(stateGroupName);
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.cbc.oneline.elements.OnelineLxElement#update(boolean)
     */
    public boolean update(boolean change) {
        LiteStateGroup group = null;
        String compName = getName();
        group = OnelineUtil.getOnelineStateGroup(getStateGroupName());
        Validate.notNull(group,
                         "Oneline State Group is not mapped in the Database");
        LiteState ls = null;

        StreamableCapObject o = null;

        o = getStreamable(compName);
        try {
            int state = -1;
            state = dynamicState.getLiteStateId(o, state);
            ls = DaoFactory.getStateDao().getLiteState(group.getStateGroupID(),
                                                       state);

        } catch (Exception e) {
            CTILogger.error(e);
        }
        if (ls != null) {
            if (updateText) {
                setCurrentTextState(ls);
                updateText();
                updateElementJSFunc(this, ls, o);

            } 
            if (updateColor) {
                setCurrentColorState(ls);
                updateColor();
            }
            change = true;
        }
        return change;
    }

    private void updateElementJSFunc(OnelineLxElement element, LiteState ls,
            StreamableCapObject o) {

        BaseControlPanel parentControlPanel = parentTable.getParentControlPanel();
        if (parentControlPanel instanceof SubControlPanel) {
            String text = getText();
            int cmdID = ls.getStateRawState();
            String linkTo = null;
            if (stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_STATE_GROUP)) {
                linkTo = parentTable.createJSFunction(cmdID,
                                                      text.toUpperCase(),
                                                      null);
            } else if (stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_VERIFY_GROUP)) {
                cmdID = getSubVerifyCommandID(ls);
                linkTo = parentTable.createJSFunction(cmdID,
                                                      text.toUpperCase(),
                                                      null);
            }
            setLinkTo(linkTo);
        } else if (parentControlPanel instanceof FeederControlPanel) {
            String text = getText();
            int cmdID = (ls.getStateRawState() == OnelineUtil.SUB_ST_EN) ? CBCCommand.ENABLE_FEEDER
                    : CBCCommand.DISABLE_FEEDER;
            String linkTo = parentTable.createJSFunction(cmdID,
                                                         text.toUpperCase(),
                                                         null);
            setLinkTo(linkTo);

        } else if (parentControlPanel instanceof CapControlPanel) {
            String text = getText();

            int cmdID = (ls.getStateRawState() == OnelineUtil.SUB_ST_EN) ? CBCCommand.ENABLE_CAPBANK
                    : CBCCommand.DISABLE_CAPBANK;
            String linkTo = parentTable.createJSFunction(cmdID,
                                                         text.toUpperCase(),
                                                         null);
            setLinkTo(linkTo);
        }

    }

    private int getSubVerifyCommandID(LiteState ls) {
        return (ls.getStateRawState() == OnelineUtil.VERIFY_EN) ? CBCCommand.CMD_ALL_BANKS
                : CBCCommand.CMD_DISABLE_VERIFY;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.cbc.oneline.elements.OnelineLxElement#getStreamable(java.lang.String)
     */
    public StreamableCapObject getStreamable(String compName) {
        StreamableCapObject o = null;
        BaseControlPanel parentControlPanel = parentTable.getParentControlPanel();
        SubBus subBusMsg = parentControlPanel.getSubBusMsg();
        OneLineDrawing onelineDrawing = parentControlPanel.getOnelineDrawing();
        if (StringUtils.contains(compName, SubControlPanel.PANEL_NAME)) {
            o = subBusMsg;

        } else if (StringUtils.contains(compName, FeederControlPanel.PANEL_NAME)) {
            List<OnelineFeeder> feeders = onelineDrawing.getFeeders();
            for (OnelineFeeder feeder : feeders) {
                String panelName = feeder.getControlPanel().getPanelName();
                if (panelName.equalsIgnoreCase(compName)) {
                    o = feeder.getStreamable();
                }
            }
        } else if (StringUtils.contains(compName, CapControlPanel.PANEL_NAME)) {
            List<OnelineCap> caps = onelineDrawing.getCaps();
            for (OnelineCap cap : caps) {
                String panelName = cap.getControlPanel().getPanelName();
                if (panelName.equalsIgnoreCase(compName)) {
                    o = cap.getStreamable();
                }
            }
        }
        return o;
    }

    public OnelineCommandTable getParentTable() {
        return parentTable;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.cbc.oneline.elements.OnelineLxElement#getDynamicState()
     */
    public OnelineState getDynamicState() {
        return dynamicState;
    }

    public String getStateGroupName() {
        return stateGroupName;
    }

    public boolean isUpdateColor() {
        return updateColor;
    }

    public void setUpdateColor(boolean updateColor) {
        this.updateColor = updateColor;
    }

    public boolean isUpdateText() {
        return updateText;
    }

    public void setUpdateText(boolean updateText) {
        this.updateText = updateText;
    }

}
