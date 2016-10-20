package com.cannontech.common.rfn.dataStreaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.ui.ModelMap;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This class tracks which pao types support data streaming, and which attributes are supported for streaming.
 */
public class DataStreamingAttributeHelper {
    private Multimap<PaoType, BuiltInAttribute> typeToSupportedAttributes = ArrayListMultimap.create();
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
                BuiltInAttribute.DEMAND,           //metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //C2SX
        RFN_420CL(PaoType.RFN420CL, 
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,           //metric 5
                BuiltInAttribute.VOLTAGE          //metric 115
                ),
        //Focus AXD
        RFN_420FX(PaoType.RFN420FX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.VOLTAGE,          //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //TODO Focus AXT?
        //Focus AXR
        RFN_410FX(PaoType.RFN410FX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.VOLTAGE,        //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXD-SD
        RFN_410FD(PaoType.RFN410FD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXR-SD
        RFN_420FRD(PaoType.RFN420FRD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocuskWh
        RFN_410FL(PaoType.RFN410FL,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,           //metric 5
                BuiltInAttribute.VOLTAGE         //metric 115
                ),
        //FocuskWh-500
        RFN_510FL(PaoType.RFN510FL,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,           //metric 5
                BuiltInAttribute.VOLTAGE         //metric 115
                ),
        //S4-AD
        RFN_530S4AD(PaoType.RFN530S4EAD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,		  //metric 3	
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-AT
        RFN_530S4AT(PaoType.RFN530S4EAT,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,		  //metric 3
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-AR
        RFN_530S4AR(PaoType.RFN530S4EAT,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KWH,		  //metric 3
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RD
        RFN_530S4RD(PaoType.RFN530S4ERD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RT
        RFN_530S4RT(PaoType.RFN530S4ERT,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //S4-RR
        RFN_530S4RR(PaoType.RFN530S4ERT,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.SUM_KVARH,       //metric 23
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //E650
        RFN_530S4X(PaoType.RFN530S4X,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Watts received Current Demand*/ //metric 6
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Var Hour Net*/				     //metric 24
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                /*VA Hour net*/                   //metric 44
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXD-500
        RFN_520FAXD(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXT-500
        RFN_520FAXT(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXR-500
        RFN_520FAXR(PaoType.RFN520FAX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Focus AXD-SD-500
        RFN_520FAXD_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusAXT-SD-500
        RFN_520FAXT_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusAXR-SD-500
        RFN_520FAXR_SD(PaoType.RFN520FAXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //TODO: Focus RXD-500??
        //FocusRXT-500
        RFN_520FRXT(PaoType.RFN520FRX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXR-500
        RFN_520FRXR(PaoType.RFN520FRX,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXD-SD-500
        RFN_520FRXD_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXT-SD-500
        RFN_520FRXT_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //FocusRXR-SD-500
        RFN_520FRXR_SD(PaoType.RFN520FRXD,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                BuiltInAttribute.SUM_KVARH,       //metric 23
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.VOLTAGE,         //metric 115
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Average Voltage Phase A*/       //metric 119
                /*Average Voltage Phase B*/       //metric 120
                /*Average Voltage Phase C*/       //metric 121
                ),
        //Elster A3D
        RFN_430A3D(PaoType.RFN430A3D,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Watts received, current demand*///metric 6 
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C, //metric 164
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Elster A3T
        RFN_430A3T(PaoType.RFN430A3T,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Watts received, current demand*///metric 6 
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C, //metric 164
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Elster A3K
        RFN_430A3K(PaoType.RFN430A3K,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Watts received, current demand*///metric 6 
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C, //metric 164
                /*Power Factor Average PF*/       //metric 80/81?
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Elster A3R
        RFN_430A3R(PaoType.RFN430A3R,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.DEMAND,          //metric 5
                /*Watts received, current demand*///metric 6 
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                BuiltInAttribute.SUM_KVAH,        //metric 43
                BuiltInAttribute.POWER_FACTOR_PHASE_A, //metric 162
                BuiltInAttribute.POWER_FACTOR_PHASE_B, //metric 163
                BuiltInAttribute.POWER_FACTOR_PHASE_C, //metric 164
                /*Power Factor Average PF*/       //metric 80/81?
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                ),
        //Sentinel-L0
        RFN_430SL0(PaoType.RFN430SL0,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Watts*/                         //metric 200
                /*Var*/                           //metric 201
                /*VA*/                            //metric 202
                ),
        //Sentinel-L1
        RFN_430SL1(PaoType.RFN430SL1,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Watts*/                         //metric 200
                /*Var*/                           //metric 201
                /*VA*/                            //metric 202
                ),
        //Sentinel-L2
        RFN_430SL2(PaoType.RFN430SL2,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Watts*/                         //metric 200
                /*Var*/                           //metric 201
                /*VA*/                            //metric 202
                ),
        //Sentinel-L3
        RFN_430SL3(PaoType.RFN430SL3,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Watts*/                         //metric 200
                /*Var*/                           //metric 201
                /*VA*/                            //metric 202
                ),
        //Sentinel-L4
        RFN_430SL4(PaoType.RFN430SL4,
                BuiltInAttribute.DELIVERED_KWH,   //metric 1
                BuiltInAttribute.RECEIVED_KWH,    //metric 2
                BuiltInAttribute.KVAR,            //metric 32
                /*Var received current demand*/   //metric 33
                /*Va delivered current demand*/   //metric 49
                /*Va received*/                   //metric 50
                /*Power Factor*/                  //metric 80
                BuiltInAttribute.VOLTAGE_PHASE_A, //metric 100
                BuiltInAttribute.VOLTAGE_PHASE_B, //metric 101
                BuiltInAttribute.VOLTAGE_PHASE_C  //metric 102
                /*Watts*/                         //metric 200
                /*Var*/                           //metric 201
                /*VA*/                            //metric 202
                )
        ;

        private PaoType paoType;
        private List<BuiltInAttribute> supportedAttributes;
        private DataStreamingPaoAttributes(PaoType paoType, BuiltInAttribute... supportedAttributes) {
            this.paoType = paoType;
            this.supportedAttributes = Lists.newArrayList(supportedAttributes);
        }
    }

    /**
     * On startup, put the enum entries into collections for easy access.
     */
    @PostConstruct
    private void init() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            typeToSupportedAttributes.putAll(dspa.paoType, dspa.supportedAttributes);
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
    public Collection<BuiltInAttribute> getSupportedAttributes(PaoType paoType) {
        return typeToSupportedAttributes.get(paoType);
    }

    /**
     * Gets all data streaming attributes supported by any of the specified paoTypes. Some of these attributes may not 
     * be supported by all specified paoTypes.
     */
    public Set<BuiltInAttribute> getAllSupportedAttributes(Collection<PaoType> paoTypes) {
        Set<BuiltInAttribute> supportedAttributes = new HashSet<>();
        for (PaoType paoType : paoTypes) {
            Collection<BuiltInAttribute> attributesForType = typeToSupportedAttributes.get(paoType);
            supportedAttributes.addAll(attributesForType);
        }
        return supportedAttributes;
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

    public Multimap<PaoType, BuiltInAttribute> getAllTypesAndAttributes(){
        return typeToSupportedAttributes;
    }

    public void buildMatrixModel(ModelMap model) {
        Multimap<PaoType, BuiltInAttribute> devicesMap = getAllTypesAndAttributes();
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
        model.addAttribute("dataStreamingDevices", devicesMap.asMap());

    }
}
