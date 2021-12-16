#pragma once

#include "connection.h"


class IM_EX_MSG CtiServerConnection : public CtiConnection
{
    virtual bool establishConnection ();

    const std::string _replyDest;

public:

    void on_session_open( proton::session & s ) override;
 
    CtiServerConnection( const std::string & clientReplyDestination,
                         const std::string & serverQueueName,
                         Que_t *inQ = NULL,
                         int termSeconds = 3 );

    virtual ~CtiServerConnection();
};
