#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   DUPAPLUS
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/DUPAPLUS.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:27 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <ctype.h>
#include <math.h>

// #include "btrieve.h"
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
#include "portglob.h"

#include "dialup.h"
#include "dupaplus.h"
#include "dev_base.h"

// Some defines to turn on/off options
// #define UTS_ID


static FLOAT    DemandDecimals = 1.0;
static FLOAT    EnergyDecimals = 1.0;
static INT      InIndex;

CTI_ap_class   APlusClasses[] = {
   {  0, 40    ,  "Primary Metering Constants"},
   {  1, 5     ,  "Password Table"},
   {  2, 64    ,  "Identification & Demand Constants"},
   {  3, 196   ,  "Main display table"},
   {  4, 176   ,  "TOU rate schedule"},
   {  6, 288   ,  "Metering Configuration"},
   {  7, 304   ,  "Secondary Metering Constants"},
   {  8, 64    ,  "Firmware Configuration"},
   {  9, 48    ,  "Status Area #1"},
   { 10, 24    ,  "Status Area #2"},
   { 11, 212   ,  "Current billing data"},
   { 12, 212   ,  "Previous period billing data"},
   { 13, 212   ,  "Previous season billing data"},
   { 14, 42    ,  "Load profile configuration"},
   { 15, 15    ,  "Event log configuration"},
   { 16, 999   ,  "Event log data"},
   { 17, 999   ,  "Load profile data"},
   { 18, 999   ,  "Load profile partial data"},
   { 19, 5     ,  "Future password table"},
   { 20, 64    ,  "Future identification and demand table"},
   { 21, 196   ,  "Future display table 1"},
   { 22, 176   ,  "Future rate schedule"},
   { 30, -1    ,  "Partial interval pulse counts"},
   { 31, 46    ,  "Modem billing call configuration table"},
   { 32, 46    ,  "Modem alarm call configuration information"},
   { 33, 64    ,  "Modem configuration information"},
   { 34, 24    ,  "Modem communication's status"},
   { 72, 999   ,  "Rules Class"}
};

/* Some of these here 'uns might be needed too */

CTI_ap_func   APlusFunctions[] = {
   {  0x01, 4     ,  "Password check"},
   {  0x02, 3     ,  "Time set"},
   {  0x06, 1     ,  "Who are you?"},
   {  0x07, 3     ,  "Billing read call en date"},
   {  0x08, 1     ,  "Call back command function"},
   {  0x09, 1     ,  "Packet size"},
   {  0x0B, 2     ,  "Critical peak"},
   {  0x0C, 1     ,  "Time sync"},
   {  0xF2, 1     ,  "Communications time-out value"},
};

/* This is global locally */


