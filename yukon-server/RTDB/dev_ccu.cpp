
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu
*
* Date:   6/7/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_ccu.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/11/15 14:08:10 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/tvhdict.h>
#include <rw/cstring.h>
#include <rw/rwdate.h>
#include <rw/tvslist.h>

#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\ctoken.h>

#include "cmdparse.h"
#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "dev_ccu.h"
#include "device.h"
#include "connection.h"

#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "prot_711.h"
#include "prot_emetcon.h"
#include "utility.h"
#include "yukon.h"



CtiDeviceCCU::CtiDeviceCCU() {}

CtiDeviceCCU::CtiDeviceCCU(const CtiDeviceCCU& aRef)
{
   *this = aRef;
}

CtiDeviceCCU::~CtiDeviceCCU()
{
}

CtiDeviceCCU& CtiDeviceCCU::operator=(const CtiDeviceCCU& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}

INT CtiDeviceCCU::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
   INT status = NORMAL;

   // Get a loop done maybe?
   if(OutMessage != NULL)
   {
      CCULoop(OutMessage);
      outList.insert( OutMessage );
      OutMessage = NULL;
   }

   return status;
}

INT CtiDeviceCCU::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList,  INT ScanPriority)
{
   return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


INT CtiDeviceCCU::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{

    /* Clear the Scan Pending flag, if neccesary it will be reset */
    resetScanPending();

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::Command_Loop):
        {
        char temp[10];
            RWCString cmd(InMessage->Return.CommandStr);


            CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                     cmd,
                                                     RWCString("Loopback Successful"),
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

    case (CtiProtocolEmetcon::PutStatus_Reset):
        {
            CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                     InMessage->Return.CommandStr,
                                                     "Reset Submitted",
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
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ACH. Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "InMessage->Sequence = " << InMessage->Sequence << endl;
            }

            break;
        }
    }


    return(NORMAL);
}


/* Routine to decode returned CCU message and update database */
INT CtiDeviceCCU::ExecuteRequest(CtiRequestMsg                  *pReq,
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
            CCULoop(OutMessage);
            outList.insert( OutMessage );
            OutMessage = NULL;
            break;
        }
        case PutStatusRequest:
        {
            if(parse.getFlags() & CMD_FLAG_PS_RESET)
            {
                if(getType() == TYPE_CCU711)
                {
                    OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);

                    if(OutMTemp != NULL)
                    {
                        // Get a reset done maybe?
                        CCU711Reset(OutMTemp);
                        outList.insert( OutMTemp );
                    }
                }
                else
                {
                    nRet = NoMethod;
                    retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                     RWCString(OutMessage->Request.CommandStr),
                                                     RWCString("Non-711 CCUs do not support this command"),
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.MacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.TrxID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     RWOrdered()));
                }
            }

            break;
        }

        case ControlRequest:
        case GetStatusRequest:
        case GetValueRequest:
        case PutValueRequest:
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
INT CtiDeviceCCU::CCULoop(OUTMESS* OutMessage)
{
    /* Build an IDLC loopback request */
    /* Load up the pieces of the structure */
    OutMessage->DeviceID    = getID();
    OutMessage->Port        = getPortID();
    OutMessage->Remote      = getAddress();
    OutMessage->EventCode   = RESULT | RCONT | ENCODED | NOWAIT;
    OutMessage->TimeOut     = 2;
    OutMessage->OutLength   = 3;
    OutMessage->InLength    = 0;
    OutMessage->Source      = 0;
    OutMessage->Destination = DEST_BASE;
    OutMessage->Command     = CMND_ACTIN;
    OutMessage->Retry       = 0;
    OutMessage->Sequence    = CtiProtocolEmetcon::Command_Loop;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus   = NULL;

    OutMessage->Buffer.OutMessage[PREIDL] = NO_OP;

    return(NORMAL);
}


/* Routine to execute a reset on a 711 CCU */
INT CtiDeviceCCU::CCU711Reset(OUTMESS* OutMessage)
{
    //  Load up the structure
    OutMessage->DeviceID    = getID();
    OutMessage->Port        = getPortID();
    OutMessage->Remote      = getAddress();
    OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED;
    OutMessage->TimeOut     = 5;
    OutMessage->OutLength   = 3;
    OutMessage->InLength    = 0;
    OutMessage->Source      = 0;
    OutMessage->Destination = DEST_BASE;
    OutMessage->Command     = CMND_ACTIN;
    OutMessage->Retry       = 0;
    OutMessage->Sequence    = CtiProtocolEmetcon::PutStatus_Reset;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus   = NULL;

    OutMessage->Buffer.OutMessage[PREIDL] = COLD;  //  16... ?

    EstablishOutMessagePriority( OutMessage, MAXPRIORITY );

    return(NORMAL);
}

