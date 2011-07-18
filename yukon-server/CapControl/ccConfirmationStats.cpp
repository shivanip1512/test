

/*---------------------------------------------------------------------------
        Filename:  ccconfirmationstats.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCConfirmationStats.
                        CtiCCConfirmationStats maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"
#include "msg_signal.h"
#include "pointtypes.h"
#include "msg_pdata.h"

#include "dbaccess.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "ccconfirmationstats.h"

using namespace capcontrol;
using std::endl;

extern ULONG _CC_DEBUG;

//RWDEFINE_COLLECTABLE( CtiCCConfirmationStats, CtiCCConfirmationStats_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCConfirmationStats::CtiCCConfirmationStats()
{
    _paoid = 0;

    init();
    _userDefCommSuccessPercentId  = 0;
    _userDefCommSuccessPercent    = -1;
    _dailyCommSuccessPercentId    = 0;
    _dailyCommSuccessPercent      = -1;
    _weeklyCommSuccessPercentId   = 0;
    _weeklyCommSuccessPercent     = -1;
    _monthlyCommSuccessPercentId  = 0;
    _monthlyCommSuccessPercent    = -1;

    return;
}


CtiCCConfirmationStats::CtiCCConfirmationStats(const CtiCCConfirmationStats& twoWayPt)
{
    operator=(twoWayPt);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCConfirmationStats::~CtiCCConfirmationStats()
{

}

/*---------------------------------------------------------------------------
    Init
---------------------------------------------------------------------------*/
void CtiCCConfirmationStats::init()
{
    _userDefCommCount = 0;
    _userDefCommFail = 0;
    _dailyCommCount = 0;
    _dailyCommFail = 0;
    _weeklyCommCount = 0;
    _weeklyCommFail = 0;
    _monthlyCommCount = 0;
    _monthlyCommFail = 0;

    return;
}

LONG CtiCCConfirmationStats::getPAOId() const
{
    return _paoid;
}
LONG CtiCCConfirmationStats::getUserDefCommCount() const
{
    return _userDefCommCount;
}
LONG CtiCCConfirmationStats::getUserDefCommFail() const
{
    return _userDefCommFail;
}
LONG CtiCCConfirmationStats::getDailyCommCount() const
{
    return _dailyCommCount;
}
LONG CtiCCConfirmationStats::getDailyCommFail() const
{
    return _dailyCommFail;
}
LONG CtiCCConfirmationStats::getWeeklyCommCount() const
{
    return _weeklyCommCount;
}
LONG CtiCCConfirmationStats::getWeeklyCommFail() const
{
    return _weeklyCommFail;
}
LONG CtiCCConfirmationStats::getMonthlyCommCount() const
{
    return _monthlyCommCount;
}
LONG CtiCCConfirmationStats::getMonthlyCommFail() const
{
    return _monthlyCommFail;
}

LONG CtiCCConfirmationStats::getUserDefCommSuccessPercentId() const
{
    return _userDefCommSuccessPercentId;
}
DOUBLE CtiCCConfirmationStats::getUserDefCommSuccessPercent() const
{
    return _userDefCommSuccessPercent;
}
LONG CtiCCConfirmationStats::getDailyCommSuccessPercentId() const
{
    return _dailyCommSuccessPercentId;
}
DOUBLE CtiCCConfirmationStats::getDailyCommSuccessPercent() const
{
    return _dailyCommSuccessPercent;
}
LONG CtiCCConfirmationStats::getWeeklyCommSuccessPercentId() const
{
    return _weeklyCommSuccessPercentId;
}
DOUBLE CtiCCConfirmationStats::getWeeklyCommSuccessPercent() const
{
    return _weeklyCommSuccessPercent;
}
LONG CtiCCConfirmationStats::getMonthlyCommSuccessPercentId() const
{
    return _monthlyCommSuccessPercentId;
}

