#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_ion
*
* Date:   2002-oct-02
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/12/27 02:51:18 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag

#include "prot_ion.h"

#include "ion_value_basic_program.h"

CtiProtocolION::CtiProtocolION()
{
    setAddresses(DefaultYukonIONMasterAddress, DefaultSlaveAddress);
}


CtiProtocolION::CtiProtocolION(const CtiProtocolION &aRef)
{
    *this = aRef;
}


CtiProtocolION::~CtiProtocolION()   {}


CtiProtocolION &CtiProtocolION::operator=(const CtiProtocolION &aRef)
{
    if( this != &aRef )
    {
        _appLayer = aRef._appLayer;
        _srcID    = aRef._srcID;
        _dstID    = aRef._dstID;
    }

    return *this;
}


void CtiProtocolION::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _srcID = srcID;
    _dstID = dstID;

    _appLayer.setAddresses(_srcID, _dstID);
}


void CtiProtocolION::setCommand( IONCommand command, ion_output_point *points, int numPoints )
{
    unsigned char *tmp;
    int tmplen;

//  this needs to be moved to porter-side
//    _appLayer.setAddresses(_slaveAddress, _masterAddress);

/*
    switch( command )
    {
        case ION_ExceptionScan:
            {

                _appLayer.setCommand(CtiIONApplication::RequestRead);

                CtiIONObjectBlock dob(CtiIONObjectBlock::NoIndex_NoRange);

                dob.addObject(CTIDBG_new CtiIONClass(CtiIONClass::Class0));

                _appLayer.addObjectBlock(dob);

                break;
            }
        case ION_Class123Read:
            {
                _appLayer.setCommand(CtiIONApplication::RequestRead);

                CtiIONObjectBlock dob1(CtiIONObjectBlock::NoIndex_NoRange),
                                  dob2(CtiIONObjectBlock::NoIndex_NoRange),
                                  dob3(CtiIONObjectBlock::NoIndex_NoRange);

                dob1.addObject(CTIDBG_new CtiIONClass(CtiIONClass::Class1));
                dob2.addObject(CTIDBG_new CtiIONClass(CtiIONClass::Class2));
                dob3.addObject(CTIDBG_new CtiIONClass(CtiIONClass::Class3));

                _appLayer.addObjectBlock(dob1);
                _appLayer.addObjectBlock(dob2);
                _appLayer.addObjectBlock(dob3);

                break;
            }
        case ION_SetAnalogOut:
            {
                if( numPoints == 1 && points[0].type == AnalogOutput )
                {
                    _appLayer.setCommand(CtiIONApplication::RequestDirectOp);

                    CtiIONObjectBlock dob(CtiIONObjectBlock::ShortIndex_ShortQty);
                    CtiIONAnalogOutputBlock *aout = CTIDBG_new CtiIONAnalogOutputBlock(CtiIONAnalogOutputBlock::AOB16Bit);

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

                    command = ION_Invalid;
                }

                break;
            }
        case ION_SetDigitalOut:
            {
                if( numPoints == 1 && points[0].type == DigitalOutput )
                {
                    _appLayer.setCommand(CtiIONApplication::RequestDirectOp);

                    CtiIONObjectBlock dob(CtiIONObjectBlock::ByteIndex_ByteQty);
                    CtiIONBinaryOutputControl *bout = CTIDBG_new CtiIONBinaryOutputControl(CtiIONBinaryOutputControl::ControlRelayOutputBlock);

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

                    command = ION_Invalid;
                }

                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                command = ION_Invalid;
            }
    }
*/

    _currentCommand.command = command;
}


int CtiProtocolION::generate( CtiXfer &xfer )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    if( _appLayer.isTransactionComplete() )
    {
        switch( _ionState )
        {
            case IONStateInit:
            case IONStateRequestFeatureManagerInfo:
            {
                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
                tmpStatement = CTIDBG_new CtiIONStatement(IONFeatureManagerHandle, tmpMethod);
                tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

                _dsOut.clear();
                _dsOut.appendItem(tmpProgram);

                _appLayer.setToOutput(_dsOut);

                _dsOut.clear();

                break;
            }

            case IONStateReceiveFeatureManagerInfo:
            {
                _appLayer.setToInput();

                break;
            }

            case IONStateRequestManagerInfo:
            {
                int i;

                _dsOut.clear();

                for( i = 0; i < _setup_handles->getSize(); i++ )
                {
                    tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadManagedClass);
                    tmpStatement = CTIDBG_new CtiIONStatement(_setup_handles->getElement(i)->getValue(), tmpMethod);
                    tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

                    _dsOut.appendItem(tmpProgram);
                }

                _appLayer.setToOutput(_dsOut);

                break;

            }

            case IONStateReceiveManagerInfo:
            {
                _appLayer.setToInput();

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                break;
            }
        }
    }

    return _appLayer.generate(xfer);
}


