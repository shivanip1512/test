#include "yukon.h"

#include <iostream>
#include <map>

#include "connection.h"
#include "cparms.h"
#include "dlldefs.h"
#include "dllyukon.h"
#include "dllbase.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "mgr_route.h"
#include "dev_gateway.h"
#include "dev_gwstat.h"
#include "dev_grp_energypro.h"
#include "gateway.h"
#include "guard.h"
#include "logger.h"
#include "numstr.h"
#include "portdecl.h"
#include "portglob.h"
#include "port_base.h"
#include "queue.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

using namespace std;
using Cti::ThreadStatusKeeper;

extern CtiPortManager PortManager;
extern CtiDeviceManager DeviceManager;


// Some Global Manager types to allow us some RTDB stuff.

#define DEFAULT_PORT 4990

static CtiMutex gwmux;

typedef map< SOCKET, CtiDeviceGateway* > GWMAP_t;

static GWMAP_t gwMap;

CtiQueue< CtiOutMessage, std::greater<CtiOutMessage> > GatewayOutMessageQueue;

// The stat stores most temps as hundreths of a degree C
// This routine converts to degree F for display

void GWConnectionThread (void *portnumber);
void GWTimeSyncThread (void *Dummy);
void GWResultThread (void *Dummy);
void KeepAliveThread (void *Dummy);

static int SendGet(USHORT Type, LONG dev);
static int ExecuteParse( CtiCommandParser &parse, CtiOutMessage *&OutMessage );
static void ReturnDataToClient(CtiDeviceGatewayStat::OpCol_t &reportableOperations);