DOUBLE CtiCCConfirmationStats::getMonthlyCommSuccessPercent() const
{
    return _monthlyCommSuccessPercent;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setPAOId(LONG paoId)
{
    _paoid = paoId;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommCount(LONG value)
{
    if (_userDefCommCount != value)
    {
        _dirty = TRUE;
    }
    _userDefCommCount = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommFail(LONG value)
{
    if (_userDefCommFail != value)
    {
        _dirty = TRUE;
    }
    _userDefCommFail = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommCount(LONG value)
{
    if (_dailyCommCount != value)
    {
        _dirty = TRUE;
    }
    _dailyCommCount = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommFail(LONG value)
{
    if (_dailyCommFail != value)
    {
        _dirty = TRUE;
    }
    _dailyCommFail = value;
    return *this;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommCount(LONG value)
{
    if (_weeklyCommCount != value)
    {
        _dirty = TRUE;
    }
    _weeklyCommCount = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommFail(LONG value)
{
    if (_weeklyCommFail != value)
    {
        _dirty = TRUE;
    }
    _weeklyCommFail = value;
    return *this;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommCount(LONG value)
{
    if (_monthlyCommCount != value)
    {
        _dirty = TRUE;
    }
    _monthlyCommCount = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommFail(LONG value)
{
    if (_monthlyCommFail != value)
    {
        _dirty = TRUE;
    }
    _monthlyCommFail = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommSuccessPercentId(LONG pointId)
{
    if (_userDefCommSuccessPercentId != pointId)
    {
        _dirty = TRUE;
    }
    _userDefCommSuccessPercentId = pointId;
    return *this;
}
CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommSuccessPercent(DOUBLE value)
{
    if (_userDefCommSuccessPercent != value)
    {
        _dirty = TRUE;
    }
    _userDefCommSuccessPercent= value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommSuccessPercentId(LONG pointId)
{
    if (_dailyCommSuccessPercentId != pointId)
    {
        _dirty = TRUE;
    }
    _dailyCommSuccessPercentId = pointId;
    return *this;
}
CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommSuccessPercent(DOUBLE  value)
{
    if (_dailyCommSuccessPercent != value)
    {
        _dirty = TRUE;
    }
    _dailyCommSuccessPercent = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommSuccessPercentId(LONG pointId)
{
    if (_weeklyCommSuccessPercentId != pointId)
    {
        _dirty = TRUE;
    }
    _weeklyCommSuccessPercentId = pointId;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommSuccessPercent(DOUBLE value)
{
    if (_weeklyCommSuccessPercent != value)
    {
        _dirty = TRUE;
    }
    _weeklyCommSuccessPercent = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommSuccessPercentId(LONG pointId)
{
    if (_monthlyCommSuccessPercentId != pointId)
    {
        _dirty = TRUE;
    }
    _monthlyCommSuccessPercentId = pointId;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommSuccessPercent(DOUBLE value)
{
    if (_monthlyCommSuccessPercent != value)
    {
        _dirty = TRUE;
    }
    _monthlyCommSuccessPercent = value;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::createPointDataMsgs(CtiMultiMsg_vec& pointChanges)
{
    if (getUserDefCommSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getUserDefCommSuccessPercentId(),getUserDefCommSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getDailyCommSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getDailyCommSuccessPercentId(),getDailyCommSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getWeeklyCommSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getWeeklyCommSuccessPercentId(),getWeeklyCommSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getMonthlyCommSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getMonthlyCommSuccessPercentId(),getMonthlyCommSuccessPercent(),NormalQuality,AnalogPointType));
    }
    return *this;
}


DOUBLE CtiCCConfirmationStats::calculateSuccessPercent(ccStatsType type)
{
    DOUBLE retVal = 0;
    LONG opCount = 0;
    LONG failCount = 0;

    switch (type)
    {
        case USER_DEF_CCSTATS:
        {
            opCount = _userDefCommCount;
            failCount = _userDefCommFail;
            break;
        }
        case DAILY_CCSTATS:
        {
            opCount = _dailyCommCount;
            failCount = _dailyCommFail;
            break;
        }
        case WEEKLY_CCSTATS:
        {
            opCount = _weeklyCommCount;
            failCount = _weeklyCommFail;
            break;
        }
        case MONTHLY_CCSTATS:
        {
            opCount = _monthlyCommCount;
            failCount = _monthlyCommFail;
            break;
        }
        default:
            break;


    }
    if (opCount > 0 && opCount >= failCount)
    {
        retVal = ((DOUBLE) (opCount - failCount) /(DOUBLE) opCount) * 100;
    }
    else
        retVal = -1;

    return retVal;
}

BOOL CtiCCConfirmationStats::setSuccessPercentPointId(LONG tempPointId, LONG tempPointOffset)
{
    BOOL retVal = FALSE;
    switch (tempPointOffset)
    {
        case 10010:
        {
            setUserDefCommSuccessPercentId(tempPointId);
            retVal = TRUE;
            break;
        }
        case 10011:
        {
            setDailyCommSuccessPercentId(tempPointId);
            retVal = TRUE;
            break;
        }
        case 10012:
        {
            setWeeklyCommSuccessPercentId(tempPointId);
            retVal = TRUE;
            break;
        }
        case 10013:
        {
            setMonthlyCommSuccessPercentId(tempPointId);
            retVal = TRUE;
            break;
        }
        default:
            break;
    }
    return retVal;

}
/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCConfirmationStats* CtiCCConfirmationStats::replicate() const
{
    return(new CtiCCConfirmationStats(*this));
}


CtiCCConfirmationStats& CtiCCConfirmationStats::operator=(const CtiCCConfirmationStats& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _userDefCommCount   = right._userDefCommCount;
        _userDefCommFail  = right._userDefCommFail;
        _dailyCommCount     = right._dailyCommCount;
        _dailyCommFail    = right._dailyCommFail;
        _weeklyCommCount    = right._weeklyCommCount;
        _weeklyCommFail   = right._weeklyCommFail;
        _monthlyCommCount   = right._monthlyCommCount;
        _monthlyCommFail  = right._monthlyCommFail;

        _userDefCommSuccessPercentId  =  right._userDefCommSuccessPercentId;
        _userDefCommSuccessPercent    =  right._userDefCommSuccessPercent;
        _dailyCommSuccessPercentId    =  right._dailyCommSuccessPercentId;
        _dailyCommSuccessPercent      =  right._dailyCommSuccessPercent;
        _weeklyCommSuccessPercentId   =  right._weeklyCommSuccessPercentId;
        _weeklyCommSuccessPercent     =  right._weeklyCommSuccessPercent;
        _monthlyCommSuccessPercentId  =  right._monthlyCommSuccessPercentId;
        _monthlyCommSuccessPercent    =  right._monthlyCommSuccessPercent;


        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }
    return *this;
}

int CtiCCConfirmationStats::operator==(const CtiCCConfirmationStats& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCConfirmationStats::operator!=(const CtiCCConfirmationStats& right) const
{
    return getPAOId() != right.getPAOId();
}


CtiCCConfirmationStats& CtiCCConfirmationStats::incrementUserDefCommCounts(LONG attempts)
{
    setUserDefCommCount(_userDefCommCount+attempts);
    _userDefCommSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);

    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::incrementUserDefCommFails(LONG errors)
{
    setUserDefCommFail(_userDefCommFail+errors);
    _userDefCommSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);

    return *this;
}



void CtiCCConfirmationStats::printCommStats()
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - DUMPING CommStats. " <<  endl;

        dout << "\t\t -  _paoid: " << _paoid
            << " _userDefCommCount " << _userDefCommCount
             << " _userDefCommFail " << _userDefCommFail
             << "  _dailyCommCount "   << _dailyCommCount
             << "  _dailyCommFail "  << _dailyCommFail
             << "  _weeklyCommCount "  << _weeklyCommCount
             << "  _weeklyCommFail " << _weeklyCommFail
             << "  _monthlyCommCount " << _monthlyCommCount
             << "  _monthlyCommFail "<< _monthlyCommFail
            << endl;
    }

}

