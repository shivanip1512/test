#include "precompiled.h"

#include "message_factory.h"

#include "RfnBroadcastReplyMessage.h"
#include "Thrift/RfnExpressComBroadcastReply_types.h"

#include "std_helper.h"

#include <boost/optional.hpp>
#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Messaging {
namespace Serialization {

IM_EX_MSG MessageFactory<::CtiMessage> g_messageFactory(::Cti::Messaging::ActiveMQ::MessageType::prefix);


const std::map<Thrift::RfnExpressComBroadcastReplyType::type, const Rfn::BroadcastResult *> ReplyToResultMap = boost::assign::map_list_of
    (Thrift::RfnExpressComBroadcastReplyType::SUCCESS,
        &Rfn::BroadcastResult::Success)
    (Thrift::RfnExpressComBroadcastReplyType::FAILURE,
        &Rfn::BroadcastResult::Failure)
    (Thrift::RfnExpressComBroadcastReplyType::TIMEOUT,
        &Rfn::BroadcastResult::Timeout)
    (Thrift::RfnExpressComBroadcastReplyType::NETWORK_TIMEOUT,
        &Rfn::BroadcastResult::NetworkTimeout);

template<>
boost::optional<Rfn::RfnBroadcastReplyMessage> IM_EX_MSG MessageSerializer<Rfn::RfnBroadcastReplyMessage>::deserialize(const std::vector<unsigned char> &buf)
try
{
    const Thrift::RfnExpressComBroadcastReply thriftMsg = DeserializeThriftBytes<Thrift::RfnExpressComBroadcastReply>(buf);

    std::map<int64_t, Thrift::RfnExpressComBroadcastReplyType::type>::const_iterator itr;

    Rfn::RfnBroadcastReplyMessage msg;

    for( itr = thriftMsg.status.begin(); itr != thriftMsg.status.end(); ++itr )
    {
        if( boost::optional<const Rfn::BroadcastResult *> result = mapFind(ReplyToResultMap, itr->second) )
        {
            msg.gatewayResults[itr->first] = *result;
        }
    }

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
}


}
}
}
