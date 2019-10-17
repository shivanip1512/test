#include "precompiled.h"

#include <iostream>

#include <process.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <crtdbg.h>

#include <stdio.h>
#include <conio.h>
#include <string>
#include <vector>

#include "color.h"
#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "porter.h"
#include "portdecl.h"
#include "portverify.h"
#include "StatisticsThread.h"
#include "master.h"
#include "elogger.h"
#include "thread_monitor.h"
#include "streamLocalConnection.h"
#include "streamAmqConnection.h"
#include "systemmsgthread.h"

#include "logger.h"
#include "numstr.h"

/* define the global area */
#include "portglob.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "mgr_route.h"
#include "mgr_config.h"
#include "mgr_point.h"
#include "mgr_dyn_paoinfo.h"

#include "port_thread_udp.h"
#include "port_thread_tcp.h"
#include "port_thread_rf_da.h"

#include "port_base.h"
#include "port_shr.h"
#include "port_shr_ip.h"
#include "rtdb.h"
#include "dllbase.h"
#include "msg_dbchg.h"
#include "msg_trace.h"

#include "rte_macro.h"
#include "rte_xcu.h"

#include "eventlog.h"
#include "cparms.h"
#include "trx_711.h"
#include "utility.h"
#include "dllyukon.h"

#include "pilserver.h"
#include "msg_pcrequest.h"
#include "database_reader.h"
#include "database_connection.h"
#include "dev_ccu721.h"

#include "connection_client.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "porter_message_serialization.h"
#include "random_generator.h"

#include "NetworkManagerMessaging.h"

#include "DeviceAttributeLookup.h"

#include <boost/interprocess/ipc/message_queue.hpp>
#include <boost/interprocess/exceptions.hpp>
#include <boost/interprocess/creation_tags.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/range/algorithm/set_algorithm.hpp>
#include <boost/range/algorithm/copy.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/transformed.hpp>

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;
using Cti::StreamLocalConnection;
using Cti::StreamConnection;

ULONG TimeSyncRate = 3600L;

static _CRT_ALLOC_HOOK pfnOldCrtAllocHook = NULL;

static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine );



void DisplayTraceList( CtiPortSPtr Port, list< CtiMessage* > &traceList, bool consume);
void LoadPorterGlobals(void);
INT  RefreshPorterRTDB(const CtiDBChangeMsg *ptr = 0);
void DebugKeyEvent(KEY_EVENT_RECORD *ke);
string GetDeviceName( ULONG id );
void KickPIL();
bool processInputFunction(CHAR Char);
void registerServices();

void reportOnWorkObjects();

extern void QueueThread();
extern void KickerThread();
extern void DispatchMsgHandlerThread();
extern HCTIQUEUE* QueueHandle(LONG pid);
void commFail(const CtiDeviceSPtr &Device);
bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero);

DLLIMPORT extern BOOL PorterQuit;

extern INT RunningInConsole;              // From portmain.cpp

// Some Global Manager types to allow us some RTDB stuff.
CtiPortManager     PortManager;
CtiDeviceManager   DeviceManager;
CtiPointManager    PorterPointManager;
CtiRouteManager    RouteManager;
map< long, CtiPortShare * > PortShareManager;

//These form the connection between Pil and Porter
extern DLLIMPORT StreamLocalConnection<OUTMESS, INMESS> PilToPorter; //Pil handles this one
StreamLocalConnection<INMESS, OUTMESS> PorterToPil;                  //Porter handles this one
StreamConnection* ScannerNexus;
extern DLLIMPORT CtiFIFOQueue< CtiMessage > PorterSystemMessageQueue;

Cti::Porter::SystemMsgThread _sysMsgThread(PorterSystemMessageQueue, DeviceManager, PortManager, PilToPorter);

using Cti::WorkerThread;

WorkerThread _dispThread (WorkerThread::Function(DispatchMsgHandlerThread).name("_dispThread"));
WorkerThread _pilThread  (WorkerThread::Function(PorterInterfaceThread)   .name("_pilThread"));
WorkerThread _tsyncThread(WorkerThread::Function(TimeSyncThread)          .name("_tsyncThread"));
WorkerThread _statisticsThread(WorkerThread::Function(Cti::Porter::StatisticsThread).name("_statisticsThread"));
WorkerThread _fillerThread(WorkerThread::Function(FillerThread)           .name("_fillerThread"));
WorkerThread _vconfThread (WorkerThread::Function(VConfigThread)          .name("_vconfThread"));

WorkerThread _queueCCU711Thread (WorkerThread::Function(QueueThread) .name("_queueCCU711Thread"));
WorkerThread _kickerCCU711Thread(WorkerThread::Function(KickerThread).name("_kickerCCU711Thread"));

CtiPorterVerification PorterVerificationThread;

static void LoadCommFailPoints(LONG ptid = 0);
static LONG GetCommFailPointID(LONG devid);
typedef map< LONG, LONG > CtiCommFailPoints_t;              // pair< LONG deviceid, LONG pointid >
static CtiCommFailPoints_t commFailDeviceIDToPointIDMap;

unsigned long totalWorkCountPointId = 0;

bool isTAPTermPort(LONG PortNumber)
{
    vector<CtiDeviceManager::ptr_type> devices;

    DeviceManager.getDevicesByPortID(PortNumber, devices);

    static const auto isTapTerm = [](const CtiDeviceSPtr &devsptr)
    {
        return devsptr->getType() == TYPE_TAPTERM;
    };

    return find_if(devices.begin(), devices.end(), isTapTerm) != devices.end();
}

