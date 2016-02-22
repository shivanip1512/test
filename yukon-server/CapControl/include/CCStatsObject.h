#pragma once


class CCStatsObject
{
public:
    CCStatsObject();
    ~CCStatsObject();

    long getOpCount() const;
    long getFailCount() const;

    double getAverage();

    void addSample( const double sample );

    CCStatsObject& operator=(const CCStatsObject& right);

private:

    void incrementOpCount(long val);
    void incrementTotal(double val);

    long _opCount;       
    double _total;  
    long _failCount;  
};

