#include "yukon.h"

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

using namespace std;

namespace Cti {
namespace Porter {

SystemMsgThread::SystemMsgThread(CtiFIFOQueue< CtiMessage > *inputQueue)
{
    _input = inputQueue;
    _pDevManager = NULL;
    _pPortManager = NULL;
    _pPilToPorter = NULL;
}

SystemMsgThread::~SystemMsgThread()
{
    _input = NULL;
    _pDevManager = NULL;
    _pPortManager = NULL;
    _pPilToPorter = NULL;
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
                CtiRequestMsg *msg = (CtiRequestMsg *)_input->getQueue(1000);

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
    vector <CtiPortManager::ptr_type> portList;
    ULONG portID = msg->GroupMessageId();
    CtiQueueDataMsg *response = NULL;

    if( _pPortManager != NULL )
    {
        if( portID != 0 )
        {
            CtiPortSPtr port = _pPortManager->PortGetEqual(portID);
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
    if( _pPortManager && _pDevManager )
    {
        if( ULONG requestID = msg->GroupMessageId() )
        {
            if( CtiConnection *Conn = (CtiConnection*)msg->getConnectionHandle() )
            {
                unsigned int entries = msg->OptionsField(); // set by Pil.

                vector<CtiPortManager::ptr_type> portList;

                getPorts(portList);

                for each( CtiPortSPtr port in portList )
                {
                    if( port )
                    {
                        entries += port->getWorkCount(requestID);

                        for each( const long deviceId in port->getQueuedWorkDevices() )
                        {
                            if( CtiDeviceSPtr tempDev = _pDevManager->getDeviceByID(deviceId) )
                            {
                                if( Cti::DeviceQueueInterface* queueInterface = tempDev->getDeviceQueueHandler() )
                                {
                                    entries += queueInterface->getRequestCount(requestID);
                                }
                            }
                        }
                    }
                }

                CtiQueueDataMsg *response = new CtiQueueDataMsg(0, 0, 0, requestID, entries, msg->UserMessageId());

                if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
                {
                    response->dump();
                }

                Conn->WriteConnQue(response);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Request count recieved with no request ID " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void SystemMsgThread::executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    extern void cancelOutMessages(void *doSendError, void* om);

    unsigned int entries = 0;
    string resultString;
    ULONG requestID = msg->GroupMessageId();
    ULONG priority;
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
                        entries += CleanQueue(port->getPortQueueHandle(), (void *)requestID, findRequestIDMatch, cancelOutMessages, (void *)1);
                    }

                    queuedDevices = port->getQueuedWorkDevices();

                    vector<long>::iterator devIter;
                    for( devIter = queuedDevices.begin(); devIter!= queuedDevices.end(); devIter++ )
                    {
                        tempDev = _pDevManager->getDeviceByID(*devIter);

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
                                    cancelOutMessages((void *)1,tempOM);
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
