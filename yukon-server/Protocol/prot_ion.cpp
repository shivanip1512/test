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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2003/01/07 21:17:55 $
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
            tmpMethod    = CTIDBG_new CtiIONMethod   (CtiIONMethod::ReadModuleSetupHandles);
            tmpStatement = CTIDBG_new CtiIONStatement(IONFeatureManager, tmpMethod);
            tmpProgram   = CTIDBG_new CtiIONProgram  (tmpStatement);

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

            for( int i = 0; i < _setup_handles->size(); i++ )
            {
                tmpMethod = CTIDBG_new CtiIONMethod(CtiIONMethod::ReadManagedClass);

                tmpStatement = CTIDBG_new CtiIONStatement(_setup_handles->getElement(i)->getValue(), tmpMethod);

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
                if( _dsIn.itemIs(0, CtiIONArray::IONUnsignedIntArray) )
                {
                    _setup_handles = (CtiIONUnsignedIntArray *)_dsIn.at(0);

                    //  remove it so it doesn't get erased when dsIn is wiped
                    _dsIn.erase(0);

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

                if( _dsIn.size() == _setup_handles->size() &&
                    _dsIn.itemsAre(CtiIONValue::IONUnsignedInt) )
                {
                    for( int i = 0; i < _dsIn.size(); i++ )
                    {
                        CtiIONUnsignedInt *managerClass = (CtiIONUnsignedInt *)_dsIn.at(i);

                        switch( managerClass->getValue() )
                        {
                            case Class_DataRec:
                            {
                                _handleManagerDataRecorder = _setup_handles->getElement(i)->getValue();

                                break;
                            }

                            case Class_DigitalIn:
                            {
                                _handleManagerDigitalIn = _setup_handles->getElement(i)->getValue();

                                break;
                            }

                            case Class_PowerMeter:
                            {
                                _handleManagerPowerMeter = _setup_handles->getElement(i)->getValue();

                                break;
                            }
                        }
                    }
                }

                delete _setup_handles;

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
                if( _dsIn.itemIs(0, CtiIONValue::IONUnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _powerMeterModules.push_back(tmpHandle->getValue());

                    _ionState = State_RequestDigitalInputModuleHandles;
                }
                else if( _dsIn.itemIs(0, CtiIONArray::IONUnsignedIntArray) )
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
                if( _dsIn.itemIs(0, CtiIONValue::IONUnsignedInt) )
                {
                    CtiIONUnsignedInt *tmpHandle = (CtiIONUnsignedInt *)_dsIn.at(0);

                    _digitalInModules.push_back(tmpHandle->getValue());

                    _ionState = State_Complete;
                }
                else if( _dsIn.itemIs(0, CtiIONArray::IONUnsignedIntArray) )
                {
                    CtiIONUnsignedIntArray *tmpHandleArray = (CtiIONUnsignedIntArray *)_dsIn.at(0);

                    for( int i = 0; i < tmpHandleArray->size(); i++ )
                    {
                        _digitalInModules.push_back(tmpHandleArray->getElement(i)->getValue());
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
                    _dsIn.itemsAre(CtiIONValue::IONBoolean) )
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


//  private functions, wrapped by sendCommRequest and recvCommResult

int CtiProtocolION::commOut( OUTMESS *&OutMessage )
{
    int retVal = NoError;

    memcpy( OutMessage->Buffer.OutMessage, &_currentCommand, sizeof(_currentCommand) );
    OutMessage->OutLength = sizeof(_currentCommand);

    OutMessage->EventCode = RESULT;

    return retVal;
}


int CtiProtocolION::commIn( INMESS *InMessage )
{
    int retVal = NoError;

    int offset;

    ion_result_descriptor_struct header;
    ion_pointdata_struct *tmpPoint;

    offset = 0;

    _pointData.clear();

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

    return retVal;
}


//  porter-side functions

int CtiProtocolION::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    memcpy( &_currentCommand, OutMessage->Buffer.OutMessage, OutMessage->OutLength );

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

