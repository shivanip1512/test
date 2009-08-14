package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;

public interface BulkFieldBackgroupProcessResultHolder extends BackgroundProcessResultHolder {

	public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders();
}
