#include "precompiled.h"

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

using namespace std;

namespace Cti {
namespace Porter {

SystemMsgThread::SystemMsgThread(CtiFIFOQueue< CtiMessage > &inputQueue,
                                 CtiDeviceManager &devMgr,
                                 CtiPortManager &portMgr,
                                 CtiLocalConnect<OUTMESS, INMESS> &pilToPorter) :
    _input(inputQueue),
    _devManager(devMgr),
    _portManager(portMgr),
    _pilToPorter(pilToPorter)
{
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
                if( CtiRequestMsg *msg = (CtiRequestMsg *)_input.getQueue(1000) )
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
            dout << CtiTime() << " SystemMessageHandlerThread received shutdown - exiting" << endl;
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Exception in SystemMsgThread, thread exiting" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " SystemMessageHandlerThread TID: " << CurrentTID() << " shutting down" << endl;
    }

}

void SystemMsgThread::push(CtiRequestMsg *entry)
{
    if( entry )
    {
        _input.putQueue(entry);
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
    if( ULONG portID = msg->GroupMessageId() )
    {
        if( CtiPortSPtr port = _portManager.getPortById(portID) )
        {
            unsigned int entries = port->getWorkCount();

            if( CtiConnection *Conn = ((CtiConnection*)msg->getConnectionHandle()) )
            {
                CtiQueueDataMsg *response = CTIDBG_new CtiQueueDataMsg(port->getPortID(), entries, port->getPortTiming(), 0, 0, msg->UserMessageId());

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
            dout << CtiTime() << " Received port entry request for unknown port " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void SystemMsgThread::executeRequestCount(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    if( ULONG requestID = msg->GroupMessageId() )
    {
        if( CtiConnection *Conn = (CtiConnection*)msg->getConnectionHandle() )
        {
            unsigned int entries = msg->OptionsField(); // set by Pil.

            for each( CtiPortSPtr port in _portManager.getPorts() )
            {
                entries += port->getWorkCount(requestID);

                for each( const long deviceId in port->getQueuedWorkDevices() )
                {
                    if( CtiDeviceSPtr tempDev = _devManager.getDeviceByID(deviceId) )
                    {
                        if( Cti::DeviceQueueInterface* queueInterface = tempDev->getDeviceQueueHandler() )
                        {
                            entries += queueInterface->getRequestCount(requestID);
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


void SystemMsgThread::executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    extern void cancelOutMessages(void *doSendError, void* om);

    ULONG requestID = msg->GroupMessageId();

    _pilToPorter.purgeRequest(requestID);

    if( requestID == 0 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Request cancel recieved with no request ID " << __FILE__ << " (" << __LINE__ << ")" << endl;

        return;
    }

    unsigned int entries = 0;

    for each( CtiPortSPtr port in _portManager.getPorts() )
    {
        if( port->getWorkCount(requestID) > 0 )
        {
            // Here we are trying to save the horrors of CleanQueue from being called without cause.
            entries += CleanQueue(port->getPortQueueHandle(), (void *)requestID, findRequestIDMatch, cancelOutMessages, (void *)1);
        }

        for each( long deviceId in port->getQueuedWorkDevices() )
        {
            if( CtiDeviceSPtr tempDev = _devManager.getDeviceByID(deviceId) )
            {
                if( Cti::DeviceQueueInterface *queueInterface = tempDev->getDeviceQueueHandler() )
                {
                    list<void*> omList;
                    queueInterface->retrieveQueueEntries(findRequestIDMatch, (void *)requestID, omList);
                    entries += omList.size();

                    for each( OUTMESS *tempOM in omList )
                    {
                        tempOM->Request.MacroOffset = 0;//No resubmitting this request, it is dead!
                        cancelOutMessages((void *)1,tempOM);
                    }
                }
            }
        }
    }

    if( CtiConnection *Conn = ((CtiConnection*)msg->getConnectionHandle()) )
    {
        CtiRequestCancelMsg *response = new CtiRequestCancelMsg(requestID, entries, msg->UserMessageId());

        if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
        {
            response->dump();
        }

        Conn->WriteConnQue(response);
    }
}

} //Namespace Cti::Porter
}
