
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   DUPSCHL
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/DUPSCHL.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <ctype.h>
#include <math.h>
#include <time.h>

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
#include "dupreq.h"
#include "dupschl.h"     // Dialup Schlumberger Protocol.

#include "dev_base.h"

UCHAR    SchlDecodeAckNak(UCHAR AckNak);
VOID     SchlDoDataDisplay(FulcrumScanData_t *Fsd);
BOOL     SchlDoClassDecode( REQUESTRECORD *ReqRec );
INT      SchlDoIdentifyCommand(REQUESTRECORD *ReqRec, SchlMeterStruct *MeterSt);
INT      SchlDoUploadData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec);
INT      SchlDoDownloadData(UINT Function, REQUESTRECORD *ReqRec);
INT      SchlDoScannerData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec);
INT      SchlDoLoadProfileData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec);
VOID     SchlDoDataShuffle(FulcrumScanData_t* Fsd);
ULONG    BytesToBase10(UCHAR* buffer, ULONG len);
ULONG    SchlPreviousMassMemoryAddress( ULONG SA,     // Starting Address
                                        ULONG CA,     // Current Address
                                        ULONG MA,     // Maximum Address
                                        ULONG RS);    // Record Size

static FLOAT    DemandDecimals = 1.0;
static FLOAT    EnergyDecimals = 1.0;
static INT      InIndex;

INT             SclumbergerRetries = 7;

/*
 * Command Char, Command Name, Sent bytes (less CRC), Rec. Bytes (less CRC)
 */
CTI_FULCRUM_FUNC SchlumFunc[] = {
   { 'I', "Identify Command"           , 12    , 18},
   { 'S', "Security Code Command"      , 9     , 1},
   { 'D', "Download Command"           , -1    , 2},
   { 'U', "Upload Command"             , 8     , -1},
};


