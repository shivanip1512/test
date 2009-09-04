package com.cannontech.dr.loadgroup.service;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;

public interface LoadGroupService extends
        ObjectMapper<DisplayablePao, LMDirectGroupBase> {
    public ObjectMapper<DisplayablePao, LMDirectGroupBase> getMapper();

    public DatedObject<LMGroupBase> findDatedGroup(int loadGroupId);

    public DisplayablePao getLoadGroup(int loadGroupId);
    
    public RowMapperWithBaseQuery<DisplayablePao> getRowMapper();
}
