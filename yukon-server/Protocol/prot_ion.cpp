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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/10/30 16:05:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag

#include "prot_ion.h"


CtiProtocolION::CtiProtocolION()
{
    setMasterAddress(DefaultYukonIONMasterAddress);
    setSlaveAddress(DefaultSlaveAddress);
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
        _appLayer      = aRef._appLayer;
        _masterAddress = aRef._masterAddress;
        _slaveAddress  = aRef._slaveAddress;
    }

    return *this;
}


/*
INT CtiDeviceION::generateCommand( CtiXfer &Transfer )
{
    INT  retCode = NORMAL;

    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
        case CmdScanData:
        case CmdLoadProfileData:
            {
                resolveNextState( );

                _dllLayer->outFrame( Transfer.getOutBuffer( ), &Transfer.getOutCount( ) );
                Transfer.setCRCFlag( 0 );
                Transfer.setInCountExpected( 250 );
                Transfer.setNonBlockingReads( true );  //  porter does the reading, and he doesn't know
                                                       //    to look at the first bytes for the length
                                                       //    of the packet - so we blindly take what we're given.
                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") Invalid command " << getCurrentCommand( ) << " scanning " << getName( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}


INT CtiDeviceION::decodeResponse( CtiXfer &Transfer, INT commReturnValue )
{
    INT  retCode = NORMAL;

    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
        case CmdScanData:
        case CmdLoadProfileData:
            {
                _dllLayer->inFrame( Transfer.getInBuffer( ), *(Transfer.getInCountActual( )) );

                resolveNextState( );

                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") Invalid command " << getCurrentCommand( ) << " scanning " << getName( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}


resultdecode from dev_ion

    // intialize the command based on the in message
    setCurrentCommand( (CtiMeterCmdStates_t)InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0] );
    setCurrentState  ( (CtiMeterMachineStates_t)InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] );

    switch( getCurrentCommand( ) )
    {
        case CmdScanData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " Scan decode for device " << getName( ) << " in progress " << endl;
                }

                decodeResultScan( InMessage, TimeNow, vgList, retList, outList );
                break;
            }
        case CmdLoadProfileData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " LP decode for device " << getName( ) << " in progress " << endl;
                }

                // just in case we're getting an empty message
                if( getCurrentState( ) == StateScanReturnLoadProfile )
                {
                    decodeResultLoadProfile( InMessage, TimeNow, vgList, retList, outList );
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime( ) << " LP decode failed device " << getName( ) << " invalid state " << endl;
                    }
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " (" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName( ) << endl;
                }
            }
    }

    return NORMAL;


void CtiDeviceION::resolveNextState( void )
{
    CtiIONDataLinkLayer::DLLExternalStatus dllStatus;

    dllStatus = _dllLayer->getStatus( );

    //  if we're coming in here after the handshake, or we're doing another command
    if( getIONState( ) == IONStateUninitialized ||
        getIONState( ) == IONStateComplete )
    {
        //  this is only used for the external appearance of the device...
        setCurrentState( StateScanValueSet1 );
        //  ...  all real state info is kept inside the IONState types.
        setIONState( IONStateInit );
    }

    //  we only compute new states when we're uninitialized or
    //    done sending or receiving a whole message
    if( dllStatus == CtiIONDataLinkLayer::InDataComplete  ||
        dllStatus == CtiIONDataLinkLayer::OutDataComplete ||
        dllStatus == CtiIONDataLinkLayer::Uninitialized )
    {
        switch( getCurrentCommand( ) )
        {
            case CmdSelectMeter:
            {
                resolveNextStateSelectMeter( );
                break;
            }
            case CmdScanData:
            {
                resolveNextStateScanData( );
                break;
            }
            case CmdLoadProfileData:
            {
                resolveNextStateLoadProfile( );
                break;
            }
        }
    }

    if( getIONState( ) == IONStateComplete )
    {
        setCurrentState( StateScanComplete );
    }
    else if( getIONState( ) == IONStateAbort )
    {
        setCurrentState( StateScanAbort );
    }
}



void CtiDeviceION::resolveNextStateSelectMeter( void )
{
    switch( getIONState( ) )
    {
//        case IONStateInit:
//            {
//                break;
//            }
        default:
            setIONState( IONStateComplete );
    }
}


void CtiDeviceION::resolveNextStateScanData( void )
{
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;
    CtiIONMethod    *tmpMethod;
    unsigned char   *tmpBuf;

    switch( getIONState( ) )
    {
        case IONStateInit:
        case IONStateRequestFeatureManagerInfo:
            {
                tmpMethod = new CtiIONMethod( CtiIONMethod::ReadModuleSetupHandles );  //  ReadManagedClass
                tmpStatement = new CtiIONStatement( IONFeatureManagerHandle, tmpMethod );  // ustabeed 132
                tmpProgram = new CtiIONProgram( tmpStatement );
                _dsBuf = new CtiIONDataStream( );
                _dsBuf->appendItem( tmpProgram );
                _appLayer->init( *_dsBuf );
                _netLayer->init( *_appLayer, 0, 20000, 102 );
                _dllLayer->setToOutput( *_netLayer, _netLayer->getSrcID( ), _netLayer->getDstID( ) );

                //  also deletes the contained tmpProgram and tmpMethod
                delete _dsBuf;

                setIONState( IONStateReceiveFeatureManagerInfo );

                break;
            }

        case IONStateReceiveFeatureManagerInfo:
            {
                _dllLayer->setToInput( );

                setIONState( IONStateRequestManagerInfo );

                break;
            }

        case IONStateRequestManagerInfo:
            {
                _netLayer->init( *_dllLayer );
                _appLayer->init( *_netLayer );

                if( (tmpBuf = new unsigned char( _appLayer->getPayloadLength( ) )) != NULL )
                {
                    _appLayer->putPayload( tmpBuf );
                    _dsBuf = new CtiIONDataStream( tmpBuf, _appLayer->getPayloadLength( ) );
                    delete tmpBuf;

                    for( int i = 0; i < _dsBuf->getItemCount( ); i++ )
                    {
                        cout << _dsBuf->getItem(i)->getType( ) << endl;
                    }
                }

                setIONState( IONStateComplete );

                break;
            }

        case IONStateReceiveManagerInfo:
            {
                _dllLayer->setToInput( );
                break;
            }

        case IONStateRequestModuleInfo:
            {
                break;
            }

        case IONStateReceiveModuleInfo:
            {
                break;
            }

        case IONStateRequestData:
            {
                break;
            }

        case IONStateReceiveData:
            {
                break;
            }
    }
}


void CtiDeviceION::resolveNextStateLoadProfile( void )
{
    switch( getIONState( ) )
    {
        default:
            setIONState( IONStateComplete );
            break;

        case IONStateInit:
            {
                break;
            }

    }
}
*/

