/*-----------------------------------------------------------------------------*
*
* File:   scansup
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/scansup.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <float.h>

#include <rw\rwtime.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
//#include "device.h"
//#include "drp.h"
//#include "elogger.h"
//#include "dsm2err.h"
//#include "alarmlog.h"
//#include "routes.h"
//#include "queues.h"
//#include "porter.h"
//#include "lm_auto.h"
//#include "perform.h"
//#include "scanner.h"

#include "scanglob.h"
#include "scansup.h"
#include "dlldefs.h"

IM_EX_SCANSUP RWTime FirstScan(const RWTime& TimeNow, ULONG ScanRate)
{
   ULONG    SecsPastHour;
   ULONG    SecsPastDay;

   RWTime   NextScan;

   if(!(ScanRate))
   {
      return(TimeNow + 1);
   }
   else if(ScanRate == 0xFFFFFFFF)
   {
      return(0xFFFFFFFF);
   }

   if(ScanRate <= 3600)
   {
      SecsPastHour = TimeNow.seconds() % 3600L;

      if((SecsPastHour % ScanRate) == 0)
      {
         return(TimeNow);
      }

      NextScan = RWTime(TimeNow - (SecsPastHour % ScanRate) + ScanRate);
   }
   else
   {
      SecsPastDay = TimeNow.seconds() % 86400L;

      NextScan = RWTime(TimeNow - (SecsPastDay % ScanRate) + ScanRate);

      while(NextScan <= TimeNow)
      {
         NextScan += ScanRate;
      }

      cout << TimeNow << " Scanning Device at " << NextScan << endl;
   }

   return(NextScan);
}

#if 0
/* Add communications error to the comm error log */
IM_EX_SCANSUP INT ReportError (CtiDeviceBase *RemoteRecord, USHORT Error)
{

   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   ERRSTRUCT ErrorRecord;
   PORT PortRecord;
   struct timeb TimeB;

   /* get the error structure */
   ErrorRecord.Error = Error;

   getError (&ErrorRecord);

   /* get the port record */
   PortRecord.Port = RemoteRecord->Port;
   PortGetEqual (&PortRecord);


   /* Now load up the Comm error record */
   UCTFTime (&TimeB);
   ComErrorRecord.TimeStamp = TimeB.time;
   if(TimeB.dstflag)
      ComErrorRecord.StatusFlag |= DSTACTIVE;
   else
      ComErrorRecord.StatusFlag &= ~DSTACTIVE;

   memcpy (ComErrorRecord.DeviceName, RemoteRecord->RemoteName, STANDNAMLEN);
   /* Figure out what to use for a port name */
   strcpy (ComErrorRecord.RouteName, "$_");

   if(PortRecord.Description[0] != ' ')
      memcpy (ComErrorRecord.RouteName + 2, PortRecord.Description, STANDNAMLEN - 2);
   else
      memcpy (ComErrorRecord.RouteName + 2, PortRecord.PortName, STANDNAMLEN - 2);

   ComErrorLogAdd (&ComErrorRecord,
                   &ErrorRecord,
                   FALSE);

   cout << "Fix This " << __FILE__ << " (" << __LINE__ << ")" << endl;

   return(NORMAL);
}
#endif


#define SCREEN_WIDTH 80

/* Function to display an array of bytes in hexadecimal */
IM_EX_SCANSUP INT TracePrint (PBYTE Message, USHORT  Length)
{
   ULONG i;
   ULONG width = 1;

   /* loop through all of the characters */
   for(i = 0; i < Length; i++)
   {
      if(width + 2 > SCREEN_WIDTH)
      {
         /* yes so goto CTIDBG_new line */
         printf ("\n");
         width = 1;
      }

      /* print byte in hex with leading zero */
      if(width + 3 > SCREEN_WIDTH)
      {
         printf ("%02hx", Message[i]);
         width += 2;
      }
      else
      {
         printf ("%02hx ", Message[i]);
         width += 3;
      }
   }

   printf ("\n");

   return(NORMAL);
}
