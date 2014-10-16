/*-----------------------------------------------------------------------------*
*
* File:   dev_tcu
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tcu.cpp-arc  $
* REVISION     :  $Revision: 1.17.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cmdparse.h"
#include "cparms.h"
#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "dev_tcu.h"
#include "connection.h"

#include "logger.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "utility.h"

#define DEBUG_PRINT_DECODE 0
//extern CtiConnection VanGoghConnection;

using std::string;
using std::endl;
using std::list;

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


YukonError_t CtiDeviceTCU::GeneralScan(CtiRequestMsg     *pReq,
                                       CtiCommandParser  &parse,
                                       OUTMESS          *&OutMessage,
                                       CtiMessageList    &vgList,
                                       CtiMessageList    &retList,
                                       OutMessageList    &outList,
                                       INT ScanPriority)
{
   YukonError_t status = ClientErrors::None;

   if(OutMessage != NULL)
   {
      setScanFlag(ScanRateGeneral);

      EstablishOutMessagePriority( OutMessage, ScanPriority );
      status = TCUScanAll(OutMessage);

      // Put the single built up OUTMESS into the Slist
      outList.push_back(OutMessage);
      OutMessage = NULL;
   }
   else
   {
      status = ClientErrors::MemoryAccess;
   }

   return status;
}

YukonError_t CtiDeviceTCU::IntegrityScan(CtiRequestMsg     *pReq,
                                         CtiCommandParser  &parse,
                                         OUTMESS          *&OutMessage,
                                         CtiMessageList    &vgList,
                                         CtiMessageList    &retList,
                                         OutMessageList    &outList,
                                         INT ScanPriority)
{
   return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


YukonError_t CtiDeviceTCU::ResultDecode(const INMESS   &InMessage,
                                        const CtiTime   TimeNow,
                                        CtiMessageList &vgList,
                                        CtiMessageList &retList,
                                        OutMessageList &outList)
{
   return TCUDecode(InMessage, TimeNow, retList);
}


/* Routine to scan internal TCU status */
YukonError_t CtiDeviceTCU::TCUScanAll (OUTMESS* OutMessage)            /* Priority to place command on queue */
{
   YukonError_t i;
   ULONG       BytesWritten;

   /* Load the forced scan message */
   if( i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANINT, 0) )
      return(i);

   /* Load all the other stuff that is needed */
   populateRemoteOutMessage(*OutMessage);
   OutMessage->OutLength       = 4;
   OutMessage->InLength        = -1;

   return ClientErrors::None;
}

/* Routine to decode returned TCU message and update database */
YukonError_t CtiDeviceTCU::TCUDecode (const INMESS &InMessage, const CtiTime ScanTime, CtiMessageList &retList)
{
   /* Misc. definitions */
   ULONG i;
   char temp[80];


   BOOL  bNoPointsFound = TRUE;
   /* Define the various records */
   CtiPointSPtr PointRecord;

   /* Variables for decoding TCU Messages */
   USHORT TCUStatus;
   FLOAT PValue;

   CtiPointDataMsg   *pData    = NULL;

   /* decode whatever message this is */
   switch(InMessage.Buffer.InMessage[2])
   {
   case MASTERFREEZE:
   case MASTERSCANALL:
   case MASTERRESET:
      {
          CTILOG_ERROR(dout, getName() << " Unsupported MasterCom Request to TCU");

          /* TCU is unaware of these */
          break;
      }
   case MASTERSCANINT:
      {
         if(useScanFlags())
         {
            if(isScanFlagSet(ScanRateGeneral))
            {
               resetScanFlag(ScanRateGeneral);

               CtiMessage *Msg = TCUDecodeStatus(InMessage);

               if(*Msg == NULL)
               {
                   CTILOG_ERROR(dout, getName() << " scanned but has no points in the DB");
               }
               else
               {
                  retList.push_back( Msg );
               }
            }
            else
            {
               CTILOG_ERROR(dout, "TCU response unexpected..");

               /* Something screwed up message goes here */
               resetScanFlag(ScanRateGeneral);
            }
         }
         else
         {
            CtiMessage *Msg = TCUDecodeStatus(InMessage);

            if(Msg != NULL)
            {
               retList.push_back(Msg);
            }
         }
         break;
      }
   case MASTERLOOPBACK:
      {
         CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                  string(InMessage.Return.CommandStr),
                                                  string(getName() + " / successful ping"),
                                                  InMessage.ErrorCode,
                                                  InMessage.Return.RouteID,
                                                  InMessage.Return.RetryMacroOffset,
                                                  InMessage.Return.Attempt,
                                                  InMessage.Return.GrpMsgID,
                                                  InMessage.Return.UserID);


         if(pLoop != NULL)
         {
            retList.push_back(pLoop);
         }

         break;
      }
   default:
      /* This should never happen so reset the scan */
      CTILOG_ERROR(dout, "unknown data "<< InMessage.Buffer.InMessage[2]);

      if(useScanFlags())
      {
         resetScanFlag();
         setScanFlag(ScanStarting);
      }

      break;
   }   /* End of switch */

   return ClientErrors::None;
}



