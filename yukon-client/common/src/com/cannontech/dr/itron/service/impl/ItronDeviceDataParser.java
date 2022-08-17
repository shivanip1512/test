package com.cannontech.dr.itron.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.opencsv.CSVReader;

public class ItronDeviceDataParser {
    
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private AttributeService attributeService;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private RecentEventParticipationService recentEventParticipationService;

    private static final Logger log = YukonLogManager.getLogger(ItronDeviceDataParser.class);
    private static final String eventIdKey = "load control event status update - event";
    private static final String statusKey = "status";
    private static final DateTime y2k = new LocalDate(2000, 1, 1).toDateTimeAtStartOfDay();
    private static final Duration year = Duration.standardDays(365);

    public Multimap<PaoIdentifier, PointValueHolder> parseAndSend(ZipFile zip) throws EmptyImportFileException {
        if(zip == null) {
            log.error("Unable to parse data, Itron file is null.");
            return null;
        }
        log.info("Parsing Itron data file {}", zip.getName());
        
        boolean hasData = false;
        Multimap<PaoIdentifier, PointValueHolder> allPointValues = HashMultimap.create();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while(entries.hasMoreElements()){
            try {
                ZipEntry file = entries.nextElement();
                InputStream stream = zip.getInputStream(file);
                try {
                    Multimap<PaoIdentifier, PointData> pointValues = parseData(stream);
                    log.debug("Parsed {} point values for {} devices.", pointValues.values().size(), pointValues.keySet().size());
                    dataSource.putValues(pointValues.values());
                    allPointValues.putAll(pointValues);
                    hasData = true;
                } catch (EmptyImportFileException e) {
                    log.info("Data file {} in zip {} is empty.", file.getName(), zip.getName());
                }
            } catch (Exception e) {
                log.error("Unable to parse Itron file", e);
            }
        }
        if(!hasData) {
            throw new EmptyImportFileException("Zip " + zip.getName() + " has only empty files.");
        }
        return allPointValues;
    }
    /**
     * Functional core of the parsing functionality of this file.
     * @throws IOException 
     * @throws EmptyImportFileException 
     */
    private Multimap<PaoIdentifier, PointData> parseData(InputStream stream) throws IOException, EmptyImportFileException {
        
      Multimap<PaoIdentifier, PointData> pointValues = HashMultimap.create();

      long maxRecordId = getMaxRecordId();
      
      try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
          CSVReader csvReader = new CSVReader(inputStreamReader);
          csvReader.readNext(); //First read to get column headers
          String[] row = csvReader.readNext(); // first row of csv
          if(row == null) {
              csvReader.close();
              throw new EmptyImportFileException("File is empty");
          }
          while (row != null) { 
              int currentRecordId = Integer.parseInt(row[0]);
              if (currentRecordId > maxRecordId) {
                  maxRecordId = currentRecordId;
                  try {
                      pointValues.putAll(generatePointData(row));
                  } catch (Exception e) {
                      log.error("Unexpected error parsing data row.", e);
                  }
              }
              row = csvReader.readNext();
          }
          csvReader.close();
      }
      
