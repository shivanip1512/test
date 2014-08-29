#pragma once

#include "thread.h"
#include "connection_listener.h"
#include "clientconn.h"


class CtiMCClientListener : public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiMCClientListener(const CtiMCClientListener&);
    CtiMCClientListener& operator=(const CtiMCClientListener&);

public:
    CtiMCClientListener();
    ~CtiMCClientListener();

    // Send a message to all attached clients
    void BroadcastMessage(CtiMessage* msg, void *Connection = NULL);

    // check all out connections to see if they
    // are still good
    void checkConnections();

    // Inherited from CtiThread
    virtual void run();
    virtual void interrupt( int id );

    // If this is set then all the messages
    // collected from the connections will
    // be put into this queue.. a little hackish but oh well
    void setQueue( CtiConnection::Que_t* queue );

private:

    CtiListenerConnection _listenerConnection;

    typedef boost::ptr_vector<CtiMCConnection> ConnectionVec;

    ConnectionVec _connections;
    CtiMutex _connmutex;

    volatile bool _doquit;

    CtiConnection::Que_t* _conn_in_queue;

    void removeAllConnections();
};
