package com.eaton.framework;

public class MeterEnums {

    public enum MeterType {
        MCT310CT("MCT-310CT"),
        MCT310IDL("MCT-310IDL"),
        MCT310IL("MCT-310IL"),
        MCT318L("MCT-318L"),
        MCT360("MCT-360"),
        MCT370("MCT-370"),
        MCT410CL("MCT-410cL"),
        MCT420CD("MCT-420cD"),
        MCT420CL("MCT-420cL"),
        MCT420FD("MCT-420fD"),
        MCT430A("MCT-430A"),
        MCT430A3("MCT-430A3"),
        MCT430S4("MCT-430S4"),
        MCT470("MCT-470"),
        RFG201(Manufacturer.GAS2, "RFG-201", "Pulse-201"),
        RFN410CL(Manufacturer.ITRON, "RFN-410cL", "C1SX"),
        RFN420CD(Manufacturer.ITRON, "RFN-420cD", "C2SX-SD"),
        RFN420CL(Manufacturer.ITRON, "RFN-420cL", "C2SX"),
        RFN420FL(Manufacturer.LANDIS_GYR, "RFN-420fL", "FocuskWh"),
        RFN420FRD_A(Manufacturer.LANDIS_GYR, "RFN-420fRD", "FocusAXR-SD"),
        RFN420FRD_R(Manufacturer.LANDIS_GYR, "RFN-420fRD", "FocusRXR-SD"),
        RFN420FRX(Manufacturer.LANDIS_GYR, "RFN-420fRX", "FocusAXR"),
        RFN420FRX_A(Manufacturer.LANDIS_GYR, "RFN-420fRX", "FocusAXR"),
        RFN420FRX_R(Manufacturer.LANDIS_GYR, "RFN-420fRX", "FocusRXR"),
        RFN420FX(Manufacturer.LANDIS_GYR, "RFN-420fX", "FocusAXD"),
        RFN430A3D(Manufacturer.ELSTER, "RFN-430A3D", "A3D"),
        RFN430A3K(Manufacturer.ELSTER, "RFN-430A3K", "A3K"),
        RFN430A3R(Manufacturer.ELSTER, "RFN-430A3R", "A3R"),
        RFN430A3T(Manufacturer.ELSTER, "RFN-430A3T", "A3T"),
        RFN430KV(Manufacturer.GENERAL_ELECTRIC, "RFN-430KV", "kV2"),
        RFN430SL0(Manufacturer.SCHLUMBERGER, "RFN-430SL0", "Sentinel-L0"),
        RFN430SL1(Manufacturer.SCHLUMBERGER, "RFN-430SL1", "Sentinel-L1"),
        RFN430SL2(Manufacturer.SCHLUMBERGER, "RFN-430SL2", "Sentinel-L2"),
        RFN430SL3(Manufacturer.SCHLUMBERGER, "RFN-430SL3", "Sentinel-L3"),
        RFN430SL4(Manufacturer.SCHLUMBERGER, "RFN-430SL4", "Sentinel-L4"),
        RFN510FL(Manufacturer.LANDIS_GYR, "RFN-510fL", "FocuskWh-500"),
        RFN520FAX_D(Manufacturer.LANDIS_GYR, "RFN-520fAX", "FocusAXD-500"),
        RFN520FAX_R(Manufacturer.LANDIS_GYR, "RFN-520fAX", "FocusAXR-500"),
        RFN520FAX_T(Manufacturer.LANDIS_GYR, "RFN-520fAX", "FocusAXT-500"),
        RFN520FAXD(Manufacturer.LANDIS_GYR, "RFN-520fAXD", "FocusAXD-SD-500"),
        RFN520FAXR(Manufacturer.LANDIS_GYR, "RFN-520fAXD", "FocusAXR-SD-500"),
        RFN520FAXT(Manufacturer.LANDIS_GYR, "RFN-520fAXD", "FocusAXT-SD-500"),
        RFN520FRX_D(Manufacturer.LANDIS_GYR, "RFN-520fRX", "FocusRXD-500"),
        RFN520FRX_R(Manufacturer.LANDIS_GYR, "RFN-520fRX", "FocusRXR-500"),
        RFN520FRX_T(Manufacturer.LANDIS_GYR, "RFN-520fRX", "FocusRXT-500"),
        RFN520FRXD_D(Manufacturer.LANDIS_GYR, "RFN-520fRXD", "FocusRXD-SD-500"),
        RFN520FRXD_R(Manufacturer.LANDIS_GYR, "RFN-520fRXD", "FocusRXR-SD-500"),
        RFN520FRXD_T(Manufacturer.LANDIS_GYR, "RFN-520fRXD", "FocusRXT-SD-500"),
        RFN530FRX(Manufacturer.LANDIS_GYR, "RFN-530fRX", "FocusAXR-530"),
        RFN530S4eAX(Manufacturer.LANDIS_GYR, "RFN-530S4eAX", "S4-AD"),
        RFN530S4eAXR_R(Manufacturer.LANDIS_GYR, "RFN-530S4eAX", "S4-AR"),
        RFN530S4eAXR_T(Manufacturer.LANDIS_GYR, "RFN-530S4eAX", "S4-AT"),
        RFN530S4eRX(Manufacturer.LANDIS_GYR, "RFN-530S4eRX", "S4-RD"),
        RFN530S4eRXR(Manufacturer.LANDIS_GYR, "RFN-530S4eRXR", "S4-RR"),
        RFN530S4eRXR_R(Manufacturer.LANDIS_GYR, "RFN-530S4eRX", "S4-RR"),
        RFN530S4eRXR_T(Manufacturer.LANDIS_GYR, "RFN-530S4eRX", "S4-RT"),
        RFN530S4X(Manufacturer.LANDIS_GYR, "RFN-530S4x", "E650"),
        RFW201_ENCODER(Manufacturer.WATER2, "RFW-201", "Encoder-201"),
        RFW201_PULSE(Manufacturer.WATER2, "RFW-201", "Pulse-201"),
        RFWMETER_NODE(Manufacturer.EKA, "RFW-Meter", "water_node"),
        RFWMETER_SENSOR(Manufacturer.EKA, "RFW-Meter", "water_sensor"),
        WRL420CD(Manufacturer.ITRON, "WRL-420cD", "C2SX-SD-W"),
        WRL420CL(Manufacturer.ITRON, "WRL-420cL", "C2SX-W");

        // Private
        private final Manufacturer manufacturer;
        private final String meterType;
        private final String model;

        MeterType(Manufacturer manufacturer, String meterType, String model) {
            this.manufacturer = manufacturer;
            this.meterType = meterType;
            this.model = model;
        }

        MeterType(String meterType) {
            this(null, meterType, null);
        }

        public Manufacturer getManufacturer() {
            return manufacturer;
        }

        public String getMeterType() {
            return meterType;
        }

        public String getModel() {
            return model;
        }
    }

    public enum Manufacturer {
        EKA("EKA"), // Owned by Eaton
        ELSTER("EE"), // Owned by Honeywell
        GAS2("GAS2"), // Denotes a battery node manufactured by Eaton connected to a gas meter
        GENERAL_ELECTRIC("GE"),
        ITRON("ITRN"),
        LANDIS_GYR("LGYR"),
        SCHLUMBERGER("SCH"), // Owned by Itron
        WATER2("WTR2");// Denotes a battery node manufactured by Eaton connected to a water meter

        // Private
        private final String manufacturer;

        Manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getManufacturer() {
            return manufacturer;
        }
    }

}