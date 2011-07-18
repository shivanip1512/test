#include "precompiled.h"
#include "Mct410.h"
#include "logger.h"
#include "ScopedLogger.h"
#include "dev_mct410.h"
#include "EmetconWords.h"
#include "cparms.h"
#include "guard.h"

using namespace std;

namespace Cti {
namespace Simulator {

const CtiTime Mct410Sim::DawnOfTime = CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2005),0, 0, 0);
const double Mct410Sim::randomReadingChance = gConfigParms.getValueAsDouble("SIMULATOR_RANDOM_READING_CHANCE_PERCENT");

//  Temporary class to access protected functions in Mct410Device and Mct4xxDevice.
//  To be deleted when we move functions to a shared location.
struct mct410_utility : private Devices::Mct410Device
{
    using Mct410Device::makeDynamicDemand;
    using Mct410Device::crc8;
};

const Mct410Sim::function_reads_t  Mct410Sim::_function_reads  = Mct410Sim::initFunctionReads();
const Mct410Sim::function_writes_t Mct410Sim::_function_writes = Mct410Sim::initFunctionWrites();
const Mct410Sim::commands_t        Mct410Sim::_commands        = Mct410Sim::initCommands();

const double Mct410Sim::Pi = 4.0 * atan(1.0);

Mct410Sim::Mct410Sim(int address) :
    _address(address),
    _mct410tag("MCT410(" + CtiNumStr(address) + ")")
{
    //  _llp_interest should eventually be persisted and restored.
    //  We could access the DynamicPaoInfo table for this... ?
    // _llp_interest.time = CtiTime::now();
    _memory_map = bytes(20, 0x00);  // Initialize the memory map to have 20 bytes of 0s for data.

    // Memory map position 0x0A is the EventFlags-1 Alarm Mask. This needs to be initialized to 0x80 in order
    // to catch the tamper flag bit that may be set at memory map position 0x06 and set the general alarm bit.
    // Refer to section 4.10 of the MCT-410 SSPEC doc for more information.
    _memory_map[MM_EventFlags1AlarmMask] = EF1_TamperFlag;
}


Mct410Sim::function_reads_t Mct410Sim::makeFunctionReadRange(unsigned readMin, unsigned readMax, boost::function2<bytes, Mct410Sim *, unsigned> fn)
{
    function_reads_t range;

    for( unsigned offset = 0; offset <= (readMax - readMin); ++offset )
    {
        range[readMin + offset] = boost::bind(fn, _1, offset);
    }

    return range;
}

Mct410Sim::function_reads_t Mct410Sim::initFunctionReads()
{
    function_reads_t reads;
    function_reads_t read_range;

    reads[FR_AllCurrentMeterReadings]      = function_read_t(&Mct410Sim::getAllCurrentMeterReadings);
    reads[FR_AllRecentDemandReadings]      = function_read_t(&Mct410Sim::getAllRecentDemandReadings);
    reads[FR_AllCurrentPeakDemandReadings] = function_read_t(&Mct410Sim::getAllCurrentPeakDemandReadings);

    read_range = makeFunctionReadRange(FR_LongLoadProfileTableMin,
                                       FR_LongLoadProfileTableMax, &Mct410Sim::getLongLoadProfile);

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel1Min,
                                       FR_LoadProfileChannel1Max,
                                       boost::bind(&Mct410Sim::getLoadProfile, _1, 1, _2));

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel2Min,
                                       FR_LoadProfileChannel2Max,
                                       boost::bind(&Mct410Sim::getLoadProfile, _1, 2, _2));

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel3Min,
                                       FR_LoadProfileChannel3Max,
                                       boost::bind(&Mct410Sim::getLoadProfile, _1, 3, _2));

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel4Min,
                                       FR_LoadProfileChannel4Max,
                                       boost::bind(&Mct410Sim::getLoadProfile, _1, 4, _2));

    reads.insert(read_range.begin(), read_range.end());

    return reads;
}


