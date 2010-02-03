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

struct PointValue{
    double value;
    unsigned quality;
    CtiTime timestamp;
};

class IM_EX_MSG GroupPointDataRequest : public MessageListener
{
    public:
        GroupPointDataRequest(DispatchConnectionPtr connection);
        ~GroupPointDataRequest();

        bool watchPoints(std::list<long> points);
        bool isComplete();
        std::map<long,PointValue> getPointValues();

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);

    private:
        DispatchConnectionPtr _connection;

        std::set<long> _points;
        std::map<long,PointValue> _values;

        bool _complete;
        bool _pointsSet;
};
