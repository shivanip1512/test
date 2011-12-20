#pragma once

#include "yukon.h"

#include "MessageListener.h"
#include "message.h"
#include "DispatchConnection.h"
#include "dlldefs.h"
#include "ctitime.h"
#include "PointDataRequest.h"

#include <list>
#include <set>
#include <map>
#include <boost/shared_ptr.hpp>

class IM_EX_MSG DispatchPointDataRequest : public PointDataRequest, public MessageListener
{
    public:
        DispatchPointDataRequest();
        ~DispatchPointDataRequest();

        virtual bool watchPoints(const std::set<PointRequest>& points);
        virtual bool isComplete();
        virtual bool hasRequestType(PointRequestType pointRequestType);
        virtual float ratioComplete(PointRequestType pointRequestType);
        virtual PointValueMap getPointValues();
        virtual PointValueMap getPointValues(PointRequestType pointRequestType);
        virtual void removePointValue(long pointId);

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);
        virtual void reportStatusToLog();
        virtual std::set<long> getMissingPoints();
        virtual PointValueMap getRejectedPointValues();
        virtual void updateRejectedValues();
        virtual bool isPointDataNew(long pointId, PointValue pv);
        void setDispatchConnection(DispatchConnectionPtr connection);

    private:
        DispatchConnectionPtr _connection;

        PointRequestTypeToPointIdMap _requestTypeToPointId;

        std::set<long> _points;
        PointValueMap _values;
        PointValueMap _rejectedValues;

        bool _complete;
        bool _pointsSet;
};

typedef boost::shared_ptr<DispatchPointDataRequest> DispatchPointDataRequestPtr;
