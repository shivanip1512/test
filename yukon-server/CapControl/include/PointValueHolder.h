#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "pointtypes.h"
#include "msg_pdata.h"

#include <map>

using std::map;

class PointValueHolder
{
    public:
        PointValueHolder();

        virtual void addPointValue(int pointId, double pointValue);
        virtual void updatePointValue(CtiPointDataMsg* message);

        bool getPointValue(int pointId, double& result);

    private:

        typedef std::map<int,double> ValueMap;
        typedef ValueMap::iterator ValueMapItr;

        ValueMap _valueMap;

};


