#pragma once

#include "connection.h"


class IM_EX_MSG CtiClientConnection : public CtiConnection
{
    const std::string _serverQueueName;

    std::unique_ptr<CtiRegistrationMsg>      _regMsg;
    std::unique_ptr<CtiPointRegistrationMsg> _ptRegMsg;

    CtiMutex _abortConnMux;
    bool     _canAbortConn;
    std::atomic_bool _reconnected;

    void recordRegistration      ( const CtiRegistrationMsg& msg );
    void recordPointRegistration ( const CtiPointRegistrationMsg& msg );
    void writeRegistration       ();

    void messagePeek ( const CtiMessage& msg ) override;

    bool establishConnection () override;
    void abortConnection     () override;

    std::unique_ptr<Cti::Messaging::Qpid::QueueProducer>    _handshakeProducer;

public:

    void on_session_open(proton::session& s) override;

    CtiClientConnection ( const std::string &serverQueueName,
                          Que_t *inQ = NULL,
                          int termSeconds = 3 );

    virtual ~CtiClientConnection ();

    bool hasReconnected();
};
