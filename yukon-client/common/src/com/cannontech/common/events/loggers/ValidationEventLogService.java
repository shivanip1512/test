package com.cannontech.common.events.loggers;

import java.util.Date;

import org.joda.time.DateTime;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface ValidationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rphValidation.validator")
    public void unreasonableValueCausedReRead(@Arg(ArgEnum.paoId) int paoId, 
                                              @Arg(ArgEnum.paoName) String paoName, 
                                              @Arg(ArgEnum.paoType) PaoType paoType, 
                                              @Arg(ArgEnum.pointId) int pointId, 
                                              @Arg(ArgEnum.pointType) PointType pointType, 
                                              @Arg(ArgEnum.pointOffset) int pointOffset);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void validationEngineStartup(@Arg(ArgEnum.changeId) long lastChangeIdProcessed,
                                        int tagsCleared);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void changedQualityOnPeakedValue(@Arg(ArgEnum.changeId) long changeId, 
                                            @Arg(ArgEnum.paoId) int paoId, 
                                            @Arg(ArgEnum.paoName) String paoName, 
                                            @Arg(ArgEnum.paoType) PaoType paoType, 
                                            @Arg(ArgEnum.pointId) int pointId, 
                                            @Arg(ArgEnum.pointType) PointType pointType, 
                                            @Arg(ArgEnum.pointOffset) int pointOffset);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEngineReset(@Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEnginePartialReset(DateTime validationResetDate, 
                                             @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletedAllTaggedRows(@Arg(ArgEnum.tagSet) String tagSet, 
                                     @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptedAllTaggedRows(@Arg(ArgEnum.tagSet) String tagSet, 
                                      @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletePointValue(@Arg(ArgEnum.changeId) long changeId, 
                                 @Arg(ArgEnum.pointValue) double value,
                                 @Arg(ArgEnum.pointDate) Date timestamp,
                                 @Arg(ArgEnum.paoName) String paoName,
                                 @Arg(ArgEnum.paoType) PaoType paoType,
                                 @Arg(ArgEnum.pointId) int pointId,
                                 @Arg(ArgEnum.pointType) PointType pointType,
                                 @Arg(ArgEnum.pointOffset) int pointOffset,
                                 @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptPointValue(@Arg(ArgEnum.changeId) long changeId,
                                 @Arg(ArgEnum.pointValue) double value,
                                 @Arg(ArgEnum.pointDate) Date timestamp,
                                 @Arg(ArgEnum.paoName) String paoName,
                                 @Arg(ArgEnum.paoType) PaoType paoType,
                                 @Arg(ArgEnum.pointId) int pointId,
                                 @Arg(ArgEnum.pointType) PointType pointType,
                                 @Arg(ArgEnum.pointOffset) int pointOffset,
                                 @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void updateQuestionableQuality(@Arg(ArgEnum.changeId) long changeId,
                                          @Arg(ArgEnum.pointValue) double value,
                                          @Arg(ArgEnum.pointDate) Date timestamp,
                                          @Arg(ArgEnum.paoName) String paoName,
                                          @Arg(ArgEnum.paoType) PaoType paoType,
                                          @Arg(ArgEnum.pointId) int pointId,
                                          @Arg(ArgEnum.pointType) PointType pointType,
                                          @Arg(ArgEnum.pointOffset) int pointOffset,
                                          @Arg(ArgEnum.username) LiteYukonUser user);
}