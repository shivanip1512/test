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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2002/12/30 16:24:45 $
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
    setConfigRead(false);
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

    _currentCommand.command = command;
}


bool CtiProtocolION::hasConfigBeenRead( void )
{
    return _configRead;
}


void CtiProtocolION::setConfigRead( bool read )
{
    _configRead = read;
}


int CtiProtocolION::generate( CtiXfer &xfer )
{
    if( _appLayer.isTransactionComplete() )
    {
        if( hasConfigBeenRead() )
        {

        }
        else
        {
            generateConfigRead();
        }
    }

    return _appLayer.generate(xfer);
}


void CtiProtocolION::generateConfigRead( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_RequestFeatureManagerInfo:
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

        case State_ReceiveFeatureManagerInfo:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestManagerInfo:
        {
            int i;

            _dsOut.clear();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadManagedClass);
            tmpStatement = CTIDBG_new CtiIONStatement(_setup_handles->getElement(_currentManagerHandle)->getValue(), tmpMethod);
            tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

            _dsOut.appendItem(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            break;

        }

        case State_ReceiveManagerInfo:
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


int CtiProtocolION::decode( CtiXfer &xfer, int status )
{
    int alStatus;

    alStatus = _appLayer.decode(xfer, status);

    if( _appLayer.isTransactionComplete() )
    {
        if( hasConfigBeenRead() )
        {
/*            switch( _currentCommand.command )
            {
                case
            }*/
        }
        else
        {
            decodeConfigRead();
        }
    }
    else if( _appLayer.errorCondition() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _ionState = State_Abort;
    }

    return alStatus;
}


void CtiProtocolION::decodeConfigRead( void )
{
    switch( _ionState )
    {
        case State_Init:
        case State_RequestFeatureManagerInfo:
        {
            //  ACH:  check for errors before i just switch to listening mode

            _ionState = State_ReceiveFeatureManagerInfo;

            break;
        }

        case State_ReceiveFeatureManagerInfo:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                if( CtiIONDataStream::itemIs(_dsIn[0], CtiIONArray::IONUnsignedIntArray) )
                {
                    _setup_handles = (CtiIONUnsignedIntArray *)_dsIn[0];
                    //  remove it so it doesn't get erased when dsIn is wiped
                    _dsIn.removeItem(0);

                    _currentManagerHandle = 0;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                        dout << endl;
                        dout << "Setup handles: " << endl;

                        for( int i = 0; i < _setup_handles->size(); i++ )
                        {
                            dout << _setup_handles->getElement(i)->getValue() << "\t";
                        }

                        dout << endl << endl;
                    }

                    _ionState = State_RequestManagerInfo;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "No manager handles returned, aborting" << endl;
                    }

                    _ionState = State_Abort;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                //  ACH:  maybe increment protocol error count and try again...
                _ionState = State_Abort;
            }

            break;
        }
        case State_RequestManagerInfo:
        {
            //  ACH:  check for errors before i just switch to listening mode

            _ionState = State_ReceiveManagerInfo;

            break;
        }

        case State_ReceiveManagerInfo:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                if( CtiIONDataStream::itemIs(_dsIn[0], CtiIONValue::IONUnsignedInt) )
                {
                    CtiIONUnsignedInt *managerClass = (CtiIONUnsignedInt *)_dsIn[0];

                    switch( managerClass->getValue() )
                    {
                        case Class_DataRec:
                        {
                            _handleManagerDataRecorder = _setup_handles->getElement(_currentManagerHandle)->getValue();

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "Found Data Recorder manager handle: " << _handleManagerDataRecorder << endl;
                            }

                            break;
                        }

                        case Class_DigitalIn:
                        {
                            _handleManagerDigitalIn = _setup_handles->getElement(_currentManagerHandle)->getValue();

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "Found Digital Input manager handle: " << _handleManagerDigitalIn << endl;
                            }

                            break;
                        }

                        case Class_PowerMeter:
                        {
                            _handleManagerPowerMeter = _setup_handles->getElement(_currentManagerHandle)->getValue();

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "Found Power Meter manager handle: " << _handleManagerPowerMeter << endl;
                            }

                            break;
                        }
                    }

                    _currentManagerHandle++;

                    if( _currentManagerHandle < _setup_handles->size() )
                    {
                        _ionState = State_RequestManagerInfo;
                    }
                    else
                    {
                        delete _setup_handles;

                        _ionState = State_Complete;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Module class not returned, aborting" << endl;
                    }

                    _ionState = State_Abort;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                //  ACH:  maybe increment protocol error count and try again...
                _ionState = State_Abort;
            }

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


bool CtiProtocolION::inputIsValid( CtiIONApplicationLayer &al, CtiIONDataStream &ds )
{
    bool result = false;
    unsigned char *buf;

    if( al.getPayloadLength() > 0 )
    {
        buf = new unsigned char[al.getPayloadLength()];

        if( buf != NULL )
        {
            al.putPayload(buf);

            ds.initialize(buf, _appLayer.getPayloadLength());

            if( ds.isValid() )
            {
                result = true;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "No manager handles returned, aborting" << endl;
                }
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

            _ionState = State_Abort;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Zero-length application layer return, aborting" << endl;
        }

        _ionState = State_Abort;
    }

    return result;
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

    _ionState = State_Init;

    return retVal;
}


bool CtiProtocolION::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?
    return _ionState == State_Complete || _ionState == State_Abort;
}


bool CtiProtocolION::hasInboundPoints( void )
{
    return false;  //_appLayer.hasInboundPoints();
}


void CtiProtocolION::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    //_appLayer.getInboundPoints(pointList);
}