Mct410Sim::function_writes_t Mct410Sim::initFunctionWrites()
{
    function_writes_t writes;

    writes[FW_PointOfInterest] = function_write_t(&Mct410Sim::putPointOfInterest);

    return writes;
}

Mct410Sim::commands_t Mct410Sim::initCommands()
{
    commands_t commands;

    commands[C_ClearAllEventFlags] = command_t(&Mct410Sim::clearEventFlags);

    return commands;
}


//  TODO-P4: See PlcInfrastructure::oneWayCommand()
bool Mct410Sim::read(const words_t &request_words, words_t &response_words, Logger &logger)
{
    ScopedLogger scope = logger.getNewScope(_mct410tag);

    if( request_words.empty() || !request_words[0] )
    {
        return false;
    }
    if( request_words[0]->type != EmetconWord::WordType_B )
    {
        //  eventually add G words?
        return false;
    }

    const EmetconWordB &b_word = *(boost::static_pointer_cast<const EmetconWordB>(request_words[0]));

    if( b_word.write )
    {
        return false;
    }

    // Potentially set zero usage flag or reverse-power flag...
    double chance = gConfigParms.getValueAsDouble("SIMULATOR_ALARM_FLAG_CHANCE_PERCENT");
    double dist = (rand() / double(RAND_MAX + 1)) * 100;
    if( dist < chance )
    {
        // 50/50 chance that the reverse-power flag is set or the zero usage flag is set.
        double value = rand() / double(RAND_MAX + 1);

        if( value < 0.50 )
        {
            // Only set the zero usage flag to true if it isn't already set...
            if ( (_memory_map[MM_EventFlags2] & EF2_ZeroUsage) == 0 )
            {
                _memory_map[MM_EventFlags2] |= EF2_ZeroUsage;
                scope.breadcrumbLog("******** Zero-Usage flag set! ********");
            }
        }
        else // If value >= 0.50
        {
            // Only set the reverse-power flag to true if it isn't already set...
            if( (_memory_map[MM_MeterAlarms1] & MA1_ReversePower) == 0 )
            {
                _memory_map[MM_MeterAlarms1] |= MA1_ReversePower;
                scope.breadcrumbLog("******** Reverse-power flag set!********");
            }
        }
    }

    // Tamper flag (bit 7 of address 0x06) gets set if reverse-power or zero usage bits are set.
    if( ((_memory_map[MM_EventFlags2] & EF2_ZeroUsage) != 0) || ((_memory_map[MM_MeterAlarms1] & MA1_ReversePower) != 0) )
    {
        // Only set tamper flag if it isn't already set.
        if ( (_memory_map[MM_EventFlags1] & EF1_TamperFlag) == 0 )
        {
            _memory_map[MM_EventFlags1] |= EF1_TamperFlag;
            scope.breadcrumbLog("******** Tamper flag set! ********");
        }
    }

    const bytes response_bytes = processRead(b_word.function, b_word.function_code);

    if( response_bytes.size() < EmetconWordD1::PayloadLength +
                                EmetconWordD2::PayloadLength +
                                EmetconWordD3::PayloadLength )
    {
        return false;
    }

    if( b_word.words_to_follow < 1 )
    {
        return true;
    }

    // Check to see if the alarm bit needs to be set! If the tamper flag has
    // previously been set, then the general alarm bit needs to be as well.
    bool alarm = false;
    if( _memory_map.size() >= MM_EventFlags1AlarmMask )
    {
        if( (_memory_map[MM_EventFlags1] & _memory_map[MM_EventFlags1AlarmMask]) > 0 )
        {
            alarm = true;
        }
    }

    response_words.push_back(word_t(new EmetconWordD1(b_word.repeater_variable,
                                                      b_word.dlc_address & ((1 << 14) - 1),  //  lowest 13 bits set
                                                      response_bytes[0],
                                                      response_bytes[1],
                                                      response_bytes[2],
                                                      0,
                                                      alarm)));

    if( b_word.words_to_follow < 2 )
    {
        return true;
    }

    response_words.push_back(word_t(new EmetconWordD2(response_bytes[3],
                                                      response_bytes[4],
                                                      response_bytes[5],
                                                      response_bytes[6],
                                                      response_bytes[7],
                                                      0,
                                                      0)));

    if( b_word.words_to_follow < 3 )
    {
        return true;
    }

    response_words.push_back(word_t(new EmetconWordD3(response_bytes[8],
                                                      response_bytes[9],
                                                      response_bytes[10],
                                                      response_bytes[11],
                                                      response_bytes[12],
                                                      0,
                                                      0)));

    return true;
}


