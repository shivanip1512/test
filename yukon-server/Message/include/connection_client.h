#pragma once

#include "connection.h"

class IM_EX_MSG CtiClientConnection : public CtiConnection
{
    const std::string _serverQueueName;

    boost::scoped_ptr<Cti::Messaging::ActiveMQ::ManagedConnection> _connection;

    std::auto_ptr<CtiRegistrationMsg>      _regMsg;
    std::auto_ptr<CtiPointRegistrationMsg> _ptRegMsg;

    void recordRegistration      ( const CtiMessage& msg );
    void recordPointRegistration ( const CtiMessage& msg );
    void writeRegistration       ();

    virtual void messagePeek ( const CtiMessage& msg );

    virtual bool establishConnection ();
    virtual void abortConnection     ();
    virtual void deleteResources     ();

    std::auto_ptr<cms::ExceptionListener> _exceptionListener;

    void onException ( const cms::CMSException& ex );

    static volatile long _clientConnectionCount;

public:

    CtiClientConnection ( const std::string &serverQueueName,
                          Que_t *inQ = NULL,
                          INT tt = 3 );

    virtual ~CtiClientConnection ();
};
