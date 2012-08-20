/*-----------------------------------------------------------------------------*
*
* File:   expresscom
*
* Date:   8/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.52.2.1 $
* DATE         :  $Date: 2008/11/17 19:46:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "expresscom.h"
#include "logger.h"
#include <boost/tokenizer.hpp>
#include "numstr.h"
#include "cparms.h"
#include "cmdparse.h"
#include "ctidate.h"

#define EXPRESSCOM_CRC_LENGTH 2

#include <rw\ctoken.h>



using namespace std;


CtiProtocolExpresscom::CtiProtocolExpresscom() :
_useProtocolCRC(true),
_CRC(0),
_useASCII(false),
_addressLevel(0),
_addressLength(0),
_spidAddress(0),                   // 1-65534
_geoAddress(0),                    // 1-65534
_substationAddress(0),             // 1-65534
_feederAddress(0),                 // Bit field 16 feeders 1 is LSB 16 is MSB.
_zipAddress(0),                    // 1-16777214 3 byte field.
_udaAddress(0),                    // 1-65534 User defined address.
_programAddress(0),                // 1-254 program load (like relay)
_splinterAddress(0),               // 1-254 subset of program.
_uniqueAddress(0),                 // 1-4294967295 UID.
_messageCount(0),
_celsiusMode(false),               // Default to false/no. (Implies Fahrenheit).
_heatingMode(false),               // Default to false/no;
_coolingMode(true),                // Default to true/yes;
_absoluteTemps(true)               // Default to true/yes.
{
}

CtiProtocolExpresscom::CtiProtocolExpresscom(const CtiProtocolExpresscom& aRef)
{
    *this = aRef;
}

CtiProtocolExpresscom::~CtiProtocolExpresscom()
{
}

CtiProtocolExpresscom& CtiProtocolExpresscom::operator=(const CtiProtocolExpresscom& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

bool CtiProtocolExpresscom::validateAddress(const unsigned int address,
                                            const AddressRanges minimum,
                                            const AddressRanges maximum)
{
    return (minimum <= address && address <= maximum);
}

INT CtiProtocolExpresscom::addAddressing( UINT    serial,
                                           USHORT  spid,
                                           USHORT  geo,
                                           USHORT  substation,
                                           USHORT  feeder,
                                           UINT    zip,
                                           USHORT  uda,
                                           BYTE    program,
                                           BYTE    splinter)
{
    _uniqueAddress      = serial;
    _spidAddress        = spid;
    _geoAddress         = geo;
    _substationAddress  = substation;
    _feederAddress      = feeder;
    _zipAddress         = zip;
    _udaAddress         = uda;
    _programAddress     = program;
    _splinterAddress    = splinter;

    bool valid = ((serial     == 0) || validateAddress(serial,     SerialMin,     SerialMax))     &&
                 ((spid       == 0) || validateAddress(spid,       SpidMin,       SpidMax))       &&
                 ((geo        == 0) || validateAddress(geo,        GeoMin,        GeoMax))        &&
                 ((substation == 0) || validateAddress(substation, SubstationMin, SubstationMax)) &&
                 ((feeder     == 0) || validateAddress(feeder,     FeederMin,     FeederMax))     &&
                 ((zip        == 0) || validateAddress(zip,        ZipMin,        ZipMax))        &&
                 ((uda        == 0) || validateAddress(uda,        UserMin,       UserMax))       &&
                 ((program    == 0) || validateAddress(program,    ProgramMin,    ProgramMax))    &&
                 ((splinter   == 0) || validateAddress(splinter,   SplinterMin,   SplinterMax));

    resolveAddressLevel();

    return valid ? NORMAL : BADPARAM;
}

INT CtiProtocolExpresscom::parseAddressing(CtiCommandParser &parse)
{
    _uniqueAddress      = static_cast<UINT>(parse.getiValue("xc_serial",   0));  //( serial     != -1 ? serial     : 0);
    _spidAddress        = parse.getiValue("xc_spid",     0);  //( spid       != -1 ? spid       : 0);
    _geoAddress         = parse.getiValue("xc_geo",      0);  //( geo        != -1 ? geo        : 0);
    _substationAddress  = parse.getiValue("xc_sub",      0);  //( substation != -1 ? substation : 0);
    _feederAddress      = parse.getiValue("xc_feeder",   0);  //( feeder     != -1 ? feeder     : 0);
    _zipAddress         = parse.getiValue("xc_zip",      0);  //( zip        != -1 ? zip        : 0);
    _udaAddress         = parse.getiValue("xc_uda",      0);  //( uda        != -1 ? uda        : 0);
    _programAddress     = parse.getiValue("xc_program",  0);  //( program    != -1 ? program    : 0);
    _splinterAddress    = parse.getiValue("xc_splinter", 0);  //( splinter   != -1 ? splinter   : 0);

    resolveAddressLevel();

    return validateParseAddressing(parse) ? NORMAL : BADPARAM;
}

INT CtiProtocolExpresscom::parseTargetAddressing(CtiCommandParser &parse)
{
    _uniqueAddress      = static_cast<UINT>(parse.getiValue("xca_serial_target",   0));  //( serial     != -1 ? serial     : 0);
    _spidAddress        = parse.getiValue("xca_spid_target",     0);  //( spid       != -1 ? spid       : 0);
    _geoAddress         = parse.getiValue("xca_geo_target",      0);  //( geo        != -1 ? geo        : 0);
    _substationAddress  = parse.getiValue("xca_sub_target",      0);  //( substation != -1 ? substation : 0);
    _feederAddress      = parse.getiValue("xca_feeder_target",   0);  //( feeder     != -1 ? feeder     : 0);
    _zipAddress         = parse.getiValue("xca_zip_target",      0);  //( zip        != -1 ? zip        : 0);
    _udaAddress         = parse.getiValue("xca_uda_target",      0);  //( uda        != -1 ? uda        : 0);
    _programAddress     = parse.getiValue("xca_program_target",  0);  //( program    != -1 ? program    : 0);
    _splinterAddress    = parse.getiValue("xca_splinter_target", 0);  //( splinter   != -1 ? splinter   : 0);

    resolveAddressLevel();

    // Either not using unique address, or unique address is > 0
    return (validateParseAddressing(parse) && (_uniqueAddress > 0 || _addressLevel != atIndividual)) ? NORMAL : BADPARAM;
}

void CtiProtocolExpresscom::addressMessage()
{
    // Let us assume that we MUST always be called first!
    _message.clear();
    _lengths.clear();
    _messageCount = 0;

    _message.push_back( _addressLevel );

    if(_addressLevel == atIndividual)
    {
        _message.push_back( HIBYTE(HIWORD(_uniqueAddress)) );
        _message.push_back( LOBYTE(HIWORD(_uniqueAddress)) );
        _message.push_back( HIBYTE(LOWORD(_uniqueAddress)) );
        _message.push_back( LOBYTE(LOWORD(_uniqueAddress)) );
    }
    else
    {
        if(_addressLevel & atSpid)
        {
            _message.push_back( HIBYTE(_spidAddress) );
            _message.push_back( LOBYTE(_spidAddress) );
        }
        if(_addressLevel & atGeo)
        {
            _message.push_back( HIBYTE( _geoAddress ) );
            _message.push_back( LOBYTE( _geoAddress ) );
        }
        if(_addressLevel & atSubstation)
        {
            _message.push_back( HIBYTE( _substationAddress ) );
            _message.push_back( LOBYTE( _substationAddress ) );
        }
        if(_addressLevel & atFeeder)
        {
            _message.push_back( HIBYTE( _feederAddress ) );
            _message.push_back( LOBYTE( _feederAddress ) );
        }
        if(_addressLevel & atZip)
        {
            _message.push_back( LOBYTE(HIWORD(_zipAddress)) );
            _message.push_back( HIBYTE(LOWORD(_zipAddress)) );
            _message.push_back( LOBYTE(LOWORD(_zipAddress)) );
        }
        if(_addressLevel & atUser)
        {
            _message.push_back( HIBYTE(_udaAddress) );
            _message.push_back( LOBYTE(_udaAddress) );
        }
        if(_addressLevel & atProgram)
        {
            _message.push_back( _programAddress );
        }
        if(_addressLevel & atSplinter)
        {
            _message.push_back( _splinterAddress );
        }
    }

    return;
}

INT CtiProtocolExpresscom::sync()
{
    INT status = NoError;
    _message.push_back(mtSync);
    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::timeSync(const CtiTime &local, bool fullsync)
{
    INT status = NoError;

    CtiTime delayedTime = local + ((unsigned long)gConfigParms.getValueAsInt("PORTER_PAGING_DELAY", 0));
    CtiDate theDate     = delayedTime.dateGMT();
    BYTE    dayOfWeek   = theDate.weekDay() % 7;    // CtiDate Monday = 1, Sunday = 7.  Protocol Sun = 0 - Sat = 6.

    _message.push_back( mtTimeSync );
    _message.push_back( (fullsync ? 0x80 : 0x00) | (local.isDST() ? 0x40 : 0x00) | (dayOfWeek & 0x07) );
    _message.push_back( delayedTime.hourGMT() );
    _message.push_back( delayedTime.minuteGMT() );
    _message.push_back( delayedTime.secondGMT() );

    if(fullsync)
    {
        _message.push_back( theDate.month() );
        _message.push_back( theDate.dayOfMonth() );
        _message.push_back( HIBYTE(LOWORD(theDate.year())) );
        _message.push_back( LOBYTE(LOWORD(theDate.year())) );
    }

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::tamperInformation()
{
    INT status = NoError;

    _message.push_back( mtTamper );
    _message.push_back( 0x03 ); // Requests both of the tamper bits from the LCR 3102.

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::demandResponseSummary()
{
    INT status = NoError;

    _message.push_back( mtDemandResponseSummary );

    incrementMessageCount();

    return status;
}

INT CtiProtocolExpresscom::signalTest(BYTE test)
{
    INT status = NoError;

    switch(test)
    {
    case stCancelProp:
    case stIncrementProp:
    case stIncrementPropAndTurnOnLight:
    case stFlashRSSI:
    case stOutputStatBody:
    case stPing:
        {
            _message.push_back( mtSignalTest );
            _message.push_back( test );
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::timedLoadControl(UINT loadMask, UINT shedtime_seconds, BYTE randin, BYTE randout, USHORT delay )
{
    INT status = NoError;
    BYTE flag;
    BYTE shedtime = 0;
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        UINT shedtime_minutes = shedtime_seconds / 60;  // This is now in integer minutes

        if((!loadMask && load == 0) || (loadMask & (0x01 << (load - 1))))         // We have a message to be build up here!
        {
            flag = (load & 0x0f);                  // Pick up the load designator;
            _message.push_back( mtTimedLoadControl );

            size_t flagpos = _message.size();         // This is where the index is.
            _message.push_back( 0x00 );                 // Hold a slot for the flags.

            if(shedtime_minutes > 255)
            {
                flag |= 0x20;                           // Control time is in hours!
                shedtime = shedtime_minutes / 60;       // This is now in integer hours
            }
            else
            {
                shedtime = shedtime_minutes;
            }

            _message.push_back( shedtime );

            if(randin != 0)
            {
                flag |= 0x80;
                _message.push_back( randin );
            }
            if(randout != 0)
            {
                flag |= 0x40;
                _message.push_back( randout );
            }
            if(delay != 0)
            {
                flag |= 0x10;
                _message.push_back( HIBYTE(delay) );
                _message.push_back( LOBYTE(delay) );
            }

            // Set the flags to their final answer.
            _message[ flagpos ] = flag;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}


INT CtiProtocolExpresscom::restoreLoadControl(UINT loadMask, BYTE random, USHORT delay )
{
    INT status = NoError;
    BYTE flag;
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        if((!loadMask && load == 0) || (loadMask & (0x01 << (load - 1))))         // We have a message to be build up here!
        {
            flag = (load & 0x0f);                  // Pick up the load designator;
            _message.push_back( mtRestoreLoadControl );
            size_t flagpos = _message.size();
            _message.push_back( flag );

            if(random != 0)
            {
                flag |= 0x80;
                _message.push_back( random );
            }
            if(delay != 0)
            {
                flag |= 0x10;
                _message.push_back( HIBYTE(delay) );
                _message.push_back( LOBYTE(delay) );
            }

            // Set the flags to their final answer.
            _message[ flagpos ] = flag;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}


INT CtiProtocolExpresscom::cycleLoadControl(UINT loadMask, BYTE cyclepercent, BYTE period_minutes, BYTE cyclecount, USHORT delay, bool preventrampin, bool allowTrueCycle)
{
    INT status = NoError;
    BYTE flag;
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flag = (load & 0x0f);                  // Pick up the load designator;
            _message.push_back( mtCycleLoadControl );
            size_t flagpos = _message.size();
            _message.push_back( flag );
            _message.push_back( cyclepercent );
            _message.push_back( period_minutes );
            _message.push_back( cyclecount );

            if(preventrampin)
            {
                flag |= 0x80;
            }
            if(allowTrueCycle)
            {
                flag |= 0x40;
            }
            if(delay != 0)
            {
                flag |= 0x10;
                _message.push_back( HIBYTE(delay) );
                _message.push_back( LOBYTE(delay) );
            }

            // Set the flags to their final answer.
            _message[ flagpos ] = flag;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}

//Very similar to cycleLoadControl, however different enough that I didnt want to mangle the cycleLoadControl function
INT CtiProtocolExpresscom::targetReductionCycleControl(UINT loadMask, BYTE cyclepercent, BYTE period_minutes, BYTE cyclecount, USHORT delay, bool preventrampin, bool allowTrueCycle, CtiCommandParser &parse)
{
    INT status = NoError;
    int kwhValue = 0;
    BYTE flag;
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flag = ((load & 0x0f) | 0x20);                  // Pick up the load designator, 0x20 = targetReduction
            _message.push_back( mtCycleLoadControl );
            size_t flagpos = _message.size();
            _message.push_back( flag );
            _message.push_back( cyclepercent );
            _message.push_back( period_minutes );
            _message.push_back( cyclecount );

            if(preventrampin)
            {
                flag |= 0x80;
            }
            if(allowTrueCycle)
            {
                flag |= 0x40;
            }
            if(delay != 0)
            {
                flag |= 0x10;
                _message.push_back( HIBYTE(delay) );
                _message.push_back( LOBYTE(delay) );
            }

            //Check for proper value range
            double targetKWH = parse.getdValue("target_kwh", 0);
            if( targetKWH > 10 && targetKWH <= 165 )
            {
                kwhValue = targetKWH+90;
            }
            else if( targetKWH <= 10 && targetKWH >= (double)0.1 )
            {
                kwhValue = targetKWH*10;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Invalid KWH value " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //Check for kwhvalue!=0 ??
            _message.push_back( kwhValue );

            int bytes = parse.getiValue("bytes_to_follow", 0);

            // We are allowing any number of bytes they desire to send.
            // This is due to the oddity of timing the adjustors, so it is possible to have
            // a slightly variable number of adjustors.
            _message.push_back(bytes);

            for( int i=1; i<=bytes; i++ )
            {
                string param = "param_" + CtiNumStr(i);
                _message.push_back(parse.getiValue(param, 100));
            }

            // Set the flags to their final answer.
            _message[ flagpos ] = flag;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}

INT CtiProtocolExpresscom::thermostatLoadControl(UINT loadMask, BYTE cyclepercent, BYTE periodminutes, BYTE cyclecount, USHORT delay, INT controltemperature, BYTE limittemperature, BYTE limitfallbackpercent, CHAR maxdeltaperhour, BYTE deltafallbackpercent, bool noramp)
{
    INT status = NoError;

    BYTE flaghi = (_heatingMode ? 0x10 : 0x00) | (_coolingMode ? 0x08 : 0x00) | (_celsiusMode ? 0x04 : 0x00);
    BYTE flaglo = (noramp ? 0x80 : 0x00);
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flaglo |= (load & 0x0f);                  // Pick up the load designator;
            _message.push_back( mtThermostatLoadControl );
            size_t flaghipos = _message.size();
            _message.push_back( flaghi );
            size_t flaglopos = _message.size();
            _message.push_back( flaglo );
            _message.push_back( cyclepercent );
            _message.push_back( periodminutes );
            _message.push_back( cyclecount );

            if(delay != 0)
            {
                flaglo |= 0x10;
                _message.push_back( HIBYTE(delay) );
                _message.push_back( LOBYTE(delay) );
            }

            if(controltemperature != 0)
            {
                flaghi |= 0x02 | (_absoluteTemps ? 0x01 : 0x00);
                _message.push_back( controltemperature );
            }

            if(limittemperature != 0)
            {
                flaglo |= 0x40;
                _message.push_back( limittemperature );
                _message.push_back( limitfallbackpercent );
            }

            if(maxdeltaperhour != 0)
            {
                flaglo |= 0x20;
                _message.push_back( maxdeltaperhour );
                _message.push_back( deltafallbackpercent );
            }

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}


/*  Ok, this really requires the document, but here's some ASCII art too.
 *
 *                                                      /---------------\     <- Controlled setpoint
 *                                                     /                 \
 *                                                    /                   \
 *                                                   /                     \
 *                                                  /                       \
 *                                                 /                         \
 *     R     A        B          C              D /             E           F \
 *                                               /                             \
 *  t0---tr------ta--------tb----------tc-------/--------td-------------te------tf---------- Uncontrolled Setpoint
 *                \                            /
 *                 \                          /
 *                  \                        /
 *                   \                      /
 *                    \                    /
 *                     \                  /
 *                      \                /
 *                       \              /
 *                        \------------/
 *
 *  USHORT flags,           // bitwise control flags.
 *  BYTE minTemp,           // absolute minimum temp at any point of cycle.
 *  BYTE maxTemp,           // absolute maximum temp at any point of control
 *  USHORT T_r,             // random minutes 0 - T_r chosen by switch and added to T_a.
 *  USHORT T_a,             // minutes of delay before control start.
 *  USHORT T_b,             // minutes of precontrol ramp time. Setpoint is transitioned linearly over this time period.
 *  BYTE delta_S_b,         // setpoint delta or absolute temperature for precontrol period.
 *  USHORT T_c,             // minutes duration of precontrol (precool/preheat) stage.
 *  USHORT T_d,             // minutes of precontrol to control ramp time. Setpoint is transitioned linearly over this time period.
 *  BYTE delta_S_d,         // setpoint delta or absolute temperature for control period.
 *  USHORT T_e,             // minutes duration of control stage.
 *  USHORT T_f,             // minutes of postcontrol ramp time. Setpoint is transitioned linearly over this time period back to the uncontrolled setpoint.
 *  BYTE delta_S_f);
 *
 */
