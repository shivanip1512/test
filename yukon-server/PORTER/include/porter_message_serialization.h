#pragma once

#include "message_factory.h"

#include "MeterProgramValidationMsg.h"
#include "PorterDynamicPaoInfoMsg.h"

#include "Thrift/MeterProgramValidation_types.h"
#include "Thrift/PorterDynamicPaoInfo_types.h"

#include "amq_connection.h"

namespace Cti::Messaging::Serialization {

template<>
boost::optional<Porter::DynamicPaoInfoRequestMsg> MessageSerializer<Porter::DynamicPaoInfoRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::DynamicPaoInfoResponseMsg>::serialize(const Porter::DynamicPaoInfoResponseMsg &msg);

template<>
boost::optional<Porter::MeterProgramValidationRequestMsg> MessageSerializer<Porter::MeterProgramValidationRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::MeterProgramValidationResponseMsg>::serialize(const Porter::MeterProgramValidationResponseMsg& msg);

}