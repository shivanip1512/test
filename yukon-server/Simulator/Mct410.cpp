#include "precompiled.h"

#include "Mct410.h"
#include "ScopedLogger.h"
#include "dev_mct410.h"
#include "cparms.h"
#include "guard.h"

/* MctBehaviors */
#include "FrozenReadParityBehavior.h"
#include "FrozenPeakTimestampBehavior.h"
#include "RandomConsumptionBehavior.h"
#include "InvalidUsageReadingBehavior.h"

#include <boost/assign/list_of.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

const double   Mct410Sim::Pi              = 4.0 * atan(1.0);
const CtiTime  Mct410Sim::DawnOfTime      = CtiTime::CtiTime(0x41D63C60);  // Jan 1, 2005 CST
const double   Mct410Sim::alarmFlagChance = gConfigParms.getValueAsDouble("SIMULATOR_ALARM_FLAG_CHANCE_PERCENT");
const unsigned Mct410Sim::MemoryMapSize   = 284;

bool Mct410Sim::_behaviorsInited = false;

//  Temporary class to access protected functions in Mct410Device and Mct4xxDevice.
//  To be deleted when we move functions to a shared location.
struct mct4xx_utility : private Devices::Mct4xxDevice
{
    using Mct4xxDevice::crc8;
};

const Mct410Sim::function_reads_t  Mct410Sim::_function_reads     = Mct410Sim::initFunctionReads();
const Mct410Sim::function_writes_t Mct410Sim::_function_writes    = Mct410Sim::initFunctionWrites();
const Mct410Sim::data_reads_t      Mct410Sim::_data_reads         = Mct410Sim::initDataReads();
const Mct410Sim::data_writes_t     Mct410Sim::_data_writes        = Mct410Sim::initDataWrites();
const Mct410Sim::commands_t        Mct410Sim::_commands           = Mct410Sim::initCommands();

// The static Behavior Collection for all MCT devices to share.
BehaviorCollection<MctBehavior> Mct410Sim::_behaviorCollection;

Mct410Sim::Mct410Sim(int address) :
    _address(address),
    _memory(address, MemoryMapSize),
    _mct410tag("MCT410(" + CtiNumStr(address) + ")")
{
    if( !_memory.isInitialized() )
    {
        /**
         * The file we wanted to load from didn't exist. Load the memory
         * map with the default information.
         */

        _memory.writeValueToMemoryMap(MM_SspecHi, 0x04);
        _memory.writeValueToMemoryMap(MM_SspecLo, 0x05);
        _memory.writeValueToMemoryMap(MM_SspecRev, 33);  //  SSPEC supporting the hourly reads

        //  _llp_interest should eventually be persisted and restored.
        //  We could access the DynamicPaoInfo table for this... ?
        // _llp_interest.time = CtiTime::now();

        _memory.writeValueToMemoryMap(MM_LoadProfileInterval, 0x0f);

        // Memory map position 0x0A is the EventFlags-1 Alarm Mask. This needs to be initialized to 0x80 in order
        // to catch the tamper flag bit that may be set at memory map position 0x06 and set the general alarm bit.
        // Refer to section 4.10 of the MCT-410 SSPEC doc for more information.
        _memory.writeValueToMemoryMap(MM_EventFlags1AlarmMask, EF1_TamperFlag);

        // These should already be 0s, but we should explicitly say so just in case.
        _memory.writeValueToMemoryMap(MM_FreezeCounter, 0);
        _memory.writeValueToMemoryMap(MM_VoltageFreezeCounter, 0);

        // Default the Demand Interval to 15 minutes.
        _memory.writeValueToMemoryMap(MM_DemandInterval, 0x0f);

        // Default the Load Profile Interval to 15 minutes.
        _memory.writeValueToMemoryMap(MM_LoadProfileInterval, 0x0f);

        unsigned long lastFreezeSeconds = CtiTime(CtiDate() - 365).seconds();  //  last freeze 1 year ago

        const bytes lastFreezeBytes = {
                static_cast<unsigned char>(lastFreezeSeconds >> 24),
                static_cast<unsigned char>(lastFreezeSeconds >> 16),
                static_cast<unsigned char>(lastFreezeSeconds >>  8),
                static_cast<unsigned char>(lastFreezeSeconds      )};

        _memory.writeDataToMemoryMap(MM_LastFreezeTimestamp, lastFreezeBytes);

        const bytes displayParameters = {
            0x08,   // displayParams !displayDisabled, displayDigits(not supported), cycleTime = 8
            0x01,   // transformerRatio = 1
            0x08    // displayDigits
        };

        _memory.writeDataToMemoryMap(MM_DisplayParameters, displayParameters);
    }
}

Mct410Sim::function_reads_t Mct410Sim::makeFunctionReadRange(unsigned readMin, unsigned readMax, std::function<bytes(Mct410Sim&, unsigned)> fn)
{
    function_reads_t range;

    for( unsigned offset = 0; offset <= (readMax - readMin); ++offset )
    {
        range.emplace(readMin + offset, [=](Mct410Sim& mct) { return fn(mct, offset); });
    }

    return range;
}

