#pragma once

#include "message_factory.h"

#include "MeterProgramStatusArchiveRequestMsg.h"

#include "Thrift/MeterProgramming_types.h"

#include "amq_connection.h"

namespace Cti::Messaging::Serialization {

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Pil::MeterProgramStatusArchiveRequestMsg>::serialize(const Pil::MeterProgramStatusArchiveRequestMsg &msg);

}