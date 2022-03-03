#include "precompiled.h"

#include "RfnMeterReadMsg.h"
#include "Thrift/RfnMeterReadRequest_types.h"

#include "amq_connection.h"
#include "message_serialization_util.h"

#include <boost/optional.hpp>

namespace Cti::Messaging::Serialization {

using RmrReqMsg = Rfn::RfnMeterReadRequestMsg;
using RmrRepMsg = Rfn::RfnMeterReadReplyMsg;
using RmrDatMsg = Rfn::RfnMeterReadDataReplyMsg;

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
    static const std::map<Rfn::RfnMeterReadingReplyType, Thrift::RfnMeterReadingReplyType::type> initialReplyTypes {
        { Rfn::RfnMeterReadingReplyType::FAILURE,
            Thrift::RfnMeterReadingReplyType::FAILURE },
        { Rfn::RfnMeterReadingReplyType::NO_GATEWAY,
            Thrift::RfnMeterReadingReplyType::NO_GATEWAY },
        { Rfn::RfnMeterReadingReplyType::NO_NODE,
            Thrift::RfnMeterReadingReplyType::NO_NODE },
        { Rfn::RfnMeterReadingReplyType::OK,
            Thrift::RfnMeterReadingReplyType::OK },
        { Rfn::RfnMeterReadingReplyType::TIMEOUT,
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

Thrift::RfnMeterReadingData toThrift(const Rfn::RfnMeterReadingData& data)
{
    Thrift::RfnMeterReadingData tdata;

    static const auto channelDataToThrift = [](const Rfn::ChannelData& cd) {
        static const std::map<Rfn::ChannelDataStatus, Thrift::ChannelDataStatus::type> statuses {
            { Rfn::ChannelDataStatus::OK, 
                Thrift::ChannelDataStatus::OK },
            { Rfn::ChannelDataStatus::PARTIAL_READ_TIMEOUT,
                Thrift::ChannelDataStatus::PARTIAL_READ_TIMEOUT },
            { Rfn::ChannelDataStatus::PARTIAL_READ_FAILURE,
                Thrift::ChannelDataStatus::PARTIAL_READ_FAILURE },
            { Rfn::ChannelDataStatus::PARTIAL_READ_LONG,
                Thrift::ChannelDataStatus::PARTIAL_READ_LONG },
            { Rfn::ChannelDataStatus::FULL_READ_PASSWORD_ERROR,
                Thrift::ChannelDataStatus::FULL_READ_PASSWORD_ERROR },
            { Rfn::ChannelDataStatus::FULL_READ_BUSY_ERROR,
                Thrift::ChannelDataStatus::FULL_READ_BUSY_ERROR },
            { Rfn::ChannelDataStatus::FULL_READ_TIMEOUT_ERROR,
                Thrift::ChannelDataStatus::FULL_READ_TIMEOUT_ERROR },
            { Rfn::ChannelDataStatus::FULL_READ_PROTOCOL_ERROR,
                Thrift::ChannelDataStatus::FULL_READ_PROTOCOL_ERROR },
            { Rfn::ChannelDataStatus::FULL_READ_NO_SUCH_CHANNEL_ERROR,
                Thrift::ChannelDataStatus::FULL_READ_NO_SUCH_CHANNEL_ERROR },
            { Rfn::ChannelDataStatus::FULL_READ_READ_RESPONSE_ERROR_UNKNOWN,
                Thrift::ChannelDataStatus::FULL_READ_READ_RESPONSE_ERROR_UNKNOWN },
            { Rfn::ChannelDataStatus::FULL_READ_UNKNOWN,
                Thrift::ChannelDataStatus::FULL_READ_UNKNOWN },
            { Rfn::ChannelDataStatus::FAILURE,
                Thrift::ChannelDataStatus::FAILURE } };
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
    static const auto datedChannelDataToThrift = [](const Rfn::DatedChannelData& dcd) {
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
    static const std::map<Rfn::RfnMeterReadingDataReplyType, Thrift::RfnMeterReadingDataReplyType::type> replyTypes {
        { Rfn::RfnMeterReadingDataReplyType::FAILURE,
            Thrift::RfnMeterReadingDataReplyType::FAILURE },
        { Rfn::RfnMeterReadingDataReplyType::NETWORK_TIMEOUT,
            Thrift::RfnMeterReadingDataReplyType::NETWORK_TIMEOUT },
        { Rfn::RfnMeterReadingDataReplyType::OK,
            Thrift::RfnMeterReadingDataReplyType::OK },
        { Rfn::RfnMeterReadingDataReplyType::TIMEOUT,
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