bytes Mct410Sim::processRead(bool function_read, unsigned function)
{
    bytes read_bytes = bytes(ReadLength, '\0');

    if( function_read )
    {
        function_reads_t::const_iterator fn_itr = _function_reads.find(function);

        if( fn_itr != _function_reads.end() )
        {
            bytes function_read_bytes = fn_itr->second(this);

            copy(function_read_bytes.begin(),
                 function_read_bytes.begin() + min(function_read_bytes.size(), (bytes::size_type)ReadLength),
                 read_bytes.begin());
        }
    }
    else
    {
        copy(_memory_map.begin() + min(_memory_map.size(), function),
             _memory_map.begin() + min(_memory_map.size(), function + ReadLength),
             read_bytes.begin());
    }

    return read_bytes;
}

/*
//  TODO: Finish initial reads (kWh, LLP)
*/

//  TODO-P4: See PlcInfrastructure::oneWayCommand()
bool Mct410Sim::write(const words_t &request_words)
{
//  TODO-P2: Combine word-checking logic with that in Ccu710::validateFeederRequest()?
//             How to do logging from inside MCTs?  Will they be asynchronous at some point?
//             They shouldn't need to be threaded, just select()-style polled, so it can work
//             with a shared logger object...

    if( request_words.empty() || !request_words[0] )
    {
        return false;
    }
    if( request_words[0]->type != EmetconWord::WordType_B )
    {
        //  eventually add G words?
        return false;
    }

    const EmetconWordB &b_word = *(boost::static_pointer_cast<const EmetconWordB>(request_words[0]));

    if( !b_word.write )
    {
        return false;
    }

    if( (b_word.words_to_follow + 1) != request_words.size() )
    {
        //_logger.log("Incorrect word count for write", feeder_operation.words.size());
        return false;
    }

    bytes data;

    for( unsigned index = 1; index <= b_word.words_to_follow; ++index )
    {
        if( !request_words[index] || request_words[index]->type != EmetconWord::WordType_C )
        {
            //_logger.log("Invalid data word", index);
            return false;
        }

        const EmetconWordC &c_word = *(boost::static_pointer_cast<const EmetconWordC>(request_words[index]));

        data.insert(data.end(),
                    c_word.data.begin(),
                    c_word.data.end());

    }

    return processWrite(b_word.function, b_word.function_code, data);
}

bool Mct410Sim::processWrite(bool function_write, unsigned function, bytes data)
{
    if( function_write )
    {
        function_writes_t::const_iterator fn_itr = _function_writes.find(function);

        if( fn_itr != _function_writes.end() )
        {
            fn_itr->second(this, data);
        }
    }
    else if( !function_write && data.empty() )
    {
        commands_t::const_iterator cmd_itr = _commands.find(function);

        if( cmd_itr != _commands.end() )
        {
            cmd_itr->second(this);
        }
    }
    else if( !function_write && (data.size() > 0) )
    {
        if( _memory_map.size() > function )
        {
            copy(data.begin(),
                 data.begin() + min(data.size(), _memory_map.size() - function),
                 _memory_map.begin() + function);
        }
    }

    return true;
}

