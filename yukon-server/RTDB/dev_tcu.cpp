/*-----------------------------------------------------------------------------*
*
* File:   dev_tcu
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tcu.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/03/11 17:27:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>

#include "cmdparse.h"
#include "cparms.h"
#include "dsm2.h"
#include "porter.h"

#include "yukon.h"
#include "pt_base.h"
#include "master.h"

#include "dev_tcu.h"
#include "device.h"
#include "connection.h"

#include "logger.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "utility.h"

#define DEBUG_PRINT_DECODE 0
//extern CtiConnection VanGoghConnection;

static char StatusPointNames50[][40] = {
   {"Local Operation   "},
   {"Alarm State       "},
   {"Busy              "},
   {"Reserved Point    "},
   {"Message Verify    "},
   {"Confidence Fail   "},
   {"Transmitter Alarm "},
   {"Power reset       "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "}
};

static char StatusPointNames55[][40] = {
   {"Local Operation   "},
   {"Alarm State       "},
   {"Queue Full        "},
   {"COP Reset         "},
   {"Message Verify    "},
   {"Reserved Point    "},
   {"Transmitter Alarm "},
   {"Power reset       "},
   {"Clock Unlock      "},
   {"Clock Fail        "},
   {"VHF Channel Busy  "},
   {"VHF Trx Inhibit   "},
   {"Paging Term. Fail "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "},
   {"Reserved Point    "}
};


INT CtiDeviceTCU::GeneralScan(CtiRequestMsg *pReq,
                              CtiCommandParser &parse,
                              OUTMESS *&OutMessage,
                              RWTPtrSlist< CtiMessage > &vgList,
                              RWTPtrSlist< CtiMessage > &retList,
                              RWTPtrSlist< OUTMESS > &outList,
                              INT ScanPriority)
{
   INT status = NORMAL;

   if(OutMessage != NULL)
   {
      setScanPending();

      EstablishOutMessagePriority( OutMessage, ScanPriority );
      status = TCUScanAll(OutMessage);

      // Put the single built up OUTMESS into the Slist
      outList.insert(OutMessage);
      OutMessage = NULL;
   }
   else
   {
      status = MEMORY;
   }

   return status;
}

INT CtiDeviceTCU::IntegrityScan(CtiRequestMsg *pReq,
                                CtiCommandParser &parse,
                                OUTMESS *&OutMessage,
                                RWTPtrSlist< CtiMessage > &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist< OUTMESS > &outList,
                                INT ScanPriority)
{
   return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


INT CtiDeviceTCU::ResultDecode(INMESS *InMessage,
                               RWTime &TimeNow,
                               RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{
#if 0
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Result decode for device " << getName() << " in progress " << endl;
   }
#endif
   return(TCUDecode(InMessage, TimeNow, retList));
};


/* Routine to scan internal TCU status */
INT CtiDeviceTCU::TCUScanAll (OUTMESS* OutMessage)            /* Priority to place command on queue */
{
   ULONG       i;
   ULONG       BytesWritten;

   /* Load the forced scan message */
   if((i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANINT, 0)) != NORMAL)
      return(i);

   /* Load all the other stuff that is needed */
   OutMessage->DeviceID        = getID();
   OutMessage->Port            = getPortID();
   OutMessage->Remote          = getAddress();
   OutMessage->TimeOut         = 2;
   OutMessage->OutLength       = 4;
   OutMessage->InLength        = -1;
   OutMessage->EventCode       = RESULT | ENCODED;
   OutMessage->Sequence        = 0;
   OutMessage->Retry           = 2;

   return(NORMAL);
}

