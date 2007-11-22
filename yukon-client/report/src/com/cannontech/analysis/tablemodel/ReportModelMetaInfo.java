package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface ReportModelMetaInfo {

    public LinkedHashMap<String, String> getMetaInfo(LiteYukonUser user);

}