FulcrumBase    SchlFulcrumBasePage[] = {
   {  0x2100,     1,    "Meter switch status"               ,  SCHL_DTYPE_INT},
   {  0x2101,     1,    "Fatal error"                       ,  SCHL_DTYPE_INT},
   {  0x2102,     1,    "Non-fatal error"                   ,  SCHL_DTYPE_INT},
   {  0x2103,     1,    "Diagnostic error"                  ,  SCHL_DTYPE_INT},
   {  0x2104,     1,    "RAM status"                        ,  SCHL_DTYPE_INT},
   {  0x2105,     1,    "KYZ status"                        ,  SCHL_DTYPE_INT},
   {  0x2106,     7,    "Time remaining in interval"        ,  SCHL_DTYPE_INT},
   {  0x210D,     1,    "Active phase status"               ,  SCHL_DTYPE_INT},
   {  0x210E,     1,    "Commuinication timeout"            ,  SCHL_DTYPE_INT},
   {  0x210F,     1,    "Remote alternate display Bag"      ,  SCHL_DTYPE_INT},
   {  0x2110,     1,    "Remote demand reset flag"          ,  SCHL_DTYPE_INT},
   {  0x2111,     1,    "Hang up flag"                      ,  SCHL_DTYPE_INT},
   {  0x2112,     1,    "Unused"                            ,  SCHL_DTYPE_INT},
   {  0x2113,     1,    "Stop metering flag"                ,  SCHL_DTYPE_INT},
   {  0x2114,     1,    "Clear billing data flag"           ,  SCHL_DTYPE_INT},
   {  0x2115,     1,    "Copy data flag"                    ,  SCHL_DTYPE_INT},
   {  0x2116,     1,    "Clock option reconfigure flag"     ,  SCHL_DTYPE_INT},
   {  0x2117,     1,    "Meter reconfigure flag"            ,  SCHL_DTYPE_INT},
   {  0x2118,     1,    "Clock option run flag"             ,  SCHL_DTYPE_INT},
   {  0x2119,     1,    "Mass memory run Bag"               ,  SCHL_DTYPE_INT},
   {  0x211A,     1,    "Time of use run flag"              ,  SCHL_DTYPE_INT},
   {  0x2170,     9,    "User defined field"                ,  SCHL_DTYPE_INT},

   {  0x211B,     1, "Model type"                           ,  SCHL_DTYPE_INT},
   {  0x211C,     2, "Program count"                        ,  SCHL_DTYPE_INT},
   {  0x211E,     2, "Demand Reset Count"                   ,  SCHL_DTYPE_INT},
   {  0x2120,     2, "Test count"                           ,  SCHL_DTYPE_INT},
   {  0x2122,     2, "Interrogate count"                    ,  SCHL_DTYPE_INT},
   {  0x2124,     2, "Outage count"                         ,  SCHL_DTYPE_INT},
   {  0x2126,     2, "Phase outage count"                   ,  SCHL_DTYPE_INT},
   {  0x2128,     2, "Minutes without load"                 ,  SCHL_DTYPE_INT},
   {  0x212A,     1, "Days without load"                    ,  SCHL_DTYPE_INT},
   {  0x212B,     1, "Allowable non-fatal error"            ,  SCHL_DTYPE_INT},
   {  0x212C,     1, "Allowable diagnostic error"           ,  SCHL_DTYPE_INT},
   {  0x212D,     1, "Calibration enabled error"            ,  SCHL_DTYPE_INT},
   {  0x212E,     1, "Days since last demand reset"         ,  SCHL_DTYPE_INT},
   {  0x212F,     1, "Days since last test"                 ,  SCHL_DTYPE_INT},
   {  0x2130,     1, "Test mode flag"                       ,  SCHL_DTYPE_INT},
   {  0x2131,     2, "Unused"                               ,  SCHL_DTYPE_INT},
   {  0x2133,     4, "Encoder configuration"                ,  SCHL_DTYPE_INT},
   {  0x2137,     1, "TOU reconfigure flag"                 ,  SCHL_DTYPE_INT},
   {  0x2138,     1, "Factory use"                          ,  SCHL_DTYPE_INT},
   {  0x2139,     3, "Future use"                           ,  SCHL_DTYPE_INT},
   {  0x213C,     9, "Meter ID"                             ,  SCHL_DTYPE_INT},
   {  0x2145,     6, "Software revision level"              ,  SCHL_DTYPE_INT},
   {  0x214B,     6, "Firmware revision level"              ,  SCHL_DTYPE_INT},
   {  0x2151,     2, "Program ID"                           ,  SCHL_DTYPE_INT},
   {  0x2153,     3, "Unit type"                            ,  SCHL_DTYPE_INT},
   {  0x2156,     8, "Unit ID"                              ,  SCHL_DTYPE_INT},
   {  0x215E,     9, "User defined field 1"                 ,  SCHL_DTYPE_INT},

   {  0x2167,     9, "User defined field 2"                 ,  SCHL_DTYPE_INT},
   {  0x2170,     9, "User defined field 3"                 ,  SCHL_DTYPE_INT},
   {  0x2179,     8, "Primary security password"            ,  SCHL_DTYPE_INT},
   {  0x2181,     8, "Secondary security password"          ,  SCHL_DTYPE_INT},
   {  0x2189,     8, "Tertiary security password"           ,  SCHL_DTYPE_INT},
   {  0x2191,     1, "Test mode options"                    ,  SCHL_DTYPE_INT},
   {  0x2191,     1, "Display setup"                        ,  SCHL_DTYPE_INT},
   {  0x2193,    34, "Allowed annunciators"                 ,  SCHL_DTYPE_INT},
   {  0x21B5,     1, "Display ON time"                      ,  SCHL_DTYPE_INT},
   {  0x21B6,     1, "Display OFF time"                     ,  SCHL_DTYPE_INT},
   {  0x21B7,     4, "Displayed register multiplier"        ,  SCHL_DTYPE_INT},
   {  0x21BB,     4, "Displayed normal Kh (load rate)"      ,  SCHL_DTYPE_INT},
   {  0x21BF,     4, "Displayed test Kh'(Ioad rate)"        ,  SCHL_DTYPE_INT},
   {  0x21C3,     4, "Normal Kh for LCD watt disk"          ,  SCHL_DTYPE_INT},
   {  0x21C7,     4, "Test Kh for LCD watt disk"            ,  SCHL_DTYPE_INT},
   {  0x21CB,   429, "Normal mode display table"            ,  SCHL_DTYPE_INT},
   {  0x2378,   429, "Alternate mode display table"         ,  SCHL_DTYPE_INT},
   {  0x2525,   121, "Test mode display table"              ,  SCHL_DTYPE_INT},
   {  0x259E,    12, "Coincident demand setup table"        ,  SCHL_DTYPE_INT},
   {  0x25AA,     4, "Demand register fullscale value"      ,  SCHL_DTYPE_INT},
   {  0x25AE,     4, "Energy register rollover value"       ,  SCHL_DTYPE_INT},
   {  0x25B2,     4, "Primary demand threshold"             ,  SCHL_DTYPE_INT},
   {  0x25B6,     4, "Secondary demand threshold"           ,  SCHL_DTYPE_INT},
   {  0x25BA,     4, "Pimary pf threshold minimum"          ,  SCHL_DTYPE_INT},
   {  0x25BE,     4, "Secondary pf threshold min"           ,  SCHL_DTYPE_INT},
   {  0x25C2,     9, "KYZ input table"                      ,  SCHL_DTYPE_INT},
   {  0x25CB,    30, "KYZ output table"                     ,  SCHL_DTYPE_INT},

   {  0x25E9,     1, "Self-read option configuration"       ,  SCHL_DTYPE_INT},
   {  0x25EA,     2, "Minutes from DR to self read"         ,  SCHL_DTYPE_INT},
   {  0x25EC,   505, "Self-read register address table"     ,  SCHL_DTYPE_INT},
   {  0x27E5,     1, "Meter operating set up"               ,  SCHL_DTYPE_INT},
   {  0x27E6,     1, "Installed hardware options"           ,  SCHL_DTYPE_INT},
   {  0x27E7,     1, "Firmware options"                     ,  SCHL_DTYPE_INT},
   {  0x27E8,     1, "I/O board type"                       ,  SCHL_DTYPE_INT},
   {  0x27E9,    15, "I/O configuration"                    ,  SCHL_DTYPE_INT},
   {  0x27F8,     1, "Demand reset lockout time"            ,  SCHL_DTYPE_INT},
   {  0x27F9,     2, "Cold load pick up time"               ,  SCHL_DTYPE_INT},
   {  0x27FB,     2, "Cold load pick up outage"             ,  SCHL_DTYPE_INT},
   {  0x27FD,     1, "Normal number of subinterval"         ,  SCHL_DTYPE_INT},
   {  0x27FE,     1, "Normal demand subintervals"           ,  SCHL_DTYPE_INT},
   {  0x27FF,     1, "Test number of subintervals"          ,  SCHL_DTYPE_INT},
   {  0x2800,     1, "Test demand subinterval length"       ,  SCHL_DTYPE_INT},
   {  0x2801,     4, "Normal Kt for Watthour LED"           ,  SCHL_DTYPE_INT},
   {  0x2805,     4, "Normal Kt for VARhour LED"            ,  SCHL_DTYPE_INT},
   {  0x2809,     4, "Test Kt for Watthour LED"             ,  SCHL_DTYPE_INT},
   {  0x280D,     4, "Test Kt for VARhour LED"              ,  SCHL_DTYPE_INT},
   {  0x2811,     1, "Meter type"                           ,  SCHL_DTYPE_INT},
   {  0x2812,     1, "Enrrgy configuration"                 ,  SCHL_DTYPE_INT},
   {  0x2813,     1, "Five highest peak rate"               ,  SCHL_DTYPE_INT},
   {  0x2814,     4, "Register multiplier"                  ,  SCHL_DTYPE_INT},
   {  0x2818,     1, "Unused"                               ,  SCHL_DTYPE_INT},
   {  0x2819,   196, "Energy register"                      ,  SCHL_DTYPE_INT},
   {  0x28DD,   426, "Demand registers"                     ,  SCHL_DTYPE_INT},
   {  0x2A87,    69, "Power factor"                         ,  SCHL_DTYPE_INT},
   {  0x2ACC,    32, "Instantaneous volts and amps"         ,  SCHL_DTYPE_INT},

   {  0x2AEC,  2000, "Time of Use Schedule"                 ,  SCHL_DTYPE_INT},
   {  0x32BC,     7, "Real time"                            ,  SCHL_DTYPE_INT},
   {  0x32C3,     3, "To DST time"                          ,  SCHL_DTYPE_INT},
   {  0x32C6,     3, "From DST time"                        ,  SCHL_DTYPE_INT},
   {  0x32C9,   128, "Mass memory program table"            ,  SCHL_DTYPE_INT},
   {  0x3349,     2, "MM record length"                     ,  SCHL_DTYPE_INT},
   {  0x334B,     3, "Logical MM start address"             ,  SCHL_DTYPE_INT},
   {  0x334E,     3, "Logical MM end address"               ,  SCHL_DTYPE_INT},
   {  0x3351,     3, "Actual MM end address"                ,  SCHL_DTYPE_INT},
   {  0x3354,     3, "Current logical MM address"           ,  SCHL_DTYPE_INT},
   {  0x3357,     3, "Reserved for future use"              ,  SCHL_DTYPE_INT},
   {  0x335A,     2, "Current MM record number"             ,  SCHL_DTYPE_INT},
   {  0x335C,     1, "Current MM interval number"           ,  SCHL_DTYPE_INT},
   {  0x335D,     1, "Number of MM channels"                ,  SCHL_DTYPE_INT},
   {  0x335F,     1, "MM recording interval length"         ,  SCHL_DTYPE_INT},
   {  0x3360,     1, "Mininimum outage length"              ,  SCHL_DTYPE_INT},
   {  0x3361,     2, "Pointer to TOU base area"             ,  SCHL_DTYPE_INT},
   {  0x3363,     1, "TOU schedule ID"                      ,  SCHL_DTYPE_INT},
   {  0x3364,     5, "TOU schedule expiration date"         ,  SCHL_DTYPE_INT},
   {  0x3369,     1, "Current TOU season"                   ,  SCHL_DTYPE_INT},
   {  0x336A,     1, "Current TOU day type"                 ,  SCHL_DTYPE_INT},
   {  0x336B,     1, "Current TOU rate"                     ,  SCHL_DTYPE_INT},
   {  0x336C,     1, "Current TOU independent outputs"      ,  SCHL_DTYPE_INT},
   {  0x336D,     2, "Address of next TOU switch point"     ,  SCHL_DTYPE_INT},
   {  0x336F,     2, "Address of next calendar event"       ,  SCHL_DTYPE_INT},
   {  0x3371,     2, "Pointer to last season area"          ,  SCHL_DTYPE_INT},

   {  0x3373,     1, "Delay season change flag"             ,  SCHL_DTYPE_INT},
   {  0x3374,    65, "Date and time of self-read"           ,  SCHL_DTYPE_INT},
   {  0x33B5,     1, "Self-read index number"               ,  SCHL_DTYPE_INT},
   {  0x33B6,   175, "Daylight savings time table"          ,  SCHL_DTYPE_INT},
   {  0x3465,    30, "Unused"                               ,  SCHL_DTYPE_INT},
   {  0x3483,     5, "Time and date of last program"        ,  SCHL_DTYPE_INT},
   {  0x3488,     5, "Time and date of last demand"         ,  SCHL_DTYPE_INT},
   {  0x348D,     5, "Time and date of last test"           ,  SCHL_DTYPE_INT},
   {  0x3492,     5, "Time and date of last interrogation"  ,  SCHL_DTYPE_INT},
   {  0x3497,     7, "Time and date of last outage"         ,  SCHL_DTYPE_INT},
   {  0x349E,     5, "Time and date of calibration"         ,  SCHL_DTYPE_INT},
   {  0x34A3,     4, "Minnutes allowed on battery"          ,  SCHL_DTYPE_INT},
   {  0x34A7,     4, "Number of minutes on battery"         ,  SCHL_DTYPE_INT},
   {  0x34AB,     2, "Number of days on battery"            ,  SCHL_DTYPE_INT},
   {  0x34AD,  1656, "Self read areas 1-4"                  ,  SCHL_DTYPE_INT},
   {  0x3B25,  3312, "Code Space (Protected)"               ,  SCHL_DTYPE_INT},
   {  0x4815,   414, "Fixed self-read area"                 ,  SCHL_DTYPE_INT},
   {  0x49B3,     4, "Average instantaneous voltage A"      ,  SCHL_DTYPE_INT},
   {  0x49B7,     4, "Average instantaneous voltage B"      ,  SCHL_DTYPE_INT},
   {  0x49BB,     4, "Average instantaneous voltage C"      ,  SCHL_DTYPE_INT},
   {  0x49BF,     4, "Average instantaneous current A"      ,  SCHL_DTYPE_INT},
   {  0x49C3,     4, "Average instantaneous current B"      ,  SCHL_DTYPE_INT},
   {  0x49B7,     4, "Average instantaneous current C"      ,  SCHL_DTYPE_INT},
   {  0x49CB,   140, "Total energy registers"               ,  SCHL_DTYPE_INT},
   {  0x4A57,   140, "TOU intermediate registers"           ,  SCHL_DTYPE_INT},
   {  0x4AE3,     4, "KW previous TOU"                      ,  SCHL_DTYPE_INT},
   {  0x4AE7,     4, "KVAR previous TOU"                    ,  SCHL_DTYPE_INT},

   {  0x4AEB,     4, "KVA previous TOU"                     ,  SCHL_DTYPE_INT},
   {  0x4AEF,     4, "PF previous TOU"                      ,  SCHL_DTYPE_INT},
   {  0x4AF3,    32, "Calling windows"                      ,  SCHL_DTYPE_INT},
   {  0x4B13,     1, "Outgoing baud rate"                   ,  SCHL_DTYPE_INT},
   {  0x4B14,     1, "Answer out window"                    ,  SCHL_DTYPE_INT},
   {  0x4B15,     1, "Answer in window"                     ,  SCHL_DTYPE_INT},
   {  0x4B16,     1, "Wait for answer"                      ,  SCHL_DTYPE_INT},
   {  0x4B17,    36, "Phone number"                         ,  SCHL_DTYPE_INT},
   {  0x4B3B,     1, "Enable off hook"                      ,  SCHL_DTYPE_INT},
   {  0x4B3C,     1, "Pulse dial mark"                      ,  SCHL_DTYPE_INT},
   {  0x4B3D,     1, "Call out window"                      ,  SCHL_DTYPE_INT},
   {  0x4B3E,     1, "Blind dialing"                        ,  SCHL_DTYPE_INT},
   {  0x4B3F,     1, "Blind dialing wait time"              ,  SCHL_DTYPE_INT},
   {  0x4B40,     2, "Event configuration"                  ,  SCHL_DTYPE_INT},
   {  0x4B42,     1, "MM channel number"                    ,  SCHL_DTYPE_INT},
   {  0x4B43,     1, "MM interval count"                    ,  SCHL_DTYPE_INT},
   {  0x4B44,    40, "Timing parameters"                    ,  SCHL_DTYPE_INT},
   {  0x4B6C,     2, "Events"                               ,  SCHL_DTYPE_INT},
   {  0x4B6E,     1, "Run flag"                             ,  SCHL_DTYPE_INT},
   {  0x4B6F,     4, "Minutes until call"                   ,  SCHL_DTYPE_INT},
   {  0x4B73,     1, "S17"                                  ,  SCHL_DTYPE_INT},
   {  0x4B74,     1, "S19"                                  ,  SCHL_DTYPE_INT},
   {  0x4B75,     1, "Modem switches"                       ,  SCHL_DTYPE_INT},
   {  0x4B76,     1, "Modem info"                           ,  SCHL_DTYPE_INT},
   {  0x4B77,     1, "Firmware info"                        ,  SCHL_DTYPE_INT},
   {  0x4B78,     1, "Ack error flag"                       ,  SCHL_DTYPE_INT},
   {  0x4B79,     1, "Outage length"                        ,  SCHL_DTYPE_INT},

   {  0x4B7A,     5, "Time of outage"                       ,  SCHL_DTYPE_INT},
   {  0x4B7F,    58, "Thermal demand registers"             ,  SCHL_DTYPE_INT},
   {  0x4BB9,     1, "End of basepage"                      ,  SCHL_DTYPE_INT},

   {  0x0000,     0, NULL                                   ,  SCHL_DTYPE_INT}
};

