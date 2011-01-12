#pragma once

#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"

class CCStatsObject
{
public:
    CCStatsObject();
    ~CCStatsObject();

    LONG getOpCount();
    void setOpCount(LONG val);
    LONG getFailCount();
    void setFailCount(LONG val);
    void incrementOpCount(LONG val);
    DOUBLE getTotal();
    void setTotal(DOUBLE val);
    void incrementTotal(DOUBLE val);

    DOUBLE getAverage();

    CCStatsObject& operator=(const CCStatsObject& right);


private:

    LONG _opCount;       
    DOUBLE _total;  
    LONG _failCount;  
};

