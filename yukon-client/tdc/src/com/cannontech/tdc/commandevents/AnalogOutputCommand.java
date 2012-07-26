package com.cannontech.tdc.commandevents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.message.util.Command;
import com.cannontech.tdc.roweditor.SendData;

public class AnalogOutputCommand {

    public static void send(long pointID, double outputValue) {

        //  Start building the Command.opArgList() 
        List<Integer> data = new ArrayList<Integer>(2);
        data.add((int) pointID);
        data.add((int) outputValue);

        Command cmd = new Command();
        cmd.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        cmd.setOperation(Command.ANALOG_OUTPUT_REQUEST);
        cmd.setOpArgList(data);
        cmd.setTimeStamp(new Date());

        SendData.getInstance().sendCommandMsg(cmd);
    }
}
