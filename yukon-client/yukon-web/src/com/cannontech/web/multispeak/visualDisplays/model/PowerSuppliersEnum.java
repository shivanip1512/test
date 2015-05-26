package com.cannontech.web.multispeak.visualDisplays.model;

public enum PowerSuppliersEnum {
    
    // The first four numbers represent the starting object ID for Today IH, Peak IH, Today LC Prediction, and Tomorrow LC Prediction.
    // These object IDs are static and know to increment by 1, starting with the start Id, and through 24 IDs.
    
    // The last five number represent the object ID for Current Load, Current IH, Load To Peak, Peak IH, and Peak Day Timestamp.
    // These object IDs are static.
    
    PENELEC("PENELEC",      1060001, 1060176, 1060373, 1060555, 1001001, 1001008, 1001015, 1060351, 1060351),
    MET_ED("MET ED",        1060026, 1060201, 1060399, 1060581, 1001002, 1001009, 1001016, 1060354, 1060354),
    JCPL("JCPL",            1060051, 1060226, 1060425, 1060607, 1001003, 1001010, 1001017, 1060357, 1060357),
    PPL("PPL",              1060076, 1060251, 1060451, 1060633, 1001004, 1001011, 1001018, 1060360, 1060360),
    WEST_PENN("WEST PENN",  1060101, 1060276, 1060477, 1060659, 1001005, 1001012, 1001019, 1060363, 1060363),
    PJM("PJM",              1060126, 1060301, 1060503, 1060685, 1001006, 1001013, 1001020, 1060366, 1060366),
    APS("APS",              1060151, 1060326, 1060529, 1060711, 1001007, 1001014, 1001021, 1060369, 1060369);

    private String description;
    private int todayIntegratedHourlyIdStart;
    private int peakDayIntegratedHourlyIdStart;
    private int todayLoadControlPredicationIdStart;
    private int tomorrowLoadControlPredicationIdStart;
    
    private int currentLoadId;
    private int currentIhId;
    private int loadToPeakId;
    private int peakIhLoadId;
    private int peakDayTimestampId;
    
    PowerSuppliersEnum(String description, int todayIntegratedHourlyIdStart,
            int peakDayIntegratedHourlyIdStart,
            int todayLoadControlPredicationIdStart,
            int tomorrowLoadControlPredicationIdStart,
            int currentLoadId,
            int currentIhId,
            int loadToPeakId,
            int peakIhLoadId,
            int peakDayTimestampId) {
        
        this.description = description;
        this.todayIntegratedHourlyIdStart = todayIntegratedHourlyIdStart;
        this.peakDayIntegratedHourlyIdStart = peakDayIntegratedHourlyIdStart;
        this.todayLoadControlPredicationIdStart = todayLoadControlPredicationIdStart;
        this.tomorrowLoadControlPredicationIdStart = tomorrowLoadControlPredicationIdStart;
        
        this.currentLoadId = currentLoadId;
        this.currentIhId = currentIhId;
        this.loadToPeakId = loadToPeakId;
        this.peakIhLoadId = peakIhLoadId;
        this.peakDayTimestampId = peakDayTimestampId;
    }

    public String getDescription() {
        return description;
    }

    public int getTodayIntegratedHourlyIdStart() {
        return todayIntegratedHourlyIdStart;
    }

    public int getPeakDayIntegratedHourlyIdStart() {
        return peakDayIntegratedHourlyIdStart;
    }

    public int getTodayLoadControlPredicationIdStart() {
        return todayLoadControlPredicationIdStart;
    }

    public int getTomorrowLoadControlPredicationIdStart() {
        return tomorrowLoadControlPredicationIdStart;
    }

    
    public int getCurrentLoadId() {
        return currentLoadId;
    }

    public int getCurrentIhId() {
        return currentIhId;
    }

    public int getLoadToPeakId() {
        return loadToPeakId;
    }

    public int getPeakIhLoadId() {
        return peakIhLoadId;
    }

    public int getPeakDayTimestampId() {
        return peakDayTimestampId;
    }
    
}