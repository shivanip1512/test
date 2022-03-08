#include "precompiled.h"

#include "cmd_rfn_MeterRead.h"
#include "cmd_rfn_helper.h"

#include "RfnMeterReadMsg.h"

#include "rfn_uom.h"

#include "std_helper.h"

#include <boost/range/algorithm/set_algorithm.hpp>
#include <boost/range/adaptor/indexed.hpp>

using namespace Cti::Logging::Set;

namespace {

struct RawChannel
{
    unsigned channelNumber;
    Cti::Devices::Rfn::UnitOfMeasure unitOfMeasure;
    std::optional<Cti::Devices::Rfn::UomModifier1> modifier1;
    std::optional<Cti::Devices::Rfn::UomModifier2> modifier2;
    std::uint32_t value;
    std::uint8_t status;
};

auto correlateRawChannels(const CtiTime now, std::vector<RawChannel> rawChannels) -> Cti::Messaging::Rfn::RfnMeterReadDataReplyMsg;

}

namespace Cti::Devices::Commands {

RfnMeterReadCommand::RfnMeterReadCommand(long userMessageId) :
    _userMessageId(userMessageId)
{
}

auto RfnMeterReadCommand::getApplicationServiceId() const -> ASID
{
    return ASID::ChannelManager;
}

auto RfnMeterReadCommand::getPriorityClass() const -> Messaging::Rfn::PriorityClass
{
    return Messaging::Rfn::PriorityClass::MeterRead;
}

std::string RfnMeterReadCommand::getCommandName() const
{
    return "RFN Meter Read";
}

unsigned char RfnMeterReadCommand::getCommandCode() const
{
    return Command::Request;
}

auto RfnMeterReadCommand::getCommandHeader() -> Bytes
{
    return { getCommandCode() };
}

auto RfnMeterReadCommand::getCommandData() -> Bytes
{
    return { };
}

unsigned char RfnMeterReadCommand::getOperation() const
{
    return {};  //  unused
}

long RfnMeterReadCommand::getUserMessageId() const
{
    return _userMessageId;
}

auto RfnMeterReadCommand::getResponseMessage() const -> std::optional<ReplyMsg>
{
    if( _response )
    {
        return *_response;
    }
    return std::nullopt;
}


auto makeFailureResponse(const CtiTime now)
{
    Messaging::Rfn::RfnMeterReadDataReplyMsg response {};

    response.replyType = Messaging::Rfn::RfnMeterReadingDataReplyType::FAILURE;
    response.data.timeStamp = now;

    return response;
}


RfnCommandResult RfnMeterReadCommand::error(const CtiTime now, const YukonError_t errorCode)
{
    _response = makeFailureResponse(now);

    return RfnIndividualCommand::error(now, errorCode);
}


RfnCommandResult RfnMeterReadCommand::decodeCommand(const CtiTime now, const RfnResponsePayload& response)
try
{
    validate(Condition(response.size() >= 2, ClientErrors::DataMissing)
        << "RFN meter read response does not include opcode and status");

    const auto responseType = response[0];

    validate(Condition(responseType == Response_fmt1 || responseType == Response_fmt23, ClientErrors::InvalidData)
        << "RFN meter read response type is unknown: " << static_cast<int>(responseType));

    const bool includesModifiers = (responseType == Response_fmt23);

    const auto readStatus = response[1];
    validate(Condition(readStatus == 0, ClientErrors::InvalidData)
        << "RFN meter read status is not OK: " << static_cast<int>(readStatus));

    constexpr auto headerSize = 3;

    validate(Condition(response.size() >= 3, ClientErrors::DataMissing)
        << "RFN meter read does not include channel count");

    const auto channelCount = response[2];
    const auto minimumChannelSize = (1 + 1 + 4 + 1) + (2 * includesModifiers);  //  channel number, uom, value, status, and whether or not we have a modifier
    const auto expectedSize = headerSize + channelCount * minimumChannelSize;

    validate(Condition(response.size() >= expectedSize, ClientErrors::DataMissing)
        << "RFN meter read response is too small for the channel count (" << response.size() << "<" << expectedSize << ")");

    auto pos = headerSize;

    auto coincidentCount = 0;

    std::vector<RawChannel> rawChannels;

    //  Collect the channels into a raw representation - no correlation for timestamps or coincidents.
    //    That correlation will be done below in correlateRawChannels().
    for( auto channel = 0; channel < channelCount; ++channel )
    {
        RawChannel rc;

        const auto minimumSize = pos + minimumChannelSize;

        validate(Condition(response.size() >= minimumSize, ClientErrors::DataMissing)
            << "RFN meter read response is too small for channel " << channel << "(" << response.size() << "<" << minimumSize << ")");

        rc.channelNumber = response[pos];
        pos++;
        rc.unitOfMeasure = Rfn::UnitOfMeasure(response[pos]);
        pos++;

        if( includesModifiers )
        {
            rc.modifier1 = Rfn::UomModifier1::of(response[pos], response[pos + 1]);
            pos += 2;

            if( rc.modifier1->getExtensionBit() )
            {
                const auto extendedSize = pos + 2 + 4 + 1;  //  Additional modifier + value + status

                validate(Condition(response.size() >= extendedSize, ClientErrors::DataMissing)
                    << "RFN meter read response is too small for a second UOM modifier (" << response.size() << "<" << extendedSize << ")");

                rc.modifier2 = Rfn::UomModifier2::of(response[pos], response[pos + 1]);
                pos += 2;

                validate(Condition( ! rc.modifier2->getExtensionBit(), ClientErrors::InvalidData)
                    << "RFN meter read response channel includes a third modifier, cannot decode");
            }
        }

        rc.value = response[pos    ] << 24 | 
                   response[pos + 1] << 16 | 
                   response[pos + 2] <<  8 | 
                   response[pos + 3];
        pos += 4;

        rc.status = response[pos];
        pos++;

        rawChannels.emplace_back(std::move(rc));
    }

    if( _response = correlateRawChannels(now, std::move(rawChannels)) )
    {
        FormattedList resultList;

        resultList.add("User message ID") 
            << _userMessageId;
        resultList.add("Reply type") 
            << static_cast<int>(_response->replyType);
        resultList.add("Timestamp")
            << _response->data.timeStamp.asString();
        
        resultList.add("# Channel data")
            << _response->data.channelDataList.size();
        
        for( const auto& indexedChannelData : _response->data.channelDataList | boost::adaptors::indexed() )
        {
            const auto& channelData = indexedChannelData.value();

            resultList.add("Channel data " + std::to_string(indexedChannelData.index())) 
                << FormattedList::of(
                    "Channel number", 
                        channelData.channelNumber,
                    "Status",
                        static_cast<int>(channelData.status),
                    "UOM",
                        channelData.unitOfMeasure,
                    "Modifiers",
                        channelData.unitOfMeasureModifiers,
                    "Value",
                        channelData.value)
                    .substr(1);  //  Lop off the leading newline
        }
        
        resultList.add("# Dated data")
            << _response->data.datedChannelDataList.size();

        for( const auto& indexedDatedChannelData : _response->data.datedChannelDataList | boost::adaptors::indexed() )
        {
            const auto& datedChannelData = indexedDatedChannelData.value();

            const auto baseChannelDescription =
                datedChannelData.baseChannelData
                    ? FormattedList::of(
                        "Channel number",
                            datedChannelData.baseChannelData->channelNumber,
                        "Status",
                        static_cast<int>(datedChannelData.baseChannelData->status),
                        "UOM",
                            datedChannelData.baseChannelData->unitOfMeasure,
                        "Modifiers",
                            datedChannelData.baseChannelData->unitOfMeasureModifiers,
                        "Value",
                            datedChannelData.baseChannelData->value)
                        .substr(1)  //  Lop off the leading newline
                    : "(none)";

            resultList.add("Dated data " + std::to_string(indexedDatedChannelData.index()))
                << FormattedList::of(
                    "Channel number",
                        datedChannelData.channelData.channelNumber,
                    "Status",
                        static_cast<int>(datedChannelData.channelData.status),
                    "Timestamp",
                        datedChannelData.timeStamp,
                    "UOM",
                        datedChannelData.channelData.unitOfMeasure,
                    "Modifiers",
                        datedChannelData.channelData.unitOfMeasureModifiers,
                    "Value",
                        datedChannelData.channelData.value,
                    "Base channel",
                        baseChannelDescription)
                    .substr(1);  //  Lop off the leading newline
        }

        return resultList.toString().substr(1);  //  Lop off the leading newline
    }

    return "No message generated" + FormattedList::of(
            "User message ID", _userMessageId,
            "Channels", channelCount);
}
catch( const YukonErrorException& ex )
{
    _response = makeFailureResponse(now);
    
    throw;
}

}