ULONG BytesToBase10(UCHAR* buffer, ULONG len)
{
   /*
    *  len is the number of bytes to decode from.
    */

   int i, j;
   ULONG temp;
   ULONG scratch = 0;

   for(i = len, j = 0; i > 0; j++, i--)
   {
      temp = 0;

      /* The high nibble */
      temp += (((buffer[j] & 0xf0) >> 4)  * 16);

      /* The Low nibble */
      temp += (buffer[j] & 0x0f);

      // printf("%d, %ld\n",buffer[i], scratch);
      scratch = scratch * 256 + temp ;
   }
   // printf("\n%d = 0x%08X\n",scratch,scratch);

   return scratch;
}

INT     SchlHandShake    (OUTMESS   *OutMessage,
                          INMESS    *InMessage,
                          CtiPort   *PortRecord,
                          CtiDeviceBase *RemoteRecord)
{
   CHAR     ConfigName[8];
   DEVICE   deviceRecord;
   INT               retval;
   INT               Retries           = OutMessage->Retry;
   BOOL              bSuccess          = FALSE;
   BYTE              Buffer[256];
   BYTE              InBuffer[512];
   ULONG             State             = SCHL_STATE_START;
   ULONG             Attempts          = 3;
   ULONG             FMask;
   ULONG             ReceiveLength;
   ULONG             CurrentFunction;

   DIALUPREPLY       *DUPRep     = &InMessage->Buffer.DUPSt.DUPRep;

   // Implementation details
   SchlMeterStruct   MeterSt;
   REQUESTRECORD     ReqRecord;
   CTIXMIT           Transfer;
   BYTE              DataBuffer[sizeof(FulcrumScanData_t)];

   BYTE  Command     = (BYTE) OutMessage->Buffer.DUPReq.Command[0];

   ULONG Function    = OutMessage->Buffer.DUPReq.Request[3];

   DUPRep->Status    = 0;  // set to a Default start point
   DUPRep->AckNak    = 0;  // set to a Default start point
   DUPRep->CompFlag  = 0;  // set to a Default start point

   //printf("\nFunction = 0x%08X\n", Function);

   InIndex = 0;   // set so our data is in order starting at zero...


   /* This is a little sanity check which could be removed later */
   if(sizeof(FulcrumScanData_t) > sizeof(InMessage->Buffer.DUPSt.DUPRep.Message) + InIndex)
   {
      printf("\n\nInMessage Buffer is smaller than data requested.  This should not happen\n\n");
   }

   if(TraceFlag)
      printf("Schlumberger Request Received\n");

   // debug DLS
   ReqRecord.DRecord  = (DEVICE *) malloc (sizeof (DEVICE));

   ReqRecord.OMessage = OutMessage;
   ReqRecord.IMessage = InMessage;
   ReqRecord.PRecord  = PortRecord;
   ReqRecord.RRecord  = RemoteRecord;

   if(!OutMessage || !InMessage || !PortRecord || !RemoteRecord)
   {
      printf("A required parameter is missing \n");
      return !NORMAL;
   }


   // Copy the request into the InMessage side....
   memcpy(&DUPRep->ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

   do
   {
      switch(State)
      {
      case SCHL_STATE_START:
         {
            retval = NORMAL;

            /*
             * get the boys talking.
             *
             * We are able to send the UTS "I" command at any time before handshake
             * to ensure that an Vectron is out there....  It will respond with
             * an ASCII "SI,VECTRON    <CR>" string....
             */

            DUPRep->CompFlag = DIALUP_COMP_START;

#ifndef UTS_ID
   #define UTS_ID 1
#endif

#ifdef UTS_ID

            // Make sure nothing is in there
            ClearBuffer(InBuffer, sizeof(InBuffer));

            Buffer[0] = 'I';

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 1;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 16;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.InTimeout         = 0;
            Transfer.Flags             = 0;
            Transfer.InTimeout         = 0;

            DialupSendAndReceive(&Transfer, &ReqRecord);
            //DUPRep->AckNak = SchlDecodeAckNak(InBuffer[0]);

            if(InBuffer[0] == NAK)
            {
               Attempts = SclumbergerRetries;
               State = SCHL_STATE_IDENTIFY;
               if(TraceFlag) printf("NAK: Fulcrum Already ONLINE\n");
               break;
            }
            else if(InBuffer[0] == ACK)
            {
               Attempts = SclumbergerRetries;
               State = SCHL_STATE_IDENTIFY;
               if(TraceFlag) printf("ACK: Fulcrum Already ONLINE\n");
               break;
            }
            else if(strncmp((CHAR*)InBuffer, "SI,FULCRUM     \r", 10)) // We go in if they are not identical
            {
               if(TraceFlag) printf("Failed to get Identity from the meter\n");
               if(TraceFlag) printf("Data Received: %.15s\n",InBuffer);
               Attempts--;

               // if(!Attempts) State = SCHL_STATE_ERRABORT;
               if(!Attempts)
               {
                  Attempts= 6;
                  State = SCHL_STATE_ATTN;
               }
               break;
            }
            else
            {
               DUPRep->CompFlag |= DIALUP_COMP_ID;     // This effectively zero's out the other possibilities

               Attempts = 6; // Per Schlumberger Spec.
               State = SCHL_STATE_ATTN;
               printf("Fulcrum Detected: <%.15s>\n",InBuffer);
            }
#else
            DUPRep->CompFlag |= DIALUP_COMP_ID;     // This effectively zero's out the other possibilities

            Attempts = 6; // Per Schlumberger Spec.
            State = SCHL_STATE_ATTN;
#endif

            break;
         }
      case SCHL_STATE_ATTN:
         {
            retval = NORMAL;
            /*
             *  We need to get the meter's attention.....
             */
            Buffer[0] = ENQ;
            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 1;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 1;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.Flags             = 0;
            Transfer.InTimeout         = 0;
            DialupSendAndReceive(&Transfer, &ReqRecord);
            DUPRep->AckNak = SchlDecodeAckNak(InBuffer[0]);

            if(InBuffer[0] == ACK)
            {
               Attempts = SclumbergerRetries;
               State = SCHL_STATE_IDENTIFY;
            }
            else if(Attempts-- > 0)
            {
               State = SCHL_STATE_ATTN;
            }
            else
            {
               State = SCHL_STATE_ERRABORT;  // Couldn't get the job done... bail out here
            }

            break;
         }
      case SCHL_STATE_IDENTIFY:
         {
            retval = NORMAL;

            if(!SchlDoIdentifyCommand(&ReqRecord, &MeterSt))
            {
               Attempts = SclumbergerRetries;
               State = SCHL_STATE_SECURITY;

            }
            else if(Attempts-- > 0)
            {
               State = SCHL_STATE_IDENTIFY;
            }
            else
            {
               State = SCHL_STATE_ERRABORT;  // Couldn't get the job done... bail out here
               retval = !NORMAL;
            }
            break;
         }
      case SCHL_STATE_SECURITY:
         {
            retval = NORMAL;

            /*
             * OK, now we need to send the security code to habla con este
             * muhere gorda
             */

            ClearBuffer(Buffer, sizeof(Buffer));     // Sanity Check...
            ClearBuffer(InBuffer, sizeof(InBuffer));
            ReceiveLength = 0;

            Buffer[0] = 'S';
             // The security Code lives here.... but it is ASCII, so we subtract 0x30 from it...
            Buffer[1] = 0x00;
            Buffer[2] = 0x00;
            Buffer[3] = 0x00;
            Buffer[4] = 0x00;
            Buffer[5] = 0x00;
            Buffer[6] = 0x00;
            Buffer[7] = 0x00;
            Buffer[8] = 0x00;

/*            Buffer[1] = RemoteRecord->getPassword()(0) - 0x30;
            Buffer[2] = RemoteRecord->getPassword()(1) - 0x30;
            Buffer[3] = RemoteRecord->getPassword()(2) - 0x30;
            Buffer[4] = RemoteRecord->getPassword()(3) - 0x30;
            Buffer[5] = RemoteRecord->getPassword()(4) - 0x30;
            Buffer[6] = RemoteRecord->getPassword()(5) - 0x30;
            Buffer[7] = RemoteRecord->getPassword()(6) - 0x30;
            Buffer[8] = RemoteRecord->getPassword()(7) - 0x30;

*/            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 9;
            Transfer.InBuffer          = InBuffer;
            Transfer.InCountExpected   = 1;
            Transfer.InCountReceived   = &ReceiveLength;
            Transfer.Flags             = XFER_ADD_CRC;
            Transfer.InTimeout         = 0;

            retval = DialupSendAndReceive(&Transfer, &ReqRecord);
            DUPRep->AckNak = SchlDecodeAckNak(InBuffer[0]);

            if( retval )
            {
               ReceiveLength = 0;
               if(TraceFlag && retval == DIALUP_ERR_BADCRC)
               {
                  printf("Data failed CRC verification. %d attempts remain\n", Attempts);
               }
               else if(TraceFlag && retval == DIALUP_ERR_NODATA)
               {
                  printf("No Reply from the meter. %d attempts remain\n", Attempts);
               }
               CTISleep(2000);
            }

            if(DUPRep->AckNak == ACK)
            {
               DUPRep->CompFlag |= DIALUP_COMP_PWD;
               State = SCHL_STATE_CONNECTED;
               bSuccess = TRUE;
            }
            else if(Attempts-- > 0)
            {
               State = SCHL_STATE_SECURITY;
            }
            else
            {
               if(TraceFlag) printf("*** Communications error or incorrect security code ***\n");

               State = SCHL_STATE_ERRABORT;
            }
            break;
         }
      case SCHL_STATE_CONNECTED:
         {
            retval = NORMAL;

            // if(TraceFlag) printf("*** The METER is listening ***\n");

            FMask             = 1;
            CurrentFunction   = 0;

            //printf("\nFunction = 0x%08X\n", Function);

            do
            {
               while(!(Function & FMask) && FMask < 0x1000)
               {
                  FMask <<= 1;
               }

               if(FMask < 0x1000)
               {
                  CurrentFunction = Function & FMask;
                  FMask <<= 1;   // Look at the next position next time through
               }
               else
               {
                  CurrentFunction = SCHL_FUNC_COMPLETE;
               }

               switch(CurrentFunction)
               {
               case SCHL_FUNC_SIMPLE_REQUEST:
                  {
                     switch(Command)
                     {
                     case SCHL_CMD_UPLOAD_ALL:
                        {
                           Transfer.InBuffer = DataBuffer;
                           SchlDoUploadData(&Transfer, &MeterSt, &ReqRecord);
                           Function = 0;
                           break;
                        }
                     case SCHL_CMD_UPLOAD_DATA:
                        {
                           printf("What's happening\n");
                           CTISleep(3000);
                           Transfer.InBuffer = DataBuffer;
                           SchlDoUploadData(&Transfer, &MeterSt, &ReqRecord);
                           break;
                        }
                     case SCHL_CMD_DOWNLOAD_DATA:
                        {
                           SchlDoDownloadData(Function, &ReqRecord);
                           break;
                        }
                     case SCHL_CMD_UPLOAD_SCAN:
                        {
                           /*
                            *  This is the scanner request... Scanned data comes through here
                            */
                           Transfer.InBuffer = DataBuffer;
                           if(SchlDoScannerData(&Transfer, &MeterSt, &ReqRecord))
                           {
                              retval = !NORMAL;
                           }
                           Function = 0;
                           break;
                        }
                     default:
                        {
                           printf("Bad Simple Command = 0x%04X at line %d\n", Command, __LINE__);
                        }
                     }
                     break;
                  }
               case SCHL_FUNC_CONFIGURATION_DATA:
                  {
                     retval = NORMAL;
                     break;
                  }
               case SCHL_FUNC_COMPLETE:
                  {
                     retval = NORMAL;
                     State = SCHL_STATE_COMPLETE;
                     break;
                  }
               default:
                  {
                     retval = !NORMAL;
                     printf("Bad CurrentFunction = 0x%04X at line %d\n", CurrentFunction, __LINE__);
                     break;
                  }
               }
            } while(CurrentFunction != SCHL_FUNC_COMPLETE);
         }
      case SCHL_STATE_COMPLETE:
         {
            retval = NORMAL;
            break;
         }
      default:
         {
            retval = !NORMAL;
            printf("Unknown \"State\" in %s = 0x%04X\n",__FILE__, State);
            break;
         }
      }
   } while(!(State & SCHL_STATE_ERRABORT) && !(State == SCHL_STATE_COMPLETE) );


   if(!retval)
   {
      if(SCHL_CMD_UPLOAD_SCAN == Command)
      {
         SchlDoDataShuffle((FulcrumScanData_t*)DataBuffer);
         // SchlDoDataDisplay((FulcrumScanData_t*)DataBuffer);

         memcpy(DUPRep->Message, DataBuffer, sizeof(FulcrumScanData_t));
         DUPRep->CompFlag |= DIALUP_COMP_SUCCESS;
         // printf("Comunication for periodic scan completed with the Shlumberger Meter\n");
      }
#ifdef OLDANDCRAPPY

      else if(SCHL_CMD_UPLOAD_LOADPROFILE == Command)
      {
         memcpy(DUPRep->Message, LPBuffer, sizeof(FulcrumLoadProfileMessage_t));
         DUPRep->CompFlag |= DIALUP_COMP_SUCCESS;
         // printf("Comunication for load profile completed with the Shlumberger Meter\n");
      }
#endif
      else
      {
         fprintf(stderr,"*** ERROR *** %s : %s %d\n",__FILE__, __FILE__, __LINE__);
      }
   }
   else
   {
      printf("Comunication with the Shlumberger Meter ** FAILED **\n");
      DUPRep->CompFlag &= ~(DIALUP_COMP_SUCCESS);
   }

   return retval;
}

INT SchlDoUploadData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec)
{
   BYTE        Buffer[10];
   ULONG       InCount = 0;
   BYTEULONG   Temp;
   /*
    *    At this point I have only the InBuffer and outbuffer elements of the Xfer
    *    struct filled....  MeterSt is fully filled
    */

   Buffer[0] = 'U';

   Temp.ul = MeterSt->Start;

   Buffer[1] = Temp.ch[2];
   Buffer[2] = Temp.ch[1];
   Buffer[3] = Temp.ch[0];

   Temp.ul = MeterSt->Stop;

   Buffer[4] = Temp.ch[2];
   Buffer[5] = Temp.ch[1];
   Buffer[6] = Temp.ch[0];

   Xfer->OutBuffer         = Buffer;
   Xfer->OutCount          = 7;
   // Xfer->InBuffer Already set to DataBuffer
   Xfer->InCountExpected   = 1;
   Xfer->InCountReceived   = &InCount;
   Xfer->Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

   do
   {
      DialupSendAndReceive(Xfer, ReqRec);

      if(Xfer->InBuffer[0] == NAK)
      {
         Xfer->OutCount          = 7;                    // Reset this to the proper value for CRC additions
         Temp.ul = MeterSt->Stop = MeterSt->Stop - 5;

         Buffer[4] = Temp.ch[2];
         Buffer[5] = Temp.ch[1];
         Buffer[6] = Temp.ch[0];
      }

   } while(Xfer->InBuffer[0] == NAK);

   Xfer->InCountExpected   = MeterSt->Length + 2;
   DialupReceive(Xfer, ReqRec);

   return NORMAL;
}