/* Routine to decode returned TCU message and update database */
INT CtiDeviceTCU::TCUDecode (INMESS *InMessage, RWTime &ScanTime, RWTPtrSlist< CtiMessage > &retList)
{
   /* Misc. definitions */
   ULONG i;
   char temp[80];


   BOOL  bNoPointsFound = TRUE;
   /* Define the various records */
   CtiPoint *PointRecord;

   /* Variables for decoding TCU Messages */
   USHORT TCUStatus;
   FLOAT PValue;

   CtiPointDataMsg   *pData    = NULL;

   /* decode whatever message this is */
   switch(InMessage->Buffer.InMessage[2])
   {
   case MASTERFREEZE:
   case MASTERSCANALL:
   case MASTERRESET:
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Unsupported MasterCom Request to TCU " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
      /* TCU is unaware of these */
      break;

   case MASTERSCANINT:
      {
         if(useScanFlags())
         {
            if(isScanPending())
            {
               resetScanPending();

               CtiMessage *Msg = TCUDecodeStatus(InMessage);

               if(*Msg == NULL)
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " " << getName() << " scanned but has no points in the DB" << endl;
               }
               else
               {
                  retList.insert( Msg );
               }
            }
            else
            {
               dout << RWTime() << " TCU response unexpected.. " << __FILE__ << " (" << __LINE__ << ")" << endl;

               /* Something screwed up message goes here */
               resetScanPending();
            }
         }
         else
         {
            CtiMessage *Msg = TCUDecodeStatus(InMessage);

            if(Msg != NULL)
            {
               // Msg->dump();
               retList.insert(Msg);
            }
         }
         break;
      }
   case MASTERLOOPBACK:
      {
         CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                  RWCString(InMessage->Return.CommandStr),
                                                  RWCString(getName() + " / successful ping"),
                                                  InMessage->EventCode & 0x7fff,
                                                  InMessage->Return.RouteID,
                                                  InMessage->Return.MacroOffset,
                                                  InMessage->Return.Attempt,
                                                  InMessage->Return.TrxID,
                                                  InMessage->Return.UserID);


         if(pLoop != NULL)
         {
            retList.insert(pLoop);
         }

         break;
      }
   default:
      /* This should never happen so reset the scan */
      dout << "This is peculiar " << __FILE__ << " (" << __LINE__ << ")" << endl;
      if(useScanFlags())
      {
         resetScanFlags();
         setScanStarting();
      }

      break;
   }   /* End of switch */

   return(NORMAL);
}



