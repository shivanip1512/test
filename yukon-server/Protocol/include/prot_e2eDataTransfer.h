#pragma once

#include "dlldefs.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Protocols {

//  Also known as E2EDT.
//  System spec:   http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
//  Firmware spec: http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/E2EDT/Design/E2E%20Data%20Transfer%20-%20Software%20Design%20Document.docx
//
//  This class is designed to manage a single device's E2EDT state.
struct IM_EX_PROT E2eDataTransferProtocol
{
    struct SendInfo
    {
        std::vector<unsigned char> message;  //  bytes payload
        int status;
    };

    struct EndpointResponse
    {
        std::vector<unsigned char> data;  //  bytes payload
        boost::optional<std::vector<unsigned char>> blockContinuation;
        int status;
    };

    typedef int ErrorCode;

    enum
    {
        MaxOutboundPayload = 1000
    };

    SendInfo sendRequest(const std::vector<unsigned char> &payload, const unsigned short id);
    EndpointResponse handleIndication(const std::vector<unsigned char> &payload);

    //  How much state is necessary?  Blockwise transfers, right?
    //  Retransmits of individual packets?  Timeouts mean retransmits, etc...
};

}
}
