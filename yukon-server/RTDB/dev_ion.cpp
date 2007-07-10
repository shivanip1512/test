/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.cpp
 *
 * Class:  CtiDeviceION
 * Date:   07/02/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "porter.h"

#include "tbl_ptdispatch.h"
#include "dev_ion.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "msg_lmcontrolhistory.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "logger.h"
#include "guard.h"

#include "utility.h"

#include "dllyukon.h"
#include "cparms.h"
#include "numstr.h"

using namespace Cti;  //  in preparation for moving devices to their own namespace


CtiDeviceION::CtiDeviceION()
{
//    resetIONScansPending();
}

CtiDeviceION::CtiDeviceION(const CtiDeviceION &aRef)
{
   *this = aRef;
}

CtiDeviceION::~CtiDeviceION() {}

CtiDeviceION &CtiDeviceION::operator=(const CtiDeviceION &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


void CtiDeviceION::setMeterGroupData( const string &meterNumber )
{
    _meterNumber         = meterNumber;
}


Protocol::Interface *CtiDeviceION::getProtocol( void )
{
    return (Protocol::Interface *)&_ion;
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceION::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


INT CtiDeviceION::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    string resultString;

    bool found = false;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                {
                    if( findStringIgnoreCase(pReq->CommandString(),"post_control") )
                    {
                        //  post-control scan
                        _ion.setCommand(CtiProtocolION::Command_ExceptionScanPostControl);
                    }
                    else
                    {
                        //  normal scan
                        _ion.setCommand(CtiProtocolION::Command_ExceptionScan);
                    }

                    found = true;

                    break;
                }

                case ScanRateAccum:
                {
                    initEventLogPosition();

                    _ion.setCommand(CtiProtocolION::Command_EventLogRead);

                    found = true;

                    break;
                }

                case ScanRateIntegrity:
                {
                    _ion.setCommand(CtiProtocolION::Command_IntegrityScan);

                    found = true;

                    break;
                }

                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }
            }

            break;
        }

        case GetStatusRequest:
        {
            if( parse.isKeyValid("eventlog") )
            {
                initEventLogPosition();

                _ion.setCommand(CtiProtocolION::Command_EventLogRead);

                found = true;
            }

            break;
        }

        case PutConfigRequest:
        {
            if( parse.isKeyValid("timesync") )
            {
                _ion.setCommand(CtiProtocolION::Command_TimeSync);

                found = true;
            }

            break;
        }

        case ControlRequest:
        {
            bool has_offset = false;
            unsigned int offset;
            CtiPointSPtr point;

            if( parse.getiValue("point") > 0 )
            {
                if( (point = getDevicePointEqual(parse.getiValue("point"))) && point->isStatus() )
                {
                    if( boost::static_pointer_cast<CtiPointStatus>(point)->getPointStatus().getControlType() == NormalControlType )
                    {
                        offset = boost::static_pointer_cast<CtiPointStatus>(point)->getPointStatus().getControlOffset();
                        has_offset = true;
                    }
                }
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");
                has_offset = true;

                point = getDeviceControlPointOffsetEqual(offset);
            }

            if( parse.getFlags() & CMD_FLAG_CTL_CLOSE && has_offset )
            {
                if( findStringIgnoreCase(gConfigParms.getValueAsString("DUKE_ISSG"),"true") == 0 )
                {
                    if( offset == 20 || offset == 21 )
                    {
                        pReq->setCommandString(pReq->CommandString() + " duke_issg_start");

                        //  call propageteRequest to put the command string into
                        //    pReq again.
                        //  is this a bit hairy?  i think it's okay, but...
                        propagateRequest(OutMessage, pReq);
                    }
                    else if( offset == 22 )
                    {
                        pReq->setCommandString(pReq->CommandString() + " duke_issg_stop");

                        propagateRequest(OutMessage, pReq);
                    }
                }

                if( point )
                {
                    //  NOTE - the control duration is completely arbitrary here.  Fix sometime if necessary
                    //           (i.e. customer doing sheds/restores that need to be accurately LMHist'd)
                    //  Also note that this is sending "CLOSED" as the expected state.
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), point->getPointID(), 1, CtiTime(), 86400, 100);

                    hist->setMessagePriority(hist->getMessagePriority() + 1);
                    vgList.push_back(hist);

                    if(point->isPseudoPoint())
                    {
                        // There is no physical point to observe and respect.  We lie to the control point.
                        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(point->getID(),
                                                                            (DOUBLE)hist->getRawState(),
                                                                            NormalQuality,
                                                                            StatusPointType,
                                                                            string("This point has been controlled"));
                        pData->setUser(pReq->getUser());
                        vgList.push_back(pData);
                    }

                    OutMessage->ExpirationTime = CtiTime().seconds() + point->getControlExpirationTime();
                }

                _ion.setCommand(CtiProtocolION::Command_ExternalPulseTrigger, offset);

                _postControlScanCount = 0;

                found = true;
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command. Command = " << parse.getCommand() << endl;
            }

            break;
        }
    }


    if( found )
    {
        OutMessage->Port         = getPortID();
        OutMessage->DeviceID     = getID();
        OutMessage->TargetID     = getID();
        OutMessage->Retry        = IONRetries;
        OutMessage->Sequence     = _ion.getCommand();
        OutMessage->MessageFlags = _ion.commandRequiresRequeueOnFail(_ion.getCommand()) ? MessageFlag_RequeueCommandOnceOnFail : 0;

        CtiReturnMsg *retmsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       string(OutMessage->Request.CommandStr),
                                                       getName() + " / command submitted",
                                                       nRet,
                                                       OutMessage->Request.RouteID,
                                                       OutMessage->Request.MacroOffset,
                                                       OutMessage->Request.Attempt,
                                                       OutMessage->Request.TrxID,
                                                       OutMessage->Request.UserID,
                                                       OutMessage->Request.SOE,
                                                       CtiMultiMsg_vec());

        retmsg->setExpectMore(true);

        retList.push_back(retmsg);

        //  fills in OutMessage with eventlog position and current command,
        //    then appends to outlist, consuming the outmessage
        _ion.sendCommRequest( OutMessage, outList );
    }
    else
    {
        nRet = NoMethod;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << CtiTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                        string(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.MacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.TrxID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        CtiMultiMsg_vec()));

        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}



