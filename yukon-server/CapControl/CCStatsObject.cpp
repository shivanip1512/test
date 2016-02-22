#include "precompiled.h"

#include "ccstatsobject.h"


CCStatsObject::CCStatsObject()
{
    _opCount          = 0;
    _total            = 0;
    _failCount        = 0;
};
CCStatsObject::~CCStatsObject()
{
};

CCStatsObject& CCStatsObject::operator=(const CCStatsObject& right)
{
    if( this != &right )
    {
        _opCount =  right._opCount;
        _total   =  right._total;
        _failCount = right._failCount;
    }
    return *this;
}


long CCStatsObject::getOpCount() const
{
    return _opCount;
}

long CCStatsObject::getFailCount() const
{
    return _failCount;
}

void CCStatsObject::incrementOpCount(long val)
{
    _opCount += val;
}

void CCStatsObject::incrementTotal(double val)
{
    _total += val;
}

double CCStatsObject::getAverage()
{
    double avg = 100;
    if( _opCount > 0 )
    {
        avg = _total / _opCount;
    }
    _failCount = ((100 - avg) / 100 ) * _opCount;

    return avg;
}

void CCStatsObject::addSample( const double sample )
{
    incrementTotal( sample );
    incrementOpCount( 1 );
}

