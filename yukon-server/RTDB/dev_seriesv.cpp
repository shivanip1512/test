/*-----------------------------------------------------------------------------*
*
* File:   dev_systemv
*
* Date:   2004-jan-14
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_seriesv.h"
#include "porter.h"

CtiProtocolBase *CtiDeviceSeriesV::getProtocol() const
{
    return (CtiProtocolBase *)&_seriesv;
}


INT CtiDeviceSeriesV::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT nRet = NORMAL;
    bool found = false;

    switch(parse.getCommand())
    {
        case LoopbackRequest:
        {
            _seriesv.setCommand(CtiProtocolSeriesV::Command_Loopback);
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
                    _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanException);
                    found = true;

                    break;
                }

                case ScanRateAccum:
                {
                    _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanAccumulator);
                    found = true;

                    break;
                }

                case ScanRateIntegrity:
                {
                    _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanIntegrity);
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
            //_seriesv.setCommandControl(CtiProtocolSeriesV::Command_Control, offset, time);

            break;
        }

        case PutValueRequest:
        {
            //_seriesv.setCommandControl(CtiProtocolSeriesV::Command_AnalogSetpoint, offset, value);

            break;
        }

/*        case PutConfigRequest:
        {
            //  SOE time sync eventually

            break;
        }*/

        case GetStatusRequest:
        case GetValueRequest:
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

            break;
        }
    }

    if( !found )
    {
        nRet = NoExecuteRequestMethod;

        retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                RWCString(OutMessage->Request.CommandStr),
                                                RWCString("System V RTUs do not support this command"),
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                RWOrdered()));
    }
    else
    {
        OutMessage->Port      = getPortID();
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Remote    = getAddress();
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Retry     = 2;
        EstablishOutMessagePriority( OutMessage, pReq->getMessagePriority() );

        if( nRet == NoError )
        {
            _seriesv.sendCommRequest(OutMessage, outList);
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;
        }
    }

    return nRet;
}


INT CtiDeviceSeriesV::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
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



INT CtiDeviceSeriesV::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
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


INT CtiDeviceSeriesV::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
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


INT CtiDeviceSeriesV::ErrorDecode( INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    return 0;
}


INT CtiDeviceSeriesV::ResultDecode( INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> seriesvPoints;

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn && !_seriesv.recvCommResult(InMessage, outList) )
    {
        if( _seriesv.hasInboundPoints() )
        {
            _seriesv.getInboundPoints(seriesvPoints);

//            processInboundPoints(InMessage, TimeNow, vgList, retList, outList, seriesvPoints);
        }
    }

    return 0;
}

