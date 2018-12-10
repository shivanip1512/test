package com.cannontech.cbc.commands;

import java.util.Date;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.model.BankMoveBean;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.capcontrol.model.BankMove;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.capcontrol.model.VerifyInactiveBanks;
import com.cannontech.message.capcontrol.model.VerifySelectedBank;
import com.cannontech.message.dispatch.message.PointData;

public class CommandHelper {
    
    public static CapControlCommand buildCommand(CommandType type, LiteYukonUser user) {
        
        CapControlCommand command = new CapControlCommand();
        command.setCommandId(type.getCommandId());
        command.setUserName(user.getUsername());
        
        return command;
    }

    public static ItemCommand buildItemCommand(int commandId, int paoId, LiteYukonUser user) {
      
        ItemCommand command = new ItemCommand();
        command.setCommandId(commandId);
        command.setItemId(paoId);
        command.setUserName(user.getUsername());
        
        return command;
    }
    
    public static ChangeOpState buildChangeOpStateCommand(LiteYukonUser user, int bankId, BankOpState state) {
        ChangeOpState command = new ChangeOpState(bankId);
        command.setUserName(user.getUsername());
        command.setState(state);
        
        return command;
    }
    
    public static BankMove buildBankMove(LiteYukonUser user, BankMoveBean bean, boolean permanentMove) {
        
        BankMove command = new BankMove();
        
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
    
    public static VerifyBanks buildVerifyBanks(LiteYukonUser user, CommandType type, int itemId, boolean disableOvUv) {
        VerifyBanks command = new VerifyBanks();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setItemId(itemId);
        command.setDisableOvUv(disableOvUv);
        
        return command;
    }
    
    public static VerifyBanks buildStopVerifyBanks(LiteYukonUser user, CommandType type, int itemId) { 
        VerifyBanks command = new VerifyBanks();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setItemId(itemId);
        command.setDisableOvUv(false);
        
        return command;
    }
    
    public static VerifyInactiveBanks buildVerifyInactiveBanks(LiteYukonUser user, 
                                                               CommandType type, 
                                                               int itemId, 
                                                               boolean disableOvUv, 
                                                               long inactiveTime) {
        
        VerifyInactiveBanks command = new VerifyInactiveBanks();
        command.setItemId(itemId);
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setDisableOvUv(disableOvUv);
        command.setCbInactivityTime(inactiveTime);
        
        return command;
    }
    
    public static VerifySelectedBank buildVerifySelectedBank(LiteYukonUser user, 
                                                               CommandType type, 
                                                               int itemId, 
                                                               int bankId,
                                                               boolean disableOvUv) {
        
        VerifySelectedBank command = new VerifySelectedBank();
        command.setUserName(user.getUsername());
        command.setCommandId(type.getCommandId());
        command.setDisableOvUv(disableOvUv);
        command.setBankId(bankId);
        command.setItemId(itemId);
        
        return command;
    }
    
    public static PointData buildManualStateChange(LiteYukonUser user, int pointId, int itemId, int state) {
        Date now = new Date();
        PointData command = new PointData();
        command.setId(pointId);
        command.setPointQuality(PointQuality.Manual);
        command.setStr("Manual change occurred using CBC Web Client");
        command.setTime(now);
        command.setTimeStamp(now);
        command.setType(PointType.Status.getPointTypeId());
        command.setUserName(user.getUsername());
        command.setValue(state);
        
        return command;
    }
    
    public static PointData buildResetOpCount(LiteYukonUser user, int pointId, int itemId, int count) {
        Date now = new Date();
        PointData command = new PointData();
        command.setId(pointId);
        command.setPointQuality(PointQuality.Manual);
        command.setStr("Capacitor Bank OP_COUNT change from CBC Client");
        command.setTime(now);
        command.setTimeStamp(now);
        command.setType(PointType.Analog.getPointTypeId());
        command.setUserName(user.getUsername());
        command.setValue(count);
        
        return command;
    }
  
}