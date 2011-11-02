#include "precompiled.h"

#include "cparms.h"
#include "dsm2err.h"
#include "dev_rtm.h"

#include "msg_cmd.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa3rdparty.h"
#include "numstr.h"
#include "ctistring.h"

using std::string;
using std::endl;
using std::list;
using std::queue;
using namespace boost::posix_time;

using std::queue;

CtiDeviceRTM::CtiDeviceRTM() :
    _state(State_Uninit),
    _code_len(0),
    _codes_received(0),
    _error_count(0),
    _in_actual(0),
    _in_total(0)
{
    memset(_inbound, 0, sizeof(_inbound));
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

    switch(parse.getCommand())
    {
    case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                {
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

            delete OutMessage;
            OutMessage = NULL;

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

            _in_total = 0;

            break;
        }
        case State_InputHeader:
        {
            xfer.setOutCount(0);
            xfer.setInBuffer(_inbound + _in_total);
            xfer.setInCountExpected(HeaderLength - _in_total);
            xfer.setInCountActual(&_in_actual);

            _code_len = 0;

            break;
        }
        case State_Input:
        {
            xfer.setOutCount(0);
            xfer.setInBuffer(_inbound + HeaderLength);
            xfer.setInCountExpected(_code_len);
            xfer.setInCountActual(&_in_actual);

            break;
        }
        case State_Ack:
        {
            //  the ack was manufactured at the end of the State_Input block below
            xfer.setOutBuffer(_outbound.Buffer.OutMessage);
            xfer.setOutCount(4);
            xfer.setInCountExpected(0);

            _in_total = 0;

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


unsigned long CtiDeviceRTM::findHeader(unsigned char *buf, unsigned long len)
{
    unsigned long offset;

    //  look for the framing bytes
    for( offset = 0; offset < len; offset++ )
    {
        if( buf[offset] == 0x42 )
        {
            //  if we can look for both framing bytes at once...
            if( (offset + 1) < len )
            {
                //  if we found the start of the header
                if( buf[offset+1] == 0x31 )
                {
                    //  found both header bytes
                    break;
                }
            }
            else
            {
                //  we found one header byte - try for the rest the next time around
                break;
            }
        }
    }

    if( offset < len)
    {
        //  move everything to the start of the packet
        memmove(buf, buf + offset, len - offset);
    }

    return len - offset;
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
                _state = State_InputHeader;

                break;
            }
            case State_InputHeader:
            {
                _in_total += _in_actual;

                _in_total = findHeader(_inbound, _in_total);

                if( _in_total >= HeaderLength )
                {
                    if( CtiProtocolSA3rdParty::TMSlen(_inbound, &_code_len) )
                    {
                        _state = State_Complete;

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
                    else if( _code_len > 200 || _code_len <= 0 )
                    {
                        _state = State_Complete;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - invalid code length (" <<_code_len << ") for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        _state = State_Input;
                    }
                }
                else
                {
                    _error_count++;
                }

                break;
            }
            case State_Input:
            {
                SA_CODE sacode;
                X205CMD x205cmd;
                CtiVerificationReport *report;
                int tms_result;
                bool bad_code = false;

                memset((void*)&sacode, 0, sizeof(SA_CODE));

                try
                {
                    tms_result = CtiProtocolSA3rdParty::procTMSmsg(_inbound, HeaderLength + _code_len, &sacode, &x205cmd);
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