YukonError_t CtiDeviceTCU::ExecuteRequest(CtiRequestMsg     *pReq,
                                          CtiCommandParser  &parse,
                                          OUTMESS          *&OutMessage,
                                          CtiMessageList    &vgList,
                                          CtiMessageList    &retList,
                                          OutMessageList    &outList)
{
   YukonError_t nRet = ClientErrors::None;
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
            vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                           pReq->getSOE(),
                                           getDescription(parse),
                                           string("Control Request for TCU failed"),
                                           LoadMgmtLogType,
                                           SignalEvent,
                                           pReq->getUser()));

            CtiReturnMsg* eMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                  string(OutMessage->Request.CommandStr),
                                                  string("Control Request for TCU failed"),
                                                  nRet,
                                                  OutMessage->Request.RouteID,
                                                  OutMessage->Request.RetryMacroOffset,
                                                  OutMessage->Request.Attempt,
                                                  OutMessage->Request.GrpMsgID,
                                                  OutMessage->Request.UserID,
                                                  OutMessage->Request.SOE,
                                                  CtiMultiMsg_vec());

            retList.push_back( eMsg );

            if(OutMessage)                // And get rid of our memory....
            {
               delete OutMessage;
               OutMessage = NULL;
            }
         }
         else
         {
            outList.push_back( OutMessage );
            OutMessage = NULL;
         }

         break;
      }
   case GetStatusRequest:
      {
         // Get a scan done maybe?
         if((nRet = TCUScanAll(OutMessage)) != 0)
         {
            CTILOG_ERROR(dout, "Could not scan "<< getName());

            vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                           pReq->getSOE(),
                                           getDescription(parse),
                                           string("Scan All Request for TCU failed"),
                                           GeneralLogType,
                                           SignalEvent,
                                           pReq->getUser()));

            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                             string(OutMessage->Request.CommandStr),
                                             string("Scan All Request for TCU failed"),
                                             nRet,
                                             OutMessage->Request.RouteID,
                                             OutMessage->Request.RetryMacroOffset,
                                             OutMessage->Request.Attempt,
                                             OutMessage->Request.GrpMsgID,
                                             OutMessage->Request.UserID,
                                             OutMessage->Request.SOE,
                                             CtiMultiMsg_vec()));

            if(OutMessage)                // And get rid of our memory....
            {
               delete OutMessage;
               OutMessage = NULL;
            }
         }
         else
         {
            outList.push_back( OutMessage );
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
                  CTILOG_ERROR(dout, "Could not loop "<< getName());

                  retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(getName() + " / ping failed to TCU"),
                                                   nRet,
                                                   OutMTemp->Request.RouteID,
                                                   OutMTemp->Request.RetryMacroOffset,
                                                   OutMTemp->Request.Attempt,
                                                   OutMTemp->Request.GrpMsgID,
                                                   OutMTemp->Request.UserID,
                                                   OutMTemp->Request.SOE,
                                                   CtiMultiMsg_vec()));
               }
               else
               {
                  outList.push_back( OutMTemp );
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
         nRet = ClientErrors::NoMethodForExecuteRequest;
         /* Set the error value in the base class. */
         // FIX FIX FIX 092999
         retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                          string(OutMessage->Request.CommandStr),
                                          string("TCU Devices do not support this command (yet?)"),
                                          nRet,
                                          OutMessage->Request.RouteID,
                                          OutMessage->Request.RetryMacroOffset,
                                          OutMessage->Request.Attempt,
                                          OutMessage->Request.GrpMsgID,
                                          OutMessage->Request.UserID,
                                          OutMessage->Request.SOE,
                                          CtiMultiMsg_vec()));

         break;
      }
   }


   return nRet;
}

