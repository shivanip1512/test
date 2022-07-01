#include "precompiled.h"

#include "RfnEdgeDrMessaging.h"

#include "Thrift/EdgeDrError_types.h"
#include "Thrift/EdgeDrUnicastRequest_types.h"
#include "Thrift/EdgeDrUnicastResponse_types.h"
#include "Thrift/EdgeDrBroadcastRequest_types.h"
#include "Thrift/EdgeDrBroadcastResponse_types.h"
#include "Thrift/EdgeDrDataNotification_types.h"

#include "amq_connection.h"
#include "message_serialization_util.h"


namespace Cti::Messaging::Serialization
{

template<>
boost::optional<Rfn::EdgeDrUnicastRequest> MessageSerializer<Rfn::EdgeDrUnicastRequest>::deserialize( const ActiveMQConnectionManager::SerializedMessage & msg )
{
    static const std::map<Thrift::EdgeUnicastPriority::type, Rfn::EdgeUnicastPriority> priorityTranslator
    {
        { Thrift::EdgeUnicastPriority::HIGH, Rfn::EdgeUnicastPriority::HIGH },
        { Thrift::EdgeUnicastPriority::LOW,  Rfn::EdgeUnicastPriority::LOW  }
    };

    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::EdgeDrUnicastRequest>( msg );

        return Rfn::EdgeDrUnicastRequest
        {
            tmsg.messageGuid,
            { std::cbegin(tmsg.paoIds),  std::cend(tmsg.paoIds)  },
            { std::cbegin(tmsg.payload), std::cend(tmsg.payload) },
            priorityTranslator.at( tmsg.queuePriority ),
            priorityTranslator.at( tmsg.networkPriority )
        };
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::EdgeDrUnicastRequest).name() << "\"" );
    }
    catch ( std::out_of_range & e )   // map::at() throws
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::EdgeDrUnicastRequest).name() << "\"" );
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Rfn::EdgeDrUnicastResponse>::serialize( const Rfn::EdgeDrUnicastResponse & msg )
{
    try
    {
        Thrift::EdgeDrUnicastResponse   tmsg;

        tmsg.__set_messageGuid( msg.messageGuid );
        tmsg.__set_paoToE2eId( msg.paoToE2eId );

        if ( msg.error )
        {
            Thrift::EdgeDrError error;

            error.__set_errorType( msg.error->errorType );
            error.__set_errorMessage( msg.error->errorMessage );

            tmsg.__set_error( error );
        }

        return SerializeThriftBytes( tmsg );        
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to serialize a \"" << typeid(Rfn::EdgeDrUnicastResponse).name() << "\"" );
    }

    return { };
}

template<>
boost::optional<Rfn::EdgeDrBroadcastRequest> MessageSerializer<Rfn::EdgeDrBroadcastRequest>::deserialize( const ActiveMQConnectionManager::SerializedMessage & msg )
{
    static const std::map<Thrift::EdgeBroadcastMessagePriority::type, Rfn::EdgeBroadcastMessagePriority> priorityTranslator
    {
        { Thrift::EdgeBroadcastMessagePriority::IMMEDIATE,      Rfn::EdgeBroadcastMessagePriority::IMMEDIATE     },
        { Thrift::EdgeBroadcastMessagePriority::NON_REAL_TIME,  Rfn::EdgeBroadcastMessagePriority::NON_REAL_TIME }
    };

    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::EdgeDrBroadcastRequest>( msg );

        std::optional<Rfn::EdgeBroadcastMessagePriority>  priority;

        if (  tmsg.__isset.priority )
        {
            priority = priorityTranslator.at( tmsg.priority );
        }

        return Rfn::EdgeDrBroadcastRequest
        {
            tmsg.messageGuid,
            { std::cbegin(tmsg.payload), std::cend(tmsg.payload) },
            priority
        };
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::EdgeDrBroadcastRequest).name() << "\"" );
    }
    catch ( std::out_of_range & e )   // map::at() throws
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::EdgeDrBroadcastRequest).name() << "\"" );
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Rfn::EdgeDrBroadcastResponse>::serialize( const Rfn::EdgeDrBroadcastResponse & msg )
{
    try
    {
        Thrift::EdgeDrBroadcastResponse   tmsg;

        tmsg.__set_messageGuid( msg.messageGuid );

        if ( msg.error )
        {
            Thrift::EdgeDrError error;

            error.__set_errorType( msg.error->errorType );
            error.__set_errorMessage( msg.error->errorMessage );

            tmsg.__set_error( error );
        }

        return SerializeThriftBytes( tmsg );        
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to serialize a \"" << typeid(Rfn::EdgeDrBroadcastResponse).name() << "\"" );
    }

    return { };
}

template<>
boost::optional<Rfn::EdgeDrDataNotification> MessageSerializer<Rfn::EdgeDrDataNotification>::deserialize( const ActiveMQConnectionManager::SerializedMessage & msg )
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::EdgeDrDataNotification>( msg );

        Rfn::EdgeDrDataNotification notification
        {
            tmsg.paoId,
            std::nullopt,
            std::nullopt,
            std::nullopt
        };

        if ( tmsg.__isset.payload )
        {
            notification.payload = { std::cbegin(tmsg.payload), std::cend(tmsg.payload) };
        }

        if ( tmsg.__isset.e2eId )
        {
            notification.e2eId = tmsg.e2eId;
        }

        if ( tmsg.__isset.error )
        {
            notification.error =
            {
                tmsg.error.errorType,
                tmsg.error.errorMessage,
            };
        }

        return notification;
    }
    catch ( apache::thrift::TException & e )
    {
        CTILOG_EXCEPTION_ERROR( dout, e, "Failed to deserialize a \"" << typeid(Rfn::EdgeDrBroadcastRequest).name() << "\"" );
    }

    return boost::none;
}

}

