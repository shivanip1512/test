

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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/10/23 21:06:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\ctoken.h>
#include <rw\re.h>
#include <rw/rwtime.h>

#include "expresscom.h"
#include "logger.h"
#include "numstr.h"
#include "yukon.h"

CtiProtocolExpresscom::CtiProtocolExpresscom() :
_useProtocolCRC(false),
_addressLevel(0),
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
{}

CtiProtocolExpresscom& CtiProtocolExpresscom::operator=(const CtiProtocolExpresscom& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

void CtiProtocolExpresscom::addAddressing( UINT    serial,
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
    resolveAddressLevel();

    return;
}

bool CtiProtocolExpresscom::parseAddressing(CtiCommandParser &parse)
{
    bool status = false;

    INT serial      = parse.getiValue("xc_serial");
    INT spid        = parse.getiValue("xc_spid");
    INT geo         = parse.getiValue("xc_geo");
    INT substation  = parse.getiValue("xc_sub");
    INT feeder      = parse.getiValue("xc_feeder");
    INT zip         = parse.getiValue("xc_zip");
    INT uda         = parse.getiValue("xc_uda");
    INT program     = parse.getiValue("xc_program");
    INT splinter    = parse.getiValue("xc_splinter");

    _uniqueAddress      = ( serial != -1 ? serial : 0 );
    _spidAddress        = ( spid != -1 ? spid : 0);
    _geoAddress         = ( geo != -1 ? geo : 0);
    _substationAddress  = ( substation != -1 ? substation : 0);
    _feederAddress      = ( feeder != -1 ? feeder : 0);
    _zipAddress         = ( zip != -1 ? zip : 0);
    _udaAddress         = ( uda != -1 ? uda : 0);
    _programAddress     = ( program != -1 ? program : 0);
    _splinterAddress    = ( splinter != -1 ? splinter : 0);

    resolveAddressLevel();

    return status;
}


void CtiProtocolExpresscom::addressMessage()
{
    bool status = true;

    // Let us assume that we MUST always be called first!
    _message.clear();

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
    _messageCount++;
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

    _messageCount++;
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

    _messageCount++;
    return status;
}

INT CtiProtocolExpresscom::timedLoadControl(UINT loadMask, UINT shedtime_seconds, BYTE randin, BYTE randout, USHORT delay )
{
    INT status = NoError;
    BYTE flag;
    BYTE shedtime = 0;
    BYTE load;

    for(load = 1; load < 15; load++)
    {
        UINT shedtime_minutes = shedtime_seconds / 60;  // This is now in integer minutes

        if(loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
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

            _messageCount++;
        }
    }

    return status;
}


INT CtiProtocolExpresscom::restoreLoadControl(UINT loadMask, BYTE rand, USHORT delay )
{
    INT status = NoError;
    BYTE flag;
    BYTE load;

    for(load = 1; load < 15; load++)
    {
        if(loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flag = (load & 0x0f);                  // Pick up the load designator;
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

            _messageCount++;
        }
    }

    return status;
}


INT CtiProtocolExpresscom::cycleLoadControl(UINT loadMask, BYTE cyclepercent, BYTE period_minutes, BYTE cyclecount, USHORT delay, bool preventrampin, bool allowTrueCycle)
{
    INT status = NoError;
    BYTE flag;
    BYTE load;

    for(load = 1; load < 15; load++)
    {
        if(loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
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

            _messageCount++;
        }
    }

    return status;
}


INT CtiProtocolExpresscom::thermostatLoadControl(UINT loadMask, BYTE cyclepercent, BYTE periodminutes, BYTE cyclecount, USHORT delay, INT controltemperature, BYTE limittemperature, BYTE limitfallbackpercent, CHAR maxdeltaperhour, BYTE deltafallbackpercent)
{
    INT status = NoError;

    BYTE flaghi = (_heatingMode ? 0x10 : 0x00) | (_coolingMode ? 0x08 : 0x00) | (_celsiusMode ? 0x04 : 0x00);
    BYTE flaglo;
    BYTE load;

    for(load = 1; load < 15; load++)
    {
        if(loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            flaglo = (load & 0x0f);                  // Pick up the load designator;
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

            _messageCount++;
        }
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
    flaghi |= ( _coolingMode ? 0x01 : 0x00);

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

    _messageCount++;
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

    _messageCount++;
    return status;
}

INT CtiProtocolExpresscom::rawconfiguration(RWCString str)
{
    int i = 0;
    BYTE configNumber;
    BYTE raw[256];

    CHAR *p;
    RWCTokenizer cmdtok(str);
    RWCString tempStr;

    if(!(tempStr = cmdtok()).isNull())
    {
        configNumber = (BYTE)strtol(tempStr.data(), &p, 16);
    }

    while( !(tempStr = cmdtok()).isNull() && i < 256 )
    {
        raw[i++] = (BYTE)strtol(tempStr.data(), &p, 16);
    }

    return configuration(configNumber, i, raw);
}

INT CtiProtocolExpresscom::rawmaintenance(RWCString str)
{
    int i = 0;
    BYTE function;
    BYTE raw[5] = { 0, 0, 0, 0, 0 };

    CHAR *p;
    RWCTokenizer cmdtok(str);
    RWCString tempStr;

    if(!(tempStr = cmdtok()).isNull())
    {
        function = (BYTE)strtol(tempStr.data(), &p, 16);
    }

    while( !(tempStr = cmdtok()).isNull() && i < 4 )
    {
        raw[i++] = (BYTE)strtol(tempStr.data(), &p, 16);
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

    _messageCount++;
    return status;
}


INT CtiProtocolExpresscom::service(BYTE action)
{
    INT status = NoError;
    BYTE load;

    _message.push_back( mtService );
    _message.push_back( action );

    _messageCount++;

    return status;
}

INT CtiProtocolExpresscom::service(UINT loadMask, bool activate)
{
    INT status = NoError;
    BYTE load;

    for(load = 1; load < 15; load++)
    {
        if(loadMask & (0x01 << (load - 1)))         // We have a message to be build up here!
        {
            _message.push_back( mtService );
            _message.push_back( (activate ? 0x80 : 0x00 ) | (load & 0x0f) );

            _messageCount++;
        }
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

    _messageCount++;
    return status;
}


INT CtiProtocolExpresscom::data(RWCString str)
{
    int i = 0;
    BYTE raw[256];

    CHAR *p;
    RWCTokenizer cmdtok(str);
    RWCString tempStr;

    while( !(tempStr = cmdtok()).isNull() && i < 256 )
    {
        raw[i++] = (BYTE)strtol(tempStr.data(), &p, 16);
    }

    return data(raw, i);
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

    _messageCount++;
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

    _messageCount++;
    return status;
}


INT CtiProtocolExpresscom::parseRequest(CtiCommandParser &parse, CtiOutMessage &OutMessage)
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

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl( parse, OutMessage );
            break;
        }
    case PutConfigRequest:
        {
            assemblePutConfig( parse, OutMessage );
            break;
        }
    case PutStatusRequest:
        {
            assemblePutStatus(parse, OutMessage);
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unsupported command on expresscom route Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    return status;
}


void CtiProtocolExpresscom::resolveAddressLevel()
{
    _addressLevel = atIndividual;

    if(_uniqueAddress == 0)
    {
        if(_spidAddress != 0)           _addressLevel |= atSpid;
        if(_geoAddress != 0)            _addressLevel |= atGeo;
        if(_substationAddress != 0)     _addressLevel |= atSubstation;
        if(_feederAddress != 0)         _addressLevel |= atFeeder;
        if(_zipAddress != 0)            _addressLevel |= atZip;
        if(_udaAddress != 0)            _addressLevel |= atUser;
        if(_programAddress != 0)        _addressLevel |= atProgram;
        if(_splinterAddress != 0)       _addressLevel |= atSplinter;
    }

    return;
}


void CtiProtocolExpresscom::dump() const
{
    if(_message.size() > 0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        char of = dout.fill('0');
        dout << " Message : ";

        for(int i = 0; i < _message.size(); i++)
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

INT CtiProtocolExpresscom::assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT  i;
    INT  status = NORMAL;
    UINT CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();
    INT  relaymask  = parse.getiValue("relaymask", 0);

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        // Add these two items to the list for control accounting!
        parse.setValue("control_interval", parse.getiValue("shed"));
        parse.setValue("control_reduction", 100 );

        INT rand  = parse.getiValue("shed_rand", 0);
        INT delay = parse.getiValue("shed_delay", 0);

        timedLoadControl(relaymask, parse.getiValue("shed"), rand, delay);
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE && !parse.isKeyValid("xctstat"))
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);
        INT delay      = parse.getiValue("xcdelaytime", 0);

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);

        cycleLoadControl(relaymask, parse.getiValue("cycle", 0), period, repeat, delay);
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        INT rand  = parse.getiValue("xcrandstart", 0);
        INT delay = parse.getiValue("xcdelaytime", 0);
        restoreLoadControl(relaymask, rand, delay);
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("xcdelaytime", 0);
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
    else if(parse.isKeyValid("xctstat"))
    {
        // Thermostat controls apply here...
        if(CtlReq == CMD_FLAG_CTL_CYCLE)        // tstat cycle
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_reduction", parse.getiValue("cycle") );
            parse.setValue("control_interval", 60 * parse.getiValue("cycle_period", 30) * parse.getiValue("cycle_count", 8));

            thermostatLoadControl( relaymask,
                                   parse.getiValue("cycle", 0),
                                   parse.getiValue("cycle_period", 30),
                                   parse.getiValue("cycle_count", 8),
                                   parse.getiValue("xcdelaytime", 0),
                                   parse.getiValue("xcctrltemp", 0),
                                   parse.getiValue("xclimittemp", 0),
                                   parse.getiValue("xclimitfbp", 0),
                                   parse.getiValue("xcmaxdperh", 0),
                                   parse.getiValue("xcmaxdperhfbp", 0));
        }
        else // tstat setpoint!  holy cow, lets dance.
        {
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
                                       parse.getiValue("xcdsf", 0));
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported expresscom command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}


INT CtiProtocolExpresscom::assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;

    if(parse.isKeyValid("xcsync"))
    {
        status = sync();
    }

    if(parse.isKeyValid("xctimesync"))
    {
        status = timeSync( RWTime() );
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
        status = data(parse.getsValue("xcdata"));
    }

    if(parse.isKeyValid("xcpservice"))
    {
        status = service((BYTE)parse.getiValue("xcpservice"));
    }
    else if(parse.isKeyValid("xctservicecancel"))
    {
        status = temporaryService( (USHORT)parse.getiValue("xctservicetime"),
                                   (bool)parse.getiValue("xctservicecancel"),
                                   (bool)parse.getiValue("xctservicebitp"),
                                   (bool)parse.getiValue("xctservicebitl") );
    }

    return status;
}

INT CtiProtocolExpresscom::assemblePutStatus(CtiCommandParser &parse, CtiOutMessage &OutMessage)
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

        status = capControl( action, subaction );
    }

    return status;
}
