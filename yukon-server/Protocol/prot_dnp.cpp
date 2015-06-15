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
#include <boost/range/algorithm/find.hpp>

using std::endl;
using std::string;

namespace Cti {
namespace Protocols {

using boost::scoped_ptr;

using namespace DNP;

DnpProtocol::DnpProtocol() :
    _command(Command_Invalid)
{
    const int DefaultMasterAddress = 5;
    const int DefaultSlaveAddress  = 1;

    setAddresses(DefaultSlaveAddress, DefaultMasterAddress);
}

DnpProtocol::~DnpProtocol()
{
    delete_container(_point_results);
}

void DnpProtocol::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _datalink.setAddresses(slaveAddress, masterAddress);
}


void DnpProtocol::setDatalinkConfirm()
{
    _datalink.setDatalinkConfirm();
}


bool DnpProtocol::setCommand( Command command )
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


bool DnpProtocol::setCommand( Command command, output_point &point )
{
    _command_parameters.clear();

    _command_parameters.push_back(point);

    return setCommand(command);
}

void DnpProtocol::setConfigData( unsigned internalRetries, bool useLocalTime, bool enableDnpTimesyncs,
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

YukonError_t DnpProtocol::generate( CtiXfer &xfer )
{
    if( _app_layer.isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_WriteTime:
            {
                auto time_now = std::make_unique<DNP::Time>(Time::T_TimeAndDate);
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

                _app_layer.setCommand(ApplicationLayer::RequestWrite, ObjectBlock::makeQuantityBlock(std::move(time_now)));

                break;
            }
            case Command_ReadTime:
            {
                _app_layer.setCommand(ApplicationLayer::RequestRead, ObjectBlock::makeNoIndexNoRange(Time::Group, Time::T_TimeAndDate));

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
                std::vector<ObjectBlockPtr> dobs;

                if ( _config->enableUnsolicitedClass1 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                }
                if ( _config->enableUnsolicitedClass2 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                }
                if ( _config->enableUnsolicitedClass3 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));
                }

                _app_layer.setCommand(ApplicationLayer::RequestEnableUnsolicited, std::move(dobs));

                break;
            }
            case Command_UnsolicitedDisable:
            {
                std::vector<ObjectBlockPtr> dobs;

                if ( _config->enableUnsolicitedClass1 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                }
                if ( _config->enableUnsolicitedClass2 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                }
                if ( _config->enableUnsolicitedClass3 )
                {
                    dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));
                }

                _app_layer.setCommand(ApplicationLayer::RequestDisableUnsolicited, std::move(dobs));

