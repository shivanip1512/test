package com.cannontech.common.pao.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.common.pao.dao.PaoSelectionDao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Maps;

public class PaoSelectionDaoImpl implements PaoSelectionDao {
    private static class RowHandler implements RowCallbackHandler {
        private Map<Integer, PaoData> paoDataByIntegerId;
        private Set<OptionalField> neededData;

        private RowHandler(Map<Integer, PaoData> paoDataByIntegerId,
                           Set<OptionalField> neededData) {
            this.paoDataByIntegerId = paoDataByIntegerId;
            this.neededData = neededData;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int paoId = rs.getInt("PaobjectId");
            PaoData paoData = paoDataByIntegerId.get(paoId);
            if (neededData.contains(OptionalField.NAME)) {
                paoData.setName(rs.getString("PaoName"));
            }
            if (neededData.contains(OptionalField.ENABLED)) {
                paoData.setEnabled(!"Y".equals(rs.getString("DisableFlag")));
            }
            if (neededData.contains(OptionalField.METER_NUMBER)) {
                paoData.setMeterNumber(rs.getString("MeterNumber"));
            }
            if (neededData.contains(OptionalField.CARRIER_ADDRESS)) {
                Integer carrierAddress = rs.getInt("Address");
                if (rs.wasNull()) {
                    carrierAddress = null;
                }
                paoData.setCarrierAddress(carrierAddress);
            }
        }
    }

    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public void addNeededData(List<PaoData> paosNeedingData, Set<OptionalField> neededData) {
        if (neededData.isEmpty()) {
            // We've already got all we need.
            return;
        }

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        final boolean needsMeterNumber = neededData.contains(OptionalField.METER_NUMBER);
        final boolean needsCarrierAddress = neededData.contains(OptionalField.CARRIER_ADDRESS);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT YP.PaobjectId, YP.PaoName, YP.DisableFlag");
                if (needsMeterNumber) {
                    sql.append(", MG.MeterNumber");
                }
                if (needsCarrierAddress) {
                    sql.append(", CS.Address");
                }
                sql.append("FROM YukonPaobject YP");
                if (needsMeterNumber) {
                    sql.append("LEFT JOIN DeviceMeterGroup MG ON YP.PaobjectId = MG.DeviceId");
                }
                if (needsCarrierAddress) {
                    sql.append("LEFT JOIN DeviceCarrierSettings CS ON YP.PaobjectId = CS.DeviceId");
                }
                sql.append("WHERE YP.PaobjectId").in(subList);
                return sql;
            }
        };

        Map<Integer, PaoData> paoDataByIntegerId = Maps.newHashMap();
        for (PaoData paoData : paosNeedingData) {
            paoDataByIntegerId.put(paoData.getPaoId().getPaoId(), paoData);
        }
        RowHandler rowHandler = new RowHandler(paoDataByIntegerId, neededData);

        template.query(sqlGenerator, paoDataByIntegerId.keySet(), rowHandler);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
