#pragma title ( "Versacom Support Routines" )
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   VERSACOM
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/VERSACOM.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:21 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        versacom.c

    Purpose:
        Generate and send Versacom messages to various remotes

    The following procedures are contained in this module:
        VersacomSend            setNibble
        VersaCommSend           VCUTime

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        3-9-98   Added Support for TAP Terminals                    WRO


   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <memory.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"

// Build in the new Versacom code!
#ifndef NEW_CODE
   #define NEW_CODE
#endif

extern CtiPortManager   PortManager;
extern HCTIQUEUE*       QueueHandle(LONG pid);

INT VersacomWrap(OUTMESS *OutMessage);
INT VersacomEmetconWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage);
INT VersacomMastercomWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage);
INT VersacomTAPWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage);

VersacomSend (OUTMESS *OutMessage)
{
   return (VersacomWrap(OutMessage));
}

/*-------------------------------------------------------------------*
 * This function accepts an OUTMESS with the assumption that the
 * internal Buffer.VSt.Message contains a valid Versacom protocol
 * byte stream.  It is fully complete and ready for broadcast.  All
 * that remains is the wrapping of the message based upon the
 * transmitter type.
 *
 * The message is copied into the new OUTMESS appropriately for the
 * wrap type.  This new OUTMESS is then written to the port queue
 * of the transmitter device.
 *
 * OutMessage memory is cleaned up in portentry.cpp's VersacomMessage()
 * which calls this routine.
 *-------------------------------------------------------------------*/
INT VersacomWrap(OUTMESS *OutMessage)
{
   INT            status            = NORMAL;
   OUTMESS        *MyOutMessage     = NULL;

   /* Allocate the memory for the queue entry */
   if((MyOutMessage = new OUTMESS) == NULL)
   {
      return(MEMORY);
   }

   MyOutMessage->EventCode = VERSACOM;
   if(OutMessage->EventCode & RESULT)
   {
      MyOutMessage->EventCode |= RESULT;
   }

   switch(CCUInfo.findValue(&OutMessage->DeviceID)->Type)
   {
   case TYPE_CCU700:
   case TYPE_CCU710:
   case TYPE_CCU711:
      {
         status = VersacomEmetconWrapped(OutMessage, MyOutMessage);
         break;
      }
   case TYPE_LCU415:
      {
         /* This is a mastercomm device */
         MyOutMessage->EventCode |= RIPPLE;

         /**** LET IT FALL THROUGH TO THE TCU CODE! ****/
      }
   case TYPE_TCU5000:
   case TYPE_TCU5500:
      {
         /* This is a mastercomm device */
         /* Load up all the goodies */

         status = VersacomMastercomWrapped(OutMessage, MyOutMessage);
         break;
      }
   case TYPE_TAPTERM:
      {
         status = VersacomTAPWrapped(OutMessage, MyOutMessage);
         break;
      }
   }

   return status;
}


/* Routine to calculate time a VHF shed will take */
VCUTime (OUTMESS *OutMessage, PULONG Seconds)
{
   SHORT Count;
   SHORT Repeats;
   PSZ RepEnv;

   /* Check for number of repeats on message transmit */
   if(CTIScanEnv ("VCOMREPEATS", &RepEnv))
   {
      Repeats = 3;
   }
   else
   {
      if(!(Repeats = atoi (RepEnv)))
      {
         Repeats = 3;
      }
   }

   /* first see how many bytes are involved */
   Count = OutMessage->Buffer.OutMessage[PREIDLEN + 3] + 1;

   /* Calculate the number of bits involved */
   Count *= 8;
   Count += 27; /* Preamble, Postamble, BCH and trailing bits */;

   /* Now Calculate the time */
   *Seconds = (((Count * 20L) + 200L) * Repeats) + 80L;
   *Seconds /= 1000L;
   *Seconds += 2;

   return(NORMAL);
}


