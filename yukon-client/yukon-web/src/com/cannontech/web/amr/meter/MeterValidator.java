package com.cannontech.web.amr.meter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.widget.meterInfo.model.CreateMeterModel;
import com.cannontech.web.widget.meterInfo.model.MeterModel;
import com.cannontech.web.widget.meterInfo.model.PlcMeterModel;
import com.cannontech.web.widget.meterInfo.model.RfMeterModel;

public class MeterValidator extends SimpleValidator<MeterModel> {
    
    private final static String key = "yukon.web.widgets.meterInformationWidget.error.";

    
    @Autowired private PaoDao paoDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private DlcAddressRangeService addressRangeService;


    public MeterValidator() {
        super(MeterModel.class);
    }

    @Override
    protected void doValidation(MeterModel meter, Errors errors) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
        PaoType type = pao.getPaoType();
        
        if(meter instanceof CreateMeterModel){
            type = ((CreateMeterModel)meter).getType();
        }
        
        // Device Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "deviceName.required");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", meter.getName(), 60);
        }
        if (!errors.hasFieldErrors("name")) {
            LiteYukonPAObject unique = paoDao.findUnique(meter.getName(), type);
            if (unique != null) {
                if (unique.getPaoIdentifier().getPaoId() != pao.getPaoIdentifier().getPaoId()) {
                    errors.rejectValue("name", key + "deviceName.unique");
                }
            }
        }
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(meter.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        // Meter Number
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "meterNumber", key + "meterNumber.required");
        
        // Validate PLC meter settings
        if (PaoType.getMctTypes().contains(type)||meter instanceof PlcMeterModel) {
            
            PlcMeterModel plc = PlcMeterModel.of(meter);
            
            // Physical Address
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", key + "physicalAddress.required");
            if (!errors.hasFieldErrors("address")) {
                boolean validAddress = addressRangeService.isValidEnforcedAddress(type, plc.getAddress());
                if (!validAddress) {
                    String ranges = addressRangeService.rangeStringEnforced(type);
                    Object[] args = new Object[] { ranges };
                    errors.rejectValue("address", key + "physicalAddress.range", args, null);
                }
            }
        }
        
        // Validate RF meter settings
        if (PaoType.getRfMeterTypes().contains(type)||meter instanceof RfMeterModel) {
            
            RfMeterModel rf = RfMeterModel.of(meter);
            RfnIdentifier rfnId = new RfnIdentifier(rf.getSerialNumber(), rf.getManufacturer(), rf.getModel());
            if (!rfnId.isBlank()) {
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serialNumber", key + "serialNumber.required");
                YukonValidationUtils.checkExceedsMaxLength(errors, "serialNumber", rf.getSerialNumber(), 30);
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "manufacturer", key + "manufacturer.required");
                YukonValidationUtils.checkExceedsMaxLength(errors, "manufacturer", rf.getManufacturer(), 60);
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "model", key + "model.required");
                YukonValidationUtils.checkExceedsMaxLength(errors, "model", rf.getModel(), 60);
            }
        }
    }

}
