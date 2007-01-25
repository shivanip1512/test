package com.cannontech.cbc.oneline.model.feeder;

import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.OnelineCommand;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.Feeder;

public class FeederControlPanel extends BaseControlPanel {

    public static final String JS_FUNCTION = "executeFeederCommand";
    public static final String PANEL_NAME = "FeederControlPanel";
    private OnelineCommand[] feederCommands = {
            new OnelineCommand("Enable", CBCCommand.ENABLE_FEEDER, true, OnelineUtil.ONELN_STATE_GROUP),
            new OnelineCommand("Reset Opcount", CBCCommand.RESET_OPCOUNT)

    };

    public FeederControlPanel(OnelineObject o) {
        super(o);
        setCommands(feederCommands);
        setPanelName(PANEL_NAME + "_" + o.getPaoId());
        setJSFunction(JS_FUNCTION);
        int currFdrIdx = ((OnelineFeeder)o).getCurrFdrIdx();
        String labelName = ((Feeder)getSubBusMsg().getCcFeeders().get(currFdrIdx)).getCcName();
        setLabelName(labelName);
        create();
    }
}
