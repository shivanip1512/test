/*-----------------------------------------------------------------------------*
*
* File:   PLIDLC
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PLIDLC.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/10/14 19:57:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#pragma title ( "Low Level IDLC Build Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PLIDLC.C

    Purpose:
        Along with PPORT.C and PORTPORT.C routines handles level 2 IDLC stuff

    The following procedures are contained in this module:
        PreIDLC                 PreUnSequenced
        PrePPU
        PostIDLC                GenReply
        RTUReply                RTUReplyHeader
        IDLCRej                 IDLCSRej
        IDLCSArm                IDLCua
        IDLCAlgStat

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        2-2-94   Updated SECTN loads                                WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>
using namespace std;

#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
#include <memory.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portglob.h"
#include "dev_base.h"
#include "trx_info.h"
#include "logger.h"
#include "guard.h"

/* Routine to generate preamble for general IDLC message */
PreIDLC (PBYTE   Message,        /* resulting command string */
         USHORT  Length,         /* length of message to follow */
         USHORT  Remote,         /* ccu Remote */
         USHORT  Reply,          /* expected reply frame number */
         USHORT  Request,        /* sequence number of frame */
         USHORT  Last,           /* 0 = more frames else last frame */
         USHORT  Source,         /* id of source process */
         USHORT  Dest,           /* id of destination process */
         USHORT  Command)        /* command */

{
   /* flag */
   Message[0] = 0x7e;

   /* ccu address */
   Message[1] = Remote << 1 | 0x01;

   /* Control Byte */
   if(Remote == BROADCAST)
      Message[2] = HDLC_UD;
   else
   {
      /* sequence numbers */
      Message[2] = Reply << 5 | Request << 1;
      if(Last)
         Message[2] |= 0x10;
   }

   /* length */
   Message[3] = (UCHAR)Length;

   /* source and destination algorithms */
   Message[4] = Source << 6 | Dest;

   /* command */
   Message[5] = (UCHAR)Command;

   return(NORMAL);
}


/* RTU IDLC preamble builder */
PreUnSequenced (PBYTE  Message,        /* resulting command string */
                USHORT Length,         /* length of message to follow */
                USHORT Port,
                USHORT Remote,         /* ccu address */
                CtiDeviceSPtr Device)           /* device type */

{
   INT   Type     = Device->getType();

   /* flag */
   Message[0] = 0x7e;

   /* RTU address */
   if(Type == TYPE_WELCORTU)
   {
      Message[1] = Remote << 1;
   }
   else
   {
      Message[1] = Remote << 1 | 0x01;
   }

   /* control flag */
   Message[2] = HDLC_UD;

   /* length */
   Message[3] = Length + 3;

   /* ACK flag */
   if(Type != TYPE_SES92RTU && Type != TYPE_WELCORTU)
   {
      Message[4] = 0x01 | ((Message[7] >> 3) & 0x02);
   }

   /* sectn */
   switch(Type)
   {
   case TYPE_ILEXRTU:
      Message[5] = SECTN_ILEXRTU | 0x80;
      Message[6] = (UCHAR)Length;
      break;

   case TYPE_LCU415:
   case TYPE_LCU415LG:
   case TYPE_LCU415ER:
   case TYPE_LCUT3026:
   case TYPE_TCU5000:
   case TYPE_TCU5500:
      Message[5] = SECTN_MASTERCOM | 0x80;
      Message[6] = (UCHAR)Length;
      break;

   case TYPE_SES92RTU:
      Message[5] = SECTN_SES92RTU | 0X80;
      Message[6] = (UCHAR)Length;
      break;

   case TYPE_CCU700:
   case TYPE_CCU710:
      Message[5] = SECTN_CCU700 | 0X80;
      Message[6] = (UCHAR)Length;
      break;

   case TYPE_WELCORTU:
      if(Device->getTrxInfo()->RemoteSequence.Reply)
      {
         Message[4] |= 0x02;
      }
      else
      {
         Message[4] &= ~0x02;
      }

      break;

   case TYPE_VTU:
      break;

   default:
      Message[6] = (UCHAR)Length;
      break;
   }

   return(NORMAL);
}


/* PPU IDLC preamble builder */
PreVTU (PBYTE  Message,        /* resulting command string */
        USHORT Length,         /* length of message to follow */
        USHORT Remote,         /* PPU address */
        USHORT PPUPort,
        USHORT ReturnLength,
        USHORT TimeOut)

{
   /* flag */
   Message[0] = 0x7e;

   /* PPU address */
   Message[1] = Remote << 1 | 0x01;

   /* control flag */
   Message[2] = HDLC_UD;

   /* length */
   Message[3] = Length + 6;

   /* Flag */
   Message[4] = 0;

   /* sectn */
   Message[5] = SECTN_PROTWRAP | 0X80;

   /* Length Again */
   Message[6] = Length + 3;

   /* Now move the message up a few bytes */
   memmove (Message + 10, Message + 7, Length);

   /* And load the rest of the info */
   Message[7] = (UCHAR)PPUPort;

   Message[8] = (UCHAR)ReturnLength;

   Message[9] = (UCHAR)TimeOut;

   return(NORMAL);
}


/* routine to place CRC at passed location in generated IDLC command */
PostIDLC (PBYTE    Message,       /* message and result */
          USHORT   Length)        /* length of the message */

{
   USHORT i;

   i = NCrcCalc_C((Message+1), Length-1);

   /* park it */
   Message[Length] = HIBYTE(i);
   Message[Length+1] = LOBYTE(i);

   return(NORMAL);
}


/* Routine to check for valid response from a IDLC CCU */

