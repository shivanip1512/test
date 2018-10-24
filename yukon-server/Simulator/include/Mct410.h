#pragma once

#include <iostream>

#include "EmetconWords.h"
#include "ctitime.h"
#include "SimulatorLogger.h"
#include "DeviceMemoryManager.h"
#include "BehaviorCollection.h"
#include "MctBehavior.h"

#include <boost/ptr_container/ptr_vector.hpp>
#include <boost/bind.hpp>

/*
    Move all MCT functions into here eventually. Phase 2 Make a MctBaseSim class containing shared functions.
*/

namespace Cti {
namespace Simulator {

class Mct410Sim
{
private:

    typedef std::function<bytes (Mct410Sim&)>           function_read_t;
    typedef function_read_t                             data_read_t;
    typedef std::function<void (Mct410Sim *, bytes)>    function_write_t;
    typedef function_write_t                            data_write_t;
    typedef std::function<void (Mct410Sim&)>            command_t;

    typedef std::map<unsigned, function_read_t>  function_reads_t;
    typedef std::map<unsigned, function_write_t> function_writes_t;
    typedef std::map<unsigned, data_read_t>      data_reads_t;
    typedef std::map<unsigned, data_write_t>     data_writes_t;
    typedef std::map<unsigned, command_t>        commands_t;
    typedef boost::ptr_vector<MctBehavior>       behaviorVec_t;

    static const function_reads_t  _function_reads;
    static       function_reads_t  initFunctionReads();
    static       function_reads_t  makeFunctionReadRange(unsigned readMin, unsigned readMax, std::function<bytes (Mct410Sim&, unsigned)> fn);

    static const function_writes_t _function_writes;
    static       function_writes_t initFunctionWrites();

    static const data_reads_t      _data_reads;
    static       data_reads_t      initDataReads();

    static const data_writes_t     _data_writes;
    static       data_writes_t     initDataWrites();

    static const commands_t        _commands;
    static       commands_t        initCommands();

    static BehaviorCollection<MctBehavior> _behaviorCollection;

    static bool _behaviorsInited;

    int _address;
    std::string _mct410tag;

    struct llp_interest_t
    {
        llp_interest_t() : channel(1) { };

        CtiTime c_time;
        unsigned channel;

    } _llp_interest;

    DeviceMemoryManager _memory;

    static const double   Pi;
    static const double   alarmFlagChance;
    static const unsigned MemoryMapSize;

    static double makeValue_instantaneousDemand(const unsigned address, const CtiTime &c_time);
    static double makeValue_averageDemand      (const unsigned address, const CtiTime &begin_time, const unsigned duration);

    static int getDynamicDemand(const unsigned address, const unsigned demandIntervalSeconds, unsigned nowSeconds);

    void writeNewPeakDemand(const int dynamicDemand, const unsigned seconds);

    void processFlags(Logger &logger);

    void putScheduledFreezeDay(const bytes &data);
    void putDisplayParameters(const bytes &data);
    void putLcdConfiguration1(const bytes &data);
    void putLcdConfiguration2(const bytes &data);
    void putIntervals(const bytes &data);

    // Data Reads
    bytes getFreezeInfo();
    bytes getScheduledFreezeDay();

    void  putFreeze(unsigned incoming_freeze);

    void setLpInterval    (unsigned interval_minutes);
    void setDemandInterval(unsigned interval_minutes);
    void setVoltageDemandInterval(unsigned interval_minutes);
    void setVoltageLpInterval(unsigned interval_minutes);

    unsigned getLpIntervalSeconds();

    bytes getFrozenKwh();
    bytes getAllFrozenChannel1Readings();
    bytes getAllCurrentMeterReadings();
    bytes getAllRecentDemandReadings();
    bytes getAllCurrentPeakDemandReadings();
    bytes getAllCurrentVoltageReadings();
    bytes getFrozenMinMaxVoltageReadings();
    bytes getDisplayParameters();
    bytes getLcdConfiguration1();
    bytes getLcdConfiguration2();
    bytes getDisconnectStatus();
    bytes getSingleDayRead  (const unsigned offset, const unsigned channel);
    bytes getLongLoadProfile(const unsigned offset);
    bytes getLoadProfile    (const unsigned offset, const unsigned channel);

    bytes getValueVectorFromMemory(unsigned pos, unsigned length);

