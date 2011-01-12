#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "pointtypes.h"
#include "ctitime.h"
#include "msg_pdata.h"

#include <map>

class PointValueHolder
{
    public:
        PointValueHolder();

        virtual void addPointValue(int pointId, double pointValue, CtiTime pointTime);
        virtual void updatePointValue(CtiPointDataMsg* message);

        bool getPointValue(int pointId, double& result) const;
        bool getPointTime(int pointId, CtiTime& time) const;

        PointValueHolder& operator=(const PointValueHolder& right);

    private:

        struct ValueTimePair
        {
            double value;
            CtiTime time;
        };

        typedef std::map<int,ValueTimePair> ValueMap;
        typedef ValueMap::const_iterator ValueMapItr;

        ValueMap _valueMap;
};
