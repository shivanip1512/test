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
    public void deletePointValue(long changeId, 
                                 @Arg(ArgEnum.pointValue) double value,
                                 @Arg(ArgEnum.pointDate) Date timestamp,
                                 @Arg(ArgEnum.paoName) String paoName,
                                 @Arg(ArgEnum.paoType) PaoType paoType,
                                 @Arg(ArgEnum.pointId) int pointId,
                                 @Arg(ArgEnum.pointType) PointType pointType,
                                 @Arg(ArgEnum.pointOffset) int pointOffset,
                                 @Arg(ArgEnum.username) LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptPointValue(long changeId,
                                 @Arg(ArgEnum.pointValue) double value,
                                 @Arg(ArgEnum.pointDate) Date timestamp,
                                 @Arg(ArgEnum.paoName) String paoName,
                                 @Arg(ArgEnum.paoType) PaoType paoType,
                                 @Arg(ArgEnum.pointId) int pointId,
                                 @Arg(ArgEnum.pointType) PointType pointType,
                                 @Arg(ArgEnum.pointOffset) int pointOffset,
                                 @Arg(ArgEnum.username) LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void updateQuestionableQuality(long changeId,
                                          @Arg(ArgEnum.pointValue) double value,
                                          @Arg(ArgEnum.pointDate) Date timestamp,
                                          @Arg(ArgEnum.paoName) String paoName,
                                          @Arg(ArgEnum.paoType) PaoType paoType,
                                          @Arg(ArgEnum.pointId) int pointId,
                                          @Arg(ArgEnum.pointType) PointType pointType,
                                          @Arg(ArgEnum.pointOffset) int pointOffset,
                                          @Arg(ArgEnum.username) LiteYukonUser user);
}
