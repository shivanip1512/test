/*-----------------------------------------------------------------------------*
*
* File:   TAPTERM
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/TAPTERM.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:21 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#pragma warning (disable : 4786)
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "portsup.h"
#include "tcpsup.h"
#include "perform.h"
#include "tapterm.h"

#include "portglob.h"

#include "port_base.h"
#include "port_modem.h"
#include "dev_base.h"
#include "dev_tap.h"
#include "xfer.h"


extern HCTIQUEUE*       QueueHandle(LONG pid);

int TAPTraceOut (PBYTE, ULONG);
int TAPTraceIn (PBYTE, PULONG, BOOL, PBYTE, ULONG, BOOL);
int TAPPrintChar (PBYTE, PULONG, CHAR);

TAPTermHandShake (OUTMESS *OutMessage,
                  INMESS *InMessage,
                  CtiPort *PortRecord,
                  CtiDeviceBase *Device)
{
   BYTE    SendBuffer[300];
   BYTE    Buffer[300];
   ULONG   ReceiveLength;
   ULONG   SendLength;
   ULONG   i, Count, Retry;
   USHORT  CheckSum;
   ULONG   QueueCount;
   BYTE    TraceBuffer[300];
   ULONG   TracePointer;

   BOOL    Waste;                // OK, This is a reference variable temp.

   BOOL    *HangUp = &Waste;      // Prime this with the temporary.

   CtiXfer trx;


   CtiDeviceTapPagingTerminal*   TapDev = (CtiDeviceTapPagingTerminal*) Device;

   if(PortRecord->isDialup())    // if it exists make the reference point at it..
   {
      HangUp = &(((CtiPortModem*)PortRecord)->getShouldHangup());
   }

   *HangUp = FALSE;

   /* If we need to log on, do so... */
   if(Device->getLogOnNeeded())
   {
      *HangUp = TRUE;

      /* Flush the buffers */
      PortRecord->outClear();

      /* Send CR's till we get a response */
      for(Count = 0; Count < TAPCOUNT_N1; Count++)
      {
         /* Send a CR to the Paging terminal */
         SendBuffer[0] = CHAR_CR;

#ifdef OLD_STUFF

         i = OutMess ((UCHAR*)SendBuffer,
                      1,
                      PortRecord,
                      Device);
#else
         trx.setOutBuffer(SendBuffer);
         trx.setOutCount(1);
         i = PortRecord->outMess(trx, Device);
#endif

         if(i != NORMAL)
         {
            return(i);
         }

         TAPTraceOut (SendBuffer, 1);

         /* Try to read the ID message */
         PortRecord->inClear();

#ifdef OLD_STUFF
         i = InMess ((UCHAR*)Buffer,
                     3,
                     PortRecord,
                     Device,
                     TAPTIME_T2,
                     &ReceiveLength,
                     0,
                     FALSE)
#else

         trx.setInBuffer(Buffer);
         trx.setInCountExpected(3);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T2);
         trx.setMessageStart();                          // This is the first "in" of this message
         trx.setMessageComplete(0);                      // This is the last "in" of this message
         trx.setTraceMask(0);

         i = PortRecord->inMess(trx, Device);
#endif


         if(i != NORMAL)
         {
            if(i != READTIMEOUT)
            {
               break;
            }
         }
         else
         {
            TAPTraceIn (TraceBuffer,
                        &TracePointer,
                        TRUE,
                        Buffer,
                        3,
                        TRUE);

            /* Check if this is ID= */
            if(!(strnicmp ((CHAR*)Buffer, "ID=", 3)))
            {
               break;
            }
         }
      }

      if(i)
      {
         return(i);
      }

      /* Build up the login message */
      SendBuffer[0] = CHAR_ESC;
      SendBuffer[1] = 'P';
      SendBuffer[2] = 'G';
      SendBuffer[3] = '1';

      SendLength = 4;

#if 0    // FIX FIX FIX This transmitter needs adding

      /* Check to see if we have a password */
      if(DeviceRecord->RouteName[2][0] != ' ')
      {
         memcpy (&SendBuffer[4], DeviceRecord->RouteName[2], 6);
         SendBuffer[10] = CHAR_CR;
         SendLength += 7;
      }
      else
      {
         SendBuffer[4] = CHAR_CR;
         SendLength += 1;
      }