INT      SchlDoDownloadData(UINT Function, REQUESTRECORD *ReqRec)
{
   return NORMAL;
}

UCHAR   SchlDecodeAckNak(UCHAR AckNak)
{
   UCHAR    ret = AckNak;     // TRUE will indicate a NAK response....

   switch(AckNak)
   {
   case ACK:
      // ACK - No Error
      break;
   case CAN:
      printf("CAN: Cannot comply. Security not satisfied\n");
      break;
   case NAK:
      printf("NAK: Invalid or improper command, or CRC check failed.\n");
      // CTISleep(2000);      // Per Spec. 3.8.2 Command set Protocol
      break;
   default:
      printf("Unknown response from meter.\n");
      // CTISleep(2000);      // Per Spec. 3.8.2 Command set Protocol
      break;
   }

   return ret;
}



BOOL    SchlDoClassDecode(REQUESTRECORD *ReqRec)
{

   OUTMESS     *OutMessage    = ReqRec->OMessage;
   INMESS      *InMessage     = ReqRec->IMessage;
   CtiPort     *PortRecord    = ReqRec->PRecord;
   CtiDeviceBase   *RemoteRecord  = ReqRec->RRecord;
   DEVICE      *DeviceRecord  = ReqRec->DRecord;
   DIALUPREPLY *DUPRep        = &ReqRec->IMessage->Buffer.DUPSt.DUPRep;

   return TRUE;
}


INT    SchlDoIdentifyCommand(REQUESTRECORD *ReqRec, SchlMeterStruct *MeterSt)
{

   BYTE        Buffer[24];
   BYTE        InBuffer[24];
   ULONG       ReceiveLength = 0;
   CTIXMIT     Transfer;

   Buffer[0 ] = 'I';
   Buffer[1 ] = 0  ;
   Buffer[2 ] = 0  ;
   Buffer[3 ] = 0  ;
   Buffer[4 ] = 0  ;
   Buffer[5 ] = 0  ;
   Buffer[6 ] = 0  ;
   Buffer[7 ] = 0  ;
   Buffer[8 ] = 0  ;
   Buffer[9 ] = 0  ;
   Buffer[10] = 0  ;
   Buffer[11] = 0  ;

   Transfer.OutBuffer         = Buffer;
   Transfer.OutCount          = 12;
   Transfer.InBuffer          = InBuffer;
   Transfer.InCountExpected   = 20;
   Transfer.InCountReceived   = &ReceiveLength;
   Transfer.InTimeout         = 0;
   Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

   DialupSendAndReceive(&Transfer, ReqRec);

   if(InBuffer[0] == ACK)
   {
      MeterSt->Start = StrToUlong(&InBuffer[12], 3);
      MeterSt->Stop  = StrToUlong(&InBuffer[15], 3);
      MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1; // Return the maximum requirements of the meter.
      memcpy(&MeterSt->UnitType, &InBuffer[1], 3);
      MeterSt->UnitType[3] = '\0';
      memcpy(&MeterSt->UnitId, &InBuffer[4], 8);
      MeterSt->UnitId[8] = '\0';

/*
      printf("Unit Type: %.3s\n",&InBuffer[1]);
      printf("Unit ID  : %.8s\n",&InBuffer[4]);
      printf("Mem Start: 0x%06X\n",MeterSt->Start);
      printf("Mem Stop : 0x%06X\n",MeterSt->Stop);
*/

      return NORMAL;
   }
   else
   {
      return !NORMAL;
   }

}

#define SCHL_SCAN_STATE_FETCH                   0

#define SCHL_SCAN_STATE_SETUP1                  10
#define SCHL_SCAN_STATE_SETUP2                  11
#define SCHL_SCAN_STATE_SETUP3                  12
#define SCHL_SCAN_STATE_SETUP4                  13
#define SCHL_SCAN_STATE_SETUP5                  14
#define SCHL_SCAN_STATE_SETUP6                  15
#define SCHL_SCAN_STATE_SETUP_TIME              16
#define SCHL_SCAN_STATE_SETUP_MM_CONFIG         17
#define SCHL_SCAN_STATE_SETUP_MM_INIT           20
#define SCHL_SCAN_STATE_SETUP_MM_READ           30
#define SCHL_SCAN_STATE_SETUP_MM_FINISH         40

#define SCHL_SCAN_STATE_FULL_RECORD             75

