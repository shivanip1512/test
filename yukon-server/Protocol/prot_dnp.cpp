#include "precompiled.h"

#include "boost/scoped_ptr.hpp"

#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "ctidate.h"
#include "prot_dnp.h"
#include "dnp_object_class.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_analoginput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_counter.h"
#include "dnp_object_time.h"
#include "dnp_object_internalindications.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

using std::endl;
using std::string;

namespace Cti       {
namespace Protocol  {

using boost::scoped_ptr;

using namespace Cti::Protocol::DNP;

DNPInterface::DNPInterface() :
    _command(Command_Invalid),
    _last_complaint(0)
{
    setAddresses(DefaultSlaveAddress, DefaultMasterAddress);
}

DNPInterface::~DNPInterface()
{
    delete_container(_string_results);
    delete_container(_point_results);
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

    //  clear the point records out
    _analog_inputs.clear();
    _analog_outputs.clear();
    _binary_inputs.clear();
    _binary_outputs.clear();
    _counters.clear();

    return _command != Command_Invalid;
}


bool DNPInterface::setCommand( Command command, output_point &point )
{
    _command_parameters.clear();

    _command_parameters.push_back(point);

    return setCommand(command);
}

void DNPInterface::setConfigData( unsigned internalRetries, bool useLocalTime, bool enableDnpTimesyncs,
                                  bool omitTimeRequest, bool enableUnsolicitedClass1,
                                  bool enableUnsolicitedClass2, bool enableUnsolicitedClass3 )
{
    _config.reset(
       new DNP::config_data(
          internalRetries,
          useLocalTime,
          enableDnpTimesyncs,
          omitTimeRequest,
          enableUnsolicitedClass1,
          enableUnsolicitedClass2,
          enableUnsolicitedClass3));

    _app_layer.setConfigData(_config.get());
}

YukonError_t DNPInterface::generate( CtiXfer &xfer )
{
    if( _app_layer.isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_WriteTime:
            {
                DNP::Time *time_now = CTIDBG_new DNP::Time(Time::T_TimeAndDate);
                const CtiTime now = CtiTime::now();

                if( _config->useLocalTime )
                {
                    const unsigned utc_seconds = now.seconds();
                    const unsigned local_seconds = convertUtcSecondsToLocalSeconds(utc_seconds);
                    time_now->setSeconds(local_seconds);
                }
                else
                {
                    time_now->setSeconds(now.seconds());
                }

                _app_layer.setCommand(ApplicationLayer::RequestWrite);

                ObjectBlock *dob = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_ByteQty);

                dob->addObject(time_now);

                _app_layer.addObjectBlock(dob);

                break;
            }
            case Command_ReadTime:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                ObjectBlock *dob = CTIDBG_new ObjectBlock(ObjectBlock::NoIndex_NoRange, Time::Group, Time::T_TimeAndDate);

                _app_layer.addObjectBlock(dob);

                break;
            }
            case Command_Loopback:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                break;
            }
            case Command_UnsolicitedInbound:
            {
                //  special case

                break;
            }
            case Command_UnsolicitedEnable:
            {
                _app_layer.setCommand(ApplicationLayer::RequestEnableUnsolicited);

                if ( _config->enableUnsolicitedClass1 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                }
                if ( _config->enableUnsolicitedClass2 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                }
                if ( _config->enableUnsolicitedClass3 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));
                }

                break;
            }
            case Command_UnsolicitedDisable:
            {
                _app_layer.setCommand(ApplicationLayer::RequestDisableUnsolicited);

                if ( _config->enableUnsolicitedClass1 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                }
                if ( _config->enableUnsolicitedClass2 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                }
                if ( _config->enableUnsolicitedClass3 )
                {
                    _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));
                }

                break;
            }
            case Command_ResetDeviceRestartBit:
            {
                InternalIndications *restart = new InternalIndications(InternalIndications::II_InternalIndications);
                restart->setValue(false);

                ObjectBlock *iin = new ObjectBlock(ObjectBlock::NoIndex_ByteStartStop, restart->getGroup(), restart->getVariation());
                iin->addObjectRange(restart, 7, 7);

                _app_layer.setCommand(ApplicationLayer::RequestWrite);
                _app_layer.addObjectBlock(iin);

                break;
            }
            case Command_Class1230Read_WithTime:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Time::Group, Time::T_TimeAndDate));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class0));

                break;
            }
            case Command_Class1230Read:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class0));

                break;
            }
            case Command_Class123Read_WithTime:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Time::Group, Time::T_TimeAndDate));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));

                break;
            }
            case Command_Class123Read:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead);

                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class1));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class2));
                _app_layer.addObjectBlock(new ObjectBlock(ObjectBlock::NoIndex_NoRange, Class::Group, Class::Class3));

                break;
            }
            case Command_SetAnalogOut:
            {
                if( _command_parameters.size() == 1 &&
                   (_command_parameters[0].type == AnalogOutputPointType || _command_parameters[0].type == AnalogOutputFloatPointType ))
                {
                    _app_layer.setCommand(ApplicationLayer::RequestDirectOp);

                    ObjectBlock  *dob  = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                    AnalogOutput *aout = NULL;
                    if( _command_parameters[0].type == AnalogOutputPointType )
                    {
                        aout = CTIDBG_new AnalogOutput(AnalogOutput::AO_16Bit);
                    }
                    else
                    {
                        aout = CTIDBG_new AnalogOutput(AnalogOutput::AO_SingleFloat);
                    }

                    aout->setControl(_command_parameters[0].aout.value);

                    dob->addObjectIndex(aout, _command_parameters[0].control_offset);

                    _app_layer.addObjectBlock(dob);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - invalid control point in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                if( _command_parameters.size() == 1 && _command_parameters[0].type == DigitalOutputPointType )
                {
                    if( _command == Command_SetDigitalOut_Direct )
                    {
                        _app_layer.setCommand(ApplicationLayer::RequestDirectOp);
                    }
                    else if( _command == Command_SetDigitalOut_SBO_SelectOnly || _command == Command_SetDigitalOut_SBO_Select )
                    {
                        _app_layer.setCommand(ApplicationLayer::RequestSelect);
                    }
                    else if( _command == Command_SetDigitalOut_SBO_Operate )
                    {
                        _app_layer.setCommand(ApplicationLayer::RequestOperate);
                    }

                    ObjectBlock         *dob;
                    BinaryOutputControl *bout;
                    output_point &op = _command_parameters[0];

                    if(op.control_offset > 255 )
                    {
                        dob = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                    }
                    else
                    {
                        dob = CTIDBG_new ObjectBlock(ObjectBlock::ByteIndex_ByteQty);
                    }

                    bout = CTIDBG_new BinaryOutputControl(BinaryOutputControl::BOC_ControlRelayOutputBlock);
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
                        dout << CtiTime() << " **** Checkpoint - invalid control point in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    _command = Command_Invalid;
                }

                break;
            }
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - invalid command " << _command << " in DNPInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _command = Command_Invalid;
            }
        }

        if( _command == Command_UnsolicitedInbound )
        {
            _app_layer.initUnsolicited();
        }
        else
        {
            //  finalize the request
            _app_layer.initForOutput();
        }
    }

    return _app_layer.generate(xfer);
}


