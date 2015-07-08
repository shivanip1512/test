package com.cannontech.common.bulk.collection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.iterator.CsvColumnReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory.FileMapperEnum;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.Device;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Sets;

public class DeviceFileUploadCollectionProducer implements DeviceCollectionProducer {
    private final Logger log = YukonLogManager.getLogger(DeviceFileUploadCollectionProducer.class);
    
    @Autowired private ObjectMapperFactory objectMapperFactory;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionProducer;
    @Autowired private DeviceDao deviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private IDatabaseCache databaseCache;
    
    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.fileUpload;
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request) throws ServletRequestBindingException {

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                throw new RuntimeException("Not multipart");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

            MultipartFile dataFile = mRequest.getFile(getSupportedType().getParameterName("dataFile"));
            if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                throw new UnsupportedOperationException("Data file is not found.");
            }

            final String uploadTypeStr =
                ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("uploadType"));
            FileMapperEnum uploadType = FileMapperEnum.valueOf(uploadTypeStr);

            Set<String> data = readDataFile(dataFile, uploadType);
            DeviceResult result = findDevices(data, uploadType);

            DeviceCollection groupCollection =
                deviceGroupCollectionProducer.createDeviceGroupCollection(result.getDevices().iterator(),
                    dataFile.getOriginalFilename());

            return groupCollection;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase collectionBase) {
        throw new UnsupportedOperationException("This producer delegates to DeviceGroupCollectionProducer and should "
                                                + "not be persisted.");
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        throw new UnsupportedOperationException("This producer delegates to DeviceGroupCollectionProducer and should "
                                                + "not be persisted.");
    }
    
    
    class DeviceResult{
        private Set<SimpleDevice> devices;
        private Set<String> errorDevices;
        
        public DeviceResult(Set<SimpleDevice> devices, Set<String> errorDevices) {
           this.devices = devices;
           this.errorDevices = errorDevices;
        }

        public Set<SimpleDevice> getDevices() {
            return devices;
        }

        public Set<String> getErrorDevices() {
            return errorDevices;
        }
    }
    
    /**
     * Creates an iterator for iterating through a device collection file line by line.
     * @throws IOException 
     */
    private Set<String> readDataFile(MultipartFile dataFile, final FileMapperEnum uploadType) throws IOException{
        InputStream inputStream = dataFile.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);
        CsvColumnReaderIterator iterator = new CsvColumnReaderIterator(csvReader, 0);
        Set<String> data = new HashSet<>();
        try {
            if (uploadType.isHasHeader()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                data.add(iterator.next());
            }
        } finally {
            CtiUtilities.close(iterator);
        }
        return data;
    }

    /**
     * Finds devices by looking at cache.
     * 
     * @return DeviceResult - contains devices found and errors
     * @throws IOException
     */
    private DeviceResult findDevices(Set<String> data, FileMapperEnum uploadType) throws IOException {
        log.debug("Getting device by " + uploadType);
        log.debug("Data " + data.size()+ " rows");
        Set<String> errors = new HashSet<>(data);
        Set<SimpleDevice> devices = new HashSet<>();
        switch (uploadType) {
        case BULK:
        case ADDRESS:
            Set<Integer> addresses = Sets.newHashSet();
            for (String deviceIdStr : data) {
                try {
                    int address = Integer.parseInt(deviceIdStr);
                    if (address > -1) {
                        addresses.add(address);
                    }
                } catch (NumberFormatException e) {/* skip device */}
            }
            for (LiteYukonPAObject pao : databaseCache.getAllDevices()) {
                if (addresses.contains(pao.getAddress()) && isDeviceValid(pao)) {
                    devices.add(new SimpleDevice(pao));
                    errors.remove(Integer.toString(pao.getAddress()));
                }
            }
            break;
        case DEVICEID:
            Map<Integer, LiteYukonPAObject> mapByDeviceId = databaseCache.getAllPaosMap();
            for (String deviceIdStr : data) {
                try {
                    int deviceId = Integer.parseInt(deviceIdStr);
                    LiteYukonPAObject pao = mapByDeviceId.get(deviceId);
                    if (pao != null && isDeviceValid(pao)) {
                        devices.add(new SimpleDevice(pao));
                        errors.remove(deviceIdStr);
                    }
                } catch (NumberFormatException e) {/* skip device */}
            }
            break;
        case METERNUMBER:
            for (SimpleMeter meter : databaseCache.getAllMeters().values()) {
                if (data.contains(meter.getMeterNumber()) && isDeviceValid(meter)) {
                    devices.add(new SimpleDevice(meter));
                    errors.remove(meter.getMeterNumber());
                }
            }
            break;
        case PAONAME:
            for (LiteYukonPAObject pao : databaseCache.getAllDevices()) {
                if (data.contains(pao.getPaoName()) && isDeviceValid(pao)) {
                    devices.add(new SimpleDevice(pao));
                    errors.remove(pao.getPaoName());
                }
            }
            break;
        default:
            break;
        }
        log.debug("Found  " + devices.size() + " devices");
        log.debug("Found  " + errors.size() + " errors");
        return new DeviceResult(devices, errors);
    }

    private boolean isDeviceValid(YukonPao yukonPao) {
        PaoIdentifier paoId = yukonPao.getPaoIdentifier();
        if (paoId == null || paoId.getPaoId() == Device.SYSTEM_DEVICE_ID
            || paoId.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
            return false;
        }
        return true;
    }
}