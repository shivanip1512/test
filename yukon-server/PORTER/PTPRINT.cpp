/*-----------------------------------------------------------------------------*
*
* File:   PTPRINT
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PTPRINT.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Routines to print the Trace" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PTPRINT.C

    Purpose:
        Prints trace messages of porter message to remotes

    The following procedures are contained in this module:
        TraceIn                 TraceOut
        tprint                  bin

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        08-24-93    Changed to write direct instead of to pipe      WRO
        9-7-93   Converted to 32 bit                                WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>
#include <iomanip>
using namespace std;

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#include <rw\rwtime.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "color.h"

#ifdef IMPORT           // Not for portglob locally.. There has to be a better way than this, but I am in a hurry
   #undef IMPORT
#endif
#include "portglob.h"
#include "portdecl.h"

#include "logger.h"
#include "guard.h"

// #include "proclog.hpp"

DLLIMPORT extern RWMutexLock coutMux;

/* Function displays inbound message in hexadecimal */

TraceIn (PBYTE   Message,          /* message to print out in hex */
         USHORT  Length,           /* length of message to print */
         USHORT  Port,             /* port number */
         USHORT  Remote,           /* device */
         USHORT  ErrorCode)

{
   CHAR TmpBuf[128];

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);

      /* set bright yellow for the time message */
      dout << RWTime();

      /* set bright cyan for the info message */
      dout << "  Port: " << setw(2);
      dout << Port;

      /* set bright cyan for the info message */
      dout << " Remote: " << setw(3);
      dout << Remote;


      if(ErrorCode)
      {
         /* set Up for bright red */
         dout << " ERROR:  " << setw(3);
         dout << ErrorCode;
      }

      /* set up for magneta */
      dout << " IN: ";

      /* then print the formatted hex trace */
      TPrint(Message, Length);

      if(ErrorCode)
      {
         PrintError(ErrorCode);
      }

      /* Restore the screen to normal */
   }

   return(NORMAL);
}


/* Function displays outbound message in hexadecimal */

TraceOut (PBYTE   Message,          /* message to print out in hex */
          USHORT  Length,           /* length of message to print */
          USHORT  Port,             /* port number */
          USHORT  Remote)           /* device */

{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      /* set bright yellow for the time message */
      dout << RWTime();

      /* set bright cyan for the info message */
      dout << "  Port: " << setw(2);
      dout << Port;

      /* set bright cyan for the info message */
      dout << " Remote: " << setw(3);
      dout << Remote;

      /* set up for green */
      dout << " OUT: ";

      /* then print the formatted hex trace */
      TPrint(Message, Length);

      /* Restore the screen to normal */
   }

   return(NORMAL);
}


#define SCREEN_WIDTH 80

/* Function to display an array of bytes in hexadecimal */
TPrint (PBYTE Message,          /* message to print out in hex */
        USHORT  Length)           /* length of message to print */

{
   ULONG i;
   ULONG width = 1;

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   CHAR  oldFill = dout.fill();

   dout.fill('0');

   dout << endl;

   /* loop through all of the characters */
   for(i = 0; i < Length; i++)
   {
      if(width + 2 > SCREEN_WIDTH)
      {
         /* yes so goto CTIDBG_new line */
         dout << endl;
         width = 1;
      }

      /* print byte in hex with leading zero */
      if(width + 3 > SCREEN_WIDTH)
      {
         dout << right << hex << setw(2) << (INT)Message[i] << " " ;
         width += 2;
      }
      else
      {
         dout << right << hex << setw(2) << (INT)Message[i] << " " ;
         width += 3;
      }
   }

   dout << dec << endl;
   dout.fill(oldFill);

   return(NORMAL);
}


/* function to print binary values */
BinPrint (BYTE Byte)
{
   USHORT i;

   /* loop through the bits in the byte */
   for(i = 0; i < 8; i++)
   {
      /* check the present bit and print appropriatly */
      if(Byte & 0x80)
       printf ("1");
      else
         printf ("0");

      Byte <<= 1;
   }

   return(NORMAL);
}
