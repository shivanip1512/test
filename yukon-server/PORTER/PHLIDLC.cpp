#pragma title ( "High Level IDLC Routines" )
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PHLIDLC
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PHLIDLC.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/07/03 21:35:13 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PHLIDLC.C

    Purpose:
        Contains routines to deal with IDLC protocol at Level 3

    The following procedures are contained in this module:
        IDLCInit                    IDLCFunction
        IDLCRCont                   IDLCRColQ
        IDLCsetTSStores             IDLCsetBaseSList
        IDLCsetDelaysets

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        1-3-95   Added Delay set Code                               WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"

#include "portglob.h"
#include "port_base.h"
#include "dev_base.h"
#include "xfer.h"
#include "mgr_port.h"

#include "trx_711.h"

extern CtiPortManager PortManager;


/* routine to initialize an idlc device and its counters */
IDLCInit (CtiPort          *PortRecord,       /* Port record */
          CtiDeviceBase    *RemoteRecord,     /* ccu record */
          REMOTESEQUENCE   *RemoteSequence)   /* various ccu specific data */
{
    extern void DisplayTraceList( CtiPort *prt, RWTPtrSlist< CtiMessage > &traceList, bool consume);
    BYTE Message[30], Reply[30];
    ULONG Count;
    ULONG i;
    RWTPtrSlist< CtiMessage > traceList;

    /* First check if the port or remote is inhibited */
    if(PortRecord->isInhibited())
        return(PORTINHIBITED);

    if(RemoteRecord->isInhibited())
        return(REMOTEINHIBITED);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Reset Request to " << RemoteRecord->getName() << endl;
    }
    /* build the message to send to the ccu */
    IDLCSArm (Message, RemoteRecord->getAddress());

    /* send the message to the ccu */
    INT     traceMask = 0;

    if(TraceFlag)          traceMask |= CtiXfer::TRACE_DEFAULT;
    if(TraceErrorsOnly)    traceMask |= CtiXfer::TRACE_ERROR;

    CtiXfer trx(Message, 5, Reply, 5, &Count, TIMEOUT, FALSE, FALSE, traceMask);

    if( (i = PortRecord->outInMess(trx, RemoteRecord, traceList)) != NORMAL)
    {
        return i;
    }

    /* check for a valid response */
    if((i = IDLCua (Reply, &RemoteSequence->Request, &RemoteSequence->Reply)) != NORMAL)
    {
        return(i);
    }

    if(trx.doTrace(i))
    {
        PortRecord->traceXfer(trx, traceList, RemoteRecord, i);
    }

    DisplayTraceList( PortRecord, traceList, true );

    return(NORMAL);
}


/* routine to build an idlc function */
IDLCFunction (CtiDevice *Dev,
              USHORT Source,    /* id of source process */
              USHORT Dest,      /* id of destination process */
              USHORT Function)  /* function to execute */
{
    INT status = (NORMAL);

    OUTMESS *OutMessage;

    if((OutMessage = new OUTMESS) == NULL)
    {
        printf ("Error Allocating Memory\n");
        return(MEMORY);
    }


    OutMessage->Priority      = MAXPRIORITY;
    OutMessage->DeviceID      = Dev->getID();
    OutMessage->Remote        = Dev->getAddress();
    OutMessage->Port          = Dev->getPortID();
    OutMessage->Sequence      = 0;
    OutMessage->EventCode     = RESULT | RCONT;
    OutMessage->TimeOut       = TIMEOUT;
    OutMessage->Retry         = 1;
    OutMessage->OutLength     = 3;
    OutMessage->InLength      = 0;
    OutMessage->Source        = 0;
    OutMessage->Destination   = Dest;
    OutMessage->Command       = CMND_ACTIN;
    OutMessage->ReturnNexus   = NULL;

    OutMessage->Buffer.OutMessage[PREIDL] = (UCHAR)Function;

    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Now output message to appropriate port queue */
    if(PortManager.writeQueue(Dev->getPortID(), OutMessage->EventCode, sizeof (*OutMessage), (CHAR *) OutMessage, OutMessage->Priority))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error writing to port queue for device " << Dev->getName() << endl;
        }

        delete (OutMessage);
        status = QUEUE_WRITE;
    }
    else
    {
        p711Info->PortQueueEnts++;
        p711Info->PortQueueConts++;
    }

    return status;
}


