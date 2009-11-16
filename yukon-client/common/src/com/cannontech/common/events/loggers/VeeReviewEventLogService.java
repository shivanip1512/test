package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface VeeReviewEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletePointValue(int changeId, double value, Date timestamp, String paoName, PaoType paoType, int pointId, PointType pointType, int pointOffset, LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptPointValue(int changeId, double value, Date timestamp, String paoName, PaoType paoType, int pointId, PointType pointType, int pointOffset, LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void updateQuestionableQuality(int changeId, double value, Date timestamp, String paoName, PaoType paoType, int pointId, PointType pointType, int pointOffset, LiteYukonUser user);
}