Mct410Sim::function_reads_t Mct410Sim::initFunctionReads()
{
    function_reads_t reads;
    function_reads_t read_range;

    reads.emplace(FR_AllCurrentMeterReadings,      [](Mct410Sim& mct) { return mct.getAllCurrentMeterReadings();        });
    reads.emplace(FR_AllRecentDemandReadings,      [](Mct410Sim& mct) { return mct.getAllRecentDemandReadings();        });
    reads.emplace(FR_AllCurrentPeakDemandReadings, [](Mct410Sim& mct) { return mct.getAllCurrentPeakDemandReadings();   });
    reads.emplace(FR_GetFrozen_kWh,                [](Mct410Sim& mct) { return mct.getFrozenKwh();                      });
    reads.emplace(FR_AllFrozenChannel1Readings,    [](Mct410Sim& mct) { return mct.getAllFrozenChannel1Readings();      });
    reads.emplace(FR_AllCurrentVoltageReadings,    [](Mct410Sim& mct) { return mct.getAllCurrentVoltageReadings();      });
    reads.emplace(FR_FrozenMinMaxVoltageReadings,  [](Mct410Sim& mct) { return mct.getFrozenMinMaxVoltageReadings();    });
    reads.emplace(FR_DisplayParameters,            [](Mct410Sim& mct) { return mct.getDisplayParameters();              });
    reads.emplace(FR_LcdConfiguration1,            [](Mct410Sim& mct) { return mct.getLcdConfiguration1();              });
    reads.emplace(FR_LcdConfiguration2,            [](Mct410Sim& mct) { return mct.getLcdConfiguration2();              });
    reads.emplace(FR_DisconnectStatus,             [](Mct410Sim& mct) { return mct.getDisconnectStatus();               });

    read_range = makeFunctionReadRange(FR_Channel1SingleDayReadMin,
                                       FR_Channel1SingleDayReadMax, 
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getSingleDayRead(offset, 1); } );

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LongLoadProfileTableMin,
                                       FR_LongLoadProfileTableMax, 
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getLongLoadProfile(offset); } );

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel1Min,
                                       FR_LoadProfileChannel1Max,
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getLoadProfile(offset, 1); });

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel2Min,
                                       FR_LoadProfileChannel2Max,
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getLoadProfile(offset, 2); });

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel3Min,
                                       FR_LoadProfileChannel3Max,
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getLoadProfile(offset, 3); });

    reads.insert(read_range.begin(), read_range.end());

    read_range = makeFunctionReadRange(FR_LoadProfileChannel4Min,
                                       FR_LoadProfileChannel4Max,
                                       [](Mct410Sim& mct, unsigned offset) { return mct.getLoadProfile(offset, 4); });

    reads.insert(read_range.begin(), read_range.end());

    return reads;
}

Mct410Sim::function_writes_t Mct410Sim::initFunctionWrites()
{
    function_writes_t writes;

    writes[FW_Intervals]          = function_write_t(&Mct410Sim::putIntervals);
    writes[FW_PointOfInterest]    = function_write_t(&Mct410Sim::putPointOfInterest);
    writes[FW_ScheduledFreezeDay] = function_write_t(&Mct410Sim::putScheduledFreezeDay);
    writes[FW_DisplayParameters] = function_write_t(&Mct410Sim::putDisplayParameters);
    writes[FW_LcdConfiguration1] = function_write_t(&Mct410Sim::putLcdConfiguration1);
    writes[FW_LcdConfiguration2] = function_write_t(&Mct410Sim::putLcdConfiguration2);

    return writes;
}

Mct410Sim::data_reads_t Mct410Sim::initDataReads()
{
    data_reads_t reads;

    reads[DR_GetFreezeInfo]      = data_read_t(&Mct410Sim::getFreezeInfo);
    reads[DR_ScheduledFreezeDay] = data_read_t(&Mct410Sim::getScheduledFreezeDay);

    return reads;
}

Mct410Sim::data_writes_t Mct410Sim::initDataWrites()
{
    data_writes_t writes;

    return writes;
}

Mct410Sim::commands_t Mct410Sim::initCommands()
{
    return {
        { C_Connect,                    [](Mct410Sim &mct) { mct.connect();         } },
        { C_Disconnect,                 [](Mct410Sim &mct) { mct.disconnect();      } },
        { C_ClearAllEventFlags,         [](Mct410Sim &mct) { mct.clearEventFlags(); } },
        { C_PutFreezeOne,               [](Mct410Sim &mct) { mct.putFreeze(1);      } },
        { C_PutFreezeTwo,               [](Mct410Sim &mct) { mct.putFreeze(2);      } },
        { C_SetLpIntervalFiveMin,       [](Mct410Sim &mct) { mct.setLpInterval(5);  } },
        { C_SetLpIntervalFifteenMin,    [](Mct410Sim &mct) { mct.setLpInterval(15); } },
        { C_SetLpIntervalThirtyMin,     [](Mct410Sim &mct) { mct.setLpInterval(30); } },
        { C_SetLpIntervalSixtyMin,      [](Mct410Sim &mct) { mct.setLpInterval(60); } },
    };
}