                break;
            }
            case Command_ResetDeviceRestartBit:
            {
                auto restart = std::make_unique<InternalIndications>(InternalIndications::II_InternalIndications);
                restart->setValue(false);

                _app_layer.setCommand(ApplicationLayer::RequestWrite, ObjectBlock::makeRangedBlock(std::move(restart), 7));

                break;
            }
            case Command_Class1230Read_WithTime:
            {
                std::vector<ObjectBlockPtr> dobs;

                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Time::Group, Time::T_TimeAndDate));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class0));

                _app_layer.setCommand(ApplicationLayer::RequestRead, std::move(dobs));

                break;
            }
            case Command_Class1230Read:
            {
                std::vector<ObjectBlockPtr> dobs;

                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class0));

                _app_layer.setCommand(ApplicationLayer::RequestRead, std::move(dobs));

                break;
            }
            case Command_Class123Read_WithTime:
            {
                std::vector<ObjectBlockPtr> dobs;

                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Time::Group, Time::T_TimeAndDate));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));

                _app_layer.setCommand(ApplicationLayer::RequestRead, std::move(dobs));

                break;
            }
            case Command_Class123Read:
            {
                std::vector<ObjectBlockPtr> dobs;

                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class1));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class2));
                dobs.push_back(ObjectBlock::makeNoIndexNoRange(Class::Group, Class::Class3));

                _app_layer.setCommand(ApplicationLayer::RequestRead, std::move(dobs));

                break;
            }
            case Command_SetAnalogOut:
            {
                if( _command_parameters.size() == 1 &&
                   (_command_parameters[0].type == AnalogOutputPointType || _command_parameters[0].type == AnalogOutputFloatPointType ))
                {
                    auto aout =
                            std::make_unique<AnalogOutput>(
                                    _command_parameters[0].type == AnalogOutputPointType
                                        ? AnalogOutput::AO_16Bit
                                        : AnalogOutput::AO_SingleFloat);

                    aout->setControl(_command_parameters[0].aout.value);

                    _app_layer.setCommand(
                            ApplicationLayer::RequestDirectOp,
                            ObjectBlock::makeLongIndexedBlock(std::move(aout), _command_parameters[0].control_offset));
                }
                else
                {
                    CTILOG_ERROR(dout, "invalid control point");

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
                    output_point &op = _command_parameters[0];

                    auto bout = std::make_unique<BinaryOutputControl>(BinaryOutputControl::BOC_ControlRelayOutputBlock);

                    bout->setControlBlock(op.dout.on_time,
                                          op.dout.off_time,
                                          op.dout.count,
                                          op.dout.control,
                                          op.dout.queue,
                                          op.dout.clear,
                                          op.dout.trip_close);

                    ObjectBlockPtr dob = ObjectBlock::makeIndexedBlock(std::move(bout), op.control_offset);

                    switch( _command )
                    {
                        case Command_SetDigitalOut_Direct:
                        {
                            _app_layer.setCommand(ApplicationLayer::RequestDirectOp, std::move(dob));
                            break;
                        }
                        case Command_SetDigitalOut_SBO_SelectOnly:
                        case Command_SetDigitalOut_SBO_Select:
                        {
                            _app_layer.setCommand(ApplicationLayer::RequestSelect, std::move(dob));
                            break;
                        }
                        case Command_SetDigitalOut_SBO_Operate:
                        {
                            _app_layer.setCommand(ApplicationLayer::RequestOperate, std::move(dob));
                            break;
                        }
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "invalid control point");

                    _command = Command_Invalid;
                }

                break;
            }
            default:
            {
                CTILOG_ERROR(dout, "invalid command "<< _command);

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

    YukonError_t retVal = ClientErrors::None;

    if( _transport.isTransactionComplete() )
    {
        retVal = _app_layer.generate(_transport);
    }

    if( ! retVal )
    {
        if( _datalink.isTransactionComplete() )
        {
            retVal = _transport.generate(_datalink);
        }
    }

    if( !retVal )
    {
        retVal = _datalink.generate(xfer);
    }

    return retVal;
}


