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

        virtual bool watchPoints(const std::set<long>& points, const std::set<long>& requestPoints);
        virtual bool isComplete();
        virtual PointValueMap getPointValues();

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);
        virtual void reportStatusToLog();

        void setDispatchConnection(DispatchConnectionPtr connection);

    private:
        DispatchConnectionPtr _connection;

        std::set<long> _points;
        PointValueMap _values;
        PointValueMap _rejectedValues;

        bool _complete;
        bool _pointsSet;
};

typedef boost::shared_ptr<DispatchPointDataRequest> DispatchPointDataRequestPtr;
