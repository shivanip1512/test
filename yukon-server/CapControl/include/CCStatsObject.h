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

    long getOpCount();
    void setOpCount(long val);
    long getFailCount();
    void setFailCount(long val);
    void incrementOpCount(long val);
    double getTotal();
    void setTotal(double val);
    void incrementTotal(double val);

    double getAverage();

    CCStatsObject& operator=(const CCStatsObject& right);


private:

    long _opCount;       
    double _total;  
    long _failCount;  
};

