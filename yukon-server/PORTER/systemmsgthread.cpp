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

namespace Cti::Pil {
extern DLLIMPORT CtiFIFOQueue< CtiMessage > ClientReturnQueue;
}

namespace Cti::Porter {

SystemMsgThread::SystemMsgThread(CtiFIFOQueue< CtiMessage > &inputQueue,
                                 CtiDeviceManager &devMgr,
                                 CtiPortManager &portMgr,
                                 StreamLocalConnection<OUTMESS, INMESS> &pilToPorter) :
    _input(inputQueue),
    _devManager(devMgr),
    _portManager(portMgr),
    _pilToPorter(pilToPorter)
{
}

void SystemMsgThread::run( void )
{
    UINT sanity = 0;

    CTILOG_INFO(dout, "SystemMsgThread started");

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
                        CTILOG_DEBUG(dout, "Beginning processing of system message");
                    }

                    executeSystemMessage(msg);

                    delete msg;
                    msg = NULL;

                }

                if(!(++sanity % 300))
                {
                    CTILOG_INFO(dout, "SystemMessageHandlerThread Thread Active");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }

        CTILOG_INFO(dout, "SystemMsgThread received shutdown - exiting");
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Exception in SystemMsgThread, thread exiting");
    }

    CTILOG_INFO(dout, "SystemMsgThread terminating");
}

void SystemMsgThread::push(CtiRequestMsg *entry)
{
    if( entry )
    {
        _input.putQueue(entry);
    }
    else
    {
        CTILOG_ERROR(dout, "cannot enqueue null pointer");
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

            auto response = std::make_unique<CtiQueueDataMsg>(port->getPortID(), entries, port->getPortTiming(), 0, 0, msg->UserMessageId());

            response->setConnectionHandle(msg->getConnectionHandle());

            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                CTILOG_DEBUG(dout, *response);
            }

            Pil::ClientReturnQueue.putQueue(response.release());
        }
        else
        {
            CTILOG_ERROR(dout, "Received port entry request for unknown port id "<< portID);
        }
    }
}

void SystemMsgThread::executeRequestCount(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    if( ULONG requestID = msg->GroupMessageId() )
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

        auto response = std::make_unique<CtiQueueDataMsg>(0, 0, 0, requestID, entries, msg->UserMessageId());

        response->setConnectionHandle(msg->getConnectionHandle());

        if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
        {
            CTILOG_DEBUG(dout, *response);
        }

        Pil::ClientReturnQueue.putQueue(response.release());
    }
    else
    {
        CTILOG_ERROR(dout, "Request count received with no request ID");
    }
}


void SystemMsgThread::executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    extern void cancelOutMessages(void *doSendError, void* om);

    ULONG requestID = msg->GroupMessageId();

    _pilToPorter.purgeRequest(requestID);

    if( requestID == 0 )
    {
        CTILOG_ERROR(dout, "Request cancel received with no request ID");
        return;
    }

    unsigned int entries = 0;

    for( auto port : _portManager.getPorts() )
    {
        if( port->getWorkCount(requestID) > 0 )
        {
            // Here we are trying to save the horrors of CleanQueue from being called without cause.
            entries += CleanQueue(port->getPortQueueHandle(), (void *)requestID, findRequestIDMatch, cancelOutMessages, (void *)1);
        }

        for( auto deviceId : port->getQueuedWorkDevices() )
        {
            if( auto tempDev = _devManager.getDeviceByID(deviceId) )
            {
                if( auto queueInterface = tempDev->getDeviceQueueHandler() )
                {
                    auto omList = queueInterface->retrieveQueueEntries(findRequestIDMatch, (void *)requestID);
                    
                    entries += omList.size();

                    for( auto outmess : omList )
                    {
                        outmess->Request.RetryMacroOffset = MacroOffset::none; //No resubmitting this request, it is dead!
                        cancelOutMessages((void *)1,outmess);
                    }
                }
            }
        }
    }

    auto response = std::make_unique<CtiRequestCancelMsg>(requestID, entries, msg->UserMessageId());

    response->setConnectionHandle(msg->getConnectionHandle());

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        CTILOG_DEBUG(dout, *response);
    }

    Pil::ClientReturnQueue.putQueue(response.release());
}

} //Namespace Cti::Porter

