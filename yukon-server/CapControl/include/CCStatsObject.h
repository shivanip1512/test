#pragma once


class CCStatsObject
{
public:
    CCStatsObject();
    ~CCStatsObject();

    long getOpCount();
    long getFailCount();

    void incrementOpCount(long val);
    void incrementTotal(double val);

    double getAverage();

    CCStatsObject& operator=(const CCStatsObject& right);

private:

    long _opCount;       
    double _total;  
    long _failCount;  
};

