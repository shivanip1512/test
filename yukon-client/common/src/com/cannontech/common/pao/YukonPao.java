package com.cannontech.common.pao;

import com.google.common.base.Function;

public interface YukonPao {
    Function<YukonPao, PaoIdentifier> TO_PAO_IDENTIFIER = new Function<YukonPao, PaoIdentifier>() {
        @Override
        public PaoIdentifier apply(YukonPao input) {
            return input.getPaoIdentifier();
        }
    };
    Function<YukonPao, Integer> TO_PAO_ID = new Function<YukonPao, Integer>() {
        @Override
        public Integer apply(YukonPao input) {
            return input.getPaoIdentifier().getPaoId();
        }
    };

    PaoIdentifier getPaoIdentifier();
}
