/*-----------------------------------------------------------------------------*
*
* File:   pilSystemMsgHandler.cpp
*
* Date:   1/5/2007
*
* Author: Jess Otteson
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/07/21 20:38:27 $
*
* HISTORY      :
* $Log: systemmsgthread.cpp,v $
* Revision 1.7  2008/07/21 20:38:27  jotteson
* YUK-4556 CCU queue backs up and returns no error when uninitialized
* Added expiration functionality regardless of port's state.
* Added 24 hour default expiration.
* Modified Cancellation and Expiration to return error and update statistics.
*
* Revision 1.6  2008/07/17 20:51:52  mfisher
* YUK-6188 PIL to Porter group submission is very slow
* Added readers/writer lock
*
* Revision 1.5  2008/07/08 22:54:57  mfisher
* YUK-6077 Several lists in PIL and Porter are not sorted
* YUK-6113 Large group reads block out higher-priority commands
* YUK-6156 MCT-410 "getvalue kwh" command execution is slow
* removed a slow CPARM lookup
* added support to purge requests from CtiLocalConnect
*
* Revision 1.4  2007/10/18 21:12:18  jotteson
* Fix for bug in timing code that makes debugging Dispatch difficult.
*
* Revision 1.3  2007/06/25 18:59:53  mfisher
* added thread names
*
* Revision 1.2  2007/02/22 17:46:42  jotteson
* Bug Id: 814, 651
* Completed integration of MACS with new system messages. QueueWrites were changed to be sure they put the proper ID into the queues. New messaging used, new device interface used.
*
* Revision 1.1  2007/01/22 21:40:08  jotteson
* Initial Revision. Thread in porter that executes system messages and returns results to requestor.
*
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>

#include "cmdparse.h"
#include "counter.h"
#include "cparms.h"
#include "device_queue_interface.h"
#include "msg_cmd.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_queuedata.h"
#include "msg_requestcancel.h"
#include "systemmsgthread.h"
#include "queues.h"
#include "port_base.h"
#include "portdecl.h"
#include <list>

static LARGE_INTEGER perfFrequency;
using namespace std;

#ifndef PERF_TO_MS
    #define PERF_TO_MS(b,a,p) (UINT)(((b).QuadPart - (a).QuadPart) / ((p).QuadPart / 1000L))
#endif

namespace Cti {
namespace Porter {

SystemMsgThread::SystemMsgThread(CtiFIFOQueue< CtiMessage > *inputQueue)
{
    _input = inputQueue;
    QueryPerformanceFrequency(&perfFrequency);
}

SystemMsgThread::~SystemMsgThread()
{
}

void SystemMsgThread::setDeviceManager(CtiDeviceManager *devMgr)
{
    if(devMgr != NULL)
    {
        _pDevManager = devMgr;
    }
}

void SystemMsgThread::setPortManager(CtiPortManager *portMgr)
{
    if(portMgr != NULL)
    {
        _pPortManager = portMgr;
    }
}

void SystemMsgThread::setPilToPorter(CtiLocalConnect<OUTMESS, INMESS> *pilToPorter)
{
    if(pilToPorter != NULL)
    {
        _pPilToPorter = pilToPorter;
    }
}

void SystemMsgThread::run( void )
{
    UINT sanity = 0;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " SystemMessageHandlerThread TID: " << CurrentTID () << endl;
    }

    SetThreadName(-1, "SysMsgThd");

    try
    {
        while( !isSet(SHUTDOWN) )
        {
            try
            {
                CtiRequestMsg *msg = (CtiRequestMsg *)_input->getQueue();

                if( msg != NULL)
                {
                    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Beginning processing of system message." << endl;
                    }

                    executeSystemMessage(msg);

                    delete msg;
                    msg = NULL;

                }

                if(!(++sanity % 300))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " SystemMessageHandlerThread run() Thread Active " << endl;
                    }
                }

                sleep(1000);
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Verification thread received shutdown - writing all pending codes to DB" << endl;
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Exception in SystemMsgThread, thread exiting" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PendingOperationThread TID: " << CurrentTID() << " shutting down" << endl;
    }

}

void SystemMsgThread::push(CtiRequestMsg *entry)
{
    if( entry )
    {
        _input->putQueue(entry);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - SystemMsgThread::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void SystemMsgThread::executeSystemMessage(CtiRequestMsg *msg)
{
    CtiCommandParser parse(msg->CommandString());

    if( parse.isKeyValid("port_entries") )
    {
        executePortEntryRequest(msg, parse);
    }
    else if( parse.isKeyValid("request_count") )
    {
        executeRequestCount(msg, parse);
    }
    else if( parse.isKeyValid("request_cancel") )
    {
        executeCancelRequest(msg, parse);
    }
}

void SystemMsgThread::executePortEntryRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    unsigned int entries = 0;
    string resultString;
    CtiConnection  *Conn = NULL;
    CtiPortSPtr port;
    vector <CtiPortManager::ptr_type> portList;
    ULONG requestID = msg->OptionsField();
    CtiQueueDataMsg *response = NULL;

    if( _pPortManager != NULL )
    {
        if( requestID != 0 )
        {
            CtiPortSPtr port = _pPortManager->PortGetEqual(msg->OptionsField());
            if( port )
            {
                entries = port->getWorkCount();

                response = CTIDBG_new CtiQueueDataMsg(port->getPortID(), entries, port->getPortTiming(), 0, 0, msg->UserMessageId());
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Received port entry request for unknown port " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            getPorts(portList);

            vector<CtiPortManager::ptr_type>::iterator portIter;
            for( portIter = portList.begin(); portIter != portList.end(); portIter ++ )
            {
                port = *portIter;

                if( port )
                {
                    entries += port->getWorkCount(requestID);
                }
            }

            response = CTIDBG_new CtiQueueDataMsg(0, entries, 0, 0, 0, msg->UserMessageId() );
        }
    }

    if( response != NULL )
    {
        if( (Conn = ((CtiConnection*)msg->getConnectionHandle())) != NULL )
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                response->dump();
            }

            Conn->WriteConnQue(response);
        }
        else
        {
            delete response;
        }
    }
}

void SystemMsgThread::executeRequestCount(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    unsigned int entries = 0;
    string resultString;
    ULONG requestID = msg->OptionsField();;
    ULONG count, priority;
    CtiDeviceSPtr tempDev;
    CtiPortSPtr port;
    vector <long> queuedDevices;
    Cti::DeviceQueueInterface* queueInterface;
    vector <CtiPortManager::ptr_type> portList;
    CtiConnection  *Conn = NULL;
    CtiQueueDataMsg *response = NULL;

    if( _pPortManager != NULL && _pDevManager != NULL )
    {
        if( requestID != 0 )
        {
            getPorts(portList);

            vector<CtiPortManager::ptr_type>::iterator portIter;
            for( portIter = portList.begin(); portIter != portList.end(); portIter ++ )
            {
                port = *portIter;

                if( port )
                {
                    entries += port->getWorkCount(requestID);
                    queuedDevices = port->getQueuedWorkDevices();

                    vector<long>::iterator devIter;
                    for( devIter = queuedDevices.begin(); devIter!= queuedDevices.end(); devIter++ )
                    {
                        tempDev = _pDevManager->getEqual(*devIter);

                        if( tempDev )
                        {
                            queueInterface = tempDev->getDeviceQueueHandler();

                            if( queueInterface != NULL )
                            {
                                queueInterface->getQueueRequestInfo(requestID, count, priority);
                                entries += count;
                            }
                        }
                    }
                }
            }

            response = CTIDBG_new CtiQueueDataMsg(0, 0, 0, requestID, entries, msg->UserMessageId());
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request count recieved with no request ID " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if( response != NULL )
    {
        if( (Conn = ((CtiConnection*)msg->getConnectionHandle())) != NULL )
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                response->dump();
            }

            Conn->WriteConnQue(response);
        }
        else
        {
            delete response;
        }
    }
}


void SystemMsgThread::executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    unsigned int entries = 0;
    string resultString;
    ULONG requestID = msg->OptionsField();
    ULONG count, priority;
    CtiDeviceSPtr tempDev;
    CtiPortSPtr port;
    Cti::DeviceQueueInterface *queueInterface;
    vector <long> queuedDevices;
    vector <CtiPortManager::ptr_type> portList;
    CtiConnection  *Conn = NULL;
    CtiRequestCancelMsg *response = NULL;

    if( _pPilToPorter != NULL )
    {
        _pPilToPorter->purgeRequest(requestID);
    }

    if( _pPortManager != NULL && _pDevManager != NULL )
    {
        if( requestID != 0 )
        {
            getPorts(portList);

            vector<CtiPortManager::ptr_type>::iterator portIter;
            for( portIter = portList.begin(); portIter != portList.end(); portIter ++ )
            {
                port = *portIter;

                if( port )
                {
                    if( port->getWorkCount(requestID) > 0 )
                    {
                        // Here we are trying to save the horrors of CleanQueue from being called without cause.
                        entries += CleanQueue(port->getPortQueueHandle(), (void *)requestID, findRequestIDMatch, cleanupOrphanOutMessages);
                    }

                    queuedDevices = port->getQueuedWorkDevices();

                    vector<long>::iterator devIter;
                    for( devIter = queuedDevices.begin(); devIter!= queuedDevices.end(); devIter++ )
                    {
                        tempDev = _pDevManager->getEqual(*devIter);

                        if( tempDev )
                        {
                            queueInterface = tempDev->getDeviceQueueHandler();

                            if( queueInterface != NULL )
                            {
                                list<void*> omList;
                                queueInterface->retrieveQueueEntries(findRequestIDMatch, (void *)requestID, omList);
                                entries += omList.size();
                                list<void*>::iterator iter = omList.begin();
                                for(; iter!= omList.end(); )
                                {
                                    OUTMESS *tempOM = (OUTMESS *)*iter;
                                    tempOM->Request.MacroOffset = 0;//No resubmitting this request, it is dead!
                                    SendError( tempOM, ErrorQueuePurged );
                                    iter = omList.erase(omList.begin());
                                }
                            }
                        }
                    }
                }
            }

            response = CTIDBG_new CtiRequestCancelMsg(requestID, entries, msg->UserMessageId());
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request cancel recieved with no request ID " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if( response != NULL )
    {
        if( (Conn = ((CtiConnection*)msg->getConnectionHandle())) != NULL )
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                response->dump();
            }

            Conn->WriteConnQue(response);
        }
        else
        {
            delete response;
        }
    }
}

void SystemMsgThread::getPorts(vector<CtiPortManager::ptr_type> &ports)
{
    CtiPortManager::coll_type::reader_lock_guard_t guard(_pPortManager->getLock());
    CtiPortManager::spiterator iter = _pPortManager->begin();
    CtiPortManager::spiterator end = _pPortManager->end();

    for( ; iter != end; iter++)
    {
        ports.push_back(iter->second);
    }
}

} //Namespace Cti::Porter
}
