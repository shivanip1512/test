#include "precompiled.h"

#include "RfnMeterDisconnectMsg.h"

#include "Thrift/RfnMeterDisconnectRequest_types.h"

#include "message_serialization_util.h"

namespace Cti::Messaging::Serialization {

using RmdReqMsg = Pil::RfnMeterDisconnectRequestMsg;
using RmdRepMsg = Pil::RfnMeterDisconnectInitialReplyMsg;
using RmdConMsg = Pil::RfnMeterDisconnectConfirmationReplyMsg;

template<>
boost::optional<RmdReqMsg> MessageSerializer<RmdReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::RfnMeterDisconnectRequest>(msg);

        return RmdReqMsg { fromThrift( tmsg.rfnIdentifier ) };
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(RmdReqMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<RmdRepMsg>::serialize(const RmdRepMsg& msg)
{
    static const std::map<Pil::RfnMeterDisconnectInitialReplyType, Thrift::RfnMeterDisconnectInitialReplyType::type> initialReplyTypes {
        { Pil::RfnMeterDisconnectInitialReplyType::FAILURE,
            Thrift::RfnMeterDisconnectInitialReplyType::FAILURE },
        { Pil::RfnMeterDisconnectInitialReplyType::NO_GATEWAY,
            Thrift::RfnMeterDisconnectInitialReplyType::NO_GATEWAY },
        { Pil::RfnMeterDisconnectInitialReplyType::NO_NODE,
            Thrift::RfnMeterDisconnectInitialReplyType::NO_NODE },
        { Pil::RfnMeterDisconnectInitialReplyType::OK,
            Thrift::RfnMeterDisconnectInitialReplyType::OK },
        { Pil::RfnMeterDisconnectInitialReplyType::TIMEOUT,
            Thrift::RfnMeterDisconnectInitialReplyType::TIMEOUT }
    };

    try
    {
        Thrift::RfnMeterDisconnectInitialReply tmsg;

        if( const auto replyType = mapFind(initialReplyTypes, msg.replyType) )
        {
            tmsg.__set_replyType(*replyType);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown reply type " << static_cast<int>(msg.replyType) << ", marking as FAILURE");
            tmsg.__set_replyType(Thrift::RfnMeterDisconnectInitialReplyType::FAILURE);
        }

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(RmdRepMsg).name() << "\"");
    }

    return{};
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<RmdConMsg>::serialize(const RmdConMsg& msg)
{
    static const std::map<Pil::RfnMeterDisconnectConfirmationReplyType, Thrift::RfnMeterDisconnectConfirmationReplyType::type> confirmReplyTypes {
        { Pil::RfnMeterDisconnectConfirmationReplyType::SUCCESS,
            Thrift::RfnMeterDisconnectConfirmationReplyType::SUCCESS },
        { Pil::RfnMeterDisconnectConfirmationReplyType::FAILURE,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE },
        { Pil::RfnMeterDisconnectConfirmationReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD },
        { Pil::RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT },
        { Pil::RfnMeterDisconnectConfirmationReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT },
        { Pil::RfnMeterDisconnectConfirmationReplyType::FAILED_UNEXPECTED_STATUS,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILED_UNEXPECTED_STATUS },
        { Pil::RfnMeterDisconnectConfirmationReplyType::NOT_SUPPORTED,
            Thrift::RfnMeterDisconnectConfirmationReplyType::NOT_SUPPORTED },
        { Pil::RfnMeterDisconnectConfirmationReplyType::NETWORK_TIMEOUT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::NETWORK_TIMEOUT },
        { Pil::RfnMeterDisconnectConfirmationReplyType::TIMEOUT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::TIMEOUT }
    };

    static const std::map<Pil::RfnMeterDisconnectState, Thrift::RfnMeterDisconnectState::type> states {
        { Pil::RfnMeterDisconnectState::UNKNOWN,
            Thrift::RfnMeterDisconnectState::UNKNOWN },
        { Pil::RfnMeterDisconnectState::CONNECTED,
            Thrift::RfnMeterDisconnectState::CONNECTED },
        { Pil::RfnMeterDisconnectState::DISCONNECTED,
            Thrift::RfnMeterDisconnectState::DISCONNECTED },
        { Pil::RfnMeterDisconnectState::ARMED,
            Thrift::RfnMeterDisconnectState::ARMED },
        { Pil::RfnMeterDisconnectState::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE,
            Thrift::RfnMeterDisconnectState::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { Pil::RfnMeterDisconnectState::CONNECTED_DEMAND_THRESHOLD_ACTIVE,
            Thrift::RfnMeterDisconnectState::CONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { Pil::RfnMeterDisconnectState::DISCONNECTED_CYCLING_ACTIVE,
            Thrift::RfnMeterDisconnectState::DISCONNECTED_CYCLING_ACTIVE },
        { Pil::RfnMeterDisconnectState::CONNECTED_CYCLING_ACTIVE,
            Thrift::RfnMeterDisconnectState::CONNECTED_CYCLING_ACTIVE },
    };

    try
    {
        Thrift::RfnMeterDisconnectConfirmationReply tmsg;

        if( const auto replyType = mapFind(confirmReplyTypes, msg.replyType) )
        {
            tmsg.__set_replyType(*replyType);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown reply type " << static_cast<int>(msg.replyType) << ", marking as FAILURE");
            tmsg.__set_replyType(Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE);
        }

        if( const auto state = mapFind(states, msg.state) )
        {
            tmsg.__set_state(*state);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown disconnect state " << static_cast<int>(msg.state) << ", marking as UNKNOWN");
            tmsg.__set_state(Thrift::RfnMeterDisconnectState::UNKNOWN);
        }

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(RmdConMsg).name() << "\"");
    }

    return{};
}


}