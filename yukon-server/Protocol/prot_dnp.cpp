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
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2004/05/27 15:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "logger.h"
#include "utility.h"
#include "porter.h"  //  for the RESULT EventCode flag
#include "prot_dnp.h"
#include "dnp_object_class.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_time.h"


CtiProtocolDNP::CtiProtocolDNP()
{
    setAddresses(DefaultSlaveAddress, DefaultYukonDNPMasterAddress);
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


void CtiProtocolDNP::initLayers( void )
{
    //_appLayer.setOptions(_options);
}


void CtiProtocolDNP::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _appLayer.setAddresses(_slaveAddress, _masterAddress);
}


void CtiProtocolDNP::setOptions( int options )
{
    _options = options;

    _appLayer.setOptions(options);
}


void CtiProtocolDNP::setCommand( DNPCommand command, dnp_output_point *points, int numPoints )
{
    unsigned char *tmp;
    int tmplen;

    switch( command )
    {
        case DNP_WriteTime:
        {
            CtiDNPTime *timeNow = CTIDBG_new CtiDNPTime(CtiDNPTime::TimeAndDate);
            RWTime Now;

            timeNow->setSeconds(Now.seconds() - rwEpoch);

            _appLayer.setCommand(CtiDNPApplication::RequestWrite);

            CtiDNPObjectBlock dob(CtiDNPObjectBlock::NoIndex_ByteQty);

            dob.addObject(timeNow);

            _appLayer.addObjectBlock(dob);

            break;
        }
        case DNP_ReadTime:
        {
            _appLayer.setCommand(CtiDNPApplication::RequestRead);

            CtiDNPObjectBlock dob(CtiDNPObjectBlock::NoIndex_NoRange,
                                  CtiDNPTime::Group, CtiDNPTime::TimeAndDate);

            _appLayer.addObjectBlock(dob);

            break;
        }
        case DNP_Class0Read:
        {
            _appLayer.setCommand(CtiDNPApplication::RequestRead);

            CtiDNPObjectBlock dob(CtiDNPObjectBlock::NoIndex_NoRange);

            dob.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class0));

            _appLayer.addObjectBlock(dob);

            break;
        }
        case DNP_Class1230Read:
        {
            _appLayer.setCommand(CtiDNPApplication::RequestRead);

            CtiDNPObjectBlock dob1(CtiDNPObjectBlock::NoIndex_NoRange),
                              dob2(CtiDNPObjectBlock::NoIndex_NoRange),
                              dob3(CtiDNPObjectBlock::NoIndex_NoRange),
                              dob0(CtiDNPObjectBlock::NoIndex_NoRange);

            dob1.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class1));
            dob2.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class2));
            dob3.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class3));
            dob0.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class0));

            _appLayer.addObjectBlock(dob1);
            _appLayer.addObjectBlock(dob2);
            _appLayer.addObjectBlock(dob3);
            _appLayer.addObjectBlock(dob0);

            break;
        }
        case DNP_Class123Read:
        {
            _appLayer.setCommand(CtiDNPApplication::RequestRead);

            CtiDNPObjectBlock dob1(CtiDNPObjectBlock::NoIndex_NoRange),
                              dob2(CtiDNPObjectBlock::NoIndex_NoRange),
                              dob3(CtiDNPObjectBlock::NoIndex_NoRange);

            dob1.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class1));
            dob2.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class2));
            dob3.addObject(CTIDBG_new CtiDNPClass(CtiDNPClass::Class3));

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
                CtiDNPAnalogOutputBlock *aout = CTIDBG_new CtiDNPAnalogOutputBlock(CtiDNPAnalogOutputBlock::AOB16Bit);

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
        case DNP_SetDigitalOut_Direct:
        {
            if( numPoints == 1 && points[0].type == DigitalOutput )
            {
                _appLayer.setCommand(CtiDNPApplication::RequestDirectOp);

                CtiDNPObjectBlock dob(CtiDNPObjectBlock::ByteIndex_ByteQty);
                CtiDNPBinaryOutputControl *bout = CTIDBG_new CtiDNPBinaryOutputControl(CtiDNPBinaryOutputControl::ControlRelayOutputBlock);

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
        case DNP_SetDigitalOut_SBO_SelectOnly:
        case DNP_SetDigitalOut_SBO_Select:
        {
            if( numPoints == 1 && points[0].type == DigitalOutput )
            {
                _appLayer.setCommand(CtiDNPApplication::RequestSelect);

                CtiDNPObjectBlock dob(CtiDNPObjectBlock::ByteIndex_ByteQty);
                CtiDNPBinaryOutputControl *bout = CTIDBG_new CtiDNPBinaryOutputControl(CtiDNPBinaryOutputControl::ControlRelayOutputBlock);

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
        case DNP_SetDigitalOut_SBO_Operate:
        {
            if( numPoints == 1 && points[0].type == DigitalOutput )
            {
                _appLayer.setCommand(CtiDNPApplication::RequestOperate);

                CtiDNPObjectBlock dob(CtiDNPObjectBlock::ByteIndex_ByteQty);
                CtiDNPBinaryOutputControl *bout = CTIDBG_new CtiDNPBinaryOutputControl(CtiDNPBinaryOutputControl::ControlRelayOutputBlock);

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


CtiProtocolDNP::DNPCommand CtiProtocolDNP::getCommand( void )
{
    return _currentCommand;
}


bool CtiProtocolDNP::commandRequiresRequeueOnFail( void )
{
    bool retVal = false;

    switch( _currentCommand )
    {
/*        case DNP_SetDigitalOut_Direct:
        case DNP_SetDigitalOut_SBO_Select:
        case DNP_SetDigitalOut_SBO_Operate:
        {
            retVal = true;

            break;
        }
*/
        default:
        {
            break;
        }
    }

    return retVal;
}


int CtiProtocolDNP::commandRetries( void )
{
    int retVal;

    switch( _currentCommand )
    {
        case DNP_SetDigitalOut_Direct:
        case DNP_SetDigitalOut_SBO_Select:
        case DNP_SetDigitalOut_SBO_Operate:
        {
            retVal = 0;

            break;
        }

        default:
        {
            retVal = Retries_Default;

            break;
        }
    }

    return retVal;
}


int CtiProtocolDNP::generate( CtiXfer &xfer )
{
    return _appLayer.generate(xfer);
}


int CtiProtocolDNP::decode( CtiXfer &xfer, int status )
{
    int retVal;

    retVal = _appLayer.decode(xfer, status);

    return retVal;
}


int CtiProtocolDNP::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    //  FIX:  move this to Porter-side...  there's little need to have this step.
    //          do like the ION and just send a command structure...  decode the points on Porter-side, then populate
    //          a little array.  no biggie.
    if( _appLayer.getLengthReq() < sizeof( OutMessage->Buffer ) )
    {
        _appLayer.serializeReq(OutMessage->Buffer.OutMessage);
        OutMessage->OutLength    = _appLayer.getLengthReq();
        OutMessage->Source       = _masterAddress;
        OutMessage->Destination  = _slaveAddress;
        OutMessage->EventCode    = RESULT;
        OutMessage->MessageFlags = commandRequiresRequeueOnFail() ? MSGFLG_REQUEUE_CMD_ONCE_ON_FAIL : 0;
        OutMessage->Retry        = commandRetries();

        switch( _currentCommand )
        {
            case DNP_WriteTime:
                OutMessage->Priority = MAXPRIORITY - 1;
                break;

            case DNP_SetDigitalOut_Direct:
            case DNP_SetDigitalOut_SBO_Operate:
            case DNP_SetDigitalOut_SBO_Select:
            case DNP_SetDigitalOut_SBO_SelectOnly:
                OutMessage->Priority = MAXPRIORITY - 2;
                break;
        }

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - application layer > " << sizeof( OutMessage->Buffer )
                 << "; need to learn how to fragment application layer.  Abandoning outbound message. *****" << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        delete OutMessage;
        OutMessage = NULL;

        //  oh well, closest thing to reality - not enough room in outmess
        retVal = MemoryError;
    }

    if( OutMessage != NULL )
    {
        outList.append(OutMessage);
        OutMessage = NULL;
    }

    return retVal;
}


int CtiProtocolDNP::recvCommResult( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    if( InMessage != NULL )
    {
        _appLayer.restoreRsp(InMessage->Buffer.InMessage, InMessage->InLength);
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolDNP::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    _appLayer.resetLink();

    _appLayer.restoreReq(OutMessage->Buffer.OutMessage, OutMessage->OutLength);

    return retVal;
}


int CtiProtocolDNP::sendCommResult( INMESS *InMessage )
{
    int retVal = NoError;

    if( _appLayer.isReplyExpected() && !_appLayer.errorCondition() )
    {
        if( _appLayer.getLengthRsp() < sizeof(InMessage->Buffer) )
        {
            _appLayer.serializeRsp(InMessage->Buffer.InMessage);
            InMessage->InLength = _appLayer.getLengthRsp();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - _appLayer.getLengthRsp() (" << _appLayer.getLengthRsp() << " >= sizeof(InMessage->Buffer) (" << sizeof( InMessage->Buffer ) << ") , not sending **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


bool CtiProtocolDNP::hasControlResult( void ) const
{
    return _appLayer.isControlResult();
}

long CtiProtocolDNP::getControlResultOffset( void ) const
{
    return _appLayer.getControlResultOffset();
}

bool CtiProtocolDNP::getControlResultSuccess( void ) const
{
    return _appLayer.getControlResultStatus() == CtiDNPBinaryOutputControl::Status_RequestAccepted;
}

const char *CtiProtocolDNP::getControlResultString( void ) const
{
    const char *retVal;

    switch( _appLayer.getControlResultStatus() )
    {
        case CtiDNPBinaryOutputControl::Status_RequestAccepted:        retVal = ControlResultStr_RequestAccepted;        break;
        case CtiDNPBinaryOutputControl::Status_ArmTimeout:             retVal = ControlResultStr_ArmTimeout;             break;
        case CtiDNPBinaryOutputControl::Status_NoSelect:               retVal = ControlResultStr_NoSelect;               break;
        case CtiDNPBinaryOutputControl::Status_FormattingError:        retVal = ControlResultStr_FormattingError;        break;
        case CtiDNPBinaryOutputControl::Status_PointNotControllable:   retVal = ControlResultStr_PointNotControllable;   break;
        case CtiDNPBinaryOutputControl::Status_QueueFullPointActive:   retVal = ControlResultStr_QueueFullPointActive;   break;
        case CtiDNPBinaryOutputControl::Status_HardwareError:          retVal = ControlResultStr_HardwareError;          break;
        default:                                                       retVal = ControlResultStr_InvalidStatus;          break;
    }

    return retVal;
}


bool CtiProtocolDNP::hasTimeResult( void ) const
{
    return _appLayer.hasTimeResult();
}

unsigned long CtiProtocolDNP::getTimeResult( void ) const
{
    return _appLayer.getTimeResult();
}



const char const *CtiProtocolDNP::ControlResultStr_RequestAccepted      = "Request accepted";
const char const *CtiProtocolDNP::ControlResultStr_ArmTimeout           = "Operate received after select timeout";
const char const *CtiProtocolDNP::ControlResultStr_NoSelect             = "Operate without select";
const char const *CtiProtocolDNP::ControlResultStr_FormattingError      = "Format error(s) in control";
const char const *CtiProtocolDNP::ControlResultStr_PointNotControllable = "Control not supported on this point";
const char const *CtiProtocolDNP::ControlResultStr_QueueFullPointActive = "Queue full, or point already active";
const char const *CtiProtocolDNP::ControlResultStr_HardwareError        = "Control hardware problems";
const char const *CtiProtocolDNP::ControlResultStr_InvalidStatus        = "Unknown/undefined error";

