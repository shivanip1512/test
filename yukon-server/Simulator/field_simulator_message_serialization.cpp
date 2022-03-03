#include "precompiled.h"

#include "field_simulator_message_serialization.h"

#include "std_helper.h"
#include "amq_connection.h"

using namespace std;

namespace Cti::Messaging::Serialization {

namespace {

Thrift::FieldSimulator::FieldSimulatorSettings populateThriftSettings(FieldSimulator::Settings settings)
{
    Thrift::FieldSimulator::FieldSimulatorSettings thriftSettings;

    thriftSettings.__set__deviceGroup(
        settings.deviceGroup);
    thriftSettings.__set__deviceConfigFailureRate(
        settings.deviceConfigFailureRate);

    return thriftSettings;
}

FieldSimulator::Settings populateSettings(Thrift::FieldSimulator::FieldSimulatorSettings thriftSettings)
{
    FieldSimulator::Settings settings;

    settings.deviceGroup =
        thriftSettings._deviceGroup;
    settings.deviceConfigFailureRate =
        thriftSettings._deviceConfigFailureRate;

    return settings;
}

}
    
using StatusRequestMsg = FieldSimulator::StatusRequestMsg;
using StatusResponseMsg = FieldSimulator::StatusResponseMsg;

template<>
boost::optional<StatusRequestMsg> MessageSerializer<StatusRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::FieldSimulator::FieldSimulatorStatusRequest>(msg);

        return StatusRequestMsg {};
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(StatusRequestMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<StatusResponseMsg>::serialize(const StatusResponseMsg& msg)
{
    try
    {
        Thrift::FieldSimulator::FieldSimulatorStatusResponse tmsg;

        tmsg.__set__settings(
            populateThriftSettings(msg.settings));

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(StatusResponseMsg).name() << "\"");
    }

    return{};
}

using ModifyReqMsg = FieldSimulator::ModifyConfigurationRequestMsg;
using ModifyRspMsg = FieldSimulator::ModifyConfigurationResponseMsg;

template<>
boost::optional<ModifyReqMsg> MessageSerializer<ModifyReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::FieldSimulator::FieldSimulatorConfigurationRequest>(msg);

        return ModifyReqMsg { populateSettings(tmsg._settings) };
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(ModifyReqMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<ModifyRspMsg>::serialize(const ModifyRspMsg& msg)
{
    try
    {
        Thrift::FieldSimulator::FieldSimulatorConfigurationResponse tmsg;

        tmsg.__set__settings(populateThriftSettings(msg.settings));
        tmsg.__set__success(msg.success);

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(ModifyRspMsg).name() << "\"");
    }

    return{};
}

}