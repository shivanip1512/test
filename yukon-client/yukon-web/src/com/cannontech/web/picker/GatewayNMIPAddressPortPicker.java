package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class GatewayNMIPAddressPortPicker extends DatabasePicker<GatewayPickerModel> {

	@Autowired private RfnGatewayService rfnGatewayService;

    private final static String[] searchColumnNames = new String[] { "paoName" };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("paoName", "yukon.web.picker.pao.name"));
        columns.add(new OutputColumn("nmIpAddress", "yukon.web.modules.operator.gateways.nmipaddress"));
        columns.add(new OutputColumn("nmPort", "yukon.web.modules.operator.gateways.nmport"));

        outputColumns = Collections.unmodifiableList(columns);
    }
    
    public GatewayNMIPAddressPortPicker() {
        super(new GatewayPickerRowMapper(), searchColumnNames);
    }
	
    @Override
    protected void updateFilters(List<SqlFilter> filters,
            List<PostProcessingFilter<GatewayPickerModel>> postFilters,
            String extraArgs, YukonUserContext userContext) {
        
        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                return new SqlStatementBuilder("Type").in(PaoType.getRfGatewayTypes());
            }
        };
        
        filters.add(filter);
        
        postFilters.add(new PostProcessingFilter<GatewayPickerModel>() {
            @Override
            public List<GatewayPickerModel> process(List<GatewayPickerModel> objectsFromDb) {
                List<GatewayPickerModel> lightPaos = new ArrayList<>();
                
                Set<RfnGateway> allGateways = rfnGatewayService.getAllGateways();

                objectsFromDb.forEach(gateway -> {
                	RfnGateway gatewayData = allGateways.stream().
                			filter(g -> g.getId() == gateway.getPaoId()).findFirst().get();
                	gateway.setNmIpAddress(gatewayData.getData().getNmIpAddress());
                	gateway.setNmPort(gatewayData.getData().getNmPort());
                	lightPaos.add(gateway);
                });
                return lightPaos;
            }
        });
    }
    
    public static class GatewayPickerRowMapper extends AbstractRowMapperWithBaseQuery<GatewayPickerModel> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            return new SimpleSqlFragment("SELECT paObjectId, paoName, type" +
                    " FROM yukonPAObject");
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            return new SimpleSqlFragment("ORDER BY LOWER(paoName)");
        }

        @Override
        public boolean needsWhere() {
            return true;
        }

        @Override
        public GatewayPickerModel mapRow(YukonResultSet rs)
                throws SQLException {
            final int paoId = rs.getInt("paObjectId");
            final String paoName = rs.getString("paoName");
            final String type = rs.getString("type");

            return new GatewayPickerModel() {

                @Override
                public int getPaoId() {
                    return paoId;
                }

                @Override
                public String getPaoName() {
                    return paoName;
                }

                @Override
                public String getType() {
                    return type;
                }};
        }
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
    @Override
    public String getIdFieldName() {
        return "paoId";
    }
    
    @Override
    protected String getDatabaseIdFieldName() {
        return "paobjectId";
    }

}