#define SCHL_SCAN_STATE_DECODE1                 101
#define SCHL_SCAN_STATE_DECODE2                 102
#define SCHL_SCAN_STATE_DECODE3                 103
#define SCHL_SCAN_STATE_DECODE4                 104
#define SCHL_SCAN_STATE_DECODE5                 105
#define SCHL_SCAN_STATE_DECODE6                 106
#define SCHL_SCAN_STATE_DECODE_TIME             107
#define SCHL_SCAN_STATE_DECODE_MM_CONFIG        108
#define SCHL_SCAN_STATE_DECODE_MM_READ          120

#define SCHL_SCAN_STATE_TEST                    500

#define SCHL_SCAN_STATE_ERRABORT                900

#define SCHL_SCAN_STATE_COMPLETE                1000

INT SchlDoScannerData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec)
{
   INT         rc = !NORMAL;
   INT         Attempts;
   INT         CRCErr;
   INT         retval;
   BOOL        bDone = FALSE;
   BYTE        Buffer[10];
   BYTE        LocalInBuffer[300];
   BYTE        *DBuffer    = Xfer->InBuffer;
   ULONG       State       = SCHL_SCAN_STATE_SETUP1;
   ULONG       NextState   = 0;
   ULONG       InCount     = 0;
   ULONG       ReceivedCount     = 0;
   BYTEULONG   Temp;

   CTIXMIT     Transfer;
   PSZ         Environment;

   if(!(CTIScanEnv ("DSM2_SCHLUMBERGER_COMMRETRIES", &Environment)))
   {
      SclumbergerRetries = atoi (Environment);
   }

   do
   {
      switch(State)
      {
      case SCHL_SCAN_STATE_FETCH:
         {
//            CTISleep(1000);

            Transfer.OutBuffer         = Buffer;
            Transfer.OutCount          = 7;
            Transfer.InBuffer          = LocalInBuffer;
            Transfer.InCountExpected   = MeterSt->Length + 3;
            Transfer.InCountReceived   = &ReceivedCount;
            Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;
            // Transfer.Flags             = XFER_ADD_CRC;

            Temp.ul = MeterSt->Start;

            Buffer[1] = Temp.ch[2];
            Buffer[2] = Temp.ch[1];
            Buffer[3] = Temp.ch[0];

            Temp.ul = MeterSt->Stop;

            Buffer[4] = Temp.ch[2];
            Buffer[5] = Temp.ch[1];
            Buffer[6] = Temp.ch[0];

            do
            {
               /*
                *  Go out and get the data
                */

               Transfer.InTimeout         = 0;
               retval = DialupSendAndReceive(&Transfer, ReqRec);

               if(retval || LocalInBuffer[0] == NAK)
               {
                  if(retval == DIALUP_ERR_BADCRC)
                  {
                     CRCErr++;
                     if(CRCErr > 20)
                     {
                        CRCErr = 0;
                        Attempts = 0; // Abort this comm NOW.
                     }
                  }
                  else
                  {
                     Attempts--;
                  }

                  Transfer.OutCount = 7;     // Reset this to the proper value for CRC additions

                  printf("%s: NAK on receive in state %d\n",__FILE__, State);

                  if(Attempts <= 0)
                     State = SCHL_SCAN_STATE_ERRABORT;
               }
               else // Good Data read
               {
                  CRCErr = 0;
                  if(DBuffer)    // Only if we have a DBuffer.. This would be horrible
                  {
                     State = NextState;
                     break; //while loop
                  }
                  else
                  {
                     printf("Bad. Very very bad.\n");
                     State = SCHL_SCAN_STATE_ERRABORT;
                     Attempts = 0;
                     break;
                  }
               }
            } while(Attempts > 0);

            break;
         }
      case SCHL_SCAN_STATE_SETUP1:
         {
            Buffer[0] = 'U';

            MeterSt->Start = 0x210D;
            MeterSt->Stop  = 0x215D;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE1;

            break;
         }
      case SCHL_SCAN_STATE_SETUP2:
         {
            Buffer[0] = 'U';

            MeterSt->Start = 0x27FD;
            MeterSt->Stop  = 0x28DC;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE2;

            break;
         }
      case SCHL_SCAN_STATE_SETUP3:
         {
            Buffer[0] = 'U';

            /* get the first third (Watts) of the demand registers */

            MeterSt->Start = 0x28DD;
            MeterSt->Stop  = 0x296A;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE3;

            break;
         }
      case SCHL_SCAN_STATE_SETUP4:
         {
            Buffer[0] = 'U';

            /* get the second third (Lagging VARS) of the demand registers */

            MeterSt->Start = 0x296B;
            MeterSt->Stop  = 0x29F8;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE4;

            break;
         }
      case SCHL_SCAN_STATE_SETUP5:
         {
            Buffer[0] = 'U';

            /* get the last third (VA) of the demand registers */

            MeterSt->Start = 0x29F9;
            MeterSt->Stop  = 0x2A86;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE5;

            break;
         }
      case SCHL_SCAN_STATE_SETUP6:
         {
            Buffer[0] = 'U';

            /* get the PF and Instantaneous Volts/Amps */

            MeterSt->Start = 0x2A87;
            MeterSt->Stop  = 0x2AEB;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE6;

            break;
         }
      case SCHL_SCAN_STATE_SETUP_TIME:
         {
            Buffer[0] = 'U';

            /* get the date and time */

            MeterSt->Start = 0x32BC;
            MeterSt->Stop  = 0x32C2;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE_TIME;

            break;
         }
      case SCHL_SCAN_STATE_DECODE1:
         {
            // I know InCount and LocalInBuffer, and Transfer.InCountReceived I also have MeterSt

            // Active Phases
            DBuffer[InCount] = LocalInBuffer[1];
            InCount++;

            // Demand Reset Count
            memcpy(&DBuffer[InCount], &LocalInBuffer[0x211E - MeterSt->Start + 1], 2);
            InCount += 2;

            // Counts
            memcpy(&DBuffer[InCount], &LocalInBuffer[0x2124 - MeterSt->Start + 1], 4);
            InCount += 4;

            memcpy(&DBuffer[InCount], &LocalInBuffer[0x213c - MeterSt->Start + 1], 34);
            InCount += 34;

            State = SCHL_SCAN_STATE_SETUP2;
            break;
         }
      case SCHL_SCAN_STATE_DECODE2:
         {
            // I know InCount and LocalInBuffer, and Transfer.InCountReceived I also have MeterSt
            // Register Multiplier
            memcpy(&DBuffer[InCount], &LocalInBuffer[0x2814 - MeterSt->Start + 1], 4);
            InCount += 4;

            // Energy Registers
            memcpy(&DBuffer[InCount], &LocalInBuffer[0x2819 - MeterSt->Start + 1], 196);
            InCount += 196;

            State = SCHL_SCAN_STATE_SETUP3;
            break;
         }
      case SCHL_SCAN_STATE_DECODE3:
         {
            // I know InCount and LocalInBuffer, and Transfer.InCountReceived I also have MeterSt
            // Watts Demand registers
            memcpy(&DBuffer[InCount], &LocalInBuffer[1], MeterSt->Length);
            InCount += MeterSt->Length;

            State = SCHL_SCAN_STATE_SETUP4;
            break;
         }
      case SCHL_SCAN_STATE_DECODE4:
         {
            // I know InCount and LocalInBuffer, and Transfer.InCountReceived I also have MeterSt
            // Lagging VARS Demand registers
            memcpy(&DBuffer[InCount], &LocalInBuffer[1], MeterSt->Length);
            InCount += MeterSt->Length;

            State = SCHL_SCAN_STATE_SETUP5;
            break;
         }
      case SCHL_SCAN_STATE_DECODE5:
         {
            // I know InCount and LocalInBuffer, and Transfer.InCountReceived I also have MeterSt
            // VA Demand registers
            memcpy(&DBuffer[InCount], &LocalInBuffer[1], MeterSt->Length);
            InCount += MeterSt->Length;

            State = SCHL_SCAN_STATE_SETUP6;
            break;
         }
      case SCHL_SCAN_STATE_DECODE6:
         {
            // PF & Instantaneous Volts/Amps
            memcpy(&DBuffer[InCount], &LocalInBuffer[1], MeterSt->Length);
            InCount += MeterSt->Length;

            State = SCHL_SCAN_STATE_SETUP_TIME;
            break;
         }
      case SCHL_SCAN_STATE_DECODE_TIME:
         {
            // Time/date
            memcpy(&DBuffer[InCount], &LocalInBuffer[1], MeterSt->Length);
            InCount += MeterSt->Length;

            State = SCHL_SCAN_STATE_COMPLETE;
            break;
         }
      case SCHL_SCAN_STATE_TEST:
         {
            Buffer[0] = 'U';

            /* get the date and time */

            MeterSt->Start = 0x2814;
            MeterSt->Stop  = 0x2817;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_COMPLETE;

/*
            printf("\n\n**** InCount = %u ****\n",InCount);
            printf("Energy Size = %d = %x\n",sizeof(FulcrumEnergyRegister_t));
            printf("Demand Size = %d = %x\n",sizeof(FulcrumDemandRegister_t));
*/
            break;
         }
      case SCHL_SCAN_STATE_COMPLETE:
         {

            ReqRec->IMessage->InLength = InCount;
            bDone = TRUE;
            rc = NORMAL;

            if(ReqRec->OMessage->Buffer.DUPReq.Command[1] == SCHL_GET_LOADPROFILE)
            {
               /* Whoo Hooo Dilly, I gots me a Load Profile request.... */

               SchlDoLoadProfileData(Xfer, MeterSt, ReqRec);
            }

            break;
         }
      }
   } while(!bDone && State != SCHL_SCAN_STATE_ERRABORT);

   return rc;
}