YukonError_t DnpProtocol::decode( CtiXfer &xfer, YukonError_t status )
{
    bool final = true;

    YukonError_t retVal = _datalink.decode(xfer, status);

    if( ! _datalink.isTransactionComplete() )
    {
        return retVal;
    }

    retVal = _transport.decode(_datalink);

    if( ! _transport.isTransactionComplete() )
    {
        return retVal;
    }

    retVal = _app_layer.decode(_transport);

    if( ! _app_layer.isTransactionComplete() )
    {
        return retVal;
    }

    if( _app_layer.errorCondition() )
    {
        _string_results.push_back("Operation failed");
        setCommand(Command_Complete);

        return retVal;
    }

    _app_layer.getObjects(_object_blocks);

    //  does the command need any special processing, or is it just pointdata?
    switch( _command )
    {
        case Command_SetAnalogOut:
        {
            if( ! _object_blocks.empty() )
            {
                if( const auto &ob = _object_blocks.front() )
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
                                retVal = ClientErrors::Abnormal;

                                o << "unsuccessful\n";

                                o << "(status = " << ao->getStatus() << ", offset = " << od.index << ", value " << ao->getValue() << ")";
                            }
                            else
                            {
                                o << "successful\n";

                                o << "(offset = " << od.index << ", value " << ao->getValue() << ")";
                            }

                            _string_results.push_back(o.str());

                            break;
                        }
                    }
                }
            }

            retVal = ClientErrors::Abnormal;

            _string_results.push_back("Analog output block not echoed");

            break;
        }
        case Command_SetDigitalOut_Direct:
        case Command_SetDigitalOut_SBO_Select:
        case Command_SetDigitalOut_SBO_SelectOnly:
        case Command_SetDigitalOut_SBO_Operate:
        {
            ObjectBlock::object_descriptor od = { nullptr, 0 };

            if( !_object_blocks.empty() )
            {
                if( const auto &ob = _object_blocks.front() )
                {
                    if( ! ob->empty()
                        && ob->getGroup()     == BinaryOutputControl::Group
                        && ob->getVariation() == BinaryOutputControl::BOC_ControlRelayOutputBlock )
                    {
                        od = ob->at(0);
                    }
                }
            }

            if( od.object )
            {
                const BinaryOutputControl *boc = reinterpret_cast<const BinaryOutputControl *>(od.object);

                //  if the select went successfully, transition to operate
                if( _command == Command_SetDigitalOut_SBO_Select && boc->getStatus() == BinaryOutputControl::Status_Success )
                {
                    if( !_command_parameters.empty() )
                    {
                        //  make a copy because _command_parameters will be cleared out when setCommand is called...  freaky deaky
                        output_point op = _command_parameters[0];

                        if( od.index == op.control_offset )
                        {
                            //  transition to the operate phase
                            setCommand(Command_SetDigitalOut_SBO_Operate, op);
                            final = false;

                            _string_results.push_back("Select successful, sending operate");
                        }
                        else
                        {
                            string str;

                            str += "Select returned mismatched control offset (";
                            str += CtiNumStr(od.index);
                            str += " != ";
                            str += CtiNumStr(op.control_offset);
                            str += ")";

                            _string_results.push_back(str);
                            retVal = ClientErrors::Abnormal;
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "empty command parameters for SBO operate");

                        _string_results.push_back("Empty command parameter list for operate");
                        retVal = ClientErrors::Abnormal;
                    }
                }

                _string_results.push_back(getControlResultString(boc->getStatus()));
            }
            else
            {
                _string_results.push_back("Device did not return a control result");
                retVal = ClientErrors::Abnormal;
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
                _string_results.push_back("Device did not return a time result");
                retVal = ClientErrors::Abnormal;
            }

            break;
        }

        case Command_WriteTime:
        {
            _string_results.push_back("Time sync sent");

            break;
        }

        case Command_UnsolicitedEnable:
        {
            _string_results.push_back("Unsolicited reporting enabled");

            break;
        }

        case Command_UnsolicitedDisable:
        {
            _string_results.push_back("Unsolicited reporting disabled");

            break;
        }

        case Command_Loopback:
        {
            _string_results.push_back("Loopback successful");

            break;
        }

        default:
        {
            break;
        }
    }

    scoped_ptr<TimeCTO> cto;
    scoped_ptr<Time>    time_sent;

    //  and this is where the pointdata gets harvested
    while( !_object_blocks.empty() )
    {
        const auto &ob = _object_blocks.front();

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

                CTILOG_WARN(dout, "found CTO object in stream for device \""<< _name <<"\" ("<< t <<", "<< cto->getMilliseconds() <<"ms)");
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

                    const int TimeDifferential  =   60;
                    const int ComplaintInterval = 3600;

                    if( _nextTimeComplaint <= now
                        && ((t - TimeDifferential) > now || (t + TimeDifferential) < now) )
                    {
                        _nextTimeComplaint = nextScheduledTimeAlignedOnRate(now, ComplaintInterval);

                        CTILOG_WARN(dout, "large time differential for device \""<< _name <<"\" ("<< t <<"); "
                                "will not complain again until "<< _nextTimeComplaint);
                    }

                    _string_results.push_back(s);
                }
                else
                {
                    _string_results.push_back("Device did not return a time result");
                    retVal = ClientErrors::Abnormal;
                }
            }
            else
            {
                pointlist_t points;
                int count = ob->size();

                ob->getPoints(points, cto.get(), time_sent.get());

                recordPoints(ob->getGroup(), points);

                for( CtiPointDataMsg *p : points )
                {
                    p->setId(p->getId() + 1);  //  convert to Yukon's 1-based offset
                    _point_results.push_back(p);
                }
            }
        }

        _object_blocks.pop();
    }

    if( final )
    {
        if( _command == Command_UnsolicitedInbound )
        {
            _string_results.clear();
        }
        else
        {
            _string_results.push_back(_app_layer.getInternalIndications());

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
                    _string_results.push_back(pointSummary(5));

                    break;
                }
            }
        }

        if( _app_layer.needsTime() && _command != Command_WriteTime )
        {
            auto itr = boost::range::find(_additional_commands, Command_WriteTime);

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
                _string_results.push_back("Attempting to clear Device Restart bit");
                setCommand(Command_ResetDeviceRestartBit);
            }

            if( _config->isAnyUnsolicitedEnabled() )
            {
                auto itr = boost::range::find(_additional_commands, Command_UnsolicitedEnable);

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

        const int IINStatusPointOffset_RestartBit = 2001;

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

        _point_results.push_back(pt_msg);
    }

    return retVal;
}


