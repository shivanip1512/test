package com.cannontech.web.updater.dr;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.dao.SepReportedAddress;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class LmReportedDataBackingService extends UpdateBackingServiceBase<LmReportedAddress> {

    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private ExpressComReportedAddressDao expressComReportedAddressDao;
    @Autowired @Qualifier("main") private Executor executor;

    private final Cache<Integer, DatedObject<LmReportedAddress>> currentAddresses =
        CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Override
    public DatedObject<LmReportedAddress> getDatedObject(int deviceId) {
        //If not present hit the DB for fetching it
        DatedObject<LmReportedAddress> datedAddress = currentAddresses.getIfPresent(deviceId);
        try {
            if (datedAddress == null) {
                datedAddress =
                    new DatedObject<>(expressComReportedAddressDao.getCurrentAddress(deviceId));
                currentAddresses.put(deviceId, datedAddress);
            }
        } catch (NotFoundException e) {/* Ignore */}
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

    private String getSepValue(DatedObject<LmReportedAddress> datedObject, String[] idBits, YukonUserContext context) {
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

    private String getExpressComValue(DatedObject<LmReportedAddress> datedObject,
            String[] idBits, YukonUserContext context) {
        String value = null;
        String fieldName = idBits[1];
        Integer relayNum = idBits.length > 2 ? Integer.valueOf(idBits[2]) : null; // set if field is relay

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
                value = accessor.getMessage("yukon.dr.config.notReadable"); 
            } else {
                value =  Integer.toString(substation);
            }
            break;
        case FEEDER:
            value = Integer.toString(address.getFeeder());
            break;
        case ZIP:
            value = Integer.toString(address.getZip());
            break;
        case UDA:
            value = Integer.toString(address.getUda());
            break;
        case RELAY_PROGRAM:
            value = Integer.toString(address.getRelayByNumber(relayNum).getProgram());
            break;
        case RELAY_SPLINTER:
            value = Integer.toString(address.getRelayByNumber(relayNum).getSplinter());
            break;
        default:
            break;
        }

        return value;
    }

    public enum ExpressComAddressField {
        TIMESTAMP, SPID, GEO, SUB, FEEDER, ZIP, UDA, RELAY_PROGRAM, RELAY_SPLINTER,
    }

    public enum SepAddressField {
        TIMESTAMP, DEVICE_CLASS, UTILITY_ENROLLMENT_GROUP, RANDOM_START_TIME_MINUTES, RANDOM_STOP_TIME_MINUTES,
    }

    /**
     * Called from Yukon Service Manager's {@link LcrReadingArchiveRequestListener} 
     * upon receiving an  {@link RfnLcrReadingArchiveRequest}
     */
    public void handleAddress(LmReportedAddress address) {
        currentAddresses.put(address.getDeviceId(), new DatedObject<>(address));
    }
}