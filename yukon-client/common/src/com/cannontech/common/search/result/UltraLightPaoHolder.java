package com.cannontech.common.search.result;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public class UltraLightPaoHolder implements YukonPao {
    private UltraLightPao ultraLightPao;

    public UltraLightPaoHolder(UltraLightPao ultraLightPao) {
        this.ultraLightPao = ultraLightPao;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(ultraLightPao.getPaoId(),
                                 PaoType.getForDbString(ultraLightPao.getType()));
    }

    public UltraLightPao getUltraLightPao() {
        return ultraLightPao;
    }
}
