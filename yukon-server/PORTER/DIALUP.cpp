#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   DIALUP
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/DIALUP.cpp-arc  $
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
#include "dupreq.h"

#include "port_base.h"

char *CTI_DIALUP_ERR[] =
{
   "Success",
   "No Data received",
   "Computed CRC does not match transmitted CRC",
   "Invalid or Unknown Alpha Class designation"
};



INT     DialupHandShake  (OUTMESS   *OutMessage,
                          INMESS    *InMessage,
                          CtiPort   *PortRecord,
                          CtiDeviceBase *RemoteRecord)
{

   switch(RemoteRecord->getType())
   {
   case TYPE_ALPHA:
      {
         // printf("Alpha HandShake request\n");
         // APlusHandShake(OutMessage, InMessage, PortRecord, RemoteRecord);

         break;
      }
   case TYPE_FULCRUM:
   case TYPE_VECTRON:
      {
         // printf("Schlumberger HandShake request\n");
         // SchlHandShake(OutMessage, InMessage, PortRecord, RemoteRecord);
         break;
      }
   default:
      printf("Error, unknown \"Identity\" in OutMessage = 0x%04X\n", OutMessage->Buffer.DUPReq.Identity);
      return !NORMAL;
   }
   return NORMAL;
}

INT     DialupPostScan  (OUTMESS *OutMessage,
                         INMESS  *InMessage,
                         CtiDeviceBase  *DeviceRecord)
{
   INT   iRet = NORMAL;

   switch(DeviceRecord->getType())
   {
   case TYPE_FULCRUM:
   case TYPE_VECTRON:
      {
         // iRet = SchlPostScan(OutMessage, InMessage, DeviceRecord);
         break;
      }
   case TYPE_ALPHA:
   default:
      printf("Error, unknown \"Identity\" in OutMessage = 0x%04X\n", OutMessage->Buffer.DUPReq.Identity);
      iRet = !NORMAL;
   }
   return iRet;
}


#ifdef OLD_STUFF
/******************************************************************************
 *
 * DialupSendAndReceive()
 *
 * 1.  Adds the CRC to the Out buffer
 * 2.  Writes it out to the port.
 * 3.  Reads in InExpect characters, or times out.
 * 4.  Checks incoming CRC
 *
 * Returns: NORMAL if no errors occur in this process, Error codes for variious
 *           failures may be added in the future.
 *
 *****************************************************************************/
INT   DialupSendAndReceive( CTIXMIT *Xfer, REQUESTRECORD *ReqRecord)
{
   INT         retval = NORMAL;
   BYTEUSHORT  CRC;
   ULONG       ExtraTimeout = Xfer->InTimeout;
   INT         Ident = ReqRecord->OMessage->Buffer.DUPReq.Identity;

   if(Xfer->Flags & XFER_ADD_CRC)
   {
      CRC.sh = CCITT16CRC(Ident, Xfer->OutBuffer, Xfer->OutCount, TRUE); // CRC func appends the CRC data
      Xfer->OutCount += 2;
   }

   if(!Xfer->OutBuffer ||
      !Xfer->InBuffer ||
      !ReqRecord->PRecord ||
      !ReqRecord->RRecord )
   {
      printf("%s:\n\n",__FILE__);
      printf("OK, printing out the vlaues:\n");
      printf("\t OutBuffer 0x%08X\n", Xfer->OutBuffer);
      printf("\t InBuffer  0x%08X\n", Xfer->InBuffer);
      printf("\t PRecord   0x%08X\n", ReqRecord->PRecord);
      printf("\t RRecord   0x%08X\n", ReqRecord->RRecord);
      return !NORMAL;
   }


   if (OutMess (Xfer->OutBuffer,
                (USHORT)Xfer->OutCount,
                ReqRecord->PRecord,
                ReqRecord->RRecord))
   {
      // if(TraceFlag) printf("Error in OutMess in %s\n",__FILE__);
      return !NORMAL;
   }

   Xfer->InTimeout =  (ULONG)(((10.0L * Xfer->InCountExpected) / ReqRecord->PRecord->getBaudRate()) + 0.5);

   if(ExtraTimeout > 0)
      Xfer->InTimeout += ExtraTimeout;


   if(Xfer->InTimeout < 1)
      Xfer->InTimeout = 2; // This is a Schlumberger Requirement
   else
   {
      /*
      this is our gray area cleanup  DLS

      It appears that our calculation of the number of seconds to retrieve Expected bytes
      is too close to exact.  We need to add 1 second to each retrieval for sanity.  Shouldn't
      affect performance as the read function will return as soon as it has read the appropriate
      number of bytes
      */
      Xfer->InTimeout += 1;
   }

   retval = InMess ( Xfer->InBuffer,
                  (USHORT)Xfer->InCountExpected,
                  ReqRecord->PRecord,
                  ReqRecord->RRecord,
                  (USHORT)Xfer->InTimeout,
                  Xfer->InCountReceived,
                  0,
                  TRUE);

   if(retval == READTIMEOUT)
   {
      retval = DIALUP_ERR_RDTIMEOUT;
   }
   else if( retval )
   {
      // if(TraceFlag) printf("Error in InMess in %s\n",__FILE__);
      return !NORMAL;
   }


   if(*(Xfer->InCountReceived) == 0)                         // We got bupkis
   {
      retval =  DIALUP_ERR_NODATA;
   }
   else if(
            (Xfer->Flags & XFER_VERIFY_CRC) &&
            CheckCCITT16CRC(Ident, Xfer->InBuffer, *(Xfer->InCountReceived))
          )    // CRC check failed.
   {
      if(TraceFlag) printf("%s : CRC Checking Error, Ident = %d\n",__FILE__, Ident);
      retval = DIALUP_ERR_BADCRC;
   }

   return retval;
}

