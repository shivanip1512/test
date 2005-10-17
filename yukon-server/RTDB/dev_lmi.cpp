/*-----------------------------------------------------------------------------*
*
* File:   dev_lmi
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2005/10/17 19:28:02 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_lmi.h"
#include "porter.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "numstr.h"
#include "dllyukon.h"
#include "cparms.h"


CtiDeviceLMI::CtiDeviceLMI() :
    _lastPreload(0UL)
{
}


CtiDeviceLMI::~CtiDeviceLMI()
{
}


Protocol::Interface *CtiDeviceLMI::getProtocol()
{
    return (Protocol::Interface *)&_lmi;
}


int CtiDeviceLMI::decode(CtiXfer &xfer, int status)
{
    int retval = CtiDeviceSingle::decode(xfer, status);

    if( _lmi.isTransactionComplete() )
    {
        //  this should make sure that we're not looked at until the next cycle - this is redone in the
        //    preload pass after CommunicateDevice()
        getExclusion().setEvaluateNextAt(_lmi.getTransmissionEnd());
    }

    return retval;
}


void CtiDeviceLMI::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    _lmi.getVerificationObjects(work_queue);
}


void CtiDeviceLMI::sendDispatchResults(CtiConnection &vg_connection)
{
    CtiReturnMsg                *vgMsg;
    CtiPointDataMsg             *pt_msg;
    CtiPointBase                *point;
    CtiPointNumeric             *pNumeric;
    RWCString                    resultString;
    RWTime                       Now;

    Protocol::Interface::pointlist_t points;
    Protocol::Interface::pointlist_t::iterator itr;

    vgMsg  = CTIDBG_new CtiReturnMsg(getID());

    //  eventually move all point retrieval here
    _lmi.getStatuses(points);

    //  then toss them into the return msg
    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        pt_msg = *itr;

        if( pt_msg && (point = getDevicePointOffsetTypeEqual(pt_msg->getId(), pt_msg->getType())) )
        {
            pt_msg->setId(point->getID());

            //  we don't need to calc the UOM on these status points - not yet, anyway

            //  we might eventually send the pointdata as string results in the INMESS for Commander, but not yet
            //_string_results.push_back(CTIDBG_new string(pt_msg->getString()));

            vgMsg->PointData().append(pt_msg);
        }
        else
        {
            delete pt_msg;
        }
    }

    points.clear();

    vg_connection.WriteConnQue(vgMsg);
}


INT CtiDeviceLMI::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT nRet = NORMAL;
    bool found = false;

    switch(parse.getCommand())
    {
        case LoopbackRequest:
        {
            _lmi.setCommand(CtiProtocolLMI::Command_Loopback);
            found = true;

            break;
        }

        case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                case ScanRateAccum:
                case ScanRateIntegrity:
                {
                    _lmi.setCommand(CtiProtocolLMI::Command_ScanAccumulator);    //  all scanrates map to an accumulator scan at the moment
                    found = true;

                    break;
                }

                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - undefined scan type \"" << parse.getiValue("scantype") << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }
            }

            break;
        }

        case GetConfigRequest:
        {
            if( parse.isKeyValid("codes") )
            {
                _lmi.setCommand(CtiProtocolLMI::Command_ReadQueuedCodes);
                found = true;
            }

            break;
        }

        case GetValueRequest:
        {
            if( parse.isKeyValid("codes") )
            {
                _lmi.setCommand(CtiProtocolLMI::Command_ReadEchoedCodes);
                found = true;
            }

            break;
        }

        case ControlRequest:
        case PutConfigRequest:
        case GetStatusRequest:
        case PutValueRequest:
        case PutStatusRequest:
        default:
        {
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint - command \"" << OutMessage->Request.CommandStr << "\" not defined for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            nRet = NoExecuteRequestMethod;

            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                    RWCString(OutMessage->Request.CommandStr),
                                                    RWCString("LMI RTUs do not support this command"),
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

    if( found && nRet == NoError )
    {
        OutMessage->Port      = getPortID();
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Remote    = getAddress();
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Retry     = 2;
        EstablishOutMessagePriority( OutMessage, pReq->getMessagePriority() );

        _lmi.sendCommRequest(OutMessage, outList);
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT CtiDeviceLMI::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan accumulator");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** AccumulatorScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString(newParse.getCommandStr());

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}



INT CtiDeviceLMI::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString(newParse.getCommandStr());

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT CtiDeviceLMI::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString(newParse.getCommandStr());

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT CtiDeviceLMI::ErrorDecode( INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    return 0;
}


INT CtiDeviceLMI::ResultDecode( INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> points;

    RWCString resultString, info;
    CtiReturnMsg *retMsg;

    if( !ErrReturn && !_lmi.recvCommResult(InMessage, outList) )
    {
        resetScanFlags();

        if( _lmi.hasInboundData() )
        {
            _lmi.getInboundData(points, info);

            processInboundData(InMessage, Now, vgList, retList, outList, points, info);
        }
    }

    return 0;
}

void CtiDeviceLMI::processInboundData(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points, RWCString &info )
{
    CtiReturnMsg    *retMsg,
                    *vgMsg;
    CtiPointDataMsg *tmpMsg;
    CtiPointBase    *point;
    CtiPointNumeric *pNumeric;
    RWCString        resultString;
    RWTime           Now;

    retMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);
    vgMsg  = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    retMsg->setUserMessageId(InMessage->Return.UserID);
    vgMsg->setUserMessageId (InMessage->Return.UserID);

    double tmpValue;

    while( !points.isEmpty() )
    {
        tmpMsg = points.removeFirst();

        //  !!! tmpMsg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId()+1, tmpMsg->getType())) != NULL )
        {
            tmpMsg->setId(point->getID());

            if( point->isNumeric() )
            {
                pNumeric = (CtiPointNumeric *)point;

                tmpValue = pNumeric->computeValueForUOM(tmpMsg->getValue());

                tmpMsg->setValue(tmpValue);

                resultString = getName() + " / " + point->getName() + ": " + CtiNumStr(tmpMsg->getValue(), ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
            }
            else if( point->isStatus() )
            {
                resultString = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), tmpMsg->getValue());
            }
            else
            {
                resultString = "";
            }

            tmpMsg->setString(resultString);

            if( !useScanFlags() )  //  if we're not Scanner, send it to VG as well (scanner will do this on his own)
            {
                //  maybe (parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE)) someday
                vgMsg->PointData().append(tmpMsg->replicateMessage());
            }
            retMsg->PointData().append(tmpMsg);
        }
        else
        {
            switch( tmpMsg->getType() )
            {
                case AnalogPointType:   resultString = getName() + " / Analog " + CtiNumStr(tmpMsg->getId()+1) + ": " + CtiNumStr(tmpMsg->getValue()) + "\n"; break;
                case StatusPointType:   resultString = getName() + " / Status " + CtiNumStr(tmpMsg->getId()+1) + ": " + CtiNumStr(tmpMsg->getValue()) + "\n"; break;
                default:                resultString = getName() + " / (unknown type) " + CtiNumStr(tmpMsg->getId()+1) + ": " + CtiNumStr(tmpMsg->getValue()) + "\n"; break;
            }

            retMsg->setResultString(retMsg->ResultString() + resultString);

            delete tmpMsg;
        }
    }

    if( !info.isNull() )
    {
        retMsg->setResultString(retMsg->ResultString() + info);
    }

    retList.append(retMsg);
    vgList.append(vgMsg);
}


void CtiDeviceLMI::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);

    _address.getSQL(db, keyTable, selector);
    _seriesv.getSQL(db, keyTable, selector);
}


void CtiDeviceLMI::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _seriesv.DecodeDatabaseReader(rdr);

    _lmi.setSystemData(_seriesv.getTickTime(),
                       _seriesv.getTimeOffset(),
                       //  this is probably zero the first time through here - we need to wait for mgr_device to load
                       //    the exclusions...  so when we add them above, we'll make this call again
                       _exclusion.getCycleTimeExclusion().getTransmitTime(),
                       _seriesv.getTransmitterLow(),
                       _seriesv.getTransmitterHigh(),
                       _seriesv.getStartCode(),
                       _seriesv.getStopCode());

    _lmi.setAddress(_address.getSlaveAddress());
    _lmi.setName(getName());
}


bool CtiDeviceLMI::hasQueuedWork() const
{
    return true;  //  always operates according to exclusion rules - the time exclusion window is what lets us preload
}

bool CtiDeviceLMI::hasPreloadWork() const
{
    return true;  //  as opposed to some other devices, this one needs to "preload" timesyncs and things all the time
                  //    maybe "preload" needs to change its name to "scheduled work" or something like that
                  //  and also, maybe all of this could be merged with Scanner when the Grand Unification takes place
}

RWTime CtiDeviceLMI::getPreloadEndTime() const
{
    RWTime preload_end, now, next_preload;

    //  make sure at least half of the period has passed before we allow another download - this should protect us against transmitting too soon
    next_preload = _lastPreload + ((_seriesv.getTickTime() * 60) / 2);

    if( isInhibited() )
    {
        //  device is inhibited, we're not executing at all
        preload_end = now;
    }
    else
    {
        //  make sure it's not zero - otherwise, return something crazy like now()
        if( _seriesv.getTickTime() )
        {
            preload_end -= preload_end.seconds() % (_seriesv.getTickTime() * 60);
            preload_end += _seriesv.getTimeOffset();

            while( preload_end < now ||
                   preload_end < next_preload )
            {
                preload_end += (_seriesv.getTickTime() * 60);
            }
        }
    }

    return preload_end;
}

LONG CtiDeviceLMI::getPreloadBytes() const
{
    return _lmi.getPreloadDataLength();
}

LONG CtiDeviceLMI::getCycleTime() const
{
    return _seriesv.getTickTime() * 60;
}

LONG CtiDeviceLMI::getCycleOffset() const
{
    return _seriesv.getTimeOffset();
}

INT CtiDeviceLMI::queuedWorkCount() const
{
    return _lmi.getNumCodes();
}


INT CtiDeviceLMI::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;

    if( OutMessage->Sequence == CtiProtocolLMI::Sequence_Code )
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - OutMessage->VerificationSequence = " << OutMessage->VerificationSequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _lmi.queueCode(OutMessage);

        OutMessage = 0;

        retval = QUEUED_TO_DEVICE;

        *dqcnt = _lmi.getNumCodes();
    }
    else if( OutMessage->Sequence == CtiProtocolLMI::Sequence_TimeSync )
    {
        //  we say that we've "enqueued" the message, when really, it's just the DevicePreprocessing() code around
        //      queueOutMessageToDevice() call that we care about

        delete OutMessage;
        OutMessage = 0;

        retval = QUEUED_TO_DEVICE;

        *dqcnt = _lmi.getNumCodes();
    }

    return retval;
}


bool CtiDeviceLMI::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool retval = false;
    RWTime now;

    if( !isInhibited() )
    {
        if( now > (_lastPreload + (_seriesv.getTickTime() * 60) / 2) )
        {
            if( !OutMessage )
            {
                OutMessage = new CtiOutMessage();
            }

            OutMessage->DeviceID = getID();
            OutMessage->Sequence = CtiProtocolLMI::Sequence_Preload;
            OutMessage->Priority = MAXPRIORITY - 1;

            OutMessage->ExpirationTime = getExclusion().getExecutionGrantExpires().seconds();  //  i'm hijacking this over

            _lastPreload = now;
        }
        else
        {
            if( OutMessage )
            {
                delete OutMessage;

                OutMessage = 0;
            }
        }
    }

    /*
    if( now > (_lmi.getTransmissionEnd()) )
    {
        if( _lmi.canDownloadCodes() && _lmi.hasQueuedCodes() )
        {
            if( !OutMessage )
            {
                OutMessage = new CtiOutMessage();
            }

            OutMessage->DeviceID = getID();
            OutMessage->Sequence = CtiProtocolLMI::Sequence_QueueCodes;
            OutMessage->Priority = MAXPRIORITY - 1;

            OutMessage->ExpirationTime = getExclusion().getExecutionGrantExpires().seconds();  //  i'm hijacking this over

            retval = true;
        }
        else if( _lmi.codeVerificationPending() )
        {
            if( !OutMessage )
            {
                OutMessage = new CtiOutMessage();
            }

            OutMessage->DeviceID = getID();
            OutMessage->Sequence = CtiProtocolLMI::Sequence_ReadEchoedCodes;
        }
        else if( !_lmi.hasQueuedCodes() && now > (_lmi.getLastCodeDownload() + (_seriesv.getTickTime() * 60)) )
        {
            if( !OutMessage )
            {
                OutMessage = new CtiOutMessage();
            }

            OutMessage->DeviceID = getID();
            OutMessage->Sequence = CtiProtocolLMI::Sequence_ClearQueuedCodes;
        }
    }
    else
    {
        if( OutMessage )
        {
            delete OutMessage;

            OutMessage = 0;
        }
    }
    */

    if(OutMessage)
    {
        incQueueProcessed(1, RWTime());
    }

    return retval;
}


