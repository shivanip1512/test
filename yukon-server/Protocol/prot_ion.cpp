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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2003/04/02 15:53:20 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag

#include "prot_ion.h"

#include "ion_value_variable_program.h"
#include "ion_value_variable_fixedarray.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_struct_types.h"

#include "numstr.h"


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
        //  MAGIC NUMBER WARNING
        _digitalInValueRegisters.insert(make_pair(0x280 + i, 0x6001 + i));
    }

    for( i = 0; i < 128; i++ )
    {
        _externalPulseRegisters.insert(make_pair(1 + i, 0x68ae + i));
    }
}


void CtiProtocolION::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _srcID = srcID;
    _dstID = dstID;

    _appLayer.setAddresses(_srcID, _dstID);
}


CtiProtocolION::IONCommand CtiProtocolION::getCommand( void )
{
    return _currentCommand.command;
}


void CtiProtocolION::setCommand( IONCommand command )
{
    _currentCommand.command = command;
}


void CtiProtocolION::setCommand( IONCommand command, unsigned int unsigned_int_parameter )
{
    _currentCommand.command                 = command;
    _currentCommand.unsigned_int_parameter  = unsigned_int_parameter;
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

                case Command_AccumulatorScan:
                case Command_EventLogRead:
                {
                    generateEventLogRead();

                    break;
                }

                case Command_ExternalPulseTrigger:
                {
                    generateExternalPulseTrigger();

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

                case Command_AccumulatorScan:
                case Command_EventLogRead:
                {
                    decodeEventLogRead();

                    break;
                }

                case Command_ExternalPulseTrigger:
                {
                    decodeExternalPulseTrigger();

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
                ion_value_register_map::iterator itr;

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
                CtiIONBoolean        *tmpBoolean;
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() == _digitalInModules.size() &&
                    _dsIn.itemsAreType(CtiIONValueVariable::Variable_Boolean) )
                {
                    _pointData.clear();

                    for( int i = 0; i < _dsIn.size(); i++ )
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
                ion_value_register_map::iterator itr;

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

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "KW");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "KVA");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "KVAR");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Volts");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Current");
                pdata.name[19] = 0;

                _pointData.push_back(pdata);

                pdata.offset = 100;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn[i++]->getNumericValue();
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

            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            start        = CTIDBG_new CtiIONUnsignedInt(_eventLogLastPosition + 1);
            end          = CTIDBG_new CtiIONUnsignedInt(_eventLogCurrentPosition - 1);  //  it's a look-ahead counter, so get the record just before the end

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
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() > 0 && _dsIn[0]->isNumeric() )
                {
                    _eventLogCurrentPosition = _dsIn[0]->getNumericValue();

                    if( _eventLogLastPosition == 0 || _eventLogDepth == 0 )
                    {
                        _ionState = State_RequestEventLogDepth;
                    }
                    else
                    {
                        if( (_eventLogCurrentPosition - _eventLogLastPosition) >= _eventLogDepth )
                        {
                            _eventLogLastPosition = _eventLogCurrentPosition - _eventLogDepth;
                        }

                        if( (_eventLogLastPosition + 1) < _eventLogCurrentPosition )
                        {
                            _ionState = State_RequestEventLogRecords;
                        }
                        else
                        {
                            _ionState = State_Complete;
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event log position return, aborting" << endl;
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
                ion_pointdata_struct  pdata;
                RWTime Now;

                if( _dsIn.size() > 0 && _dsIn[0]->isNumeric() )
                {
                    _eventLogDepth = _dsIn[0]->getNumericValue();

                    if( (_eventLogCurrentPosition - _eventLogLastPosition) >= _eventLogDepth )
                    {
                        _eventLogLastPosition = _eventLogCurrentPosition - _eventLogDepth;
                    }

                    if( (_eventLogLastPosition + 1) < _eventLogCurrentPosition )
                    {
                        _ionState = State_RequestEventLogRecords;
                    }
                    else
                    {
                        _ionState = State_Complete;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event log depth return, aborting" << endl;
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
                ion_pointdata_struct  pdata;
                RWTime Now;

                _ionState = State_Complete;

                if( _dsIn[0]->isStructArray() )
                {
                    CtiIONStructArray *tmp = (CtiIONStructArray *)_dsIn[0];

                    if( tmp->isStructArrayType(CtiIONStructArray::StructArray_LogArray) )
                    {
                        _eventLogs.push_back((CtiIONLogArray *)_dsIn[0]);

                        _dsIn.erase(0);

                        CtiIONLogRecord *record;
                        CtiIONEvent     *event;
                        CtiIONCharArray *tmpArray;
                        const char      *tmpStr;

                        CtiIONStructArray::const_iterator itr;

                        //  output only for debug
                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                        {
                            for( itr = tmp->begin(); itr != tmp->end(); itr++ )
                            {
                                record = (CtiIONLogRecord *)(*itr);

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "Log Position: " << record->getLogPosition()->getValue() << endl;
                                    dout << "Timestamp (seconds)     : " << record->getTimestamp()->getSeconds() << endl;
                                    dout << "Timestamp (milliseconds): " << record->getTimestamp()->getMilliseconds() << endl;
                                    dout << "Log Values:" << endl;

                                    if( record->getLogValues()->isStructType(CtiIONStruct::StructType_Event) )
                                    {
                                        event = (CtiIONEvent *)(record->getLogValues());

                                        dout << "Priority:" << event->getPriority()->getValue() << endl;
                                        dout << "Event State:" << event->getEventState()->getValue() << endl;
                                        dout << "Cause Handle:" << event->getCauseHandle()->getValue() << endl;

                                        if( CtiIONFixedArray::isFixedArrayType( event->getCauseValue(), CtiIONFixedArray::FixedArray_Char ) )
                                        {
                                            tmpArray = (CtiIONCharArray *)(event->getCauseValue());

                                            tmpStr = tmpArray->toString();

                                            dout << "Cause Value:" << tmpStr << endl;
                                        }
                                        else
                                        {
                                            dout << "Cause Value is not a char array... ?" << endl;
                                        }

                                        dout << "Effect Handle:" << event->getEffectHandle()->getValue() << endl;

                                        if( CtiIONFixedArray::isFixedArrayType( event->getEffectValue(), CtiIONFixedArray::FixedArray_Char ) )
                                        {
                                            tmpArray = (CtiIONCharArray *)(event->getEffectValue());

                                            tmpStr = tmpArray->toString();

                                            dout << "Effect Value:" << tmpStr << endl;
                                        }
                                        else
                                        {
                                            dout << "Effect Value is not a char array... ?" << endl;
                                        }
                                    }
                                    else
                                    {
                                        dout << "Not an event structure...  life is bad." << endl;
                                    }

                                    dout << endl;
                                }
                            }
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Invalid event log record return, aborting" << endl;
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
                dout << "Unknown state " << _ionState << " in CtiProtocolION::decodeEventLogRead" << endl;
            }

            _ionState = State_Abort;

            break;
        }
    }
}


void CtiProtocolION::generateExternalPulseTrigger( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_SendExternalPulseTrigger:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            ion_value_register_map::iterator itr;

            if( (itr = _externalPulseRegisters.find(_currentCommand.unsigned_int_parameter)) != _externalPulseRegisters.end() )
            {
                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::WriteRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement((*itr).second, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                _dsOut.push_back(tmpProgram);
            }
            else
            {
                _ionState = State_Abort;
            }

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

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


void CtiProtocolION::decodeExternalPulseTrigger( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_SendExternalPulseTrigger:
        {
            _ionState = State_Complete;

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


bool CtiProtocolION::inputIsValid( CtiIONApplicationLayer &al, CtiIONDataStream &ds )
{
    bool result;
    unsigned char *buf;

    //  default is that the input is invalid
    result = false;

    if( al.getPayloadLength() > 0 )
    {
        buf = CTIDBG_new unsigned char[al.getPayloadLength()];

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

    unsigned char *buf;
    unsigned long len, offset;

    ion_result_descriptor_struct  header;
    ion_pointdata_struct         *points;
    CtiIONDataStream              tmpDS;

    //  clear out the data vectors
    _pointData.clear();

    while( !_eventLogs.empty() )
    {
        delete _eventLogs.back();
        _eventLogs.pop_back();
    }


    _eventLogsComplete = true;

    if( InMessage != NULL )
    {
        buf = InMessage->Buffer.InMessage;
        len = InMessage->InLength;
        offset = 0;

        if( sizeof(header) <= len )
        {
            memcpy(&header, buf + offset, sizeof(header));
            offset += sizeof(header);

            points = (ion_pointdata_struct *)(buf + offset);

            for( int i = 0; i < header.numPoints; i++ )
            {
                _pointData.push_back(points[i]);
                offset += sizeof(ion_pointdata_struct);
            }

            _eventLogsComplete = header.eventLogsComplete;

            tmpDS.initialize(buf + offset, header.eventLogLength);

            while( !tmpDS.empty() )
            {
                if( tmpDS.itemIsType(0, CtiIONStructArray::StructArray_LogArray) )
                {
                    _eventLogs.push_back((CtiIONLogArray *)tmpDS[0]);
                }
                else
                {
                    delete tmpDS[0];
                }

                tmpDS.erase(0);
            }
        }
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


bool CtiProtocolION::hasInboundData( void )
{
    return !_pointData.empty() || !_eventLogs.empty();
}


void CtiProtocolION::getInboundData( RWTPtrSlist< CtiPointDataMsg > &pointList, RWTPtrSlist< CtiSignalMsg > &signalList )
{
    vector< ion_pointdata_struct >::const_iterator p_itr;
    vector< CtiIONLogArray * >::const_iterator       e_itr;

    unsigned long maxEventRecord = 0;

    CtiPointDataMsg *pointdata;
    CtiSignalMsg    *signal;

    for( p_itr = _pointData.begin(); p_itr != _pointData.end(); p_itr++ )
    {
        //  NOTE:  offsets must be 1-based to work with Yukon
        pointdata = CTIDBG_new CtiPointDataMsg((*p_itr).offset, (*p_itr).value, NormalQuality, (*p_itr).type);
        pointdata->setString((*p_itr).name);
        pointdata->setTime(RWTime((*p_itr).time));

        pointList.append(pointdata);
    }

    _pointData.clear();

    for( e_itr = _eventLogs.begin(); e_itr != _eventLogs.end(); e_itr++ )
    {
        CtiIONLogArray  *tmpArray;
        CtiIONLogRecord *tmpRecord;
        CtiIONEvent     *tmpEvent;
        RWCString desc, action;

        if( *e_itr != NULL )
        {
            tmpArray = *e_itr;

            for( int i = 0; i < tmpArray->size(); i++ )
            {
                tmpRecord = tmpArray->at(i);

                if( CtiIONStruct::isStructType(tmpRecord->getLogValues(), CtiIONStruct::StructType_Event) )
                {
                    tmpEvent = (CtiIONEvent *)tmpRecord->getLogValues();

                    desc  = RWCString("pri=")      + tmpEvent->getPriority()->toString();
                    desc += RWCString(", cause=")  + tmpEvent->getCauseValue()->toString();
                    desc += RWCString(", effect=") + tmpEvent->getEffectValue()->toString();
                    desc += RWCString(", nlog=")   + "EventLogCtl 1";

                    action  = RWCString("c_h=")      + tmpEvent->getCauseHandle()->toString();
                    action += RWCString(", e_h=")    + tmpEvent->getEffectHandle()->toString();
                    action += RWCString(", state=")  + tmpEvent->getEventState()->toString();
                    action += RWCString(", record=") + tmpRecord->getLogPosition()->toString();

                    if( maxEventRecord < tmpRecord->getLogPosition()->getValue() )
                    {
                        maxEventRecord = tmpRecord->getLogPosition()->getValue();
                    }

                    signal = CTIDBG_new CtiSignalMsg(EventLogPointOffset, 0, desc, action, GeneralLogType);
                    signal->setMessageTime(RWTime(tmpRecord->getTimestamp()->getSeconds() + rwEpoch));

                    signalList.append(signal);
                }
            }

            delete (*e_itr);
        }
    }

    _eventLogs.clear();

    //  create a new pointdata msg to update our current event log position, if appropriate
    if( maxEventRecord > 0 )
    {
        if( maxEventRecord > _eventLogLastPosition )
        {
            _eventLogLastPosition = maxEventRecord;
        }

        RWCString msg("Event log position: " + CtiNumStr(maxEventRecord));

        pointdata = new CtiPointDataMsg(EventLogPointOffset, maxEventRecord, NormalQuality, AnalogPointType, msg, TAG_POINT_MUST_ARCHIVE);

        pointList.append(pointdata);
    }
}


bool CtiProtocolION::areEventLogsComplete( void )
{
    return _eventLogsComplete;
}


//  porter-side functions

int CtiProtocolION::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;
    ion_outmess_struct tmpOM;

    memcpy(&tmpOM, OutMessage->Buffer.OutMessage, sizeof(tmpOM));

    _currentCommand         = tmpOM.cmd_struct;
    _eventLogLastPosition   = tmpOM.event_log_last_position;

    _ionState = State_Init;

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

        InMessage->InLength = 0;

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

    vector< CtiIONLogArray * >::const_iterator itr;

    for( itr = _eventLogs.begin(); itr != _eventLogs.end(); itr++ )
    {
        if( *itr != NULL )
        {
            size += (*itr)->getSerializedLength();
        }
    }

    return size;
}


void CtiProtocolION::putResult( unsigned char *buf )
{
    int offset, eventlog_length;
    ion_result_descriptor_struct header;
    vector< ion_pointdata_struct >::const_iterator p_itr;
    vector< CtiIONLogArray * >::const_iterator     e_itr;

    offset = 0;

    header.numPoints = _pointData.size();

    eventlog_length = 0;
    for( e_itr = _eventLogs.begin(); e_itr != _eventLogs.end(); e_itr++ )
    {
        if( *e_itr != NULL )
        {
            eventlog_length += (*e_itr)->getSerializedLength();
        }
    }

    header.eventLogLength = eventlog_length;
    if( _eventLogLastPosition < (_eventLogCurrentPosition - 1) )
    {
        header.eventLogsComplete = false;
    }
    else
    {
        header.eventLogsComplete = true;
    }

    memcpy(buf + offset, &header, sizeof(header));
    offset += sizeof(header);

    for( p_itr = _pointData.begin(); p_itr != _pointData.end(); p_itr++ )
    {
        memcpy(buf + offset, &(*p_itr), sizeof(*p_itr));
        offset += sizeof(*p_itr);
    }

    for( e_itr = _eventLogs.begin(); e_itr != _eventLogs.end(); e_itr++ )
    {
        if( *e_itr != NULL )
        {
            (*e_itr)->putSerialized(buf + offset);

            offset += (*e_itr)->getSerializedLength();
        }
    }

    //  clear out the data vectors
    _pointData.clear();

    while( !_eventLogs.empty() )
    {
        delete _eventLogs.back();

        _eventLogs.pop_back();
    }
}


bool CtiProtocolION::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?

    return _ionState == State_Complete || _ionState == State_Abort;
}

