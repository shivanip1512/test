#include "precompiled.h"

#include "cmdparse.h"
#include "dev_710.h"
#include "dsm2.h"
#include "cti_asmc.h"
#include "pt_base.h"

using namespace std;

YukonError_t CtiDeviceCCU710::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("loop");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "General Scan of device "<< getName() <<" in progress");
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

YukonError_t CtiDeviceCCU710::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,  INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


YukonError_t CtiDeviceCCU710::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t retVal = ClientErrors::None;

    switch(InMessage.Sequence)
    {
        case Command_Loop:
        {
            unsigned char expectedAck;
            unsigned char expectedBytes[2];
            string cmd(InMessage.Return.CommandStr);
            CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                    cmd,
                                                    string(),
                                                    InMessage.ErrorCode,
                                                    InMessage.Return.RouteID,
                                                    InMessage.Return.RetryMacroOffset,
                                                    InMessage.Return.Attempt,
                                                    InMessage.Return.GrpMsgID,
                                                    InMessage.Return.UserID);

            resetScanFlag();

            //  expect two ACK characters
            expectedAck = Parity_C( 0x40 | (getAddress() & 0x03) );

            //  Calculate the necessary address components
            if( getAddress() > 3 )
            {
                expectedBytes[0] = Parity_C( ((getAddress() & 0x1c) << 1) | 0x45 );
                expectedBytes[1] = Parity_C( 0x55 );
            }
            else
            {
                expectedBytes[0] = Parity_C( 0x55 );
                expectedBytes[1] = Parity_C( 0x55 );
            }

            if( InMessage.Buffer.InMessage[0] == expectedAck &&
                InMessage.Buffer.InMessage[1] == expectedAck &&
                InMessage.Buffer.InMessage[2] == expectedBytes[0] &&
                InMessage.Buffer.InMessage[3] == expectedBytes[1] )
            {
                if( isDebugLudicrous() )
                {
                    CTILOG_DEBUG(dout, getName() <<" / successful ping");
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
                    retVal = ClientErrors::Framing;
                    retMsg->setStatus(retVal);
                }
            }

            if( retMsg != NULL )
            {
                retList.push_back(retMsg);
                retMsg = NULL;
            }

            break;
        }

        default:
        {
            CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
        }
    }

    return retVal;
}


/* Routine to decode returned CCU message and update database */
YukonError_t CtiDeviceCCU710::ExecuteRequest(CtiRequestMsg     *pReq,
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
               outList.push_back( OutMTemp );
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
         nRet = ClientErrors::NoMethodForExecuteRequest;
         /* Set the error value in the base class. */
         // FIX FIX FIX 092999
         retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                          string(OutMessage->Request.CommandStr),
                                          string("CCU Devices do not support this command (yet?)"),
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
   OutMessage->Sequence    = Command_Loop;
   OutMessage->ReturnNexus = NULL;
   OutMessage->SaveNexus   = NULL;

   return ClientErrors::None;
}

CtiDeviceCCU710::CtiDeviceCCU710()
{}

INT CtiDeviceCCU710::getProtocolWrap() const
{
   return ProtocolWrapNone;
}

/* Routine to do a loopback preamble for a 700/710 */
INT CtiDeviceCCU710::LPreamble(PBYTE Pre, USHORT Remote)
{
   USHORT i;

   /* load the CCU address */
   Pre[0] = Remote & 0x03;

   if(Remote > 3)
   {
      Pre[0] |= 0x40;
      Pre[1] = ((Remote & 0x1c) << 1) | 0x45;
   }
   else
      Pre[1] = 0x55;

   Pre[0] |= 2 << 3;

   Pre[2] = 0x55;

   /* calculate the parity on all three bytes */
   for(i = 0; i < 3; i++)
      Pre[i] = Parity_C (Pre[i]);

   return ClientErrors::None;
}

