package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface VeeReviewEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletePointValue(int changeId, 
                                 @Arg(EventLogArgEnum.pointValue) double value,
                                 @Arg(EventLogArgEnum.pointDate) Date timestamp,
                                 @Arg(EventLogArgEnum.paoName) String paoName,
                                 @Arg(EventLogArgEnum.paoType) PaoType paoType,
                                 @Arg(EventLogArgEnum.pointId) int pointId,
                                 @Arg(EventLogArgEnum.pointType) PointType pointType,
                                 @Arg(EventLogArgEnum.pointOffset) int pointOffset,
                                 @Arg(EventLogArgEnum.username) LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptPointValue(int changeId,
                                 @Arg(EventLogArgEnum.pointValue) double value,
                                 @Arg(EventLogArgEnum.pointDate) Date timestamp,
                                 @Arg(EventLogArgEnum.paoName) String paoName,
                                 @Arg(EventLogArgEnum.paoType) PaoType paoType,
                                 @Arg(EventLogArgEnum.pointId) int pointId,
                                 @Arg(EventLogArgEnum.pointType) PointType pointType,
                                 @Arg(EventLogArgEnum.pointOffset) int pointOffset,
                                 @Arg(EventLogArgEnum.username) LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void updateQuestionableQuality(int changeId,
                                          @Arg(EventLogArgEnum.pointValue) double value,
                                          @Arg(EventLogArgEnum.pointDate) Date timestamp,
                                          @Arg(EventLogArgEnum.paoName) String paoName,
                                          @Arg(EventLogArgEnum.paoType) PaoType paoType,
                                          @Arg(EventLogArgEnum.pointId) int pointId,
                                          @Arg(EventLogArgEnum.pointType) PointType pointType,
                                          @Arg(EventLogArgEnum.pointOffset) int pointOffset,
                                          @Arg(EventLogArgEnum.username) LiteYukonUser user);
}