bytes Mct410Sim::getZeroes()
{
    return bytes(13, 0x00);
}

bytes Mct410Sim::getAllCurrentPeakDemandReadings()
{
    bytes data = bytes(13, 0x00);

    CtiTime now;

    //  ensure reads during the same minute will return the same value.
    now -= now.second();

    const unsigned consumption_Wh = getHectoWattHours(_address, now);

    const unsigned char *byte_ptr = reinterpret_cast<const unsigned char *>(&consumption_Wh);

    // Function Read 0x93 contains the consumption read at
    // positions 6-8 of the 13 byte data response.
    reverse_copy(byte_ptr, byte_ptr + 3, data.begin() + 6);

    return data;
}

bytes Mct410Sim::getAllCurrentMeterReadings()
{
    bytes data = bytes(13, 0x00);

    CtiTime now;

    //  ensure reads during the same minute will return the same value.
    now -= now.second();

    const unsigned   consumption_hWh  = getHectoWattHours(_address, now);

    const unsigned char *byte_ptr = reinterpret_cast<const unsigned char *>(&consumption_hWh);

    //  copy out the three least-significant bytes in big-endian order
    reverse_copy(byte_ptr, byte_ptr + 3, data.begin());

    return data;
}

unsigned Mct410Sim::getHectoWattHours(const unsigned address, const CtiTime now )
{
    double dist = rand() / double(RAND_MAX + 1);
    double chance = dist * 100;

    if(chance < randomReadingChance)
    {
        return makeValue_random_consumption(address);
    }

    const unsigned duration = now.seconds() - DawnOfTime.seconds();
    const double   consumption_Ws  = makeValue_consumption(address, DawnOfTime, duration);
    const double   consumption_Wh  = consumption_Ws / SecondsPerHour;
    //  hecto-watt-hours - the 100 watt-hour units the MCT returns

    // Mod the hWh by 10000000 to reduce the range from 0 to 9999999,
    // since the MCT Device reads hWh this corresponds to 999,999.9 kWh
    // which is the desired changeover point.
    return int(consumption_Wh / 100.0) % 10000000;
}

bytes Mct410Sim::getAllRecentDemandReadings()
{
    bytes data = bytes(13, 0x00);

    const unsigned now_seconds = CtiTime::now().seconds();

    const unsigned beginningOfLastInterval = now_seconds - (now_seconds % Demand_Interval_seconds) - Demand_Interval_seconds;

    const double   demand_Ws  = makeValue_consumption(_address, beginningOfLastInterval, Demand_Interval_seconds);
    const double   demand_Wh  = demand_Ws / SecondsPerHour;

    int dynamicDemand = mct410_utility::makeDynamicDemand(demand_Wh);

    const unsigned char *demand_ptr = reinterpret_cast<const unsigned char *>(&dynamicDemand);

    //  copy out the two least-significant bytes in big-endian order
    reverse_copy(demand_ptr, demand_ptr + 2, data.begin());

//  TODO-P2: recent voltage, outages, ch2/3 demand

    return data;
}

double Mct410Sim::getConsumptionMultiplier(const unsigned address)
{
    unsigned address_range = address % 1000;

    if( address_range < 400 )               //  This section of if-statements is used to determine
    {                                       //  the multiplier of the consumption used by a household
        return 1.0;                         //  based on the address of the meter. The following scale
    }                                       //  shows how these multipliers are determined:
    else if( address_range < 600 )          //
    {                                       //  Address % 1000:
        return 2.0;                         //     Range 000-399: 1x Consumption Multiplier - 40% of Households
    }                                       //
    else if( address_range < 800 )          //     Range 400-599: 2x Consumption Multiplier - 20% of Households
    {                                       //
        return 3.0;                         //     Range 600-799: 3x Consumption Multiplier - 20% of Households
    }                                       //
    else if( address_range < 950 )          //     Range 800-949: 5x Consumption Multiplier - 15% of Households
    {                                       //
        return 5.0;                         //     Range 950-994: 10x Consumption Multiplier - 4.5% of Households
    }                                       //
    else if( address_range < 995 )          //     Range 995-999: 20x Consumption Multiplier - 0.5% of Households
    {                                       //
        return 10.0;                        //  This scale was made in order to represent more real-world
    }                                       //  readings and model the fact that some households consume
    else                                    //  drastically more than other households do. The consumption
    {
        return 20.0;
    }
}