namespace {

using namespace Cti;
using namespace Cti::Messaging::Rfn;
    
std::map<std::uint8_t, ChannelDataStatus> CmrStatusLookup {
    { 0, ChannelDataStatus::OK },
    { 1, ChannelDataStatus::PARTIAL_READ_TIMEOUT },
	{ 2, ChannelDataStatus::PARTIAL_READ_FAILURE },
	{ 3, ChannelDataStatus::PARTIAL_READ_LONG },
	{ 4, ChannelDataStatus::FULL_READ_PASSWORD_ERROR },
	{ 5, ChannelDataStatus::FULL_READ_BUSY_ERROR },
	{ 6, ChannelDataStatus::FULL_READ_TIMEOUT_ERROR },
	{ 7, ChannelDataStatus::FULL_READ_PROTOCOL_ERROR },
	{ 8, ChannelDataStatus::FULL_READ_NO_SUCH_CHANNEL_ERROR },
	{ 9, ChannelDataStatus::FULL_READ_READ_RESPONSE_ERROR_UNKNOWN },
	{ 10, ChannelDataStatus::FULL_READ_UNKNOWN },
	{ 11, ChannelDataStatus::FAILURE }};

//  Helper methods for creating the messaging objects
ChannelData makeChannelData(const RawChannel& rawChannel)
{
    ChannelData cd;

    cd.channelNumber = rawChannel.channelNumber;
    cd.status = mapFindOrDefault(CmrStatusLookup, rawChannel.status, ChannelDataStatus::FAILURE);
    cd.unitOfMeasure = rawChannel.unitOfMeasure.getName();
    cd.value = rawChannel.value;

    if( rawChannel.modifier1 )
    {
        boost::insert(cd.unitOfMeasureModifiers, rawChannel.modifier1->getModifierStrings());

        if( rawChannel.modifier2 )
        {
            boost::insert(cd.unitOfMeasureModifiers, rawChannel.modifier2->getModifierStrings());
        }
    }

    return cd;
}

DatedChannelData makeReferenceChannel(const RawChannel& rawChannel, CtiTime timestamp)
{
    return {
        makeChannelData(rawChannel),
        timestamp };
}

DatedChannelData makeCoincidentChannel(const DatedChannelData& referenceChannel, const RawChannel& rawChannel)
{
    return {
        makeChannelData(rawChannel),
        referenceChannel.timeStamp,
        referenceChannel.channelData };
}

//  Correlation helper to associate channels with their timestamp and coincident channels.
RfnMeterReadDataReplyMsg correlateRawChannels(const CtiTime now, const std::vector<RawChannel> rawChannels)
{
    RfnMeterReadDataReplyMsg response;

    response.replyType = RfnMeterReadingDataReplyType::OK;

    response.data.timeStamp = now;
    response.data.recordInterval = 0;  //  we don't have this - could someday use DynamicPaoInfo in RfnDeviceResultProcessor
    //response.data.rfnIdentifier  //  filled in by RfnDeviceResultProcessor

    struct CorrelatedChannel
    {
        RawChannel base;
        std::optional<CtiTime> timestamp;
        std::vector<RawChannel> coincidents;  //  Do we need to check that multiple coincidents don't occur at the same offset?
    };

    std::map<unsigned, CorrelatedChannel> correlated;

    //  Correlate the channels:
    for( auto& channel : rawChannels )
    {
        //  Is this a timestamp channel?
        if( channel.unitOfMeasure.isTime() )
        {
            //  Timestamp channels immediately follow their base channel
            const auto baseOffset = channel.channelNumber - 1;
            const auto timestamp = CtiTime(channel.value);

            if( auto existing = mapFindRef(correlated, baseOffset);
                ! existing )
            {
                CTILOG_DEBUG(dout, "No channel for timestamp, discarding" << FormattedList::of(
                    "Channel #", channel.channelNumber,
                    "Timestamp", timestamp));
            }
            else if( existing->timestamp )
            {
                CTILOG_DEBUG(dout, "Channel already has timestamp, discarding" << FormattedList::of(
                    "Channel #", existing->base.channelNumber,
                    "Existing", *existing->timestamp,
                    "Discarded", timestamp));
            }
            else
            {
                existing->timestamp = timestamp;
            }
        }
        //  Is this a coincident channel?
        else if( channel.modifier2 && channel.modifier2->getCoincidentOffset() )
        {
            //  Get the reference channel number from the coincident channel number minus its coincident offset
            const auto baseOffset = channel.channelNumber - channel.modifier2->getCoincidentOffset();

            std::set<std::string> modifiers;
            boost::set_union(
                channel.modifier1->getModifierStrings(),
                channel.modifier2->getModifierStrings(),
                std::inserter(modifiers, begin(modifiers)));

            //  Try to correlate this coincident with its base
            if( auto base = mapFindRef(correlated, baseOffset);
                ! base )
            {
                CTILOG_DEBUG(dout, "No channel for coincident, discarding" << FormattedList::of(
                    "Channel #", channel.channelNumber,
                    "Coincident", channel.modifier2->getCoincidentOffset(),
                    "Base offset", baseOffset,
                    "Unit", channel.unitOfMeasure.getName(),
                    "Modifiers", modifiers,
                    "Value", channel.value,
                    "Status", channel.status));
            }
            else
            {
                base->coincidents.push_back(channel);
            }
        }
        //  This is not a timestamp or a coincident, so it is a base channel.
        //    It might get a timestamp and coincidents added to it later.
        else
        {
            correlated.emplace(channel.channelNumber, CorrelatedChannel { channel });
        }
    }

    //  Form up the message components from the correlated channels
    for( const auto& [channelNumber, channel] : correlated )
    {
        if( channel.timestamp )
        {
            const auto referenceChannel = makeReferenceChannel(channel.base, *channel.timestamp);

            response.data.datedChannelDataList.emplace_back(referenceChannel);

            for( const auto& coincident : channel.coincidents )
            {
                response.data.datedChannelDataList.emplace_back(
                    makeCoincidentChannel(referenceChannel, coincident));
            }
        }
        else
        {
            response.data.channelDataList.emplace_back(
                makeChannelData(channel.base));
        }
    }

    return response;
}

}
