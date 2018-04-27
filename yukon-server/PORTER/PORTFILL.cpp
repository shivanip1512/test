/*-----------------------------------------------------------------------------*
*
* File:   PORTFILL
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTFILL.cpp-arc  $
* REVISION     :  $Revision: 1.26.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTFILL.C

    Purpose:
        Thread to generate filler messages for a TCU (Used as a poor man's
        propogation test).

    The following procedures are contained in this module:
        FillerThread                SendFiller

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO


   -------------------------------------------------------------------- */
#include <process.h>
#include <vector>

#include <stdlib.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "porter.h"
#include "portdecl.h"
#include "master.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "port_base.h"
#include "dev_base.h"
#include "rtdb.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "mgr_route.h"
#include "mutex.h"
#include "dev_tcu.h"
#include "dev_tap.h"
#include "dev_snpp.h"
#include "dev_tnpp.h"
#include "dev_pagingreceiver.h"
#include "dev_wctp.h"
#include "prot_versacom.h"
#include "expresscom.h"

using namespace std;

extern CtiRouteManager RouteManager;
extern HCTIQUEUE* QueueHandle(LONG pid);
extern CtiRouteManager    RouteManager;

static void WriteMessageToPorter(OUTMESS *&OutMessage);
static USHORT gsUID = 0;
static USHORT gsSPID = 0;

static CtiMutex vectorLock;                     // Protects iterations and loads.
static vector< ULONG > vcomFillerPaos;          // PaoIds which will send out Versacom filler messages.
static vector< ULONG > xcomFillerPaos;          // PaoIds which will send out Expresscom filler messages.




