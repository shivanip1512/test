#include "precompiled.h"

#include <process.h>
#include <iostream>

#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
#include <memory.h>

#include "queues.h"
#include "dsm2.h"
#include "porter.h"
#include "portglob.h"
#include "dev_base.h"
#include "trx_info.h"
#include "logger.h"
#include "guard.h"
#include "cparms.h"

using namespace std;

/* Routine to generate preamble for general IDLC message */
INT PreIDLC (PBYTE   Message,        /* resulting command string */
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

   return ClientErrors::None;
}


/* RTU IDLC preamble builder */
INT PreUnSequenced (PBYTE  Message,        /* resulting command string */
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
      Message[4] |= 0x01;       // This is FIN.  Thanks Bill Crouch.
      if(Device->getTrxInfo()->RemoteSequence.Reply)
      {
         Message[4] |= 0x02;
      }
      else
      {
         Message[4] &= ~0x02;
      }

      break;

   default:
      Message[6] = (UCHAR)Length;
      break;
   }

   return ClientErrors::None;
}


/* routine to place CRC at passed location in generated IDLC command */
INT PostIDLC (PBYTE    Message,       /* message and result */
          USHORT   Length)        /* length of the message */

{
   USHORT i;

   i = NCrcCalc_C((Message+1), Length-1);

   /* park it */
   Message[Length] = HIBYTE(i);
   Message[Length+1] = LOBYTE(i);

   return ClientErrors::None;
}


/* Routine to check for valid response from a IDLC CCU */

YukonError_t GenReply (PBYTE Reply,            /* reply message */
              USHORT  Length,         /* reply message length */
              PUSHORT ReqNum,         /* request number */
              PUSHORT RepNum,         /* reply number */
              USHORT RemoteAddress,
              USHORT Command,
              bool* sequencingBroken)

{
   USHORT Save;

   Save = Reply[Length-2] << 8 | Reply[Length-1];

   if(Save != NCrcCalc_C ((Reply+1), Length-3))
   {
      return ClientErrors::BadCrc;
   }

   if(RemoteAddress != (Reply[1] >> 1))
   {
      CTILOG_WARN(dout, "Address mismatch, possible extended address - RemoteAddress = " << RemoteAddress << ", Reply[1] = " << Reply[1]);
      return ClientErrors::BadCcu;
   }

   *ReqNum = (Reply[2] >> 5) & 0x07;

   int seqNum = (Reply[2] >> 1) & 0x07;
   const bool slaveSequenceAdjusted = (Reply[6] & STAT_NSADJ);
   const bool sequencesMatch = (seqNum == *RepNum);

   if(*sequencingBroken)
   {
       if(sequencesMatch && slaveSequenceAdjusted)
       {
           /* Sequencing was adjusted because we lost a message.
              We're back on track because sequences match and the
              slave confirmed the adjustment. */
           *sequencingBroken = false;
       }
       else
       {
           return ClientErrors::Framing;
       }
   }
   else if(slaveSequenceAdjusted)
   {
       *RepNum = seqNum;
   }
   else if(!sequencesMatch)
   {
       return ClientErrors::Framing;
   }

   if(++(*RepNum) >= 8)
   {
      *RepNum = 0;
   }

   /* check for a reqack */
   if(Reply[6] & STAT_REQACK)
   {
      return ClientErrors::ReqackFlagSet;
   }

   /* Make sure the command was echoed back properly */
   if((Reply[5] & 0x7f) != Command)
   {
      /* We didn't get back the message we were expecting,
         probably due to an earlier timeout - try again */
      return ClientErrors::ReadTimeout;
   }

   return ClientErrors::None;
}


/* Routine to check for valid response from a IDLC RTU */
YukonError_t RTUReply (PBYTE Reply, USHORT Length)

{
   USHORT Save;

   Save = Reply[Length-2] << 8 | Reply[Length-1];

   /* check the crc */
   if(Save != NCrcCalc_C ((Reply+1), Length-3))
   {
      return ClientErrors::BadCrc;
   }

   return ClientErrors::None;
}


/* function gets length from unsequeced rtu frame */
YukonError_t RTUReplyHeader (USHORT Type, USHORT RemoteAddress, PBYTE  Message, PULONG ReadLength)
{
   USHORT Length1, Length2;

   if(RemoteAddress != (Message[1] >> 1))
   {
      CTILOG_WARN(dout, "Address mismatch, possible extended address - RemoteAddress = " << RemoteAddress << ", Message[1] = " << Message[1]);
      return ClientErrors::BadCcu;
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
      {
         return ClientErrors::Framing;
      }

      *ReadLength = Length2 + 2;
   }

   return ClientErrors::None;

}


/* Routine to test for an idlc reject frame */
YukonError_t IDLCRej (PBYTE Reply, PUSHORT ReqNum)
{
   USHORT Save;

   Save = Reply[3] << 8 | Reply[4];

   /* check the crc */
   if(Save != NCrcCalc_C((Reply+1),2))
      return ClientErrors::None;

   /* check for a reject frame */
//   if ((Reply[2] & 0x1f) != REJ)
   /* Softened reject detect code */
   if((Reply[2] & 0x0f) != REJ)
      return ClientErrors::None;

/* if we have one get us back into sequence */
   if(ReqNum != NULL)
   {
      *ReqNum = Reply[2] >> 5 & 0x07;
   }

   return ClientErrors::BadSequence;
}


/* Routine to generate an idlc reset command for a given ccu */
INT IDLCSArm (PBYTE Message, USHORT Remote)
{
   /* framing */
   Message[0] = 0x7e;

   /* address */
   Message[1] = Remote << 1 | 0x01;

   /* command */
   Message[2] = RESET_REQ;

   /* crc */
   PostIDLC (Message, 3);

   return ClientErrors::None;
}


/* function to check for a correct reset acknowledge and reset framing */

YukonError_t IDLCua (PBYTE Response, PUSHORT ReqNum, PUSHORT RepNum)
{
   USHORT Store;

   Store = Response[3] << 8 | Response[4];

   /* check crc */
   if(Store != NCrcCalc_C((Response + 1), 2))
   {
      return ClientErrors::BadHdlcUaFrame;
   }

   /* check for reset acknowledge */
   if(Response[2] == RESET_ACK)
   {
      *ReqNum = 0;
      *RepNum = 0;
      return ClientErrors::None;
   }

   return ClientErrors::BadHdlcUaFrame;
}


/* Routine to decode the Algorithm status part of the message */
INT IDLCAlgStat (PBYTE Message, PUSHORT Status)
{
    int retval = ClientErrors::None;

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
        retval = ClientErrors::Abnormal;
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

        retval = ClientErrors::Abnormal;
    }

    return retval;
}
