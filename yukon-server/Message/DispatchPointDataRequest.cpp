#include "yukon.h"

#include "DispatchPointDataRequest.h"
#include "msg_signal.h"
#include "pointdefs.h"

DispatchPointDataRequest::DispatchPointDataRequest() :
    _complete(false),
    _pointsSet(false)
{
}

DispatchPointDataRequest::~DispatchPointDataRequest()
{
    if (_connection.get() != NULL)
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

void DispatchPointDataRequest::setDispatchConnection(DispatchConnectionPtr connection)
{
    _connection = connection;
    _connection->addMessageListener(this);
}

void DispatchPointDataRequest::processNewMessage(CtiMessage* message)
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

bool DispatchPointDataRequest::watchPoints(std::list<long> points)
{
    if (_connection.get() != NULL)
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
    else
    {
        return false;
    }
}

bool DispatchPointDataRequest::isComplete()
{
    return _complete;
}

std::map<long,PointValue> DispatchPointDataRequest::getPointValues()
{
    return _values;
}
