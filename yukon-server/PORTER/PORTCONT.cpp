/*-----------------------------------------------------------------------------*
*
* File:   PORTCONT
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTCONT.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#pragma title ( "Routines to Build Switch Control Messages" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTCONT.C

    Purpose:
        Routines to perform switch control on RTU's, VCU's and TCU's

    The following procedures are contained in this module:
        RemoteControl

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
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "lm_auto.h"
#include "perform.h"
#include "scanner.h"
#include "ilex.h"
#include "master.h"
#include "elogger.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "hashkey.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"

extern CtiPortManager PortManager;

/* Routine to perform a control on an RTU/TCU/VCU */
RemoteControl (OUTMESS *OutMessage)
{
#if 0    // FIX FIX FIX 090299 CGP
   OUTMESS *MyOutMessage;
   REMOTE RemoteRecord;
   CTIPOINT PointRecord;
   DEVICE DeviceRecord;
   CtiPortSPtr PortRecord;
   CONTROLMAP ControlMapRecord;
   ULONG i, j;
   USHORT DIO24;
   USHORT Port;
   USHORT Offset;

   /* Make sure this is an open or a close */
   if(OutMessage->Buffer.CSt.Value != OPENED &&
      OutMessage->Buffer.CSt.Value != CLOSED)
   {
      return(BADSTATE);
   }

   /* get the device record */
   memcpy (DeviceRecord.DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
   if((i = DeviceGetEqual (&DeviceRecord)) != NORMAL)
      return(i);

   /* Check if device inhibited */
   if(DeviceRecord.Status & (INHIBITED | CONTROLINHIBIT))
   {
      return(DEVICEINHIBITED);
   }

   /* get the point record */
   memcpy (PointRecord.DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
   memcpy (PointRecord.PointName, OutMessage->Buffer.CSt.PointName, STANDNAMLEN);
   if((i = PointGetEqual (&PointRecord)) != NORMAL)
   {
      return(i);
   }

   if(PointRecord.Status & (INHIBITED | CONTROLINHIBIT))
   {
      return(POINTINHIBITED);
   }

   /* make sure that this is one of em we know how to do */
   switch(DeviceRecord.DeviceType)
   {
   case TYPE_ILEXRTU:
   case TYPE_LCU415:
   case TYPE_LCU415LG:
   case TYPE_LCU415ER:
   case TYPE_LCUT3026:
   case TYPE_WELCORTU:
   case TYPE_VTU:
      break;

   case TYPE_DAS08:
      /* Find out which DIO24 */
      if((DIO24 = PointRecord.PointOffset / 100) < 1 ||
         DIO24 > 9 ||
         DIO24Mode[DIO24 - 1] == 0xffff)
      {
         return(BADPARAM);
      }

      /* Find out which port */
      if((Port = (PointRecord.PointOffset % 100) / 10) < 1 ||
         Port > 3)
      {
         return(BADPARAM);
      }

      /* get the offset */
      if((Offset = PointRecord.PointOffset % 10) < 1 ||
         Offset > 8)
      {
         return(BADPARAM);
      }

      if(OutMessage->Buffer.CSt.Value == OPENED)
      {
#ifdef MONKEYS_FLY
         WPort (DIO24Base + (DIO24 - 1) * 4 + Port - 1,
                (RPort (DIO24Base + (DIO24 - 1) * 4 + Port - 1) | (0x01 << (Offset - 1))));
#else
         fprintf(stderr,"Nobody has YUKONed this yet\n");
#endif

      }
      else
      {
#ifdef MONKEYS_FLY
         WPort (DIO24Base + (DIO24 - 1) * 4 + Port - 1,
                (RPort (DIO24Base + (DIO24 - 1) * 4 + Port - 1) &  ~(0x01 << (Offset - 1))));
#else
         fprintf(stderr,"Nobody has YUKONed this yet\n");
#endif
      }

      PointRecord.PreviousValue = PointRecord.CurrentValue;
      PointRecord.PreviousTime = PointRecord.CurrentTime;
      PointRecord.PreviousQuality = PointRecord.CurrentQuality;
      PointRecord.CurrentValue = OutMessage->Buffer.CSt.Value;
      PointRecord.CurrentTime = LongTime ();
      PointRecord.CurrentQuality = DSTSET (NORMAL);

      if((i = PointFastUpdate (&PointRecord)) != NORMAL)
      {
         return(i);
      }

      return(NORMAL);


   default:
      return(BADTYPE);
   }

   /* get the remote record */
   memcpy (RemoteRecord.RemoteName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
   if((i = RemoteGetEqual (&RemoteRecord)) != NORMAL)
   {
      return(i);
   }


   /* Make sure that we have a port */
   if(NULL != (PortRecord = PortManager.getMap().findValue(&TempKey)))
   {
      if( PortRecord->verifyPortIsRunnable( hPorterEvents[P_QUIT_EVENT] ) )
      {
         return BADPORT;
      }
   }

   /* it exists so now figure out what to do to it */
   /* Start by saving off the one we are interested in */
   PointRecord.PreviousValue = PointRecord.CurrentValue;
   PointRecord.PreviousTime = PointRecord.CurrentTime;
   PointRecord.PreviousQuality = PointRecord.CurrentQuality;
   PointRecord.CurrentValue = OutMessage->Buffer.CSt.Value;
   PointRecord.CurrentTime = LongTime ();
   PointRecord.CurrentQuality = DSTSET (NORMAL);

   if((i = PointFastUpdate (&PointRecord)) != NORMAL)
   {
      return(i);
   }

   /* Allocate some memory for the message out */
   if((MyOutMessage = CTIDBG_new OUTMESS) == NULL)
   {
      printf ("Error Allocating Memory\n");
      return(MEMORY);
   }

   /* Load all the other stuff that is needed */
   MyOutMessage->Port = RemoteRecord.Port;
   MyOutMessage->Remote = RemoteRecord.Remote;
   memcpy (MyOutMessage->DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
   memcpy (MyOutMessage->PointName, OutMessage->Buffer.CSt.PointName, STANDNAMLEN);
   MyOutMessage->TimeOut = 2;
   MyOutMessage->InLength = -1;
   MyOutMessage->Sequence = 0;
   MyOutMessage->Retry = 0;
   MyOutMessage->ReturnNexus = OutMessage->ReturnNexus;
   MyOutMessage->SaveNexus = OutMessage->SaveNexus;

   switch(DeviceRecord.DeviceType)
   {
   case TYPE_ILEXRTU:
      /* This actually takes two messages... a select and an execute */
      MyOutMessage->OutLength = 4;
      MyOutMessage->Priority = MAXPRIORITY;
      MyOutMessage->EventCode = NORESULT | ENCODED;
      MyOutMessage->ReturnNexus = NULL;
      MyOutMessage->SaveNexus = NULL;

      if(OutMessage->Buffer.CSt.Value == OPENED)
      {
         if((i = ILEXHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, RemoteRecord.Remote, ILEXSBOSELECT, 0, 0)) != NORMAL)
         {
            delete (MyOutMessage);
            return(i);
         }

         /* set the operation time */
         MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN + 1] = PointRecord.OpenTime / 100;
      }
      else if(OutMessage->Buffer.CSt.Value == CLOSED)
      {
         if((i = ILEXHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, RemoteRecord.Remote, ILEXSBOSELECT, 1, 0)) != NORMAL)
         {
            delete (MyOutMessage);
            return(i);
         }

         /* set the operation time */
         MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN + 1] = PointRecord.CloseTime / 100;
      }
      else
      {
         delete (MyOutMessage);
         return(BADSTATE);
      }

      /* set the point number */
      MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN] = PointRecord.PointOffset - 1;

      /* Sent the message on to the remote */
      /* Message is loaded so send it to appropriate queue */
      if(PortManager.writeQueue(RemoteRecord.Port, MyOutMessage->EventCode, sizeof (*MyOutMessage), (char *) MyOutMessage, MyOutMessage->Priority))
      {
         printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
         delete (MyOutMessage);
         return(QUEUE_WRITE);
      }

      /* Now send an execute */
      if((MyOutMessage = CTIDBG_new OUTMESS) == NULL)
      {
         printf ("Error Allocating Memory\n");
         return(MEMORY);
      }

      /* Load all the other stuff that is needed */
      MyOutMessage->Port = RemoteRecord.Port;
      MyOutMessage->Remote = RemoteRecord.Remote;
      memcpy (MyOutMessage->DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
      memcpy (MyOutMessage->PointName, OutMessage->Buffer.CSt.PointName, STANDNAMLEN);
      MyOutMessage->TimeOut = 2;
      MyOutMessage->InLength = -1;
      MyOutMessage->Sequence = 0;
      MyOutMessage->Retry = 0;
      MyOutMessage->ReturnNexus = OutMessage->ReturnNexus;
      MyOutMessage->SaveNexus = OutMessage->SaveNexus;
      MyOutMessage->OutLength = 3;
      MyOutMessage->Priority = MAXPRIORITY;
      MyOutMessage->EventCode = NORESULT | ENCODED;
      MyOutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
      MyOutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

      /* set up the execute */
      if((i = ILEXHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, RemoteRecord.Remote, ILEXSBOEXECUTE, 0, 0)) != NORMAL)
      {
         delete (MyOutMessage);
         return(i);
      }

      /* set the point number */
      MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN] = PointRecord.PointOffset - 1;

      /* Sent the message on to the remote */
      /* Message is loaded so send it to appropriate queue */
      if(PortManager.writeQueue(RemoteRecord.Port, MyOutMessage->EventCode, sizeof (*MyOutMessage), (char *) MyOutMessage, MyOutMessage->Priority))
      {
         printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
         delete (MyOutMessage);
         return(QUEUE_WRITE);
      }

      break;

   case TYPE_LCU415:
   case TYPE_LCU415LG:
   case TYPE_LCU415ER:
   case TYPE_LCUT3026:
      switch(PointRecord.PointOffset)
      {
      case 9:
         /* This is a lockout on / off message */
         MyOutMessage->OutLength = 4;
         MyOutMessage->Priority = MAXPRIORITY - 1;
         MyOutMessage->EventCode = NORESULT | ENCODED;
         MyOutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
         MyOutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

         if(PointRecord.CurrentValue == OPENED)
         {
            Send4PartToLogger ("Cms",
                               PointRecord.DeviceName,
                               "Sent LCU control loc",
                               "kout to OFF",
                               " ");

            if((i = MasterHeader(MyOutMessage->Buffer.OutMessage + PREIDLEN,
                                 RemoteRecord.Remote,
                                 MASTERLOCKOUTRESET,
                                 0)) != NORMAL)
            {
               delete (MyOutMessage);
               return(i);
            }
         }
         else if(PointRecord.CurrentValue == CLOSED)
         {
            Send4PartToLogger ("Cms",
                               PointRecord.DeviceName,
                               "Sent LCU control loc",
                               "kout to ON",
                               " ");

            if((i = MasterHeader(MyOutMessage->Buffer.OutMessage + PREIDLEN,
                                 RemoteRecord.Remote,
                                 MASTERLOCKOUTSET,
                                 0)) != NORMAL)
            {
               delete (MyOutMessage);
               return(i);
            }
         }
         else
         {
            return(BADSTATE);
         }

         break;

      default:
         /* On these points it is necessary to include the desired state of all points */
         memcpy (PointRecord.DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
         PointRecord.PointType = CONTROLPOINT;

         MyOutMessage->Buffer.OutMessage[PREIDLEN + MASTERLENGTH] = 0;
         if(!(PointGetDeviceTypeFirst (&PointRecord)))
         {
            do
            {
               if(PointRecord.CurrentValue == CLOSED)
               {
                  MyOutMessage->Buffer.OutMessage[PREIDLEN + MASTERLENGTH] |= (0x01 << (PointRecord.PointOffset - 1));
               }
            } while(!(PointGetDeviceTypeNext (&PointRecord)));
         }

         /* Load up to output to the device */
         MyOutMessage->OutLength = 5;
         MyOutMessage->Priority = MAXPRIORITY;
         MyOutMessage->EventCode = NORESULT | ENCODED;
         MyOutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
         MyOutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

         if((i = MasterHeader(MyOutMessage->Buffer.OutMessage + PREIDLEN,
                              RemoteRecord.Remote,
                              MASTERCONTROL,
                              1)) != NORMAL)
         {
            delete (MyOutMessage);
            return(i);
         }
      }

      /* Message is loaded so send it to appropriate queue */
      if(PortManager.writeQueue(RemoteRecord.Port,
                                MyOutMessage->EventCode,
                                sizeof (*MyOutMessage),
                                (char *) MyOutMessage,
                                MyOutMessage->Priority))
      {
         printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
         delete (MyOutMessage);
         return(QUEUE_WRITE);
      }

      break;

   case TYPE_WELCORTU:
   case TYPE_VTU:
      MyOutMessage->OutLength = 3;
      MyOutMessage->Priority = MAXPRIORITY;
      MyOutMessage->EventCode = NORESULT | ENCODED;
      MyOutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
      MyOutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

      /* Load Up the SBO Sectin */
      MyOutMessage->Buffer.OutMessage[5] = IDLC_SBOSELECT | 0x80;
      MyOutMessage->Buffer.OutMessage[6] = 3;

      MyOutMessage->Buffer.OutMessage[7] = LOBYTE (PointRecord.PointOffset - 1);


      /* Load the appropriate times into the message */
      switch(DeviceRecord.DeviceType)
      {
      case TYPE_WELCORTU:
         if(OutMessage->Buffer.CSt.Value == OPENED)
         {
            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (PointRecord.OpenTime / 10);
            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (PointRecord.OpenTime / 10) & 0x3f) | 0x40;
         }
         else if(OutMessage->Buffer.CSt.Value == CLOSED)
         {
            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (PointRecord.CloseTime / 10);
            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (PointRecord.CloseTime / 10) & 0x3f) | 0x80;
         }
         else
         {
            free (MyOutMessage);
            return(BADSTATE);
         }

         break;

      case TYPE_VTU:
         if(OutMessage->Buffer.CSt.Value == OPENED)
         {
            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (PointRecord.OpenTime);
            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (PointRecord.OpenTime) & 0x3f) | 0x40;
         }
         else if(OutMessage->Buffer.CSt.Value == CLOSED)
         {
            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (PointRecord.CloseTime);
            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (PointRecord.CloseTime) & 0x3f) | 0x80;
         }
         else
         {
            delete (MyOutMessage);
            return(BADSTATE);
         }

         break;
      }

      /* Message is loaded so send it to appropriate queue */
      if(PortManager.writeQueue(RemoteRecord.Port,
                                MyOutMessage->EventCode,
                                sizeof (*MyOutMessage),
                                (char *) MyOutMessage,
                                MyOutMessage->Priority))
      {
         printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
         delete (MyOutMessage);
         return(QUEUE_WRITE);
      }

      /* Now send an execute */
      if((MyOutMessage = CTIDBG_new OUTMESS) == NULL)
      {
         printf ("Error Allocating Memory\n");
         return(MEMORY);
      }

      /* Load all the other stuff that is needed */
      MyOutMessage->Port = RemoteRecord.Port;
      MyOutMessage->Remote = RemoteRecord.Remote;
      memcpy (MyOutMessage->DeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
      memcpy (MyOutMessage->PointName, OutMessage->Buffer.CSt.PointName, STANDNAMLEN);
      MyOutMessage->TimeOut = 2;
      MyOutMessage->InLength = -1;
      MyOutMessage->Sequence = 0;
      MyOutMessage->Retry = 0;
      MyOutMessage->ReturnNexus = OutMessage->ReturnNexus;
      MyOutMessage->SaveNexus = OutMessage->SaveNexus;
      MyOutMessage->OutLength = 1;
      MyOutMessage->Priority = MAXPRIORITY;
      MyOutMessage->EventCode = NORESULT | ENCODED;
      MyOutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
      MyOutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

      /* set up the SBO Execute */
      MyOutMessage->Buffer.OutMessage[5] = IDLC_SBOEXECUTE | 0x80;
      MyOutMessage->Buffer.OutMessage[6] = 1;

      MyOutMessage->Buffer.OutMessage[7] = LOBYTE (PointRecord.PointOffset - 1);

      /* Sent the message on to the remote */
      /* Message is loaded so send it to appropriate queue */
      if(PortManager.writeQueue(RemoteRecord.Port,
                                MyOutMessage->EventCode,
                                sizeof (*MyOutMessage),
                                (char *) MyOutMessage,
                                MyOutMessage->Priority))
      {
         printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
         delete (MyOutMessage);
         return(QUEUE_WRITE);
      }

      break;

   default:
      return(BADTYPE);
   }

   /* Check to see if we need to force a scan to detect the change */
   if(DeviceRecord.DeviceType == TYPE_ILEXRTU    ||
      DeviceRecord.DeviceType == TYPE_WELCORTU)
   {
      /* always force a scan for RTU's - it does not hurt anything */
      if(DeviceLock (&DeviceRecord))
      {
         DeviceUnLock (&DeviceRecord);
         return(NORMAL);
      }

      /* Open semaphore to scanner */
      if(ScannerSem == (HEV) NULL)
      {
         if(CTIOpenEventSem (SCANNERSEM, &ScannerSem, MUTEX_ALL_ACCESS))
         {
            DeviceUnLock (&DeviceRecord);
            return(NORMAL);
         }
      }

      /* set next scan to now */
      DeviceRecord.NextScan = LongTime ();
      DeviceFastUpdate (&DeviceRecord);

      if(CTIPostEventSem (ScannerSem))
      {
         CTICloseEventSem (&ScannerSem);
         ScannerSem = (HEV) NULL;
      }
   }
   else if(DeviceRecord.DeviceType == TYPE_LCU415 ||
           DeviceRecord.DeviceType == TYPE_LCU415LG ||
           DeviceRecord.DeviceType == TYPE_LCU415ER ||
           DeviceRecord.DeviceType == TYPE_LCUT3026)
   {
      memcpy (ControlMapRecord.ControlDeviceName, OutMessage->Buffer.CSt.DeviceName, STANDNAMLEN);
      memcpy (ControlMapRecord.ControlPointName, OutMessage->Buffer.CSt.PointName, STANDNAMLEN);

      if(ControlGetEqual (&ControlMapRecord))
      {
         /* no control map record */
         return(NORMAL);
      }

      /* get the device record for the scan device */
      memcpy (DeviceRecord.DeviceName, ControlMapRecord.StatusDeviceName, STANDNAMLEN);
      if(DeviceLock (&DeviceRecord))
      {
         DeviceUnLock (&DeviceRecord);
         return(NORMAL);
      }

      /* Check if the device is out of scan */
      if(DeviceRecord.Status & INHIBITED)
      {
         DeviceUnLock (&DeviceRecord);
         return(NORMAL);
      }

      /* Check out the next scan time */
      if(DeviceRecord.NextScan == -1L)
      {
         DeviceUnLock (&DeviceRecord);
         return(NORMAL);
      }


      /* if we will find out about this quickly wait */
      if(DeviceRecord.ScanRate <= 10)
      {
         DeviceUnLock (&DeviceRecord);
         return(NORMAL);
      }

      /* Force a scan on the status point */
      /* Open semaphore to scanner */
      if(ScannerSem == (HEV) NULL)
      {
         if(CTIOpenEventSem (SCANNERSEM, &ScannerSem, MUTEX_ALL_ACCESS))
         {
            DeviceUnLock (&DeviceRecord);
            return(NORMAL);
         }
      }

      if(DeviceRecord.NextScan > LongTime () + 5L &&
         DeviceRecord.NextAccumScan > LongTime () + 5L)
      {
         DeviceRecord.NextScan = LongTime () + 5L;
         DeviceFastUpdate (&DeviceRecord);

         if(CTIPostEventSem (ScannerSem))
         {
            CTICloseEventSem (&ScannerSem);
            ScannerSem = (HEV) NULL;
         }
      }
      else
         DeviceUnLock (&DeviceRecord);

   }
#else
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

#endif
   return(NORMAL);
}
