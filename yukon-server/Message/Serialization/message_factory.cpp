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

#include "LMEatonCloudMessages.h"
#include "Thrift/LMEatonCloudCommandData_types.h"

#include "LMEcobeeMessages.h"
#include "Thrift/LMEcobeeCommandData_types.h"

#include "std_helper.h"

#include <boost/optional.hpp>
#include <boost/assign/list_of.hpp>

namespace Cti::Messaging::Serialization
{

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
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnExpressComBroadcastReply>(buf);

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
        if ( m.header->groupId )
        {
            header.__set_groupId( *m.header->groupId );
        } 
        header.__set_priority   ( m.header->priority );
        if ( m.header->expiration )
        {
            header.__set_expiration( *m.header->expiration );
        }

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

template<>
boost::optional<Rfn::RfnSetChannelConfigRequestMessage> IM_EX_MSG MessageSerializer<Rfn::RfnSetChannelConfigRequestMessage>::deserialize(const std::vector<unsigned char>& buf)
try
{
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnSetChannelConfigRequest>(buf);

    Rfn::RfnSetChannelConfigRequestMessage msg;

    msg.rfnIdentifier.manufacturer = thriftMsg.rfnIdentifier.sensorManufacturer;
    msg.rfnIdentifier.model        = thriftMsg.rfnIdentifier.sensorModel;
    msg.rfnIdentifier.serialNumber = thriftMsg.rfnIdentifier.sensorSerialNumber;

    msg.reportingInterval = thriftMsg.reportingInterval;
    msg.recordingInterval = thriftMsg.recordingInterval;

    if ( thriftMsg.__isset.header )
    {
        Rfn::NetworkManagerRequestHeader header;

        header.clientGuid = thriftMsg.header.clientGuid;
        header.sessionId  = thriftMsg.header.sessionId;
        header.messageId  = thriftMsg.header.messageId;
        header.groupId    = thriftMsg.header.groupId;
        header.priority   = thriftMsg.header.priority;
        header.expiration = thriftMsg.header.expiration;
        //  we don't use thriftMsg.header.lifetime

        msg.header = header;
    }

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
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
std::vector<unsigned char> IM_EX_MSG MessageSerializer<Rfn::RfnSetChannelConfigReplyMessage>::serialize(const Rfn::RfnSetChannelConfigReplyMessage& m)
try
{
    Thrift::RfnSetChannelConfigReply reply;

    reply.__set_reply( static_cast<Thrift::SetChannelConfigReplyType::type>( m.replyCode ) );

    return SerializeThriftBytes( reply );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

template<>
boost::optional<Rfn::RfnSetChannelConfigReplyMessage> IM_EX_MSG MessageSerializer<Rfn::RfnSetChannelConfigReplyMessage>::deserialize( const std::vector<unsigned char> &buf )
try
{
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnSetChannelConfigReply>(buf);

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
boost::optional<Rfn::RfnGetChannelConfigRequestMessage> IM_EX_MSG MessageSerializer<Rfn::RfnGetChannelConfigRequestMessage>::deserialize(const std::vector<unsigned char>& buf)
try
{
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnGetChannelConfigRequest>(buf);

    RfnIdentifier rfnIdentifier;

    rfnIdentifier.manufacturer = thriftMsg.rfnIdentifier.sensorManufacturer;
    rfnIdentifier.model        = thriftMsg.rfnIdentifier.sensorModel;
    rfnIdentifier.serialNumber = thriftMsg.rfnIdentifier.sensorSerialNumber;

    Rfn::RfnGetChannelConfigRequestMessage msg { rfnIdentifier };

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
}


template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<Rfn::RfnGetChannelConfigReplyMessage>::serialize(const Rfn::RfnGetChannelConfigReplyMessage& m)
try
{
    Thrift::RfnIdentifier             identifier;
    Thrift::RfnGetChannelConfigReply  reply;

    identifier.__set_sensorManufacturer(m.rfnIdentifier.manufacturer);
    identifier.__set_sensorModel(m.rfnIdentifier.model);
    identifier.__set_sensorSerialNumber(m.rfnIdentifier.serialNumber);

    reply.__set_rfnIdentifier(identifier);

    reply.__set_timestamp( m.timestamp.seconds() );

    std::set<Thrift::ChannelInfo> channelInfo;

    for( const auto& entry : m.channelInfo )
    {
        Thrift::ChannelInfo   info;

        info.UOM = entry.UOM;
        info.uomModifier = entry.uomModifier;
        info.channelNum = entry.channelNumber;
        info.enabled = entry.enabled;

        channelInfo.insert(info);
    }

    reply.__set_channelInfo( channelInfo );

    reply.__set_recordingInterval( m.recordingInterval );
    reply.__set_reportingInterval( m.reportingInterval );
    reply.__set_reply( static_cast<Thrift::GetChannelConfigReplyType::type>( m.replyCode ) );

    return SerializeThriftBytes( reply );
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
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnGetChannelConfigReply>(buf);

    Rfn::RfnGetChannelConfigReplyMessage msg;

    msg.timestamp            = thriftMsg.timestamp/1000;

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
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnDeviceCreationReply>(buf);

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
    const auto thriftMsg = DeserializeThriftBytes<Thrift::RfnDataStreamingUpdateReply>(buf);

    Rfn::DataStreamingUpdateReplyMessage msg;

    msg.success = thriftMsg.success;

    return msg;
}
catch( apache::thrift::TException )
{
    //  log?
    return boost::none;
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEatonCloudStopRequest>::serialize(const LoadManagement::LMEatonCloudStopRequest &m)
try
{
    const std::map<LoadManagement::LMEatonCloudStopRequest::StopType, Thrift::LMEatonCloudStopType::type>   stopTranslator
    {
        { LoadManagement::LMEatonCloudStopRequest::StopType::Restore,   Thrift::LMEatonCloudStopType::RESTORE       },
        { LoadManagement::LMEatonCloudStopRequest::StopType::StopCycle, Thrift::LMEatonCloudStopType::STOP_CYCLE    }
    };

    Thrift::LMEatonCloudStopCommand request;

    request.__set__groupId      (  m._groupId                       );
    request.__set__restoreTime  (  m._stopTime.seconds()            );
    request.__set__vRelayId     (  m._relayId                       );

    if ( auto result = mapFind( stopTranslator, m._stopType ) )
    {
        request.__set__stopType ( *result );
    }
    else
    {
        CTILOG_ERROR( dout, "Unsupported Stop Type enumeration with key: '" << static_cast<int>( m._stopType ) << "'" );

        return {};
    }

    return SerializeThriftBytes( request );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEatonCloudCycleRequest>::serialize(const LoadManagement::LMEatonCloudCycleRequest &m)
try
{
    const std::map<LoadManagement::LMEatonCloudCycleRequest::CycleType, Thrift::LMEatonCloudCycleType::type>   cycleTranslator
    {
        { LoadManagement::LMEatonCloudCycleRequest::CycleType::StandardCycle,   Thrift::LMEatonCloudCycleType::STANDARD     },
        { LoadManagement::LMEatonCloudCycleRequest::CycleType::TrueCycle,       Thrift::LMEatonCloudCycleType::TRUE_CYCLE   },
        { LoadManagement::LMEatonCloudCycleRequest::CycleType::SmartCycle,      Thrift::LMEatonCloudCycleType::SMART_CYCLE  }
    };

    Thrift::LMEatonCloudScheduledCycleCommand   request;

    request.__set__groupId              (  m._groupId               );
    request.__set__controlStartDateTime (  m._startTime.seconds()   );
    request.__set__controlEndDateTime   (  m._stopTime.seconds()    );
    request.__set__isRampIn             (  m._rampIn  ==  LoadManagement::LMEatonCloudCycleRequest::RampingState::On  );
    request.__set__isRampOut            (  m._rampOut ==  LoadManagement::LMEatonCloudCycleRequest::RampingState::On  );
    request.__set__dutyCyclePercentage  (  m._dutyCyclePercent      );
    request.__set__dutyCyclePeriod      (  m._dutyCyclePeriod       );
    request.__set__criticality          (  m._criticality           );
    request.__set__vRelayId             (  m._relayId               );

    if ( auto result = mapFind( cycleTranslator, m._cycleType ) )
    {
        request.__set__cyclingOption    ( *result );
    }
    else
    {
        CTILOG_ERROR( dout, "Unsupported Cycle Type enumeration with key: '" << static_cast<int>( m._cycleType ) << "'" );

        return {};
    }

    return SerializeThriftBytes( request );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEcobeeCyclingControlMessage>::serialize(const LoadManagement::LMEcobeeCyclingControlMessage &m)
try
{
    Thrift::LMEcobeeCycleControlCommand    msg;
    
    msg.__set__programId             ( m._programId     );
    msg.__set__groupId               ( m._groupId       );
    msg.__set__controlStartDateTime  ( m._startTime     );
    msg.__set__controlEndDateTime    ( m._stopTime      );
    msg.__set__dutyCycle             ( m._dutyCycle     );
    msg.__set__isRampInOut           ( m._rampingOption );
    msg.__set__isMandatory           ( m._mandatory     );
    
    return SerializeThriftBytes( msg );
}
catch( apache::thrift::TException )
{
    //  log?
    return {};
}

const std::map<LoadManagement::TempOptionTypes, Thrift::LMEcobeeTemperatureTypes::type>    tempTranslator
{
    { LoadManagement::TempOptionTypes::Heat, Thrift::LMEcobeeTemperatureTypes::HEAT },
    { LoadManagement::TempOptionTypes::Cool, Thrift::LMEcobeeTemperatureTypes::COOL }
}; 

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEcobeeSetpointControlMessage>::serialize(const LoadManagement::LMEcobeeSetpointControlMessage &m)
try
{
    Thrift::LMEcobeeSetpointControlCommand    msg;

    msg.__set__programId             ( m._programId         );
    msg.__set__groupId               ( m._groupId           );
    msg.__set__controlStartDateTime  ( m._startTime         );
    msg.__set__controlEndDateTime    ( m._stopTime          );
    msg.__set__isMandatory           ( m._mandatory         );
    msg.__set__temperatureOffset     ( m._temperatureOffset );

    if ( auto result = mapFind(tempTranslator, m._temperatureOption ) )
    {
        msg.__set__temperatureOption ( *result );
    }
    else
    {
        CTILOG_ERROR( dout, "Unsupported Temperature Option type with key: '"<< static_cast<int>( m._temperatureOption )<< "'" );

        return {};
    }

    return SerializeThriftBytes( msg );
}
catch( apache::thrift::TException )
{
    //log?
    return {};
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEcobeePlusControlMessage>::serialize(const LoadManagement::LMEcobeePlusControlMessage &m)
try
{
    Thrift::LMEcobeePlusControlCommand   msg;

    msg.__set__programId             ( m._programId         );
    msg.__set__groupId               ( m._groupId           );
    msg.__set__controlStartDateTime  ( m._startTime         );
    msg.__set__controlEndDateTime    ( m._stopTime          );
    msg.__set__randomTimeSeconds     ( m._randomTimeSeconds );

    if (auto result = mapFind(tempTranslator, m._temperatureOption))       
    {
        msg.__set__temperatureOption ( *result );
    }
    else
    {
        CTILOG_ERROR(dout, "Unsupported Temperature Option type with key: '" << static_cast<int>( m._temperatureOption ) << "'");

        return {};
    }

    return SerializeThriftBytes( msg );
}
catch( apache::thrift::TException )
{
    //log?
    return {};
}

template<>
std::vector<unsigned char> IM_EX_MSG MessageSerializer<LoadManagement::LMEcobeeRestoreMessage>::serialize(const LoadManagement::LMEcobeeRestoreMessage &m)
try
{
    Thrift::LMEcobeeRestore msg;

    msg.__set__groupId      ( m._groupId     );
    msg.__set__restoreTime  ( m._restoreTime );

    return SerializeThriftBytes( msg );
}
catch( apache::thrift::TException )
{
    //log?
    return {};
}

}


