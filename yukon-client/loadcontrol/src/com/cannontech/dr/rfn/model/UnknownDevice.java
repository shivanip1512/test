package com.cannontech.dr.rfn.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public final class UnknownDevice implements YukonPao {

    private final YukonPao pao;
    private final UnknownStatus unknownStatus;

    public UnknownDevice(YukonPao pao, UnknownStatus unknownStatus) {
        this.pao = pao;
        this.unknownStatus = unknownStatus;
    }

    public YukonPao getPao() {
        return pao;
    }

    public UnknownStatus getUnknownStatus() {
        return unknownStatus;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return pao.getPaoIdentifier();
    }
}