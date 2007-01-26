
/*-----------------------------------------------------------------------------*
*
* File:   dev_fmu
*
* Date:   9/26/2006
*
* Author: Julie Richter
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 19:56:13 $
*
* HISTORY      :
* $Log: dev_fmu.cpp,v $
* Revision 1.1  2007/01/26 19:56:13  jrichter
* FMU stuff for jess....
*

*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "cparms.h"
#include "dsm2err.h"
#include "dev_fmu.h"

#include "msg_cmd.h"
#include "porter.h"
#include "prot_fmu.h"
#include "numstr.h"

using namespace Cti;
using namespace Protocol;
using namespace fmuProtocol;
         
CtiDeviceFMU::CtiDeviceFMU() :
    _state(State_Uninit)
{
}

CtiDeviceFMU::~CtiDeviceFMU()
{
}


INT CtiDeviceFMU::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  list< CtiMessage* > &vgList,list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority)
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
/*CtiProtocolFMU& CtiDeviceFMU::getFMUProtocol( void )
{
   return  _fmuProtocol;
} */

INT CtiDeviceFMU::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
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
                case ScanRateAccum:
                case ScanRateIntegrity:
                {
                    USHORT cmd = Cti::Protocol::fmuProtocol::dataReqCmd;
                    int error_code;
                    BYTE *opData = NULL;
                    //_cmd_len = 0;

                    if( !(error_code = (getFMUProtocol().generate(OutMessage->Buffer.OutMessage, _cmd_len, getAddress(), _sequence + 1, cmd, opData))) )
                    {
                         _sequence += 1;
                        OutMessage->OutLength = _cmd_len;

                        OutMessage->DeviceID = getID();
                        OutMessage->TargetID = getID();
                        OutMessage->Port = getPortID();
                        OutMessage->EventCode = RESULT | ENCODED;

                        outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                        resultString = " Command successfully sent on route " + getName() + "\n";

                        /*for( int i = 0; i < OutMessage->OutLength; i++ )
                        {
                            resultString += CtiNumStr(OutMessage->Buffer.OutMessage[i]).hex().zpad(2) + " ";
                        } */
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
                                                            string("FMU Devices do not support this command."),
                                                            nRet,
                                                            OutMessage->Request.RouteID,
                                                            OutMessage->Request.MacroOffset,
                                                            OutMessage->Request.Attempt,
                                                            OutMessage->Request.TrxID,
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
        case GetConfigRequest:
        {   
            if(parse.isKeyValid("time read") )
            {
                //command = Command_ReadTime;
                USHORT cmd = Cti::Protocol::fmuProtocol::timeRead;
                int error_code;
                BYTE *opData = NULL;


                if( !(error_code = (getFMUProtocol().generate(OutMessage->Buffer.OutMessage, _cmd_len, getAddress(),  _sequence + 1, cmd, opData))) )
                {
                    _sequence += 1;
                    OutMessage->OutLength = _cmd_len;

                    OutMessage->DeviceID = getID();
                    OutMessage->TargetID = getID();
                    OutMessage->Port = getPortID();
                    OutMessage->EventCode = RESULT | ENCODED;

                    outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                    resultString = " Command successfully sent on route " + getName() + "\n";

                    /*for( int i = 0; i < OutMessage->OutLength; i++ )
                    {
                        resultString += CtiNumStr(OutMessage->Buffer.OutMessage[i]).hex().zpad(2) + " ";
                    } */
                }
                else
                {
                    nRet = !NORMAL;
                }
            }
            break;
        }
        case PutConfigRequest:
        {
            if(parse.isKeyValid("time sync") )
            {
                USHORT cmd = Cti::Protocol::fmuProtocol::timeSyncReq;
            }
            else if (parse.isKeyValid("comm sync") )
            {
                USHORT cmd = Cti::Protocol::fmuProtocol::commSync;
            }
            else
            {
                nRet = NoExecuteRequestMethod;
                retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                        string(OutMessage->Request.CommandStr),
                                                        string("FMU Devices do not support this command (yet?)"),
                                                        nRet,
                                                        OutMessage->Request.RouteID,
                                                        OutMessage->Request.MacroOffset,
                                                        OutMessage->Request.Attempt,
                                                        OutMessage->Request.TrxID,
                                                        OutMessage->Request.UserID,
                                                        OutMessage->Request.SOE,
                                                        CtiMultiMsg_vec()));
             
                if(OutMessage)                // And get rid of our memory....
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }
            }
            break;
        }

        case ControlRequest:
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
        case PutValueRequest:
        case PutStatusRequest:
        default:
        {
            nRet = NoExecuteRequestMethod;
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("FMU Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.TrxID,
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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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


INT CtiDeviceFMU::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    string resultString;
    CtiReturnMsg *retMsg;

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
                                                       InMessage->Return.TrxID,
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
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.push_back(retMsg);
    }

    return ErrReturn;
}


INT CtiDeviceFMU::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(InMessage->Return.CommandStr),
                                                     string(),
                                                     InMessage->EventCode & 0x7fff,
                                                     InMessage->Return.RouteID,
                                                     InMessage->Return.MacroOffset,
                                                     InMessage->Return.Attempt,
                                                     InMessage->Return.TrxID,
                                                     InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointSPtr     commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    resetScanFlag();

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

            retList.push_back( pMsg );
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}