int CtiProtocolION::decode( CtiXfer &xfer, int status )
{
    int alStatus;

    alStatus = _appLayer.decode(xfer, status);

    if( _appLayer.isTransactionComplete() )
    {
        switch( _ionState )
        {
            case IONStateInit:
            case IONStateRequestFeatureManagerInfo:
            {
                //  ACH:  check for errors before i just switch to listening mode

                _ionState = IONStateReceiveFeatureManagerInfo;

                break;
            }

            case IONStateReceiveFeatureManagerInfo:
            {
                unsigned char *buf;

                if( _appLayer.getPayloadLength() > 0 )
                {
                    buf = new unsigned char[_appLayer.getPayloadLength()];

                    if( buf != NULL )
                    {
                        _appLayer.putPayload(buf);

                        _dsIn.initialize(buf, _appLayer.getPayloadLength());

                        if( CtiIONDataStream::itemIs(_dsIn[0], CtiIONArray::IONUnsignedIntArray) )
                        {
                            _setup_handles = (CtiIONUnsignedIntArray *)_dsIn[0];

                            //  remove it so it doesn't get erased when dsIn is wiped
                            _dsIn.removeItem(0);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                                dout << endl;
                                dout << "Setup handles: " << endl;

                                for( int i = 0; i < _setup_handles->getSize(); i++ )
                                {
                                    dout << _setup_handles->getElement(i)->getValue() << "\t";
                                }

                                dout << endl << endl;
                            }

                            _ionState = IONStateRequestManagerInfo;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "No manager handles returned, aborting" << endl;
                            }

                            _ionState = IONStateAbort;
                        }

                        delete buf;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "Unable to allocate memory for ION datastream decode, aborting" << endl;
                        }

                        _ionState = IONStateAbort;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Zero-length application layer return for Feature Manager handle request, aborting" << endl;
                    }

                    _ionState = IONStateAbort;
                }

                break;
            }
            case IONStateRequestManagerInfo:
            {
                //  ACH:  check for errors before i just switch to listening mode

                _ionState = IONStateReceiveManagerInfo;

                break;
            }

            case IONStateReceiveManagerInfo:
            {
                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "Unknown state " << _ionState << " in CtiProtocolION::decode" << endl;
                }

                break;
            }
        }
    }
    else if( _appLayer.errorCondition() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _ionState = IONStateAbort;
    }

    return alStatus;
}


int CtiProtocolION::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
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


int CtiProtocolION::recvCommResult( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal;

    retVal = commIn(InMessage);

    return retVal;
}


int CtiProtocolION::commOut( OUTMESS *&OutMessage )
{
    int retVal = NoError;

    memcpy( OutMessage->Buffer.OutMessage, &_currentCommand, sizeof(_currentCommand) );
    OutMessage->OutLength   = sizeof(_currentCommand);

    OutMessage->EventCode   = RESULT;

    return retVal;
}


int CtiProtocolION::commIn( INMESS *InMessage )
{
    int retVal = NoError;

//    _appLayer.restoreRsp(InMessage->Buffer.InMessage, InMessage->InLength);

    return retVal;
}


int CtiProtocolION::sendCommResult( INMESS *InMessage )
{
    int retVal = NoError;

/*    if( _appLayer.isReplyExpected() )
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
*/    {
        InMessage->InLength = 0;
    }

    return retVal;
}


int CtiProtocolION::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    memcpy( &_currentCommand, OutMessage->Buffer.OutMessage, OutMessage->OutLength );

    _ionState = IONStateInit;

    return retVal;
}


bool CtiProtocolION::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?
    return _ionState == IONStateComplete || _ionState == IONStateAbort;
}


bool CtiProtocolION::hasInboundPoints( void )
{
    return false;  //_appLayer.hasInboundPoints();
}


void CtiProtocolION::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    //_appLayer.getInboundPoints(pointList);
}

