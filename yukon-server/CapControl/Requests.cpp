#include "precompiled.h"

#include "Requests.h"

#include "LitePoint.h"

#include "cccapbank.h"

#include <boost/assign/list_of.hpp>
#include <boost/algorithm/string/predicate.hpp>

using std::string;

extern unsigned long _MSG_PRIORITY;
namespace Cti           {
namespace CapControl    {

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

typedef std::map<BankOperationType, CommandInfo> BankOperationCommands;

const BankOperationCommands availableOperations = boost::assign::map_list_of
    (BankOperation_Open,  makeCommandInfo("control open",  &LitePoint::getStateZeroControl))
    (BankOperation_Close, makeCommandInfo("control close", &LitePoint::getStateOneControl))
    (BankOperation_Flip,  makeCommandInfo("control flip",  0));


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

    BankOperationCommands::const_iterator itr = availableOperations.find(bankOperation);

    if( itr == availableOperations.end() )
    {
        return "command-not-found";
    }

    return getCommandStringForOperation(p, itr->second);
}

std::unique_ptr<CtiRequestMsg> createBankOperationRequest( const CtiCCCapBank &capBank, const BankOperationType bankOperation )
{
    auto operationCmd = getBankOperationCommand( bankOperation, capBank );

    if( capBank.supportsHeartbeat() )
    {
        operationCmd += " select pointid " + std::to_string( capBank.getControlPointId() );
    }

    return std::unique_ptr<CtiRequestMsg>(
       createPorterRequestMsg(
          capBank.getControlDeviceId(),
          operationCmd ) );
}

std::unique_ptr<CtiRequestMsg> createBankOpenRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Open);
}

std::unique_ptr<CtiRequestMsg> createBankCloseRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Close);
}

std::unique_ptr<CtiRequestMsg> createBankFlipRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Flip);
}


CtiRequestMsg* createPorterRequestMsg(long controllerId, const string& commandString)
{
    CtiRequestMsg* reqMsg = new CtiRequestMsg(controllerId, commandString);
    reqMsg->setMessagePriority(_MSG_PRIORITY);
    return reqMsg;
}

CtiRequestMsg* createPorterRequestMsg(long controllerId, const string& commandString, const string& user)
{
    CtiRequestMsg* reqMsg = createPorterRequestMsg(controllerId, commandString);
    reqMsg->setUser(user);
    return reqMsg;
}

}
}
