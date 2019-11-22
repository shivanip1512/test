#include "precompiled.h"

#include "RfnMeter.h"

#include "rfn_identifier.h"

#include "CParms.h"

#include "logger.h"

#include "std_helper.h"
#include "random_generator.h"

#include <boost/serialization/map.hpp>

#include <map>
#include <random>
#include <time.h>

namespace Cti::Simulator {

namespace {
RandomGenerator<unsigned> idGenerator;
std::map<RfnIdentifier, std::map<unsigned, std::string>> meterProgrammingRequests;

std::map<RfnIdentifier, std::string> configurationIds;

const std::vector<std::string> remoteGuids {
    //low-mid-high-and-version (final nibble)
    //1b21dd213814000 - 100-ns counts between Gregorian epoch and Unix epoch
    //719,528 days between Gregorian epoch and Unix epoch
    // 1 ms = 10,000 100ns increments
    // 1 s = 10,000,000 100 ns increments
    //  d952eb22-0a3e-11ea = 1ea0a3ed952eb22 - epoch = 37EC6CC5D1AB22 = 1574107563 856 1570 = 2019-11-18 20:06:03
    "d952eb22-0a3e-11ea-8d71-362b9e155667",  //  1574107563 856 1570 = 2019-11-18 20:06:03
    "d952ee74-0a4e-11ea-8d71-362b9e155667",  //  1574114435 803 8306 = 2019-11-18 22:00:35
    "d952efdc-0a5e-11ea-8d71-362b9e155667",  //  1574121307 751 5042 = 2019-11-18 23:55:07
    "d952f112-0a6e-11ea-8d71-362b9e155667",  //  1574128179 699 1778 = 2019-11-19 01:49:39
    "d952f23e-0a7e-11ea-8d71-362b9e155667",  //  1574135051 646 8514 = 2019-11-19 03:44:11
    "d952f36a-0a8e-11ea-8d71-362b9e155667",  //  1574141923 594 5250 = 2019-11-19 05:38:43
    "d952f68a-0a9e-11ea-8d71-362b9e155667",  //  1574148795 542 1986 = 2019-11-19 07:33:15
    "d952f7d4-0aae-11ea-8d71-362b9e155667",  //  1574155667 489 8722 = 2019-11-19 09:27:47
    "d952f90a-0abe-11ea-8d71-362b9e155667",  //  1574162539 437 5458 = 2019-11-19 11:22:19
    "d952fa40-0ace-11ea-8d71-362b9e155667",  //  1574169411 385 2194 = 2019-11-19 13:16:51
};

const std::vector<std::string> yukonGuids{
    "EE8358B0-92B7-4603-A148-A06E5489D4C7",
    "70A9331E-BA48-4F7A-ADDD-525CAD8FD578",
    "0474477D-1D69-459D-A8E8-65F71EC46923",
    "B9D7E497-5907-42E3-980B-D3F8A47F8D11",
    "770E5A24-A3AF-4985-B15F-F40718A4F183",
};

constexpr char Source_Yukon { 'R' };

const std::vector<char> sources { Source_Yukon, 'P', 'N', 'U', 'X' };

}

namespace streaming_metrics {

using metrics = std::initializer_list<unsigned short>;
// 1, 2, 100, 101, 102 RFN_530S4AD(PaoType.RFN530S4EAX,  //S4-AD
// 1, 2, 100, 101, 102 RFN_530S4AR(PaoType.RFN530S4EAXR,  //S4-AR
// 1, 2, 100, 101, 102 RFN_530S4AT(PaoType.RFN530S4EAXR,  //S4-AT
const metrics streaming_s4_a = { 1, 2, 3, 100, 101, 102 };

// 1, 2, 23, 43, 100, 101, 102 RFN_530S4RD(PaoType.RFN530S4ERX,  //S4-RD
// 1, 2, 23, 43, 100, 101, 102 RFN_530S4RR(PaoType.RFN530S4ERXR,  //S4-RR
// 1, 2, 23, 43, 100, 101, 102 RFN_530S4RT(PaoType.RFN530S4ERXR,  //S4-RT
const metrics streaming_s4_r = { 1, 2, 23, 43, 100, 101, 102 };

// 1, 2, 32, 33, 49, 50, 80, 100, 101, 102 RFN_430SL0(PaoType.RFN430SL0,  //Sentinel-L0
// 1, 2, 32, 33, 49, 50, 80, 100, 101, 102 RFN_430SL1(PaoType.RFN430SL1,  //Sentinel-L1
// 1, 2, 32, 33, 49, 50, 80, 100, 101, 102 RFN_430SL2(PaoType.RFN430SL2,  //Sentinel-L2
// 1, 2, 32, 33, 49, 50, 80, 100, 101, 102 RFN_430SL3(PaoType.RFN430SL3,  //Sentinel-L3
// 1, 2, 32, 33, 49, 50, 80, 100, 101, 102 RFN_430SL4(PaoType.RFN430SL4,  //Sentinel-L4
const metrics streaming_sentinel = { 1, 2, 32, 33, 49, 50, 80, 100, 101, 102, 200, 201, 202 };

// 1, 2, 5, 115 RFN_410CL(PaoType.RFN410CL,   //C1SX
// 1, 2, 5, 115 RFN_410FL(PaoType.RFN410FL,  //FocuskWh
// 1, 2, 5, 115 RFN_420CL(PaoType.RFN420CL,   //C2SX
// 1, 2, 5, 115 RFN_510FL(PaoType.RFN510FL,  //FocuskWh-500
const metrics streaming_centron   = { 1, 2, 5, 115 };
const metrics streaming_focus_kwh = { 1, 2, 5, 115 };

// 1, 2, 5, 115 RFN_410FD(PaoType.RFN410FD,  //FocusAXD-SD
// 1, 2, 5, 115 RFN_410FX(PaoType.RFN410FX,  //FocusAXR
// 1, 2, 5, 115 RFN_420FRD(PaoType.RFN420FRD,  //FocusAXR-SD
// 1, 2, 5, 115 RFN_420FX(PaoType.RFN420FX,  //FocusAXD
const metrics streaming_focus_ax_400 = { 1, 2, 5, 115 };

// 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 RFN_520FRXD_SD(PaoType.RFN520FRXD,  //FocusRXD-SD-500
// 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 RFN_520FRXR(PaoType.RFN520FRX,  //FocusRXR-500
// 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 RFN_520FRXR_SD(PaoType.RFN520FRXD,  //FocusRXR-SD-500
// 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 RFN_520FRXT(PaoType.RFN520FRX,  //FocusRXT-500
// 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 RFN_520FRXT_SD(PaoType.RFN520FRXD,  //FocusRXT-SD-500
const metrics streaming_focus_rx = { 1, 2, 5, 32, 33, 23, 49, 50, 43, 115, 100, 101, 102, 119, 120, 121 };

// 1, 2, 5, 32, 33, 49, 50, 43, 100, 101, 102 RFN_430A3D(PaoType.RFN430A3D,  //Elster A3D
// 1, 2, 5, 32, 33, 49, 50, 43, 100, 101, 102 RFN_430A3T(PaoType.RFN430A3T,  //Elster A3T
const metrics streaming_elster_a3dt = { 1, 2, 5, 6, 32, 33, 49, 50, 43, 162, 163, 164, 100, 101, 102 };

// 1, 2, 5, 32, 33, 49, 50, 43, 80/81?, 80, 100, 101, 102 RFN_430A3K(PaoType.RFN430A3K,  //Elster A3K
// 1, 2, 5, 32, 33, 49, 50, 43, 80/81?, 80, 100, 101, 102 RFN_430A3R(PaoType.RFN430A3R,  //Elster A3R
const metrics streaming_elster_a3kr = { 1, 2, 5, 6, 32, 33, 49, 50, 43, 162, 163, 164, 80, 81, 100, 101, 102 };

// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXD(PaoType.RFN520FAX,  //Focus AXD-500
// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXD_SD(PaoType.RFN520FAXD,  //Focus AXD-SD-500
// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXR(PaoType.RFN520FAX,  //Focus AXR-500
// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXR_SD(PaoType.RFN520FAXD,  //FocusAXR-SD-500
// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXT(PaoType.RFN520FAX,  //Focus AXT-500
// 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 RFN_520FAXT_SD(PaoType.RFN520FAXD,  //FocusAXT-SD-500
const metrics streaming_focus_ax_500 = { 1, 2, 5, 49, 50, 43, 113, 100, 101, 102, 119, 120, 121 };

// 1, 2, 5, 6, 32, 33, 23, 49, 50, 43, 100, 101, 102, 119, 120, 121 RFN_530S4X(PaoType.RFN530S4X,  //E650
const metrics streaming_s4_x = { 1, 2, 5, 6, 32, 33, 23, 24, 49, 50, 43, 44, 100, 101, 102, 119, 120, 121 };

const std::map<std::string, std::map<std::string, const metrics>> perType {
    { "ITRN", {
        { "C1SX",    streaming_centron },
        { "C2SX",    streaming_centron },
        { "C2SX-SD", streaming_centron },
    }},
    { "LGYR", {
        { "FocuskWh",        streaming_focus_kwh },

        { "FocusAXD",        streaming_focus_ax_400 },
        { "FocusAXD-SD",     streaming_focus_ax_400 },
        { "FocusAXR",        streaming_focus_ax_400 },
        { "FocusAXR-SD",     streaming_focus_ax_400 },

        { "FocusAXD-500",    streaming_focus_ax_500 },
        { "FocusAXD-SD-500", streaming_focus_ax_500 },

        { "E650",            streaming_s4_x }
    }},
    { "EE", {
        { "A3D", streaming_elster_a3dt },
        { "A3T", streaming_elster_a3dt },

        { "A3K", streaming_elster_a3kr },
        { "A3R", streaming_elster_a3kr }
    }},
    { "SCH", {
        { "SENTINEL-L0", streaming_sentinel },
        { "SENTINEL-L1", streaming_sentinel },
        { "SENTINEL-L2", streaming_sentinel },
        { "SENTINEL-L3", streaming_sentinel },
        { "SENTINEL-L4", streaming_sentinel }
    }},
};


/* For the RFN_520 and RFN_530 meters below, there are cases where multiple manufacturer/model combinations
* map to the same pao type, like the 'S4-AT' and 'S4-AR' model strings that both map to the PaoType 'RFN530S4EAXR'.
* This is intentional- multiple meter models are functionally the same in Yukon, this should not be changed. */
/*
RFN_510FL(PaoType.RFN510FL, "LGYR", "FocuskWh-500"),
RFN_520FAXD(PaoType.RFN520FAX, "LGYR", "FocusAXD-500"),
RFN_520FAXT(PaoType.RFN520FAX, "LGYR", "FocusAXT-500"),
RFN_520FAXR(PaoType.RFN520FAX, "LGYR", "FocusAXR-500"),
RFN_520FRXD(PaoType.RFN520FRX, "LGYR", "FocusRXD-500"),
RFN_520FRXT(PaoType.RFN520FRX, "LGYR", "FocusRXT-500"),
RFN_520FRXR(PaoType.RFN520FRX, "LGYR", "FocusRXR-500"),
RFN_520FAXD_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXD-SD-500"),
RFN_520FAXT_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXT-SD-500"),
RFN_520FAXR_SD(PaoType.RFN520FAXD, "LGYR", "FocusAXR-SD-500"),
RFN_520FRXD_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXD-SD-500"),
RFN_520FRXT_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXT-SD-500"),
RFN_520FRXR_SD(PaoType.RFN520FRXD, "LGYR", "FocusRXR-SD-500"),

//this manufacturer value doesn't actually exist yet.
RFN_530FAX(PaoType.RFN530FAX, "LGYR", "FocusAXT-530"),
RFN_530FRX(PaoType.RFN530FRX, "LGYR", "FocusAXR-530"),

RFN_530S4X(PaoType.RFN530S4X, "LGYR", "E650"),
RFN_530S4AD(PaoType.RFN530S4EAX, "LGYR", "S4-AD"),
RFN_530S4AT(PaoType.RFN530S4EAXR, "LGYR", "S4-AT"),
RFN_530S4AR(PaoType.RFN530S4EAXR, "LGYR", "S4-AR"),
RFN_530S4RD(PaoType.RFN530S4ERX, "LGYR", "S4-RD"),
RFN_530S4RT(PaoType.RFN530S4ERXR, "LGYR", "S4-RT"),
RFN_530S4RR(PaoType.RFN530S4ERXR, "LGYR", "S4-RR"),
*/


}

std::vector<unsigned char> DataStreamingRead(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId);
std::vector<unsigned char> DataStreamingWrite(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId);
std::vector<unsigned char> GetMeterProgrammingConfiguration(const RfnIdentifier & rfnId);

auto RfnMeter::doChannelManagerRequest(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId) -> Bytes
{
    if( ! request.empty() )
    {
        switch( request[0] )
        {
            case 0x84:
            {
                return DataStreamingRead(request, rfnId);
            }
            case 0x86:
            {
                return DataStreamingWrite(request, rfnId);
            }
            case 0x91:
            {
                return GetMeterProgrammingConfiguration(rfnId);
            }
        }
    }
    return {};
}

auto RfnMeter::processReply(const e2edt_reply_packet& reply, const RfnIdentifier& rfnId) -> std::unique_ptr<e2edt_request_packet>
{
    if( auto requests = mapFindRef(meterProgrammingRequests, rfnId) )
    {
        if( auto existingRequestPath = mapFind(*requests, reply.token) )
        {
            if( reply.block && reply.block->more )
            {
                auto request = std::make_unique<e2edt_request_packet>();

                request->id = idGenerator();
                request->confirmable = true;
                request->method = Protocols::Coap::RequestMethod::Get;
                request->block = reply.block;
                request->block->num++;

                request->path = *existingRequestPath;
                request->token = idGenerator();

                return request;
            }
            else
            {
                
            }
        }
    }

    return nullptr;
}

auto RfnMeter::processChannelManagerPost(const e2edt_request_packet& post_request, const RfnIdentifier& rfnId) -> std::unique_ptr<e2edt_request_packet>
{
    if( ! post_request.payload.empty() )
    {
        switch( post_request.payload[0] )
        {
            case 0x90:
                auto request = std::make_unique<e2edt_request_packet>();

                request->id = idGenerator();
                request->confirmable = true;
                request->method = Protocols::Coap::RequestMethod::Get;
                        
                if( const auto meterProgramInfo = ParseSetMeterProgram(post_request.payload, rfnId);
                    meterProgramInfo )
                {
                    request->path = meterProgramInfo->path;
                    request->token = idGenerator();

                    meterProgrammingRequests[rfnId][request->token] = request->path;

                    return request;
                }
        }
    }

    return nullptr;
}

auto RfnMeter::ParseSetMeterProgram(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId) -> std::optional<path_size>
{
    auto pos = 1;
    const auto end = request.size();

    if( request[pos++] != 2 )
    {
        return std::nullopt;  //  error, must have two TLVs
    }
    
    std::optional<int> size;
    std::optional<std::string> uri;

    while( pos < end )
    {
        if( end - pos < 3 )
        {
            return std::nullopt;  //  error, TLV header too small
        }
        auto type = request[pos];
        auto len = ntohs(*reinterpret_cast<const unsigned short *>(request.data() + pos + 1));

        pos += 3;

        if( pos + len >= end )
        {
            return std::nullopt;  //  error, buffer too small
        }

        switch( type )
        {
        case 0x01:
            if( len != 4 )
            {
                return std::nullopt;  //  error, size must be 4 bytes
            }
            size = ntohl(*reinterpret_cast<const u_long*>(request.data() + pos));
            break;
        case 0x02:
            uri = std::string(request.data() + pos, request.data() + pos + len);
            break;
        }
        pos += len;
    }

    if( ! size || ! uri )
    {
        return std::nullopt;  //  error, missing one of the two required parameters
    }

    return path_size { *uri, *size };
}

struct metric_response
{
    bool enabled;
    struct channel
    {
        unsigned metricId;
        unsigned interval;
        unsigned error;
        unsigned enabled;
    };

