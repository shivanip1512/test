package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;

import com.cannontech.user.YukonUserContext;

public interface ReportModelMetaInfo {

    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext);

}
