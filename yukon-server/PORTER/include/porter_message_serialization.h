#pragma once

#include "message_factory.h"

#include "PorterDynamicPaoInfoMsg.h"

#include "Thrift/PorterDynamicPaoInfo_types.h"

#include "amq_connection.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

template<>
boost::optional<Porter::PorterDynamicPaoInfoRequestMsg> MessageSerializer<Porter::PorterDynamicPaoInfoRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::PorterDynamicPaoInfoResponseMsg>::serialize(const Porter::PorterDynamicPaoInfoResponseMsg &msg);

}
}
}
