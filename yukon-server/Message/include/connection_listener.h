#pragma once

#include <string>
#include "dlldefs.h"
#include "critical_section.h"
#include "connection.h"

class IM_EX_MSG CtiListenerConnection : public Cti::Messaging::BaseConnection
{
    const std::string _serverQueueName;

    boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> _connection;

    std::auto_ptr<cms::Session>                            _session;
    std::auto_ptr<cms::Destination>                        _clientReplyDest;
    std::auto_ptr<Cti::Messaging::ActiveMQ::QueueConsumer> _consumer;

    CtiCriticalSection _closeConnectionMux;

    bool _closed;
    bool _valid;

    static volatile long _listenerConnectionCount;

    const std::string _title;

    void logStatus    ( std::string funcName, std::string note ) const;
    void logDebug     ( std::string funcName, std::string note ) const;
    void logException ( std::string fileName, int line, std::string exceptionName = "", std::string note = "" ) const;

    void releaseResources();
    void closeConnection();

public:

    CtiListenerConnection( const std::string &serverQueueName );
    virtual ~CtiListenerConnection();

    void start();
    virtual void close();

    bool verifyConnection();
    bool acceptClient();

    boost::shared_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> getConnection() const;

    std::auto_ptr<cms::Destination> getClientReplyDest() const;

    std::string who() const;
    std::string getServerQueueName() const;
};



