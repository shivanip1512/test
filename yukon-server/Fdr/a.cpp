#include "yukon.h"
#include <rw/toolpro/inetaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/portstrm.h>
#include <rw/toolpro/winsock.h>
#include <rw/pstream.h>
#include <iostream.h>
#include <conio.h>
//using namespace std;

#define A_PORTNUMBER 1510

void main( int argc, char **argv )    
{
    long                _portNumber;
    RWInetPort          _inetPort;
    RWInetAddr          _inetAddr;
    RWSocketListener    *_sockListener;
    RWSocketPortal      *_portalPtr, 
                        _portal;
    RWPortalStreambuf   *_sInBuf, 
                        *_sOutBuf;
    RWpostream          *_oStream;
    RWpistream          *_iStream;
    char ch;

    RWWinSockInfo info;
    
    if( argc > 1 )
    {
        RWSocketPortal psck(RWInetAddr(A_PORTNUMBER, argv[1]));
                
        _sInBuf  = new RWPortalStreambuf(psck);
        _sOutBuf = new RWPortalStreambuf(psck);
        _oStream = new RWpostream(_sOutBuf);
        _iStream = new RWpistream(_sInBuf);
        do
        {
            ch = (char)getch( );
            (*_oStream) << ch;
            _oStream->vflush( );
        } while( ch != 'q' );
    }
    else
    {
        //  to set up connections
        _inetAddr = RWInetAddr(RWInetPort(A_PORTNUMBER));
        _sockListener = new RWSocketListener(_inetAddr);
                
        //  to accept connections
        _portal = (*_sockListener)();
                
        _sInBuf  = new RWPortalStreambuf(_portal);
        _sOutBuf = new RWPortalStreambuf(_portal);
        _oStream = new RWpostream(_sOutBuf);
        _iStream = new RWpistream(_sInBuf);
        do
        {
            (*_iStream) >> ch;
            cout << ch << flush;
        } while( ch != 'q' && _portal.socket().valid( ) );
    }
}
