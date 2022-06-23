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

MessagePtr<Thrift::NetworkManagerCancelRequest>::type populateThrift( const NetworkManagerCancelRequest& imsg )
{
    MessagePtr<Thrift::NetworkManagerCancelRequest>::type omsg( new Thrift::NetworkManagerCancelRequest );

    omsg->__set_clientGuid( imsg.clientGuid );
    omsg->__set_sessionId ( imsg.sessionId );
    omsg->__set_type      ( mapping( imsg.type ) );
    omsg->__set_ids       ( imsg.ids );

    return omsg;
}

MessagePtr<NetworkManagerCancelRequest>::type populateMessage( const Thrift::NetworkManagerCancelRequest& imsg )
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

MessagePtr<Thrift::NetworkManagerCancelRequestAck>::type populateThrift( const NetworkManagerCancelRequestAck& imsg )
{
    MessagePtr<Thrift::NetworkManagerCancelRequestAck>::type omsg( new Thrift::NetworkManagerCancelRequestAck );

    auto request = populateThrift(static_cast<const NetworkManagerCancelRequest>(imsg));

    omsg->__set_request( *request );

    return omsg;
}

MessagePtr<NetworkManagerCancelRequestAck>::type populateMessage( const Thrift::NetworkManagerCancelRequestAck& imsg )
{
    MessagePtr<NetworkManagerCancelRequestAck>::type omsg( new NetworkManagerCancelRequestAck );

    auto request = populateMessage(imsg.request);

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

MessagePtr<Thrift::NetworkManagerCancelResponse>::type populateThrift( const NetworkManagerCancelResponse& imsg )
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

MessagePtr<NetworkManagerCancelResponse>::type populateMessage( const Thrift::NetworkManagerCancelResponse& imsg )
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


MessagePtr<Thrift::NetworkManagerRequestAck>::type populateThrift( const NetworkManagerRequestAck& imsg )
{
    MessagePtr<Thrift::NetworkManagerRequestAck>::type omsg( new Thrift::NetworkManagerRequestAck );

    Thrift::NetworkManagerRequestHeader hdr;

    hdr.__set_clientGuid( imsg.header.clientGuid );
    hdr.__set_sessionId ( imsg.header.sessionId );
    hdr.__set_messageId ( imsg.header.messageId );
    if ( imsg.header.groupId )
    {
        hdr.__set_groupId( *imsg.header.groupId );
    }
    hdr.__set_priority(imsg.header.priority);
    if ( imsg.header.expiration )
    {
        hdr.__set_expiration( *imsg.header.expiration );
    }

    //  Default all messages to expire with our session
    hdr.__set_lifetime  ( Thrift::NetworkManagerMessageLifetime::SESSION );

    omsg->__set_header( hdr );

    return omsg;
}

MessagePtr<NetworkManagerRequestAck>::type populateMessage( const Thrift::NetworkManagerRequestAck& imsg )
{
    MessagePtr<NetworkManagerRequestAck>::type omsg( new NetworkManagerRequestAck );

    NetworkManagerRequestHeader header;

    header.clientGuid = imsg.header.clientGuid;
    header.expiration = imsg.header.expiration;
    header.groupId    = imsg.header.groupId;
    header.messageId  = imsg.header.messageId;
    header.priority   = imsg.header.priority;
    header.sessionId  = imsg.header.sessionId;

    omsg->header = header;

    return omsg;
}


const std::string NetworkManagerMessagePrefix = "com.eaton.eas.yukon.networkmanager.";

Serialization::MessageFactory<NetworkManagerBase> DLLEXPORT nmMessageFactory { NetworkManagerMessagePrefix };

struct NetworkManagerMessageRegistration  //  must be named so it can have a constructor
{
    NetworkManagerMessageRegistration()
    {
        nmMessageFactory.registerSerializer<NetworkManagerCancelRequest,    Thrift::NetworkManagerCancelRequest>    ( &populateThrift, &populateMessage, "NetworkManagerCancelRequest" );
        nmMessageFactory.registerSerializer<NetworkManagerCancelRequestAck, Thrift::NetworkManagerCancelRequestAck> ( &populateThrift, &populateMessage, "NetworkManagerCancelRequestAck" );
        nmMessageFactory.registerSerializer<NetworkManagerCancelResponse,   Thrift::NetworkManagerCancelResponse>   ( &populateThrift, &populateMessage, "NetworkManagerCancelResponse" );
        nmMessageFactory.registerSerializer<NetworkManagerRequestAck,       Thrift::NetworkManagerRequestAck>       ( &populateThrift, &populateMessage, "NetworkManagerRequestAck" );
    }

} nmMessageRegistration;

}
}
}
