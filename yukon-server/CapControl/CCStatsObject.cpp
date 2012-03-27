/*---------------------------------------------------------------------------
        Filename:  ccstatsobject.cpp

        Programmer:  Julie Richter

        Description:    CCStatsObject


        Initial Date:  2/24/2009

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

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


long CCStatsObject::getOpCount()
{
    return _opCount;
}
void CCStatsObject::setOpCount(long val)
{
    _opCount = val;
}

long CCStatsObject::getFailCount()
{
    return _failCount;
}
void CCStatsObject::setFailCount(long val)
{
    _failCount = val;
}

void CCStatsObject::incrementOpCount(long val)
{
    _opCount += val;
}
double CCStatsObject::getTotal()
{
    return _total;
}
void CCStatsObject::setTotal(double val)
{
    _total = val;

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
