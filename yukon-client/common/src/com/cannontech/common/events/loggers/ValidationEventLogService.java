package com.cannontech.common.events.loggers;

import org.joda.time.DateTime;

import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ValidationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rphValidation.validator")
    public void unreasonableValueCausedReRead(PaoIdentifier paoIdentifier, PointIdentifier pointIdentifier);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void validationEngineStartup(long lastChangeIdProcessed, int tagsCleared);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.validator")
    public void changedQualityOnPeakedValue(PaoIdentifier paoIdentifier, int changeId);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEngineReset(LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void validationEnginePartialReset(DateTime since, LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void deletedAllTaggedRows(String string, LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rphValidation.helper")
    public void acceptedAllTaggedRows(String string, LiteYukonUser yukonUser);

}
