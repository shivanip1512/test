/*-----------------------------------------------------------------------------*
*
* File:   dev_lmi
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/05/24 17:48:38 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "dev_lmi.h"
#include "porter.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "numstr.h"
#include "dllyukon.h"
#include "cparms.h"


CtiProtocolBase *CtiDeviceLMI::getProtocol() const
{
    return (CtiProtocolBase *)&_lmi;
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
                {
                    _lmi.setCommand(CtiProtocolLMI::Command_ScanException);
                    found = true;

                    break;
                }

                case ScanRateAccum:
                {
                    _lmi.setCommand(CtiProtocolLMI::Command_ScanAccumulator);
                    found = true;

                    break;
                }

                case ScanRateIntegrity:
                {
                    _lmi.setCommand(CtiProtocolLMI::Command_ScanIntegrity);    //  all of these need to change, yo
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

        case ControlRequest:
        {
            int code = parse.getiValue("lmi_code");

            if( code )
            {
                _lmi.setCommand(CtiProtocolLMI::Command_QueueCode, code);
            }

            break;
        }

        case PutConfigRequest:
        case GetStatusRequest:
        case GetValueRequest:
        case PutValueRequest:
        case PutStatusRequest:
        case GetConfigRequest:
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

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn && !_lmi.recvCommResult(InMessage, outList) )
    {
        if( _lmi.hasInboundPoints() )
        {
            _lmi.getInboundPoints(points);

            processInboundPoints(InMessage, Now, vgList, retList, outList, points);
        }
    }

    return 0;
}

void CtiDeviceLMI::processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points )
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
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
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
            delete tmpMsg;
        }
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
}


bool CtiDeviceLMI::hasQueuedWork() const
{
    return _lmi.hasCodes();
}


INT CtiDeviceLMI::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    _lmi.queueCode(atoi(OutMessage->Buffer.SASt._codeSimple));

    return QUEUED_TO_DEVICE;
}


bool CtiDeviceLMI::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool retval = false;

    if( _lmi.hasCodes() )
    {
        if( !OutMessage )
        {
            OutMessage = new CtiOutMessage();
        }

        OutMessage->DeviceID = getID();
        OutMessage->Sequence = CtiProtocolLMI::QueuedWorkToken;

        OutMessage->ExpirationTime = getExclusion().getExecutionGrantExpires().seconds();  //  i'm hijacking this over

        retval = true;
    }
    else
    {
        if( OutMessage )
        {
            delete OutMessage;
        }
    }

    return retval;
}


LONG CtiDeviceLMI::deviceQueueCommunicationTime() const
{
    RWTime Now;

    long percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 166),
         fudge   = gConfigParms.getValueAsULong("PORTER_LMI_FUDGE", 1000);

    long millis;

    millis = (percode * _lmi.numCodes()) + fudge;

    return millis;
}


LONG CtiDeviceLMI::deviceMaxCommunicationTime() const
{
    long  maxtime = gConfigParms.getValueAsULong("PORTER_MAX_TRANSMITTER_TIME", 0);

    return maxtime;
}