#endif

      if(!TapDev->getPassword().isNull())
      {
         memcpy (&SendBuffer[4], TapDev->getPassword().data(), 6);
         SendBuffer[10] = CHAR_CR;
         SendLength += 7;
      }
      else
      {
         SendBuffer[4] = CHAR_CR;
         SendLength += 1;
      }

      TAPTraceOut (SendBuffer, SendLength);

      /* Flush the receive buffer */
      PortRecord->inClear();

      /* Send this out to the Terminal */
#ifdef OLD_STUFF
      i = OutMess ((UCHAR*)SendBuffer,
                   SendLength,
                   PortRecord,
                   Device);
#else
      trx.setOutBuffer(SendBuffer);
      trx.setOutCount(SendLength);
      i = PortRecord->outMess(trx, Device);
#endif

      if(i != NORMAL)
      {
         return(i);
      }

      TAPTraceIn (TraceBuffer, &TracePointer, TRUE, NULL, 0, FALSE);

      /* Wait for the ACK characater */
      for(;;)
      {

#ifdef OLD_STUFF
         i = InMess ((UCHAR*)Buffer,
                     1,
                     PortRecord,
                     Device,
                     TAPTIME_T3,
                     &ReceiveLength,
                     0,
                     FALSE);
#else
         trx.setInBuffer(Buffer);
         trx.setInCountExpected(1);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart();                          // This is the first "in" of this message
         trx.setMessageComplete(0);                      // This is the last "in" of this message
         trx.setTraceMask(0);

         i = PortRecord->inMess(trx, Device);
#endif

         if(i != NORMAL)
         {

            return(i);
         }

         TAPTraceIn (TraceBuffer, &TracePointer, FALSE, Buffer, 1, FALSE);

         if(Buffer[0] == CHAR_ACK)
         {
            break;
         }

         if(Buffer[0] == CHAR_NAK)
         {
            break;
         }

         if(Buffer[0] == CHAR_ESC)
         {

            /* get two more characters */
#ifdef OLD_STUFF

            i = InMess ((UCHAR*)(Buffer + 1),
                        2,
                        PortRecord,
                        Device,
                        TAPTIME_T3,
                        &ReceiveLength,
                        0,
                        FALSE)
#else

            trx.setInBuffer(Buffer + 1);
            trx.setInCountExpected(2);
            trx.setInCountActual(&ReceiveLength);
            trx.setInTimeout(TAPTIME_T3);
            trx.setMessageStart(FALSE);
            trx.setMessageComplete(FALSE);
            trx.setTraceMask(0);

            i = PortRecord->inMess(trx, Device);
#endif
            if(i != NORMAL)
            {
               return(i);
            }

            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, Buffer + 1, 2, FALSE);
         }
      }

      /* We have three characters, see if we were successful */
      if(Buffer[0] != CHAR_ACK)
      {
         TAPTraceIn (TraceBuffer, &TracePointer, FALSE, NULL, 0, TRUE);

         if(Buffer[2] == CHAR_EOT)
         {
            return(ERRUNKNOWN);
         }

         return(ERRUNKNOWN);
      }

      /* We are hooked up if we get here so wait till we get an ESC */
      do
      {

#ifdef OLD_STUFF

         i = InMess ((UCHAR*)Buffer, 1, PortRecord, Device, TAPTIME_T3, &ReceiveLength, 0, FALSE);
#else
         trx.setInBuffer(Buffer);
         trx.setInCountExpected(1);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart(FALSE);
         trx.setMessageComplete(FALSE);
         trx.setTraceMask(0);

         i = PortRecord->inMess(trx, Device);
#endif
         if(i != NORMAL)
         {
            return(i);
         }

         TAPTraceIn (TraceBuffer, &TracePointer, FALSE, Buffer, 1, FALSE);
      } while(Buffer[0] != CHAR_ESC);

      /* There should be three more characters coming back */
#ifdef OLD_STUFF

      i = InMess ((UCHAR*)Buffer,
                  3,
                  PortRecord,
                  Device,
                  TAPTIME_T3,
                  &ReceiveLength,
                  0,
                  FALSE);
