/*-----------------------------------------------------------------------------*
*
* File:   dev_fmu
*
* Date:   9/26/2006
*
* Author: Julie Richter
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/08/14 15:57:39 $
*
* HISTORY      :
* $Log: dev_fmu.cpp,v $
* Revision 1.8  2008/08/14 15:57:39  jotteson
* YUK-6333  Change naming in request message and change cancellation to use this new named field instead of user ID
* Cancellation now uses the new group message ID.
* Group Message ID name added to Request, Result, Out, and In messages.
*
* Revision 1.7  2008/06/06 20:28:01  jotteson
* YUK-6005 Porter LLP expect more set incorrectly
* Added an option to override expect more in the error decode call.
* Made LLP retry 3 times before failing.
*
* Revision 1.6  2007/09/24 19:48:36  mfisher
* changed DawnOfTime to an enum to prevent tagging difficulties
*
* Revision 1.5  2007/04/27 16:49:18  mfisher
* moved CVS header to top so Slick's diff would ignore it
*
* Revision 1.4  2007/04/18 20:04:12  mfisher
* YUK-3192
* Changed CtiString::appendLong() to CtiString::append() so that you can append nulls to the raw config string
*
* Revision 1.3  2007/02/12 19:19:16  jotteson
* Communications with the FMU are now working.
*
* Revision 1.2  2007/01/30 18:16:25  jrichter
* A little bit cleaned up...
*
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
#include "numstr.h"
#include "ctistring.h"

#define StartByte 0xE7

using std::string;
using std::endl;
using std::list;
using std::queue;
using namespace boost::posix_time;

CtiDeviceFMU::CtiDeviceFMU() :
    _state(State_Uninit),
    _sequence(SequenceUnknown),
    _cmd(0),
    _cmd_len(0),
    _code_len(0),
    _codes_received(0),
    _endOfTransactionFlag(false),
    _error_count(0),
    _in_actual(0),
    _in_expected(0),
    _in_remaining(0),
    _prevState(State_Uninit)
{
    memset(_inbound, 0, sizeof(_inbound));
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
                    int function = DataRequest;
                    int error_code;
                    BYTE *opData = NULL;

                    OutMessage->DeviceID = getID();
                    OutMessage->TargetID = getID();
                    OutMessage->Port = getPortID();
                    OutMessage->Function = function;
                    OutMessage->EventCode = RESULT | ENCODED;

                    outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                    resultString = " Command successfully sent on route " + getName() + "\n";

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
        case GetConfigRequest:
        {
            if(parse.isKeyValid("time") )
            {
                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->Function = TimeRead;
                OutMessage->EventCode = RESULT | ENCODED;

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                resultString = " Command successfully sent on route " + getName() + "\n";

            }
            break;
        }
        case PutValueRequest:
        case PutConfigRequest:
        {
            if(parse.isKeyValid("timesync") )
            {
                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->Function = TimeSend;
                OutMessage->EventCode = RESULT | ENCODED;

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                resultString = " Command successfully sent on route " + getName() + "\n";
            }
            else if ( CtiString(parse.getCommandStr()).contains(" locom") )
            {
                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->Function = LoComCommand;
                OutMessage->EventCode = RESULT | ENCODED;

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                resultString = " Command successfully sent on route " + getName() + "\n";
            }
            else if ( CtiString(parse.getCommandStr()).contains(" external") )
            {
                OutMessage->DeviceID = getID();
                OutMessage->TargetID = getID();
                OutMessage->Port = getPortID();
                OutMessage->Function = ExternalDevCommand;
                OutMessage->EventCode = RESULT | ENCODED;

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                resultString = " Command successfully sent on route " + getName() + "\n";
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
            break;
        }

        case ControlRequest:
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
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

    CtiReturnMsg *retReturn = NULL;
    if( OutMessage )
    {
        retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());
    }

    if( retReturn != NULL )
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

    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        string result_string;

        //  safety first
        if( InMessage->InLength > sizeof(InMessage->Buffer.InMessage) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint InMessage->InLength > sizeof(InMessage->Buffer.InMessage) for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            InMessage->InLength = sizeof(InMessage->Buffer.InMessage);
        }
        InMessage->Buffer.InMessage[InMessage->InLength - 1] = 0;

        result_string.assign(reinterpret_cast<char *>(InMessage->Buffer.InMessage), InMessage->InLength);

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage->Return.CommandStr),
                                         result_string.data(),
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
        string resultString;

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage->Return.CommandStr),
                                         resultString,
                                         InMessage->EventCode & 0x7fff,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.GrpMsgID,
                                         InMessage->Return.UserID);

        retList.push_back(retMsg);
    }

    return ErrReturn;
}

INT CtiDeviceFMU::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, list< CtiMessage* > &retList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage.Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(InMessage.Return.CommandStr),
                                                     string(),
                                                     InMessage.EventCode & 0x7fff,
                                                     InMessage.Return.RouteID,
                                                     InMessage.Return.MacroOffset,
                                                     InMessage.Return.Attempt,
                                                     InMessage.Return.GrpMsgID,
                                                     InMessage.Return.UserID);
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
    if( OutMessage->Function == DataRequest )
    {
        _state = State_Request_Data;
    }
    else if( OutMessage->Function == TimeSend )
    {
        _state = State_Send_Time_Sync;
    }
    else if( OutMessage->Function == TimeRead )
    {
        _state = State_Read_Time;
    }
    else if( OutMessage->Function == LoComCommand )
    {
        _state = State_Send_LoCom_Command;
        strncpy(_outbound.Request.CommandStr, OutMessage->Request.CommandStr, COMMAND_STR_SIZE );
    }
    else if( OutMessage->Function == ExternalDevCommand )
    {
        _state = State_Send_Direct_Command;
        strncpy(_outbound.Request.CommandStr, OutMessage->Request.CommandStr, COMMAND_STR_SIZE );
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _error_count    = 0;
    _codes_received = 0;
    _code_len       = 0;
    _endOfTransactionFlag = false;
    return 0;
}

int CtiDeviceFMU::sendCommResult(INMESS *InMessage)
{
    char * buf = reinterpret_cast<char *>(InMessage->Buffer.InMessage);
    string result_string;
    stringlist_t::iterator itr;

    for( itr = _stringList.begin(); itr != _stringList.end(); itr++ )
    {
        result_string += *(*itr);
        result_string += "\n";
    }

    while( !_stringList.empty() )
    {
        delete _stringList.back();

        _stringList.pop_back();
    }

    if( result_string.size() >= sizeof(InMessage->Buffer.InMessage) )
    {
        //  make sure we complain about it so we know the magnitude of the problem when people bring it up...
        //    one possible alternative is to send multple InMessages across with the string data - although,
        //    considering that the largest message I saw was on the order of 60k, sending 15 InMessages is not very appealing
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Info - result_string.size = " << result_string.size() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        string cropped("\n---cropped---");

        //  erase the end chunk so we can append the "cropped" string in
        result_string.erase(sizeof(InMessage->Buffer.InMessage) - cropped.size() - 1, result_string.size());
        result_string += cropped;
    }

    InMessage->InLength = result_string.size() + 1;

    //  make sure we don't overrun the buffer, even though we just checked above
    strncpy(buf, result_string.c_str(), sizeof(InMessage->Buffer.InMessage) - 1);
    //  and mark the end with a null, again, just to be sure
    InMessage->Buffer.InMessage[sizeof(InMessage->Buffer.InMessage) - 1] = 0;

    return NoError;
}


int CtiDeviceFMU::generate(CtiXfer &xfer)
{
    if( _sequence == SequenceUnknown )
    {
        _prevState = _state;
        _state = State_Uninit;
    }
    switch( _state )
    {
        case State_Uninit:
        {
            setupResetSequenceMessage(xfer);

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(10);
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Request_Data:
        {
            setupRequestDataMessage(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Send_Reset_Log:
        {
            setupResetLogMessage(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Send_Time_Sync:
        {
            setupTimeSyncMessage(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Read_More:
        {
            setupReadMore(xfer);
            break;
        }

        case State_Read_Time:
        {
            setupTimeReadMessage(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Send_LoCom_Command:
        {
            setupLoComCommand(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Send_Direct_Command:
        {
            setupExternalDevCommand(xfer);
            _prevState = _state;

            xfer.setInBuffer(_inbound);
            xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
            xfer.setInCountActual(&_in_actual);
            break;
        }

        case State_Do_Nothing:
        {
            _state = _prevState;
            break;
        }

        case State_Ack_Continue:
        case State_Ack_Complete:
        {
            int length = 0;
            int data;

            if( _state == State_Ack_Continue )
            {
                data = 0x80;
                xfer.setInBuffer(_inbound);
                xfer.setInCountExpected(8); //I am reading out 8 bytes, the data follows this....
                xfer.setInCountActual(&_in_actual);
            }
            else
            {
                data = 0x40;
                xfer.setInCountExpected(0);
            }
            setupHeader(xfer, AckResponse, length, SequenceFlagEnd | SequenceFlagStart);
            xfer.getOutBuffer()[length++] = 1;    //Data length
            xfer.getOutBuffer()[length++] = data; //Data Complete

            USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
            xfer.getOutBuffer()[length++] = crc1;
            xfer.getOutBuffer()[length++] = crc1 >> 8;
            xfer.setOutCount(length);
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
    else // status == NORMAL;
    {
        switch( _state )
        {
            case State_Uninit:
            {
                // This should mean that I just asked for a sequence change, see if we had an ack.
                if( xfer.getInBuffer()[6] == AckResponse )
                {
                    _sequence = 0;
                    if( _prevState != _state )
                    {
                        _state = _prevState;
                    }
                    else
                    {
                        _state = State_Complete;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Unable to reset sequence on device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }

            case State_Send_LoCom_Command:
            case State_Read_Time:
            case State_Send_Time_Sync:
            case State_Send_Reset_Log:
            case State_Ack_Continue:
            case State_Request_Data:
            {
                _sequence ++;
                _state = State_Read_More;
                _in_remaining = xfer.getInBuffer()[7];
                _in_remaining += 2;
                break;
            }

            case State_Read_More: //Remember for this call we messed with the inBuffer location, so getInBuffer is wrong.
            {
                if( checkMessageCRC() )
                {
                    if( _inbound[6] == DataResponse )
                    {
                        decodeDataRead();
                        if( _inbound[5] & SequenceFlagEnd )
                        {
                            _state = State_Ack_Complete;
                        }
                        else
                        {
                            _state = State_Ack_Continue;
                        }
                    }
                    else if( _inbound[6] == TimeResponse )
                    {
                        CtiTime now;
                        ULONG sec = ((ULONG) _inbound[8]  << 24) |
                                    ((ULONG) _inbound[9]  << 16) |
                                    ((ULONG) _inbound[10] << 8)  |
                                    ((ULONG) _inbound[11]);
                        sec += DawnOfTime;
                        _stringList.push_back(CTIDBG_new string("Current Time is: " + now.asString() + " received time is: " + CtiTime(sec).asString()));
                        _state = State_Complete;
                    }
                    else if( _inbound[6] == LoCommResponse || _inbound[6] == ExternalDevResponse )
                    {
                        decodeGenericResponse();
                        _state = State_Complete;
                    }
                    else if( _inbound[6] == AckResponse )
                    {
                        // Everyone is happy.... What do we do now?
                        if( _inbound[8] & 0x40  )
                        {
                            //Complete
                            _state = State_Complete;
                        }
                        else
                        {
                            _state = State_Complete;
                            status = NoMethod;
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - Ack not decoded properly \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else if( _inbound[6] == NakResponse )
                    {
                        nakDecode();
                        status = NACK1;
                        if( _inbound[8] & 0x80 )
                        {
                            //Retry
                            _error_count++;
                            _state = _prevState;
                        }
                        else if( _inbound[8] & 0x02 )
                        {
                            //reset sequence
                            _state = State_Uninit;
                        }
                        else
                        {
                            _error_count ++;
                            _state = State_Complete;
                            status = UnknownError;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - Incorrect Value Received \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Bad CRC Received on Device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    //_state = State_Send_Nak;
                }

                break;
            }

            case State_Ack_Complete:
            {
                _state = State_Complete;
                break;
            }

            case State_Complete:
            {
                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Default State \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                _state = State_Complete;
                break;
            }
        }
    }

    return status;
}

// Sets up the first few bytes of each message. Stops at the data length and data fields.
// Note that a sequence > 31 is not technically correct, but incrementing above this does
// Produce the correct result.
void CtiDeviceFMU::setupHeader(CtiXfer &xfer, UCHAR command, int &length, UCHAR sequenceFlags)
{
    length = 7;
    int address = getAddress();
    xfer.setOutBuffer(_outbound.Buffer.OutMessage);
    xfer.setInBuffer(_inbound);
    xfer.setInCountActual(&_in_actual);

    _outbound.Buffer.OutMessage[0] = StartByte;
    _outbound.Buffer.OutMessage[1] = address >> 24;
    _outbound.Buffer.OutMessage[2] = address >> 16;
    _outbound.Buffer.OutMessage[3] = address >> 8;
    _outbound.Buffer.OutMessage[4] = address;
    _outbound.Buffer.OutMessage[5] = (_sequence & 0x3F) | (sequenceFlags & 0xC0);
    _outbound.Buffer.OutMessage[6] = command;

    _sequence ++;
}

void CtiDeviceFMU::setupResetSequenceMessage(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, NakResponse, length, sequenceFlags);

    xfer.getOutBuffer()[length++] = 1;    //Data length
    xfer.getOutBuffer()[length++] = 0x40; //Reset Sequence

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupRequestDataMessage(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, DataRequest, length, sequenceFlags);

    xfer.getOutBuffer()[length++] = 1;    //Data length
    xfer.getOutBuffer()[length++] = 0x01; //Read All New Data

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupResetLogMessage(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, ResetLog, length, sequenceFlags);

    xfer.getOutBuffer()[length++] = 0;    //Data length

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupTimeSyncMessage(CtiXfer &xfer)
{
    int length = 0;
    ULONG timestamp;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, TimeSend, length, sequenceFlags);

    xfer.getOutBuffer()[length++] = 4;    //Data length

    timestamp = CtiTime::now().seconds();
    timestamp = timestamp - DawnOfTime;
    xfer.getOutBuffer()[length++] = timestamp >> 24;
    xfer.getOutBuffer()[length++] = timestamp >> 16;
    xfer.getOutBuffer()[length++] = timestamp >> 8;
    xfer.getOutBuffer()[length++] = timestamp;

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupTimeReadMessage(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, TimeRead, length, sequenceFlags);

    xfer.getOutBuffer()[length++] = 0;    //Data length

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupExternalDevCommand(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    CtiString rawData;
    char *p;
    CtiString cmdStr(_outbound.Request.CommandStr);

    setupHeader(xfer, ExternalDevCommand, length, sequenceFlags);

    if(!(cmdStr = cmdStr.match(" external *=( *0x[a-zA-Z0-9]+)+")).empty())
    {
        CtiTokenizer cmdtok(cmdStr);
        CtiString temp;
        int rawloc;

        //  go past "external"
        cmdtok(" =");

        while( !(temp = cmdtok(" =")).empty() )
        {
            rawData.append(1, (char)strtol(temp.c_str(), &p, 16));
        }
    }

    xfer.getOutBuffer()[length++] = rawData.length();    //Data length

    for( int i = 0; i < _rawData.length(); i++ )
    {
        xfer.getOutBuffer()[length++] = _rawData[i];
    }

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupLoComCommand(CtiXfer &xfer)
{
    int length = 0;
    int sequenceFlags = SequenceFlagStart | SequenceFlagEnd;
    setupHeader(xfer, LoComCommand, length, sequenceFlags);

    CtiString cmdStr(_outbound.Request.CommandStr);

    if(!(cmdStr = cmdStr.match(" locom *= *[a-zA-Z0-9:,-]+")).empty())
    {
        int   i;
        int   val;
        char str[10];
        char *ptr = NULL;

        cmdStr = cmdStr.match("= *[a-zA-Z0-9:,-]+");
        cmdStr = cmdStr.strip(CtiString::leading, '=');
        cmdStr = cmdStr.strip(CtiString::leading, ' ');

        //We now have just the data. lets hope it isnt empty!
        _rawData = cmdStr;
    }

    xfer.getOutBuffer()[length++] = cmdStr.length();    //Data length

    for( int i = 0; i < cmdStr.length(); i++ )
    {
        xfer.getOutBuffer()[length++] = cmdStr[i];
    }

    USHORT crc1 = crc16( xfer.getOutBuffer()+1, length-1 );
    xfer.getOutBuffer()[length++] = crc1;
    xfer.getOutBuffer()[length++] = crc1 >> 8;

    xfer.setOutCount(length);
}

void CtiDeviceFMU::setupReadMore(CtiXfer &xfer)
{
    xfer.setOutCount(0);
    xfer.setInBuffer(_inbound + 8); //Header length
    xfer.setInCountExpected(_in_remaining);
}

// Function to check the CRC of the message in the _inbound data buffer.
// Returns true if the CRC is valid, false if not.
bool CtiDeviceFMU::checkMessageCRC()
{
    UCHAR length = _inbound[7] + 7; //Very fmu dependent

    USHORT crc = crc16(_inbound + 1, length);
    if( (crc & 0x00FF) == _inbound[length + 1] && (crc >> 8) == _inbound[length + 2] )
    {
        return true;
    }
    else
    {
        return false;
    }
}

void CtiDeviceFMU::nakDecode()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint - Nack Reported:";

    if( _inbound[8] & 0x10 )
    {
        dout <<"  Invalid Request";
    }
    else if( _inbound[8] & 0x08 )
    {
        dout <<"  Invalid Data";
    }
    else if( _inbound[8] & 0x04 )
    {
        dout <<"  Invalid Data Length";
    }
    else if( _inbound[8] & 0x02 )
    {
        dout <<"  Invalid Sequence";
    }
    else if( _inbound[8] & 0x01 )
    {
        dout <<"  Invalid Command";
    }
    dout << "  " << __FILE__ << " (" << __LINE__ << ")" << endl;
}

// Function to decode the data in the _inbound data buffer.
void CtiDeviceFMU::decodeDataRead()
{
    int dataLength = _inbound[7];
    int readLoc = 8; //9th byte
    if( dataLength > 0 )
    {
        ULONG timestamp;
        UCHAR flexCode;
        UCHAR rssi;
        UCHAR recvLen;
        string code;
        CtiVerificationBase::Protocol p;

        do
        {
            _codes_received ++;
            timestamp = ((ULONG) _inbound[readLoc++] << 24) |
                        ((ULONG) _inbound[readLoc++] << 16) |
                        ((ULONG) _inbound[readLoc++] << 8)  |
                        ((ULONG) _inbound[readLoc++]);
            timestamp += DawnOfTime;
            flexCode = _inbound[readLoc++];
            rssi     = _inbound[readLoc++];
            recvLen  = _inbound[readLoc++];

            if( _inbound[readLoc] == 'S' || _inbound[readLoc] == 'U' )
            {
                p = CtiVerificationBase::Protocol_Expresscom;
            }
            else if( _inbound[readLoc] == 'H' || _inbound[readLoc] == 0xA5 )
            {
                p = CtiVerificationBase::Protocol_Versacom;
            }
            else
            {
                p = CtiVerificationBase::Protocol_Versacom; //What should I do here? There is no invalid protocol so far.
            }

            code = "";
            for( int i=0; i<recvLen; i++ )
            {
                code += CtiNumStr(_inbound[readLoc++]).hex();
            }

            CtiVerificationReport(p, getID(), code, from_time_t(timestamp));

        } while (readLoc < (recvLen + 7));
    }

    if( _inbound[5] & SequenceFlagEnd )
    {
        _stringList.push_back(CTIDBG_new string("Number of received messages retrieved: " + CtiNumStr(_codes_received)));
    }
}

void CtiDeviceFMU::decodeGenericResponse()
{
    string response = "Device Returned Message (hex): ";
    string strResponse = "Device Returned Message: ";
    int length = _inbound[7];

    for(int i=0; i<length; i++)
    {
        response += CtiNumStr(_inbound[8+i]).hex();
        strResponse += _inbound[8+i];
    }

    _stringList.push_back(CTIDBG_new string(strResponse + "\n" + response));
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

const unsigned short CtiDeviceFMU::crc16table[256] =
{
    0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
    0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
    0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
    0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
    0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
    0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
    0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
    0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
    0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
    0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
    0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
    0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
    0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
    0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
    0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
    0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
    0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
    0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
    0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
    0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
    0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
    0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
    0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
    0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
    0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
    0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
    0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
    0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
    0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
    0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
    0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
    0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
};

unsigned short CtiDeviceFMU::crc16( const unsigned char *data, int length )
{
    unsigned short tmp, crc;

    crc = 0x0000;

    for( int i = 0; i < length; i++ )
    {
        tmp = crc ^ (unsigned short)data[i];
        crc = (crc >> 8) ^ crc16table[tmp & 0x00FF];
    }

    return crc;

}
