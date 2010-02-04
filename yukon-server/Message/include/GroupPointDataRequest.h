#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"

#include "MessageListener.h"
#include "message.h"
#include "DispatchConnection.h"
#include "dlldefs.h"
#include "ctitime.h"

#include <list>
#include <set>
#include <map>
#include <boost/shared_ptr.hpp>

struct PointValue{
    double value;
    unsigned quality;
    CtiTime timestamp;
};

typedef std::map<long,PointValue> PointValueMap;

class IM_EX_MSG GroupPointDataRequest : public MessageListener
{
    public:
        GroupPointDataRequest(DispatchConnectionPtr connection);
        ~GroupPointDataRequest();

        bool watchPoints(std::list<long> points);
        bool isComplete();
        PointValueMap getPointValues();

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);

    private:
        DispatchConnectionPtr _connection;

        std::set<long> _points;
        PointValueMap _values;

        bool _complete;
        bool _pointsSet;
};

typedef boost::shared_ptr<GroupPointDataRequest> GroupRequestPtr;
