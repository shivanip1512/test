

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/10/08 20:14:14 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include "expresscom.h"
#include "logger.h"
#include "yukon.h"

CtiProtocolExpresscom::CtiProtocolExpresscom() :
_useProtocolCRC(false),
_loadNumber(0xFF),                 // This may one day bite me...
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
_heatingMode(true),                // Default to true/yes;
_coolingMode(true),                // Default to true/yes;
_absoluteTemps(false)              // Default to false/no. (Implies delta)
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
                                           BYTE    splinter,
                                           BYTE    load)
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

    if(load > 15)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Controlled load number is greater than 15.  The protocol cannot support this." << endl;
        }
    }

    _loadNumber         = load & 0x0f;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
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

    INT load = parse.getiValue("relaymask");

    if(load > 15)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Controlled load number is greater than 15.  The protocol cannot support this." << endl;
        }

        status = true;
    }

    _loadNumber         = load & 0x0f;

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

    _messageCount++;
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

    _messageCount++;
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

    _messageCount++;
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

    _messageCount++;
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


INT CtiProtocolExpresscom::maintenence(BYTE function, BYTE opt1, BYTE opt2, BYTE opt3, BYTE opt4)
{
    INT status = NoError;

    _message.push_back( mtMaintenence );
    _message.push_back( function );
    _message.push_back( opt1 );
    _message.push_back( opt2 );
    _message.push_back( opt3 );
    _message.push_back( opt4 );

    _messageCount++;
    return status;
}


INT CtiProtocolExpresscom::service(BYTE load, bool activate)
{
    INT status = NoError;

    _message.push_back( mtService );
    _message.push_back( (activate ? 0x80 : 0x00 ) | (load & 0x0f) );

    _messageCount++;
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
    _messageCount++;
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
    INT            status = NORMAL;


    addressMessage();

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl( parse, OutMessage );
            break;
        }
    case PutConfigRequest:
    case PutStatusRequest:
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
    INT  relay  = parse.getiValue("relaymask");

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        UINT hasrand  = parse.isKeyValid("shed_rand");
        UINT hasdelay = parse.isKeyValid("shed_delay");

        relay = (relay  == INT_MIN) ? 0  : relay ;     // If zero it means all of them!

        // Add these two items to the list for control accounting!
        parse.Map()["control_interval"]  = CtiParseValue( parse.getiValue("shed") );
        parse.Map()["control_reduction"] = CtiParseValue( 100 );

        if( !(hasrand || hasdelay) && !(relay & 0xfffffff8) )     // Positional relays...
        {
            // Control time is in the parsers iValue!
            timedLoadControl(relay, parse.getiValue("shed"));
        }
        else
        {
            INT rand  = parse.getiValue("shed_rand");
            INT delay = parse.getiValue("shed_delay");

            // If not specified, uses the last sent data.  Acts as a modification.
            rand   = (rand == INT_MIN) ? 120  : rand;       // If not specified, it will continue the command in progress, modifying the other parameters
            delay  = (delay  == INT_MIN) ? 0  : delay ;     // If not specified, it will continue the command in progress, modifying the other parameters

            if(relay != 0)
            {
                for( i = 0; i < 15; i++ )
                {
                    if( relay & (0x01 << i) )
                    {
                        timedLoadControl(parse.getiValue("shed"), (i+1), rand, delay);
                    }
                }
            }
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE)
    {
        // Add these two items to the list for control accounting!
        parse.Map()["control_reduction"] = CtiParseValue( parse.getiValue("cycle") );

        INT period     = parse.getiValue("cycle_period");
        INT repeat     = parse.getiValue("cycle_count");
        INT delay      = parse.getiValue("cycle_delay");

        // If not specified, uses the last sent data.  Acts as a modification.
        relay  = (relay  == INT_MIN) ? 0  : relay ;     // If zero it means all of them!
        period = (period == INT_MIN) ? 30 : period;     // If not specified, it will continue the command in progress, modifying the other parameters
        repeat = (repeat == INT_MIN) ? 8  : repeat;     // If not specified, it will continue the command in progress, modifying the other parameters
        delay  = (delay  == INT_MIN) ? 0  : delay ;     // If not specified, it will continue the command in progress, modifying the other parameters

        parse.Map()["control_interval"]  = CtiParseValue( 60 * period * repeat );

        if(relay != 0)
        {
            for( i = 0; i < 7; i++ )
            {
                if( relay & (0x01 << i) )
                {
                    cycleLoadControl((i+1), parse.getiValue("cycle"), period, repeat, delay);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE || CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        restoreLoadControl(relay);
    }
    else if(CtlReq == CMD_FLAG_CTL_OPEN)
    {
        parse.Map()["control_reduction"] = CtiParseValue( 100 );

        capControl(ccControl, ccControlOpen);
    }
    else if(CtlReq == CMD_FLAG_CTL_CLOSE)
    {
        capControl(ccControl, ccControlClose);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported expresscom command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}
