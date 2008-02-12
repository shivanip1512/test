

/*---------------------------------------------------------------------------
        Filename:  ccoperationstats.h
        
        Programmer:  Julie Richter
                
        Description:    Header file for CtiCCOperationStats
                        CtiCCOperationStats maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CtiCCOperationStatsIMPL_H
#define CtiCCOperationStatsIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>


#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"


                
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

    
    CtiCCOperationStats& setPAOId(LONG paoId);
    CtiCCOperationStats& setUserDefOpCount(LONG value);
    CtiCCOperationStats& setUserDefConfFail(LONG value);
    CtiCCOperationStats& setDailyOpCount(LONG value);
    CtiCCOperationStats& setDailyConfFail(LONG value);
    CtiCCOperationStats& setWeeklyOpCount(LONG value);
    CtiCCOperationStats& setWeeklyConfFail(LONG value);
    CtiCCOperationStats& setMonthlyOpCount(LONG value);
    CtiCCOperationStats& setMonthlyConfFail(LONG value);


    BOOL isDirty();
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    void restore(RWDBReader& rdr);
    void setDynamicData(RWDBReader& rdr);
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

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCOperationStats* CtiCCOperationStatsPtr;
#endif