INT CtiProtocolExpresscom::thermostatSetpointControl(BYTE minTemp, BYTE maxTemp, USHORT T_r, USHORT T_a, USHORT T_b, BYTE delta_S_b, USHORT T_c, USHORT T_d, BYTE delta_S_d, USHORT T_e, USHORT T_f, BYTE delta_S_f, bool hold, bool bumpFlag, BYTE stage)
{
    INT status = NoError;

    BYTE flaghi = 0x00;
    BYTE flaglo = (hold ? 0x80 : 0x00);

    bool twobytetimes = false;

    if (bumpFlag)
        _message.push_back( mtThermostatSetpointBump );
    else
        _message.push_back( mtThermostatSetpointControl );

    size_t flagposhi = _message.size();
    _message.push_back(flaghi);
    size_t flagposlo = _message.size();
    _message.push_back(flaglo);

    flaghi |= ( _absoluteTemps ? 0x40 : 0x00);
    flaghi |= ( _celsiusMode ? 0x20 : 0x00);

    if(T_r > 255 || T_a > 255 || T_b > 255 || T_c > 255 || T_d > 255 || T_e > 255 || T_f > 255)
    {
        twobytetimes = true;
        flaghi |= 0x10;
    }

    flaghi |= ( _heatingMode ? 0x02 : 0x00);
    flaghi |= ( _coolingMode ? 0x01 : 0x00);

    if(minTemp != 0)
    {
        flaghi |= 0x08;
        _message.push_back( minTemp );
    }
    if(maxTemp != 0)
    {
        flaghi |= 0x04;
        _message.push_back( maxTemp );
    }

    if( T_r  != 0)
    {
        flaglo |= 0x40;
        if(twobytetimes) _message.push_back( HIBYTE(T_r) );
        _message.push_back( T_r );
    }

    if( T_a != 0)
    {
        flaglo |= 0x20;
        if(twobytetimes) _message.push_back( HIBYTE(T_a) );
        _message.push_back( T_a );
    }

    if( T_b != 0 || delta_S_b != 0 )
    {
        flaglo |= 0x10;
        if(twobytetimes) _message.push_back( HIBYTE(T_b) );
        _message.push_back( T_b );
        _message.push_back( delta_S_b );
    }

    if( T_c != 0)
    {
        flaglo |= 0x08;
        if(twobytetimes) _message.push_back( HIBYTE(T_c) );
        _message.push_back( T_c );
    }

    if( T_d != 0 || delta_S_d != 0)
    {
        flaglo |= 0x04;
        if(twobytetimes) _message.push_back( HIBYTE(T_d) );
        _message.push_back( T_d );
        _message.push_back( delta_S_d );
    }

    if( T_e != 0)
    {
        flaglo |= 0x02;
        if(twobytetimes) _message.push_back( HIBYTE(T_e) );
        _message.push_back( T_e );
    }

    if( T_f != 0 || delta_S_f != 0)
    {
        flaglo |= 0x01;
        if(twobytetimes) _message.push_back( HIBYTE(T_f) );
        _message.push_back(T_f);
        _message.push_back(delta_S_f);
    }

    _message[flagposhi] = flaghi;
    _message[flagposlo] = flaglo;

    if (bumpFlag)
        _message.push_back(stage);  //stage is hex minutes into control.

    incrementMessageCount();
    return status;
}
INT CtiProtocolExpresscom::backlightIlluminationMsg(BYTE numCycles, BYTE dutyCycle, BYTE cycPeriod)
{
    _message.push_back( mtBacklightIllumination );
    _message.push_back( numCycles );
    _message.push_back( dutyCycle );
    _message.push_back( cycPeriod );

    incrementMessageCount();
    return NoError;
}