YukonError_t DNPInterface::decode( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retVal;
    bool final = true;

    retVal = _app_layer.decode(xfer, status);

    if( _app_layer.errorCondition() )
    {
        _string_results.push_back(CTIDBG_new string("Operation failed"));
        setCommand(Command_Complete);
    }
    else if( _app_layer.isTransactionComplete() )
    {
        _app_layer.getObjects(_object_blocks);

        //  does the command need any special processing, or is it just pointdata?
        switch( _command )
        {
            case Command_SetAnalogOut:
            {
                if( ! _object_blocks.empty() )
                {
                    if( const ObjectBlock *ob = _object_blocks.front() )
                    {
                        if( ! ob->empty() && ob->getGroup() == AnalogOutput::Group )
                        {
                            const ObjectBlock::object_descriptor od = ob->at(0);

                            if( od.object )
                            {
                                const AnalogOutput *ao = reinterpret_cast<const AnalogOutput *>(od.object);

                                std::ostringstream o;

                                o << "Analog output request ";

                                if( ao->getStatus() )
                                {
                                    retVal = Error_Abnormal;

                                    o << "unsuccessful\n";

                                    o << "(status = " << ao->getStatus() << ", offset = " << od.index << ", value " << ao->getValue() << ")";
                                }
                                else
                                {
                                    o << "successful\n";

                                    o << "(offset = " << od.index << ", value " << ao->getValue() << ")";
                                }

                                _string_results.push_back(new string(o.str()));

                                break;
                            }
                        }
                    }
                }

                retVal = Error_Abnormal;

                _string_results.push_back(new string("Analog output block not echoed"));

                break;
            }
            case Command_SetDigitalOut_Direct:
            case Command_SetDigitalOut_SBO_Select:
            case Command_SetDigitalOut_SBO_SelectOnly:
            case Command_SetDigitalOut_SBO_Operate:
            {
                const ObjectBlock *ob;

                if( !_object_blocks.empty() &&
                    (ob = _object_blocks.front()) &&
                    !ob->empty() &&
                    ob->getGroup()     == BinaryOutputControl::Group &&
                    ob->getVariation() == BinaryOutputControl::BOC_ControlRelayOutputBlock )
                {
                    ObjectBlock::object_descriptor od = ob->at(0);

                    if( od.object )
                    {
                        const BinaryOutputControl *boc = reinterpret_cast<const BinaryOutputControl *>(od.object);

                        //  if the select went successfully, transition to operate
                        if( _command == Command_SetDigitalOut_SBO_Select && boc->getStatus() == BinaryOutputControl::Status_Success )
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

                                    _string_results.push_back(CTIDBG_new string("Select successful, sending operate"));
                                }
                                else
                                {
                                    string str;

                                    str += "Select returned mismatched control offset (";
                                    str += CtiNumStr(od.index);
                                    str += " != ";
                                    str += CtiNumStr(op.control_offset);
                                    str += ")";

                                    _string_results.push_back(CTIDBG_new string(str));
                                    retVal = Error_Abnormal;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - empty command parameters for SBO operate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                _string_results.push_back(CTIDBG_new string("Empty command parameter list for operate"));
                                retVal = Error_Abnormal;
                            }
                        }

                        _string_results.push_back(CTIDBG_new string(getControlResultString(boc->getStatus())));
                    }
                    else
                    {
                        _string_results.push_back(CTIDBG_new string("Device did not return a control result"));
                        retVal = Error_Abnormal;
                    }
                }
                else
                {
                    _string_results.push_back(CTIDBG_new string("Device did not return a control result"));
                    retVal = Error_Abnormal;
                }

                break;
            }

            case Command_ReadTime:
            {
                //  make sure it's there - if not, yelp
                if( _object_blocks.empty()
                    || !_object_blocks.front()
                    || _object_blocks.front()->empty()
                    || _object_blocks.front()->getGroup()     != DNP::Time::Group
                    || _object_blocks.front()->getVariation() != DNP::Time::T_TimeAndDate
                    || !_object_blocks.front()->at(0).object )
                {
                    _string_results.push_back(CTIDBG_new string("Device did not return a time result"));
                    retVal = Error_Abnormal;
                }

                break;
            }

            case Command_WriteTime:
            {
                _string_results.push_back(CTIDBG_new string("Time sync sent"));

                break;
            }

            case Command_UnsolicitedEnable:
            {
                _string_results.push_back(CTIDBG_new string("Unsolicited reporting enabled"));

                break;
            }

            case Command_UnsolicitedDisable:
            {
                _string_results.push_back(CTIDBG_new string("Unsolicited reporting disabled"));

                break;
            }

            case Command_Loopback:
            {
                _string_results.push_back(CTIDBG_new string("Loopback successful"));

                break;
            }

            default:
            {
                break;
            }
        }

        scoped_ptr<TimeCTO> cto;
        scoped_ptr<Time>    time_sent;

        scoped_ptr<const ObjectBlock> ob;

        //  and this is where the pointdata gets harvested
        while( !_object_blocks.empty() )
        {
            ob.reset(_object_blocks.front());

            if( ob && !ob->empty() )
            {
                if( ob->getGroup() == TimeCTO::Group )
                {
                    cto.reset(CTIDBG_new TimeCTO(*(reinterpret_cast<const TimeCTO *>(ob->at(0).object))));

                    if( _config->useLocalTime )
                    {
                        const unsigned local_seconds = cto->getSeconds();
                        const unsigned utc_seconds = convertLocalSecondsToUtcSeconds(local_seconds);
                        cto->setSeconds(utc_seconds);
                    }

                    CtiTime t(cto->getSeconds());

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - Cti::Protocol::DNPInterface::decode() - found CTO object in stream for device \"" << _name << "\" (" << t << ", " << cto->getMilliseconds() << "ms) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else if( ob->getGroup()     == DNP::Time::Group &&
                         ob->getVariation() == DNP::Time::T_TimeAndDate )
                {
                    ObjectBlock::object_descriptor od = ob->at(0);

                    if( od.object )
                    {
                        time_sent.reset(CTIDBG_new Time(*(reinterpret_cast<const DNP::Time *>(od.object))));

                        if( _config->useLocalTime )
                        {
                            const unsigned local_seconds = time_sent->getSeconds();
                            const unsigned utc_seconds = convertLocalSecondsToUtcSeconds(local_seconds);
                            time_sent->setSeconds(utc_seconds);
                        }

                        string s;

                        CtiTime t(time_sent->getSeconds());
                        CtiTime now;

                        s = "Device time: ";
                        s.append(t.asString());
                        s.append(".");
                        s.append(CtiNumStr((int)time_sent->getMilliseconds()).zpad(3));

                        if( ((_last_complaint + ComplaintInterval) < now.seconds())
                            && ((t.seconds() - TimeDifferential) > now.seconds()
                                || (t.seconds() + TimeDifferential) < now.seconds()) )
                        {
                            _last_complaint = now.seconds();

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::DNPInterface::decode() - large time differential for device \"" << _name << "\" (" << t << "); will not complain again until " << CtiTime(_last_complaint + ComplaintInterval) << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        _string_results.push_back(CTIDBG_new string(s));
                    }
                    else
                    {
                        _string_results.push_back(CTIDBG_new string("Device did not return a time result"));
                        retVal = Error_Abnormal;
                    }
                }
                else
                {
                    pointlist_t points;
                    int count = ob->size();

                    ob->getPoints(points, cto.get(), time_sent.get());

                    recordPoints(ob->getGroup(), points);

                    _point_results.insert(_point_results.end(), points.begin(), points.end());
                }
            }

            _object_blocks.pop();
        }

        if( final )
        {
            if( _command == Command_UnsolicitedInbound )
            {
                delete_container(_string_results);

                _string_results.clear();
            }
            else
            {
                _string_results.push_back(CTIDBG_new string(_app_layer.getInternalIndications()));

                switch( _command )
                {
                    case Command_Class0Read:
                    case Command_Class1Read:
                    case Command_Class2Read:
                    case Command_Class3Read:
                    case Command_Class123Read:
                    case Command_Class123Read_WithTime:
                    case Command_Class1230Read:
                    case Command_Class1230Read_WithTime:
                    {
                        _string_results.push_back(CTIDBG_new string(pointSummary(5)));

                        break;
                    }
                }
            }

            if( _app_layer.needsTime() && _command != Command_WriteTime )
            {
                Command_deq_itr itr = std::find(_additional_commands.begin(),
                                                _additional_commands.end(),
                                                Command_WriteTime);

                if( itr == _additional_commands.end() )
                {
                    _additional_commands.insert(_additional_commands.begin(), Command_WriteTime);
                }
            }

            // Point value for the message regarding device restart.
            bool deviceRestarted = false;

            if( _app_layer.hasDeviceRestarted() )
            {
                deviceRestarted = true;

                if (_command != Command_ResetDeviceRestartBit )
                {
                    _string_results.push_back(new string("Attempting to clear Device Restart bit"));
                    setCommand(Command_ResetDeviceRestartBit);
                }

                if( _config->isAnyUnsolicitedEnabled() )
                {
                    Command_deq_itr itr = std::find(_additional_commands.begin(),
                                                    _additional_commands.end(),
                                                    Command_UnsolicitedEnable);

                    if( itr == _additional_commands.end() )
                    {
                        // Device restarted, so let's tell it to enabled unsolicited commands.
                        _additional_commands.push_back(Command_UnsolicitedEnable);
                    }
                }
            }
            else
            {
                if ( !_additional_commands.empty() )
                {
                    setCommand(_additional_commands.front());
                    _additional_commands.pop_front();
                }
                else
                {
                    setCommand(Command_Complete);
                }
            }

            // Add the point message for the restart bit.
            CtiPointDataMsg* pt_msg =
                new CtiPointDataMsg(IINStatusPointOffset_RestartBit,
                                    deviceRestarted,
                                    NormalQuality,
                                    StatusPointType);

            // We need to set up a millisecond time for the point message.
            SYSTEMTIME st;
            GetLocalTime(&st);

            pt_msg->setTimeWithMillis(
               CtiTime( CtiDate(st.wDay, st.wMonth, st.wYear), st.wHour, st.wMinute, st.wSecond ),
               st.wMilliseconds );

            _point_results.insert(_point_results.end(), pt_msg);
        }
    }

    return retVal;
}


