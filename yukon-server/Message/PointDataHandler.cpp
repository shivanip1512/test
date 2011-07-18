#include "precompiled.h"

#include "PointDataHandler.h"
#include "msg_ptreg.h"

using std::set;

PointDataHandler::PointDataHandler()
{
}

void PointDataHandler::clear()
{
    _pointIdMap.clear();
    _paoIdMap.clear();
}

bool PointDataHandler::addPoint(int pointId, int paoId)
{
    PointIdMapItr pointItr = _pointIdMap.find(pointId);
    if (pointItr == _pointIdMap.end())
    {
        set<int> newSet;
        newSet.insert(paoId);

        _pointIdMap[pointId] = newSet;
    }
    else
    {
        //Do we care if it fails? Means we re-regitered..
        pointItr->second.insert(paoId);
    }

    PaoIdMapItr paoItr = _paoIdMap.find(paoId);
    if (paoItr == _paoIdMap.end())
    {
        set<int> newSet;
        newSet.insert(pointId);

        _paoIdMap[paoId] = newSet;
    }
    else
    {
        //Do we care if it fails? Means we re-regitered..
        paoItr->second.insert(pointId);
    }

    registerForPoint(pointId);

    return true;
}

bool PointDataHandler::removePointOnPao(int pointId, int paoId)
{
    //Remove Pao from point set
    PointIdMapItr pointItr = _pointIdMap.find(pointId);
    if (pointItr != _pointIdMap.end())
    {
        pointItr->second.erase(paoId);

        //Check if the set is empty.
        if (pointItr->second.size() == 0)
        {
            _pointIdMap.erase(pointId);
            unRegisterForPoint(pointId);
        }
    }

    //Remove point from pao set
    PaoIdMapItr paoItr = _paoIdMap.find(paoId);
    if (paoItr != _paoIdMap.end())
    {
        paoItr->second.erase(pointId);
    }

    return true;
}

bool PointDataHandler::removeAllPointsForPao(int paoId)
{
    //Find all the points this pao is registered for
    PaoIdMapItr paoItr = _paoIdMap.find(paoId);
    std::list<int> removeSets;

    if (paoItr != _paoIdMap.end())
    {
        //Remove the reference to this pao for each point.
        for each(int pointId in paoItr->second)
        {
            PointIdMapItr pointItr = _pointIdMap.find(pointId);
            if (pointItr != _pointIdMap.end())
            {
                pointItr->second.erase(paoId);

                //Check if the set is empty.
                if (pointItr->second.size() == 0)
                {
                    removeSets.push_back(pointId);
                }
            }
        }
        //Remove this pao
        _paoIdMap.erase(paoId);
    }

    for each (int i in removeSets)
    {
        _pointIdMap.erase(i);
        unRegisterForPoint(i);
    }

    return true;
}

bool PointDataHandler::removePointId(int pointId)
{
    //Find all Paos this point is referenced in.
    PointIdMapItr pointItr = _pointIdMap.find(pointId);
    if (pointItr != _pointIdMap.end())
    {
        //For each Pao, remove it's reference under the point sets.
        for each (int paoId in pointItr->second)
        {
            PaoIdMapItr paoItr = _paoIdMap.find(paoId);
            if (paoItr != _paoIdMap.end())
            {
                paoItr->second.erase(pointId);
            }
        }
        _pointIdMap.erase(pointId);
        unRegisterForPoint(pointId);
    }

    return true;
}

/**
 * Calls into the paoHandler to give it the Point Data message.
 *
 * @param message
 *
 * @return bool
 */
bool PointDataHandler::processIncomingPointData(CtiPointDataMsg* message)
{
    PointDataListener* listener = NULL;
    int pointId = message->getId();

    PointIdMapItr pointItr = _pointIdMap.find(pointId);
    if (pointItr != _pointIdMap.end())
    {
        //Get each object from the store and call its handle function.
        for each (int paoId in pointItr->second)
        {
            _pointDataListener->handlePointDataByPaoId(paoId,message);
        }

        return true;
    }

    return false;
}

/**
 *
 *
 * @param message
 */
void PointDataHandler::processNewMessage(CtiMessage* message)
{

}

/**
 * Returns a list of all point id's being tracked.
 *
 * @return std::list<int>
 */
void PointDataHandler::getAllPointIds(std::set<long>& pointIds)
{
    for (PointIdMapItr itr = _pointIdMap.begin(); itr != _pointIdMap.end(); itr++)
    {
        pointIds.insert(itr->first);
    }
}

/**
 * Sets the Handler, not responsible for deleting the old one.
 *
 * @param pointDataListener
 */
void PointDataHandler::setPointDataListener(PointDataListener* pointDataListener)
{
    _pointDataListener = pointDataListener;
}
