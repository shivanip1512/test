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


//-----------------------------------------------------------------------------
//  MFG event code argument
//-----------------------------------------------------------------------------

// default mfg event code (with no argument defined)
template <int eventCode>
string resolveMfgEventArgument( const unsigned long argument )
{
    return string("");
}

// mfg event code 2 - Control Output 1 Tripped
template <>
string resolveMfgEventArgument<2>( const unsigned long argument )
{
    string result;

    result += "Changed state: ";
    const unsigned long changedState = extractValue( argument, 3, 0 ); // Bits 0..3 indicates the changed state
    switch( changedState )
    {
        case 1:  result += "closed"; break;
        case 2:  result += "opened"; break;
        case 3:  result += "locked open"; break;
        default: result += "invalid state: " + CtiNumStr(changedState);
    }

    result += "\nAttempt: ";
    result += isBitSet( argument, 4 )?string("failed"):string("successful"); // Bit 4: Whether this change failed

    result += "\nReason of change: ";
    const unsigned long reasonOfChange = extractValue( argument, 15, 8 ); // High byte indicates the reason the state is changed
    switch( reasonOfChange )
    {
        case 1:  result += "MP02 operation"; break;
        case 2:  result += "Maximum Power"; break;
        case 3:  result += "Prepay"; break;
        case 4:  result += "Prepay Power"; break;
        case 5:  result += "MP23 operation"; break;
        case 6:  result += "Manual operation"; break;
        case 7:  result += "Schedule"; break;
        case 8:  result += "MP30 invoked"; break;
        case 9:  result += "Resync"; break;
        case 10: result += "Power Up Resync (PUR)"; break;
        default: result += "invalid reason of change: " + CtiNumStr(reasonOfChange);
    }

    if( reasonOfChange == 8 || reasonOfChange == 10 )
    {
        result += "\nPrevious internal status: ";
        result += isBitSet( argument, 6 )?string("closed"):string("open"); // Bit 6: Previous internal status (MP30 and PUR only)

        result += "\nPrevious external status: ";
        result += isBitSet( argument, 7 )?string("closed"):string("open"); // Bit 7: Previous external status (MP30 and PUR only)
    }

    return result;
}

// mfg event code 3 - Control Output 2 Tripped
template <>
string resolveMfgEventArgument<3>( const unsigned long argument )
{
    string result;

    result += "Changed state: ";
    const unsigned long changedState = extractValue( argument, 7, 0 ); // Low byte indicates the changed state
    switch( changedState )
    {
        case 1:  result += "closed"; break;
        case 2:  result += "opened"; break;
        default: result += "invalid state: " + CtiNumStr(changedState);
    }

    result += "\nReason of change: ";
    const unsigned long reasonOfChange = extractValue( argument, 15, 8 ); // High byte indicates the reason for the changed state
    switch( reasonOfChange )
    {
        case 1:  result += "Manual (MP02)"; break;
        case 2:  result += "Tariff-Based"; break;
        case 3:  result += "Time-Based"; break;
        default: result += "invalid reason: " + CtiNumStr(reasonOfChange);
    }

    return result;
}

// mfg event code 4 - Phase Loss
template <>
string resolveMfgEventArgument<4>( const unsigned long argument )
{
    string result;

    result += "Missing phases: ";
    if( isBitSet( argument, 0 )) result += "A ";
    if( isBitSet( argument, 1 )) result += "B ";
    if( isBitSet( argument, 2 )) result += "C ";

    return result;
}