    CtiTime  getLongLoadProfilePeriodOfInterest() const;
    unsigned getLongLoadProfileChannelOfInterest() const;

    void putPointOfInterest(const bytes &payload);

    void connect();
    void disconnect();
    void clearEventFlags();

    enum Numerics
    {
        SecondsPerMinute =       60,
        SecondsPerHour   =     3600,
        SecondsPerDay    =    86400,
        SecondsPerYear   = 31556926,

        ReadLength = 13,

        LoadProfile_IntervalsPerBlock = 6
    };

    enum Commands
    {
        C_Connect = 0x4C,
        C_Disconnect = 0x4D,
        C_PutFreezeOne = 0x51,
        C_PutFreezeTwo = 0x52,
        C_SetLpIntervalFiveMin = 0x70,
        C_SetLpIntervalFifteenMin = 0x71,
        C_SetLpIntervalThirtyMin = 0x72,
        C_SetLpIntervalSixtyMin = 0x73,
        C_ClearAllEventFlags = 0x8A
    };

    enum FunctionReads
    {
        FR_Channel1SingleDayReadMin = 0x30,
        FR_Channel1SingleDayReadMax = 0x37,

        FR_LongLoadProfileTableMin = 0x40,
        FR_LongLoadProfileTableMax = 0x4f,

        FR_LoadProfileChannel1Min  = 0x50,
        FR_LoadProfileChannel1Max  = 0x5f,
        FR_LoadProfileChannel2Min  = 0x60,
        FR_LoadProfileChannel2Max  = 0x6f,
        FR_LoadProfileChannel3Min  = 0x70,
        FR_LoadProfileChannel3Max  = 0x7f,
        FR_LoadProfileChannel4Min  = 0x80,
        FR_LoadProfileChannel4Max  = 0x8f,

        FR_AllCurrentMeterReadings = 0x90,
        FR_GetFrozen_kWh = 0x91,
        FR_AllRecentDemandReadings = 0x92,
        FR_AllCurrentPeakDemandReadings = 0x93,
        FR_AllFrozenChannel1Readings = 0x94,
        FR_AllCurrentVoltageReadings = 0x95,
        FR_FrozenMinMaxVoltageReadings = 0x96,

        // Note that in Porter, the function bit is included in the function
        // code, so that the function 0x1f3 in porter corresponds to 0xf3 here.
        FR_DisplayParameters       = 0xf3,
        FR_LcdConfiguration1       = 0xf6,
        FR_LcdConfiguration2       = 0xf7,
        FR_DisconnectStatus        = 0xfe,
    };

    enum FunctionWrites
    {
        FW_Intervals       = 0x03,
        FW_PointOfInterest = 0x05,
        FW_ScheduledFreezeDay = 0x4f,
        // TODO: These are unique to the MCT-420.  They should be broken out 
        //       into a specialization eventually.
        FW_DisplayParameters = 0xf3,
        FW_LcdConfiguration1 = 0xf6,
        FW_LcdConfiguration2 = 0xf7
    };

    enum DataReads
    {
        DR_GetFreezeInfo      = 0x26,
        DR_ScheduledFreezeDay = 0x4f
    };

    // TODO: Since adding function information to this map, it probably should be turned into a structure.
    enum MemoryMap
    {
        MM_SspecLo = 0x00,
        MM_SspecRev = 0x01,
        MM_SspecHi = 0x04,
        MM_EventFlags1  = 0x06,
        MM_EventFlags2  = 0x07,
        MM_MeterAlarms1 = 0x08,
        MM_EventFlags1AlarmMask = 0x0A,
        MM_Status2Flags = 0x0E,
        MM_DemandInterval = 0x1a,
        MM_LoadProfileInterval = 0x1b,
        MM_VoltageDemandInterval = 0x1c,
        MM_VoltageLoadProfileInterval = 0x1d,
        MM_LastFreezeTimestamp = 0x26,
        MM_FreezeCounter = 0x2a,
        MM_LastVoltageFreezeTimestamp = 0x2b,
        MM_VoltageFreezeCounter = 0x2f,
        MM_ScheduledFreezeDay = 0x4f,
        MM_FrozenMeterReading1 = 0x83,
        MM_CurrentPeakDemand1 = 0x88,
        MM_FrozenPeakDemand1 = 0x8a,
        MM_CurrentPeakDemand1Timestamp = 0x91,
        MM_FrozenPeakDemand1Timestamp = 0x95,

