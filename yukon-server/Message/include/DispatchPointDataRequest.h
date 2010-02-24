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

        virtual bool watchPoints(std::list<long> points);
        virtual bool isComplete();
        virtual PointValueMap getPointValues();

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);

        void setDispatchConnection(DispatchConnectionPtr connection);

    private:
        DispatchConnectionPtr _connection;

        std::set<long> _points;
        PointValueMap _values;

        bool _complete;
        bool _pointsSet;
};

typedef boost::shared_ptr<DispatchPointDataRequest> DispatchPointDataRequestPtr;
