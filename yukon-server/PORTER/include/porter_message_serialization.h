#pragma once

#include "message_factory.h"

#include "PorterDynamicPaoInfoMsg.h"
#include "MeterProgramStatusArchiveRequestMsg.h"

#include "Thrift/PorterDynamicPaoInfo_types.h"
#include "Thrift/MeterProgramming_types.h"

#include "amq_connection.h"

namespace Cti::Messaging::Serialization {

template<>
boost::optional<Porter::DynamicPaoInfoRequestMsg> MessageSerializer<Porter::DynamicPaoInfoRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::DynamicPaoInfoResponseMsg>::serialize(const Porter::DynamicPaoInfoResponseMsg &msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::MeterProgramStatusArchiveRequestMsg>::serialize(const Porter::MeterProgramStatusArchiveRequestMsg &msg);

}