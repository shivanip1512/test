
/*-----------------------------------------------------------------------------*
*
* File:   dev_gateway
*
* Date:   6/17/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/01/04 22:16:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <time.h>

#include <rw/re.h>
#undef mask_                // Stupid RogueWave!

#include <rw/rwdate.h>
#include <rw/rwtime.h>

#include "dev_gateway.h"
#include "logger.h"
#include "numstr.h"

HANDLE CtiDeviceGateway::hPostPorter = INVALID_HANDLE_VALUE;

void CtiDeviceGateway::postPorter()
{
    if( hPostPorter == INVALID_HANDLE_VALUE)
    {
        hPostPorter = OpenEvent(EVENT_ALL_ACCESS, FALSE, "CtiGWResultEvent");
    }

    if( hPostPorter != INVALID_HANDLE_VALUE)
    {
        SetEvent( hPostPorter );
    }
}

CtiDeviceGateway::CtiDeviceGateway(SOCKET msgsock) :
_ipaddr(0),
_socketConnected(false),
_msgsock(msgsock)
{
    for(int i = 0; i < 6; i++)
    {
        _mac[i] = 0;
    }
}

CtiDeviceGateway::CtiDeviceGateway(const CtiDeviceGateway& aRef) :
_ipaddr(0)
{
    for(int i = 0; i < 6; i++)
    {
        _mac[i] = 0;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    *this = aRef;
}

CtiDeviceGateway::~CtiDeviceGateway()
{
    interrupt(CtiThread::SHUTDOWN);

    shutdown(_msgsock, 0x02);
    closesocket(_msgsock);

    join();

    SMAP_t::iterator smitr;

    while(!_statMap.empty())
    {
        smitr = _statMap.begin();
        CtiDeviceGatewayStat *pGW = (*smitr).second;
        delete pGW;
        pGW = 0;
    }
}

CtiDeviceGateway& CtiDeviceGateway::operator=(const CtiDeviceGateway& aRef)
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

SOCKET CtiDeviceGateway::getSocket() const
{
    return _msgsock;
}

void CtiDeviceGateway::sendtm_Clock (BYTE hour, BYTE minute)
{
    TM_CLOCK tm_Clock;
    struct tm *newtime;
    RWTime now;
    RWDate today( now );


    RWCString gmt_str = today.asString("%Y/%m/%d ") + CtiNumStr(now.hour()).zpad(2) + ":" + CtiNumStr(now.minute()).zpad(2) + ":" + CtiNumStr(now.second()).zpad(2) + " GMT";

    tm_Clock.Type = htons (TYPE_TM_CLOCK);
    tm_Clock.tm_sec = now.second();
    tm_Clock.tm_min = now.minute();
    tm_Clock.tm_hour = now.hour();
    tm_Clock.tm_mday = today.dayOfMonth();
    tm_Clock.tm_mon = today.month();
    tm_Clock.tm_year = today.year() - 1900;
    tm_Clock.tm_wday = (today.weekDay() % 7);    // Put it in a tm form.

    if(now.isDST())
    {
        tm_Clock.tm_isdst = TRUE;
    }
    else
    {
        tm_Clock.tm_isdst = FALSE;
    }

    send (_msgsock, (char *)&tm_Clock, sizeof (TM_CLOCK), 0);
}

void CtiDeviceGateway::sendGMTClock (BYTE hour, BYTE minute)
{
    TM_CLOCK tm_Clock;
    struct tm *newtime;
    RWTime now;
    RWDate today( now, RWZone::utc() );


    RWCString gmt_str = today.asString("%Y/%m/%d ") + CtiNumStr(now.hourGMT()).zpad(2) + ":" + CtiNumStr(now.minuteGMT()).zpad(2) + ":" + CtiNumStr(now.second()).zpad(2) + " GMT";

    tm_Clock.Type = htons (TYPE_GMTTM_CLOCK);
    tm_Clock.tm_sec = now.second();
    tm_Clock.tm_min = now.minuteGMT();
    tm_Clock.tm_hour = now.hourGMT();
    tm_Clock.tm_mday = today.dayOfMonth();
    tm_Clock.tm_mon = today.month();
    tm_Clock.tm_year = today.year() - 1900;
    tm_Clock.tm_wday = (today.weekDay() % 7);    // Put it in a tm form.

    if(now.isDST())
    {
        tm_Clock.tm_isdst = TRUE;
    }
    else
    {
        tm_Clock.tm_isdst = FALSE;
    }

    send (_msgsock, (char *)&tm_Clock, sizeof (TM_CLOCK), 0);
}

int CtiDeviceGateway::processParse(CtiCommandParser &parse, CtiOutMessage *&OutMessage)
{
    int processed = 0;
    int serialnumber = parse.getiValue("serial", 0);
    SMAP_t::iterator smitr;

    if(_msgsock != INVALID_SOCKET)
    {
        if( (parse.getCommand() == PutConfigRequest) )
        {
            if(parse.getCommandStr().contains("timezone") &&
               (( serialnumber == 0 || _statMap.find( serialnumber ) != _statMap.end() )) )
            {
                RWCString CmdStr = parse.getCommandStr().match(" timezone [0-9]+[, ]+[yn]?[, ]+([0-9]+)?[, ]+[yn]?");
                RWCString tstr;
                RWCTokenizer tokens(CmdStr);

                USHORT zone = 0;    // Default to GMT
                USHORT minoffset = 0;    // Default to GMT
                BOOL dodst = FALSE;
                BOOL syncstats = TRUE;

                tokens(" \t\n\0");  // Hop the timezone string.

                if(!(tstr = tokens(", \t\n\0")).isNull())
                {
                    zone = atoi(tstr.data());
                }
                if(!(tstr = tokens(", \t\n\0")).isNull())
                {
                    dodst = (tstr.contains("y") ? TRUE : FALSE);
                }
                if(!(tstr = tokens(", \t\n\0")).isNull())
                {
                    minoffset = atoi(tstr.data());
                }
                if(!(tstr = tokens(", \t\n\0")).isNull())
                {
                    syncstats = (tstr.contains("y") ? TRUE : FALSE);
                }

                sendSetTimezone( zone, dodst, minoffset, syncstats );
                sendGMTClock();

                processed++;
            }
            else if( serialnumber == 0 || _statMap.find( serialnumber ) != _statMap.end() )
            {
                if(parse.getCommandStr().contains("timesync"))
                {
                    processed++;
                    sendtm_Clock(parse.getiValue("xctimesync_hour", -1), parse.getiValue("xctimesync_minute", -1));

                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Timesync sent to gateway hosting thermostat " << serialnumber << endl;
                }
            }
            if(parse.getiValue("timesync", 0))
            {
                if( serialnumber == 0 || _statMap.find( serialnumber ) != _statMap.end() )
                {
                    if(parse.getCommandStr().contains(" local") )
                    sendtm_Clock();
                    else
                        sendGMTClock();

                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Timesync sent to gateway hosting thermostat " << serialnumber << endl;
                }
            }
            else if(parse.getCommandStr().contains(" set bind on"))
            {
                sendSetBindMode(TRUE);
            }
            else if(parse.getCommandStr().contains(" set bind off"))
            {
                sendSetBindMode(FALSE);
            }
            else if(parse.getCommandStr().contains(" set ping on"))
            {
                sendSetPingMode(TRUE);
            }
            else if(parse.getCommandStr().contains(" set ping off"))
            {
                sendSetPingMode(FALSE);
            }
            else if(parse.getCommandStr().contains(" clear"))
            {
                sendSetBindMode(FALSE);
                sendSetPingMode(FALSE);
            }

            processed++;
        }


        // Pass them through to the individual thermostats.
        {
            if(serialnumber == 0)
            {
                for( smitr = _statMap.begin(); smitr != _statMap.end(); smitr++ )
                {
                    CtiDeviceGatewayStat *pGW = (*smitr).second;
                    pGW->processParse( _msgsock, parse, OutMessage );
                    pGW->printPacketData();

                    processed++;
                }
            }
            else
            {
                smitr = _statMap.find( serialnumber );
                if( smitr != _statMap.end() )
                {
                    CtiDeviceGatewayStat *pGW = (*smitr).second;
                    pGW->processParse( _msgsock, parse, OutMessage );

                    processed++;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Socket is broken" << endl;
    }

    return processed;
}

int CtiDeviceGateway::checkPendingOperations(  )
{
    int processed = 0;
    SMAP_t::iterator smitr;

    for( smitr = _statMap.begin(); smitr != _statMap.end(); smitr++ )
    {
        CtiDeviceGatewayStat *pGW = (*smitr).second;
        processed += pGW->checkPendingOperations();
    }

    return processed;
}


int CtiDeviceGateway::sendQueryRuntime(LONG dev, UCHAR Reset)
{
    int cnt = 0;
    SMAP_t::iterator smitr;

    if(dev != 0)
    {
        SMAP_t::iterator smitr = _statMap.find( dev );
        if( smitr != _statMap.end() )
        {
            cnt++;
            CtiDeviceGatewayStat *pGW = (*smitr).second;
            pGW->sendQueryRuntime(_msgsock, Reset);
        }
    }
    else
    {
        for( smitr = _statMap.begin(); smitr != _statMap.end(); smitr++ )
        {
            cnt++;
            CtiDeviceGatewayStat *pGW = (*smitr).second;
            pGW->sendQueryRuntime(_msgsock, Reset);
        }
    }

    return cnt;
}

int CtiDeviceGateway::sendGet(USHORT Type, LONG dev)
{
    int cnt = 0;

    if(dev == 0)
    {
        cnt = _statMap.size();      // This is our best guess at this point.  We might want to improve this in the future??
    }
    else
    {
        SMAP_t::iterator smitr = _statMap.find( dev );
        if( smitr != _statMap.end() )
        {
            // Device is on this GW!
            cnt = 1;
        }
    }

    if( dev == 0 || cnt > 0 )
    {
        GET Get;

        Get.Type = htons (Type);
        Get.DeviceID = htonl(dev);

        send (_msgsock, (char *)&Get, sizeof(GET), 0);
    }

    return cnt;
}


int CtiDeviceGateway::sendKeepAlive (void)
{
    int val = NORMAL;
    KEEPALIVE KeepAlive;

    KeepAlive.Type = htons (TYPE_KEEPALIVE);
    if( SOCKET_ERROR == send (_msgsock, (char *)&KeepAlive, sizeof (KEEPALIVE), 0) )
    {
        set(GW_CLEAN_ME);
        val = NOTNORMAL;
    }

    return val;
}


void CtiDeviceGateway::sendSetBindMode (UCHAR BindMode)
{
    SETBINDMODE SetBindMode;

    SetBindMode.Type = htons (TYPE_SETBINDMODE);
    SetBindMode.BindMode = BindMode;

    send (_msgsock, (char *)&SetBindMode, sizeof (SETBINDMODE), 0);
}


void CtiDeviceGateway::sendSetPingMode (UCHAR PingMode)
{
    SETPINGMODE SetPingMode;

    SetPingMode.Type = htons (TYPE_SETPINGMODE);
    SetPingMode.PingMode = PingMode;

    send (_msgsock, (char *)&SetPingMode, sizeof (SETPINGMODE), 0);
}

void CtiDeviceGateway::sendSetTimezone(ULONG minutesWestOfGreenwich, UCHAR doIt, USHORT DSTMinutesOffset, UCHAR syncStats)
{
    TIMEZONE SetTimezone;

    SetTimezone.Type = htons (TYPE_SETTIMEZONE);
    SetTimezone.Timezone.ZoneMinutesWestOfGreenwich = htonl( minutesWestOfGreenwich );
    SetTimezone.Timezone.ObserveDST = doIt;
    SetTimezone.Timezone.DSTMinutesOffset = htons(DSTMinutesOffset);
    SetTimezone.Timezone.StatsSyncTimeToGW = syncStats;


    send (_msgsock, (char *)&SetTimezone, sizeof(TIMEZONE), 0);
}


void CtiDeviceGateway::sendSetRSSIConfiguration (UCHAR AllMessages)
{
    SETRSSICONFIGURATION SetRSSIConfiguration;

    SetRSSIConfiguration.Type = htons (TYPE_SETRSSICONFIGURATION);
    SetRSSIConfiguration.AllMessages = AllMessages;

    send (_msgsock, (char *)&SetRSSIConfiguration, sizeof (SETRSSICONFIGURATION), 0);
}


void CtiDeviceGateway::sendSetNetworkID (USHORT NetworkID)
{
    SETNETWORKID SetNetworkID;

    SetNetworkID.Type = ntohs (TYPE_SETNETWORKID);
    SetNetworkID.NetworkID = ntohs (NetworkID);

    send (_msgsock, (char *)&SetNetworkID, sizeof (SETNETWORKID), 0);
}

int CtiDeviceGateway::getMessageLength(GATEWAYRXSTRUCT *GatewayRX)
{
    int Length = 0;

    // Based on the Type figure out how many more bytes we need
    switch(ntohs (GatewayRX->Type) )
    {
    case TYPE_ALLOWEDSYSTEMSWITCH:
        Length = sizeof (GatewayRX->U.AllowedSystemSwitch);
        break;

    case TYPE_BATTERY:
        Length = sizeof (GatewayRX->U.Battery);
        break;

    case TYPE_RUNTIME:
        Length = sizeof (GatewayRX->U.Clock);
        break;

    case TYPE_SETPOINTS_CH:
    case TYPE_SETPOINTS:
        Length = sizeof (GatewayRX->U.Setpoints);
        break;

    case TYPE_DEADBAND:
        Length = sizeof (GatewayRX->U.Deadband);
        break;

    case TYPE_DEVICEABSENT:
        Length = sizeof (GatewayRX->U.DeviceAbsent);
        break;

    case TYPE_DEVICETYPE:
        Length = sizeof (GatewayRX->U.DeviceType);
        break;

    case TYPE_DISPLAYEDTEMPERATURE:
        Length = sizeof (GatewayRX->U.DisplayedTemp);
        break;

    case TYPE_DLC_CH:
    case TYPE_DLC:
        Length = sizeof (GatewayRX->U.DLC);
        break;

    case TYPE_FANSWITCH_CH:
    case TYPE_FANSWITCH:
        Length = sizeof (GatewayRX->U.FanSwitch);
        break;

    case TYPE_FILTER_CH:
    case TYPE_FILTER:
        Length = sizeof (GatewayRX->U.Filter);
        break;

    case TYPE_HEATPUMPFAULT:
        Length = sizeof (GatewayRX->U.HeatPumpFault);
        break;

    case TYPE_SETPOINTLIMITS_CH:
    case TYPE_SETPOINTLIMITS:
        Length = sizeof (GatewayRX->U.SetpointLimits);
        break;

    case TYPE_OUTDOORTEMP:
        Length = sizeof (GatewayRX->U.OutdoorTemp);
        break;

    case TYPE_SCHEDULE_CH:
    case TYPE_SCHEDULE:
        Length = sizeof (GatewayRX->U.Schedule);
        break;

    case TYPE_SYSTEMSWITCH_CH:
    case TYPE_SYSTEMSWITCH:
        Length = sizeof (GatewayRX->U.SystemSwitch);
        break;

    case TYPE_UTILSETPOINT_CH:
    case TYPE_UTILSETPOINT:
        Length = sizeof (GatewayRX->U.UtilSetpoint);
        break;

    case TYPE_CLOCK:
        Length = sizeof (GatewayRX->U.Clock);
        break;

    case TYPE_CLOCKDST:
        Length = sizeof (GatewayRX->U.ClockDST);
        break;

    case TYPE_DEVICEBOUND:
        Length = sizeof (GatewayRX->U.DeviceBound);
        break;

    case TYPE_DEVICEUNBOUND:
        Length = sizeof (GatewayRX->U.DeviceUnbound);
        break;

    case TYPE_COMMSTATUS:
        Length = sizeof (GatewayRX->U.CommFaultStatus);
        break;

    case TYPE_BINDMODE:
        Length = sizeof (GatewayRX->U.BindMode);
        break;

    case TYPE_PINGMODE:
        Length = sizeof (GatewayRX->U.PingMode);
        break;

    case TYPE_ERROR:
        Length = sizeof (GatewayRX->U.ErrorReport);
        break;

    case TYPE_RSSI:
        Length = sizeof (GatewayRX->U.Rssi);
        break;

    case TYPE_RESET:
        Length = sizeof (GatewayRX->Reset);
        break;

    case TYPE_RETURNCODE:
        Length = sizeof (GatewayRX->Return);
        break;

    case TYPE_ADDRESSING:
        Length = sizeof(GatewayRX->Addressing);
        break;

    case TYPE_KEEPALIVE:
        Length = 0;
        break;

    default:
        Length = 0;
        break;

    }

    if(0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "   " << ntohs (GatewayRX->Type) << ": " << Length << " and " << sizeof(GWCOMMAND) << " and " << sizeof(RETURNCODEREPORT) << endl;
    }

    return Length;
}

void CtiDeviceGateway::run()
{
    int loops;
    int rc, Length;
    GATEWAYRXSTRUCT GatewayRX;

    u_long bytesavail;
    u_long nonblock= FALSE;
    ioctlsocket(_msgsock, FIONBIO, &nonblock);  // Make it into a blocking socket.

    try
    {
        while( !isSet(SHUTDOWN) && _msgsock != INVALID_SOCKET )
        {
            ioctlsocket(_msgsock, FIONREAD, &bytesavail);

            if(bytesavail < 2)
            {
                sleep(500);
                continue;
            }

            /* Wait for 2 bytes to be available so we can get the type in */
            rc = recv (_msgsock, (char *)&GatewayRX , 2,0);

            if(rc == SOCKET_ERROR)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " recv() failed with error " << WSAGetLastError() << endl;
                }
                closesocket (_msgsock);
                _msgsock = INVALID_SOCKET;
                _socketConnected = FALSE;
                break;
            }
            else if(rc == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Connection closed by client " << endl;
                }
                closesocket (_msgsock);
                _msgsock = INVALID_SOCKET;
                _socketConnected = FALSE;
                break;
            }
            else
            {
                Length = getMessageLength(&GatewayRX);
            }

            // Get the number of bytes based on the type

            if(Length)
            {
                loops = 0;
                do
                {
                    ioctlsocket(_msgsock, FIONREAD, &bytesavail);
                    if(bytesavail < Length) sleep(500);

                    if(!(++loops % 10))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  There are " << bytesavail << " bytes there " << endl;
                    }
                } while(bytesavail < Length && !isSet(SHUTDOWN) && loops < 30);

                if(isSet(SHUTDOWN))
                {
                    continue;
                }

                /* Wait for the rest of the bytes, or clean out the mess! */
                char *cp = ((char *)&GatewayRX) + 2;
                int getlen = (bytesavail < Length ? bytesavail : Length);
                int gotlen = 0;

                do
                {
                    rc = recv (_msgsock, cp + gotlen, getlen ,0);

                    if(rc != SOCKET_ERROR)
                    {
                        gotlen += rc;
                        getlen -= rc;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                while( rc != SOCKET_ERROR && getlen > 0 );

                if(gotlen != Length)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Only read " << rc << " Length " << Length << " bytes available " << bytesavail << endl;
                    }

                    continue;
                }

                if(rc == SOCKET_ERROR)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() <<  " recv() failed with error " << WSAGetLastError() << endl;
                    }
                    closesocket (_msgsock);
                    _msgsock = INVALID_SOCKET;
                    break;
                }
                else if(rc == 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() <<  " Connection closed by client" << endl;
                    }
                    closesocket (_msgsock);
                    _msgsock = INVALID_SOCKET;
                    break;
                }
            }

            // Now let any gwstat have at the message!
            ULONG did = getDeviceID(&GatewayRX);

            if(did)
            {
                CtiDeviceGatewayStat *pGW = 0;

                if( did )
                {
                    if(did != 0xFFFFFFFF)
                    {
                        SMAP_t::iterator smitr = _statMap.find( did );
                        if( smitr != _statMap.end() )
                        {
                            pGW = (*smitr).second;
                        }
                        else
                        {
                            pGW = new CtiDeviceGatewayStat(did);
                            _statMap.insert( SMAP_t::value_type(did, pGW) );
                            _statSN.push_back(did);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Stat " << pGW->getDeviceSerialNumber() << " New EnergyPro Stat Connected." << endl;
                            }

                            pGW->sendGet( _msgsock, TYPE_GETALL );  // Update the works!
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Gateway " << getMACAddress() << " / " << getIPAddress() <<" Message Type " << (int)ntohs(GatewayRX.Type) << endl;
                        }
                    }
                }

                if(rc == SOCKET_ERROR)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() <<  " recv() failed with error " << WSAGetLastError() << endl;
                    }
                    shutdown(_msgsock, 0x02);
                    closesocket (_msgsock);
                    break;
                }
                else if(rc == 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() <<  " Connection closed by client" << endl;
                    }
                    shutdown(_msgsock, 0x02);
                    closesocket (_msgsock);
                    break;
                }
                else
                {
                if(pGW)
                {
                    pGW->clearPrintList();
                    pGW->convertGatewayRXStruct(GatewayRX);
                    pGW->printPacketData();
                }
            }
            }
            else
            {
                // Look at the message as the gateway!
                processGatewayMessage(GatewayRX);
            }

            postPorter();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CtiDeviceGateway::run() is exiting." << endl;
    }

    set(GW_THREAD_TERMINATED);
}

