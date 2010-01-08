#include "yukon.h"
#include "Mct410.h"
#include "logger.h"
#include "dev_mct410.h"
#include "EmetconWords.h"

using namespace std;

namespace Cti {
namespace Simulator {

//  Temporary class to access protected functions in CtiDeviceMct410 and CtiDeviceMct4xx.
//  To be deleted when we move functions to a shared location.
struct mct410_utility : private CtiDeviceMCT410
{
    static int makeDynamicDemand(double d)
    {
        return CtiDeviceMCT410::makeDynamicDemand(d);
    };
    static unsigned char crc8(unsigned char *buf, unsigned int len)
    {
        return CtiDeviceMCT410::crc8(buf,len);
    };
};

const Mct410Sim::function_reads_t  Mct410Sim::_function_reads  = Mct410Sim::initFunctionReads();
const Mct410Sim::function_writes_t Mct410Sim::_function_writes = Mct410Sim::initFunctionWrites();

const double Mct410Sim::Pi = 4.0 * atan(1.0);

Mct410Sim::Mct410Sim(int address)
{
    //  _llp_interest should eventually be persisted and restored.
    //  We could access the DynamicPaoInfo table for this... ?
    // _llp_interest.time = CtiTime::now();

    _address = address;
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
    //reads[FR_AllCurrentPeakDemandReadings] = function_read_t(&Mct410Sim::getAllCurrentPeakDemandReadings);

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


//  TODO-P4: See PlcInfrastructure::oneWayCommand()
bool Mct410Sim::read(const words_t &request_words, words_t &response_words)
{
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

    response_words.push_back(word_t(new EmetconWordD1(b_word.repeater_variable,
                                                      b_word.dlc_address & ((1 << 14) - 1),  //  lowest 13 bits set
                                                      response_bytes[0],
                                                      response_bytes[1],
                                                      response_bytes[2],
                                                      0,
                                                      0)));

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
    else
    {
        copy(data.begin(),
             data.begin() + min(data.size(), _memory_map.size() - function),
             _memory_map.begin() + function);
    }

    return true;
}


bytes Mct410Sim::getZeroes()
{
    return bytes(13, 0x00);
}


bytes Mct410Sim::getAllCurrentMeterReadings()
{
    bytes data = bytes(13, 0x00);

    const unsigned now_seconds = CtiTime::now().seconds();

    //  ensure reads during the same minute will return the same value.
    const unsigned beginningOfThisMinute = now_seconds - (now_seconds % SecondsPerMinute);
    const unsigned beginningOfThisYear   = now_seconds - (now_seconds % SecondsPerYear);

    const double   consumption_Ws  = makeValue_consumption(_address, beginningOfThisYear, beginningOfThisMinute - beginningOfThisYear);
    const double   consumption_Wh  = consumption_Ws / SecondsPerHour;
    //  hecto-watt-hours - the 100 watt-hour units the MCT returns
    const unsigned consumption_hWh = consumption_Wh / 100.0;

    const unsigned char *byte_ptr = reinterpret_cast<const unsigned char *>(&consumption_hWh);

    //  copy out the three least-significant bytes in big-endian order
    reverse_copy(byte_ptr, byte_ptr + 3, data.begin());

    return data;
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

//  The consumption value is constructed using the current time and meter address.
double Mct410Sim::makeValue_consumption(const unsigned address, const CtiTime &c_time, const unsigned duration)
{
    if( duration == 0 )  return 0;

    const unsigned begin_seconds = c_time.seconds();
    const unsigned end_seconds   = c_time.seconds() + duration;

    const double year_period = 2.0 * Pi / static_cast<double>(SecondsPerYear);
    const double day_period  = 2.0 * Pi / static_cast<double>(SecondsPerDay);

    const double year_period_reciprocal = 1.0 / year_period;
    const double day_period_reciprocal  = 1.0 / day_period;

    const double amp_year = 1000.0;
    const double amp_day  =  500.0;

//  TODO-P3: move all value computation into policy classes
    return amp_year * (duration + year_period_reciprocal * (sin(begin_seconds * year_period) - sin(end_seconds * year_period))) +
           amp_day  * (duration + day_period_reciprocal  * (sin(begin_seconds * day_period)  - sin(end_seconds * day_period)));
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

    const double amp_day  =  500.0;
    const double amp_year = 1000.0;

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


}
}

