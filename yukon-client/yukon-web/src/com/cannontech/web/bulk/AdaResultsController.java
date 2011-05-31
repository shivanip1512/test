package com.cannontech.web.bulk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.bulk.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.model.ArchiveData;
import com.cannontech.web.bulk.model.DeviceArchiveData;
import com.cannontech.web.bulk.model.DeviceCollectionCreationException;
import com.cannontech.web.bulk.model.ReadType;
import com.cannontech.web.bulk.service.AdaResultsHelper;
import com.google.common.collect.Lists;

@Controller
public class AdaResultsController {
    
    private final static int BAR_WIDTH = 400;
    private AdaResultsHelper adaResultsHelper;
    
    @RequestMapping("intervalDataAnalysis/results")
    public String view(ModelMap model, int analysisId, HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
//        Analysis analysis = analysisDao.getForId(analysisId);
//        List<DeviceArchiveData> data = someDao.getSomeData(analysisId);
        
        
        // Test data 
        Analysis analysis = new Analysis();
        analysis.setAttribute(BuiltInAttribute.LOAD_PROFILE);
        analysis.setIntervalLength(Duration.standardMinutes(15));
        analysis.setExcludeBadPointQualities(true);
        
        List<DeviceArchiveData> data = Lists.newArrayList();
        
        DeviceCollection collection = new ListBasedDeviceCollection() {

            @Override
            public List<SimpleDevice> getDeviceList() {
                List<SimpleDevice> list = Lists.newArrayList();
                list.add(new SimpleDevice(1988, PaoType.MCT410IL));
                list.add(new SimpleDevice(2011, PaoType.MCT410IL));
                list.add(new SimpleDevice(2628, PaoType.MCT410IL));
                return null;
            }

            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", "idList");
                paramMap.put("idList.ids", "1988,2011,2628");

                return paramMap;
            }

            @Override
            public long getDeviceCount() {
                return 3;
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.idList");
            }
            
        };
        
        ReadType[] readtypes = new ReadType[]{ReadType.DATA_PRESENT, ReadType.DATA_MISSING};
        
        int n = (int)(50.0 * Math.random()) + 30;
        int m = (int)(3000.0 * Math.random()) + 300;
        
        model.addAttribute("intervals", m);
        
        Instant analysisStart = new Instant().minus(Duration.standardMinutes(m * 15));
        Instant analysisEnd = new Instant();
        Interval archiveRange = new Interval(analysisStart, analysisEnd);
        analysis.setDateTimeRange(archiveRange);
        
        int previousD = 0;
        for (int i = 0; i < n; i++) {
            
            DeviceArchiveData mct = new DeviceArchiveData();
            mct.setId(new PaoIdentifier(1988, PaoType.MCT410IL));
            mct.setArchiveRange(archiveRange);
            
            List<ArchiveData> mctResults = Lists.newArrayList();
            
            for (int j = 0; j < m; j++) {
                int d = (int)(20.0 * Math.random());
                if (d >= 2) d = previousD;
                
                Instant intervalStart = analysisStart.plus(Duration.standardMinutes(j * 15));
                Instant intervalEnd = analysisStart.plus(Duration.standardMinutes((j + 1) * 15));
                ArchiveData archiveData = new ArchiveData(new Interval(intervalStart, intervalEnd), readtypes[d]);
                mctResults.add(archiveData);
                previousD = d;
            }
            mct.setArchiveData(mctResults);
            data.add(mct);
        }
        
       // End test data
        
        ArchiveAnalysisResult result = adaResultsHelper.buildResults(analysis, BAR_WIDTH, data, request);
        
        // test data
        result.setDeviceCollection(collection);
        // end text data
        
        if (analysis.getAttribute().isProfile()) {
            model.addAttribute("showReadOption", true);
        }
        
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        model.addAttribute("result", result);
        
        model.addAttribute("barWidth", BAR_WIDTH);
        
        return "intervalDataAnalysis/results.jsp";
    }

    @Autowired
    public void setAdaResultsHelper(AdaResultsHelper adaResultsHelper) {
        this.adaResultsHelper = adaResultsHelper;
    }
    
}