#pragma once

#include "dlldefs.h"
#include "prot_e2eDataTransfer.h"
#include "dev_rfn.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"

#include <boost/ptr_container/ptr_deque.hpp>

namespace Cti {
namespace Pil {

struct RfnDeviceRequest
{
    Devices::Commands::RfnCommandSPtr command;
    Devices::RfnIdentifier rfnIdentifier;
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

private:

    typedef std::set<Devices::RfnIdentifier> RfnIdentifierSet;

    RfnIdentifierSet handleConfirms();
    RfnIdentifierSet handleIndications();
    RfnIdentifierSet handleTimeouts();
    void             handleNewRequests(const RfnIdentifierSet &recentCompletions);
    void             sendMessages(void);
    void             postResults();
    void             handleStatistics();

    typedef std::vector<unsigned char> SerializedMessage;

    Protocols::E2eDataTransferProtocol _e2edt;

    struct PacketInfo
    {
        std::vector<unsigned char> serializedMessage;
        CtiTime  timeSent;
        unsigned retransmissionDelay;
        unsigned retransmits;
        unsigned maxRetransmits;
    };

    PacketInfo sendE2eDataRequestPacket(const std::vector<unsigned char> &e2ePacket, const unsigned char applicationServiceId, const Devices::RfnIdentifier &rfnIdentifier);

    void handleRfnE2eDataIndicationMsg(const SerializedMessage &msg);
    void handleRfnE2eDataConfirmMsg(const SerializedMessage &msg);

    void checkForNewRequest(const Devices::RfnIdentifier &rfnId);

    typedef boost::ptr_deque<Messaging::Rfn::E2eDataIndicationMsg> IndicationQueue;
    typedef boost::ptr_deque<Messaging::Rfn::E2eDataConfirmMsg> ConfirmQueue;
    typedef std::map<long, unsigned short> DeviceIdToE2eIdMap;
    typedef std::priority_queue<RfnDeviceRequest> RequestQueue;
    typedef std::map<Devices::RfnIdentifier, RequestQueue> RfnIdToRequestQueue;

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

    typedef std::map<Devices::RfnIdentifier, ActiveRfnRequest> RfnIdToActiveRequest;

    RfnIdToActiveRequest _activeRequests;

    typedef std::map<time_t, RfnIdentifierSet> ExpirationMap;

    ExpirationMap _upcomingExpirations;

    std::vector<SerializedMessage> _messages;
};

}
}
