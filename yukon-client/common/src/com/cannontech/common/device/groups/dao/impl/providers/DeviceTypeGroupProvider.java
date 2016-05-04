package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;


public class DeviceTypeGroupProvider extends CompleteBinningDeviceGroupProviderBase<String> {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;

    @Override
    protected List<String> getAllBins() {
        Set<String> paoTypes = dbCache.getAllPaoTypes().stream().filter(new Predicate<PaoType>() {
            @Override
            public boolean test(PaoType pao) {
                return pao.getPaoCategory() == PaoCategory.DEVICE && pao != PaoType.SYSTEM;
            }
        }).map(new Function<PaoType, String>() {
            @Override
            public String apply(PaoType pao) {
                return pao.getDbString();
            }
        }).collect(Collectors.toSet());

        return new ArrayList<String>(paoTypes);
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        PaoType paoType = PaoType.getForDbString(bin);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type").eq_k(paoType);
        return sql;
    }
    
    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        String type = device.getPaoIdentifier().getPaoType().getDbString(); 
        return Collections.singleton(type);
    }
}
