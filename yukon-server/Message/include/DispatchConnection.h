#pragma once

#include "connection.h"
#include "message.h"
#include "MessageListener.h"

#include <boost/shared_ptr.hpp>
#include <list>
#include <set>
#include <map>
#include <string>

/**
 * This class is an extension of the default connection class to
 * allow for grouping of point registration messages without the
 * client having to group them up or even know what a point
 * registration message is.
 *
 * @author Thain Spar (12/10/2009)
 */
class IM_EX_MSG DispatchConnection : public CtiConnection
{
    private:

        mutable CtiMutex _regListMux;
        mutable CtiMutex _listenerMux;

        std::set<long> _addList;
        std::set<long> _removeList;
        std::set<long> _registeredPoints;

        std::set<MessageListener*> _messageListeners;
        //typedef std::map<MessageListener*,std::set<long> > MessageListenerMap;
        //MessageListenerMap _messageListeners;

        //Hidden
        DispatchConnection();
    public:

        typedef CtiConnection Inherited;

        DispatchConnection(const std::string &connectionName, const int &port, const std::string &host, Que_t *inQ = NULL, int tt = 3);

        virtual void preWork();
        virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);

        void registerForPoints(MessageListener* listener, const std::set<long>& pointIds);
        void registerForPoint(MessageListener* listener, long pointId);
        void unRegisterForPoints(MessageListener* listener, const std::set<long>& pointIds);
        void unRegisterForPoint(MessageListener* listener, long pointId);

        void requestPointValues(const std::set<long>& pointIds);

        void addMessageListener(MessageListener* messageListener);
        void removeMessageListener(MessageListener* messageListener);
};

typedef boost::shared_ptr<DispatchConnection> DispatchConnectionPtr;