/* Do the ABB PowerPlus Alpha Mambo */
INT
APlusHandShake   (OUTMESS     *OutMessage,
                  INMESS      *InMessage,
                  CtiPort     *PortRecord,
                  CtiDeviceBase   *RemoteRecord)
{
   INT         retval = NORMAL;
   INT         ret;
   INT         Attempts = 3;
   ULONG       PWord;
   INT         Retries = OutMessage->Retry;
   BYTE        Command;                      // Command Byte comming in from calling app.
   BYTE        Function;                     // If a data command
   BYTE        Class;                        // If a class read Function needs to be a class
   BYTE        Buffer[256];
   BYTE        InBuffer[512];
   ULONG       ReceiveLength;
   ULONG       State = HANDSHAKE_INIT_STATE;
   BYTEULONG   EncKey;
   BYTEULONG   EncPWD;
   BYTEUSHORT  Length;
   BYTEUSHORT  Offset;
   BOOL        bSuccess = FALSE;
   DIALUPREPLY  *DUPRep    = &InMessage->Buffer.DUPSt.DUPRep;

   // Implementation datails
   REQUESTRECORD ReqRecord;
   CTIXMIT       Transfer;

   ReqRecord.OMessage = OutMessage;
   ReqRecord.IMessage = InMessage;
   ReqRecord.PRecord  = PortRecord;
   ReqRecord.RRecord  = RemoteRecord;

   printf("ALPHA DEBUG:  Inside handshake routine\n");

   if(!OutMessage || !InMessage || !PortRecord || !RemoteRecord )
   {
      printf("Record cannot be determined from the provided information\n");
      retval =  MISPARAM;                       // Missing Parameters
      State = HANDSHAKE_ERROR_STATE;
   }
   else
   {
      // Copy the request into the InMessage side....
      memcpy(&DUPRep->ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));
   }

   while(
          (State != HANDSHAKE_ERROR_STATE)      &&
          (State != HANDSHAKE_COMPLETE_STATE)   &&
          Attempts > 0
        )
   {
      switch(State)
      {
      case HANDSHAKE_INIT_STATE:
         {
            Command  = (BYTE) OutMessage->Buffer.DUPReq.Command[0];     // Command Byte comming in from calling app.
            Function = (BYTE) OutMessage->Buffer.DUPReq.Command[1];     // If a data command
            Class    = (BYTE) OutMessage->Buffer.DUPReq.Command[1];     // If a class read Function needs to be a class
         /*
            if(TraceFlag)
               printf("Alpha Request Received Command: 0x%02X Class/Function: 0x%02X\n\tLen, Offset: 0x%02X, 0x%02X\n",Command, Class, Length.sh, Offset.sh);
         */

            InIndex = 0;   // set so our return data is in order starting at zero...

            Length.sh = OutMessage->Buffer.DUPReq.Command[2];
            Offset.sh = OutMessage->Buffer.DUPReq.Command[3];

            DUPRep->CompFlag = DIALUP_COMP_START;     // This effectively zero's out the other possibilities
            Attempts = 10;

#ifdef UTS_ID
            State = HANDSHAKE_IDENTIFY_STATE;
#else
            State = HANDSHAKE_WHO_STATE;
#endif
            break;
         }
      case HANDSHAKE_IDENTIFY_STATE:
         {
            ClearBuffer(Buffer, sizeof(Buffer));      // Reset the buffers to Zeros
            ClearBuffer(InBuffer, sizeof(InBuffer));
            ReceiveLength = 0;
            /*
             * get the boys talking.
             *
             * We are able to send the UTS "I" command at any time before handshake to ensure
             * that an Alpha P+ is out there....  It will respond with an ASCII "ABB Alpha,01   "
             * string....
             *
             */

            Buffer[0] = 'I';

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 1;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 16;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = 0;

            DialupSendAndReceive(&Transfer, &ReqRecord);

            if(ReceiveLength && strncmp((CHAR*)InBuffer, "ABB ALPHA,01   \r", ReceiveLength)) // We go in if they are not identical
            {
               printf("Failed to get ABB Indentity from the ALPHA\n");

               Attempts--;
               retval = ERRUNKNOWN;
               State = HANDSHAKE_IDENTIFY_STATE;      // Yes, this is redundant, but I like it.

            }
            else
            {
               DUPRep->CompFlag |= DIALUP_COMP_ID;
               retval            = NORMAL;
               Attempts          = 6;
               State             = HANDSHAKE_WHO_STATE;
            }
            break;
         }
      case HANDSHAKE_WHO_STATE:
         {
            /*
             * We send a "Who are You?" message to the meter
             * Meter responds with an ID sequence and a password encryption key
             * We respond with one of three passwords based upon the desired access.
             *
             * The Alpha UART is multiplexed amongst the optical & internal interface
             * it looks at us about once every three seconds, so to sync up the first
             * time we need to try a little harder... thus this do loop
             */
            ClearBuffer(Buffer, sizeof(Buffer));      // Reset the buffers to Zeros
            ClearBuffer(InBuffer, sizeof(InBuffer));
            ReceiveLength = 0;

            sprintf((CHAR*)Buffer,"%c%c%c%c%c%c", STX, AP_CMD_WITH_DATA, AP_FUNC_WHO_ARE_YOU, 0x00, 0x01, 0x00);

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 6;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 15;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

            ret = DialupSendAndReceive(&Transfer, &ReqRecord);


            if( ret || ReceiveLength != 15 )
            {
               if(ReceiveLength >= 2)
                  DUPRep->AckNak  = DecodeAckNak(InBuffer[2]);


               if(TraceFlag && ret == DIALUP_ERR_BADCRC)
               {
                  printf("Alpha data failed CRC verification. %d attempts remain\n", Attempts);
                  retval = BADCRC;
               }
               else if(TraceFlag && ret == DIALUP_ERR_NODATA)
               {
                  printf("No Reply from the Alpha. %d attempts remain\n", Attempts);
                  retval = READTIMEOUT;
               }
               else if(TraceFlag && ret == DIALUP_ERR_RDTIMEOUT)
               {
                  printf("Timeout on Alpha data read\n");
                  retval = READTIMEOUT;
               }
               else
               {
                  printf("Unknown error. Aborting read.\n");
                  sprintf((CHAR*)Buffer,"%c%c", STX, AP_CMD_TERMINATE);
                  retval                     = ERRUNKNOWN;

                  Transfer.OutBuffer         = Buffer;
                  Transfer.OutCount          = 2;
                  Transfer.Flags             = XFER_ADD_CRC;
                  DialupSend(&Transfer, &ReqRecord);
               }

               Attempts--;
               ReceiveLength  = 0;
               State          = HANDSHAKE_WHO_STATE;

               CTISleep(1000);
            }
            else    // We got a ID/ENC. Key combination
            {
               EncKey.ul   = MAKEULONG ( MAKEUSHORT (InBuffer[12], InBuffer[11]), MAKEUSHORT (InBuffer[10], InBuffer[9]));
               retval      = NORMAL;
               Attempts    = 3;
               State       = HANDSHAKE_PASSWORD_STATE;
            }

            break;
         }
      case HANDSHAKE_PASSWORD_STATE:
         {
            memcpy(Buffer, RemoteRecord->getPassword(), 8);       // This is where the password is kept.
            Buffer[8] = '\0';
            PWord = atol((CHAR*)Buffer);

            EncPWD.ul = APlusCrypt(EncKey.ul , PWord );

            ClearBuffer(Buffer, sizeof(Buffer));     // Sanity Check...
            ClearBuffer(InBuffer, sizeof(InBuffer));
            ReceiveLength = 0;

            sprintf((CHAR*)Buffer,"%c%c%c%c%c%c%c%c%c",
                    STX,
                    AP_CMD_WITH_DATA,
                    AP_FUNC_PASSWORD,
                    PAD,
                    0x04,
                    EncPWD.ch[3],
                    EncPWD.ch[2],
                    EncPWD.ch[1],
                    EncPWD.ch[0]);

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 9;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 6;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

            DialupSendAndReceive(&Transfer, &ReqRecord);

            DUPRep->AckNak  = DecodeAckNak(InBuffer[2]);

            if(DUPRep->AckNak || ReceiveLength != 6)
            {
               // This guy naked all over my password, or we got nothing back

               Attempts--;
               ReceiveLength  = 0;   // We need a new password
               retval         = ERRUNKNOWN;
               State          = HANDSHAKE_PASSWORD_STATE;
            }
            else
            {
               DUPRep->CompFlag |= DIALUP_COMP_PWD;
               /* Displays Status */
               DUPRep->Status    = PowerPlusStatus(InBuffer[3], FALSE, NULL);
               Attempts          = 3;
               State             = HANDSHAKE_COMMAND_STATE;
            }

            break;   // Break the password loop
         }
      case HANDSHAKE_COMMAND_STATE:
         {
            if(TraceFlag)
               printf("*** The ALPHA is listening ***\n");

            /**********************************************************************
             *
             * Alpha is waiting on us for commands.
             *
             * Main dispatch for Alpha commands
             *
             **********************************************************************/

            State = HANDSHAKE_COMPLETE_STATE;

            switch(Command)
            {
            case AP_CMD_NO_DATA:
               {
                  DoCommandNoData(Function, &ReqRecord);
                  break;
               }
            case AP_CMD_WITH_DATA:
               {
                  DoCommandWithData(Function, &ReqRecord);
                  break;
               }
            case AP_CMD_CLASS_READ:
               {
                  DUPRep->CompFlag |= DIALUP_COMP_CLASS0;

                  if(DoGetClassZero(&ReqRecord))
                  {
                     if(TraceFlag)
                        printf("Couldn't get configuration data.  Using default decimal offsets\n");

                     // Just in case garbage happened.
                     DemandDecimals = 1.0;
                     EnergyDecimals = 1.0;
                     //break;
                  }

                  switch(Class)
                  {
                  case 72:                         // Rules Class uses Offset to chose the table.
                     {
                        if(Offset.sh > 2)
                        {
                           printf("Rules class reads can only be made for offset 1 and 2\n");
                           printf("Data is returned for 1 & 3, or 2 & 4 respecively\n");
                           retval = BADPARAM;
                        }
                        else
                        {
                           DoClassRead(Class,         // Class
                                       Offset.sh,     // Offset   0 = Default
                                       Length.sh,     // Length   0 = Default
                                       &ReqRecord);

                        }
                        break;
                     }
                  default:
                     {
                        DoClassRead(Class,   // Class
                                    Offset.sh,     // Offset   0 = Default
                                    Length.sh,     // Length   0 = Default
                                    &ReqRecord);
                        break;
                     }
                  }
                  break;
               }
            case AP_CMD_PARTIAL_READ:
               {
                  DoClassRead(Class,         // Class
                              Offset.sh,     // Offset
                              Length.sh,     // Length
                              &ReqRecord);
                  break;
               }
            default:
               printf("\n\n***** Something just went very wrong *****\n");
               printf("Command = 0x%02X", Command);
               State = HANDSHAKE_ERROR_STATE;
               break;
            }

            // Terminate the session
            sprintf((CHAR*)Buffer,"%c%c", STX, AP_CMD_TERMINATE);
            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 2;
            Transfer.Flags             = XFER_ADD_CRC;
            DialupSend(&Transfer, &ReqRecord);

            break;
         }
      case HANDSHAKE_ERROR_STATE:
      default:
         {
            // Be sure he's set to something bad enough...
            if(!retval) retval = ERRUNKNOWN;

            if(TraceFlag)
            {
               printf("Communication failed in stage 0x%04X\n", State);
               printf("*** Communications Error or Incorrect Alpha password ***\n");
            }

            #ifdef DEBUG
            AlphaFailures++;
            if(TraceFlag)
               printf("%u communication failures to alphas since porter started\n", AlphaFailures);
            #endif

         }
      }
   }

   return retval;
}

UCHAR DecodeAckNak(UCHAR AckNak)
{
   UCHAR    ret = AckNak;     // TRUE will indicate a NAK response....

   if(TraceFlag)
   {
      switch(AckNak)
      {
         case 0x00:
            // ACK - No Error
            break;
         case 0x01:
            printf("NAK: Bad CRC\n");
            break;
         case 0x02:
            printf("NAK: Communications Lockout against this function\n");
            break;
         case 0x03:
            printf("NAK: Illegal command, syntax, or length\n");
            break;
         case 0x04:
            printf("NAK: Framing Error\n");
            break;
         case 0x05:
            printf("NAK: Timeout Error\n");
            break;
         case 0x06:
            printf("NAK: Invalid Password\n");
            break;
         case 0x07:
            printf("NAK: NAK Received from computer\n");
            break;
         case 0x0C:
            printf("NAK: Request in progress, try again later\n");
            break;
         case 0x0D:
            printf("NAK: Too busy to honor request, try again later\n");
            break;
         case 0x0F:
            printf("NAK: Rules Class NAK, Request not supported by current class 70/71 definition\n");
            break;
         default:
            printf("NAK: Unknown NAK. Refer to ABB documentation\n");
            break;
      }
   }
   return ret;
}