void Mct410Sim::initBehaviors(Logger &logger)
{
    // We only want this called once or behaviors will get out of hand.
    if( !_behaviorsInited )
    {
        if( double parityChance = gConfigParms.getValueAsDouble("SIMULATOR_INVALID_FROZEN_READ_PARITY_PROBABILITY") )
        {
            logger.log("Frozen Read Parity Behavior Enabled - Probability: " + CtiNumStr(parityChance, 2) + "%");

            _behaviorCollection.push_back(
                    std::make_unique<FrozenReadParityBehavior>(
                            parityChance));
        }
        if( double timestampChance = gConfigParms.getValueAsDouble("SIMULATOR_INVALID_FROZEN_PEAK_TIMESTAMP_PROBABILITY") )
        {
            logger.log("Frozen Peak Timestamp Behavior Enabled - Probability: " + CtiNumStr(timestampChance, 2) + "%");

            _behaviorCollection.push_back(
                    std::make_unique<FrozenPeakTimestampBehavior>(
                            timestampChance));
        }
        if( double randomReadingChance = gConfigParms.getValueAsDouble("SIMULATOR_RANDOM_READING_CHANCE_PERCENT") )
        {
            logger.log("Random Consimption Behavior Enabled - Probability: " + CtiNumStr(randomReadingChance, 2) + "%");

            _behaviorCollection.push_back(
                    std::make_unique<RandomConsumptionBehavior>(
                            randomReadingChance));
        }
        if( double invalidUsageReadingChance = gConfigParms.getValueAsDouble("SIMULATOR_INVALID_USAGE_READING_CHANCE_PERCENT") )
        {
            logger.log("Invalid usage reading behavior enabled - Probability: " + CtiNumStr(invalidUsageReadingChance, 2) + "%");

            _behaviorCollection.push_back(
                    std::make_unique<InvalidUsageReadingBehavior>(
                            invalidUsageReadingChance));
        }

        _behaviorsInited = true;
    }
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
    processFlags(scope);

    const bytes response_bytes = processRead(b_word.function, b_word.function_code, scope);

    if( response_bytes.size() < EmetconWordD1::PayloadLength +
                                EmetconWordD2::PayloadLength +
                                EmetconWordD3::PayloadLength )
    {
        return false;
    }

    switch( b_word.words_to_follow )
    {
        case 3:
        {
            response_words.insert(response_words.begin(),
                                  word_t(new EmetconWordD3(response_bytes[8],
                                                           response_bytes[9],
                                                           response_bytes[10],
                                                           response_bytes[11],
                                                           response_bytes[12],
                                                           0,
                                                           0)));
        }
        case 2:
        {
            response_words.insert(response_words.begin(),
                                  word_t(new EmetconWordD2(response_bytes[3],
                                                           response_bytes[4],
                                                           response_bytes[5],
                                                           response_bytes[6],
                                                           response_bytes[7],
                                                           0,
                                                           0)));
        }
        case 1:
        {
            unsigned char eventFlags = _memory.getValueFromMemoryMapLocation(MM_EventFlags1);
            unsigned char eventFlagsMask = _memory.getValueFromMemoryMapLocation(MM_EventFlags1AlarmMask);
            bool alarm = eventFlags & eventFlagsMask;

            response_words.insert(response_words.begin(),
                                  word_t(new EmetconWordD1(b_word.repeater_variable,
                                                           b_word.dlc_address & ((1 << 14) - 1),  //  lowest 13 bits set
                                                           response_bytes[0],
                                                           response_bytes[1],
                                                           response_bytes[2],
                                                           0,
                                                           alarm)));
        }
        case 0:
        {
            return true;
        }
        default:
        {
            scope.breadcrumbLog("***** Words to follow set to invalid value: " + CtiNumStr(b_word.words_to_follow) + " *****");
            return false; // Right?
        }
    }
}

auto byte_to_string = [](unsigned char i) { 
    stringstream s;
    s << setfill('0') << setw(2) << hex << (unsigned)i;
    return s.str();
};

bytes Mct410Sim::processRead(bool function_read, unsigned function, Logger &logger)
{
    bytes read_bytes = bytes(ReadLength, '\0');

    cout << "processRead(" << function_read << ", 0x" << hex << function << ")" << endl;

    if( function_read )
    {
        if( auto functionRead = mapFind(_function_reads, function) )
        {
            bytes function_read_bytes = (*functionRead)(*this);

            copy(function_read_bytes.begin(),
                 function_read_bytes.begin() + min(function_read_bytes.size(), (bytes::size_type)ReadLength),
                 read_bytes.begin());
        }
    }
    else
    {
        // Data Read
        if( auto dataRead = mapFind(_data_reads, function) )
        {
            bytes data_read_bytes = (*dataRead)(*this);

            copy(data_read_bytes.begin(),
                 data_read_bytes.begin() + min(data_read_bytes.size(), (bytes::size_type)ReadLength),
                 read_bytes.begin());
        }
        else
        {
            // This data read isn't supported yet. Grab from memory.
            read_bytes = _memory.getValueVectorFromMemoryMap(function, ReadLength);
        }
    }

    cout << join(read_bytes | boost::adaptors::transformed(byte_to_string), " ") << endl;

    MctMessageContext context = { read_bytes, function, function_read };

    _behaviorCollection.processMessage(context, logger);

    // Return the processed message.
    return context.data;
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
    cout << "processWrite(" << function_write << ", 0x" << hex << function << ")" << endl;
    cout << join(data | boost::adaptors::transformed(byte_to_string), " ") << endl;

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
            cmd_itr->second(*this);
        }
    }
    else if( !function_write && (data.size() > 0) )
    {
        _memory.writeDataToMemoryMap(function, data);
    }

    return true;
}

void Mct410Sim::processFlags(Logger &logger)
{
    double dist = (rand() / double(RAND_MAX + 1)) * 100;
    if( dist < alarmFlagChance )
    {
        // 50/50 chance that the reverse-power flag is set or the zero usage flag is set.
        double value = rand() / double(RAND_MAX + 1);

        if( value < 0.50 )
        {
            // Only set the zero usage flag to true if it isn't already set...
            unsigned char flag = _memory.getValueFromMemoryMapLocation(MM_EventFlags2);
            if ( (flag & EF2_ZeroUsage) == 0 )
            {
                flag |= EF2_ZeroUsage;
                _memory.writeValueToMemoryMap(MM_EventFlags2, flag);
                logger.breadcrumbLog("******** Zero-Usage flag set! ********");
            }
        }
        else // If value >= 0.50
        {
            // Only set the reverse-power flag to true if it isn't already set...
            unsigned char flag = _memory.getValueFromMemoryMapLocation(MM_MeterAlarms1);
            if( (flag & MA1_ReversePower) == 0 )
            {
                flag |= MA1_ReversePower;
                _memory.writeValueToMemoryMap(MM_MeterAlarms1, flag);
                logger.breadcrumbLog("******** Reverse-power flag set!********");
            }
        }
    }

    // Tamper flag (bit 7 of address 0x06) gets set if reverse-power or zero usage bits are set.
    unsigned char eventFlags  = _memory.getValueFromMemoryMapLocation(MM_EventFlags2);
    unsigned char meterAlarms = _memory.getValueFromMemoryMapLocation(MM_MeterAlarms1);
    if( ((eventFlags & EF2_ZeroUsage) != 0) || ((meterAlarms & MA1_ReversePower) != 0) )
    {
        // Only set tamper flag if it isn't already set.
        if ( (eventFlags & EF1_TamperFlag) == 0 )
        {
            eventFlags |= EF1_TamperFlag;
            _memory.writeValueToMemoryMap(MM_EventFlags1, eventFlags);
            logger.breadcrumbLog("******** Tamper flag set! ********");
        }
    }
}

