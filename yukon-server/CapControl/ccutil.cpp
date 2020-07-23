#include "precompiled.h"
#include "ccutil.h"
#include "pointdefs.h"
#include "ccsubstationbusstore.h"

#include <boost/bimap.hpp>
#include <boost/assign/list_of.hpp>

using std::string;

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

double calculateKVARSolution( const std::string & controlUnits, double setPoint, double varValue, double wattValue,
                              const CapControlPao & pao )
{
    if( ciStringEqual(controlUnits,ControlStrategy::KVarControlUnit) )
    {
        return setPoint - varValue;
    }

    if( ciStringEqual(controlUnits,ControlStrategy::PFactorKWKVarControlUnit))
    {
        double targetKVAR = 0.0;
        if (setPoint != 0)
        {
            double targetKVA = wattValue / (setPoint/100.0);
            double NaNDefenseDouble = (targetKVA*targetKVA)-(wattValue*wattValue);
            if( NaNDefenseDouble > 0.0 )
            {
                targetKVAR = sqrt(NaNDefenseDouble);
                if (setPoint < 0)
                {
                    targetKVAR = 0 - targetKVAR;
                }
            }
        }

        return targetKVAR - varValue;
    }

    if( ciStringEqual(controlUnits,ControlStrategy::VoltsControlUnit) ||
        ciStringEqual(controlUnits,ControlStrategy::MultiVoltControlUnit)||
        ciStringEqual(controlUnits,ControlStrategy::MultiVoltVarControlUnit)||
        ciStringEqual(controlUnits,ControlStrategy::TimeOfDayControlUnit)||
        ciStringEqual(controlUnits,ControlStrategy::IntegratedVoltVarControlUnit) )
    {
        return 0.0;
    }

    if( ciStringEqual(controlUnits,ControlStrategy::NoControlUnit) )
    {
        // Our pao has no strategy assigned so issue a warning, but only show the warning once in a while (per YUK-18135).

        static std::map<long, std::chrono::system_clock::time_point>    showWarning;

        auto now = std::chrono::system_clock::now();

        auto & paoWarning = showWarning[ pao.getPaoId() ];

        if ( paoWarning <= now )
        {
            paoWarning = now + std::chrono::minutes( 5 );   // 5 minutes between warnings, minimum

            CTILOG_WARN( dout, "No strategy assigned to object: " << pao.getPaoName() );
        }

        return 0.0;
    }

    //  We've tested against all actual controlUnits above so at this point we have some unknown value

    CTILOG_ERROR(dout, "Invalid/unknown control units: " << controlUnits);

    return 0.0;
}

/* 
    Normally a disabled bank is completely ignored by the control strategies, but for some of the voltage control
        strategies (MultiVolt, MultiVoltVar and IVVC) we want to allow the voltage on a bank to be used by the
        control algorithm while doing cost calculations - even though we won't attempt to actually control it.
        The getMaxDailyOpsHitFlag is set when we disable a bank due to its maximum operations setting.  If it is
        set, we still want to use the bank even if it is disabled.
*/
bool eligibleForVoltageControl( CtiCCCapBank & bank )
{
    return bank.isSwitched()
           && ( ! bank.getDisableFlag() || bank.getMaxDailyOpsHitFlag() );
}

}
}