LONG CtiDeviceFMU::getAddress() const
{
    return getIED().getSlaveAddress();
}

int CtiDeviceFMU::recvCommRequest(OUTMESS *OutMessage)
{
    _outbound = *OutMessage;
    _state    = State_Output;
    _error_count    = 0;
    _codes_received = 0;
    _code_len       = 0;
    _endOfTransactionFlag = false;
    return 0;
}

int CtiDeviceFMU::sendCommResult(INMESS *InMessage)
{
    InMessage->Buffer.InMessage[0] = _codes_received;

    return 0;
}


int CtiDeviceFMU::generate(CtiXfer &xfer)
{
    switch( _state )
    {
        case State_Output:
        {

            // State_Output the message to the remote
            xfer.setOutBuffer(_outbound.Buffer.OutMessage);
            xfer.setOutCount(_outbound.OutLength);

            if (!_endOfTransactionFlag) 
            {
                //_code_len = getExpectedCodeLength();
                _state = State_Input; // no matter
            }
            _state = State_Input;
            //EXPECTING HEADER...
            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8);
            xfer.setInCountActual(&_in_actual);
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

                _code_len = 0;
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
            xfer.setOutCount(10);
            xfer.setInCountExpected(0);

            break;
        }
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - unknown state in CtiDeviceFMU::generate() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


int CtiDeviceFMU::decode(CtiXfer &xfer,  int status)
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
                //if _code_len == 0, decode header...
                if( !_code_len )
                {
                    if( _in_actual >= 8 )
                    {
                        int status = 0;
                        if (getFMUProtocol().decodeHeader(xfer, status) ) 
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - No code length for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        else
                        {
                            if (getFMUProtocol().getAddress() == getAddress()) 
                            {
                                if (getFMUProtocol().getSequence() != _sequence) 
                                {
                                    //create NAK and reset Seq request.
                                    // FLAG NAK, FLAG RESET SEQ
                                    USHORT cmd = Cti::Protocol::fmuProtocol::nak;
                                    BYTE opData = NAK_ACTION_RESET_SEQUENCE | NAK_INVALID_SEQUENCE;
                                    getFMUProtocol().generate(_outbound.Buffer.OutMessage, _cmd_len, getAddress(), _sequence + 1, cmd, &opData);

                                    
                                }
                                else
                                {
                                    _code_len = getFMUProtocol().getDataLen();
                                    //_expectMore = TRUE;
                                }
                            }
                            else
                            {
                                //create NAK and reset Seq request.
                                // FLAG NAK, FLAG RESET SEQ
                                USHORT cmd = Cti::Protocol::fmuProtocol::nak;
                                BYTE opData = NAK_INVALID_CMD;
                                getFMUProtocol().generate(_outbound.Buffer.OutMessage, _cmd_len, getAddress(), _sequence + 1, cmd, &opData);



                            }
                        }
                    }
                    else
                    {
                        _error_count++;
                    }
                }
                //if _code_len > 0, decode data portion of packet...
                else
                {
                    if( _in_actual >= _code_len )
                    {
                        int status = 0;
                        if (getFMUProtocol().decodeData(xfer, status) ) 
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - No code length for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        else
                        {
                            
                        }
                    }
                    else
                    {
                        _error_count++;
                    }


                   /* CtiVerificationReport *report;
                    int tms_result;
                    bool bad_code = false;
                    if (!bad_code) 
                    {
                        string codestr("-");
                        string cmdStr = CtiProtocolSA3rdParty::asString(sacode);
                        CtiVerificationBase::Protocol prot_type;
                        switch(sacode.type)
                        {
                        case VERSACOM:
                            prot_type = CtiVerificationBase::Protocol_Versacom;
                            codestr   = getFmuMsg()[i].code;
                            break;
                        case EXPRESSCOM:
                            prot_type = CtiVerificationBase::Protocol_Expresscom;
                            codestr   = protFmu.code;
                            break;
                        }
                    } */
                    
                    //  this way, we acknowledge the code even if it made procTMSmsg() pop
                    
                    /*report = CTIDBG_new CtiVerificationReport(prot_type, getID(), codestr, second_clock::universal_time(), cmdStr);
                    _verification_objects.push(report);
                     */
                    _code_len = 0;

                    _outbound.Buffer.OutMessage[0] = '0xE7';
                    _outbound.Buffer.OutMessage[1] = '0xff';
                    _outbound.Buffer.OutMessage[2] = _inbound[_code_len+6];
                    _outbound.Buffer.OutMessage[3] = _inbound[_code_len+7];

                    _state = State_Ack;
                }

                break;
            }
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unknown state in CtiDeviceFMU::decode() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return status;
}


bool CtiDeviceFMU::isTransactionComplete()
{
    return ((_error_count > MaxErrors) || (_state == State_Complete));
}

void CtiDeviceFMU::getVerificationObjects(queue<CtiVerificationBase *> &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

CtiProtocolFMU CtiDeviceFMU::getFMUProtocol()
{
    return _fmuProtocol;
}