INT CtiDeviceTCU::ExecuteRequest(CtiRequestMsg                  *pReq,
                                 CtiCommandParser               &parse,
                                 OUTMESS                        *&OutMessage,
                                 RWTPtrSlist< CtiMessage >      &vgList,
                                 RWTPtrSlist< CtiMessage >      &retList,
                                 RWTPtrSlist< OUTMESS >         &outList)
{
   INT nRet = NORMAL;
   /*
    *  This method should only be called by the dev_base method
    *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
    *   (NOTE THE DIFFERENCE IN ARGS)
    *   That method prepares an outmessage for submission to the internals..
    */

   switch(parse.getCommand())
   {
   case ControlRequest:
      {

         VSTRUCT VSt;

         // Get a control request done maybe?
         if((nRet = TCUControl(OutMessage, &VSt)) != 0)
         {
            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                           pReq->getSOE(),
                                           getDescription(parse),
                                           RWCString("Control Request for TCU failed"),
                                           LoadMgmtLogType,
                                           SignalEvent,
                                           pReq->getUser()));

            CtiReturnMsg* eMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                  RWCString(OutMessage->Request.CommandStr),
                                                  RWCString("Control Request for TCU failed"),
                                                  nRet,
                                                  OutMessage->Request.RouteID,
                                                  OutMessage->Request.MacroOffset,
                                                  OutMessage->Request.Attempt,
                                                  OutMessage->Request.TrxID,
                                                  OutMessage->Request.UserID,
                                                  OutMessage->Request.SOE,
                                                  RWOrdered());

            retList.insert( eMsg );

            if(OutMessage)                // And get rid of our memory....
            {
               delete OutMessage;
               OutMessage = NULL;
            }
         }
         else
         {
            outList.insert( OutMessage );
            OutMessage = NULL;
         }

         break;
      }
   case GetStatusRequest:
      {
         // Get a scan done maybe?
         if((nRet = TCUScanAll(OutMessage)) != 0)
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " error scanning " << getName()<< endl;
            }

            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                           pReq->getSOE(),
                                           getDescription(parse),
                                           RWCString("Scan All Request for TCU failed"),
                                           GeneralLogType,
                                           SignalEvent,
                                           pReq->getUser()));

            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                             RWCString(OutMessage->Request.CommandStr),
                                             RWCString("Scan All Request for TCU failed"),
                                             nRet,
                                             OutMessage->Request.RouteID,
                                             OutMessage->Request.MacroOffset,
                                             OutMessage->Request.Attempt,
                                             OutMessage->Request.TrxID,
                                             OutMessage->Request.UserID,
                                             OutMessage->Request.SOE,
                                             RWOrdered()));

            if(OutMessage)                // And get rid of our memory....
            {
               delete OutMessage;
               OutMessage = NULL;
            }
         }
         else
         {
            outList.insert( OutMessage );
            OutMessage = NULL;
         }

         break;
      }
   case LoopbackRequest:
      {
         int cnt = parse.getiValue("count");

         for(int i = 0; i < cnt; i++)
         {
            OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);

            if(OutMTemp != NULL)
            {
               // Get a scan done maybe?
               if((nRet = TCULoop(OutMTemp)) != 0)
               {
                  {
                     CtiLockGuard<CtiLogger> doubt_guard(dout);
                     dout << RWTime() << " error looping " << getName()<< endl;
                  }
                  retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                   RWCString(OutMessage->Request.CommandStr),
                                                   RWCString(getName() + " / ping failed to TCU"),
                                                   nRet,
                                                   OutMTemp->Request.RouteID,
                                                   OutMTemp->Request.MacroOffset,
                                                   OutMTemp->Request.Attempt,
                                                   OutMTemp->Request.TrxID,
                                                   OutMTemp->Request.UserID,
                                                   OutMTemp->Request.SOE,
                                                   RWOrdered()));
               }
               else
               {
                  outList.insert( OutMTemp );
               }
            }
         }

         break;
      }
   case GetValueRequest:
   case PutValueRequest:
   case PutStatusRequest:
   case GetConfigRequest:
   case PutConfigRequest:
   default:
      {
         nRet = NoExecuteRequestMethod;
         /* Set the error value in the base class. */
         // FIX FIX FIX 092999
         retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                          RWCString(OutMessage->Request.CommandStr),
                                          RWCString("TCU Devices do not support this command (yet?)"),
                                          nRet,
                                          OutMessage->Request.RouteID,
                                          OutMessage->Request.MacroOffset,
                                          OutMessage->Request.Attempt,
                                          OutMessage->Request.TrxID,
                                          OutMessage->Request.UserID,
                                          OutMessage->Request.SOE,
                                          RWOrdered()));

         break;
      }
   }


   return nRet;
}

/* Routine to execute a versacom message */
INT CtiDeviceTCU::TCUControl(OUTMESS* OutMessage, VSTRUCT *VSt)
{
   /* Load up the pieces of the structure */
   OutMessage->DeviceID                = getID();
   OutMessage->Port                    = getPortID();
   OutMessage->Remote                  = getAddress();
   OutMessage->Retry                   = 2; //VSt->Retry;
   OutMessage->EventCode               = VERSACOM | NOWAIT | NORESULT | QUEUED;
   OutMessage->Sequence                = 0;
   OutMessage->ReturnNexus             = NULL;
   OutMessage->SaveNexus               = NULL;

   OutMessage->Buffer.VSt = *VSt;

   return(CtiInvalidRequest);
}

/* Routine to execute a loop message */
INT CtiDeviceTCU::TCULoop(OUTMESS* OutMessage)
{
   /* Build a mastercom loopback request */
   MasterHeader (OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERLOOPBACK, 0);

   OutMessage->TimeOut = 2;
   OutMessage->OutLength = MASTERLENGTH;
   OutMessage->InLength = -1;
   OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

   /* Load up the pieces of the structure */
   OutMessage->DeviceID                = getID();
   OutMessage->Port                    = getPortID();
   OutMessage->Remote                  = getAddress();
   OutMessage->Retry                   = 0; //VSt->Retry;
   OutMessage->Sequence                = 0;
   OutMessage->ReturnNexus             = NULL;
   OutMessage->SaveNexus               = NULL;


   return(NORMAL);
}


