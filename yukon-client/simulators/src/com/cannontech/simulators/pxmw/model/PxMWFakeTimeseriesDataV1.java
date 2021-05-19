package com.cannontech.simulators.pxmw.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PxMWFakeTimeseriesDataV1 {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private Map<PaoType, Map<String, PxMWTimeSeriesResultV1>> channels = new HashMap<>();
    private final Logger log = YukonLogManager.getLogger(PxMWDataV1.class);

    /**
     * Parses template with sample data
     */
    private void load() {
        if (!channels.isEmpty()) {
            return;
        }
        channels.clear();

        load(PaoType.LCR6200C);
        load(PaoType.LCR6600C);
    }

    private void load(PaoType type) {
        Map<String, PxMWTimeSeriesResultV1> parsedChannels = new HashMap<>();

        int fileNum = 1;
        while (true) {
            String path = "src/com/cannontech/simulators/pxmw/model/timeseries_data_" + type + "_" + fileNum + ".json";
            File file = new File(path);
            if (!file.exists()) {
                break;
            }
            try {
                PxMWTimeSeriesDeviceResultV1[] template = new ObjectMapper().readValue(file,
                        PxMWTimeSeriesDeviceResultV1[].class);
                parsedChannels.putAll(template[0].getResults()
                        .stream()
                        .collect(Collectors.toMap(t -> t.getTag(), t -> t)));
                log.info("Parsed file:{}", file);
                fileNum++;
            } catch (Exception e) {
                log.info("Error parsing:" + file, e);
            }
        }

        log.info("{} Parsed {} channels:{}", type, parsedChannels.size(),
                parsedChannels.keySet().stream().collect(Collectors.joining(",")));
        List<String> allChannels = Arrays.asList(MWChannel.values()).stream().map(value -> value.getChannelId().toString())
                .collect(Collectors.toList());
        log.info("{} Required {} channels:{}", type, allChannels.size(), allChannels.stream().collect(Collectors.joining(",")));
        Set<String> missingChannels = new HashSet<>();
        missingChannels.addAll(allChannels);
        missingChannels.removeAll(parsedChannels.keySet());
        log.info("{} Missing {} channels:{}", type, missingChannels.size(),
                missingChannels.stream().collect(Collectors.joining(",")));
        channels.put(type, parsedChannels);
    }

    /**
     * Creates result from the template
     * if randomBadData is true creates bad data by randomly substituting date with 123 and value with the word "test"
     * @param deviceGuid 
     */
    public List<PxMWTimeSeriesResultV1> getValues(String deviceGuid, List<String> tags, PaoType type, boolean randomBadData) {
        Random random = new Random();
        load();
        
        
        List<String> externalEventIds = getExternalEventIds(deviceGuid);
                
        Map<String, PxMWTimeSeriesResultV1> parsedChannels = channels.get(type);
        List<PxMWTimeSeriesResultV1> result = new ArrayList<>();
        for (String tag : tags) {
            
            boolean isEventState = tag.equals(MWChannel.EVENT_STATE.getChannelId().toString());
            PxMWTimeSeriesResultV1 template = parsedChannels.get(tag);
            if (template == null) {
                continue;
            }

            List<PxMWTimeSeriesValueV1> values ;

            if (randomBadData) {
                values = template.getValues().stream()
                        .map(t -> new PxMWTimeSeriesValueV1(random.nextBoolean() ? 123 : t.getTimestamp(),
                                random.nextBoolean() ? "Test" : t.getValue()))
                        .collect(Collectors.toList());
            } else {
                Interval interval = new Interval();
                if(isEventState) {
                    if (!externalEventIds.isEmpty()) {
                        result.add(new PxMWTimeSeriesResultV1(template.getTag(), template.getTrait(),
                                List.of(new PxMWTimeSeriesValueV1(interval.now.getMillis() / 1000,
                                        getEventStateValue(externalEventIds)))));
                    }
                    continue;
                }
                
                values = template.getValues().stream()
                        .map(t -> {
                            PxMWTimeSeriesValueV1 value = new PxMWTimeSeriesValueV1(interval.now.getMillis() / 1000, t.getValue());
                            interval.now = interval.now.minusMinutes(5);
                            return value;
                        })
                        .collect(Collectors.toList());

                result.add(new PxMWTimeSeriesResultV1(template.getTag(), template.getTrait(), values));
            }
        }
        return result;
    }
    
    private String getEventStateValue(List<String> externalEventIds) {
        //"123,2;124,2"
        /* Received: 1, Started: 2 , Complete: 3. Canceled: 4 */
        return StringUtils.join(externalEventIds.stream().map(id -> id+",1;"+id+",2;"+id+",3;").collect(Collectors.toList()), ";");
    }

    public List<String> getExternalEventIds(String deviceGuid) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ExternalEventId");
        sql.append("FROM ControlEventDevice ced");
        sql.append("JOIN DeviceGuid dg ON ced.DeviceId = dg.DeviceId");
        sql.append("JOIN ControlEvent ce ON ce.ControlEventId = ced.ControlEventId");
        sql.append("WHERE Result").eq_k(ControlEventDeviceStatus.UNKNOWN);
        sql.append("AND dg.Guid").eq(deviceGuid);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }
    
    private static class Interval {
        DateTime now = DateTime.now();
    }
}