UCHAR PowerPlusStatus(UCHAR Status, BOOL bClear, REQUESTRECORD *ReqRecord)
{
   UCHAR    ret = Status;     // TRUE will indicate a NAK response....
   BYTE     OBuff[20];
   BYTE     IBuff[20];
   ULONG    Cnt;

   DIALUPREPLY  *DUPRep;
   CTIXMIT     Transfer;

/****************************************
 * Your basic defines for status
      #define  AP_AUTOREAD_OCCURED  0x80
      #define  AP_SEASON_CHANGE     0x40
      #define  AP_POWER_FAIL        0x20
      #define  AP_GENERAL_ALARM     0x10
      #define  AP_WRITE_PROTECT     0x08
      #define  AP_FUTURE_CONFIG     0x04
      #define  AP_DEMAND_RESET      0x02
      #define  AP_TIME_CHANGE       0x01
 ****************************************
*/
   if(Status & AP_TIME_CHANGE && TraceFlag)
   {
      printf("Alpha Status: Time change has occured\n");
   }

   if(Status & AP_DEMAND_RESET && TraceFlag)
   {
      printf("Alpha Status: Demand Reset has occurred\n");
   }

   if(Status & AP_FUTURE_CONFIG && TraceFlag)
   {
      printf("Alpha Status: Future Config Bit set\n");
   }

   if(Status & AP_WRITE_PROTECT && TraceFlag)
   {
      printf("Alpha Status: Alpha is write protected\n");
   }

   if(Status & AP_GENERAL_ALARM && TraceFlag)
   {
      printf("Alpha Status: General Alarm \n");
   }

   if(Status & AP_POWER_FAIL && TraceFlag)
   {
      printf("Alpha Status: Power Fail\n");
   }

   if(Status & AP_SEASON_CHANGE && TraceFlag)
   {
      printf("Alpha Status: Season Change has occurred\n");
   }

   if(Status & AP_AUTOREAD_OCCURED && TraceFlag)
   {
      printf("Alpha Status: Autoread has occurred\n");
   }


   if(bClear && ReqRecord)
   {

      DUPRep = &ReqRecord->IMessage->Buffer.DUPSt.DUPRep;

      sprintf((CHAR*)OBuff,"%c%c%c",
              STX,
              AP_CMD_NO_DATA,
              AP_FUNC_CAO);

      Transfer.OutBuffer         = OBuff;
      Transfer.OutCount          = 3;
      Transfer.InBuffer          = IBuff;
      Transfer.InCountExpected   = 6;
      Transfer.InCountReceived   = &Cnt;
      Transfer.InTimeout         = 0;
      Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;
      if(
         !DialupSendAndReceive(&Transfer, ReqRecord) ||
         (DUPRep->AckNak = DecodeAckNak(IBuff[2]))
        )
      {
         printf("Error 0x%02X Clearing Status Bits \n",IBuff[2]);
      }
   }

   return ret;
}




ULONG APlusCrypt(ULONG Key, ULONG PWord)
{
/*
   CRYPT.C
   5-11-94
   D. C. Olivier

This routine duplicates the encryption algorithm used in the A+.

*/

   unsigned long  pword;      /* Password */
   int              i;         /* loop index */
   int              j, k = 0; /* used to simulate rotate   */
   /*   through carry */

   union
   {
      unsigned long  key ;    /* encryption key */
      struct
      {           /* broken into bytes */
         unsigned char byta, bytb, bytc, bytd;
      } parts;
   } val;


/* get input values, 8 hex digits each */

   val.key = Key;       // strtoul(argv[1], &stopper, 0x10);
   pword = PWord;       // strtoul(argv[2], &stopper, 0x10);


/* Add an arbitrary number to the key just for fun. */

   val.key  += 0xab41;


/* Generate a four bit checksum to be used as loop index. */

   i = val.parts.byta + val.parts.bytb + val.parts.bytc + val.parts.bytd;
   i = i & 0x0f;

   while(i >= 0)
   {

/* set 'j' to the value of the high bit before shifting.
   Simulates carry flag. */

      if(val.parts.bytd >= 0x80) j = 1;
      else           j = 0;

/* Shift the key.  Add in the carry flag from the previous loop. */

      val.key = val.key << 1;
      val.key += k;

      k = j;


/* Apply the key to the password. */

      pword ^= val.key;

      i--;

   }
//   printf("\n %lX",pword);

   return pword;
}

INT   DoCommandNoData (UINT Function, REQUESTRECORD  *ReqRec)
{
   INT         nRet = 0;
   INT         BytesToSend    = 3;     // All NO_DATA commands have these values
   INT         BytesToGet     = 6;
   BOOL        bSuccess       = FALSE;
   BYTE        Buffer[20];
   BYTE        InBuffer[20];
   INT         Retries        = ReqRec->OMessage->Retry;
   ULONG       ReceiveLength  = 0;

   DIALUPREPLY  *DUPRep    = &ReqRec->IMessage->Buffer.DUPSt.DUPRep;
   CTIXMIT     Transfer;

   ClearBuffer(Buffer, sizeof(Buffer));         // Sanity Check...
   ClearBuffer(InBuffer, sizeof(InBuffer));    // Sanity Check...

   do
   {
      sprintf((CHAR*)Buffer,"%c%c%c", STX, AP_CMD_NO_DATA, Function); // Build Command string

      Transfer.OutBuffer         = Buffer;
      Transfer.OutCount          = BytesToSend;
      Transfer.InBuffer          = InBuffer;
      Transfer.InCountExpected   = BytesToGet;
      Transfer.InCountReceived   = &ReceiveLength;
      Transfer.InTimeout         = 0;
      Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

      nRet = DialupSendAndReceive(&Transfer, ReqRec);

      DUPRep->AckNak = DecodeAckNak(InBuffer[2]);
      if(nRet || DUPRep->AckNak)
      {
         printf("ERROR: Could not complete function 0x%02X\n",Function);
         if(nRet)
            printf("\tERROR: Error on Send/Receive = 0x%02X\n",nRet);
         return !NORMAL;
      }
      else
      {
         DUPRep->CompFlag |= DIALUP_COMP_SUCCESS;
         bSuccess = TRUE;
      }

      DUPRep->Status = PowerPlusStatus(InBuffer[3], FALSE, ReqRec);
      Retries--;

   } while (!bSuccess && Retries);


   return NORMAL;
}

/******************************************************************************
 *
 * Data is in ReqRec->OMessage->Buffer.OutMessage[] array.
 *
 * Format: STX 0x18 FUNC DATA ... DATA.
 *
 * Length is determined from the FUNC field & is not passed in from the calling
 *    program
 *
 *****************************************************************************/