double Mct410Sim::makeValue_random_consumption(const unsigned address)
{
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << "******** Random consumption value generated for address " << address << " ********" << endl;
    }
    double dist = rand() / double(RAND_MAX+1);
    return int(dist * 10000000);
}

//  The consumption value is constructed using the current time and meter address.
double Mct410Sim::makeValue_consumption(const unsigned address, const CtiTime &c_time, const unsigned duration)
{
    if( duration == 0 )  return 0;

    const double consumption_multiplier = getConsumptionMultiplier(address);

    const unsigned begin_seconds = c_time.seconds();
    const unsigned end_seconds   = c_time.seconds() + duration;

    const double year_period = 2.0 * Pi / static_cast<double>(SecondsPerYear);
    const double day_period  = 2.0 * Pi / static_cast<double>(SecondsPerDay);

    const double year_period_reciprocal = 1.0 / year_period;
    const double day_period_reciprocal  = 1.0 / day_period;

    /*  Introduced an offset value to be added to the value of the curve
        based  on the value of the address passed in to the function. This
        offset allows for a range of 0-7350 to be added to the kWh reading
        that is returned to the device, thus providing a better way of
        distinguishing meter readings whose addresses are similar or near
        each other in value. Meters whose addresses are within one of each
        other will now have a 7.35 kWh difference between them for easier
        distinguishing.                                                     */
    const double offset = (address % 1000) * 7.35 * 3600000;

    /*  These multipliers affect the amplification of the curve for the
        consumption. amp_year being set at 700.0 gives a normalized
        curve for the meter which results in a 1x Consumption Multiplier
        calculating to about 775-875 kWh per month, as desired.             */
    const double amp_year =  700.0;
    const double amp_day  =  500.0;

//  TODO-P3: move all value computation into policy classes
    return (amp_year * (duration + year_period_reciprocal * (sin(begin_seconds * year_period) - sin(end_seconds * year_period))) +
           amp_day  * (duration + day_period_reciprocal  * (sin(begin_seconds * day_period)  - sin(end_seconds * day_period))) +
           offset) * consumption_multiplier;
}

//  Generate output using two cosine waves - 1 year period and 1 day period.
double Mct410Sim::makeValue_instantaneousDemand(const unsigned address, const CtiTime &c_time)
{
    //  demand is modeled by:
    //
    //    day_amplitude  * (1 + cos(2 * pi * day_fraction))
    //    +
    //    year_amplitude * (1 + cos(2 * pi * year_fraction))

    const double day_curve  = 1 + cos(2 * Pi * c_time.seconds() / static_cast<double>(SecondsPerDay));
    const double year_curve = 1 + cos(2 * Pi * c_time.seconds() / static_cast<double>(SecondsPerYear));

    const double amp_day  = 500.0;
    const double amp_year = 700.0;

    return amp_day  * day_curve +
           amp_year * year_curve;
}

//  Generate output using two cosine waves - 1 year period and 1 day period.
double Mct410Sim::makeValue_averageDemand(const unsigned address, const CtiTime &begin_time, const unsigned duration)
{
    if( duration == 0 )
    {
        return makeValue_instantaneousDemand(address, begin_time);
    }

    return makeValue_consumption(address, begin_time, duration) / duration;
}

