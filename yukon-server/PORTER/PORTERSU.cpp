/*-----------------------------------------------------------------------------*
*
* File:   PORTERSU
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTERSU.cpp-arc  $
* REVISION     :  $Revision: 1.33.2.2 $
* DATE         :  $Date: 2008/11/21 16:14:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <process.h>
#include <iostream>

#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <string.h>

#include "color.h"
#include "connection.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
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
#include "msg_commerrorhistory.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

using namespace std;

using Cti::Porter::PorterStatisticsManager;

extern CtiDeviceManager DeviceManager;

HCTIQUEUE*   QueueHandle(LONG pid);

void AddCommErrorEntry(OUTMESS *OutMessage, INMESS *InMessage, INT ErrorCode)
{
    extern CtiConnection VanGoghConnection;

    if( OutMessage != 0 &&
        ErrorCode != NORMAL &&
        !( ErrorCode != DEVICEINHIBITED ||  // Do nothing in these cases.  This would just fill a database table with garbage.
           ErrorCode != PORTINHIBITED) )
    {
        INT ErrorType = GetErrorType(ErrorCode);

        string cmd(OutMessage->Request.CommandStr);
        string omess;
        string imess;

        traceBuffer(omess, OutMessage->Buffer.OutMessage, OutMessage->OutLength);

        if(InMessage != 0)
        {
            traceBuffer(imess, InMessage->Buffer.InMessage, InMessage->InLength);
        }

        CtiCommErrorHistoryMsg *pCommError = CTIDBG_new CtiCommErrorHistoryMsg((OutMessage->TargetID > 0 ? OutMessage->TargetID : OutMessage->DeviceID),
                                                                        ErrorType,
                                                                        ErrorCode,
                                                                        cmd,
                                                                        omess,
                                                                        imess);
        int vgccnt = VanGoghConnection.outQueueCount();
        if( vgccnt > 0 &&  !(vgccnt % 100))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Dispatch is not accepting  messages.  " << VanGoghConnection.outQueueCount() << " queue entries waiting." << endl;
            }
        }

        VanGoghConnection.WriteConnQue(pCommError);
    }

    return;
}

/* Routine to send error message back to originating process */
INT SendError (OUTMESS *&OutMessage, USHORT ErrorCode, INMESS *PassedInMessage)
{
    if(!OutMessage)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " SendError generally requires an OutMessage." << endl;
        }
        return NORMAL;
    }

    /* create and send return message if calling process expects it */
    if(ErrorCode == PORTINHIBITED || OutMessage->EventCode & RESULT)
    {
        INMESS *InMessage;

        INMESS DefaultInMessage;

        if( PassedInMessage )
        {
            InMessage = PassedInMessage;
        }
        else
        {
            //  They didn't pass in an inmessage, so we have to make one
            DefaultInMessage.DeviceID = PORTERSU_DEVID;

            OutEchoToIN( OutMessage, &DefaultInMessage );

            DefaultInMessage.InLength      = 0;
            DefaultInMessage.EventCode     = ErrorCode;

            CtiTime now;

            DefaultInMessage.Time = now.seconds();

            DefaultInMessage.MilliTime = 0; // TimeB.millitm;
            if(now.isDST())
                DefaultInMessage.MilliTime |= DSTACTIVE;

            if(OutMessage->EventCode & BWORD)
            {
                /* save the names */
                DefaultInMessage.Buffer.DSt.Time = DefaultInMessage.Time;
                DefaultInMessage.Buffer.DSt.DSTFlag = DefaultInMessage.MilliTime & DSTACTIVE;
            }

            InMessage = &DefaultInMessage;
        }

        if(PorterDebugLevel & PORTER_DEBUG_SENDERROR)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->DeviceID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " SendError returning an Inmessage for " << tempDev->getName() << " error " << ErrorCode << endl;
            }
        }

        /* send message back to originating process */
        if(InMessage->ReturnNexus != NULL)
        {
            ULONG BytesWritten;

            INT writeResult = InMessage->ReturnNexus->CTINexusWrite(InMessage, sizeof (INMESS), &BytesWritten, 30L);

            if(writeResult || BytesWritten == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime()  << " TID: " << GetCurrentThreadId() << " Error \"" << writeResult << "\" returning error condition to client " << endl;
                    dout << "  DeviceID " << OutMessage->DeviceID << " TargetID " << OutMessage->TargetID << " " << OutMessage->Request.CommandStr  << endl;
                }

                if(writeResult == BADSOCK)
                {
                    extern void blitzNexusFromCCUQueue(CtiDeviceSPtr Device, CtiConnect *&Nexus);
                    CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->DeviceID);
                    blitzNexusFromCCUQueue( tempDev, InMessage->ReturnNexus );
                }
                // 111901 CGP.  You better not close this.. It is the OutMessage's! // InMessage.ReturnNexus->CTINexusClose();
            }
        }
    }

    AddCommErrorEntry( OutMessage, PassedInMessage, ErrorCode );

    if(PorterDebugLevel & PORTER_DEBUG_SENDERROR)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DeviceID / TargetID " << OutMessage->DeviceID << " / " << OutMessage->TargetID << ", Error " << ErrorCode << " " << FormatError(ErrorCode) << endl;
        }
    }

    //If using statistics, send an error attempt.
    PorterStatisticsManager.newCompletion(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, ErrorCode, OutMessage->MessageFlags);

    /* free up the Memory */
    delete (OutMessage);
    OutMessage = 0;

    return(NORMAL);
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
