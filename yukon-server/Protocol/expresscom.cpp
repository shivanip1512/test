
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   expresscom
*
* Date:   8/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/08/16 14:38:37 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include "expresscom.h"
#include "logger.h"
#include "yukon.h"

CtiProtocolExpresscom::CtiProtocolExpresscom()
{}

CtiProtocolExpresscom::CtiProtocolExpresscom(const CtiProtocolExpresscom& aRef)
{
    *this = aRef;
}

CtiProtocolExpresscom::~CtiProtocolExpresscom()
{}

CtiProtocolExpresscom& CtiProtocolExpresscom::operator=(const CtiProtocolExpresscom& aRef)
{
    if(this != &aRef)
    {
    }
    return *this;
}


void CtiProtocolExpresscom::terminateMessage()
{
    _message.push_back(_messageStartCRC ? 'V' : 'T');
    return;
}

void CtiProtocolExpresscom::addressMessage()
{
    bool status = true;

    // Let us assume that we MUST always be called first!
    _message.clear();

    _message.push_back(_messageStartCRC ? 'U' : 'S');  // Terminate the message with this! message.push_back(_messageStartCRC ? 'V' : 'T');
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
    return status;
}

INT CtiProtocolExpresscom::timeSync(RWTime &local)
{
    INT status = NoError;

    RWTime gmt( local.hourGMT(), local.minuteGMT(), local.second(), RWZone::utc() );
    RWDate date( gmt, RWZone::utc() );

    BYTE dayOfWeek = date.weekDay() % 7;    // RWDate Monday = 1, Sunday = 7.  Protocol Sun = 0 - Sat = 6.

    _message.push_back( mtTimeSync );
    _message.push_back( 0x80 | (gmt.isDST() ? 0x40 : 0x00) | (dayOfWeek & 0x07) );
    _message.push_back( gmt.hourGMT() );
    _message.push_back( gmt.minuteGMT() );
    _message.push_back( gmt.second() );
    _message.push_back( date.month() );
    _message.push_back( date.dayOfMonth() );
    _message.push_back( HIBYTE(LOWORD(date.year())) );
    _message.push_back( LOBYTE(LOWORD(date.year())) );

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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    return status;
}

INT CtiProtocolExpresscom::timedLoadControl(BYTE load, UINT shedtime_seconds, BYTE randin, BYTE randout, USHORT delay )
{
    INT status = NoError;
    BYTE flag = (load & 0x0f);                  // Pick up the load designator;
    BYTE shedtime = 0;

    _message.push_back( mtTimedLoadControl );

    size_t flagpos = _message.size();         // This is where the index is.
    _message.push_back( 0x00 );                 // Hold a slot for the flags.

    shedtime_seconds /= 60;                     // This is now in integer minutes
    if(shedtime_seconds > 255)
    {
        flag |= 0x20;                           // Control time is in hours!
        shedtime = shedtime_seconds / 60;       // This is now in integer hours
    }
    else
    {
        shedtime = shedtime_seconds;
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

    return status;
}


INT CtiProtocolExpresscom::restoreLoadControl(BYTE load, BYTE rand, USHORT delay )
{
    INT status = NoError;
    BYTE flag = (load & 0x0f);                  // Pick up the load designator;

    _message.push_back( mtRestoreLoadControl );
    size_t flagpos = _message.size();
    _message.push_back( flag );

    if(rand != 0)
    {
        flag |= 0x80;
        _message.push_back( rand );
    }
    if(delay != 0)
    {
        flag |= 0x10;
        _message.push_back( HIBYTE(delay) );
        _message.push_back( LOBYTE(delay) );
    }

    // Set the flags to their final answer.
    _message[ flagpos ] = flag;

    return status;
}


INT CtiProtocolExpresscom::cycleLoadControl(BYTE load, BYTE cyclepercent, BYTE period_minutes, BYTE cyclecount, USHORT delay, bool preventrampin, bool allowTrueCycle)
{
    INT status = NoError;
    BYTE flag = (load & 0x0f);                  // Pick up the load designator;

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

    return status;
}


INT CtiProtocolExpresscom::thermostatLoadControl(BYTE load, BYTE cyclepercent, BYTE periodminutes, BYTE cyclecount, USHORT delay, INT controltemperature, BYTE limittemperature, BYTE limitfallbackpercent, CHAR maxdeltaperhour, BYTE deltafallbackpercent)
{
    INT status = NoError;

    BYTE flaghi = (_heatingMode ? 0x10 : 0x00) | (_coolingMode ? 0x08 : 0x00) | (_celsiusMode ? 0x04 : 0x00);
    BYTE flaglo = (load & 0x0f);

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
INT CtiProtocolExpresscom::thermostatSetpointControl(BYTE minTemp, BYTE maxTemp, USHORT T_r, USHORT T_a, USHORT T_b, BYTE delta_S_b, USHORT T_c, USHORT T_d, BYTE delta_S_d, USHORT T_e, USHORT T_f, BYTE delta_S_f)
{
    INT status = NoError;

    BYTE flaghi = 0x00;
    BYTE flaglo = 0x00;

    bool twobytetimes = false;

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
    flaghi |= ( _coolingMode ? 0x02 : 0x00);

    if(minTemp != 0)
    {
        flaghi |= 0x04;
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

    return status;
}


INT CtiProtocolExpresscom::configuration(BYTE configNumber, BYTE length, PBYTE data)
{
    INT status = NoError;
    _message.push_back( mtConfiguration );
    _message.push_back( configNumber );
    _message.push_back( length );

    for(int i = 0; i < length; i++)
    {
        _message.push_back( data[i] );
    }

    return status;
}


INT CtiProtocolExpresscom::maintenence(BYTE function, BYTE opt1, BYTE opt2, BYTE opt3, BYTE opt4)
{
    INT status = NoError;

    _message.push_back( mtMaintenence );
    _message.push_back( function );
    _message.push_back( opt1 );
    _message.push_back( opt2 );
    _message.push_back( opt3 );
    _message.push_back( opt4 );

    return status;
}


INT CtiProtocolExpresscom::service(BYTE load, bool activate)
{
    INT status = NoError;

    _message.push_back( mtService );
    _message.push_back( (activate ? 0x80 : 0x00 ) | (load & 0x0f) );

    return status;
}


INT CtiProtocolExpresscom::temporaryService(USHORT hoursout, bool cancel, bool deactiveColdLoad, bool deactiveLights)
{
    INT status = NoError;
    BYTE flags = (cancel ? 0x00 : 0x80) | (deactiveColdLoad ? 0x02 : 0x00) | (deactiveLights ? 0x01 : 0x00);

    _message.push_back( mtTemporaryService );
    _message.push_back( flags );

    if(flags & 0x80)    // This is an OOS.  include the time fields
    {
        _message.push_back( HIBYTE(hoursout) );
        _message.push_back( LOBYTE(hoursout) );
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return status;
}


INT CtiProtocolExpresscom::data(PBYTE data, BYTE length, BYTE dataTransmitType, BYTE targetPort)
{
    INT status = NoError;

    _message.push_back( mtData );
    _message.push_back( (dataTransmitType & 0xf0) | (targetPort & 0x0f) );
    _message.push_back( length );

    for(int i = 0; i < length; i++)
    {
        _message.push_back( data[i] );
    }

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
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
            break;
        }
    }

    return status;
}