bytes Mct410Sim::getFreezeInfo()
{
    bytes data;

    int last_freeze = _memory.getValueFromMemoryMapLocation(MM_LastFreezeTimestamp, MML_LastFreezeTimestamp);
    int last_voltage_freeze = _memory.getValueFromMemoryMapLocation(MM_LastVoltageFreezeTimestamp, MML_LastVoltageFreezeTimestamp);

    // Last Freeze Timestamp
    data.push_back(last_freeze >> 24);
    data.push_back(last_freeze >> 16);
    data.push_back(last_freeze >>  8);
    data.push_back(last_freeze);

    // Freeze Counter
    data.push_back(_memory.getValueFromMemoryMapLocation(MM_FreezeCounter));

    // Last Voltage Freeze Timestamp
    data.push_back(last_voltage_freeze >> 24);
    data.push_back(last_voltage_freeze >> 16);
    data.push_back(last_voltage_freeze >>  8);
    data.push_back(last_voltage_freeze);

    // Voltage Freeze Counter
    data.push_back(_memory.getValueFromMemoryMapLocation(MM_VoltageFreezeCounter));

    return data;
}

bytes Mct410Sim::getScheduledFreezeDay()
{
    return bytes(1, _memory.getValueFromMemoryMapLocation(MM_ScheduledFreezeDay));
}

/**
 * From MCT410 SSPEC:
 *
 * MCT-410: This command will perform a Freeze on the Current
 * Meter Reading and Peak Demand for Channel 1, 2, and 3.  It
 * will also freeze the TOU Data.
 *
 * All of this will only take place if the Freeze Counter
 * correlates correctly to the last freeze that was received.
 */
void Mct410Sim::putFreeze(unsigned incoming_freeze)
{
    int freeze_counter = _memory.getValueFromMemoryMapLocation(MM_FreezeCounter);
    if( ((freeze_counter % 2) && (incoming_freeze == 2)) || (!(freeze_counter % 2) && (incoming_freeze == 1)) )
    {
        // The last freeze occurred now. Write it!
        {
            bytes data;

            int last_freeze_timestamp = CtiTime::now().seconds();

            data.push_back(last_freeze_timestamp >> 24);
            data.push_back(last_freeze_timestamp >> 16);
            data.push_back(last_freeze_timestamp >> 8);
            data.push_back(last_freeze_timestamp);

            _memory.writeDataToMemoryMap(MM_LastFreezeTimestamp, data);
        }

        // Increment and record the freeze counter.
        {
            freeze_counter = (freeze_counter + 1) % 0x100;

            _memory.writeValueToMemoryMap(MM_FreezeCounter, freeze_counter);
        }

        // Freeze the current peak demand and record it, then reset peak demand to 0.
        {
            bytes data, zeroes(MML_CurrentPeakDemand1, 0x00);

            short int peakDemand = _memory.getValueFromMemoryMapLocation(MM_CurrentPeakDemand1, MML_CurrentPeakDemand1);

            data.push_back(peakDemand >> 8);
            data.push_back(peakDemand);

            _memory.writeDataToMemoryMap(MM_FrozenPeakDemand1, data);
            _memory.writeDataToMemoryMap(MM_CurrentPeakDemand1, zeroes);
        }

        // Write the current peak demand timestamp to Frozen Peak Demand timestamp
        {
            bytes currentPeakDemandTimestamp = _memory.getValueVectorFromMemoryMap(MM_CurrentPeakDemand1Timestamp,
                                                                                   MML_CurrentPeakDemand1Timestamp);

            _memory.writeDataToMemoryMap(MM_FrozenPeakDemand1Timestamp, currentPeakDemandTimestamp);
        }

        // Reset the current peak demand timestamp to 0.
        {
            bytes data(MML_CurrentPeakDemand1Timestamp, 0x00);

            _memory.writeDataToMemoryMap(MM_CurrentPeakDemand1Timestamp, data);
        }

        // Freeze the current kWh value.
        {
            bytes data;

            unsigned hwh = getHectoWattHours(_address, CtiTime());

            data.push_back(hwh >> 16);
            data.push_back(hwh >> 8);
            data.push_back(hwh);

            /**
             * We need to set the parity of the frozen kWh value to match
             * the freeze command that froze the kWh.
             *
             * The least significant bit of the Frozen Meter reading should
             * be set to 0 if this command was a freeze one or 1 if this
             * command was a freeze two as per section 4.63 of the MCT 410
             * SSPEC-S01029.
             */
            if( incoming_freeze == 1 )
            {
                data.back() &= 0xfe;
            }
            else
            {
                data.back() |= 0x01;
            }

            _memory.writeDataToMemoryMap(MM_FrozenMeterReading1, data);
        }
    }
}

/**
 * From MCT-410 SSPEC:
 *
 * 4.22 Load Profile Interval
 *
 * Interval, in minute increments, over which standard load
 * profile values are kept for.  Valid values are 5, 15, 30 and
 * 60. This value should not be written directly but rather is
 * set by commands 0x70 - 0x73.
 */
void Mct410Sim::setLpInterval(unsigned interval_minutes)
{
    switch( interval_minutes )
    {
        case 5:
        case 15:
        case 30:
        case 60:
        {
            _memory.writeValueToMemoryMap(MM_LoadProfileInterval, interval_minutes);
        }
    }
}

void Mct410Sim::setDemandInterval(unsigned interval_minutes)
{
    if( interval_minutes <= 60 )
    {
        _memory.writeValueToMemoryMap(MM_DemandInterval, interval_minutes);
    }
}

void Mct410Sim::setVoltageDemandInterval(unsigned interval_minutes)
{
    if( interval_minutes <= 255 )
    {
        _memory.writeValueToMemoryMap(MM_VoltageDemandInterval, interval_minutes);
    }
}

