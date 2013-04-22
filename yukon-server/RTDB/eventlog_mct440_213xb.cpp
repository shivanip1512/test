#include "precompiled.h"
#include "numstr.h"
#include "eventlog_mct440_213xb.h"

using namespace std;

namespace Cti {
namespace Devices {

namespace { // anonymous namespace

//-----------------------------------------------------------------------------
// value extraction
//-----------------------------------------------------------------------------

template < typename type_t >
type_t extractValue( const type_t value, const int bitPosH, const int bitPosL )
{
    const int shiftL = sizeof(type_t)*8 - bitPosH - 1,
              shiftR = shiftL + bitPosL;

    return (value << shiftL) >> shiftR;
}

template < typename type_t >
bool isBitSet( const type_t value, const int bitPos )
{
    return (value >> bitPos) & 1;
}

//-----------------------------------------------------------------------------
// Event code argument
//-----------------------------------------------------------------------------

// default event code (with no argument defined)
template <int eventCode>
string resolveEventArgument( const unsigned long argument )
{
    return string("");
}

// event code 2 - Primary Power Up
template <>
string resolveEventArgument<2>( const unsigned long argument )
{
    string result = "Reset Cause: ";

    switch( argument )
    {
    case   0:
    case   1:
    case   2: result += "Unused"; break;
    case   3: result += "Power failure"; break;
    case   4: result += "Watchdog reset"; break;
    case   5: result += "Switchover event"; break;
    case   6: result += "Flash Refresh"; break;
    case   7: result += "Unknown reset cause"; break;
    case   8: result += "Brown-out"; break;
    case   9: result += "ID code set"; break;
    case  10: result += "Measurement error, reset called to re-synch"; break;
    case  11: result += "Boot ROM Switchover"; break;
    case  12: result += "Bad Stack"; break;
    case  13: result += "NVM Locked"; break;
    case  14: result += "Clock error correction"; break;
    case  15: result += "User stack or interrupt stack corruption"; break;
    case 100: result += "Miscellaneous exception"; break;
    case 101: result += "UDI exception"; break;
    case 102: result += "Overflow exception"; break;
    case 103: result += "BRKI exception"; break;
    case 104: result += "Address match exception"; break;
    case 105: result += "Single step exception"; break;
    case 106: result += "DBC exception"; break;
    case 107: result += "NMI exception"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 3 - Time Changed (old time – no arg)
template <>
string resolveEventArgument<3>( const unsigned long argument )
{
    return "delta value: " + ((argument == 0)?string("absolute"):CtiNumStr(argument));
}

// event code 7 - End Device Accessed for Read
template <>
string resolveEventArgument<7>( const unsigned long argument )
{
    return "Table number: " + CtiNumStr(argument);
}

// event code 8 - End Device Accessed for Write
template <>
string resolveEventArgument<8>( const unsigned long argument )
{
    return "Table number: " + CtiNumStr(argument);
}

// event code 9 - Procedure Invoked
template <>
string resolveEventArgument<9>( const unsigned long argument )
{
    return "Procedure number: " + CtiNumStr(argument);
}

// event code 10 - Table Written To
template <>
string resolveEventArgument<10>( const unsigned long argument )
{
    return "Table number: " + CtiNumStr(argument);
}

// event code 14 - Reset List Pointers
template <>
string resolveEventArgument<14>( const unsigned long argument )
{
    string result = "List reset: ";

    switch( argument )
    {
    case 2:   result += "Self read data"; break;
    case 3:   result += "Load profile data set 1"; break;
    case 7:   result += "All LP data sets"; break;
    case 8:   result += "History Log"; break;
    case 255: result += "All lists"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 15 - Update List Pointers
template <>
string resolveEventArgument<15>( const unsigned long argument )
{
    string result = "List updated: ";

    switch( argument )
    {
    case 2:   result += "Self read data"; break;
    case 3:   result += "Load profile data set 1"; break;
    case 7:   result += "All LP data sets"; break;
    case 8:   result += "History Log"; break;
    case 255: result += "All lists"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 17 - History Log Pointers Updated
template <>
string resolveEventArgument<17>( const unsigned long argument )
{
    return "Value: " + CtiNumStr(argument);
}

// event code 19 - Event Log Pointers Updated
template <>
string resolveEventArgument<19>( const unsigned long argument )
{
    return "Value: " + CtiNumStr(argument);
}

// event code 24 - Season Change
template <>
string resolveEventArgument<24>( const unsigned long argument )
{
    return "New season number: " + CtiNumStr(argument);
}

// event code 26 - Special Schedule Activation
template <>
string resolveEventArgument<26>( const unsigned long argument )
{
    return "New special schedule: " + CtiNumStr(argument);
}

// event code 27 - Tier Switch Change
template <>
string resolveEventArgument<27>( const unsigned long argument )
{
    return "New current tier: " + CtiNumStr(argument);
}

// event code 28 - Pending Table Activation
template <>
string resolveEventArgument<28>( const unsigned long argument )
{
    return "Table number: " + CtiNumStr(argument);
}

// event code 29 - Pending Table Clear
template <>
string resolveEventArgument<29>( const unsigned long argument )
{
    return "Table number: " + CtiNumStr(argument);
}

// event code 36 - Meter Reprogrammed
template <>
string resolveEventArgument<36>( const unsigned long argument )
{
    string result = "New firmware version: ";

    const unsigned long majorVersion = extractValue( argument, 15, 12 ),
                        minorVersion = extractValue( argument, 11,  5 ),
                        build        = extractValue( argument,  4,  0 );

    if( majorVersion < 10 && minorVersion < 100 )
    {
        result += CtiNumStr(majorVersion) + "." + CtiNumStr(minorVersion) + "." + CtiNumStr(build);
    }
    else
    {
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 37 - Configuration Error Detected
template <>
string resolveEventArgument<37>( const unsigned long argument )
{
    return ( argument == 0x0000 )? string("Neuron Init Timeout") : string("");
}

// event code 38 - Self Check error Detected
template <>
string resolveEventArgument<38>( const unsigned long argument )
{
    string result;

    switch( argument )
    {
    case   0: result += "Watch-dog timeout"; break;
    case   2: result += "Abnormal Vdet4 power interrupt"; break;
    case   3: result += "Stack overflow"; break;
    case 100: result += "Miscellaneous exception"; break;
    case 101: result += "UDI exception"; break;
    case 102: result += "Overflow exception"; break;
    case 103: result += "BRKI exception"; break;
    case 104: result += "Address match exception"; break;
    case 105: result += "Single step exception"; break;
    case 106: result += "DBC exception"; break;
    case 107: result += "NMI exception"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 39 - RAM Failure Detected
template <>
string resolveEventArgument<39>( const unsigned long argument )
{
    string result;

    switch( argument )
    {
    case 1: result += "LP value corrupted"; break;
    case 3: result += "Ctrl output value corrupted"; break;
    case 4: result += "RAM memory test error"; break;
    case 5: result += "RTC code in NVRAM corrupted"; break;
    case 6: result += "NVRAM alarm variable corrupted"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 40 - ROM Failure Detected
template <>
string resolveEventArgument<40>( const unsigned long argument )
{
    string result;

    switch( argument )
    {
    case 0: result += "Bootrom invaid CRC"; break;
    case 1: result += "System image invalid CRC"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 41 - Nonvolatile Memory Failure Detected
template <>
string resolveEventArgument<41>( const unsigned long argument )
{
    string result;

    switch( argument )
    {
    case 65535: result += "All tables"; break;
    case 65534: result += "bootrom NVM CRC/signature invalid"; break;
    case 65533: result += "bootrom flash write error"; break;
    default:
        result += "Table number: " + CtiNumStr( argument & 0x3fff ) + " - ";

        if( !(argument & 0xC000) )
        {
            result += " failure detected in background checks";
        }
        else
        {
            if( argument & 0x8000 ) result += " failure detected during read-before-write";
            if( argument & 0x4000 ) result += " failure detected during read-after-write";
        }
    }

    return result;
}

// event code 42 - Clock Error Detected
template <>
string resolveEventArgument<42>( const unsigned long argument )
{
    string result;

    switch( argument )
    {
    case 0: result += "FRAM initialization or clock corruption"; break;
    case 1: result += "Process initialization"; break;
    case 2: result += "meter in stop mode"; break;
    case 3: result += "meter in wait mode and reset abnormally (not from the cold start interrupt)"; break;
    case 4: result += "meter in boot stop mode due to Vdet4 test failure"; break;
    case 5: result += "meter running safe image"; break;
    case 6: result += "meter in boot stop mode due to exception while in watch mode"; break;
    default:
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// event code 43 - Measurement Error Detected
template <>
string resolveEventArgument<43>( const unsigned long argument )
{
    string result = "channel: " + CtiNumStr( extractValue( argument, 8, 0 )) + " - "; // Bit 8..0

    if(isBitSet( argument, 9  )) result += "Measurement interrupts missing";                // Bit 9
    if(isBitSet( argument, 10 )) result += "The amplifier gain of line 1 out of range";     // Bit 10
    if(isBitSet( argument, 11 )) result += "The amplifier gain of line 2 out of range";     // Bit 11
    if(isBitSet( argument, 12 )) result += "The amplifier gain of line 3 out of range";     // Bit 12
    if(isBitSet( argument, 13 )) result += "The calibrated temperature value out of range"; // Bit 13

    return result;
}

// event code 47 - Tamper Detected
template <>
string resolveEventArgument<47>( const unsigned long argument )
{
    string result;

    if( isBitSet( argument, 0 )) result += "Meter cover removed";                     // Bit 0
    if( isBitSet( argument, 1 )) result += "Feature Removed: Pulse channel 1 tamper"; // Bit 1
    if( isBitSet( argument, 2 )) result += "Feature Removed: Pulse channel 2 tamper"; // Bit 2
    if( isBitSet( argument, 3 )) result += "Tilt tamper";                             // Bit 3

    return result;
}

// event code 48 - Reverse Rotation Detected
template <>
string resolveEventArgument<48>( const unsigned long argument )
{
    string result;

    if( isBitSet( argument, 0 )) result += "Phase A reversed"; // Bit 0
    if( isBitSet( argument, 1 )) result += "Phase B reversed"; // Bit 1
    if( isBitSet( argument, 2 )) result += "Phase C reversed"; // Bit 2
    if( isBitSet( argument, 3 )) result += "Phase Total";      // Bit 3

    return result;
}

// event code 50 - Disconnect Switch Error Detected
template <>
string resolveEventArgument<50>( const unsigned long argument )
{
    string result;

    // Argument format 1
    if( argument == 0 )
    {
        result += "No information";
    }
    // Argument format 2
    else if( extractValue( argument, 0, 0 ) == 0x0001 )
    {
        result += "Internal state: ";
        const unsigned long internalState = extractValue( argument, 2, 1 ); // Bits 1..2: internal state
        switch( internalState )
        {
            case 1:  result += "closed"; break;
            case 2:  result += "opened"; break;
            case 3:  result += "locked open"; break;
            default: result += "invalid state: " + CtiNumStr(internalState);
        }

        // Bit 3: external state
        result += "\nExternal state: " + (isBitSet( argument, 3 )?string("closed"):string("open"));

        const unsigned long phaseARmsVoltage = 175 + extractValue( argument,  7,  4 ) * 5, // Bits 4..7: Phase A RMS voltage
                            phaseBRmsVoltage = 175 + extractValue( argument, 11,  8 ) * 5, // Bits 8..11: Phase B RMS voltage
                            phaseCRmsVoltage = 175 + extractValue( argument, 15, 12 ) * 5; // Bits 12..15: Phase C RMS voltage

        result += "\nPhase A RMS voltage: " + ((phaseARmsVoltage > 245)? string("> 245"):string("<= ") + CtiNumStr(phaseARmsVoltage)) + " V RMS";
        result += "\nPhase B RMS voltage: " + ((phaseBRmsVoltage > 245)? string("> 245"):string("<= ") + CtiNumStr(phaseBRmsVoltage)) + " V RMS";
        result += "\nPhase C RMS voltage: " + ((phaseCRmsVoltage > 245)? string("> 245"):string("<= ") + CtiNumStr(phaseCRmsVoltage)) + " V RMS";
    }
    // Argument format 3
    else if( extractValue( argument, 1, 0 ) == 0x0002 )
    {
        // Bits 2..4 frequency +/-3 in Hz.
        result += "Frequency deviation: " + CtiNumStr((int)extractValue( argument, 4, 2 ) - 3) + " Hz";

        // Bits 5..9: meter temperature divided by 8, in deg C, 2s complement
        result += "\nMeter temparature: " + CtiNumStr(extractValue( (int)argument, 9, 5 )) + " C";

        // Bits 10..12: highest instantaneous power (fwd+rev) over last 5 seconds,
        result += "\nHighest instantaneous power (fwd+rev) over last 5 seconds: ";
        const unsigned long highestInstantaneousPowerMin = extractValue( argument, 12, 10 ) * 16;
        if( highestInstantaneousPowerMin < 112 )
            result += CtiNumStr(highestInstantaneousPowerMin) + ".." + CtiNumStr(highestInstantaneousPowerMin + 15) + " kW";
        else
            result += ">= 112 kW";

        //Bits 13..15: highest phase DC voltage compensation (absolute value)
        result += "\nHighest phase DC voltage compensation: ";
        const unsigned long highestPhaseDCVoltageMin = extractValue( argument, 15, 13 ) * 8;
        if( highestPhaseDCVoltageMin < 56 )
            result += CtiNumStr(highestPhaseDCVoltageMin) + ".." + CtiNumStr(highestPhaseDCVoltageMin + 7) + " V";
        else
            result += ">= 56 V";
    }
    // Argument format 4
    else if( extractValue( argument, 2, 0 ) == 0x0004 )
    {
        result += "Internal state: ";
        const unsigned long internalState = extractValue( argument, 4, 3 ); // Bits 3..4: internal state
        switch( internalState )
        {
            case 1:  result += "closed"; break;
            case 2:  result += "opened"; break;
            case 3:  result += "locked open"; break;
            default: result += "invalid state: " + CtiNumStr(internalState);
        }

        // Bit 5: disconnect state
        result += "\nDisconnect state: " + (isBitSet( argument, 5 )?string("closed"):string("open"));

        //Bit 6: load side voltage
        result += "\nLoad side voltage: " + (isBitSet( argument, 6 )?string("present"):string("none"));

        // Bit 7..15: instantaneous power in watts, capped at 511.
        result += "\nInstantaneous power: " + CtiNumStr( extractValue(argument, 15, 7 )) + " W";
    }
    else
    {
        result += "Invalid argument " + CtiNumStr(argument);
    }

    return result;
}

//-----------------------------------------------------------------------------
// event code jump table item
//-----------------------------------------------------------------------------

struct EventCodeItem
{
    const char* fieldName;
    string (*resolveEventArgumentOp) (const unsigned long);
};

//-----------------------------------------------------------------------------
// event code jump table
//-----------------------------------------------------------------------------

const EventCodeItem evenCodeLookupTable[] =
{
    { "No Event"                            , resolveEventArgument<0>  }, // event code 0
    { "Primary Power Down"                  , resolveEventArgument<1>  }, // event code 1
    { "Primary Power Up"                    , resolveEventArgument<2>  }, // event code 2
    { "Reset cause"                         , resolveEventArgument<3>  }, // event code 3
    { "delta value"                         , resolveEventArgument<4>  }, // event code 4
    { "Time Changed (old time)"             , resolveEventArgument<5>  }, // event code 5
    { "Time Changed (new time)"             , resolveEventArgument<6>  }, // event code 6
    { "End Device Accessed for Read"        , resolveEventArgument<7>  }, // event code 7
    { "End Device Accessed for Write"       , resolveEventArgument<8>  }, // event code 8
    { "Procedure Invoked"                   , resolveEventArgument<9>  }, // event code 9
    { "Table Written To"                    , resolveEventArgument<10> }, // event code 10
    { "End Device Programmed"               , resolveEventArgument<11> }, // event code 11
    { "Communication Terminated Normally"   , resolveEventArgument<12> }, // event code 12
    { "Communication Terminated Abnormally" , resolveEventArgument<13> }, // event code 13
    { "Reset List Pointers"                 , resolveEventArgument<14> }, // event code 14
    { "Update List Pointers"                , resolveEventArgument<15> }, // event code 15
    { "History Log Cleared"                 , resolveEventArgument<16> }, // event code 16
    { "History Log Pointers Updated"        , resolveEventArgument<17> }, // event code 17
    { "Event Log Cleared"                   , resolveEventArgument<18> }, // event code 18
    { "Event Log Pointers Updated"          , resolveEventArgument<19> }, // event code 19
    { "Demand Reset Occurred"               , resolveEventArgument<20> }, // event code 20
    { "Self-Read Occurred"                  , resolveEventArgument<21> }, // event code 21
    { "Daylight Savings Time On"            , resolveEventArgument<22> }, // event code 22
    { "Daylight Savings Time Off"           , resolveEventArgument<23> }, // event code 23
    { "Season Change"                       , resolveEventArgument<24> }, // event code 24
    { "Rate Change"                         , resolveEventArgument<25> }, // event code 25
    { "Special Schedule Activation"         , resolveEventArgument<26> }, // event code 26
    { "Tier Switch Change"                  , resolveEventArgument<27> }, // event code 27
    { "Pending Table Activation"            , resolveEventArgument<28> }, // event code 28
    { "Pending Table Clear"                 , resolveEventArgument<29> }, // event code 29
    { "Metering Mode Started"               , resolveEventArgument<30> }, // event code 30
    { "Metering Mode Stopped"               , resolveEventArgument<31> }, // event code 31
    { "Test Mode Started"                   , resolveEventArgument<32> }, // event code 32
    { "Test Mode Stopped"                   , resolveEventArgument<33> }, // event code 33
    { "Meter Shop Mode Started"             , resolveEventArgument<34> }, // event code 34
    { "Meter Shop Mode Stopped"             , resolveEventArgument<35> }, // event code 35
    { "Meter Reprogrammed"                  , resolveEventArgument<36> }, // event code 36
    { "Configuration Error Detected"        , resolveEventArgument<37> }, // event code 37
    { "Self Check error Detected"           , resolveEventArgument<38> }, // event code 38
    { "RAM Failure Detected"                , resolveEventArgument<39> }, // event code 39
    { "ROM Failure Detected"                , resolveEventArgument<40> }, // event code 40
    { "Nonvolatile Memory Failure Detected" , resolveEventArgument<41> }, // event code 41
    { "Clock Error Detected"                , resolveEventArgument<42> }, // event code 42
    { "Measurement Error Detected"          , resolveEventArgument<43> }, // event code 43
    { "Low Battery Detected"                , resolveEventArgument<44> }, // event code 44
    { "Low Loss Potential Detected"         , resolveEventArgument<45> }, // event code 45
    { "Demand Overload Detected"            , resolveEventArgument<46> }, // event code 46
    { "Tamper Detected"                     , resolveEventArgument<47> }, // event code 47
    { "Reverse Rotation Detected"           , resolveEventArgument<48> }, // event code 48
    { "Aborted Save All"                    , resolveEventArgument<49> }, // event code 49
    { "Disconnect Switch Error Detected"    , resolveEventArgument<50> }, // event code 50
};

const unsigned long EventCodeNumEntries = sizeof(evenCodeLookupTable)/sizeof(evenCodeLookupTable[0]);

} // anonymous namespace

//-----------------------------------------------------------------------------
// resolveEventCode()
//
// returns true if the event exist, false otherwise
//-----------------------------------------------------------------------------

bool Mct440_213xBEventLog::resolveEventCode( const unsigned long eventCode,
                                             const unsigned long argument,
                                             string& resolvedEventName,
                                             string& resolvedArgument )
{
    if( eventCode >= EventCodeNumEntries )
    {
        resolvedEventName = "";
        resolvedArgument  = "";

        return false;
    }

    const EventCodeItem& item = evenCodeLookupTable[eventCode];

    resolvedEventName = item.fieldName;
    resolvedArgument  = item.resolveEventArgumentOp( argument );

    return true;
}

}
}

