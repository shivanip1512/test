/*-----------------------------------------------------------------------------*
*
* File:   PHLIDLC
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PHLIDLC.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/08/11 19:51:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#pragma title ( "High Level IDLC Routines" )
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
IDLCInit (CtiPortSPtr      PortRecord,        /* Port record */
          CtiDeviceSPtr &RemoteRecord,     /* ccu record */
          REMOTESEQUENCE   *RemoteSequence)   /* various ccu specific data */
{
    extern void DisplayTraceList( CtiPortSPtr prt, RWTPtrSlist< CtiMessage > &traceList, bool consume);
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
IDLCFunction (CtiDeviceSPtr &Dev,
              USHORT Source,    /* id of source process */
              USHORT Dest,      /* id of destination process */
              USHORT Function)  /* function to execute */
{
    INT status = (NORMAL);

    OUTMESS *OutMessage;

    if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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
        if(p711Info)
        {
            p711Info->PortQueueEnts++;
            p711Info->PortQueueConts++;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


/* routine to build an idlc RCONT function */
IDLCRCont (CtiDeviceSPtr &Dev)
{
    INT status = NORMAL;
    OUTMESS *OutMessage;
    ULONG i;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(p711Info->PortQueueConts)
        return(NORMAL);

    if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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
IDLCRColQ (CtiDeviceSPtr &Dev, INT priority)
{
    INT status = NORMAL;
    OUTMESS *OutMessage;
    ULONG i;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(!p711Info->GetStatus(INRCOLQ))
    {
        if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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
IDLCSetTSStores (CtiDeviceSPtr &Dev, USHORT Priority, USHORT Trigger, USHORT Period)
{
    INT status = NORMAL;
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Allocate some memory */
    if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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
IDLCSetBaseSList (CtiDeviceSPtr &Dev)
{
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Allocate some memory */
    if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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

/* Excerpt from a technote on spread spectrum radios.

For some time I have been trying to convince the CCU-711 to take communications delays other than the
defaults.  It never complained about the numbers I sent it, it just ignored the hell out of them.  I learned
two things yesterday...  If any one of 4 values is out of range it sets all 4 back to the defaults without
bothering to REQACK the command.  In addition two of the delays had different minimum values in the code
from what the SSpec specified.  The system point of view geniusus were incapable of following their own
convuluted and ill concieved SSpec and then didn't bother to update it.  I could go on for years about this...

Anyway Porter looks for a file in the data directory called DELAY.DAT. This file has the format:

Port,Remote,T_RTSON,T_CTSTO,T_KEYOFF,T_INTRATO,BA_TRIG .
.
.

Where:

Port is the port number

Remote is the remote number

T_RTSON is the delay from asserting RTS and Radio Keying until the CCU starts looking for CTS.
 Valid values are 8 to 2040 and it defaults to 600.  Units are milliseconds.

T_CTSTO is the length of time the CCU will wait for CTS to come back from the communications
 device after T_RTSON is done.  Valid range is 168 to 2040 and it defaults to 304.  Units
 are milliseconds.

T_KEYOFF is the length of time between end of data (RTS off) and release of radio key-on.
 Valid range is 0 to 160 and it defaults to 32.  Units are milliseconds.

T_INTRATO is the intra character timeout for a single IDLC frame.  Valid range is 248 to 2040
and it defaults to 400.  Units are milliseconds.

BA_TRIG is the length time between IDLC messages that CCU waits to reset IDLC communications
 and the serial port (Communications watchdog so to speak).  Valid values are 0 to 65,535 (65,535
 disables this timer) and it defaults to 170.  Units are in seconds.

Upon detecting a cold start in the CCU (which by the way sets these values to defaults on cold start)
porter will check for the the correct port and remote combo in the delay.dat file and if the ccu is
in the file, download the values found.  Porter forces the values to be in range.  Note that versions
of porter prior to this Fridays release incorrectly do this for T_CTSTO (which must be a minimum
of 168) and T_INTRATO (which must be a minimum of 248) so these values must be set to at least
the minimum...

Please note that if you bother reading the SSpec ignore the hell out of anything it has to say about
these values.

What I had Les load into the CCU at MVEA was the following

3,10,8,168,32,248,170

Which should work well for any CCU hooked to one of these spread sprectum radios

*/

/* Routine to initialize CCU algorithm status list */
IDLCSetDelaySets (CtiDeviceSPtr &Dev)
{
    bool filefound = false;
    bool devfound = false;
    USHORT Index;
    OUTMESS *OutMessage;
    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Defines for file handle */
    FILE *HFile = NULL;
    INT   MyPort;
    USHORT MyRemote;

    // 072302 CGP. Let's just make this happen by default.
    USHORT T_RTSOn      = 180;          // 600 CCU default
    USHORT T_CTSTo      = 168;          // 304 CCU default
    USHORT T_KeyOff     = 32;           // 32  CCU default
    USHORT T_IntraTo    = 248;          // 400 CCU default
    USHORT BA_Trig      = 170;          // 170 CCU default

    /* attempt to open the OLD file */
    if((HFile = fopen ("DATA\\DELAY.DAT", "r")) != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " DELAY.DAT MUST be moved to SERVER\\CONFIG\\DELAY.DAT" << endl;
        }

        fclose (HFile);
        HFile = NULL;

        filefound = true;
    }

    if((HFile = fopen(gDelayDatFile.data(), "r")) != NULL)
    {
        /* Walk through the file looking for the appropriate port and remote */
        for(;;)
        {
            if(fscanf (HFile,"%d,%hd,%hd,%hd,%hd,%hd,%hd", &MyPort,  &MyRemote, &T_RTSOn, &T_CTSTo, &T_KeyOff, &T_IntraTo, &BA_Trig) != 7)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Device not detected in delay.dat file **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                T_RTSOn      = 180;
                T_CTSTo      = 168;
                T_KeyOff     = 32;
                T_IntraTo    = 248;
                BA_Trig      = 170;

                break;
            }

            if(MyPort == Dev->getPortID() && MyRemote == Dev->getAddress())
            {
                devfound = true;
                break;
            }
        }

        fclose (HFile);
        filefound = true;
    }

    if(!filefound && PorterDebugLevel & PORTER_DEBUG_CCUCONFIG)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " No delay.dat file was found.  Port Control was looking here " << gDelayDatFile << endl;
        }
    }

    if(devfound)
    {
        /* If we get here we got one */
        if(PorterDebugLevel & PORTER_DEBUG_CCUCONFIG)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " " << Dev->getName() << " (" << Dev->getPortID() << "," << Dev->getAddress() << ") timings being set to: " << endl;
            dout << " T_RTSOn    " << T_RTSOn << endl;
            dout << " T_CTSTo    " << T_CTSTo << endl;
            dout << " T_KeyOff   " << T_KeyOff << endl;
            dout << " T_IntraTo  " << T_IntraTo << endl;
            dout << " BA_Trig    " << BA_Trig << endl;
        }

        /* Allocate some memory */
        if((OutMessage = CTIDBG_new OUTMESS) == NULL)
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
    }

    return(NORMAL);
}