INT SchlDoLoadProfileData(CTIXMIT *Xfer, SchlMeterStruct* MeterSt, REQUESTRECORD *ReqRec)
{
   INT                           rc = !NORMAL;
   INT                           Attempts;
   INT                           retval;
   BOOL                          bDone                = FALSE;
   BYTE                          Buffer[10];
   BYTE                          LocalInBuffer[300];
   ULONG                         State                = SCHL_SCAN_STATE_SETUP_TIME;
   ULONG                         NextState            = 0;
   ULONG                         ReceivedCount        = 0;
   BYTEULONG                     Temp;

   CTIXMIT                       Transfer;            // Local Transfer structure.

   /*
    *   Load Profile Data Variables
    */

   time_t                        ltime;
   ULONG                         MMMaxRecord          = 0L;
   ULONG                         MMMaxAddress         = 0L;
   INT                           MMCurrentYear        = 0;

   INT                           MMIndex              = 0;
   INT                           MMVintage            = 0;        // This is the last index we need to retrieve to make scanner happy

   ULONG                         MMTemp               = 0L;
   ULONG                         MMCount              = 0;
   ULONG                         MMStart              = 0L;
   ULONG                         MMStop               = 0L;
   ULONG                         MMCurrent            = 0L;
   ULONG                         MMPos                = 0L;
   ULONG                         MMBlockSize          = 0L;
   ULONG                         MMScanStartAddress   = 0L;
   SchlLoadProfile_t             *MMLProfile          = NULL;

   struct tm                     *tm_ptr;

   BYTE                          *DBuffer             = (BYTE*) malloc(sizeof(FulcrumLoadProfileMessage_t)); // Xfer->InBuffer;
   time_t                        ScannerRequestTime   = (time_t) ReqRec->OMessage->Buffer.DUPReq.LP_Time;

   FulcrumRealTimeRegister_t     TimeDate;
   FulcrumLoadProfileMessage_t   *FulcrumLProfile     = (FulcrumLoadProfileMessage_t*)DBuffer;
   FulcrumMMConfig_t             *MassMemoryConfig    = &(FulcrumLProfile->MMConfig);

   INMESS                        MyInMessage;
   ULONG                         BytesWritten;

   if(!DBuffer) return(FALSE);


   memcpy(&MyInMessage, ReqRec->IMessage, sizeof(INMESS));
   MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0]   = SCHL_CMD_UPLOAD_LOADPROFILE;
   MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.LastFileTime = ReqRec->OMessage->Buffer.DUPReq.LastFileTime;
   do
   {
      switch(State)
      {
      case SCHL_SCAN_STATE_FETCH:
         {
            // CTISleep(1000);
            Transfer.OutBuffer         = Buffer;                           // Request pointer
            Transfer.OutCount          = 7;                                // Request bytes
            Transfer.InBuffer          = LocalInBuffer;                    // Reply pointer
            Transfer.InCountExpected   = MeterSt->Length + 3;              // Bytes expected
            Transfer.InCountReceived   = &ReceivedCount;                   // Bytes rec. (output)
            Transfer.Flags             = XFER_ADD_CRC | XFER_VERIFY_CRC;

            Temp.ul = MeterSt->Start;

            Buffer[1] = Temp.ch[2];
            Buffer[2] = Temp.ch[1];
            Buffer[3] = Temp.ch[0];

            Temp.ul = MeterSt->Stop;

            Buffer[4] = Temp.ch[2];
            Buffer[5] = Temp.ch[1];
            Buffer[6] = Temp.ch[0];

            do
            {
               /*
                *  Go out and get the data
                */
               Transfer.InTimeout          = 90;
               retval = DialupSendAndReceive(&Transfer, ReqRec);

               if(retval || LocalInBuffer[0] == NAK)
               {
                  Attempts--;
                  Transfer.OutCount = 7;     // Reset this to the proper value for CRC additions

                  printf("%s: NAK on receive in state %d\n",__FILE__, State);

                  if(Attempts <= 0)
                     State = SCHL_SCAN_STATE_ERRABORT;
               }
               else // Good Data read
               {
                  if(DBuffer)    // Only if we have a DBuffer.. This would be horrible
                  {
                     State = NextState;
                     break; //while loop
                  }
                  else
                  {
                     printf("Bad. Very very bad.\n");
                     State = SCHL_SCAN_STATE_ERRABORT;
                     Attempts = 0;
                     break;
                  }
               }
            } while(Attempts > 0);

            break;
         }
      case SCHL_SCAN_STATE_SETUP_TIME:
         {
            Buffer[0] = 'U';

            /* get the date and time */

            MeterSt->Start = 0x32BC;
            MeterSt->Stop  = 0x32C2;
            MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

            Attempts    = SclumbergerRetries;
            State       = SCHL_SCAN_STATE_FETCH;
            NextState   = SCHL_SCAN_STATE_DECODE_TIME;

            break;
         }
      case SCHL_SCAN_STATE_DECODE_TIME:
         {
            // Time/date
            memcpy(&TimeDate, &LocalInBuffer[1], MeterSt->Length);

            State = SCHL_SCAN_STATE_SETUP_MM_CONFIG;
            break;
         }

      case SCHL_SCAN_STATE_SETUP_MM_CONFIG:
         {
            if(ScannerRequestTime > 0)
            {
               Buffer[0] = 'U';

               /* get the Mass Memory Configuration.... */

               MeterSt->Start = 0x32C9;
               MeterSt->Stop  = 0x335F;
               MeterSt->Length = MeterSt->Stop - MeterSt->Start + 1;

               Attempts    = SclumbergerRetries;
               State       = SCHL_SCAN_STATE_FETCH;
               NextState   = SCHL_SCAN_STATE_DECODE_MM_CONFIG;
            }
            else
            {
               printf("Requestor did not specify Load Profile Time \n");
               State       = SCHL_SCAN_STATE_COMPLETE;
            }
            break;
         }
      case SCHL_SCAN_STATE_DECODE_MM_CONFIG:
         {
            memcpy(MassMemoryConfig, &LocalInBuffer[1], MeterSt->Length);

            // Convert to values we can use here... (little endian)
            MassMemoryConfig->RecordLength  = (USHORT)BytesToBase10((UCHAR*)&(MassMemoryConfig->RecordLength),2);
            MassMemoryConfig->CurrentRecord = (USHORT)BytesToBase10((UCHAR*)&(MassMemoryConfig->CurrentRecord),2);

            State = SCHL_SCAN_STATE_SETUP_MM_INIT;
            break;
         }
      case SCHL_SCAN_STATE_SETUP_MM_INIT:
         {
            ltime          = LongTime();         // Should also get DST stuff set for us... heh heh.
            tm_ptr         = localtime(&ltime);
            MMCurrentYear  = tm_ptr->tm_year;    // Minus 1900.... Used to figure "when" the records are.

            MMStart      = BytesToBase10(MassMemoryConfig->LogicalStart  ,3);
            MMStop       = BytesToBase10(MassMemoryConfig->LogicalEnd    ,3);
            MMCurrent    = BytesToBase10(MassMemoryConfig->CurrentLogical,3);

            MMMaxRecord  = (MMStop - MMStart) / MassMemoryConfig->RecordLength;   // Number of records which fit in MM
            MMMaxAddress = MMMaxRecord * MassMemoryConfig->RecordLength;          // Last Valid Address Should be MaxRecord - MaxRecord % RecordLength

            /*
             * Decide on the preferred block size for our requests....
             * May be a (Communication protocol) maximum 256 bytes less ACK & 2 byte CRC..
             * I'm using 250, or the RecordLength to make life easy, and pretty.
             */
            if(MassMemoryConfig->RecordLength < 252)
               MMBlockSize = MassMemoryConfig->RecordLength;
            else
               MMBlockSize = 250;

/*
            printf("\n*** Mem Range %ld to %ld \nCurrent %ld\nRecord Length = %d\n\n"
                   , MMStart
                   , MMStop
                   , MMCurrent
                   , MassMemoryConfig->RecordLength);
*/

            MMLProfile  = (SchlLoadProfile_t*) malloc ((MMMaxRecord + 1) * sizeof(SchlLoadProfile_t));

            if(MMLProfile != NULL)
            {

               MMIndex            = 0;
               MMScanStartAddress = MMCurrent;

               do
               {
                  /*
                   *  Compute values for the MMLProfile array back to a UNIX time prior to the
                   *  Scanner requested time.  MMScanStartAddress is backed progressivly through
                   *  time and recorded to allow faster requests of the data.
                   */

                  MMLProfile[MMIndex].RecordAddress = MMScanStartAddress;

                  if(MMIndex == 0)
                  {
                     /*
                      * We need the tm_ptr initialized to allow us to determine the
                      * current year for the meter... This value is not stored in the MM Records
                      */
                     tm_ptr = localtime(&ltime);

                     /*
                      * We are looking at the MMCurrent record.  This effectively "primes"
                      * my computational pump and sets up our time base...  If this is wrong,
                      * the load profile data will represent the wrong time intervals
                      *
                      * FulcrumScan is the Meter Configuration Data brought back in prior scans.
                      */
                     tm_ptr->tm_mon  = (INT)TimeDate.Month - 1; // Months are zero based in time functions
                     tm_ptr->tm_mday = (INT)TimeDate.DayOfMonth;
                     tm_ptr->tm_hour = (INT)TimeDate.Hours;
                     tm_ptr->tm_min  = (INT)TimeDate.Minutes - ((INT)TimeDate.Minutes % (INT)MassMemoryConfig->IntervalLength)- ((INT)MassMemoryConfig->CurrentInterval * (INT)MassMemoryConfig->IntervalLength);
                     tm_ptr->tm_sec  = 0;

                     /*
                      *  Schlumberger keeps modulo 100 years.. bad bad bad and I must fixee here
                      */
                     if(MMCurrentYear > 99)  // We are 2000 or beyond
                        tm_ptr->tm_year = (INT)TimeDate.Year + 100;
                     else
                        tm_ptr->tm_year = (INT)TimeDate.Year;

                     /*
                      *  Now build a unix time out of the prior settings.
                      *  NOTE:  This is a UNIX time of RECORD START!
                      */
                     ltime = mktime(tm_ptr);
                     MMLProfile[MMIndex].RecordTime    = ltime;

// #define DEBUGTIMES TRUE
#ifdef DEBUGTIMES
                     tm_ptr = localtime(&ltime);     // This is the time address of the NEXT (in time) interval
                     fprintf(stderr,"%d Month/Day  %d/%d ",MMIndex , tm_ptr->tm_mon+1, tm_ptr->tm_mday);
                     fprintf(stderr,"  %d:%02d:%02d \n", tm_ptr->tm_hour, tm_ptr->tm_min, tm_ptr->tm_sec);
#endif
                     // And the next INDEX
                     MMIndex++;

                  }
                  else
                  {
                     /*
                      *  Use some magic to figure out the time of the current interval.
                      *
                      *  Note that ltime should represent the time of the last intervals start...
                      *
                      *  We need only to subtract off the number of whole hours which
                      *  are filled in one record (60 intervals).  Pretty nice of the berger
                      *  engineers don't you think?
                      */

                     tm_ptr = localtime(&ltime);     // This is the time address of the NEXT (in time) interval

                     // Since IntervalLength minutes/interval * 60 intervals/record * 1/60 hours/minute == hours/record
                     tm_ptr->tm_hour -= (INT)MassMemoryConfig->IntervalLength;
                     tm_ptr->tm_min  = 0;
                     tm_ptr->tm_sec  = 0;             // Sanity only...

                     ltime = mktime(tm_ptr);         // get the new time value.

                     MMLProfile[MMIndex].RecordTime    = ltime;       // UNIX time of record start!

#ifdef DEBUGTIMES
                     tm_ptr = localtime(&ltime);     // This is the time address of the NEXT (in time) interval
                     fprintf(stderr,"%d Month/Day  %d/%d ",MMIndex , tm_ptr->tm_mon+1, tm_ptr->tm_mday);
                     fprintf(stderr,"  %d:%02d:%02d \n", tm_ptr->tm_hour, tm_ptr->tm_min, tm_ptr->tm_sec);
#endif
                     MMIndex++;

                  }

                  // Back 1 Record with the Address.
                  MMScanStartAddress = SchlPreviousMassMemoryAddress(MMStart,
                                                                     MMScanStartAddress,
                                                                     MMMaxAddress,
                                                                     MassMemoryConfig->RecordLength);

                  /*
                   *  A note about our conditions here.  We stop computations if we find a time
                   *  prior to the point scanner asked for.  i.e. if we get all this data
                   *  scanner will already have some amount of it.
                   *
                   *  We also stop scanning if we loop through all MM and are once again
                   *  looking at the Current Address
                   */

               } while(MMScanStartAddress != MMCurrent && ScannerRequestTime < ltime);

               // This allows us to only retrieve the MM Records we need for scanner
               MMVintage = MMIndex - 1;    // Scanner gets oldest request first....
               MMIndex   = 0;              // Reset for usage later...


               /*
                *  OK, now we know who is the "oldest" record we want to bring back.  He is
                *  MMVintage, and we also want to return the following interval's time.
                *  We can fill in the FulcrumLProfile struct's times now.
                */

               FulcrumLProfile->RecordTime         = MMLProfile[MMVintage].RecordTime;

               if(MMVintage >= 1)
                  FulcrumLProfile->NextRecordTime  = MMLProfile[MMVintage - 1].RecordTime;
               else
                  FulcrumLProfile->NextRecordTime  = 0L;  // Only get the CurrentAddress record.

               Buffer[0] = 'U';

               MMScanStartAddress = MMLProfile[MMVintage].RecordAddress;
               MeterSt->Start     = MMScanStartAddress;
               // MMPos points to the byte address in Schl. MM where the scan stopped.
               // This is either the end of Record, or the most we can ask for in one read.
               MMPos              = MMScanStartAddress + MMBlockSize;
               MeterSt->Stop      = MMPos;
               MeterSt->Length    = MeterSt->Stop - MeterSt->Start + 1;

               Attempts    = SclumbergerRetries;
               State     = SCHL_SCAN_STATE_FETCH;            // Fetch MMBlockSize bytes from Schl
               NextState = SCHL_SCAN_STATE_DECODE_MM_READ;   // After fetch go to this State.
            }
            else
            {
               printf("\n\n**** Error mallocing for Mass Memory Read ****\n\n");
               State = SCHL_SCAN_STATE_ERRABORT;
            }

            break;
         }
      case SCHL_SCAN_STATE_DECODE_MM_READ:
         {

            /*
             *  Attend to the data which we DID just bring back.
             */
            if(LocalInBuffer[0] == ACK)
            {
               memcpy(&(FulcrumLProfile->MMBuffer[MMCount]), &LocalInBuffer[1], MeterSt->Length - 1);
               MMCount += (MeterSt->Length - 1);

               /*
                * Now decide if we need more, or if we need to go back a record
                */
               if(MMPos < MMScanStartAddress + MassMemoryConfig->RecordLength)
               {
                  /*
                   *  We need to go out and get more data, because we did NOT get it all last time.
                   */

                  // Ho much more to get???
                  MMTemp = (MMScanStartAddress + MassMemoryConfig->RecordLength) - MMPos;
                  MeterSt->Start = MMPos;
                  if(MMTemp > MMBlockSize)
                  {
                     MMPos += MMBlockSize;
                     MeterSt->Stop = MMPos;
                  }
                  else
                  {
                     MMPos += MMTemp;
                     MeterSt->Stop = MMPos;
                  }

                  MeterSt->Length   = MeterSt->Stop - MeterSt->Start + 1;

                  Attempts    = SclumbergerRetries;
                  State       = SCHL_SCAN_STATE_FETCH;
                  NextState   = SCHL_SCAN_STATE_DECODE_MM_READ;   // Caome back here!
               }
               else
               {
                  /*
                   *  We've got the record! get it back to SCANNER & Let him decide what to do next.
                   */
                  State = SCHL_SCAN_STATE_FULL_RECORD;

               }
            }
            else
            {
               fprintf(stderr,"*** NAK on Read.. Error recovery code is strangely absent \n");
               fprintf(stderr,"*** I'm taking my ball and going home. \n");
               fprintf(stderr,"*** %s : %s %d\n",__FILE__, __FILE__, __LINE__);
            }

            break;
         }
      case SCHL_SCAN_STATE_SETUP_MM_READ:
         {
            /*
             *  We have just completed an entire get on a record.
             *  Point at the next one to get
             */
            MMIndex++;                                   // Next Address

            /*
             *  Check if we've gotten as many as Scanner needs.
             */
            if(MMIndex <= MMVintage)
            {
               /*
                *  Use our hard earned MMLProfile to set up the next read
                */
               Buffer[0] = 'U';
#if 0
               MMPos = SchlPreviousMassMemoryAddress(MMStart,
                                                     MMPos,
                                                     MMMaxAddress,
                                                     MassMemoryConfig->RecordLength);
               MMPos = SchlPreviousMassMemoryAddress(MMStart,
                                                     MMPos,
                                                     MMMaxAddress,
                                                     MassMemoryConfig->RecordLength);

               if( MMPos != MMLProfile[MMVintage - MMIndex].RecordAddress )
               {
                  fprintf(stderr,"I'm taking my ball and going home\n");
                  fprintf(stderr,"MMPos = 0x%08X RA = 0x%08X\n",MMPos, MMLProfile[MMIndex].RecordAddress);
                  fprintf(stderr,"*** CGP *** %s : %s %d\n",__FILE__, __FILE__, __LINE__);
               }
#endif
               MMScanStartAddress = MMLProfile[MMVintage - MMIndex].RecordAddress;
               MeterSt->Start     = MMScanStartAddress;
               // MMPos points to the byte address in Schl. MM where the scan stopped.
               // This is either the end or Record, or the most we can ask for in one read.
               MMPos              = MMScanStartAddress + MMBlockSize;
               MeterSt->Stop      = MMPos;
               MeterSt->Length    = MeterSt->Stop - MeterSt->Start + 1;

               FulcrumLProfile->RecordTime         = MMLProfile[MMVintage - MMIndex].RecordTime;
               if(MMVintage - MMIndex >= 1)
                  FulcrumLProfile->NextRecordTime  = MMLProfile[MMVintage - MMIndex - 1].RecordTime;
               else
                  FulcrumLProfile->NextRecordTime  = 0L;  // Only get the CurrentAddress record.

               Attempts    = SclumbergerRetries;
               State              = SCHL_SCAN_STATE_FETCH;
               NextState          = SCHL_SCAN_STATE_DECODE_MM_READ;

            }
            else
            {
               /*
                *  We've gotten all the required MM Records.
                *  This is the End of the MM request sequence and
                *  all decoding is done after this on scanner side
                */
               State = SCHL_SCAN_STATE_COMPLETE;
            }

            break;
         }
      case SCHL_SCAN_STATE_FULL_RECORD:
         {
            MyInMessage.InLength = sizeof(FulcrumLoadProfileMessage_t);

            memcpy(&(MyInMessage.Buffer.DUPSt.DUPRep.Message), DBuffer, sizeof(FulcrumLoadProfileMessage_t));
            MyInMessage.Buffer.DUPSt.DUPRep.CompFlag |= DIALUP_COMP_SUCCESS;

            MyInMessage.EventCode = NORMAL;  // I hope

            /* send message back to originating process */
            if(ReqRec->OMessage->ReturnNexus.NexusState != CTINEXUS_STATE_NULL)
            {
               if(CTINexusWrite (&(ReqRec->OMessage->ReturnNexus),
                                 &MyInMessage,
                                 sizeof (INMESS),
                                 &BytesWritten,
                                 0L))
               {
                  printf ("Error Writing to Return Pipe\n");
                  State = SCHL_SCAN_STATE_ERRABORT;
                  continue;
               }

               MMCount = 0;
               State = SCHL_SCAN_STATE_SETUP_MM_READ;       // Do another one...
            }
            else
            {
               State = SCHL_SCAN_STATE_ERRABORT;
            }

            break;
         }
      case SCHL_SCAN_STATE_COMPLETE:
         {
            // ReqRec->IMessage->InLength = sizeof(FulcrumLoadProfileMessage_t);
            printf("Comunication for load profile completed with the Shlumberger Meter\n");
            bDone = TRUE;
            rc = NORMAL;

            break;
         }
      }
   } while(!bDone && State != SCHL_SCAN_STATE_ERRABORT);

   //fprintf(stderr,"Load Profile Data was asked for %ld it is now %ld\n", ScannerRequestTime, LongTime());

   if(DBuffer)    free(DBuffer);
   if(MMLProfile) free(MMLProfile);

   return rc;
}


