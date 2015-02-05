#pragma once

#include "dlldefs.h"

#include <boost/optional.hpp>
#include <boost/random.hpp>

#include <vector>
#include <map>

namespace Cti {
namespace Protocols {

//  Also known as E2EDT.
//  System spec:   http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
//  Firmware spec: http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/E2EDT/Design/E2E%20Data%20Transfer%20-%20Software%20Design%20Document.docx
//
//  This class is designed to manage a single device's E2EDT state.
class IM_EX_PROT E2eDataTransferProtocol : private boost::noncopyable
{
public:

    struct EndpointResponse
    {
        unsigned long token;

        std::vector<unsigned char> data;  //  bytes payload

        std::vector<unsigned char> ack;
        std::vector<unsigned char> blockContinuation;

        bool final;
    };

    struct PayloadTooLarge {};

    enum
    {
        MaxOutboundPayload = 1000
    };

    E2eDataTransferProtocol();

    //  throws PayloadTooLarge
    std::vector<unsigned char> sendRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    boost::optional<EndpointResponse> handleIndication(const std::vector<unsigned char> &payload, const long endpointId);

    void handleTimeout(const long endpointId);

private:

    std::map<long, unsigned short> _outboundIds;
    std::map<long, unsigned short> _inboundIds;

    boost::random::mt19937 _generator;

    unsigned short getOutboundIdForEndpoint(long endpointId);
    std::vector<unsigned char> sendBlockContinuation(const unsigned size, const unsigned num, const long endpointId, const unsigned long token);
    std::vector<unsigned char> sendAck(const unsigned short id);
};

}
}