void DnpProtocol::recordPoints( int group, const pointlist_t &points )
{
    pointtype_values *target = 0;

    switch( group )
    {
        case AnalogInput::Group:
        case AnalogInputChange::Group:  target = &_analog_inputs;   break;

        case AnalogOutputStatus::Group: target = &_analog_outputs;  break;

        case BinaryInput::Group:
        case BinaryInputChange::Group:  target = &_binary_inputs;   break;

        case BinaryOutput::Group:       target = &_binary_outputs;  break;

        case Counter::Group:
        case CounterFrozen::Group:
        case CounterEvent::Group:       target = &_counters;        break;

        default:
        {
            return;
        }
    }

    for each( const CtiPointDataMsg *msg in points )
    {
        if( msg )
        {
            (*target)[msg->getId() + 1] = msg->getValue();  //  convert to 1-based offset for display
        }
    }
}


string DnpProtocol::pointSummary(unsigned points)
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


string DnpProtocol::pointDataReport(const std::map<unsigned, double> &pointdata, unsigned points)
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


void DnpProtocol::getInboundPoints( pointlist_t &points )
{
    points.insert(points.end(), _point_results.begin(), _point_results.end());

    _point_results.clear();
}


auto DnpProtocol::getInboundStrings() -> stringlist_t
{
    stringlist_t tmp;

    tmp.swap(_string_results);

    return std::move(tmp);
}


bool DnpProtocol::isTransactionComplete( void ) const
{
    //  ACH: factor in application layer retries... ?

    return _command == Command_Complete || _command == Command_Invalid;
}


const auto ControlResultStrings = std::map<int, const char *> {
        { BinaryOutputControl::Status_Success,           "Request accepted, initiated, or queued."},
        { BinaryOutputControl::Status_Timeout,           "Request not accepted because the operate message was received after the arm timer timed out. The arm timer was started when the select operation for the same point was received."},
        { BinaryOutputControl::Status_NoSelect,          "Request not accepted because no previous matching select request exists. {An operate message was sent to activate an output that was not previously armed with a matching select message.},"},
        { BinaryOutputControl::Status_FormatError,       "Request not accepted because there were formatting errors in the control request."},
        { BinaryOutputControl::Status_NotSupported,      "Request not accepted because a control operation is not supported for this point."},
        { BinaryOutputControl::Status_AlreadyActive,     "Request not accepted, because the control queue is full or the point is already active."},
        { BinaryOutputControl::Status_HardwareError,     "Request not accepted because of control hardware problems."},
        { BinaryOutputControl::Status_Local,             "Request not accepted because Local/Remote switch is in Local position."},
        { BinaryOutputControl::Status_TooManyObjs,       "Request not accepted because too many objects appeared in the same request."},
        { BinaryOutputControl::Status_NotAuthorized,     "Request not accepted because of insufficient authorization."},
        { BinaryOutputControl::Status_AutomationInhibit, "Request not accepted because it was inhibited by a local automation process."},
        { BinaryOutputControl::Status_ProcessingLimited, "Request not accepted because the device cannot process any more activities than are presently in progress."},
        { BinaryOutputControl::Status_OutOfRange,        "Request not accepted because the value is outside the acceptable range permitted for this point."},
        { BinaryOutputControl::Status_NonParticipating,  "Outstation shall not issue or perform the control operation."},
        { BinaryOutputControl::Status_Undefined,         "Request not accepted because of some other undefined reason."}};

std::string DnpProtocol::getControlResultString( int result_status ) const
{
    const boost::optional<const char *> controlResultString = mapFind(ControlResultStrings, result_status);

    std::string result = "Control result (" + std::to_string(result_status) + "): ";

    if( controlResultString )
    {
        result += *controlResultString;
    }
    else
    {
        result += "Unknown error code (Reserved for future use.)";
    }

    return result;
}


unsigned DnpProtocol::convertLocalSecondsToUtcSeconds( const unsigned seconds )
{
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

unsigned DnpProtocol::convertUtcSecondsToLocalSeconds( const unsigned seconds )
{
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


}
}