void Mct410Sim::setVoltageLpInterval(unsigned interval_minutes)
{
    switch( interval_minutes )
    {
        case 5:
        case 15:
        case 30:
        case 60:
        {
            _memory.writeValueToMemoryMap(MM_VoltageLoadProfileInterval, interval_minutes);
        }
    }
}

unsigned Mct410Sim::getLpIntervalSeconds()
{
    return (SecondsPerMinute * _memory.getValueFromMemoryMapLocation(MM_LoadProfileInterval));
}

void Mct410Sim::putScheduledFreezeDay(const bytes &data)
{
    if( !data.empty() )
    {
        _memory.writeValueToMemoryMap(MM_ScheduledFreezeDay, data[0]);
    }
}

void Mct410Sim::putDisplayParameters(const bytes &data)
{
    if( !data.empty() )
    {
        unsigned char newbyte=data[0] & 0x7f;
        _memory.writeValueToMemoryMap(MM_DisplayParameters, newbyte);
        _memory.writeValueToMemoryMap(MM_DisplayParameters+1, data[1]);
        _memory.writeValueToMemoryMap(MM_DisplayParameters+2, data[2]);
    }
}

void Mct410Sim::putLcdConfiguration1(const bytes &data)
{
    if( !data.empty() )
    {
        _memory.writeDataToMemoryMap(MM_LcdConfiguration1, data);
    }
}

void Mct410Sim::putLcdConfiguration2(const bytes &data)
{
    if( !data.empty() )
    {
        _memory.writeDataToMemoryMap(MM_LcdConfiguration2, data);
    }
}

void Mct410Sim::putIntervals(const bytes &data)
{
    // This should always be 4 bytes of data.
    if( data.size() == 4 )
    {
        setDemandInterval(data[0]);
        setLpInterval(data[1]);
        setVoltageDemandInterval(data[2]);
        setVoltageLpInterval(data[3]);
    }
    else
    {
        _memory.writeDataToMemoryMap(MM_DemandInterval, data);
    }
}

bytes Mct410Sim::getFrozenKwh()
{
    const bytes frozenData = _memory.getValueVectorFromMemoryMap(MM_FrozenMeterReading1, MML_FrozenMeterReading1);
    unsigned freezeCounter = _memory.getValueFromMemoryMapLocation(MM_FreezeCounter);

    return formatFrozenKwh(frozenData, freezeCounter);
}

/**
 * Precondition: frozenData must be exactly three bytes of data.
 */
bytes Mct410Sim::formatFrozenKwh(const bytes &frozenData, unsigned freezeCounter)
{
    bytes data;

    // Bytes 0-2:  Frozen Meter Read 1
    data.insert(data.begin(), frozenData.begin(), frozenData.end());

    // Byte 3: Freeze Counter
    data.insert(data.end(), freezeCounter);

    // Bytes 4-6:  Frozen Meter Read 2
    // Bytes 7-9:  Frozen Meter Read 3
    // Bytes 10-12: No Data
    data.insert(data.end(), 9, 0x00);

    return data;
}

bytes Mct410Sim::getAllFrozenChannel1Readings()
{
    const short peakDemand = _memory.getValueFromMemoryMapLocation(MM_FrozenPeakDemand1,
                                                                   MML_FrozenPeakDemand1);
    const unsigned long frozenTime = _memory.getValueFromMemoryMapLocation(MM_FrozenPeakDemand1Timestamp,
                                                                           MML_FrozenPeakDemandTimestamp);
    const unsigned long frozenRead = _memory.getValueFromMemoryMapLocation(MM_FrozenMeterReading1,
                                                                           MML_FrozenMeterReading1);

    const unsigned freezeCounter = _memory.getValueFromMemoryMapLocation(MM_FreezeCounter);

    const unsigned hwh = getHectoWattHours(_address, CtiTime::now());

    return formatAllFrozenChannel1Readings(frozenRead, frozenTime, hwh, freezeCounter, peakDemand);
}

bytes Mct410Sim::formatAllFrozenChannel1Readings(const unsigned long frozenRead, const unsigned long frozenTime,
                                                 const unsigned hWh, const unsigned freezeCounter, const short peakDemand )
{
    bytes data;

    // Bytes 0-1: Frozen Peak Demand #1
    data.push_back(peakDemand >> 8);
    data.push_back(peakDemand);

    // Bytes 2-5: Frozen Time Of Peak #1
    data.push_back(frozenTime >> 24);
    data.push_back(frozenTime >> 16);
    data.push_back(frozenTime >> 8);
    data.push_back(frozenTime);

    // Bytes 6-8: Frozen Meter Read #1
    data.push_back(frozenRead >> 16);
    data.push_back(frozenRead >> 8);
    data.push_back(frozenRead);

    // Byte 9: Freeze Counter
    data.push_back(freezeCounter);

    // Bytes 10-12: Current Meter Read #1
    data.push_back(hWh >> 16);
    data.push_back(hWh >> 8);
    data.push_back(hWh);

    return data;
}

bytes Mct410Sim::getAllCurrentPeakDemandReadings()
{
    bytes peakDemand = _memory.getValueVectorFromMemoryMap(MM_CurrentPeakDemand1, MML_CurrentPeakDemand1);
    bytes peakTimestamp = _memory.getValueVectorFromMemoryMap(MM_CurrentPeakDemand1Timestamp, MML_CurrentPeakDemand1Timestamp);

    return formatAllCurrentPeakDemandReadings(_address, peakDemand, peakTimestamp);
}

bytes Mct410Sim::formatAllCurrentPeakDemandReadings(const unsigned address, const bytes &peakDemand, const bytes &peakTimestamp)
{
    bytes data, consumption, zeroes(4, 0x00);

    CtiTime now;

    //  ensure reads during the same minute will return the same value.
    now -= now.second();

    const unsigned consumption_hWh = getHectoWattHours(address, now);

    consumption.push_back(consumption_hWh >> 16);
    consumption.push_back(consumption_hWh >> 8);
    consumption.push_back(consumption_hWh);

    /**
     * Bytes 0-1: Peak Demand
     * Bytes 2-5: Peak Demand Timestamp
     * Bytes 6-8: Current Meter Read
     * Bytes 9-12: No Data
     */
    data.insert(data.begin(), peakDemand.begin(), peakDemand.end());
    data.insert(data.end(), peakTimestamp.begin(), peakTimestamp.end());
    data.insert(data.end(), consumption.begin(), consumption.end());
    data.insert(data.end(), zeroes.begin(), zeroes.end());

    return data;
}

