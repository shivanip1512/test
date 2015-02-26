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
    void *connectionHandle;

    bool operator<(const RfnDeviceRequest &rhs) const
    {
        return priority < rhs.priority;
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

    RfnRequestManager();

    typedef boost::ptr_deque<RfnDeviceResult> ResultQueue;
    typedef std::vector<RfnDeviceRequest> RfnDeviceRequestList;

    void tick();

    unsigned long submitRequests(const RfnDeviceRequestList &requests, unsigned long requestId);

    ResultQueue getResults(unsigned max);

    void cancelByGroupMessageId(long groupMessageId);

protected:

    virtual boost::optional<Protocols::E2eDataTransferProtocol::EndpointResponse> handleE2eDtIndication(const std::vector<unsigned char> &payload, const long endpointId);
    virtual std::vector<unsigned char> sendE2eDtRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token);

private:

    typedef std::set<RfnIdentifier> RfnIdentifierSet;

    RfnIdentifierSet handleConfirms();
    RfnIdentifierSet handleIndications();
    RfnIdentifierSet handleTimeouts();
    void             handleNewRequests(const RfnIdentifierSet &recentCompletions);
    void             postResults();
    void             handleStatistics();

    typedef Messaging::Rfn::E2eMessenger E2eMessenger;
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

    PacketInfo sendE2eDataRequestPacket(const std::vector<unsigned char> &e2ePacket, const ApplicationServiceIdentifiers &asid, const RfnIdentifier &rfnIdentifier, const unsigned priority);

    void receiveConfirm(const E2eMessenger::Confirm &msg);
    void receiveIndication(const E2eMessenger::Indication &msg);

    void checkForNewRequest(const RfnIdentifier &rfnId);

    typedef std::vector<E2eMessenger::Indication> IndicationQueue;
    typedef std::vector<E2eMessenger::Confirm> ConfirmQueue;
    typedef std::map<long, unsigned short> DeviceIdToE2eIdMap;
    typedef std::priority_queue<RfnDeviceRequest> RequestQueue;
    typedef std::map<RfnIdentifier, RequestQueue> RfnIdToRequestQueue;

    CtiCriticalSection   _indicationMux;
    IndicationQueue      _indications;

    CtiCriticalSection   _confirmMux;
    ConfirmQueue         _confirms;

    CtiCriticalSection   _submittedRequestsMux;
    RfnDeviceRequestList _submittedRequests;

    CtiCriticalSection   _resultsMux;
    ResultQueue          _results;

    ResultQueue _tickResults;

    RfnIdToRequestQueue  _pendingRequests;

    struct ActiveRfnRequest
    {
        RfnDeviceRequest request;
        Devices::Commands::RfnCommand::RfnResponsePayload response;
        PacketInfo currentPacket;
        unsigned short e2eId;
        enum
        {
            Submitted,
            PendingConfirm,
            PendingReply,
        }
        status;
        time_t timeout;
    };

    typedef std::map<RfnIdentifier, ActiveRfnRequest> RfnIdToActiveRequest;

    RfnIdToActiveRequest _activeRequests;

    typedef std::map<time_t, RfnIdentifierSet> ExpirationMap;

    ExpirationMap _upcomingExpirations;
};

}
}