/* routine to build an idlc RCONT function */
IDLCRCont (CtiDevice *Dev)
{
    INT status = NORMAL;
    OUTMESS *OutMessage;
    ULONG i;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(p711Info->PortQueueConts)
        return(NORMAL);

    if((OutMessage = new OUTMESS) == NULL)
    {
        printf ("Error Allocating Memory\n");
        return(MEMORY);
    }

    OutMessage->DeviceID = Dev->getID();

    /* Figure out what the priority should be */
    OutMessage->Priority = 0;
    for(i = 0; i < MAXQUEENTRIES; i++)
    {
        if(p711Info->QueTable[i].InUse)
        {
            if(p711Info->QueTable[i].Priority > OutMessage->Priority)
            {
                OutMessage->Priority = (UCHAR)p711Info->QueTable[i].Priority;
            }
        }
    }
    /* But don't let it be less than 11 */
    if(OutMessage->Priority < 11)
        OutMessage->Priority = 11;

    OutMessage->Remote = Dev->getAddress();
    OutMessage->Port = Dev->getPortID();
    OutMessage->Sequence = 0;
    OutMessage->EventCode = RESULT | RCONT;
    OutMessage->TimeOut = TIMEOUT;
    OutMessage->Retry = 1;
    OutMessage->OutLength = 2;
    OutMessage->InLength = p711Info->RContInLength;
    OutMessage->Source = 0;
    OutMessage->Destination = DEST_QUEUE;
    OutMessage->Command = CMND_RCONT;
    OutMessage->ReturnNexus = NULL;

    /* Now output message to appropriate port queue */
    if(PortManager.writeQueue(Dev->getPortID(), OutMessage->EventCode, sizeof (*OutMessage), (CHAR *) OutMessage, OutMessage->Priority))
    {
        printf ("Error Writing to Port Queue\n");

        delete (OutMessage);
        status = QUEUE_WRITE;
    }
    else
    {
        p711Info->PortQueueEnts++;
        p711Info->PortQueueConts++;
    }

    return(status);
}


/* routine to build an idlc RCOLQ function */
IDLCRColQ (CtiDevice *Dev, INT priority)
{
    INT status = NORMAL;
    OUTMESS *OutMessage;
    ULONG i;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(!p711Info->GetStatus(INRCOLQ))
    {
        if((OutMessage = new OUTMESS) == NULL)
        {
            printf ("Error Allocating Memory\n");
            return(MEMORY);
        }

        OutMessage->DeviceID = Dev->getID();

        /* Figure out what the priority should be, but don't let it be less than 11 */
        OutMessage->Priority = priority;

        for(i = 0; i < MAXQUEENTRIES; i++)
        {
            if(p711Info->QueTable[i].InUse)                                   // If in use
            {
                if(p711Info->QueTable[i].EventCode & RESULT)                   // And expecting a result.
                {
                    if(p711Info->QueTable[i].Priority > OutMessage->Priority)   // And of a higher priority than current..
                    {
                        OutMessage->Priority = (UCHAR)p711Info->QueTable[i].Priority;
                    }
                }
            }
        }

        /* Calculate the length we should get */
        if(p711Info->NCOcts < (MaxOcts - 20))        // Can I get it all in one?
        {
            if(p711Info->NCOcts > 0)
            {
                p711Info->RContInLength = p711Info->NCOcts + 1;
            }
            else
            {
                /* figure out what the longest single response could be */
                p711Info->RContInLength = 0;
                for(i = 0; i < MAXQUEENTRIES; i++)
                {
                    if(p711Info->QueTable[i].InUse)
                    {
                        if(p711Info->QueTable[i].Length + 17 > p711Info->RContInLength)
                        {
                            p711Info->RContInLength = p711Info->QueTable[i].Length + 17;
                        }
                    }
                }
            }
        }
        else
        {
            p711Info->RContInLength = (MaxOcts - 20);
        }

        /* Make sure that we are greater than or equal to the minimum */
        if(p711Info->RContInLength < p711Info->RColQMin)
        {
            p711Info->RContInLength = p711Info->RColQMin;
        }

        OutMessage->Remote = Dev->getAddress();
        OutMessage->Port = Dev->getPortID();
        OutMessage->Sequence = 0;
        OutMessage->EventCode = RESULT | RCONT;
        OutMessage->TimeOut = TIMEOUT;
        OutMessage->Retry = 1;
        OutMessage->OutLength = 3;
        OutMessage->InLength = p711Info->RContInLength;
        OutMessage->Source = 0;
        OutMessage->Destination = DEST_QUEUE;
        OutMessage->Command = CMND_RCOLQ;
        OutMessage->ReturnNexus = NULL;

        OutMessage->Buffer.OutMessage[PREIDL] = (UCHAR)p711Info->RContInLength;

        /* Now output message to appropriate port queue */
        if(PortManager.writeQueue(Dev->getPortID(), OutMessage->EventCode, sizeof (*OutMessage), (CHAR *) OutMessage, OutMessage->Priority))
        {
            printf ("Error Writing to Port Queue\n");
            delete (OutMessage);
            return(QUEUE_WRITE);
        }
        else
        {
            p711Info->SetStatus(INRCOLQ);
            p711Info->PortQueueEnts++;
            p711Info->PortQueueConts++;
        }
    }

    return(status);
}


