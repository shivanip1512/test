package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;

public class LoadGroupPicker extends DatabasePaoPicker {

    @Override
    protected void updateFilters(List<SqlFilter> filters, List<PostProcessingFilter<UltraLightPao>> postFilters,
            String extraArgs, YukonUserContext userContext) {

        PaoType programType = PaoType.valueOf(extraArgs);

        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {

                SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
                statementBuilder.append("Type").in(PaoType.getAllLMGroupTypes());
                return statementBuilder;
            }
        };

        filters.add(filter);

        if (programType != null) {
            postFilters.add(new PostProcessingFilter<UltraLightPao>() {
                @Override
                public List<UltraLightPao> process(List<UltraLightPao> objectsFromDb) {
                    List<UltraLightPao> lightPaos = new ArrayList<>();

                    objectsFromDb.forEach(group -> {
                        PaoType loadGroupType = PaoType.getForDbString(group.getType());
                        if (loadGroupType.isLoadGroup()) {

                            boolean isSepProgram = programType == PaoType.LM_SEP_PROGRAM;
                            boolean isEcobeeProgram = programType == PaoType.LM_ECOBEE_PROGRAM;
                            boolean isHoneywellProgram = programType == PaoType.LM_HONEYWELL_PROGRAM;
                            boolean isNestProgram = programType == PaoType.LM_NEST_PROGRAM;
                            boolean isItronProgram = programType == PaoType.LM_ITRON_PROGRAM;
                            boolean isMeterDisconnectProgram = programType == PaoType.LM_METER_DISCONNECT_PROGRAM;

                            if (isSepProgram && loadGroupType == PaoType.LM_GROUP_DIGI_SEP) {
                                lightPaos.add(group);
                            } else if ((!isSepProgram && !isGroupSepCompatible(loadGroupType))
                                    && (!isEcobeeProgram && !isGroupEcobeeCompatible(loadGroupType))
                                    && (!isHoneywellProgram && !isGroupHoneywellCompatible(loadGroupType))
                                    && (!isItronProgram && !isGroupItronCompatible(loadGroupType))
                                    && (!isNestProgram && !isGroupNestCompatible(loadGroupType))
                                    && (!isMeterDisconnectProgram && !isGroupMeterDisconnectCompatible(loadGroupType))) {
                                lightPaos.add(group);
                            } else if (isEcobeeProgram && isGroupEcobeeCompatible(loadGroupType)) {
                                lightPaos.add(group);
                            } else if (isHoneywellProgram && isGroupHoneywellCompatible(loadGroupType)) {
                                lightPaos.add(group);
                            } else if (isNestProgram && isGroupNestCompatible(loadGroupType)) {
                                lightPaos.add(group);
                            } else if (isItronProgram && isGroupItronCompatible(loadGroupType)) {
                                lightPaos.add(group);
                            } else if (isMeterDisconnectProgram && isGroupMeterDisconnectCompatible(loadGroupType)) {
                                lightPaos.add(group);
                            }
                        }
                    });
                    return lightPaos;
                }
            });
        }
    }

    private boolean isGroupSepCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_DIGI_SEP;
    }

    private boolean isGroupEcobeeCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_ECOBEE;
    }

    private boolean isGroupHoneywellCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_HONEYWELL;
    }

    private boolean isGroupNestCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_NEST;
    }

    private boolean isGroupItronCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_ITRON;
    }
    
    private boolean isGroupMeterDisconnectCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_METER_DISCONNECT;
    }
}