VOID SchlDoDataShuffle(FulcrumScanData_t* Fsd)
{
   /*
    *  Well God bless 'em but Intel is still Little Endian so we shuffle the bytes here
    */

   ShortLittleEndian(&Fsd->DemandResetCount);
   ShortLittleEndian(&Fsd->OutageCount);
   ShortLittleEndian(&Fsd->PhaseOutageCount);

   FltLittleEndian(&Fsd->RegisterMultiplier);

   FltLittleEndian(&Fsd->Watthour.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->Watthour.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->Watthour.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->Watthour.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->Watthour.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->Watthour.RateE_IntEnergy);

   FltLittleEndian(&Fsd->VARhourLag.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->VARhourLag.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->VARhourLag.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->VARhourLag.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->VARhourLag.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->VARhourLag.RateE_IntEnergy);

   FltLittleEndian(&Fsd->VAhour.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->VAhour.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->VAhour.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->VAhour.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->VAhour.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->VAhour.RateE_IntEnergy);

   FltLittleEndian(&Fsd->Qhour.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->Qhour.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->Qhour.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->Qhour.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->Qhour.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->Qhour.RateE_IntEnergy);

   FltLittleEndian(&Fsd->V2hour.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->V2hour.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->V2hour.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->V2hour.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->V2hour.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->V2hour.RateE_IntEnergy);

   FltLittleEndian(&Fsd->Amphour.Energy.RateE_Energy);
   FltLittleEndian(&Fsd->Amphour.Energy.RateA_Energy);
   FltLittleEndian(&Fsd->Amphour.Energy.RateB_Energy);
   FltLittleEndian(&Fsd->Amphour.Energy.RateC_Energy);
   FltLittleEndian(&Fsd->Amphour.Energy.RateD_Energy);
   FltLittleEndian(&Fsd->Amphour.RateE_IntEnergy);

   //fprintf(stderr,"Watts Demand\n");
   FltLittleEndian(&Fsd->WattsDemand.TotalMaximum.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.A_Maximum.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.B_Maximum.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.C_Maximum.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.D_Maximum.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Peak1.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Peak2.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Peak3.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Peak4.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Peak5.PeakValue);
   FltLittleEndian(&Fsd->WattsDemand.Instantaneous);
   FltLittleEndian(&Fsd->WattsDemand.TotalPreviousInterval);
   FltLittleEndian(&Fsd->WattsDemand.Coincident);
   FltLittleEndian(&Fsd->WattsDemand.TotalCumulative);
   FltLittleEndian(&Fsd->WattsDemand.A_Cumulative);
   FltLittleEndian(&Fsd->WattsDemand.B_Cumulative);
   FltLittleEndian(&Fsd->WattsDemand.C_Cumulative);
   FltLittleEndian(&Fsd->WattsDemand.D_Cumulative);
   FltLittleEndian(&Fsd->WattsDemand.TotalContinuousCumulative);
   FltLittleEndian(&Fsd->WattsDemand.A_ContinuousCumulative);
   FltLittleEndian(&Fsd->WattsDemand.B_ContinuousCumulative);
   FltLittleEndian(&Fsd->WattsDemand.C_ContinuousCumulative);
   FltLittleEndian(&Fsd->WattsDemand.D_ContinuousCumulative);

   //fprintf(stderr,"VARLag Demand\n");
   FltLittleEndian(&Fsd->VARLagDemand.TotalMaximum.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.A_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.B_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.C_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.D_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Peak1.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Peak2.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Peak3.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Peak4.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Peak5.PeakValue);
   FltLittleEndian(&Fsd->VARLagDemand.Instantaneous);
   FltLittleEndian(&Fsd->VARLagDemand.TotalPreviousInterval);
   FltLittleEndian(&Fsd->VARLagDemand.Coincident);
   FltLittleEndian(&Fsd->VARLagDemand.TotalCumulative);
   FltLittleEndian(&Fsd->VARLagDemand.A_Cumulative);
   FltLittleEndian(&Fsd->VARLagDemand.B_Cumulative);
   FltLittleEndian(&Fsd->VARLagDemand.C_Cumulative);
   FltLittleEndian(&Fsd->VARLagDemand.D_Cumulative);
   FltLittleEndian(&Fsd->VARLagDemand.TotalContinuousCumulative);
   FltLittleEndian(&Fsd->VARLagDemand.A_ContinuousCumulative);
   FltLittleEndian(&Fsd->VARLagDemand.B_ContinuousCumulative);
   FltLittleEndian(&Fsd->VARLagDemand.C_ContinuousCumulative);
   FltLittleEndian(&Fsd->VARLagDemand.D_ContinuousCumulative);

   //fprintf(stderr,"VA Demand\n");
   FltLittleEndian(&Fsd->VADemand.TotalMaximum.PeakValue);
   FltLittleEndian(&Fsd->VADemand.A_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VADemand.B_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VADemand.C_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VADemand.D_Maximum.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Peak1.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Peak2.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Peak3.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Peak4.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Peak5.PeakValue);
   FltLittleEndian(&Fsd->VADemand.Instantaneous);
   FltLittleEndian(&Fsd->VADemand.TotalPreviousInterval);
   FltLittleEndian(&Fsd->VADemand.Coincident);
   FltLittleEndian(&Fsd->VADemand.TotalCumulative);
   FltLittleEndian(&Fsd->VADemand.A_Cumulative);
   FltLittleEndian(&Fsd->VADemand.B_Cumulative);
   FltLittleEndian(&Fsd->VADemand.C_Cumulative);
   FltLittleEndian(&Fsd->VADemand.D_Cumulative);
   FltLittleEndian(&Fsd->VADemand.TotalContinuousCumulative);
   FltLittleEndian(&Fsd->VADemand.A_ContinuousCumulative);
   FltLittleEndian(&Fsd->VADemand.B_ContinuousCumulative);
   FltLittleEndian(&Fsd->VADemand.C_ContinuousCumulative);
   FltLittleEndian(&Fsd->VADemand.D_ContinuousCumulative);

   FltLittleEndian (&Fsd->PowerFactor.INSTPF);
   FltLittleEndian (&Fsd->PowerFactor.AVGPF);

   FltLittleEndian(&Fsd->InstReg.TotalVolts);
   FltLittleEndian(&Fsd->InstReg.A_Volts);
   FltLittleEndian(&Fsd->InstReg.B_Volts);
   FltLittleEndian(&Fsd->InstReg.C_Volts);
   FltLittleEndian(&Fsd->InstReg.TotalAmps);
   FltLittleEndian(&Fsd->InstReg.A_Amps);
   FltLittleEndian(&Fsd->InstReg.B_Amps);
   FltLittleEndian(&Fsd->InstReg.C_Amps);


}

