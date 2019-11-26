#pragma once

#include "dlldefs.h"
#include "yukon.h"  //  for YukonError_t
#include "rfn_identifier.h"
#include "e2e_messaging.h"

#include <boost/optional.hpp>
#include <boost/random.hpp>

#include <vector>
#include <map>

namespace Cti::Protocols {

//  Also known as E2EDT.
//  System spec:   http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
//  Firmware spec: http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/E2EDT/Design/E2E%20Data%20Transfer%20-%20Software%20Design%20Document.docx
//
//  This class is designed to manage a single device's E2EDT state.
class IM_EX_PROT E2eDataTransferProtocol : private boost::noncopyable
{
public:

    using Bytes = std::vector<unsigned char>;

    enum
    {
        MaxOutboundPayload = 1024
    };

    E2eDataTransferProtocol();

    //  throws PayloadTooLarge
    Bytes sendRequest(const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendPost   (const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendReply  (const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendBlockReply(const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token, const Coap::Block block);

    Bytes sendAck(const unsigned short id);
    Bytes sendBadRequest(const unsigned short id);

    Bytes sendBlockContinuation(const Coap::BlockSize size, const unsigned num, const RfnIdentifier endpointId, const unsigned long token);

    //  throws E2eException
    E2e::EndpointMessage handleIndication(const Bytes &payload, const RfnIdentifier endpointId);
    static YukonError_t translateIndicationCode(const unsigned short code, const RfnIdentifier endpointId);

    void handleTimeout(const RfnIdentifier endpointId);

protected:

    virtual unsigned short getOutboundId();

private:

    struct RequestId
    {
        unsigned short id;
        bool active;
    };

    using EndpointIds = std::map<RfnIdentifier, unsigned short>;
    using RequestIds = std::map<RfnIdentifier, RequestId>;

    RequestIds  _outboundIds;
    EndpointIds _inboundIds;

    boost::random::mt19937 _generator;

    unsigned short getOutboundIdForEndpoint(const RfnIdentifier endpointId);
};

}