INT VersacomEmetconWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage)
{
   ULONG          i;
   INT            status = NORMAL;
   USHORT         Length;
   BSTRUCT        BSt;
   DEVICE         DeviceRecord;
   ROUTE          RouteRecord;

   /* this is an EMETCON device so load up the Preamble, B, and C words */
   /* copy everything for now... fix later */
   *MyOutMessage = *OutMessage;

   /* Versacom wrapped into Emetcon requires faking the word build
      routines into supporting the structure */

   /* Calcultate the number of words that will be involved */
   if(OutMessage->Buffer.VSt.Nibbles <= 6)
      OutMessage->Buffer.VSt.NumW = 0;
   else if(OutMessage->Buffer.VSt.Nibbles <= 16)
      OutMessage->Buffer.VSt.NumW = 1;
   else if(OutMessage->Buffer.VSt.Nibbles <= 26)
      OutMessage->Buffer.VSt.NumW = 2;
   else
      OutMessage->Buffer.VSt.NumW = 3;

   /* Load up the hunks of the B structure that we need */
   BSt.Port                = OutMessage->Port;
   BSt.Remote              = OutMessage->Remote;
   BSt.NumW                = OutMessage->Buffer.VSt.NumW;

   BSt.DlcRoute.Amp        = OutMessage->Buffer.VSt.DlcRoute.Amp;
   BSt.DlcRoute.Feeder     = OutMessage->Buffer.VSt.DlcRoute.Feeder;
   BSt.DlcRoute.Stages     = OutMessage->Buffer.VSt.DlcRoute.Stages;
   BSt.DlcRoute.RepVar     = OutMessage->Buffer.VSt.DlcRoute.RepVar;
   BSt.DlcRoute.RepFixed   = OutMessage->Buffer.VSt.DlcRoute.RepFixed;

   /* Now build the address */
   BSt.Address = 0x3e0000 | ((ULONG) OutMessage->Buffer.VSt.Message[0] << 8) | (ULONG) OutMessage->Buffer.VSt.Message[1];

   /* Now build the function */
   BSt.Function = OutMessage->Buffer.VSt.Message[2];
   if(OutMessage->Buffer.VSt.Nibbles < 5)
   {
      BSt.Function |= ((OutMessage->Buffer.VSt.Nibbles + 1) / 2) & 0x000f;
   }

   /* Now build the IO function */
   if(OutMessage->Buffer.VSt.Nibbles < 5)
      BSt.IO = 2;
   else
      BSt.IO = 0;

   /* If anything left place it in structure for C words */
   if(OutMessage->Buffer.VSt.NumW)
   {
      /* see how much is left */
      BSt.Length = ((OutMessage->Buffer.VSt.Nibbles - 6) + 1) / 2;

      /* clear the memory buffer */
      memset (BSt.Message, 0, sizeof (BSt.Message));
      /* copy it into a cleared out message buffer */
      memcpy (BSt.Message, OutMessage->Buffer.VSt.Message + 3, BSt.Length);
   }
   else
      BSt.Length = 0;

   /* Things are now ready to go */
   switch(CCUInfo.findValue(&OutMessage->DeviceID)->Type)
   {
   case TYPE_CCU700:
   case TYPE_CCU710:
      {
         MyOutMessage->EventCode &= ~QUEUED;
         MyOutMessage->EventCode |= (DTRAN | BWORD);
      }
   case TYPE_CCU711:
      {
         /* check if queing is allowed */
         if(NoQueing)
         {
            MyOutMessage->EventCode &= ~QUEUED;
            MyOutMessage->EventCode |= (DTRAN  | BWORD);
         }
         else
            MyOutMessage->EventCode |= BWORD;


         if(MyOutMessage->EventCode & DTRAN)
         {
            /* Load the B word */
            B_Word (MyOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN, &BSt);
            /* Load the C words if neccessary */
            if(OutMessage->Buffer.VSt.NumW)
               C_Words (MyOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN,
                        BSt.Message,
                        BSt.Length,
                        &BSt.NumW);

            /* Now do the preamble */
            BPreamble (MyOutMessage->Buffer.OutMessage+PREIDLEN, &BSt);

            /* Calculate the length of the message */
            MyOutMessage->OutLength = PREAMLEN + (OutMessage->Buffer.VSt.NumW + 1) * BWORDLEN + 3;
            MyOutMessage->EventCode |= DTRAN & BWORD;
            MyOutMessage->TimeOut   = TIMEOUT + BSt.DlcRoute.Stages * (BSt.NumW + 1);

            /* load the IDLC specific stuff for DTRAN */
            MyOutMessage->Source                = 0;
            MyOutMessage->Destination           = DEST_DLC;
            MyOutMessage->Command               = CMND_DTRAN;
            MyOutMessage->InLength              = 2;
            MyOutMessage->Buffer.OutMessage[6]  = (UCHAR)MyOutMessage->InLength;
            MyOutMessage->EventCode             &= ~RCONT;

            /* Put the beastie on the queue */
            if(PortManager.writeQueue(OutMessage->Port,
                                      MyOutMessage->EventCode,
                                      sizeof (*MyOutMessage),
                                      (char *) MyOutMessage,
                                      MyOutMessage->Priority))
            {
               printf ("\nError Placing entry onto Queue\n");
               status = QUEUE_WRITE;
            }
            else
            {
               CCUInfo.findValue(&MyOutMessage->DeviceID)->PortQueueEnts++;
            }

            break;
         }
         else
         {
            /* this is queing so check if this CCU queue is open */
            if(CCUInfo.findValue(&OutMessage->DeviceID)->QueueHandle == (HCTIQUEUE) NULL)
            {
               status = BADCCU;
            }
            else
            {
               /* Load up the B word stuff */
               MyOutMessage->Buffer.BSt = BSt;

               /* Go ahead and send block to the appropriate queing queue */
               if(WriteQueue (CCUInfo.findValue(&MyOutMessage->DeviceID)->QueueHandle,
                              MyOutMessage->EventCode,
                              sizeof (*MyOutMessage),
                              (char *) MyOutMessage,
                              MyOutMessage->Priority))
               {
                  printf ("Error Writing to Queue for Port,CCU:  %hd,%hd\n", MyOutMessage->Port, MyOutMessage->Remote);
                  status = QUEUE_WRITE;
               }
            }
         }
         break;
      }
   }

   if(status != NORMAL)
   {
      /* Calling process is informed when VersacomWrap() returns to the VersacomSend callee */
      delete (MyOutMessage);
   }

   return status;
}

