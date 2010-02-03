#include "yukon.h"

#include "GroupPointDataRequest.h"
#include "msg_signal.h"
#include "pointdefs.h"

GroupPointDataRequest::GroupPointDataRequest(DispatchConnectionPtr connection) : _complete(false), _pointsSet(false)
{
    _connection = connection;
    _connection->addMessageListener(this);
}

GroupPointDataRequest::~GroupPointDataRequest()
{
    if (_connection->valid() == 1)
    {
        std::list<long> points;
        for each (long pointId in _points)
        {
            points.push_back(pointId);
        }

        _connection->removeMessageListener(this);
        //Don't do this until unregister is fixed to not unregister if more than one listener is listening.
        //_connection->unRegisterForPoints(this,points);
    }
}

void GroupPointDataRequest::processNewMessage(CtiMessage* message)
{
    bool processMessage = false;

    //Run though this even if complete.. that way we will have the most current value whenever the data is collected.
    if (_pointsSet == true)
    {
        long pointId;
        double pointValue;
        unsigned pointQuality;
        CtiTime pointTime;

        //Quality is not in both signal and pdata...
        if (message->isA() == MSG_POINTDATA)
        {
            CtiPointDataMsg* pData = (CtiPointDataMsg*)message;
            pointQuality = pData->getQuality();


            if (pointQuality == NormalQuality || pointQuality == ManualQuality)
            {
                pointId = pData->getId();
                pointValue = pData->getValue();
                pointTime = pData->getTime();

                if (_points.find(pointId) != _points.end())
                {
                    PointValue pv;
                    pv.timestamp = pointTime;
                    pv.quality = pointQuality;
                    pv.value = pointValue;

                    _values[pointId] = pv;

                    //Check if we are done. and set flag
                    if (_values.size() == _points.size())
                    {
                        _complete = true;
                    }
                }//else: a point we don't care about.
            }
        }
    }

    delete message;
}

bool GroupPointDataRequest::watchPoints(std::list<long> points)
{
    if (_pointsSet == true)
    {
        //Already called.
        return false;
    }

    _connection->registerForPoints(this,points);

    for each (int point in points)
    {
        _points.insert(point);
    }

    _pointsSet = true;

    return true;
}

bool GroupPointDataRequest::isComplete()
{
    return _complete;
}

std::map<long,PointValue> GroupPointDataRequest::getPointValues()
{
    return _values;
}
