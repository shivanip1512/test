package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DashboardEventLogService {
    
    //Assign a dashboard to users
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void dashboardAssigned(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                  int numUsersAssigned,
                                  @Arg(ArgEnum.dashboardName) String dashboardName);

    //Unassign a dashboard from users
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void dashboardUnassigned(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                    int numUsersUnassigned,
                                    @Arg(ArgEnum.dashboardName) String dashboardName);

    //Change dashboard owner
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void dashboardOwnerChanged(@Arg(ArgEnum.username) LiteYukonUser oldOwner,
                                      @Arg(ArgEnum.username) LiteYukonUser newOwner,
                                      @Arg(ArgEnum.dashboardName) String dashboardName);
    
    //Delete dashboard
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void dashboardDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(ArgEnum.dashboardName) String dashboardName);

    //Add a widget to a dashboard
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void widgetAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                            @Arg(ArgEnum.widgetType) String widgetType,
                            @Arg(ArgEnum.dashboardName) String dashboardName);
    
    //Remove a widget from a dashboard
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void widgetRemoved(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                              @Arg(ArgEnum.widgetType) String widgetType,
                              @Arg(ArgEnum.dashboardName) String dashboardName);
    
    //Edit dashboard description
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void nameChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                              @Arg(ArgEnum.dashboardName) String oldName,
                              @Arg(ArgEnum.dashboardName) String newName);
    
    //Edit dashboard description
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.tools.dashboard")
    public void descriptionChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                              String oldDescription,
                              String newDescription,
                              @Arg(ArgEnum.dashboardName) String dashboardName);
}