void DNPInterface::recordPoints( int group, const pointlist_t &points )
{
    pointlist_t::const_iterator itr;

    unsigned count = points.size();

    //  Is it in the map already?
    if( _point_count.find(group) != _point_count.end() )
    {
        _point_count[group] += count;
    }
    else
    {
        _point_count[group] = count;
    }

    switch( group )
    {
        case AnalogInput::Group:
        case AnalogInputChange::Group:
        {
            for( itr = points.begin(); itr != points.end(); itr++ )
            {
                _analog_inputs[(*itr)->getId()] = (*itr)->getValue();
            }

            break;
        }

        case AnalogOutputStatus::Group:
        {
            for( itr = points.begin(); itr != points.end(); itr++ )
            {
                _analog_outputs[(*itr)->getId()] = (*itr)->getValue();
            }

            break;
        }

        case BinaryInput::Group:
        case BinaryInputChange::Group:
        {
            for( itr = points.begin(); itr != points.end(); itr++ )
            {
                _binary_inputs[(*itr)->getId()] = (*itr)->getValue();
            }

            break;
        }

        case BinaryOutput::Group:
        {
            for( itr = points.begin(); itr != points.end(); itr++ )
            {
                _binary_outputs[(*itr)->getId()] = (*itr)->getValue();
            }

            break;
        }

        case Counter::Group:
        case CounterFrozen::Group:
        case CounterEvent::Group:
        {
            for( itr = points.begin(); itr != points.end(); itr++ )
            {
                _counters[(*itr)->getId()] = (*itr)->getValue();
            }

            break;
        }
    }
}


