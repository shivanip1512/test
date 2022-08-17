#include "precompiled.h"

#include "message_factory.h"

#include "RfnBroadcastReplyMessage.h"
#include "Thrift/RfnExpressComBroadcastReply_types.h"

#include "RfnWaterNodeMessaging.h"
#include "Thrift/RfnWaterNodeConfigMessages_types.h"

#include "Thrift/RfnE2eData_types.h"
#include "Thrift/NetworkManagerMessaging_types.h"

#include "DeviceCreation.h"
#include "Thrift/DeviceCreation_types.h"

#include "RfnDataStreamingUpdate.h"
#include "Thrift/RfnDataStreamingUpdate_types.h"

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

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<Rfn::RfnSetChannelConfigRequestMessage>::serialize( const Rfn::RfnSetChannelConfigRequestMessage &m )
try
{
    Thrift::RfnIdentifier               identifier;
    Thrift::RfnSetChannelConfigRequest  request;

    identifier.__set_sensorManufacturer ( m.rfnIdentifier.manufacturer ); 
    identifier.__set_sensorModel        ( m.rfnIdentifier.model );
    identifier.__set_sensorSerialNumber ( m.rfnIdentifier.serialNumber ); 

    request.__set_rfnIdentifier         ( identifier );

    request.__set_reportingInterval     ( m.reportingInterval );
    request.__set_recordingInterval     ( m.recordingInterval );

    if ( m.header )
    {
        Thrift::NetworkManagerRequestHeader     header;

        header.__set_clientGuid ( m.header->clientGuid );
        header.__set_sessionId  ( m.header->sessionId );
        header.__set_messageId  ( m.header->messageId );
        header.__set_groupId    ( m.header->groupId );
        header.__set_priority   ( m.header->priority );
        header.__set_expiration ( m.header->expiration );
        header.__set_lifetime   ( Thrift::NetworkManagerMessageLifetime::SESSION ); 

        request.__set_header    ( header );
    }

    return SerializeThriftBytes( request );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

namespace Thrift
{

bool ChannelInfo::operator<( const ChannelInfo & rhs ) const
{
    return std::tie( UOM, uomModifier, channelNum, enabled )
            < std::tie( rhs.UOM, rhs.uomModifier, rhs.channelNum, rhs.enabled );
}

}

template<>
boost::optional<Rfn::RfnSetChannelConfigReplyMessage> IM_EX_MSG MessageSerializer<Rfn::RfnSetChannelConfigReplyMessage>::deserialize( const std::vector<unsigned char> &buf )
try
{
    const Thrift::RfnSetChannelConfigReply thriftMsg = DeserializeThriftBytes<Thrift::RfnSetChannelConfigReply>(buf);

    Rfn::RfnSetChannelConfigReplyMessage msg( thriftMsg.reply );

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<Rfn::RfnGetChannelConfigRequestMessage>::serialize( const Rfn::RfnGetChannelConfigRequestMessage &m )
try
{
    Thrift::RfnIdentifier               identifier;
    Thrift::RfnGetChannelConfigRequest  request;

    identifier.__set_sensorManufacturer ( m.rfnIdentifier.manufacturer ); 
    identifier.__set_sensorModel        ( m.rfnIdentifier.model );
    identifier.__set_sensorSerialNumber ( m.rfnIdentifier.serialNumber ); 

    request.__set_rfnIdentifier         ( identifier );

    return SerializeThriftBytes( request );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

template<>
boost::optional<Rfn::RfnGetChannelConfigReplyMessage> IM_EX_MSG MessageSerializer<Rfn::RfnGetChannelConfigReplyMessage>::deserialize( const std::vector<unsigned char> &buf )
try
{
    const Thrift::RfnGetChannelConfigReply thriftMsg = DeserializeThriftBytes<Thrift::RfnGetChannelConfigReply>(buf);

    Rfn::RfnGetChannelConfigReplyMessage msg;

    msg.timestamp           = thriftMsg.timestamp;

    for ( const auto & entry : thriftMsg.channelInfo )
    {
        Rfn::RfnGetChannelConfigReplyMessage::ChannelInfo   info;

        info.UOM            = entry.UOM;
        info.uomModifier    = entry.uomModifier;
        info.channelNumber  = entry.channelNum;
        info.enabled        = entry.enabled;

        msg.channelInfo.insert( info );
    }

    msg.rfnIdentifier.manufacturer  = thriftMsg.rfnIdentifier.sensorManufacturer; 
    msg.rfnIdentifier.model         = thriftMsg.rfnIdentifier.sensorModel; 
    msg.rfnIdentifier.serialNumber  = thriftMsg.rfnIdentifier.sensorSerialNumber; 

    msg.recordingInterval   = thriftMsg.recordingInterval; 
    msg.reportingInterval   = thriftMsg.reportingInterval; 
    msg.replyCode           = thriftMsg.reply; 

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<RfnDeviceCreationRequestMessage>::serialize(const RfnDeviceCreationRequestMessage &m)
try
{
    Thrift::RfnIdentifier               identifier;
    Thrift::RfnDeviceCreationRequest    request;

    identifier.__set_sensorManufacturer(m.rfnIdentifier.manufacturer);
    identifier.__set_sensorModel(m.rfnIdentifier.model);
    identifier.__set_sensorSerialNumber(m.rfnIdentifier.serialNumber);

    request.__set_rfnIdentifier(identifier);

    return SerializeThriftBytes(request);
}
catch (apache::thrift::TException)
{
    //  log?
    return {};
}

template<>
boost::optional<RfnDeviceCreationReplyMessage> IM_EX_MSG MessageSerializer<RfnDeviceCreationReplyMessage>::deserialize(const std::vector<unsigned char> &buf)
try
{
    const Thrift::RfnDeviceCreationReply thriftMsg = DeserializeThriftBytes<Thrift::RfnDeviceCreationReply>(buf);

    RfnDeviceCreationReplyMessage msg;

    if ( thriftMsg.__isset.descriptor )
    {
        const auto & descriptor = thriftMsg.descriptor;
        msg.descriptor = DeviceCreationDescriptor();
        msg.descriptor->paoId      = descriptor.paoId;
        msg.descriptor->category   = descriptor.category;
        msg.descriptor->deviceType = descriptor.deviceType;
    }
    msg.success = thriftMsg.success;

    return msg;
}
catch (apache::thrift::TException)
{
    //  log?
    return boost::none;
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<Rfn::DataStreamingUpdateMessage>::serialize(const Rfn::DataStreamingUpdateMessage &m)
try
{
    Thrift::RfnDataStreamingUpdate update;

    update.paoId = m.paoId;
    update.json  = m.json;

    return SerializeThriftBytes(update);
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

template<>
boost::optional<Rfn::DataStreamingUpdateReplyMessage> IM_EX_MSG MessageSerializer<Rfn::DataStreamingUpdateReplyMessage>::deserialize(const std::vector<unsigned char> &buf)
try
{
    const Thrift::RfnDataStreamingUpdateReply thriftMsg = DeserializeThriftBytes<Thrift::RfnDataStreamingUpdateReply>(buf);

    Rfn::DataStreamingUpdateReplyMessage msg;

    msg.success = thriftMsg.success;

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
