/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        MASTER.C

    Purpose:
        Routines To Build and decode the various mastercom messages

    The following procedures are contained in this module:
        MasterHeader                PreMaster
        PostMaster                  MasterReply

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO


   -------------------------------------------------------------------- */
#include "precompiled.h"

// These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>

#include "cti_asmc.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "master.h"
//#include "porter.h"


/* Routine to build the mastercomm header */
YukonError_t MasterHeader (PBYTE Message, USHORT Remote, USHORT Command, USHORT Length)
{
   /* Start Byte */
   Message[0] = MASTERHEADER;

   /* Address Byte */
   if(Remote != RTUGLOBAL)
      Message[1] = (UCHAR)Remote;
   else
      Message[1] = (UCHAR)MASTERGLOBAL;

   /* Command Byte */
   Message[2] = (UCHAR)Command;

   /* Length Byte */
   Message[3] = (UCHAR)Length;

   return ClientErrors::None;

}


/* Routine to tack CRC onto a Mastercom message */
int PreMaster (PBYTE Message, USHORT Length)
{
   USHORT CRC;

   CRC = SCrcCalc_C (Message, Length);

   Message[Length]     = LOBYTE (CRC);
   Message[Length + 1] = HIBYTE (CRC);

   return ClientErrors::None;
}


/* Routine to check out the first 4 bytes of a mastercom reply */

YukonError_t PostMaster (PBYTE  Message, USHORT Remote, PULONG   Length)
{
   if(Message[0] != 0x01)
   {
      return ClientErrors::Framing;
   }

   if(Message[1] != Remote)
   {
      return ClientErrors::BadCcu;
   }

   *Length = Message[3];

   return ClientErrors::None;
}


/* Routine to check the CRC on the master reply message */
YukonError_t MasterReply (PBYTE Message,
             USHORT Length)

{
   USHORT CRC;

   CRC = SCrcCalc_C (Message, Length - 2);

   if(LOBYTE (CRC) != Message[Length - 2])
   {
      return ClientErrors::BadCrc;
   }

   if(HIBYTE (CRC) != Message[Length - 1])
   {
      return ClientErrors::BadCrc;
   }

   return ClientErrors::None;

}