string DNPInterface::pointSummary(unsigned points)
{
    string report;

    report += "Point data report:\n";
    report += "AI: " + CtiNumStr(_analog_inputs.size()).spad(5) + "; ";
    report += "AO: " + CtiNumStr(_analog_outputs.size()).spad(5) + "; ";
    report += "DI: " + CtiNumStr(_binary_inputs.size()).spad(5) + "; ";
    report += "DO: " + CtiNumStr(_binary_outputs.size()).spad(5) + "; ";
    report += "Counters: " + CtiNumStr(_counters.size()).spad(5) + "; ";
    report += "\n";

    if( _analog_inputs.empty()  && _analog_outputs.empty() &&
        _binary_inputs.empty()  && _binary_outputs.empty() &&
        _counters.empty() )
    {
        report += "(No points returned)\n";
    }
    else
    {
        report += "First/Last ";
        report += CtiNumStr(points);
        report += " points of each type returned:\n";

        if( !_analog_inputs.empty() )   report += "Analog inputs:\n"  + pointDataReport(_analog_inputs,  points);
        if( !_analog_outputs.empty() )  report += "Analog outputs:\n" + pointDataReport(_analog_outputs, points);

        if( !_binary_inputs.empty() )   report += "Binary inputs:\n"  + pointDataReport(_binary_inputs,  points);
        if( !_binary_outputs.empty() )  report += "Binary outputs:\n" + pointDataReport(_binary_outputs, points);

        if( !_counters.empty() )        report += "Counters:\n"       + pointDataReport(_counters, points);
    }

    return report;
}