CtiReturnMsg* CtiDeviceTCU::TCUDecodeStatus(INMESS *InMessage)
{
   ULONG       i;
   char        temp[80];

   /* Define the various records */
   CtiPoint          *PointRecord;

   /* Variables for decoding TCU Messages */
   USHORT            TCUStatus;
   FLOAT             PValue;

   CtiPointDataMsg   *pData    = NULL;

   CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                           RWCString(InMessage->Return.CommandStr),
                                           RWCString("TCU status request complete"),
                                           InMessage->EventCode & 0x7fff,
                                           InMessage->Return.RouteID,
                                           InMessage->Return.MacroOffset,
                                           InMessage->Return.Attempt,
                                           InMessage->Return.TrxID,
                                           InMessage->Return.UserID);

   /* update the scan time */
   // FIX FIX FIX 091499 CGP ?????  DeviceRecord->LastFullScan = TimeB->time;

   /* Rebuild the status word */
   TCUStatus = MAKEUSHORT (InMessage->Buffer.InMessage[4], InMessage->Buffer.InMessage[5]);

   /* Now loop through and update remote processes as needed */
   for(i = 1; i <= 16; i++)
   {
      if((PointRecord = getDevicePointOffsetTypeEqual(i + 16, StatusPointType)) != NULL)
      {
         PValue = ((TCUStatus >> (i - 1)) & 0x0001) ? CLOSED : OPENED;

         if(getType() == TYPE_TCU5000)
         {
            sprintf(temp,"Offset %2d: %s is %s", i + 16, StatusPointNames50[i - 1], (PValue == CLOSED ? "CLOSED" : "OPENED"));
         }
         else if(getType() == TYPE_TCU5500)
         {
            sprintf(temp,"Offset %2d: %s is %s", i + 16, StatusPointNames55[i - 1], (PValue == CLOSED ? "CLOSED" : "OPENED"));
         }
         else
         {
            sprintf(temp,"Error %s (%d)", __FILE__, __LINE__);
         }

#if 0
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << temp << endl;
         }
#endif

         pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(),
                                     PValue,
                                     NormalQuality,
                                     StatusPointType,
                                     temp);
         if(pData != NULL)
         {
            if(isScanException())
            {
               pData->setExemptionStatus(TRUE);                 // May be short circuited!
            }

            if(pPIL != NULL)
            {
               pPIL->PointData().insert(pData);
               pData = NULL;  // We just put it on the list...
            }
            else
            {
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
               delete pData;
               pData = NULL;
            }
         }
      }
   }

   if(pPIL->PointData().entries() == 0)
   {
      pPIL->setResultString("Communication Successful.  TCU has no DB defined points.");
   }

   return pPIL;
}

CtiDeviceTCU& CtiDeviceTCU::setSendFiller(bool yesno)
{
   _sendFiller = yesno;
   return *this;
}
bool CtiDeviceTCU::getSendFiller() const
{
   return _sendFiller;
}

CtiDeviceTCU::CtiDeviceTCU() : _sendFiller(false) {}

CtiDeviceTCU::CtiDeviceTCU(const CtiDeviceTCU& aRef)
{
   *this = aRef;
}

CtiDeviceTCU::~CtiDeviceTCU()
{
}

CtiDeviceTCU& CtiDeviceTCU::operator=(const CtiDeviceTCU& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      setSendFiller( aRef.getSendFiller() );
   }
   return *this;
}

INT CtiDeviceTCU::getProtocolWrap() const
{
    INT protocol = ProtocolWrapIDLC;

    if(gConfigParms.isOpt("TCU_PROTOCOLWRAP"))
    {
        if( !gConfigParms.getValueAsString("TCU_PROTOCOLWRAP").compareTo("mastercom", RWCString::ignoreCase) ||
            !gConfigParms.getValueAsString("TCU_PROTOCOLWRAP").compareTo("none", RWCString::ignoreCase) )
        {
            protocol = ProtocolWrapNone;
        }
    }

    return protocol;
}

