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
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2005/03/10 20:59:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_dnp.h"
#include "dnp_object_class.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_time.h"

namespace Cti       {
namespace Protocol  {

using namespace Cti::Protocol::DNP;

DNPInterface::DNPInterface() :
    _io_state(IOState_Uninitialized)
{
    setAddresses(DefaultSlaveAddress, DefaultMasterAddress);
}

DNPInterface::DNPInterface(const DNPInterface &aRef)
{
    *this = aRef;
}

DNPInterface::~DNPInterface()   {}

DNPInterface &DNPInterface::operator=(const DNPInterface &aRef)
{
    if( this != &aRef )
    {
        _app_layer     = aRef._app_layer;
        _masterAddress = aRef._masterAddress;
        _slaveAddress  = aRef._slaveAddress;
    }

    return *this;
}


void DNPInterface::initLayers( void )
{
    //_app_layer.setOptions(_options);
}


void DNPInterface::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _app_layer.setAddresses(_slaveAddress, _masterAddress);
}


void DNPInterface::setOptions( int options )
{
    _options = options;

    _app_layer.setOptions(options);
}


bool DNPInterface::setCommand( Command command )
{
    _command = command;

    return _command != Command_Invalid;
}


bool DNPInterface::setCommand( Command command, output_point &point )
{
    _command_parameters.clear();

    _command_parameters.push_back(point);

    return setCommand(command);
}

/*
bool DNPInterface::setCommand( Command command, vector<output_point> &point_vector )
{
    _command_parameters.clear();

    _command_parameters.insert(_command_parameters.end(),
                               point_vector.begin(),
                               point_vector.end());

    return setCommand(command);
}
*/

bool DNPInterface::commandRequiresRequeueOnFail( void )
{
    bool retVal = false;
/*
    switch( _command )
    {
        case Command_SetDigitalOut_Direct:
        case Command_SetDigitalOut_SBO_Select:
        case Command_SetDigitalOut_SBO_Operate:
        {
            retVal = true;

            break;
        }

        default:
        {
            break;
        }
    }
*/
    return retVal;
}


int DNPInterface::commandRetries( void )
{
    int retVal;

    switch( _command )
    {
        case Command_SetDigitalOut_Direct:
        case Command_SetDigitalOut_SBO_Select:
        case Command_SetDigitalOut_SBO_Operate:
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


int DNPInterface::generate( CtiXfer &xfer )
{
    if( _app_layer.isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_WriteTime:
            {
                DNP::Time *time_now = CTIDBG_new DNP::Time(Time::TimeAndDate);
                RWTime Now;

                time_now->setSeconds(Now.seconds() - rwEpoch);

                _app_layer.setCommand(Application::RequestWrite);

                ObjectBlock *dob = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_ByteQty);

                dob->addObject(time_now);

                _app_layer.addObjectBlock(dob);

                break;
            }
            case Command_ReadTime:
            {
                _app_layer.setCommand(Application::RequestRead);

                ObjectBlock *dob = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Time::Group, Time::TimeAndDate);

                _app_layer.addObjectBlock(dob);

                break;
            }
            case Command_Class0Read:
            {
                _app_layer.setCommand(Application::RequestRead);

                ObjectBlock *dob = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange);

                dob->addObject(CTIDBG_new Class(Class::Class0));

                _app_layer.addObjectBlock(dob);

                break;
            }
            case Command_Class1230Read:
            {
                _app_layer.setCommand(Application::RequestRead);

                ObjectBlock *dob1 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1),
                            *dob2 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2),
                            *dob3 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3),
                            *dob0 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class0);

                _app_layer.addObjectBlock(dob1);
                _app_layer.addObjectBlock(dob2);
                _app_layer.addObjectBlock(dob3);
                _app_layer.addObjectBlock(dob0);

                break;
            }
            case Command_Class123Read:
            {
                _app_layer.setCommand(Application::RequestRead);

                ObjectBlock *dob1 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1),
                            *dob2 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2),
                            *dob3 = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3);

                _app_layer.addObjectBlock(dob1);
                _app_layer.addObjectBlock(dob2);
                _app_layer.addObjectBlock(dob3);

                break;
            }
            case Command_SetAnalogOut:
            {
                if( _command_parameters.size() == 1 && _command_parameters[0].type == AnalogOutput )
                {
                    _app_layer.setCommand(Application::RequestDirectOp);

                    ObjectBlock       *dob  = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                    AnalogOutputBlock *aout = CTIDBG_new AnalogOutputBlock(AnalogOutputBlock::AOB16Bit);

                    aout->setControl(_command_parameters[0].aout.value);

                    dob->addObjectIndex(aout, _command_parameters[0].control_offset);

                    _app_layer.addObjectBlock(dob);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - invalid control point in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    _command = Command_Invalid;
                }

                break;
            }
            case Command_SetDigitalOut_Direct:
            case Command_SetDigitalOut_SBO_SelectOnly:
            case Command_SetDigitalOut_SBO_Select:
            case Command_SetDigitalOut_SBO_Operate:
            {
                if( _command_parameters.size() == 1 && _command_parameters[0].type == DigitalOutput )
                {
                    if( _command == Command_SetDigitalOut_Direct )
                    {
                        _app_layer.setCommand(Application::RequestDirectOp);
                    }
                    else if( _command == Command_SetDigitalOut_SBO_SelectOnly || _command == Command_SetDigitalOut_SBO_Select )
                    {
                        _app_layer.setCommand(Application::RequestSelect);
                    }
                    else if( _command == Command_SetDigitalOut_SBO_Operate )
                    {
                        _app_layer.setCommand(Application::RequestOperate);
                    }

                    ObjectBlock         *dob  = CTIDBG_new ObjectBlock(ObjectBlock::ByteIndex_ByteQty);
                    BinaryOutputControl *bout = CTIDBG_new BinaryOutputControl(BinaryOutputControl::ControlRelayOutputBlock);
                    output_point &op = _command_parameters[0];

                    bout->setControlBlock(op.dout.on_time,
                                          op.dout.off_time,
                                          op.dout.count,
                                          op.dout.control,
                                          op.dout.queue,
                                          op.dout.clear,
                                          op.dout.trip_close);

                    dob->addObjectIndex(bout, op.control_offset);

                    _app_layer.addObjectBlock(dob);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - invalid control point in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    _command = Command_Invalid;
                }

                break;
            }
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - invalid command " << _command << " in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _command = Command_Invalid;
            }
        }

        //  finalize the request
        _app_layer.initForOutput();
    }

    return _app_layer.generate(xfer);
}


