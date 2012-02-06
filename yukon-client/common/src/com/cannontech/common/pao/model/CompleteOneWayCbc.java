package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;

@YukonPao(tableBacked=false, 
          paoTypes={PaoType.CBC_7010, PaoType.CBC_7011, PaoType.CBC_7012, PaoType.CBC_EXPRESSCOM, 
                    PaoType.CBC_FP_2800, PaoType.CAPBANKCONTROLLER})
public class CompleteOneWayCbc extends CompleteCbcBase {
    // This page intentionally left blank.
}
