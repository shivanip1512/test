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

#include <boost/range/algorithm_ext/insert.hpp>

#include <map>
#include <random>
#include <optional>
#include <thread>
#include <tuple>
#include <time.h>

using ASIDs = Cti::Messaging::Rfn::ApplicationServiceIdentifiers;
namespace Coap = Cti::Protocols::Coap;

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

Bytes asBytes(const char* hex_string);

NodeInfo getNodeInfo(const RfnIdentifier& rfnId)
{
    return mapFindOrCompute(nodeInfo, rfnId, NodeInfo::of);
}


using PayloadOrStatus = std::variant<Bytes, Coap::ResponseCode>;
using ReplySender = std::function<void(PayloadOrStatus&&)>;
using DelayedReplySender = std::function<void(Bytes&&)>;

void processGetRequest(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& request, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceId);
void processPostRequest(const E2eRequestSender e2eRequestSender, const ReplySender e2eReplySender, const Bytes& request, const unsigned token, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceId);

void RfnMeter::processRequest(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceId)
{
    const auto sendReply = [&request, e2eReplySender](PayloadOrStatus&& payloadOrStatus) {
        e2edt_reply_packet reply;

        reply.id = request.id;

        if( auto rc = std::get_if<Coap::ResponseCode>(&payloadOrStatus) )
        {
            if( reply.status = *rc;
                reply.status != Coap::ResponseCode::EmptyMessage )
            {
                reply.token = request.token;
            }
        }
        else
        {
            reply.status = Protocols::Coap::ResponseCode::Content;
            reply.token = request.token;
            reply.payload = std::move(std::get<Bytes>(payloadOrStatus));
        }

        e2eReplySender(reply);
    };

    const auto sendDelayedReply = [token=request.token, e2eRequestSender](Bytes&& payload) {
        e2edt_request_packet delayedReply;

        delayedReply.id = idGenerator();
        delayedReply.method = Protocols::Coap::RequestMethod::Post;
        delayedReply.token = token;
        delayedReply.confirmable = false;
        delayedReply.payload = std::move(payload);

        //  Delay the actual data reply by 4 seconds (arbitrary)
        std::thread([e2eRequestSender](e2edt_request_packet&& delayedReply) {
            Sleep(4000);

            e2eRequestSender(delayedReply);
        }, std::move(delayedReply)).detach();
    };

    switch( request.method )
    {
        default:
        {
            CTILOG_INFO(dout, "Received unknown method (" << static_cast<int>(request.method) << ") for rfnIdentifier " << rfnIdentifier);
            return;
        }
        case Protocols::Coap::RequestMethod::Get:
        {
            processGetRequest(sendReply, sendDelayedReply, request.payload, rfnIdentifier, applicationServiceId);
            return;
        }
        case Protocols::Coap::RequestMethod::Post:
        {
            processPostRequest(e2eRequestSender, sendReply, request.payload, request.token, rfnIdentifier, applicationServiceId);
        }
    }
}

void doChannelManagerRequest(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& request, const RfnIdentifier rfnIdentifier);
void doBulkMessageRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier);
void doEventManagerRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier);
void doHubMeterRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier);

