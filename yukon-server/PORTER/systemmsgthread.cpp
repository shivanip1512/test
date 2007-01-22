/*-----------------------------------------------------------------------------*
*
* File:   pilSystemMsgHandler.cpp
*
* Date:   1/5/2007
*
* Author: Jess Otteson
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/22 21:40:08 $
*
* HISTORY      :
* $Log: systemmsgthread.cpp,v $
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
#include "msg_cmd.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_queuedata.h"
#include "systemmsgthread.h"
#include "port_base.h"

static LARGE_INTEGER perfFrequency;
using namespace std;

#ifndef PERF_TO_MS
    #define PERF_TO_MS(b,a,p) ((UINT)((b).QuadPart - (a).QuadPart) / ((UINT)(p).QuadPart / 1000L))
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

void SystemMsgThread::run( void )
{
    UINT sanity = 0;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " SystemMessageHandlerThread TID: " << CurrentTID () << endl;
    }

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
}

void SystemMsgThread::executePortEntryRequest(CtiRequestMsg *msg, CtiCommandParser &parse)
{
    unsigned int entries = 0;
    string resultString;
    CtiConnection  *Conn = NULL;
    CtiReturnMsg   *retMsg = CTIDBG_new CtiReturnMsg(msg->DeviceId(),
                                                    msg->CommandString(),
                                                    "Unknown Error",
                                                    0,
                                                    0,
                                                    0,
                                                    0,
                                                    msg->TransmissionId(),
                                                    msg->UserMessageId(),
                                                    0);

    if( _pPortManager != NULL )
    {
        if( msg->OptionsField() != 0 )
        {
            CtiPortSPtr port = _pPortManager->PortGetEqual(msg->OptionsField());
            if( port )
            {
                entries = port->getWorkCount();

                resultString = "There are ";
                resultString += entries;
                resultString += " entries on this port./n";
                retMsg->setResultString(resultString);
                retMsg->insert(CTIDBG_new CtiQueueDataMsg(port->getPortID(), entries, port->getPortTiming()));
            }
            else
            {
                resultString = "Error, unknown port./n";
                retMsg->setStatus(BADRANGE);
                retMsg->setResultString(resultString);
            }
        }
        else
        {
            resultString = "Error, unknown port./n";
            retMsg->setStatus(CtiInvalidRequest);
            retMsg->setResultString(resultString);
        }
    }

    if( retMsg != NULL )
    {
        if( (Conn = ((CtiConnection*)msg->getConnectionHandle())) != NULL )
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                retMsg->dump();
            }

            Conn->WriteConnQue(retMsg);
        }
        else
        {
            delete retMsg;
        }
    }
}

} //Namespace Cti::Porter
}