/* Routine to Set CCU time sync values */
IDLCSetTSStores (CtiDevice *Dev, USHORT Priority, USHORT Trigger, USHORT Period)
{
    INT status = NORMAL;
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Allocate some memory */
    if((OutMessage = new OUTMESS) == NULL)
    {
        printf ("Error Allocating Memory\n");
        return(MEMORY);
    }

    /* Load up the queue structure */
    OutMessage->DeviceID = Dev->getID();
    OutMessage->Port = Dev->getPortID();
    OutMessage->Remote = Dev->getAddress();
    OutMessage->TimeOut = TIMEOUT;
    OutMessage->Retry = 1;
    OutMessage->InLength = 0;
    OutMessage->Source = 0;
    OutMessage->Destination = DEST_TSYNC;
    OutMessage->Command = CMND_WMEMS;
    OutMessage->Sequence = 0;
    OutMessage->Priority = MAXPRIORITY - 1;
    OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | RCONT;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus = NULL;

    Index = PREIDL;

    /* Load Priority */
    OutMessage->Buffer.OutMessage[Index++] = 4;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1000);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1000);

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (Priority);

    /* Load Trigger time */
    OutMessage->Buffer.OutMessage[Index++] = 5;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1100);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1100);

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (Trigger);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (Trigger);

    /* Load Period */
    OutMessage->Buffer.OutMessage[Index++] = 5;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1101);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1101);

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (Period);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (Period);

    /* Last SETL */
    OutMessage->Buffer.OutMessage[Index++] = 0;

    /* Thats it so send the message */
    OutMessage->OutLength = Index - PREIDL + 2;

    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
    {
        printf ("Error Writing to Queue for Port %2hd\n", OutMessage->Port);

        delete (OutMessage);
        return(QUEUE_WRITE);
    }
    else
    {
        p711Info->PortQueueEnts++;
        p711Info->PortQueueConts++;
    }

    return(NORMAL);
}


/* Routine to initialize CCU algorithm status list */
IDLCSetBaseSList (CtiDevice *Dev)
{
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Allocate some memory */
    if((OutMessage = new OUTMESS) == NULL)
    {
        printf ("Error Allocating Memory\n");
        return(MEMORY);
    }

    /* Load up the queue structure */
    OutMessage->DeviceID      = Dev->getID();
    OutMessage->Port          = Dev->getPortID();
    OutMessage->Remote        = Dev->getAddress();
    OutMessage->TimeOut       = TIMEOUT;
    OutMessage->Retry         = 1;
    OutMessage->InLength      = 0;
    OutMessage->Source        = 0;
    OutMessage->Destination   = DEST_BASE;
    OutMessage->Command       = CMND_WMEMS;
    OutMessage->Sequence      = 0;
    OutMessage->Priority      = MAXPRIORITY - 1;
    OutMessage->EventCode     = NOWAIT | NORESULT | ENCODED | RCONT;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus = NULL;

    Index = PREIDL;

    /* Load Values */
    OutMessage->Buffer.OutMessage[Index++] = 11;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1200);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1200);

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (2);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (4);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (5);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);


    /* Last SETL */
    OutMessage->Buffer.OutMessage[Index++] = 0;

    /* Thats it so send the message */
    OutMessage->OutLength = Index - PREIDL + 2;

    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
    {
        printf ("Error Writing to Queue for Port %2hd\n", OutMessage->Port);

        delete (OutMessage);
        return(QUEUE_WRITE);
    }
    else
    {
        p711Info->PortQueueEnts++;
        p711Info->PortQueueConts++;
    }

    return(NORMAL);
}