ULONG CtiDeviceGateway::getDeviceID(GATEWAYRXSTRUCT *GatewayRX)
{
    ULONG did = 0;

    // Based on the Type figure out how many more bytes we need
    switch(ntohs(GatewayRX->Type))
    {
    case TYPE_ALLOWEDSYSTEMSWITCH:
    case TYPE_BATTERY:
    case TYPE_RSSI:
    case TYPE_RUNTIME:
    case TYPE_SETPOINTS_CH:
    case TYPE_SETPOINTS:
    case TYPE_DEADBAND:
    case TYPE_DEVICEABSENT:
    case TYPE_DEVICETYPE:
    case TYPE_DISPLAYEDTEMPERATURE:
    case TYPE_DLC_CH:
    case TYPE_DLC:
    case TYPE_FANSWITCH_CH:
    case TYPE_FANSWITCH:
    case TYPE_FILTER_CH:
    case TYPE_FILTER:
    case TYPE_HEATPUMPFAULT:
    case TYPE_SETPOINTLIMITS_CH:
    case TYPE_SETPOINTLIMITS:
    case TYPE_OUTDOORTEMP:
    case TYPE_SCHEDULE_CH:
    case TYPE_SCHEDULE:
    case TYPE_SYSTEMSWITCH_CH:
    case TYPE_SYSTEMSWITCH:
    case TYPE_UTILSETPOINT_CH:
    case TYPE_UTILSETPOINT:
    case TYPE_CLOCK:
    case TYPE_CLOCKDST:
    case TYPE_DEVICEBOUND:
    case TYPE_DEVICEUNBOUND:
        did = ntohl (GatewayRX->U.DLC.DeviceID);
        break;

    case TYPE_RETURNCODE:
        did = ntohl (GatewayRX->Return.DeviceID);
        break;

    default:
        did = 0;
        break;

    }

    return did;
}

