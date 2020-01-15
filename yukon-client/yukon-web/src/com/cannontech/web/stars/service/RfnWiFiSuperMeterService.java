package com.cannontech.web.stars.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.stars.gateway.model.SuperMeterInfo;

public class RfnWiFiSuperMeterService {

    private static final Logger log = YukonLogManager.getLogger(RfnWiFiSuperMeterService.class);

    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    public List<SuperMeterInfo> getWiFiSuperMetersForVirtualGateway(List<Integer> gatewaysIds) {
        // Select all the devices in DynamicRfnDeviceData that have the virtualGateway as their gateway
        Collection<RfnDevice> superMeters = getSuperMeterList(gatewaysIds);
        // Turn the list of RfnDevices into SuperMeterInfo objects
        List<SuperMeterInfo> superMeterInfo = superMeters.stream()
                                                         .map(this::buildSuperMeterInfoObject)
                                                         .filter(Objects::nonNull)
                                                         .collect(Collectors.toList());

        return superMeterInfo;
    }

    private List<RfnDevice> getSuperMeterList(List<Integer> gatewaysIds) {
        SqlStatementBuilder allGatewaysSql = buildSuperMeterSelect(gatewaysIds);

        return jdbcTemplate.query(allGatewaysSql, TypeRowMapper.INTEGER).stream()
                                                                        .map(rfnDeviceDao::getDeviceForId)
                                                                        .collect(Collectors.toList());
    }

    private SqlStatementBuilder buildSuperMeterSelect(List<Integer> gatewaysIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM DynamicRfnDeviceData");
        sql.append("WHERE GatewayId").in(gatewaysIds);
        sql.append("ORDER BY LastTransferTime DESC");

        return sql;
    }

    private SuperMeterInfo buildSuperMeterInfoObject(RfnDevice rfnDevice) {
        PaoIdentifier paoIdentifier = rfnDevice.getPaoIdentifier();
        LitePoint commStatusPointId = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPointId = attributeService.findPointForAttribute(paoIdentifier,
                BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);

        SuperMeterInfo superMeterInfo = new SuperMeterInfo(paoIdentifier, commStatusPointId, rssiPointId);
        log.debug("Created SuperMeterInfo object for {}", superMeterInfo);

        return superMeterInfo;
    }

}