    std::vector<channel> metrics;
};

const std::array<std::uint8_t, 5> intervals { 1, 3, 5, 15, 30 };

RandomGenerator<size_t> random_interval_index { intervals.size() - 1 };  //  Constrain the new index to be a 0-based index that excludes the last element

metric_response mangleResponse(metric_response contents, double mangleFactor)
{
    unsigned long long steve = mangleFactor * pow( 2, contents.metrics.size() + 2 );  //  up to 61 metrics allowed for this method - as of 2016, we have 16 max (see streaming_s4_x above)

    CTILOG_INFO(dout, "mangler = " << steve);

    if( steve & 0x01 && ! contents.metrics.empty() )
    {
        //  select a random new interval
        auto index = random_interval_index();
        
        if( intervals[index] == contents.metrics[0].interval )
        {
            //  ... but if it's the same as the original, use the next one
            ++index %= intervals.size();
        }

        for( auto& metric : contents.metrics )
        {
            metric.interval = intervals[index];
        }
    }
    int enabledChannels = 0;
    for( auto& metric : contents.metrics )
    {
        steve >>= 1;
        if( steve & 0x01 )
        {
            if( metric.enabled )
            {
                metric.enabled = false;
            }
            else if( enabledChannels++ < 6 )
            {
                metric.enabled = true;
            }
        }
    }

    return contents;
}


extern std::mt19937_64 gen;
extern std::uniform_real_distribution<double> dist;
std::uniform_int_distribution<unsigned> selector;

std::string GenerateConfigurationId(const RfnIdentifier & rfnId)
{
    const auto index = selector(gen);

    auto source = sources[index % sources.size()];

    return source + (source == Source_Yukon
                         ? yukonGuids [index % yukonGuids.size()]
                         : remoteGuids[index % remoteGuids.size()]);
}

std::vector<unsigned char> GetMeterProgrammingConfiguration(const RfnIdentifier & rfnId)
{
    auto configurationId = mapFindOrCompute(configurationIds, rfnId, GenerateConfigurationId);

    //  Success response
    //  TODO - add support for failure responses
    //  TODO - track individual meter state
    std::vector<unsigned char> response { 0x92, 0x00, 0x00, 0x01, 0x03 };

    response.push_back(configurationId.length());
    response.insert(response.end(), configurationId.begin(), configurationId.end());

    return response;
}

std::vector<unsigned char> makeDataStreamingResponse(const unsigned char responseCode, const metric_response& original)
{
    //  Response format:
    //  0x87,       //  command code
    //      0x03,  //  number of metrics
    //      0x01,  //  data streaming on/off
    //      0x00, 0x05,  //  metric ID 1
    //      0x01,        //  metric ID 1 enable/disable
    //      0x05,        //  metric ID 1 interval
    //      0x00,        //  metric ID 1 status
    //      0x00, 0x73,  //  metric ID 2
    //      0x00,        //  metric ID 2 enable/disable
    //      0x0f,        //  metric ID 2 interval
    //      0x00,        //  metric ID 2 status
    //      0x00, 0x53,  //  metric ID 3
    //      0x01,        //  metric ID 3 enable/disable
    //      0x1e,        //  metric ID 3 interval
    //      0x00,        //  metric ID 3 status
    //      0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

    std::vector<unsigned char> response { responseCode };

    const auto mangleChance = gConfigParms.getValueAsDouble("SIMULATOR_RFN_DATA_STREAMING_CONFIG_MANGLE_CHANCE");
    const auto mangleHappen = dist(gen);

    const auto& contents = 
            (mangleChance && mangleHappen < mangleChance) 
                ? mangleResponse(original, mangleHappen / mangleChance)  //  Normalize to a 0.0-1.0 number again
                : original;

    const auto disableChance = gConfigParms.getValueAsDouble("SIMULATOR_RFN_DATA_STREAMING_DISABLE_CHANCE");
    const auto disableHappen = dist(gen) < disableChance;

    response.push_back(contents.metrics.size());
    response.push_back(contents.enabled && ! disableHappen);

    const auto channelErrorChance = gConfigParms.getValueAsDouble("SIMULATOR_RFN_DATA_STREAMING_CHANNEL_ERROR_CHANCE");
    const auto channelErrorHappen = dist(gen) < channelErrorChance;

    for( const auto channel : contents.metrics )
    {
        response.push_back(channel.metricId >> 8);
        response.push_back(channel.metricId);
        response.push_back(channel.enabled);
        response.push_back(channel.interval);
        response.push_back(channelErrorHappen ? 5 : channel.error);  //  return a ChannelNotSupported error
    }

    //  Sequence number
    response.push_back(0xde);
    response.push_back(0xad);
    response.push_back(0xbe);
    response.push_back(0xef);

    return response;
}

std::vector<unsigned char> DataStreamingRead(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId)
{
    streaming_metrics::metrics modelMetrics;

    const auto streamingEnabled = gConfigParms.isTrue("SIMULATOR_RFN_DATA_STREAMING_READ_STREAMING_ENABLED", true);
    const auto channelsEnabled  = gConfigParms.isTrue("SIMULATOR_RFN_DATA_STREAMING_READ_CHANNELS_ENABLED", true);
    const auto interval         = gConfigParms.getValueAsULong("SIMULATOR_RFN_DATA_STREAMING_READ_INTERVAL", 5);
    const auto status = 0;  //  MetricStatus::OK

    metric_response response { streamingEnabled };

    if( const auto models = Cti::mapFindRef(streaming_metrics::perType, rfnId.manufacturer) )
    {
        if( const auto metrics = Cti::mapFindRef(*models, rfnId.model) )
        {
            for( const auto metricId : *metrics )
            {
                response.metrics.push_back(metric_response::channel{metricId, interval, status, channelsEnabled});
            }
        }
    }

    return makeDataStreamingResponse(0x85, response);
}

std::vector<unsigned char> DataStreamingWrite(const std::vector<unsigned char>& request, const RfnIdentifier & rfnId)
{
    //  Request format:
    //  0x86,  //  command code
    //      0x01,  //  number of metrics
    //      0x01,  //  data streaming ON
    //      0x00, 0x05,  //  metric ID 1
    //      0x01,        //  metric ID 1 enable/disable
    //      0x05         //  metric ID 1 interval

    std::map<unsigned, metric_response::channel> requestedChannels;
    metric_response response { true };

    if( request.size() >= 3 )
    {
        const auto metricCount = request[1];
        response.enabled       = request[2];
    
        if( request.size() >= metricCount * 4 + 3 )
        {
            for( size_t i = 3; i < request.size(); i += 4 )
            {
                const unsigned metricId = request[i] << 8 | request[i+1];
                const bool enabled = request[i+2];
                const auto interval = request[i+3];

                if( enabled )
                {
                    metric_response::channel requestedChannel = {metricId};

                    requestedChannel.enabled  = enabled;
                    requestedChannel.interval = interval;
                    requestedChannel.error    = 0;

                    requestedChannels.emplace(metricId, requestedChannel);
                }
            }
        }
    }

    streaming_metrics::metrics modelMetrics;

    const unsigned default_interval = 
            requestedChannels.empty()
                ? 5
                : requestedChannels.begin()->second.interval;

    if( const auto models = Cti::mapFindRef(streaming_metrics::perType, rfnId.manufacturer) )
    {
        if( const auto metrics = Cti::mapFindRef(*models, rfnId.model) )
        {
            for( const auto metricId : *metrics )
            {
                if( auto channel = Cti::mapFind(requestedChannels, metricId) )
                {
                    response.metrics.push_back(metric_response::channel{metricId, channel->interval, 0, channel->enabled});
                }
                else
                {
                    response.metrics.push_back(metric_response::channel{metricId, default_interval, 0, false});
                }
            }
        }
    }

    return makeDataStreamingResponse(0x87, response);
}

}