bytes Mct410Sim::getAllCurrentMeterReadings()
{
    return formatAllCurrentMeterReadings(_address);
}

bytes Mct410Sim::formatAllCurrentMeterReadings(const unsigned address)
{
    bytes data(13, 0x00);

    CtiTime now;

    //  ensure reads during the same minute will return the same value.
    now -= now.second();

    const unsigned   consumption_hWh  = getHectoWattHours(address, now);

    const unsigned char *byte_ptr = reinterpret_cast<const unsigned char *>(&consumption_hWh);

    //  copy out the three least-significant bytes in big-endian order
    reverse_copy(byte_ptr, byte_ptr + 3, data.begin());

    return data;
}

unsigned Mct410Sim::getHectoWattHours(const unsigned address, const CtiTime now )
{
    const unsigned duration = now.seconds() - DawnOfTime.seconds();
    const double   consumption_Ws  = makeValueConsumption(address, DawnOfTime, duration);
    const double   consumption_Wh  = consumption_Ws / SecondsPerHour;
    //  hecto-watt-hours - the 100 watt-hour units the MCT returns

    /*  Introduced an offset value to be added to the value of the curve
        based  on the value of the address passed in to the function. This
        offset allows for a range of 0-7350 to be added to the kWh reading
        that is returned to the device, thus providing a better way of
        distinguishing meter readings whose addresses are similar or near
        each other in value. Meters whose addresses are within one of each
        other will now have a 7.35 kWh difference between them for easier
        distinguishing.                                                     */
    const double offset = (address % 1000) * 7350;

    long long reading = consumption_Wh + (offset * getConsumptionMultiplier(address));

    // Mod the hWh by 10000000 to reduce the range from 0 to 9999999,
    // since the MCT Device reads hWh this corresponds to 999,999.9 kWh
    // which is the desired changeover point.
    return (reading / 100) % 10000000;
}

bytes Mct410Sim::getAllRecentDemandReadings()
{
    const unsigned nowSeconds = CtiTime::now().seconds();

    const short peakDemand = _memory.getValueFromMemoryMapLocation(MM_CurrentPeakDemand1, MML_CurrentPeakDemand1);
    const unsigned demandInterval = _memory.getValueFromMemoryMapLocation(MM_DemandInterval) * SecondsPerMinute;

    int dynamicDemand = getDynamicDemand(_address, demandInterval, nowSeconds);

    // Check if we have a new peak demand...
    const unsigned lastFreezeTimestamp = _memory.getValueFromMemoryMapLocation(MM_LastFreezeTimestamp, MML_LastFreezeTimestamp);

    peak_demand_t peakDemandSinceFreeze = checkForNewPeakDemand(_address, demandInterval, lastFreezeTimestamp, CtiTime::now());

    if( peakDemandSinceFreeze.peakDemand > peakDemand )
    {
        // New peak demand value and timestamp
        writeNewPeakDemand(peakDemandSinceFreeze.peakDemand, peakDemandSinceFreeze.peakTimestamp);
    }

    return formatAllRecentDemandReadings(dynamicDemand);
}

Mct410Sim::peak_demand_t Mct410Sim::checkForNewPeakDemand(const unsigned address, const unsigned demandInterval,
                                               const unsigned lastFreezeTimestamp, const CtiTime c_time)
{
    double maxIntervalConsumption = 0;
    unsigned maxIntervalTimestamp = 0;

    // Align it to its following interval.
    unsigned firstIntervalStart = lastFreezeTimestamp - (lastFreezeTimestamp % demandInterval) + demandInterval;

    CtiTime intervalBegin(firstIntervalStart);

    for( ; (intervalBegin.seconds() + demandInterval) < c_time.seconds() ; intervalBegin.addSeconds(demandInterval) )
    {
        // Calculate the consumption from the start point of the interval.
        double intervalConsumption = makeValueConsumption(address, intervalBegin, demandInterval);

        if( intervalConsumption > maxIntervalConsumption )
        {
            maxIntervalConsumption = intervalConsumption;

            maxIntervalTimestamp = intervalBegin.seconds();
        }
    }

    unsigned short peakDemand = getDynamicDemand(address, demandInterval, maxIntervalTimestamp);

    // Timestamp for the peak is the end of the interval.
    peak_demand_t result = { peakDemand, maxIntervalTimestamp + demandInterval };

    return result;
}

bytes Mct410Sim::formatAllRecentDemandReadings(const int dynamicDemand)
{
    bytes data(13, 0x00);

    const unsigned char *demand_ptr = reinterpret_cast<const unsigned char *>(&dynamicDemand);

    //  copy out the two least-significant bytes in big-endian order
    reverse_copy(demand_ptr, demand_ptr + 2, data.begin());

    //  TODO-P2: recent voltage, outages, ch2/3 demand

    return data;
}

int Mct410Sim::getDynamicDemand(const unsigned address, const unsigned demandIntervalSeconds, unsigned nowSeconds)
{
    const unsigned beginningOfLastInterval = nowSeconds - (nowSeconds % demandIntervalSeconds) - demandIntervalSeconds;

    const double demand_hwh_begin = getHectoWattHours(address, beginningOfLastInterval);
    const double demand_hwh_end   = getHectoWattHours(address, beginningOfLastInterval + demandIntervalSeconds);

    const double diffWh = (demand_hwh_end - demand_hwh_begin) * 100.0;

    return Devices::Mct410Device::Utility::makeDynamicDemand(diffWh);
}

