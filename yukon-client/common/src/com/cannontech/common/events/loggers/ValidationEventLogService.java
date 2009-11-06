package com.cannontech.common.events.loggers;

import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface ValidationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rphValidation.validator")
    public void unreasonableValueCausedReRead(PaoIdentifier paoIdentifier, PointIdentifier pointIdentifier);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void validationEngineStartup(long lastChangeIdProcessed, int tagsCleared);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void changedQualityOnPeakedValue(PaoIdentifier paoIdentifier, int changeId);

}
