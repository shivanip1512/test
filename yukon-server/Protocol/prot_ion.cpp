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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/02/12 01:15:05 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag

#include "prot_ion.h"

#include "ion_value_variable_program.h"
#include "ion_value_variable_fixedarray.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_struct_types.h"


CtiProtocolION::CtiProtocolION()
{
    setAddresses(DefaultYukonIONMasterAddress, DefaultSlaveAddress);
    setConfigRead(false);
    resetEventLogInfo();
    initializeSets();
}


CtiProtocolION::CtiProtocolION(const CtiProtocolION &aRef)
{
    *this = aRef;
}


CtiProtocolION::~CtiProtocolION()
{
    _dsIn.clearAndDestroy();
    _dsOut.clearAndDestroy();
}


CtiProtocolION &CtiProtocolION::operator=(const CtiProtocolION &aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( this != &aRef )
    {
        _appLayer = aRef._appLayer;
        _srcID    = aRef._srcID;
        _dstID    = aRef._dstID;
    }

    return *this;
}


void CtiProtocolION::initializeSets( void )
{
    int i;

    for( i = 0; i < 38; i++ )
    {
        //  MAGIC NUMBER WARNING:
        _digitalInValueRegisters.insert(make_pair(0x280 + i, 0x6001 + i));
    }
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

    _ionState = State_Init;
}


void CtiProtocolION::resetEventLogInfo( void )
{
    _eventLogLastPosition = 0;
    _eventLogCurrentPosition = 0;
    _eventLogDepth = 0;
}


void CtiProtocolION::setEventLogLastPosition( unsigned long lastPosition )
{
    _eventLogLastPosition = lastPosition;
}


unsigned long CtiProtocolION::getEventLogLastPosition( void )
{
    return _eventLogLastPosition;
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
        if( !hasConfigBeenRead() )
        {
            generateConfigRead();
        }
        else
        {
            switch( _currentCommand.command )
            {
                case Command_ExceptionScan:
                {
                    generateExceptionScan();

                    break;
                }

                case Command_IntegrityScan:
                {
                    generateIntegrityScan();

                    break;
                }

                case Command_EventLogRead:
                {
                    generateEventLogRead();

                    break;
                }

//                case Command_ScanLoadProfile:

                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Unknown state " << _ionState << " in CtiProtocolION::generate" << endl;
                    }

                    _ionState = State_Abort;
                }
            }
        }
    }

    return _appLayer.generate(xfer);
}


