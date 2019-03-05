package com.cannontech.dr.itron.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.yukon.IDatabaseCache;

enum ItronDataCategory {
    EVENT_CAT_NIC_EVENT,
    EVENT_CAT_PROGRAM_EVENTS
}

public class ItronDeviceDataParser {
    
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private AttributeService attributeService;
    
    private static final Logger log = YukonLogManager.getLogger(ItronDeviceDataParser.class);

    private class ItronData {
        private List<PointData> pointData;
        private long maxRecordId;
        
        ItronData(List<PointData> pointData, long maxRecordId) {
            this.setPointData(pointData);
            this.setMaxRecordId(maxRecordId);
        }
        
        public List<PointData> getPointData() {
            return pointData;
        }
        public long getMaxRecordId() {
            return maxRecordId;       
        }

        public void setMaxRecordId(long maxRecordId) {
            this.maxRecordId = maxRecordId;
        }

        public void setPointData(List<PointData> pointData) {
            this.pointData = pointData;
        }
    }
    
    void parseAndSend(File file) throws FileNotFoundException, IOException {
        long maxRecordId = getMaxRecordId();
        ItronData data = parseData(file, maxRecordId);
        dataSource.putValues(data.getPointData());
        setMaxRecordId(data.getMaxRecordId());
    }

    private void setMaxRecordId(long maxRecordId) {
        persistedSystemValueDao.setValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID, maxRecordId);
    }

    private ItronData parseData(File file, long maxRecordId) throws FileNotFoundException, IOException {
      List<PointData> pointDataList = new ArrayList<>();
      
      try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file))) {
          CSVReader csvReader = new CSVReader(inputStreamReader);
          csvReader.readNext(); //First read to get column headers
          String[] row = csvReader.readNext(); //Get first row of csv;
          while (row != null) { 
              int currentRecordId = Integer.parseInt(row[0]);
              if (currentRecordId > maxRecordId) {
                  maxRecordId = currentRecordId;
                  pointDataList.add(generatePointData(row));
              }
              row = csvReader.readNext();
          }
          csvReader.close();
      }
      
      return new ItronData(pointDataList, maxRecordId);
    }

    private PointData generatePointData(String[] rowData) {
        ItronDataCategory category = ItronDataCategory.valueOf(rowData[1]);
        String eventTime = rowData[3];//ISO 8601 YYYY-MM-DD
        String source = rowData[5];//Mac address
        String[] text = rowData[9].split(",");
        long eventId = 0;
        long payload = 0;
        
        switch(category) {
        case EVENT_CAT_NIC_EVENT:
            byte[] decoded = null;
            for(int i = 0; i < text.length; i+=2) {
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
                    String[] second = tuple[1].split("data(");
                    String hexString = second[1].trim().replaceAll("[^0-9]+", "");
                    try {
                        decoded = Hex.decodeHex(hexString.toCharArray());
                    } catch (DecoderException e) {
                        log.warn("caught exception in generatePointData", e);
                    }
                }
            }
            
            ItronDataEventType event = ItronDataEventType.getFromHex(eventId);//This could be null if the mapping doesn't exist.
            if (event != null) {
                ByteBuffer wrapped = ByteBuffer.wrap(decoded);
                int deviceId = deviceDao.getDeviceIdFromMacAddress(source);
                LiteYukonPAObject lpo = serverDatabaseCache.getAllPaosMap().get(deviceId);
                //building the point
                LitePoint lp = attributeService.getPointForAttribute(lpo, event.getAttribute(wrapped));
                PointData pointData = event.getPointData(wrapped, lp);
                pointData.setTimeStamp(new DateTime(eventTime).toDate());
                return pointData;
            }
        case EVENT_CAT_PROGRAM_EVENTS:
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
        }
        
        PointData pointData = new PointData();
        pointData.setTimeStamp(new DateTime(eventTime).toDate());
        return pointData;
    }

    private long getMaxRecordId() {
        return persistedSystemValueDao.getLongValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID);
    }
    
}
