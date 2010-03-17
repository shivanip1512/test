#pragma once

#include "EmetconWords.h"

#include "ctitime.h"

#include <boost/function.hpp>
#include <boost/bind.hpp>

/*
    Move all MCT functions into here eventually. Phase 2 Make a MctBaseSim class containing shared functions.
*/

namespace Cti {
namespace Simulator {

class Mct410Sim
{
private:

    typedef boost::function1<bytes, Mct410Sim *>        function_read_t;
    typedef boost::function2<void,  Mct410Sim *, bytes> function_write_t;

    typedef std::map<unsigned, function_read_t>  function_reads_t;
    typedef std::map<unsigned, function_write_t> function_writes_t;

    static const function_reads_t  _function_reads;
    static       function_reads_t  initFunctionReads();
    static       function_reads_t  makeFunctionReadRange(unsigned readMin, unsigned readMax, boost::function2<bytes, Mct410Sim *, unsigned> fn);

    static const function_writes_t _function_writes;
    static       function_writes_t initFunctionWrites();

    int _address;

    struct llp_interest_t
    {
        llp_interest_t() : channel(1) { };

        CtiTime time;
        unsigned channel;

    } _llp_interest;

    bytes _memory_map;

    static const double Pi;
    static const CtiTime DawnOfTime;
    static const unsigned randomReadingChance;

    unsigned getHectoWattHours(const unsigned _address, const CtiTime c_time);

    static double makeValue_random_consumption ();
    static double makeValue_consumption        (const unsigned address, const CtiTime &c_time, const unsigned duration);
    static double makeValue_instantaneousDemand(const unsigned address, const CtiTime &c_time);
    static double makeValue_averageDemand      (const unsigned address, const CtiTime &begin_time, const unsigned duration);

    bytes getZeroes();

    bytes getAllCurrentMeterReadings();
    bytes getAllRecentDemandReadings();
    bytes getAllCurrentPeakDemandReadings();
    bytes getLongLoadProfile(unsigned offset);
    bytes getLoadProfile    (unsigned offset, unsigned channel);

    CtiTime  getLongLoadProfilePeriodOfInterest() const;
    unsigned getLongLoadProfileChannelOfInterest() const;

    static void fill_loadProfile(const unsigned address, const CtiTime &blockStart, const int interval_length, byte_appender &out_itr);

    void putPointOfInterest(const bytes &payload);

    enum Numerics
    {
        SecondsPerMinute =     60,
        SecondsPerHour   =     3600,
        SecondsPerDay    =    86400,
        SecondsPerYear   = 31556926,

        ReadLength = 13,

        Demand_Interval_seconds = 300,

        LoadProfile_Interval_seconds  = 3600,
        LoadProfile_IntervalsPerBlock = 6
    };

    enum Writes
    {
    };

    enum FunctionReads
    {
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
        FR_AllRecentDemandReadings = 0x92,
        FR_AllCurrentPeakDemandReadings = 0x93
    };

    enum FunctionWrites
    {
        FW_Intervals       = 0x03,
        FW_PointOfInterest = 0x05
    };

    bytes processRead (bool function, unsigned function_code);
    bool    processWrite(bool function, unsigned function_code, bytes data);

    static unsigned int mctGetValue(int mctAddress, CtiTime time);
    unsigned char *getLongLoadProfileData(int function, int bytesToReturn);

public:

    Mct410Sim(int address);

//  TODO-P4: See PlcInfrastructure::oneWayCommand()
    bool read (const words_t &request_words, words_t &response_words);
    bool write(const words_t &request_words);

protected:
    // Moved to protected for unit testing.
    static double getConsumptionMultiplier(const unsigned address);
};

}
}

