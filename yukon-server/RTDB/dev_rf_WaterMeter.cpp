#include "precompiled.h"

#include "dev_rf_WaterMeter.h"
#include "RfnWaterNodeMessaging.h"

#include "config_data_rfn.h"
#include "config_helpers.h"
#include "amq_connection.h"
#include "message_factory.h"

#include <future>


namespace Cti       {
namespace Devices   {

YukonError_t RfWaterMeterDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if ( auto configPart = parse.findStringForKey("installvalue") )
    {
        if ( *configPart == "all" || *configPart == "intervals" )
        {
            executeConfigInstallSingle( pReq, parse, returnMsgs, rfnRequests, *configPart,
                                        bindConfigMethod( &RfWaterMeterDevice::executePutConfigIntervals, this ) );
            
            return ClientErrors::None;
        }
    }

    return ClientErrors::NoMethod; 
}

YukonError_t RfWaterMeterDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if ( auto configPart = parse.findStringForKey("installvalue") )
    {
        if ( *configPart == "all" || *configPart == "intervals" )
        {
            executeConfigInstallSingle( pReq, parse, returnMsgs, rfnRequests, *configPart,
                                        bindConfigMethod( &RfWaterMeterDevice::executeGetConfigIntervals, this ) );

            return ClientErrors::None;
        }
    }

    return ClientErrors::NoMethod; 
}

YukonError_t processChannelConfigReply( const Cti::Messaging::Rfn::RfnSetChannelConfigReplyMessage & reply )
{
    CTILOG_INFO( dout, "Received RFN Reply: " << reply.description() );

    // translate reply.replyCode to YukonError_t

    static const std::map<int, YukonError_t>  replyCodeToYukonError
    {
        { 0, ClientErrors::None             },
        { 1, ClientErrors::InvalidWaterNode },
        { 2, ClientErrors::UnknownWaterNode },
        { 3, ClientErrors::UnknownGateway   },
        { 4, ClientErrors::WaterNodeFailure }
    };

    return mapFindOrDefault( replyCodeToYukonError, reply.replyCode, ClientErrors::Unknown ); 
}

boost::optional<Messaging::Rfn::RfnGetChannelConfigReplyMessage> readConfigurationFromNM( const RfnIdentifier & rfnId )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::Rfn;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    Rfn::RfnGetChannelConfigRequestMessage  request( rfnId );

    ActiveMQConnectionManager::SerializedMessage    serialized
        = Messaging::Serialization::MessageSerializer<Rfn::RfnGetChannelConfigRequestMessage>::serialize( request ); 

    std::promise<boost::optional<Rfn::RfnGetChannelConfigReplyMessage>> producer;
    auto consumer = producer.get_future();

    auto msgReceivedCallback =
        [ & ]( const Rfn::RfnGetChannelConfigReplyMessage & reply )
        {
            producer.set_value( reply );
        };

    auto timedOutCallback =
        [ & ](  )
        {
            producer.set_value( boost::none );
        };

    ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnGetChannelConfigReplyMessage>(
            OutboundQueue::GetWaterChannelConfigRequest,
            serialized,
            msgReceivedCallback,
            std::chrono::seconds{ 5 },
            timedOutCallback );

    return consumer.get();
}

YukonError_t RfWaterMeterDevice::executePutConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::Rfn;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if ( ! deviceConfig )
    {
        return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
    }

    // IntervalSettings

    unsigned    reportingInterval,
                recordingInterval;

    try
    {
        reportingInterval = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds );
        recordingInterval = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds );
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }

    // Read config from NM

    auto configInfo = readConfigurationFromNM( _rfnId );

    if ( ! configInfo )     // no response back from NM
    {
        return ClientErrors::NetworkManagerTimeout;
    }

    // compare NM reported config to the device config

    const bool settingsMatch =
            ( configInfo->recordingInterval == recordingInterval )
        &&  ( configInfo->reportingInterval == reportingInterval );

    if ( settingsMatch )
    {
        if ( ! parse.isKeyValid("force") )
        {
            return ClientErrors::ConfigCurrent;
        }
    }
    else
    {
        if ( parse.isKeyValid( "verify" ) )
        {
            reportConfigMismatchDetails<unsigned>( "Reporting Interval",
                                                   reportingInterval, configInfo->reportingInterval,
                                                   pReq, returnMsgs );

            reportConfigMismatchDetails<unsigned>( "Recording Interval",
                                                   recordingInterval, configInfo->recordingInterval,
                                                   pReq, returnMsgs );

            return ClientErrors::ConfigNotCurrent;
        }
        else
        {
            // send the config...

            Rfn::RfnSetChannelConfigRequestMessage  request;

            request.rfnIdentifier       = _rfnId;
            request.recordingInterval   = recordingInterval;
            request.reportingInterval   = reportingInterval;

            ActiveMQConnectionManager::SerializedMessage    serialized
                = Cti::Messaging::Serialization::MessageSerializer<Rfn::RfnSetChannelConfigRequestMessage>::serialize( request ); 

            std::promise<YukonError_t>  producer;
            auto consumer = producer.get_future();

            auto msgReceivedCallback =
                [ & ]( const Rfn::RfnSetChannelConfigReplyMessage & reply )
                {
                    producer.set_value( processChannelConfigReply( reply ) );
                };

            auto timedOutCallback =
                [ & ](  )
                {
                    producer.set_value( ClientErrors::NetworkManagerTimeout );
                };

            ActiveMQConnectionManager::enqueueMessageWithCallbackFor<Rfn::RfnSetChannelConfigReplyMessage>(
                    OutboundQueue::SetWaterChannelConfigRequest,
                    serialized,
                    msgReceivedCallback,
                    std::chrono::seconds{ 5 },
                    timedOutCallback );

            return consumer.get();
        }
    }

    return ClientErrors::None;
}

YukonError_t RfWaterMeterDevice::executeGetConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::Rfn;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    if ( auto configInfo = readConfigurationFromNM( _rfnId ) )
    {
        // get the reply code, if not SUCCESS then return the translated error code

        if ( configInfo->replyCode != Rfn::RfnGetChannelConfigReplyMessage::SUCCESS ) 
        {
            CTILOG_INFO( dout, "Received RFN Reply: " << configInfo->description() );

            // translate reply.replyCode to YukonError_t

            static const std::map<int, YukonError_t>  replyCodeToYukonError
            {
                { 0, ClientErrors::None             },
                { 1, ClientErrors::InvalidWaterNode },
                { 2, ClientErrors::UnknownWaterNode },
                { 3, ClientErrors::WaterNodeFailure }
            };

            return mapFindOrDefault( replyCodeToYukonError, configInfo->replyCode, ClientErrors::Unknown ); 
        }

        // SUCCESS -- load a string into the return message and send it out

        std::string resultString = configInfo->to_string();

        auto retMsg = std::make_unique<CtiReturnMsg>(
            pReq->DeviceId(),
            pReq->CommandString(),
            resultString,
            ClientErrors::None,
            0,
            MacroOffset::none,
            0,
            pReq->GroupMessageId(),
            pReq->UserMessageId() );

        returnMsgs.push_back( retMsg.release() );

        return ExecutionComplete;
    }

    return ClientErrors::NetworkManagerTimeout;     // no response back from NM
}

}
}

