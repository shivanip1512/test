#include "precompiled.h"

#include "DispatchConnection.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "collectable.h"

using std::string;

DispatchConnection::DispatchConnection()
{

}

DispatchConnection::DispatchConnection( const string& connectionName, const int &port, const string &host, Que_t *inQ, int tt) :
    CtiConnection(port,host,inQ,tt)
{
    Inherited::setName(connectionName);
}

void DispatchConnection::registerForPoint(MessageListener* listener, long pointId)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    if (_registeredPoints.find(pointId) == _registeredPoints.end())
    {
        _addList.insert(pointId);
    }

    return;
}

void DispatchConnection::registerForPoints(MessageListener* listener, const std::set<long>& pointIds)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    for each (int pointId in pointIds)
    {
        if (_registeredPoints.find(pointId) == _registeredPoints.end())
        {
            _addList.insert(pointId);
        }
    }

    return;
}

void DispatchConnection::unRegisterForPoint(MessageListener* listener, long pointId)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    _registeredPoints.erase(pointId);
    _removeList.insert(pointId);
}

void DispatchConnection::unRegisterForPoints(MessageListener* listener, const std::set<long>& pointIds)
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    for each (int pointId in pointIds)
    {
        _registeredPoints.erase(pointId);
        _removeList.insert(pointId);
    }

    return;
}

void DispatchConnection::requestPointValues(const std::set<long>& pointIds)
{
    CtiCommandMsg* cmdMsg = new CtiCommandMsg();
    cmdMsg->setOperation(CtiCommandMsg::PointDataRequest);

    CtiCommandMsg::CtiOpArgList_t points;
    for each (int pointId in pointIds)
    {
        points.push_back(pointId);
    }

    cmdMsg->setOpArgList(points);
    WriteConnQue(cmdMsg);
}

/**
 * This is the outthreads call to us to do our thing. Grab all
 * registration points and create a single registration message.
 */
void DispatchConnection::preWork()
{
    CtiLockGuard< CtiMutex > guard(_regListMux);

    if (_removeList.size() > 0)
    {
        CtiPointRegistrationMsg* msg = new CtiPointRegistrationMsg(REG_REMOVE_POINTS);
        for each (long pointId in _removeList)
        {
            msg->insert(pointId);
            _registeredPoints.erase(pointId);
        }

        WriteConnQue(msg);
    }

    if (_addList.size() > 0)
    {
        int flag = REG_ADD_POINTS;//By default we will register as an add
        if (_registeredPoints.size() == 0)
        {
            //This is a first time registration.
            flag = REG_NOTHING;
        }

        CtiPointRegistrationMsg* msg = new CtiPointRegistrationMsg(flag);
        for each (long pointId in _addList)
        {
            msg->insert(pointId);
            _registeredPoints.insert(pointId);
        }

        WriteConnQue(msg);

        //If REG_ADD_POINTS follow up with point request message
        if (flag == REG_ADD_POINTS)
        {
            requestPointValues(_addList);
        }
    }

    _removeList.clear();
    _addList.clear();
}

void DispatchConnection::writeIncomingMessageToQueue(CtiMessage* msgPtr)
{
    if (msgPtr == NULL)
    {
        return;
    }

    if (msgPtr->isA() == MSG_MULTI)
    {
        CtiMultiMsg* multi = (CtiMultiMsg*)msgPtr;
        for each (RWCollectable* rwcol in multi->getData())
        {
            //These are all messages.
            CtiMessage* msg = (CtiMessage*)rwcol;
            writeIncomingMessageToQueue(msg->replicateMessage());
        }
    }
    else
    {
        CtiLockGuard< CtiMutex > guard(_listenerMux);
        for each (MessageListener* listener in _messageListeners)
        {
            if (listener != NULL)
            {
                //Listener is now responsible for the memory.
                listener->processNewMessage(msgPtr->replicateMessage());
            }
        }
    }

    delete msgPtr;
    msgPtr = NULL;
}

void DispatchConnection::addMessageListener(MessageListener* messageListener)
{
    CtiLockGuard< CtiMutex > guard(_listenerMux);
    _messageListeners.insert(messageListener);
}

void DispatchConnection::removeMessageListener(MessageListener* messageListener)
{
    CtiLockGuard< CtiMutex > guard(_listenerMux);
    _messageListeners.erase(messageListener);
}
