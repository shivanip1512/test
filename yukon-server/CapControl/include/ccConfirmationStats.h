#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"
#include "ccoperationstats.h"  
          
class CtiCCConfirmationStats  
{

public:

    CtiCCConfirmationStats();
    CtiCCConfirmationStats(const CtiCCConfirmationStats& cap);

    virtual ~CtiCCConfirmationStats();

    void init();

    LONG getPAOId() const;
    LONG getUserDefCommCount() const;
    LONG getUserDefCommFail() const;
    LONG getDailyCommCount() const;
    LONG getDailyCommFail() const;
    LONG getWeeklyCommCount() const;
    LONG getWeeklyCommFail() const;
    LONG getMonthlyCommCount() const;
    LONG getMonthlyCommFail() const;
    LONG    getUserDefCommSuccessPercentId() const;
    DOUBLE  getUserDefCommSuccessPercent() const;
    LONG    getDailyCommSuccessPercentId() const;
    DOUBLE  getDailyCommSuccessPercent() const;
    LONG    getWeeklyCommSuccessPercentId() const;
    DOUBLE  getWeeklyCommSuccessPercent() const;
    LONG    getMonthlyCommSuccessPercentId() const;
    DOUBLE  getMonthlyCommSuccessPercent() const;

    
    CtiCCConfirmationStats& setPAOId(LONG paoId);
    CtiCCConfirmationStats& setUserDefCommCount(LONG value);
    CtiCCConfirmationStats& setUserDefCommFail(LONG value);
    CtiCCConfirmationStats& setDailyCommCount(LONG value);
    CtiCCConfirmationStats& setDailyCommFail(LONG value);
    CtiCCConfirmationStats& setWeeklyCommCount(LONG value);
    CtiCCConfirmationStats& setWeeklyCommFail(LONG value);
    CtiCCConfirmationStats& setMonthlyCommCount(LONG value);
    CtiCCConfirmationStats& setMonthlyCommFail(LONG value);
    CtiCCConfirmationStats& setUserDefCommSuccessPercentId(LONG pointId);
    CtiCCConfirmationStats& setUserDefCommSuccessPercent(DOUBLE value);
    CtiCCConfirmationStats& setDailyCommSuccessPercentId(LONG pointId);
    CtiCCConfirmationStats& setDailyCommSuccessPercent(DOUBLE  value);
    CtiCCConfirmationStats& setWeeklyCommSuccessPercentId(LONG pointId);
    CtiCCConfirmationStats& setWeeklyCommSuccessPercent(DOUBLE value);
    CtiCCConfirmationStats& setMonthlyCommSuccessPercentId(LONG pointId);
    CtiCCConfirmationStats& setMonthlyCommSuccessPercent(DOUBLE value); 

    CtiCCConfirmationStats& incrementAllCommCounts(LONG attempts);
    CtiCCConfirmationStats& incrementAllCommFails(LONG errors);
    CtiCCConfirmationStats& incrementMonthlyCommCounts(LONG attempts);
    CtiCCConfirmationStats& incrementMonthlyCommFails(LONG errors);
    CtiCCConfirmationStats& incrementWeeklyCommCounts(LONG attempts);
    CtiCCConfirmationStats& incrementWeeklyCommFails(LONG errors);
    CtiCCConfirmationStats& incrementDailyCommCounts(LONG attempts);
    CtiCCConfirmationStats& incrementDailyCommFails(LONG errors);
    CtiCCConfirmationStats& incrementUserDefCommCounts(LONG attempts);
    CtiCCConfirmationStats& incrementUserDefCommFails(LONG errors);
    


    DOUBLE calculateSuccessPercent(capcontrol::ccStatsType type);
    BOOL setSuccessPercentPointId(LONG tempPointId, LONG tempPointOffset);
    CtiCCConfirmationStats& createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

    void printCommStats();
    
    CtiCCConfirmationStats* replicate() const;


    CtiCCConfirmationStats& operator=(const CtiCCConfirmationStats& right);

    int operator==(const CtiCCConfirmationStats& right) const;
    int operator!=(const CtiCCConfirmationStats& right) const;

  
        
private:

    LONG _paoid;

    LONG _userDefCommCount;
	LONG _userDefCommFail;
	LONG _dailyCommCount;
	LONG _dailyCommFail;
	LONG _weeklyCommCount;	
	LONG _weeklyCommFail;
	LONG _monthlyCommCount;
	LONG _monthlyCommFail;

    LONG    _userDefCommSuccessPercentId;
    DOUBLE  _userDefCommSuccessPercent;
    LONG    _dailyCommSuccessPercentId;
    DOUBLE  _dailyCommSuccessPercent;
    LONG    _weeklyCommSuccessPercentId;
    DOUBLE  _weeklyCommSuccessPercent;
    LONG    _monthlyCommSuccessPercentId;
    DOUBLE  _monthlyCommSuccessPercent;
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCConfirmationStats* CtiCCConfirmationStatsPtr;
