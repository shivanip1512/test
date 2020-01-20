package com.cannontech.common.rfn.dataStreaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.ui.ModelMap;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class tracks which pao types support data streaming, and which attributes are supported for streaming.
 */
public class DataStreamingAttributeHelper {
    private Map<PaoType, Set<BuiltInAttribute>> typeToSupportedAttributes = Maps.newEnumMap(PaoType.class);
    private Set<BuiltInAttribute> supportedAttributes = new HashSet<>();

    /**
     * Each entry in this enum maps a PaoType to the attributes it supports for data streaming.
     * @see RfnManufacturerModel
     * @see PaoType
     * @see metricIdToAttributeMapping.json
     */
    //TODO add missing metrics/attributes!
    public static enum DataStreamingPaoAttributes {
        //C1SX
        RFN_410CL(PaoType.RFN410CL, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //C2SX
        RFN_420CL(PaoType.RFN420CL, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //C2SX
        RFN_420CD(PaoType.RFN420CD, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //C2SX-W
        WRL_420CL(PaoType.WRL420CL, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //C2SX-W
        WRL_420CD(PaoType.WRL420CD, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //Focus AXD
        RFN_420FX(PaoType.RFN420FX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //Focus AXR-SD
        RFN_420FD(PaoType.RFN420FD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //FocuskWh (as 420FL)
        RFN_420FL(PaoType.RFN420FL,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //FocuskWh-500
        RFN_510FL(PaoType.RFN510FL,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //S4-AD
        RFN_530S4AD(PaoType.RFN530S4EAX,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-AT
        RFN_530S4AT(PaoType.RFN530S4EAXR,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-AR
        RFN_530S4AR(PaoType.RFN530S4EAXR,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RD
        RFN_530S4RD(PaoType.RFN530S4ERX,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /* Removed due to RFNFIVE-636
                BuiltInAttribute.NET_KVARH,       //metric 24
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.NET_KVAH,        //metric 44
                */
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RT
        RFN_530S4RT(PaoType.RFN530S4ERXR,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /* Removed due to RFNFIVE-636
                BuiltInAttribute.NET_KVARH,       //metric 24
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.NET_KVAH,        //metric 44
                */
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RR
        RFN_530S4RR(PaoType.RFN530S4ERXR,
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,         //metric 3
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /* Removed due to RFNFIVE-636
                BuiltInAttribute.NET_KVARH,       //metric 24
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.NET_KVAH,        //metric 44
                */
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //E650
        RFN_530S4X(PaoType.RFN530S4X,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Var Hour Net*/                  //metric 24
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C, //metric 102
                BuiltInAttribute.AVERAGE_VOLTAGE_PHASE_A, //metric 119
                BuiltInAttribute.AVERAGE_VOLTAGE_PHASE_B, //metric 120
                BuiltInAttribute.AVERAGE_VOLTAGE_PHASE_C  //metric 121
                ),
        //Focus AXD-500
        RFN_520FAXD(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXT-500
        RFN_520FAXT(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXR-500
        RFN_520FAXR(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXD-SD-500
        RFN_520FAXD_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusAXT-SD-500
        RFN_520FAXT_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusAXR-SD-500
        RFN_520FAXR_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //TODO: Focus RXD-500??
        //FocusRXT-500
        RFN_520FRXT(PaoType.RFN520FRX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXR-500
        RFN_520FRXR(PaoType.RFN520FRX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXD-SD-500
        RFN_520FRXD_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXT-SD-500
        RFN_520FRXT_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXR-SD-500
        RFN_520FRXR_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE          //metric 115
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusAXT-530
        RFN_530FAXT(PaoType.RFN530FAX,
                BuiltInAttribute.DELIVERED_KWH,     //metric 1
                BuiltInAttribute.RECEIVED_KWH,      //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,  //metric 5
                BuiltInAttribute.VOLTAGE,           //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A,   //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B,   //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C    //metric 102
                ),
        //FocusAXR-530
        RFN_530FAXR(PaoType.RFN530FRX,
                BuiltInAttribute.DELIVERED_KWH,     //metric 1
                BuiltInAttribute.RECEIVED_KWH,      //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,  //metric 5
                BuiltInAttribute.SUM_KVARH,         //metric 23
                BuiltInAttribute.SUM_KVAH,          //metric 43
                BuiltInAttribute.VOLTAGE,           //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A,   //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B,   //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C    //metric 102
                ),
        //Elster A3D
        RFN_430A3D(PaoType.RFN430A3D,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.RECEIVED_DEMAND, //metric 6
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Elster A3T
        RFN_430A3T(PaoType.RFN430A3T,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.RECEIVED_DEMAND, //metric 6
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Elster A3K
        RFN_430A3K(PaoType.RFN430A3K,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.RECEIVED_DEMAND, //metric 6
                BuiltInAttribute.DELIVERED_KVA,   //metric 49
                BuiltInAttribute.RECEIVED_KVA,    //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C, //metric 102
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C  //metric 164
                ),
        //Elster A3R
        RFN_430A3R(PaoType.RFN430A3R,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DELIVERED_DEMAND,//metric 5
                BuiltInAttribute.RECEIVED_DEMAND, //metric 6
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.DELIVERED_KVA,   //metric 49
                BuiltInAttribute.RECEIVED_KVA,    //metric 50
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.DELIVERED_KVAR,  //metric 32
                BuiltInAttribute.RECEIVED_KVAR,   //metric 33
                BuiltInAttribute.AVERAGE_POWER_FACTOR_Q124,//metric 81
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C, //metric 102
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C  //metric 164
                ),
        //Sentinel-L0
        RFN_430SL0(PaoType.RFN430SL0,
                BuiltInAttribute.DELIVERED_KWH    //metric 1
                ),
        //Sentinel-L1
        RFN_430SL1(PaoType.RFN430SL1,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.INSTANTANEOUS_KW,//metric 200
                BuiltInAttribute.KVAR,            //metric 201
                BuiltInAttribute.KVA,             //metric 202
                BuiltInAttribute.POWER_FACTOR,    //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Sentinel-L2
        RFN_430SL2(PaoType.RFN430SL2,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.INSTANTANEOUS_KW,//metric 200
                BuiltInAttribute.KVAR,            //metric 201
                BuiltInAttribute.KVA,             //metric 202
                BuiltInAttribute.POWER_FACTOR,    //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Sentinel-L3
        RFN_430SL3(PaoType.RFN430SL3,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.INSTANTANEOUS_KW,//metric 200
                BuiltInAttribute.KVAR,            //metric 201
                BuiltInAttribute.KVA,             //metric 202
                BuiltInAttribute.POWER_FACTOR,    //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Sentinel-L4
        RFN_430SL4(PaoType.RFN430SL4,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.INSTANTANEOUS_KW,//metric 200
                BuiltInAttribute.KVAR,            //metric 201
                BuiltInAttribute.KVA,             //metric 202
                BuiltInAttribute.POWER_FACTOR,    //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                )
        ;

        private PaoType paoType;
        private Set<BuiltInAttribute> supportedAttributes;
        private DataStreamingPaoAttributes(PaoType paoType, BuiltInAttribute... supportedAttributes) {
            this.paoType = paoType;
            this.supportedAttributes = Sets.newEnumSet(Arrays.asList(supportedAttributes), BuiltInAttribute.class);
        }
        
        PaoType getPaoType() {
            return paoType;
        }
        
        Set<BuiltInAttribute> getSupportedAttributes() {
            return supportedAttributes;
        }
    }

    /**
     * On startup, put the enum entries into collections for easy access.
     */
    @PostConstruct
    private void init() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            typeToSupportedAttributes.put(dspa.paoType, dspa.supportedAttributes);
            supportedAttributes.addAll(dspa.supportedAttributes);
        }
    }

    /**
     * @return True if the specified paoType supports data streaming for at least one attribute, otherwise false;
     */
    public boolean supportsDataStreaming(PaoType paoType) {
        return typeToSupportedAttributes.keySet().contains(paoType);
    }

    /**
     * Gets all data streaming attributes supported by the specified type. If the type does not support data streaming,
     * an empty collection will be returned.
     */
    public Set<BuiltInAttribute> getSupportedAttributes(PaoType paoType) {
        return typeToSupportedAttributes.getOrDefault(paoType, Collections.emptySet());
    }

    /**
     * Gets all data streaming attributes supported by any of the specified paoTypes. Some of these attributes may not 
     * be supported by all specified paoTypes.
     */
    public Set<BuiltInAttribute> getAllSupportedAttributes(Collection<PaoType> paoTypes) {
        return paoTypes.stream()
                        .map(typeToSupportedAttributes::get)
                        .filter(Objects::nonNull)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
    }

    /**
     * Gets all attributes that support data streaming on at least one pao type.
     */
    public Set<BuiltInAttribute> getAllSupportedAttributes() {
        return supportedAttributes;
    }

    /**
     * Gets all PaoTypes that support data streaming for at least one attribute.
     */
    public Set<PaoType> getAllSupportedPaoTypes() {
        return typeToSupportedAttributes.keySet();
    }

    public Map<PaoType,Set<BuiltInAttribute>> getAllTypesAndAttributes(){
        return typeToSupportedAttributes;
    }

    public void buildMatrixModel(ModelMap model) {
        List<PaoType> deviceList = new ArrayList<>();
        deviceList.addAll(getAllSupportedPaoTypes());
        final Comparator<PaoType> paoComparator = (pt1, pt2) -> pt1.getDbString().compareTo(pt2.getDbString());
        Collections.sort(deviceList, paoComparator);
        List<BuiltInAttribute> biaList = new ArrayList<>();
        biaList.addAll(getAllSupportedAttributes());
        final Comparator<BuiltInAttribute> biaComparator = (bia1, bia2) -> bia1.getDescription()
                .compareToIgnoreCase(bia2.getDescription());

        Collections.sort(biaList, biaComparator);
        model.addAttribute("devices", deviceList);
        model.addAttribute("attributes", biaList);
        model.addAttribute("dataStreamingDevices", getAllTypesAndAttributes());

    }
}
