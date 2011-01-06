#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"

namespace Cti {
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

  //RWDECLARE_COLLECTABLE( CtiCCOperationStats )


    CtiCCOperationStats();
    CtiCCOperationStats(const CtiCCOperationStats& cap);

    virtual ~CtiCCOperationStats();

    void init();

    LONG getPAOId() const;
    LONG getUserDefOpCount() const;
    LONG getUserDefConfFail() const;
    LONG getDailyOpCount() const;
    LONG getDailyConfFail() const;
    LONG getWeeklyOpCount() const;
    LONG getWeeklyConfFail() const;
    LONG getMonthlyOpCount() const;
    LONG getMonthlyConfFail() const;
    LONG    getUserDefOpSuccessPercentId() const;
    DOUBLE  getUserDefOpSuccessPercent() const;
    LONG    getDailyOpSuccessPercentId() const;
    DOUBLE  getDailyOpSuccessPercent() const;
    LONG    getWeeklyOpSuccessPercentId() const;
    DOUBLE  getWeeklyOpSuccessPercent() const;
    LONG    getMonthlyOpSuccessPercentId() const;
    DOUBLE  getMonthlyOpSuccessPercent() const;

    
    CtiCCOperationStats& setPAOId(LONG paoId);
    CtiCCOperationStats& setUserDefOpCount(LONG value);
    CtiCCOperationStats& setUserDefConfFail(LONG value);
    CtiCCOperationStats& setDailyOpCount(LONG value);
    CtiCCOperationStats& setDailyConfFail(LONG value);
    CtiCCOperationStats& setWeeklyOpCount(LONG value);
    CtiCCOperationStats& setWeeklyConfFail(LONG value);
    CtiCCOperationStats& setMonthlyOpCount(LONG value);
    CtiCCOperationStats& setMonthlyConfFail(LONG value);
    CtiCCOperationStats& incrementAllOpCounts();
    CtiCCOperationStats& incrementAllOpFails();
    CtiCCOperationStats& incrementMonthlyOpCounts();
    CtiCCOperationStats& incrementMonthlyOpFails();
    CtiCCOperationStats& incrementWeeklyOpCounts();
    CtiCCOperationStats& incrementWeeklyOpFails();
    CtiCCOperationStats& incrementDailyOpCounts();
    CtiCCOperationStats& incrementDailyOpFails();
    CtiCCOperationStats& setUserDefOpSuccessPercentId(LONG pointId);
    CtiCCOperationStats& setUserDefOpSuccessPercent(DOUBLE value);
    CtiCCOperationStats& setDailyOpSuccessPercentId(LONG pointId);
    CtiCCOperationStats& setDailyOpSuccessPercent(DOUBLE  value);
    CtiCCOperationStats& setWeeklyOpSuccessPercentId(LONG pointId);
    CtiCCOperationStats& setWeeklyOpSuccessPercent(DOUBLE value);
    CtiCCOperationStats& setMonthlyOpSuccessPercentId(LONG pointId);
    CtiCCOperationStats& setMonthlyOpSuccessPercent(DOUBLE value); 
    


    DOUBLE calculateSuccessPercent(capcontrol::ccStatsType type);
    BOOL setSuccessPercentPointId(LONG tempPointId, LONG tempPointOffset);
    CtiCCOperationStats& createPointDataMsgs(CtiMultiMsg_vec& pointChanges);

    void printOpStats();
    BOOL isDirty();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);


    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);
    CtiCCOperationStats* replicate() const;


    CtiCCOperationStats& operator=(const CtiCCOperationStats& right);

    int operator==(const CtiCCOperationStats& right) const;
    int operator!=(const CtiCCOperationStats& right) const;

  
        
private:

    LONG _paoid;

    LONG _userDefOpCount;
	LONG _userDefConfFail;
	LONG _dailyOpCount;
	LONG _dailyConfFail;
	LONG _weeklyOpCount;	
	LONG _weeklyConfFail;
	LONG _monthlyOpCount;
	LONG _monthlyConfFail;

    LONG    _userDefOpSuccessPercentId;
    DOUBLE  _userDefOpSuccessPercent;
    LONG    _dailyOpSuccessPercentId;
    DOUBLE  _dailyOpSuccessPercent;
    LONG    _weeklyOpSuccessPercentId;
    DOUBLE  _weeklyOpSuccessPercent;
    LONG    _monthlyOpSuccessPercentId;
    DOUBLE  _monthlyOpSuccessPercent;
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCOperationStats* CtiCCOperationStatsPtr;
