#include "precompiled.h"

#include "ccoperationstats.h"
#include "pointtypes.h"
#include "pointdefs.h"
#include "msg_pdata.h"
#include "logger.h"
#include "ccid.h"
#include "ccutil.h"
#include "database_util.h"
#include "row_reader.h"

using namespace capcontrol;
using std::string;
using std::endl;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::setVariableIfDifferent;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCOperationStats::CtiCCOperationStats()
{
    _paoid = 0;

    init();
    _userDefOpSuccessPercentId  = 0;
    _userDefOpSuccessPercent    = 0;
    _dailyOpSuccessPercentId    = 0;
    _dailyOpSuccessPercent      = 0;
    _weeklyOpSuccessPercentId   = 0;
    _weeklyOpSuccessPercent     = 0;
    _monthlyOpSuccessPercentId  = 0;
    _monthlyOpSuccessPercent    = 0;

    _insertDynamicDataFlag = true;
    _dirty = true;

    return;
}


CtiCCOperationStats::CtiCCOperationStats(const CtiCCOperationStats& twoWayPt)
{
    operator=(twoWayPt);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCOperationStats::~CtiCCOperationStats()
{

}

/*---------------------------------------------------------------------------
    Init
---------------------------------------------------------------------------*/
void CtiCCOperationStats::init()
{
    _userDefOpCount = 0;
    _userDefConfFail = 0;
    _dailyOpCount = 0;
    _dailyConfFail = 0;
    _weeklyOpCount = 0;
    _weeklyConfFail = 0;
    _monthlyOpCount = 0;
    _monthlyConfFail = 0;

    return;
}

long CtiCCOperationStats::getPAOId() const
{
    return _paoid;
}
long CtiCCOperationStats::getUserDefOpCount() const
{
    return _userDefOpCount;
}
long CtiCCOperationStats::getUserDefConfFail() const
{
    return _userDefConfFail;
}
long CtiCCOperationStats::getDailyOpCount() const
{
    return _dailyOpCount;
}
long CtiCCOperationStats::getDailyConfFail() const
{
    return _dailyConfFail;
}
long CtiCCOperationStats::getWeeklyOpCount() const
{
    return _weeklyOpCount;
}
long CtiCCOperationStats::getWeeklyConfFail() const
{
    return _weeklyConfFail;
}
long CtiCCOperationStats::getMonthlyOpCount() const
{
    return _monthlyOpCount;
}
long CtiCCOperationStats::getMonthlyConfFail() const
{
    return _monthlyConfFail;
}

long CtiCCOperationStats::getUserDefOpSuccessPercentId() const
{
    return _userDefOpSuccessPercentId;
}
double CtiCCOperationStats::getUserDefOpSuccessPercent() const
{
    return _userDefOpSuccessPercent;
}
long CtiCCOperationStats::getDailyOpSuccessPercentId() const
{
    return _dailyOpSuccessPercentId;
}
double CtiCCOperationStats::getDailyOpSuccessPercent() const
{
    return _dailyOpSuccessPercent;
}
long CtiCCOperationStats::getWeeklyOpSuccessPercentId() const
{
    return _weeklyOpSuccessPercentId;
}
double CtiCCOperationStats::getWeeklyOpSuccessPercent() const
{
    return _weeklyOpSuccessPercent;
}
long CtiCCOperationStats::getMonthlyOpSuccessPercentId() const
{
    return _monthlyOpSuccessPercentId;
}

double CtiCCOperationStats::getMonthlyOpSuccessPercent() const
{
    return _monthlyOpSuccessPercent;
}


CtiCCOperationStats& CtiCCOperationStats::setPAOId(long paoId)
{
    _paoid = paoId;
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setUserDefOpCount(long value)
{
    _dirty |= setVariableIfDifferent(_userDefOpCount, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setUserDefConfFail(long value)
{
    _dirty |= setVariableIfDifferent(_userDefConfFail, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setDailyOpCount(long value)
{
    _dirty |= setVariableIfDifferent(_dailyOpCount, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setDailyConfFail(long value)
{
    _dirty |= setVariableIfDifferent(_dailyConfFail, value);
    return *this;
}


CtiCCOperationStats& CtiCCOperationStats::setWeeklyOpCount(long value)
{
    _dirty |= setVariableIfDifferent(_weeklyOpCount, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setWeeklyConfFail(long value)
{
    _dirty |= setVariableIfDifferent(_weeklyConfFail, value);
    return *this;
}


CtiCCOperationStats& CtiCCOperationStats::setMonthlyOpCount(long value)
{
    _dirty |= setVariableIfDifferent(_monthlyOpCount, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setMonthlyConfFail(long value)
{
    _dirty |= setVariableIfDifferent(_monthlyConfFail, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setUserDefOpSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_userDefOpSuccessPercentId, pointId);
    return *this;
}
CtiCCOperationStats& CtiCCOperationStats::setUserDefOpSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_userDefOpSuccessPercent, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setDailyOpSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_dailyOpSuccessPercentId, pointId);
    return *this;
}
CtiCCOperationStats& CtiCCOperationStats::setDailyOpSuccessPercent(double  value)
{
    _dirty |= setVariableIfDifferent(_dailyOpSuccessPercent, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setWeeklyOpSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_weeklyOpSuccessPercentId, pointId);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setWeeklyOpSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_weeklyOpSuccessPercent, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setMonthlyOpSuccessPercentId(long pointId)
{
    _dirty |= setVariableIfDifferent(_monthlyOpSuccessPercentId, pointId);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::setMonthlyOpSuccessPercent(double value)
{
    _dirty |= setVariableIfDifferent(_monthlyOpSuccessPercent, value);
    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::createPointDataMsgs(CtiMultiMsg_vec& pointChanges)
{
    if (getUserDefOpSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getUserDefOpSuccessPercentId(),getUserDefOpSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getDailyOpSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getDailyOpSuccessPercentId(),getDailyOpSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getWeeklyOpSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getWeeklyOpSuccessPercentId(),getWeeklyOpSuccessPercent(),NormalQuality,AnalogPointType));
    }
    if (getMonthlyOpSuccessPercentId() > 0)
    {
        pointChanges.push_back(new CtiPointDataMsg(getMonthlyOpSuccessPercentId(),getMonthlyOpSuccessPercent(),NormalQuality,AnalogPointType));
    }
    return *this;
}
CtiCCOperationStats& CtiCCOperationStats::incrementAllOpCounts()
{
    setMonthlyOpCount(_monthlyOpCount+1);
    setWeeklyOpCount(_weeklyOpCount+1);
    setDailyOpCount(_dailyOpCount+1);
    setUserDefOpCount(_userDefOpCount+1);
    _userDefOpSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);
    _dailyOpSuccessPercent   = calculateSuccessPercent(DAILY_CCSTATS);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementAllOpFails()
{
    setMonthlyConfFail(_monthlyConfFail+1);
    setWeeklyConfFail(_weeklyConfFail+1);
    setDailyConfFail(_dailyConfFail+1);
    setUserDefConfFail(_userDefConfFail+1);
    _userDefOpSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);
    _dailyOpSuccessPercent   = calculateSuccessPercent(DAILY_CCSTATS);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementMonthlyOpCounts()
{
    setMonthlyOpCount(_monthlyOpCount+1);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementMonthlyOpFails()
{
    setMonthlyConfFail(_monthlyConfFail+1);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}


CtiCCOperationStats& CtiCCOperationStats::incrementWeeklyOpCounts()
{
    setMonthlyOpCount(_monthlyOpCount+1);
    setWeeklyOpCount(_weeklyOpCount+1);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementWeeklyOpFails()
{
    setMonthlyConfFail(_monthlyConfFail+1);
    setWeeklyConfFail(_weeklyConfFail+1);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementDailyOpCounts()
{
    setDailyOpCount(_dailyOpCount+1);
    setMonthlyOpCount(_monthlyOpCount+1);
    setWeeklyOpCount(_weeklyOpCount+1);
    _dailyOpSuccessPercent   = calculateSuccessPercent(DAILY_CCSTATS);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}

CtiCCOperationStats& CtiCCOperationStats::incrementDailyOpFails()
{
    setMonthlyConfFail(_monthlyConfFail+1);
    setWeeklyConfFail(_weeklyConfFail+1);
    setDailyConfFail(_dailyConfFail+1);
    _dailyOpSuccessPercent   = calculateSuccessPercent(DAILY_CCSTATS);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    return *this;
}




bool CtiCCOperationStats::isDirty()
{
    return _dirty;
}
void CtiCCOperationStats::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if (_dirty)
    {
        if( !_insertDynamicDataFlag )
        {
            static const string updateSql = "update dynamicccoperationstatistics set "
                                            "userdefopcount = ?, "
                                            "userdefconffail = ?, "
                                            "dailyopcount = ?, "
                                            "dailyconffail = ?, "
                                            "weeklyopcount = ?, "
                                            "weeklyconffail = ?, "
                                            "monthlyopcount = ?, "
                                            "monthlyconffail = ? "
                                            " where paobjectid = ?";

            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater << _userDefOpCount
                    << _userDefConfFail
                    << _dailyOpCount
                    << _dailyConfFail
                    << _weeklyOpCount
                    << _weeklyConfFail
                    << _monthlyOpCount
                    << _monthlyConfFail
                    << _paoid;

            if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
            {
                _dirty = false; // No error occured!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted CC Operation Statistics into DynamicCCOperationStatistics: " << getPAOId());
            static const string insertSql = "insert into dynamicccoperationstatistics values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Cti::Database::DatabaseWriter inserter(conn, insertSql);

            inserter << _paoid
                << _userDefOpCount
                << _userDefConfFail
                << _dailyOpCount
                << _dailyConfFail
                << _weeklyOpCount
                << _weeklyConfFail
                << _monthlyOpCount
                << _monthlyConfFail;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, inserter.asString());
            }

            if( Cti::Database::executeCommand( inserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                _dirty = false; // No error occured!
            }
        }
    }

}
double CtiCCOperationStats::calculateSuccessPercent(ccStatsType type)
{
    double retVal = 100;
    double opCount = 0;
    double failCount = 0;

    switch (type)
    {
        case USER_DEF_CCSTATS:
        {
            opCount = _userDefOpCount;
            failCount = _userDefConfFail;
            break;
        }
        case DAILY_CCSTATS:
        {
            opCount = _dailyOpCount;
            failCount = _dailyConfFail;
            break;
        }
        case WEEKLY_CCSTATS:
        {
            opCount = _weeklyOpCount;
            failCount = _weeklyConfFail;
            break;
        }
        case MONTHLY_CCSTATS:
        {
            opCount = _monthlyOpCount;
            failCount = _monthlyConfFail;
            break;
        }
        default:
            break;


    }

    if (opCount > 0 && opCount >= failCount)
    {
        retVal = ((opCount - failCount) / opCount) * 100;
    }

    return retVal;
}

bool CtiCCOperationStats::setSuccessPercentPointId(long tempPointId, long tempPointOffset)
{
    bool retVal = false;
    switch (tempPointOffset)
    {
        case 10000:
        {
            setUserDefOpSuccessPercentId(tempPointId);
            retVal = true;
            break;
        }
        case 10001:
        {
            setDailyOpSuccessPercentId(tempPointId);
            retVal = true;
            break;
        }
        case 10002:
        {
            setWeeklyOpSuccessPercentId(tempPointId);
            retVal = true;
            break;
        }
        case 10003:
        {
            setMonthlyOpSuccessPercentId(tempPointId);
            retVal = true;
            break;
        }
        default:
            break;
    }
    return retVal;

}
void CtiCCOperationStats::setDynamicData(Cti::RowReader& rdr)
{
    rdr["paobjectid"] >> _paoid;

    rdr["userdefopcount"] >> _userDefOpCount;
    rdr["userdefconffail"] >> _userDefConfFail;
    rdr["dailyopcount"] >> _dailyOpCount;
    rdr["dailyconffail"] >> _dailyConfFail;
    rdr["weeklyopcount"] >> _weeklyOpCount;
    rdr["weeklyconffail"] >> _weeklyConfFail;
    rdr["monthlyopcount"] >> _monthlyOpCount;
    rdr["monthlyconffail"] >> _monthlyConfFail;

    _userDefOpSuccessPercent = calculateSuccessPercent(USER_DEF_CCSTATS);
    _dailyOpSuccessPercent   = calculateSuccessPercent(DAILY_CCSTATS);
    _weeklyOpSuccessPercent  = calculateSuccessPercent(WEEKLY_CCSTATS);
    _monthlyOpSuccessPercent = calculateSuccessPercent(MONTHLY_CCSTATS);

    _insertDynamicDataFlag = false;
    _dirty = false;

    //return *this;


}
/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCOperationStats* CtiCCOperationStats::replicate() const
{
    return(new CtiCCOperationStats(*this));
}


CtiCCOperationStats& CtiCCOperationStats::operator=(const CtiCCOperationStats& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _userDefOpCount   = right._userDefOpCount;
        _userDefConfFail  = right._userDefConfFail;
        _dailyOpCount     = right._dailyOpCount;
        _dailyConfFail    = right._dailyConfFail;
        _weeklyOpCount    = right._weeklyOpCount;
        _weeklyConfFail   = right._weeklyConfFail;
        _monthlyOpCount   = right._monthlyOpCount;
        _monthlyConfFail  = right._monthlyConfFail;

        _userDefOpSuccessPercentId  =  right._userDefOpSuccessPercentId;
        _userDefOpSuccessPercent    =  right._userDefOpSuccessPercent;
        _dailyOpSuccessPercentId    =  right._dailyOpSuccessPercentId;
        _dailyOpSuccessPercent      =  right._dailyOpSuccessPercent;
        _weeklyOpSuccessPercentId   =  right._weeklyOpSuccessPercentId;
        _weeklyOpSuccessPercent     =  right._weeklyOpSuccessPercent;
        _monthlyOpSuccessPercentId  =  right._monthlyOpSuccessPercentId;
        _monthlyOpSuccessPercent    =  right._monthlyOpSuccessPercent;


        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }
    return *this;
}

int CtiCCOperationStats::operator==(const CtiCCOperationStats& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCOperationStats::operator!=(const CtiCCOperationStats& right) const
{
    return getPAOId() != right.getPAOId();
}