      setMaxRecordId(maxRecordId);
      return pointValues;
    }
    
    /**
     * Bulk of parsing logic is done here for the parser. 
     * @param rowData - parses single row of a csv entry
     * @param pointValues - return pointValues using this object
     */
    public Multimap<PaoIdentifier, PointData> generatePointData(String[] rowData) {
        if (log.isTraceEnabled()) {
            log.trace("Parsing row data: [{}]", String.join(",", Arrays.asList(rowData)));
        }
        
        Multimap<PaoIdentifier, PointData> pointValues = HashMultimap.create();
        ItronDataCategory category;
        try {
            category = ItronDataCategory.valueOf(rowData[1]);
        } catch (IllegalArgumentException e) {
            log.info("Unknown Itron data category: {}", rowData[1]);
            return pointValues;
        }
        String eventTime = rowData[3]; //ISO 8601 YYYY-MM-DD
        String source = rowData[5]; //Mac address
        String[] text = rowData[9].split(",");//Tokenized CSV text
        
        // Ignore data that is very old or very future
        if (!isReasonableEventTime(eventTime)) {
            log.warn("Ignoring data with invalid timestamp: {}", eventTime);
            return pointValues;
        }
        
        switch(category) {
            case EVENT_CAT_NIC_EVENT:
                pointValues = parseNicEvent(source, text, eventTime);
                break;
            case EVENT_CAT_PROGRAM_EVENTS:
                parseLoadControlEvent(source, text, eventTime);
                break;
            case EVENT_CAT_ESP:
                //This event type is not used.
                break;
        }
        
        return pointValues;
    }
    
    /**
     * Validate that this event timestamp is reasonable for valid data. Criteria are currently identical to 
     * RfnDataValidator.isTimestampValid.
     */
    private boolean isReasonableEventTime(String eventTimeString) {
        DateTime eventDateTime = new DateTime(eventTimeString);
        return eventDateTime.isAfter(y2k) && eventDateTime.isBefore(Instant.now().plus(year));
    }
    
    private Multimap<PaoIdentifier, PointData> parseNicEvent(String source, String[] text, String eventTime) {
        long eventId = 0;
        byte[] decodedData = null;
        Multimap<PaoIdentifier, PointData> pointValues = HashMultimap.create();
        
        for(int i = 0; i < text.length; i++) {
            Map.Entry<String, String> entry = getEntry(text[i]);
            if (keyMatches(entry, "log event id")) {
                //sample: text = log event ID: 32907 (0x808b)
                //tuple (log event ID, 32907 (0x808b)
                String[] chunkedData = entry.getValue().trim().split(" ");
                eventId = Long.parseLong(chunkedData[0]);
            } else if (keyMatches(entry, "payload")) {
                String[] data = entry.getValue().split("data\\(");
                decodedData = decodeData(data);
            }
        }
        
        if (decodedData == null) {
            log.trace("Ignoring EVENT_CAT_NIC_EVENT with no payload.");
            return pointValues;
        }
        
        ItronDataEventType event = ItronDataEventType.getFromHex(eventId); //This could be null if the mapping doesn't exist.
        if (event != null) {
            // Get the device from the Mac Address
            Optional<LiteYukonPAObject> paoFromMac = getPaoFromMac(source);
            if (paoFromMac.isEmpty()) {
                log.debug("Ignoring data for unknown device with MAC {}", source);
                return pointValues;
            }
            LiteYukonPAObject pao = paoFromMac.get();
            
            // Determine the appropriate attribute, and find or create the related point
            BuiltInAttribute attribute = null;
            try {
                attribute = event.getAttribute(decodedData);
            } catch (IllegalArgumentException e) {
                log.warn("No attribute matches {} with data {}. Ignoring.", event, Arrays.toString(decodedData));
                return pointValues;
            }
            
            LitePoint litePoint;
            try {
                litePoint = attributeService.createAndFindPointForAttribute(pao, attribute);
            } catch (Exception e) {
                log.debug("Cannot get point for {} on {}. {}", attribute, pao, e.getMessage());
                return pointValues;
            }
            
            // Update Asset Availability, then add the point data to the list to return.
            try {
                double currentValue = dataSource.getPointValue(litePoint.getPointID()).getValue();
                Optional<PointData> optionalPointData = event.getPointData(decodedData, currentValue, eventTime , litePoint);
                
                optionalPointData.ifPresent(pointData -> {
                    updateAssetAvailability(pointData, pao);
                    updatePointDataWithLitePointSettings(pointData, litePoint);
                    pointValues.put(pao.getPaoIdentifier(), pointData);
                    if (log.isDebugEnabled()) {
                        log.debug("Parsed point data for {} - {} ({} {}). Value: {} {}.", pao.getPaoName(), litePoint.getPointName(), litePoint.getPointTypeEnum(), litePoint.getPointOffset(), pointData.getValue(), pointData.getPointDataTimeStamp() );
                    }
                });
            } catch (Exception e) {
                log.error("Error processing point data: " + Arrays.toString(decodedData) + " for point " + litePoint, e);
            }
        } else if (log.isTraceEnabled()){
            log.trace("No mapping for event ID {}", eventId);
        }
        return pointValues;
    }
    
    private byte[] decodeData(String[] data) {
        //sample: payload: data(0500000000)
        //sample: payload: Event ID (5250009) data(00501BD900)
        //Entry ["payload", "sometimes-present-ignored-text data(0500000000)"];
        try {
            if (data.length >= 2) {
                String hexString = data[1].trim().replaceAll("[^0-9a-fA-F]+", "");
                return Hex.decodeHex(hexString.toCharArray());
            }
            log.trace("Ignoring payload with no data.");
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("Caught exception in generatePointData", e);
            } else {
                log.warn("Caught exception in generatePointData: {}. Enable TRACE logging for details.", e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Parse load control events. Example events look like:
     *  Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event Received. 
     *  Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c8, ESI: 00:13:50:05:00:92:84:42, Status: Event Started. 
     *  Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event cancelled.
     *  Load Control Event status update - Event: 361, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event Completed.
     * There are additional statuses that we are not handling, such as "event rejected" and "event state unknown".
     */
    private void parseLoadControlEvent(String mac, String[] data, String eventTimeString) {
        Optional<ItronLoadControlEventStatus> eventStatus = Optional.empty();
        Optional<Integer> eventId = Optional.empty();
        for(int i = 0; i < data.length; i++) {
            Map.Entry<String, String> entry = getEntry(data[i]);
            if (keyMatches(entry, statusKey)) {
                eventStatus = ItronLoadControlEventStatus.fromValue(entry.getValue());
            } else if (keyMatches(entry, eventIdKey)) {
                eventId = Optional.of(Integer.parseInt(entry.getValue().trim()));
            }
        }
        
        Instant eventTime = new Instant(eventTimeString);
        Optional<LiteYukonPAObject> pao = getPaoFromMac(mac);
        
        if (pao.isPresent() && eventStatus.isPresent() && eventId.isPresent()) {
            updateRecentEventParticipation(eventId.get(), eventStatus.get(), pao.get(), eventTime);
        } else {
            log.debug("Ignored load control event due to missing info. Pao: {}, eventStatus: {}, eventId: {}", pao, eventStatus, eventId);
        }
    }
    
    /**
     * Attempt to retrieve a pao based on its mac address.
     */
    private Optional<LiteYukonPAObject> getPaoFromMac(String mac) {
        try {
            int deviceId = deviceDao.getDeviceIdFromMacAddress(mac);
            return Optional.ofNullable(serverDatabaseCache.getAllPaosMap().get(deviceId));
        } catch (NotFoundException e) {
            log.debug("Ignoring data for unknown device with MAC {}", mac);
            log.trace("Exception: ", e);
            return Optional.empty();
        }
    }
    
    /**
     * Update the PointData with the point ID, point type and quality from the lite point; and force archive.
     */
    private void updatePointDataWithLitePointSettings(PointData pointData, LitePoint litePoint) {
        pointData.setId(litePoint.getLiteID());
        pointData.setType(litePoint.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTagsPointMustArchive(true);
    }
    
    /**
     * Updates the Asset Availability "last communication time" for the PAO, based on the point data.
     */
    private void updateAssetAvailability(PointData pointData, LiteYukonPAObject pao) {
        AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(pao.getPaoIdentifier().getPaoId());
        times.setLastCommunicationTime(new Instant(pointData.getTimeStamp()));
        dynamicLcrCommunicationsDao.insertData(times);
    }
    
    /**
     * Updates the Recent Event Participation based on a load control event.
     */
    private void updateRecentEventParticipation(int eventId, ItronLoadControlEventStatus eventStatus, LiteYukonPAObject pao, Instant timestamp) {
        int paoId = pao.getPaoIdentifier().getPaoId();
        recentEventParticipationService.updateDeviceControlEvent(eventId, paoId, eventStatus, timestamp);
    }
    
    /**
     * Checks to see if the key String of the entry matches an expected key.
     */
    private boolean keyMatches(Map.Entry<String, String> entry, String expectedKey) {
        return entry.getKey().equalsIgnoreCase(expectedKey);
    }
    
    /**
     * Converts a colon-separated key/value String into an Entry object.
     */
    private Map.Entry<String, String> getEntry(String text) {
        String[] tuple = text.split(":");
        String key = tuple[0].trim().toLowerCase();
        String value = null;
        if (tuple.length >= 2) {
            value = tuple[1];
        } else {
            log.trace("Parsed entry with no value.");
        }
        return new AbstractMap.SimpleEntry<>(key, value);
    }
    
    /**
     * Get persisted itron record Id
     */
    private long getMaxRecordId() {
        return persistedSystemValueDao.getLongValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID);
    }
    
    /**
     * Set persisted itron record Id
     */
    private void setMaxRecordId(long maxRecordId) {
        persistedSystemValueDao.setValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID, maxRecordId);
    }

}

enum ItronDataCategory {
    EVENT_CAT_NIC_EVENT,
    EVENT_CAT_PROGRAM_EVENTS,
    EVENT_CAT_ESP
}
