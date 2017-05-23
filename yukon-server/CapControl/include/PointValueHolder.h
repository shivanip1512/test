#pragma once

#include "yukon.h"
#include "pointtypes.h"
#include "ctitime.h"

class CtiPointDataMsg;


class PointValueHolder
{
public:

    PointValueHolder() {    }

    virtual void addPointValue( const int pointId, const double pointValue, const CtiTime & pointTime );
    virtual void updatePointValue( const CtiPointDataMsg & message );

    bool getPointValue( const int pointId, double & result ) const;
    bool getPointTime( const int pointId, CtiTime & time ) const;

private:

    struct ValueTimePair
    {
        double value;
        CtiTime time;
    };

    typedef std::map<int, ValueTimePair> ValueMap;

    ValueMap _valueMap;
};