void Mct410Sim::writeNewPeakDemand(const int dynamicDemand, const unsigned seconds)
{
    bytes demandData, demandTimestamp;

    demandData.push_back(dynamicDemand >> 8);
    demandData.push_back(dynamicDemand);

    demandTimestamp.push_back(seconds >> 24);
    demandTimestamp.push_back(seconds >> 16);
    demandTimestamp.push_back(seconds >>  8);
    demandTimestamp.push_back(seconds);

    _memory.writeDataToMemoryMap(MM_CurrentPeakDemand1, demandData);
    _memory.writeDataToMemoryMap(MM_CurrentPeakDemand1Timestamp, demandTimestamp);
}

bytes Mct410Sim::getAllCurrentVoltageReadings()
{
    bytes result;

    {
        auto reading = 1234;

        result.push_back(reading >> 8);
        result.push_back(reading);
    }
    {
        auto sec = CtiTime{ CtiDate{ 1, 2, 2015 }, 12, 34, 56 }.seconds();

        result.push_back(sec >> 24);
        result.push_back(sec >> 16);
        result.push_back(sec >> 8);
        result.push_back(sec);
    }

    {
        auto reading = 1166;

        result.push_back(reading >> 8);
        result.push_back(reading);
    }
    {
        auto sec = CtiTime{ CtiDate{ 1, 2, 2015 }, 1, 23, 45 }.seconds();

        result.push_back(sec >> 24);
        result.push_back(sec >> 16);
        result.push_back(sec >> 8);
        result.push_back(sec);
    }

    return result;
}

bytes Mct410Sim::getFrozenMinMaxVoltageReadings()
{
    bytes result;

    {
        auto reading = 1244;

        result.push_back(reading >> 8);
        result.push_back(reading);
    }
    {
        auto sec = CtiTime{ CtiDate{ 31, 1, 2015 }, 12, 34, 56 }.seconds();

        result.push_back(sec >> 24);
        result.push_back(sec >> 16);
        result.push_back(sec >> 8);
        result.push_back(sec);
    }

    {
        auto reading = 1156;

        result.push_back(reading >> 8);
        result.push_back(reading);
    }
    {
        auto sec = CtiTime{ CtiDate{ 31, 1, 2015 }, 1, 23, 45 }.seconds();

        result.push_back(sec >> 24);
        result.push_back(sec >> 16);
        result.push_back(sec >> 8);
        result.push_back(sec);
    }

    return result;
}

bytes Mct410Sim::getDisplayParameters()
{
    return _memory.getValueVectorFromMemoryMap(MM_DisplayParameters, MML_DisplayParameters);
}

bytes Mct410Sim::getLcdConfiguration1()
{
    return _memory.getValueVectorFromMemoryMap(MM_LcdConfiguration1, MML_LcdConfiguration1);
}

bytes Mct410Sim::getLcdConfiguration2()
{
    return _memory.getValueVectorFromMemoryMap(MM_LcdConfiguration2, MML_LcdConfiguration2);
}

bytes Mct410Sim::getDisconnectStatus()
{
    return _memory.getValueVectorFromMemoryMap(MM_Status2Flags, 1);
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
    else                                    //  drastically more than other households do.
    {
        return 20.0;
    }
}

//  The consumption value is constructed using the specified time and meter address.
double Mct410Sim::makeValueConsumption(const unsigned address, const CtiTime consumptionTime, const unsigned duration)
{
    if( duration == 0 )  return 0;

    const double consumption_multiplier = getConsumptionMultiplier(address);

    const unsigned begin_seconds = consumptionTime.seconds();
    const unsigned end_seconds   = consumptionTime.seconds() + duration;

    const double year_period = 2.0 * Pi / static_cast<double>(SecondsPerYear);
    const double day_period  = 2.0 * Pi / static_cast<double>(SecondsPerDay);

    const double year_period_reciprocal = 1.0 / year_period;
    const double day_period_reciprocal  = 1.0 / day_period;

    /*  These multipliers affect the amplification of the curve for the
        consumption. amp_year being set at 700.0 gives a normalized
        curve for the meter which results in a 1x Consumption Multiplier
        calculating to about 775-875 kWh per month, as desired.             */
    const double amp_year =  700.0;
    const double amp_day  =  500.0;

//  TODO-P3: move all value computation into policy classes
    return (amp_year * (duration + year_period_reciprocal * (sin(begin_seconds * year_period) - sin(end_seconds * year_period))) +
            amp_day  * (duration + day_period_reciprocal  * (sin(begin_seconds * day_period)  - sin(end_seconds * day_period)))) * consumption_multiplier;
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

    return makeValueConsumption(address, begin_time, duration) / duration;
}

bytes Mct410Sim::getLongLoadProfile(const unsigned offset)
{
    unsigned lpIntervalSeconds = getLpIntervalSeconds();

    return formatLongLoadProfile(offset, _address, _llp_interest.c_time, _llp_interest.channel, lpIntervalSeconds);
}

bytes Mct410Sim::formatLongLoadProfile(const unsigned offset, const unsigned address, const CtiTime periodOfInterest,
                                       const unsigned channelOfInterest, const unsigned lpIntervalSeconds )
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

    *result_oitr++ = mct4xx_utility::crc8(&interest_bytes.front(), interest_bytes.size());

    const CtiTime blockStart = periodOfInterest + offset * LoadProfile_IntervalsPerBlock * lpIntervalSeconds;

    if( channelOfInterest != 1 )
    {
        fill_n(result_oitr, 12, 0);

        return result_bytes;
    }

    fillLongLoadProfile(address, blockStart, lpIntervalSeconds, result_oitr);

    return result_bytes;
}

