/*-----------------------------------------------------------------------------*
*
* File:   dev_rtm
*
* Date:   7/12/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2008/10/17 11:14:38 $
*
* HISTORY      :
* $Log: dev_rtm.cpp,v $
* Revision 1.25  2008/10/17 11:14:38  mfisher
* YUK-2869 Add CCU-721
* Miscellaneous fixes/updates to get the test plan working
*
* Revision 1.24  2008/08/14 15:57:40  jotteson
* YUK-6333  Change naming in request message and change cancellation to use this new named field instead of user ID
* Cancellation now uses the new group message ID.
* Group Message ID name added to Request, Result, Out, and In messages.
*
* Revision 1.23  2008/06/06 20:28:01  jotteson
* YUK-6005 Porter LLP expect more set incorrectly
* Added an option to override expect more in the error decode call.
* Made LLP retry 3 times before failing.
*
* Revision 1.22  2008/01/25 22:29:23  jotteson
* YUK-5184 Verification Reports not working for sa 205
* Changed CtiString to allow padding up to a given size.
* Changed RTM to pad properly, not use the pointer address.
*
* Revision 1.21  2007/03/28 21:18:42  jotteson
* Memory leak's fixed.
*
* Revision 1.20  2006/10/16 17:38:10  jotteson
* Adding sa305 and saDigital to verification process.
*
* Revision 1.19  2006/04/19 15:51:29  mfisher
* removed CtiNumStr call around 205 code reporting
*
* Revision 1.18  2006/03/23 15:29:18  jotteson
* Mass update of point* to smart pointers. Point manager now uses smart pointers.
*
* Revision 1.17  2006/02/27 23:58:31  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.16  2006/02/24 00:19:12  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.15  2006/02/17 17:04:35  tspar
* CtiMultiMsg:  replaced RWOrdered with vector<RWCollectable*> throughout the tree
*
* Revision 1.14  2006/01/05 18:27:10  cplender
* Removed annoying printouts about VRReport
*
* Revision 1.13  2005/12/20 17:20:24  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.12  2005/10/19 02:50:23  cplender
* Altered the dev_single.h inherited scanflags methods.
* They are always available.
* They are a map of boolean values.
*
* Some of the elements moved out of the table object into the dev_single object
*
* Revision 1.11  2005/08/15 15:12:06  cplender
* Altered the protocol to do a reverse decode on RTM receipts.
*
* Revision 1.10  2005/02/10 23:24:00  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
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
#include "yukon.h"

#include "cparms.h"
#include "dsm2err.h"
#include "dev_rtm.h"

#include "msg_cmd.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa3rdparty.h"
#include "numstr.h"
#include "ctistring.h"


CtiDeviceRTM::CtiDeviceRTM() :
    _state(State_Uninit),
    _code_len(0),
    _codes_received(0),
    _error_count(0),
    _in_actual(0),
    _in_expected(0)
{
    memset(_inbound, 0, sizeof(_inbound));
}

CtiDeviceRTM::~CtiDeviceRTM()
{
}


INT CtiDeviceRTM::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  list< CtiMessage* > &vgList,list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CtiDeviceRTM::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT nRet = NORMAL;
    string      resultString;

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

                    int cmd_len = 300;
                    int error_code = 0;
                    error_code = CtiProtocolSA3rdParty::formatTMScmd(OutMessage->Buffer.OutMessage, &cmd_len, TMS_ALL, getAddress());

                    if( !error_code )
                    {
                        OutMessage->OutLength = cmd_len * 2;

                        OutMessage->DeviceID = getID();
                        OutMessage->TargetID = getID();
                        OutMessage->Port = getPortID();
                        OutMessage->EventCode = RESULT | ENCODED;

                        outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

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
                        dout << CtiTime() << " Invalid scan type \"" << parse.getiValue("scantype") << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    nRet = NoExecuteRequestMethod;
                    retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                            string(OutMessage->Request.CommandStr),
                                                            string("RTM Devices do not support this command."),
                                                            nRet,
                                                            OutMessage->Request.RouteID,
                                                            OutMessage->Request.MacroOffset,
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

                    break;
                }
            }
            break;
        }
    case ControlRequest:
/*        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            Sleep(500);

            CtiProtocolSA3rdParty prot;
            prot.setGroupType(GRP_SA_RTM);

            if(parse.getCommandStr().contains(" init", string::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_INIT);
            }
            else if(parse.getCommandStr().contains(" one", string::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ONE);
            }
            else if(parse.getCommandStr().contains(" ack", string::ignoreCase))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ACK);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                parse.setValue("rtm_command", TMS_ALL);
            }

            prot.setTransmitterAddress( getAddress() );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Address " << getAddress() << endl;
                prot.parseCommand(parse, *OutMessage);
            }


            if(prot.messageReady())
            {
                string      byteString;

                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->EventCode = RESULT | ENCODED;

                OutMessage->Buffer.SASt = prot.getSAData();
                OutMessage->OutLength = prot.getSABufferLen();

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

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
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("RTM Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
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

            break;
        }
    }

    bool xmore = true;

    if( resultString.empty() )
    {
        xmore = false;
        resultString = getName() + " did not transmit commands";

        string desc, actn;

        desc = getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on device";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.push_back(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return nRet;
}


INT CtiDeviceRTM::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    string resultString;

    resetScanFlag();

    if( !ErrReturn )
    {
        resetScanFlag();

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(InMessage->Return.CommandStr),
                                                       getName() + " / scan successful, " + CtiNumStr(InMessage->Buffer.InMessage[0]) + " codes returned",
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.GrpMsgID,
                                                       InMessage->Return.UserID);

        retList.push_back(retMsg);
    }
    else
    {
        char error_str[80];

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(InMessage->Return.CommandStr),
                                                       resultString,
                                                       ErrReturn,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.GrpMsgID,
                                                       InMessage->Return.UserID);

        retList.push_back(retMsg);
    }

    return ErrReturn;
}


INT CtiDeviceRTM::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, list< CtiMessage* > &retList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage.Return.CommandStr);
    CtiPointDataMsg  *commFailed;
    CtiPointSPtr     commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    resetScanFlag();

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

    if(pMsg != NULL)
    {
        pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
        pMsg->insert(CtiCommandMsg::OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());          // The id (device or point which failed)
        pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

        if(InMessage.EventCode != 0)
        {
            pMsg->insert(InMessage.EventCode);
        }
        else
        {
            pMsg->insert(GeneralScanAborted);
        }

        retList.push_back( pMsg );
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    return NoError;
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
            dout << CtiTime() << " **** Checkpoint - unknown state in CtiDeviceRTM::generate() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                            dout << CtiTime() << " **** Checkpoint - No code length for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        if( _code_len > 200 || _code_len < 0 )
                        {
                            _state = State_Complete;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - invalid code length (" << _code_len << ") for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
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

                    memset((void*)&sacode, 0, sizeof(SA_CODE));

                    try
                    {
                        tms_result = CtiProtocolSA3rdParty::procTMSmsg(_inbound, _code_len + 8, &sacode, &x205cmd);
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - error in CtiProtocolSA3rdParty::procTMSmsg() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        bad_code = true;
                    }

                    //  this way, we acknowledge the code even if it made procTMSmsg() pop
                    if( bad_code || tms_result == TMS_CODE)
                    {
                        if( !bad_code )
                        {
                            CtiString codestr("-");
                            string cmdStr = CtiProtocolSA3rdParty::asString(sacode);
                            CtiVerificationBase::Protocol prot_type = CtiVerificationBase::Protocol_Invalid;
                            switch(sacode.type)
                            {
                            case GOLAY:
                                prot_type = CtiVerificationBase::Protocol_Golay;
                                codestr = sacode.code;
                                break;
                            case SADIG:
                                prot_type = CtiVerificationBase::Protocol_SADigital;
                                codestr = sacode.code;
                                break;
                            case SA205:
                                prot_type = CtiVerificationBase::Protocol_SA205;
                                codestr = sacode.code;
                                codestr.padFront(6, "0");
                                break;
                            case SA305:
                                prot_type = CtiVerificationBase::Protocol_SA305;
                                codestr = sacode.code;
                                break;
                            }

                            _codes_received++;


                            /*{
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** VReport Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << cmdStr << endl;
                            }*/
                            report = CTIDBG_new CtiVerificationReport(prot_type, getID(), codestr, second_clock::universal_time(), cmdStr);
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
                dout << CtiTime() << " **** Checkpoint - unknown state in CtiDeviceRTM::decode() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

