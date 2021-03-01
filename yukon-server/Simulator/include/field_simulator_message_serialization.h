#pragma once

#include "message_factory.h"

#include "FieldSimulatorMsg.h"

#include "Thrift/FieldSimulator_types.h"

#include "amq_connection.h"

namespace Cti::Messaging::Serialization {

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<FieldSimulator::StatusResponseMsg>::serialize(const FieldSimulator::StatusResponseMsg& msg);

template<>
boost::optional<FieldSimulator::ModifyConfigurationRequestMsg> MessageSerializer<FieldSimulator::ModifyConfigurationRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<FieldSimulator::ModifyConfigurationResponseMsg>::serialize(const FieldSimulator::ModifyConfigurationResponseMsg& msg);

}