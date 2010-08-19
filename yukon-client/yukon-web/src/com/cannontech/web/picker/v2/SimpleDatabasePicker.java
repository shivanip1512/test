package com.cannontech.web.picker.v2;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.google.common.base.Function;
import com.google.common.base.Functions;

public abstract class SimpleDatabasePicker<T> extends DatabasePicker<T, T> {
    protected SimpleDatabasePicker(RowMapperWithBaseQuery<T> rowMapper,
            String[] searchColumnNames) {
        super(rowMapper, searchColumnNames);
    }

    protected Function<T, T> getTypeTranslator() {
        return Functions.identity();
    }
}
