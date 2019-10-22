package com.cannontech.dr.itron.service.impl;

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
import org.joda.time.Instant;
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
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
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
                    log.debug("Parsed " + pointValues.values().size() + " point values for " + 
                              pointValues.keySet().size() + " devices.");
                    dataSource.putValues(pointValues.values());
                    allPointValues.putAll(pointValues);
                    hasData = true;
                } catch (EmptyImportFileException e) {
                    log.info(file.getName() + " in zip " + zip.getName() + " is empty.", e);
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
     */
    private Multimap<PaoIdentifier, PointData> parseData(InputStream stream) throws Exception {
        
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
                  pointValues.putAll(generatePointData(row));
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
            log.trace("Parsing row data: [" + String.join(",", Arrays.asList(rowData)) + "]");
        }
        
        Multimap<PaoIdentifier, PointData> pointValues = HashMultimap.create();
        ItronDataCategory category = ItronDataCategory.valueOf(rowData[1]);
        String eventTime = rowData[3];//ISO 8601 YYYY-MM-DD
        String source = rowData[5];//Mac address
        String[] text = rowData[9].split(",");//Tokenized CSV text
        long eventId = 0;
        
        switch(category) {
        case EVENT_CAT_NIC_EVENT:
            byte[] decodedData = null;
            for(int i = 0; i < text.length; i++) {
                Map.Entry<String, String> entry = getEntry(text[i]);
                if (entry.getKey().equals("log event id")) {
                    //sample: text = log event ID: 32907 (0x808b)
                    //tuple (log event ID, 32907 (0x808b)
                    eventId = Long.parseLong(entry.getValue().trim().split(" ")[0]);
                } else if (entry.getKey().equals("payload")) {
                    //sample: payload: data(0500000000)
                    //sample: payload: Event ID (5250009) data(00501BD900)
                    //Entry ["payload", "sometimes-present-ignored-text data(0500000000)"];
                    try {
                        String[] data = entry.getValue().split("data\\(");
                        if (data.length >= 2) {
                            String hexString = data[1].trim().replaceAll("[^0-9a-fA-F]+", "");
                            decodedData = Hex.decodeHex(hexString.toCharArray());
                        } else {
                            log.trace("Ignoring payload with no data.");
                        }
                    } catch (Exception e) {
                        if (log.isTraceEnabled()) {
                            log.trace("Caught exception in generatePointData", e);
                        } else {
                            log.warn("Caught exception in generatePointData: " + e.getMessage() + ". Enable TRACE logging for details.");
                        }
                    }
                }
            }
            
            ItronDataEventType event = ItronDataEventType.getFromHex(eventId); //This could be null if the mapping doesn't exist.
            if (event != null) {
                // Get the device from the Mac Address
                LiteYukonPAObject pao;
                try {
                    int deviceId = deviceDao.getDeviceIdFromMacAddress(source);
                    pao = serverDatabaseCache.getAllPaosMap().get(deviceId);
                } catch (NotFoundException e) {
                    log.debug("Ignoring data for unknown device with MAC " + source);
                    break;
                }
                
                // Determine the appropriate attribute, and find or create the related point
                BuiltInAttribute attribute = null;
                try {
                    attribute = event.getAttribute(decodedData);
                } catch (IllegalArgumentException e) {
                    log.warn("No attribute matches " + event + " with data " + Arrays.toString(decodedData) + ". Ignoring.");
                    break;
                }
                LitePoint litePoint = attributeService.createAndFindPointForAttribute(pao, attribute);
                
                // Update Asset Availability and Recent Event Participation, then add the point data to the list to
                // return.
                double currentValue = dataSource.getPointValue(litePoint.getPointID()).getValue();
                Optional<PointData> optionalPointData = event.getPointData(decodedData, currentValue, eventTime , litePoint);
                final byte[] decodedFinal = decodedData;
                optionalPointData.ifPresent(pointData -> {
                    updateRecentEventParticipation(pointData, event, pao, decodedFinal);
                    updateAssetAvailability(pointData, pao);
                    updatePointDataWithLitePointSettings(pointData, litePoint);
                    pointValues.put(pao.getPaoIdentifier(), pointData);
                });
            } else if (log.isTraceEnabled()){
                log.trace("No mapping for event ID " + eventId);
            }
            break;
        case EVENT_CAT_PROGRAM_EVENTS:
            //This event type is sent from Itron, but we are not parsing it because we can get the same information from EVENT_CAT_NIC_EVENTs
            break;
        default:
            log.trace("Unknown Event Category: " + rowData[1]);
            break;
        }
        
        return pointValues;
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
     * If the event is a control event (control start or control stop), this updates the recent event participation data 
     * for the PAO.
     */
    private void updateRecentEventParticipation(PointData pointData, ItronDataEventType event, LiteYukonPAObject pao, byte[] data) {
        if (event.isControlEventType()) {
            log.debug("EventId: " + event.decode(data) + ", EventType: "
                      + event.name() + ", deviceId: " + pao.getPaoIdentifier().getPaoId());
            EventPhase phase = EventPhase.COMPLETED;
            if (event == ItronDataEventType.EVENT_STARTED) {
                phase = EventPhase.PHASE_1;
            }
            recentEventParticipationService.updateDeviceControlEvent((int) event.decode(data),
                                                                     pao.getPaoIdentifier().getPaoId(),
                                                                     phase, 
                                                                     new Instant(pointData.getMillis()));
        }
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
    EVENT_CAT_PROGRAM_EVENTS
}