string DNPInterface::pointDataReport(const std::map<unsigned, double> &pointdata, unsigned points)
{
    string report, item;

    std::map<unsigned, double>::const_iterator itr;
    std::map<unsigned, double>::const_reverse_iterator r_itr;

    report += "[";

    if( pointdata.size() <= (points * 2) )
    {
        for( itr = pointdata.begin(); itr != pointdata.end(); itr++ )
        {
            item = CtiNumStr(itr->first) + ":" + CtiNumStr(itr->second, 0);

            if( itr != pointdata.begin() )
            {
                report += ", ";
            }

            report += item;
        }
    }
    else
    {
        std::map<unsigned, double>::const_iterator itr_end;

        string first, second;
        int i;

        for( i = 0, itr = pointdata.begin(); i < points; itr++, i++ )
        {
            item  = CtiNumStr(itr->first);
            item += ":";
            item += CtiNumStr(itr->second, 0);
            item += ", ";

            first += item;
        }

        r_itr = pointdata.rbegin();

        for( i = 0; i < points; r_itr++, i++ )
        {
            item  = CtiNumStr(r_itr->first);
            item += ":";
            item += CtiNumStr(r_itr->second, 0);

            if( i )
            {
                item += ", ";
            }

            second = item + second;
        }

        report += first + " ... " + second;
    }

    report += "]\n";

    return report;
}


