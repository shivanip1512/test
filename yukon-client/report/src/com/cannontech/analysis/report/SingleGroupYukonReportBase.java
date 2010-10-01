package com.cannontech.analysis.report;

import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.google.common.collect.Lists;

public abstract class SingleGroupYukonReportBase extends GroupYukonReportBase {

    public SingleGroupYukonReportBase(BareReportModel bareModel) {
        super(bareModel);
    }

    protected final List<ColumnLayoutData> getMultiGroupFieldData() {
        List<ColumnLayoutData> groupFieldData = Lists.newArrayList(getGroupFieldData());
        return groupFieldData;
    }

    /**
     * This method is here for the sole purpose of supporting older reports.
     * @return
     */
    protected abstract ColumnLayoutData getGroupFieldData();

}
