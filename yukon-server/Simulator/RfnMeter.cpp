#include "precompiled.h"

#include "RfnMeter.h"

#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataIndicationMsg.h"

#include "e2e_packet.h"

#include "CParms.h"

#include "logger.h"

#include "std_helper.h"
#include "random_generator.h"

#include "NodeInfo.h"

#include <map>
#include <random>
#include <optional>
#include <thread>
#include <tuple>
#include <time.h>

namespace Cti::Simulator {

namespace {
RandomGenerator<unsigned> idGenerator;

struct MeterProgrammingRequest
{
    unsigned initialToken;
    unsigned currentToken;
    std::string path;
};

std::map<RfnIdentifier, MeterProgrammingRequest> meterProgrammingRequests;

std::map<RfnIdentifier, NodeInfo> nodeInfo;

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

using Bytes = std::vector<unsigned char>;

NodeInfo getNodeInfo(const RfnIdentifier& rfnId)
{
    return mapFindOrCompute(nodeInfo, rfnId, NodeInfo::of);
}

void doBadRequest(const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& originalRequest)
{
    e2edt_reply_packet reply{};

    reply.token = request.token;
    reply.status = Protocols::Coap::ResponseCode::BadRequest;

    e2eReplySender(originalRequest, reply);
}

void doChannelManagerRequest(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);
void processChannelManagerPost(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& post_request, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);
void doBulkMessageRequest(const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);

void RfnMeter::processRequest(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& requestMsg)
{
    using ASIDs = Messaging::Rfn::ApplicationServiceIdentifiers;

    switch( request.method )
    {
        default:
        {
            CTILOG_INFO(dout, "Received unknown method (" << static_cast<int>(request.method) << ") for rfnIdentifier " << requestMsg.rfnIdentifier);
            return;
        }
        case Protocols::Coap::RequestMethod::Get:
        {
            switch( const auto asid = requestMsg.applicationServiceId )
            {
                default:
                {
                    CTILOG_WARN(dout, "Received unhandled ASID (" << static_cast<int>(asid) << ") for rfnIdentifier " << requestMsg.rfnIdentifier);
                    doBadRequest(e2eReplySender, request, requestMsg);
                    return;
                }
                case ASIDs::BulkMessageHandler:
                {
                    doBulkMessageRequest(e2eReplySender, request, requestMsg);
                    return;
                }
                case ASIDs::ChannelManager:
                {
                    doChannelManagerRequest(e2eRequestSender, e2eReplySender, request, requestMsg);
                    return;
                }
            }
        }
        case Protocols::Coap::RequestMethod::Post:
        {
            //  The only POST we process at present is the Set Meter Configuration request, which results in a GET request back to Yukon.
            switch( const auto asid = requestMsg.applicationServiceId; 
                    asid )
            {
                default:
                {
                    CTILOG_WARN(dout, "Received unhandled ASID (" << static_cast<int>(asid) << ") for rfnIdentifier " << requestMsg.rfnIdentifier);
                    doBadRequest(e2eReplySender, request, requestMsg);
                    return;
                }
                case ASIDs::ChannelManager:
                {
                    processChannelManagerPost(e2eRequestSender, e2eReplySender, request, requestMsg);
                    return;
                }
            }
        }
    }
}

Bytes GetMeterProgrammingConfiguration(const RfnIdentifier & rfnId);

void RfnMeter::processReply(const E2eRequestSender e2eRequestSender, const e2edt_reply_packet& reply, const Messaging::Rfn::E2eDataRequestMsg& originalRequest)
{
    auto itr = meterProgrammingRequests.find(originalRequest.rfnIdentifier);

    if( itr == meterProgrammingRequests.end() )
    {
        CTILOG_WARN(dout, "No active meter programming request for " << originalRequest.rfnIdentifier);
        return;
    }

    auto& programmingRequest = itr->second;

    if( programmingRequest.currentToken != reply.token )
    {
        CTILOG_WARN(dout, "Received unexpected token " << FormattedList::of(
            "Path", programmingRequest.path,
            "Initial token", programmingRequest.initialToken,
            "Current token", programmingRequest.currentToken,
            "Reply token", reply.token));

        return;
    }

    if( reply.status != Protocols::Coap::ResponseCode::Content )
    {
        CTILOG_WARN(dout, "Request failed, aborting programming request");
        meterProgrammingRequests.erase(itr);
        return;
    }

    if( reply.block && reply.block->more )
    {
        e2edt_request_packet newRequest;

        newRequest.id = idGenerator();
        newRequest.confirmable = true;
        newRequest.method = Protocols::Coap::RequestMethod::Get;
        newRequest.block = reply.block;
        newRequest.block->num++;

        newRequest.path = programmingRequest.path;
        newRequest.token = idGenerator();

        programmingRequest.currentToken = newRequest.token;

        e2eRequestSender(originalRequest, newRequest);

        return;
    }

    //  request is complete, store off configurationId and issue response

    const auto guid = programmingRequest.path.substr(
        static_cast<std::string::size_type>(
            programmingRequest.path.find_last_of('/') + 1U));

    getNodeInfo(originalRequest.rfnIdentifier).setConfigurationId(GuidPrefix::Yukon, guid);

    e2edt_request_packet newRequest;

    newRequest.id = idGenerator();
    newRequest.confirmable = false;
    newRequest.method = Protocols::Coap::RequestMethod::Post;

    newRequest.token = programmingRequest.initialToken;

    meterProgrammingRequests.erase(itr);

    newRequest.payload = GetMeterProgrammingConfiguration(originalRequest.rfnIdentifier);

    e2eRequestSender(originalRequest, newRequest);
}

Bytes DataStreamingRead (const Bytes& request, const RfnIdentifier & rfnId);
Bytes DataStreamingWrite(const Bytes& request, const RfnIdentifier & rfnId);

void doChannelManagerRequest(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& originalRequest)
{
    if( request.payload.empty() )
    {
        return;
    }

    e2edt_reply_packet reply {};

    switch( request.payload[0] )
    {
        default:
        {
            doBadRequest(e2eReplySender, request, originalRequest);
            return;
        }
        case 0x84:
        {
            reply.payload = DataStreamingRead(request.payload, originalRequest.rfnIdentifier);
            reply.token = request.token;
            reply.status = Protocols::Coap::ResponseCode::Content;
            break;
        }
        case 0x86:
        {
            reply.payload = DataStreamingWrite(request.payload, originalRequest.rfnIdentifier);
            reply.token = request.token;
            reply.status = Protocols::Coap::ResponseCode::Content;
            break;
        }
        case 0x91:
        {
            reply.status = Protocols::Coap::ResponseCode::EmptyMessage;

            //  Delay the actual data reply by 4 seconds (arbitrary)
            std::thread([e2eRequestSender, originalRequest, token=request.token]() {
                Sleep(4000);
                
                e2edt_request_packet delayedRequest;

                delayedRequest.id = idGenerator();
                delayedRequest.method = Protocols::Coap::RequestMethod::Post;
                delayedRequest.token = token;
                delayedRequest.confirmable = false;
                delayedRequest.payload = GetMeterProgrammingConfiguration(originalRequest.rfnIdentifier);

                e2eRequestSender(originalRequest, delayedRequest);
            }).detach();

            break;
        }
    }

    reply.id = request.id;

    e2eReplySender(originalRequest, reply);
}

void doBulkMessageRequest(const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& originalRequest)
{
    if( request.payload.empty() )
    {
        return;
    }

    e2edt_reply_packet reply{};

    switch( request.payload[0] )
    {
        default:
        {
            doBadRequest(e2eReplySender, request, originalRequest);
            return;
        }
/*        case 0x1d:
        {
            reply.payload = DataStreamingRead(request.payload, requestMsg.rfnIdentifier);
            reply.token = request.token;
            reply.status = Protocols::Coap::ResponseCode::Content;
            break;
        }*/
    }

    reply.id = request.id;

    e2eReplySender(originalRequest, reply);
}

auto ParseSetMeterProgram(const Bytes& request, const RfnIdentifier & rfnId) -> std::optional<std::tuple<std::string, unsigned>>;

void processChannelManagerPost(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& post_request, const Messaging::Rfn::E2eDataRequestMsg& originalRequest)
{
    if( ! post_request.payload.empty() )
    {
        switch( post_request.payload[0] )
        {
            default:
            {
                doBadRequest(e2eReplySender, post_request, originalRequest);
                return;
            }
            case 0x90:
            {
                if( const auto pathSize = ParseSetMeterProgram(post_request.payload, originalRequest.rfnIdentifier) )
                {
                    const auto [path, size] = *pathSize;

                    auto itr = meterProgrammingRequests.find(originalRequest.rfnIdentifier);

                    if( itr != meterProgrammingRequests.end() )
                    {
                        if( itr->second.path == path )
                        {
                            CTILOG_INFO(dout, "Received duplicate path request, ignoring" << FormattedList::of(
                                "Device", originalRequest.rfnIdentifier,
                                "Path", path));

                            return;
                        }

                        CTILOG_INFO(dout, "Replacing existing meter programming request" << FormattedList::of(
                            "Device", originalRequest.rfnIdentifier,
                            "Existing path", itr->second.path,
                            "New path", path));

                        itr->second.path = path;
                        itr->second.initialToken = post_request.token;
                    }

                    e2edt_request_packet newRequest;

                    newRequest.id = idGenerator();
                    newRequest.confirmable = true;
                    newRequest.method = Protocols::Coap::RequestMethod::Get;
                    newRequest.path = path;
                    newRequest.token = idGenerator();

                    itr->second.currentToken = newRequest.token;

                    e2eRequestSender(originalRequest, newRequest);

                    return;
                }
            }
        }
    }
}

auto ParseSetMeterProgram(const Bytes& request, const RfnIdentifier & rfnId) -> std::optional<std::tuple<std::string, unsigned>>
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

    return { { *uri, *size } };
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

Bytes GetMeterProgrammingConfiguration(const RfnIdentifier & rfnId)
{
    const auto configurationId = getNodeInfo(rfnId).getConfigurationId();

    //  Success response
    //  TODO - add support for failure responses
    //  TODO - track individual meter state
    Bytes response { 0x92, 0x00, 0x00, 0x01, 0x03 };

    response.push_back(configurationId.length());
    response.insert(response.end(), configurationId.begin(), configurationId.end());

    return response;
}

Bytes makeDataStreamingResponse(const unsigned char responseCode, const metric_response& original)
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

    Bytes response { responseCode };

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

Bytes DataStreamingRead(const Bytes& request, const RfnIdentifier & rfnId)
{
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

Bytes DataStreamingWrite(const Bytes& request, const RfnIdentifier & rfnId)
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