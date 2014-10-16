#include "precompiled.h"

#include "cparms.h"
#include "dsm2err.h"
#include "dev_rtc.h"

#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa305.h"
#include "prot_sa3rdparty.h"
#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"

#include "verification_objects.h"

#include "numstr.h"

using std::string;
using std::endl;
using std::list;
using std::make_pair;
using std::queue;
using std::pair;
using namespace boost::posix_time;

ptime::time_duration_type getRTCVerificationTimeoutDuration()
{
    return seconds(gConfigParms.getValueAsInt("RTC_VERIFICATION_DURATION", 300));
}

CtiDeviceRTC::CtiDeviceRTC() :
_millis(0)
{
}

CtiDeviceRTC::~CtiDeviceRTC()
{
    _workQueue.clearAndDestroy();

    while(!_repeatList.empty())
    {
        CtiRepeatCol::value_type repeat = _repeatList.front();
        _repeatList.pop_front();

        CtiOutMessage *pOM = repeat.second;
        delete pOM;
    }

    while(!_verification_objects.empty())
    {
        CtiVerificationBase * pV = (CtiVerificationBase *)_verification_objects.front();
        _verification_objects.pop();
        delete pV;
    }
}

YukonError_t CtiDeviceRTC::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
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


YukonError_t CtiDeviceRTC::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "IntegrityScan for \"" << getName() << "\"");
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


YukonError_t CtiDeviceRTC::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    OutMessage->Buffer.SASt._commandType = parse.getCommand();

    switch(parse.getCommand())
    {
    case LoopbackRequest:
    case ScanRequest:
        {
            OutMessage->DeviceID = getID();
            OutMessage->Port = getPortID();

            CTILOG_INFO(dout, getName() <<" Status scan request");

            outList.push_back(OutMessage);
            OutMessage = 0;
            break;
        }
    case ControlRequest:
        {
            CTILOG_ERROR(dout, "Unexpected ControlRequest command");
        }
    case GetStatusRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = ClientErrors::NoMethodForExecuteRequest;
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("RTC Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.RetryMacroOffset,
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


    return nRet;
}


YukonError_t CtiDeviceRTC::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const YukonError_t ErrReturn = InMessage.ErrorCode;

    string resultString;

    if( !ErrReturn )
    {
        if(InMessage.InLength >= 7 && InMessage.Buffer.InMessage[1] == 0x23)  // This is the STATUS POLL RESPONSE.
        {
            BYTE relayStatus = InMessage.Buffer.InMessage[2] & 0x3f;           // Bit 0 is relay 1 Bit 5 is relay 6.
            BYTE statusInput = (InMessage.Buffer.InMessage[3] & 0x0c) >> 2;    // Bit 0 is input 1, Bit 1 is input 2.
            BYTE latchedStatusInput = (InMessage.Buffer.InMessage[3] & 0x03);  // Bit 0 is input 1, Bit 1 is input 2.
            bool timeSlotAbort = (InMessage.Buffer.InMessage[3] & 0x10);       // true if the key-up aborted due to LBT.  Cleared by successful keyup.
            BYTE statusChanged = (InMessage.Buffer.InMessage[3] & 0x60) >> 5;  // Bit 0 is input 1, Bit 1 is input 2.

        }
        else if(InMessage.InLength == 2 && ((InMessage.Buffer.InMessage[0] & 0x0f) == getAddress()))
        {
            // This is quite likely a MESSAGE ACKNOWLEGEMENT.
            CTILOG_INFO(dout, "Received MESSAGE ACKNOWLEGDED");
        }
        else
        {
            CTILOG_ERROR(dout, "Received unknown response type");
        }

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(InMessage.Return.CommandStr),
                                                       getName() + " / operation complete",
                                                       InMessage.ErrorCode,
                                                       InMessage.Return.RouteID,
                                                       InMessage.Return.RetryMacroOffset,
                                                       InMessage.Return.Attempt,
                                                       InMessage.Return.GrpMsgID,
                                                       InMessage.Return.UserID);

        retList.push_back(retMsg);
    }
    else
    {
        const string error_str = GetErrorString(ErrReturn);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(InMessage.Return.CommandStr),
                                                       resultString,
                                                       ErrReturn,
                                                       InMessage.Return.RouteID,
                                                       InMessage.Return.RetryMacroOffset,
                                                       InMessage.Return.Attempt,
                                                       InMessage.Return.GrpMsgID,
                                                       InMessage.Return.UserID);

        retList.push_back(retMsg);
    }

    return ErrReturn;
}