INT   DoCommandWithData (UINT Function, REQUESTRECORD  *ReqRec)
{
   OUTMESS     *OutMessage    = ReqRec->OMessage;
   INMESS      *InMessage     = ReqRec->IMessage;
   DIALUPREPLY  *DUPRep          = &ReqRec->IMessage->Buffer.DUPSt.DUPRep;


   INT         i;
   INT         DataBytes      = APlusFunctions[GetAPFuncOffset(KEY_AP_FUNC, (void*) &Function)].DataBytes;
   INT         BytesToSend;         // Varies based upon DataBytes.
   INT         BytesToGet     = 6;  // All DATA commands get 6 bytes back.
   BOOL        bSuccess       = FALSE;
   BYTE        Buffer[40];
   BYTE        InBuffer[20];
   INT         Retries        = ReqRec->OMessage->Retry;
   ULONG       ReceiveLength  = 0;
   CTIXMIT     Transfer;

   ClearBuffer(Buffer, sizeof(Buffer));         // Sanity Check...
   ClearBuffer(InBuffer, sizeof(InBuffer));    // Sanity Check...

   do
   {
      Buffer[0] = STX;
      Buffer[1] = AP_CMD_WITH_DATA;
      Buffer[2] = Function;
      Buffer[3] = PAD;
      Buffer[4] = DataBytes;

      for( i = 5; i < 5 + DataBytes; i++)
      {
         Buffer[i] = OutMessage->Buffer.OutMessage[i - 2];
      }

      BytesToSend = 5 + DataBytes;

      Transfer.OutBuffer         = Buffer;
      Transfer.OutCount          = BytesToSend;
      Transfer.InBuffer          = InBuffer;
      Transfer.InCountExpected   = BytesToGet;
      Transfer.InCountReceived   = &ReceiveLength;
      Transfer.InTimeout         = 0;
      Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

      if(
          !DialupSendAndReceive(&Transfer, ReqRec) ||
          (DUPRep->AckNak = DecodeAckNak(InBuffer[2]))
        )
      {
         printf("ERROR: Could not complete function 0x%02X\n",Function);
         return !NORMAL;
      }
      else
      {
         bSuccess = TRUE;
         DUPRep->CompFlag |= DIALUP_COMP_SUCCESS;
      }

      DUPRep->Status = PowerPlusStatus(InBuffer[3], FALSE, ReqRec);

      Retries--;

   } while (!bSuccess && Retries);

   return NORMAL;
}

