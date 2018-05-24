package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface PointEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataDeleted(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String value, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataUpdated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String oldValue, String newValue, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataAdded(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String value, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointCreated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointUpdated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointDeleted(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsCreateInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsCreateCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsCreateCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsUpdateInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsUpdateCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsUpdateCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsDeleteInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsDeleteCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointsDeleteCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
}
