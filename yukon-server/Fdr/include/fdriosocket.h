#include <iostream>

#include "ctitime.h"

#include "fdrio.h"

template <class T>
class CtiFDRIOSocket : public CtiFDRIO<T>
{
public:
    CtiFDRIOSocket::CtiFDRIOSocket( std::string &destination, long portNumber, 
                                    FDRSocketNum socketStyle = FDRSingleSocket )
        : CtiFDRIO<T>( ), _destination(destination), _portNumber(portNumber), _socketStyle(socketStyle)
    {
        _inPortal = _outPortal = NULL;
        _connectionThreadFunc = rwMakeThreadFunction( *this, &CtiFDRIOSocket<T,CB>::_connectionThread );
        _connectionThreadFunc.start( );
    };
    
    ~CtiFDRIOSocket( );

private:
    std::string           _destination;       //  the destination IP
    long                _portNumber;        //  the socket's port#
    enum FDRSocketNum   _socketStyle;       //  one or two sockets for the connection
    RWInetPort          *_inetPort;
    RWInetAddr          *_inetAddr;
    RWSocketListener    *_sockListener;
    RWSocketPortal      *_inPortal,
                        *_outPortal,
                        *_submittedPortal;
    RWPortalStreambuf   *_sInBuf, 
                        *_sOutBuf;
    RWpostream          *_oStream;
    RWpistream          *_iStream;
    RWSemaphore         _inSockSem,
                        _outSockSem,
                        _submittedSockSem;
    
    void _inThread( void );
    void _outThread( void );
    void _connectionThread( void );
    RWThreadFunction    _connectionThreadFunc;

protected:
    //  these functions are to be overridden if the communications protocol used can send and receive
    //    objects of different sizes.

    virtual int _idSizeof( char *buf )  //  this function should return a size based off of
        {                               //    first few (4 will be available) bytes of buf.  
            return sizeof( T );         //    sizeof was already taken, so i decided that it was
        };                              //    returning the "size of" an object based off of an "id"...
    
    virtual T *_prepareInput( char *buf )   //  this function should convert buf into its T equivalent.
        {                                   
            T *tmp = new T = (T)(*buf);
            delete buf;
            return tmp;
        };
    
    virtual char *_prepareOutput( T *toOutput ) //  this function should convert a T into its socket
        {                                       //    transport equivalent.
            char *tmp = malloc( sizeof( T ) );
            memcpy( (void *)tmp, (void *)toOutput, sizeof( T ) );
            delete toOutput;
            return tmp;
        };

public:
    void submitSocketPortal( RWSocketPortal *portal );

    enum FDRSocketNum
    {
        FDRSingleSocket,
        FDRDualSocket  
    };
};

