#include "precompiled.h"

#include "ccconfirmationstats.h"
#include "pointtypes.h"
#include "pointdefs.h"
#include "msg_pdata.h"
#include "logger.h"
#include "ccutil.h"

using namespace capcontrol;
using std::endl;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::setVariableIfDifferent;

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

long CtiCCConfirmationStats::getPAOId() const
{
    return _paoid;
}
long CtiCCConfirmationStats::getUserDefCommCount() const
{
    return _userDefCommCount;
}
long CtiCCConfirmationStats::getUserDefCommFail() const
{
    return _userDefCommFail;
}
long CtiCCConfirmationStats::getDailyCommCount() const
{
    return _dailyCommCount;
}
long CtiCCConfirmationStats::getDailyCommFail() const
{
    return _dailyCommFail;
}
long CtiCCConfirmationStats::getWeeklyCommCount() const
{
    return _weeklyCommCount;
}
long CtiCCConfirmationStats::getWeeklyCommFail() const
{
    return _weeklyCommFail;
}
long CtiCCConfirmationStats::getMonthlyCommCount() const
{
    return _monthlyCommCount;
}
long CtiCCConfirmationStats::getMonthlyCommFail() const
{
    return _monthlyCommFail;
}

long CtiCCConfirmationStats::getUserDefCommSuccessPercentId() const
{
    return _userDefCommSuccessPercentId;
}
double CtiCCConfirmationStats::getUserDefCommSuccessPercent() const
{
    return _userDefCommSuccessPercent;
}
long CtiCCConfirmationStats::getDailyCommSuccessPercentId() const
{
    return _dailyCommSuccessPercentId;
}
double CtiCCConfirmationStats::getDailyCommSuccessPercent() const
{
    return _dailyCommSuccessPercent;
}
long CtiCCConfirmationStats::getWeeklyCommSuccessPercentId() const
{
    return _weeklyCommSuccessPercentId;
}
double CtiCCConfirmationStats::getWeeklyCommSuccessPercent() const
{
    return _weeklyCommSuccessPercent;
}
long CtiCCConfirmationStats::getMonthlyCommSuccessPercentId() const
{
    return _monthlyCommSuccessPercentId;
}

double CtiCCConfirmationStats::getMonthlyCommSuccessPercent() const
{
    return _monthlyCommSuccessPercent;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setPAOId(long paoId)
{
    _paoid = paoId;
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommCount(long value)
{
    _dirty |= setVariableIfDifferent(_userDefCommCount, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommFail(long value)
{
    _dirty |= setVariableIfDifferent(_userDefCommFail, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommCount(long value)
{
    _dirty |= setVariableIfDifferent(_dailyCommCount, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommFail(long value)
{
    _dirty |= setVariableIfDifferent(_dailyCommFail, value);
    return *this;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommCount(long value)
{
    _dirty |= setVariableIfDifferent(_weeklyCommCount, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommFail(long value)
{
    _dirty |= setVariableIfDifferent(_weeklyCommFail, value);
    return *this;
}


CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommCount(long value)
{
    _dirty |= setVariableIfDifferent(_monthlyCommCount, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommFail(long value)
{
    _dirty |= setVariableIfDifferent(_monthlyCommFail, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_userDefCommSuccessPercentId, pointId);
    return *this;
}
CtiCCConfirmationStats& CtiCCConfirmationStats::setUserDefCommSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_userDefCommSuccessPercent, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_dailyCommSuccessPercentId, pointId);
    return *this;
}
CtiCCConfirmationStats& CtiCCConfirmationStats::setDailyCommSuccessPercent(double  value)
{
    _dirty |= setVariableIfDifferent(_dailyCommSuccessPercent, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_weeklyCommSuccessPercentId, pointId);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setWeeklyCommSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_weeklyCommSuccessPercent, value);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_monthlyCommSuccessPercentId, pointId);
    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::setMonthlyCommSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_monthlyCommSuccessPercent, value);
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


double CtiCCConfirmationStats::calculateSuccessPercent(ccStatsType type)
{
    double retVal = 0;
    double opCount = 0;
    double failCount = 0;

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
        retVal = ((opCount - failCount) / opCount) * 100;
    }
    else
        retVal = -1;

    return retVal;
}

bool CtiCCConfirmationStats::setSuccessPercentPointId(long tempPointId, long tempPointOffset)
{
    switch (tempPointOffset)
    {
        case 10010:
        {
            setUserDefCommSuccessPercentId(tempPointId);
            return true;
        }
        case 10011:
        {
            setDailyCommSuccessPercentId(tempPointId);
            return true;
        }
        case 10012:
        {
            setWeeklyCommSuccessPercentId(tempPointId);
            return true;
        }
        case 10013:
        {
            setMonthlyCommSuccessPercentId(tempPointId);
            return true;
        }
        default:
            return false;
    }

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


CtiCCConfirmationStats& CtiCCConfirmationStats::incrementUserDefCommCounts(long attempts)
{
    setUserDefCommCount(_userDefCommCount+attempts);
    _userDefCommSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);

    return *this;
}

CtiCCConfirmationStats& CtiCCConfirmationStats::incrementUserDefCommFails(long errors)
{
    setUserDefCommFail(_userDefCommFail+errors);
    _userDefCommSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);

    return *this;
}




