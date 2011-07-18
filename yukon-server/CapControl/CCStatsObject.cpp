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


LONG CCStatsObject::getOpCount()
{
    return _opCount;
}
void CCStatsObject::setOpCount(LONG val)
{
    _opCount = val;
}

LONG CCStatsObject::getFailCount()
{
    return _failCount;
}
void CCStatsObject::setFailCount(LONG val)
{
    _failCount = val;
}

void CCStatsObject::incrementOpCount(LONG val)
{
    _opCount += val;
}
DOUBLE CCStatsObject::getTotal()
{
    return _total;
}
void CCStatsObject::setTotal(DOUBLE val)
{
    _total = val;

}
void CCStatsObject::incrementTotal(DOUBLE val)
{
    _total += val;
}

DOUBLE CCStatsObject::getAverage()
{
    DOUBLE avg = 100;
    if( _opCount > 0 )
    {
        avg = _total / _opCount;
    }
    _failCount = ((100 - avg) / 100 ) * _opCount;

    return avg;
}