/* Routine to execute a versacom message */
YukonError_t CtiDeviceTCU::TCUControl(OUTMESS* OutMessage, VSTRUCT *VSt)
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

   return ClientErrors::InvalidRequest;
}

/* Routine to execute a loop message */
YukonError_t CtiDeviceTCU::TCULoop(OUTMESS* OutMessage)
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


   return ClientErrors::None;
}


CtiReturnMsg* CtiDeviceTCU::TCUDecodeStatus(const INMESS &InMessage)
{
   ULONG       i;
   char        temp[80];

   /* Define the various records */
   CtiPointSPtr          PointRecord;

   /* Variables for decoding TCU Messages */
   USHORT            TCUStatus;
   FLOAT             PValue;

   CtiPointDataMsg   *pData    = NULL;

   CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                           string(InMessage.Return.CommandStr),
                                           string("TCU status request complete"),
                                           InMessage.ErrorCode,
                                           InMessage.Return.RouteID,
                                           InMessage.Return.RetryMacroOffset,
                                           InMessage.Return.Attempt,
                                           InMessage.Return.GrpMsgID,
                                           InMessage.Return.UserID);

   /* update the scan time */
   // FIX FIX FIX 091499 CGP ?????  DeviceRecord->LastFullScan = TimeB->time;

   /* Rebuild the status word */
   TCUStatus = MAKEUSHORT (InMessage.Buffer.InMessage[4], InMessage.Buffer.InMessage[5]);

   /* Now loop through and update remote processes as needed */
   for(i = 1; i <= 16; i++)
   {
      if((PointRecord = getDevicePointOffsetTypeEqual(i + 16, StatusPointType)))
      {
         PValue = ((TCUStatus >> (i - 1)) & 0x0001) ? STATE_CLOSED : STATE_OPENED;

         if(getType() == TYPE_TCU5000)
         {
            sprintf(temp,"Offset %2d: %s is %s", i + 16, StatusPointNames50[i - 1], (PValue == STATE_CLOSED ? "CLOSED" : "OPENED"));
         }
         else if(getType() == TYPE_TCU5500)
         {
            sprintf(temp,"Offset %2d: %s is %s", i + 16, StatusPointNames55[i - 1], (PValue == STATE_CLOSED ? "CLOSED" : "OPENED"));
         }
         else
         {
            sprintf(temp,"Error %s (%d)", __FILE__, __LINE__);
         }

         pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(),
                                     PValue,
                                     NormalQuality,
                                     StatusPointType,
                                     temp);
         if(pData != NULL)
         {
            if(isScanFlagSet(ScanException))
            {
               pData->setExemptionStatus(TRUE);                 // May be short circuited!
            }

            if(pPIL != NULL)
            {
               pPIL->PointData().push_back(pData);
               pData = NULL;  // We just put it on the list...
            }
         }
      }
   }

   if(pPIL->PointData().size() == 0)
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

CtiDeviceTCU::CtiDeviceTCU() : _sendFiller(false)
{}

INT CtiDeviceTCU::getProtocolWrap() const
{
    INT protocol = ProtocolWrapIDLC;

    if(gConfigParms.isOpt("TCU_PROTOCOLWRAP"))
    {
        if( ciStringEqual(gConfigParms.getValueAsString("TCU_PROTOCOLWRAP"),"mastercom") ||
            ciStringEqual(gConfigParms.getValueAsString("TCU_PROTOCOLWRAP"),"none") )
        {
            protocol = ProtocolWrapNone;
        }
    }

    return protocol;
}

