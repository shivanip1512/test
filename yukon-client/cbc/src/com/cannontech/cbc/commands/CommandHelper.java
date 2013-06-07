package com.cannontech.cbc.commands;

import java.util.Date;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.model.BankMoveBean;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.messaging.message.capcontrol.BankMoveMessage;
import com.cannontech.messaging.message.capcontrol.ChangeOpStateMessage;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.message.capcontrol.CommandType;
import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifyInactiveBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifySelectedBankMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

public class CommandHelper {
    
    public static CommandMessage buildCommand(CommandType type, LiteYukonUser user) {
        
        CommandMessage command = new CommandMessage();
        command.setCommandId(type.getCommandId());
        command.setUserName(user.getUsername());
        
        return command;
    }

    public static ItemCommandMessage buildItemCommand(int commandId, int paoId, LiteYukonUser user) {
      
        ItemCommandMessage command = new ItemCommandMessage();
        command.setCommandId(commandId);
        command.setItemId(paoId);
        command.setUserName(user.getUsername());
        
        return command;
    }
    
    public static ChangeOpStateMessage buildChangeOpStateCommand(LiteYukonUser user, int bankId, BankOpState state) {
        ChangeOpStateMessage command = new ChangeOpStateMessage(bankId);
        command.setUserName(user.getUsername());
        command.setState(state);
        
        return command;
    }
    
    public static BankMoveMessage buildBankMove(LiteYukonUser user, BankMoveBean bean, boolean permanentMove) {
        
        BankMoveMessage command = new BankMoveMessage();
        
        command.setUserName(user.getUsername());
        command.setItemId(bean.getBankId());
        
        command.setNewFeederId(bean.getNewFeederId());
        command.setOldFeederId(bean.getOldFeederId());
        command.setPermanentMove(permanentMove);
        command.setDisplayOrder(bean.getDisplayOrder());
        command.setTripOrder(bean.getTripOrder());
        command.setCloseOrder(bean.getCloseOrder());
        
        return command;
    }
    
    public static VerifyBanksMessage buildVerifyBanks(LiteYukonUser user, CommandType type, int itemId, boolean disableOvUv) {
        VerifyBanksMessage command = new VerifyBanksMessage();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setItemId(itemId);
        command.setDisableOvUv(disableOvUv);
        
        return command;
    }
    
    public static VerifyBanksMessage buildStopVerifyBanks(LiteYukonUser user, CommandType type, int itemId) { 
        VerifyBanksMessage command = new VerifyBanksMessage();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setItemId(itemId);
        command.setDisableOvUv(false);
        
        return command;
    }
    
    public static VerifyInactiveBanksMessage buildVerifyInactiveBanks(LiteYukonUser user, 
                                                               CommandType type, 
                                                               int itemId, 
                                                               boolean disableOvUv, 
                                                               long inactiveTime) {
        
        VerifyInactiveBanksMessage command = new VerifyInactiveBanksMessage();
        command.setItemId(itemId);
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setDisableOvUv(disableOvUv);
        command.setCbInactivityTime(inactiveTime);
        
        return command;
    }
    
    public static VerifySelectedBankMessage buildVerifySelectedBank(LiteYukonUser user, 
                                                               CommandType type, 
                                                               int itemId, 
                                                               int bankId,
                                                               boolean disableOvUv) {
        
        VerifySelectedBankMessage command = new VerifySelectedBankMessage();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setDisableOvUv(disableOvUv);
        command.setBankId(bankId);
        command.setItemId(itemId);
        
        return command;
    }
    
    public static PointDataMessage buildManualStateChange(LiteYukonUser user, int pointId, int itemId, int state) {
        Date now = new Date();
        PointDataMessage command = new PointDataMessage();
        command.setId(pointId);
        command.setPointQuality(PointQuality.Manual);
        command.setStr("Manual change occurred using CBC Web Client");
        command.setTime(now);
        command.setTimeStamp(now);
        command.setType(PointTypes.STATUS_POINT);
        command.setUserName(user.getUsername());
        command.setValue(state);
        
        return command;
    }
    
    public static PointDataMessage buildResetOpCount(LiteYukonUser user, int pointId, int itemId, int count) {
        Date now = new Date();
        PointDataMessage command = new PointDataMessage();
        command.setId(pointId);
        command.setPointQuality(PointQuality.Manual);
        command.setStr("Capacitor Bank OP_COUNT change from CBC Client");
        command.setTime(now);
        command.setTimeStamp(now);
        command.setType(PointTypes.ANALOG_POINT);
        command.setUserName(user.getUsername());
        command.setValue(count);
        
        return command;
    }
  
}