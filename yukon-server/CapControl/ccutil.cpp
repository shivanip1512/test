/*---------------------------------------------------------------------------
        Filename:  ccstatsobject.cpp

        Programmer:  Julie Richter

        Description:    CCStatsObject


        Initial Date:  2/24/2009

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#include "precompiled.h"
#include "ccutil.h"
#include "pointdefs.h"
#include "cccapbank.h"

using std::string;

extern unsigned long _MSG_PRIORITY;
namespace Cti           {
namespace CapControl    {

std::set<int> initClosedStates()
{
    std::set<int> s;
    s.insert(CtiCCCapBank::Close);
    s.insert(CtiCCCapBank::ClosePending);
    s.insert(CtiCCCapBank::CloseQuestionable);
    s.insert(CtiCCCapBank::CloseFail);
    return s;
}

std::set<int> initOpenStates()
{
    std::set<int> s;
    s.insert(CtiCCCapBank::Open);
    s.insert(CtiCCCapBank::OpenPending);
    s.insert(CtiCCCapBank::OpenQuestionable);
    s.insert(CtiCCCapBank::OpenFail);
    return s;
}

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString)
{
    CtiRequestMsg* reqMsg = new CtiRequestMsg(controllerId, commandString);
    reqMsg->setMessagePriority(_MSG_PRIORITY);
    return reqMsg;
};

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString, const string& user)
{
    CtiRequestMsg* reqMsg = createPorterRequestMsg(controllerId, commandString);
    reqMsg->setUser(user);
    return reqMsg;
};

bool isQualityOk(unsigned quality)
{
    if (quality == NormalQuality || quality == ManualQuality)
    {
        return true;
    }
    return false;
}

MissingPointAttribute::MissingPointAttribute(const long ID, const PointAttribute & attribute, string paoType, bool complainFlag)
    : std::exception(),
      _description("Missing Point Attribute: '")
{
    _complain = complainFlag;
    _description += attribute.name() + "' on "+ paoType +" with ID: " + CtiNumStr(ID);
}


const char * MissingPointAttribute::what( ) const
{
    return _description.c_str();
}
const bool MissingPointAttribute::complain( ) const
{
    return _complain;
}


Phase resolvePhase( const std::string & p )
{
    if ( p == "A" )
    {
        return Phase_A;
    }
    else if ( p == "B" )
    {
        return Phase_B;
    }
    else if ( p == "C" )
    {
        return Phase_C;
    }
    else if ( p == "*" )
    {
        return Phase_Poly;
    }
//    else if ( p == "?" )

    return Phase_Unknown;
}


std::string desolvePhase( const Phase & p )
{
    switch ( p )
    {
        case Phase_A:
        {
            return "A";
        }
        case Phase_B:
        {
            return "B";
        }
        case Phase_C:
        {
            return "C";
        }

        case Phase_Poly:
        {
            return "*";
        }
//        default:
//        case Phase_Unknown:
    }

    return "?";
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
        default:
        {
            return CtiPAOScheduleManager::Undefined;
        }
    }
}


double calculatePowerFactor( const double kvar, const double kwatt )
{
    double pf   = 1.0;
    double kva  = std::sqrt( std::pow( kwatt, 2.0 ) + std::pow( kvar, 2.0 ) );

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


}
}
