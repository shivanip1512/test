#pragma once

#include "dlldefs.h"
#include "prot_e2eDataTransfer.h"
#include "dev_rfn.h"
#include "rfn_asid.h"
#include "rfn_e2e_messenger.h"

#include <boost/ptr_container/ptr_deque.hpp>

namespace Cti {
namespace Pil {

struct RfnDeviceRequest
{
    Devices::Commands::RfnCommandSPtr command;
    RfnIdentifier rfnIdentifier;
    long deviceId;
    unsigned long rfnRequestId;
    std::string commandString;
    unsigned priority;
    long userMessageId;
    long groupMessageId;
    ConnectionHandle connectionHandle;

    bool operator>(const RfnDeviceRequest &rhs) const
    {
        return priority > rhs.priority;
    }
};

struct RfnDeviceResult
{
    RfnDeviceResult(const RfnDeviceRequest request_, Devices::Commands::RfnCommandResult commandResult_, const YukonError_t status_) :
        request(request_),
        commandResult(commandResult_),
        status(status_)
    {
    }

    RfnDeviceRequest request;
    Devices::Commands::RfnCommandResult commandResult;
    YukonError_t status;
};

class IM_EX_CTIPIL RfnRequestManager
{
public:

    using ResultQueue          = std::deque<std::unique_ptr<RfnDeviceResult>>;
    using RfnDeviceRequestList = std::vector<RfnDeviceRequest>;

    void tick();

    unsigned long submitRequests(const RfnDeviceRequestList &requests, unsigned long requestId);

    ResultQueue getResults(unsigned max);

    void  cancelByGroupMessageId(long groupMessageId);
    size_t countByGroupMessageId(long groupMessageId);

    void start();

protected:

    virtual Protocols::E2eDataTransferProtocol::EndpointResponse handleE2eDtIndication(const std::vector<unsigned char> &payload, const long endpointId);
    virtual std::vector<unsigned char> sendE2eDtRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token);

private:

    typedef std::set<RfnIdentifier> RfnIdentifierSet;

    RfnIdentifierSet handleConfirms();
    RfnIdentifierSet handleIndications();
    RfnIdentifierSet handleTimeouts();
    void             handleNewRequests(const RfnIdentifierSet &recentCompletions);
    void             postResults();
    void             handleStatistics();

    typedef Messaging::Rfn::ApplicationServiceIdentifiers ApplicationServiceIdentifiers;

    Protocols::E2eDataTransferProtocol _e2edt;

    struct PacketInfo
    {
        std::vector<unsigned char> payloadSent;
        CtiTime  timeSent;
        unsigned retransmissionDelay;
        unsigned retransmits;
        unsigned maxRetransmits;
    };

    PacketInfo sendE2eDataRequestPacket(const std::vector<unsigned char> &e2ePacket, const ApplicationServiceIdentifiers &asid, const RfnIdentifier &rfnIdentifier, const unsigned priority, const long groupMessageId, const CtiTime timeout);
    void sendE2eDataAck(const std::vector<unsigned char> &e2eAck, const ApplicationServiceIdentifiers &asid,  const RfnIdentifier &rfnIdentifier);

    void checkForNewRequest(const RfnIdentifier &rfnId);

    using IndicationQueue  = std::vector<Messaging::Rfn::E2eMessenger::Indication>;
    using ConfirmQueue     = std::vector<Messaging::Rfn::E2eMessenger::Confirm>;
    using ExpirationCauses = std::map<RfnIdentifier, YukonError_t>;
    using RequestQueue     = std::multiset<RfnDeviceRequest, std::greater<RfnDeviceRequest>>;
    using RfnIdToRequestQueue = std::map<RfnIdentifier, RequestQueue>;

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex                _indicationMux;
    IndicationQueue      _indications;

    Mutex                _confirmMux;
    ConfirmQueue         _confirms;

    Mutex                _expirationMux;
    ExpirationCauses     _expirations;

    Mutex                _submittedRequestsMux;
    RfnDeviceRequestList _submittedRequests;

    Mutex                _resultsMux;
    ResultQueue          _results;

    ResultQueue _tickResults;

    Mutex                _pendingRequestsMux;
    RfnIdToRequestQueue  _pendingRequests;

    struct RfnRequestIdentifier
    {
        RfnIdentifier rfnId;
        unsigned long token;
    };

    using RfnTimeouts = std::multimap<CtiTime, RfnRequestIdentifier>;
    using NodeTokens = std::map<RfnIdentifier, unsigned long>;

    RfnTimeouts _awaitingIndications;
    NodeTokens  _activeTokens;

    struct ActiveRfnRequest
    {
        RfnDeviceRequest request;
        Devices::Commands::RfnCommand::RfnResponsePayload response;
        PacketInfo currentPacket;
        enum
        {
            Submitted,
            PendingConfirm,
            PendingReply,
        }
        status;
        CtiTime timeout;
    };

    typedef std::map<RfnIdentifier, ActiveRfnRequest> RfnIdToActiveRequest;

    Mutex                _activeRequestsMux;
    RfnIdToActiveRequest _activeRequests;
};

}
}