#else
      trx.setInBuffer(Buffer);
      trx.setInCountExpected(3);
      trx.setInCountActual(&ReceiveLength);
      trx.setInTimeout(TAPTIME_T3);
      trx.setMessageStart(TRUE);
      trx.setMessageComplete(FALSE);
      trx.setTraceMask(0);

      i = PortRecord->inMess(trx, Device);
#endif
      if(i != NORMAL)
      {
         return(i);
      }

      TAPTraceIn (TraceBuffer, &TracePointer, FALSE,  Buffer, 3, TRUE);

      /* Check that we got [p */
      if(Buffer[0] != '[')
      {
         return(ERRUNKNOWN);
      }

      if(Buffer[1] != 'p')
      {
         return(ERRUNKNOWN);
      }

      if(Buffer[2] != CHAR_CR)
      {
         return(ERRUNKNOWN);
      }

      Device->setLogOnNeeded( FALSE );
      *HangUp = FALSE;
   }

   /* Build up the message to send to the paging terminal */
   SendLength = 0;
   SendBuffer[SendLength++] = CHAR_STX;

   /* Load the CapCode into the message */
   #if 0
   for(Count = 0; Count < STANDNAMLEN; Count++)
   {
      if(OutMessage->Buffer.TAPSt.CapCode[Count] == ' ')
      {
         break;
      }

      SendBuffer[SendLength++] = OutMessage->Buffer.TAPSt.CapCode[Count];
   }
   #else
   for(Count = 0; Count < TapDev->getTap().getPagerNumber().length() ; Count++)
   {
      SendBuffer[SendLength++] = TapDev->getTap().getPagerNumber()[(size_t)Count]; // OutMessage->Buffer.TAPSt.CapCode[Count];
   }
   #endif

   SendBuffer[SendLength++] = CHAR_CR;

   /* Load up the message */
   for(Count = 0; Count < OutMessage->Buffer.TAPSt.Length; Count++)
   {
      /* Check if this is a SUB character */
      if(OutMessage->Buffer.TAPSt.Message[Count] < 0x20)
      {
         SendBuffer[SendLength++] = CHAR_SUB;
         SendBuffer[SendLength++] = OutMessage->Buffer.TAPSt.Message[Count] + 0x40;
      }
      else
      {
         SendBuffer[SendLength++] = OutMessage->Buffer.TAPSt.Message[Count] & 0x7f;
      }
   }

   SendBuffer[SendLength++] = CHAR_CR;
   SendBuffer[SendLength++] = CHAR_ETX;

   /* Compute the CheckSum */
   CheckSum = 0;

   for(Count = 0; Count < SendLength; Count++)
   {
      CheckSum += SendBuffer[Count] & 0x7f;
   }

   SendBuffer[SendLength++] = ((CheckSum >> 8) & 0x0f) + 0x30;
   SendBuffer[SendLength++] = ((CheckSum >> 4) & 0x0f) + 0x30;
   SendBuffer[SendLength++] = (CheckSum & 0x0f) + 0x30;

   SendBuffer[SendLength++] = CHAR_CR;

   /* We may be doing this a few times */
   for(Retry = 0; Retry < TAPCOUNT_N2; Retry++)
   {
      /* Send this out to the Terminal */
      TAPTraceOut (SendBuffer, SendLength);

#ifdef OLD_STUFF
      i = OutMess ((UCHAR*)SendBuffer, SendLength, PortRecord, Device);
#else
      trx.setOutBuffer(SendBuffer);
      trx.setOutCount(SendLength);
      i = PortRecord->outMess(trx, Device);
#endif

      if(i != NORMAL)
      {
         return(i);
      }

      /* We should get a response back */
      PortRecord->inClear();


      TAPTraceIn (TraceBuffer, &TracePointer, TRUE, NULL, 0, FALSE);

      Count = 0;
      do
      {
#ifdef OLD_STUFF

         i = InMess ((UCHAR*)&Buffer[Count],
                     1,
                     PortRecord,
                     Device,
                     TAPTIME_T3,
                     &ReceiveLength,
                     0,
                     FALSE);
#else
         trx.setInBuffer(&Buffer[Count]);
         trx.setInCountExpected(1);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart(TRUE);
         trx.setMessageComplete(FALSE);
         trx.setTraceMask(0);

         i = PortRecord->inMess(trx, Device);
#endif
         if(i != NORMAL)
         {
            break;
         }

         TAPTraceIn (TraceBuffer, &TracePointer,  FALSE, &Buffer[Count], 1, FALSE);

         Count++;
      } while(Buffer[Count - 1] != CHAR_ACK &&
              Buffer[Count - 1] != CHAR_NAK &&
              Buffer[Count - 1] != CHAR_RS &&
              Buffer[Count - 1] != CHAR_ESC);

      /* There should be another character or two coming in... */
      if(Buffer[Count - 1] == CHAR_ESC)
      {
#ifdef OLD_STUFF
         if(InMess ((UCHAR*)&Buffer[Count],
                    2,
                    PortRecord,
                    Device,
                    TAPTIME_T3,
                    &ReceiveLength,
                    0,
                    FALSE) == NORMAL)
#else
         trx.setInBuffer(&Buffer[Count]);
         trx.setInCountExpected(2);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart(TRUE);
         trx.setMessageComplete(FALSE);
         trx.setTraceMask(0);

         if( PortRecord->inMess(trx, Device) == NORMAL)
#endif
         {
            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, &Buffer[Count], 2, TRUE);
         }
         else
         {
            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, NULL, 0, TRUE);
         }
      }
      else
      {
#ifdef OLD_STUFF
         if(InMess ((UCHAR*)&Buffer[Count],
                    1,
                    PortRecord,
                    Device,
                    TAPTIME_T3,
                    &ReceiveLength,
                    0,
                    FALSE) == NORMAL)
#else
         trx.setInBuffer(&Buffer[Count]);
         trx.setInCountExpected(1);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart(TRUE);
         trx.setMessageComplete(FALSE);
         trx.setTraceMask(0);

         if( PortRecord->inMess(trx, Device) == NORMAL)
#endif
         {
            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, &Buffer[Count], 1, TRUE);
         }
         else
         {
            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, NULL, 0, TRUE);
         }
      }

      if(i)
      {
         continue;
      }

      /* We have some kind of response... see what it is */
      else if(Buffer[Count - 1] == CHAR_ACK)
      {
         break;
      }
      else if(Buffer[Count - 1] == CHAR_NAK)
      {
         continue;
      }
      else
      {
         break;
      }
   }

   if(i)
   {
      Device->setLogOnNeeded(TRUE);
   }
   else if(Buffer[Count - 1] == CHAR_NAK ||
           Buffer[Count - 1] == CHAR_ESC)
   {
      Device->setLogOnNeeded(TRUE);
      i = ERRUNKNOWN;
   }
   else
   {
      if(Buffer[Count -1] == CHAR_RS)
      {
         i = ERRUNKNOWN;
      }

      /* Check if there are any more elements on this queue */
      QueryQueue (*QueueHandle(PortRecord->getPortID()), &QueueCount);

      if(QueueCount == 0)
      {
         Device->setLogOnNeeded(TRUE);
      }
   }

   /* Initiate a disconect sequence */
   if( Device->getLogOnNeeded() )
   {
      /* Force a hangup when we get back */
      *HangUp = TRUE;

      SendBuffer[0] = CHAR_EOT;
      SendBuffer[1] = CHAR_CR;

      TAPTraceOut (SendBuffer, 2);

#ifdef OLD_STUFF
      i = OutMess ((UCHAR*)SendBuffer,
                   2,
                   PortRecord,
                   Device);
#else
      trx.setOutBuffer(SendBuffer);
      trx.setOutCount(2);
      i = PortRecord->outMess(trx, Device);
#endif
      if(i == NORMAL)
      {
         PortRecord->inClear();

         TAPTraceIn (TraceBuffer, &TracePointer,  TRUE, NULL, 0, FALSE);

         Count = 0;
         do
         {
#ifdef OLD_STUFF
            if(InMess ((UCHAR*)&Buffer[Count],
                       1,
                       PortRecord,
                       Device,
                       TAPTIME_T3,
                       &ReceiveLength,
                       0,
                       FALSE) != NORMAL)
#else
            trx.setInBuffer(&Buffer[Count]);
            trx.setInCountExpected(1);
            trx.setInCountActual(&ReceiveLength);
            trx.setInTimeout(TAPTIME_T3);
            trx.setMessageStart(TRUE);
            trx.setMessageComplete(FALSE);
            trx.setTraceMask(0);

            if( PortRecord->inMess(trx, Device) == NORMAL)
#endif
            {
               break;
            }

            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, &Buffer[Count], 1, FALSE);

            Count++;
         } while(Buffer[Count - 1] != CHAR_ESC);

         /* get two more characters ... */
#ifdef OLD_STUFF
         if(InMess ((UCHAR*)&Buffer[Count],
                    2,
                    PortRecord,
                    Device,
                    TAPTIME_T3,
                    &ReceiveLength,
                    0,
                    FALSE) == NORMAL)
#else
         trx.setInBuffer(&Buffer[Count]);
         trx.setInCountExpected(2);
         trx.setInCountActual(&ReceiveLength);
         trx.setInTimeout(TAPTIME_T3);
         trx.setMessageStart(TRUE);
         trx.setMessageComplete(FALSE);
         trx.setTraceMask(0);

         if( PortRecord->inMess(trx, Device) == NORMAL)
#endif
         {
            TAPTraceIn (TraceBuffer, &TracePointer, FALSE, &Buffer[Count], 2, TRUE);
         }
         else
         {
            TAPTraceIn (TraceBuffer,
                        &TracePointer,
                        FALSE,
                        NULL,
                        0,
                        TRUE);
         }
      }
   }

   return(i);
}

