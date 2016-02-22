#include "precompiled.h"

#include "ccstatsobject.h"



long CCStatsObject::getOpCount() const
{
    return count( accumulator );
}

long CCStatsObject::getFailCount() const
{
    return ( ( 100.0 - getAverage() ) / 100.0 ) * getOpCount();
}

double CCStatsObject::getAverage() const
{
    return getOpCount() > 0
            ? mean( accumulator )
            : 100.0;
}

void CCStatsObject::addSuccessSample( const double sample )
{
    accumulator( sample );
}

