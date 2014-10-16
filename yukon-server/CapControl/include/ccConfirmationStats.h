#pragma once

#include "ccoperationstats.h"

class CtiCCConfirmationStats
{

public:

    CtiCCConfirmationStats();
    CtiCCConfirmationStats(const CtiCCConfirmationStats& cap);

    virtual ~CtiCCConfirmationStats();

    void init();

    long getPAOId() const;
    long getUserDefCommCount() const;
    long getUserDefCommFail() const;
    long getDailyCommCount() const;
    long getDailyCommFail() const;
    long getWeeklyCommCount() const;
    long getWeeklyCommFail() const;
    long getMonthlyCommCount() const;
    long getMonthlyCommFail() const;
    long    getUserDefCommSuccessPercentId() const;
    double  getUserDefCommSuccessPercent() const;
    long    getDailyCommSuccessPercentId() const;
    double  getDailyCommSuccessPercent() const;
    long    getWeeklyCommSuccessPercentId() const;
    double  getWeeklyCommSuccessPercent() const;
    long    getMonthlyCommSuccessPercentId() const;
    double  getMonthlyCommSuccessPercent() const;


    CtiCCConfirmationStats& setPAOId(long paoId);
    CtiCCConfirmationStats& setUserDefCommCount(long value);
    CtiCCConfirmationStats& setUserDefCommFail(long value);
    CtiCCConfirmationStats& setDailyCommCount(long value);
    CtiCCConfirmationStats& setDailyCommFail(long value);
    CtiCCConfirmationStats& setWeeklyCommCount(long value);
    CtiCCConfirmationStats& setWeeklyCommFail(long value);
    CtiCCConfirmationStats& setMonthlyCommCount(long value);
    CtiCCConfirmationStats& setMonthlyCommFail(long value);
    CtiCCConfirmationStats& setUserDefCommSuccessPercentId(long pointId);
    CtiCCConfirmationStats& setUserDefCommSuccessPercent(double value);
    CtiCCConfirmationStats& setDailyCommSuccessPercentId(long pointId);
    CtiCCConfirmationStats& setDailyCommSuccessPercent(double  value);
    CtiCCConfirmationStats& setWeeklyCommSuccessPercentId(long pointId);
    CtiCCConfirmationStats& setWeeklyCommSuccessPercent(double value);
    CtiCCConfirmationStats& setMonthlyCommSuccessPercentId(long pointId);
    CtiCCConfirmationStats& setMonthlyCommSuccessPercent(double value);

    CtiCCConfirmationStats& incrementAllCommCounts(long attempts);
    CtiCCConfirmationStats& incrementAllCommFails(long errors);
    CtiCCConfirmationStats& incrementMonthlyCommCounts(long attempts);
    CtiCCConfirmationStats& incrementMonthlyCommFails(long errors);
    CtiCCConfirmationStats& incrementWeeklyCommCounts(long attempts);
    CtiCCConfirmationStats& incrementWeeklyCommFails(long errors);
    CtiCCConfirmationStats& incrementDailyCommCounts(long attempts);
    CtiCCConfirmationStats& incrementDailyCommFails(long errors);
    CtiCCConfirmationStats& incrementUserDefCommCounts(long attempts);
    CtiCCConfirmationStats& incrementUserDefCommFails(long errors);



    double calculateSuccessPercent(capcontrol::ccStatsType type);
    bool setSuccessPercentPointId(long tempPointId, long tempPointOffset);
    CtiCCConfirmationStats& createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

    CtiCCConfirmationStats* replicate() const;


    CtiCCConfirmationStats& operator=(const CtiCCConfirmationStats& right);

    int operator==(const CtiCCConfirmationStats& right) const;
    int operator!=(const CtiCCConfirmationStats& right) const;



private:

    long _paoid;

    long _userDefCommCount;
    long _userDefCommFail;
    long _dailyCommCount;
    long _dailyCommFail;
    long _weeklyCommCount;
    long _weeklyCommFail;
    long _monthlyCommCount;
    long _monthlyCommFail;

    long    _userDefCommSuccessPercentId;
    double  _userDefCommSuccessPercent;
    long    _dailyCommSuccessPercentId;
    double  _dailyCommSuccessPercent;
    long    _weeklyCommSuccessPercentId;
    double  _weeklyCommSuccessPercent;
    long    _monthlyCommSuccessPercentId;
    double  _monthlyCommSuccessPercent;
    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

};

typedef CtiCCConfirmationStats* CtiCCConfirmationStatsPtr;