INT CtiProtocolExpresscom::criticalPeakPricing(  BOOL includeHeatPoint, BYTE minHeat, BOOL includeCoolPoint, BYTE maxCool,
                                                 BOOL useCelsius, USHORT controlTime, BOOL deltaFlag, BOOL wakeFlag, BYTE wake,
                                                 BOOL leaveFlag, BYTE leave, BOOL returnFlag, BYTE ret, BOOL sleepFlag, BYTE sleep)
{
    CPPFlags_t flag;
    flag.raw = 0;
    flag.setpointincluded = (includeHeatPoint || includeCoolPoint);
    flag.usecelsius = useCelsius;
    flag.heatsetpoint = includeHeatPoint;
    flag.usedelta =  deltaFlag;
    flag.sleepsp = sleepFlag;
    flag.returnsp = returnFlag;
    flag.leavesp = leaveFlag;
    flag.wakesp = wakeFlag;

    _message.push_back( mtCriticalPeakPricing );
    _message.push_back(flag.raw);
    _message.push_back(HIBYTE(controlTime));
    _message.push_back(LOBYTE(controlTime));

    if(includeHeatPoint)
    {
        _message.push_back(0xFF);
        _message.push_back(minHeat);
    }
    else if(includeCoolPoint)
    {
        _message.push_back(maxCool);
        _message.push_back(0xFF);
    }
    if(wakeFlag)
    {
        _message.push_back(wake);
    }
    if(leaveFlag)
    {
        _message.push_back(leave);
    }
    if(returnFlag)
    {
        _message.push_back(ret);
    }
    if(sleepFlag)
    {
        _message.push_back(sleep);
    }

    incrementMessageCount();
    return NoError;
}


INT CtiProtocolExpresscom::configuration(BYTE configNumber, BYTE length, PBYTE data)
{
    _message.push_back( mtConfiguration );
    _message.push_back( configNumber );
    _message.push_back( length );

    for(int i = 0; i < length; i++)
    {
        _message.push_back( data[i] );
    }

    incrementMessageCount();
    return NoError;
}