RWTime CtiDeviceLMI::selectCompletionTime() const
{
    return getPreloadEndTime();
}


LONG CtiDeviceLMI::deviceQueueCommunicationTime() const
{
    RWTime Now;

    long percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250),
         fudge   = gConfigParms.getValueAsULong("PORTER_LMI_FUDGE", 1000);

    long millis;

    millis = (percode * _lmi.getNumCodes()) + fudge;

    return millis;
}


LONG CtiDeviceLMI::deviceMaxCommunicationTime() const
{
    long  maxtime = gConfigParms.getValueAsULong("PORTER_MAX_TRANSMITTER_TIME", 0);

    return maxtime;
}


bool CtiDeviceLMI::hasExclusions() const
{
    return _lmi_exclusion.hasExclusions();
}

CtiDeviceExclusion CtiDeviceLMI::exclusion() const
{
    return _lmi_exclusion;
}

CtiDeviceExclusion& CtiDeviceLMI::getExclusion()
{
    return _lmi_exclusion;
}

CtiDeviceBase::exclusions CtiDeviceLMI::getExclusions() const
{
    //  this is the only one we advertise
    return _lmi_exclusion.getExclusions();
}

void CtiDeviceLMI::addExclusion(CtiTablePaoExclusion &paox)
{
    try
    {
        if( paox.getExclusionId() == 0 )
        {
            //  these are the port-manufactured exclusion timings
            _lmi_exclusion.addExclusion(paox);
        }
        else
        {
            if( !_lmi_exclusion.hasTimeExclusion() && paox.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime )
            {
                //  this sets _lmi_exclusion to be everything BUT the transmit time - the port will do this later
                //    in the preload code, but we need to initialize it here if it's never been done

                CtiTablePaoExclusion paox_lmi(paox);

                paox_lmi.setCycleOffset((paox.getCycleOffset() + paox.getTransmitTime()) % paox.getCycleTime());
                paox_lmi.setTransmitTime(paox.getCycleTime() - paox.getTransmitTime());

                _lmi_exclusion.addExclusion(paox_lmi);
                _lmi_exclusion.setEvaluateNextAt(RWTime::now());
            }

            _exclusion.addExclusion(paox);

            if( _exclusion.getCycleTimeExclusion().getCycleTime() != (_seriesv.getTickTime() * 60) ||
                _exclusion.getCycleTimeExclusion().getCycleOffset() != _seriesv.getTimeOffset() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Exclusion and Series 5 cycle time and/or offset do not match for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " **** (" << _exclusion.getCycleTimeExclusion().getCycleTime()   << ") (" << (_seriesv.getTickTime() * 60) << "), ("
                                              << _exclusion.getCycleTimeExclusion().getCycleOffset() << ") (" <<  _seriesv.getTimeOffset()     << ")" << endl;
            }

            _lmi.setSystemData(_seriesv.getTickTime(),
                               _seriesv.getTimeOffset(),
                               _exclusion.getCycleTimeExclusion().getTransmitTime(),
                               _seriesv.getTransmitterLow(),
                               _seriesv.getTransmitterHigh(),
                               _seriesv.getStartCode(),
                               _seriesv.getStopCode());
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void CtiDeviceLMI::clearExclusions()
{
    try
    {
        _exclusion.clearExclusions();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}


/*
 *  Check if the passed id is in the exclusion list?
 */
bool CtiDeviceLMI::isDeviceExcluded(long id) const
{
    bool bstatus = false;

    try
    {
        bstatus = _lmi_exclusion.isDeviceExcluded(id);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

bool CtiDeviceLMI::isExecuting() const
{
    return _lmi_exclusion.isExecuting();
}

void CtiDeviceLMI::setExecuting(bool set)
{
    _lmi_exclusion.setExecuting(set);
    return;
}

bool CtiDeviceLMI::isExecutionProhibited(const RWTime &now, LONG did)
{
    return _lmi_exclusion.isExecutionProhibited(now, did);
}

size_t CtiDeviceLMI::setExecutionProhibited(unsigned long id, RWTime& releaseTime)
{
    return _lmi_exclusion.setExecutionProhibited(id,releaseTime);
}

bool CtiDeviceLMI::removeInfiniteProhibit(unsigned long id)
{
    return _lmi_exclusion.removeInfiniteProhibit(id);
}

