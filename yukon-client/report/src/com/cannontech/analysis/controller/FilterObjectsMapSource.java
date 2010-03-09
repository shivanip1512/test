package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;

import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public interface FilterObjectsMapSource {
    LinkedHashMap<ReportFilter, List<? extends Object>> getFilterObjectsMap(int userId);
}