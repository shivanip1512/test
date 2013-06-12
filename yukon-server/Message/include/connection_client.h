#pragma once

#include "connection.h"

class IM_EX_MSG CtiClientConnection : public CtiConnection
{
    const std::string _brokerUri;
    const std::string _serverQueueName;

    std::auto_ptr<cms::Connection> _connection;

    std::auto_ptr<boost::thread> _brokerConnThread;
    boost::mutex                 _brokerConnMutex;
    bool                         _brokerConnStarted;
    
    void brokerConnThread      ();
    bool startBrokerConnection ();
 
    std::auto_ptr<CtiRegistrationMsg>      _regMsg;
    std::auto_ptr<CtiPointRegistrationMsg> _ptRegMsg;

    void recordRegistration      ( const CtiMessage& msg );
    void recordPointRegistration ( const CtiMessage& msg );
    void writeRegistration       ();

    virtual void messagePeek ( const CtiMessage& msg );

    virtual INT  establishConnection ();
    virtual void endConnection       ();
    virtual void cleanUp             ();

    std::auto_ptr<cms::ExceptionListener> _exceptionListener;

    void onException ( const cms::CMSException& ex );

    static volatile long _clientConnectionCount;

public:

    CtiClientConnection ( const std::string &serverQueueName,
                          Que_t *inQ = NULL,
                          INT tt = 3 );

    virtual ~CtiClientConnection ();
};
