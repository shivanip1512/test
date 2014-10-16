#pragma once

#include "message.h"

namespace Cti {
    class RowReader;

namespace Database {
    class DatabaseConnection;
}
}

namespace capcontrol
{
    typedef enum
    {
        USER_DEF_CCSTATS = 0,
        DAILY_CCSTATS,
        WEEKLY_CCSTATS,
        MONTHLY_CCSTATS
    } ccStatsType;
};



class CtiCCOperationStats
{
public:
    CtiCCOperationStats();
    CtiCCOperationStats(const CtiCCOperationStats& cap);

    virtual ~CtiCCOperationStats();

    void init();

    long getPAOId() const;
    long getUserDefOpCount() const;
    long getUserDefConfFail() const;
    long getDailyOpCount() const;
    long getDailyConfFail() const;
    long getWeeklyOpCount() const;
    long getWeeklyConfFail() const;
    long getMonthlyOpCount() const;
    long getMonthlyConfFail() const;
    long    getUserDefOpSuccessPercentId() const;
    double  getUserDefOpSuccessPercent() const;
    long    getDailyOpSuccessPercentId() const;
    double  getDailyOpSuccessPercent() const;
    long    getWeeklyOpSuccessPercentId() const;
    double  getWeeklyOpSuccessPercent() const;
    long    getMonthlyOpSuccessPercentId() const;
    double  getMonthlyOpSuccessPercent() const;


    CtiCCOperationStats& setPAOId(long paoId);
    CtiCCOperationStats& setUserDefOpCount(long value);
    CtiCCOperationStats& setUserDefConfFail(long value);
    CtiCCOperationStats& setDailyOpCount(long value);
    CtiCCOperationStats& setDailyConfFail(long value);
    CtiCCOperationStats& setWeeklyOpCount(long value);
    CtiCCOperationStats& setWeeklyConfFail(long value);
    CtiCCOperationStats& setMonthlyOpCount(long value);
    CtiCCOperationStats& setMonthlyConfFail(long value);
    CtiCCOperationStats& incrementAllOpCounts();
    CtiCCOperationStats& incrementAllOpFails();
    CtiCCOperationStats& incrementMonthlyOpCounts();
    CtiCCOperationStats& incrementMonthlyOpFails();
    CtiCCOperationStats& incrementWeeklyOpCounts();
    CtiCCOperationStats& incrementWeeklyOpFails();
    CtiCCOperationStats& incrementDailyOpCounts();
    CtiCCOperationStats& incrementDailyOpFails();
    CtiCCOperationStats& setUserDefOpSuccessPercentId(long pointId);
    CtiCCOperationStats& setUserDefOpSuccessPercent(double value);
    CtiCCOperationStats& setDailyOpSuccessPercentId(long pointId);
    CtiCCOperationStats& setDailyOpSuccessPercent(double  value);
    CtiCCOperationStats& setWeeklyOpSuccessPercentId(long pointId);
    CtiCCOperationStats& setWeeklyOpSuccessPercent(double value);
    CtiCCOperationStats& setMonthlyOpSuccessPercentId(long pointId);
    CtiCCOperationStats& setMonthlyOpSuccessPercent(double value);



    double calculateSuccessPercent(capcontrol::ccStatsType type);
    bool setSuccessPercentPointId(long tempPointId, long tempPointOffset);
    CtiCCOperationStats& createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

    bool isDirty();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);


    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);
    CtiCCOperationStats* replicate() const;


    CtiCCOperationStats& operator=(const CtiCCOperationStats& right);

    int operator==(const CtiCCOperationStats& right) const;
    int operator!=(const CtiCCOperationStats& right) const;



private:

    long _paoid;

    long _userDefOpCount;
    long _userDefConfFail;
    long _dailyOpCount;
    long _dailyConfFail;
    long _weeklyOpCount;
    long _weeklyConfFail;
    long _monthlyOpCount;
    long _monthlyConfFail;

    long    _userDefOpSuccessPercentId;
    double  _userDefOpSuccessPercent;
    long    _dailyOpSuccessPercentId;
    double  _dailyOpSuccessPercent;
    long    _weeklyOpSuccessPercentId;
    double  _weeklyOpSuccessPercent;
    long    _monthlyOpSuccessPercentId;
    double  _monthlyOpSuccessPercent;
    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

};

typedef CtiCCOperationStats* CtiCCOperationStatsPtr;
