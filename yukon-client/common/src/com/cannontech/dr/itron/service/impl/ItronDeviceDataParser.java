package com.cannontech.dr.itron.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.ItronDataEventType;
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
    
    private static final Logger log = YukonLogManager.getLogger(ItronDeviceDataParser.class);

    public Multimap<PaoIdentifier, PointValueHolder> parseAndSend(ZipFile zip) throws EmptyImportFileException {
        if(zip == null) {
            log.error("Unable to parse Itron file, no file found");
            //nothing to parse
            return null;
        }
        boolean hasData = false;
        Multimap<PaoIdentifier, PointValueHolder> allPointValues = HashMultimap.create();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while(entries.hasMoreElements()){
            try {
                ZipEntry file = entries.nextElement();
                InputStream stream = zip.getInputStream(file);
                try {
                    Multimap<PaoIdentifier, PointData> pointValues = parseData(stream);
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
          String[] row = csvReader.readNext(); //Get first row of csv;
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
    private Multimap<PaoIdentifier, PointData> generatePointData(String[] rowData) {
        Multimap<PaoIdentifier, PointData> pointValues = HashMultimap.create();
        ItronDataCategory category = ItronDataCategory.valueOf(rowData[1]);
        String eventTime = rowData[3];//ISO 8601 YYYY-MM-DD
        String source = rowData[5];//Mac address
        String[] text = rowData[9].split(",");
        long eventId = 0;
        long payload = 0;
        
        switch(category) {
        case EVENT_CAT_NIC_EVENT:
            byte[] decoded = null;
            for(int i = 0; i < text.length; i++) {
                String[] tuple = text[i].split(":");
                if(tuple[0].trim().toLowerCase().equals("log event id")) {
                    //sample: text = log event ID: 32907 (0x808b)
                    //tuple (log event ID, 32907 (0x808b)
                    eventId = Long.parseLong(tuple[1].trim().split(" ")[0]);
                }
                else if(tuple[0].trim().toLowerCase().equals("payload")) {
                    //sample: payload: data(0500000000)
                    //sample: payload: Event ID (5250009) data(00501BD900)
                    //tuple (payload, magicString data(0500000000);
                    String[] second = tuple[1].split("data\\(");
                    String hexString = second[1].trim().replaceAll("[^0-9a-fA-F]+", "");
                    try {
                        decoded = Hex.decodeHex(hexString.toCharArray());
                    } catch (DecoderException e) {
                        log.warn("caught exception in generatePointData", e);
                    }
                }
            }
            
            ItronDataEventType event = ItronDataEventType.getFromHex(eventId);//This could be null if the mapping doesn't exist.
            if (event != null) {
                int deviceId = deviceDao.getDeviceIdFromMacAddress(source);
                LiteYukonPAObject lpo = serverDatabaseCache.getAllPaosMap().get(deviceId);
                //building the point
                boolean pointExists = attributeService.createPointForAttribute(lpo, event.getAttribute(decoded));
                LitePoint lp = attributeService.findPointForAttribute(lpo, event.getAttribute(decoded));
                double currentValue = dataSource.getPointValue(lp.getLiteID()).getValue(); //Should I be using liteId or pointId?
                Optional<PointData> optionalPointData = event.getPointData(decoded, currentValue, eventTime);
                optionalPointData.ifPresent(pointData -> {
                    pointData.setId(lp.getLiteID());
                    pointData.setType(lp.getPointType());
                    pointData.setPointQuality(PointQuality.Normal);
                    pointData.setTagsPointMustArchive(true);
                    pointValues.put(lpo.getPaoIdentifier(), pointData);
                });
            }
        }
        
      /*  case EVENT_CAT_PROGRAM_EVENTS:
            for(int i = 0; i < text.length; i+=2) {
                String[] tuple = text[i].split(":");
                if(tuple[0].trim().toLowerCase().startsWith("Load Control Event")) {
                    //sample: Load Control Event status update - Event: 688
                    //tuple (log event ID, 32907 (0x808b)
                    eventId = Long.parseLong(tuple[1].trim().split(" ")[0]);
                }
                else if(tuple[0].trim().toLowerCase().equals("status")) {
                    //sample: Status: Event Completed. 
                    //tuple (status, Event Completed.
                    String value = tuple[1];
                }
            }
            break;
        }*/
        
        
        return pointValues;
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
