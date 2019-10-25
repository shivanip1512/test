#include "precompiled.h"

#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"
#include "rfn_asid.h"

#include "message_serialization_util.h"

#include "Thrift/RfnE2eData_types.h"

namespace Cti::Messaging::Rfn {

using namespace Messaging::Serialization;

E2eMsg::Protocol mapping( Thrift::RfnE2eProtocol::type protocol )
{
    switch( protocol )
    {
        default:
        case Thrift::RfnE2eProtocol::APPLICATION:
            return E2eMsg::Application;

        case Thrift::RfnE2eProtocol::NETWORK:
            return E2eMsg::Network;

        case Thrift::RfnE2eProtocol::LINK:
            return E2eMsg::Link;
    }
}

Thrift::RfnE2eProtocol::type mapping( E2eMsg::Protocol protocol )
{
    switch( protocol )
    {
        default:
        case E2eMsg::Application:
            return Thrift::RfnE2eProtocol::APPLICATION;

        case E2eMsg::Network:
            return Thrift::RfnE2eProtocol::NETWORK;

        case E2eMsg::Link:
            return Thrift::RfnE2eProtocol::LINK;
    }
}


NetworkManagerRequestHeader translate(const Thrift::NetworkManagerRequestHeader &hdr)
{
    NetworkManagerRequestHeader header;

    header.clientGuid = hdr.clientGuid;
    header.expiration = hdr.expiration;
    header.groupId    = hdr.groupId;
    header.messageId  = hdr.messageId;
    header.priority   = hdr.priority;
    header.sessionId  = hdr.sessionId;

    return header;
}


Thrift::NetworkManagerRequestHeader translate(const NetworkManagerRequestHeader &hdr)
{
    Thrift::NetworkManagerRequestHeader header;

    header.__set_clientGuid( hdr.clientGuid );
    header.__set_sessionId ( hdr.sessionId );
    header.__set_messageId ( hdr.messageId );
    header.__set_groupId   ( hdr.groupId );
    header.__set_priority  ( hdr.priority );
    header.__set_expiration( hdr.expiration );

    //  Default all messages to expire with our session
    header.__set_lifetime  ( Thrift::NetworkManagerMessageLifetime::SESSION );

    return header;
}


//=============================================================================
//  RfnE2eDataRequest
//=============================================================================

MessagePtr<Thrift::RfnE2eDataRequest>::type serialize( const E2eDataRequestMsg& imsg )
{
    MessagePtr<Thrift::RfnE2eDataRequest>::type omsg( new Thrift::RfnE2eDataRequest );

    omsg->__set_e2eProtocol         ( mapping(imsg.protocol) );

    omsg->__set_applicationServiceId( static_cast<int8_t>(imsg.applicationServiceId) );
    omsg->__set_payload             ( transformContainer<std::string>( imsg.payload ) );
    omsg->__set_priority            ( imsg.highPriority
                                        ? Thrift::RfnE2eMessagePriority::APP_HI
                                        : Thrift::RfnE2eMessagePriority::APP_LO );

    {
        Thrift::RfnIdentifier rfnId;

        rfnId.__set_sensorManufacturer  ( imsg.rfnIdentifier.manufacturer );
        rfnId.__set_sensorModel         ( imsg.rfnIdentifier.model );
        rfnId.__set_sensorSerialNumber  ( imsg.rfnIdentifier.serialNumber );

        omsg->__set_rfnIdentifier       ( rfnId );

        omsg->__set_header              ( translate( imsg.header ) );
    }

    if( imsg.security )
    {
        omsg->__set_security( *imsg.security );
    }

    return omsg;
}

MessagePtr<E2eDataRequestMsg>::type deserialize( const Thrift::RfnE2eDataRequest& imsg )
{
    MessagePtr<E2eDataRequestMsg>::type omsg( new E2eDataRequestMsg );

    omsg->protocol             = mapping(imsg.e2eProtocol);
    omsg->applicationServiceId = static_cast<ApplicationServiceIdentifiers>( imsg.applicationServiceId );
    omsg->payload              = transformContainer<std::vector<unsigned char>>( imsg.payload );
    omsg->highPriority         = imsg.priority == Thrift::RfnE2eMessagePriority::APP_HI;

    ::Cti::RfnIdentifier rfnId;

    rfnId.manufacturer         = imsg.rfnIdentifier.sensorManufacturer;
    rfnId.model                = imsg.rfnIdentifier.sensorModel;
    rfnId.serialNumber         = imsg.rfnIdentifier.sensorSerialNumber;

    omsg->rfnIdentifier        = rfnId;

    if( imsg.__isset.header )
    {
        omsg->header = translate(imsg.header);
    }

    if( imsg.__isset.security )
    {
        omsg->security         = imsg.security;
    }

    return omsg;
}

//=============================================================================
//  RfnE2eDataConfirm
//=============================================================================

MessagePtr<Thrift::RfnE2eDataConfirm>::type serialize( const E2eDataConfirmMsg& imsg )
{
    MessagePtr<Thrift::RfnE2eDataConfirm>::type omsg( new Thrift::RfnE2eDataConfirm );

    omsg->__set_e2eProtocol         ( mapping(imsg.protocol) );
    omsg->__set_applicationServiceId( static_cast<int8_t>(imsg.applicationServiceId) );
    omsg->__set_replyType           ( static_cast<Thrift::RfnE2eDataReplyType::type>( imsg.replyType ) );

    Thrift::RfnIdentifier rfnId;

    rfnId.__set_sensorManufacturer  ( imsg.rfnIdentifier.manufacturer );
    rfnId.__set_sensorModel         ( imsg.rfnIdentifier.model );
    rfnId.__set_sensorSerialNumber  ( imsg.rfnIdentifier.serialNumber );

    omsg->__set_rfnIdentifier       ( rfnId );

    if( imsg.header )
    {
        omsg->__set_header( translate( *imsg.header ) );
    }

    return omsg;
}

MessagePtr<E2eDataConfirmMsg>::type deserialize( const Thrift::RfnE2eDataConfirm& imsg )
{
    MessagePtr<E2eDataConfirmMsg>::type omsg( new E2eDataConfirmMsg );

    omsg->protocol             = mapping(imsg.e2eProtocol);
    omsg->applicationServiceId = static_cast<ApplicationServiceIdentifiers>( imsg.applicationServiceId );
    omsg->replyType            = static_cast<E2eDataConfirmMsg::ReplyType>( imsg.replyType );

    ::Cti::RfnIdentifier rfnId;

    rfnId.manufacturer         = imsg.rfnIdentifier.sensorManufacturer;
    rfnId.model                = imsg.rfnIdentifier.sensorModel;
    rfnId.serialNumber         = imsg.rfnIdentifier.sensorSerialNumber;

    omsg->rfnIdentifier        = rfnId;

    if( imsg.__isset.header )
    {
        omsg->header = translate( imsg.header );
    }

    return omsg;
}

//=============================================================================
//  RfnE2eDataIndication
//=============================================================================

MessagePtr<Thrift::RfnE2eDataIndication>::type serialize( const E2eDataIndicationMsg& imsg )
{
    MessagePtr<Thrift::RfnE2eDataIndication>::type omsg( new Thrift::RfnE2eDataIndication );

    omsg->__set_e2eProtocol         ( mapping(imsg.protocol) );
    omsg->__set_applicationServiceId( static_cast<int8_t>(imsg.applicationServiceId) );
    omsg->__set_payload             ( transformContainer<std::string>( imsg.payload ) );
    omsg->__set_priority            ( imsg.highPriority
                                        ? Thrift::RfnE2eMessagePriority::APP_HI
                                        : Thrift::RfnE2eMessagePriority::APP_LO );

    Thrift::RfnIdentifier rfnId;

    rfnId.__set_sensorManufacturer  ( imsg.rfnIdentifier.manufacturer );
    rfnId.__set_sensorModel         ( imsg.rfnIdentifier.model );
    rfnId.__set_sensorSerialNumber  ( imsg.rfnIdentifier.serialNumber );

    omsg->__set_rfnIdentifier       ( rfnId );

    if( imsg.security )
    {
        omsg->__set_security        ( *imsg.security );
    }

    return omsg;
}

MessagePtr<E2eDataIndicationMsg>::type deserialize( const Thrift::RfnE2eDataIndication& imsg )
{
    MessagePtr<E2eDataIndicationMsg>::type omsg( new E2eDataIndicationMsg );

    omsg->protocol             = mapping(imsg.e2eProtocol);
    omsg->applicationServiceId = static_cast<ApplicationServiceIdentifiers>( imsg.applicationServiceId );
    omsg->payload              = transformContainer<std::vector<unsigned char>>( imsg.payload );
    omsg->highPriority         = imsg.priority == Thrift::RfnE2eMessagePriority::APP_HI;

    ::Cti::RfnIdentifier rfnId;

    rfnId.manufacturer         = imsg.rfnIdentifier.sensorManufacturer;
    rfnId.model                = imsg.rfnIdentifier.sensorModel;
    rfnId.serialNumber         = imsg.rfnIdentifier.sensorSerialNumber;

    omsg->rfnIdentifier        = rfnId;

    if( imsg.__isset.security )
    {
        omsg->security         = imsg.security;
    }

    return omsg;
}

const std::string RfnMessagePrefix = "com.eaton.eas.yukon.networkmanager.e2e.rfn.";

Serialization::MessageFactory<E2eMsg> DLLEXPORT e2eMessageFactory(RfnMessagePrefix);

struct RfnMessageRegistration  //  must be named so it can have a constructor
{
    RfnMessageRegistration()
    {
        e2eMessageFactory.registerSerializer<E2eDataRequestMsg,    Thrift::RfnE2eDataRequest>    ( &serialize, &deserialize, "E2eDataRequest" );
        e2eMessageFactory.registerSerializer<E2eDataConfirmMsg,    Thrift::RfnE2eDataConfirm>    ( &serialize, &deserialize, "E2eDataConfirm" );
        e2eMessageFactory.registerSerializer<E2eDataIndicationMsg, Thrift::RfnE2eDataIndication> ( &serialize, &deserialize, "E2eDataIndication" );
    }

} rfnMessageRegistration;

}