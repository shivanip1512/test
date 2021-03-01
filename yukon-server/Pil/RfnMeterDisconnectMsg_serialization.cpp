#include "precompiled.h"

#include "RfnMeterDisconnectMsg.h"
#include "Thrift/RfnMeterDisconnectRequest_types.h"

#include "amq_connection.h"
#include "message_serialization_util.h"

namespace Cti::Messaging::Serialization {

using RmdReqMsg = Rfn::RfnMeterDisconnectRequestMsg;
using RmdRepMsg = Rfn::RfnMeterDisconnectInitialReplyMsg;
using RmdConMsg = Rfn::RfnMeterDisconnectConfirmationReplyMsg;

template<>
boost::optional<RmdReqMsg> MessageSerializer<RmdReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg)
{
    static const std::map<Thrift::RfnMeterDisconnectCmdType::type, Rfn::RfnMeterDisconnectCmdType> actionTypes {
        { Thrift::RfnMeterDisconnectCmdType::ARM,
            Rfn::RfnMeterDisconnectCmdType::ARM },
        { Thrift::RfnMeterDisconnectCmdType::QUERY,
            Rfn::RfnMeterDisconnectCmdType::QUERY },
        { Thrift::RfnMeterDisconnectCmdType::RESUME,
            Rfn::RfnMeterDisconnectCmdType::RESUME },
        { Thrift::RfnMeterDisconnectCmdType::TERMINATE,
            Rfn::RfnMeterDisconnectCmdType::TERMINATE } };

    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::RfnMeterDisconnectRequest>(msg);

        RmdReqMsg msg;
        
        msg.rfnIdentifier = fromThrift( tmsg.rfnIdentifier );

        if( const auto action = mapFind(actionTypes, tmsg.action) )
        {
            msg.action = *action;
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown action type " << static_cast<int>(tmsg.action) << ", discarding request to " << msg.rfnIdentifier);
            return boost::none;
        }

        return msg;
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
    static const std::map<Rfn::RfnMeterDisconnectInitialReplyType, Thrift::RfnMeterDisconnectInitialReplyType::type> initialReplyTypes {
        { Rfn::RfnMeterDisconnectInitialReplyType::FAILURE,
            Thrift::RfnMeterDisconnectInitialReplyType::FAILURE },
        { Rfn::RfnMeterDisconnectInitialReplyType::NO_GATEWAY,
            Thrift::RfnMeterDisconnectInitialReplyType::NO_GATEWAY },
        { Rfn::RfnMeterDisconnectInitialReplyType::NO_NODE,
            Thrift::RfnMeterDisconnectInitialReplyType::NO_NODE },
        { Rfn::RfnMeterDisconnectInitialReplyType::OK,
            Thrift::RfnMeterDisconnectInitialReplyType::OK },
        { Rfn::RfnMeterDisconnectInitialReplyType::TIMEOUT,
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
    static const std::map<Rfn::RfnMeterDisconnectConfirmationReplyType, Thrift::RfnMeterDisconnectConfirmationReplyType::type> confirmReplyTypes {
        { Rfn::RfnMeterDisconnectConfirmationReplyType::SUCCESS,
            Thrift::RfnMeterDisconnectConfirmationReplyType::SUCCESS },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::FAILURE,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::FAILED_UNEXPECTED_STATUS,
            Thrift::RfnMeterDisconnectConfirmationReplyType::FAILED_UNEXPECTED_STATUS },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::NOT_SUPPORTED,
            Thrift::RfnMeterDisconnectConfirmationReplyType::NOT_SUPPORTED },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::NETWORK_TIMEOUT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::NETWORK_TIMEOUT },
        { Rfn::RfnMeterDisconnectConfirmationReplyType::TIMEOUT,
            Thrift::RfnMeterDisconnectConfirmationReplyType::TIMEOUT }
    };

    static const std::map<Rfn::RfnMeterDisconnectState, Thrift::RfnMeterDisconnectState::type> states {
        { Rfn::RfnMeterDisconnectState::UNKNOWN,
            Thrift::RfnMeterDisconnectState::UNKNOWN },
        { Rfn::RfnMeterDisconnectState::CONNECTED,
            Thrift::RfnMeterDisconnectState::CONNECTED },
        { Rfn::RfnMeterDisconnectState::DISCONNECTED,
            Thrift::RfnMeterDisconnectState::DISCONNECTED },
        { Rfn::RfnMeterDisconnectState::ARMED,
            Thrift::RfnMeterDisconnectState::ARMED },
        { Rfn::RfnMeterDisconnectState::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE,
            Thrift::RfnMeterDisconnectState::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { Rfn::RfnMeterDisconnectState::CONNECTED_DEMAND_THRESHOLD_ACTIVE,
            Thrift::RfnMeterDisconnectState::CONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { Rfn::RfnMeterDisconnectState::DISCONNECTED_CYCLING_ACTIVE,
            Thrift::RfnMeterDisconnectState::DISCONNECTED_CYCLING_ACTIVE },
        { Rfn::RfnMeterDisconnectState::CONNECTED_CYCLING_ACTIVE,
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