INT VersacomMastercomWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage)
{
   INT            status = NORMAL;
   ULONG          i;
   USHORT         Length;

   /* This is a mastercomm device */
   /* Load up all the goodies */

   MyOutMessage->DeviceID  = OutMessage->DeviceID;
   MyOutMessage->Port      = OutMessage->Port;
   MyOutMessage->Remote    = OutMessage->Remote;
   MyOutMessage->Priority  = OutMessage->Priority;
   MyOutMessage->Retry     = OutMessage->Retry;
   MyOutMessage->Sequence  = OutMessage->Sequence;
   MyOutMessage->TimeOut   = 2;
   MyOutMessage->InLength  = -1;

   /* Calculate the length */
   Length = (OutMessage->Buffer.VSt.Nibbles + 1) / 2;

   MyOutMessage->OutLength = MASTERLENGTH + Length;

   /* Build MasterComm header */
   if((i = MasterHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Remote, MASTERSEND, Length)) != NORMAL)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") Error: " << i << endl;
      status = i;
   }
   else
   {
      /* Copy message into buffer */
      memcpy (MyOutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, OutMessage->Buffer.VSt.Message, Length);

      /* Put the beastie on the queue */
      if((i = PortManager.writeQueue(OutMessage->Port,
                          MyOutMessage->EventCode,
                          sizeof (*MyOutMessage),
                          (char *) MyOutMessage,
                          MyOutMessage->Priority)) != NORMAL)
      {
         printf ("\nError Placing entry onto Queue:  %ld\n", i);
         status = SYSTEM;
      }
   }

   if(status != NORMAL)
   {
      /* Calling process is informed when VersacomWrap() returns to the VersacomSend callee */
      delete (MyOutMessage);
   }

   return status;
}