void DNPInterface::getInboundPoints( pointlist_t &points )
{
    points.insert(points.end(), _point_results.begin(), _point_results.end());

    _point_results.clear();
}


void DNPInterface::getInboundStrings( stringlist_t &strings )
{
    strings.insert(strings.end(), _string_results.begin(), _string_results.end());

    _string_results.clear();
}


bool DNPInterface::isTransactionComplete( void ) const
{
    //  ACH: factor in application layer retries... ?

    return _command == Command_Complete || _command == Command_Invalid;
}


const std::map<int, const char *> ControlResultStrings = boost::assign::map_list_of
        (BinaryOutputControl::Status_Success,           "Request accepted, initiated, or queued.")
        (BinaryOutputControl::Status_Timeout,           "Request not accepted because the operate message was received after the arm timer timed out. The arm timer was started when the select operation for the same point was received.")
        (BinaryOutputControl::Status_NoSelect,          "Request not accepted because no previous matching select request exists. (An operate message was sent to activate an output that was not previously armed with a matching select message.)")
        (BinaryOutputControl::Status_FormatError,       "Request not accepted because there were formatting errors in the control request.")
        (BinaryOutputControl::Status_NotSupported,      "Request not accepted because a control operation is not supported for this point.")
        (BinaryOutputControl::Status_AlreadyActive,     "Request not accepted, because the control queue is full or the point is already active.")
        (BinaryOutputControl::Status_HardwareError,     "Request not accepted because of control hardware problems.")
        (BinaryOutputControl::Status_Local,             "Request not accepted because Local/Remote switch is in Local position.")
        (BinaryOutputControl::Status_TooManyObjs,       "Request not accepted because too many objects appeared in the same request.")
        (BinaryOutputControl::Status_NotAuthorized,     "Request not accepted because of insufficient authorization.")
        (BinaryOutputControl::Status_AutomationInhibit, "Request not accepted because it was inhibited by a local automation process.")
        (BinaryOutputControl::Status_ProcessingLimited, "Request not accepted because the device cannot process any more activities than are presently in progress.")
        (BinaryOutputControl::Status_OutOfRange,        "Request not accepted because the value is outside the acceptable range permitted for this point.")
        (BinaryOutputControl::Status_NonParticipating,  "Outstation shall not issue or perform the control operation.")
        (BinaryOutputControl::Status_Undefined,         "Request not accepted because of some other undefined reason.")
        ;