/******************************************************************************
 *
 * DialupSend()
 *
 * 1.  Optionally adds the CRC to the Out buffer
 * 2.  Writes it out to the port.
 *
 * Returns: NORMAL if no errors occur in this process, Error codes for variious
 *           failures may be added in the future.
 *
 *****************************************************************************/
INT   DialupSend(CTIXMIT *Xfer, REQUESTRECORD *ReqRecord)
{
   BYTEUSHORT  CRC;
   INT         Ident = ReqRecord->OMessage->Buffer.DUPReq.Identity;

   if(Xfer->Flags & XFER_ADD_CRC)
   {
      CRC.sh = CCITT16CRC(Ident, Xfer->OutBuffer, Xfer->OutCount, TRUE); // CRC func appends the CRC data to the Buffer at char #6,7.
      Xfer->OutCount += 2;
   }

   if(!Xfer->OutBuffer     ||
      !ReqRecord->PRecord  ||
      !ReqRecord->RRecord  ||
      !ReqRecord->DRecord)
   {
      printf("%s:\n\n",__FILE__);
      printf("OK, printing out the vlaues:\n");
      printf("\t OutBuffer 0x%08X\n", Xfer->OutBuffer);
      printf("\t PRecord   0x%08X\n", ReqRecord->PRecord);
      printf("\t RRecord   0x%08X\n", ReqRecord->RRecord);
      printf("\t DRecord   0x%08X\n", ReqRecord->DRecord);
      return !NORMAL;
   }


   if (OutMess (Xfer->OutBuffer,
                (USHORT)Xfer->OutCount,           // If CRC was added, this is two bytes larger.
                ReqRecord->PRecord,
                ReqRecord->RRecord))
   {
      // if(TraceFlag) printf("Error in OutMess in %s\n",__FILE__);
      return !NORMAL;
   }


   return NORMAL;
}

/******************************************************************************
 *
 * DialupReceive()
 *
 * 3.  Reads in InExpect characters, or times out.
 *
 * Returns: NORMAL if no errors occur in this process, Error codes for variious
 *           failures may be added in the future.
 *
 *****************************************************************************/
INT   DialupReceive(CTIXMIT *Xfer, REQUESTRECORD *ReqRecord)
{
   ULONG ExtraTimeout = Xfer->InTimeout;
   INT         Ident = ReqRecord->OMessage->Buffer.DUPReq.Identity;

   Xfer->InTimeout = (10L * Xfer->InCountExpected) / (ULONG) ReqRecord->PRecord->getBaudRate();

   if(ExtraTimeout > 0)
      Xfer->InTimeout += ExtraTimeout;


   if(Xfer->InTimeout < 1) Xfer->InTimeout = 1;

   if( InMess (Xfer->InBuffer,
               (USHORT)Xfer->InCountExpected,       // Always get the next 8 for a class read.
               ReqRecord->PRecord,
               ReqRecord->RRecord,
               (USHORT)Xfer->InTimeout,
               Xfer->InCountReceived,
               0,
               TRUE))
   {
      // if(TraceFlag) printf("Error in InMess portion of %s\n",__FILE__);
      return !NORMAL;
   }

   if(*Xfer->InCountReceived == 0)                         // We got bupkis
   {
      return DIALUP_ERR_NODATA;
   }

   if((Xfer->Flags & XFER_VERIFY_CRC) && CheckCCITT16CRC(Ident, Xfer->InBuffer, *Xfer->InCountReceived))    // CRC check failed.
   {
      if(TraceFlag) printf("%s : CRC Checking Error\n", __FILE__);
      // DumpHexString(InBuffer, *InCount);
      return DIALUP_ERR_BADCRC;
   }


   return NORMAL;
}

#endif