INT   DoClassRead(BYTE Class, USHORT Offset, USHORT Length, REQUESTRECORD  *ReqRec)
{
   OUTMESS     *OutMessage    = ReqRec->OMessage;
   INMESS      *InMessage     = ReqRec->IMessage;
   DIALUPREPLY  *DUPRep          = &ReqRec->IMessage->Buffer.DUPSt.DUPRep;

   INT         ret;
   INT         iClass = (INT) Class;

   // Class Offset is the position of the requested class in the APlusClasses array
   INT         ClassOffset = GetAPClassOffset(KEY_AP_CLASS, (void *)&iClass);
   // ClassLength is the byte length count of the requested class from the APlusClasses array
   INT         ClassLength = APlusClasses[ClassOffset].Length;
   INT         BytesToSend;
   INT         BytesToGet;

   INT         State[2]       = { ALPHA_INIT_STATE, ALPHA_INIT_STATE };

   INT         Retries        = ReqRec->OMessage->Retry;
   BOOL        bSuccess       = FALSE;    // Did we get it??
   BOOL        bCheckCRC      = FALSE;
   BYTE        Buffer[256];
   BYTE        InBuffer[512];         //   = ReqRec->IMessage->Buffer.InMessage;
   ULONG       ReceiveLength;
   CTIXMIT     Transfer;

   BYTEUSHORT  ReqLength;
   BYTEUSHORT  ReqOffset;

   ReqLength.sh = Length;
   ReqOffset.sh = Offset;

   if(Retries <= 1)
   {
      if(TraceFlag) printf("OutMessage had retries set to %d... Reset to 3\n", Retries);
      Retries = 3;
   }

   if( ClassOffset < 0 )
   {
      return DIALUP_ERR_INVALIDCLASS;      // That class isn't happenin'
   }

   do
   {
      switch(State[0])
      {
      case ALPHA_INIT_STATE:
         {
            //printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            bSuccess = FALSE;

            // Make sure we "fail" back to this point if we get here a second time...
            DUPRep->CompFlag &= (DIALUP_COMP_START |
                                 DIALUP_COMP_ID    |
                                 DIALUP_COMP_PWD   |
                                 DIALUP_COMP_CLASS0 );

            if(!(--Retries)) break;    // This would fail after the send anyway...

            ClearBuffer(InBuffer, sizeof(InBuffer));
            ClearBuffer(Buffer, sizeof(Buffer));     // Sanity Check...

            InIndex        = 0;
            ReceiveLength  = 0;
            BytesToSend    = 8;         // 8 bytes, CRC is added by the send

            Buffer[0]      = STX;
            Buffer[1]      = AP_CMD_CLASS_READ;
            Buffer[2]      = PAD;
            Buffer[3]      = ReqLength.ch[1];
            Buffer[4]      = ReqLength.ch[0];
            Buffer[5]      = ReqOffset.ch[1];
            Buffer[6]      = ReqOffset.ch[0];
            Buffer[7]      = Class;

            State[1]       = ALPHA_INIT_STATE;
            State[0]       = ALPHA_SENDCLASS_STATE;

            // This falls through to ALPHA_SENDCLASS_STATE!!! Do not move either of them...!!!
         }
      case ALPHA_SENDCLASS_STATE:
         {
            DUPRep->CompFlag |= DIALUP_COMP_SENDCLASS;
            //printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            /*
             * Should go through here only once per successful attempt
             */

            State[1] = State[0]; // ALL routes change the state, so doing this here is more efficient

            if(Length)                                   // Length is "const"
            {
               if( Length > 64)
               {
                  BytesToGet           = 6;              // GET 6 in case it is a NAK!
                  State[0] = ALPHA_RECUNKNOWN_STATE;
               }
               else
               {                                         // We can getem all
                  BytesToGet = Length + 7;               // 7 is header + CRC
                  bCheckCRC  = TRUE;

                  State[0] = ALPHA_RECKNOWN_STATE;
               }
            }
            else
            {
               if(ClassLength > 64)
               {
                  BytesToGet           = 6;
                  State[0] = ALPHA_RECUNKNOWN_STATE;
               }
               else
               {                                         // We can getem all
                  BytesToGet = ClassLength + 7;          // 7 is header + CRC
                  bCheckCRC  = TRUE;
                  State[0] = ALPHA_RECKNOWN_STATE;
               }
            }

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = BytesToSend;
            Transfer.Flags             = XFER_ADD_CRC;
            ret = DialupSend(&Transfer, ReqRec);   // Ask for the class

            if( ret )
            {
               printf("Error writing to Alpha: 0x%02X\n Retrys Left %d, Error = 0x%08X\n",Class, Retries, ret);
               CTISleep(500);
               State[0] = ALPHA_INIT_STATE;
            }
            break;
         }
      case ALPHA_RECKNOWN_STATE:
         {
            // printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            DUPRep->CompFlag |= DIALUP_COMP_RECKNOWN;
            /*
             *  I know how many bytes exactly to expect and CAN get them all with one read.
             */
            Transfer.InBuffer          = &InBuffer[InIndex];
            Transfer.InCountExpected   = BytesToGet;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = 0;
            if(bCheckCRC)
               Transfer.Flags          = XFER_VERIFY_CRC;

            ret = DialupReceive(&Transfer, ReqRec);
            DUPRep->AckNak = DecodeAckNak(InBuffer[InIndex + 2]);

            if( ret || DUPRep->AckNak)
            {
               if(ret)
               {
                  if(TraceFlag)
                  {
                     printf("%s(): Line #%d\n",__FILE__, __LINE__);
                     printf("Error reading class: %2d\nRetrys Left %d, Error 0x%08X: %s\n",Class, Retries, ret, CTI_DIALUP_ERR[ret]);
                  }

                  if(ret == DIALUP_ERR_NODATA)
                  {
                     printf("No Data from meter\n");
                     State[0] = State[1]; // Go back to whomever sent us here
                  }
               }
               if(DUPRep->AckNak)
               {
                  if(TraceFlag) printf("%s: We have been Alpha NAK'd at line #%d\n",__FILE__, __LINE__);
                  State[0] = State[1]; // Go back to whomever sent us here
               }
               CTISleep(500);

               break;
            }

            DUPRep->Status = InBuffer[InIndex + 3];
            /*
             * The data bytes are packed betwixt the header and CRC bytes.  The header is
             * located 0..4, the CRC is after all else and is 2 bytes long (total of 7)
             * we know we got ReceiveLength bytes, so we just copy over the data with a
             * memcpy into the InMessage buffer area.
             */
            memcpy(&(DUPRep->Message[InIndex]), &InBuffer[InIndex + 5], ReceiveLength - 7);
            InIndex += (ReceiveLength - 7);

            State[1] = State[0];
            State[0] = ALPHA_COMPLETE_STATE;
            break;
         }
      case ALPHA_RECUNKNOWN_STATE:
         {
            DUPRep->CompFlag |= DIALUP_COMP_RECUNKNOWN;
            // printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            /*
             * I don't know how many bytes to get, so I can only fetch the first X
             * and look at the 5th byte for a length count.
             * I then go get the remainder and set up for the next pass.
             */
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = BytesToGet;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = 0;

            ret = DialupReceive(&Transfer, ReqRec);

            if( ret )
            {
               if(ret)
               {
                  if(TraceFlag)
                  {
                     printf("%s(): Line #%d\n",__FILE__, __LINE__);
                     printf("Error reading class: %2d\nRetrys Left %d, Error 0x%08X: %s\n",Class, Retries, ret, CTI_DIALUP_ERR[ret]);
                  }
               }
               CTISleep(500);
               State[1] = ALPHA_INIT_STATE;
               State[0] = ALPHA_INIT_STATE;
               break;
            }

            DUPRep->AckNak = DecodeAckNak(InBuffer[2]);

            if(!(DUPRep->AckNak))
            {
               // Copy in our solo data byte here .
               DUPRep->Message[InIndex] = InBuffer[5];
               InIndex++;

               DUPRep->Status = InBuffer[3];
               // Got one last read.. get two crcs at end, minus the data byte we got last read and viola, add only one.
               BytesToGet = (InBuffer[4] & ~0x80) + 1;

               if(InBuffer[4] & 0x80)
               {
                  // This will be the last read the alpha has spoken...
                  State[0] = ALPHA_COMPLETE_STATE;
               }
               else
               {
                  State[1] = State[0];
                  State[0] = ALPHA_CONTINUERD_STATE;
               }

               Transfer.InBuffer          = &InBuffer[6];
               Transfer.InCountExpected   = BytesToGet;
               Transfer.InCountReceived   = &ReceiveLength;
               Transfer.InTimeout         = BytesToGet;
               Transfer.Flags             = 0;

               ret = DialupReceive(&Transfer, ReqRec);

               DUPRep->AckNak = DecodeAckNak(InBuffer[2]);

               if( ret || DUPRep->AckNak)
               {
                  if(ret)
                  {
                     if(TraceFlag)
                     {
                         printf("%s(): Line #%d\n",__FILE__, __LINE__);
                         printf("Error reading class: %2d\nRetrys Left %d, Error 0x%08X: %s\n",Class, Retries, ret, CTI_DIALUP_ERR[ret]);
                     }
                  }
                  if(DUPRep->AckNak)
                  {
                     if(TraceFlag) printf("%s: We have been Alpha NAK'd at line #%d\n",__FILE__, __LINE__);
                  }
                  CTISleep(500);
                  State[1] = State[0];
                  State[0] = ALPHA_INIT_STATE;
                  break;
               }

               /*
                * The data bytes are packed betwixt the header and CRC bytes.  The header is
                * located 0..4, the CRC is after all else and is 2 bytes long (total of 7)
                * we know we got ReceiveLength bytes, so we just copy over the data with a
                * memcpy into the InMessage buffer area.
                */
               memcpy(&(DUPRep->Message[InIndex]), &InBuffer[6], BytesToGet - 2);
               InIndex += (BytesToGet - 2);
            }
            else
            {
               State[0] = State[1]; // Go back to whomever sent us here and retry.....
            }

            break;
         }
      case ALPHA_CONTINUERD_STATE:
         {
            DUPRep->CompFlag |= DIALUP_COMP_CONTREAD;
            // printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);

            ClearBuffer(&Buffer[1], 20);     // Speed it up a bit.
            Buffer[1] = AP_CMD_CONTINUE_RD;

            BytesToSend = 2;     // Send adds the CRC computation and bits.
            BytesToGet  = 6;     // We just don't know anymore

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = BytesToSend;
            Transfer.Flags             = XFER_ADD_CRC;
            ret = DialupSend(&Transfer, ReqRec);   // Ask for the class

            if( ret )
            {
               if(TraceFlag)
               {
                  printf("%s(): Line #%d\n",__FILE__, __LINE__);
                  printf("Error reading class: %2d\nRetrys Left %d, Error 0x%08X: %s\n",Class, Retries, ret, CTI_DIALUP_ERR[ret]);
               }
               CTISleep(500);
               State[0] = ALPHA_INIT_STATE;
            }
            else {
               State[1] = State[0];
               State[0] = ALPHA_RECUNKNOWN_STATE;
            }
            break;
         }
      case ALPHA_COMPLETE_STATE:
         {
            if(Class == 72)
            {
               if(!(DUPRep->CompFlag & DIALUP_COMP_RCREAD1))
               {
                  DUPRep->CompFlag |= DIALUP_COMP_RCREAD1;
                  State[1] = State[0];
                  State[0] = ALPHA_INIT_RCLASSRD2;
                  break;
               }
               else
               {
                  DUPRep->CompFlag |= DIALUP_COMP_RCREAD2;
               }
            }

            DUPRep->CompFlag |= DIALUP_COMP_SUCCESS;
            // printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            InMessage->InLength = InIndex;
            bSuccess = TRUE;
            DoClassDecode(ReqRec);
            break;
         }
      case ALPHA_INIT_RCLASSRD2:
         {
            /*
             *  This state is used to get the second half of the rc data from offsets 3 & 4
             */
            BYTEUSHORT     TakeTwo;
            //printf("Entering State[0] = 0x%04X: Line (%d) \n",State[0], __LINE__);
            bSuccess = FALSE;

            ClearBuffer(InBuffer, sizeof(InBuffer));
            ClearBuffer(Buffer, sizeof(Buffer));     // Sanity Check...

            ReceiveLength  = 0;
            BytesToSend    = 8;         // 8 bytes, CRC is added by the send

            TakeTwo.sh     = ReqOffset.sh + 2;

            Buffer[0]      = STX;
            Buffer[1]      = AP_CMD_CLASS_READ;
            Buffer[2]      = PAD;
            Buffer[3]      = ReqLength.ch[1];
            Buffer[4]      = ReqLength.ch[0];
            Buffer[5]      = TakeTwo.ch[1];
            Buffer[6]      = TakeTwo.ch[0];
            Buffer[7]      = Class;

            State[1]       = State[0];
            State[0]       = ALPHA_SENDCLASS_STATE;
            break;
         }
      }
   } while (Retries > 0 && !bSuccess);

   if( bSuccess )
   {
      return NORMAL;
   }
   else
   {
      return !NORMAL;
   }
}

INT   DoReadAll(REQUESTRECORD *ReqRec)
{
   INT i;

   for(i = 1; i < 16; i++)
   {
      printf("Class %d\n",i);
      DoClassRead((BYTE) i, 0, 0, ReqRec);
   }

   return NORMAL;
}

