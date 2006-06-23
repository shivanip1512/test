/*-----------------------------------------------------------------------------*
*
* File:   expresscom
*
* Date:   8/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.34 $
* DATE         :  $Date: 2006/06/23 19:30:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "expresscom.h"
#include "logger.h"
#include <boost/tokenizer.hpp>
#include "numstr.h"
#include "cparms.h"
#include "cmdparse.h"
#include "ctidate.h"


#include <rw\ctoken.h>
#include <rw\re.h>



using namespace std;


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

    _uniqueAddress      = parse.getiValue("xc_serial", 0);   //( serial != -1 ? serial : 0 );
    _spidAddress        = parse.getiValue("xc_spid", 0);     //( spid != -1 ? spid : 0);
    _geoAddress         = parse.getiValue("xc_geo", 0);      //( geo != -1 ? geo : 0);
    _substationAddress  = parse.getiValue("xc_sub", 0);      //( substation != -1 ? substation : 0);
    _feederAddress      = parse.getiValue("xc_feeder", 0);   //( feeder != -1 ? feeder : 0);
    _zipAddress         = parse.getiValue("xc_zip", 0);      //( zip != -1 ? zip : 0);
    _udaAddress         = parse.getiValue("xc_uda", 0);      //( uda != -1 ? uda : 0);
    _programAddress     = parse.getiValue("xc_program", 0);  //( program != -1 ? program : 0);
    _splinterAddress    = parse.getiValue("xc_splinter", 0); //( splinter != -1 ? splinter : 0);

    resolveAddressLevel();

    return status;
}


void CtiProtocolExpresscom::addressMessage()
{
    bool status = true;

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

INT CtiProtocolExpresscom::timeSync(CtiTime &local, bool fullsync)
{
    INT status = NoError;

    CtiTime gmt = local.toUTCtime();
    CtiDate date( gmt );

    BYTE dayOfWeek = date.weekDay() % 7;    // CtiDate Monday = 1, Sunday = 7.  Protocol Sun = 0 - Sat = 6.

    gmt = gmt + ((unsigned long)gConfigParms.getValueAsInt("PORTER_PAGING_DELAY", 0));

    _message.push_back( mtTimeSync );
    _message.push_back( (fullsync ? 0x80 : 0x00) | (gmt.isDST() ? 0x40 : 0x00) | (dayOfWeek & 0x07) );
    _message.push_back( gmt.hourGMT() );
    _message.push_back( gmt.minuteGMT() );
    _message.push_back( gmt.second() );

    if(fullsync)
    {
        _message.push_back( date.month() );
        _message.push_back( date.dayOfMonth() );
        _message.push_back( HIBYTE(LOWORD(date.year())) );
        _message.push_back( LOBYTE(LOWORD(date.year())) );
    }

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


INT CtiProtocolExpresscom::restoreLoadControl(UINT loadMask, BYTE rand, USHORT delay )
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

            //Check fot kwhvalue!=0
            _message.push_back( kwhValue );

            //Check if this is too high
            int bytes = parse.getiValue("bytes_to_follow", 0);
            if( bytes > ((period_minutes*cyclecount+59)/60)) //1h1m = 2 allowed, 1h = 1allowed
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Invalid number of bytes: " << bytes << " bytes, " << cyclecount << " cycles, " << period_minutes << " minutes "  << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

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
INT CtiProtocolExpresscom::thermostatSetpointControl(BYTE minTemp, BYTE maxTemp, USHORT T_r, USHORT T_a, USHORT T_b, BYTE delta_S_b, USHORT T_c, USHORT T_d, BYTE delta_S_d, USHORT T_e, USHORT T_f, BYTE delta_S_f, bool hold)
{
    INT status = NoError;

    BYTE flaghi = 0x00;
    BYTE flaglo = (hold ? 0x80 : 0x00);

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

    incrementMessageCount();
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

    incrementMessageCount();
    return status;
}

INT CtiProtocolExpresscom::rawconfiguration(string str)
{
    int i = 0;
    BYTE configNumber;
    BYTE raw[256];

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    if(!(tempStr = *beg).empty())
    {
        configNumber = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    while( !(tempStr = *(++beg)).empty() && i < 256 )
    {
        raw[i++] = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    return configuration(configNumber, i, raw);
}

INT CtiProtocolExpresscom::rawmaintenance(string str)
{
    int i = 0;
    BYTE function;
    BYTE raw[5] = { 0, 0, 0, 0, 0};

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    if(!(tempStr = *beg).empty())
    {
        function = (BYTE)strtol(tempStr.c_str(), &p, 16);
    }

    while( !(tempStr = *(++beg)).empty() && i < 4 )
    {
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


INT CtiProtocolExpresscom::data(string str)
{
    int i = 0;
    BYTE raw[256];

    CHAR *p;
    boost::tokenizer<> cmdtok(str);
    boost::tokenizer<>::iterator beg=cmdtok.begin();
    string tempStr;

    while( !(tempStr = *(++beg)).empty() && i < 256 )
    {
        raw[i++] = (BYTE)strtol(tempStr.c_str(), &p, 16);
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

    // Set any concatenated messages' priority!
    if(parse.isKeyValid("xcpriority"))
    {
        status = priority((BYTE)parse.getiValue("xcpriority"));
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
            status = assemblePutConfig( parse, OutMessage );
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
                dout << CtiTime() << " Unsupported command on expresscom route Command = " << parse.getCommand() << endl;
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
    _addressLength = atIndividualLen+1;

    if(_uniqueAddress == 0)
    {
        _addressLength = 1;
        if(_spidAddress != 0)           {_addressLevel |= atSpid;       _addressLength += atSpidLen;      }
        if(_geoAddress != 0)            {_addressLevel |= atGeo;        _addressLength += atGeoLen;       }
        if(_substationAddress != 0)     {_addressLevel |= atSubstation; _addressLength += atSubstationLen;}
        if(_feederAddress != 0)         {_addressLevel |= atFeeder;     _addressLength += atFeederLen;    }
        if(_zipAddress != 0)            {_addressLevel |= atZip;        _addressLength += atZipLen;       }
        if(_udaAddress != 0)            {_addressLevel |= atUser;       _addressLength += atUserLen;      }
        if(_programAddress != 0)        {_addressLevel |= atProgram;    _addressLength += atProgramLen;   }
        if(_splinterAddress != 0)       {_addressLevel |= atSplinter;   _addressLength += atSplinterLen;  }
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
                                   hold);
    }
    else if(parse.isKeyValid("xcflip"))
    {
        status = capControl( ccControl, ccControlSwap );
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Unsupported expresscom command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}


INT CtiProtocolExpresscom::assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage)
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
                configureLoadAddressing(parse);
            }
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
        status = data(parse.getsValue("xcdata"));
    }

    if(parse.isKeyValid("xcschedule"))
    {
        status = parseSchedule(parse);
    }

    if(parse.isKeyValid("xcsetstate"))
    {
        BYTE fanstate = (BYTE)parse.getiValue("xcfanstate", 0);
        BYTE sysstate = (BYTE)parse.getiValue("xcsysstate", 0);;
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        bool restore = parse.isKeyValid("xcrunprog") ? true : false;
        bool temporary = parse.isKeyValid("xcholdprog") ? false : true;

        thermostatSetState( relaymask,
                            temporary,
                            restore,
                            parse.getiValue("xctimeout", -1),
                            parse.getiValue("xcsettemp", -1),
                            fanstate,
                            sysstate,
                            delay);
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
        BYTE data1 = parse.getiValue("cbc_data1", 0);
        BYTE data2 = parse.getiValue("cbc_data2", 0);

        status = capControl( action, subaction, data1, data2 );
  }

    return status;
}

INT CtiProtocolExpresscom::parseSchedule(CtiCommandParser &parse)
{
    INT status = NoError;

    BYTE dow;
    BYTE pod;

    vector< BYTE > schedule(1024);

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

        for(int i = 0; i < schedule.size(); i++)
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
    INT status = NoError;

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
            int zip = (USHORT)parse.getiValue("xca_zip", 0);
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

INT CtiProtocolExpresscom::configureLoadAddressing(CtiCommandParser &parse)
{
    INT status = NoError;

    BYTE length = 1;
    BYTE raw[8];

    string progStr       = parse.getsValue("xca_program");
    string splinterStr   = parse.getsValue("xca_splinter");
    string tStr;

    int prog       = -1;
    int splinter   = -1;
    BYTE loadmask   = ((BYTE)parse.getiValue("xca_loadmask", 0) & 0x0f);
    BYTE load;


    boost::char_separator<char> sep(",");
    Boost_char_tokenizer ptok(progStr, sep);
    Boost_char_tokenizer::iterator pbeg=ptok.begin();


    Boost_char_tokenizer rtok(splinterStr, sep);
    Boost_char_tokenizer::iterator rbeg=rtok.begin();


    for(load = 0; load < 15; load++)
    {
        if(loadmask & (0x01 << load))
        {
            prog        = ( !(tStr = *pbeg).empty() ? atoi(tStr.c_str()) : prog);           // The last program address in the comma delimited string will PAD out alll loads.  If there is only one, it is assigned to all loads.
            splinter    = ( !(tStr = *rbeg).empty() ? atoi(tStr.c_str()) : splinter);       // The last splinter address in the comma delimited string will PAD out alll loads.  If there is only one, it is assigned to all loads.

            length = 1;
            raw[0] = (prog >= 0 ? 0x20 : 0x00) | (splinter >= 0 ? 0x10 : 0x00) | (load+1);

            if((raw[0] & 0x30) != 0)
            {
                if(prog >= 0)
                {
                    raw[length++] = (BYTE)prog;
                }
                if(splinter >= 0)
                {
                    raw[length++] = (BYTE)splinter;
                }
                status = configuration( 0x07, length, raw );
            }
        }
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

BYTE CtiProtocolExpresscom::getByte(int pos, int messageNum)//1 based number
{
    try
    {
        if(pos<_addressLength || (messageNum == 1 && pos<_lengths.at(0)))
        {
            return _message[pos];
        }
        else if(_lengths.size()>=messageNum && messageNum>1 && messageNum<=_messageCount && pos>0 && pos<(_addressLength + _lengths.at(messageNum-1) - _lengths.at(messageNum-2)))
        {
            return _message[_lengths.at(messageNum-2) + pos - _addressLength];
        }
        else
        {
            return 0;
        }
    }
    catch(...)//_lengths.at() can throw
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return 0;
    }
}

int CtiProtocolExpresscom::messageSize(int messageNum)
{
    try
    {

        if(messageNum == 1)
        {
            return _lengths.at(0);
        }
        else if(messageNum>1 && messageNum<=_messageCount && _lengths.size()>=messageNum)
        {
            return _addressLength + _lengths.at(messageNum-1) - _lengths.at(messageNum-2);
        }
        else
        {
            return 0;
        }
    }
    catch(...)//_lengths.at() can throw
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return 0;
    }
}