int DNPInterface::decode( CtiXfer &xfer, int status )
{
    int  retVal;
    bool final = true;

    retVal = _app_layer.decode(xfer, status);

    if( _app_layer.isTransactionComplete() )
    {
        _app_layer.getObjects(_object_blocks);

        //  this block is for commands that return strings instead of pointdata messages.  we must decode and prepare
        //    these here so they don't get misused and abused by the getInboundPoints function
        if( !_object_blocks.empty() )
        {
            switch( _command )
            {
                case Command_SetDigitalOut_Direct:
                case Command_SetDigitalOut_SBO_Select:
                case Command_SetDigitalOut_SBO_SelectOnly:
                case Command_SetDigitalOut_SBO_Operate:
                {
                    const ObjectBlock *ob = _object_blocks.front();

                    if( ob->getGroup()     == BinaryOutputControl::Group &&
                        ob->getVariation() == BinaryOutputControl::ControlRelayOutputBlock )
                    {
                        ObjectBlock::object_descriptor od = ob->at(0);

                        if( od.object )
                        {
                            const BinaryOutputControl *boc = reinterpret_cast<const BinaryOutputControl *>(od.object);

                            //  if the select went successfully, transition to operate
                            if( _command == Command_SetDigitalOut_SBO_Select &&
                                boc->getStatus() == BinaryOutputControl::Status_RequestAccepted )
                            {
                                if( !_command_parameters.empty() )
                                {
                                    //  make a copy because _command_parameters will be cleared out when setCommand is called...  freaky deaky
                                    output_point op = _command_parameters.at(0);

                                    if( od.index == op.control_offset )
                                    {
                                        //  transition to the operate phase
                                        setCommand(Command_SetDigitalOut_SBO_Operate, op);
                                        final = false;

                                        _results.push(CTIDBG_new string("Select successful, sending operate"));
                                    }
                                    else
                                    {
                                        string str;

                                        str += "Select returned mismatched control offset (";
                                        str += CtiNumStr(od.index);
                                        str += " != ";
                                        str += CtiNumStr(op.control_offset);
                                        str += ")";

                                        _results.push(CTIDBG_new string(str));
                                    }
                                }
                            }

                            _results.push(CTIDBG_new string(getControlResultString(boc->getStatus())));
                        }
                    }

                    break;
                }

                case Command_ReadTime:
                {
                    const ObjectBlock *ob = _object_blocks.front();

                    if( ob->getGroup()     == DNP::Time::Group &&
                        ob->getVariation() == DNP::Time::TimeAndDate )
                    {
                        ObjectBlock::object_descriptor od = ob->at(0);
                        string s = "Empty time result block";

                        if( od.object )
                        {
                            const DNP::Time *time = reinterpret_cast<const DNP::Time *>(od.object);

                            //  change to ptime
                            RWTime t(time->getSeconds() + rwEpoch);

                            s = "Device time: ";
                            s.append(t.asString());

                        }

                        _results.push(CTIDBG_new string(s));
                    }

                    break;
                }

                case Command_WriteTime:
                {
                    if( !_app_layer.errorCondition() )
                    {
                        _results.push(CTIDBG_new string("Time sync sent"));
                    }
                }

                /*default:
                {
                    break;
                }*/
            }
        }

        if( final )
        {
            //  set final = false in the above switch statement if you want to do anything crazy (like SBO)
            setCommand(Command_Complete);
        }
    }

    return retVal;
}