INT GetAPFuncOffset(UINT Key, VOID *ptr)
{
   int   i;
   INT   Offset;
   INT   inputFunction = *(int *)ptr;

   switch(Key)
   {
      case KEY_AP_FUNC:
         {
            Offset = -1;
            for(i = 0; i < sizeof(APlusFunctions)/sizeof(CTI_ap_func) ; i++)
            {
               if(APlusFunctions[i].Function == (UINT)inputFunction) {
                  Offset = i;
                  break;
               }
            }
            break;
         }
      case KEY_AP_DESC:
         {
            Offset = -1;
            for(i = 0; i < sizeof(APlusFunctions)/sizeof(CTI_ap_func) ; i++)
            {
               if(!strcmp((char*)ptr, APlusFunctions[i].Description)) {
                  Offset = i;
                  break;
               }
            }
            break;
         }
      default:
         Offset = -1;
   }

   return Offset;
}

INT GetAPClassOffset(UINT Key, VOID *ptr)
{
   int   i;
   INT   Offset;
   INT   inputClass = *(int *)ptr;

   switch(Key)
   {
      case KEY_AP_CLASS:
         {
            Offset = -1;
            for(i = 0; i < sizeof(APlusClasses)/sizeof(CTI_ap_class) ; i++)
            {
               if(APlusClasses[i].Class == (UINT)inputClass) {
                  Offset = i;
                  break;
               }
            }
            break;
         }
      case KEY_AP_DESC:
         {
            Offset = -1;
            for(i = 0; i < sizeof(APlusClasses)/sizeof(CTI_ap_class) ; i++)
            {
               if(!strcmp((char*)ptr, APlusClasses[i].Description)) {
                  Offset = i;
                  break;
               }
            }
            break;
         }
      default:
         Offset = -1;

   }

//   printf("%s: Class Offset w/in struct is %d\n",__FILE__, Offset);

   return Offset;
}

