#pragma once

#include "yukon.h"
#include "ctitime.h"

#include <list>
#include <map>
#include <boost/shared_ptr.hpp>

struct PointValue{
    double value;
    unsigned quality;
    CtiTime timestamp;
};

typedef std::map<long,PointValue> PointValueMap;

class IM_EX_MSG PointDataRequest
{
    public:
        virtual bool watchPoints(const std::set<long>& points, const std::set<long>& requestPoints)=0;
        virtual bool isComplete()=0;
        virtual PointValueMap getPointValues()=0;
        virtual void reportStatusToLog()=0;
};

typedef boost::shared_ptr<PointDataRequest> PointDataRequestPtr;
