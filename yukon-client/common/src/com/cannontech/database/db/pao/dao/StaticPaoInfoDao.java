package com.cannontech.database.db.pao.dao;

import com.cannontech.common.pao.PaoInfo;

public interface StaticPaoInfoDao {

    public String getValue(PaoInfo paoInfoKey, int paoId);

    public void saveValue(PaoInfo paoInfoKey, int paoId, String value);

    public Integer getPaoIdForKeyValue(PaoInfo paoInfoKey, String value);
}
