#include "precompiled.h"

#include "RfnBroadcastMessaging.h"
#include "Thrift/RfnBroadcastMessaging_types.h"

#include "amq_connection.h"
#include "message_serialization_util.h"


namespace Cti::Messaging::Serialization
{

namespace Thrift
{
    bool RfnIdentifier::operator<( const RfnIdentifier & r ) const
    {
        return boost::tie(   sensorManufacturer,   sensorModel,   sensorSerialNumber )
             < boost::tie( r.sensorManufacturer, r.sensorModel, r.sensorSerialNumber );
    }
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Rfn::RfnBroadcastRequest>::serialize( const Rfn::RfnBroadcastRequest & msg )
{
    static const std::map<Rfn::RfnBroadcastDeliveryType, Thrift::RfnBroadcastDeliveryType::type> deliveryTranslator
    {
        { Rfn::RfnBroadcastDeliveryType::IMMEDIATE,     Thrift::RfnBroadcastDeliveryType::IMMEDIATE       },
        { Rfn::RfnBroadcastDeliveryType::NON_REAL_TIME, Thrift::RfnBroadcastDeliveryType::NON_REAL_TIME   }
    };

    try
    {
        Thrift::RfnBroadcastRequest   tmsg;

        tmsg.__set_sourceId( msg.sourceId );
        tmsg.__set_messageId( msg.messageId );
        tmsg.__set_broadcastApplicationId( msg.broadcastApplicationId );
        tmsg.__set_deliveryType( deliveryTranslator.at( msg.deliveryType ) );

        tmsg.__set_payload( { std::cbegin(msg.payload), std::cend(msg.payload) } );

        if ( msg.header )
        {
            const auto & header = *msg.header;

            Thrift::NetworkManagerRequestHeader nmHeader;

            nmHeader.__set_clientGuid( header.clientGuid );
            nmHeader.__set_sessionId( header.sessionId );
            if ( header.groupId )
            {
                nmHeader.__set_groupId( *header.groupId );
            }
            nmHeader.__set_messageId( header.messageId );
            if ( header.expiration )
            {
                nmHeader.__set_expiration( *header.expiration );
            }
            nmHeader.__set_priority( header.priority );

            tmsg.__set_header( nmHeader );
        }

        return SerializeThriftBytes( tmsg );        
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to serialize a \"" << typeid(Rfn::RfnBroadcastRequest).name() << "\"" );
    }
    catch ( std::out_of_range & e )   // map::at() throws
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to serialize a \"" << typeid(Rfn::RfnBroadcastRequest).name() << "\"" );
    }

    return { };
}

template<>
boost::optional<Rfn::RfnBroadcastReply> MessageSerializer<Rfn::RfnBroadcastReply>::deserialize( const ActiveMQConnectionManager::SerializedMessage & msg )
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::RfnBroadcastReply>( msg );

        Rfn::RfnBroadcastReply  reply;

        reply.replyType = tmsg.replyType;

        if ( tmsg.__isset.failureReason )
        {
            reply.failureReason = tmsg.failureReason;
        }

        for ( auto & [rfnId, message] : tmsg.gatewayErrors )
        {
            reply.gatewayErrors.emplace(
                RfnIdentifier { rfnId.sensorManufacturer, rfnId.sensorModel, rfnId.sensorSerialNumber },
                message );
        }

        if ( tmsg.__isset.header )
        {
            Rfn::NetworkManagerRequestHeader nmHeader;

            nmHeader.clientGuid = tmsg.header.clientGuid;
            nmHeader.messageId  = tmsg.header.messageId;
            nmHeader.priority   = tmsg.header.priority;
            nmHeader.sessionId  = tmsg.header.sessionId;
            if ( tmsg.header.__isset.expiration )
            {
                nmHeader.expiration = tmsg.header.expiration;
            }
            if ( tmsg.header.__isset.groupId )
            {
                nmHeader.expiration = tmsg.header.groupId;
            }

            reply.header = nmHeader;
        }

        return reply;
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::RfnBroadcastReply).name() << "\"" );
    }

    return boost::none;
}

}

