package com.cannontech.common.events.loggers;

import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommChannelEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "device.commChannel")
    public void commChannelCreated(@Arg(ArgEnum.deviceName) String commChannelName,
                                   @Arg(ArgEnum.paoType) PaoType portType,
                                   @Arg(ArgEnum.baudRate) BaudRate baudRate,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "device.commChannel")
    public void commChannelUpdated(@Arg(ArgEnum.deviceName) String commChannelName,
                                   @Arg(ArgEnum.paoType) PaoType portType,
                                   @Arg(ArgEnum.baudRate) BaudRate baudRate,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "device.commChannel")
    public void commChannelDeleted(@Arg(ArgEnum.deviceName) String commChannelName,
                                   @Arg(ArgEnum.paoType) PaoType portType,
                                   @Arg(ArgEnum.baudRate) BaudRate baudRate,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

}