VOID SchlDoDataDisplay(FulcrumScanData_t *Fsd)
{
   printf("Meter ID          :   %.9s\n",Fsd->MeterId);
   printf("Unit Type         :   %.3s\n",Fsd->UnitType);
   printf("Unit  ID          :   %.8s\n",Fsd->UnitId);

   printf("Register Multiplier:  %.5f\n",Fsd->RegisterMultiplier);
   printf("Phase Outages     :  %d\n",Fsd->PhaseOutageCount);

   printf("Time              :   %02d:%02d:%02d\n",Fsd->TimeDate.Hours, Fsd->TimeDate.Minutes, Fsd->TimeDate.Seconds);
   printf("Date              :   %02d/%02d/%02d\n",Fsd->TimeDate.Month, Fsd->TimeDate.DayOfMonth, Fsd->TimeDate.Year);

}

ULONG SchlPreviousMassMemoryAddress( ULONG SA,     // Starting Address
                                     ULONG CA,     // Current Address
                                     ULONG MA,     // Maximum Address
                                     ULONG RS)     // Record Size
{
   ULONG PA;

   PA = CA - RS;

   if(PA < SA || CA < RS)
   {
      PA = MA;
   }

   return PA;
}



/* Routine to output Forced scan to the meter */
INT SchlPostScan  ( OUTMESS *OutMessage,
                    INMESS  *InMessage,
                    CtiDeviceBase  *DeviceRecord)            /* Priority to place command on queue */

{
   FulcrumLoadProfileMessage_t   *FulcrumLProfile     = (FulcrumLoadProfileMessage_t*)&InMessage->Buffer.DUPSt.DUPRep.Message;
   time_t                        TimeRequested = 0L;

   if(OutMessage->Buffer.DUPReq.Command[1] == SCHL_GET_LOADPROFILE)
   {
      TimeRequested = OutMessage->Buffer.DUPReq.LP_Time;
   }
   else
   {
      TimeRequested = FulcrumLProfile->NextRecordTime;
   }

   FulcrumLProfile->NextRecordTime = 0L;
   FulcrumLProfile->RecordTime     = 0L;

   if(TimeRequested)
   {
      /* Re-establish the OutMessage and requeue it. */
      OutMessage->Buffer.DUPReq.LP_Time = TimeRequested;

      OutMessage->Buffer.DUPReq.Identity = IDENT_SCHLUMBERGER;

      /* Load the read last interval message */
      OutMessage->Buffer.DUPReq.Command[0] = 'L';      // One call does it all...
      OutMessage->Buffer.DUPReq.Command[1] = 0;
      OutMessage->Buffer.DUPReq.Request[3] = SCHL_FUNC_SIMPLE_REQUEST;
      /* Load all the other stuff that is needed */

      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;

      /* Queue the post message to porter */
      if(WriteQueue (PortQueueHandle[DeviceRecord->getPortID()],
                     OutMessage->EventCode,
                     sizeof (*OutMessage),
                     (char *) OutMessage,
                     OutMessage->Priority))
      {
         printf ("Error Writing Post Scan Message into Queue\n");
         return(!NORMAL);
      }

      return(NORMAL);

   }

   return(!NORMAL);
}