const CtiDeviceGateway::SNVECT_t& CtiDeviceGateway::getThermostatSerialNumbers() const
{
    if( _statMap.size() != _statSN.size() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return _statSN;
}

void CtiDeviceGateway::processGatewayMessage(GATEWAYRXSTRUCT &GatewayRX)
{
    // this is a message type that does not require more than type
    switch(ntohs (GatewayRX.Type))
    {
    case TYPE_KEEPALIVE:
        {
            // printf ("Keepalive Message Received\n");
            break;
        }
    case TYPE_COMMSTATUS:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Gateway " << getMACAddress() << " / " << getIPAddress() << " Comm Status: " << (GatewayRX.U.CommFaultStatus.CommFaultStatus ? "FAILED" : "OK") << endl;
            }

            break;
        }
    case TYPE_BINDMODE:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Gateway " << getMACAddress() << " / " << getIPAddress() << " Bind Mode: " << (GatewayRX.U.BindMode.BindMode ? "ACTIVE" : "INACTIVE") << endl;
            }

            break;
        }
    case TYPE_PINGMODE:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Gateway " << getMACAddress() << " / " << getIPAddress() << " Ping Mode: " << (GatewayRX.U.PingMode.PingMode ? "ACTIVE" : "INACTIVE") << endl;
            }

            break;
        }
    case TYPE_RESET:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Gateway " << getMACAddress() << " / " << getIPAddress() << " Reset: " << (GatewayRX.Reset.Address) << endl;
            }

            break;
        }
    case TYPE_ERROR:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Gateway " << getMACAddress() << " / " << getIPAddress() << " Error: " << ntohs(GatewayRX.U.ErrorReport.Error) << endl;
            }

            break;
        }
    case TYPE_RETURNCODE:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                char of = dout.fill('0');
                dout << RWTime() << " Gateway ReturnCode : 0x" << hex << setw(4) << ntohs(GatewayRX.Return.ReturnCode) << dec << endl;
                dout.fill(of);
            }

            break;
        }
    case TYPE_ADDRESSING:
        {
            IN_ADDR ipaddr;

            memcpy(_mac, GatewayRX.Addressing.Mac, 6);
            _ipaddr = ntohl(GatewayRX.Addressing.IPAddress);
            _spid = ntohs(GatewayRX.Addressing.Spid);
            _geo = ntohs(GatewayRX.Addressing.Geo);
            _feeder = ntohs(GatewayRX.Addressing.Feeder);
            _zip = ntohl(GatewayRX.Addressing.Zip);
            _uda = ntohs(GatewayRX.Addressing.Uda);
            _program = GatewayRX.Addressing.Program;
            _splinter = GatewayRX.Addressing.Splinter;

            ipaddr.S_un.S_addr = (ULONG)_ipaddr;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "MAC                    ";

                for(int i = 0; i < 5; i++)
                {
                    dout << hex << (int)GatewayRX.Addressing.Mac[i] << ":";
                }
                dout << hex << (int)GatewayRX.Addressing.Mac[5] << dec << endl;

                dout << "Local IP               " << getIPAddress() << endl;
                dout << "SPID                   " << ntohs(GatewayRX.Addressing.Spid) << endl;
                dout << "GEO                    " << ntohs(GatewayRX.Addressing.Geo) << endl;
                dout << "FEEDER                 " << ntohs(GatewayRX.Addressing.Feeder) << endl;
                dout << "ZIP                    " << ntohl(GatewayRX.Addressing.Zip) << endl;
                dout << "UDA                    " << ntohs(GatewayRX.Addressing.Uda) << endl;
                dout << "PROGRAM                " << (int)GatewayRX.Addressing.Program << endl;
                dout << "SPLINTER               " << (int)GatewayRX.Addressing.Splinter << endl;
            }

            break;
        }
    default:
            break;
        }

    return;
}

