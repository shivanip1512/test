


/*-----------------------------------------------------------------------------*
*
* File:   portgw
*
* Date:   6/12/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/05 12:47:20 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include <windows.h>
#include <iostream>
#include <map>
using namespace std;

#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "mgr_device.h"
#include "dev_gateway.h"
#include "dev_gwstat.h"
#include "dev_grp_energypro.h"
#include "gateway.h"
#include "guard.h"
#include "logger.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "mgr_route.h"
#include "numstr.h"
#include "portdecl.h"
#include "portglob.h"
#include "port_base.h"
#include "queue.h"

extern CtiPortManager PortManager;
extern CtiDeviceManager DeviceManager;


// Some Global Manager types to allow us some RTDB stuff.

#define DEFAULT_PORT 5000 // 4990

static CtiMutex gwmux;

typedef map< SOCKET, CtiDeviceGateway* > GWMAP_t;

static GWMAP_t gwMap;

CtiQueue< CtiOutMessage, less<CtiOutMessage> > GatewayOutMessageQueue;

// The stat stores most temps as hundreths of a degree C
// This routine converts to degree F for display

void GWConnectionThread (void *Dummy);
void GWTimeSyncThread (void *Dummy);
void GWResultThread (void *Dummy);
void KeepAliveThread (void *Dummy);

static int SendGet(USHORT Type, LONG dev);
static int ExecuteParse( CtiCommandParser &parse, CtiOutMessage *&OutMessage );
static void ReturnDataToClient(CtiDeviceGatewayStat::OpCol_t &reportableOperations);


void GWTimeSyncThread (void *Dummy)
{
    RWTime now;
    RWTime midnightnext = now + 60;

    while(!PorterQuit)
    {
        now = now.now();

        if(now > midnightnext && gwMap.size() > 0)
        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                midnightnext = now + (3600 * (23 - now.hour())) + (60 * (59 - now.minute())) + (60 - now.second());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << now << " Timesyncs to all connected gateways.  Next Sync at " << midnightnext << endl;
                }

                {
                    GWMAP_t::iterator itr;

                    for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                    {
                        CtiDeviceGateway *pGW = (*itr).second;
                        pGW->sendtm_Clock();
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        Sleep (1000);
    }
}


void KeepAliveThread (void *Dummy)
{
    RWTime now;

    while(!PorterQuit)
    {
        now = now.now();

        if(!(now.seconds() % 60))
        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                {
                    GWMAP_t::iterator itr;

                    for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                    {
                        CtiDeviceGateway *pGW = (*itr).second;
                        pGW->sendKeepAlive();
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        Sleep (1000);
    }
}


VOID GWConnectionThread(VOID *Arg)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " GWConnectionThread started as TID  " << CurrentTID() << endl;
    }

    unsigned short port=DEFAULT_PORT;
    int fromlen;
    int ioctl_opt = 1;
    struct sockaddr_in local, from;
    WSADATA wsaData;
    SOCKET listen_socket;
    int rc, Length;
    SOCKET msgsock;

    if(WSAStartup(0x202,&wsaData) == SOCKET_ERROR)
    {
        fprintf(stderr,"WSAStartup failed with error %d\n",WSAGetLastError());
        WSACleanup();
        return;
    }


    local.sin_family = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port = htons(port);

    listen_socket = socket(AF_INET, SOCK_STREAM,0); // TCP socket
    if(listen_socket == INVALID_SOCKET)
    {
        fprintf(stderr,"socket() failed with error %d\n",WSAGetLastError());
        WSACleanup();
        return;
    }
    //
    // bind() associates a local address and port combination with the
    // socket just created.

    if(bind (listen_socket, (struct sockaddr*)&local, sizeof(local)) == SOCKET_ERROR)
    {
        fprintf (stderr, "bind() failed with error %d\n", WSAGetLastError());
        WSACleanup();
        return;
    }

    //
    // start listening on the socket for incoming connections
    //
    if(listen (listen_socket, SOMAXCONN) == SOCKET_ERROR)
    {
        fprintf (stderr, "listen() failed with error %d\n", WSAGetLastError());
        WSACleanup();
        return;
    }

    u_long nonblock = TRUE;
    ioctlsocket(listen_socket, FIONBIO, &nonblock);
    fromlen = sizeof(from);

    while(!PorterQuit)
    {
        msgsock= accept (listen_socket, (struct sockaddr*)&from, &fromlen);
        if(msgsock == INVALID_SOCKET && WSAGetLastError() == WSAEWOULDBLOCK)
        {
            Sleep(2500);
            continue;
        }

        if(msgsock == INVALID_SOCKET)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Unrecoverable error **** " << WSAGetLastError() << " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }

        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                CtiDeviceGateway *pGW = 0;

                GWMAP_t::iterator itr = gwMap.find( msgsock );
                if( itr != gwMap.end() )
                {
                    pGW = (*itr).second;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " Whoa!!!  Already in the list!!! " << endl;
                    }
                }
                else
                {
                    pGW = new CtiDeviceGateway(msgsock);
                    pGW->start();

                    Sleep(2500);

                    pGW->sendGet( TYPE_GETALL, 0 );
                    gwMap.insert( GWMAP_t::value_type(msgsock, pGW) );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " New gateway is connected." << endl;
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " GWConnectionThread shutdown." << endl;
    }

    return;
}

VOID PorterGWThread(void *pid)
{
    INT status;
    OUTMESS        *OutMessage;
    REQUESTDATA    ReadResult;
    BYTE           ReadPriority;
    ULONG          ReadLength;
    ULONG          QueEntries;
    RWTime         lastQueueReportTime;

    WSADATA wsaData;
    LONG portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiDeviceBase *Device = NULL;
    CtiPortSPtr Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PorterGWThread started as TID  " << CurrentTID() << endl;
    }

    lastQueueReportTime = lastQueueReportTime.seconds() - (lastQueueReportTime.seconds() % 300L);

    if(WSAStartup(0x202, &wsaData) == SOCKET_ERROR)
    {
        fprintf(stderr,"WSAStartup failed with error %d\n",WSAGetLastError());
        WSACleanup();
        return;
    }

    _beginthread(GWConnectionThread, 0, NULL);
    _beginthread(GWResultThread, 0, NULL);
    _beginthread(GWTimeSyncThread, 0, NULL);
    _beginthread(KeepAliveThread, 0, NULL);

    while(!PorterQuit)
    {
        try
        {
            OutMessage = GatewayOutMessageQueue.getQueue( 2500 );

            if(OutMessage)
            {
                ExecuteParse( CtiCommandParser( OutMessage->Request.CommandStr ), OutMessage );
            }

            if(OutMessage)
            {
                delete OutMessage;
                OutMessage = 0;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    {
        CtiLockGuard< CtiMutex > guard(gwmux, 15000);

        if(guard.isAcquired())
        {
            GWMAP_t::iterator itr;

            for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
            {
                CtiDeviceGateway *pGW = (*itr).second;
                pGW->interrupt(CtiThread::SHUTDOWN);
                pGW->join();
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PorterGWThread shutdown." << endl;
    }

    return;
}


int SendGet(USHORT Type, LONG dev)
{
    int cnt = 0;

    CtiLockGuard< CtiMutex > guard(gwmux, 15000);

    if(guard.isAcquired())
    {
        {
            GWMAP_t::iterator itr;

            for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
            {
                CtiDeviceGateway *pGW = (*itr).second;
                cnt += pGW->sendGet(Type, dev);
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return cnt;
}

int ExecuteParse( CtiCommandParser &parse, CtiOutMessage *&OutMessage )
{
    int status = 0;
    int processedby = 0;

    CtiLockGuard< CtiMutex > guard(gwmux, 15000);

    if(guard.isAcquired())
    {
        {
            GWMAP_t::iterator itr;

            for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
            {
                CtiDeviceGateway *pGW = (*itr).second;
                processedby += pGW->processParse(parse, OutMessage);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Parse Processed by " << processedby << " EnergyPro Thermostats" << endl;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


void GWResultThread (void *Dummy)
{

    HANDLE  hArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_GWRESULT_EVENT]
    };

    UINT  tidbitCnt = 0;
    DWORD dwSleepTime = 30000;


    while(!PorterQuit)
    {
        DWORD dwWait = WaitForMultipleObjects(sizeof(hArray) / sizeof(HANDLE), hArray, FALSE, dwSleepTime);

        if(dwWait != WAIT_TIMEOUT)
        {
            switch( dwWait - WAIT_OBJECT_0 )
            {
            case WAIT_OBJECT_0:         // P_QUIT_EVENT:
                {
                    PorterQuit = TRUE;
                    continue;
                }
            case WAIT_OBJECT_0 + 1:     // P_GWRESULT_EVENT:
                {
                    ResetEvent( hPorterEvents[P_GWRESULT_EVENT] );
                    dwSleepTime = 500;          // We must timeout to do a processing run!

                    if( tidbitCnt++ < 100 )     // Do a processing run if we have seen more than 100 tidbits too.
                        continue;               // The while loop...

                    break;
                }
            default:
                {
                    Sleep(50);          // No crazy loops here please.
                    break;
                }
            }
        }
        else
        {
            dwSleepTime = 30000;
        }

        tidbitCnt = 0;

        CtiDeviceGatewayStat::OpCol_t reportableOperations;

        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);
            if(guard.isAcquired())
            {
                CtiPendingStatOperation op;

                GWMAP_t::iterator gwmapitr;
                for(gwmapitr = gwMap.begin(); gwmapitr != gwMap.end(); gwmapitr++)
                {
                    CtiDeviceGateway *pGW = (*gwmapitr).second;
                    pGW->checkPendingOperations();

                    while( pGW->getCompletedOperation(op) )         // Look for any completed operations on this gateway.
                    {
                        pair<CtiDeviceGatewayStat::OpCol_t::iterator, bool> ip = reportableOperations.insert(op);

                        if(ip.second == false)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Operation already in the reportable list!  (This is a logic problem)" << endl;
                                dout << " Stat      " << op.getSerial() << endl;
                                dout << " Operation " << op.getOperation() << endl;
                            }
                        }
                    }
                }
            }
        }

        if( !reportableOperations.empty() )
        {
            ReturnDataToClient(reportableOperations);
        }
    }
}


void ReturnDataToClient(CtiDeviceGatewayStat::OpCol_t &reportableOperations)
{
    extern INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);

    RWTime now;
    INMESS InMessage;

    try
    {
        CtiDeviceGatewayStat::OpCol_t::iterator ro_itr;
        for(ro_itr = reportableOperations.begin(); ro_itr != reportableOperations.end(); ro_itr++)
        {
            CtiPendingStatOperation &op = *ro_itr;

            #if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Stat      " << op.getSerial() << endl;
                dout << " Operation " << op.getOperation() << endl;
            }
            #endif

            if(op.getOutMessage() != 0)
            {
                CtiOutMessage *pOM = CTIDBG_new CtiOutMessage(*op.getOutMessage());
                pOM->EventCode |=  RESULT;
                OutEchoToIN( pOM, &InMessage );


                CtiPendingStatOperation::PGRReplyVector_t::const_iterator citr;
                CtiPendingStatOperation::PGRReplyVector_t &rv = op.getConstReplyVector();

                if(!rv.empty())
                {
                    for(citr = rv.begin(); citr != rv.end(); citr++)
                    {
                        _snprintf( (char*)(InMessage.Buffer.GWRSt.MsgData), 1000, "%s", (*citr).data());
                        ReturnResultMessage( NORMAL, &InMessage, pOM );
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Reply vector is empty" << endl;
                    }
                }

                if(pOM)
                {
                    delete pOM;
                    pOM = 0;
                }
            }
            else
            {
                CtiPendingStatOperation::PGRReplyVector_t::const_iterator citr;
                CtiPendingStatOperation::PGRReplyVector_t &rv = op.getConstReplyVector();

                {
                    for(citr = rv.begin(); citr != rv.end(); citr++)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << (*citr).data() << endl;
                        }
                    }
                }
            }
        }

        reportableOperations.clear();
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}