GenReply (PBYTE Reply,            /* reply message */
          USHORT  Length,         /* reply message length */
          PUSHORT ReqNum,         /* request number */
          PUSHORT RepNum,         /* reply number */
          USHORT RemoteAddress)

{
   USHORT Save;

   Save = Reply[Length-2] << 8 | Reply[Length-1];

   if(Save != NCrcCalc_C ((Reply+1), Length-3))
   {
      return(BADCRC);
   }

   if(RemoteAddress != (Reply[1] >> 1))
   {
      cerr <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
      return(BADCCU);
   }


   *ReqNum = Reply[2] >> 5 & 0x07;

   Save = Reply[2] >> 1 & 0x07;

   /* Make sure the slave did not jack around sequencing */
   if(Reply[6] & STAT_NSADJ)
   {
      *RepNum = Save;
   }
   else
   {
      if(Save != *RepNum)
      {
         return(FRAMEERR);
      }
   }

   if(++(*RepNum) >= 8)
   {
      *RepNum = 0;
   }

   /* check for a reqack */
   if(Reply[6] & STAT_REQACK)
   {
      return(REQACK);
   }

   return(NORMAL);
}


/* Routine to check for valid response from a IDLC RTU */
RTUReply (PBYTE Reply, USHORT Length)

{
   USHORT Save;

   Save = Reply[Length-2] << 8 | Reply[Length-1];

   // CtiLockGuard<CtiLogger> doubt_guard(dout);
   // dout << "CRC Bytes : " << hex << (INT)Reply[Length-2] << (INT)Reply[Length-1] << dec << endl;

   /* check the crc */
   if(Save != NCrcCalc_C ((Reply+1), Length-3))
      return(BADCRC);

   return(NORMAL);
}


/* function gets length from unsequeced rtu frame */
RTUReplyHeader (USHORT Type, USHORT RemoteAddress, PBYTE  Message, PULONG ReadLength)
{
   USHORT Length1, Length2;

   if(RemoteAddress != (Message[1] >> 1))
   {
      cerr <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
      return(BADCCU);
   }

   Length1 = Message[3];
   Length2 = Message[6];

   if(Type == TYPE_WELCORTU)
   {
      if(Length1 == 1)
      {
         *ReadLength =  0;
      }
      else
      {
         *ReadLength = Length1 - 1;
      }
   }
   else
   {
      if(Length1 != (Length2 + 3))
         return(FRAMEERR);

      *ReadLength = Length2 + 2;
   }

   return(NORMAL);

}


/* Routine to test for an idlc reject frame */
IDLCRej (PBYTE Reply, PUSHORT ReqNum)
{
   USHORT Save;

   Save = Reply[3] << 8 | Reply[4];

   /* check the crc */
   if(Save != NCrcCalc_C((Reply+1),2))
      return(NORMAL);

   /* check for a reject frame */
//   if ((Reply[2] & 0x1f) != REJ)
   /* Softened reject detect code */
   if((Reply[2] & 0x0f) != REJ)
      return(NORMAL);

/* if we have one get us back into sequence */
   if(ReqNum != NULL)
   {
      *ReqNum = Reply[2] >> 5 & 0x07;
   }

   return(BADSEQUENCE);
}


/* Routine to generate retransmit request for idlc */
IDLCSRej (PBYTE Message,      /* resulting command */
          USHORT Remote,         /* ccu address */
          USHORT SeqNum)         /* expected sequence number */

{
   /* flag */
   Message[0] = 0x7e;

   /* ccu address */
   Message[1] = Remote << 1 | 0x01;

   /* framing and command */
   Message[2] = SeqNum << 5 | SREJ;

   /* crc */
   PostIDLC (Message, 3);

   return(NORMAL);
}


/* Routine to generate an idlc reset command for a given ccu */
IDLCSArm (PBYTE Message, USHORT Remote)
{
   /* framing */
   Message[0] = 0x7e;

   /* address */
   Message[1] = Remote << 1 | 0x01;

   /* command */
   Message[2] = RESET_REQ;

   /* crc */
   PostIDLC (Message, 3);

   return(NORMAL);
}


/* function to check for a correct reset acknowledge and reset framing */

IDLCua (PBYTE Response, PUSHORT ReqNum, PUSHORT RepNum)
{
   USHORT Store;

   Store = Response[3] << 8 | Response[4];

   /* check crc */
   if(Store != NCrcCalc_C((Response + 1), 2))
      return(BADUA);

   /* check for reset acknowledge */
   if(Response[2] == RESET_ACK)
   {
      *ReqNum = 0;
      *RepNum = 0;
      return(NORMAL);
   }

   return(BADUA);
}


/* Routine to decode the Algorithm status part of the message */
IDLCAlgStat (PBYTE Message, PUSHORT Status)
{
    int retval = NORMAL;

    USHORT i;

    Status[0] = (Message[0] >> 6) & 0x03;
    Status[1] = (Message[0] >> 4) & 0x03;
    Status[2] = (Message[0] >> 2) & 0x03;
    Status[3] = Message[0] & 0x03;
    Status[4] = (Message[1] >> 6) & 0x03;
    Status[5] = (Message[1] >> 4) & 0x03;
    Status[6] = (Message[1] >> 2) & 0x03;
    Status[7] = Message[1] & 0x03;

    if( !Message[0] && !Message[1] )
    {
        //  we have no statuses - something's wrong
        retval = NOTNORMAL;
    }
    else if( Status[0] || Status[3] || Status[6] || Status[7] )
    {
        /*
        note, we set up the algorithm statuses like so...  if anything comes
        back in 0, 3, 6, or 7, we know that they're not initialized properly

        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (2);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (4);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (5);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
        */

        retval = NOTNORMAL;
    }

    return retval;
}
