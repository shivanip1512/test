#include "precompiled.h"

#include "NetworkManagerRequest.h"

#include "message_serialization_util.h"

#include "Thrift/NetworkManagerMessaging_types.h"

#include <boost/bimap.hpp>

namespace Cti {
namespace Messaging {
namespace Rfn {

using namespace Messaging::Serialization;

namespace {

using tCancel = Thrift::NetworkManagerCancelType::type;
using mCancel = NetworkManagerCancelRequest::CancelType;

tCancel mapping(mCancel t)
{
    switch( t )
    {
        default:                CTILOG_WARN(dout, "unknown value " << static_cast<int>(t));
        case mCancel::Group:    return tCancel::GROUP_IDS;
        case mCancel::Message:  return tCancel::MESSAGE_IDS;
    }
}

mCancel mapping(tCancel t)
{
    switch( t )
    {
        default:                    CTILOG_WARN(dout, "unknown value " << t);
                                    return mCancel::Invalid;
        case tCancel::GROUP_IDS:    return mCancel::Group;
        case tCancel::MESSAGE_IDS:  return mCancel::Message;
    }
}

}

//=============================================================================
//  NetworkManagerCancelRequest
//=============================================================================

MessagePtr<Thrift::NetworkManagerCancelRequest>::type serialize( const NetworkManagerCancelRequest& imsg )
{
    MessagePtr<Thrift::NetworkManagerCancelRequest>::type omsg( new Thrift::NetworkManagerCancelRequest );

    omsg->__set_clientGuid( imsg.clientGuid );
    omsg->__set_sessionId ( imsg.sessionId );
    omsg->__set_type      ( mapping( imsg.type ) );
    omsg->__set_ids       ( imsg.ids );

    return omsg;
}

MessagePtr<NetworkManagerCancelRequest>::type deserialize( const Thrift::NetworkManagerCancelRequest& imsg )
{
    MessagePtr<NetworkManagerCancelRequest>::type omsg( new NetworkManagerCancelRequest );

    omsg->clientGuid = imsg.clientGuid;
    omsg->sessionId  = imsg.sessionId;
    omsg->type       = mapping( imsg.type );
    omsg->ids        = imsg.ids;

    return omsg;
}

//=============================================================================
//  NetworkManagerCancelRequestAck
//=============================================================================

MessagePtr<Thrift::NetworkManagerCancelRequestAck>::type serialize( const NetworkManagerCancelRequestAck& imsg )
{
    MessagePtr<Thrift::NetworkManagerCancelRequestAck>::type omsg( new Thrift::NetworkManagerCancelRequestAck );

    auto request = serialize(static_cast<const NetworkManagerCancelRequest>(imsg));

    omsg->__set_request( *request );

    return omsg;
}

MessagePtr<NetworkManagerCancelRequestAck>::type deserialize( const Thrift::NetworkManagerCancelRequestAck& imsg )
{
    MessagePtr<NetworkManagerCancelRequestAck>::type omsg( new NetworkManagerCancelRequestAck );

    auto request = deserialize(imsg.request);

    static_cast<NetworkManagerCancelRequest &>(*omsg) = *request;

    return omsg;
}

//=============================================================================
//  NetworkManagerCancelResponse
//=============================================================================

namespace {

using tStatus = Thrift::NetworkManagerMessageCancelStatus::type;
using mStatus = NetworkManagerCancelResponse::MessageStatus;

mStatus mapping(tStatus s)
{
    switch( s )
    {
        default:                    CTILOG_WARN(dout, "unknown value " << s);
                                    return mStatus::Invalid;
        case tStatus::NOT_FOUND:    return mStatus::NotFound;
        case tStatus::SUCCESS:      return mStatus::Success;
    }
}

tStatus mapping(mStatus s)
{
    switch( s )
    {
        default:                    CTILOG_WARN(dout, "unknown value " << static_cast<int>(s));
        case mStatus::NotFound:     return tStatus::NOT_FOUND;
        case mStatus::Success:      return tStatus::SUCCESS;
    }
}

}

MessagePtr<Thrift::NetworkManagerCancelResponse>::type serialize( const NetworkManagerCancelResponse& imsg )
{
    MessagePtr<Thrift::NetworkManagerCancelResponse>::type omsg( new Thrift::NetworkManagerCancelResponse );

    omsg->__set_clientGuid( imsg.clientGuid );
    omsg->__set_sessionId ( imsg.sessionId );

    Thrift::MessageStatusPerId messageIds;

    for( const auto kv : imsg.results )
    {
        messageIds[kv.first] = mapping(kv.second);
    }

    omsg->__set_messageIds( messageIds );

    return omsg;
}

MessagePtr<NetworkManagerCancelResponse>::type deserialize( const Thrift::NetworkManagerCancelResponse& imsg )
{
    MessagePtr<NetworkManagerCancelResponse>::type omsg( new NetworkManagerCancelResponse );

    omsg->clientGuid = imsg.clientGuid;
    omsg->sessionId  = imsg.sessionId;

    for( const auto kv : imsg.messageIds )
    {
        omsg->results[kv.first] = mapping(kv.second);
    }

    return omsg;
}


const std::string NetworkManagerMessagePrefix = "com.eaton.eas.yukon.networkmanager.";

Serialization::MessageFactory<NetworkManagerBase> nmMessageFactory(NetworkManagerMessagePrefix);

struct NetworkManagerMessageRegistration  //  must be named so it can have a constructor
{
    NetworkManagerMessageRegistration()
    {
        nmMessageFactory.registerSerializer<NetworkManagerCancelRequest,    Thrift::NetworkManagerCancelRequest>    ( &serialize, &deserialize, "NetworkManagerCancelRequest" );
        nmMessageFactory.registerSerializer<NetworkManagerCancelRequestAck, Thrift::NetworkManagerCancelRequestAck> ( &serialize, &deserialize, "NetworkManagerCancelRequestAck" );
        nmMessageFactory.registerSerializer<NetworkManagerCancelResponse,   Thrift::NetworkManagerCancelResponse>   ( &serialize, &deserialize, "NetworkManagerCancelResponse" );
    }

} nmMessageRegistration;

}
}
}
