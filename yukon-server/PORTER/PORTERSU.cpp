/*-----------------------------------------------------------------------------*
*
* File:   PORTERSU
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTERSU.cpp-arc  $
* REVISION     :  $Revision: 1.29 $
* DATE         :  $Date: 2006/04/25 20:45:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
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
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "elogger.h"
#include "alarmlog.h"
#include "drp.h"
#include "perform.h"
#include "das08.h"

/* define the global area */
#include "portglob.h"
#include "portdecl.h"
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
SendError (OUTMESS *&OutMessage, USHORT ErrorCode, INMESS *PassedInMessage)
{
    INMESS InMessage;
    ULONG BytesWritten;
    ERRSTRUCT ErrStruct;
    struct timeb TimeB;

    if(!OutMessage)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " SendError generally requires an OutMessage." << endl;
        }
        return NORMAL;
    }

    InMessage.DeviceID = PORTERSU_DEVID;
    /* create and send return message if calling process expects it */
    if(ErrorCode == PORTINHIBITED || OutMessage->EventCode & RESULT)
    {
        OutEchoToIN( OutMessage, &InMessage );

        InMessage.InLength      = 0;
        InMessage.EventCode     = ErrorCode;

        CtiTime now;

        InMessage.Time = now.seconds();

        InMessage.MilliTime = 0; // TimeB.millitm;
        if(now.isDST())
            InMessage.MilliTime |= DSTACTIVE;

        if(OutMessage->EventCode & BWORD)
        {
            /* save the names */
            InMessage.Buffer.DSt.Time = InMessage.Time;
            InMessage.Buffer.DSt.DSTFlag = InMessage.MilliTime & DSTACTIVE;
        }


        if(PorterDebugLevel & PORTER_DEBUG_SENDERROR)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getEqual(OutMessage->DeviceID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " SendError returning an Inmessage for " << tempDev->getName() << " error " << ErrorCode << endl;
            }
        }

        /* send message back to originating process */
        if(InMessage.ReturnNexus != NULL)
        {
            INT writeResult = InMessage.ReturnNexus->CTINexusWrite(&InMessage, sizeof (InMessage), &BytesWritten, 30L);

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
                    CtiDeviceSPtr tempDev = DeviceManager.getEqual(OutMessage->DeviceID);
                    blitzNexusFromCCUQueue( tempDev, InMessage.ReturnNexus );
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
    statisticsNewCompletion(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, ErrorCode, OutMessage->MessageFlags);

    /* free up the Memory */
    delete (OutMessage);
    OutMessage = 0;

    return(NORMAL);
}


/* Add Remote error to the comm error log */
ReportRemoteError (CtiDeviceSPtr RemoteRecord, ERRSTRUCT *ErrorRecord)
{
    COMM_ERROR_LOG_STRUCT ComErrorRecord;
    CtiPortSPtr PortRecord;

    if(!(ErrorRecord->Error) || ErrorRecord->Type == ERRTYPEPROTOCOL)
    {
        return(NORMAL);
    }

    /* get the port record */
    if(PortRecord = PortManager.PortGetEqual (RemoteRecord->getPortID()))
    {
        /* Now load up the Comm error record */
        ComErrorRecord.TimeStamp = LongTime ();
        ComErrorRecord.StatusFlag = DSTFlag();

        ::memcpy (ComErrorRecord.DeviceName, RemoteRecord->getName().c_str(), STANDNAMLEN);

        /* Figure out what to use for a port name */
        ::strcpy (ComErrorRecord.RouteName, "$_");

        if((PortRecord->getName())[0] = ' ')
        {
            ::memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName().c_str(), STANDNAMLEN - 2);
        }
        else
        {
            ::memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName().c_str(), STANDNAMLEN - 2);
        }

        // cout << __FILE__ << " " << __LINE__ << endl;
        ComErrorLogAdd (&ComErrorRecord,  ErrorRecord, FALSE);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could not find portid = " << RemoteRecord->getPortID() << " in the database.  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return(NORMAL);
}



/* Add Device error to the comm error log */
ReportDeviceError (CtiDeviceSPtr DeviceRecord, CtiPortSPtr PortRecord, ERRSTRUCT *ErrorRecord)
{
    COMM_ERROR_LOG_STRUCT ComErrorRecord;

    if(!(ErrorRecord->Error))
    {
        return(NORMAL);
    }

    /* Now load up the Comm error record */
    ComErrorRecord.TimeStamp   = LongTime ();
    ComErrorRecord.StatusFlag  = DSTFlag();

    ::memcpy (ComErrorRecord.DeviceName, DeviceRecord->getName().c_str(), STANDNAMLEN);

    /* Figure out what to use for a port name */

    if(PortRecord->isTCPIPPort())
    {
        ::memcpy (ComErrorRecord.RouteName + 2, PortRecord->getIPAddress().c_str(), STANDNAMLEN - 2);
    }
    else
    {
        ::strcpy (ComErrorRecord.RouteName, "$_");
        if((PortRecord->getName())[0] != ' ')
        {
            ::memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName().c_str(), STANDNAMLEN - 2);
        }
        else
        {
            ::memcpy (ComErrorRecord.RouteName + 2, PortRecord->getPhysicalPort().c_str(), STANDNAMLEN - 2);
        }
    }

    ComErrorLogAdd (&ComErrorRecord, ErrorRecord, FALSE);

    return(NORMAL);
}


HCTIQUEUE*   QueueHandle(LONG pid)
{
    HCTIQUEUE *pQue    = NULL;
    CtiPortSPtr pPort;

    if( (pPort = PortManager.PortGetEqual(pid)) )
    {
        pQue = &(pPort->getPortQueueHandle());
    }

    return pQue;
}