int CtiProtocolION::decode( CtiXfer &xfer, int status )
{
    int alStatus;

    alStatus = _appLayer.decode(xfer, status);

    if( _appLayer.errorCondition() )
    {
        // ACH:  do various commands need to handle an error differently?
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _ionState = State_Abort;
    }
    //  ACH:  move isInputValid check here - allow for empty appLayer... ?
    else if( _appLayer.isTransactionComplete() )
    {
        if( !hasConfigBeenRead() )
        {
            decodeConfigRead();
        }
        else
        {
            switch( _currentCommand.command )
            {
                case Command_ExceptionScan:
                {
                    decodeExceptionScan();

                    break;
                }

                case Command_IntegrityScan:
                {
                    decodeIntegrityScan();

                    break;
                }

                case Command_EventLogRead:
                {
                    decodeEventLogRead();

                    break;
                }

//                case Command_ScanLoadProfile:

                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Unknown state " << _ionState << " in CtiProtocolION::decode" << endl;
                    }

                    _ionState = State_Abort;
                }
            }
        }
    }

    _dsIn.clearAndDestroy();

    return alStatus;
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
            tmpProgram   = CTIDBG_new CtiIONProgram();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
            tmpStatement = CTIDBG_new CtiIONStatement(IONFeatureManager, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.clearAndDestroy();

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveFeatureManagerInfo:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestManagerInfo:
        {
            _dsOut.clearAndDestroy();

            tmpProgram = CTIDBG_new CtiIONProgram();

            for( int i = 0; i < _setup_handles.size(); i++ )
            {
                tmpMethod = CTIDBG_new CtiIONMethod(CtiIONMethod::ReadManagedClass);

                tmpStatement = CTIDBG_new CtiIONStatement(_setup_handles[i], tmpMethod);

                tmpProgram->addStatement(tmpStatement);
            }

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveManagerInfo:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestPowerMeterModuleHandles:
        {
            int i;

            _dsOut.clearAndDestroy();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
            tmpStatement = CTIDBG_new CtiIONStatement(_handleManagerPowerMeter, tmpMethod);
            tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;

        }

        case State_ReceivePowerMeterModuleHandles:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestDigitalInputModuleHandles:
        {
            int i;

            _dsOut.clearAndDestroy();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
            tmpStatement = CTIDBG_new CtiIONStatement(_handleManagerDigitalIn, tmpMethod);
            tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveDigitalInputModuleHandles:
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
                if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedIntArray *tmpArray;

                    tmpArray = (CtiIONUnsignedIntArray *)_dsIn[0];
                    _setup_handles.clear();

                    for( int i = 0; i < tmpArray->size(); i++ )
                    {
                        _setup_handles.push_back((tmpArray->at(i))->getValue());
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
                _handleManagerDataRecorder = 0;
                _handleManagerDigitalIn    = 0;
                _handleManagerPowerMeter   = 0;

                if( _dsIn.size() == _setup_handles.size() &&
                    _dsIn.itemsAreType(CtiIONValueFixed::Fixed_UnsignedInt) )
                {
                    for( int i = 0; i < _dsIn.size(); i++ )
                    {
                        CtiIONUnsignedInt *managerClass = (CtiIONUnsignedInt *)_dsIn.at(i);

                        switch( managerClass->getValue() )
                        {
                            case Class_DataRec:
                            {
                                _handleManagerDataRecorder = _setup_handles[i];

                                break;
                            }

                            case Class_DigitalIn:
                            {
                                _handleManagerDigitalIn = _setup_handles[i];

                                break;
                            }

                            case Class_PowerMeter:
                            {
                                _handleManagerPowerMeter = _setup_handles[i];

                                break;
                            }
                        }
                    }
                }

                _setup_handles.clear();

                if( _handleManagerPowerMeter && _handleManagerDigitalIn && _handleManagerDataRecorder )
                {
                    _ionState = State_RequestPowerMeterModuleHandles;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Not all manager handles found, aborting" << endl;
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

        case State_RequestPowerMeterModuleHandles:
        {
            //  ACH:  check for errors before i just switch to listening mode

            _ionState = State_ReceivePowerMeterModuleHandles;

            break;
        }

        case State_ReceivePowerMeterModuleHandles:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                if( _dsIn.itemIsType(0, CtiIONValueFixed::Fixed_UnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _powerMeterModules.push_back(tmpHandle->getValue());

                    _ionState = State_RequestDigitalInputModuleHandles;
                }
                else if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _powerMeterModules.push_back(tmpHandle->getValue());

                    _ionState = State_RequestDigitalInputModuleHandles;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Power meter handles not returned, aborting" << endl;
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

        case State_RequestDigitalInputModuleHandles:
        {
            //  ACH:  check for errors before i just switch to listening mode

            _ionState = State_ReceiveDigitalInputModuleHandles;

            break;
        }

        case State_ReceiveDigitalInputModuleHandles:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                if( _dsIn.itemIsType(0, CtiIONValueFixed::Fixed_UnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _digitalInModules.push_back(tmpHandle->getValue());

                    _ionState = State_Complete;
                }
                else if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedIntArray *tmpHandleArray = (CtiIONUnsignedIntArray *)_dsIn.at(0);

                    for( int i = 0; i < tmpHandleArray->size(); i++ )
                    {
                        _digitalInModules.push_back(tmpHandleArray->at(i)->getValue());
                    }

                    _ionState = State_Init;
                    setConfigRead(true);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Digital input handles not returned, aborting" << endl;
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
                dout << "Unknown state " << _ionState << " in CtiProtocolION::decodeConfigRead" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::generateExceptionScan( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_RequestDigitalInputData:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            for( int i = 0; i < _digitalInModules.size(); i++ )
            {
                IONValueRegisterMap::iterator itr;

                if( (itr = _digitalInValueRegisters.find(_digitalInModules[i])) != _digitalInValueRegisters.end() )
                {
                    tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                    tmpStatement = CTIDBG_new CtiIONStatement((*itr).second, tmpMethod);

                    tmpProgram->addStatement(tmpStatement);
                }

            }

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveDigitalInputData:
        {
            _appLayer.setToInput();

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unknown state " << _ionState << " in CtiProtocolION::generateExceptionScan" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::decodeExceptionScan( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_RequestDigitalInputData:
        {
            _ionState = State_ReceiveDigitalInputData;

            break;
        }

        case State_ReceiveDigitalInputData:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONBoolean        *tmpValue;
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() == _digitalInModules.size() &&
                    _dsIn.itemsAreType(CtiIONValueVariable::Variable_Boolean) )
                {
                    _pointData.clear();

                    for( int i = 0; i < _dsIn.size(); i++ )
                    {
                        tmpValue = (CtiIONBoolean *)(_dsIn[i]);

                        //  MAGIC NUMBER:  1-based offset
                        pdata.offset = i + 1;
                        pdata.time   = Now.seconds();
                        pdata.type   = StatusPointType;
                        pdata.value  = tmpValue->getValue();

                        _snprintf(pdata.name, 19, "Digital Input %d", pdata.offset);
                        pdata.name[19] = 0;

                        _pointData.push_back(pdata);
                    }

                    _ionState = State_Complete;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid digital inputs return, aborting" << endl;
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
                dout << "Unknown state " << _ionState << " in CtiProtocolION::decodeExceptionScan" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::generateIntegrityScan( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_RequestPowerMeterData:
        {
            tmpProgram   = CTIDBG_new CtiIONProgram();

            for( int i = 0; i < _digitalInModules.size(); i++ )
            {
                IONValueRegisterMap::iterator itr;

                if( (itr = _digitalInValueRegisters.find(_digitalInModules[i])) != _digitalInValueRegisters.end() )
                {
                    tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                    tmpStatement = CTIDBG_new CtiIONStatement((*itr).second, tmpMethod);

                    tmpProgram->addStatement(tmpStatement);
                }

            }

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_KWtotal, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_KVAtotal, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_KVARtotal, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Va, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Vb, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Vc, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Ia, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Ib, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_Ic, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_PowerMeter1S_PFSIGNtotal, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.clearAndDestroy();

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceivePowerMeterData:
        {
            _appLayer.setToInput();

            break;
        }
    }
}


void CtiProtocolION::decodeIntegrityScan( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_RequestPowerMeterData:
        {
            _ionState = State_ReceivePowerMeterData;

            break;
        }

        case State_ReceivePowerMeterData:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                int i;
                CtiIONNumeric *tmpNumeric;
                CtiIONBoolean *tmpBoolean;
                ion_pointdata_struct pdata;
                RWTime Now;

                _pointData.clear();

                for( i = 0; i < _digitalInModules.size(); i++ )
                {
                    tmpBoolean = (CtiIONBoolean *)(_dsIn[i]);

                    //  MAGIC NUMBER:  1-based offset
                    pdata.offset = i + 1;
                    pdata.time   = Now.seconds();
                    pdata.type   = StatusPointType;
                    pdata.value  = tmpBoolean->getValue();

                    _snprintf(pdata.name, 19, "Digital Input %d", pdata.offset);
                    pdata.name[19] = 0;

                    _pointData.push_back(pdata);
                }

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "KW");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "KVA");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "KVAR");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                tmpNumeric = (CtiIONNumeric *)(_dsIn[i++]);

                pdata.offset = 100;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = tmpNumeric->getNumericValue();
                _snprintf(pdata.name, 19, "Power Factor");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                _ionState = State_Complete;
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
                dout << "Unknown state " << _ionState << " in CtiProtocolION::decodeIntegrityScan" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::generateEventLogRead( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_RequestEventLogPosition:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadLogRegisterPosition);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_EventLog, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveEventLogPosition:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestEventLogDepth:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_ELDepth, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveEventLogDepth:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestEventLogRecords:
        {
            CtiIONRange       *tmpRange;
            CtiIONUnsignedInt *start, *end;

            if( (_eventLogCurrentPosition - _eventLogLastPosition) >= _eventLogDepth )
            {
                _eventLogLastPosition = _eventLogCurrentPosition - _eventLogDepth;
            }

            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            start        = CTIDBG_new CtiIONUnsignedInt(_eventLogLastPosition);
            end          = CTIDBG_new CtiIONUnsignedInt(_eventLogCurrentPosition);

            tmpRange     = CTIDBG_new CtiIONRange(start, end);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadValue, tmpRange);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_EventLog, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveEventLogRecords:
        {
            _appLayer.setToInput();

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unknown state " << _ionState << " in CtiProtocolION::generateExceptionScan" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::decodeEventLogRead( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_RequestEventLogPosition:
        {
            _ionState = State_ReceiveEventLogPosition;

            break;
        }

        case State_ReceiveEventLogPosition:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONNumeric        *tmpValue;
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() > 0 && _dsIn[0]->isNumeric() )
                {
                    tmpValue = (CtiIONNumeric *)(_dsIn[0]);

                    //  MAGIC NUMBER WARNING:  subtract 1 because it's a look-ahead counter
                    _eventLogCurrentPosition = tmpValue->getNumericValue() - 1;

                    if( _eventLogLastPosition == 0 && _eventLogDepth == 0 )
                    {
                        _ionState = State_RequestEventLogDepth;
                    }
                    else
                    {
                        _ionState = State_RequestEventLogRecords;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event handle return, aborting" << endl;
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

        case State_RequestEventLogDepth:
        {
            _ionState = State_ReceiveEventLogDepth;

            break;
        }

        case State_ReceiveEventLogDepth:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONNumeric        *tmpValue;
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() > 0 && _dsIn[0]->isNumeric() )
                {
                    tmpValue = (CtiIONNumeric *)(_dsIn[0]);

                    _eventLogDepth = tmpValue->getNumericValue();

                    _ionState = State_RequestEventLogRecords;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event handle return, aborting" << endl;
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

        case State_RequestEventLogRecords:
        {
            _ionState = State_ReceiveEventLogRecords;

            break;
        }

        case State_ReceiveEventLogRecords:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONNumeric        *tmpValue;
                ion_pointdata_struct  pdata;
                RWTime Now;

                _ionState = State_Complete;
/*                if( _dsIn.itemIs(0, CtiIONValue::IONNumeric) )
                {
                    tmpValue = (CtiIONNumeric *)(_dsIn[0]);

                    _eventLogDepth = tmpValue->getNumericValue();

                    _ionState = State_RequestEventLogDepth;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event handle return, aborting" << endl;
                    }

                    _ionState = State_Abort;
                }
*/            }
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
                dout << "Unknown state " << _ionState << " in CtiProtocolION::decodeEventLogRead" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


bool CtiProtocolION::inputIsValid( CtiIONApplicationLayer &al, CtiIONDataStream &ds )
{
    bool result;
    unsigned char *buf;

    //  default is that the input is invalid
    result = false;

    if( al.getPayloadLength() > 0 )
    {
        buf = new unsigned char[al.getPayloadLength()];

        if( buf != NULL )
        {
            al.putPayload(buf);

            ds.initialize(buf, _appLayer.getPayloadLength());

            if( ds.isValid() )
            {
                //  this is the only place that valid is set
                result = true;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "Invalid datastream" << endl;
                }
            }

            delete buf;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unable to allocate memory for ION datastream decode" << endl;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Zero-length application layer return" << endl;
        }
    }

    return result;
}


//  scanner-/PIL-side functions

int CtiProtocolION::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    ion_outmess_struct tmp_om_struct;

    if( OutMessage != NULL )
    {
        tmp_om_struct.cmd_struct              = _currentCommand;
        tmp_om_struct.event_log_last_position = _eventLogLastPosition;

        //  ACH:  add support for variable-length control point attachments
        memcpy( OutMessage->Buffer.OutMessage, &tmp_om_struct, sizeof(tmp_om_struct) );
        OutMessage->OutLength = sizeof(tmp_om_struct);

        OutMessage->EventCode = RESULT;

        outList.append(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolION::recvCommResult( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    int offset;

    ion_result_descriptor_struct header;
    ion_pointdata_struct *tmpPoint;

    offset = 0;

    _pointData.clear();

    if( InMessage != NULL )
    {
        if( sizeof(header) <= InMessage->InLength )
        {
            memcpy(&header, InMessage->Buffer.InMessage + offset, sizeof(header));
            offset += sizeof(header);

            for( int i = 0; i < header.numPoints; i++ )
            {
                //  NASTY CAST w00t!!!
                tmpPoint = (ion_pointdata_struct *)(InMessage->Buffer.InMessage + offset + (i * sizeof(ion_pointdata_struct)));

                _pointData.push_back(*tmpPoint);
            }
        }
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


bool CtiProtocolION::hasInboundPoints( void )
{
    return !_pointData.empty();
}


void CtiProtocolION::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    PointDataIterator itr;
    CtiPointDataMsg *pMsg;

    for( itr = _pointData.begin(); itr != _pointData.end(); itr++ )
    {
        //  NOTE:  offsets must be 1-based to work with Yukon
        pMsg = new CtiPointDataMsg((*itr).offset, (*itr).value, NormalQuality, (*itr).type);
        pMsg->setString((*itr).name);
        pMsg->setTime(RWTime((*itr).time));

        pointList.append(pMsg);
    }

    _pointData.clear();
}


//  porter-side functions

int CtiProtocolION::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;
    ion_outmess_struct tmpOM;

    //  ACH:  hmm...  probably have to support putvalue requests someday...  that means
    //          supporting variable length outmess requests (w/ values)
    memcpy(&tmpOM, OutMessage->Buffer.OutMessage, sizeof(tmpOM));

    setCommand(tmpOM.cmd_struct.command);

    _eventLogLastPosition = tmpOM.event_log_last_position;

    return retVal;
}


int CtiProtocolION::sendCommResult( INMESS *InMessage )
{
    int retVal = NoError;

    if( resultSize() < sizeof( InMessage->Buffer ) )
    {
        putResult( InMessage->Buffer.InMessage );
        InMessage->InLength = resultSize();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "!!!  results > 4k!  !!!" << endl;
            dout << "Not sending!!" << endl;
        }

        //  oh well, closest thing to reality - not enough room in outmess
        retVal = MemoryError;
    }

    return retVal;
}


unsigned long CtiProtocolION::resultSize( void )
{
    unsigned long size;

    size  = 0;
    size += sizeof(ion_result_descriptor_struct);
    size += sizeof(ion_pointdata_struct) * _pointData.size();
//    size += sizeof(ion_eventdata_struct) * _eventData.size();

    return size;
}


void CtiProtocolION::putResult( unsigned char *buf )
{
    int offset;
    ion_result_descriptor_struct header;
    PointDataIterator itr;

    offset = 0;

    header.numEvents = 0;  //  someday
    header.numPoints = _pointData.size();

    memcpy(buf + offset, &header, sizeof(header));
    offset += sizeof(header);

    for( itr = _pointData.begin(); itr != _pointData.end(); itr++ )
    {
        memcpy(buf + offset, &(*itr), sizeof(*itr));
        offset += sizeof(*itr);
    }
}


bool CtiProtocolION::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?

    return _ionState == State_Complete || _ionState == State_Abort;
}

