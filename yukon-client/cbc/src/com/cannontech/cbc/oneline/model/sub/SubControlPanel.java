package com.cannontech.cbc.oneline.model.sub;

import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.OnelineCommand;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.yukon.cbc.CBCCommand;

public class SubControlPanel extends BaseControlPanel {

    public static final String JS_FUNCTION = "executeSubCommand";
    public static final String PANEL_NAME = "SubControlPanel";

    private OnelineCommand[] subCommands = {
            new OnelineCommand("En_Dis_Sub",
                               CBCCommand.ENABLE_SUBBUS,
                               true,
                               OnelineUtil.ONELN_STATE_GROUP),
            new OnelineCommand("Confirm Open", CBCCommand.CONFIRM_OPEN),
            new OnelineCommand("Confirm Close", CBCCommand.CONFIRM_CLOSE),
            new OnelineCommand("Reset Opcount", CBCCommand.RESET_OPCOUNT),
            new OnelineCommand("Verify_All_Stop_Verify",
                               CBCCommand.CMD_ALL_BANKS,
                               true,
                               OnelineUtil.ONELN_VERIFY_GROUP),
            new OnelineCommand("Verify Failed And Questionable",
                               CBCCommand.CMD_FQ_BANKS,
                               true, // isDynamic
                               OnelineUtil.ONELN_VERIFY_GROUP,
                               true,// isUpdateColor
                               false),// isUpdateText
            new OnelineCommand("Verify Failed",
                               CBCCommand.CMD_FAILED_BANKS,
                               true,
                               OnelineUtil.ONELN_VERIFY_GROUP,
                               true,
                               false),
            new OnelineCommand("Verify Questionable",
                               CBCCommand.CMD_QUESTIONABLE_BANKS,
                               true,
                               OnelineUtil.ONELN_VERIFY_GROUP,
                               true,
                               false) 
            };

    public SubControlPanel(OnelineObject o, String n) {
        super(o);

        // setup commands
        setCommands(subCommands);
        setPanelName(PANEL_NAME);
        setJSFunction(JS_FUNCTION);
        setLabelName(((OnelineSub) o).getSubBusMsg().getCcName());
        setFileName(n);
        create();
    }

}
