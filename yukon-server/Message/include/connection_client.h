#pragma once

#include "connection.h"

class IM_EX_MSG CtiClientConnection : public CtiConnection
{
    const std::string _serverQueueName;

    std::unique_ptr<CtiRegistrationMsg>      _regMsg;
    std::unique_ptr<CtiPointRegistrationMsg> _ptRegMsg;

    CtiMutex _abortConnMux;
    bool     _canAbortConn;

    void recordRegistration      ( const CtiMessage& msg );
    void recordPointRegistration ( const CtiMessage& msg );
    void writeRegistration       ();

    virtual void messagePeek ( const CtiMessage& msg );

    virtual bool establishConnection ();
    virtual void abortConnection     ();

    std::unique_ptr<cms::ExceptionListener> _exceptionListener;

    void onException ( const cms::CMSException& ex );

public:

    CtiClientConnection ( const std::string &serverQueueName,
                          Que_t *inQ = NULL,
                          int termSeconds = 3 );

    virtual ~CtiClientConnection ();
};
