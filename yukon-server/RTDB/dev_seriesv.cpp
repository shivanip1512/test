#include "precompiled.h"


#include "dev_seriesv.h"
#include "porter.h"

using std::string;
using std::endl;
using std::list;

Cti::Protocols::Interface *CtiDeviceSeriesV::getProtocol()
{
    return (Cti::Protocols::Interface *)&_seriesv;
}


YukonError_t CtiDeviceSeriesV::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::None;
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
                    CTILOG_ERROR(dout, "Invalid scan type \""<< parse.getiValue("scantype") <<"\" for device \""<< getName() <<"\"");

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
            CTILOG_ERROR(dout, "command \"" << OutMessage->Request.CommandStr << "\" not defined for device \"" << getName() << "\"");

            break;
        }
    }

    if( !found )
    {
        nRet = ClientErrors::NoMethodForExecuteRequest;

        retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                string(OutMessage->Request.CommandStr),
                                                string("System V RTUs do not support this command"),
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.RetryMacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec()));
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

        if( nRet == ClientErrors::None )
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


YukonError_t CtiDeviceSeriesV::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan accumulator");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "AccumulatorScan for \"" << getName() << "\"");
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



YukonError_t CtiDeviceSeriesV::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
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


YukonError_t CtiDeviceSeriesV::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "IntegrityScan for \"" << getName() << "\"");
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


YukonError_t CtiDeviceSeriesV::ResultDecode( const INMESS &InMessage, const CtiTime Now, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    list<CtiPointDataMsg*> seriesvPoints;

    string resultString;
    CtiReturnMsg *retMsg;

    if( !InMessage.ErrorCode && !_seriesv.recvCommResult(InMessage, outList) )
    {
        if( _seriesv.hasInboundPoints() )
        {
            _seriesv.getInboundPoints(seriesvPoints);
        }
    }

    return ClientErrors::None;
}

