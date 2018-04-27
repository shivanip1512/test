#include "precompiled.h"

#include <process.h>
#include <iostream>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <string.h>

#include "color.h"
#include "queues.h"
#include "dsm2.h"
#include "error.h"
#include "porter.h"
#include "master.h"
#include "elogger.h"

/* define the global area */
#include "portglob.h"
#include "portdecl.h"
#include "StatisticsManager.h"
#include "c_port_interface.h"
#include "rtdb.h"
#include "dev_base.h"
#include "port_base.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

using namespace std;

using Cti::Porter::PorterStatisticsManager;
using Cti::Timing::Chrono;
using Cti::StreamConnection;
using Cti::StreamConnectionException;

extern CtiDeviceManager DeviceManager;

HCTIQUEUE*   QueueHandle(LONG pid);

YukonError_t CheckIfOutMessageIsExpired(OUTMESS *&OutMessage, const CtiTime now)
{
    if( ! OutMessage )
    {
        return ClientErrors::Memory;
    }

    if( OutMessage->ExpirationTime != 0 && OutMessage->ExpirationTime < now.seconds() )
    {
        // This OM has expired and should not be acted upon!
        SendError(OutMessage, ClientErrors::RequestExpired);

        return ClientErrors::RequestExpired;
    }

    return ClientErrors::None;
}

/* Routine to send error message back to originating process */
YukonError_t SendError (OUTMESS *&OutMessage, YukonError_t ErrorCode, INMESS *PassedInMessage)
{
    if(!OutMessage)
    {
        CTILOG_WARN(dout, "SendError generally requires an OutMessage.");

        return ClientErrors::None;
    }

    /* create and send return message if calling process expects it */
    if(ErrorCode == ClientErrors::PortInhibited || OutMessage->EventCode & RESULT)
    {
        INMESS DefaultInMessage;

        if( ! PassedInMessage )
        {
            //  They didn't pass in an inmessage, so we have to make one
            DefaultInMessage.DeviceID = PORTERSU_DEVID;

            OutEchoToIN( *OutMessage, DefaultInMessage );

            DefaultInMessage.InLength  = 0;
            DefaultInMessage.ErrorCode = ErrorCode;

            CtiTime now;

            DefaultInMessage.Time = now.seconds();

            DefaultInMessage.MilliTime = 0;
            if(now.isDST())
            {
                DefaultInMessage.MilliTime |= DSTACTIVE;
            }

            if(OutMessage->EventCode & BWORD)
            {
                DefaultInMessage.Buffer.DSt.Time = DefaultInMessage.Time;
                DefaultInMessage.Buffer.DSt.DSTFlag = DefaultInMessage.MilliTime & DSTACTIVE;
            }
        }

        const INMESS & InMessage =
                PassedInMessage
                    ? *PassedInMessage
                    : DefaultInMessage;

        if(PorterDebugLevel & PORTER_DEBUG_SENDERROR)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->DeviceID);

            if(tempDev)
            {
                CTILOG_DEBUG(dout, "SendError returning an Inmessage for "<< tempDev->getName() <<" error "<< ErrorCode);
            }
        }

        if( OutMessage->Command == CMND_LGRPQ )
        {
            //  If this was an LGRPQ to a CCU-711, dequeue all of the queued OutMessages!
            DeQueue(InMessage);
        }

        /* send message back to originating process */
        if( InMessage.ReturnNexus != NULL )
        {
            boost::optional<std::string> writeError;

            try
            {
                const size_t bytesWritten  = InMessage.ReturnNexus->write(&InMessage, sizeof(INMESS), Chrono::seconds(30));
                if( bytesWritten != sizeof(INMESS) )
                {
                    std::string reason = Cti::StreamBuffer() <<"Timeout (Wrote "<< bytesWritten <<"/"<< sizeof(INMESS) <<" bytes)";

                    writeError = "";
                    writeError->swap(reason);
                }
            }
            catch( const StreamConnectionException &ex )
            {
                writeError = ex.what();
            }

            if( writeError )
            {
                CTILOG_INFO(dout, "Returning error condition to client"<<
                        endl <<"DeviceID "<< OutMessage->DeviceID <<" TargetID "<< OutMessage->TargetID <<" Command "<< OutMessage->Request.CommandStr <<
                        endl <<"Reason: "<< *writeError);

                if( ! InMessage.ReturnNexus->isValid() )
                {
                    extern void blitzNexusFromCCUQueue(CtiDeviceSPtr Device, const StreamConnection *Nexus);
                    CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->DeviceID);
                    blitzNexusFromCCUQueue( tempDev, InMessage.ReturnNexus );
                }
                // 111901 CGP.  You better not close this.. It is the OutMessage's!
            }
        }
    }

    if(PorterDebugLevel & PORTER_DEBUG_SENDERROR)
    {
        CTILOG_DEBUG(dout, "DeviceID / TargetID "<< OutMessage->DeviceID <<" / "<< OutMessage->TargetID <<", Error "<< ErrorCode <<" -> "<< CtiError::GetErrorString(ErrorCode));
    }

    //If using statistics, send an error attempt.
    PorterStatisticsManager.newCompletion(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, ErrorCode, OutMessage->MessageFlags);

    /* free up the Memory */
    delete (OutMessage);
    OutMessage = 0;

    return ClientErrors::None;
}




HCTIQUEUE*   QueueHandle(LONG pid)
{
    HCTIQUEUE *pQue    = NULL;
    CtiPortSPtr pPort;

    if( (pPort = PortManager.getPortById(pid)) )
    {
        pQue = &(pPort->getPortQueueHandle());
    }

    return pQue;
}