YukonError_t CtiDeviceRTC::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t retCode = ClientErrors::None;

    CtiCommandParser  parse(InMessage.Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(InMessage.Return.CommandStr),
                                                     string(),
                                                     InMessage.ErrorCode,
                                                     InMessage.Return.RouteID,
                                                     InMessage.Return.RetryMacroOffset,
                                                     InMessage.Return.Attempt,
                                                     InMessage.Return.GrpMsgID,
                                                     InMessage.Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointSPtr     commPoint;

    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    if( pPIL != NULL )
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if(pMsg != NULL)
        {
            pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(CtiCommandMsg::OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            pMsg->insert(
                    InMessage.ErrorCode
                        ? InMessage.ErrorCode
                        : ClientErrors::GeneralScanAborted);

            retList.push_back( pMsg );
        }

        delete pPIL;
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceRTC::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}

string CtiDeviceRTC::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, DUS.baudrate, "
                                     "RTC.rtcaddress, RTC.response, RTC.lbtmode "
                                   "FROM Device DV, DeviceRTC RTC, DeviceDirectCommSettings CS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = RTC.deviceid AND YP.paobjectid = DV.deviceid AND "
                                     "YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void CtiDeviceRTC::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _rtcTable.DecodeDatabaseReader(rdr);

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
}

LONG CtiDeviceRTC::getAddress() const
{
    return _rtcTable.getRTCAddress();
}

INT CtiDeviceRTC::queueRepeatToDevice(OUTMESS *&OutMessage)
{
    INT status = ClientErrors::None;

    if(OutMessage->Buffer.SASt._groupType == SA205 || OutMessage->Buffer.SASt._groupType == SA305 || OutMessage->Buffer.SASt._groupType == SA105)
    {
        _millis += messageDuration(OutMessage->Buffer.SASt._groupType);

        OutMessage->MessageFlags |= MessageFlag_QueuedToDevice;
        _repeatList.push_back( make_pair(_repeatTime, OutMessage) );

        if(OutMessage->Buffer.SASt._groupType == SA305)
        {
            CTILOG_INFO(slog, getName() <<": Requeued for "<< _repeatTime <<" repeat transmit. ACH... ");
        }
        else
        {
            CTILOG_INFO(slog, getName() <<": Requeued for "<< _repeatTime << " repeat transmit: " << CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt));
        }

        OutMessage= 0;

        status = ClientErrors::QueuedToDevice;
    }

    return status;
}

YukonError_t CtiDeviceRTC::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    YukonError_t status = ClientErrors::None;

    if( !(MessageFlag_QueuedToDevice & OutMessage->MessageFlags) && hasExclusions())
    {
        _millis += messageDuration(OutMessage->Buffer.SASt._groupType);

        OutMessage->MessageFlags |= MessageFlag_QueuedToDevice; // This OM has already been queued to the device once.

        _workQueue.putQueue(OutMessage);
        OutMessage= 0;

        if(dqcnt)
        {
            *dqcnt = (UINT)_workQueue.entries();
        }

        status = ClientErrors::QueuedToDevice;
    }

    return status;
}

unsigned CtiDeviceRTC::queuedWorkCount() const
{
    return _workQueue.entries();
}

bool CtiDeviceRTC::hasQueuedWork() const
{
    bool hasq = false;
    CtiTime now;

    if(!_repeatList.empty())
    {
        CtiRepeatCol::const_reference repeat = _repeatList.front();

        if(repeat.first <= now)
        {
            // This is a ready list entry.
            hasq = true;
        }
    }

    if( !hasq && _workQueue.entries() > 0  )
    {
        hasq = true;
    }

    return hasq;
}

bool CtiDeviceRTC::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool gotOM = false;

    CtiTime now;
    CtiRepeatCol::value_type repeat;

    OutMessage = 0;

    if(!_repeatList.empty() && (repeat = _repeatList.front()).first <= now)
    {
        _repeatList.pop_front();
        OutMessage = repeat.second;
        OutMessage->Buffer.SASt._retransmit = TRUE;     // This makes the slog say retransmit.
        gotOM = true;
    }
    else
    {
        do
        {
            OutMessage = _workQueue.getQueue( 500 );

            if(OutMessage && OutMessage->ExpirationTime > 0 && OutMessage->ExpirationTime < now.seconds())
            {
                CTILOG_WARN(dout, "Expired command on "<< getName() <<"'s device queue. Expired at "<< CtiTime(OutMessage->ExpirationTime) <<"  ET = "<< OutMessage->ExpirationTime);

                delete OutMessage;
                OutMessage = 0;
            }

        } while( !OutMessage && _workQueue.entries() );

        if(OutMessage)
            gotOM = true;
    }

    if(gotOM)
    {
        _millis -= messageDuration(OutMessage->Buffer.SASt._groupType);

        if(_millis < 0) _millis = 0;
    }

    return gotOM;
}