TAPTraceOut (PBYTE Message, ULONG Count)
{
   ULONG i;
   BYTE  TraceBuffer[300];
   ULONG TracePointer;

   if(TraceFlag)
   {
      sprintf ((CHAR*)TraceBuffer, "Sent to TAP Terminal:  ");
      TracePointer = 23;

      for(i = 0; i < Count; i++)
      {
         TAPPrintChar (TraceBuffer, &TracePointer, Message[i]);
      }

      sprintf ((CHAR*)&TraceBuffer[TracePointer], "\n\0");

      printf ("%s", TraceBuffer);
   }

   return(NORMAL);
}

TAPTraceIn (PBYTE TraceBuffer,
            PULONG TracePointer,
            BOOL Header,
            PBYTE Message,
            ULONG Count,
            BOOL Trailer)
{
   ULONG i;

   if(TraceFlag)
   {
      if(Header)
      {
         sprintf ((CHAR*)TraceBuffer, "Received from TAP Terminal:  ");
         *TracePointer = 29;
      }

      if(Count && Message != NULL)
      {
         for(i = 0; i < Count; i++)
         {
            TAPPrintChar (TraceBuffer, TracePointer, Message[i]);
         }
      }

      if(Trailer)
      {
         sprintf ((CHAR*)&TraceBuffer[*TracePointer], "\n\0");
         printf ("%s", TraceBuffer);
      }
   }

   return(NORMAL);
}


