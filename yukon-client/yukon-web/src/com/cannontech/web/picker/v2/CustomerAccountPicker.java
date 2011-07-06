package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightCustomerAccount;
import com.google.common.collect.Lists;

public class CustomerAccountPicker extends LucenePicker<UltraLightCustomerAccount> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.customerAccount.";

        OutputColumn column = new OutputColumn("accountNumber", titleKeyPrefix + "accountNumber");
        column.setMaxCharsDisplayed(40);
        columns.add(column);

        columns.add(new OutputColumn("accountId", titleKeyPrefix + "accountId"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    @Override
    public String getIdFieldName() {
        return "accountId";
    }

    @Override
    protected String getLuceneIdFieldName() {
        return "accountId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}