const char *DNPInterface::getControlResultString( int result_status ) const
{
    const boost::optional<const char *> controlResultString = mapFind(ControlResultStrings, result_status);

    if( ! controlResultString )
    {
        return "Unknown error code (Reserved for future use.)";
    }

    return *controlResultString;
}


DNP::ApplicationLayer& DNPInterface::getApplicationLayer()
{
    return _app_layer;
}

DNPInterface::Command DNPInterface::getCommand()
{
    return _command;
}

void DNPInterface::addStringResults(string *s)
{
    _string_results.push_back(s);
    return;
}

unsigned DNPInterface::convertLocalSecondsToUtcSeconds( const unsigned seconds )
{
    //  this is CRAZY WIN32 SPECIFIC
    _TIME_ZONE_INFORMATION tzinfo;

    switch( GetTimeZoneInformation(&tzinfo) )
    {
        //  Bias is in minutes - subtract the difference to convert the local time to UTC
        case TIME_ZONE_ID_STANDARD:     return (seconds + (tzinfo.Bias + tzinfo.StandardBias) * 60);
        case TIME_ZONE_ID_DAYLIGHT:     return (seconds + (tzinfo.Bias + tzinfo.DaylightBias) * 60);

        case TIME_ZONE_ID_INVALID:
        case TIME_ZONE_ID_UNKNOWN:
        default:
            return seconds;
    }
}

unsigned DNPInterface::convertUtcSecondsToLocalSeconds( const unsigned seconds )
{
    //  this is CRAZY WIN32 SPECIFIC
    _TIME_ZONE_INFORMATION tzinfo;

    switch( GetTimeZoneInformation(&tzinfo) )
    {
        //  Bias is in minutes - add the difference to convert UTC to local time
        case TIME_ZONE_ID_STANDARD:     return (seconds - (tzinfo.Bias + tzinfo.StandardBias) * 60);
        case TIME_ZONE_ID_DAYLIGHT:     return (seconds - (tzinfo.Bias + tzinfo.DaylightBias) * 60);

        case TIME_ZONE_ID_INVALID:
        case TIME_ZONE_ID_UNKNOWN:
        default:
            return seconds;
    }
}

DNPSlaveInterface::DNPSlaveInterface()
{
   getApplicationLayer().completeSlave();
   setOptions(DNPSlaveInterface::Options_SlaveResponse);
}


YukonError_t DNPSlaveInterface::slaveDecode( CtiXfer &xfer )
{
    if( xfer.getOutBuffer()[10] & 0x80 )
    {
        slaveTransactionComplete();
        return NoError;
    }

    return getApplicationLayer().decode(xfer, NoError);
}