void CtiProtocolION::setMasterAddress( unsigned short address )
{
    _masterAddress = address;
}


void CtiProtocolION::setSlaveAddress( unsigned short address )
{
    _slaveAddress = address;
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

                dob.addObject(new CtiIONClass(CtiIONClass::Class0));

                _appLayer.addObjectBlock(dob);

                break;
            }
        case ION_Class123Read:
            {
                _appLayer.setCommand(CtiIONApplication::RequestRead);

                CtiIONObjectBlock dob1(CtiIONObjectBlock::NoIndex_NoRange),
                                  dob2(CtiIONObjectBlock::NoIndex_NoRange),
                                  dob3(CtiIONObjectBlock::NoIndex_NoRange);

                dob1.addObject(new CtiIONClass(CtiIONClass::Class1));
                dob2.addObject(new CtiIONClass(CtiIONClass::Class2));
                dob3.addObject(new CtiIONClass(CtiIONClass::Class3));

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
                    CtiIONAnalogOutputBlock *aout = new CtiIONAnalogOutputBlock(CtiIONAnalogOutputBlock::AOB16Bit);

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
                    CtiIONBinaryOutputControl *bout = new CtiIONBinaryOutputControl(CtiIONBinaryOutputControl::ControlRelayOutputBlock);

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
                tmpMethod    = new CtiIONMethod   ( CtiIONMethod::ReadModuleSetupHandles );  //  ReadManagedClass
                tmpStatement = new CtiIONStatement( IONFeatureManagerHandle, tmpMethod );  // ustabeed 132
                tmpProgram   = new CtiIONProgram  ( tmpStatement );

                _dsBuf.clear();
                _dsBuf.appendItem( tmpProgram );

                _appLayer.init( _dsBuf );

                _ionState = IONStateReceiveFeatureManagerInfo;

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

    return _appLayer.generate( xfer );
}


int CtiProtocolION::decode( CtiXfer &xfer, int status )
{
    return 0; //_appLayer.decode(xfer, status);
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
/*
    if( _appLayer.hasOutput() )
    {
        OUTMESS *OutMessage = new CtiOutMessage();

        InEchoToOut(InMessage, OutMessage);
        //  copy over the other stuff we need
        OutMessage->DeviceID = InMessage->DeviceID;
        OutMessage->Port     = InMessage->Port;

        retVal = sendCommRequest(OutMessage, outList);
    }
*/
    return retVal;
}


int CtiProtocolION::commOut( OUTMESS *&OutMessage )
{
    int retVal = NoError;

    memcpy( OutMessage->Buffer.OutMessage, &_currentCommand, sizeof(_currentCommand) );
    OutMessage->OutLength   = sizeof(_currentCommand);

//  these should be filled in by the porter-side device
//    OutMessage->Source      = _masterAddress;
//    OutMessage->Destination = _slaveAddress;

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
    return _appLayer.isTransactionComplete() | _appLayer.errorCondition();
}


bool CtiProtocolION::hasInboundPoints( void )
{
    return false;  //_appLayer.hasInboundPoints();
}


void CtiProtocolION::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    //_appLayer.getInboundPoints(pointList);
}

