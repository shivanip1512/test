/*-----------------------------------------------------------------------------*
*
* File:   dev_rtm
*
* Date:   7/12/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/01/31 17:09:25 $
*
* HISTORY      :
* $Log: dev_rtm.cpp,v $
* Revision 1.9  2005/01/31 17:09:25  mfisher
* potential patch for the procTMSmsg() exception
*
* Revision 1.8  2005/01/18 19:12:48  cplender
* resetScanFlags
*
* Revision 1.7  2005/01/17 16:29:31  mfisher
* added the resetScanFlags clobber to resultDecode
*
* Revision 1.6  2005/01/13 17:49:57  mfisher
* Returning ErrReturn for error instead of InMessage->EventCode
*
* Revision 1.5  2004/11/03 19:21:20  mfisher
* finished up protocol stuff, added ACK
*
* Revision 1.4  2004/09/20 16:11:04  mfisher
* implemented comms in generate() and decode()
*
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
#include "dsm2err.h"
#include "dev_rtm.h"

#include "msg_cmd.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa3rdparty.h"
#include "numstr.h"


CtiDeviceRTM::CtiDeviceRTM() :
    _state(State_Uninit)
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
                    // parse.setValue("rtm_command", TMS_INIT);
                    // parse.setValue("rtm_command", TMS_ONE);
                    // parse.setValue("rtm_command", TMS_ACK);

                    parse.setValue("rtm_command", TMS_ALL);

                    int cmd_len, error_code;

                    if( !(error_code = CtiProtocolSA3rdParty::formatTMScmd(OutMessage->Buffer.OutMessage, &cmd_len, TMS_ALL, getAddress())) )
                    {
                        OutMessage->OutLength = cmd_len * 2;

                        OutMessage->DeviceID = getID();
                        OutMessage->TargetID = getID();
                        OutMessage->Port = getPortID();
                        OutMessage->EventCode = RESULT | ENCODED;

                        outList.insert( CTIDBG_new OUTMESS( *OutMessage ) );

                        resultString = " Command successfully sent on route " + getName() + "\n";

                        for( int i = 0; i < OutMessage->OutLength; i++ )
                        {
                            resultString += CtiNumStr(OutMessage->Buffer.OutMessage[i]).hex().zpad(2) + " ";
                        }
                    }
                    else
                    {
                        nRet = !NORMAL;
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
/*        {
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
        }*/
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

    bool xmore = true;

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

    resetScanFlags();

    if( !ErrReturn )
    {
        resetScanFlags();

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       getName() + " / scan successful, " + CtiNumStr(InMessage->Buffer.InMessage[0]) + " codes returned",
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.append(retMsg);
    }
    else
    {
        char error_str[80];

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + RWCString(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       resultString,
                                                       ErrReturn,
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

    resetScanFlags();

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

int CtiDeviceRTM::recvCommRequest(OUTMESS *OutMessage)
{
    _outbound = *OutMessage;
    _state    = State_Output;
    _error_count    = 0;
    _codes_received = 0;
    _code_len       = 0;

    return 0;
}

int CtiDeviceRTM::sendCommResult(INMESS *InMessage)
{
    InMessage->Buffer.InMessage[0] = _codes_received;

    return 0;
}


int CtiDeviceRTM::generate(CtiXfer &xfer)
{
    switch( _state )
    {
        case State_Output:
        {
            // State_Output the message to the remote
            xfer.setOutBuffer(_outbound.Buffer.OutMessage);
            xfer.setOutCount(_outbound.OutLength);

            break;
        }
        case State_Input:
        {
            xfer.setOutCount(0);

            if( _code_len )
            {
                xfer.setInBuffer(_inbound + 8);
                xfer.setInCountExpected(_code_len);
                xfer.setInCountActual(&_in_actual);
            }
            else
            {
                xfer.setInBuffer(_inbound);
                xfer.setInCountExpected(8);
                xfer.setInCountActual(&_in_actual);
            }

            break;
        }
        case State_Ack:
        {
            //  the ack was manufactured at the end of the State_Input block below
            xfer.setOutBuffer(_outbound.Buffer.OutMessage);
            xfer.setOutCount(4);
            xfer.setInCountExpected(0);

            break;
        }
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - unknown state in CtiDeviceRTM::generate() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


int CtiDeviceRTM::decode(CtiXfer &xfer,  int status)
{
    if( status )
    {
        //  leave the state where it is, we'll retry
        _error_count++;
    }
    else
    {
        switch( _state )
        {
            case State_Output:
            case State_Ack:
            {
                _state = State_Input;

                break;
            }
            case State_Input:
            {
                if( !_code_len )
                {
                    if( _in_actual >= 8 )
                    {
                        if( CtiProtocolSA3rdParty::TMSlen(_inbound, &_code_len) )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - No code length for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        _error_count++;
                    }
                }
                else
                {
                    SA_CODE sacode;
                    X205CMD x205cmd;
                    CtiVerificationReport *report;
                    int tms_result;
                    bool bad_code = false;

                    try
                    {
                        tms_result = CtiProtocolSA3rdParty::procTMSmsg(_inbound, _code_len + 8, &sacode, &x205cmd);
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - error in CtiProtocolSA3rdParty::procTMSmsg() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        bad_code = true;
                    }

                    //  this way, we acknowledge the code even if it made procTMSmsg() pop
                    if( bad_code || tms_result == TMS_CODE)
                    {
                        if( !bad_code )
                        {
                            _codes_received++;

                            report = CTIDBG_new CtiVerificationReport(CtiVerificationBase::Protocol_SA205, getID(), sacode.code, second_clock::universal_time());

                            _verification_objects.push(report);
                        }

                        //  this is a bit of a hack based on watching port traces of successful comms
                        //    the LRC is copied from the last inbound and tagged after an "ACK" message type
                        _outbound.Buffer.OutMessage[0] = 'B';
                        _outbound.Buffer.OutMessage[1] = 'F';
                        _outbound.Buffer.OutMessage[2] = _inbound[_code_len+6];
                        _outbound.Buffer.OutMessage[3] = _inbound[_code_len+7];

                        _state = State_Ack;
                    }
                    else // if( tms_result == TMS_EMPTY )
                    {
                        _state = State_Complete;
                    }

                    _code_len = 0;
                }

                break;
            }
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - unknown state in CtiDeviceRTM::decode() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return status;
}


bool CtiDeviceRTM::isTransactionComplete()
{
    return ((_error_count > MaxErrors) || (_state == State_Complete));
}

void CtiDeviceRTM::getVerificationObjects(queue<CtiVerificationBase *> &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

