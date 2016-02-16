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


    void setPAOId(long paoId);
    void setUserDefOpCount(long value);
    void setUserDefConfFail(long value);
    void setDailyOpCount(long value);
    void setDailyConfFail(long value);
    void setWeeklyOpCount(long value);
    void setWeeklyConfFail(long value);
    void setMonthlyOpCount(long value);
    void setMonthlyConfFail(long value);
    void incrementAllOpCounts();
    void incrementAllOpFails();
    void incrementMonthlyOpCounts();
    void incrementMonthlyOpFails();
    void incrementWeeklyOpCounts();
    void incrementWeeklyOpFails();
    void incrementDailyOpCounts();
    void incrementDailyOpFails();
    void setUserDefOpSuccessPercentId(long pointId);
    void setUserDefOpSuccessPercent(double value);
    void setDailyOpSuccessPercentId(long pointId);
    void setDailyOpSuccessPercent(double  value);
    void setWeeklyOpSuccessPercentId(long pointId);
    void setWeeklyOpSuccessPercent(double value);
    void setMonthlyOpSuccessPercentId(long pointId);
    void setMonthlyOpSuccessPercent(double value);



    double calculateSuccessPercent(capcontrol::ccStatsType type);
    bool setSuccessPercentPointId(long tempPointId, long tempPointOffset);
    void createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

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
