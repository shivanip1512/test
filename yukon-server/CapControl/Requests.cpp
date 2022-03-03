#include "precompiled.h"

#include "Requests.h"

#include "msg_pcrequest.h"
#include "LitePoint.h"

#include "connection_client.h"
#include "cccapbank.h"

#include <boost/algorithm/string/predicate.hpp>
#include <boost/range/numeric.hpp>

using std::string;

namespace Cti::CapControl {

namespace MessagePriorities {
    extern uint8_t Operate;
    extern uint8_t Heartbeat;
    extern uint8_t DnpTimesync;
    extern uint8_t Scan;
    extern uint8_t Other;
}

typedef std::string (LitePoint::*CommandLookupMethod)() const;

struct CommandInfo
{
    std::string defaultCommand;
    CommandLookupMethod lookupMethod;
};

CommandInfo makeCommandInfo(std::string s, CommandLookupMethod m)
{
    CommandInfo info = { s, m };

    return info;
}

const std::map<BankOperationType, CommandInfo> availableOperations {
    { BankOperation_Open,  makeCommandInfo("control open",  &LitePoint::getStateZeroControl) },
    { BankOperation_Close, makeCommandInfo("control close", &LitePoint::getStateOneControl) },
    { BankOperation_Flip,  makeCommandInfo("control flip",  0) } };



std::string getCommandStringForOperation(const LitePoint &p, const CommandInfo &info)
{
    if( info.lookupMethod )
    {
        string s = (p.*(info.lookupMethod))();

        if( ! s.empty() )
        {
            return s;
        }
    }

    return info.defaultCommand;
}


BankOperationType resolveOperationTypeForPointId( const std::string &commandString, CtiCCCapBank & capBank )
{
    const LitePoint p = capBank.getControlPoint();

    if( ! commandString.empty() )
    {
        for( const auto command : availableOperations )
        {
            BankOperationType operation = command.first;
            CommandInfo info = command.second;

            if( boost::starts_with( commandString, getCommandStringForOperation( p, info ) ) )
            {
                return operation;
            }
        }
    }

    return BankOperation_Unknown;
}


std::string getBankOperationCommand( const BankOperationType bankOperation, const CtiCCCapBank & capBank )
{
    const LitePoint & p = capBank.getControlPoint();

    const auto itr = availableOperations.find(bankOperation);

    if( itr == availableOperations.end() )
    {
        return "command-not-found";
    }

    return getCommandStringForOperation(p, itr->second);
}

PorterRequest createBankOperationRequest( const CtiCCCapBank &capBank, const BankOperationType bankOperation )
{
    auto operationCmd = getBankOperationCommand( bankOperation, capBank );

    if( capBank.supportsHeartbeat() )
    {
        operationCmd += " select pointid " + std::to_string( capBank.getControlPointId() );
    }

    return createPorterRequestMsg(
                capBank.getControlDeviceId(),
                operationCmd,
                RequestType::Operate );
}

PorterRequest createBankOpenRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Open);
}

PorterRequest createBankCloseRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Close);
}

PorterRequest createBankFlipRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Flip);
}


PorterRequest createPorterRequestMsg(long controllerId, const string& commandString, RequestType requestType)
{
    auto reqMsg = std::make_unique<CtiRequestMsg>(controllerId, commandString);
    reqMsg->setMessagePriority(getRequestPriority(requestType));
    return { requestType, std::move(reqMsg) };
}

PorterRequest createPorterRequestMsg(long controllerId, const string& commandString, RequestType requestType, const string& user)
{
    auto request = createPorterRequestMsg(controllerId, commandString, requestType);
    request->setUser(user);
    return request;
}

std::unique_ptr<CtiRequestMsg> extractRequestMsg(PorterRequest request)
{
    auto msg = std::move(request.message);

    msg->setMessagePriority(Cti::CapControl::getRequestPriority(request.type));

    return msg;
}

void sendPorterRequest(CtiClientConnection& porterConnection, PorterRequest request, CallSite callsite)
{
    if( request.message )
    {
        porterConnection.WriteConnQue(extractRequestMsg(std::move(request)), callsite);
    }
}

void sendPorterRequests(CtiClientConnection& porterConnection, PorterRequests requests, CallSite callsite)
{
    if( ! requests.empty() )
    {
        auto multi = std::make_unique<CtiMultiMsg>();
        int maxPriority = 0;

        for( auto& request : requests )
        {
            auto requestMsg = extractRequestMsg(std::move(request));

            maxPriority = std::max(maxPriority, requestMsg->getMessagePriority());

            multi->getData().push_back(requestMsg.release());
        }

        multi->setMessagePriority(maxPriority);

        porterConnection.WriteConnQue(std::move(multi), callsite);
    }
}

uint8_t getRequestPriority(RequestType requestType)
{
    switch( requestType )
    {
        case RequestType::DnpTimesync:
            return MessagePriorities::DnpTimesync;
        case RequestType::Heartbeat:
            return MessagePriorities::Heartbeat;
        case RequestType::Operate:
            return MessagePriorities::Operate;
        case RequestType::Scan:
            return MessagePriorities::Scan;

        default: 
            [[fallthrough]];
        case RequestType::Other:
            return MessagePriorities::Other;
    }
}

std::ostream& operator<<(std::ostream& o, const PorterRequest& request)
{
    if( request.empty() )
    {
        return o << "[empty PorterRequest]";
    }
    return o << "[type:" << static_cast<int>(request.type) << ",message:" << request->toString() << "]";
}

}