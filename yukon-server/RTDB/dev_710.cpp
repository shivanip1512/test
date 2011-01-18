#include "yukon.h"

#include "cmdparse.h"
#include "dev_710.h"
#include "dsm2.h"
#include "cti_asmc.h"
#include "pt_base.h"

using namespace std;

INT CtiDeviceCCU710::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  list< CtiMessage* > &vgList,list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("loop");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

INT CtiDeviceCCU710::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList,  INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


INT CtiDeviceCCU710::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int retVal = NORMAL;

    switch(InMessage->Sequence)
    {
        case Command_Loop:
        {
            unsigned char expectedAck;
            unsigned char expectedBytes[2];
            string cmd(InMessage->Return.CommandStr);
            CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                    cmd,
                                                    string(),
                                                    InMessage->EventCode & 0x7fff,
                                                    InMessage->Return.RouteID,
                                                    InMessage->Return.MacroOffset,
                                                    InMessage->Return.Attempt,
                                                    InMessage->Return.GrpMsgID,
                                                    InMessage->Return.UserID);

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

            if( InMessage->Buffer.InMessage[0] == expectedAck &&
                InMessage->Buffer.InMessage[1] == expectedAck &&
                InMessage->Buffer.InMessage[2] == expectedBytes[0] &&
                InMessage->Buffer.InMessage[3] == expectedBytes[1] )
            {
                if( isDebugLudicrous() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                retList.push_back(retMsg);
                retMsg = NULL;
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - IM->Sequence = " << InMessage->Sequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                 list< CtiMessage* >      &vgList,
                                 list< CtiMessage* >      &retList,
                                 list< OUTMESS* >         &outList)
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
         nRet = NoExecuteRequestMethod;
         /* Set the error value in the base class. */
         // FIX FIX FIX 092999
         retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                          string(OutMessage->Request.CommandStr),
                                          string("CCU Devices do not support this command (yet?)"),
                                          nRet,
                                          OutMessage->Request.RouteID,
                                          OutMessage->Request.MacroOffset,
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

   return(NORMAL);
}

