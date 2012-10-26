package com.cannontech.web.updater.dr;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.dao.SepReportedAddress;
import com.cannontech.dr.dao.SepReportedAddressDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class LmReportedDataBackingService extends UpdateBackingServiceBase<LmReportedAddress> {

    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private ExpressComReportedAddressDao expressComReportedAddressDao;
    @Autowired private SepReportedAddressDao sepReportedAddressDao;
    @Autowired @Qualifier("main") private Executor executor;
    
    private Map<Integer, DatedObject<LmReportedAddress>> currentAddresses = new ConcurrentHashMap<Integer, DatedObject<LmReportedAddress>>();

    @Override
    public DatedObject<LmReportedAddress> getDatedObject(int deviceId) {
        DatedObject<LmReportedAddress> datedAddress = currentAddresses.get(deviceId);
        return datedAddress;
    }
    
    @Override
    public Object getValue(DatedObject<LmReportedAddress> datedObject, String[] idBits, YukonUserContext context) {

        if (datedObject == null) {
            return null;
        }
        
        if (datedObject.getObject() instanceof ExpressComReportedAddress) {
            return getExpressComValue(datedObject, idBits, context);
        } else if (datedObject.getObject() instanceof SepReportedAddress) {
            return getSepValue(datedObject, idBits, context);
        } else {
            throw new IllegalArgumentException("Invalid Address Type");
        }
        
    }
    
    private Object getSepValue(DatedObject<LmReportedAddress> datedObject, String[] idBits, YukonUserContext context) {
        String value = null;
        String fieldName = idBits[1];
        
        SepReportedAddress address = (SepReportedAddress) datedObject.getObject();
        SepAddressField field = SepAddressField.valueOf(fieldName);
        
        switch (field) {
        
        case TIMESTAMP:
            value =  dateFormattingService.format(address.getTimestamp(), DateFormatEnum.DATEHM, context);
            break;
            
        case DEVICE_CLASS:
            value =  Integer.toString(address.getDeviceClass());
            break;
            
        case UTILITY_ENROLLMENT_GROUP:
            value =  Integer.toString(address.getUtilityEnrollmentGroup());
            break;
            
        case RANDOM_START_TIME_MINUTES:
            value =  Integer.toString(address.getRandomStartTimeMinutes());
            break;
            
        case RANDOM_STOP_TIME_MINUTES:
            value =  Integer.toString(address.getRandomStopTimeMinutes());
            break;
            
        default:
            break;
        }
        
        return value;
    }

    private Object getExpressComValue(DatedObject<LmReportedAddress> datedObject, String[] idBits, YukonUserContext context) {
        String value = null;
        String fieldName = idBits[1];
        
        ExpressComReportedAddress address = (ExpressComReportedAddress) datedObject.getObject();
        ExpressComAddressField field = ExpressComAddressField.valueOf(fieldName);
        
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
            int substation = address.getSubstation();
            if (substation == -1) {
                MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
                value = accessor.getMessage("yukon.web.modules.operator.hardwareConfig.notReadable"); 
            } else {
                value =  Integer.toString(substation);
            }
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
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 0) {
                    value =  Integer.toString(relay.getProgram());
                    break;
                }
            }
            break;
            
        case RELAY_1_SPLINTER:
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 0) {
                    value =  Integer.toString(relay.getSplinter());
                    break;
                }
            }
            break;
            
        case RELAY_2_PROGRAM:
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 1) {
                    value =  Integer.toString(relay.getProgram());
                    break;
                }
            }
            break;
            
        case RELAY_2_SPLINTER:
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 1) {
                    value =  Integer.toString(relay.getSplinter());
                    break;
                }
            }
            break;
            
        case RELAY_3_PROGRAM:
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 2) {
                    value =  Integer.toString(relay.getProgram());
                    break;
                }
            }
            break;
            
        case RELAY_3_SPLINTER:
            for (ExpressComReportedAddressRelay relay : address.getRelays()) {
                if (relay.getRelayNumber() == 2) {
                    value =  Integer.toString(relay.getSplinter());
                    break;
                }
            }
            break;

        default:
            break;
        }
        
        return value;
    }

    public enum ExpressComAddressField {
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
    
    public enum SepAddressField {
        TIMESTAMP,
        DEVICE_CLASS,
        UTILITY_ENROLLMENT_GROUP,
        RANDOM_START_TIME_MINUTES,
        RANDOM_STOP_TIME_MINUTES,
    }
    
    @PostConstruct
    public void loadAddresses() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Set<ExpressComReportedAddress> allCurrentXcomAddresses = expressComReportedAddressDao.getAllCurrentAddresses();
                for (ExpressComReportedAddress address : allCurrentXcomAddresses) {
                    DatedObject<LmReportedAddress> current = currentAddresses.get(address.getDeviceId());
                    if (current == null
                            || current.getObject().getTimestamp().isBefore(address.getTimestamp())) {
                        currentAddresses.put(address.getDeviceId(), new DatedObject<LmReportedAddress>(address));
                    }
                }
                Set<SepReportedAddress> allCurrentSepAddresses = sepReportedAddressDao.getAllCurrentAddresses();
                for (SepReportedAddress address : allCurrentSepAddresses) {
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