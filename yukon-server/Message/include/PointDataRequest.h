#pragma once

#include "yukon.h"
#include "ctitime.h"

#include <list>
#include <map>
#include <set>
#include <boost/shared_ptr.hpp>

struct PointValue{
    double value;
    unsigned quality;
    CtiTime timestamp;
};

enum PointRequestType
{
    CbcRequestType,
    RegulatorRequestType,
    OtherRequestType,
    BusPowerRequestType
};

typedef std::map<long,PointValue> PointValueMap;
typedef std::map<PointRequestType,std::set<long>> PointRequestTypeToPointIdMap;

class IM_EX_MSG PointRequest
{
    public:
        PointRequest(long pointId, PointRequestType requestType, bool requestLatestValue = true)
        {
            this->pointId = pointId;
            this->pointRequestType = requestType;
            this->requestLatestValue = requestLatestValue;
        }

        bool operator <(const PointRequest& b) const
        {
            return pointId < b.pointId;
        }

        long pointId;
        PointRequestType pointRequestType;
        bool requestLatestValue;
};

class IM_EX_MSG PointDataRequest
{
    public:
        virtual bool watchPoints(const std::set<PointRequest>& points)=0;
        virtual bool isComplete()=0;
        virtual bool hasRequestType(PointRequestType pointRequestType)=0;
        virtual float ratioComplete(PointRequestType pointRequestType)=0;
        virtual PointValueMap getPointValues()=0;
        virtual PointValueMap getPointValues(PointRequestType pointRequestType)=0;
        virtual std::string createStatusReport()=0;
        virtual std::set<long> getMissingPoints()=0;
        virtual PointValueMap getRejectedPointValues()=0;
        virtual bool isPointStale(long pointId, CtiTime & staleTime)=0;
        virtual unsigned requestSize() = 0;
};

typedef boost::shared_ptr<PointDataRequest> PointDataRequestPtr;
