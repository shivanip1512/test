#pragma once

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>

#include <rw/thr/thread.h>
#include <rw/thr/prodcons.h>

#include "mc.h"
#include "clientconn.h"
#include "observe.h"
#include "guard.h"
#include "logger.h"
#include "msg_multi.h"
#include "queue.h"

class CtiMCClientListener : public CtiThread, public CtiObserver
{
public:
    CtiMCClientListener(UINT port);
    ~CtiMCClientListener();

    // Send a message to all attached clients
    void BroadcastMessage(CtiMessage* msg, void *Connection = NULL);

    // check all out connections to see if they
    // are still good
    void checkConnections();

    // Inherited from CtiObserver
    // Instances of CtiMCClientListener register
    // themselves as observers each time they
    // create a connection
    virtual void update(CtiObservable& observable);

    // Inherited from CtiThread
    virtual void run();
    virtual void interrupt(int id );

    // If this is set then all the messages
    // collected from the connections will
    // be put into this queue.. a little hackish but oh well
    void setQueue(CtiQueue< CtiMessage, std::greater<CtiMessage> >* queue );

    friend std::ostream& operator<<( std::ostream& ostrm, CtiMCClientListener& listener );

protected:

private:
    RWSocketListener* _listener;

    UINT _port;

    std::vector<CtiMCConnection*> _connections;
    RWMutexLock _connmutex;

    volatile bool _doquit;

    // collectables written to this queue will be
    // broadcast to all the connections
    CtiPCPtrQueue< RWCollectable > _broadcast_queue;

    CtiQueue< CtiMessage, std::greater<CtiMessage> >* _conn_in_queue;

    bool removeInvalidConnections(CtiMCConnection& conn);
};
