package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.yukon.IDatabaseCache;

public class AllMetersGroupProvider extends CompleteBinningDeviceGroupProviderBase<String> {
    
    @Autowired public IDatabaseCache dbCache;
    @Autowired public ConfigurationSource configurationSource;
    public static enum MeterGroup  {
        PLC("All PLC Meters"),
        RF("All RFN Meters"),
        RFW("All RFW Meters"),;
        private final String name;
        
        private MeterGroup(String s){
            name = s;
        }
        
        public String toString() {
            return name;
        }
    }
    
    @Override
    protected List<String> getAllBins() {
        return Stream.of(MeterGroup.values()).map(Enum::toString).collect(Collectors.toList());
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        PaoClass paoClass = device.getPaoIdentifier().getPaoType().getPaoClass();
        if (paoClass.equals(PaoClass.RFMESH)||paoClass.equals(PaoClass.CARRIER)){
            String type = device.getPaoIdentifier().getPaoType().getDbString();
            if (type.matches("RFW*")){
                return Collections.singleton(MeterGroup.RFW.toString());
            }
            else if (type.matches("RFN*")) {
                return Collections.singleton(MeterGroup.RF.toString());
            }
            else if (paoClass.equals(PaoClass.CARRIER)) {
                return Collections.singleton(MeterGroup.PLC.toString());
            }
        }
        
        return Collections.emptySet();
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        String rfTemplatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (MeterGroup.RF.toString().equals(bin)) {
            sql.append("SELECT ypo.paobjectid");
            sql.append("FROM Device d");
            sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
            sql.append("WHERE ypo.Type").startsWith("RFN-%");
            sql.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix+"%");

        }
        else if (MeterGroup.RFW.toString().equals(bin)) {
            sql.append("SELECT ypo.paobjectid");
            sql.append("FROM Device d");
            sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
            sql.append("WHERE ypo.Type").startsWith("RFW%");
            sql.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix+"%");
        }
        else if (MeterGroup.PLC.toString().equals(bin)) {
            sql.append("SELECT ypo.paobjectid");
            sql.append("FROM Device d");
            sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
            sql.append("WHERE ypo.PAOClass").eq(PaoClass.CARRIER.getDbString());
        }
        return sql;
    }
}
