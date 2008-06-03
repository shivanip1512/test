package com.cannontech.web.updater.bulk;

import com.cannontech.common.bulk.service.BulkOperationCallbackResults;

public interface BulkResultValueAccessor {

    public Object getValue(BulkOperationCallbackResults bulkOperationCallbackResults);
}
