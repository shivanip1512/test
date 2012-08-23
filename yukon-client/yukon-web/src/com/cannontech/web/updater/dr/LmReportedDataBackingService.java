package com.cannontech.web.updater.dr;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.util.DatedObject;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.dao.LmDeviceReportedDataDao;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.dao.LmReportedAddressRelay;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class LmReportedDataBackingService extends UpdateBackingServiceBase<LmReportedAddress> {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private LmDeviceReportedDataDao lmDeviceReportedDataDao;
    @Autowired @Qualifier("main") private Executor executor;
    
    private Map<Integer, DatedObject<LmReportedAddress>> currentAddresses = new ConcurrentHashMap<Integer, DatedObject<LmReportedAddress>>();

    @Override
    public DatedObject<LmReportedAddress> getDatedObject(int deviceId) {
        DatedObject<LmReportedAddress> datedAddress = currentAddresses.get(deviceId);
        return datedAddress;
    }
    
    @Override
    public Object getValue(DatedObject<LmReportedAddress> datedObject, String[] idBits, YukonUserContext context) {

        String value = null;
        
        String fieldName = idBits[1];
        AddressField field = AddressField.valueOf(fieldName);
        
        if (datedObject != null) {
            LmReportedAddress address = datedObject.getObject();
            
            switch (field) {
            
            case TIMESTAMP:
                value =  dateFormattingService.format(address.getTimestamp(), DateFormatEnum.DATEHM, context);
                break;
                
            case SPID:
                value =  Integer.toString(address.getSpid());
                break;
                
            case GEO:
                value =  Integer.toString(address.getGeo());
                break;
                
            case SUB:
                value =  Integer.toString(address.getSubstation());
                break;
                
            case FEEDER:
                value =  Integer.toString(address.getFeeder());
                break;
                
            case ZIP:
                value =  Integer.toString(address.getZip());
                break;
                
            case UDA:
                value =  Integer.toString(address.getUda());
                break;
                
            case RELAY_1_PROGRAM:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 0) {
                        value =  Integer.toString(relay.getProgram());
                        break;
                    }
                }
                break;
                
            case RELAY_1_SPLINTER:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 0) {
                        value =  Integer.toString(relay.getSplinter());
                        break;
                    }
                }
                break;
                
            case RELAY_2_PROGRAM:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 1) {
                        value =  Integer.toString(relay.getProgram());
                        break;
                    }
                }
                break;
                
            case RELAY_2_SPLINTER:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 1) {
                        value =  Integer.toString(relay.getSplinter());
                        break;
                    }
                }
                break;
                
            case RELAY_3_PROGRAM:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 2) {
                        value =  Integer.toString(relay.getProgram());
                        break;
                    }
                }
                break;
                
            case RELAY_3_SPLINTER:
                for (LmReportedAddressRelay relay : address.getRelays()) {
                    if (relay.getRelayNumber() == 2) {
                        value =  Integer.toString(relay.getSplinter());
                        break;
                    }
                }
                break;

            default:
                break;
            }
        }
        
        return value;
    }
    
    public enum AddressField {
        TIMESTAMP,
        SPID,
        GEO,
        SUB,
        FEEDER,
        ZIP,
        UDA,
        RELAY_1_PROGRAM,
        RELAY_1_SPLINTER,
        RELAY_2_PROGRAM,
        RELAY_2_SPLINTER,
        RELAY_3_PROGRAM,
        RELAY_3_SPLINTER
    }
    
    @PostConstruct
    public void loadAddresses() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Set<LmReportedAddress> allCurrentAddresses = lmDeviceReportedDataDao.getAllCurrentAddresses();
                for (LmReportedAddress address : allCurrentAddresses) {
                    DatedObject<LmReportedAddress> current = currentAddresses.get(address.getDeviceId());
                    if (current == null
                            || current.getObject().getTimestamp().isBefore(address.getTimestamp())) {
                        currentAddresses.put(address.getDeviceId(), new DatedObject<LmReportedAddress>(address));
                    }
                }
            }
        });
    }
    
    /**
     * Called from Yukon Service Maganger's {@link LcrReadingArchiveRequestListener} 
     * upon recieving an  {@link RfnLcrReadingArchiveRequest}
     * @param address
     */
    public void handleAddress(LmReportedAddress address) {
        currentAddresses.put(address.getDeviceId(), new DatedObject<LmReportedAddress>(address));
    }
    
}