bytes Mct410Sim::getSingleDayRead(const unsigned offset, const unsigned channel)
{
    const unsigned demandInterval = _memory.getValueFromMemoryMapLocation(MM_DemandInterval) * SecondsPerMinute;

    unsigned lpIntervalSeconds = getLpIntervalSeconds();

    double maxIntervalConsumption = 0;
    CtiTime maxIntervalTime;

    CtiDate readDay = CtiDate::now() - 1 - offset;

    const auto hectoWattHours = getHectoWattHours(_address, CtiDate::now() - offset);

    const auto dayBegin = CtiTime(readDay) + demandInterval;
    const auto dayEnd   = CtiTime(readDay + 1);

    for( CtiTime intervalBegin = dayBegin; intervalBegin < dayEnd; intervalBegin.addSeconds(demandInterval) )
    {
        // Calculate the consumption from the start point of the interval.
        double intervalConsumption = makeValueConsumption(_address, intervalBegin, demandInterval);

        if( intervalConsumption > maxIntervalConsumption )
        {
            maxIntervalConsumption = intervalConsumption;

            maxIntervalTime = intervalBegin;
        }
    }

    unsigned short peakDemand = getDynamicDemand(_address, demandInterval, maxIntervalTime.seconds());
    unsigned short peakTimestampMinutes = (maxIntervalTime.seconds() - dayBegin.seconds() + demandInterval) / SecondsPerMinute;

    // Timestamp for the peak is the end of the interval.
    peak_demand_t peakDemandDaily = { peakDemand, peakTimestampMinutes };

    return formatSingleDayRead(hectoWattHours, peakDemandDaily, 17, readDay.dayOfMonth(), readDay.month(), channel);
}

bytes Mct410Sim::formatSingleDayRead(const unsigned hectoWattHours, const peak_demand_t peakDemandDaily, const unsigned outages, const unsigned day, const unsigned month, const unsigned channel)
{
    bytes singleDayRead;

    singleDayRead.push_back(hectoWattHours >> 16);
    singleDayRead.push_back(hectoWattHours >> 8);
    singleDayRead.push_back(hectoWattHours);

    singleDayRead.push_back(peakDemandDaily.peakDemand >> 8);
    singleDayRead.push_back(peakDemandDaily.peakDemand);

    singleDayRead.push_back(peakDemandDaily.peakTimestamp >> 8);
    singleDayRead.push_back(peakDemandDaily.peakTimestamp);

    singleDayRead.push_back(outages >> 8);
    singleDayRead.push_back(outages);

    singleDayRead.push_back(day);

    singleDayRead.push_back(((channel - 1) << 4) | (month - 1));

    return singleDayRead;
}

bytes Mct410Sim::getLoadProfile(const unsigned offset, const unsigned channel)
{
    CtiTime readTime = CtiTime::now();
    unsigned lpIntervalSeconds = getLpIntervalSeconds();

    return formatLoadProfile(offset, channel, _address, readTime, lpIntervalSeconds);
}

bytes Mct410Sim::formatLoadProfile(const unsigned offset, const unsigned channel, const unsigned address,
                                   const CtiTime readTime, const unsigned lpIntervalSeconds)
{
    bytes result_bytes;
    byte_appender result_oitr = byte_appender(result_bytes);

    *result_oitr++ = getTablePointer(readTime, lpIntervalSeconds);

    //  Only doing Channel 1 for now.
    if( channel != 1 )
    {
        fill_n(result_oitr, 12, 0x00);

        return result_bytes;
    }

    const long block_length = LoadProfile_IntervalsPerBlock * lpIntervalSeconds;

    const CtiTime previous_block_end = readTime - (readTime.seconds() % block_length);

    fillLoadProfile(address, previous_block_end, lpIntervalSeconds, result_oitr);

    return result_bytes;
}

void Mct410Sim::fillLoadProfile(const unsigned address, const CtiTime &blockStart, const unsigned interval_length, byte_appender &out_itr)
{
    for( unsigned interval = 0; interval < LoadProfile_IntervalsPerBlock; ++interval )
    {
        // How long ago did this interval start? Start with the most recent interval and work backward.
        const int intervalOffset = (interval + 1) * interval_length;
        const CtiTime intervalStart = blockStart - intervalOffset;

        const double consumptionWs = makeValueConsumption(address, intervalStart, interval_length);

        appendCalculatedLpValue(consumptionWs, out_itr);
    }
}

void Mct410Sim::fillLongLoadProfile(const unsigned address, const CtiTime &blockStart, const unsigned interval_length, byte_appender &out_itr)
{
    for( unsigned interval = 0; interval < LoadProfile_IntervalsPerBlock; ++interval )
    {
        // How long ago did this interval start? Start with the most distant interval and work forward.
        const int intervalOffset = (LoadProfile_IntervalsPerBlock - interval) * interval_length;
        const CtiTime intervalStart = blockStart - intervalOffset;

        const double consumptionWs = makeValueConsumption(address, intervalStart, interval_length);

        appendCalculatedLpValue(consumptionWs, out_itr);
    }
}

void Mct410Sim::appendCalculatedLpValue(const double consumptionWs, byte_appender &out_itr)
{
    double consumptionWh = consumptionWs / SecondsPerHour;

    int dynamicDemand = Devices::Mct410Device::Utility::makeDynamicDemand(consumptionWh);

    *out_itr++ = dynamicDemand >> 8;
    *out_itr++ = dynamicDemand >> 0;
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

    _llp_interest.c_time = tmp_time;
}

bytes Mct410Sim::getValueVectorFromMemory(unsigned pos, unsigned length)
{
    return _memory.getValueVectorFromMemoryMap(pos, length);
}

void Mct410Sim::connect()
{
    _memory.writeValueToMemoryMap(MM_Status2Flags, 0x00);
}

void Mct410Sim::disconnect()
{
    _memory.writeValueToMemoryMap(MM_Status2Flags, 0x03);
}

void Mct410Sim::clearEventFlags()
{
    _memory.writeValueToMemoryMap(MM_EventFlags1, 0x00);
    _memory.writeValueToMemoryMap(MM_MeterAlarms1, 0x00);
}

unsigned Mct410Sim::getTablePointer(const CtiTime c_time, unsigned intervalSeconds)
{
    //  LP table pointer, see SSPEC-S01029 MCT-410.doc, 8.12.1, page 82
    return ((c_time.seconds() / intervalSeconds) % 96) + 1;
}

}
}

