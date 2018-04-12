#include "precompiled.h"
#include "ccutil.h"
#include "pointdefs.h"
#include "ccsubstationbusstore.h"

#include <boost/bimap.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/algorithm/string/predicate.hpp>

using std::string;

extern unsigned long _MSG_PRIORITY;
namespace Cti           {
namespace CapControl    {

const std::set<int> ClosedStates = boost::assign::list_of
    (CtiCCCapBank::Close)
    (CtiCCCapBank::ClosePending)
    (CtiCCCapBank::CloseQuestionable)
    (CtiCCCapBank::CloseFail);

const std::set<int> OpenStates = boost::assign::list_of
    (CtiCCCapBank::Open)
    (CtiCCCapBank::OpenPending)
    (CtiCCCapBank::OpenQuestionable)
    (CtiCCCapBank::OpenFail);

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

std::auto_ptr<CtiRequestMsg> createBankOperationRequest( const CtiCCCapBank &capBank, const BankOperationType bankOperation )
{
    auto operationCmd = getBankOperationCommand( bankOperation, capBank );

    if( capBank.supportsHeartbeat() )
    {
        operationCmd += " select pointid " + std::to_string( capBank.getControlPointId() );
    }

    return std::auto_ptr<CtiRequestMsg>(
       createPorterRequestMsg(
          capBank.getControlDeviceId(),
          operationCmd ) );
}

std::auto_ptr<CtiRequestMsg> createBankOpenRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Open);
}

std::auto_ptr<CtiRequestMsg> createBankCloseRequest(const CtiCCCapBank &capBank)
{
    return createBankOperationRequest(capBank, BankOperation_Close);
}

std::auto_ptr<CtiRequestMsg> createBankFlipRequest(const CtiCCCapBank &capBank)
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

bool isQualityOk(unsigned quality)
{
    return (quality == NormalQuality ||
            quality == ManualQuality);
}

MissingAttribute::MissingAttribute( const long ID, const Attribute & attribute, const std::string & paoType, bool complainFlag )
    : _description("Missing Attribute: '")
{
    _complain = complainFlag;
    _description += attribute.getName() + "' on "+ paoType +" with ID: " + CtiNumStr(ID);
}

const char * MissingAttribute::what( ) const
{
    return _description.c_str();
}
const bool MissingAttribute::complain( ) const
{
    return _complain;
}

NoAttributeValue::NoAttributeValue(const long ID, const Attribute & attribute, string paoType)
    : _description("No Attribute Value for: '")
{
    _description += attribute.getName() + "' on "+ paoType +" with ID: " + CtiNumStr(ID);
}


const char * NoAttributeValue::what( ) const
{
    return _description.c_str();
}


typedef boost::bimap<std::string, Phase> PhaseLookup;

const string String_A = "A";
const string String_B = "B";
const string String_C = "C";
const string String_Poly = "*";
const string String_Unknown = "?";


const PhaseLookup phases = boost::assign::list_of<PhaseLookup::relation>
    (String_A, Phase_A)
    (String_B, Phase_B)
    (String_C, Phase_C)
    (String_Poly, Phase_Poly)
    (String_Unknown, Phase_Unknown);


Phase resolvePhase( const std::string & p )
{
    PhaseLookup::left_const_iterator itr = phases.left.find(p);

    if( itr != phases.left.end() )
    {
        return itr->second;
    }

    return Phase_Unknown;
}


std::string desolvePhase( const Phase & p )
{
    PhaseLookup::right_const_iterator itr = phases.right.find(p);

    if( itr != phases.right.end() )
    {
        return itr->second;
    }

    return String_Unknown;
}

CtiPAOScheduleManager::VerificationStrategy ConvertIntToVerificationStrategy(int verifyId)
{
    switch (verifyId)
    {
        case CtiPAOScheduleManager::AllBanks:
        case CtiPAOScheduleManager::FailedAndQuestionableBanks:
        case CtiPAOScheduleManager::FailedBanks:
        case CtiPAOScheduleManager::QuestionableBanks:
        case CtiPAOScheduleManager::BanksInactiveForXTime:
        case CtiPAOScheduleManager::StandAloneBanks:
        case CtiPAOScheduleManager::SelectedForVerificationBanks:
        {
            return CtiPAOScheduleManager::VerificationStrategy(verifyId);
        }
    }

    return CtiPAOScheduleManager::Undefined;
}


double calculatePowerFactor( const double kvar, const double kwatt )
{
    double pf        = 1.0;
    const double kva = std::sqrt( std::pow( kwatt, 2 ) + std::pow( kvar, 2 ) );

    if ( kva != 0.0 )
    {
        pf = std::abs( kwatt / kva );
        if ( kvar < 0.0 )               // leading
        {
            pf = 2.0 - pf;
        }
    }

    return pf;
}

// use when populating a string of flags
char populateFlag( const bool flag )
{
    return flag ? 'Y' : 'N';
}

// use when sending a single flag to the database writer
std::string serializeFlag( const bool flag )
{
    return flag ? "Y" : "N";
}

bool deserializeFlag( const std::string & flags, const unsigned index )
{
    return ( index < flags.size() &&
                ( flags[ index ] == 'Y' ||
                  flags[ index ] == 'y' ||
                  flags[ index ] == '1' ) );
}

const std::string SystemUser = "cap control";

}
}
