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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2002/10/09 19:31:37 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_dnp.h"
#include "dnp_object_class.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_analogoutput.h"

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


void CtiProtocolDNP::setCommand( DNPCommand command, dnp_output_point *points, int numPoints )
{
    unsigned char *tmp;
    int tmplen;

    _appLayer.setAddresses(_slaveAddress, _masterAddress);

    switch( command )
    {
        case DNP_Class0Read:
            {
                _appLayer.setCommand(CtiDNPApplication::RequestRead);

                CtiDNPObjectBlock dob(CtiDNPObjectBlock::NoIndex_NoRange);

                dob.addObject(new CtiDNPClass(CtiDNPClass::Class0));

                _appLayer.addObjectBlock(dob);

                break;
            }
        case DNP_Class123Read:
            {
                _appLayer.setCommand(CtiDNPApplication::RequestRead);

                CtiDNPObjectBlock dob1(CtiDNPObjectBlock::NoIndex_NoRange),
                                  dob2(CtiDNPObjectBlock::NoIndex_NoRange),
                                  dob3(CtiDNPObjectBlock::NoIndex_NoRange);

                dob1.addObject(new CtiDNPClass(CtiDNPClass::Class1));
                dob2.addObject(new CtiDNPClass(CtiDNPClass::Class2));
                dob3.addObject(new CtiDNPClass(CtiDNPClass::Class3));

                _appLayer.addObjectBlock(dob1);
                _appLayer.addObjectBlock(dob2);
                _appLayer.addObjectBlock(dob3);

                break;
            }
        case DNP_SetAnalogOut:
            {
                if( numPoints == 1 && points[0].type == AnalogOutput )
                {
                    _appLayer.setCommand(CtiDNPApplication::RequestDirectOp);

                    CtiDNPObjectBlock dob(CtiDNPObjectBlock::ShortIndex_ShortQty);
                    CtiDNPAnalogOutputBlock *aout = new CtiDNPAnalogOutputBlock(CtiDNPAnalogOutputBlock::AOB16Bit);

                    aout->setControl(points[0].aout.value);

                    dob.addObjectIndex(aout, points[0].offset);

                    _appLayer.addObjectBlock(dob);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    command = DNP_Invalid;
                }

                break;
            }
        case DNP_SetDigitalOut:
            {
                if( numPoints == 1 && points[0].type == DigitalOutput )
                {
                    _appLayer.setCommand(CtiDNPApplication::RequestDirectOp);

                    CtiDNPObjectBlock dob(CtiDNPObjectBlock::ByteIndex_ByteQty);
                    CtiDNPBinaryOutputControl *bout = new CtiDNPBinaryOutputControl(CtiDNPBinaryOutputControl::ControlRelayOutputBlock);

                    bout->setControlBlock(points[0].dout.on_time,
                                          points[0].dout.off_time,
                                          points[0].dout.count,
                                          points[0].dout.control,
                                          points[0].dout.queue,
                                          points[0].dout.clear,
                                          points[0].dout.trip_close);

                    dob.addObjectIndex(bout, points[0].offset);

                    _appLayer.addObjectBlock(dob);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

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


int CtiProtocolDNP::commOut( OUTMESS *&OutMessage )
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


int CtiProtocolDNP::commIn( INMESS *InMessage )
{
    int retVal = NoError;

    _appLayer.restoreRsp(InMessage->Buffer.InMessage, InMessage->InLength);

    return retVal;
}


int CtiProtocolDNP::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal;

    retVal = commOut(OutMessage);

    if( OutMessage != NULL )
    {
        outList.append(OutMessage);
        OutMessage = NULL;
    }

    return retVal;
}


int CtiProtocolDNP::recvCommResult( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal;

    retVal = commIn(InMessage);

    if( _appLayer.hasOutput() )
    {
        OUTMESS *OutMessage = new CtiOutMessage();

        InEchoToOut(InMessage, OutMessage);
        //  copy over the other stuff we need
        OutMessage->DeviceID = InMessage->DeviceID;
        OutMessage->TargetID = InMessage->TargetID;
        OutMessage->Port     = InMessage->Port;

        retVal = sendCommRequest(OutMessage, outList);
    }

    return retVal;
}


int CtiProtocolDNP::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    _appLayer.restoreReq(OutMessage->Buffer.OutMessage, OutMessage->OutLength);

    return retVal;
}


int CtiProtocolDNP::sendCommResult( INMESS *InMessage )
{
    int retVal = NoError;

    if( _appLayer.isReplyExpected() )
    {
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
    }
    else
    {
        InMessage->InLength = 0;
    }

    return retVal;
}


bool CtiProtocolDNP::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?
    return _appLayer.isTransactionComplete() | _appLayer.errorCondition();
}


bool CtiProtocolDNP::hasInboundPoints( void )
{
    return _appLayer.hasInboundPoints();
}


void CtiProtocolDNP::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    _appLayer.getInboundPoints(pointList);
}