void CtiDeviceION::initEventLogPosition( void )
{
    if( _ion.getEventLogLastPosition() == 0 )
    {
        CtiPointAnalogSPtr tmpPoint;
        unsigned long   lastRecordPosition;

        if( tmpPoint = boost::static_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(CtiProtocolION::EventLogPointOffset, AnalogPointType) ))
        {
            CtiTablePointDispatch pd(tmpPoint->getPointID());

            if(pd.Restore().errorCode() == RWDBStatus::ok)
            {
                _ion.setEventLogLastPosition(pd.getValue());
            }
            else
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint: Invalid Reader/No DynamicPointDispatch for EventLog Point - reading ALL events **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
}

INT CtiDeviceION::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority )
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



INT CtiDeviceION::IntegrityScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CtiDeviceION::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan accumulator");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator (EventLog) Scan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan accumulator");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


int CtiDeviceION::ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    list<CtiPointDataMsg*> pointData;
    list<CtiSignalMsg*>    eventData;
    string returnInfo;

    bool expectMore = false;

    string commandStr(InMessage->Return.CommandStr);

    resetScanFlag(ScanRateGeneral);

    if( !ErrReturn && !_ion.recvCommResult(InMessage, outList) )
    {
        _ion.getInboundData(pointData, eventData, returnInfo);

        switch( InMessage->Sequence )
        {
            case CtiProtocolION::Command_ExternalPulseTrigger:
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                expectMore = true;

                CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                 "scan general post_control",
                                                                 InMessage->Return.UserID,
                                                                 InMessage->Return.TrxID,
                                                                 InMessage->Return.RouteID,
                                                                 InMessage->Return.MacroOffset,
                                                                 InMessage->Return.Attempt);

                if( findStringIgnoreCase(commandStr,"duke_issg_start") )
                {
                    newReq->setCommandString(newReq->CommandString() + " duke_issg_start");
                }
                else if( findStringIgnoreCase(commandStr,"duke_issg_stop") )
                {
                    newReq->setCommandString(newReq->CommandString() + " duke_issg_stop");
                }

                newReq->setMessagePriority(MAXPRIORITY);

                newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                CtiCommandParser parse(newReq->CommandString());

                CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                delete newReq;

                break;
            }

            case CtiProtocolION::Command_ExceptionScanPostControl:
            {
                if( _postControlScanCount < IONPostControlScanMax )
                {
                    if( findStringIgnoreCase(commandStr,"duke_issg_start") )
                    {
                        if( _ion.hasPointUpdate(StatusPointType, 1) && _ion.getPointUpdateValue(StatusPointType, 1) == 0 &&
                            _ion.hasPointUpdate(StatusPointType, 2) && _ion.getPointUpdateValue(StatusPointType, 2) == 0 )
                        {
                            expectMore = true;

                            CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                             "scan general post_control duke_issg_start",
                                                                             InMessage->Return.UserID,
                                                                             InMessage->Return.TrxID,
                                                                             InMessage->Return.RouteID,
                                                                             InMessage->Return.MacroOffset,
                                                                             InMessage->Return.Attempt);

                            newReq->setMessagePriority(MAXPRIORITY);

                            newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                            CtiCommandParser parse(newReq->CommandString());

                            CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                            ++_postControlScanCount;

                            delete newReq;
                        }
                    }
                    else if( findStringIgnoreCase(commandStr,"duke_issg_stop") )
                    {
                        if( _ion.hasPointUpdate(StatusPointType, 1) && _ion.getPointUpdateValue(StatusPointType, 1) != 0 ||
                            _ion.hasPointUpdate(StatusPointType, 2) && _ion.getPointUpdateValue(StatusPointType, 2) != 0 )
                        {
                            expectMore = true;

                            CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                             "scan general post_control duke_issg_stop",
                                                                             InMessage->Return.UserID,
                                                                             InMessage->Return.TrxID,
                                                                             InMessage->Return.RouteID,
                                                                             InMessage->Return.MacroOffset,
                                                                             InMessage->Return.Attempt);

                            newReq->setMessagePriority(MAXPRIORITY);

                            newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                            CtiCommandParser parse(newReq->CommandString());

                            CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                            ++_postControlScanCount;

                            delete newReq;
                        }
                    }
                }

                break;
            }

            case CtiProtocolION::Command_EventLogRead:
            {
                if( !_ion.areEventLogsComplete() )
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - submitting request for additional event logs **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    expectMore = true;

                    CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                     "getstatus eventlog",
                                                                     InMessage->Return.UserID,
                                                                     InMessage->Return.TrxID,
                                                                     InMessage->Return.RouteID,
                                                                     InMessage->Return.MacroOffset,
                                                                     InMessage->Return.Attempt);

                    newReq->setMessagePriority(MAXPRIORITY);

                    newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                    CtiCommandParser parse(newReq->CommandString());

                    CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                    delete newReq;
                }
                else
                {
                    CtiRequestMsg *newReq;

                    if( findStringIgnoreCase(gConfigParms.getValueAsString("PORTER_ION_EVENTLOG_TIMESYNCS"),"true") == 0 )
                    {
                        newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                          "putconfig timesync",
                                                          InMessage->Return.UserID,
                                                          InMessage->Return.TrxID,
                                                          InMessage->Return.RouteID,
                                                          InMessage->Return.MacroOffset,
                                                          InMessage->Return.Attempt);

                        newReq->setMessagePriority(MAXPRIORITY);

                        newReq->setConnectionHandle(0);  //  discard the return, we don't want to interrupt the scan

                        CtiDeviceBase::ExecuteRequest(newReq, CtiCommandParser(newReq->CommandString()), vgList, retList, outList);

                        delete newReq;
                    }

                    expectMore = true;

                    newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                      "scan general",
                                                      InMessage->Return.UserID,
                                                      InMessage->Return.TrxID,
                                                      InMessage->Return.RouteID,
                                                      InMessage->Return.MacroOffset,
                                                      InMessage->Return.Attempt);

                    newReq->setMessagePriority(MAXPRIORITY);

                    newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                    CtiDeviceBase::ExecuteRequest(newReq, CtiCommandParser(newReq->CommandString()), vgList, retList, outList);

                    delete newReq;
                }

                break;
            }

            case CtiProtocolION::Command_IntegrityScan:
            {
                CtiRequestMsg *newReq;

                expectMore = true;

                newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                  "scan general",
                                                  InMessage->Return.UserID,
                                                  InMessage->Return.TrxID,
                                                  InMessage->Return.RouteID,
                                                  InMessage->Return.MacroOffset,
                                                  InMessage->Return.Attempt);

                newReq->setMessagePriority(MAXPRIORITY);

                newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                CtiDeviceBase::ExecuteRequest(newReq, CtiCommandParser(newReq->CommandString()), vgList, retList, outList);

                delete newReq;

                break;
            }
        }

        processInboundData(InMessage, TimeNow, vgList, retList, outList, pointData, eventData, returnInfo, expectMore);

        pointData.clear();
        eventData.clear();

        _ion.clearInboundData();
    }
    else
    {
        char error_str[80];
        string resultString;

        if( !ErrReturn )
        {
            ErrReturn = NOTNORMAL;
        }

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


void CtiDeviceION::processInboundData( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList,
                                       list<CtiPointDataMsg*> &points, list<CtiSignalMsg*> &events, string &returnInfo, bool expectMore )
{
    CtiReturnMsg *retMsg, *vgMsg;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    retMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);
    vgMsg  = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    retMsg->setUserMessageId(InMessage->Return.UserID);
    vgMsg->setUserMessageId (InMessage->Return.UserID);

    while( !points.empty() )
    {
        CtiPointDataMsg *tmpMsg = points.front();points.pop_front();

        CtiPointSPtr     point;
        double           value;
        string        resultString;

        //  !!! tmpMsg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) )
        {
            tmpMsg->setId(point->getID());

            //  generate the point update string, if applicable
            if( point->isNumeric() )
            {
                value = boost::static_pointer_cast<CtiPointNumeric>(point)->computeValueForUOM(tmpMsg->getValue());
                tmpMsg->setValue(value);

                //tmpMsg->getString()
                resultString = getName() + " / " + point->getName() + ": " + CtiNumStr(tmpMsg->getValue(), boost::static_pointer_cast<CtiPointNumeric>(point)->getPointUnits().getDecimalPlaces());
            }
            else if( point->isStatus() )
            {
                resultString = getName() + " / " + point->getName() + ": " + ResolveStateName(boost::static_pointer_cast<CtiPointStatus>(point)->getStateGroupID(), tmpMsg->getValue());
            }
            else
            {
                resultString = "";
            }

            tmpMsg->setString(resultString);

            if( !useScanFlags() )  //  if we're not Scanner, send it to VG as well (scanner will do this on his own)
            {
                //  maybe (parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE)) someday
                vgMsg->PointData().push_back(tmpMsg->replicateMessage());
            }

            retMsg->PointData().push_back(tmpMsg);
        }
        else
        {
            // 100303 CGP... We must delete it .
            delete tmpMsg;
        }
    }

    while( !events.empty() )
    {
        CtiSignalMsg *tmpSignal = events.front();events.pop_front();
        CtiPointSPtr point;

        if( (point = getDevicePointOffsetTypeEqual(tmpSignal->getId(), AnalogPointType)) )
        {
            tmpSignal->setId(point->getID());

            //  only send to Dispatch
            vgList.push_back(tmpSignal->replicateMessage());
        }
        else
        {
            // 100303 CGP... We must delete it .
            delete tmpSignal;
        }
    }

    retMsg->setResultString(returnInfo);

    //  not too kosher, but gets the job done
    if( findStringIgnoreCase(parse.getCommandStr(),"eventlog") )
    {
        if( !_ion.areEventLogsComplete() )
        {
            retMsg->setExpectMore(true);
        }
        else
        {
            retMsg->setResultString(retMsg->ResultString() + "\nEvent log collection complete.");
        }
    }

    if( expectMore )
    {
        retMsg->setExpectMore();
    }

    retList.push_back(retMsg);
    vgList.push_back(vgMsg);
}