int DNPSlaveInterface::slaveGenerate( CtiXfer &xfer )
{
    if( getApplicationLayer().isTransactionNotStarted() )
    {
        switch( getCommand() )
        {
            case Command_Class1230Read:
            case Command_Class123Read:
            {
                getApplicationLayer().setCommand(ApplicationLayer::ResponseResponse);
                ObjectBlock         *dob1;
                ObjectBlock         *dob2;
                ObjectBlock         *dob3;

                DNP::AnalogInputChange *ainc;
                DNP::BinaryInputChange *binc;
                DNP::CounterEvent *counterevent;
                DNP::AnalogInput *ain;
                DNP::BinaryInput *bin;
                DNP::Counter *counterin;

                dob1 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                dob2 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                dob3 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);

                for ( int i = 0; i < _input_point_list.size(); i++ )
                {
                    input_point &ip =  _input_point_list[i];

                    if( ip.type == AnalogInputType )
                    {
                        if( ip.includeTime)
                        {
                            ainc = CTIDBG_new DNP::AnalogInputChange( DNP::AnalogInputChange::AIC_32BitWithTime);
                            ainc->setTime(ip.timestamp);
                            ainc->setValue(ip.ain.value);
                            ainc->setOnlineFlag(ip.online);
                            dob1->addObjectIndex(ainc, ip.control_offset);
                        }
                        else
                        {
                            ain = CTIDBG_new DNP::AnalogInput( DNP::AnalogInput::AI_32Bit );
                            ain->setValue(ip.ain.value);
                            ain->setOnlineFlag(ip.online);
                            dob1->addObjectIndex(ain, ip.control_offset);
                        }
                    }
                    else if( ip.type == DigitalInput )
                    {
                        if( ip.includeTime)
                        {
                            binc = CTIDBG_new DNP::BinaryInputChange( DNP::BinaryInputChange::BIC_WithTime);
                            binc->setTime(ip.timestamp);
                            binc->setStateValue(ip.din.trip_close);
                            binc->setOnlineFlag(ip.online);
                            dob2->addObjectIndex(binc, ip.control_offset);
                        }
                        else
                        {
                            bin = CTIDBG_new DNP::BinaryInput( DNP::BinaryInput::BI_WithStatus);
                            bin->setStateValue(ip.din.trip_close);
                            bin->setOnlineFlag(ip.online);
                            dob2->addObjectIndex(bin, ip.control_offset);
                        }
                    }
                    else if( ip.type == Counters )
                    {
                        if( ip.includeTime)
                        {
                            counterevent = CTIDBG_new DNP::CounterEvent( DNP::CounterEvent::CE_Delta32BitWithTime);
                            counterevent->setTime(ip.timestamp);
                            counterevent->setValue(ip.counterin.value);
                            counterevent->setOnlineFlag(ip.online);
                            dob3->addObjectIndex(counterevent, ip.control_offset);
                        }
                        else
                        {
                            counterin = CTIDBG_new DNP::Counter( DNP::Counter::C_Binary32Bit );
                            counterin->setValue(ip.counterin.value);
                            counterin->setOnlineFlag(ip.online);
                            dob3->addObjectIndex(counterin, ip.control_offset);
                        }
                    }
                }
                addObjectBlock(dob1);
                addObjectBlock(dob2);
                addObjectBlock(dob3);

                break;
            }
            case Command_UnsolicitedInbound:
            case Command_WriteTime:
            case Command_ReadTime:
            case Command_Loopback:
            case Command_UnsolicitedEnable:
            case Command_UnsolicitedDisable:
            case Command_SetAnalogOut:
            case Command_SetDigitalOut_Direct:
            case Command_SetDigitalOut_SBO_SelectOnly:
            case Command_SetDigitalOut_SBO_Select:
            case Command_SetDigitalOut_SBO_Operate:
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - invalid command " << getCommand() << " in DNPSlaveInterface::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                setSlaveCommand(Command_Invalid);
            }
        }

        //  finalize the request
        getApplicationLayer().initForSlaveOutput();

    }

    int retVal = getApplicationLayer().generate(xfer);
    if( retVal )
    {
        slaveTransactionComplete();
    }
    return retVal;
}

void DNPSlaveInterface::slaveTransactionComplete()
{
    if( getApplicationLayer().errorCondition() )
    {
        addStringResults(CTIDBG_new string("Operation failed"));
    }
    setSlaveCommand(Command_Complete);

    return;
}

void DNPSlaveInterface::addInputPoint(const input_point &ip)
{
    _input_point_list.push_back(ip);
    return;
}

bool DNPSlaveInterface::setSlaveCommand( Command command )
{
    setCommand(command);

    if( getCommand() == Command_Complete)
    {
        _input_point_list.clear();
        getApplicationLayer().completeSlave();
    }
    return getCommand() != Command_Invalid;
}

void DNPSlaveInterface::addObjectBlock(DNP::ObjectBlock *objBlock)
{
    if( !objBlock->empty() )
    {
        getApplicationLayer().addObjectBlock(objBlock);
    }
    else
    {
        delete objBlock;
    }
}
void DNPSlaveInterface::setOptions( int options, int seqNumber )
{
    Inherited::setOptions(options);
    getApplicationLayer().setSequenceNumber(seqNumber);
}

}
}