/* Routine to generate filler messages */
static void applySendFiller(const long unusedid, CtiPortSPtr Port, void *uid)
{
    ULONG j;
    OUTMESS *OutMessage;

    try
    {
        if( !Port->isInhibited() )
        {
            CtiRouteManager::coll_type::reader_lock_guard_t guard(RouteManager.getLock());
            CtiRouteManager::spiterator   rte_itr;

            for(rte_itr = RouteManager.begin() ; !PorterQuit && rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr) )
            {
                if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0) )
                {
                    PorterQuit = TRUE;
                    break;
                }

                CtiRouteSPtr Route = rte_itr->second;

                if( !Route || !Route->isDefaultRoute() )
                {
                    continue;
                }

                CtiDeviceSPtr TransmitterDevice = DeviceManager.getDeviceByID( Route->getTrxDeviceID() );


                if(TransmitterDevice && Port->getPortID() == TransmitterDevice->getPortID() && !TransmitterDevice->isInhibited())
                {
                    switch(TransmitterDevice->getType())
                    {
                    case TYPE_TCU5000:
                    case TYPE_TCU5500:
                        {
                            CTILOG_INFO(dout, "Filler message on route "<< Route->getName() <<" for transmitter "<< TransmitterDevice->getName());

                            CtiDeviceTCU *xcu = (CtiDeviceTCU *)TransmitterDevice.get();

                            // In the beginning (6/25/01) NONE will send a filler message.
                            if(xcu->getSendFiller())
                            {
                                /* Allocate some memory */
                                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                                {
                                    return;
                                }

                                /* send a filler to this guy */
                                OutMessage->DeviceID = xcu->getID();
                                OutMessage->TargetID = xcu->getID();
                                OutMessage->Port     = xcu->getPortID();
                                OutMessage->Remote   = xcu->getAddress();
                                OutMessage->TimeOut  = 2;
                                OutMessage->Retry    = 0;
                                OutMessage->OutLength = 6;
                                OutMessage->InLength = -1;
                                OutMessage->Sequence = 0;
                                OutMessage->Priority = MAXPRIORITY;
                                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
                                OutMessage->ReturnNexus = NULL;

                                MasterHeader (OutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Remote, MASTERSEND, 2);

                                /* Now build the filler message */
                                OutMessage->Buffer.OutMessage[PREIDLEN + 4] = 0xb8;
                                OutMessage->Buffer.OutMessage[PREIDLEN + 5] = LOBYTE ((gsUID == 0 ? 0xff : gsUID));

                                WriteMessageToPorter(OutMessage);
                            }

                            break;
                        }
                    default:
                        break;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return;
}

/* Routine to generate filler messages */
static void applySendFillerPage(const long unusedid, CtiPortSPtr Port, void *uid)
{
    INT status = ClientErrors::None;
    ULONG i, j;
    OUTMESS OutMessage;

    try
    {
        if( !Port->isInhibited() )
        {
            /*Scan ports */
            CtiRouteManager::coll_type::reader_lock_guard_t guard(RouteManager.getLock());
            CtiRouteManager::spiterator   rte_itr;

            for(rte_itr = RouteManager.begin() ; !PorterQuit && rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr) )
            {
                if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0) )
                {
                    PorterQuit = TRUE;
                    break;
                }

                CtiRouteSPtr Route = rte_itr->second;

                if( !Route || !Route->isDefaultRoute() )
                {
                    continue;
                }

                CtiDeviceSPtr TransmitterDevice = DeviceManager.getDeviceByID( Route->getTrxDeviceID() );

                if(TransmitterDevice && Port->getPortID() == TransmitterDevice->getPortID() && !TransmitterDevice->isInhibited())
                {
                    switch(TransmitterDevice->getType())
                    {
                    case TYPE_SNPP:
                        CTILOG_WARN(dout, "SNPP filler is not implemented on route "<< Route->getName() <<" for transmitter "<< TransmitterDevice->getName());
                        break;
                    case TYPE_WCTP:
                        {
                            CtiDeviceWctpTerminal *tapTRX = (CtiDeviceWctpTerminal *)TransmitterDevice.get();

                            if(!tapTRX->isInhibited() && tapTRX->getSendFiller())
                            {
                                if(gsUID != 0)
                                {
                                    CTILOG_INFO(dout, "VERSACOM Filler message on route "<< Route->getName() <<" for transmitter "<< TransmitterDevice->getName());

                                    OutMessage.DeviceID = tapTRX->getID();
                                    OutMessage.TargetID = tapTRX->getID();
                                    OutMessage.Priority = MAXPRIORITY;
                                    OutMessage.Buffer.VSt.UtilityID = (BYTE)gsUID; // All Call
                                    OutMessage.Retry    = 2;
                                    OutMessage.InLength = -1;
                                    OutMessage.Sequence = 0;
                                    OutMessage.Port = tapTRX->getPortID();
                                    OutMessage.Remote = tapTRX->getAddress();
                                    OutMessage.Buffer.VSt.CommandType  = VFILLER;

                                    /*
                                     *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
                                     *  control path to take with this OutMessage!
                                     */
                                    OutMessage.EventCode = VERSACOM | NORESULT | ENCODED;

                                    /*
                                     * OK, these are the items we are about to set out to perform..  Any additional signals will
                                     * be added into the list upon completion of the Execute!
                                     */

                                    CtiProtocolVersacom  Versacom(tapTRX->getType());

                                    // Someone else did all the parsing and is just needs building
                                    // Prime the Protocol device with the vstruct, and call the update routine
                                    if((status = Versacom.primeAndAppend(OutMessage.Buffer.VSt)) == ClientErrors::None)
                                    {
                                        status = Versacom.updateVersacomMessage();
                                    }


                                    if(status == ClientErrors::None)
                                    {
                                        OutMessage.TimeOut   = 2;
                                        OutMessage.InLength  = -1;

                                        for(j = 0; j < Versacom.entries(); j++)
                                        {
                                            OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( OutMessage );  // Create and copy

                                            if(NewOutMessage != NULL)
                                            {
                                                VSTRUCT VSt = Versacom.getVStruct(j);                      // Copy in the structure

                                                /* Calculate the length */
                                                USHORT Length = VSt.Nibbles +  2;

                                                NewOutMessage->TimeOut              = 2;
                                                NewOutMessage->InLength             = -1;
                                                NewOutMessage->OutLength            = Length;
                                                NewOutMessage->Buffer.TAPSt.Length  = Length;

                                                /* Build the message */
                                                NewOutMessage->Buffer.TAPSt.Message[0] = 'h';

                                                for(i = 0; i < VSt.Nibbles; i++)
                                                {
                                                    if(i % 2)
                                                    {
                                                        ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", VSt.Message[i / 2] & 0x0f);
                                                    }
                                                    else
                                                    {
                                                        ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (VSt.Message[i / 2] >> 4) & 0x0f);
                                                    }
                                                }

                                                NewOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';
                                                NewOutMessage->Buffer.TAPSt.Message[i + 2] = '\0';

                                                /* Now add it to the collection of outbound messages */
                                                WriteMessageToPorter(NewOutMessage);
                                            }
                                            else
                                            {
                                                status = ClientErrors::MemoryAccess;
                                            }
                                        }
                                    }
                                }

                                if(gsSPID != 0)
                                {
                                    CTILOG_INFO(dout, "EXPRESSCOM Filler message on route "<< Route->getName() <<" for transmitter "<< TransmitterDevice->getName());

                                    CtiCommandParser parse( "putconfig xcom sync" );
                                    CtiProtocolExpresscom  xcom;

                                    string byteString;

                                    // Add code here to send a sync page expresscom style.
                                    OutMessage.DeviceID = tapTRX->getID();
                                    OutMessage.Port     = tapTRX->getPortID();
                                    OutMessage.Remote   = tapTRX->getAddress();
                                    OutMessage.TimeOut  = 2;
                                    OutMessage.InLength = -1;

                                    OutMessage.Retry = 2;


                                    xcom.addAddressing(0, gsSPID);
                                    xcom.parseRequest(parse);
                                    xcom.setUseASCII(true);
                                    if( !gConfigParms.isTrue("YUKON_USE_EXPRESSCOM_CRC", true) )
                                    {
                                        xcom.setUseCRC(false);
                                    }

                                    if(xcom.entries() > 0)
                                    {
                                        OutMessage.EventCode |= ENCODED;               // Make the OM be ignored by porter...

                                        OutMessage.OutLength            = xcom.messageSize() +  2;
                                        OutMessage.Buffer.TAPSt.Length  = xcom.messageSize() +  2;

                                        /* Build the message */
                                        OutMessage.Buffer.TAPSt.Message[0] = xcom.getStartByte();

                                        int curByte;
                                        for(i = 0; i < xcom.messageSize(); i++)
                                        {
                                            curByte = xcom.getByte(i);
                                            OutMessage.Buffer.TAPSt.Message[i + 1] = curByte;
                                        }
                                        OutMessage.Buffer.TAPSt.Message[i + 1] = xcom.getStopByte();
                                        OutMessage.Buffer.TAPSt.Message[i + 2] = '\0';

                                        for(i = 0; i < OutMessage.OutLength; i++)
                                        {
                                            byteString += (char)OutMessage.Buffer.TAPSt.Message[i];
                                        }

                                        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( OutMessage );  // Create and copy
                                        /* Now add it to the collection of outbound messages */
                                        WriteMessageToPorter(NewOutMessage);
                                    }
                                }
                            }

                            break;
                        }
                    case TYPE_TAPTERM:
                        {
                            auto &tap = static_cast<Cti::Devices::TapPagingTerminal &>(*TransmitterDevice);

                            if( ! tap.isInhibited() && tap.getSendFiller() )
                            {
                                if( gsUID )
                                {
                                    CTILOG_INFO(dout, "VERSACOM Filler message on route "<< Route->getName() <<" for transmitter "<< tap.getName());

                                    OutMessage.DeviceID = tap.getID();
                                    OutMessage.TargetID = tap.getID();
                                    OutMessage.Priority = MAXPRIORITY;
                                    OutMessage.Buffer.VSt.UtilityID = (BYTE)gsUID; // All Call
                                    OutMessage.Retry    = 2;
                                    OutMessage.InLength = -1;
                                    OutMessage.Sequence = 0;
                                    OutMessage.Port = tap.getPortID();
                                    OutMessage.Remote = tap.getAddress();
                                    OutMessage.Buffer.VSt.CommandType  = VFILLER;

                                    /*
                                     *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
                                     *  control path to take with this OutMessage!
                                     */
                                    OutMessage.EventCode = VERSACOM | NORESULT | ENCODED;

                                    /*
                                     * OK, these are the items we are about to set out to perform..  Any additional signals will
                                     * be added into the list upon completion of the Execute!
                                     */

                                    CtiProtocolVersacom  Versacom(tap.getType());

                                    // Someone else did all the parsing and is just needs building
                                    // Prime the Protocol device with the vstruct, and call the update routine
                                    if((status = Versacom.primeAndAppend(OutMessage.Buffer.VSt)) == ClientErrors::None)
                                    {
                                        status = Versacom.updateVersacomMessage();
                                    }


                                    if(status == ClientErrors::None)
                                    {
                                        OutMessage.TimeOut   = 2;
                                        OutMessage.InLength  = -1;

                                        for(j = 0; j < Versacom.entries(); j++)
                                        {
                                            OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( OutMessage );  // Create and copy

                                            if(NewOutMessage != NULL)
                                            {
                                                VSTRUCT VSt = Versacom.getVStruct(j);                      // Copy in the structure

                                                /* Calculate the length */
                                                USHORT Length = VSt.Nibbles +  2;

                                                NewOutMessage->TimeOut              = 2;
                                                NewOutMessage->InLength             = -1;
                                                NewOutMessage->OutLength            = Length;
                                                NewOutMessage->Buffer.TAPSt.Length  = Length;

                                                /* Build the message */
                                                NewOutMessage->Buffer.TAPSt.Message[0] = 'h';

                                                for(i = 0; i < VSt.Nibbles; i++)
                                                {
                                                    if(i % 2)
                                                    {
                                                        ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", VSt.Message[i / 2] & 0x0f);
                                                    }
                                                    else
                                                    {
                                                        ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (VSt.Message[i / 2] >> 4) & 0x0f);
                                                    }
                                                }

                                                NewOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';
                                                NewOutMessage->Buffer.TAPSt.Message[i + 2] = '\0';

                                                /* Now add it to the collection of outbound messages */
                                                WriteMessageToPorter(NewOutMessage);
                                            }
                                            else
                                            {
                                                status = ClientErrors::MemoryAccess;
                                            }
                                        }
                                    }
                                }

                                if( gsSPID )
                                {
                                    CTILOG_INFO(dout, "EXPRESSCOM Filler message on route "<< Route->getName() <<" for transmitter "<< tap.getName());

                                    CtiCommandParser parse( "putconfig xcom sync" );
                                    CtiProtocolExpresscom  xcom;

                                    string byteString;

                                    // Add code here to send a sync page expresscom style.
                                    OutMessage.DeviceID = tap.getID();
                                    OutMessage.Port     = tap.getPortID();
                                    OutMessage.Remote   = tap.getAddress();
                                    OutMessage.TimeOut  = 2;
                                    OutMessage.InLength = -1;

                                    OutMessage.Retry = 2;


                                    xcom.addAddressing(0, gsSPID);
                                    xcom.parseRequest(parse);
                                    xcom.setUseASCII(true);

                                    //By default we use the CRC.
                                    if( !gConfigParms.isTrue("YUKON_USE_EXPRESSCOM_CRC", true) )
                                    {
                                        xcom.setUseCRC(false);
                                    }

                                    if(xcom.entries() > 0)
                                    {
                                        OutMessage.EventCode |= ENCODED;               // Make the OM be ignored by porter...

                                        OutMessage.OutLength            = xcom.messageSize() +  2;
                                        OutMessage.Buffer.TAPSt.Length  = xcom.messageSize() +  2;

                                        /* Build the message */
                                        OutMessage.Buffer.TAPSt.Message[0] = xcom.getStartByte();

                                        int curByte;
                                        for(i = 0; i < xcom.messageSize(); i++)
                                        {
                                            curByte = xcom.getByte(i);
                                            OutMessage.Buffer.TAPSt.Message[i + 1] = curByte;
                                        }
                                        OutMessage.Buffer.TAPSt.Message[i + 1] = xcom.getStopByte();
                                        OutMessage.Buffer.TAPSt.Message[i + 2] = '\0';

                                        for(i = 0; i < OutMessage.OutLength; i++)
                                        {
                                            byteString += (char)OutMessage.Buffer.TAPSt.Message[i];
                                        }

                                        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( OutMessage );  // Create and copy
                                        /* Now add it to the collection of outbound messages */
                                        WriteMessageToPorter(NewOutMessage);
                                    }
                                }
                            }

                            break;
                        }
                    default:
                        break;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }


    return;
}

/* Routine to generate filler messages */
void FillerThread()
{
    ULONG FillerRate = {300L};

    CTILOG_INFO(dout, "FillerThread started");

    /* First try to get the utility ID */
    if( !(gConfigParms.getValueAsString("PORTER_VERSACOM_UTILID")).empty() )
    {
        if( !(gsUID = atoi(gConfigParms.getValueAsString("PORTER_VERSACOM_UTILID").c_str())) )
        {
            // We have no joy in mudville.
            CTILOG_ERROR(dout, "FillerThread found no PORTER_VERSACOM_UTILID cparm. Exiting.");
            return;
        }
    }

    if( !(gConfigParms.getValueAsString("PORTER_EXPRESSCOM_SPID")).empty() )
    {
        ULONG value = gConfigParms.getValueAsULong("PORTER_EXPRESSCOM_SPID", 0);
        if(value <= CtiProtocolExpresscom::SpidMax)
        {
            gsSPID = static_cast<USHORT>(value);       // truncate from ULONG to USHORT...
        }
        else
        {
            gsSPID = 0;     // disable the Expresscom fill messages.
            CTILOG_WARN(dout, "Invalid value for PORTER_EXPRESSCOM_SPID cparm. Disabling Expresscom fill messages.");
        }
    }

    if(gConfigParms.isOpt("PORTER_FILLER_RATE"))
    {
        string FillerCommand = gConfigParms.getValueAsString("PORTER_FILLER_RATE");

        /* Find out how often to send fillers */
        if(!(FillerRate = atol(FillerCommand.c_str())))
        {
            /* Unable to convert so assume every hour */
            FillerRate = 3600L;
        }
    }
    else
    {
        if(!gConfigParms.isOpt("PORTER_VERSACOM_FILLER_RATE"))
        {
            FillerRate = 3600L;
        }
        else
        {
            string FillerCommand = gConfigParms.getValueAsString("PORTER_VERSACOM_FILLER_RATE");

            /* Find out how often to send fillers */
            if(!(FillerRate = atol(FillerCommand.c_str())))
            {
                /* Unable to convert so assume every five minutes */
                FillerRate = 3600L;
            }
        }
    }

    /* Let the port routines get started */
    if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 60000L) )
    {
        PorterQuit = TRUE;
    }

    if( !PorterQuit )
    {
        /* loop doing fillers at filler rate */
        for(; !PorterQuit ;)
        {
            /* Figure out how long to wait */
            ULONG WaitTime = 1000L * (FillerRate - (LongTime () % FillerRate));

            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], WaitTime) )
            {
                PorterQuit = TRUE;
                continue;
            }

            CTILOG_INFO(dout, "Sending Filler Messages");

            /* Send filler messages */
            PortManager.apply( applySendFiller, (void*)0);
            PortManager.apply( applySendFillerPage, (void*)0 );
        }
    }
}


void WriteMessageToPorter(OUTMESS *&OutMessage)
{
    /* Now add it to the collection of outbound messages */
    // In the beginning (6/25/01) ALL will send a sync message.

    if( OutMessage && PortManager.writeQueue(OutMessage))
    {
        CTILOG_ERROR(dout, "Could not write command to port queue for Port "<< OutMessage->Port);

        delete (OutMessage);
        OutMessage = 0;
    }

    OutMessage = 0;

    return;
}
