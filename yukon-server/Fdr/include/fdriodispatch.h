#pragma once

#include "message.h"
#include "connection_client.h"

#include "fdrio.cpp"

class CtiFDRIODispatch : public CtiFDRIO<CtiMessage>
{
public:
    CtiFDRIODispatch( CtiConnection *dispatchConn ) : _dispatchConn(dispatchConn)
        {   };

private:
    void _inThread( void );
    void _outThread( void );

protected:
    CtiClientConnection *_dispatchConn;
    BOOL _dbChange;
};
