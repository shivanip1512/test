#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PORTERSU
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTERSU.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <process.h>
#include <iostream>
using namespace std;

#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <string.h>

#include "color.h"
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
#include "logger.h"
#include "utility.h"

extern CtiDeviceManager    DeviceManager;

IM_EX_CTIBASE extern RWMutexLock  coutMux;

HCTIQUEUE*   QueueHandle(LONG pid);


INT StartPortThread (void *arg)
{
    CtiPort *PortRecord = (CtiPort *)arg;

    /* Check the range of the port and start the appropriate resources */
    if((PortRecord->getPortID() <= PORTDIRECTMAX) || (PortRecord->getPortID() <= PORTTCPMAX))
    {
        /* Let the world know this port is starting */
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Enabling Port:  " << PortRecord->getPortID() << " as " << PortRecord->getName() << endl;
        }

        /*
         * Start up the Port thread.
         * This may be one dat a good plca to start a unique thread based upon port type.
         * Not today however 072599 CGP
         */
        if(_beginthread (PortThread, PORT_THREAD_STK_SIZE, (void*)PortRecord->getPortID()) == -1)
        {
            printf("Error Starting Port Thread %2hd\n", PortRecord->getPortID());
            return(!NORMAL);
        }
    }

    return(NORMAL);
}

/* Routine to send error message back to originating process */
SendError (OUTMESS *&OutMessage, USHORT ErrorCode)
{
    INMESS InMessage;
    ULONG BytesWritten;
    CtiDeviceBase *RemoteRecord;
    REMOTEPERF RemotePerf;
    ERRSTRUCT ErrStruct;
    struct timeb TimeB;

    char  errstr[80];
    char  logstr[128];

    InMessage.DeviceID = PORTERSU_DEVID;

    /* create and send return message if calling process expects it */
    if(OutMessage->EventCode & RESULT)
    {
        OutEchoToIN( OutMessage, &InMessage );

        InMessage.DeviceID      = OutMessage->DeviceID;
        InMessage.TargetID      = OutMessage->TargetID;

        InMessage.RouteID       = OutMessage->RouteID;

        InMessage.Port          = OutMessage->Port;
        InMessage.Remote        = OutMessage->Remote;

        InMessage.Sequence      = OutMessage->Sequence;
        InMessage.InLength      = 0;
        InMessage.ReturnNexus   = OutMessage->ReturnNexus;
        InMessage.SaveNexus     = OutMessage->SaveNexus;
        InMessage.EventCode     = ErrorCode;

        /* get the time into the return message */
        UCTFTime (&TimeB);
        InMessage.Time = TimeB.time;
        InMessage.MilliTime = TimeB.millitm;
        if(TimeB.dstflag)
            InMessage.MilliTime |= DSTACTIVE;

        if(OutMessage->EventCode & BWORD)
        {
            /* save the names */
            InMessage.Buffer.DSt.Time = InMessage.Time;
            InMessage.Buffer.DSt.DSTFlag = InMessage.MilliTime & DSTACTIVE;
        }

        /* if calling sequence is waiting make return high priority */
        if(OutMessage->EventCode & WAIT)
            InMessage.Priority = MAXPRIORITY;
        else if(OutMessage->Priority == MAXPRIORITY)
            InMessage.Priority = MAXPRIORITY - 1;

        if(PorterDebugLevel & 0x00000010)
        {
            CtiDeviceBase *tempDev = DeviceManager.getEqual(OutMessage->DeviceID);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " SendError returning an Inmessage for " << tempDev->getName() << " error " << ErrorCode << endl;
            }
        }

        /* send message back to originating process */
        if(InMessage.ReturnNexus != NULL)
        {
            INT writeResult = InMessage.ReturnNexus->CTINexusWrite (&InMessage, sizeof (InMessage), &BytesWritten, 3L);

            if(writeResult || BytesWritten == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error \"" << writeResult << "\" returning error condition to client " << endl;
                }
                // 111901 CGP.  You better not close this.. It is the OutMessage's! // InMessage.ReturnNexus->CTINexusClose();
            }
        }
    }

    /* Attempt to get the remote record */
    if(OutMessage->Remote != 0xffff)
    {
        if(NULL != (RemoteRecord = DeviceManager.RemoteGetPortRemoteEqual ((LONG)OutMessage->Port, (LONG)OutMessage->Remote)))
        {
            RemotePerf.Port   = RemoteRecord->getPortID();
            RemotePerf.Remote = RemoteRecord->getAddress();
            RemotePerf.Error  = ErrorCode;
            RemotePerfUpdate (&RemotePerf, &ErrStruct);
            // cout << __FILE__ << " " << __LINE__ << " " << ErrorCode << endl;
            ReportRemoteError (RemoteRecord, &ErrStruct);
        }
    }

    GetErrorString(ErrorCode, errstr);
    sprintf(logstr," DeviceID %d, Error %3hd: %s", OutMessage->DeviceID, ErrorCode, errstr);
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << logstr << endl;
    }

    /* free up the Memory */
    delete (OutMessage);

    return(NORMAL);
}


