#include "precompiled.h"

#include "RfnMeterReadMsg.h"

#include "message_serialization_util.h"

#include "Thrift/RfnMeterReadRequest_types.h"

#include <boost/optional.hpp>

namespace Cti::Messaging::Serialization {

using RmrReqMsg = Pil::RfnMeterReadRequestMsg;
using RmrRepMsg = Pil::RfnMeterReadReplyMsg;
using RmrDatMsg = Pil::RfnMeterReadDataReplyMsg;

template<>
boost::optional<RmrReqMsg> MessageSerializer<RmrReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::RfnMeterReadRequest>(msg);

        return RmrReqMsg { fromThrift(tmsg.rfnIdentifier) };
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(RmrReqMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<RmrRepMsg>::serialize(const RmrRepMsg& msg)
{
    static const std::map<Pil::RfnMeterReadingReplyType, Thrift::RfnMeterReadingReplyType::type> initialReplyTypes {
        { Pil::RfnMeterReadingReplyType::FAILURE,
            Thrift::RfnMeterReadingReplyType::FAILURE },
        { Pil::RfnMeterReadingReplyType::NO_GATEWAY,
            Thrift::RfnMeterReadingReplyType::NO_GATEWAY },
        { Pil::RfnMeterReadingReplyType::NO_NODE,
            Thrift::RfnMeterReadingReplyType::NO_NODE },
        { Pil::RfnMeterReadingReplyType::OK,
            Thrift::RfnMeterReadingReplyType::OK },
        { Pil::RfnMeterReadingReplyType::TIMEOUT,
            Thrift::RfnMeterReadingReplyType::TIMEOUT }
    };

    try
    {
        Thrift::RfnMeterReadReply tmsg;

        if( const auto replyType = mapFind(initialReplyTypes, msg.replyType) )
        {
            tmsg.__set_replyType(*replyType);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown reply type " << static_cast<int>(msg.replyType) << ", marking as FAILURE");
            tmsg.__set_replyType(Thrift::RfnMeterReadingReplyType::FAILURE);
        }

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(RmrRepMsg).name() << "\"");
    }

    return{};
}

Thrift::RfnMeterReadingData toThrift(const Pil::RfnMeterReadingData& data)
{
    Thrift::RfnMeterReadingData tdata;

    static const auto channelDataToThrift = [](const Pil::ChannelData& cd) {
        static const std::map<Pil::ChannelDataStatus, Thrift::ChannelDataStatus::type> statuses {
            { Pil::ChannelDataStatus::FAILURE, 
                Thrift::ChannelDataStatus::FAILURE },
            { Pil::ChannelDataStatus::LONG,
                Thrift::ChannelDataStatus::LONG },
            { Pil::ChannelDataStatus::OK,
                Thrift::ChannelDataStatus::OK },
            { Pil::ChannelDataStatus::TIMEOUT, 
                Thrift::ChannelDataStatus::TIMEOUT } };
        Thrift::ChannelData tcd;
        tcd.__set_channelNumber(cd.channelNumber);
        if( auto status = mapFind(statuses, cd.status) )
        {
            tcd.__set_status(*status);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown channel data status " << static_cast<int>(cd.status) << ", setting to FAILURE");
            tcd.__set_status(Thrift::ChannelDataStatus::FAILURE);
        }

        tcd.__set_unitOfMeasure(cd.unitOfMeasure);
        tcd.__set_unitOfMeasureModifiers(cd.unitOfMeasureModifiers);
        tcd.__set_value(cd.value);

        return tcd;
    };
    static const auto datedChannelDataToThrift = [](const Pil::DatedChannelData& dcd) {
        Thrift::DatedChannelData tdcd;

        tdcd.__set_channelData(channelDataToThrift(dcd.channelData));
        tdcd.__set_timeStamp(CtiTimeToMilliseconds(dcd.timeStamp));
        if( dcd.baseChannelData )
        {
            tdcd.__set_baseChannelData(channelDataToThrift(*dcd.baseChannelData));
        }
        return tdcd;
    };

    auto tChannelDataList = boost::copy_range<std::vector<Thrift::ChannelData>>(
        data.channelDataList | boost::adaptors::transformed(channelDataToThrift));

    auto tDatedChannelDataList = boost::copy_range<std::vector<Thrift::DatedChannelData>>(
        data.datedChannelDataList | boost::adaptors::transformed(datedChannelDataToThrift));

    tdata.__set_channelDataList(tChannelDataList);
    tdata.__set_datedChannelDataList(tDatedChannelDataList);
    tdata.__set_recordInterval(data.recordInterval);
    tdata.__set_rfnIdentifier(toThrift(data.rfnIdentifier));
    tdata.__set_timeStamp(CtiTimeToMilliseconds(data.timeStamp));

    return tdata;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<RmrDatMsg>::serialize(const RmrDatMsg& msg)
{
    static const std::map<Pil::RfnMeterReadingDataReplyType, Thrift::RfnMeterReadingDataReplyType::type> replyTypes {
        { Pil::RfnMeterReadingDataReplyType::FAILURE,
            Thrift::RfnMeterReadingDataReplyType::FAILURE },
        { Pil::RfnMeterReadingDataReplyType::NETWORK_TIMEOUT,
            Thrift::RfnMeterReadingDataReplyType::NETWORK_TIMEOUT },
        { Pil::RfnMeterReadingDataReplyType::OK,
            Thrift::RfnMeterReadingDataReplyType::OK },
        { Pil::RfnMeterReadingDataReplyType::TIMEOUT,
            Thrift::RfnMeterReadingDataReplyType::TIMEOUT }
    };

    try
    {
        Thrift::RfnMeterReadDataReply tmsg;

        if( const auto replyType = mapFind(replyTypes, msg.replyType) )
        {
            tmsg.__set_replyType(*replyType);
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown reply type " << static_cast<int>(msg.replyType) << ", marking as FAILURE");
            tmsg.__set_replyType(Thrift::RfnMeterReadingDataReplyType::FAILURE);
        }

        tmsg.__set_data( toThrift(msg.data) );

        return SerializeThriftBytes(tmsg);
    }
    catch( apache::thrift::TException& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(RmrDatMsg).name() << "\"");
    }

    return{};
}

}