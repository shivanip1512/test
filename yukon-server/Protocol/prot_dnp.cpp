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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/06/20 21:00:38 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_dnp.h"

CtiProtocolDNP::CtiProtocolDNP()
{
    setMasterAddress(DefaultYukonDNPMasterAddress);
    setSlaveAddress(DefaultSlaveAddress);
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
        _appLayer      = aRef._appLayer;
        _masterAddress = aRef._masterAddress;
        _slaveAddress  = aRef._slaveAddress;
    }

    return *this;
}


void CtiProtocolDNP::setMasterAddress( unsigned short address )
{
    _masterAddress = address;
}


void CtiProtocolDNP::setSlaveAddress( unsigned short address )
{
    _slaveAddress = address;
}


void CtiProtocolDNP::setCommand( DNPCommand command, XferPoint *points, int numPoints )
{
    switch( command )
    {
        case DNP_Class0Read:
            {
                dnp_point_descriptor cls0rd;

                _appLayer.setCommand(CtiDNPApplication::RequestRead, _slaveAddress, _masterAddress);

                cls0rd.group     = 60;
                cls0rd.variation =  1;
                cls0rd.qual_code =  6;
                cls0rd.qual_idx  =  0;
                cls0rd.qual_x    =  0;  //  unused bit

                _appLayer.addData((unsigned char *)&cls0rd, 3);

                break;
            }
        case DNP_Class123Read:
            {
                dnp_point_descriptor clsrd;

                _appLayer.setCommand(CtiDNPApplication::RequestRead, _slaveAddress, _masterAddress);

                clsrd.group     = 60;
                clsrd.variation =  2;
                clsrd.qual_code =  6;
                clsrd.qual_idx  =  0;
                clsrd.qual_x    =  0;  //  unused bit

                _appLayer.addData((unsigned char *)&clsrd, 3);

                clsrd.variation =  3;

                _appLayer.addData((unsigned char *)&clsrd, 3);

                clsrd.variation =  4;

                _appLayer.addData((unsigned char *)&clsrd, 3);

                break;
            }
        case DNP_SetAnalogOut:
            {
                if( numPoints > 0 )
                {
                    dnp_point_descriptor control;
                    dnp_analog_output_block_32_bit *aob;

                    _appLayer.setCommand(CtiDNPApplication::RequestDirectOp, _slaveAddress, _masterAddress);

                    control.group     = 41;  //  binary output
                    control.variation =  1;  //  1
                    control.qual_idx  =  1;  //  1 octet index
                    control.qual_code =  7;  //  1 octect quantity
                    control.qual_x    =  0;  //  unused bit
                    control.idx_qty.qty_1oct.num = 1;

                    control.idx_qty.qty_1oct.data[0] = (unsigned char)points->offset - 1;

                    aob = (dnp_analog_output_block_32_bit *)(control.idx_qty.qty_1oct.data + 1);

                    aob->status = 0;
                    aob->value  = LONG_MAX / 4;

                    _appLayer.addData((unsigned char *)&control, 10);
                }
                else
                {
                    command = DNP_Invalid;
                }

                break;
            }
        case DNP_SetDigitalOut:
            {
                if( numPoints > 0 )
                {
                    dnp_point_descriptor control;
                    dnp_control_relay_output_block *crob;

                    _appLayer.setCommand(CtiDNPApplication::RequestDirectOp, _slaveAddress, _masterAddress);

                    control.group     = 12;  //  binary output
                    control.variation =  1;  //  1
                    control.qual_idx  =  1;  //  1 octet index
                    control.qual_code =  7;  //  1 octect quantity
                    control.qual_x    =  0;  //  unused bit
                    control.idx_qty.qty_1oct.num = 1;

                    control.idx_qty.qty_1oct.data[0] = (unsigned char)points->offset - 1;

                    crob = (dnp_control_relay_output_block *)(control.idx_qty.qty_1oct.data + 1);

                    crob->count = 1;
                    crob->off_time = 0;
                    crob->on_time  = 0;
                    crob->off_time = 0;

                    crob->control_code.clear      = 0;
                    crob->control_code.queue      = 0;
                    crob->control_code.code       = 1;
                    crob->control_code.trip_close = 0;
                    crob->status                  = 0;

                    _appLayer.addData((unsigned char *)&control, 16);
                }
                else
                {
                    command = DNP_Invalid;
                }

                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                command = DNP_Invalid;
            }
    }

    _currentCommand = command;
}


int CtiProtocolDNP::generate( CtiXfer &xfer )
{
    return _appLayer.generate(xfer);
}


int CtiProtocolDNP::decode( CtiXfer &xfer, int status )
{
    return _appLayer.decode(xfer, status);
}


int CtiProtocolDNP::commOut( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal;

    retVal = sendOutbound(OutMessage);

    if( OutMessage != NULL )
    {
        outList.append(OutMessage);
        OutMessage = NULL;
    }

    return retVal;
}


int CtiProtocolDNP::commIn( INMESS *InMessage,   RWTPtrSlist< OUTMESS > &outList )
{
    int retVal;

    retVal = recvInbound(InMessage);

    if( _appLayer.hasOutput() )
    {
        OUTMESS *OutMessage = new CtiOutMessage();

        InEchoToOut(InMessage, OutMessage);
        //  copy over the other stuff we need
        OutMessage->DeviceID = InMessage->DeviceID;
        OutMessage->Port     = InMessage->Port;

        retVal = commOut(OutMessage, outList);
    }

    return retVal;
}


int CtiProtocolDNP::sendOutbound( OUTMESS *&OutMessage )
{
    int retVal = NoError;

    if( _appLayer.getLengthReq() < sizeof( OutMessage->Buffer ) )
    {
        _appLayer.serializeReq(OutMessage->Buffer.OutMessage);
        OutMessage->OutLength   = _appLayer.getLengthReq() + 2 * sizeof(short);
        OutMessage->Source      = _masterAddress;
        OutMessage->Destination = _slaveAddress;
        OutMessage->EventCode   = RESULT;
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


int CtiProtocolDNP::sendInbound( INMESS *InMessage )
{
    int retVal = NoError;

    if( _appLayer.getLengthRsp() < sizeof( InMessage->Buffer ) )
    {
        _appLayer.serializeRsp(InMessage->Buffer.InMessage);
        InMessage->InLength = _appLayer.getLengthRsp();
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


int CtiProtocolDNP::recvOutbound( OUTMESS *OutMessage )
{
    int retVal = NoError;

    _appLayer.restoreReq(OutMessage->Buffer.OutMessage, OutMessage->OutLength);

    return retVal;
}


int CtiProtocolDNP::recvInbound( INMESS *InMessage )
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

