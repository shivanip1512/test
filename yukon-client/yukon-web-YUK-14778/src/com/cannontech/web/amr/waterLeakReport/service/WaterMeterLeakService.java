package com.cannontech.web.amr.waterLeakReport.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.waterLeakReport.model.WaterLeakReportFilter;
import com.cannontech.web.amr.waterLeakReport.model.WaterMeterLeak;

public interface WaterMeterLeakService {
    
    List<WaterMeterLeak> getLeaks(WaterLeakReportFilter filter, YukonUserContext userContext);

    List<WaterMeterLeak> getIntervalLeaks(WaterLeakReportFilter filter, YukonUserContext userContext);

    List<WaterMeterLeak> getLeaks(Iterable<? extends YukonPao> devices, 
            Range<Instant> range, 
            boolean includeDisabledPaos,
            double threshold, 
            YukonUserContext userContext);
}
