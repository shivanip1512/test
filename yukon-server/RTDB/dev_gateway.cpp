
/*-----------------------------------------------------------------------------*
*
* File:   dev_gateway
*
* Date:   6/17/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/12/19 16:53:52 $
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
    RWDate today( now, RWZone::utc() );


    RWCString gmt_str = today.asString("%Y/%m/%d ") + CtiNumStr(now.hourGMT()).zpad(2) + ":" + CtiNumStr(now.minuteGMT()).zpad(2) + ":" + CtiNumStr(now.second()).zpad(2) + " GMT";

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&tm_Clock, TYPE_TM_CLOCK, sizeof(TM_CLOCK), 0 );
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
            if(parse.getCommandStr().contains("timezone"))
            {
                RWCString CmdStr = parse.getCommandStr().match(" timezone [0-9]+[, ]+[yn]?[, ]+([0-9]+)?");
                RWCString tstr;
                RWCTokenizer tokens(CmdStr);

                USHORT zone = 0;    // Default to GMT
                USHORT minoffset = 0;    // Default to GMT
                BOOL dodst = FALSE;

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

                sendSetTimezone( zone, dodst, minoffset );
                sendtm_Clock(0,0);

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
                    sendtm_Clock();

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
        CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&Get, Type, sizeof(GET), dev );
        send (_msgsock, (char *)&Get, sizeof(GET), 0);
    }

    return cnt;
}


int CtiDeviceGateway::sendKeepAlive (void)
{
    int val = NORMAL;
    KEEPALIVE KeepAlive;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&KeepAlive, TYPE_KEEPALIVE, sizeof(KEEPALIVE), 0 );

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

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetBindMode, TYPE_SETBINDMODE, sizeof(SETBINDMODE), 0 );
    SetBindMode.BindMode = BindMode;

    send (_msgsock, (char *)&SetBindMode, sizeof (SETBINDMODE), 0);
}


void CtiDeviceGateway::sendSetPingMode (UCHAR PingMode)
{
    SETPINGMODE SetPingMode;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetPingMode, TYPE_SETPINGMODE, sizeof(SETPINGMODE), 0 );
    SetPingMode.PingMode = PingMode;

    send (_msgsock, (char *)&SetPingMode, sizeof (SETPINGMODE), 0);
}


void CtiDeviceGateway::sendSetRSSIConfiguration (UCHAR AllMessages)
{
    SETRSSICONFIGURATION SetRSSIConfiguration;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetRSSIConfiguration, TYPE_SETRSSICONFIGURATION, sizeof(SETRSSICONFIGURATION), 0 );
    SetRSSIConfiguration.AllMessages = AllMessages;

    send (_msgsock, (char *)&SetRSSIConfiguration, sizeof (SETRSSICONFIGURATION), 0);
}


void CtiDeviceGateway::sendSetNetworkID (USHORT NetworkID)
{
    SETNETWORKID SetNetworkID;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetNetworkID, TYPE_SETNETWORKID, sizeof(SETNETWORKID), 0 );
    SetNetworkID.NetworkID = ntohs (NetworkID);

    send (_msgsock, (char *)&SetNetworkID, sizeof (SETNETWORKID), 0);
}

int CtiDeviceGateway::getMessageLength(GATEWAYRXSTRUCT *GatewayRX)
{
    int Length = ntohs(GatewayRX->Hdr.Length);
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

            if(bytesavail < sizeof(GWHEADER))
            {
                sleep(500);
                continue;
            }

            /* Wait for 2 bytes to be available so we can get the type in */
            rc = recv (_msgsock, (char *)&GatewayRX, sizeof(GWHEADER), 0);

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
                Length = getMessageLength(&GatewayRX) - sizeof(GWHEADER);
            }

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
                char *cp = ((char *)&GatewayRX) + sizeof(GWHEADER);
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

            // Look at the message as the gateway!
            processGatewayMessage(GatewayRX);

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
                            dout << " Gateway Message Type " << (int)ntohs(GatewayRX.Hdr.Type) << endl;
                        }
                    }
                }

                if(pGW)
                {
                    pGW->clearPrintList();
                    pGW->convertGatewayRXStruct(GatewayRX);
                    pGW->printPacketData();
                }
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
    ULONG did = ntohl (GatewayRX->Hdr.DeviceID);
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


    switch(ntohs (GatewayRX.Hdr.Type))
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
            ULONG did = ntohl(GatewayRX.Hdr.DeviceID);

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

                dout << "Device ID              " << did << endl;
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
    case TYPE_TIMEZONE_MWG:
        {
            _minutesWestOfGreenwich = ntohl(GatewayRX.Timezone.ZoneMinutesWestOfGreenwich);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl;
                dout << "TIMEZONE               " << ntohl(GatewayRX.Timezone.ZoneMinutesWestOfGreenwich) << endl;
            }

            break;
        }
    default:
        {
#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unknown Message Type Received: " << ntohs(GatewayRX.Hdr.Type) << endl;
            }
#endif
            break;
        }
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

/*
    typedef struct
    {
        unsigned char   Mac[6];
        unsigned long   IPAddress;
        unsigned long   DefaultServer;
        unsigned short  Spid;
        unsigned short  Geo;
        unsigned short  Feeder;
        unsigned long   Zip;
        unsigned short  Uda;
        unsigned char   Program;
        unsigned char   Splinter;

    } ADDRESSING_REPORT;
 */

void CtiDeviceGateway::sendSetAddressing(ULONG DeviceId, USHORT Spid, USHORT Geo, USHORT Feeder, ULONG Zip, USHORT Uda, UCHAR Program, UCHAR Splinter, ULONG ServerIP)
{
    ADDRESSING SetAddress;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetAddress, TYPE_SETADDRESSING, sizeof(ADDRESSING), DeviceId );

    SetAddress.Addressing.DefaultServer = htonl(ServerIP);
    SetAddress.Addressing.Spid     = htons(Spid);
    SetAddress.Addressing.Geo      = htons(Geo);
    SetAddress.Addressing.Feeder   = htons(Feeder);
    SetAddress.Addressing.Zip      = htonl(Zip);
    SetAddress.Addressing.Uda      = htons(Uda);
    SetAddress.Addressing.Program  = Program;
    SetAddress.Addressing.Splinter = Splinter;

    send (_msgsock, (char *)&SetAddress, sizeof(ADDRESSING), 0);
}

void CtiDeviceGateway::sendSetTimezone(ULONG minutesWestOfGreenwich, UCHAR doIt, USHORT DSTMinutesOffset)
{
    TIMEZONEMSG SetTimezone;

    CtiDeviceGatewayStat::BuildHeader((GWHEADER*)&SetTimezone, TYPE_SETTIMEZONE, sizeof(TIMEZONEMSG), 0 );

    SetTimezone.Timezone.ZoneMinutesWestOfGreenwich = htonl( minutesWestOfGreenwich );
    SetTimezone.Timezone.DoDST = doIt;
    SetTimezone.Timezone.DSTMinutesOffset = htons(DSTMinutesOffset);

    send (_msgsock, (char *)&SetTimezone, sizeof(TIMEZONEMSG), 0);
}

bool CtiDeviceGateway::shouldClean()
{
    return isSet(GW_CLEAN_ME);
}