        // These are really function write codes, but we need to make this persistent so 
        // the data got stuck in here.
        MM_DisplayParameters = 0xf3,
        MM_LcdConfiguration1 = 0xf6,    // This involves 13 bytes that would overlap the    
        MM_LcdConfiguration2 = 0x103    // next memory, so we moved LcdConfiguration2 down.
    };

    enum MemoryMapLengths
    {
        MML_Default = 1,
        MML_FrozenPeakDemand1 = 2,
        MML_CurrentPeakDemand1 = 2,
        MML_FrozenMeterReading1 = 3,
        MML_LastFreezeTimestamp = 4,
        MML_LastVoltageFreezeTimestamp = 4,
        MML_FrozenPeakDemandTimestamp = 4,
        MML_CurrentPeakDemand1Timestamp = 4,
        MML_DisplayParameters = 3,
        MML_LcdConfiguration1 = 13,
        MML_LcdConfiguration2 = 13
    };

    enum EventFlags1StatusFlags
    {
        EF1_TamperFlag  = 0x80
        /* Other Status flags that may be used in the future:
            DSTChange   = 0x40,
            HolidayFlag = 0x20,
            RTCAdjusted = 0x10,
            PowerFailCarryover  = 0x08,
            OverVoltage    = 0x04,
            UnderVoltage   = 0x02,
            PowerFailEvent = 0x01
        */
    };

    enum EventFlags2StatusFlags
    {
        EF2_ZeroUsage = 0x01
    };

    enum MeterAlarms1StatusFlags
    {
        MA1_ReversePower = 0x80
    };

    bytes processRead (bool function, unsigned function_code, Logger &logger);
    bool  processWrite(bool function, unsigned function_code, bytes data);

    static unsigned int mctGetValue(int mctAddress, CtiTime c_time);
    unsigned char *getLongLoadProfileData(int function, int bytesToReturn);

    static void appendCalculatedLpValue(const double consumptionWs, byte_appender &out_itr);

public:

    Mct410Sim(int address);

    static void initBehaviors(Logger &logger);

//  TODO-P4: See PlcInfrastructure::oneWayCommand()
    bool read (const words_t &request_words, words_t &response_words, Logger &logger);
    bool write(const words_t &request_words);

protected:

    struct peak_demand_t
    {
        unsigned short peakDemand;
        unsigned peakTimestamp;
    };

    static const CtiTime DawnOfTime;

    static unsigned getTablePointer(const CtiTime c_time, unsigned intervalSeconds);

    // Moved to protected for unit testing.
    static double getConsumptionMultiplier(const unsigned address);
    static double makeValueConsumption    (const unsigned address, const CtiTime consumptionTime, const unsigned duration);

    static unsigned getHectoWattHours(const unsigned address, const CtiTime c_time);

    static void fillLoadProfile    (const unsigned address, const CtiTime &blockStart, const unsigned interval_length, byte_appender &out_itr);
    static void fillLongLoadProfile(const unsigned address, const CtiTime &blockStart, const unsigned interval_length, byte_appender &out_itr);

    static bytes formatAllFrozenChannel1Readings(const unsigned long frozenRead, const unsigned long frozenTime,
                                                 const unsigned hwh, const unsigned freezeCounter, const short peakDemand);

    static bytes formatAllCurrentMeterReadings(const unsigned address);
    static bytes formatAllRecentDemandReadings(const int dynamicDemand);
    static bytes formatAllCurrentPeakDemandReadings(const unsigned address, const bytes &peakDemand, const bytes &peakTimestamp);

    static bytes formatSingleDayRead(const unsigned hectoWattHours, const peak_demand_t peakDemandDaily, const unsigned outages, 
                                     const unsigned day, const unsigned month, const unsigned channel);

    static bytes formatLongLoadProfile(unsigned offset, const unsigned address, const CtiTime periodOfInterest,
                                       const unsigned channelOfInterest, const unsigned lpIntervalSeconds);

    static bytes formatFrozenKwh(const bytes &frozenData, unsigned freezeCounter);

    static bytes formatLoadProfile(const unsigned offset, const unsigned channel, const unsigned address,
                                   const CtiTime readTime, const unsigned lpIntervalSeconds);

    static peak_demand_t checkForNewPeakDemand(const unsigned address, const unsigned demandInterval,
                                               const unsigned lastFreezeTimestamp, const CtiTime c_time);
};

}
}

