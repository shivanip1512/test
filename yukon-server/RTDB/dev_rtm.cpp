
/*-----------------------------------------------------------------------------*
*
* File:   dev_rtm
*
* Date:   7/12/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/07/30 21:35:07 $
*
* HISTORY      :
* $Log: dev_rtm.cpp,v $
* Revision 1.3  2004/07/30 21:35:07  cplender
* RTM stuff
*
* Revision 1.2  2004/07/21 19:48:57  cplender
* Added the rtm.
*
* Revision 1.1  2004/07/20 16:19:22  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#include "cparms.h"
#include "dev_rtm.h"

#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa3rdparty.h"
#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"


CtiDeviceRTM::CtiDeviceRTM()
{
}

CtiDeviceRTM::~CtiDeviceRTM()
{
}

INT CtiDeviceRTM::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT CtiDeviceRTM::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan integrity");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT CtiDeviceRTM::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NORMAL;
    RWCString      resultString;

    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    switch(parse.getCommand())
    {
    case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    CtiProtocolSA3rdParty prot;
                    prot.setGroupType(GRP_SA_RTM);

                    // parse.setValue("rtm_command", TMS_INIT);
                    parse.setValue("rtm_command", TMS_ONE);
                    // parse.setValue("rtm_command", TMS_ALL);
                    // parse.setValue("rtm_command", TMS_ACK);

                    prot.setTransmitterAddress( getAddress() );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        prot.parseCommand(parse, *OutMessage);
                    }


                    if(prot.messageReady())
                    {
                        RWCString      byteString;

                        OutMessage->DeviceID = getID();
                        OutMessage->TargetID = getID();
                        OutMessage->Port = getPortID();
                        OutMessage->EventCode = RESULT | ENCODED;

                        OutMessage->Buffer.SASt = prot.getSAData();
                        OutMessage->OutLength = prot.getSABufferLen();

                        outList.insert( CTIDBG_new OUTMESS( *OutMessage ) );

                        prot.copyMessage(byteString);
                        resultString = " Command successfully sent on route " + getName() + "\n" + byteString;
                    }


                    break;
                }
                case ScanRateAccum:
                case ScanRateIntegrity:
                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Invalid scan type \"" << parse.getiValue("scantype") << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    nRet = NoExecuteRequestMethod;
                    retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                            RWCString(OutMessage->Request.CommandStr),
                                                            RWCString("RTM Devices do not support this command."),
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

                    break;
                }
            }
            break;
        }
    case ControlRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            Sleep(500);

            CtiProtocolSA3rdParty prot;
            prot.setGroupType(GRP_SA_RTM);

            if(parse.getCommandStr().contains(" init", RWCString::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_INIT);
            }
            else if(parse.getCommandStr().contains(" one", RWCString::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ONE);
            }
            else if(parse.getCommandStr().contains(" ack", RWCString::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ACK);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ALL);
            }

            prot.setTransmitterAddress( getAddress() );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Address " << getAddress() << endl;
                prot.parseCommand(parse, *OutMessage);
            }


            if(prot.messageReady())
            {
                RWCString      byteString;

                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->EventCode = RESULT | ENCODED;

                OutMessage->Buffer.SASt = prot.getSAData();
                OutMessage->OutLength = prot.getSABufferLen();

                outList.insert( CTIDBG_new OUTMESS( *OutMessage ) );

                prot.copyMessage(byteString);
                resultString = " Command successfully sent on route " + getName() + "\n" + byteString;
            }

            break;
        }
    case GetStatusRequest:
    case LoopbackRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = NoExecuteRequestMethod;
            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                    RWCString(OutMessage->Request.CommandStr),
                                                    RWCString("RTM Devices do not support this command (yet?)"),
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

            break;
        }
    }

    bool xmore;

    if( resultString.isNull() )
    {
        xmore = false;
        resultString = getName() + " did not transmit commands";

        RWCString desc, actn;

        desc = getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on device";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.insert(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return nRet;
}


INT CtiDeviceRTM::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        BYTE buff[] = { 0x42, 0x31, 0x32, 0x41, 0x30, 0x30, 0x30, 0x32, 0x30, 0x35, 0x39, 0x43 };


    }
    else
    {
        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       getName() + " / operation failed",
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.append(retMsg);
    }

    return ErrReturn;
}


INT CtiDeviceRTM::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                                     RWCString(InMessage->Return.CommandStr),
                                                     RWCString(),
                                                     InMessage->EventCode & 0x7fff,
                                                     InMessage->Return.RouteID,
                                                     InMessage->Return.MacroOffset,
                                                     InMessage->Return.Attempt,
                                                     InMessage->Return.TrxID,
                                                     InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    if( pPIL != NULL )
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if(pMsg != NULL)
        {
            pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            if(InMessage->EventCode != 0)
            {
                pMsg->insert(InMessage->EventCode);
            }
            else
            {
                pMsg->insert(GeneralScanAborted);
            }

            retList.insert( pMsg );
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}

LONG CtiDeviceRTM::getAddress() const
{
    return getIED().getSlaveAddress();
}


INT CtiDeviceRTM::prepareOutMessageForComms(CtiOutMessage *&OutMessage)
{
    RWTime now;
    INT status = NORMAL;

    ULONG msgMillis = 0;

    try
    {
        CtiProtocolSA3rdParty prot;

        OutMessage->OutLength = OutMessage->Buffer.SASt._bufferLen;

        prot.setSAData( OutMessage->Buffer.SASt );
        prot.setGroupType( GRP_SA_RTM );

        {
            CtiLockGuard<CtiLogger> doubt_guard(slog);
            slog << RWTime() << " " << getName() << ": " << prot.asString() << endl;
            slog << RWTime() << " " << getName() << " sending " << OutMessage->Buffer.SASt._bufferLen << " bytes" << endl;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getName() << ": " << prot.asString() << endl;
            dout << RWTime() << " " << getName() << " sending " << OutMessage->Buffer.SASt._bufferLen << " bytes" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}
