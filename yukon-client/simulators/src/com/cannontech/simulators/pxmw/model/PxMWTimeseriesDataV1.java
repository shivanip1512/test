package com.cannontech.simulators.pxmw.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PxMWTimeseriesDataV1 {
    private Map<String, PxMWTimeSeriesResultV1> parsedChannels = new HashMap<>();
    private final Logger log = YukonLogManager.getLogger(PxMWDataV1.class);
    
    /**
     * Parses template with sample data
     */
    private void load() {
        if(!parsedChannels.isEmpty()) {
            return;
        }
        int fileNum = 1;
        while (true) {
            String path = "src/com/cannontech/simulators/pxmw/model/timeseries_data" + fileNum + ".json";
            File file = new File(path);
            if(!file.exists()) {
                break;
            }
            try {
                PxMWTimeSeriesDeviceResultV1[] template = new ObjectMapper().readValue(file, PxMWTimeSeriesDeviceResultV1[].class);
                parsedChannels.putAll(template[0].getResults()
                        .stream()
                        .collect(Collectors.toMap(t -> t.getTag(), t -> t)));
                log.info("Parsed file:{}", file);
                fileNum++;
            } catch (Exception e) {
                log.info("Error parsing:" + file, e);
            }
        }
        
        log.info("Parsed {} channels:{}", parsedChannels.size(), parsedChannels.keySet().stream().collect(Collectors.joining(",")));
        List<String> allChannels = Arrays.asList(MWChannel.values()).stream().map(value -> value.getChannelId().toString())
                .collect(Collectors.toList());
        log.info("Required {} channels:{}", allChannels.size(), allChannels.stream().collect(Collectors.joining(",")));
        Set<String> missingChannels = new HashSet<>(); 
        missingChannels.addAll(allChannels);
        missingChannels.removeAll(parsedChannels.keySet());
        log.info("Missing{} channels:{}", missingChannels.size(), missingChannels.stream().collect(Collectors.joining(",")));
    }
    
    /**
     * Creates result from the template
     */
    public List<PxMWTimeSeriesResultV1> getValues(List<String> tags, String timestamp) {
        load();

        List<PxMWTimeSeriesResultV1> result = new ArrayList<>();
        for (String tag : tags) {
            PxMWTimeSeriesResultV1 template = parsedChannels.get(tag);
            if (template == null) {
                continue;
            }
            DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
            long time = parser.parseDateTime(timestamp).getMillis();

            List<PxMWTimeSeriesValueV1> values = template.getValues().stream()
                    .map(t -> new PxMWTimeSeriesValueV1(time, t.getValue())).collect(Collectors.toList());

            result.add(new PxMWTimeSeriesResultV1(template.getTag(), template.getTrait(), values));
        }
        return result;
    }
    
    public void messupTheData(List<PxMWTimeSeriesDeviceResultV1> results) {
        if(results.isEmpty()) {
            return;
        }
        PxMWTimeSeriesDeviceResultV1 result = results.get(0);
        
        result.getResults().clear();
        
        if(results.size() == 1) {
            return;
        }
        
        result = results.get(1);
        
        PxMWTimeSeriesResultV1 detail = result.getResults().get(0);
        
        List<PxMWTimeSeriesValueV1> badValues = detail.getValues().stream().map(v -> new PxMWTimeSeriesValueV1(0, "Test"))
                .collect(Collectors.toList());
        detail.getValues().clear();
        detail.getValues().addAll(badValues);
        
        
        results.add(new PxMWTimeSeriesDeviceResultV1("Test", new ArrayList<>()));
       
    }
}
