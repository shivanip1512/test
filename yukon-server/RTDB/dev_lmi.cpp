/*-----------------------------------------------------------------------------*
*
* File:   dev_lmi
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
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


CtiDeviceLMI::CtiDeviceLMI()
{
}


CtiDeviceLMI::~CtiDeviceLMI()
{
}


CtiProtocolBase *CtiDeviceLMI::getProtocol() const
{
    return (CtiProtocolBase *)&_lmi;
}


int CtiDeviceLMI::decode(CtiXfer &xfer, int status)
{
    int retval = CtiDeviceSingle::decode(xfer, status);

    if( _lmi.isTransactionComplete() )
    {
        getExclusion().setEvaluateNextAt(_lmi.getTransmittingUntil());
    }

    return retval;
}


void CtiDeviceLMI::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    _lmi.getVerificationObjects(work_queue);
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
}


void CtiDeviceLMI::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _lmi.setAddress(_address.getSlaveAddress());
    _lmi.setName(getName());
}


bool CtiDeviceLMI::hasQueuedWork() const
{
    return _lmi.hasQueuedCodes() || _lmi.codeVerificationPending();
}

INT CtiDeviceLMI::queuedWorkCount() const
{
    return _lmi.getNumCodes();
}


INT CtiDeviceLMI::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;

    //  make sure we don't requeue our "go" OM
    if( getExclusion().hasExclusions() && OutMessage->Sequence != CtiProtocolLMI::Sequence_QueuedWork && OutMessage->MessageFlags & MSGFLG_APPLY_EXCLUSION_LOGIC )
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

    return retval;
}


bool CtiDeviceLMI::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool retval = false;

    if( RWTime::now() > (_lmi.getTransmittingUntil()) )
    {
        if( _lmi.codeVerificationPending() )
        {
            if( !OutMessage )
            {
                OutMessage = new CtiOutMessage();
            }

            OutMessage->DeviceID = getID();
            OutMessage->Sequence = CtiProtocolLMI::Sequence_RetrieveEchoedCodes;
        }
        else if( _lmi.hasQueuedCodes() )
        {
            //  can we do anything in the time we're given?
            if( _lmi.canTransmit(getExclusion().getExecutionGrantExpires().seconds()) )
            {
                if( !OutMessage )
                {
                    OutMessage = new CtiOutMessage();
                }

                OutMessage->DeviceID = getID();
                OutMessage->Sequence = CtiProtocolLMI::Sequence_QueuedWork;

                OutMessage->ExpirationTime = getExclusion().getExecutionGrantExpires().seconds();  //  i'm hijacking this over

                retval = true;
            }
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

    if(OutMessage)
    {
        incQueueProcessed(1, RWTime());
    }

    return retval;
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