/* Routine to initialize CCU algorithm status list */
IDLCSetDelaySets (CtiDevice *Dev)
{
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Defines for file handle */
    FILE *HFile;
    USHORT MyPort, MyRemote, T_RTSOn, T_CTSTo;
    USHORT T_KeyOff, T_IntraTo, BA_Trig;

    /* attempt to open the file */
    if((HFile = fopen ("DATA\\DELAY.DAT", "r")) == NULL)
    {
        return(NORMAL);
    }

    /* Walk through the file looking for the appropriate port and remote */
    for(;;)
    {
        if(fscanf (HFile,"%hd,%hd,%hd,%hd,%hd,%hd,%hd", &MyPort,  &MyRemote, &T_RTSOn, &T_CTSTo, &T_KeyOff, &T_IntraTo, &BA_Trig) != 7)
        {
            fclose (HFile);
            return(NORMAL);
        }

        if(MyPort == Dev->getPortID() && MyRemote == Dev->getAddress())
        {
            fclose (HFile);
            break;
        }
    }

    /* If we get here we got one */

    /* Allocate some memory */
    if((OutMessage = new OUTMESS) == NULL)
    {
        printf ("Error Allocating Memory\n");
        return(MEMORY);
    }

    /* Load up the queue structure */
    OutMessage->DeviceID      = Dev->getID();
    OutMessage->Port          = Dev->getPortID();
    OutMessage->Remote        = Dev->getAddress();
    OutMessage->TimeOut       = TIMEOUT;
    OutMessage->Retry         = 1;
    OutMessage->InLength      = 0;
    OutMessage->Source        = 0;
    OutMessage->Destination   = DEST_BASE;
    OutMessage->Command       = CMND_WMEMS;
    OutMessage->Sequence      = 0;
    OutMessage->Priority      = MAXPRIORITY;
    OutMessage->EventCode     = NOWAIT | NORESULT | ENCODED | RCONT;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus = NULL;

    Index = PREIDL;

    /* Load Values */
    OutMessage->Buffer.OutMessage[Index++] = 4;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1010);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1010);

    T_RTSOn /= 8;

    if(T_RTSOn < 1)
    {
        T_RTSOn = 1;
    }
    else if(T_RTSOn > 255)
    {
        T_RTSOn = 255;
    }

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (T_RTSOn);


    OutMessage->Buffer.OutMessage[Index++] = 4;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1011);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1011);

    T_CTSTo /= 8;

    if(T_CTSTo < 21)
    {
        T_CTSTo = 21;
    }
    else if(T_CTSTo > 255)
    {
        T_CTSTo = 255;
    }

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (T_CTSTo);

    OutMessage->Buffer.OutMessage[Index++] = 4;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1012);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1012);

    T_KeyOff /= 8;

    if(T_KeyOff > 20)
    {
        T_KeyOff = 20;
    }

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (T_KeyOff);

    OutMessage->Buffer.OutMessage[Index++] = 4;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1013);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1013);

    T_IntraTo /= 8;

    if(T_IntraTo < 31)
    {
        T_IntraTo = 31;
    }
    else if(T_IntraTo > 255)
    {
        T_IntraTo = 255;
    }

    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (T_IntraTo);


    OutMessage->Buffer.OutMessage[Index++] = 5;

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1100);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1100);

    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (BA_Trig);
    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (BA_Trig);

    /* Last SETL */
    OutMessage->Buffer.OutMessage[Index++] = 0;

    /* Thats it so send the message */
    OutMessage->OutLength = Index - PREIDL + 2;

    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
    {
        printf ("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
        delete (OutMessage);
        return(QUEUE_WRITE);
    }
    else
    {
        p711Info->PortQueueEnts++;
        p711Info->PortQueueConts++;
    }

    return(NORMAL);
}