BOOL  DoClassDecode(REQUESTRECORD *ReqRec)
{
   INT      i;

   OUTMESS     *OutMessage    = ReqRec->OMessage;
   INMESS      *InMessage     = ReqRec->IMessage;
   CtiPort     *PortRecord    = ReqRec->PRecord;
   CtiDeviceBase   *RemoteRecord  = ReqRec->RRecord;
   DEVICE      *DeviceRecord  = ReqRec->DRecord;
   DIALUPREPLY *DUPRep        = &ReqRec->IMessage->Buffer.DUPSt.DUPRep;

   /* We know we just did a class read, so look at the requested class */
   switch(DUPRep->ReqSt.Command[1])
   {
   case 0:
      {
         AlphaClass0Parts *ptr = (AlphaClass0Parts *)(DUPRep->Message);

         ptr->Real.UKH     = (FLOAT) BCDtoBase10(ptr->Byte.UKH     ,     3);
         ptr->Real.UPR     = (FLOAT) BCDtoBase10(&ptr->Byte.UPR    ,     1);
         ptr->Real.UKE     = (FLOAT) BCDtoBase10(ptr->Byte.UKE     ,     5);
         ptr->Real.INTNORM = (FLOAT) BCDtoBase10(&ptr->Byte.INTNORM,     1);
         ptr->Real.INTTEST = (FLOAT) BCDtoBase10(&ptr->Byte.INTTEST,     1);
         ptr->Real.DPLOCE  = (FLOAT) BCDtoBase10(&ptr->Byte.DPLOCE ,     1);
         ptr->Real.DPLOCD  = (FLOAT) BCDtoBase10(&ptr->Byte.DPLOCD ,     1);
         ptr->Real.NUMSBI  = (FLOAT) BCDtoBase10(&ptr->Byte.NUMSBI ,     1);
         ptr->Real.VTRATIO = (FLOAT) BCDtoBase10(ptr->Byte.VTRATIO ,     3);
         ptr->Real.CTRATIO = (FLOAT) BCDtoBase10(ptr->Byte.CTRATIO ,     3);
         ptr->Real.XFACTOR = (FLOAT) BCDtoBase10(ptr->Byte.XFACTOR ,     4);

         ptr->bDataIsReal = TRUE;

         DUPRep->CompFlag |= DIALUP_COMP_DECODED;
         break;
      }
   case 1:
   case 2:
   case 3:
   case 4:
   case 5:
   case 6:
   case 7:
   case 8:
   case 9:
   case 10:
   case 14:
   case 15:
   case 16:
      {
         CHAR  *MyInMessage = (CHAR*)(InMessage->Buffer.DUPSt.DUPRep.Message);

         // Just go ahead and churn out the bytes...

         fprintf(stdout,"Request Completion Status = 0x%04X\n",DUPRep->CompFlag);

         if(InMessage->InLength > 0)
         {
            fprintf(stdout,"Displaying Class %d data, %d bytes\n", DUPRep->ReqSt.Command[1], InMessage->InLength);
            fprintf(stdout,"     00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");
            fprintf(stdout,"     -----------------------------------------------");
            for(i = 0; (UINT)i < InMessage->InLength && InMessage->InLength < 400; i++)
            {
               if(!(i % 16)) fprintf(stdout,"\n%02X:  ",(i/16));
               fprintf(stdout,"%02X ",MyInMessage[i]);
            }
            fprintf(stdout,"\n");
         }
         break;
      }
   case 11:
   case 12:
   case 13:
      {
         BillingDataParts *ptr = (BillingDataParts *) (DUPRep->Message);

         ptr->Real.EAVGPF         = ((FLOAT) BCDtoBase10(ptr->Byte.EAVGPF,      2)) / 1000.0;
         ptr->Real.ETKWH2         = ((FLOAT) BCDtoBase10(ptr->Byte.ETKWH2,      7)) / (1000000.0 * EnergyDecimals);
         ptr->Real.ETKWH1         = ((FLOAT) BCDtoBase10(ptr->Byte.ETKWH1,      7)) / (1000000.0 * EnergyDecimals);
         ptr->Real.EKVARH1        = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH1,     7)) / (1000000.0 * EnergyDecimals);
         ptr->Real.EKVARH2        = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH2,     7)) / (1000000.0 * EnergyDecimals);
         ptr->Real.EKVARH3        = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH3,     7)) / (1000000.0 * EnergyDecimals);
         ptr->Real.EKVARH4        = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH4,     7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.DKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.DKWC2,       3)) / (DemandDecimals);
         ptr->Real.DKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.DKWCUM2,     3)) / (DemandDecimals);
         ptr->Real.DTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Year,   1);
         ptr->Real.DTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Month,  1);
         ptr->Real.DTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Day,    1);
         ptr->Real.DTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Hour,   1);
         ptr->Real.DTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Minute, 1);
         ptr->Real.DKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.DKW2,        3)) / (DemandDecimals);
         ptr->Real.DKWH2          = ((FLOAT) BCDtoBase10(ptr->Byte.DKWH2,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.CKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.CKWC2,       3)) / (DemandDecimals);
         ptr->Real.CKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.CKWCUM2,     3)) / (DemandDecimals);
         ptr->Real.CTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Year,   1);
         ptr->Real.CTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Month,  1);
         ptr->Real.CTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Day,    1);
         ptr->Real.CTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Hour,   1);
         ptr->Real.CTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Minute, 1);
         ptr->Real.CKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.CKW2,        3)) / (DemandDecimals);
         ptr->Real.CKWH2          = ((FLOAT) BCDtoBase10(ptr->Byte.CKWH2,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.BKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.BKWC2,       3)) / (DemandDecimals);
         ptr->Real.BKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.BKWCUM2,     3)) / (DemandDecimals);
         ptr->Real.BTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Year,   1);
         ptr->Real.BTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Month,  1);
         ptr->Real.BTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Day,    1);
         ptr->Real.BTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Hour,   1);
         ptr->Real.BTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Minute, 1);
         ptr->Real.BKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.BKW2,        3)) / (DemandDecimals);
         ptr->Real.BKWH2          = ((FLOAT) BCDtoBase10(ptr->Byte.BKWH2,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.AKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.AKWC2,       3)) / (DemandDecimals);
         ptr->Real.AKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.AKWCUM2,     3)) / (DemandDecimals);
         ptr->Real.ATD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Year,   1);
         ptr->Real.ATD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Month,  1);
         ptr->Real.ATD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Day,    1);
         ptr->Real.ATD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Hour,   1);
         ptr->Real.ATD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Minute, 1);
         ptr->Real.AKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.AKW2,        3)) / (DemandDecimals);
         ptr->Real.AKWH2          = ((FLOAT) BCDtoBase10(ptr->Byte.AKWH2,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.DKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.DKWC1,       3)) / (DemandDecimals);
         ptr->Real.DKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.DKWCUM1,     3)) / (DemandDecimals);
         ptr->Real.DTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Year,   1);
         ptr->Real.DTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Month,  1);
         ptr->Real.DTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Day,    1);
         ptr->Real.DTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Hour,   1);
         ptr->Real.DTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Minute, 1);
         ptr->Real.DKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.DKW1,        3)) / (DemandDecimals);
         ptr->Real.DKWH1          = ((FLOAT) BCDtoBase10(ptr->Byte.DKWH1,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.CKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.CKWC1,       3)) / (DemandDecimals);
         ptr->Real.CKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.CKWCUM1,     3)) / (DemandDecimals);
         ptr->Real.CTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Year,   1);
         ptr->Real.CTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Month,  1);
         ptr->Real.CTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Day,    1);
         ptr->Real.CTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Hour,   1);
         ptr->Real.CTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Minute, 1);
         ptr->Real.CKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.CKW1,        3)) / (DemandDecimals);
         ptr->Real.CKWH1          = ((FLOAT) BCDtoBase10(ptr->Byte.CKWH1,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.BKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.BKWC1,       3)) / (DemandDecimals);
         ptr->Real.BKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.BKWCUM1,     3)) / (DemandDecimals);
         ptr->Real.BTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Year,   1);
         ptr->Real.BTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Month,  1);
         ptr->Real.BTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Day,    1);
         ptr->Real.BTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Hour,   1);
         ptr->Real.BTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Minute, 1);
         ptr->Real.BKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.BKW1,        3)) / (DemandDecimals);
         ptr->Real.BKWH1          = ((FLOAT) BCDtoBase10(ptr->Byte.BKWH1,       7)) / (1000000.0 * EnergyDecimals);

         ptr->Real.AKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.AKWC1,       3)) / (DemandDecimals);
         ptr->Real.AKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.AKWCUM1,     3)) / (DemandDecimals);
         ptr->Real.ATD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Year,   1);
         ptr->Real.ATD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Month,  1);
         ptr->Real.ATD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Day,    1);
         ptr->Real.ATD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Hour,   1);
         ptr->Real.ATD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Minute, 1);
         ptr->Real.AKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.AKW1,        3)) / (DemandDecimals);
         ptr->Real.AKWH1          = ((FLOAT) BCDtoBase10(ptr->Byte.AKWH1,       7)) / (1000000.0 * EnergyDecimals);

         ptr->bDataIsReal = TRUE;

         DUPRep->CompFlag |= DIALUP_COMP_DECODED;

         break;
      }
   case 72:
      {
         AlphaClass72Parts *ptr;

         ptr = (AlphaClass72Parts *)(DUPRep->Message);

         if(DUPRep->CompFlag & DIALUP_COMP_RCREAD1)
         {
            ptr->Real.BLK1    = ((FLOAT) BCDtoBase10(ptr->Byte.BLK1,    3)) / DemandDecimals;
            ptr->Real.BLK2    = ((FLOAT) BCDtoBase10(ptr->Byte.BLK2,    3)) / DemandDecimals;
            ptr->Real.CUMOUT  = (INT)    BCDtoBase10(ptr->Byte.CUMOUT,  2);
            ptr->Real.SYSTAT  = (INT)    BCDtoBase10(ptr->Byte.SYSTAT,  1);

            ptr->Real.AKW1    = ((FLOAT) BCDtoBase10(ptr->Byte.AKW1,    3)) / DemandDecimals;
            ptr->Real.ATD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Year,   1);
            ptr->Real.ATD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Month,  1);
            ptr->Real.ATD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Day,    1);
            ptr->Real.ATD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Hour,   1);
            ptr->Real.ATD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.ATD1.Minute, 1);
            ptr->Real.AKWH1   = ((FLOAT) BCDtoBase10(ptr->Byte.AKWH1,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.BKW1    = ((FLOAT) BCDtoBase10(ptr->Byte.BKW1,    3)) / DemandDecimals;
            ptr->Real.BTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Year,   1);
            ptr->Real.BTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Month,  1);
            ptr->Real.BTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Day,    1);
            ptr->Real.BTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Hour,   1);
            ptr->Real.BTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.BTD1.Minute, 1);
            ptr->Real.BKWH1   = ((FLOAT) BCDtoBase10(ptr->Byte.BKWH1,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.CKW1    = ((FLOAT) BCDtoBase10(ptr->Byte.CKW1,    3)) / DemandDecimals;
            ptr->Real.CTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Year,   1);
            ptr->Real.CTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Month,  1);
            ptr->Real.CTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Day,    1);
            ptr->Real.CTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Hour,   1);
            ptr->Real.CTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.CTD1.Minute, 1);
            ptr->Real.CKWH1   = ((FLOAT) BCDtoBase10(ptr->Byte.CKWH1,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.DKW1    = ((FLOAT) BCDtoBase10(ptr->Byte.DKW1,    3)) / DemandDecimals;
            ptr->Real.DTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Year,   1);
            ptr->Real.DTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Month,  1);
            ptr->Real.DTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Day,    1);
            ptr->Real.DTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Hour,   1);
            ptr->Real.DTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.DTD1.Minute, 1);
            ptr->Real.DKWH1   = ((FLOAT) BCDtoBase10(ptr->Byte.DKWH1,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.ETKWH1  = ((FLOAT) BCDtoBase10(ptr->Byte.ETKWH1,  7)) / 1000000.0;
            ptr->Real.ETKWH2  = ((FLOAT) BCDtoBase10(ptr->Byte.ETKWH2,  6)) / 100000.0;

            ptr->Real.AKW2    = ((FLOAT) BCDtoBase10(ptr->Byte.AKW2,    3)) / DemandDecimals;
            ptr->Real.ATD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Year,   1);
            ptr->Real.ATD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Month,  1);
            ptr->Real.ATD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Day,    1);
            ptr->Real.ATD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Hour,   1);
            ptr->Real.ATD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.ATD2.Minute, 1);
            ptr->Real.AKWH2   = ((FLOAT) BCDtoBase10(ptr->Byte.AKWH2,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.BKW2    = ((FLOAT) BCDtoBase10(ptr->Byte.BKW2,    3)) / DemandDecimals;
            ptr->Real.BTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Year,   1);
            ptr->Real.BTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Month,  1);
            ptr->Real.BTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Day,    1);
            ptr->Real.BTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Hour,   1);
            ptr->Real.BTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.BTD2.Minute, 1);
            ptr->Real.BKWH2   = ((FLOAT) BCDtoBase10(ptr->Byte.BKWH2,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.CKW2    = ((FLOAT) BCDtoBase10(ptr->Byte.CKW2,    3)) / DemandDecimals;
            ptr->Real.CTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Year,   1);
            ptr->Real.CTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Month,  1);
            ptr->Real.CTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Day,    1);
            ptr->Real.CTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Hour,   1);
            ptr->Real.CTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.CTD2.Minute, 1);
            ptr->Real.CKWH2   = ((FLOAT) BCDtoBase10(ptr->Byte.CKWH2,   5)) / (100.0 * EnergyDecimals);

            ptr->Real.TD.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.TD.Year,   1);
            ptr->Real.TD.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.TD.Month,  1);
            ptr->Real.TD.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.TD.Day,    1);
            ptr->Real.TD.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.TD.Hour,   1);
            ptr->Real.TD.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.TD.Minute, 1);
            ptr->Real.SECONDS      = (UCHAR) BCDtoBase10(ptr->Byte.SECONDS,    1);

            ptr->Real.CUMDR        = (UINT) BCDtoBase10(ptr->Byte.CUMDR,       1);
            ptr->Real.SEARAT       = (UINT) BCDtoBase10(ptr->Byte.SEARAT,      1);

         }

         if(DUPRep->CompFlag & DIALUP_COMP_RCREAD2)
         {
            /*
             *  Offset 3 & 4 data follows...
             */

            if(!(DUPRep->CompFlag & DIALUP_COMP_RCREAD1))
            {
               ptr->Real.BLK1    = ((FLOAT) BCDtoBase10(ptr->Byte.BLK1_EX,    3)) / DemandDecimals;
               ptr->Real.BLK2    = ((FLOAT) BCDtoBase10(ptr->Byte.BLK2_EX,    3)) / DemandDecimals;
               ptr->Real.CUMOUT  = (INT)    BCDtoBase10(ptr->Byte.CUMOUT_EX,  2);
               ptr->Real.SYSTAT  = (INT)    BCDtoBase10(ptr->Byte.SYSTAT_EX,  1);
            }

            ptr->Real.AKWCUM1 = ((FLOAT) BCDtoBase10(ptr->Byte.AKWCUM1,    3)) / (DemandDecimals);
            ptr->Real.AKWC1   = ((FLOAT) BCDtoBase10(ptr->Byte.AKWC1  ,    3)) / (DemandDecimals);
            ptr->Real.BKWCUM1 = ((FLOAT) BCDtoBase10(ptr->Byte.BKWCUM1,    3)) / (DemandDecimals);
            ptr->Real.BKWC1   = ((FLOAT) BCDtoBase10(ptr->Byte.BKWC1  ,    3)) / (DemandDecimals);
            ptr->Real.CKWCUM1 = ((FLOAT) BCDtoBase10(ptr->Byte.CKWCUM1,    3)) / (DemandDecimals);
            ptr->Real.CKWC1   = ((FLOAT) BCDtoBase10(ptr->Byte.CKWC1  ,    3)) / (DemandDecimals);
            ptr->Real.DKWCUM1 = ((FLOAT) BCDtoBase10(ptr->Byte.DKWCUM1,    3)) / (DemandDecimals);
            ptr->Real.DKWC1   = ((FLOAT) BCDtoBase10(ptr->Byte.DKWC1  ,    3)) / (DemandDecimals);

            ptr->Real.AKWCUM2 = ((FLOAT) BCDtoBase10(ptr->Byte.AKWCUM2,    3)) / (DemandDecimals);
            ptr->Real.AKWC2   = ((FLOAT) BCDtoBase10(ptr->Byte.AKWC2  ,    3)) / (DemandDecimals);
            ptr->Real.BKWCUM2 = ((FLOAT) BCDtoBase10(ptr->Byte.BKWCUM2,    3)) / (DemandDecimals);
            ptr->Real.BKWC2   = ((FLOAT) BCDtoBase10(ptr->Byte.BKWC2  ,    3)) / (DemandDecimals);
            ptr->Real.CKWCUM2 = ((FLOAT) BCDtoBase10(ptr->Byte.CKWCUM2,    3)) / (DemandDecimals);
            ptr->Real.CKWC2   = ((FLOAT) BCDtoBase10(ptr->Byte.CKWC2  ,    3)) / (DemandDecimals);
            ptr->Real.DKWCUM2 = ((FLOAT) BCDtoBase10(ptr->Byte.DKWCUM2,    3)) / (DemandDecimals);
            ptr->Real.DKWC2   = ((FLOAT) BCDtoBase10(ptr->Byte.DKWC2  ,    3)) / (DemandDecimals);

            ptr->Real.EKVARH4 = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH4,    7)) / (1000000.0 * EnergyDecimals);
            ptr->Real.EKVARH3 = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH3,    6)) / (100000.0 * EnergyDecimals);
            ptr->Real.EKVARH2 = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH2,    7)) / (1000000.0 * EnergyDecimals);
            ptr->Real.EKVARH1 = ((FLOAT) BCDtoBase10(ptr->Byte.EKVARH1,    6)) / (100000.0 * EnergyDecimals);

            ptr->Real.DKW2    = ((FLOAT) BCDtoBase10(ptr->Byte.DKW2,    3)) / DemandDecimals;
            ptr->Real.DTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Year,   1);
            ptr->Real.DTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Month,  1);
            ptr->Real.DTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Day,    1);
            ptr->Real.DTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Hour,   1);
            ptr->Real.DTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.DTD2.Minute, 1);
            ptr->Real.DKWH2   = ((FLOAT) BCDtoBase10(ptr->Byte.DKWH2,   5)) / (100.0 * EnergyDecimals);
            ptr->Real.EAVGPF  = ((FLOAT) BCDtoBase10(ptr->Byte.EAVGPF,  2)) / 1000.0;
         }

         ptr->bDataIsReal = TRUE;