void DNPInterface::getInboundPoints( queue< CtiPointDataMsg * > &points )
{
    const TimeCTO *cto = 0;

    while( !_object_blocks.empty() )
    {
        const ObjectBlock *ob = _object_blocks.front();

        if( ob->getGroup() == TimeCTO::Group && !ob->empty() )
        {
            cto = reinterpret_cast<const TimeCTO *>(ob->at(0).object);
        }
        else
        {
            ob->getPoints(points, cto);
        }

        _object_blocks.pop();
        delete ob;
    }
}


void DNPInterface::getInboundStrings( queue< string * > &strings )
{
    while( !_results.empty() )
    {
        strings.push(_results.front());

        _results.pop();
    }
}


bool DNPInterface::isTransactionComplete( void )
{
    //  ACH: factor in application layer retries... ?

    return _command == Command_Complete;
}


const char *DNPInterface::getControlResultString( int result_status ) const
{
    const char *retVal;

    switch( result_status )
    {
        case BinaryOutputControl::Status_RequestAccepted:        retVal = ControlResultStr_RequestAccepted;        break;
        case BinaryOutputControl::Status_ArmTimeout:             retVal = ControlResultStr_ArmTimeout;             break;
        case BinaryOutputControl::Status_NoSelect:               retVal = ControlResultStr_NoSelect;               break;
        case BinaryOutputControl::Status_FormattingError:        retVal = ControlResultStr_FormattingError;        break;
        case BinaryOutputControl::Status_PointNotControllable:   retVal = ControlResultStr_PointNotControllable;   break;
        case BinaryOutputControl::Status_QueueFullPointActive:   retVal = ControlResultStr_QueueFullPointActive;   break;
        case BinaryOutputControl::Status_HardwareError:          retVal = ControlResultStr_HardwareError;          break;
        default:                                                 retVal = ControlResultStr_InvalidStatus;          break;
    }

    return retVal;
}



const char const *DNPInterface::ControlResultStr_RequestAccepted      = "Request accepted";
const char const *DNPInterface::ControlResultStr_ArmTimeout           = "Operate received after select timeout";
const char const *DNPInterface::ControlResultStr_NoSelect             = "Operate without select";
const char const *DNPInterface::ControlResultStr_FormattingError      = "Format error(s) in control";
const char const *DNPInterface::ControlResultStr_PointNotControllable = "Control not supported on this point";
const char const *DNPInterface::ControlResultStr_QueueFullPointActive = "Queue full, or point already active";
const char const *DNPInterface::ControlResultStr_HardwareError        = "Control hardware problems";
const char const *DNPInterface::ControlResultStr_InvalidStatus        = "Unknown/undefined error";

}
}