/* Add Remote error to the comm error log */
ReportRemoteError (CtiDeviceBase *RemoteRecord, ERRSTRUCT *ErrorRecord)
{
    COMM_ERROR_LOG_STRUCT ComErrorRecord;
    CtiPort *PortRecord;

    if(!(ErrorRecord->Error) || ErrorRecord->Type == ERRTYPEDLC)
    {
        return(NORMAL);
    }

    /* get the port record */
    if(PortRecord = PortManager.PortGetEqual (RemoteRecord->getPortID()))
    {
        /* Now load up the Comm error record */
        ComErrorRecord.TimeStamp = LongTime ();
        ComErrorRecord.StatusFlag = DSTFlag();

        memcpy (ComErrorRecord.DeviceName, RemoteRecord->getName(), STANDNAMLEN);

        /* Figure out what to use for a port name */
        strcpy (ComErrorRecord.RouteName, "$_");

        if((PortRecord->getName())(0) = ' ')
        {
            memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName(), STANDNAMLEN - 2);
        }
        else
        {
            memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName(), STANDNAMLEN - 2);
        }

        // cout << __FILE__ << " " << __LINE__ << endl;
        ComErrorLogAdd (&ComErrorRecord,  ErrorRecord, FALSE);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could not find portid = " << RemoteRecord->getPortID() << " in the database.  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return(NORMAL);
}



/* Add Device error to the comm error log */
ReportDeviceError (CtiDeviceBase    *DeviceRecord, CtiPort *PortRecord, ERRSTRUCT *ErrorRecord)
{
    COMM_ERROR_LOG_STRUCT ComErrorRecord;

    if(!(ErrorRecord->Error))
    {
        return(NORMAL);
    }

    /* Now load up the Comm error record */
    ComErrorRecord.TimeStamp   = LongTime ();
    ComErrorRecord.StatusFlag  = DSTFlag();

    memcpy (ComErrorRecord.DeviceName, DeviceRecord->getName(), STANDNAMLEN);

    /* Figure out what to use for a port name */

    if(PortRecord->getType() & TCPIPSERVER)
    {
        memcpy (ComErrorRecord.RouteName, DeviceRecord->getName(), STANDNAMLEN);
    }
    else if(PortRecord->isTCPIPPort())
    {
        memcpy (ComErrorRecord.RouteName + 2, PortRecord->getIPAddress(), STANDNAMLEN - 2);
    }
    else
    {
        strcpy (ComErrorRecord.RouteName, "$_");
        if((PortRecord->getName())(0) != ' ')
        {
            memcpy (ComErrorRecord.RouteName + 2, PortRecord->getName(), STANDNAMLEN - 2);
        }
        else
        {
            memcpy (ComErrorRecord.RouteName + 2, PortRecord->getPhysicalPort(), STANDNAMLEN - 2);
        }
    }

    ComErrorLogAdd (&ComErrorRecord, ErrorRecord, FALSE);

    return(NORMAL);
}


HCTIQUEUE*   QueueHandle(LONG pid)
{
    HCTIQUEUE   *pQue    = NULL;
    CtiPort     *pPort   = NULL;

    if( NULL != (pPort = PortManager.PortGetEqual(pid)) )
    {
        pQue = &(pPort->getPortQueueHandle());
    }

    return pQue;
}