LONG CtiDeviceRTC::deviceQueueCommunicationTime() const
{
    LONG millis = _millis;

    if(millis > 0)
    {
        millis += gConfigParms.getValueAsULong("PORTER_RTC_TIME_TRANSMIT", 0);
    }

    LONG fudge = gConfigParms.getValueAsULong("PORTER_RTC_FUDGE", 0);

    if(fudge > 0)
    {
        millis += fudge;
    }

    return millis;
}

LONG CtiDeviceRTC::deviceMaxCommunicationTime() const
{
    return gConfigParms.getValueAsULong("PORTER_MAX_TRANSMITTER_TIME", 0);
}


INT CtiDeviceRTC::prepareOutMessageForComms(CtiOutMessage *&OutMessage)
{
    CtiTime now;
    INT status = ClientErrors::None;

    CtiOutMessage *rtcOutMessage = 0;       // This pointer is used to process the queued outmessages on this device.

    INT codecount = 1;      // One is for The one in OutMessage.

    ULONG msgMillis = 0;

    try
    {
        if(OutMessage->Buffer.SASt._commandType == ControlRequest ||
           OutMessage->Buffer.SASt._commandType == PutConfigRequest)
        {
            writeCodeToSimulatorLog(OutMessage->Buffer.SASt);

            // Any repeats that are generated here should be sent out in the next cycle, or 5 minutes from now!
            _repeatTime = getExclusion().getNextTimeSlotOpen();

            if(OutMessage->Retry-- > 0)
            {
                // This OM needs to be plopped on the retry queue!
                CtiOutMessage *omcopy = new CtiOutMessage(*OutMessage);
                queueRepeatToDevice(omcopy);
            }

            msgMillis += messageDuration(OutMessage->Buffer.SASt._groupType);

            addVerificationForOutMessage(*OutMessage);

            while( codecount <= gConfigParms.getValueAsULong("PORTER_SA_RTC_MAXCODES",35) && ((now + (msgMillis / 1000) + 1) < getExclusion().getExecutingUntil()) && getOutMessage(rtcOutMessage) )
            {
                if( OutMessage->OutLength + 10 + rtcOutMessage->OutLength <= sizeof(OutMessage->Buffer.OutMessage) )     // 10 is for the time slot message.
                {
                    addVerificationForOutMessage(*rtcOutMessage);

                    now = now.now();
                    codecount++;
                    memcpy((char*)(&OutMessage->Buffer.OutMessage[OutMessage->OutLength]), (char*)rtcOutMessage->Buffer.SASt._buffer, rtcOutMessage->OutLength);
                    OutMessage->OutLength += rtcOutMessage->OutLength;

                    msgMillis += messageDuration(rtcOutMessage->Buffer.SASt._groupType);

                    writeCodeToSimulatorLog(rtcOutMessage->Buffer.SASt);

                    if(rtcOutMessage->Retry-- > 0)
                    {
                        // This OM needs to be plopped on the retry queue!
                        queueRepeatToDevice(rtcOutMessage);
                    }

                    delete rtcOutMessage;
                    rtcOutMessage = 0;
                }
                else
                {
                    if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                    {
                        CTILOG_DEBUG(dout, "OutMessage buffer is FULL");
                    }

                    rtcOutMessage->Priority = rtcOutMessage->Priority + 1 > MAXPRIORITY ? MAXPRIORITY : rtcOutMessage->Priority + 1;          // Try to keep the order;
                    _workQueue.putQueue(rtcOutMessage); // Put it back, the OutMessage buffer is FULL.
                    rtcOutMessage = 0;

                    break;
                }
            }

            if(rtcOutMessage)   // This means we be leaking.
            {
                CTILOG_ERROR(dout, "MEMORY LEAK detected");
            }

            CtiProtocolSA3rdParty().appendVariableLengthTimeSlot(getAddress(),
                                                                 OutMessage->Buffer.OutMessage,
                                                                 OutMessage->OutLength,
                                                                 OutMessage->Buffer.SASt._delayToTx,
                                                                 OutMessage->Buffer.SASt._maxTxTime,
                                                                 OutMessage->Buffer.SASt._lbt);

            getExclusion().setEvaluateNextAt((now + (msgMillis / 1000) + 1));


            CTILOG_INFO(slog, getName() <<" transmitting "<< msgMillis <<" \"RF\" milliseconds of codes.  Completes at "<< (now + (msgMillis / 1000) + 1) <<" < "<< getExclusion().getExecutingUntil());
        }
        else if(OutMessage->Buffer.SASt._commandType == ScanRequest || OutMessage->Buffer.SASt._commandType == LoopbackRequest)
        {
            CtiProtocolSA3rdParty().statusScan(getAddress(), OutMessage->Buffer.OutMessage, OutMessage->OutLength);
            OutMessage->InLength = 7;
            OutMessage->EventCode |= RESULT | WAIT;
            OutMessage->TimeOut = 2;
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}


void CtiDeviceRTC::writeCodeToSimulatorLog(const CtiSAData &SASt) const
{
    if( SASt._groupType == SA305 )
    {
        CtiProtocolSA305 prot( SASt._buffer, SASt._bufferLen );
        prot.setTransmitterType(getType());
        string cmdStr = prot.getDescription();

        CTILOG_INFO(slog, getName() <<": "<< cmdStr);
    }
    else
    {
        CTILOG_INFO(slog, getName() << ": "<< CtiProtocolSA3rdParty::asString(SASt));
    }
}


void CtiDeviceRTC::addVerificationForOutMessage(CtiOutMessage &OutMessage)
{
    string code;
    string cmdStr;

    const ptime::time_duration_type timeout = getRTCVerificationTimeoutDuration();

    if( !OutMessage.VerificationSequence )
    {
        OutMessage.VerificationSequence = VerificationSequenceGen();
    }

    if( OutMessage.Buffer.SASt._code305[0] != '\0' )
    {
        CtiProtocolSA305 prot( OutMessage.Buffer.SASt._buffer, OutMessage.Buffer.SASt._bufferLen );
        prot.setTransmitterType(getType());

        cmdStr = prot.getDescription();
        code   = prot.getAsciiString();

        _verification_objects.push(new CtiVerificationWork(CtiVerificationBase::Protocol_SA305, OutMessage, cmdStr, code, timeout));
    }
    else if( OutMessage.Buffer.SASt._code205 )
    {
        code = CtiNumStr(OutMessage.Buffer.SASt._code205).zpad(6);
        cmdStr = CtiProtocolSA3rdParty::asString(OutMessage.Buffer.SASt);

        if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
        {
            CTILOG_DEBUG(dout, getName() << " : " << cmdStr);
        }

        _verification_objects.push(new CtiVerificationWork(CtiVerificationBase::Protocol_SA205, OutMessage, cmdStr, code, timeout));
    }
    else if( OutMessage.Buffer.SASt._groupType == SADIG )
    {
        char codestr[8] = {};  //  default initialize all elements to 0
        strncpy(codestr, OutMessage.Buffer.SASt._codeSimple, 7);
        cmdStr = CtiProtocolSA3rdParty::asString(OutMessage.Buffer.SASt);

        if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
        {
            CTILOG_DEBUG(dout, getName() << " : " << cmdStr);
        }

        _verification_objects.push(new CtiVerificationWork(CtiVerificationBase::Protocol_SADigital, OutMessage, cmdStr, codestr, timeout));
    }
    else
    {
        char codestr[7] = {};  //  default initialize all elements to 0
        strncpy(codestr, OutMessage.Buffer.SASt._codeSimple, 6);
        string golay_codestr;
        golay_codestr  = codestr;
        golay_codestr += "-";
        golay_codestr += CtiNumStr(OutMessage.Buffer.SASt._function - 1);  // 0-based to match the RTM's return

        cmdStr = CtiProtocolSA3rdParty::asString(OutMessage.Buffer.SASt);

        if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
        {
            CTILOG_DEBUG(dout, getName() << " : " << cmdStr);
        }

        _verification_objects.push(new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, OutMessage, cmdStr, golay_codestr, timeout));
    }
}


void CtiDeviceRTC::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}


ULONG CtiDeviceRTC::messageDuration(const int groupType)
{
    switch( groupType )
    {
        case SA205:  return gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_205_CODE", 1000);
        case SA305:  return gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_305_CODE", 1000);
        default:     return gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_SIMPLE_CODE", 250);
    }
}
