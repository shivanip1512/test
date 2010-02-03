#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"

#include "MessageListener.h"
#include "message.h"
#include "DispatchConnection.h"
#include "dlldefs.h"

#include <list>
#include <set>
#include <map>

struct PointIdValue{
    int pointId;
    double value;
};

class IM_EX_MSG GroupPointDataRequest : public MessageListener
{
    public:
        GroupPointDataRequest(DispatchConnectionPtr connection);
        ~GroupPointDataRequest();

        bool watchPoints(std::list<long> points);
        bool isComplete();
        std::map<long,double> getPointValues();

        //MessageListener
        virtual void processNewMessage(CtiMessage* message);

    private:
        DispatchConnectionPtr _connection;

        std::set<long> _points;
        std::map<long,double> _values;

        bool _complete;
        bool _pointsSet;
};
