package com.cannontech.amr.meter.dao.impl;

import java.sql.SQLException;

import org.springframework.jdbc.core.SqlProvider;

import com.cannontech.amr.meter.model.IedMeter;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class MeterRowMapper implements YukonRowMapper<YukonMeter>, SqlProvider {

    private final String sql = "SELECT ypo.paObjectId, ypo.paoName, ypo.type, ypo.disableFlag, " +
                                "dmg.meterNumber, dcs.address, dr.routeId, rypo.paoName as route, " +
                                "cypo.paoName as port, ddcs.portId, serialNumber, manufacturer, model "+
                                "from YukonPaObject ypo " + 
                                "join Device d on ypo.paObjectId = d.deviceId " + 
                                "join DeviceMeterGroup dmg on d.deviceId = dmg.deviceId " + 
                                "left join DeviceCarrierSettings dcs on d.deviceId = dcs.deviceId " + 
                                "LEFT JOIN DeviceDirectCommSettings ddcs ON d.deviceId = ddcs.deviceId " + 
                                "left join DeviceRoutes dr on d.deviceId = dr.deviceId " + 
                                "left join YukonPaObject rypo on dr.routeId = rypo.paObjectId " +
                                "LEFT JOIN YukonPaObject cypo ON ddcs.portId = cypo.paObjectId " +
                                "LEFT JOIN RFNAddress rfna ON rfna.deviceId = d.deviceid";

    @Override
    public YukonMeter mapRow(YukonResultSet rs) throws SQLException {

        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("paObjectId",  "type");

        String paoName = rs.getString("paoName");
        String meterNumber = rs.getString("meterNumber");
        boolean disabled = rs.getBoolean("disableFlag");;

        if (paoIdentifier.getPaoType().isRfn()) {
            String serialNumber = rs.getString("serialNumber");
            String manufacturer = rs.getString("manufacturer");
            String model = rs.getString("model");
            
            RfnIdentifier rfnIdentifier = new RfnIdentifier(serialNumber, manufacturer, model);
            return new RfnMeter(paoIdentifier, rfnIdentifier, meterNumber, paoName, disabled);
        } else if (paoIdentifier.getPaoType().isPlc()) {
            String address = rs.getString("address");
            String routeName = rs.getString("route");
            int routeId = rs.getInt("routeId");
            return new PlcMeter(paoIdentifier, meterNumber, paoName, disabled, routeName, routeId, address);
        } else if (paoIdentifier.getPaoType().isIed()) {
            IedMeter iedMeter = new IedMeter(paoIdentifier, meterNumber, paoName, disabled);
            iedMeter.setPortId(rs.getInt("portId"));
            iedMeter.setPort(rs.getString("port"));
            return iedMeter;
        } else {
            return null;
        }
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}