INT CtiDeviceION::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT retCode = NORMAL, ErrReturn = InMessage->EventCode & 0x3fff;

    //CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *retMsg;
    CtiCommandMsg    *pMsg;
    string         resultString;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                     string(InMessage->Return.CommandStr),
                                     string(),
                                     InMessage->EventCode & 0x7fff,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset,
                                     InMessage->Return.Attempt,
                                     InMessage->Return.TrxID,
                                     InMessage->Return.UserID);

    if( retMsg != NULL )
    {
        //  send a Device Failed/Point Failed message to dispatch, if applicable
        pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if( pMsg != NULL )
        {
            switch( InMessage->Sequence )
            {
                case CtiProtocolION::Command_ExceptionScan:
                {
                    pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                    pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                    pMsg->insert(getID());          //  The id (device or point which failed)
                    pMsg->insert(ScanRateGeneral);  //  defined in yukon.h
                    pMsg->insert(InMessage->EventCode);

                    break;
                }

                case CtiProtocolION::Command_IntegrityScan:
                {
                    pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                    pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                    pMsg->insert(getID());          //  The id (device or point which failed)
                    pMsg->insert(ScanRateIntegrity);
                    pMsg->insert(InMessage->EventCode);

                    break;
                }

                case CtiProtocolION::Command_EventLogRead:
                {
                    pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                    pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                    pMsg->insert(getID());          //  The id (device or point which failed)
                    pMsg->insert(ScanRateAccum);
                    pMsg->insert(InMessage->EventCode);

                    break;
                }

                default:
                {
                    delete pMsg;
                    pMsg = NULL;

                    break;
                }
            }

            if( pMsg != NULL )
            {
                retMsg->insert(pMsg);
            }
        }

        char error_str[80];

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg->setResultString(resultString);

        retList.push_back(retMsg);
    }

    return retCode;
}


void CtiDeviceION::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceAddress::getSQL(db, keyTable, selector);
}


void CtiDeviceION::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _ion.setAddresses(_address.getMasterAddress(), _address.getSlaveAddress());
}

