/*-----------------------------------------------------------------------------*
*
* File:   PORTFILL
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTFILL.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:31 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#pragma warning (disable : 4786)
#pragma title ( "Filler Message Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
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
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"
#include "master.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "port_base.h"
#include "dev_base.h"
#include "rtdb.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_tcu.h"
#include "dev_tap.h"
#include "prot_versacom.h"

extern HCTIQUEUE* QueueHandle(LONG pid);

INT SendFillerPage(USHORT UtilityID);

/* Routine to generate filler messages */
VOID FillerThread (PVOID Arg)

{
   ULONG FillerRate = {300L};
   USHORT UtilityID;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Filler Thread Starting as TID: " << CurrentTID() << endl;
   }

   /* First try to get the utility ID */
   if( !(gConfigParms.getValueAsString("PORTER_VERSACOM_UTILID")).isNull() )
   {
      if( !(UtilityID = atoi(gConfigParms.getValueAsString("PORTER_VERSACOM_UTILID").data())) )
      {
         // We have no joy in mudville.
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Filler Thread found no PORTER_VERSACOM_UTILID cparm.  Exiting." << endl;
         }
         return;
      }
   }

   if(!gConfigParms.isOpt("PORTER_VERSACOM_FILLER_RATE"))
   {
      FillerRate = 3600L;
   }
   else
   {
      RWCString FillerCommand = gConfigParms.getValueAsString("PORTER_VERSACOM_FILLER_RATE");

      /* Find out how often to send fillers */
      if(!(FillerRate = atol(FillerCommand.data())))
      {
         /* Unable to convert so assume every five minutes */
         FillerRate = 3600L;
      }
   }

   /* Let the port routines get started */
   if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 60000L) )
   {
      PorterQuit = TRUE;
   }

   if( !PorterQuit )
   {
      /* send out first fillers */
      SendFiller (UtilityID);

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

         /* Send filler messages */
         SendFiller (UtilityID);

         SendFillerPage(UtilityID);

      }
   }
}

/* Routine to generate filler messages */
SendFiller (USHORT UtilityID)
{
   ULONG j;
   OUTMESS *OutMessage;

   /*Scan ports */
   CtiPort *PortRecord;

   RWRecursiveLock<RWMutexLock>::LockGuard  prt_guard(PortManager.getMux());
   RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(DeviceManager.getMux());

   CtiRTDB<CtiPort>::CtiRTDBIterator        itr_prt(PortManager.getMap());
   CtiRTDB<CtiDeviceBase>::CtiRTDBIterator  itr_dev(DeviceManager.getMap());

   // Scan through the ports
   for(; ++itr_prt && !PorterQuit ;)
   {
      CtiPort *Port = itr_prt.value();

      itr_dev.reset(DeviceManager.getMap());    // Reset the device iterator.

      for(; ++itr_dev && !PorterQuit ;)
      {
         if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0) )
         {
            PorterQuit = TRUE;
            break;
         }

         CtiDeviceBase *TransmitterDevice = itr_dev.value();

         if(Port->getPortID() == TransmitterDevice->getPortID() && !TransmitterDevice->isInhibited())
         {
            switch(TransmitterDevice->getType())
            {
            case TYPE_TCU5000:
            case TYPE_TCU5500:
               {
                  CtiDeviceTCU *xcu = (CtiDeviceTCU *)TransmitterDevice;

                  // In the beginning (6/25/01) NONE will send a filler message.

                  if(xcu->getSendFiller())
                  {
                     /* Allocate some memory */
                     if((OutMessage = new OUTMESS) == NULL)
                     {
                        continue;
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
                     OutMessage->SaveNexus = NULL;

                     MasterHeader (OutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Remote, MASTERSEND, 2);

                     /* Now build the filler message */
                     OutMessage->Buffer.OutMessage[PREIDLEN + 4] = 0xb8;
                     OutMessage->Buffer.OutMessage[PREIDLEN + 5] = LOBYTE (UtilityID);

                     if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                     {
                        printf ("Error Writing to Queue for Port %2hd\n", xcu->getPortID());
                        delete (OutMessage);
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

   return(NORMAL);
}

/* Routine to generate filler messages */
SendFillerPage(USHORT UtilityID)
{
   INT status = NORMAL;
   ULONG i, j;
   OUTMESS OutMessage;

   /*Scan ports */
   CtiPort *PortRecord;

   RWRecursiveLock<RWMutexLock>::LockGuard  prt_guard(PortManager.getMux());
   RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(DeviceManager.getMux());

   CtiRTDB<CtiPort>::CtiRTDBIterator        itr_prt(PortManager.getMap());
   CtiRTDB<CtiDeviceBase>::CtiRTDBIterator  itr_dev(DeviceManager.getMap());

   // Scan through the ports
   for(; ++itr_prt && !PorterQuit ;)
   {
      CtiPort *Port = itr_prt.value();

      itr_dev.reset(DeviceManager.getMap());    // Reset the device iterator.

      for(; ++itr_dev && !PorterQuit ;)
      {
         if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0) )
         {
            PorterQuit = TRUE;
            break;
         }

         CtiDeviceBase *TransmitterDevice = itr_dev.value();

         if(Port->getPortID() == TransmitterDevice->getPortID() && !TransmitterDevice->isInhibited())
         {
            switch(TransmitterDevice->getType())
            {
            case TYPE_TAPTERM:
               {
                  CtiDeviceTapPagingTerminal *tapTRX = (CtiDeviceTapPagingTerminal *)TransmitterDevice;

                  if(!tapTRX->isInhibited() && tapTRX->getSendFiller())
                  {

                     OutMessage.DeviceID = tapTRX->getID();
                     OutMessage.TargetID = tapTRX->getID();
                     OutMessage.Priority = MAXPRIORITY;
                     OutMessage.Buffer.VSt.UtilityID = 0xFF; // All Call
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
                     if((status = Versacom.primeVStruct(OutMessage.Buffer.VSt)) == NORMAL)
                     {
                        status = Versacom.updateVersacomMessage();
                     }


                     if(status == NORMAL)
                     {
                        OutMessage.TimeOut   = 2;
                        OutMessage.InLength  = -1;

                        for(j = 0; j < Versacom.entries(); j++)
                        {
                           OUTMESS *NewOutMessage = new OUTMESS( OutMessage );  // Create and copy

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
                                    sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", VSt.Message[i / 2] & 0x0f);
                                 }
                                 else
                                 {
                                    sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (VSt.Message[i / 2] >> 4) & 0x0f);
                                 }
                              }

                              NewOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';

                              /* Now add it to the collection of outbound messages */
                              // In the beginning (6/25/01) ALL will send a filler message.
                              if(PortManager.writeQueue(NewOutMessage->Port, NewOutMessage->EventCode, sizeof (*NewOutMessage), (char *) NewOutMessage, NewOutMessage->Priority))
                              {
                                 printf ("Error Writing to Queue for Port %2hd\n", tapTRX->getPortID());
                                 delete (NewOutMessage);
                              }
                           }
                           else
                           {
                              status = MEMORY;
                           }
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

   return(NORMAL);
}