void populateRouteAssociations(CtiDeviceManager *DM, CtiRouteManager *RM)
{
    try
    {
        CtiRouteManager::spiterator itr;

        for(itr = RM->begin(); itr != RM->end() ; RM->nextPos(itr))
        {
            CtiRouteSPtr pRte = itr->second;

            switch(pRte->getType())
            {
                case RouteTypeCCU:
                case RouteTypeTCU:
                case RouteTypeLCU:
                case RouteTypeVersacom:
                case RouteTypeExpresscom:
                case RouteTypeTap:
                case RouteTypeRDS:
                case RouteTypeWCTP:
                case RouteTypeTNPP:
                case RouteTypeSNPP:
                case RouteTypeRTC:
                case RouteTypeSeriesVLMI:
                {
                    CtiRouteXCUSPtr pXCU = boost::static_pointer_cast<CtiRouteXCU>(itr->second);

                    const long dID = pXCU->getCommRoute().getTrxDeviceID();

                    if( dID > 0 )
                    {
                        pXCU->setDevicePointer(DM->getDeviceByID(dID));
                    }
                    break;
                }
                case RouteTypeMacro:
                {
                    Cti::Routes::MacroRouteSPtr pMac = boost::static_pointer_cast<Cti::Routes::MacroRoute>(itr->second);

                    try
                    {
                        // Lock it so that it cannot conflict with an ExecuteRequest() on the route!!
                        CtiLockGuard< CtiMutex > listguard(pMac->getMacroMux());

                        for each( const long subrouteId in pMac->getSubrouteIds() )
                        {
                            pMac->addSubroute(RM->getRouteById(subrouteId));
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                default:
                {
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/* Routine to load all routes on a system */
static void applyLoadAllRoutes(const long portid, CtiPortSPtr Port, void *unusedPtr)
{
    extern INT LoadPortRoutes (USHORT Port);
    LoadPortRoutes(Port->getPortID());
    return;
}

static void applyPortVerify(const long unusedid, CtiPortSPtr Port, void *unusedPtr)
{
    Port->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
}

static void applyNewLoad(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    /* Find out if we have a TAP terminal */
    ptPort->setTAP( isTAPTermPort(ptPort->getPortID()) );

    if((PorterDebugLevel & PORTER_DEBUG_VERBOSE) && ptPort->isTAP())
    {
        CTILOG_DEBUG(dout, ptPort->getName() << " has a TAP device on it.");
    }

    ptPort->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
}

static void applyPortShares(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
#define PORTSHARENEXUS 1930

    //  this is a one-time load, making this thread-safe (if the map is treated as a read-only collection).
    //  if/when we update this to allow real-time reloads of the port shares,
    //    we need to keep it thread-safe.

    if( (ptPort->getSharedPortType() == "acs") || (ptPort->getSharedPortType() == "ilex" ) )
    {
        try
        {
            std::unique_ptr<CtiPortShareIP> newPortShareIP(new CtiPortShareIP(ptPort, PORTSHARENEXUS + PortShareManager.size()));
            newPortShareIP->setIPPort(ptPort->getSharedSocketNumber());
            newPortShareIP->start();
            PortShareManager.insert(std::make_pair(ptPort->getPortID(), newPortShareIP.release()));
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Unable to initialize shared port for "<< ptPort->getName());
        }
    }
}

static void applyQueuedDevicePortMatchup(const long unusedid, CtiDeviceSPtr RemoteDevice, void *portPtr)
{
    if(RemoteDevice->getDeviceQueueHandler() != NULL && portPtr != NULL)
    {
        CtiPortManager *portMgrPtr = (CtiPortManager*)portPtr;
        CtiPortSPtr port = portMgrPtr->getPortById(RemoteDevice->getPortID());
        if(port)
        {
            port->addDeviceQueuedWork(RemoteDevice->getID(), -1);
        }
    }
}

void resumeMctLoadProfileRequests()
{
    extern Cti::Pil::PilServer PIL;
    using boost::range::set_intersection;

    set<long> request_paos;

    set_intersection(
            Cti::DynamicPaoInfoManager::getPaoIdsHavingInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin),
            Cti::DynamicPaoInfoManager::getPaoIdsHavingInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd),
            inserter(request_paos,
                     request_paos.end()));

    set<long> request_with_channel_paos;

    set_intersection(
            request_paos,
            Cti::DynamicPaoInfoManager::getPaoIdsHavingInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel),
            inserter(request_with_channel_paos,
                     request_with_channel_paos.end()));

    for each( long paoid in request_with_channel_paos )
    {
        std::unique_ptr<CtiRequestMsg> newReq(
                new CtiRequestMsg(paoid, "getvalue lp resume"));

        newReq->setMessagePriority(6);  //  CtiDeviceSingle::ScanPriority_LoadProfile

        PIL.putQueue(newReq.release());
    }
}

struct coldStartDevice
{
    long port_id;

    coldStartDevice(long port_id_) : port_id(port_id_)  {  };

    void operator()(CtiDeviceSPtr &RemoteDevice)
    {
        if(port_id == RemoteDevice->getPortID() && !RemoteDevice->isInhibited() && RemoteDevice->getAddress() != CCUGLOBAL)
        {
            /* Cold Start */
            IDLCFunction (RemoteDevice, 0, DEST_BASE, COLD);
        }
    }
};

static void applyColdStart(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    if(!ptPort->isInhibited())
    {
        vector<CtiDeviceManager::ptr_type> devices;

        DeviceManager.getDevicesByType(TYPE_CCU711, devices);

        for_each(devices.begin(), devices.end(), coldStartDevice(ptPort->getPortID()));
    }
}

void applyDeviceQueuePurge(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    extern bool findAllQueueEntries(void *unused, void *d);
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    LONG PortID = (LONG)lprtid;
    ULONG QueEntCnt = 0L;

    if(PortID == RemoteDevice->getPortID())
    {
        bool commsuccess = false;
        if(RemoteDevice->adjustCommCounts( commsuccess, false ))
        {
            commFail(RemoteDevice);
        }

        switch( RemoteDevice->getType() )
        {
            case TYPE_CCU711:
            {
                CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteDevice->getTrxInfo();

                if(pInfo != NULL)
                {
                    QueryQueue(pInfo->QueueHandle, &QueEntCnt);
                    if(QueEntCnt)
                    {
                        CTILOG_WARN(dout, "CCU: "<< RemoteDevice->getName() <<" PURGING "<< QueEntCnt <<" queue queue entries");

                        CleanQueue(pInfo->QueueHandle, NULL, findAllQueueEntries, cleanupOrphanOutMessages);
                        // PurgeQueue(pInfo->QueueHandle);
                    }

                    QueryQueue(pInfo->ActinQueueHandle, &QueEntCnt);
                    if(QueEntCnt)
                    {
                        CTILOG_WARN(dout, "CCU: "<< RemoteDevice->getName() <<" PURGING "<< QueEntCnt <<" actin queue entries");

                        CleanQueue(pInfo->ActinQueueHandle, NULL, findAllQueueEntries, cleanupOrphanOutMessages);
                        //PurgeQueue(pInfo->ActinQueueHandle);
                    }

                    //  make sure we clear out the pending bits - otherwise the device will refuse any new queued requests
                    if(pInfo->getStatus(INLGRPQ))
                    {
                        pInfo->clearStatus(INLGRPQ);
                    }
                }

                break;
            }
            case TYPE_CCU721:
            {
                auto entries = RemoteDevice->getDeviceQueueHandler()->retrieveQueueEntries(findAllQueueEntries, NULL);

                CTILOG_WARN(dout, "CCU: "<< RemoteDevice->getName() <<" PURGING "<<  entries.size() <<" queue entries");

                for( auto om : entries )
                {
                    cleanupOrphanOutMessages(NULL, om);
                }

                break;
            }
        }
    }
}

void applyPortQueuePurge(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    extern bool findAllQueueEntries(void *unused, void *d);
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    LONG id = (LONG)unusedPtr;

    if( (id != 0 && ptPort->getPortID() != id) )
    {
        // Skip it!
        return;
    }

    if(!ptPort->isInhibited())
    {
        ULONG QueEntCnt = 0L;
        /* Print out the port queue information */
        QueryQueue(*QueueHandle(ptPort->getPortID()), &QueEntCnt);

        CTILOG_WARN(dout, "Port: "<< ptPort->getPortID() <<" / "<< ptPort->getName() <<" PURGING "<< QueEntCnt <<" port queue entries");

        CleanQueue(*QueueHandle(ptPort->getPortID()), NULL, findAllQueueEntries, cleanupOrphanOutMessages);
        // PurgeQueue(*QueueHandle(ptPort->getPortID()));

        DeviceManager.apply(applyDeviceQueuePurge,(void*)ptPort->getPortID());
    }
}

static char* metric_names[] = {
    "   WAIT        ",
    "   RESULT      ",
    "   QUEUED      ",
    "   ACTIN       ",
    "   AWORD       ",
    "   BWORD       ",
    "   DTRAN       ",
    "   RCONT       ",
    "   RIPPLE      ",
    "   STAGE       ",
    "   VERSACOM    ",
    "   TSYNC       ",
    "   REMS        ",
    "   FISHERPIERCE",
    "   EN/DECODED  ",
    "   COMMANDCODE "
};

void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    LONG PortID = (LONG)lprtid;
    ULONG QueEntCnt = 0L;
    ULONG AQueEntCnt = 0L;

    if(lprtid == NULL || PortID == RemoteDevice->getPortID())
    {
        const auto QueWorkCnt    = RemoteDevice->queuedWorkCount();
        const auto ent           = RemoteDevice->getExclusion().getEvaluateNextAt();
        const auto hasExclusions = RemoteDevice->getExclusion().hasExclusions();

        switch( RemoteDevice->getType() )
        {
            case TYPE_CCU711:
            {
                CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteDevice->getTrxInfo();

                if(pInfo != NULL)
                {
                    QueryQueue (pInfo->QueueHandle, &QueEntCnt);
                    QueryQueue (pInfo->ActinQueueHandle, &AQueEntCnt);
                    int ccuStatus = (int)pInfo->getStatus();
                    {
                        std::ostringstream logmsg;
                        logmsg << RemoteDevice->getName() <<" queue report:";

                        if( QueWorkCnt )
                        {
                            logmsg << endl << QueWorkCnt <<" queued work elements.";

                            if( hasExclusions )
                            {
                                logmsg << " Evaluate next at "<< ent <<".";
                            }
                        }

                        logmsg << endl
                                << "Queue Entries - Queue: " << QueEntCnt
                                <<", Actin: "                << AQueEntCnt
                                <<", Status Byte: "          << hex << setw(4) << ccuStatus << dec
                                <<", FreeSlots: "            << pInfo->FreeSlots;

                        for(int i = 0; i < 32; i++)
                        {
                            if(pInfo->QueTable[i].InUse)
                            {
                                logmsg << endl <<"CCU QueTable Slot "<< setw(3) << i <<" is "<<
                                        ((pInfo->QueTable[i].InUse & INUSE) ? "    INUSE" : "NOT INUSE" ) <<" and "<<
                                        ((pInfo->QueTable[i].InUse & INCCU) ? "    INCCU" : "NOT INCCU" ) <<
                                        " TimeSent: "<< CtiTime( pInfo->QueTable[i].TimeSent ) <<
                                        " Sequence: "<< hex << setw(5) << pInfo->QueTable[i].QueueEntrySequence << dec;
                            }
                        }

                        CTILOG_INFO(dout, logmsg);
                    }
                }

                break;
            }

            case TYPE_CCU721:
            {
                using Cti::Devices::Ccu721Device;
                Cti::Devices::Ccu721SPtr ccu = boost::static_pointer_cast<Ccu721Device>(RemoteDevice);

                const auto queue_report = ccu->queueReport();

                CTILOG_INFO(dout, RemoteDevice->getName() <<" queue report: "<<
                        endl << queue_report);

                break;
            }

            default:
            {
                if(QueWorkCnt > 0)
                {
                    Cti::StreamBuffer output;
                    output << QueWorkCnt <<" queued commands. ";

                    if( hasExclusions )
                    {
                        output << "Evaluate next at "<< ent <<". ";
                    }

                    output << "Transmitter: "<< RemoteDevice->getName();

                    if( hasExclusions && (ent < ent.now()) )
                    {
                        output <<". PAST DUE: "<< (ent.now().seconds() - ent.seconds()) <<" seconds.";
                    }

                    CTILOG_INFO(dout, output);
                }
            }
        }
    }
}

void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    /* Report on the state of the queues */

    if(!ptPort->isInhibited())
    {
        ULONG QueEntCnt = ptPort->queueCount();
        /* Print out the port queue information */

        CtiQueueAnalysis_t qa;
        ::memset(&qa, 0, sizeof(qa));

        ptPort->applyPortQueue(&qa, applyPortQueueOutMessageReport);

        {
            Cti::StreamBuffer output;
            output <<" Port: "<< ptPort->getPortID() <<" / "<< ptPort->getName() <<" Port Queue Entries: "<< QueEntCnt;

            if( ptPort->getConnectedDevice() )
            {
                output <<". Connected To: "<< GetDeviceName(ptPort->getConnectedDevice());
            }

            CTILOG_INFO(dout, output);
        }
    }
}
    
// struct used to accumulate point data messages which contain the queue counts for each port 
// and a value for total work count
struct PortQueueCounts
{
    unsigned long totalWorkCount; // total work count accross all ports
    std::unique_ptr<CtiMultiMsg> portQueueCountMsgs; //multi message for queue counts of each port
};

void applyPortWorkReport( const long portId, CtiPortSPtr ptPort, void *passedPtr )
{
    unsigned long queEntCnt = 0;

    /* Report on the state of the queues */
    if( !ptPort->isInhibited() )
    {
        queEntCnt = ptPort->getWorkCount();
        auto queueCounts = static_cast<PortQueueCounts*>( passedPtr );

        if( queueCounts )
        {
            queueCounts->totalWorkCount += queEntCnt;

            auto deviceType = static_cast<DeviceTypes>( ptPort->getType() );

            if( const auto typeAndOffset = Cti::DeviceAttributeLookup::Lookup( deviceType, Attribute::PortQueueCount ) )
            {
                if( const auto pointId = GetPIDFromDeviceAndOffset( portId, typeAndOffset->offset ) )
                {
                    if( auto pointData = std::make_unique<CtiPointDataMsg>(pointId, queEntCnt, NormalQuality, AnalogPointType) )
                    {
                        queueCounts->portQueueCountMsgs->insert( pointData.release() );
                    }
                }
            }
        }
    }

    /* Print out the port queue information */
    CTILOG_INFO( dout, "Port: " << ptPort->getPortID() << " / " << ptPort->getName() << " Port/Device Work Entries: " << queEntCnt );
}


namespace {

inline boost::posix_time::ptime getAbsTimeFromMillis( unsigned long millis )
{
    return boost::posix_time::microsec_clock::universal_time() + boost::posix_time::milliseconds(millis);
}

// keeps track of the last time we logged open message queue failure
boost::optional<CtiTime> nextScannerloggingFail;

void writeDynamicPaoInfo()
{
    const std::set<long> paoIdsWritten = Cti::DynamicPaoInfoManager::writeInfo();

    if( paoIdsWritten.empty() )
    {
        return;
    }

    try
    {
        // Open the message_queue.
        boost::interprocess::message_queue message_queue(
                boost::interprocess::open_only,     // open only
                "SCANNER_DYNAMICDATA_DBCHANGE"      // name
                );

        // reset the next scanner logging
        nextScannerloggingFail = boost::none;

        try
        {
            for each( long paoId in paoIdsWritten )
            {
                if( ! message_queue.timed_send(&paoId, sizeof(long), 0, getAbsTimeFromMillis(1000)) )
                {
                    CTILOG_ERROR(dout, "Timeout while sending paoIds to Scanner.");

                    return;
                }
            }
        }
        catch( const boost::interprocess::interprocess_exception &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex);
        }
    }
    catch( const boost::interprocess::interprocess_exception &ex )
    {
        CtiTime now;

        // Log this error on the first incident, or after one hour or more
        if( ! nextScannerloggingFail || now >= *nextScannerloggingFail )
        {
            CTILOG_EXCEPTION_WARN(dout, ex, "Interprocess message queue could not be open, SCANNER may not be started.");

            nextScannerloggingFail = now + (60*60); // now + 1 hour
        }
    }
}

} // namespace anonymous

INT PorterMainFunction (INT argc, CHAR **argv)
{
    /* Misc Definitions */
    INT    i, j;
    int WorkReportIntervalInSeconds;
    CtiTime nextWorkReportTime, nextFlush;

    /* stuff for kbd handling */
    INPUT_RECORD      inRecord;
    HANDLE            hStdIn = GetStdHandle(STD_INPUT_HANDLE);
    DWORD             Count;
    CHAR              Char;

    /* Print out the program name and revison */
    Cti::identifyExecutable(CompileInfo);

    SetThreadName(-1, "PortrMain");

    Cti::DynamicPaoInfoManager::setOwner(Cti::Application_Porter);

    static const char *PorterGuid = "134B8C32-4505-B5EC-1371-8CBC643446A0";

    Cti::Messaging::Rfn::SessionInfoManager::setClientGuid( PorterGuid ); 

    /* check for various flags */
    if(argc > 1)
    {
        for(i = 1; i < argc; i++)
        {
            if(!(stricmp (argv[i], "/D")))
            {
                Double = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/T")))
            {
                TraceFlag = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/E")))
            {
                TraceErrorsOnly = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/C")))
            {
                ResetAll711s = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/R")))
            {
                LoadRoutes = TRUE;
                continue;
            }

            if(!(stricmp (argv[i], "/W")))
            {
                VCUWait = TRUE;
                continue;
            }
            if(!(::strcmp (argv[i], "/?")))
            {
                cout << "/C       Reset (Cold Start) all 711's in system" << endl;
                cout << "/D       Emetcon Double - CCU's Double feild transmissions" << endl;
                cout << "/E       Trace Communication for failed communications (errors) only" << endl;
                cout << "/N       No queueing to CCU 711's" << endl;
                cout << "/R       Download emetcon routes" << endl;
                cout << "/T       Trace Communication for all ports / remotes" << endl;
                cout << "/W       VCU Wait - VCU transmissions on same port must wait for completion" << endl;
                cout << endl;
                exit(-1);
            }
        }
    }

    bool writeLogMessage = true;

    while ( ! PorterQuit && RefreshPorterRTDB() )
    {
        if ( writeLogMessage )
        {
            CTILOG_ERROR(dout, "Database connection attempt failed.");

            writeLogMessage = false;
        }
        Sleep( 5000 );
    }
    if ( PorterQuit )
    {
        return -1;
    }

    SET_CRT_OUTPUT_MODES;
    if(gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
        ENABLE_CRT_SHUTDOWN_CHECK;

    //Match up the connections
    PilToPorter.setMatchingConnection(PorterToPil);
    PorterToPil.setMatchingConnection(PilToPorter);

    pfnOldCrtAllocHook = _CrtSetAllocHook(MyAllocHook);

    _pilThread.start();
    _dispThread.start();
    _tsyncThread.start();
    _statisticsThread.start();
    registerServices();

    /* Start the verification thread */
    if(gConfigParms.isTrue("PORTER_START_VERIFICATIONTHREAD", true))
    {
        PorterVerificationThread.start();
    }

    ThreadMonitor.start(CtiThreadMonitor::Porter);

    /*
     *  Now start up the ports' thread
     */
    try
    {
        PortManager.apply( applyPortVerify, NULL );

        PortManager.apply( applyPortShares, NULL );
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    extern void ConnectionThread(Cti::StreamConnection *MyNexus);

    boost::thread porterToPilConnection    (ConnectionThread, &PorterToPil);

    Cti::StreamAmqConnection<INMESS, CtiOutMessage> PorterToScanner(
            Cti::Messaging::ActiveMQ::Queues::OutboundQueue::ScannerInMessages,
            Cti::Messaging::ActiveMQ::Queues::InboundQueue ::ScannerOutMessages);

    ScannerNexus = &PorterToScanner;

    boost::thread porterToScannerConnection(ConnectionThread, ScannerNexus);

    /* Check if we need to start the filler thread */
    if(gConfigParms.isOpt("PORTER_START_FILLERTHREAD") && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_FILLERTHREAD").c_str())))
    {
        _fillerThread.start();
    }

    /* Check if we need to start the versacom config thread */
    if( !((gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD")).empty()) && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD").c_str())))
    {
        _vconfThread.start();
    }

    _sysMsgThread.start();

    if( (WorkReportIntervalInSeconds = gConfigParms.getValueAsULong("PORTER_WORK_COUNT_TIME", 60)) <= 0 )
    {
        //This is a failure case
        WorkReportIntervalInSeconds = 60;
    }

    if(gLogPorts)
    {
        CTILOG_INFO( dout, "Trace is now FORCED on by configuration entry YUKON_LOG_PORTS");

        TraceFlag = TRUE;
    }
    else if(TraceFlag)
    {
        if(TraceErrorsOnly)
        {
            CTILOG_INFO( dout, "Trace is now on for errors only");
        }
        else
        {
            CTILOG_INFO( dout, "Trace is now on for all messages");
        }
    }
    else
    {
        CTILOG_INFO( dout, "Trace is now off for all messages");
    }

    CtiDeviceManager::ptr_type system;

    if( system = DeviceManager.getDeviceByID(0) )
    {
        if( system->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence) )
        {
            VerificationSequenceGen(true, system->getDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence));
        }
    }

    resumeMctLoadProfileRequests();

    /* Startup is done so main process becomes input thread */
    for(;!PorterQuit;)
    {
        if(RunningInConsole == FALSE)
        {
            CTISleep (1000L);
        }
        else
        {
            if(PeekConsoleInput(hStdIn, &inRecord, 1L, &Count) && (Count > 0))     // There is something ther if we succeed.
            {
                if(inRecord.EventType != KEY_EVENT)
                {
                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);     // Read out the offending input.
                }
                else    // These are to only kind we care about!
                {
                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);

                    if( inRecord.Event.KeyEvent.bKeyDown != FALSE )
                    {
                        if((inRecord.Event.KeyEvent.dwControlKeyState & (LEFT_ALT_PRESSED | RIGHT_ALT_PRESSED)))
                        {
                            Char = ::tolower(inRecord.Event.KeyEvent.uChar.AsciiChar);
                        }
                        else
                        {
                            Char = 0;
                        }

                        if( processInputFunction(Char) )
                        {
                            if(Char != 0)
                            {
                                Cti::FormattedList items;
                                items.add("wVirtualScanCode")   << CtiNumStr(inRecord.Event.KeyEvent.wVirtualScanCode).xhex(4);
                                items.add("wVirtualKeyCode")    << CtiNumStr(inRecord.Event.KeyEvent.wVirtualKeyCode).xhex(4);
                                items.add("Char")               << CtiNumStr(Char).xhex(2);
                                items.add("dwControlKeyState")  << CtiNumStr(inRecord.Event.KeyEvent.dwControlKeyState).xhex(8);

                                CTILOG_DEBUG(dout, "key pressed event (No ALT)."<<
                                        items);
                            }
                        }
                    }
                }
            }
        }

        const CtiTime Now;

        if( nextFlush < Now )
        {
            nextFlush = Now + 60;
            writeDynamicPaoInfo();
            PorterPointManager.processExpired();
        }

        dout->poke();  //  called 4x/second (see sleep at bottom of loop)
        slog->poke();

        if( nextWorkReportTime < Now )
        {
            nextWorkReportTime = nextScheduledTimeAlignedOnRate(Now, WorkReportIntervalInSeconds);
            reportOnWorkObjects();
        }

        CTISleep(250);
    }

    if( system = DeviceManager.getDeviceByID(0) )
    {
        system->setDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence, VerificationSequenceGen());
    }

    PorterCleanUp(0);
    _CrtSetAllocHook(pfnOldCrtAllocHook);

    writeDynamicPaoInfo();

    return 0;
}


void registerServices()
{
    using Cti::Messaging::ActiveMQConnectionManager;
    using MessageDescriptor = ActiveMQConnectionManager::MessageDescriptor;
    using SerializedMessage = ActiveMQConnectionManager::SerializedMessage;
    using Cti::Messaging::ActiveMQ::Queues::InboundQueue;
    using Cti::Messaging::Porter::DynamicPaoInfoDurationKeys;
    using Cti::Messaging::Porter::DynamicPaoInfoPercentageKeys;
    using Cti::Messaging::Porter::DynamicPaoInfoTimestampKeys;
    using Cti::Messaging::Porter::DynamicPaoInfoRequestMsg;
    using Cti::Messaging::Porter::DynamicPaoInfoResponseMsg;
    using Cti::Messaging::Serialization::MessageSerializer;

    ActiveMQConnectionManager::registerReplyHandler(
        InboundQueue::PorterDynamicPaoInfoRequest,
        [](const MessageDescriptor &md) -> std::unique_ptr<SerializedMessage>
        {
            if( auto req = MessageSerializer<DynamicPaoInfoRequestMsg>::deserialize(md.msg) )
            {
                DynamicPaoInfoResponseMsg rsp;

                rsp.deviceId = req->deviceId;

                static const std::map<DynamicPaoInfoDurationKeys, CtiTableDynamicPaoInfo::PaoInfoKeys> durationKeyLookup{
                    { DynamicPaoInfoDurationKeys::RfnVoltageProfileInterval, CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval },
                    { DynamicPaoInfoDurationKeys::MctIedLoadProfileInterval, CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval }};

                for( const auto key : req->durationKeys )
                {
                    if( const auto mappedKey = Cti::mapFind(durationKeyLookup, key) )
                    {
                        long longValue;

                        if( Cti::DynamicPaoInfoManager::getInfo(req->deviceId, *mappedKey, longValue) )
                        {
                            //  switch per key, since some may be stored as minutes, some as seconds, etc
                            switch( *mappedKey )
                            {
                            case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
                                rsp.durationValues.emplace(key, std::chrono::seconds(longValue));
                                break;
                            case CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval:
                                rsp.durationValues.emplace(key, std::chrono::minutes(longValue));
                                break;
                            }
                        }
                    }
                }

                static const std::map<DynamicPaoInfoTimestampKeys, CtiTableDynamicPaoInfo::PaoInfoKeys> timestampKeyLookup{
                    { DynamicPaoInfoTimestampKeys::RfnVoltageProfileEnabledUntil, CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil } };

                for( const auto key : req->timestampKeys )
                {
                    if( const auto mappedKey = Cti::mapFind(timestampKeyLookup, key) )
                    {
                        CtiTime timeValue;

                        if( Cti::DynamicPaoInfoManager::getInfo(req->deviceId, *mappedKey, timeValue) )
                        {
                            //  presumably all timestamp values are stored as CtiTimes
                            rsp.timestampValues.emplace(key,
                                    std::chrono::system_clock::time_point(
                                            std::chrono::seconds(timeValue.seconds())));
                        }
                    }
                }

                static const std::map<DynamicPaoInfoPercentageKeys, CtiTableDynamicPaoInfo::PaoInfoKeys> percentageKeyLookup {
                    { DynamicPaoInfoPercentageKeys::MeterProgrammingProgress, CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress } };

                Cti::RandomGenerator randomPercentage { 100 };

                for( const auto key : req->percentageKeys )
                {
                    if( const auto mappedKey = Cti::mapFind(percentageKeyLookup, key) )
                    {
                        double percentage = randomPercentage();

                        //if( Cti::DynamicPaoInfoManager::getInfo(req->deviceId, *mappedKey, percentage) )
                        {
                            rsp.percentageValues.emplace(key, percentage);
                        }
                    }
                }

                auto serializedRsp = MessageSerializer<DynamicPaoInfoResponseMsg>::serialize(rsp);

                if( !serializedRsp.empty() )
                {
                    return std::make_unique<SerializedMessage>(std::move(serializedRsp));
                }
            }

            return nullptr;
        });
}


/*
 *  called in an atexit() routine to clean up the schtick.
 */
void APIENTRY PorterCleanUp (ULONG Reason)
{
    PorterQuit = TRUE;
    SetEvent( hPorterEvents[P_QUIT_EVENT] );

    //  delete/stop the shared ports
    map<long, CtiPortShare *>::iterator itr;
    for( itr = PortShareManager.begin(); itr != PortShareManager.end(); itr++ )
    {
        delete itr->second;
    }
    PortShareManager.erase(PortShareManager.begin(), PortShareManager.end());

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
    _sysMsgThread.interrupt(CtiThread::SHUTDOWN);

    if(_pilThread         .isRunning())    _pilThread         .interrupt();
    if(_dispThread        .isRunning())    _dispThread        .interrupt();
    if(_tsyncThread       .isRunning())    _tsyncThread       .interrupt();
    if(_statisticsThread  .isRunning())    _statisticsThread  .interrupt();
    if(_fillerThread      .isRunning())    _fillerThread      .interrupt();
    if(_vconfThread       .isRunning())    _vconfThread       .interrupt();
    if(_queueCCU711Thread .isRunning())    _queueCCU711Thread .interrupt();
    if(_kickerCCU711Thread.isRunning())    _kickerCCU711Thread.interrupt();

    if(PorterVerificationThread.isRunning())    PorterVerificationThread.interrupt(CtiPorterVerification::SHUTDOWN);

    ThreadMonitor.join();
    _sysMsgThread.join();

    if( _tsyncThread.isRunning() )
    {
        if( ! _tsyncThread.tryJoinFor(Cti::Timing::Chrono::seconds(2)) )
        {
            CTILOG_ERROR(dout, "_tsyncThread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_tsyncThread shutdown");
        }
    }

    if( _fillerThread.isRunning() )
    {
        if( ! _fillerThread.tryJoinFor(Cti::Timing::Chrono::seconds(2)) )
        {
            CTILOG_ERROR(dout, "_fillerThread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_fillerThread shutdown");
        }
    }

    if( _vconfThread.isRunning() )
    {
        if(_vconfThread.tryJoinFor(Cti::Timing::Chrono::seconds(2)) )
        {
            CTILOG_ERROR(dout, "_vconfThread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_vconfThread shutdown");
        }
    }


    if( _dispThread.isRunning() )
    {
        if( ! _dispThread.tryJoinFor(Cti::Timing::Chrono::seconds(15)) )
        {
            CTILOG_ERROR(dout, "_dispThread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_dispThread shutdown");
        }
    }

    if( _pilThread.isRunning() )
    {
        if( ! _pilThread.tryJoinFor(Cti::Timing::Chrono::seconds(15)) )
        {
            CTILOG_ERROR(dout, "_pilThread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_pilThread shutdown");
        }
    }

    if( _queueCCU711Thread.isRunning() )
    {
        if( ! _queueCCU711Thread.tryJoinFor(Cti::Timing::Chrono::milliseconds(1500)) )
        {
            CTILOG_ERROR(dout, "_queueCCU711Thread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_queueCCU711Thread shutdown");
        }
    }

    if( _kickerCCU711Thread.isRunning() )
    {
        if( ! _kickerCCU711Thread.tryJoinFor(Cti::Timing::Chrono::milliseconds(1500)) )
        {
            CTILOG_ERROR(dout, "_kickerCCU711Thread did not shutdown");
        }
        else
        {
            CTILOG_INFO(dout, "_kickerCCU711Thread shutdown");
        }
    }

    if(gConfigParms.isTrue("PORTER_START_VERIFICATIONTHREAD", true))
    {
        PorterVerificationThread.join();

        CTILOG_INFO(dout, "_verificationThread shutdown");
    }

    if( _statisticsThread.isRunning() )
    {
        while( ! _statisticsThread.tryJoinFor(Cti::Timing::Chrono::milliseconds(1500)) )
        {
            CTILOG_WARN(dout, "_statisticsThread has not shutdown");
        }

        CTILOG_INFO(dout, "_statisticsThread shutdown");
    }
}



void DebugKeyEvent(KEY_EVENT_RECORD *ke)
{
    // FUNCTION not called, but might still be useful?

    if(ke->bKeyDown)  fprintf(stdout, "Key     DOWN\n");
    else              fprintf(stdout, "Key NOT DOWN\n");

    fprintf(stdout, "Repeat Count = %d\n",ke->wRepeatCount);
    fprintf(stdout, "Key  Code %d = 0x%04X\n", ke->wVirtualKeyCode, ke->wVirtualKeyCode);
    fprintf(stdout, "Scan Code %d = 0x%04X\n", ke->wVirtualScanCode, ke->wVirtualScanCode);
    fprintf(stdout, "Char         = %x\n",ke->uChar.AsciiChar);

    fprintf(stdout, "Control State: 0x%08X\n", ke->dwControlKeyState);

    if(ke->dwControlKeyState & CAPSLOCK_ON              )
    {
        fprintf(stdout, "\tCapsLock ON\n");
    }
    if(ke->dwControlKeyState & ENHANCED_KEY             )
    {
        fprintf(stdout, "\tEnhanced Key\n");
    }
    if(ke->dwControlKeyState & LEFT_ALT_PRESSED         )
    {
        fprintf(stdout, "\tL Alt      \n");
    }
    if(ke->dwControlKeyState & LEFT_CTRL_PRESSED        )
    {
        fprintf(stdout, "\tL Ctrl     \n");
    }
    if(ke->dwControlKeyState & NUMLOCK_ON               )
    {
        fprintf(stdout, "\tNumLock ON\n");
    }
    if(ke->dwControlKeyState & RIGHT_ALT_PRESSED        )
    {
        fprintf(stdout, "\tR Alt      \n");
    }
    if(ke->dwControlKeyState & RIGHT_CTRL_PRESSED       )
    {
        fprintf(stdout, "\tR Ctrl     \n");
    }
    if(ke->dwControlKeyState & SCROLLLOCK_ON            )
    {
        fprintf(stdout, "\tScroll Lock\n");
    }
    if(ke->dwControlKeyState & SHIFT_PRESSED            )
    {
        fprintf(stdout, "\tShift      \n");
    }

    fprintf(stdout, "\n");

}

static void applyRefreshRepeaterRoles(const long device_id, CtiDeviceSPtr device, void *dbchg)
{
    extern Cti::Pil::PilServer PIL;

    if( !isRepeater(device->getType()) )
    {
        return;
    }

    if( device->isInhibited() )
    {
        return;
    }

    const CtiDBChangeMsg *pChg = static_cast<const CtiDBChangeMsg *>(dbchg);

    if( !pChg || RouteManager.isRepeaterRelevantToRoute(device->getID(), pChg->getId()) )
    {
        CTILOG_INFO(dout, "Refreshing roles for repeater \""<< device->getName() <<"\"");

        PIL.putQueue( new CtiRequestMsg(device->getID(), "putconfig emetcon install") );
    }
}


void refreshRepeaterRoutes(const CtiDBChangeMsg *pChg)
{
    static CtiTime lastRefresh(PASTDATE);
    const unsigned long porterAutoRoleRate = gConfigParms.getValueAsULong("PORTER_AUTOROLE_RATE", 0);
    bool refreshNeeded = false;

    if( porterAutoRoleRate && CtiTime::now() > (lastRefresh + porterAutoRoleRate) )
    {
        CTILOG_INFO(dout, "AutoRole Download timer (master.cfg: PORTER_AUTOROLE_RATE) expired. "
                "All repeaters will have their role table refreshed.");

        refreshNeeded = true;
    }
    else if(pChg != NULL)
    {
        CTILOG_INFO(dout, "Reloading repeater routes based upon db change. "
                "All relevant repeaters will have their role table refreshed.");

        refreshNeeded = true;
    }

    if(refreshNeeded)
    {
        try
        {
            lastRefresh = CtiTime::now();      // Update our static variable.

            DeviceManager.apply(applyRefreshRepeaterRoles, (void *)pChg);

            CTILOG_INFO(dout, "Downloading routes to all CCU-711s on repeater route change");

            PortManager.apply( applyLoadAllRoutes, NULL );
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
}


bool isPortDbChange(const CtiDBChangeMsg *pChg)
{
    if( ! pChg )
    {
        return true;
    }

    switch( resolvePAOCategory(pChg->getCategory()) )
    {
        case PAO_CATEGORY_PORT:
        {
            return true;
        }

        case PAO_CATEGORY_DEVICE:
        {
            //  Special case for RFN-1200 device/port hybrid.  Future RF-DA device types should probably be handled here as well.
            return resolveDeviceType(pChg->getObjectType()) == TYPE_RFN1200;
        }
    }

    return false;
}


INT RefreshPorterRTDB(const CtiDBChangeMsg *pChg)
{
    INT   status = ClientErrors::None;

    // Reload the globals used by the porter app too.
    InitYukonBaseGlobals();
    LoadPorterGlobals();

    // Make sure the database is available before we try to load anything from it.
    if ( ! canConnectToDatabase() )
    {
        return -1;
    }

    if( !PorterQuit && (pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb)) )
    {
        ReloadStateNames();
    }

    if(!PorterQuit && isPortDbChange(pChg) )
    {
        CTILOG_INFO(dout, "Reloading all ports based upon DB change");

        CtiPortManager::coll_type::writer_lock_guard_t guard(PortManager.getLock());
        PortManager.RefreshList();

        if( pChg )
        {
            if( CtiPortSPtr port = PortManager.getPortById(pChg->getId()) )
            {
                port->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
            }
        }
    }

    if(!PorterQuit && (pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) ) )
    {
        CtiDeviceManager::coll_type::writer_lock_guard_t guard(DeviceManager.getLock());

        if(pChg == NULL)
        {
            DeviceManager.refreshAllDevices();

            DeviceManager.apply(attachRouteManagerToDevice, &RouteManager);
            DeviceManager.apply(attachPointManagerToDevice, &PorterPointManager);
            Cti::ConfigManager::initialize();
            DeviceManager.refreshGroupHierarchy();
            DeviceManager.apply(applyQueuedDevicePortMatchup, &PortManager);
        }
        else
        {
            const long chgid = pChg->getId();

            DeviceManager.refreshDeviceByID(chgid, pChg->getCategory(), pChg->getObjectType());

            if( CtiDeviceSPtr pDev = DeviceManager.getDeviceByID(chgid) )
            {
                pDev->setRouteManager(&RouteManager);
                pDev->setPointManager(&PorterPointManager);

                if( pDev->isGroup() ) //Doing this call here saves us a tiny bit of time! yay!
                {
                    DeviceManager.refreshGroupHierarchy(chgid);
                }

                if(pDev->getDeviceQueueHandler() != NULL)
                {
                    CtiPortSPtr port = PortManager.getPortById(pDev->getPortID());
                    if(port)
                    {
                        port->addDeviceQueuedWork(pDev->getID(), -1);
                    }
                }

                if( pDev->getType() == TYPE_TAPTERM )
                {
                    try
                    {
                        PortManager.apply( applyNewLoad, NULL );
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
            }
        }
    }

    if(PorterQuit)
    {
        return -1;
    }

    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
    {
        Cti::readers_writer_lock_t::writer_lock_guard_t lock{ RouteManager.getLock() };

        RouteManager.RefreshList();

        //  Assign transmitter devices and fill macro subroute pointers
        populateRouteAssociations(&DeviceManager, &RouteManager);

        refreshRepeaterRoutes(pChg);
    }

    if(pChg != NULL && (pChg->getDatabase() == ChangeConfigDb))
    {
        Cti::ConfigManager::handleDbChange(pChg->getId(), pChg->getCategory(), pChg->getObjectType(), pChg->getTypeOfChange());
    }


    if(pChg != NULL && (pChg->getDatabase() == ChangePointDb))
    {
        if(pChg->getTypeOfChange() == ChangeTypeDelete)
        {
            PorterPointManager.erase(pChg->getId());

            if( getDebugLevel() & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "Deleting pointid ("<< pChg->getId() <<")");
            }
        }
        else
        {
            PorterPointManager.refreshList(pChg->getId(), 0, resolvePointType(pChg->getObjectType()));

            if( getDebugLevel() & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "Refreshing pointid ("<< pChg->getId() <<")");
            }

            // We also need to reload all the point groups to make certain the control strings get updated.
            if( pChg->getId() != 0 )
            {
                if(pChg->getObjectType() == "Status") // only status points can be in point groups.
                {
                    DeviceManager.refreshPointGroups();
                    LoadCommFailPoints(pChg->getId()); // only status points can be comm fail points
                }
            }
            else
            {
                DeviceManager.refreshPointGroups();
            }
        }
    }

    if(pChg == NULL)
    {
        if(gConfigParms.isTrue("PORTER_LOAD_ALL_POINTS"))
        {
            CTILOG_INFO(dout, "PORTER_LOAD_ALL_POINTS is set");
            PorterPointManager.refreshList();
        }

        try
        {
            LoadCommFailPoints();
            PortManager.apply( applyNewLoad, NULL );
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    /* see if we need to start process's for queuing */
    {
        if( (DeviceManager.containsType(TYPE_CCU711) || DeviceManager.containsType(TYPE_CCU721))
            && !(_queueCCU711Thread.isRunning()) )
        {
            _queueCCU711Thread.start();

            if( ! _kickerCCU711Thread.isRunning() )
            {
                _kickerCCU711Thread.start();
            }
        }
    }

    return status;
}


void LoadPorterGlobals(void)
{
    string Temp;

    if(!(Temp = gConfigParms.getValueAsString("PORTER_RELOAD_RATE")).empty())
    {
        PorterRefreshRate = std::max((unsigned long)gConfigParms.getValueAsULong("PORTER_RELOAD_RATE"), (unsigned long)86400);
    }

    /* Check the number of queue octets allowed */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_MAXOCTS")).empty())
    {
        MaxOcts = atoi(Temp.c_str());
        if(MaxOcts < 81)
        {
            MaxOcts = 81;
        }
        else if(MaxOcts > 261)
        {
            MaxOcts = 261;
        }
    }
    else
    {
        MaxOcts = 261;
    }

    /* Check if we are using L and G LCU's */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_LANDGLCUS")).empty() && (!(stricmp ("YES", Temp.c_str()))))
    {
        LAndGLCUs = TRUE;
    }

    /*  set the frequency info -- Defaults to 12.5 */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_FREQ")).empty())
    {
        /* Decode the system frequency */
        if(!(stricmp (Temp.c_str(), "7.3")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 51;
        }
        else if(!(stricmp (Temp.c_str(), "7.8")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 48;
        }
        else if(!(stricmp (Temp.c_str(), "8.3")))
        {
            DLCFreq1 = 49;
            DLCFreq2 = 12;
        }
        else if(!(stricmp (Temp.c_str(), "8.8")))
        {
            DLCFreq1 = 124;
            DLCFreq2 = 55;
        }
        else if(!(stricmp (Temp.c_str(), "9.6")))
        {
            DLCFreq1 = 17;
            DLCFreq2 = 7;
        }
        else if(!(stricmp (Temp.c_str(), "10.4")))
        {
            DLCFreq1 = 121;
            DLCFreq2 = 46;
        }
        else if(!(stricmp (Temp.c_str(), "12.5")))
        {
            DLCFreq1 = 94;
            DLCFreq2 = 37;
        }
        else if(!(stricmp (Temp.c_str(), "13.8")))
        {
            DLCFreq1 = 123;
            DLCFreq2 = 35;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_DEBUGLEVEL")).empty())
    {
        char *eptr;
        PorterDebugLevel = strtoul(Temp.c_str(), &eptr, 16);
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_TSYNC_RATE")).empty())
    {
        if(!(TimeSyncRate = atol (Temp.c_str())))
        {
            /* Unable to convert so assume "NOT EVER" */
            TimeSyncRate = 0L;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU5000_QUEUEBUSY")).empty())
    {
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU_QUEUEBUSY")).empty())
    {
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_CCU_DELAY_FILE")).empty())
    {
        gDelayDatFile = Temp;

    }

    if(isDebugLudicrous())
    {
        CTILOG_DEBUG(dout, "Using CCU Delay information from "<< gDelayDatFile);
    }

    if(DebugLevel & 0x0001)
    {
        Cti::FormattedList logItems;
        logItems.add("PORTER_RELOAD_RATE")      << PorterRefreshRate;
        logItems.add("PORTER_MAXOCTS")          << MaxOcts;
        logItems.add("PORTER_MCT400SERIESSPID") << (int)gMCT400SeriesSPID;

        CTILOG_DEBUG(dout, "Loading porter globals:"<<
                logItems);
    }
}




void DisplayTraceList( CtiPortSPtr Port, list< CtiMessage* > &traceList, bool consume)
{
    try
    {
        Port->fileTraces(traceList);

        for( int attempt = 5; attempt >= 0; attempt-- )
        {
            Cti::TryLockGuard<CtiCriticalSection> coutTryGuard(coutMux);

            if( ! coutTryGuard.isAcquired() && attempt )
            {
                Sleep(100);

                continue;
            }

            for each( const CtiMessage *msg in traceList )
            {
                if( msg )
                {
                    const CtiTraceMsg &traceMsg = static_cast<const CtiTraceMsg &>(*msg);
                    const std::string &text = traceMsg.getTrace();

                    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), (WORD)traceMsg.getAttributes());
                    DWORD bytesWritten;
                    WriteConsole(GetStdHandle(STD_OUTPUT_HANDLE), text.data(), text.length(), &bytesWritten, NULL);

                    if( traceMsg.isEnd() )
                    {
                        WriteConsole(GetStdHandle(STD_OUTPUT_HANDLE), "\n", 1, &bytesWritten, NULL);
                    }
                }
            }

            SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);

            break;
        }

        if(consume)
        {
            delete_container( traceList );
            traceList.clear();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

string GetDeviceName( ULONG id )
{
    string name;

    CtiDeviceSPtr pDev = DeviceManager.getDeviceByID( id );

    if(pDev)
    {
        name = pDev->getName();
    }

    return name;
}

void KickPIL()
{
    // TEARDOWN

    if( _pilThread.isRunning() )
    {
        _pilThread.interrupt();

        _pilThread.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(15));
    }

    Sleep(2500);

    // AND RESTART

    _pilThread.start();

    return;
}

bool processInputFunction(CHAR Char)
{
    bool process_fail = false;
    PSZ Environment;

    switch(Char)
    {
    case 0x68:              // alt - h
    case 0x3f:              // alt - ?
        {
            // Print some instructions
            cout << "Port Control" << endl;
            cout << endl;
            cout << "Alt - C     Reset (cold start) all CCU 711's in system" << endl;
            cout << "Alt - D     Send Emetcon \"Doubles\" to field devices" << endl;
            cout << "Alt - E     Trace error communications only" << endl;
            cout << "Alt - F     Toggle trace filtering off, or reload from environment." << endl;
            cout << "             PORTER_TRACE_PORT" << endl;
            cout << "             PORTER_TRACE_REMOTE" << endl;
            cout << "Alt - H     This help screen" << endl;
            cout << "Alt - L     Toggle printer logging" << endl;
            cout << "Alt - P     Purge all port queues. (Careful)" << endl;
            cout << "Alt - Q     Display port queue counts / stats" << endl;
            cout << "Alt - R     Download all CCU Default Routes" << endl;
            cout << "Alt - S     Issue a system wide timesync" << endl;
            cout << "Alt - T     Trace all communications" << endl;
            cout << "Alt - Z     Reload all CPARMs" << endl;
            cout << endl;

            break;
        }
    case 0x74:              // alt-t
        {
            TraceFlag = !TraceFlag;
            if(TraceFlag)
            {
                if(TraceErrorsOnly)
                {
                    CTILOG_INFO(dout, "Trace is Now On for Errors Only");
                }
                else
                {
                    CTILOG_INFO(dout, "Trace is Now On for All Messages");
                }
            }
            else
            {
                CTILOG_INFO(dout, "Trace is Now Off for All Messages");
            }

            break;
        }
    case 0x7a:              // alt-z
        {
            gConfigParms.RefreshConfigParameters();
            LoadPorterGlobals();

            CTILOG_INFO(dout, "CPARMs have been reloaded");

            break;
        }
    case 0x65:              // alt-e
        {
            TraceErrorsOnly = !TraceErrorsOnly;
            if(TraceErrorsOnly)
            {
                TraceFlag = TRUE;
                CTILOG_INFO(dout, "Trace is Now On for Errors Only");
            }
            else if(TraceFlag)
            {
                CTILOG_INFO(dout, "Trace is Now On for All Messages");
            }
            else
            {
                CTILOG_INFO(dout, "Trace is Now Off for All Messages");
            }

            break;

        }
    case 0x72:              // alt-r
        {
            CTILOG_INFO(dout, "Downloading Routes to All CCU-711's");

            PortManager.apply( applyLoadAllRoutes, NULL );
            break;
        }
    case 0x64:              // alt-d
        {
            Double = !Double;
            if(Double)
            {
                CTILOG_INFO(dout, "Commands will be sent double");
            }
            else
            {
                CTILOG_INFO(dout, "Commands will not be sent double");
            }

            break;
        }
    case 0x66:              // alt-f trace filter.
        {
            if(TracePort || TraceRemote)
            {
                CTILOG_INFO(dout, "Trace filter is now off");
                TracePort = 0;
                TraceRemote = 0;
            }
            else
            {
                if(!(CTIScanEnv ("PORTER_TRACE_PORT", &Environment)))
                {
                    TracePort = atoi (Environment);
                    CTILOG_INFO(dout, "Filtering Traces for Port "<< TracePort);
                }
                else if(!(CTIScanEnv ("PORTER_TRACE_REMOTE",  &Environment)))
                {
                    TraceRemote = atoi (Environment);
                    CTILOG_INFO(dout, "Filtering Traces for Remote "<< TraceRemote);
                }
                else
                {
                    CTILOG_INFO(dout, "Neither PORTER_TRACE_PORT nor PORTER_TRACE_REMOTE defined in the environment");
                }
            }
            break;
        }
    case 0x6d:              // alt-m trace filter.
        {
#ifndef DEBUG_MEMORY
            CTILOG_INFO(dout, "Module not compiled for Memory Dumps");
            _CrtDumpMemoryLeaks();
#else
            fprintf(stderr, "Memory Dump ------- \n");
            fprintf(stderr, "  Start Memory Deltas ------- \n");
            _dump_allocated_delta(10);
            fprintf(stderr, "  Stop  Memory Deltas ------- \n\n");
            fprintf(stderr, "  Start Allocated Memory  ------- \n");
            _dump_allocated(10);
            fprintf(stderr, "  Stop  Allocated Memory  ------- \n\n");
            fprintf(stderr, "  Start Heap Check  ------- \n");
            _heap_check();
            fprintf(stderr, "  Stop  Heap Check  ------- \n");
            fprintf(stderr, "Memory Dump ------- \n");
#endif

            break;
        }
    case 0x63:              // alt-c
        {
            /* Issue a cold start to each CCU */
            CTILOG_INFO(dout, "Issuing Cold Starts to All CCU-711's");

            PortManager.apply( applyColdStart, NULL);

            break;
        }
    case 0x6b:              // alt-k
        {
            CTILOG_INFO(dout, "Kicking PIL.");

            KickPIL();

            CTILOG_INFO(dout, "PIL has been kicked.");

            break;
        }
    case 0x73:              // alt-s
        {
            /* Force a time sync */
            CTILOG_INFO(dout, "Forcing System Wide Time Sync");

            //CTIPostEventSem (TimeSyncSem);
            SetEvent(hPorterEvents[P_TIMESYNC_EVENT]);

            break;
        }
    case 0x70:              // alt-p
        {
            /* DUMP the queues */
            PortManager.apply( applyPortQueuePurge, NULL );
            break;
        }
    case 0x71:              // alt-q
        {
            PortManager.apply( applyPortQueueReport, (void*)1 );
            DeviceManager.apply( applyDeviceQueueReport, NULL );

            CTILOG_INFO(dout, "There are "<< OutMessageCount() <<" OutMessages held by Port Control.");

            break;
        }
    case 0x77:              // alt-w
        {
            reportOnWorkObjects();
            // PortManager.apply( applyPortWorkReport, NULL );
            break;
        }
    default:
        process_fail = true;
        break;
    }

    return process_fail;
}




static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine )
{
    static ULONG lastAlloc = 0;
    static ULONG prevLastAlloc = 0;
    static ULONG pprevLastAlloc = 0;

    int alloc_cnt = 0;
    int dealloc_cnt = 0;

    if( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) )
    {
        if(lRequest > 1000000 )
        {
            if( (nSize == 24) )
            {
                if(lastAlloc == (lRequest - 1))
                {
                    alloc_cnt++;

                    if(prevLastAlloc == (lRequest - 2))
                    {
                        alloc_cnt++;

                        if(pprevLastAlloc == (lRequest - 3))
                        {
                            alloc_cnt++;
                        }
                        pprevLastAlloc = prevLastAlloc;
                    }
                    prevLastAlloc = lastAlloc;
                }

                alloc_cnt++;

                lastAlloc = lRequest;
            }
            if((nSize == 52) )
            {
                alloc_cnt++;
            }
            else if((nSize == 1316) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 68) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 63) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 416) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 40) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 164) )
            {
                alloc_cnt++;
            }
        }
    }
    else if(nAllocType == _HOOK_FREE)
    {
        // Hmm, freeing memory here!
        // pvData is being freed
        if(pvData != 0)
        {
            dealloc_cnt++;
        }
    }

#ifdef IGNORE_CRT_ALLOC
    if(_BLOCK_TYPE(nBlockUse) == _CRT_BLOCK)  // Ignore internal C runtime library allocations
        return TRUE;
#endif
    extern int _crtDbgFlag;

    if( ((_CRTDBG_ALLOC_MEM_DF & _crtDbgFlag) == 0) && ( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) ) )
    {
        // Someone has pdisabled that the runtime should log this allocation
        // so we do not log this allocation
        if(pfnOldCrtAllocHook != NULL)
            pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
        return TRUE;
    }

    // call the previous alloc hook
    if(pfnOldCrtAllocHook != NULL)
        pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
    return TRUE; // allow the memory operation to proceed
}

bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero)
{
    bool retVal = false;
    bool isCommFail = !wasFailure; //This is inverted for some reason

    CtiDeviceSPtr device = DeviceManager.getDeviceByID(deviceID);

    if( device )
    {
        if( device->adjustCommCounts(isCommFail, retryGtZero) )
        {
            commFail(device);
        }
        retVal = true;
    }

    return retVal;
}

void commFail(const CtiDeviceSPtr &Device)
{
    extern CtiClientConnection VanGoghConnection;

    CtiPointSPtr  pPoint;
    char temp[80];
    LONG pointid;
    bool state = Device->isCommFailed();        // Ask the device if it has accumulated enough errors to be failed!

    //if( NULL != (pPoint = Device->getDevicePointOffsetTypeEqual(COMM_FAIL_OFFSET, StatusPointType)) )
    if( 0 != (pointid = GetCommFailPointID(Device->getID())) )
    {
        sprintf(temp, "Communication status %s", state ? "FAILED" : "GOOD");
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pointid, (double)(state ? STATE_CLOSED : STATE_OPENED), NormalQuality, StatusPointType, temp);

        if(pData != NULL)
        {
            VanGoghConnection.WriteConnQue(pData, CALLSITE);
        }
    }
    else if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && Device && state)
    {
        CTILOG_DEBUG(dout, Device->getName() <<" would be COMM FAILED if it had offset "<< COMM_FAIL_OFFSET <<" defined");
    }
}

void LoadCommFailPoints(LONG ptid)
{
    if(ptid == 0)
    {
        commFailDeviceIDToPointIDMap.clear();       // No more map.
    }
    else
    {
        // remove the map elements with ptid as their value

        CtiCommFailPoints_t::iterator entry = commFailDeviceIDToPointIDMap.begin();

        while ( entry != commFailDeviceIDToPointIDMap.end() )
        {
            if ( entry->second == ptid )
            {
                entry = commFailDeviceIDToPointIDMap.erase( entry );
            }
            else
            {
                ++entry;
            }
        }
    }

    LONG did, pid;
    string sql = string("select paobjectid, pointid from point where pointoffset = ") + CtiNumStr(COMM_FAIL_OFFSET) + string(" and pointtype = 'Status'");
    if(ptid) sql += string(" and pointid = " + CtiNumStr(ptid));

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    while(rdr() && rdr.isValid())
    {
        rdr["paobjectid"] >> did;
        rdr["pointid"] >> pid;


        std::pair< CtiCommFailPoints_t::iterator, bool > resultpair = commFailDeviceIDToPointIDMap.insert( std::make_pair(did, pid) );

        if(resultpair.second == false)           // Insert was unsuccessful (should never be this way!).
        {
            CTILOG_ERROR(dout, "Pao "<< did <<" has multiple pids at offset 2000?");
        }
    }

    return;
}

LONG GetCommFailPointID(LONG devid)
{
    LONG pid = 0;

    CtiCommFailPoints_t::iterator itr = commFailDeviceIDToPointIDMap.find( devid );

    if( itr != commFailDeviceIDToPointIDMap.end() )
    {
        pid = (*itr).second;
    }

    return pid;
}

void reportOnWorkObjects()
{
    extern CtiClientConnection VanGoghConnection;
    PortQueueCounts queueCounts{ 0, std::make_unique <CtiMultiMsg>() };
    PortManager.apply( applyPortWorkReport, &queueCounts );

    if( !totalWorkCountPointId )
    {
        totalWorkCountPointId = GetPIDFromDeviceAndOffset( 0, 1500 );
    }

    if( totalWorkCountPointId )
    {
        auto pData = std::make_unique<CtiPointDataMsg>( totalWorkCountPointId, queueCounts.totalWorkCount, NormalQuality, AnalogPointType );

        if ( pData )
        {
            queueCounts.portQueueCountMsgs->insert( pData.release() );
        }
    }

    if( queueCounts.portQueueCountMsgs->getCount() ) 
    {
        if ( !VanGoghConnection.WriteConnQue(queueCounts.portQueueCountMsgs.release(), CALLSITE) )
        {
            CTILOG_INFO( dout, "Port queue counts and total work count messages sent successfully." );
        }
    }
}
