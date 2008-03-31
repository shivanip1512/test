/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/03/31 21:17:35 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_ccu721.h"
#include "prot_emetcon.h"
#include "porter.h"
#include "numstr.h"


using Cti::Protocol::Klondike;


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


INT CCU721::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    INT nRet = NoError, command = Klondike::Command_Invalid;

    switch(parse.getCommand())
    {
        case LoopbackRequest:
        {
            command = Klondike::Command_CheckStatus;
            nRet = _klondike.setCommand(command);

            break;
        }
/*
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

    if( !nRet && command != Klondike::Command_Invalid )
    {
        OutMessage->Port      = getPortID();
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Remote    = getAddress();
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Retry     = 0;  //  the retries will be handled internally by the protocol if they are necessary;
                                    //    resubmitting the OM is unpredictable with protocol-based devices
        EstablishOutMessagePriority( OutMessage, pReq->getMessagePriority() );

        _klondike.sendCommRequest(OutMessage, outList);
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT CCU721::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority )
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


INT CCU721::ErrorDecode( INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    return 0;
}


INT CCU721::ResultDecode( INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

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

    selector.where( (keyTable["type"] == "CCU-721") && selector.where() );
}


void CCU721::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _klondike.setAddresses(_address.getSlaveAddress(), _address.getMasterAddress());
    _klondike.setName(getName().data());
}


LONG CCU721::getAddress() const
{
    return _address.getSlaveAddress();
}

bool CCU721::hasQueuedWork() const
{
    return _klondike.hasQueuedWork();
}


INT CCU721::queuedWorkCount() const
{
    return _klondike.queuedWorkCount();
}


INT CCU721::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;

    if( OutMessage->DeviceID != OutMessage->TargetID )
    {
        if( !(OutMessage->EventCode & DTRAN) )
        {
            //  the OM is now owned by _klondike
            _klondike.addQueuedWork(OutMessage);

            *dqcnt = _klondike.queuedWorkCount();

            retval = QUEUED_TO_DEVICE;
        }
    }

    return retval;
}


bool CCU721::buildCommand(CtiOutMessage *&OutMessage, Commands command)
{
    bool command_built = false;
    CtiTime now;

    if( OutMessage )
    {
        switch( command )
        {
            case Command_ReadQueue:
            {
                if( _klondike.hasRemoteWork() && !_klondike.isReadingDeviceQueue() )
                {
                    _klondike.setReadingDeviceQueue(true);

                    OutMessage->Priority = _klondike.getRemoteWorkPriority();

                    command_built = true;
                }

                break;
            }
            case Command_WriteQueue:
            {
                if( _klondike.hasWaitingWork() && !_klondike.isLoadingDeviceQueue() )
                {
                    _klondike.setLoadingDeviceQueue(true);

                    OutMessage->Priority = _klondike.getWaitingWorkPriority();

                    command_built = true;
                }

                break;
            }
        }

        if( command_built )
        {
            OutMessage->DeviceID = getID();
            OutMessage->Port     = getPortID();
            OutMessage->Sequence = command;
        }
    }

    return command_built;
}


int CCU721::recvCommRequest(OUTMESS *OutMessage)
{
    bool error = false;

    _current_outmessage = *OutMessage;

    if( _current_outmessage.EventCode & DTRAN &&
        _current_outmessage.EventCode & BWORD )
    {
        error = !_klondike.setCommandDirectTransmission(_current_outmessage.Buffer.BSt);
    }
    else
    {
        error = !_klondike.setCommand(_current_outmessage.Sequence);
    }

    return error;
}


int CCU721::recvCommResult(INMESS *InMessage, list<OUTMESS *> &outList)
{
    return 0;
}


int CCU721::sendCommRequest(OUTMESS *&OutMessage, list <OUTMESS *> &outList)
{
    return 0;
}


int CCU721::sendCommResult(INMESS *InMessage)
{
    using Cti::Protocol::Emetcon;

    if( _klondike.errorCondition() )
    {
        InMessage->EventCode = _klondike.errorCode();

        InMessage->Buffer.DSt.Time     = InMessage->Time;
        InMessage->Buffer.DSt.DSTFlag  = InMessage->MilliTime & DSTACTIVE;
    }
    else
    {
        switch( _klondike.getCommand() )
        {
            case Klondike::Command_DirectMessageRequest:
            {
                unsigned char input[255], input_length;

                _klondike.getResultDirectTransmission(input, 255,input_length );

                //  if this was targeted at an MCT
                if( _current_outmessage.TargetID != _current_outmessage.DeviceID && InMessage->DeviceID != 0 && InMessage->TargetID != 0 )
                {
                    if( (_current_outmessage.EventCode & BWORD) &&
                        (_current_outmessage.Buffer.BSt.IO & Emetcon::IO_Read ) )
                    {
                        //  inbound command - decode the D words
                        InMessage->EventCode = Klondike::decodeDWords(input, input_length, _current_outmessage.Remote, &(InMessage->Buffer.DSt));
                        InMessage->Buffer.DSt.Time    = InMessage->Time;
                        InMessage->Buffer.DSt.DSTFlag = InMessage->MilliTime & DSTACTIVE;
                        InMessage->Buffer.DSt.Length  = (input_length / DWORDLEN) * 5 - 2;  //  calculate the number of bytes we get back

                        InMessage->InLength = InMessage->Buffer.DSt.Length;
                    }
                    else
                    {
                        InMessage->InLength = 0;
                    }
                }

                break;
            }

            case Klondike::Command_WaitingQueueRead:
            {
                incQueueProcessed(1, CtiTime());

                break;
            }
        }
    }

    return 0;
}


}
}
