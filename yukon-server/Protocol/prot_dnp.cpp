#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_dnp
*
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:19:14 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "prot_dnp.h"

CtiProtocolDNP::CtiProtocolDNP()    {}

CtiProtocolDNP::CtiProtocolDNP(int address)
{
    _address = address;
}

CtiProtocolDNP::CtiProtocolDNP(const CtiProtocolDNP &aRef)
{
    *this = aRef;
}

CtiProtocolDNP::~CtiProtocolDNP()   {}

CtiProtocolDNP &CtiProtocolDNP::operator=(const CtiProtocolDNP &aRef)
{
    if( this != &aRef )
    {
        _appLayer = aRef._appLayer;
        _address  = aRef._address;
    }

    return *this;
}


void CtiProtocolDNP::setCommand( DNPCommand command, XferPoint *points, int numPoints )
{
    switch( command )
    {
        case DNP_Class0Read:
            {
                dnp_point_descriptor cls0rd;

                _appLayer.setCommand(CtiDNPApplication::RequestRead, _address, CtiProtocolDNP::YukonDNPMasterAddress);

                cls0rd.group     = 60;
                cls0rd.variation =  1;
                cls0rd.qual_code =  6;
                cls0rd.qual_idx  =  0;
                cls0rd.qual_x    =  0;  //  unused bit

                _appLayer.addData((unsigned char *)&cls0rd, 3);

                break;
            }
        case DNP_SetAnalogOut:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        case DNP_SetDigitalOut:
            {
                if( numPoints > 0 )
                {
                    dnp_point_descriptor control;
                    dnp_control_relay_output_block *crob;

                    _appLayer.setCommand(CtiDNPApplication::RequestWrite, _address, CtiProtocolDNP::YukonDNPMasterAddress);

                    control.group     = 12;  //  binary output
                    control.variation =  1;  //  1
                    control.qual_idx  =  1;  //  1 octet index
                    control.qual_code =  7;  //  1 octect quantity
                    control.qual_x    =  0;  //  unused bit
                    control.idx_qty.qty_1oct.num = 1;

                    control.idx_qty.qty_1oct.data[0] = (unsigned char)points->value;

                    crob = (dnp_control_relay_output_block *)(control.idx_qty.qty_1oct.data + 1);

                    crob->count = 1;
                    crob->off_time = 0;
                    crob->on_time  = 0;
                    crob->off_time = 0;

                    crob->control_code.clear      = 0;
                    crob->control_code.queue      = 0;
                    crob->control_code.code       = 3;
                    crob->control_code.trip_close = 1;

                    _appLayer.addData((unsigned char *)&control, 13);

                }
                else
                {
                    command = DNP_Invalid;
                }

                break;
            }
        default:
            command = DNP_Invalid;
    }

    _currentCommand = command;
}

/*
void CtiProtocolDNP::initForInput( void )
{
    _appLayer.initForInput();
}
*/

int CtiProtocolDNP::generate( CtiXfer &xfer )
{
    return _appLayer.generate(xfer);
}


int CtiProtocolDNP::decode( CtiXfer &xfer, int status )
{
    return _appLayer.decode(xfer, status);
}


int CtiProtocolDNP::sendAppReqLayer( OUTMESS *OutMessage )
{
    int retVal = NoError;

    if( _appLayer.getLengthReq() < sizeof( OutMessage->Buffer ) )
    {
        _appLayer.serializeReq(OutMessage->Buffer.OutMessage);
        OutMessage->OutLength   = _appLayer.getLengthReq() + 2 * sizeof(short);
        OutMessage->Source      = YukonDNPMasterAddress;
        OutMessage->Destination = _address;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "ACH:  need to learn how to fragment application layer.  Abandoning outbound message." << endl;
        }

        delete OutMessage;
        OutMessage = NULL;

        //  oh well, closest thing to reality - not enough room in outmess
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolDNP::sendAppRspLayer( INMESS *InMessage )
{
    int retVal = NoError;

    if( _appLayer.getLengthRsp() < sizeof( InMessage->Buffer ) )
    {
        _appLayer.serializeRsp(InMessage->Buffer.InMessage);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "!!!  application layer > 4k!  !!!" << endl;
        }

        //  oh well, closest thing to reality - not enough room in outmess
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolDNP::recvAppReqLayer( OUTMESS *OutMessage )
{
    int retVal = NoError;

    _appLayer.restoreReq(OutMessage->Buffer.OutMessage, OutMessage->OutLength);

    return retVal;
}


int CtiProtocolDNP::recvAppRspLayer( INMESS *InMessage )
{
    int retVal = NoError;

    _appLayer.restoreRsp(InMessage->Buffer.InMessage, InMessage->InLength);

    return retVal;
}


bool CtiProtocolDNP::isTransactionComplete( void )
{
    return _appLayer.isTransactionComplete();
}


bool CtiProtocolDNP::hasPoints( void )
{
    return _appLayer.inHasPoints();
}


void CtiProtocolDNP::sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
{
    _appLayer.sendPoints(vgList, retList);
}

