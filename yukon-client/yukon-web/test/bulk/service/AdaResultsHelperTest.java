package bulk.service;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.ReadSequence;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.bulk.ada.service.AdaResultsHelper;
import com.google.common.collect.Lists;

public class AdaResultsHelperTest {
    
    @Test
    public void buildBarsTest() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.MCT410CL);
        
        Analysis analysis = new Analysis();
        // 1 hour analyisis
        DateTime analysisStart = new DateTime(2010, 1, 1, 12, 0, 0, 0);
        DateTime analysisEnd = new DateTime(2010, 1, 1, 13, 0, 0, 0);
        analysis.setDateTimeRange(new Interval(analysisStart, analysisEnd));
        // 15 min interval
        analysis.setIntervalPeriod(Period.minutes(15));
        int barWidth = 400; 
        
        // archive data: M,M,M,M
        List<ArchiveData> archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_MISSING, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_MISSING, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_MISSING, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        
        // device archive data
        List<DeviceArchiveData> datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        List<ReadSequence> timeline = datas.get(0).getTimeline();
        Assert.assertEquals(1, timeline.size());
        Assert.assertEquals(400, timeline.get(0).getWidth());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(0).getColor());
        
        // archive data: P,M,M,M
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_PRESENT, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_MISSING, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_MISSING, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(2, timeline.size());
        Assert.assertEquals(100, timeline.get(0).getWidth());
        Assert.assertEquals(300, timeline.get(1).getWidth());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(1).getColor());
        
        // archive data: M,P,M,M
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_MISSING, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_PRESENT, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_MISSING, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(3, timeline.size());
        Assert.assertEquals(100, timeline.get(0).getWidth());
        Assert.assertEquals(100, timeline.get(1).getWidth());
        Assert.assertEquals(200, timeline.get(2).getWidth());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(1).getColor());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(2).getColor());
        
        // archive data: M,M,P,M
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_MISSING, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_MISSING, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_PRESENT, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(3, timeline.size());
        Assert.assertEquals(200, timeline.get(0).getWidth());
        Assert.assertEquals(100, timeline.get(1).getWidth());
        Assert.assertEquals(100, timeline.get(2).getWidth());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(1).getColor());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(2).getColor());
        
        // archive data: M,M,M,P
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_MISSING, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_MISSING, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_MISSING, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_PRESENT, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(2, timeline.size());
        Assert.assertEquals(300, timeline.get(0).getWidth());
        Assert.assertEquals(100, timeline.get(1).getWidth());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(1).getColor());
        
        // archive data: P,P,M,M
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_PRESENT, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_PRESENT, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_MISSING, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        
       
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(2, timeline.size());
        Assert.assertEquals(200, timeline.get(0).getWidth());
        Assert.assertEquals(200, timeline.get(1).getWidth());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(1).getColor());

        // archive data: M,P,P,M
        archiveData = Lists.newArrayList();
        archiveData.add(new ArchiveData(new Interval(analysisStart, analysisStart.plus(Duration.standardMinutes(15))), ReadType.DATA_MISSING, 0L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(15)), analysisStart.plus(Duration.standardMinutes(30))), ReadType.DATA_PRESENT, 1L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(30)), analysisStart.plus(Duration.standardMinutes(45))), ReadType.DATA_PRESENT, 2L));
        archiveData.add(new ArchiveData(new Interval(analysisStart.plus(Duration.standardMinutes(45)), analysisStart.plus(Duration.standardMinutes(60))), ReadType.DATA_MISSING, 3L));
        datas = Lists.newArrayList();
        datas.add(new DeviceArchiveData(paoIdentifier, BuiltInAttribute.LOAD_PROFILE, archiveData, analysis.getDateTimeRange()));
        

        
        AdaResultsHelper.buildBars(analysis, barWidth, datas);
        timeline = datas.get(0).getTimeline();
        Assert.assertEquals(3, timeline.size());
        Assert.assertEquals(100, timeline.get(0).getWidth());
        Assert.assertEquals(200, timeline.get(1).getWidth());
        Assert.assertEquals(100, timeline.get(2).getWidth());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(0).getColor());
        Assert.assertEquals(ReadType.DATA_PRESENT.name(), timeline.get(1).getColor());
        Assert.assertEquals(ReadType.DATA_MISSING.name(), timeline.get(2).getColor());
    }

}