void GWTimeSyncThread (void *Dummy)
{
    CtiTime now;
    CtiTime midnightnext = now + 60;
    CtiTime querynext = now + 60;
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("Porter GW Time Sync Thread");

    while(!PorterQuit)
    {
        now = now.now();

        threadStatus.monitorCheck(CtiThreadRegData::None);

        if(now > querynext && gwMap.size() > 0)
        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                // Hourly runtime updates.
                querynext = nextScheduledTimeAlignedOnRate(now, 3600);

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << now << " Query runtimes from all connected gateways.  Next query at " << querynext << endl;
                }

                {
                    GWMAP_t::iterator itr;

                    for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                    {
                        CtiDeviceGateway *pGW = (*itr).second;
                        pGW->sendQueryRuntime(0, FALSE);    // Get them all, reset none = FALSE

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Gateway Runtimes queried." << endl;
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if(now > midnightnext && gwMap.size() > 0)
        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                midnightnext = nextScheduledTimeAlignedOnRate(now, 86400);

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << now << " Timesyncs to all connected gateways.  Next Sync at " << midnightnext << endl;
                }

                {
                    GWMAP_t::iterator itr;

                    for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                    {
                        CtiDeviceGateway *pGW = (*itr).second;
                        pGW->sendGMTClock();
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        Sleep (1000);
    }
}


void KeepAliveThread (void *Dummy)
{
    CtiTime now;
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("Porter GW Keepalive Thread");

    while(!PorterQuit)
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        now = now.now();

        if(!(now.seconds() % 60))
        {
            CtiLockGuard< CtiMutex > guard(gwmux, 1000);

            if(guard.isAcquired())
            {
                {
                    GWMAP_t::iterator itr;

                    try
                    {
                        for(itr = gwMap.begin(); itr != gwMap.end() ; itr++)
                        {
                            CtiDeviceGateway *pGW = (*itr).second;
                            if(pGW->sendKeepAlive())
                            {
                                pGW->interrupt(CtiThread::SHUTDOWN);
                                pGW->join();
                            }
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** HANDLED EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    do
                    {
                        try
                        {
                            for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                            {
                                CtiDeviceGateway *pGW = (*itr).second;
                                if(pGW->shouldClean())
                                {
                                    gwMap.erase(itr);
                                    break;
                                }
                            }
                        }
                        catch(...)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** HANDLED EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                    } while(itr != gwMap.end());
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   Unable to acquire gwmux" << endl;
            }
        }

        Sleep (500);
    }
}


void GWConnectionThread(VOID *Arg)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " GWConnectionThread started as TID  " << CurrentTID() << endl;
    }

    int address_request;
    unsigned short port = (unsigned short)Arg; // DEFAULT_PORT;
    int fromlen;
    int ioctl_opt = 1;
    struct sockaddr_in local, from;
    SOCKET listen_socket;
    int rc, Length;
    SOCKET msgsock;

    local.sin_family = AF_INET;
    local.sin_addr.s_addr = INADDR_ANY;
    local.sin_port = htons(port);

    listen_socket = socket(AF_INET, SOCK_STREAM,0); // TCP socket
    if(listen_socket == INVALID_SOCKET)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " socket() failed with error " << WSAGetLastError() << endl;
        }
        return;
    }
    //
    // bind() associates a local address and port combination with the
    // socket just created.

    if(bind (listen_socket, (struct sockaddr*)&local, sizeof(local)) == SOCKET_ERROR)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " bind() failed with error " << WSAGetLastError() << endl;
        }
        return;
    }

    //
    // start listening on the socket for incoming connections
    //
    if(listen (listen_socket, SOMAXCONN) == SOCKET_ERROR)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " listen() failed with error " << WSAGetLastError() << endl;
        }
        return;
    }

    u_long nonblock = TRUE;
    ioctlsocket(listen_socket, FIONBIO, &nonblock);
    fromlen = sizeof(from);

    while(!PorterQuit)
    {
        CtiDeviceGateway *pGW = 0;

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
                dout << CtiTime() << " **** Unrecoverable error **** " << WSAGetLastError() << " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " New gateway initiating connection." << endl;
        }

        pGW = new CtiDeviceGateway(msgsock);
        pGW->start();
        pGW->sendGet( TYPE_GETADDRESSING, 0 );
        pGW->sendGet( TYPE_GETALL, 0 );
        pGW->sendQueryRuntime(0, FALSE);    // Get the runtimes at connect, reset none = FALSE

        address_request = 0;

        {
            CtiLockGuard< CtiMutex > guard(gwmux, 15000);

            if(guard.isAcquired())
            {
                GWMAP_t::iterator itr = gwMap.find( msgsock );
                if( itr != gwMap.end() )
                {
                    delete pGW;             // Don't leak here.
                    pGW = (*itr).second;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " Gateway already in the list! " << endl;
                    }
                    pGW = 0;
                }
                else
                {
                    gwMap.insert( GWMAP_t::value_type(msgsock, pGW) );
                    pGW = 0;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if(pGW)
        {
            delete pGW;
            pGW = 0;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " GWConnectionThread shutdown." << endl;
    }

    return;
}

VOID PorterGWThread(void *pid)
{
    extern CtiConnection    VanGoghConnection;

    UINT sanity=0;
    INT status;
    OUTMESS        *OutMessage;
    REQUESTDATA    ReadResult;
    BYTE           ReadPriority;
    ULONG          ReadLength;
    ULONG          QueEntries;
    CtiTime        lastQueueReportTime;

    ThreadStatusKeeper threadStatus("Porter GW Thread");

    LONG portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiDeviceSPtr Device;
    CtiPortSPtr Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PorterGWThread started as TID  " << CurrentTID() << endl;
    }

    lastQueueReportTime = lastQueueReportTime.seconds() - (lastQueueReportTime.seconds() % 300L);

    // Start a connection thread for each port in the list
    {
        string portstr = gConfigParms.getValueAsString("PORTER_GATEWAY_PORTS", "4990, 4995, 5000");
        string tempstr;


        boost::char_separator<char> sep(", ");
        Boost_char_tokenizer porttok(portstr, sep);
        Boost_char_tokenizer::iterator tok_iter = porttok.begin();

        while( tok_iter != porttok.end() )
        {
            tempstr = *tok_iter++;
            unsigned short portnumber = atoi(tempstr.c_str());

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " PorterGWThread starting port number " << portnumber << endl;

            }

            _beginthread(GWConnectionThread, 0, (void*)portnumber);
        }
    }
    _beginthread(GWResultThread, 0, NULL);
    _beginthread(GWTimeSyncThread, 0, NULL);
    _beginthread(KeepAliveThread, 0, NULL);

    while(!PorterQuit)
    {
        try
        {
            threadStatus.monitorCheck(CtiThreadRegData::None);

            OutMessage = GatewayOutMessageQueue.getQueue( 2500 );

            if(OutMessage)
            {
                ExecuteParse( CtiCommandParser( OutMessage->Request.CommandStr ), OutMessage );
            }
            else
            {
                CtiLockGuard< CtiMutex > guard(gwmux, 500);

                if(guard.isAcquired())
                {
                    GWMAP_t::iterator itr;

                    for(itr = gwMap.begin(); itr != gwMap.end() ;itr++)
                    {
                        CtiDeviceGateway *pGW = (*itr).second;
                        CtiMultiMsg *pMsg = (CtiMultiMsg *)pGW->rsvpToDispatch();

                        if(pMsg)
                            VanGoghConnection.WriteConnQue(pMsg);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
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
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PorterGWThread shutdown." << endl;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " Parse Processed by " << processedby << " EnergyPro Thermostats" << endl;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


void GWResultThread (void *Dummy)
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("Porter GW Result Thread");

    HANDLE  hArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_GWRESULT_EVENT]
    };

    UINT  tidbitCnt = 0;
    DWORD dwSleepTime = 30000;

    while(!PorterQuit)
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

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
                                dout << CtiTime() << " **** Operation already in the reportable list!  (This is a logic problem)" << endl;
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

    CtiTime now;
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    string tempTS;
                    for(citr = rv.begin(); citr != rv.end(); citr++)
                    {
                        tempTS = *citr;
                        _snprintf( (char*)(InMessage.Buffer.GWRSt.MsgData), 1000, "%s", tempTS.c_str());
                        ReturnResultMessage( NORMAL, &InMessage, pOM );
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                            dout << CtiTime() << " " << (*citr).c_str() << endl;
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}