INT VersacomTAPWrapped(OUTMESS *OutMessage, OUTMESS *MyOutMessage)
{
   ULONG          i;
   INT            status            = NORMAL;
   USHORT         Length;
   DEVICE         DeviceRecord;
   ROUTE          RouteRecord;
   static PSZ     CapEnvironment    = NULL;

   /* TAP - Telelocator Alphanumeric Protcol (very loose use of the word) is a method of sending a page to
   a paging supplier over LL or dialup.  TAP itself is handled elsewhere but at this point we need to:
       1.  ASCIIize the message into an ASCII hex representation of the versacom message.
       2.  Put an "H" on for a header and a "G" for the trailer of the message
       3.  Find the capcode for this message.  The devicename is in the devicename feild of the message.  This allows
           us to get the device record, which in turn allow us to get the first route record, which has the capcode in
           the first repeater name feild.  If this fails it checks for DSM2_CAPCODE for the capcode.  If that
           fails we are screwed big time. */

   /* get the device record */
   // FIX FIX FIX 090199 CGP memcpy (DeviceRecord.DeviceName, OutMessage->DeviceName, STANDNAMLEN);

   if((status = DeviceGetEqual (&DeviceRecord)) == NORMAL)
   {
      /* Check the Route */
      if(DeviceRecord.RouteName[0][0] != ' ')
      {
         memcpy (RouteRecord.RouteName, DeviceRecord.RouteName[0], STANDNAMLEN);
         if((status = RouteGetEqual (&RouteRecord)) == NORMAL)
         {
            /* First Repeater Name should be the CAPCODE */
            if(RouteRecord.Repeater[0].RepeaterName[0] != ' ')
            {
               memcpy (MyOutMessage->Buffer.TAPSt.CapCode, RouteRecord.Repeater[0].RepeaterName, STANDNAMLEN);
            }
            else
            {
               status = ERRUNKNOWN;
            }
         }
      }
      else
      {
         status = ERRUNKNOWN;
      }
   }

   if(status == NORMAL)
   {
      if(CapEnvironment == NULL)
      {
         /* Attempt to get the capcode from environment */
         if((CTIScanEnv ("DSM2_CAPCODE", &CapEnvironment)) != NORMAL)
         {
            printf ("Error getting Default CapCode\n");
            status = SYSTEM;
         }
      }
      else
      {
         /* Copy the sucker over */
         for(i = 0; i < STANDNAMLEN; i++)
         {
            if(CapEnvironment[i] == '\0')
            {
               break;
            }
            MyOutMessage->Buffer.TAPSt.CapCode[i] = CapEnvironment[i];
         }

         for(; i < STANDNAMLEN; i++)
         {
            MyOutMessage->Buffer.TAPSt.CapCode[i] = ' ';
         }
      }

      MyOutMessage->Port      = OutMessage->Port;
      MyOutMessage->Remote    = OutMessage->Remote;
      MyOutMessage->Priority  = OutMessage->Priority;
      MyOutMessage->Retry     = OutMessage->Retry;
      MyOutMessage->Sequence  = OutMessage->Sequence;
      MyOutMessage->TimeOut   = 2;
      MyOutMessage->InLength  = -1;

      /* Calculate the length */
      Length = OutMessage->Buffer.VSt.Nibbles +  2;

      MyOutMessage->OutLength = Length;
      MyOutMessage->Buffer.TAPSt.Length = Length;

      /* Build the message */
      MyOutMessage->Buffer.TAPSt.Message[0] = 'h';

      for(i = 0; i < OutMessage->Buffer.VSt.Nibbles; i++)
      {
         if(i % 2)
         {
            sprintf (&MyOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", OutMessage->Buffer.VSt.Message[i / 2] & 0x0f);
         }
         else
         {
            sprintf (&MyOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (OutMessage->Buffer.VSt.Message[i / 2] >> 4) & 0x0f);
         }
      }

      MyOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';

      #ifdef DEBUG7
      {
          printf ("TAP Message:  ");
      }

      for(i = 0; i < Length; i++)
      {
         printf ("%c", MyOutMessage->Buffer.TAPSt.Message[i]);
      }
      printf ("\n");
      #else
      /* Put the beastie on the queue */
      if((status = PortManager.writeQueue(OutMessage->Port,
                               MyOutMessage->EventCode,
                               sizeof (*MyOutMessage),
                               (char *) MyOutMessage,
                               MyOutMessage->Priority)) != NORMAL)
      {
         printf ("\nError Placing entry onto Queue:  %ld\n", i);
      }
      #endif
   }


   if(status != NORMAL)
   {
      /* Calling process is informed when VersacomWrap() returns to the VersacomSend callee */
      delete (MyOutMessage);
   }

   return status;
}


