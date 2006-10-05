/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/10/05 16:44:50 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_ccu721.h"
#include "porter.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "numstr.h"
#include "dllyukon.h"
#include "cparms.h"


namespace Cti       {
namespace Device    {

CCU721::CCU721()
{
}


CCU721::~CCU721()
{
}


Protocol::Interface *CCU721::getProtocol()
{
    return (Protocol::Interface *)&_klondike;
}


INT CCU721::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT nRet = NORMAL;
    bool found = false;

    switch(parse.getCommand())
    {
/*        case LoopbackRequest:
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
                        dout << CtiTime() << " **** Checkpoint - undefined scan type \"" << parse.getiValue("scantype") << "\" for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                                    CtiString(OutMessage->Request.CommandStr),
                                                    CtiString("LMI RTUs do not support this command"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.TrxID,
                                                    OutMessage->Request.UserID,
                                                    OutMessage->Request.SOE,
                                                    RWOrdered()));

            break;
        }*/
    }

    if( found && nRet == NoError )
    {
        OutMessage->Port      = getPortID();
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Remote    = getAddress();
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Retry     = 0;  //  the retries will be handled internally by the protocol if they are necessary;
                                    //    resubmitting the OM is unpredictable with protocol-based devices
        EstablishOutMessagePriority( OutMessage, pReq->getMessagePriority() );

        //_lmi.sendCommRequest(OutMessage, outList);
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT CCU721::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CCU721::ErrorDecode( INMESS *InMessage, CtiTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    return 0;
}


INT CCU721::ResultDecode( INMESS *InMessage, CtiTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> points;

    //CtiString resultString, info;
    CtiReturnMsg *retMsg;
/*
    if( !ErrReturn && !_lmi.recvCommResult(InMessage, outList) )
    {
        resetScanFlag();

        if( _lmi.hasInboundData() )
        {
            _lmi.getInboundData(points, info);

            processInboundData(InMessage, Now, vgList, retList, outList, points, info);
        }
    }
*/
    return 0;
}


void CCU721::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);

    _address.getSQL(db, keyTable, selector);
}


void CCU721::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _klondike.setAddresses(_address.getSlaveAddress(), _address.getMasterAddress());
    _klondike.setName(getName().data());
}


bool CCU721::hasQueuedWork() const
{
    return true;  //  always operates according to exclusion rules - the time exclusion window is what lets us preload
}


INT CCU721::queuedWorkCount() const
{
    return 1;//_klondike.getNumCodes();
}


INT CCU721::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;
/*
    if( OutMessage->Sequence == CtiProtocolLMI::Sequence_Code )
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - OutMessage->VerificationSequence = " << OutMessage->VerificationSequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
*/
    return retval;
}


bool CCU721::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool retval = false;
    CtiTime now;
/*
    if( !isInhibited() && _seriesv.getTickTime() )
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

    if(OutMessage)
    {
        incQueueProcessed(1, CtiTime());
    }
*/
    return retval;
}


}
}

