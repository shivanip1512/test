/*---------------------------------------------------------------------------
        Filename:  ccstatsobject.cpp
        
        Programmer:  Julie Richter
                
        Description:    CCStatsObject
                        

        Initial Date:  2/24/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#include "yukon.h"
#include "ccstatsobject.h"
 

CCStatsObject::CCStatsObject()
{
    _opCount          = 0;
    _total            = 0;
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
    if( _opCount > 0 )
    {
        return _opCount / _total;
    }
    else
    {
        return 100;
    }
}
