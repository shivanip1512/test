#include "yukon.h"

#include "DispatchConnection.h"
#include "msg_ptreg.h"
#include "collectable.h"


DispatchConnection::DispatchConnection() : _registered(false)
{

}

DispatchConnection::DispatchConnection( const string& connectionName, const int &port, const string &host, Que_t *inQ, int tt) :
    CtiConnection(port,host,inQ,tt),
    _registered(false)
{
    Inherited::setName(connectionName);
}

void DispatchConnection::registerForPoint(int pointId)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    _addList.push_back(pointId);
}

void DispatchConnection::registerForPoints(const std::list<int>& pointIds)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    for each( int pointId in pointIds)
    {
        _addList.push_back(pointId);
    }
    return;
}

void DispatchConnection::unRegisterForPoint(int pointId)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    _removeList.push_back(pointId);
}

void DispatchConnection::unRegisterForPoints(const std::list<int>& pointIds)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    for each( int pointId in pointIds)
    {
        _removeList.push_back(pointId);
    }

    return;
}

/**
 * This is the outthreads call to us to do our thing. Grab all
 * registration points and create a single registration message.
 */
void DispatchConnection::preWork()
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    if (_addList.size() > 0)
    {
        int flag = REG_ADD_POINTS;//By default we will register as an add
        if (!_registered)
        {
            //This is a first time registration.
            flag = REG_NOTHING;
            _registered = true;
        }

        CtiPointRegistrationMsg* msg = new CtiPointRegistrationMsg(flag);
        for each(int pointId in _addList)
        {
            msg->insert(pointId);
        }
        _addList.clear();

        //If REG_ADD_POINTS follow up with point request message
        WriteConnQue(msg);
    }

    if (_removeList.size() > 0)
    {
        CtiPointRegistrationMsg* msg = new CtiPointRegistrationMsg(REG_REMOVE_POINTS);
        for each(int pointId in _removeList)
        {
            msg->insert(pointId);
        }
        _removeList.clear();

        WriteConnQue(msg);
    }
}
