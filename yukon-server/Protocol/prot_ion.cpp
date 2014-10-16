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
* REVISION     :  $Revision: 1.38 $
* DATE         :  $Date: 2008/04/25 21:45:14 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag

#include "cparms.h"
#include "msg_signal.h"

#include "prot_ion.h"

#include "ion_value_variable_program.h"
#include "ion_value_variable_fixedarray.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_struct_types.h"

#include "numstr.h"

using namespace std;


CtiProtocolION::CtiProtocolION()
{
    setAddresses(DefaultYukonIONMasterAddress, DefaultSlaveAddress);
    setConfigRead(false);
    initializeSets();

    _client_eventLogLastPosition = 0;
    _eventLogLastPosition        = 0;
    _eventLogCurrentPosition     = 1;
    _eventLogDepth               = 1;
}

CtiProtocolION::~CtiProtocolION()
{
    _dsIn.clearAndDestroy();
    _dsOut.clearAndDestroy();
}

void CtiProtocolION::initializeSets( void )
{
    int i;

    //  <INSERT MAGIC NUMBER WARNINGS HERE>

    for( i = 0; i < 38; i++ )
    {
        _digitalInValueRegisters.insert(make_pair(0x280 + i, 0x6001 + i));
    }
    for( i = 0; i < 30; i++ )
    {
        _digitalOutValueRegisters.insert(make_pair(0x300 + i, 0x6027 + i));
    }


    for( i = 0; i < 128; i++ )
    {
        _externalPulseRegisters.insert(make_pair(1 + i, 0x68ae + i));
    }

    for( i = 0; i < 32; i++ )
    {
        _externalBooleanRegisters.insert(make_pair(1 + i, 0x608f + i));
    }
    for( i = 0; i < 28; i++ )
    {
        _externalBooleanRegisters.insert(make_pair(33 + i, 0x633e + i));
    }
}


void CtiProtocolION::setAddresses( unsigned short masterAddress, unsigned short slaveAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _appLayer.setAddresses(_masterAddress, _slaveAddress);
}


CtiProtocolION::IONCommand CtiProtocolION::getCommand( void )
{
    return _submittedCommand.command;
}


void CtiProtocolION::setCommand( IONCommand command )
{
    _submittedCommand.command = command;
}


void CtiProtocolION::setCommand( IONCommand command, unsigned int unsigned_int_parameter )
{
    _submittedCommand.command                = command;
    _submittedCommand.unsigned_int_parameter = unsigned_int_parameter;
}


void CtiProtocolION::restoreCommand( IONCommand command )
{
    _executingCommand.command = command;
    _protocolErrors = 0;
    _abortStatus    = ClientErrors::None;
    _infoString     = "";

    _ionState   = State_Init;
    _retryState = _ionState;
}


void CtiProtocolION::restoreCommand( IONCommand command, unsigned int unsigned_int_parameter )
{
    _executingCommand.command                = command;
    _executingCommand.unsigned_int_parameter = unsigned_int_parameter;
    _protocolErrors = 0;
    _abortStatus    = ClientErrors::None;
    _infoString     = "";

    _ionState   = State_Init;
    _retryState = _ionState;
}


bool CtiProtocolION::commandRequiresRequeueOnFail( IONCommand command )
{
    bool retVal = false;

    switch( command )
    {
        case Command_ExternalPulseTrigger:
        case Command_EventLogRead:
        {
            retVal = true;

            break;
        }

        default:
        {
            break;
        }
    }

    return retVal;
}


bool CtiProtocolION::hasConfigBeenRead( void )
{
    return _configRead;
}


void CtiProtocolION::setConfigRead( bool read )
{
    _configRead = read;
}


YukonError_t CtiProtocolION::generate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    if( _appLayer.isTransactionComplete() )
    {
        if( !hasConfigBeenRead() )
        {
            generateConfigRead();
        }
        else
        {
            switch( _executingCommand.command )
            {
                case Command_ExceptionScan:
                case Command_ExceptionScanPostControl:
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

                case Command_TimeSync:
                {
                    generateTimeSync();

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
                    CTILOG_ERROR(dout, "Unknown command "<< _executingCommand.command);

                    _ionState   = State_Abort;
                    _retryState = State_Abort;
                }
            }
        }
    }

    if( _ionState != State_Abort )
    {
        retVal = _appLayer.generate(xfer);
    }
    else
    {
        xfer.setOutCount(0);
        xfer.setInCountExpected(0);

        retVal = ClientErrors::BadRange;
    }

    return retVal;
}


