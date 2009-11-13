package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface VeeReviewEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.review")
    public void deletePointValue(int changeId, double value, Date timestamp, int type, LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.review")
    public void acceptPointValue(int changeId, double value, Date timestamp, int type, LiteYukonUser user);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.review")
    public void updateQuestionableQuality(int changeId, double value, Date timestamp, int type, LiteYukonUser user);
}