void processGetRequest(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& request, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceId)
{
    switch( applicationServiceId )
    {
        default:
        {
            CTILOG_WARN(dout, "Received unhandled ASID (" << static_cast<int>(applicationServiceId) << ") for rfnIdentifier " << rfnIdentifier);
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case ASIDs::BulkMessageHandler:
        {
            doBulkMessageRequest(sendReply, request, rfnIdentifier);
            return;
        }
        case ASIDs::EventManager:
        {
            doEventManagerRequest(sendReply, request, rfnIdentifier);
            return;
        }
        case ASIDs::ChannelManager:
        {
            doChannelManagerRequest(sendReply, sendDelayedReply, request, rfnIdentifier);
            return;
        }
        case ASIDs::HubMeterCommandSet:
        {
            doHubMeterRequest(sendReply, request, rfnIdentifier);
            return;
        }
    }
}

Bytes GetMeterProgrammingConfiguration(const RfnIdentifier & rfnId);

void RfnMeter::processReply(const E2eRequestSender e2eRequestSender, const e2edt_reply_packet& reply, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceIdentifier)
{
    const auto itr = meterProgrammingRequests.find(rfnIdentifier);

    if( itr == meterProgrammingRequests.end() )
    {
        CTILOG_WARN(dout, "No active meter programming request for " << rfnIdentifier);
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

        e2eRequestSender(newRequest);

        return;
    }

    //  request is complete, store off configurationId and issue response

    const auto guid = programmingRequest.path.substr(
        static_cast<std::string::size_type>(
            programmingRequest.path.find_last_of('/') + 1U));

    getNodeInfo(rfnIdentifier).setConfigurationId(GuidPrefix::Yukon, guid);

    e2edt_request_packet newRequest;

    newRequest.id = idGenerator();
    newRequest.confirmable = false;
    newRequest.method = Protocols::Coap::RequestMethod::Post;

    newRequest.token = programmingRequest.initialToken;

    meterProgrammingRequests.erase(itr);

    newRequest.payload = GetMeterProgrammingConfiguration(rfnIdentifier);

    e2eRequestSender(newRequest);
}

Bytes DataStreamingRead (const Bytes& request, const RfnIdentifier& rfnId);
Bytes DataStreamingWrite(const Bytes& request, const RfnIdentifier& rfnId);
auto GetMeterRead       (const Bytes& payload, const RfnIdentifier& rfnId) -> std::optional<Bytes>;
void doMeterDisconnect(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& payload, const RfnIdentifier& rfnId);

void doChannelManagerRequest(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& request, const RfnIdentifier rfnIdentifier)
{
    if( request.empty() )
    {
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    switch( request[0] )
    {
        default:
        {
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case 0x01:
        {
            if( const auto reply = GetMeterRead(request, rfnIdentifier) )
            {
                sendReply(*reply);
            }
            else
            {
                sendReply(Coap::ResponseCode::BadRequest);
            }
            return;
        }
        case 0x55:
        {
            /*
            55 02 00

            56 00 00 00 00
            */
            sendReply(asBytes(
                "56 00 00 00 00"));
            return;
        }
        case 0x57:
        {
            if( request.size() >= 2 )
            {
                switch( request[1] )
                {
                    //  Write
                    case 0x00:
                        /*
                        57 00 00

                        58 00 00 00
                        */
                        sendReply(asBytes(
                            "58 00 00 00"));
                        return;

                    //  Read
                    case 0x01:
                        /*
                        57 01

                        58 01 00 00
                        */
                        sendReply(asBytes(
                            "58 01 00 00"));
                        return;
                }
            }
            return;
        }
        case 0x60:
        {
            /*
            60 01 00 
                OR
            60 04 0a 01 03 02 00 64 02 0a 01 68 01 68 00 f0
            01 e0 00 00 03 0a 01 68 01 68 01 68 01 68 00 00
            04 0a 02 d0 02 d0 00 00 00 00 00 00 05 0a 05 a0
            00 00 00 00 00 00 00 00 06 03 88 06 00 07 03 88
            02 00 08 03 08 00 00 09 03 00 00 00 0a 01 00

            61 00 00 00 01 00 
            */
            sendReply(asBytes(
                "61 00 00 00 01 00"));
            return;
        }
        case 0x62:
        {
            /*
            62 0f 

            63 00 
            */
            sendReply(asBytes(
                "63 00"));
            return;
        }
        case 0x68:
        {
            /*
            68 00 01 01 00 02 04 05 
            
            69 00 00 00

                OR

            68 06 00

            69 06 00 00

                OR

            68 02 00

            69 02 00 00
            */
            switch( request[1] )
            {
                case 0x00:
                    sendReply(asBytes(
                        "69 00 00 00"));
                    break;
                case 0x02:
                    sendReply(asBytes(
                        "69 02 00 00"));
                    break;
                case 0x06:
                    sendReply(asBytes(
                        "69 06 00 00"));
                    break;
            }
            return;
        }
        case 0x70:
        {
            /*
            70 00 1d 00 05 01 04 02 06 03 0b 04 0f 05 10 06
            11 07 08 08 09 09 07 0a 00 0b 00 0c 00 0d 00 0e
            00 0f 00 10 00 11 00 12 00 13 00 14 00 15 00 16
            00 17 00 18 00 19 00 fd 06 fe 04 ff 00 
    
            71 00 00 
            */
            sendReply(asBytes(
                "71 00 00"));
            return;
        }
        case 0x78:
        {
            /*
            78 00 01 01 00 43 21 00 01 00 03 00 04 00 05 00
            07 00 09 00 29 00 31 00 33 00 70 00 72 00 73 00
            f0 03 e9 03 eb 03 ec 03 ef 03 f1 07 d1 07 d3 07
            d4 07 d7 07 d9 0b b9 0b bb 0b bc 0b bf 0b c1 0f
            a1 0f a3 0f a4 0f a7 0f a9

            79 00 00 01 02 00 ad 2b 00 01 00 00 00 03 00 00 
            00 04 00 00 00 29 00 00 00 05 00 00 00 07 00 00 
            01 00 00 08 00 09 00 00 01 00 00 08 00 f0 00 00 
            01 00 00 08 00 31 00 00 00 33 00 00 01 00 00 08 
            00 73 00 07 03 e9 00 00 03 eb 00 00 03 ec 00 00 
            03 ef 00 00 04 e8 00 08 03 f1 00 00 04 e8 00 08 
            07 d1 00 00 07 d3 00 00 07 d4 00 00 07 d7 00 00 
            08 d0 00 08 07 d9 00 00 08 d0 00 08 0b b9 00 00 
            0b bb 00 00 0b bc 00 00 0b bf 00 00 0c b8 00 08 
            0b c1 00 00 0c b8 00 08 0f a1 00 00 0f a3 00 00 
            0f a4 00 00 0f a7 00 00 10 a0 00 08 0f a9 00 00 
            10 a0 00 08 
            */
            sendReply(asBytes(
                "79 00 00 01 02"
                    " 00 cd"  //  payload size
                    " 33"     //  channel count
                        " 00 01 00 00"
                        " 00 02 00 00"
                        " 00 03 00 00"
                        " 00 04 00 00"
                        " 00 29 00 00"
                        " 00 2a 00 00"
                        " 00 05 00 00"
                        " 00 07 00 00"
                        " 01 00 00 08"
                        " 00 09 00 00"
                        " 01 00 00 08"
                        " 00 f0 00 00"
                        " 01 00 00 08"
                        " 00 31 00 00"
                        " 00 33 00 00"
                        " 01 00 00 08"
                        " 00 73 00 07"
                        " 00 81 00 00"
                        " 00 82 00 00"
                        " 03 e9 00 00"
                        " 03 ea 00 00"
                        " 03 eb 00 00"
                        " 03 ec 00 00"
                        " 03 ef 00 00"
                        " 04 e8 00 08"
                        " 03 f1 00 00"
                        " 04 e8 00 08"
                        " 07 d1 00 00"
                        " 07 d2 00 00"
                        " 07 d3 00 00"
                        " 07 d4 00 00"
                        " 07 d7 00 00"
                        " 08 d0 00 08"
                        " 07 d9 00 00"
                        " 08 d0 00 08"
                        " 0b b9 00 00"
                        " 0b ba 00 00"
                        " 0b bb 00 00"
                        " 0b bc 00 00"
                        " 0b bf 00 00"
                        " 0c b8 00 08"
                        " 0b c1 00 00"
                        " 0c b8 00 08"
                        " 0f a1 00 00"
                        " 0f a2 00 00"
                        " 0f a3 00 00"
                        " 0f a4 00 00"
                        " 0f a7 00 00"
                        " 10 a0 00 08"
                        " 0f a9 00 00"
                        " 10 a0 00 08"));
            return;
        }
        case 0x7a:
        {
            /*
            7a 00 01 01 15 00 00 0e 10 00 00 54 60 06 00 01
            00 03 00 04 00 29 00 31 00 73 
    
            7b 00 00 01 02 19 06 00 01 00 00 00 03 00 00 00
            04 00 00 00 29 00 00 00 31 00 00 00 73 00 07 
            */
            if( rfnIdentifier.model == "C2SX-SD" )
            {
                sendReply(asBytes(
                    "7b 00 00 01 02"
                        " 21"
                        " 08"
                            " 00 01 00 00"
                            " 00 02 00 00"
                            " 00 03 00 00"
                            " 00 05 00 00"
                            " 00 29 00 00"
                            " 00 2a 00 00"
                            " 00 31 00 00"
                            " 00 73 00 07"));
            }
            else
            {
                sendReply(asBytes(
                    "7b 00 00 01 02"
                        " 21"
                        " 08"
                            " 00 01 00 00"
                            " 00 02 00 00"
                            " 00 03 00 00"
                            " 00 04 00 00"
                            " 00 29 00 00"
                            " 00 2a 00 00"
                            " 00 31 00 00"
                            " 00 73 00 07"));
            }
            return;
        }
        case 0x80:
        {
            doMeterDisconnect(sendReply, sendDelayedReply, request, rfnIdentifier);
            return;
        }
        case 0x82:
        {
            /*
            82 00 01 01 01 00

            83 00 00 01 01 01 01 01
            */
            sendReply(asBytes(
                "83 00 00 01 01 01 01 00"));
            return;
        }
        case 0x84:
        {
            sendReply(DataStreamingRead(request, rfnIdentifier));
            return;
        }
        case 0x86:
        {
            sendReply(DataStreamingWrite(request, rfnIdentifier));
            return;
        }
        case 0x91:
        {
            //  Send an empty ack for the ID
            sendReply(Protocols::Coap::ResponseCode::EmptyMessage);
            //  then send a separate data response for the token
            sendDelayedReply(GetMeterProgrammingConfiguration(rfnIdentifier));
            return;
        }
    }
}

auto GetMeterRead(const Bytes& payload, const RfnIdentifier& rfnId) -> std::optional<Bytes>
{
    return asBytes(
        "03"    //  Response type 3" contains one or more modifiers
        " 00"   //  Response status (OK)
        " 02"   //  Number of channels in response

        " 17"   //  Channel number
        " 81"       //  Unit of measure (Watth)
        " 80 90"    //  Modifier 1, Quadrant 1, Quadrant 4, has extension bit set
        " 00 00"    //  Modifier 2, no extension bit
        " 00 00 00 2a" //  Data
        " 00"       //  Status (OK)

        " 18"   //  Channel number
        " 82"       //  Unit of measure (Varh)
        " 80 00"    //  Modifier 1, has extension bit set
        " 00 00"    //  Modifier 2, no extension bit
        " 00 00 00 15" //  Data
        " 00"       //  Status (OK)
    );
}

void doMeterDisconnect(const ReplySender sendReply, const DelayedReplySender sendDelayedReply, const Bytes& payload, const RfnIdentifier& rfnId)
{
    if( payload.size() >= 2 )
    {
        switch( payload[1] )
        {
            case 0x01:
                sendReply(Coap::ResponseCode::EmptyMessage);
                sendDelayedReply(Bytes{ 0x81, payload[1], 0x00, payload[1] });  //  Echo the action
                return;

            case 0x02:
            case 0x03:
                sendReply(Bytes { 0x81, payload[1], 0x00, payload[1] });  //  Echo the action
                return;

            case 0x04:
                sendReply(Bytes { 0x81, payload[1], 0x00, 0x03 });  //  Resume
                return;
        }
    }

    sendReply(Coap::ResponseCode::BadRequest);
}


Bytes processAggregateRequests(const Bytes& request, const RfnIdentifier rfnIdentifier);

void doBulkMessageRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier)
{
    if( request.empty() )
    {
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    switch( request[0] )
    {
        default:
        {
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case 0x01:
        {
            sendReply(processAggregateRequests(request, rfnIdentifier));
            return;
        }
    }
}

Bytes processAggregateRequests(const Bytes& payload, const RfnIdentifier rfnIdentifier)
{
    constexpr auto HeaderLength = 1 + 1 + 2;  //  command + count + length
    constexpr auto RequestHeaderLength = 1 + 2 + 2;  //  ASID + contextId + length

    auto itr = payload.begin();
    const auto request_end = payload.end();

    if( itr + HeaderLength >= request_end )
    {
        CTILOG_WARN(dout, "No header while processing aggregate message for " << rfnIdentifier);
        return {};
    }

    itr++;  //  ignore the command byte, we already know it is 0x01
    const auto count = *itr++;
    const uint16_t length = *itr++ << 8
                          | *itr++;

    if( length + HeaderLength > payload.size() )
    {
        CTILOG_WARN(dout, "Not enough bytes while processing aggregate message for " << rfnIdentifier << FormattedList::of(
            "Total bytes", payload.size(),
            "Expected bytes", length + HeaderLength));

        return {};
    }

    Bytes result;
    
    result.resize(HeaderLength);

    auto replies = 0;

    for( auto index = 1; itr + RequestHeaderLength < request_end; ++index )
    {
        const auto contextId_first  = *itr++;
        const auto contextId_second = *itr++;

        const auto applicationServiceId = ASIDs{ *itr++ };
        
        const auto payloadLength = *itr++ << 8
                                 | *itr++;

        if( itr + payloadLength > request_end )
        {
            CTILOG_WARN(dout, "Ran out of bytes while processing aggregate message for " << rfnIdentifier << FormattedList::of(
                "Position", itr - payload.begin(),
                "Total bytes", payload.size(),
                "Payload bytes", length,
                "Expected bytes", payloadLength,
                "Remaining bytes", request_end - itr,
                "Request count", count,
                "Request index", index));

            return {};
        }

        Bytes payload { itr, itr + payloadLength };
        
        itr += payloadLength;

        if( applicationServiceId == ASIDs::BulkMessageHandler )
        {
            CTILOG_WARN(dout, "Discarding nested BulkMessageHandler request for " << rfnIdentifier);
            continue;
        }

        PayloadOrStatus response;

        processGetRequest(
            [&response, rfnIdentifier](PayloadOrStatus&& r) {
                response = std::move(r);
            }, 
            [rfnIdentifier](Bytes&& delayed) {
                CTILOG_WARN(dout, "Discarding delayed response generated for " << rfnIdentifier)
            },
            payload, 
            rfnIdentifier, 
            applicationServiceId);

        if( const auto reply = std::get_if<Bytes>(&response) )
        {
            CTILOG_DEBUG(dout, "Writing aggregate reply for " << rfnIdentifier << FormattedList::of(
                "Request count", count,
                "Request index", index,
                "Context ID", (contextId_first << 8) | contextId_second,
                "ASID", as_underlying(applicationServiceId),
                "Reply size", reply->size()));

            result.push_back(contextId_first);
            result.push_back(contextId_second);
            result.push_back(as_underlying(applicationServiceId));
            result.push_back(reply->size() >> 8);
            result.push_back(reply->size());
            boost::insert(result, result.end(), *reply);
            ++replies;
        }
        else 
        {
            CTILOG_WARN(dout, std::get<Coap::ResponseCode>(response) << " returned while processing component request for " << rfnIdentifier << FormattedList::of(
                "Request count", count,
                "Request index", index,
                "Context ID", (contextId_first << 8) | contextId_second,
                "ASID", as_underlying(applicationServiceId)));
        }
    }

    const auto payloadSize = result.size() - HeaderLength;

    result[0] = 0x01;
    result[1] = replies;
    result[2] = payloadSize >> 8;
    result[3] = payloadSize;

    return result;
}

auto GetConfigNotification(const Bytes& payload, const RfnIdentifier& rfnId) -> std::optional<Bytes>;

void doEventManagerRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier)
{
    if( request.empty() )
    {
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    switch( request[0] )
    {
        default:
        {
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case 0x24:
        {
            /*
            24 01

            29 01
            */
            sendReply(asBytes(
                "29 01"));
            return;
        }
        case 0x25:
        {
            /*
            25 04 07 e6 00 01 f4 00 10 80 00 01 c0 
                OR
            25 04 07 e7 00 01 e8 48 10 80 00 01 c0

            29 01
            */
            sendReply(asBytes(
                "29 01"));
            return;
        }
        case 0x26:
        {
            /*
            26 0f

            29 01
            */
            sendReply(asBytes(
                "29 01"));
            return;
        }
        case 0x27:
        {
            /*
            27 3c 

            29 01
            */
            sendReply(asBytes(
                "29 01"));
            return;
        }
        case 0x28:
        {
            /*
            28 02

            29 01
            */
            sendReply(asBytes(
                "29 01"));
            return;
        }
        case 0x88:
        {
            /*
            88 00 01 01 07 01 00 23 00 19 0f 03 
            
            89 00 00 01 01 07 01 00 23 00 19 0f 03
            */
            sendReply(asBytes(
                "89 00 00 01 01 07 01 00 23 00 19 0f 03"));
            return;
        }
    }
}

void doHubMeterRequest(const ReplySender sendReply, const Bytes& request, const RfnIdentifier rfnIdentifier)
{
    if( request.empty() )
    {
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    switch( request[0] )
    {
        default:
        {
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case 0x1d:
        {
        if( auto reply = GetConfigNotification(request, rfnIdentifier) )
        {
            sendReply(*reply);
            }
            else
            {
                sendReply(Coap::ResponseCode::BadRequest);
            }
            return;
        }
    }
}

Bytes asBytes(const char* hex_string)
{
    Bytes result;

    for( auto pos = hex_string; *pos; )
    {
        if( std::isalnum(*pos) )
        {
            char* end;
            result.push_back(strtoul(pos, &end, 16));
            pos = end;
        }
        else
        {
            pos++;
        }
    }

    return result;
}

auto GetConfigNotification(const Bytes& payload, const RfnIdentifier& rfnId) -> std::optional<Bytes>
{
    if( rfnId.manufacturer == "ITRN" && rfnId.model == "C2SX" )
    {
        return asBytes(
            "1e"
            " 00 0f" //  15 TLVs
                " 00 0b 00 12"  //  OV/UV configuration
                    " 04 07 e6 01 0f 3c 02 01 06 00 01 f4 00 10 80 00"
                    " 01 c0"
                " 00 0b 00 12"  //  OV/UV configuration
                    " 04 07 e7 01 0f 3c 02 01 06 00 01 e8 48 10 80 00"
                    " 01 c0"
                " 00 0c 00 07"  //  Temperature configuration
                    " 01 00 23 00 19 0f 03"
                " 00 01 00 01"  //  TOU enable/disable
                    " 01"
                " 00 02 00 38"  //  TOU schedule
                    //  Day table    
                    " 02 00 64"
                    //  Schedule 1 switch times
                    " 01 68"
                    " 01 68"
                    " 00 f0"
                    " 01 e0"
                    " 00 00"
                    //  Schedule 2 switch times
                    " 01 68"
                    " 01 68"
                    " 01 68"
                    " 01 68"
                    " 00 00"
                    //  Schedule 3 switch times
                    " 02 d0"
                    " 02 d0"
                    " 00 00"
                    " 00 00"
                    " 00 00"
                    //  Schedule 4 switch times
                    " 05 a0"
                    " 00 00"
                    " 00 00"
                    " 00 00"
                    " 00 00"
                    //  Schedule 1 rates
                    " 88 06 00"  //  Little endian - 0x000688 = 000 000 000 000 011 010 001 000 - reversed = A B C D A A A A
                    //  Schedule 2 rates
                    " 88 02 00"  //  Little endian - 0x000288 = 000 000 000 000 001 010 001 000 - reversed = A B C B A A A A
                    //  Schedule 3 rates
                    " 08 00 00"
                    //  Schedule 4 rates
                    " 00 00 00"
                    //  default rate
                    " 00"
                " 00 03 00 0c"  //  TOU holiday
                    " 5e 72 fc 50"  //  Holiday 1
                    " 5e 74 4d d0"  //  Holiday 2
                    " 5e 75 9f 50"  //  Holiday 3
                " 00 04 00 01"  //  Demand Freeze Day
                    " 20"
                " 00 08 00 02"  //  Voltage profile
                    " 04 05"
                " 00 09 00 3b"  //  C2SX Display
                    " 1d"
                        " 00 05"
                        " 01 04"
                        " 02 06"
                        " 03 0b"
                        " 04 0f"
                        " 05 10"
                        " 06 11"
                        " 07 08"
                        " 08 09"
                        " 09 07"
                        " 0a 00"
                        " 0b 00"
                        " 0c 00"
                        " 0d 00"
                        " 0e 00"
                        " 0f 00"
                        " 10 00"
                        " 11 00"
                        " 12 00"
                        " 13 00"
                        " 14 00"
                        " 15 00"
                        " 16 00"
                        " 17 00"
                        " 18 00"
                        " 19 00"
                        " fd 06"
                        " fe 04"
                        " ff 01"
                " 00 06 00 d5"  //  Channel selection
                    " 35"
                        " 00 01 00 00   00 02 00 00   00 03 00 00   00 04 00 00   00 29 00 00   00 2a 00 00   00 05 00 00   00 07 00 00"
                        " 01 00 00 08   00 09 00 00   01 00 00 08   00 f0 00 00   01 00 00 08   00 31 00 00   00 33 00 00   01 00 00 08"
                        " 00 73 00 07   00 81 00 07   01 00 00 08   00 82 00 07   01 00 00 08   03 e9 00 00   03 ea 00 00   03 eb 00 00"
                        " 03 ec 00 00   03 ef 00 00   04 e8 00 08   03 f1 00 00   04 e8 00 08   07 d1 00 00   07 d2 00 00   07 d3 00 00"
                        " 07 d4 00 00   07 d7 00 00   08 d0 00 08   07 d9 00 00   08 d0 00 08   0b b9 00 00   0b ba 00 00   0b bb 00 00"
                        " 0b bc 00 00   0b bf 00 00   0c b8 00 08   0b c1 00 00   0c b8 00 08   0f a1 00 00   0f a2 00 00   0f a3 00 00"
                        " 0f a4 00 00   0f a7 00 00   10 a0 00 08   0f a9 00 00   10 a0 00 08"
                " 00 0e 00 01"  //  Demand interval configuration
                    " 0f"
                " 00 07 00 01"  //  Disconnect
                    " 00"
                " 00 05 00 29"  //  Interval recording
                    " 00 00 0e 10"
                    " 00 00 54 60"
                    " 08"
                        " 00 01 00 00   00 02 00 00   00 03 00 00   00 04 00 00   00 29 00 00   00 2a 00 00   00 31 00 00   00 73 00 07"
                " 00 0d 00 1a"  //  Data Streaming configuration
                    " 04 01 00 01 01 05 00 00 02 00 05 00 00 05 00 05"
                    " 00 00 73 00 05 00 00 00 00 3a"
                " 00 0f 00 01"  //  Voltage profile status
                    " 01");
    }
    if( rfnId.manufacturer == "ITRN" && rfnId.model == "C2SX-SD" )
    {
        return asBytes(
            "1e 00 0f"
                " 00 0b 00 12"  //  OV/UV configuration
                    " 04 07 e6 01 0f 3c 02 01 06 00 01 f4 00 10 80 00"
                    " 01 c0"
                " 00 0b 00 12"  //  OV/UV configuration
                    " 04 07 e7 01 0f 3c 02 01 06 00 01 e8 48 10 80 00"
                    " 01 c0"
                " 00 0c 00 07"  //  Temperature configuration
                    " 01 00 23 00 19 0f 03"
                " 00 01 00 01"  //  TOU enable/disable
                    " 01"
                " 00 02 00 38"  //  TOU schedule
                    " 02 00 64"
                        //  Schedule 1 switch times
                        " 01 68"
                        " 01 68"
                        " 00 f0"
                        " 01 e0"
                        " 00 00"
                        //  Schedule 2 switch times
                        " 01 68"
                        " 01 68"
                        " 01 68"
                        " 01 68"
                        " 00 00"
                        //  Schedule 3 switch times
                        " 02 d0"
                        " 02 d0"
                        " 00 00"
                        " 00 00"
                        " 00 00"
                        //  Schedule 4 switch times            
                        " 05 a0"
                        " 00 00"
                        " 00 00"
                        " 00 00"
                        " 00 00"
                        //  Schedule 1 rates
                        " 88 06 00"
                        //  Schedule 2 rates
                        " 88 02 00"
                        //  Schedule 3 rates
                        " 08 00 00"
                        //  Schedule 4 rates
                        " 00 00 00"
                        //  Default TOU rate
                        " 00"
                " 00 03 00 0c"  //  TOU holiday
                    " 00 00 00 00 00 00 00 00 00 00 00 00"
                " 00 04 00 01"  //  Demand Freeze Day
                    " 20"
                " 00 08 00 02"  //  Voltage profile
                    " 04 05"
                " 00 09 00 3b"  //  C2SX Display
                    " 1d 00 05 01 04 02 06 03 0b 04 0f 05 10 06 11 07"
                    " 08 08 09 09 07 0a 00 0b 00 0c 00 0d 00 0e 00 0f"
                    " 00 10 00 11 00 12 00 13 00 14 00 15 00 16 00 17"
                    " 00 18 00 19 00 fd 05 fe 08 ff 01"
                " 00 06 00 d5"  //  Channel selection
                    " 35"
                        " 00 01 00 00   00 02 00 00   00 03 00 00   00 04 00 00   00 29 00 00   00 2a 00 00   00 05 00 00   00 07 00 00"
                        " 01 00 00 08   00 09 00 00   01 00 00 08   00 f0 00 00   01 00 00 08   00 31 00 00   00 33 00 00   01 00 00 08"
                        " 00 73 00 07   00 81 00 07   01 00 00 08   00 82 00 07   01 00 00 08   03 e9 00 00   03 ea 00 00   03 eb 00 00"
                        " 03 ec 00 00   03 ef 00 00   04 e8 00 08   03 f1 00 00   04 e8 00 08   07 d1 00 00   07 d2 00 00   07 d3 00 00"
                        " 07 d4 00 00   07 d7 00 00   08 d0 00 08   07 d9 00 00   08 d0 00 08   0b b9 00 00   0b ba 00 00   0b bb 00 00"
                        " 0b bc 00 00   0b bf 00 00   0c b8 00 08   0b c1 00 00   0c b8 00 08   0f a1 00 00   0f a2 00 00   0f a3 00 00"
                        " 0f a4 00 00   0f a7 00 00   10 a0 00 08   0f a9 00 00   10 a0 00 08"
                " 00 0e 00 01"  //  Demand interval configuration
                    " 0f"
                " 00 07 00 02"  //  Disconnect
                    " 01 00"
                " 00 05 00 29"  //  Interval recording
                    " 00 00 0e 10"
                    " 00 00 54 60"
                    " 08"
                        " 00 01 00 00"
                        " 00 02 00 00"
                        " 00 03 00 00"
                        " 00 29 00 00"
                        " 00 2a 00 00"
                        " 00 05 00 00"
                        " 00 31 00 00"
                        " 00 73 00 07"
                " 00 0d 00 1a"  //  Data Streaming configuration
                    " 04 00 00 01 00 1e 00 00 02 00 1e 00 00 05 00 1e"
                    " 00 00 73 00 1e 00 00 00 00 03"
                " 00 0f 00 01"  //  Voltage profile status
                    " 01");
    }
    return std::nullopt;
}

auto ParseSetMeterProgram(const Bytes& payload, const RfnIdentifier & rfnId) -> std::optional<std::tuple<std::string, unsigned>>;

void processPostRequest(const E2eRequestSender e2eRequestSender, const ReplySender sendReply, const Bytes& request, const unsigned token, const RfnIdentifier rfnIdentifier, const ASIDs applicationServiceId)
{
    //  The only POST we process at present is the Set Meter Configuration request, which results in a GET request back to Yukon.
    if( applicationServiceId != ASIDs::ChannelManager )
    {
        CTILOG_WARN(dout, "Received unhandled ASID (" << static_cast<int>(applicationServiceId) << ") for rfnIdentifier " << rfnIdentifier);
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    if( request.empty() )
    {
        sendReply(Coap::ResponseCode::BadRequest);
        return;
    }

    switch( request[0] )
    {
        default:
        {
            sendReply(Coap::ResponseCode::BadRequest);
            return;
        }
        case 0x90:
        {
            if( const auto pathSize = ParseSetMeterProgram(request, rfnIdentifier) )
            {
                const auto [path, size] = *pathSize;

                CTILOG_INFO(dout, "Received Meter Programming Set Configuration request" << FormattedList::of(
                    "Device", rfnIdentifier,
                    "Path", path,
                    "Size", size));

                e2edt_request_packet newRequest;

                newRequest.id = idGenerator();
                newRequest.confirmable = true;
                newRequest.method = Protocols::Coap::RequestMethod::Get;
                newRequest.path = path;
                newRequest.token = idGenerator();

                if( auto existingRequest = mapFindRef(meterProgrammingRequests, rfnIdentifier) )
                {
                    CTILOG_INFO(dout, "Replacing existing meter programming request" << FormattedList::of(
                        "Device", rfnIdentifier,
                        "Existing path", existingRequest->path,
                        "New path", path));

                    existingRequest->initialToken = token;
                    existingRequest->currentToken = newRequest.token;
                    existingRequest->path = path;
                }
                else
                {
                    meterProgrammingRequests.emplace(rfnIdentifier, MeterProgrammingRequest { token, newRequest.token, path });
                }

                e2eRequestSender(newRequest);
                e2eRequestSender(newRequest);

                return;
            }
        }
    }
}

auto ParseSetMeterProgram(const Bytes& payload, const RfnIdentifier & rfnId) -> std::optional<std::tuple<std::string, unsigned>>
{
    auto pos = 1;
    const auto end = payload.size();

    if( payload[pos++] != 2 )
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
        auto type = payload[pos];
        auto len = ntohs(*reinterpret_cast<const unsigned short *>(payload.data() + pos + 1));

        pos += 3;

        if( pos + len > end )
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
            size = ntohl(*reinterpret_cast<const u_long*>(payload.data() + pos));
            break;
        case 0x02:
            uri = std::string(payload.data() + pos, payload.data() + pos + len);
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

Bytes DataStreamingRead(const Bytes& payload, const RfnIdentifier & rfnId)
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

Bytes DataStreamingWrite(const Bytes& payload, const RfnIdentifier & rfnId)
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

    if( payload.size() >= 3 )
    {
        const auto metricCount = payload[1];
        response.enabled       = payload[2];
    
        if( payload.size() >= metricCount * 4 + 3 )
        {
            for( size_t i = 3; i < payload.size(); i += 4 )
            {
                const unsigned metricId = payload[i] << 8 | payload[i+1];
                const bool enabled = payload[i+2];
                const auto interval = payload[i+3];

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