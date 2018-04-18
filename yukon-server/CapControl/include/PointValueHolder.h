#pragma once

#include "yukon.h"
#include "pointtypes.h"
#include "PointDataRequest.h"   // for the PointValue struct
#include "pointdefs.h"

class CtiTime;
class CtiPointDataMsg;


class PointValueHolder
{
public:

    PointValueHolder() {    }

    void addPointValue( const int pointId,
                        const double pointValue,
                        const CtiTime & pointTime,
                        const PointQuality_t pointQuality );

    void updatePointValue( const CtiPointDataMsg & message );

    bool getPointValue( const int pointId, double & result ) const;
    bool getPointTime( const int pointId, CtiTime & time ) const;

    PointValue getCompletePointInfo( const int pointId ) const;

private:

    void add( const int pointId,
              const double pointValue,
              const CtiTime & pointTime,
              const unsigned pointQuality );

    typedef std::map<int, PointValue> ValueMap;

    ValueMap _valueMap;
};