TAPPrintChar (PBYTE TraceBuffer, PULONG TracePointer, CHAR Char)
{
   switch(Char)
   {
   case CHAR_CR:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<CR>");
      *TracePointer += 4;
      break;

   case CHAR_LF:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<LF>");
      *TracePointer += 4;
      break;

   case CHAR_ESC:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<ESC>");
      *TracePointer += 5;
      break;

   case CHAR_STX:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<STX>");
      *TracePointer += 5;
      break;

   case CHAR_ETX:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<ETX>");
      *TracePointer += 5;
      break;

   case CHAR_US:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<US>");
      *TracePointer += 4;
      break;

   case CHAR_ETB:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<ETB>");
      *TracePointer += 5;
      break;

   case CHAR_EOT:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<EOT>");
      *TracePointer += 5;
      break;

   case CHAR_SUB:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<SUB>");
      *TracePointer += 5;
      break;

   case CHAR_ACK:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<ACK>");
      *TracePointer += 5;
      break;

   case CHAR_NAK:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<NAK>");
      *TracePointer += 5;
      break;

   case CHAR_RS:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "<RS>");
      *TracePointer += 4;
      break;

   default:
      sprintf ((CHAR*)&TraceBuffer[*TracePointer], "%c", Char);
      *TracePointer += 1;
      break;
   }

   return(NORMAL);
}
