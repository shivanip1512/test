package com.eaton.framework;

public final class MeterConstants {

	//Private
	private String manufacturer;
	private String meterType;
	private String model;
	
	//Public
	public static final MeterConstants RFG201 = new MeterConstants(Manufacturers.GAS2, "RFG-201", "Pulse-201");
	public static final MeterConstants RFN410CL = new MeterConstants(Manufacturers.ITRON, "RFN-410cL", "C1SX");
	public static final MeterConstants RFN420CD = new MeterConstants(Manufacturers.ITRON, "RFN-420cD", "C2SX-SD");
	public static final MeterConstants RFN420CL = new MeterConstants(Manufacturers.ITRON, "RFN-420cL", "C2SX");
	public static final MeterConstants RFN420FL = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fL", "FocuskWh");
	public static final MeterConstants RFN420FRD_A = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fRD", "FocusAXR-SD");
	public static final MeterConstants RFN420FRD_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fRD", "FocusRXR-SD");
	public static final MeterConstants RFN420FRX = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fRX", "FocusAXR");
	public static final MeterConstants RFN420FRX_A = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fRX", "FocusAXR");
	public static final MeterConstants RFN420FRX_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fRX", "FocusRXR");
	public static final MeterConstants RFN420FX = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-420fX", "FocusAXD");
	public static final MeterConstants RFN430A3D = new MeterConstants(Manufacturers.ELSTER, "RFN-430A3D", "A3D");
	public static final MeterConstants RFN430A3K = new MeterConstants(Manufacturers.ELSTER, "RFN-430A3K", "A3K");
	public static final MeterConstants RFN430A3R = new MeterConstants(Manufacturers.ELSTER, "RFN-430A3R", "A3R");
	public static final MeterConstants RFN430A3T = new MeterConstants(Manufacturers.ELSTER, "RFN-430A3T", "A3T");
	public static final MeterConstants RFN430KV = new MeterConstants(Manufacturers.GENERAL_ELECTRIC, "RFN-430KV", "kV2");
	public static final MeterConstants RFN430SL0  = new MeterConstants(Manufacturers.SCHLUMBERGER, "RFN-430SL0", "Sentinel-L0");
	public static final MeterConstants RFN430SL1  = new MeterConstants(Manufacturers.SCHLUMBERGER, "RFN-430SL1", "Sentinel-L1");
	public static final MeterConstants RFN430SL2  = new MeterConstants(Manufacturers.SCHLUMBERGER, "RFN-430SL2", "Sentinel-L2");
	public static final MeterConstants RFN430SL3  = new MeterConstants(Manufacturers.SCHLUMBERGER, "RFN-430SL3", "Sentinel-L3");
	public static final MeterConstants RFN430SL4  = new MeterConstants(Manufacturers.SCHLUMBERGER, "RFN-430SL4", "Sentinel-L4");
	public static final MeterConstants RFN510FL = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-510fL", "FocuskWh-500");
	public static final MeterConstants RFN520FAX_D = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAX", "FocusAXD-500");
	public static final MeterConstants RFN520FAX_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAX", "FocusAXR-500");
	public static final MeterConstants RFN520FAX_T = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAX", "FocusAXT-500");
	public static final MeterConstants RFN520FAXD = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAXD", "FocusAXD-SD-500");
	public static final MeterConstants RFN520FAXR = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAXD", "FocusAXR-SD-500");
	public static final MeterConstants RFN520FAXT = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fAXD", "FocusAXT-SD-500");
	public static final MeterConstants RFN520FRX_D = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRX", "FocusRXD-500");
	public static final MeterConstants RFN520FRX_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRX", "FocusRXR-500");
	public static final MeterConstants RFN520FRX_T = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRX", "FocusRXT-500");
	public static final MeterConstants RFN520FRXD_D = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRXD", "FocusRXD-SD-500");
	public static final MeterConstants RFN520FRXD_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRXD", "FocusRXR-SD-500");
	public static final MeterConstants RFN520FRXD_T = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-520fRXD", "FocusRXT-SD-500");
	public static final MeterConstants RFN530FRX = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530fRX", "FocusAXR-530");
	public static final MeterConstants RFN530S4eAX = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eAX", "S4-AD");
	public static final MeterConstants RFN530S4eAXR_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eAX", "S4-AR");
	public static final MeterConstants RFN530S4eAXR_T = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eAX", "S4-AT");
	public static final MeterConstants RFN530S4eRX = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eRX", "S4-RD");
	public static final MeterConstants RFN530S4eRXR = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eRXR", "S4-RR");
	public static final MeterConstants RFN530S4eRXR_R = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eRX", "S4-RR");
	public static final MeterConstants RFN530S4eRXR_T = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4eRX", "S4-RT");
	public static final MeterConstants RFN530S4X = new MeterConstants(Manufacturers.LANDIS_GYR, "RFN-530S4x", "E650");
	public static final MeterConstants RFW201_ENCODER = new MeterConstants(Manufacturers.WATER2, "RFW-201", "Encoder-201");
	public static final MeterConstants RFW201_PULSE = new MeterConstants(Manufacturers.WATER2, "RFW-201", "Pulse-201");
	public static final MeterConstants RFWMETER_NODE = new MeterConstants(Manufacturers.EKA, "RFW-Meter", "water_node");
	public static final MeterConstants RFWMETER_SENSOR = new MeterConstants(Manufacturers.EKA, "RFW-Meter", "water_sensor");
	public static final MeterConstants WRL420CD = new MeterConstants(Manufacturers.ITRON, "WRL-420cD", "C2SX-SD-W");
	public static final MeterConstants WRL420CL = new MeterConstants(Manufacturers.ITRON, "WRL-420cL", "C2SX-W");

	
	
	//================================================================================
    // Constructors
    //================================================================================
	
    private MeterConstants(String manufacturer, String meterType, String model) {
    	this.manufacturer = manufacturer;
    	this.meterType = meterType;
    	this.model = model;
    }
       
    public static final class Manufacturers {

    	public static final String EKA = "EKA";//Owned by Eaton
    	public static final String ELSTER = "EE";//Owned by Honeywell
    	public static final String GAS2 = "GAS2";//Denotes a battery node manufactured by Eaton connected to a gas meter
    	public static final String GENERAL_ELECTRIC = "GE";
    	public static final String ITRON = "ITRN";
    	public static final String LANDIS_GYR = "LGYR";
    	public static final String SCHLUMBERGER = "SCH";//Owned by Itron
    	public static final String WATER2 = "WTR2";//Denotes a battery node manufactured by Eaton connected to a water meter

    }
    
    //================================================================================
    // Getters and Setters
    //================================================================================
    
    public String getManufacturer() {
    	return manufacturer;
    }
    
    public String getMeterType() {
    	return meterType;
    }
    
    public String getModel() {
    	return model;
    }
        

}