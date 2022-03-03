#pragma once

#include "CallSite.h"
#include "msg_pcrequest.h"

#include <string>
#include <memory>
#include <vector>

class CtiCCCapBank;
class CtiClientConnection;

namespace Cti::CapControl {

enum BankOperationType
{
    BankOperation_Open,
    BankOperation_Close,
    BankOperation_Flip,
    BankOperation_Unknown
};

enum class RequestType
{
    Other,
    DnpTimesync,
    Heartbeat,
    Operate,
    Scan,
};

BankOperationType resolveOperationTypeForPointId(const std::string &commandString, CtiCCCapBank & bank);

struct PorterRequest
{
    RequestType type;
    std::unique_ptr<CtiRequestMsg> message;
    CtiRequestMsg* get() const
    {
        return message.get();
    }
    bool empty() const
    {
        return ! message;
    }
    bool has_value() const
    {
        return *this;
    }
    operator bool() const
    {
        return message.operator bool();
    }
    static PorterRequest none()
    {
        return {};
    }
    CtiRequestMsg* operator->() const
    {
        return message.get();
    }
};

std::ostream& operator<<(std::ostream& o, const PorterRequest& request);

using PorterRequests = std::vector<PorterRequest>;

PorterRequest createBankOpenRequest (const CtiCCCapBank &capBank);
PorterRequest createBankCloseRequest(const CtiCCCapBank &capBank);
PorterRequest createBankFlipRequest (const CtiCCCapBank &capBank);

PorterRequest createPorterRequestMsg(long controllerId, const std::string& commandString, RequestType requestType);
PorterRequest createPorterRequestMsg(long controllerId, const std::string& commandString, RequestType requestType, const std::string& user);

void sendPorterRequest(CtiClientConnection& porterConnection, PorterRequest request, CallSite callsite);
void sendPorterRequests(CtiClientConnection& porterConnection, PorterRequests requests, CallSite callsite);

uint8_t getRequestPriority(RequestType requestType);

}