INT CtiProtocolExpresscom::rawconfiguration(string str)
{
    int i = 0;
    BYTE configNumber = 0;
    BYTE raw[256];

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    if(beg != cmdtok.end())
    {
        tempStr = *beg;
        configNumber = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    while( ++beg != cmdtok.end() && i < 256 )
    {
        tempStr = *beg;
        raw[i++] = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    return configuration(configNumber, i, raw);
}

INT CtiProtocolExpresscom::rawmaintenance(string str)
{
    int i = 0;
    BYTE function = 0;
    BYTE raw[5] = { 0, 0, 0, 0, 0};

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    if(beg != cmdtok.end())
    {
        tempStr = *beg;
        function = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    while( ++beg != cmdtok.end() && i < 4 )
    {
        tempStr = *beg;
        raw[i++] = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    return maintenance(function, raw[0], raw[1], raw[2], raw[3]);
}


INT CtiProtocolExpresscom::maintenance(BYTE function, BYTE opt1, BYTE opt2, BYTE opt3, BYTE opt4)
{
    INT status = NoError;

    _message.push_back( mtMaintenance );
    _message.push_back( function );
    _message.push_back( opt1 );
    _message.push_back( opt2 );
    _message.push_back( opt3 );
    _message.push_back( opt4 );

    incrementMessageCount();
    return status;
}


INT CtiProtocolExpresscom::service(BYTE action)
{
    INT status = NoError;
    BYTE load;

    _message.push_back( mtService );
    _message.push_back( action );

    incrementMessageCount();

    return status;
}

INT CtiProtocolExpresscom::service(UINT loadMask, bool activate)
{
    INT status = NoError;
    BYTE load;

    for(load = 0; load < 15; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            _message.push_back( mtService );
            _message.push_back( (activate ? 0x80 : 0x00 ) | (load & 0x0f) );

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}


INT CtiProtocolExpresscom::temporaryService(USHORT hoursout, bool cancel, bool deactiveColdLoad, bool deactiveLights)
{
    INT status = NoError;
    BYTE flags = (cancel ? 0x80 : 0x00) | (deactiveColdLoad ? 0x02 : 0x00) | (deactiveLights ? 0x01 : 0x00);

    _message.push_back( mtTemporaryService );
    _message.push_back( flags );

    if(flags & 0x80)    // This is an OOS.  include the time fields
    {
        _message.push_back( HIBYTE(hoursout) );
        _message.push_back( LOBYTE(hoursout) );
    }

    incrementMessageCount();
    return status;
}


INT CtiProtocolExpresscom::data(string str, BYTE configByte)
{
    int i = 0;
    BYTE raw[256];

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    while( beg != cmdtok.end() && i < 256 )
    {
        tempStr = *beg++;
        raw[i++] = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    return data(raw, i, configByte>>4, configByte & 0x0F);
}

INT CtiProtocolExpresscom::dataMessageBlock(BYTE priority, BOOL hourFlag, BOOL deleteFlag, BOOL clearFlag, BYTE timePeriod, BYTE port, string str)
{
    INT status = NoError;
    BYTE flags = 0;
    BYTE msgBlock[124];
    INT i = 0;

    msgBlock[0] = priority;
    flags |= (hourFlag ? 0x01 : 0x00);
    flags |= (deleteFlag ? 0x02 : 0x00);
    flags |= (clearFlag ? 0x04 : 0x00);
    msgBlock[1] = flags;
    msgBlock[2] = timePeriod;

    while (i < str.length() && i < 121)
    {
        msgBlock[i+3] = str.data()[i];
        i++;
    }

    status = data(msgBlock, i+3, 0x02, port );
    return status;

}

INT CtiProtocolExpresscom::data(PBYTE data, BYTE length, BYTE dataTransmitType, BYTE targetPort)
{
    INT status = NoError;
    BYTE txTypePort = 0;
    txTypePort |= ((dataTransmitType << 4) & 0xf0);
    txTypePort |= (targetPort & 0x0f);
    _message.push_back( mtData );

    _message.push_back( txTypePort );
    _message.push_back( length );

    for(int i = 0; i < length; i++)
    {
        _message.push_back( data[i] );
    }

    incrementMessageCount();
    return status;
}


INT CtiProtocolExpresscom::capControl(BYTE action, BYTE subAction, BYTE data1, BYTE data2)
{
    INT status = NoError;

    _message.push_back( mtCapcontrol );
    _message.push_back( action );
    _message.push_back( subAction );

    switch(action)
    {
    case ccControl:
        {
            break;
        }
    case ccOneByteConfig:
        {
            switch(subAction)
            {
            case ccOneByteConfigControlRelayTime:
                {
                    _message.push_back( data1 );
                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
            break;
        }
    case ccTwoByteConfig:
        {
            switch(subAction)
            {
            case ccTwoByteConfigOVUVCalcTime:
                {
                    _message.push_back( data1 );
                    _message.push_back( data2 );
                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
            break;
        }
    }

    incrementMessageCount();
    return status;
}


INT CtiProtocolExpresscom::parseRequest(CtiCommandParser &parse)
{
    INT status = NORMAL;

    addressMessage();

    if(parse.isKeyValid("xcdelta"))
    {
        _absoluteTemps = false;
    }
    if(parse.isKeyValid("xccelsius"))
    {
        _celsiusMode = true;
    }
    if(parse.isKeyValid("xcmode"))
    {
        INT controlmode = parse.getiValue("xcmode");

        if(controlmode == 1 || controlmode == 3)
        {
            _coolingMode = true;
        }
        else
        {
            _coolingMode = false;
        }

        if(controlmode == 2 || controlmode == 3)
        {
            _heatingMode = true;
        }
        else
        {
            _heatingMode = false;
        }
    }

    // Set any concatenated messages' priority!
    if(parse.isKeyValid("xcpriority"))
    {
        status = priority((BYTE)parse.getiValue("xcpriority"));
    }

    /*  
        This applies to beat the peak meters, which need to have this sent as a request message
        The goal is to treat "command (shed/restore) [$time] btp [$tier]"
        as "putconfig xcom extended tier $tier [timeout $time]"
    */
    if( parse.isKeyValid("btp") )
    {
        parse.setCommand(PutConfigRequest);
        parse.setValue( "xcextier",  true );
        parse.setValue( "xcextierlevel", parse.getiValue("btp_alert_level") );
        parse.setValue( "xctiertimeout", parse.getiValue("shed")/60 ); //shed is in seconds, timeout is in minutes
        
    }

    switch(parse.getCommand())
    {
        case GetValueRequest:
        {
            assembleGetValue(parse);
            break;
        }
        case ControlRequest:
        {
            assembleControl(parse);
            break;
        }
        case PutConfigRequest:
        {
            status = assemblePutConfig(parse);
            break;
        }
        case PutStatusRequest:
        {
            assemblePutStatus(parse);
            break;
        }
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unsupported command on expresscom route Command = " << parse.getCommand() << endl;
            }

            status = ErrorInvalidRequest;

            break;
        }
    }

    calcCRC(_message.begin(), _message.size());

    return validateParseAddressing(parse) ? status : BADPARAM;
}


void CtiProtocolExpresscom::resolveAddressLevel()
{
    _addressLevel  = atIndividual;
    _addressLength = atIndividualLen + 1;

    if( _uniqueAddress == 0 )
    {
        _addressLength = 1;

        if( _spidAddress       != 0 )   {   _addressLevel |= atSpid;       _addressLength += atSpidLen;         }
        if( _geoAddress        != 0 )   {   _addressLevel |= atGeo;        _addressLength += atGeoLen;          }
        if( _substationAddress != 0 )   {   _addressLevel |= atSubstation; _addressLength += atSubstationLen;   }
        if( _feederAddress     != 0 )   {   _addressLevel |= atFeeder;     _addressLength += atFeederLen;       }
        if( _zipAddress        != 0 )   {   _addressLevel |= atZip;        _addressLength += atZipLen;          }
        if( _udaAddress        != 0 )   {   _addressLevel |= atUser;       _addressLength += atUserLen;         }
        if( _programAddress    != 0 )   {   _addressLevel |= atProgram;    _addressLength += atProgramLen;      }
        if( _splinterAddress   != 0 )   {   _addressLevel |= atSplinter;   _addressLength += atSplinterLen;     }
    }

    return;
}


void CtiProtocolExpresscom::dump() const
{
    if( _message.size() > 0 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        char of = dout.fill('0');

        dout << " Message : ";

        for( int i = 0; i < _message.size(); i++ )
        {
            dout << hex << setw(2) << (int)_message[i] << dec << " ";
        }

        dout << endl;

        dout.fill(of);
    }
}

BYTE CtiProtocolExpresscom::getStartByte() const
{
    return(_useProtocolCRC ? 'u' : 's');  // Terminate the message with this! message.push_back(_useProtocolCRC ? 'V' : 'T');
}
BYTE CtiProtocolExpresscom::getStopByte() const
{
    return(_useProtocolCRC ? 'v' : 't');
}

INT CtiProtocolExpresscom::assembleGetValue(CtiCommandParser &parse)
{
    INT  status = NORMAL;

    if(parse.isKeyValid("xctamper"))
    {
        status = tamperInformation();
    }
    else if(parse.isKeyValid("xcdrsummary") )
    {
        status = demandResponseSummary();
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unsupported expresscom command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}

INT CtiProtocolExpresscom::assembleControl(CtiCommandParser &parse)
{
    INT  i;
    INT  status = NORMAL;
    UINT CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();
    INT  relaymask  = parse.getiValue("relaymask", 0);

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed");
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );

            INT randin  = parse.getiValue("xcrandstart", 0);
            INT randout = parse.getiValue("xcrandstop", 0);
            INT delay = parse.getiValue("delaytime_sec", 0) / 60;

            timedLoadControl(relaymask, shed_seconds, randin, randout, delay);
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE && !parse.isKeyValid("xctcycle"))
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);
        INT delay      = parse.getiValue("delaytime_sec", 0) / 60;
        bool noramp    = (parse.getiValue("xcnoramp", 0) ? true : false);
        bool tc        = (parse.getiValue("xctruecycle", 0) ? true : false);

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);

        if(!parse.isKeyValid("xctargetcycle"))
        {
            cycleLoadControl(relaymask, parse.getiValue("cycle", 0), period, repeat, delay, noramp, tc);
        }
        else
        {
            targetReductionCycleControl(relaymask, parse.getiValue("cycle", 0), period, repeat, delay, noramp, tc, parse);
        }

    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        INT rand  = parse.getiValue("xcrandstart", 0);
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        restoreLoadControl(relaymask, rand, delay);
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        cycleLoadControl(relaymask, 0, 1, 1, delay);
    }
    else if(CtlReq == CMD_FLAG_CTL_OPEN)
    {
        parse.setValue("control_reduction", 100 );
        capControl(ccControl, ccControlOpen);
    }
    else if(CtlReq == CMD_FLAG_CTL_CLOSE)
    {
        capControl(ccControl, ccControlClose);
    }
    else if(parse.isKeyValid("xctcycle"))
    {
        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle") );
        parse.setValue("control_interval", 60 * parse.getiValue("cycle_period", 30) * parse.getiValue("cycle_count", 8));

        bool noramp = ( parse.getiValue("xcnoramp", 0) ? true : false );

        thermostatLoadControl( relaymask,
                               parse.getiValue("cycle", 0),
                               parse.getiValue("cycle_period", 30),
                               parse.getiValue("cycle_count", 8),
                               parse.getiValue("delaytime_sec", 0) / 60,
                               parse.getiValue("xcctrltemp", 0),
                               parse.getiValue("xclimittemp", 0),
                               parse.getiValue("xclimitfbp", 0),
                               parse.getiValue("xcmaxdperh", 0),
                               parse.getiValue("xcmaxdperhfbp", 0),
                               noramp);
    }
    else if(parse.isKeyValid("xcsetpoint"))
    {
        bool hold = ( parse.getiValue("xcholdtemp", 0) ? true : false);
        bool bump = ( parse.getiValue("xcbump", 0) ? true : false);

        double totalcontroltime = (parse.getiValue("xctb", 0) + parse.getiValue("xctc", 0) + parse.getdValue("xctd", 0) + parse.getiValue("xcte", 0) + parse.getiValue("xctf", 0)) * 60.0;

        // Add these two items to the list for control accounting!
        if(totalcontroltime > 0) parse.setValue("control_reduction", 100 );
        parse.setValue("control_interval", (int)totalcontroltime);

        thermostatSetpointControl( parse.getiValue("xcmintemp", 0),
                                   parse.getiValue("xcmaxtemp", 0),
                                   parse.getiValue("xctr", 0),
                                   parse.getiValue("xcta", 0),
                                   parse.getiValue("xctb", 0),
                                   parse.getiValue("xcdsb", 0),
                                   parse.getiValue("xctc", 0),
                                   parse.getiValue("xctd", 0),
                                   parse.getiValue("xcdsd", 0),
                                   parse.getiValue("xcte", 0),
                                   parse.getiValue("xctf", 0),
                                   parse.getiValue("xcdsf", 0),
                                   hold,
                                   bump,
                                   parse.getiValue("xcstage", 0x00));
    }
    else if(parse.isKeyValid("xcflip"))
    {
        status = capControl( ccControl, ccControlSwap );
    }
    else if (parse.isKeyValid("xcbacklight"))
    {
       backlightIlluminationMsg( parse.getiValue("xcbacklightcycle", 0),
                                 parse.getiValue("xcbacklightduty", 0),
                                 parse.getiValue("xcbacklightperiod", 0));


    }
    else if(parse.isKeyValid("xccpp"))
    {
        BYTE setPoint = 0xFF;
        bool includeHeatPoint = parse.isKeyValid("xcminheat");
        bool includeCoolPoint = parse.isKeyValid("xcmaxcool");

        status = criticalPeakPricing(   includeHeatPoint,
                                        parse.getiValue("xcminheat", 0),
                                        includeCoolPoint,
                                        parse.getiValue("xcmaxcool", 0),
                                        _celsiusMode,
                                        parse.getiValue("xccontroltime", 0),
                                        parse.isKeyValid("xcdelta"),
                                        parse.isKeyValid("xcwake"),
                                        parse.getiValue("xcwake", 0),
                                        parse.isKeyValid("xcleave"),
                                        parse.getiValue("xcleave", 0),
                                        parse.isKeyValid("xcreturn"),
                                        parse.getiValue("xcreturn", 0),
                                        parse.isKeyValid("xcsleep"),
                                        parse.getiValue("xcsleep", 0));

    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unsupported expresscom command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}


INT CtiProtocolExpresscom::assemblePutConfig(CtiCommandParser &parse)
{
    INT status = NORMAL;

    int serial = parse.getiValue("xc_serial", 0);
    int relaymask  = parse.getiValue("relaymask", 0);


    // Service should be first parse to ensure a device which is awake and listening to us if so directed.

    if(!_addressLevel || (_addressLevel & ~atSpid))  // Allow only serial, or levels BEYOND SPID only.
    {
        if(parse.isKeyValid("xcpservice"))
        {
            status = service((BYTE)parse.getiValue("xcpservice"));
        }
        else if(parse.isKeyValid("xctservicecancel"))
        {
            // This is special syntax to cause any controlled load to restore before going o.o.s.
            priority(0);                    // Need to make certain the restore is high priority.
            restoreLoadControl(0, 2, 0);    // 0,2,0 == all relays, 0-2 minutes randomization, 0 delay minutes.
            status = temporaryService( (USHORT)parse.getiValue("xctservicetime"),
                                       (bool)parse.getiValue("xctservicecancel"),
                                       (bool)parse.getiValue("xctservicebitp"),
                                       (bool)parse.getiValue("xctservicebitl") );
        }
    }

    if(parse.isKeyValid("xcaddress"))
    {
        /*  Truths:
            1. Any load checkbox on the "from group" will be ignored in favor of the standard load specification in in the parse.
            2. If the "from group" specifies program and/or splinter, only program and/or splinter may be sent.
            3. If the "from group" does not specify program or splinter ANY addressing is allowed.
            4. Any serial number can be sent any message.
        */
        if(!_addressLevel || (_addressLevel & ~atSpid))  // Allow only serial, or levels BEYOND SPID only.
        {
            // Now we need to make certain that we have a match between "from" and "to" if group addressing is used.
            bool go = false;
            bool from_loads = _addressLevel & (atProgram|atSplinter);       // Does the "from group" specify load addresses
            bool to_lcr = (parse.isKeyValid("xca_spid") ||
                           parse.isKeyValid("xca_geo") ||
                           parse.isKeyValid("xca_sub") ||
                           parse.isKeyValid("xca_feeder") ||
                           parse.isKeyValid("xca_zip") ||
                           parse.isKeyValid("xca_uda"));           // true iff the to addresses include LCR level addresses.

            bool to_loads = (parse.isKeyValid("xca_program") || parse.isKeyValid("xca_splinter") );  // true iff the to addresses include load level addresses.

            if(!_addressLevel || !from_loads)    // Serially addressed, or the from address is not load based.
                go = true;
            else if(from_loads && !to_lcr && to_loads)
                go = true;
            else
            {
                status = BADPARAM;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Incompatible addressing modes.  Assigning load level addressing to a group without load level addressing is not allowed." << endl;
                }
            }

            if(go)
            {
                configureGeoAddressing(parse);
                configureLoadMaskAddressing(parse);
            }
        }
    }

    if(parse.isKeyValid("xcgenericaddress"))
    {
        //Add our new target addressing! This overrides all addressing levels!
        if((status = parseTargetAddressing(parse)) != NORMAL)
        {
            //We already know it is wrong, quit!
            return status;
        }

        addressMessage();

        // Send Zip, UDA, Feeder, Substation, Geo, SPID (Config 0x01)
        int tempStatus = configureGeoAddressing(parse);
        // Send program, splinter, load (Config 0x07)
        status = configureLoadAddressing(parse);

        // If either succeeds, we are ok. Unless we have MISPARAM which indicates
        // we would be sending something unexpected based on user input.
        if(tempStatus == NORMAL && status != MISPARAM)
        {
            status = NORMAL;
        }
    }

    if(parse.isKeyValid("xcsync"))
    {
        status = sync();
    }

    if(parse.isKeyValid("xctimesync"))
    {
        bool dsync = parse.getiValue("xcdatesync", 0) ? true : false;

        CtiTime now;

        if(parse.isKeyValid("xctimesync_hour"))
        {
            unsigned uhour = parse.getiValue("xctimesync_hour", 0);
            unsigned uminute = parse.getiValue("xctimesync_minute", 0);
            unsigned usecond = parse.getiValue("xctimesync_second", 0);
            now = CtiTime(uhour, uminute, usecond);
        }

        status = timeSync( now, dsync );
    }

    if(parse.isKeyValid("xcrawconfig"))
    {
        status = rawconfiguration(parse.getsValue("xcrawconfig"));
    }

    if(parse.isKeyValid("xcrawmaint"))
    {
        status = rawmaintenance(parse.getsValue("xcrawmaint"));
    }

    if(parse.isKeyValid("xcdata"))
    {
        if (parse.isKeyValid("xcascii"))
        {
            dataMessageBlock(parse.getiValue("xcdatapriority", -1),
                             parse.isKeyValid("xchour"),
                             parse.isKeyValid("xcdeletable"),
                             parse.isKeyValid("xcclear"),
                             parse.getiValue("xcdatatimeout", -1),
                             parse.getiValue("xcdataport", 0),
                             parse.getsValue("xcdata"));
        }
        else
        {
            status = data(parse.getsValue("xcdata"), parse.getiValue("xcdatacfgbyte", 0));
        }
    }

    if(parse.isKeyValid("xcschedule"))
    {
        status = parseSchedule(parse);
    }

    if(parse.isKeyValid("xcsetstate"))
    {
        BYTE fanstate = (BYTE)parse.getiValue("xcfanstate", 0);
        BYTE sysstate = (BYTE)parse.getiValue("xcsysstate", 0);
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        bool restore = parse.isKeyValid("xcrunprog") ? true : false;
        bool temporary = parse.isKeyValid("xcholdprog") ? false : true;

        if(parse.isKeyValid("xctwosetpoints"))
        {
            thermostatSetStateTwoSetpoint( relaymask,
                                           temporary,
                                           restore,
                                           parse.getiValue("xctimeout", -1),
                                           parse.getiValue("xcsetcooltemp", -1),
                                           parse.getiValue("xcsetheattemp", -1),
                                           fanstate,
                                           sysstate,
                                           delay);
        }
        else
        {
            thermostatSetState( relaymask,
                                temporary,
                                restore,
                                parse.getiValue("xctimeout", -1),
                                parse.getiValue("xcsettemp", -1),
                                fanstate,
                                sysstate,
                                delay);
        }
    }
    else if (parse.isKeyValid("xcextier"))
    {
        status = extendedTierCommand(parse.getiValue("xcextierlevel", -1),
                            parse.getiValue("xcextierrate", -1),
                            parse.getiValue("xcextiercmd", -1),
                            parse.getiValue("xcextierdisp", -1),
                            parse.getiValue("xctiertimeout", -1),
                            parse.getiValue("xctierdelay", -1));

    }
    else if(parse.isKeyValid("xcutilusage"))
    {
        status = updateUtilityUsage( parse );
    }
    else if(parse.isKeyValid("xcconfig"))
    {
        BYTE config =  (parse.getiValue("xcthermoconfig", 0) & 0xFF);
        status = configuration( cfgThermostatConfig,
                                1,  //length of data
                                (BYTE*)config );
    }
    else if (parse.isKeyValid("xcdisplay") )
    {
        if (parse.isKeyValid("xcdisplaymessage"))
        {
            BYTE data[17];
            data[0] = parse.getiValue("xcdisplaymessageid") & 0x0F;
            string displayStr = parse.getsValue("xcdisplaymessage");
            data[1] = (BYTE)&displayStr;
            int length = displayStr.size() + 1;
            if (length > 17)
                length = 17;

            status = configuration( cfgDisplayMessages,
                                    length,  //length of data
                                    data );
        }
        else
        {
            BYTE config = (parse.getiValue("xclcddisplay", 0) & 0xFF);
            status = configuration( cfgDisplayMessages,
                                    1,  //length of data
                                    (BYTE*)config );
        }

    }
    else if (parse.isKeyValid("xcutilinfo"))
    {
        status = updateUtilityInformation(parse.getiValue("xcutilchan", 0),
                                          (bool)parse.getiValue("xcdisplaycost"),
                                          (bool)parse.getiValue("xcdisplayusage"),
                                          (bool)parse.getiValue("xcchargecents"),
                                          parse.getsValue("xcoptionalstring") );

    }
    else if(parse.isKeyValid("xccold"))
    {
        status = configureColdLoad(parse);
    }
    else if(parse.isKeyValid("lcrmode"))
    {
        status = configureLCRMode(parse);
    }
    else if(parse.isKeyValid("gold"))
    {
        status = configureEmetconGoldAddress(parse);
    }
    else if(parse.isKeyValid("silver"))
    {
        status = configureEmetconSilverAddress(parse);
    }
    else if(parse.isKeyValid("xctargetloadamps"))
    {
        status = configureTargetLoadAmps(parse);
    }
    else if(parse.isKeyValid("preferredchannellist"))
    {
        status = configurePreferredChannels(parse);
    }
    else if(parse.isKeyValid("xcpricetier"))
    {
        status = configurePriceTierCommand(parse.getiValue("xcpricetier", 0));
    }
    else if (parse.isKeyValid("xccomparerssi"))
    {
        status = compareRSSI();
    }
    else if(parse.isKeyValid("xccmdinitiator"))
    {
        status = commandInitiator(parse.getiValue("xccmdinitiator", 0));
    }
    if(parse.isKeyValid("ovuv"))
    {
        BYTE action = 0x01;
        BYTE subaction = 0x03 + parse.getiValue("ovuv");
        BYTE data1 = parse.getiValue("cbc_data1", 0);
        BYTE data2 = parse.getiValue("cbc_data2", 0);

        status = capControl( action, subaction, data1, data2 );
    }


    return status;
}

INT CtiProtocolExpresscom::assemblePutStatus(CtiCommandParser &parse)
{
    INT status = NORMAL;

    if(parse.isKeyValid("xcproptest"))
    {
        status = signalTest( (BYTE)parse.getiValue("xcproptest") );
    }
    else if(parse.isKeyValid("ovuv"))
    {
        BYTE action = 0x01;
        BYTE subaction = 0x03 + parse.getiValue("ovuv");
        BYTE data1 = parse.getiValue("cbc_data1", 0);
        BYTE data2 = parse.getiValue("cbc_data2", 0);

        status = capControl( action, subaction, data1, data2 );
  }

    return status;
}

INT CtiProtocolExpresscom::configurePriceTierCommand(BYTE priceTier)
{
    INT status = NoError;
    _message.push_back(mtThermostatPriceTier);
    if(priceTier > 4)
    {
        priceTier = 0;
    }
    _message.push_back(priceTier);

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::compareRSSI()
{
    INT status = NoError;
    _message.push_back(mtCompareRSSI);

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::commandInitiator(BYTE commandId)
{
    INT status = NoError;
     _message.push_back(mtCommandInitiator);
     _message.push_back(commandId);

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::updateUtilityUsage(CtiCommandParser &parse )
{
    INT status = NoError;
    INT numUsageValues = parse.getiValue("xcnumutilvalues", 0);
    _message.push_back( mtUpdateUtilityUsage );
    _message.push_back( numUsageValues );


    for (int chanIndex = 0; chanIndex < numUsageValues; chanIndex++)
    {
        string chan("xcchan_" +  CtiNumStr(chanIndex));
        string chanBucket("xcchanbucket_" +  CtiNumStr(chanIndex));
        string chanValue("xcchanvalue_" + CtiNumStr(chanIndex));

        BYTE chanNum = parse.getiValue(chan, 0);
        BYTE bucket = parse.getiValue(chanBucket, 0);
        USHORT chanVal = parse.getdValue(chanValue, 0);

        _message.push_back(LOBYTE(chanNum));
        _message.push_back(LOBYTE(bucket));

        _message.push_back( HIBYTE(chanVal) );
        _message.push_back( LOBYTE(chanVal) );

    }
    incrementMessageCount();

    return status;
}


INT CtiProtocolExpresscom::updateUtilityInformation( BYTE chan, BOOL displayCost, BOOL displayUsage,
                                                     BOOL currencyInCents, string optionalString)
{
    INT status = NoError;
    BYTE config = 0x00;
    BYTE utilFlags = 0x00;

    _message.push_back( mtConfiguration );
    _message.push_back( cfgUtilityInformation );

    size_t sizepos = _message.size();         // This is where the index is.
    _message.push_back( 0x00 );                 // Hold a slot for the flags.

    _message.push_back( chan );

    size_t configpos = _message.size();

    if (displayCost > 0)
    {
        config |= 0x01;
    }
    if(displayUsage > 0)
    {
        config |= 0x02;
    }
    if(currencyInCents > 0)
    {
        config |= 0x04;
    }
    _message.push_back(config);

    if (optionalString.length() > 0)
    {
        int length = optionalString.length();
        if (length > 20)
            length = 20;
        _message.push_back(length);
        for(int i = 0; i < length; i++)
        {
            _message.push_back( optionalString[i] );
        }
    }


    _message[sizepos] = _message.size() - sizepos - 1; //The size byte is not counted in the size

    incrementMessageCount();

    return status;
}


INT CtiProtocolExpresscom::parseSchedule(CtiCommandParser &parse)
{
    INT status = NoError;

    BYTE dow;
    BYTE pod;

    vector< BYTE > schedule;

    schedule.push_back(0);      // This is the number of schedule points!

    for(dow = 0; dow < 10; dow ++)
    {
        for(pod = 0; pod < 16; pod ++)
        {
            BYTE per = ( (dow) << 4 | (pod & 0x0f) );
            string hhstr("xctodshh_" + CtiNumStr(per));
            string mmstr("xctodsmm_" + CtiNumStr(per));
            string heatstr("xctodsheat_" + CtiNumStr(per));
            string coolstr("xctodscool_" + CtiNumStr(per));

            BYTE hh = (BYTE)parse.getiValue(hhstr, 0xff);
            BYTE mm = (BYTE)parse.getiValue(mmstr, 0xff);
            BYTE heat = (BYTE)parse.getiValue(heatstr, 0xff);
            BYTE cool = (BYTE)parse.getiValue(coolstr, 0xff);

            if(hh != 0xff || mm != 0xff || heat != 0xff || cool != 0xff)
            {
                // One of them were defined!
#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << hhstr << " " << (int)hh << endl;
                    dout << mmstr << " " << (int)mm << endl;
                    dout << heatstr << " " << (int)heat << endl;
                    dout << coolstr << " " << (int)cool << endl;
                }
#endif
                schedule.push_back(per);
                schedule.push_back(hh);
                schedule.push_back(mm);
                schedule.push_back(heat);
                schedule.push_back(cool);

                schedule[0] = schedule[0] + 1;
            }
        }
    }

    schedulePoint(schedule);

    return status;
}

INT CtiProtocolExpresscom::schedulePoint(vector< BYTE > &schedule)
{
    INT status = NoError;

    if(schedule.size() > 1)
    {
        BYTE data[1024];

        int i;  // now must be defined outside of forloop
        for(i = 0; i < schedule.size() && i < 1024; i++)
        {
            data[i] = schedule[i];
        }

        // OK, lets build a configuration message.

        status = configuration( 0x26, i, data);
    }

    return status;
}

INT CtiProtocolExpresscom::configureGeoAddressing(CtiCommandParser &parse)
{
    INT status = BADPARAM;

    BYTE length = 1;
    BYTE raw[20];

    raw[0] = (parse.isKeyValid("xca_spid") ? 0x80 : 0x00) |
             (parse.isKeyValid("xca_geo") ? 0x40 : 0x00) |
             (parse.isKeyValid("xca_sub") ? 0x20 : 0x00) |
             (parse.isKeyValid("xca_feeder") ? 0x10 : 0x00) |
             (parse.isKeyValid("xca_zip") ? 0x08 : 0x00) |
             (parse.isKeyValid("xca_uda") ? 0x04 : 0x00);

    if(raw[0] != 0)
    {
        if(raw[0] & 0x80)
        {
            USHORT spid = (USHORT)parse.getiValue("xca_spid", 0);
            raw[length++] = HIBYTE(spid);
            raw[length++] = LOBYTE(spid);
        }
        if(raw[0] & 0x40)
        {
            USHORT geo = (USHORT)parse.getiValue("xca_geo", 0);
            raw[length++] = HIBYTE(geo);
            raw[length++] = LOBYTE(geo);
        }
        if(raw[0] & 0x20)
        {
            USHORT sub = (USHORT)parse.getiValue("xca_sub", 0);
            raw[length++] = HIBYTE(sub);
            raw[length++] = LOBYTE(sub);
        }
        if(raw[0] & 0x10)
        {
            USHORT feeder = (USHORT)parse.getiValue("xca_feeder", 0);
            raw[length++] = HIBYTE(feeder);
            raw[length++] = LOBYTE(feeder);
        }
        if(raw[0] & 0x08)
        {
            int zip = parse.getiValue("xca_zip", 0);
            raw[length++] = LOBYTE(HIWORD(zip));
            raw[length++] = HIBYTE(LOWORD(zip));
            raw[length++] = LOBYTE(LOWORD(zip));
        }
        if(raw[0] & 0x04)
        {
            USHORT uda = (USHORT)parse.getiValue("xca_uda", 0);
            raw[length++] = HIBYTE(uda);
            raw[length++] = LOBYTE(uda);
        }

        status = configuration( 0x01, length, raw );
    }

    return status;
}

// Takes a list of programs, splinters, and loads and possibly sends multiple commands
INT CtiProtocolExpresscom::configureLoadMaskAddressing(CtiCommandParser &parse)
{
    INT status = BADPARAM;

    BYTE length = 1;
    BYTE raw[8];

    string programStr  = parse.getsValue("xca_program");
    string splinterStr = parse.getsValue("xca_splinter");

    int program   = -1;
    int splinter  = -1;
    BYTE loadmask = parse.getiValue("xca_loadmask", 0);
    BYTE load;

    boost::char_separator<char> sep(",");
    Boost_char_tokenizer programs (programStr,  sep);
    Boost_char_tokenizer splinters(splinterStr, sep);

    Boost_char_tokenizer::iterator current_program  = programs.begin();
    Boost_char_tokenizer::iterator current_splinter = splinters.begin();

    for( load = 0; load < 15; load++ )
    {
        if( loadmask & (0x01 << load) )
        {
            if( current_program  != programs.end() )
            {
                if( !current_program->empty() )   program  = atoi(current_program->c_str());  // The last program address in the comma delimited string will PAD out alll loads.  If there is only one, it is assigned to all loads.
                current_program++;
            }

            if( current_splinter != splinters.end() )
            {
                if( !current_splinter->empty() )  splinter = atoi(current_splinter->c_str());  // The last splinter address in the comma delimited string will PAD out alll loads.  If there is only one, it is assigned to all loads.
                current_splinter++;
            }

            length = 1;
            raw[0] = (program >= 0 ? 0x20 : 0x00) | (splinter >= 0 ? 0x10 : 0x00) | (load + 1);

            if( (raw[0] & 0x30) != 0 )
            {
                if( program  >= 0 )  raw[length++] = (BYTE)program;
                if( splinter >= 0 )  raw[length++] = (BYTE)splinter;

                status = configuration( 0x07, length, raw );
            }
        }
    }

    return status;
}

// Configures a single load (1-15) or all loads (0)
// Returns BADPARAM if the parameters are non existang, and MISPARAM if half of the parameters exist
INT CtiProtocolExpresscom::configureLoadAddressing(CtiCommandParser &parse)
{
    INT status = BADPARAM;

    BYTE length = 1;
    BYTE raw[8];

    int program   = parse.getiValue("xca_program");
    int splinter  = parse.getiValue("xca_splinter");
    int load      = parse.getiValue("xca_load");

    if( load != INT_MIN && (program != INT_MIN || splinter != INT_MIN) )
    {
        length = 1;
        raw[0] = (program >= 0 ? 0x20 : 0x00) | (splinter >= 0 ? 0x10 : 0x00) | (load);

        if( program  != INT_MIN )  raw[length++] = (BYTE)program;
        if( splinter != INT_MIN )  raw[length++] = (BYTE)splinter;

        status = configuration( 0x07, length, raw );
    }
    else if( load != INT_MIN || program != INT_MIN || splinter != INT_MIN )
    {
        // If we dont have the combination of load and program or splinter, but we do have
        // One of them, this is a different error.
        status = MISPARAM;
    }

    return status;
}

INT CtiProtocolExpresscom::thermostatSetState(UINT loadMask, bool temporary, bool restore, int timeout_min, int setpoint, BYTE fanstate, BYTE sysstate, USHORT delay)
{
    INT status = NoError;
    BYTE flaghi;
    BYTE flaglo;
    BYTE load;

    for(load = 0; load < 16; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flaghi = ( (temporary ? 0x80 : 0x00) | (fanstate & 0x03) | (sysstate & 0x1c) | (restore ? 0x20 : 0x00));
            flaglo = ( _celsiusMode ? 0x20 : 0x00) | (load & 0x0f);                  // Pick up the load designator;

            _message.push_back( mtThermostatSetState );

            size_t flagposhi = _message.size();
            _message.push_back(flaghi);
            size_t flagposlo = _message.size();
            _message.push_back(flaglo);

            if(timeout_min > 0)
            {
                BYTE timeout;

                flaghi |= 0x40;         // Timeout included.

                if(timeout_min > 255)
                {
                    flaglo |= 0x80;                         // Control time is in hours!

                    if(timeout_min / 60 > 255)
                    {
                        timeout = 255;
                    }
                    else
                    {
                        timeout = LOBYTE(timeout_min / 60);     // This is now in integer hours
                    }
                }
                else
                {
                    timeout = LOBYTE(timeout_min);
                }

                _message.push_back(timeout);
            }

            if(setpoint > 0)
            {
                flaglo |= 0x10;         // Temp setpoint included.
                _message.push_back(LOBYTE(setpoint));
            }

            if(delay > 0)
            {
                flaglo |= 0x40;
                _message.push_back(HIBYTE(delay));
                _message.push_back(LOBYTE(delay));
            }

            _message[flagposhi] = flaghi;
            _message[flagposlo] = flaglo;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}

INT CtiProtocolExpresscom::thermostatSetStateTwoSetpoint(UINT loadMask, bool temporary, bool restore, int timeout_min, int setcoolpoint, int setheatpoint, BYTE fanstate, BYTE sysstate, USHORT delay)
{
    INT status = NoError;
    BYTE flaghi;
    BYTE flaglo;
    BYTE load;

    for(load = 0; load < 16; load++)
    {
        if((!loadMask && load == 0) || loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flaghi = ( (temporary ? 0x80 : 0x00) | (fanstate & 0x03) | (sysstate & 0x1c) | (restore ? 0x20 : 0x00));
            flaglo = ( _celsiusMode ? 0x20 : 0x00) | (load & 0x0f);                  // Pick up the load designator;

            _message.push_back( mtThermostatSetStateTwoSetpoint );
            size_t flagposhi = _message.size();
            _message.push_back(flaghi);
            size_t flagposlo = _message.size();
            _message.push_back(flaglo);

            if(timeout_min > 0)
            {
                BYTE timeout;

                flaghi |= 0x40;         // Timeout included.

                if(timeout_min > 255)
                {
                    flaglo |= 0x80;                         // Control time is in hours!

                    if(timeout_min / 60 > 255)
                    {
                        timeout = 255;
                    }
                    else
                    {
                        timeout = LOBYTE(timeout_min / 60);     // This is now in integer hours
                    }
                }
                else
                {
                    timeout = LOBYTE(timeout_min);
                }

                _message.push_back(timeout);
            }

            if(setcoolpoint > 0)
            {
                flaglo |= 0x10;         // Temp setpoint included.
                _message.push_back(LOBYTE(setcoolpoint));
                if (setheatpoint > 0)
                {
                    _message.push_back(LOBYTE(setheatpoint));
                }
                else
                {
                    _message.push_back(LOBYTE(0xFF));  //setpointheat = 0xff, no change.
                }

            }
            else if(setheatpoint > 0)
            {
                flaglo |= 0x10;         // Temp setpoint included.
                _message.push_back(LOBYTE(0xFF)); //setpointcool = 0xff, no change.
                _message.push_back(LOBYTE(setheatpoint));
            }

            if(delay > 0)
            {
                flaglo |= 0x40;
                _message.push_back(HIBYTE(delay));
                _message.push_back(LOBYTE(delay));
            }

            _message[flagposhi] = flaghi;
            _message[flagposlo] = flaglo;

            incrementMessageCount();
        }

        if(!loadMask && load == 0) break;
    }

    return status;
}

INT CtiProtocolExpresscom::extendedTierCommand(int level, int rate, int cmd, int display, int timeout, int delay)
{

    INT status = NoError;
    BYTE flags;
    _message.push_back( mtExtendedTierMsg );

    flags = 0x00;
    size_t flagpos = _message.size();

    _message.push_back(flags);
    _message.push_back(level);

    if(rate > 0)
    {
        flags |= 0x80;         // rate setpoint included.
        _message.push_back(rate);
    }
    if(cmd > 0)
    {
        flags |= 0x40;         // cmd setpoint included.
        _message.push_back(cmd);
    }
    if(display > 0)
    {
        flags |= 0x20;         // display setpoint included.
        _message.push_back(display);
    }
    if(timeout > 0)
    {
        flags |= 0x10;         // timeout setpoint included.
        _message.push_back(HIBYTE(timeout));
        _message.push_back(LOBYTE(timeout));
    }
    if(delay > 0)
    {
        flags |= 0x08;         // delay setpoint included.
        _message.push_back(HIBYTE(delay));
        _message.push_back(LOBYTE(delay));
    }

    _message[flagpos] = flags;
    incrementMessageCount();

    return status;
}

INT CtiProtocolExpresscom::configureColdLoad(CtiCommandParser &parse)
{
    INT status = NoError;
    BYTE config[3];
    USHORT time;

    if(parse.isKeyValid("xccoldload_r"))
    {
        time = parse.getiValue("xccoldload_r");
        time = time*2;//half seconds!
        config[0] = 0;
        config[1] = time>>8;
        config[2] = time;
        status = configuration( cfgColdLoad,
                                3,  //length of data
                                config );
    }
    else
    {
        for(int i = 1; i<=15; i++)
        {
            string coldLoadStr = "xccoldload_r";
            coldLoadStr += CtiNumStr(i);
            if(parse.isKeyValid(coldLoadStr))
            {
                time = parse.getiValue(coldLoadStr);
                time = time*2;//half seconds!
                config[0] = i;
                config[1] = time>>8;
                config[2] = time;
                status = configuration( cfgColdLoad,
                                        3,  //length of data
                                        config );
            }
        }
    }

    return status;
}

INT CtiProtocolExpresscom::configureLCRMode(CtiCommandParser &parse)
{
    INT status = SYNTAX;
    BYTE config = 0;

    /*  Length: 1 byte
        Data Byte 1: 8 bit field
        Bit 0 - Versacom
        Bit 1 - ExpressCom
        Bit 2 - Emetcon A words
        Bit 3 - Golay
    */
    if(parse.isKeyValid("modevcom"))
    {
        config |= 1;
    }
    if(parse.isKeyValid("modexcom"))
    {
        config |= (1<<1);
    }
    if(parse.isKeyValid("modeemetcon"))
    {
        config |= (1<<2);
    }
    if(parse.isKeyValid("modegolay"))
    {
        config |= (1<<3);
    }

    if(config != 0)
    {
        status = configuration( cfgProtocolMode,
                                1,  //length of data
                                &config );
    }
    return status;
}

INT CtiProtocolExpresscom::configureEmetconGoldAddress(CtiCommandParser &parse)
{
    INT status = SYNTAX;
    BYTE config = 0;

    config = parse.getiValue("gold");

    config = config - 1; //We have the user input this as 1-4

    if(config < 4) //Gold address is configured as 0-3
    {
        status = configuration( cfgEmetconGoldAddress,
                                1,  //length of data
                                &config );
    }
    return status;
}

INT CtiProtocolExpresscom::configureEmetconSilverAddress(CtiCommandParser &parse)
{
    INT status = SYNTAX;
    BYTE config = 0;

    config = parse.getiValue("silver");
    config = config - 1; //We have the user input this as 1-60

    if(config < 60) //Silver address is configured as 0-59
    {
        status = configuration( cfgEmetconSilverAddress,
                                1,  //length of data
                                &config );
    }
    return status;
}

INT CtiProtocolExpresscom::configurePreferredChannels(CtiCommandParser &parse)
{
    std::vector<float> channels = parse.parseListOfFloats(parse.getsValue("preferredchannellist"));

    std::vector<BYTE> config;

    for each(const float &channel in channels)
    {
        if(channel >= 108.05 || channel <= 63.95) // 64-108 are known possible in hardware. Compensting for floating point errors.
        {
            return BADPARAM;
        }
        int hundrethsOfMhz = ((channel * 100)+.5); // Positive only rounding, works for my use.
        config.push_back(hundrethsOfMhz>>8);
        config.push_back(hundrethsOfMhz);
    }

    if(config.size() > 0) // &*config.begin() is not safe if this is not true.
    {
        return configuration( cfgPreferredChannels, config.size(), &config.front());
    }
    else
    {
        return BADPARAM;
    }
}

INT CtiProtocolExpresscom::configureTargetLoadAmps(CtiCommandParser &parse)
{
    BYTE config[2];
    DOUBLE amps;
    short tenthsOfAmps; // This is configured in amps*10. 20.1 amps = 201

    amps = parse.getdValue("xctargetloadamps");
    tenthsOfAmps = (amps * 10);

    config[0] = tenthsOfAmps >> 8;
    config[1] = tenthsOfAmps;

    return configuration( cfgTargetLoadAmps, 2, config);
}

INT CtiProtocolExpresscom::priority(BYTE priority)
{
    INT status = NoError;

    if(priority > 3) priority = 3;

    _message.push_back( mtPriority );
    _message.push_back( priority );

    incrementMessageCount();
    return status;

}

void CtiProtocolExpresscom::incrementMessageCount()
{
    _lengths.push_back(_message.size());
    _messageCount++;
}

BYTE CtiProtocolExpresscom::getByte(int pos, int messageNum)// messageNum is a 1 based number
{
    char byte[5];
    BYTE retVal;
    try
    {
        if(_useASCII)
        {
            int messageLoc = pos/2;
            if(messageNum == 0)
            {
                if( messageLoc < _message.size())
                {
                    retVal = _message[messageLoc];
                }
                else
                {
                    if(_useProtocolCRC && pos < messageSize())
                    {
                        // crcPos is 0 for the first 2 crc bytes and 1 for the secondary 2.
                        int crcPos = messageLoc-_message.size();
                        retVal = _CRC>>(8*(1-crcPos));
                    }
                    else
                    {
                        retVal = 0;
                    }
                }
            }
            else if(messageLoc<_addressLength || (messageNum == 1 && messageLoc<_lengths.at(0)))
            {
                retVal = _message[messageLoc];
            }
            else if(_lengths.size()>=messageNum && messageNum>1 && messageNum<=_messageCount && messageLoc>0 && messageLoc<(_addressLength + _lengths.at(messageNum-1) - _lengths.at(messageNum-2)))
            {
                retVal = _message[_lengths.at(messageNum-2) + messageLoc - _addressLength];
            }
            else
            {
                retVal = 0;
            }
            ::sprintf(byte, "%1X", (retVal>>(4*(1-pos%2)))&0x0F);
            retVal = byte[0];
        }
        else
        {
            if(messageNum == 0)
            {
                if(pos < _message.size())
                {
                    retVal = _message[pos];
                }
                else
                {
                    int difference = pos-_message.size();
                    if(_useProtocolCRC && difference <= 1 )
                    {
                        retVal = _CRC>>(8*(1-difference));
                    }
                    else
                    {
                        retVal = 0;
                    }
                }
            }
            else if(pos<_addressLength || (messageNum == 1 && pos<_lengths.at(0)))
            {
                retVal = _message[pos];
            }
            else if(_lengths.size()>=messageNum && messageNum>1 && messageNum<=_messageCount && pos>0 && pos<(_addressLength + _lengths.at(messageNum-1) - _lengths.at(messageNum-2)))
            {
                retVal = _message[_lengths.at(messageNum-2) + pos - _addressLength];
            }
            else
            {
                retVal = 0;
            }
        }
    }
    catch(...)//_lengths.at() can throw
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        retVal = 0;
    }

    return retVal;
}

bool CtiProtocolExpresscom::getFullMessage(std::vector<unsigned char> &message)
{
    if(messageSize() <= 0)
    {
        return false;
    }

    message.push_back(getStartByte());

    int curByte;
    for(int i = 0; i < messageSize(); i++)
    {
        message.push_back(getByte(i));
    }

    message.push_back(getStopByte());

    return true;
}

string CtiProtocolExpresscom::getMessageAsString()
{
    string message;
    if(messageSize() <= 0)
    {
        return message;
    }

    message.push_back(getStartByte());

    bool wasUseAscii = _useASCII;
    _useASCII = true;

    int curByte;
    for(int i = 0; i < messageSize(); i++)
    {
        message.push_back(getByte(i));
    }

    message.push_back(getStopByte());

    _useASCII = wasUseAscii;

    return message;
}

int CtiProtocolExpresscom::messageSize(int messageNum)
{
    int size;
    try
    {
        if(messageNum == 0)
        {
            if(_useASCII)
            {
                size = _message.size()*2;
                if(_useProtocolCRC)
                {
                    size += 2*EXPRESSCOM_CRC_LENGTH;
                }
            }
            else
            {
                size = _message.size();
                if(_useProtocolCRC)
                {
                    size += EXPRESSCOM_CRC_LENGTH;
                }
            }
        }
        else if(messageNum == 1)
        {
            size = _lengths.at(0);
        }
        else if(messageNum>1 && messageNum<=_messageCount && _lengths.size()>=messageNum)
        {
            size = _addressLength + _lengths.at(messageNum-1) - _lengths.at(messageNum-2);
        }
        else
        {
            size = 0;
        }
    }
    catch(...)//_lengths.at() can throw
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        size = 0;
    }

    return size;
}

void CtiProtocolExpresscom::setUseCRC(bool useCRC)
{
    _useProtocolCRC = useCRC;
}

bool CtiProtocolExpresscom::getUseCRC()
{
    return _useProtocolCRC;
}

void CtiProtocolExpresscom::setUseASCII(bool useASCII)
{
    _useASCII = useASCII;
}

bool CtiProtocolExpresscom::getUseASCII()
{
    return _useASCII;
}

/* here are the CRC's of each 8bit character, already generated - faster execution, more ROM */
unsigned short CtiProtocolExpresscom::crctbl[256] = {  /* generated by crcgen.c */
    0x0000,0x1021,0x2042,0x3063,0x4084,0x50A5,0x60C6,0x70E7,
    0x8108,0x9129,0xA14A,0xB16B,0xC18C,0xD1AD,0xE1CE,0xF1EF,
    0x1231,0x0210,0x3273,0x2252,0x52B5,0x4294,0x72F7,0x62D6,
    0x9339,0x8318,0xB37B,0xA35A,0xD3BD,0xC39C,0xF3FF,0xE3DE,
    0x2462,0x3443,0x0420,0x1401,0x64E6,0x74C7,0x44A4,0x5485,
    0xA56A,0xB54B,0x8528,0x9509,0xE5EE,0xF5CF,0xC5AC,0xD58D,
    0x3653,0x2672,0x1611,0x0630,0x76D7,0x66F6,0x5695,0x46B4,
    0xB75B,0xA77A,0x9719,0x8738,0xF7DF,0xE7FE,0xD79D,0xC7BC,
    0x48C4,0x58E5,0x6886,0x78A7,0x0840,0x1861,0x2802,0x3823,
    0xC9CC,0xD9ED,0xE98E,0xF9AF,0x8948,0x9969,0xA90A,0xB92B,
    0x5AF5,0x4AD4,0x7AB7,0x6A96,0x1A71,0x0A50,0x3A33,0x2A12,
    0xDBFD,0xCBDC,0xFBBF,0xEB9E,0x9B79,0x8B58,0xBB3B,0xAB1A,
    0x6CA6,0x7C87,0x4CE4,0x5CC5,0x2C22,0x3C03,0x0C60,0x1C41,
    0xEDAE,0xFD8F,0xCDEC,0xDDCD,0xAD2A,0xBD0B,0x8D68,0x9D49,
    0x7E97,0x6EB6,0x5ED5,0x4EF4,0x3E13,0x2E32,0x1E51,0x0E70,
    0xFF9F,0xEFBE,0xDFDD,0xCFFC,0xBF1B,0xAF3A,0x9F59,0x8F78,
    0x9188,0x81A9,0xB1CA,0xA1EB,0xD10C,0xC12D,0xF14E,0xE16F,
    0x1080,0x00A1,0x30C2,0x20E3,0x5004,0x4025,0x7046,0x6067,
    0x83B9,0x9398,0xA3FB,0xB3DA,0xC33D,0xD31C,0xE37F,0xF35E,
    0x02B1,0x1290,0x22F3,0x32D2,0x4235,0x5214,0x6277,0x7256,
    0xB5EA,0xA5CB,0x95A8,0x8589,0xF56E,0xE54F,0xD52C,0xC50D,
    0x34E2,0x24C3,0x14A0,0x0481,0x7466,0x6447,0x5424,0x4405,
    0xA7DB,0xB7FA,0x8799,0x97B8,0xE75F,0xF77E,0xC71D,0xD73C,
    0x26D3,0x36F2,0x0691,0x16B0,0x6657,0x7676,0x4615,0x5634,
    0xD94C,0xC96D,0xF90E,0xE92F,0x99C8,0x89E9,0xB98A,0xA9AB,
    0x5844,0x4865,0x7806,0x6827,0x18C0,0x08E1,0x3882,0x28A3,
    0xCB7D,0xDB5C,0xEB3F,0xFB1E,0x8BF9,0x9BD8,0xABBB,0xBB9A,
    0x4A75,0x5A54,0x6A37,0x7A16,0x0AF1,0x1AD0,0x2AB3,0x3A92,
    0xFD2E,0xED0F,0xDD6C,0xCD4D,0xBDAA,0xAD8B,0x9DE8,0x8DC9,
    0x7C26,0x6C07,0x5C64,0x4C45,0x3CA2,0x2C83,0x1CE0,0x0CC1,
    0xEF1F,0xFF3E,0xCF5D,0xDF7C,0xAF9B,0xBFBA,0x8FD9,0x9FF8,
    0x6E17,0x7E36,0x4E55,0x5E74,0x2E93,0x3EB2,0x0ED1,0x1EF0
};

unsigned short CtiProtocolExpresscom::addCRC(unsigned short crc, unsigned char data)
{
    crc = crc^(data);
    crc = crctbl[(unsigned char)crc];
    return(crc);
}

void CtiProtocolExpresscom::calcCRC(vector< BYTE >::iterator data, unsigned char len)
{
    _CRC = 0;
    char byte[5];
    vector< BYTE >::iterator start = data;

    for( int i=0 ; i < len; i++, data++)
        _CRC = addCRC(_CRC,*data);

    data = start;
}

/*
    Input validation for Expresscom addresses (YUK-7224).
*/
bool CtiProtocolExpresscom::validateParseAddressing(const CtiCommandParser &parse)
{
    bool valid = true;
    unsigned int address;

    static const string serial_("serial");
    static const string xc_serial_("xc_serial");
    static const string xca_serial_target_("xca_serial_target");
    static const string serial_raw_("serial_raw_input");

    if(parse.isKeyValid(serial_))
    {
        address = parse.getiValue(serial_, 0);
        valid &= validateAddress(address, SerialMin, SerialMax);

        // The call to strtoul() that parsed the original command line entered serial number returns
        // UINT_MAX if it encounters an error.  Unfortunately UINT_MAX is a valid serial number, so we
        // convert the serial to a string and compare to what we originally entered.  This will catch
        // bad parameters that are outside the 32 bit space.
        // eg: 'putconfig xcom ... serial 61234567890 ...' etc.
        // NOTE: we also have to worry about serials entered in hexadecimal.

        if(address == UINT_MAX)
        {
            static const boost::regex   number_regexp( CtiString("((0x[0-9a-f]+)|([0-9]+))") );
            static const boost::regex   regexp( CtiString("serial[= ]+((0x[0-9a-f]+)|([0-9]+))") );

            char        buffer[64];
            CtiString   command = parse.getCommandStr();
            CtiString   token = command.match(regexp);
            CtiString   number_token = token.match(number_regexp);

            if(number_token.length() >= 2 && number_token[0] == '0' && number_token[1] == 'x')   // entered in hex...
            {
                // convert - zero pad to the proper length
                _snprintf(buffer, 64, "0x%0*x", (number_token.length() - 2), address);
            }
            else
            {
                _snprintf(buffer, 64, "%u", address);
            }

            valid &= ( number_token == CtiString(buffer) );
        }
    }

    if(parse.isKeyValid(xc_serial_))
    {
        address = parse.getiValue(xc_serial_, 0);
        valid &= validateAddress(address, SerialMin, SerialMax);
    }

    if(parse.isKeyValid(xca_serial_target_))
    {
        address = parse.getiValue(xca_serial_target_, 0);
        valid &= validateAddress(address, SerialMin, SerialMax);
    }

    static const string xc_spid_("xc_spid");
    static const string xca_spid_("xca_spid");
    static const string xca_spid_target_("xca_spid_target");

    if(parse.isKeyValid(xc_spid_))
    {
        address = parse.getiValue(xc_spid_, 0);
        valid &= validateAddress(address, SpidMin, SpidMax);
    }

    if(parse.isKeyValid(xca_spid_))
    {
        address = parse.getiValue(xca_spid_, 0);
        valid &= validateAddress(address, SpidMin, SpidMax);
    }

    if(parse.isKeyValid(xca_spid_target_))
    {
        address = parse.getiValue(xca_spid_target_, 0);
        valid &= validateAddress(address, SpidMin, SpidMax);
    }

    static const string xc_geo_("xc_geo");
    static const string xca_geo_("xca_geo");
    static const string xca_geo_target_("xca_geo_target");

    if(parse.isKeyValid(xc_geo_))
    {
        address = parse.getiValue(xc_geo_, 0);
        valid &= validateAddress(address, GeoMin, GeoMax);
    }

    if(parse.isKeyValid(xca_geo_))
    {
        address = parse.getiValue(xca_geo_, 0);
        valid &= validateAddress(address, GeoMin, GeoMax);
    }

    if(parse.isKeyValid(xca_geo_target_))
    {
        address = parse.getiValue(xca_geo_target_, 0);
        valid &= validateAddress(address, GeoMin, GeoMax);
    }

    static const string xc_sub_("xc_sub");
    static const string xca_sub_("xca_sub");
    static const string xca_sub_target_("xca_sub_target");

    if(parse.isKeyValid(xc_sub_))
    {
        address = parse.getiValue(xc_sub_, 0);
        valid &= validateAddress(address, SubstationMin, SubstationMax);
    }

    if(parse.isKeyValid(xca_sub_))
    {
        address = parse.getiValue(xca_sub_, 0);
        valid &= validateAddress(address, SubstationMin, SubstationMax);
    }

    if(parse.isKeyValid(xca_sub_target_))
    {
        address = parse.getiValue(xca_sub_target_, 0);
        valid &= validateAddress(address, SubstationMin, SubstationMax);
    }

    static const string xc_feeder_("xc_feeder");
    static const string xca_feeder_("xca_feeder");
    static const string xca_feeder_target_("xca_feeder_target");

    if(parse.isKeyValid(xc_feeder_))
    {
        address = parse.getiValue(xc_feeder_, 0);
        valid &= validateAddress(address, FeederMin, FeederMax);
    }

    if(parse.isKeyValid(xca_feeder_))
    {
        address = parse.getiValue(xca_feeder_, 0);
        valid &= validateAddress(address, FeederMin, FeederMax);
    }

    if(parse.isKeyValid(xca_feeder_target_))
    {
        address = parse.getiValue(xca_feeder_target_, 0);
        valid &= validateAddress(address, FeederMin, FeederMax);
    }

    static const string xc_zip_("xc_zip");
    static const string xca_zip_("xca_zip");
    static const string xca_zip_target_("xca_zip_target");

    if(parse.isKeyValid(xc_zip_))
    {
        address = parse.getiValue(xc_zip_, 0);
        valid &= validateAddress(address, ZipMin, ZipMax);
    }

    if(parse.isKeyValid(xca_zip_))
    {
        address = parse.getiValue(xca_zip_, 0);
        valid &= validateAddress(address, ZipMin, ZipMax);
    }

    if(parse.isKeyValid(xca_zip_target_))
    {
        address = parse.getiValue(xca_zip_target_, 0);
        valid &= validateAddress(address, ZipMin, ZipMax);
    }

    static const string xc_uda_("xc_uda");
    static const string xca_uda_("xca_uda");
    static const string xca_uda_target_("xca_uda_target");

    if(parse.isKeyValid(xc_uda_))
    {
        address = parse.getiValue(xc_uda_, 0);
        valid &= validateAddress(address, UserMin, UserMax);
    }

    if(parse.isKeyValid(xca_uda_))
    {
        address = parse.getiValue(xca_uda_, 0);
        valid &= validateAddress(address, UserMin, UserMax);
    }

    if(parse.isKeyValid(xca_uda_target_))
    {
        address = parse.getiValue(xca_uda_target_, 0);
        valid &= validateAddress(address, UserMin, UserMax);
    }

    static const string xc_program_("xc_program");
    static const string xca_program_("xca_program");
    static const string xca_program_target_("xca_program_target");

    if(parse.isKeyValid(xc_program_))
    {
        address = parse.getiValue(xc_program_, 0);
        valid &= validateAddress(address, ProgramMin, ProgramMax);
    }

    if(parse.isKeyValid("xcgenericaddress"))    // We have a single value in the parse numeric field
    {
        if(parse.isKeyValid(xca_program_))
        {
            address = parse.getiValue(xca_program_, 0);
            valid &= validateAddress(address, ProgramMin, ProgramMax);
        }
    }
    else if(parse.isKeyValid("xcaddress"))      // We have a (potentially comma seperated) string value.
    {
        string programStr  = parse.getsValue(xca_program_);

        boost::char_separator<char> sep(",");

        Boost_char_tokenizer programs( programStr, sep );
        Boost_char_tokenizer::iterator current_program = programs.begin();

        for( ; current_program != programs.end(); ++current_program )
        {
            if( !current_program->empty() )
            {
                address = atoi(current_program->c_str());
                valid &= validateAddress(address, ProgramMin, ProgramMax);
            }
        }
    }

    if(parse.isKeyValid(xca_program_target_))
    {
        address = parse.getiValue(xca_program_target_, 0);
        valid &= validateAddress(address, ProgramMin, ProgramMax);
    }

    static const string xc_splinter_("xc_splinter");
    static const string xca_splinter_("xca_splinter");
    static const string xca_splinter_target_("xca_splinter_target");

    if(parse.isKeyValid(xc_splinter_))
    {
        address = parse.getiValue(xc_splinter_, 0);
        valid &= validateAddress(address, SplinterMin, SplinterMax);
    }

    if(parse.isKeyValid("xcgenericaddress"))    // We have a single value in the parse numeric field
    {
        if(parse.isKeyValid(xca_splinter_))
        {
            address = parse.getiValue(xca_splinter_, 0);
            valid &= validateAddress(address, SplinterMin, SplinterMax);
        }
    }
    else if(parse.isKeyValid("xcaddress"))      // We have a (potentially comma seperated) string value.
    {
        string splinterStr  = parse.getsValue(xca_splinter_);

        boost::char_separator<char> sep(",");

        Boost_char_tokenizer splinters( splinterStr, sep );
        Boost_char_tokenizer::iterator current_splinter = splinters.begin();

        for( ; current_splinter != splinters.end(); ++current_splinter )
        {
            if( !current_splinter->empty() )
            {
                address = atoi(current_splinter->c_str());
                valid &= validateAddress(address, SplinterMin, SplinterMax);
            }
        }
    }

    if(parse.isKeyValid(xca_splinter_target_))
    {
        address = parse.getiValue(xca_splinter_target_, 0);
        valid &= validateAddress(address, SplinterMin, SplinterMax);
    }

    return valid;
}

