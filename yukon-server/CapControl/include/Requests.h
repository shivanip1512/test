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

struct CategorizedRequest
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
    static CategorizedRequest none()
    {
        return {};
    }
    CtiRequestMsg* operator->() const
    {
        return message.get();
    }
};

std::ostream& operator<<(std::ostream& o, const CategorizedRequest& request);

using CategorizedRequests = std::vector<CategorizedRequest>;

CategorizedRequest createBankOpenRequest (const CtiCCCapBank &capBank);
CategorizedRequest createBankCloseRequest(const CtiCCCapBank &capBank);
CategorizedRequest createBankFlipRequest (const CtiCCCapBank &capBank);

CategorizedRequest createPorterRequestMsg(long controllerId, const std::string& commandString, RequestType requestType);
CategorizedRequest createPorterRequestMsg(long controllerId, const std::string& commandString, RequestType requestType, const std::string& user);

void sendPorterRequest(CtiClientConnection& porterConnection, CategorizedRequest request, CallSite callsite);
void sendPorterRequests(CtiClientConnection& porterConnection, CategorizedRequests requests, CallSite callsite);

uint8_t getRequestPriority(RequestType requestType);

}