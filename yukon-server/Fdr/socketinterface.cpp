#include "yukon.h"

#include <windows.h>
//#include <limits.h>
//#include <iostream>

//using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

//#include <rw/toolpro/neterr.h>
//#include <rw\thr\mutex.h>
//#include <rw/toolpro/inetaddr.h>
//#include <rw/toolpro/sockport.h>
//#include <rw/toolpro/portstrm.h>
//#include <rw/toolpro/winsock.h>
//#include <rw/pstream.h>

#include "socketinterface.h"

//DLLIMPORT extern RWMutexLock coutMux;

// constructors
CtiFDRSocketInterface::CtiFDRSocketInterface(RWCString & interfaceType, int myPortNumber)
:   CtiFDRInterface(interfaceType),
    iPortNumber(myPortNumber)
{
}


CtiFDRSocketInterface::~CtiFDRSocketInterface( )
{   
/*    try
    {
        delete _sInBuf;
        delete _sOutBuf;
        delete _oStream;
        delete _iStream;
        try
        {
            delete _portal;
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() != RWNETNOTINITIALISED)
            {
                cout << "Socket Error :" << msg.errorNumber() << " occurred" << endl;
                cout << "  " << msg.why() << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        cout << endl << "Socket interface deletion failed: " ;
        cout << msg.why() << endl;
        throw;
    }
*/
    
} // end destructor

BOOL CtiFDRSocketInterface::init()
{
//    inetPort = RWInetPort(_portNumber);
//    inetAddr = RWInetAddr(_inetPort);
//    sockListener = new RWSocketListener(_inetAddr);
    
//    portal = (*sockListener)();
    
//    try
//    {
/*    //  -- output --
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
*/    //  -- output --
    //  -- input --
        
                
//        _sInBuf  = new RWPortalStreambuf(_portal);
//        _sOutBuf = new RWPortalStreambuf(_portal);
//        _oStream = new RWpostream(_sOutBuf);
//        _iStream = new RWpistream(_sInBuf);
//        do
//        {
//            (*_iStream) >> ch;
//            cout << ch << flush;
//        } while( ch != 'q' && _portal.socket().valid( ) );
    //  -- input --
//    }
//    catch(RWxmsg& msg )
//    {
//        cout RWTime( ) << " - " << __FILE__ << "(" << __LINE__ << ") - RW I/O stream creation failed: " << msg.why() << endl;
//        msg.raise();
//    }
    return TRUE;
}


BOOL CtiFDRSocketInterface::run()
{
    RWWinSockInfo       info;
    RWInetAddr *        aInetAddress = 0;
    RWSocketListener *  aListener = 0;

    Inherited::run();

    // THIS WILL CHANGE -- FOR DEBUGGING
    getConnecton(aInetAddress, aListener);


    return TRUE;
}
        
BOOL CtiFDRSocketInterface::stop()
{
    return FALSE;
}


BOOL CtiFDRSocketInterface::halt()
{
    return FALSE;
}


// our version of this method
bool CtiFDRSocketInterface::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    return FALSE;
}



/*---------------------------------------------------------------------------
    listen
    
    Listens for a connection
---------------------------------------------------------------------------
*/
bool CtiFDRSocketInterface::getConnecton(RWInetAddr * myInetAddress, RWSocketListener * myListener)
{  
    RWSocketPortal          myPortal;


    try
    {
        myListener = new RWSocketListener( RWInetAddr( (int) iPortNumber )  );

        for ( ; ; )
        {
            
            // blocks until connected
            myPortal = (*myListener)();

        }
    } 
    
    
    catch ( RWxmsg& msg )
    {
        {    
            cerr << "CtiFDRSocketInterface::listen - " << msg.why() << endl;
        }
    }

    return TRUE;
}

