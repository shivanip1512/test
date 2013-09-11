package com.cannontech.dr.program.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramKwSavingsField extends ProgramBackingFieldBase {

    @Autowired EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "KW_SAVINGS";
    }

    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return estimatedLoadService.getKwSavings(program.getPaoIdentifier());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMProgramBase program1 = getProgramFromYukonPao(pao1);
                LMProgramBase program2 = getProgramFromYukonPao(pao2);
                if (program1 == program2) {
                    return 0;
                }
                if (program1 == null) {
                    return 1;
                }
                if (program2 == null) {
                    return -1;
                }
                Double state1 = 0.0;  // TO BE DETERMINED
                Double state2 = 0.0;
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }       

}
