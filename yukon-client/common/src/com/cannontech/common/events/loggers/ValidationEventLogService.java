package com.cannontech.common.events.loggers;

import org.joda.time.DateTime;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface ValidationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rphValidation.validator")
    public void unreasonableValueCausedReRead(@Arg(EventLogArgEnum.paoId) int paoId, 
                                              @Arg(EventLogArgEnum.paoName) String paoName, 
                                              @Arg(EventLogArgEnum.paoType) PaoType paoType, 
                                              @Arg(EventLogArgEnum.pointId) int pointId, 
                                              @Arg(EventLogArgEnum.pointType) PointType pointType, 
                                              @Arg(EventLogArgEnum.pointOffset) int pointOffset);
//////////////
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void validationEngineStartup(@Arg(EventLogArgEnum.username) long lastChangeIdProcessed,
                                        @Arg(EventLogArgEnum.username) int tagsCleared);
////////
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void changedQualityOnPeakedValue(int changeId, 
                                            @Arg(EventLogArgEnum.paoId) int paoId, 
                                            @Arg(EventLogArgEnum.paoName) String paoName, 
                                            @Arg(EventLogArgEnum.paoType) PaoType paoType, 
                                            @Arg(EventLogArgEnum.pointId) int pointId, 
                                            @Arg(EventLogArgEnum.pointType) PointType pointType, 
                                            @Arg(EventLogArgEnum.pointOffset) int pointOffset);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEngineReset(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEnginePartialReset(DateTime validationResetDate, 
                                             @Arg(EventLogArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletedAllTaggedRows(@Arg(EventLogArgEnum.tagSet) String tagSet, 
                                     @Arg(EventLogArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptedAllTaggedRows(@Arg(EventLogArgEnum.tagSet) String tagSet, 
                                      @Arg(EventLogArgEnum.username) LiteYukonUser yukonUser);

}