// mfg event code 5 - Phase Inversion
template <>
string resolveMfgEventArgument<5>( const unsigned long argument )
{
    string result;

    result += "Phase Inversion: ";
    switch( argument )
    {
        case 4:  result += "+ 180 degrees inverted"; break;
        case 6:  result += "- 120 degrees inverted"; break;
        case 2:  result += "+ 120 degrees inverted"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 6 - Serial Port Error
template <>
string resolveMfgEventArgument<6>( const unsigned long argument )
{
    string result;

    result += "Error Details: ";
    switch( argument )
    {
        case 0x0000: result += "Optical port failed to put Neuron into nascent state"; break;
        case 0x0001: result += "A message failed between the meter and the Neuron (does not include PLC messages)"; break;
        case 0x4000: result += "Invalid command length received"; break;
        case 0x8000: result += "Invalid command received"; break;
        case 0xC000: result += "PLC driver unable to receive message (Rx buffer is full)"; break;
        case 0xE000: result += "PLC driver length received will overflow Rx buffer"; break;
        default:     result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 7 - General Error
template <>
string resolveMfgEventArgument<7>( const unsigned long argument )
{
    string result;

    result += "Error Details: ";
    switch( argument )
    {
        case 0x0000: result += "Unexpected timeout of process hold at the start of a power down, power down process not reliable"; break;
        case 0x0001: result += "LCD readback failed"; break;
        case 0x8000:
        {
            result += "Power Safe Critical Section period limit exceeded.";
            result += "\nAddress where GE occurred (considering <SYSIMG_Entry> 0xC000): ";
            const unsigned long addr = extractValue( argument, 14, 0 ) * 4 + 0xC000;
            result +=  string("0x") + CtiNumStr(addr).xhex().zpad(8).toString();
            break;
        }
        default:     result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 8 - General Error
template <>
string resolveMfgEventArgument<8>( const unsigned long argument )
{
    string result;

    result += "Channel Id: ";
    switch( argument )
    {
        case 0:  result += "(0) optical"; break;
        case 1:  result += "(1) PLC"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 10 - Current on Low Voltage
template <>
string resolveMfgEventArgument<10>( const unsigned long argument )
{
    string result;

    result += "Phases: ";
    if( isBitSet( argument, 0 )) result += "A ";
    if( isBitSet( argument, 1 )) result += "B ";
    if( isBitSet( argument, 2 )) result += "C ";

    return result;
}

// mfg event code 13 - CRC or Image ID Error
template <>
string resolveMfgEventArgument<13>( const unsigned long argument )
{
    string result;

    result += "Failure: ";
    switch( argument )
    {
        case 0:  result += "CRC/ID"; break;
        case 1:  result += "digest"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 14 - CRC or Image ID Error
template <>
string resolveMfgEventArgument<14>( const unsigned long argument )
{
    string result;

    result += "Details: ";
    switch( argument )
    {
        case 0:  result += "code bank changed successfully as commanded"; break;
        case 1:  result += "unexpected code bank changed detected"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 16 - MEP Installed or Removed
template <>
string resolveMfgEventArgument<16>( const unsigned long argument )
{
    string result;

    result += "Details: ";
    switch( argument )
    {
        case 0:  result += "MEP card installed"; break;
        case 1:  result += "MEP card removed"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 17 - MEP Alarm
template <>
string resolveMfgEventArgument<17>( const unsigned long argument )
{
    string result;

    result += "Device Number: ";
    const unsigned long device_nbr = extractValue( argument, 15, 13 );
    switch( device_nbr )
    {
        case 0x0: result += "invalid device - Reserved"; break;
        case 0x1: result += "M-Bus device 1"; break;
        case 0x2: result += "M-Bus device 2"; break;
        case 0x3: result += "M-Bus device 3"; break;
        case 0x4: result += "M-Bus device 4"; break;
        case 0x5: result += "MEP device"; break;
        default:  result += "invalid device: " + CtiNumStr(device_nbr);
    }

    result += "\nDevice Information: ";
    const unsigned long devince_info = extractValue( argument, 12, 0 );
    switch( devince_info )
    {
        case 0:  result += "scheduled blg read completed - billing data"; break;
        case 1:  result += "scheduled read completed – app errors"; break;
        case 2:  result += "status read completed – device alarms"; break;
        case 3:  result += "billing read overflow"; break;
        case 4:  result += "device read failed"; break;
        case 5:  result += "serial # mismatch on billing read"; break;
        default: result += "invalid device information: " + CtiNumStr(devince_info);
    }

    return result;
}

// mfg event code 19 - Phase Rotation Changed
template <>
string resolveMfgEventArgument<19>( const unsigned long argument )
{
    string result;

    result += "Current phase rotation status: ";
    switch( argument )
    {
    case 0: "L1 L2 L3"; break;
    case 1: "L3 L2 L1"; break;
    case 2: "Rotation Unknown"; break;
    default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 23 - Phase Rotation Changed
template <>
string resolveMfgEventArgument<23>( const unsigned long argument )
{
    string result;

    result += "MFG Logs: ";
    if( isBitSet( argument, 0 )) result += "Meter OTR ";
    if( isBitSet( argument, 1 )) result += "MBus OTR ";
    if( isBitSet( argument, 2 )) result += "Config ID ";

    return result;
}

// mfg event code 24 - Phase Rotation Changed
template <>
string resolveMfgEventArgument<24>( const unsigned long argument )
{
    string result;

    result += "Details: ";
    switch( argument )
    {
    case 0x0000: "initial meter load"; break;
    case 0x0001: "MP06 invocation"; break;
    case 0x0002: "interface Compatibility Setting changed during commissioning."; break;
    case 0x0003: "interface Compatibility or ANSI Compliance setting was changed via a table write"; break;
    default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 26 - MEP Installed or Removed
template <>
string resolveMfgEventArgument<26>( const unsigned long argument )
{
    string result;

    result += "Security control word override is detected: ";
    switch( argument )
    {
        case 0:  result += "override condition gone"; break;
        case 1:  result += "override detected"; break;
        default: result += "invalid argument: " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 27 - Power Quality Event Detected
template <>
string resolveMfgEventArgument<27>( const unsigned long argument )
{
    string result;

    result += "Events: ";
    if( isBitSet( argument, 0 )) result += "\nphase A sag detected";
    if( isBitSet( argument, 1 )) result += "\nphase B sag detected";
    if( isBitSet( argument, 2 )) result += "\nphase C sag detected";
    if( isBitSet( argument, 3 )) result += "\nphase A surge detected";
    if( isBitSet( argument, 4 )) result += "\nphase B surge detected";
    if( isBitSet( argument, 5 )) result += "\nphase C surge detected";
    if( isBitSet( argument, 6 )) result += "\nphase A over-current detected";
    if( isBitSet( argument, 7 )) result += "\nphase B over-current detected";
    if( isBitSet( argument, 8 )) result += "\nphase C over-current detected";

    return result;
}

// mfg event code 29 - THD Event Detected
template <>
string resolveMfgEventArgument<29>( const unsigned long argument )
{
    string result;

    result += "Events: ";
    if( isBitSet( argument, 0 )) result += "\nphase A VTHD detected";
    if( isBitSet( argument, 1 )) result += "\nphase B VTHD detected";
    if( isBitSet( argument, 2 )) result += "\nphase C VTHD detected";
    if( isBitSet( argument, 3 )) result += "\nphase A ITHD detected";
    if( isBitSet( argument, 4 )) result += "\nphase B ITHD detected";
    if( isBitSet( argument, 5 )) result += "\nphase C ITHD detected";
    if( isBitSet( argument, 6 )) result += "\nphase A VATHD detected";
    if( isBitSet( argument, 7 )) result += "\nphase B VATHD detected";
    if( isBitSet( argument, 8 )) result += "\nphase C VATHD detected";

    return result;
}

// mfg event code 31 - LSV on open disonnect switch
template <>
string resolveMfgEventArgument<31>( const unsigned long argument )
{
    string result;

    result += "LSV status: ";
    const unsigned long lsvStatus = extractValue( argument, 2, 0 );
    switch( lsvStatus )
    {
        case 0:  result += "indeterminate"; break;
        case 1:  result += "present but phase can't be determined"; break;
        case 2:  result += "present on phases indicated by next 3 bits"; break;
        case 3:  result += "in transition"; break;
        case 4:  result += "out of range"; break;
        default: result += "invalid LSV status " + CtiNumStr(lsvStatus);
    }

    result += "\nPer phase (A to C) LSV indicators: ";
    if( isBitSet( argument, 3 )) result += "\npresent on phase A";
    if( isBitSet( argument, 4 )) result += "\npresent on phase B";
    if( isBitSet( argument, 5 )) result += "\npresent on phase C";

    return result;
}

// mfg event code 32 - MEP status event
template <>
string resolveMfgEventArgument<32>( const unsigned long argument )
{
    string result;

    result += "Device Number: ";
    const unsigned long device_nbr = extractValue( argument, 15, 13 );
    switch( device_nbr )
    {
        case 0x0: result += "M-Bus device 0"; break;
        case 0x1: result += "M-Bus device 1"; break;
        case 0x2: result += "M-Bus device 2"; break;
        case 0x3: result += "M-Bus device 3"; break;
        case 0x4: result += "M-Bus device 4"; break;
        default:  result += "invalid device number " + CtiNumStr(device_nbr);
    }

    result += "\nDevice Information: ";
    const unsigned long device_info = extractValue( argument, 12, 0 );
    switch( device_info )
    {
        case 0:  result += "device paired to meter"; break;
        case 1:  result += "pairing failed – no empty slots"; break;
        case 2:  result += "pairing failed – no comms"; break;
        case 3:  result += "baud rate changed"; break;
        case 4:  result += "device logically removed from meter"; break;
        default: result += "invalid device information " + CtiNumStr(device_info);
    }

    return result;
}

// mfg event code 33 - Max Power Level Changed
template <>
string resolveMfgEventArgument<33>( const unsigned long argument )
{
    string result;

    result += "Details: ";
    switch( argument )
    {
        case 0:  result += "Max power level changed from primary to secondary"; break;
        case 1:  result += "Max power level changed from secondary to primary"; break;
        default: result += "invalid argument " + CtiNumStr(argument);
    }

    return result;
}

// mfg event code 34 - Last Sag Event Lowest Voltage
template <>
string resolveMfgEventArgument<34>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nVoltage: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value) + " V";

    return result;
}

// mfg event code 35 - Last Surge Event Highest Voltage
template <>
string resolveMfgEventArgument<35>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nVoltage: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value) + " V";

    return result;
}

// mfg event code 36 - Max value for Voltage THD Event
template <>
string resolveMfgEventArgument<36>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 37 - Max value for Current THD Event
template <>
string resolveMfgEventArgument<37>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 38 - Max value for apparent power THD Event
template <>
string resolveMfgEventArgument<38>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 39 - Max value for apparent power THD Event
template <>
string resolveMfgEventArgument<39>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 40 - Average value for Current THD Event
template <>
string resolveMfgEventArgument<40>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 41 - Average value for apparent power THD Event
template <>
string resolveMfgEventArgument<41>( const unsigned long argument )
{
    string result;

    result += "Phase: ";
    const unsigned long phase = extractValue( argument, 1, 0 );
    switch( phase )
    {
        case 0:  result += "A"; break;
        case 1:  result += "B"; break;
        case 2:  result += "C"; break;
        default: result += "invalid phase " + CtiNumStr(phase);
    }

    result += "\nTHD value: ";
    const unsigned long value = extractValue( argument, 15, 2 );
    result += CtiNumStr(value);

    return result;
}

// mfg event code 42 - Disconnect Pulsed
template <>
string resolveMfgEventArgument<42>( const unsigned long argument )
{
    string result;

    const unsigned long hbyte = extractValue( argument, 15, 8 );

    // format 1
    if( hbyte != 0 )
    {
        result += "Desired external state: ";
        result += isBitSet( argument, 0 )?string("closed"):string("open");
        if( isBitSet( argument, 1 )) result += "\nPower down pending";

        result += "\nLow 16 bits of the program counter of the calling routine: ";
        const unsigned long programCounter = extractValue( argument, 15, 2 ) * 4;
        result += CtiNumStr(programCounter);
    }
    // format 2
    else
    {
        result += "Disconnect pulse Count: ";
        const unsigned long disconnectPulseCount = extractValue( argument, 7, 0 );
        result += CtiNumStr(disconnectPulseCount);
    }

    return result;
}

// mfg event code 43 - M-Bus Sniffer Match
template <>
string resolveMfgEventArgument<43>( const unsigned long argument )
{
    string result;

    result += "Device Number: ";
    const unsigned long device_nbr = extractValue( argument, 15, 13 );
    switch( device_nbr )
    {
        case 0x0: result += "M-Bus device 0"; break;
        case 0x1: result += "M-Bus device 1"; break;
        case 0x2: result += "M-Bus device 2"; break;
        case 0x3: result += "M-Bus device 3"; break;
        case 0x4: result += "M-Bus device 4"; break;
        default:  result += "invalid device number " + CtiNumStr(device_nbr);
    }

    result += "\nData byte following MDT match: ";
    const unsigned long value = extractValue( argument, 7, 0 );
    result += string("0x") + CtiNumStr(value).xhex().zpad(2).toString();

    return result;
}

// mfg event code 44 - M-Bus Device Alarm
template <>
string resolveMfgEventArgument<44>( const unsigned long argument )
{
    string result;

    result += "Device Number: ";
    const unsigned long device_nbr = extractValue( argument, 15, 13 );
    switch( device_nbr )
    {
        case 0x0: result += "M-Bus device 0"; break;
        case 0x1: result += "M-Bus device 1"; break;
        case 0x2: result += "M-Bus device 2"; break;
        case 0x3: result += "M-Bus device 3"; break;
        case 0x4: result += "M-Bus device 4"; break;
        default:  result += "invalid device number " + CtiNumStr(device_nbr);
    }

    result += "\nAlarm data byte: ";
    const unsigned long value = extractValue( argument, 7, 0 );
    result += string("0x") + CtiNumStr(value).xhex().zpad(2).toString();

    return result;
}

// mfg event code 45 - M-Bus Alarm Match
template <>
string resolveMfgEventArgument<45>( const unsigned long argument )
{
    string result;

    result += "Device Number: ";
    const unsigned long device_nbr = extractValue( argument, 15, 13 );
    switch( device_nbr )
    {
        case 0x0: result += "M-Bus device 0"; break;
        case 0x1: result += "M-Bus device 1"; break;
        case 0x2: result += "M-Bus device 2"; break;
        case 0x3: result += "M-Bus device 3"; break;
        case 0x4: result += "M-Bus device 4"; break;
        default:  result += "invalid device number " + CtiNumStr(device_nbr);
    }

    result += "\nAlarm data byte: ";
    const unsigned long value = extractValue( argument, 7, 0 );
    result += string("0x") + CtiNumStr(value).xhex().zpad(2).toString();

    return result;
}

// mfg event code 46 - M-Bus Alarm Match
template <>
string resolveMfgEventArgument<46>( const unsigned long argument )
{
    string result;

    result += "Detail: ";
    switch( argument )
    {
        case 0:  result += "Over power threshold exceeded, associated tier forced and superseded other tier controls"; break;
        case 1:  result += "Power dropped below the over power threshold and other tier controls recovered"; break;
        default: result += "invalid argument " + CtiNumStr(argument);
    }


    return result;
}

// mfg event code 47 - Dimmer effect detected
template <>
string resolveMfgEventArgument<47>( const unsigned long argument )
{
    string result;

    result += "Detail: ";
    if( isBitSet( argument, 0 )) result += "\nDimmer effect detected on phase L1";
    if( isBitSet( argument, 1 )) result += "\nDimmer effect detected on phase L2";
    if( isBitSet( argument, 2 )) result += "\nDimmer effect detected on phase L3";

    return result;
}

//-----------------------------------------------------------------------------
//  MFG event code jump table
//-----------------------------------------------------------------------------

const EventCodeItem evenCodeMfgLookupTable[] =
{
    { "Load Profile Memory Overflow"               , resolveMfgEventArgument<0>  }, // event code 64  - mfg 0
    { "Self Read Recorded"                         , resolveMfgEventArgument<1>  }, // event code 65  - mfg 1
    { "Control Output 1 Tripped"                   , resolveMfgEventArgument<2>  }, // event code 66  - mfg 2
    { "Control Output 2 Tripped"                   , resolveMfgEventArgument<3>  }, // event code 67  - mfg 3
    { "Phase Loss"                                 , resolveMfgEventArgument<4>  }, // event code 68  - mfg 4
    { "Phase Inversion"                            , resolveMfgEventArgument<5>  }, // event code 69  - mfg 5
    { "Serial Port Error"                          , resolveMfgEventArgument<6>  }, // event code 70  - mfg 6
    { "General Error"                              , resolveMfgEventArgument<7>  }, // event code 71  - mfg 7
    { "Invalid Password"                           , resolveMfgEventArgument<8>  }, // event code 72  - mfg 8
    { "Remote Comm Inactive"                       , resolveMfgEventArgument<9>  }, // event code 73  - mfg 9
    { "Current on Low Voltage"                     , resolveMfgEventArgument<10> }, // event code 74  - mfg 10
    { "Pulse 1 Tamper"                             , resolveMfgEventArgument<11> }, // event code 75  - mfg 11
    { "Pulse 2 Tamper"                             , resolveMfgEventArgument<12> }, // event code 76  - mfg 12
    { "CRC or Image ID Error"                      , resolveMfgEventArgument<13> }, // event code 77  - mfg 13
    { "Code Bank Change"                           , resolveMfgEventArgument<14> }, // event code 78  - mfg 14
    { "Load Profile Not Stuffed"                   , resolveMfgEventArgument<15> }, // event code 79  - mfg 15
    { "MEP Installed or Removed"                   , resolveMfgEventArgument<16> }, // event code 80  - mfg 16
    { "MEP Alarm"                                  , resolveMfgEventArgument<17> }, // event code 81  - mfg 17
    { "MEP Auto-Discovery Complete"                , resolveMfgEventArgument<18> }, // event code 82  - mfg 18
    { "Phase Rotation Changed"                     , resolveMfgEventArgument<19> }, // event code 83  - mfg 19
    { "Prepay Credit Exhausted"                    , resolveMfgEventArgument<20> }, // event code 84  - mfg 20
    { "Prepay Warning Acknowledged"                , resolveMfgEventArgument<21> }, // event code 85  - mfg 21
    { "History Log Overflow Pending"               , resolveMfgEventArgument<22> }, // event code 86  - mfg 22
    { "Mfg Log Reads Available"                    , resolveMfgEventArgument<23> }, // event code 87  - mfg 23
    { "Interface Change"                           , resolveMfgEventArgument<24> }, // event code 88  - mfg 24
    { "Magnetic Tamper"                            , resolveMfgEventArgument<25> }, // event code 89  - mfg 25
    { "Access Lockout override"                    , resolveMfgEventArgument<26> }, // event code 90  - mfg 26
    { "Power Quality Event Detected"               , resolveMfgEventArgument<27> }, // event code 91  - mfg 27
    { "Event Log Unread Entries"                   , resolveMfgEventArgument<28> }, // event code 92  - mfg 28
    { "THD Event Detected"                         , resolveMfgEventArgument<29> }, // event code 93  - mfg 29
    { "LP Unread Entries"                          , resolveMfgEventArgument<30> }, // event code 94  - mfg 30
    { "LSV on open disonnect switch"               , resolveMfgEventArgument<31> }, // event code 95  - mfg 31
    { "MEP status event"                           , resolveMfgEventArgument<32> }, // event code 96  - mfg 32
    { "Max Power Level Changed"                    , resolveMfgEventArgument<33> }, // event code 97  - mfg 33
    { "Last Sag Event Lowest Voltage"              , resolveMfgEventArgument<34> }, // event code 98  - mfg 34
    { "Last Surge Event Highest Voltage"           , resolveMfgEventArgument<35> }, // event code 99  - mfg 35
    { "Max value for Voltage THD Event"            , resolveMfgEventArgument<36> }, // event code 100 - mfg 36
    { "Max value for Current THD Event"            , resolveMfgEventArgument<37> }, // event code 101 - mfg 37
    { "Max value for apparent power THD Event"     , resolveMfgEventArgument<38> }, // event code 102 - mfg 38
    { "Average value for Voltage THD Event"        , resolveMfgEventArgument<39> }, // event code 103 - mfg 39
    { "Average value for Current THD Event"        , resolveMfgEventArgument<40> }, // event code 104 - mfg 40
    { "Average value for apparent power THD Event" , resolveMfgEventArgument<41> }, // event code 105 - mfg 41
    { "Disconnect Pulsed"                          , resolveMfgEventArgument<42> }, // event code 106 - mfg 42
    { "M-Bus Sniffer Match"                        , resolveMfgEventArgument<43> }, // event code 107 - mfg 43
    { "M-Bus Device Alarm"                         , resolveMfgEventArgument<44> }, // event code 108 - mfg 44
    { "M-Bus Alarm Match"                          , resolveMfgEventArgument<45> }, // event code 109 - mfg 45
    { "Over Power Threshold Exceeded"              , resolveMfgEventArgument<46> }, // event code 110 - mfg 46
    { "Dimmer effect detected"                     , resolveMfgEventArgument<47> }, // event code 111 - mfg 47
};

const unsigned long EventCodeMfgNumEntries = sizeof(evenCodeMfgLookupTable)/sizeof(evenCodeMfgLookupTable[0]);

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
    const unsigned long mfgOffset = 64;

    if( eventCode < mfgOffset )
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
    }
    else
    {
        const unsigned long eventCodeMfg = eventCode - mfgOffset;

        if( eventCodeMfg >= EventCodeMfgNumEntries )
        {
            resolvedEventName = "";
            resolvedArgument  = "";
            return false;
        }

        const EventCodeItem& item = evenCodeMfgLookupTable[eventCodeMfg];
        resolvedEventName = item.fieldName;
        resolvedArgument  = item.resolveEventArgumentOp( argument );
    }

    return true;
}


}
}
