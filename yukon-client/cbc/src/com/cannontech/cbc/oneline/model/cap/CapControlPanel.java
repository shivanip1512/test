package com.cannontech.cbc.oneline.model.cap;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.OnelineCommand;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;

public class CapControlPanel extends BaseControlPanel {
    public static final String JS_FUNCTION = "executeCapBankCommand";
    public static final String PANEL_NAME = "CapControlPanel";
    private List<OnelineCommand> manualCommands;

    private OnelineCommand[] systemCommands = {
            new OnelineCommand("Confirm", CBCCommand.OPEN_CAPBANK),
            new OnelineCommand("Open", CBCCommand.OPEN_CAPBANK),
            new OnelineCommand("Close", CBCCommand.CLOSE_CAPBANK),
            new OnelineCommand("Enable OVUV", CBCCommand.BANK_ENABLE_OVUV),
            new OnelineCommand("Disable OVUV", CBCCommand.BANK_DISABLE_OVUV),
            new OnelineCommand("Scan 2-Way Device", CBCCommand.SCAN_2WAY_DEV) };
    private CapOnelineCommandTable manCommandTable;
    private CapOnelineCommandTable sysCommandTable;

    public CapControlPanel(OnelineObject o) {
        super(o);
        setCommands(getManualCommands());
        setPanelName(PANEL_NAME + "_" + o.getPaoId());
        setJSFunction(JS_FUNCTION);
        int currFdrIdx = ((OnelineCap) o).getDrawing().getFeeders().size() - 1;
        Feeder f = ((Feeder) getSubBusMsg().getCcFeeders().get(currFdrIdx));
        int currCapIdx = ((OnelineCap) o).getCurrentCapIdx();
        CapBankDevice c = (CapBankDevice) f.getCcCapBanks().get(currCapIdx);
        String labelName = c.getCcName();
        setLabelName(labelName);
        create();
    }

    @Override
    public void create() {
        List<String> addParam = new ArrayList<String>(1);
        addParam.add("true");

        StringBuffer b = new StringBuffer(getLabelName());
        b.append(":Manual Commands");
        Point2D point1 = getReferenceLn().getPoint1();
        createCommandTable(manCommandTable, point1,
                           getManualCommands(),
                           OnelineUtil.stringToList(b.toString()),
                           addParam);
        addParam.set(0, "false");
        Point p = new Point ((int)point1.getX() + 200, (int)point1.getY());
        createCommandTable(sysCommandTable,
                           p, systemCommands,
                           OnelineUtil.stringToList("null:System Commands"),
                           addParam);

    }

    private void createCommandTable(CapOnelineCommandTable commandTable,
            Point2D point2D, OnelineCommand[] commands, List<String> labelName,
            List<String> addParams) {
        commandTable = new CapOnelineCommandTable(commands, this, point2D);
        commandTable.setAddParams(addParams);
        commandTable.setPanelName(getPanelName());
        String function = getJSFunction();
        commandTable.setJSFunction(function);
        commandTable.setLabelNames(labelName);
        commandTable.drawTable(getDrawing());
    }



    public OnelineCommand[] getManualCommands() {
        if (manualCommands == null) {
            manualCommands = new ArrayList<OnelineCommand>(0);
            OnelineCommand endis = new OnelineCommand("EN_DIS_CAP",
                                                      CBCCommand.ENABLE_CAPBANK,
                                                      true, OnelineUtil.ONELN_STATE_GROUP);
            OnelineCommand reset = new OnelineCommand("Reset Opcount",
                                                      CBCCommand.RESET_OPCOUNT);
            manualCommands.add(endis);
            manualCommands.add(reset);
            LiteState[] stateNames = CBCDisplay.getCBCStateNames();
            for (int i = 0; i < stateNames.length; i++) {
                LiteState state = stateNames[i];
                OnelineCommand c = new OnelineCommand(state.getStateText(),
                                                      state.getStateRawState());
                manualCommands.add(c);
            }
        }
        return manualCommands.toArray(new OnelineCommand[manualCommands.size()]);
    }

    public CapOnelineCommandTable getManCommandTable() {
        return manCommandTable;
    }

    public CapOnelineCommandTable getSysCommandTable() {
        return sysCommandTable;
    }

}