#if (DEBUG & DBG_PRINT_RECEIPTS)
         printf("BLK1      82,09,03    = %f\n",ptr->Real.BLK1);
         printf("BLK2      82,63,03    = %f\n",ptr->Real.BLK2);
         printf("CUMOUT    09,46,02    = %d\n",ptr->Real.CUMOUT);
         printf("SYSTAT    09,05,01    = 0x%02X\n",ptr->Real.SYSTAT);
#endif

         DUPRep->CompFlag |= DIALUP_COMP_DECODED;
         break;
      }
   default:
      break;
   }

   return TRUE;
}

INT DoGetClassZero(REQUESTRECORD *RecInRecord)
{
   ULONG             i;
   INT               ret = ERRUNKNOWN;
   BYTE              Command  = 0x05;    // Command Byte comming in from calling app.
   BYTE              Class    = 0x00;    // If a class read Function needs to be a class
   INMESS            InCompData;
   OUTMESS           OutCompData;        // gets the class 0 (computational data) data from the meter.

   // Implementation datails
   REQUESTRECORD     RRec;
   AlphaClass0Parts  *C0ptr;

   // set up the InCompData Structure
   InCompData.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = 0;   // Needed for the decode function
   memset(InCompData.Buffer.DUPSt.DUPRep.Message, 0x00, sizeof(InCompData.Buffer.DUPSt.DUPRep.Message));

   OutCompData.Retry  = 3;                            // set him up just a bit

   RRec.OMessage = &OutCompData;
   RRec.IMessage = &InCompData;
   RRec.PRecord  = RecInRecord->PRecord;
   RRec.RRecord  = RecInRecord->RRecord;
   RRec.DRecord  = RecInRecord->DRecord;

   // Copy the request into the InMessage side....
   // memcpy(&DUPRep->OutSt, &OutCompData.Buffer.DUPRep, sizeof(OutCompData.Buffer.DUPRep));

   if(!DoClassRead( 0, 0, 0, &RRec)) // Class, Offset, Length
   {
      C0ptr = (AlphaClass0Parts *)InCompData.Buffer.DUPSt.DUPRep.Message;

      if(C0ptr->bDataIsReal)
      {
         DemandDecimals = 1.0;
         EnergyDecimals = 1.0;

         for(i = 0; i < C0ptr->Real.DPLOCD; i++)
            DemandDecimals *=  10.0;

         for(i = 0; i < C0ptr->Real.DPLOCE; i++)
            EnergyDecimals *= 10.0;

         ret = NORMAL;
      }
   }
   memset(InCompData.Buffer.DUPSt.DUPRep.Message, 0x00, sizeof(InCompData.Buffer.DUPSt.DUPRep.Message));


   return ret;
}
