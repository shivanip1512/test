package com.cannontech.common.rfn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Only use this enum if you know what you are doing!
 * Creation of new RfnDevices should utilize the template name structure (*RfnTemplate_manufacturer_model)
 *  see {@link RfnDeviceCreationService}.create method.
 *
 */
public enum RfnManufacturerModel {
    
    RFN_LCR_6200(PaoType.LCR6200_RFN, "CPS", "1077"),
    RFN_LCR_6600(PaoType.LCR6600_RFN, "CPS", "1082"),
    
//    RFN_440_2131T(PaoType.RFN440_2131T, "ELO", "2131T"),
    RFN_440_2131TD(PaoType.RFN440_2131TD, "ELO", "0131"),
//    RFN_440_2132T(PaoType.RFN440_2131T, "ELO", "2132T"),
    RFN_440_2132TD(PaoType.RFN440_2132TD, "ELO", "0132"),
//    RFN_440_2133T(PaoType.RFN440_2131T, "ELO", "2133T"),
    RFN_440_2133TD(PaoType.RFN440_2133TD, "ELO", "0133"),

    RFN_430A3D(PaoType.RFN430A3D, "EE", "A3D"),
    RFN_430A3T(PaoType.RFN430A3T, "EE", "A3T"),
    RFN_430A3K(PaoType.RFN430A3K, "EE", "A3K"),
    RFN_430A3R(PaoType.RFN430A3R, "EE", "A3R"),

    RFN_WATER_SENSOR(PaoType.RFWMETER, "Eka", "water_sensor"),
    RFN_WATER_NODE(PaoType.RFWMETER, "Eka", "water_node"),
    
    RFN_430KV(PaoType.RFN430KV, "GE", "kV2"),
    
    RFN_420FL(PaoType.RFN420FL, "LGYR", "FocuskWh"),
    RFN_420FX(PaoType.RFN420FX, "LGYR", "FocusAXD"),
    RFN_420FD(PaoType.RFN420FD, "LGYR", "FocusAXR-SD"),
    RFN_420FRX(PaoType.RFN420FRX, "LGYR", "FocusRXR"),
    RFN_420FRD(PaoType.RFN420FRD, "LGYR", "FocusRXR-SD"),
    
    RFN_410CL(PaoType.RFN410CL, "ITRN", "C1SX"),
    RFN_420CL(PaoType.RFN420CL, "ITRN", "C2SX"),
    RFN_420CD(PaoType.RFN420CD, "ITRN", "C2SX-SD"),
    
    RFN_410FL(PaoType.RFN410FL, "LGYR", "FocuskWh"),
    RFN_410FX_D(PaoType.RFN410FX, "LGYR", "FocusAXD"),
    RFN_410FX_R(PaoType.RFN410FX, "LGYR", "FocusAXR"),
    RFN_410FD_D(PaoType.RFN410FD, "LGYR", "FocusAXR-SD"),
    RFN_410FD_R(PaoType.RFN410FD, "LGYR", "FocusAXD-SD"),
    
    RFN_430SL0(PaoType.RFN430SL0, "SCH", "SENTINEL-L0"),
    RFN_430SL1(PaoType.RFN430SL1, "SCH", "SENTINEL-L1"),
    RFN_430SL2(PaoType.RFN430SL2, "SCH", "SENTINEL-L2"),
    RFN_430SL3(PaoType.RFN430SL3, "SCH", "SENTINEL-L3"),
    RFN_430SL4(PaoType.RFN430SL4, "SCH", "SENTINEL-L4"),
    
    /* For the RFN_520 and RFN_530 meters below, there are cases where multiple manufacturer/model combinations
     * map to the same pao type, like the 'S4-AT' and 'S4-AR' model strings that both map to the PaoType 'RFN530S4EAT'.
     * This is intentional- multiple meter models are functionally the same in Yukon, this should not be changed. */
    RFN_510FL(PaoType.RFN510FL, "LGYR", "FocuskWh-500"),
    RFN_520FAXD(PaoType.RFN520FAX, "LGYR", "FocusAXD-500"),
    RFN_520FAXT(PaoType.RFN520FAX, "LGYR", "FocusAXT-500"),
    RFN_520FAXR(PaoType.RFN520FAX, "LGYR", "FocusAXR-500"),
    RFN_520FRXD(PaoType.RFN520FRX, "LGYR", "FocusRXD-500"),
    RFN_520FRXT(PaoType.RFN520FRX, "LGYR", "FocusRXT-500"),
    RFN_520FRXR(PaoType.RFN520FRX, "LGYR", "FocusRXR-500"),
    RFN_520FAXD_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXD-SD-500"),
    RFN_520FAXT_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXT-SD-500"),
    RFN_520FAXR_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXR-SD-500"),
    RFN_520FRXD_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXD-SD-500"),
    RFN_520FRXT_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXT-SD-500"),
    RFN_520FRXR_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXR-SD-500"),
    
    RFN_530S4X(PaoType.RFN530S4X, "LGYR", "E650"),
    RFN_530S4AD(PaoType.RFN530S4EAD, "LGYR", "S4-AD"),
    RFN_530S4AT(PaoType.RFN530S4EAT, "LGYR", "S4-AT"),
    RFN_530S4AR(PaoType.RFN530S4EAT, "LGYR", "S4-AR"),
    RFN_530S4RD(PaoType.RFN530S4ERD, "LGYR", "S4-RD"),
    RFN_530S4RT(PaoType.RFN530S4ERT, "LGYR", "S4-RT"),
    RFN_530S4RR(PaoType.RFN530S4ERT, "LGYR", "S4-RR"),
    ;
    
    private PaoType type;
    private String manufacturer;
    private String model;
    private static final Table<String, String, RfnManufacturerModel> lookup = HashBasedTable.<String, String, RfnManufacturerModel>create();
    
    private RfnManufacturerModel(PaoType type, String manufacturer, String model) {
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
    }
    
    static {
        //  These have model strings that were reused for the RFN-420 Focus models.
        Set<RfnManufacturerModel> duplicateLgyrModels = Sets.immutableEnumSet(RFN_410FL, RFN_410FX_D, RFN_410FD_D);
        
        Stream.of(values())
            .filter(mm -> !duplicateLgyrModels.contains(mm))
            .forEach(mm -> lookup.put(mm.manufacturer, mm.model, mm));
    }
    
    public static List<RfnManufacturerModel> getForType(PaoType type) {
        return Optional.of(
                Stream.of(values())
                        .filter(item -> item.type == type)
                        .collect(Collectors.toCollection(() -> new ArrayList<RfnManufacturerModel>())))
                .filter(list -> ! list.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Unknown template for type: " + type));
    }
    
    /**
     * Performs a reverse lookup of manufacturer+model strings to an RfnManufacturerModel entry, if one exists.<br>
     * Will return the RFN-420 versions of the reused LGYR Focus manufacturer+model strings.
     */
    public static RfnManufacturerModel of(RfnIdentifier rfnIdentifier) {
        return lookup.get(rfnIdentifier.getSensorManufacturer(), rfnIdentifier.getSensorModel());
    }
    
    public PaoType getType() {
        return type;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getModel() {
        return model;
    }
    
}