bytes Mct410Sim::getLongLoadProfile(unsigned offset)
{
    // Example of the crc for byte 0 of LLP
    //
    //    unsigned char interest[5];
    //    unsigned long tmptime = _llpInterest.time;
    //
    //    interest[0] = (tmptime >> 24) & 0x000000ff;
    //    interest[1] = (tmptime >> 16) & 0x000000ff;
    //    interest[2] = (tmptime >>  8) & 0x000000ff;
    //    interest[3] = (tmptime)       & 0x000000ff;
    //    interest[4] = _llpInterest.channel + 1;
    //
    //    if( crc8(interest, 5) == DSt->Message[0] )

    //  CRC calculation
    const CtiTime  periodOfInterest  = _llp_interest.time;
    const unsigned channelOfInterest = _llp_interest.channel;

    bytes interest_bytes;
    byte_appender interest_oitr = byte_appender(interest_bytes);

    const unsigned long tmptime = periodOfInterest.seconds();

    *interest_oitr++ = tmptime >> 24;
    *interest_oitr++ = tmptime >> 16;
    *interest_oitr++ = tmptime >>  8;
    *interest_oitr++ = tmptime >>  0;
    *interest_oitr++ = channelOfInterest;

    bytes result_bytes;
    byte_appender result_oitr = byte_appender(result_bytes);

    *result_oitr++ = mct410_utility::crc8(&interest_bytes.front(), interest_bytes.size());

    const CtiTime blockStart = periodOfInterest + offset * LoadProfile_IntervalsPerBlock * LoadProfile_Interval_seconds;

    if( _llp_interest.channel != 1 )
    {
        fill_n(result_oitr, 12, 0);

        return result_bytes;
    }

    fill_loadProfile(_address, blockStart, LoadProfile_Interval_seconds, result_oitr);

    return result_bytes;
}


bytes Mct410Sim::getLoadProfile(unsigned offset, unsigned channel)
{
    CtiTime now;

    bytes      result_bytes;
    byte_appender result_oitr = byte_appender(result_bytes);

    //  LP table pointer, see SSPEC-S01029 MCT-410.doc, 8.12.1, page 82
    *result_oitr++ = ((now.seconds() / (LoadProfile_Interval_seconds)) % 96) + 1;

    //  Only doing Channel 1 for now.
    if( channel != 1 )
    {
        fill_n(result_oitr, 12, 0x00);

        return result_bytes;
    }

    const long block_length = LoadProfile_IntervalsPerBlock * LoadProfile_Interval_seconds;

    const CtiTime previous_block_end = now - (now.seconds() % block_length);

    fill_loadProfile(_address, previous_block_end - LoadProfile_Interval_seconds, -LoadProfile_Interval_seconds, result_oitr);

    return result_bytes;
}


void Mct410Sim::fill_loadProfile(const unsigned address, const CtiTime &blockStart, const int interval_length, byte_appender &out_itr)
{
    for( unsigned interval = 0; interval < LoadProfile_IntervalsPerBlock; ++interval )
    {
        double value = makeValue_consumption(address, blockStart + interval * interval_length, interval_length);

        value /= SecondsPerHour;  //  convert from watt-seconds to watt-hours

        int dynamicDemand = mct410_utility::makeDynamicDemand(value);

        *out_itr++ = dynamicDemand >> 8;
        *out_itr++ = dynamicDemand >> 0;
    }
}

void Mct410Sim::putPointOfInterest(const bytes &payload)
{
    if( payload.size() < 6 )  return;

//  TODO-P4: Filter writes on SPID
    //  if( _spid != payload[0] )  return;

    _llp_interest.channel = payload[1];

    unsigned long tmp_time = 0;

    tmp_time |= payload[2] << 24;
    tmp_time |= payload[3] << 16;
    tmp_time |= payload[4] <<  8;
    tmp_time |= payload[5];

    _llp_interest.time = tmp_time;
}


void Mct410Sim::clearEventFlags()
{
    _memory_map[MM_EventFlags1] = 0x00;
    _memory_map[MM_MeterAlarms1] = 0x00;
}

}
}

