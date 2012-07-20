package com.cannontech.common.point;

import com.cannontech.database.data.point.PointUnits;

public enum UnitOfMeasure {
    KW("kW", "kW", PointUnits.UOMID_KW),                                //0
    KWH("kWH", "kWH", PointUnits.UOMID_KWH),                            //1
    KVA("kVA", "kVA", PointUnits.UOMID_KVA),                            //2
    KVAR("kVAr", "kVAr", PointUnits.UOMID_KVAR),                        //3
    KVAH("kVAh", "kVAh", PointUnits.UOMID_KVAH),                        //4
    KVARH("kVArh", "kVArh", PointUnits.UOMID_KVARH),                    //5
    KVOLTS("kVolts", "kVolts", PointUnits.UOMID_KVOLTS),                //6
    KQ("kQ", "kQ", PointUnits.UOMID_KQ),                                //7
    AMPS("Amps", "Amps", PointUnits.UOMID_AMPS),                        //8
    COUNTS("Counts", "Counts", PointUnits.UOMID_COUNTS),                //9
    DEGREES("Degrees", "Degrees", PointUnits.UOMID_DEGREES),            //10
    DOLLARS("Dollars", "Dollars", PointUnits.UOMID_DOLLARS),            //11
    DOLLAR_CHAR("Dollar Char","$", PointUnits.UOMID_DOLLAR_CHAR),       //12
    FEET("Feet", "Feet", PointUnits.UOMID_FEET),                        //13
    GALLONS("Gallons", "Gallons", PointUnits.UOMID_GALLONS),            //14
    GAL_PM("Gal/PM", "Gal/PM", PointUnits.UOMID_GAL_PM),                //15
    GAS_CFT("GAS-CFT", "GAS-CFT", PointUnits.UOMID_GAS_CFT),            //16
    HOURS("Hours", "Hours", PointUnits.UOMID_HOURS),                    //17
    LEVEL("Level", "Level", PointUnits.UOMID_LEVELS),                   //18
    MINUTES("Minutes", "Minutes", PointUnits.UOMID_MINUTES),            //19
    MW("MW", "MW", PointUnits.UOMID_MW),                                //20
    MWH("MWh", "MWh", PointUnits.UOMID_MWH),                            //21
    MVA("MVA", "MVA", PointUnits.UOMID_MVA),                            //22
    MVAR("MVAr", "MVAr", PointUnits.UOMID_MVAR),                        //23
    MVAH("MVAh", "MVAh", PointUnits.UOMID_MVAH),                        //24
    MVARH("MVArh", "MVArh", PointUnits.UOMID_MVARH),                    //25
    OPS("Ops", "Ops", PointUnits.UOMID_OPS),                            //26
    PF("PF", "PF", PointUnits.UOMID_PF),                                //27
    PERCENT("Percent", "Percent", PointUnits.UOMID_PERCENT),            //28
    PERCENT_CHAR("Percent Char", "%", PointUnits.UOMID_PERCENT),        //29
    PSI("PSI", "PSI", PointUnits.UOMID_PSI),                            //30
    SECONDS("Seconds", "Seconds", PointUnits.UOMID_SECONDS),            //31
    TEMP_F("Temp-F", "Temp-F", PointUnits.UOMID_TEMP_F),                //32
    TEMP_C("Temp-C", "Temp-C", PointUnits.UOMID_TEMP_C),                //33
    VARS("Vars", "Vars", PointUnits.UOMID_VARS),                        //34
    VOLTS("Volts", "Volts", PointUnits.UOMID_VOLTS),                    //35
    VOLTAMPS("VoltAmps", "VoltAmps", PointUnits.UOMID_VOLTAMPS),        //36
    VA("VA", "VA", PointUnits.UOMID_VA),                                //37
    CUBIC_FEET("Cubic Feet", "ft^3", PointUnits.UOMID_CUBIC_FEET),      //38
    WATTS("Watts", "Watts", PointUnits.UOMID_WATTS),                    //39
    HZ("Hz", "Hz", PointUnits.UOMID_HZ),                                //40
    VOLTS_FROM_V2H("Volts from V2H", "Volts", PointUnits.UOMID_VOLTS_V2H),//41
    AMPS_FROM_A2H("Amps from A2H", "Amps", PointUnits.UOMID_AMPS_V2H),  //42
    TAP("LTC Tap Position", "Tap", PointUnits.UOMID_TAP),               //43
    MILES("Miles", "Miles", PointUnits.UOMID_MILES),                    //44
    MS("Milliseconds", "Ms", PointUnits.UOMID_MS),                      //45
    PPM("Parts Per Million", "PPM", PointUnits.UOMID_PPM),              //46
    MPH("Miles Per Hour", "MPH", PointUnits.UOMID_MPH),                 //47
    INCHES("Inches", "Inches", PointUnits.UOMID_INCHES),                //48
    KPH("Kilometers Per Hour", "KPH", PointUnits.UOMID_KPH),            //49
    MILIBARS("Milibars", "Milibars", PointUnits.UOMID_MILIBARS),        //50
    KILOMETERS_PER_HOUR("Kilometers Per Hour", "km/h", PointUnits.UOMID_KH_H),//51
    METERS_PER_SECOND("Meters Per Second", "m/s", PointUnits.UOMID_M_S),//52
    KV("KVolts", "KV", PointUnits.UOMID_KV),                            //53
    UNDEFINED("Undefined", "UNDEF", PointUnits.UOMID_UNDEF),            //54
    CUBIC_METERS("Cubic Meters", "m^3", PointUnits.UOMID_CUBIC_METERS), //55
    ;
    
    private String longName;
    private String uomName;
    private int id;
    
    private UnitOfMeasure(String longName, String uomName, int id) {
        this.longName = longName;
        this.uomName = uomName;
        this.id = id;
    }
    
    public String getLongName() {
        return longName;
    }

    public String getUomName() {
        return uomName;
    }

    public int getId() {
        return id;
    }
}