bool CtiDeviceGateway::getCompletedOperation( CtiPendingStatOperation &op )
{
    bool gotone = false;

    SMAP_t::iterator smitr;

    for( smitr = _statMap.begin(); smitr != _statMap.end(); smitr++ )
    {
        CtiDeviceGatewayStat *pGW = (*smitr).second;
        if( true == (gotone = pGW->getCompletedOperation(op) ) )
        {
            break;
        }
    }

    return gotone;
}

RWCString CtiDeviceGateway::getMACAddress() const
{
    bool ismaced = false;
    int i;
    RWCString maddr;

    for(i = 0; i < 6; i++)
    {
        if(_mac[i] != 0)
        {
            ismaced = true;
            break;
        }
    }

    if(ismaced)
    {
        for(i = 0; i < 5; i++)
        {
            maddr.append(CtiNumStr(_mac[i]).hex().zpad(2) + ":");
        }
        maddr.append(CtiNumStr(_mac[5]).hex().zpad(2));
    }

    return maddr;
}

RWCString CtiDeviceGateway::getIPAddress() const
{
    RWCString ipstr;
    IN_ADDR ipaddr;

    if(_ipaddr != 0)
    {
        ipaddr.S_un.S_addr = (ULONG)_ipaddr;
        ipstr = CtiNumStr(ipaddr.S_un.S_un_b.s_b4) + "." + CtiNumStr(ipaddr.S_un.S_un_b.s_b3) + "." + CtiNumStr(ipaddr.S_un.S_un_b.s_b2) + "." + CtiNumStr(ipaddr.S_un.S_un_b.s_b1);
    }

    return ipstr;
}

bool CtiDeviceGateway::shouldClean()
{
    return isSet(GW_CLEAN_ME);
}

CtiMessage* CtiDeviceGateway::rsvpToDispatch()
{
    CtiMultiMsg *pMultiTemp = 0;
    CtiMultiMsg *pMulti = new CtiMultiMsg;

    SMAP_t::iterator smitr;

    for( smitr = _statMap.begin(); smitr != _statMap.end(); smitr++ )
    {
        CtiDeviceGatewayStat *pGW = (*smitr).second;
        pMultiTemp = (CtiMultiMsg *)pGW->rsvpToDispatch();

        if(pMultiTemp)
        {
            pMulti->insert(pMultiTemp);
        }
    }

    if(pMulti->getCount() == 0)
    {
        delete pMulti;
        pMulti = 0;
    }

    return pMulti;
}

