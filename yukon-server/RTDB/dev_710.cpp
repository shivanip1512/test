/*-----------------------------------------------------------------------------*
*
* File:   dev_710
*
* Date:   6/21/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_710.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/03/11 17:27:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <windows.h>
#include <iostream>
using namespace std;

#include "cmdparse.h"
#include "dev_710.h"
#include "dsm2.h"
#include "prot_emetcon.h"
#include "cti_asmc.h"
#include "pt_base.h"

#if 0
#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\ctoken.h>

#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "device.h"
#include "connection.h"

#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "prot_711.h"
#include "yukon.h"

#endif


INT CtiDeviceCCU710::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("loop");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("loop");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}

INT CtiDeviceCCU710::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList,  INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


INT CtiDeviceCCU710::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int retVal = NORMAL;

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::Command_Loop):
        {
            unsigned char expectedAck;
            RWCString cmd(InMessage->Return.CommandStr);
            CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                    cmd,
                                                    RWCString(),
                                                    InMessage->EventCode & 0x7fff,
                                                    InMessage->Return.RouteID,
                                                    InMessage->Return.MacroOffset,
                                                    InMessage->Return.Attempt,
                                                    InMessage->Return.TrxID,
                                                    InMessage->Return.UserID);

            resetScanPending();

            //  expect two ACK characters
            expectedAck = Parity_C( 0x40 | (getAddress() & 0x03) );

            if( InMessage->Buffer.InMessage[0] == expectedAck &&
                InMessage->Buffer.InMessage[1] == expectedAck )
            {
                if( getDebugLevel() & 0x01 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                if( retMsg != NULL )
                {
                    retMsg->setResultString(getName() + " / successful ping");
                }
            }
            else
            {
                if( retMsg != NULL)
                {
                    retMsg->setResultString(getName() + " / ping failed");
                    retVal = FRAMEERR;
                    retMsg->setStatus(retVal);
                }
            }

            if( retMsg != NULL )
            {
                retList.insert(retMsg);
                retMsg = NULL;
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - IM->Sequence = " << InMessage->Sequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    return retVal;
}


/* Routine to decode returned CCU message and update database */
INT CtiDeviceCCU710::ExecuteRequest(CtiRequestMsg                  *pReq,
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
   case LoopbackRequest:
      {
         int cnt = parse.getiValue("count");

         for(int i = 0; i < cnt; i++)
         {
            OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);

            if(OutMTemp != NULL)
            {
               // Get a loop done maybe?
               Loopback(OutMTemp);
               outList.insert( OutMTemp );
            }
         }
         break;
      }
   case ControlRequest:
   case GetStatusRequest:
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
                                          RWCString("CCU Devices do not support this command (yet?)"),
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

/* Routine to execute a loop message */
INT CtiDeviceCCU710::Loopback(OUTMESS* OutMessage)
{

   OutMessage->DeviceID    = getID();
   OutMessage->TargetID    = getID();
   OutMessage->Port        = getPortID();
   OutMessage->Remote      = getAddress();

   /* Build a loopback preamble */
   LPreamble (OutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Remote);

   OutMessage->OutLength   = 3 + 3;   /* n't Ask */
   OutMessage->InLength    = 4;
   OutMessage->TimeOut     = 2;
   OutMessage->EventCode   = ENCODED | RESULT | NOWAIT;
   OutMessage->Retry       = 0;
   OutMessage->Sequence    = CtiProtocolEmetcon::Command_Loop;
   OutMessage->ReturnNexus = NULL;
   OutMessage->SaveNexus   = NULL;

   return(NORMAL);
}

CtiDeviceCCU710::CtiDeviceCCU710() {}

CtiDeviceCCU710::CtiDeviceCCU710(const CtiDeviceCCU710& aRef)
{
  *this = aRef;
}

CtiDeviceCCU710::~CtiDeviceCCU710() {}

CtiDeviceCCU710& CtiDeviceCCU710::operator=(const CtiDeviceCCU710& aRef)
{
  if(this != &aRef)
  {
     Inherited::operator=(aRef);
  }
  return *this;
}

INT CtiDeviceCCU710::getProtocolWrap() const
{
   return ProtocolWrapNone;
}

