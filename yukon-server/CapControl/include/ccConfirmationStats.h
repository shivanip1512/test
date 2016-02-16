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


    void setPAOId(long paoId);
    void setUserDefCommCount(long value);
    void setUserDefCommFail(long value);
    void setDailyCommCount(long value);
    void setDailyCommFail(long value);
    void setWeeklyCommCount(long value);
    void setWeeklyCommFail(long value);
    void setMonthlyCommCount(long value);
    void setMonthlyCommFail(long value);
    void setUserDefCommSuccessPercentId(long pointId);
    void setUserDefCommSuccessPercent(double value);
    void setDailyCommSuccessPercentId(long pointId);
    void setDailyCommSuccessPercent(double  value);
    void setWeeklyCommSuccessPercentId(long pointId);
    void setWeeklyCommSuccessPercent(double value);
    void setMonthlyCommSuccessPercentId(long pointId);
    void setMonthlyCommSuccessPercent(double value);

    void incrementAllCommCounts(long attempts);
    void incrementAllCommFails(long errors);
    void incrementMonthlyCommCounts(long attempts);
    void incrementMonthlyCommFails(long errors);
    void incrementWeeklyCommCounts(long attempts);
    void incrementWeeklyCommFails(long errors);
    void incrementDailyCommCounts(long attempts);
    void incrementDailyCommFails(long errors);
    void incrementUserDefCommCounts(long attempts);
    void incrementUserDefCommFails(long errors);



    double calculateSuccessPercent(capcontrol::ccStatsType type);
    bool setSuccessPercentPointId(long tempPointId, long tempPointOffset);
    void createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

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