YukonError_t CtiProtocolION::decode( CtiXfer &xfer, YukonError_t status )
{
    const YukonError_t alStatus = _appLayer.decode(xfer, status);

    if( _appLayer.errorCondition() )
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout, "_appLayer.errorCondition() is true");
        }

        _protocolErrors++;
        _ionState = _retryState;
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
            switch( _executingCommand.command )
            {
                case Command_ExceptionScan:
                case Command_ExceptionScanPostControl:
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

                case Command_TimeSync:
                {
                    decodeTimeSync();

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
                    CTILOG_ERROR(dout, "Unknown state "<< _ionState);

                    _ionState   = State_Abort;
                    _retryState = State_Abort;
                }
            }
        }
    }

    if( _protocolErrors > Protocol_ErrorMax )
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout,  "_protocolErrors > Protocol_ErrorMax ("<< _protocolErrors <<" > "<< Protocol_ErrorMax <<")");
        }

        _ionState    = State_Abort;
        _retryState  = State_Abort;
        _abortStatus = alStatus;
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

        case State_RequestDigitalOutputModuleHandles:
        {
            int i;

            _dsOut.clearAndDestroy();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
            tmpStatement = CTIDBG_new CtiIONStatement(_handleManagerDigitalOut, tmpMethod);
            tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveDigitalOutputModuleHandles:
        {
            _appLayer.setToInput();

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown state "<< _ionState);
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
            //  any errors would have been caught by decode(), so it's safe to switch to listening mode
            _ionState   = State_ReceiveFeatureManagerInfo;

            break;
        }

        case State_ReceiveFeatureManagerInfo:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedIntArray *tmpArray;

                    tmpArray = (CtiIONUnsignedIntArray *)_dsIn.at(0);
                    _setup_handles.clear();

                    for( int i = 0; i < tmpArray->size(); i++ )
                    {
                        _setup_handles.push_back((tmpArray->at(i))->getValue());
                    }

                    _ionState   = State_RequestManagerInfo;
                    _retryState = _ionState;
                }
                else
                {
                    CTILOG_ERROR(dout, "No manager handles returned, aborting");

                    _ionState   = State_Abort;
                    _retryState = State_Abort;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        case State_RequestManagerInfo:
        {
            //  any errors would have been caught by decode(), so it's safe to switch to listening mode
            _ionState   = State_ReceiveManagerInfo;

            break;
        }

        case State_ReceiveManagerInfo:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                _handleManagerDataRecorder = 0;
                _handleManagerDigitalIn    = 0;
                _handleManagerDigitalOut   = 0;
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

                            case Class_DigitalOut:
                            {
                                _handleManagerDigitalOut = _setup_handles[i];

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

                if( _handleManagerPowerMeter && _handleManagerDigitalIn && _handleManagerDigitalOut && _handleManagerDataRecorder )
                {
                    _ionState   = State_RequestPowerMeterModuleHandles;
                    _retryState = _ionState;
                }
                else
                {
                    CTILOG_ERROR(dout, "Not all manager handles found, aborting");

                    _ionState   = State_Abort;
                    _retryState = State_Abort;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        case State_RequestPowerMeterModuleHandles:
        {
            //  any errors would have been caught by decode(), so it's safe to switch to listening mode
            _ionState   = State_ReceivePowerMeterModuleHandles;

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

                    _ionState   = State_RequestDigitalInputModuleHandles;
                    _retryState = _ionState;
                }
                else if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _powerMeterModules.push_back(tmpHandle->getValue());

                    _ionState   = State_RequestDigitalInputModuleHandles;
                    _retryState = _ionState;
                }
                else
                {
                    CTILOG_ERROR(dout, "Power meter handles not returned, aborting");

                    _ionState   = State_Abort;
                    _retryState = _ionState;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _ionState   = State_Abort;
                _retryState = _ionState;
            }

            break;
        }

        case State_RequestDigitalInputModuleHandles:
        {
            //  any errors would have been caught by decode(), so it's safe to switch to listening mode
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

                    _ionState = State_RequestDigitalOutputModuleHandles;
                    _retryState = _ionState;
                }
                else if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedIntArray *tmpHandleArray = (CtiIONUnsignedIntArray *)_dsIn.at(0);

                    for( int i = 0; i < tmpHandleArray->size(); i++ )
                    {
                        _digitalInModules.push_back(tmpHandleArray->at(i)->getValue());
                    }

                    _ionState   = State_RequestDigitalOutputModuleHandles;
                    _retryState = _ionState;
                }
                else
                {
                    CTILOG_ERROR(dout, "Digital input handles not returned, aborting");

                    _ionState   = State_Abort;
                    _retryState = _ionState;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        case State_RequestDigitalOutputModuleHandles:
        {
            //  any errors would have been caught by decode(), so it's safe to switch to listening mode
            _ionState = State_ReceiveDigitalOutputModuleHandles;

            break;
        }

        case State_ReceiveDigitalOutputModuleHandles:
        {
            if( inputIsValid( _appLayer, _dsIn ) )
            {
                //  it looks like this is superfluous - it should always come back as an array (and elsewhere, too)
                if( _dsIn.itemIsType(0, CtiIONValueFixed::Fixed_UnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _digitalOutModules.push_back(tmpHandle->getValue());

                    _ionState   = State_Init;
                    _retryState = _ionState;
                    setConfigRead(true);
                }
                else if( _dsIn.itemIsType(0, CtiIONFixedArray::FixedArray_UnsignedInt) )
                {
                    CtiIONUnsignedIntArray *tmpHandleArray = (CtiIONUnsignedIntArray *)_dsIn.at(0);

                    for( int i = 0; i < tmpHandleArray->size(); i++ )
                    {
                        _digitalOutModules.push_back(tmpHandleArray->at(i)->getValue());
                    }

                    _ionState   = State_Init;
                    _retryState = _ionState;
                    setConfigRead(true);
                }
                else
                {
                    CTILOG_ERROR(dout, "Digital output handles not returned");

                    _ionState   = State_Init;
                    _retryState = _ionState;
                    setConfigRead(true);
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState   = State_Abort;
            _retryState = _ionState;

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
        {
            //  clear this out before ReceiveDigitalInputData and ReceiveDigitalOutputData fill it up
            clearCollectedData();
        }
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

        case State_RequestDigitalOutputData:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            for( int i = 0; i < _digitalOutModules.size(); i++ )
            {
                ion_value_register_map::iterator itr;

                if( (itr = _digitalOutValueRegisters.find(_digitalOutModules[i])) != _digitalOutValueRegisters.end() )
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

        case State_ReceiveDigitalOutputData:
        {
            _appLayer.setToInput();

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _retryState = _ionState;
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
            _ionState   = State_ReceiveDigitalInputData;

            break;
        }

        case State_ReceiveDigitalInputData:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONBoolean        *tmpBoolean;
                ion_pointdata_struct  pdata;
                CtiTime Now;

                if( _dsIn.size() == _digitalInModules.size() &&
                    _dsIn.itemsAreType(CtiIONValueVariable::Variable_Boolean) )
                {
                    for( int i = 0; i < _dsIn.size(); i++ )
                    {
                        tmpBoolean = (CtiIONBoolean *)_dsIn.at(i);

                        //  MAGIC NUMBER:  1-based offset
                        pdata.offset = i + 1;
                        pdata.time   = Now.seconds();
                        pdata.type   = StatusPointType;
                        pdata.value  = tmpBoolean->getValue();

                        _snprintf(pdata.name, 19, "Digital Input %d", pdata.offset);
                        pdata.name[19] = 0;

                        _collectedPointData.push_back(pdata);
                    }

                    if( !_digitalOutModules.empty() )
                    {
                        _ionState   = State_RequestDigitalOutputData;
                        _retryState = _ionState;
                    }
                    else
                    {
                        _ionState   = State_Complete;
                        _retryState = _ionState;
                    }
                }
                else
                {
                    if( isDebugLudicrous() )
                    {
                        CTILOG_DEBUG(dout, "Invalid digital inputs return");
                    }

                    _ionState   = State_RequestDigitalOutputData;
                    _retryState = _ionState;

                    _infoString += "\nNo digital inputs to return";
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        case State_RequestDigitalOutputData:
        {
            _ionState   = State_ReceiveDigitalOutputData;

            break;
        }

        case State_ReceiveDigitalOutputData:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                CtiIONBoolean        *tmpBoolean;
                ion_pointdata_struct  pdata;
                CtiTime Now;

                if( _dsIn.size() == _digitalOutModules.size() &&
                    _dsIn.itemsAreType(CtiIONValueVariable::Variable_Boolean) )
                {
                    for( int i = 0; i < _dsIn.size(); i++ )
                    {
                        tmpBoolean = (CtiIONBoolean *)_dsIn.at(i);

                        //  MAGIC NUMBER:  1-based offset, +50 offset for seperating digital out from digital ins
                        pdata.offset = i + 1 + 50;
                        pdata.time   = Now.seconds();
                        pdata.type   = StatusPointType;
                        pdata.value  = tmpBoolean->getValue();

                        _snprintf(pdata.name, 19, "Digital Output %d", pdata.offset);
                        pdata.name[19] = 0;

                        _collectedPointData.push_back(pdata);
                    }

                    _ionState   = State_Complete;
                    _retryState = _ionState;
                }
                else
                {
                    if( isDebugLudicrous() )
                    {
                        CTILOG_DEBUG(dout, "Invalid digital outputs return");
                    }

                    _ionState   = State_Complete;
                    _retryState = _ionState;
                    _infoString += "\nNo digital outputs to return";
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState   = State_Abort;
            _retryState = _ionState;

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
        {
            //  clear these out before the decodes fill it up
            clearCollectedData();
        }
        case State_RequestPowerMeterData:
        {
            tmpProgram   = CTIDBG_new CtiIONProgram();

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

            if( gConfigParms.isTrue("DUKE_ISSG") )
            {
                ion_value_register_map::iterator itr;

                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement(Register_Arithmetic01Result1, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement(Register_Arithmetic01Result2, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement(Register_Arithmetic01Result7, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement(Register_Arithmetic01Result8, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                if( (itr = _externalBooleanRegisters.find(5)) != _externalBooleanRegisters.end() )
                {
                    tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
                    tmpStatement = CTIDBG_new CtiIONStatement((*itr).second, tmpMethod);

                    tmpProgram->addStatement(tmpStatement);
                }
            }

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
                int i = 0;
                CtiIONBoolean *tmpBoolean;
                ion_pointdata_struct pdata;
                CtiTime Now;

                _collectedPointData.clear();

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "KW");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "KVA");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "KVAR");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Volts");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Volts");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Volts");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_A_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase A Current");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_B_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase B Current");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = OFFSET_INSTANTANEOUS_PHASE_C_CURRENT;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Phase C Current");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                pdata.offset = 100;
                pdata.time   = Now.seconds();
                pdata.type   = AnalogPointType;
                pdata.value  = _dsIn.at(i++)->getNumericValue();
                _snprintf(pdata.name, 19, "Power Factor");
                pdata.name[19] = 0;

                _collectedPointData.push_back(pdata);

                if( gConfigParms.isTrue("DUKE_ISSG") )
                {
                    pdata.offset = 257;
                    pdata.time   = Now.seconds();
                    pdata.type   = AnalogPointType;
                    pdata.value  = _dsIn.at(i++)->getNumericValue();
                    _snprintf(pdata.name, 19, "Notify Delay");
                    pdata.name[19] = 0;

                    _collectedPointData.push_back(pdata);

                    pdata.offset = 258;
                    pdata.time   = Now.seconds();
                    pdata.type   = AnalogPointType;
                    pdata.value  = _dsIn.at(i++)->getNumericValue();
                    _snprintf(pdata.name, 19, "Control Delay");
                    pdata.name[19] = 0;

                    _collectedPointData.push_back(pdata);

                    pdata.offset = 263;
                    pdata.time   = Now.seconds();
                    pdata.type   = AnalogPointType;
                    pdata.value  = _dsIn.at(i++)->getNumericValue();
                    _snprintf(pdata.name, 19, "Notify Remain");
                    pdata.name[19] = 0;

                    _collectedPointData.push_back(pdata);

                    pdata.offset = 264;
                    pdata.time   = Now.seconds();
                    pdata.type   = AnalogPointType;
                    pdata.value  = _dsIn.at(i++)->getNumericValue();
                    _snprintf(pdata.name, 19, "Control Remain");
                    pdata.name[19] = 0;

                    _collectedPointData.push_back(pdata);

                    if( i < _dsIn.size() )
                    {
                        pdata.offset = 101;
                        pdata.time   = Now.seconds();
                        pdata.type   = StatusPointType;
                        pdata.value  = _dsIn.at(i++)->getNumericValue();
                        _snprintf(pdata.name, 19, "Program");
                        pdata.name[19] = 0;

                        _collectedPointData.push_back(pdata);
                    }
                }

                _ionState   = State_Complete;
                _retryState = _ionState;
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _ionState   = State_Abort;
                _retryState = _ionState;
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;

            break;
        }
    }
}


void CtiProtocolION::generateEventLogRead( void )
{
    CtiIONMethod      *tmpMethod;
    CtiIONStatement   *tmpStatement;
    CtiIONProgram     *tmpProgram;
    CtiIONRange       *tmpRange;
    CtiIONUnsignedInt *start,
                      *end;


    switch( _ionState )
    {
        case State_Init:
        {
            //  clear these out before the decodes fill it up
            clearCollectedData();
        }
        case State_RequestEventLogStatusInfo:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadLogRegisterPosition);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_EventLog, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadRegisterValue);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_ELDepth, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveEventLogStatusInfo:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestEventLogLastPositionSearch:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            start = CTIDBG_new CtiIONUnsignedInt(_eventLogSearchPokePoint);
            end   = CTIDBG_new CtiIONUnsignedInt(_eventLogSearchPokePoint);

            tmpRange     = CTIDBG_new CtiIONRange(start, end);

            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadValue, tmpRange);
            tmpStatement = CTIDBG_new CtiIONStatement(Register_EventLogController_EventLog, tmpMethod);

            tmpProgram->addStatement(tmpStatement);

            _dsOut.push_back(tmpProgram);

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        case State_ReceiveEventLogLastPositionSearch:
        {
            _appLayer.setToInput();

            break;
        }

        case State_RequestEventLogRecords:
        {
            _dsOut.clearAndDestroy();

            tmpProgram   = CTIDBG_new CtiIONProgram();

            //  getting the one immediately following our last position
            start        = CTIDBG_new CtiIONUnsignedInt(_eventLogLastPosition + 1);

            //  ((41 - 1) - (35 + 1)) = 5, for example - getting 36, 37, 38, 39, 40
            if( ((_eventLogCurrentPosition - 1) - (_eventLogLastPosition + 1)) > 5 )
            {
                end = CTIDBG_new CtiIONUnsignedInt(_eventLogLastPosition + 5);  //  get a max of 5 at once
            }
            else
            {
                end = CTIDBG_new CtiIONUnsignedInt(_eventLogCurrentPosition - 1);  //  it's a look-ahead counter, so get the record just before the end
            }

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
            CTILOG_ERROR(dout, "Unknown state "<< _ionState)

            _ionState = State_Abort;
            _retryState = _ionState;
        }
    }
}


void CtiProtocolION::decodeEventLogRead( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_RequestEventLogStatusInfo:
        {
            _ionState = State_ReceiveEventLogStatusInfo;

            break;
        }

        case State_ReceiveEventLogStatusInfo:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                ion_pointdata_struct  pdata;
                CtiTime Now;

                if( _dsIn.size() >= 2 && _dsIn.at(0)->isNumeric() && _dsIn.at(1)->isNumeric() )
                {
                    if( _dsIn.at(0)->getNumericValue() > 0 &&
                        _dsIn.at(1)->getNumericValue() > 0 )
                    {
                        _eventLogCurrentPosition = _dsIn.at(0)->getNumericValue();
                        _eventLogDepth           = _dsIn.at(1)->getNumericValue();

                        //  Check if _eventLogLastPosition is outside the range defined
                        //    by _eventLogCurrentPosition and _eventLogDepth
                        if( !eventLogLastPositionValid(_eventLogCurrentPosition, _eventLogDepth, _eventLogLastPosition) )
                        {
                            //  unreasonable value, so reset _eventLogLastPosition

                            //  make sure we don't go negative
                            if( _eventLogCurrentPosition < _eventLogDepth )
                            {
                                _eventLogLastPosition = 0;
                            }
                            else
                            {
                                //  set it to the element just before the ones we want to get,
                                //    since it keeps track of the records already retrieved
                                _eventLogLastPosition = _eventLogCurrentPosition - _eventLogDepth - 1;
                            }
                        }

                        if( (_eventLogLastPosition + 1) >= _eventLogCurrentPosition )
                        {
                            //  we seem to have gotten all of the event logs
                            _ionState   = State_Complete;
                            _retryState = _ionState;
                        }
                        else
                        {
                            _eventLogSearchSuccessPoint = _eventLogCurrentPosition;   //  must be >= 1
                            _eventLogSearchFailPoint    = _eventLogLastPosition + 1;
                            _eventLogSearchPokePoint    = _eventLogSearchFailPoint;

                            _eventLogSearchCounter = 0;

                            _ionState   = State_RequestEventLogLastPositionSearch;
                            _retryState = _ionState;
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Event log position/depth return < 0, aborting" );

                        _ionState   = State_Abort;
                        _retryState = _ionState;
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Invalid event log position/depth return, aborting");

                    _ionState = State_Abort;
                    _retryState = _ionState;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        case State_RequestEventLogLastPositionSearch:
        {
            _ionState = State_ReceiveEventLogLastPositionSearch;

            break;
        }

        case State_ReceiveEventLogLastPositionSearch:
        {
            if( inputIsValid(_appLayer, _dsIn) )
            {
                ion_pointdata_struct  pdata;
                CtiTime Now;

                if( _dsIn.at(0)->isStruct() )
                {
                    CtiIONStruct *tmpStruct = (CtiIONStruct *)_dsIn.at(0);

                    if( tmpStruct->isStructType(CtiIONStruct::StructType_Exception) )
                    {
                        _eventLogSearchFailPoint = _eventLogSearchPokePoint;
                    }
                }

                if( _dsIn.at(0)->isStructArray() )
                {
                    CtiIONStructArray *tmpStructArray = (CtiIONStructArray *)_dsIn.at(0);

                    if( tmpStructArray->isStructArrayType(CtiIONStructArray::StructArrayType_LogArray) )
                    {
                        _eventLogSearchSuccessPoint = _eventLogSearchPokePoint;
                    }
                }

                //  if the fail point is within 1
                if( _eventLogSearchSuccessPoint <= (_eventLogSearchFailPoint + 1) )
                {
                    //  subtract 1 so we collect the successful one
                    _eventLogLastPosition = _eventLogSearchSuccessPoint - 1;

                    _ionState = State_RequestEventLogRecords;
                    _retryState = _ionState;
                }
                else if( ++_eventLogSearchCounter > 20 )
                {
                    CTILOG_WARN(dout, "Breaking out of possible eventlog search infinite loop. _eventLogSearchCounter > 20, setting _eventLogLastPosition = "<< _eventLogSearchSuccessPoint);

                    _eventLogLastPosition = _eventLogSearchSuccessPoint;

                    _ionState = State_RequestEventLogRecords;
                    _retryState = _ionState;
                }
                else
                {
                    _eventLogSearchPokePoint = (_eventLogSearchSuccessPoint + _eventLogSearchFailPoint) / 2;

                    _ionState = State_RequestEventLogLastPositionSearch;
                    _retryState = _ionState;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
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
                CtiTime Now;

                _ionState = State_Complete;
                _retryState = _ionState;

                if( _dsIn.at(0)->isStructArray() )
                {
                    CtiIONStructArray *tmp = (CtiIONStructArray *)_dsIn.at(0);

                    if( tmp->isStructArrayType(CtiIONStructArray::StructArrayType_LogArray) )
                    {
                        _collectedEventLogs.push_back((CtiIONLogArray *)_dsIn.at(0));

                        _dsIn.erase(0);

                        CtiIONLogRecord *record;
                        CtiIONEvent     *event;
                        CtiIONCharArray *tmpArray;
                        const char      *tmpStr;
                        unsigned long    currentLogEntry;

                        CtiIONStructArray::const_iterator itr;

                        for( itr = tmp->begin(); itr != tmp->end(); itr++ )
                        {
                            record = (CtiIONLogRecord *)(*itr);

                            currentLogEntry = record->getLogPosition()->getValue();

                            if( _eventLogLastPosition < currentLogEntry )
                            {
                                _eventLogLastPosition = currentLogEntry;
                            }

                            //  output only for debug
                            if( isDebugLudicrous() )
                            {
                                Cti::FormattedList itemList;

                                itemList.add("Log Position")             << record->getLogPosition()->getValue();
                                itemList.add("Timestamp (seconds)")      << record->getTimestamp()->getSeconds();
                                itemList.add("Timestamp (milliseconds)") << record->getTimestamp()->getMilliseconds();
                                itemList <<"--- Log Values ---";

                                if( record->getLogValues()->isStructType(CtiIONStruct::StructType_Event) )
                                {
                                    event = (CtiIONEvent *)(record->getLogValues());

                                    itemList.add("Priority")     << event->getPriority()->getValue();
                                    itemList.add("Event State")  << event->getEventState()->getValue();
                                    itemList.add("Cause Handle") << event->getCauseHandle()->getValue();

                                    if( CtiIONFixedArray::isFixedArrayType( event->getCauseValue(), CtiIONFixedArray::FixedArray_Char ) )
                                    {
                                        tmpArray = (CtiIONCharArray *)(event->getCauseValue());

                                        tmpStr = tmpArray->toString();

                                        itemList.add("Cause Value") << tmpStr;
                                    }
                                    else
                                    {
                                        itemList.add("Cause Value") << "is not a char array... ?";
                                    }

                                    itemList.add("Effect Handle") << event->getEffectHandle()->getValue();

                                    if( CtiIONFixedArray::isFixedArrayType( event->getEffectValue(), CtiIONFixedArray::FixedArray_Char ) )
                                    {
                                        tmpArray = (CtiIONCharArray *)(event->getEffectValue());

                                        tmpStr = tmpArray->toString();

                                        itemList.add("Effect Value") << tmpStr;
                                    }
                                    else
                                    {
                                        itemList.add("Effect Value") <<"is not a char array... ?";
                                    }
                                }
                                else
                                {
                                    itemList << "Not an event structure...  life is bad.";
                                }


                                CTILOG_DEBUG(dout, itemList);
                            }
                        }
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Invalid event log record return, aborting");

                    _ionState = State_Abort;
                    _retryState = _ionState;
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid input, setting state to "<< _retryState);

                _protocolErrors++;
                _ionState = _retryState;
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;
        }
    }
}


bool CtiProtocolION::eventLogLastPositionValid( unsigned long currentPos, unsigned long depth, unsigned long lastPos )
{
    bool retVal = false;

    //  ensure that:
    //
    //  (currentPos - depth) < lastPos < currentPos
    //
    if( lastPos < currentPos )
    {
        if( (lastPos + depth) > currentPos )
        {
            retVal = true;
        }
    }

    return retVal;
}


void CtiProtocolION::generateTimeSync( void )
{
    CtiIONMethod    *tmpMethod;
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;

    switch( _ionState )
    {
        case State_Init:
        case State_SendTimeSync:
        {
            _appLayer.setToTimeSync();

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;
        }
    }
}


void CtiProtocolION::decodeTimeSync( void )
{
    //  ACH:  ditto of decode()
    switch( _ionState )
    {
        case State_Init:
        case State_SendTimeSync:
        {
            _ionState = State_Complete;
            _retryState = _ionState;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;
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

            if( (itr = _externalPulseRegisters.find(_executingCommand.unsigned_int_parameter)) != _externalPulseRegisters.end() )
            {
                tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::WriteRegisterValue);
                tmpStatement = CTIDBG_new CtiIONStatement((*itr).second, tmpMethod);

                tmpProgram->addStatement(tmpStatement);

                _dsOut.push_back(tmpProgram);
            }
            else
            {
                _ionState = State_Abort;
                _retryState = _ionState;
            }

            _appLayer.setToOutput(_dsOut);

            _dsOut.clearAndDestroy();

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;
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
            _retryState = _ionState;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unknown state "<< _ionState);

            _ionState = State_Abort;
            _retryState = _ionState;
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

            ds.initialize(buf, al.getPayloadLength());

            if( ds.isValid() )
            {
                //  this is the only place that valid is set
                result = true;
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid datastream");
            }

            delete [] buf;
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to allocate memory for ION datastream decode");
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Zero-length application layer return");
    }

    return result;
}


//  ****  scanner-/PIL-side functions  ****


//  this function will only be called from client-side, after
//    the device checks its event log point in dynamicpointdispatch.
void CtiProtocolION::setEventLogLastPosition( unsigned long lastPosition )
{
    _client_eventLogLastPosition = lastPosition;
}


//  this function is only called from client-side to check if
//    we need to query dynamicpointdispatch for the last event log retrieved
unsigned long CtiProtocolION::getEventLogLastPosition( void )
{
    return _client_eventLogLastPosition;
}


YukonError_t CtiProtocolION::sendCommRequest( OUTMESS *&OutMessage, list< OUTMESS* > &outList )
{
    YukonError_t retVal = ClientErrors::None;

    ion_outmess_struct tmp_om_struct;

    if( OutMessage != NULL )
    {
        tmp_om_struct.cmd_struct                  = _submittedCommand;
        tmp_om_struct.client_eventLogLastPosition = getEventLogLastPosition();  //  send what the client thinks (may be overridden by
                                                                                //    porter if there's contention between two clients)

        //  ACH:  add support for variable-length control point attachments
        memcpy( OutMessage->Buffer.OutMessage, &tmp_om_struct, sizeof(tmp_om_struct) );
        OutMessage->OutLength = sizeof(tmp_om_struct);

        OutMessage->EventCode = RESULT;

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        retVal = ClientErrors::Memory;
    }

    return retVal;
}


YukonError_t CtiProtocolION::recvCommResult( const INMESS &InMessage, list< OUTMESS* > &outList )
{
    YukonError_t retVal = ClientErrors::None;

    const unsigned char *buf;
    unsigned long len, offset;

    ion_result_descriptor_struct  header;
    const ion_pointdata_struct   *points;
    CtiIONDataStream              tmpDS;
    char *tmpStr;

    //  clear out the data vectors
    clearInboundData();

    _eventLogsComplete = false;

    buf = InMessage.Buffer.InMessage;
    len = InMessage.InLength;
    offset = 0;

    if( sizeof(header) <= len )
    {
        memcpy(&header, buf + offset, sizeof(header));
        offset += sizeof(header);

        tmpStr = CTIDBG_new char[header.resultDescriptorStringLength];
        memcpy(tmpStr, buf + offset, header.resultDescriptorStringLength);
        tmpStr[header.resultDescriptorStringLength-1] = 0;
        _returnedInfoString = tmpStr;
        delete [] tmpStr;
        offset += header.resultDescriptorStringLength;

        points = (const ion_pointdata_struct *)(buf + offset);

        for( int i = 0; i < header.numPoints; i++ )
        {
            _returnedPointData.push_back(points[i]);
            offset += sizeof(ion_pointdata_struct);
        }

        _eventLogsComplete = header.eventLogsComplete;

        tmpDS.initialize(buf + offset, header.eventLogLength);

        while( !tmpDS.empty() )
        {
            if( tmpDS.itemIsType(0, CtiIONStructArray::StructArrayType_LogArray) )
            {
                _returnedEventLogs.push_back((CtiIONLogArray *)tmpDS.at(0));
            }
            else
            {
                delete tmpDS.at(0);
            }

            tmpDS.erase(0);
        }
    }
    else
    {
        retVal = ClientErrors::DataMissing;
    }

    return retVal;
}


void CtiProtocolION::getInboundData( list< CtiPointDataMsg* > &pointList, list< CtiSignalMsg* > &signalList, string &returnedInfo )
{
    vector< ion_pointdata_struct >::const_iterator p_itr;
    vector< CtiIONLogArray * >::const_iterator       e_itr;

    unsigned long maxEventRecord = 0;

    CtiPointDataMsg *pointdata;
    CtiSignalMsg    *signal;

    returnedInfo = _returnedInfoString;

    for( p_itr = _returnedPointData.begin(); p_itr != _returnedPointData.end(); p_itr++ )
    {
        //  NOTE:  offsets must be 1-based to work with Yukon
        pointdata = CTIDBG_new CtiPointDataMsg((*p_itr).offset, (*p_itr).value, NormalQuality, (*p_itr).type);
        pointdata->setString((*p_itr).name);
        pointdata->setTime(CtiTime((*p_itr).time));
        pointdata->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);

        pointList.push_back(pointdata);
    }

    //  ACH/FIX:  this should be moved to porter-side...  he should be the one sending the event logs, and keeping track of current event log position
    for( e_itr = _returnedEventLogs.begin(); e_itr != _returnedEventLogs.end(); e_itr++ )
    {
        CtiIONLogArray  *tmpArray;
        CtiIONLogRecord *tmpRecord;
        CtiIONEvent     *tmpEvent;
        string desc, action;

        if( *e_itr != NULL )
        {
            tmpArray = *e_itr;

            for( int i = 0; i < tmpArray->size(); i++ )
            {
                tmpRecord = tmpArray->at(i);

                if( CtiIONStruct::isStructType(tmpRecord->getLogValues(), CtiIONStruct::StructType_Event) )
                {
                    tmpEvent = (CtiIONEvent *)tmpRecord->getLogValues();

                    //  DESCRIPTION :
                    //  pri=xxx, cause=xxxxxxxxx, effect=xxxxxxxxxx, nlog=xxxxx
                    //
                    //  ACTION (Additional Info) :
                    //  c_h=xxx, e_h=xxx, state=x, record=xxxxx

                    desc  = string("pri=")      + tmpEvent->getPriority()->toString();
                    desc += string(", cause=")  + tmpEvent->getCauseValue()->toString();
                    desc += string(", effect=") + tmpEvent->getEffectValue()->toString();
                    desc += string(", nlog=")   + "EventLogCtl 1";

                    action  = string("c_h=")      + tmpEvent->getCauseHandle()->toString();
                    action += string(", e_h=")    + tmpEvent->getEffectHandle()->toString();
                    action += string(", state=")  + tmpEvent->getEventState()->toString();
                    action += string(", record=") + tmpRecord->getLogPosition()->toString();

                    if( maxEventRecord < tmpRecord->getLogPosition()->getValue() )
                    {
                        maxEventRecord = tmpRecord->getLogPosition()->getValue();
                    }

                    signal = CTIDBG_new CtiSignalMsg(EventLogPointOffset, 0, desc, action, GeneralLogType);
                    signal->setMessageTime(CtiTime(tmpRecord->getTimestamp()->getSeconds() ));

                    signalList.push_back(signal);
                }
            }
        }
    }

    //  create a new pointdata msg to update our current event log position, if appropriate
    //  ACH/FIX:  this should be moved to porter-side...  he should be the one sending the event logs, and keeping track of current event log position
    if( maxEventRecord > 0 )
    {
        //  FIX:  perhaps this should happen in recvCommResult?  odd for this function to be manipulating internal variables
        //if( maxEventRecord > getEventLogLastPosition() )
        //{
            //  always take what Porter says...  so he can override bad counters
            setEventLogLastPosition(maxEventRecord);
        //}

        string msg("Event log position: " + CtiNumStr(maxEventRecord));

        pointdata = new CtiPointDataMsg(EventLogPointOffset, maxEventRecord, NormalQuality, AnalogPointType, msg, TAG_POINT_MUST_ARCHIVE);

        pointList.push_back(pointdata);
    }
}


void CtiProtocolION::clearInboundData( void )
{
    clearReturnedData();
}


void CtiProtocolION::clearReturnedData( void )
{
    _returnedPointData.clear();

    while( !_returnedEventLogs.empty() )
    {
        delete _returnedEventLogs.back();
        _returnedEventLogs.pop_back();
    }
}


bool CtiProtocolION::hasPointUpdate( CtiPointType_t type, int offset ) const
{
    vector< ion_pointdata_struct >::const_iterator p_itr;
    bool found = false;

    for( p_itr = _returnedPointData.begin(); p_itr != _returnedPointData.end() && !found; p_itr++ )
    {
        if( (*p_itr).offset == offset && (*p_itr).type == type )
        {
            found = true;
        }
    }

    return found;
}



double CtiProtocolION::getPointUpdateValue( CtiPointType_t type, int offset ) const
{
    vector< ion_pointdata_struct >::const_iterator p_itr;
    bool   found = false;
    double value = 0.0;

    for( p_itr = _returnedPointData.begin(); p_itr != _returnedPointData.end() && !found; p_itr++ )
    {
        if( (*p_itr).offset == offset && (*p_itr).type == type )
        {
            value = (*p_itr).value;
            found = true;
        }
    }

    return value;
}



bool CtiProtocolION::areEventLogsComplete( void )
{
    return _eventLogsComplete;
}


//  porter-side functions

YukonError_t CtiProtocolION::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retVal = ClientErrors::None;
    ion_outmess_struct tmpOM;

    memcpy(&tmpOM, OutMessage->Buffer.OutMessage, sizeof(tmpOM));

    restoreCommand(tmpOM.cmd_struct.command, tmpOM.cmd_struct.unsigned_int_parameter);

    if( isDebugLudicrous() )
    {
        CTILOG_DEBUG(dout, "tmpOM.client_eventLogLastPosition = "<< tmpOM.client_eventLogLastPosition );
    }

    //  make sure that we don't have collisions between clients (i.e., Macs and Scanner)
    if( tmpOM.client_eventLogLastPosition >= _eventLogLastPosition )
    {
        _eventLogLastPosition = tmpOM.client_eventLogLastPosition;
    }

    return retVal;
}


YukonError_t CtiProtocolION::sendCommResult( INMESS &InMessage )
{
    YukonError_t retVal = ClientErrors::None;

    if( _ionState == State_Abort )
    {
        InMessage.ErrorCode = _abortStatus;
        InMessage.InLength = 0;
    }
    else if( resultSize() < sizeof( InMessage.Buffer ) )
    {
        putResult( InMessage.Buffer.InMessage );
        InMessage.InLength = resultSize();
    }
    else
    {
        CTILOG_ERROR(dout, "!!! results > 4k! - Not sending!");

        InMessage.InLength = 0;

        //  oh well, closest thing to reality - not enough room in outmess
        retVal = ClientErrors::Memory;
    }

    return retVal;
}


unsigned long CtiProtocolION::resultSize( void )
{
    unsigned long size;
    vector< CtiIONLogArray * >::const_iterator itr;

    size  = 0;

    size += sizeof(ion_result_descriptor_struct);

    //  ACH:  limit this size?
    size += _infoString.length() + 1;

    size += sizeof(ion_pointdata_struct) * _collectedPointData.size();

    for( itr = _collectedEventLogs.begin(); itr != _collectedEventLogs.end(); itr++ )
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

    header.numPoints = _collectedPointData.size();

    //  ACH:  limit this size?
    header.resultDescriptorStringLength = _infoString.length() + 1;

    eventlog_length = 0;
    for( e_itr = _collectedEventLogs.begin(); e_itr != _collectedEventLogs.end(); e_itr++ )
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

    memcpy(buf + offset, _infoString.c_str(), _infoString.length() + 1);
    offset += _infoString.length() + 1;

    for( p_itr = _collectedPointData.begin(); p_itr != _collectedPointData.end(); p_itr++ )
    {
        memcpy(buf + offset, &(*p_itr), sizeof(*p_itr));
        offset += sizeof(*p_itr);
    }

    for( e_itr = _collectedEventLogs.begin(); e_itr != _collectedEventLogs.end(); e_itr++ )
    {
        if( *e_itr != NULL )
        {
            (*e_itr)->putSerialized(buf + offset);

            offset += (*e_itr)->getSerializedLength();
        }
    }

    clearCollectedData();
}


bool CtiProtocolION::isTransactionComplete( void ) const
{
    //  ACH: factor in application layer retries... ?

    return _ionState == State_Complete || _ionState == State_Abort;
}


void CtiProtocolION::clearCollectedData( void )
{
    _collectedPointData.clear();

    while( !_collectedEventLogs.empty() )
    {
        delete _collectedEventLogs.back();
        _collectedEventLogs.pop_back();
    }
}


