#include "precompiled.h"

#include "DispatchPointDataRequest.h"
#include "msg_signal.h"
#include "pointdefs.h"
#include "logger.h"
#include "PointDataRequest.h"
#include "std_helper.h"
#include "desolvers.h"

#include <boost/range/algorithm/set_algorithm.hpp>
#include <boost/range/adaptor/map.hpp>

using Cti::Logging::Set::operator<<;
using std::endl;

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

/*
    This whole class needs a rethink.  I believe it should just be a container for all the point data messages
    that match the requested point IDs.  All of the special processing for accepted/rejected points etc. should
    be done somewhere else (for example an IVVCPointDataRequest).
*/
void DispatchPointDataRequest::processNewMessage(CtiMessage* message)
{
    //Run though this even if complete.. that way we will have the most current value whenever the data is collected.
    if ( _pointsSet && ( message->isA() == MSG_POINTDATA ) )
    {
        CtiPointDataMsg * pData = static_cast<CtiPointDataMsg*>(message);
        long pointId = pData->getId();

        if ( _points.find(pointId) != _points.end() )   // are we watching this point?
        {
            PointValue pv;
            pv.timestamp = pData->getTime();
            pv.quality = pData->getQuality();
            pv.value = pData->getValue();

            bool newPointId = ( _values.find( pointId ) == _values.end() );

            if ( pv.quality == NormalQuality || pv.quality == ManualQuality )
            {
                if ( newPointId || pv.timestamp >= _values[ pointId ].timestamp )
                {
                    _values[ pointId ] = pv;

                    if ( newPointId )
                    {
                        _rejectedValues.erase( pointId );
                    }
                }
            }
            else
            {
                if ( newPointId )
                {
                    if ( _rejectedValues.find( pointId ) == _rejectedValues.end() || pv.timestamp >= _rejectedValues[ pointId ].timestamp )
                    {
                        _rejectedValues[pointId] = pv;
                    }
                }
            }

            _complete = ( _values.size() == _points.size() );
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


/*
   A non-zero number of point IDs of a particular request type exist within the point request.
*/
bool DispatchPointDataRequest::hasRequestType(PointRequestType pointRequestType)
{
    PointRequestTypeToPointIdMap::iterator itr = _requestTypeToPointId.find(pointRequestType);

    return itr != _requestTypeToPointId.end() && itr->second.size() > 0;
}


float DispatchPointDataRequest::ratioComplete(PointRequestType pointRequestType)
{
    if ( hasRequestType(pointRequestType) )
    {
        PointRequestTypeToPointIdMap::iterator itr = _requestTypeToPointId.find(pointRequestType);

        const float total = itr->second.size();
        int complete = 0;

        for each ( long pointId in itr->second )
        {
            if ( _values.find(pointId) != _values.end() )
            {
                ++complete;
            }
        }

        return complete / total;
    }

    return 0.0;
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

bool DispatchPointDataRequest::isPointStale( long pointId, CtiTime & staleTime )
{
    if ( _values.count(pointId) && _values[pointId].timestamp <= staleTime )
    {
        _rejectedValues.insert( { pointId, _values[pointId] } );
        _values.erase( pointId );
        return true;
    }
    return false;
}

std::set<long> DispatchPointDataRequest::getMissingPoints()
{
    std::vector<long> seenValues;

    seenValues.reserve(_values.size() + _rejectedValues.size());

    boost::range::set_union(
        _values | boost::adaptors::map_keys,
        _rejectedValues | boost::adaptors::map_keys,
        std::inserter(seenValues, seenValues.begin()));

    std::set<long> missingIds;

    boost::range::set_difference(
        _points,
        seenValues,
        std::inserter(missingIds, missingIds.begin()));

    return missingIds;
}

PointValueMap DispatchPointDataRequest::getRejectedPointValues()
{
    return _rejectedValues;
}

std::string DispatchPointDataRequest::createStatusReport()
{
    Cti::StreamBuffer outLog;

    outLog << endl;
    outLog << " Point Data Request Status: " << endl;
    outLog << " -------------------------- " << endl;

    //Missing Values    
    const auto & missingPoints = getMissingPoints();
    if( ! missingPoints.empty() )
    {
        outLog << " Points missing: " << endl;

        outLog << " " << missingPoints << endl;

        outLog << endl;
    }

    //Rejected Values    
    if( _rejectedValues.size() )
    {
        outLog << " Points Received but rejected: " << endl;

        for each (PointValueMap::value_type pv in _rejectedValues)
        {
            outLog << " Point Id: " << pv.first
                << " Quality: " << desolvePointQuality(static_cast<PointQuality_t>(pv.second.quality))
                << " Value: " << pv.second.value
                << " Timestamp: " << pv.second.timestamp << endl;
        }
        outLog << endl;
    }

    //Accepted values
    if ( _values.size() )
    {
        outLog << " Points Received and accepted: " << endl;

        for each (PointValueMap::value_type pv in _values)
        {
            outLog << " Point Id: " << pv.first
                << " Quality: " << desolvePointQuality(static_cast<PointQuality_t>(pv.second.quality))
                << " Value: " << pv.second.value
                << " Timestamp: " << pv.second.timestamp << endl;
        }
    }

    return outLog;
}
