#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __FDRIODISPATCH_H__
#define __FDRIODISPATCH_H__

#include "message.h"
#include "connection.h"

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
    CtiConnection *_dispatchConn;
    BOOL _dbChange;

public:

};

#endif  //  #ifndef __FDRIODISPATCH_H__
