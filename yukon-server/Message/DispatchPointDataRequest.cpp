#include "yukon.h"

#include "DispatchPointDataRequest.h"
#include "msg_signal.h"
#include "pointdefs.h"
#include "logger.h"
#include "PointDataRequest.h"

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
        //Quality is not in both signal and pdata...
        if (message->isA() == MSG_POINTDATA)
        {
            CtiPointDataMsg* pData = (CtiPointDataMsg*)message;
            long pointId = pData->getId();

            if (_points.find(pointId) != _points.end())
            {
                PointValue pv;
                pv.timestamp = pData->getTime();
                pv.quality = pData->getQuality();
                pv.value = pData->getValue();

                if (pv.quality == NormalQuality || pv.quality == ManualQuality)
                {
                    _values[pointId] = pv;
                }
                else
                {
                    _rejectedValues[pointId] = pv;
                }

                //Check if we are done. and set flag
                if (_values.size() == _points.size())
                {
                    _complete = true;
                }
            }//else: a point we don't care about.
        }
    }

    delete message;
}

bool DispatchPointDataRequest::watchPoints(const std::set<PointRequest>& pointRequests)
{
    if (_connection.get() != NULL)
    {
        if (_pointsSet == true)
        {
            //Already called.
            return false;
        }

        std::set<long> points;
        std::set<long> requestPoints;

        for each (PointRequest pointRequest in pointRequests)
        {
            points.insert(pointRequest.pointId);

            if (pointRequest.requestLatestValue == true)
            {
                requestPoints.insert(pointRequest.pointId);
            }

            PointRequestTypeToPointIdMap::iterator requestTypeItr;
            if ((requestTypeItr = _requestTypeToPointId.find(pointRequest.pointRequestType)) != _requestTypeToPointId.end())
            {
                (requestTypeItr->second).insert(pointRequest.pointId);
            }
            else
            {
                std::set<long> typePointIds;
                typePointIds.insert(pointRequest.pointId);
                _requestTypeToPointId[pointRequest.pointRequestType] = typePointIds;
            }
        }

        _connection->registerForPoints(this,points);
        _points.insert(points.begin(),points.end());

        _pointsSet = true;

        _connection->requestPointValues(requestPoints);
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

float DispatchPointDataRequest::ratioComplete(PointRequestType pointRequestType)
{
    PointRequestTypeToPointIdMap::iterator itr = _requestTypeToPointId.find(pointRequestType);

    int complete = 0;
    int total = itr->second.size();

    if (total != 0)
    {
        for each (long pointId in itr->second)
        {
            PointValueMap::iterator valueItr = _values.find(pointId);
            if (valueItr != _values.end())
            {
                ++complete;
            }
        }

        return complete/(float)total;
    }
    else //Avoiding a divide by zero.
    {
        return 0.0;
    }

}

PointValueMap DispatchPointDataRequest::getPointValues()
{
    return _values;
}

PointValueMap DispatchPointDataRequest::getPointValues(PointRequestType pointRequestType)
{
    PointValueMap pointMap;

    PointRequestTypeToPointIdMap::iterator typeItr = _requestTypeToPointId.find(pointRequestType);

    if (typeItr == _requestTypeToPointId.end())
    {
        return pointMap;
    }

    std::set<long> pointIds = typeItr->second;
    for each (long pointId in pointIds)
    {
        PointValueMap::iterator itr = _values.find(pointId);
        if (itr != _values.end())
        {
            pointMap[pointId] = itr->second;
        }
    }

    return pointMap;
}

void DispatchPointDataRequest::reportStatusToLog()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() <<" Point Data Request Status: " << endl;
    dout << CtiTime() <<" -------------------------- " << endl;

    //Missing Values
    dout << CtiTime() <<" Points missing: " << endl;
    for each (long pointId in _points)
    {
        PointValueMap::iterator itr = _values.find(pointId);
        if (itr != _values.end())
        {
            break;
        }
        else
        {
            dout <<  pointId << " ";
        }
    }
    dout << endl;

    //Rejected Values
    dout << CtiTime() <<" Points Received but rejected: " << endl;
    for each (PointValueMap::value_type pv in _rejectedValues)
    {
        dout << CtiTime() << " Point Id: " << pv.first
                          << " Quality: "  << pv.second.quality
                          << " Timestamp: " << pv.second.timestamp << endl;
    }
    dout << endl;
    //Accepted values
    dout << CtiTime() <<" Points Received and accepted: " << endl;
    for each (PointValueMap::value_type pv in _values)
    {
        dout << CtiTime() << " Point Id: " << pv.first
                          << " Quality: "  << pv.second.quality
                          << " Timestamp: " << pv.second.timestamp << endl;
    }
    dout << endl;
}
