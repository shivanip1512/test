package com.cannontech.multispeak.service;

import com.cannontech.common.pao.YukonPao;

public interface MspIdentifiablePaoService {
    /**
     * get Object Id
     *
     * @param paoIdentifier
     * @return
     */
    public String getObjectId(YukonPao paoIdentifier);
}