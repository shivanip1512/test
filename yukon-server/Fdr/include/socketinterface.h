#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __SOCKETINTERFACE_H__
#define __SOCKETINTERFACE_H__

#include <rw/toolpro/inetaddr.h>
//#include <rw/toolpro/sockport.h>
//#include <rw/toolpro/portstrm.h>
//#include <rw/toolpro/winsock.h>
#include <rw/cstring.h>

using namespace std;

#include "dlldefs.h"
#include "fdrinterface.h"

class CtiMessage;
class RWInetAddr;
class RWSocketListener;


class IM_EX_FDRBASE CtiFDRSocketInterface : public CtiFDRInterface
{                                    
    typedef CtiFDRInterface Inherited;

    public:

        CtiFDRSocketInterface(RWCString & interfaceType, int myPortNumber);
        ~CtiFDRSocketInterface();

        // we need to imlement this this interface
        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );

        virtual BOOL    init(void);
        virtual BOOL    run(void);
        BOOL            stop(void);
        BOOL            halt(void);
    
        bool    getConnecton(RWInetAddr * myInetAddress, RWSocketListener * myListener);

    protected:

        //void interfaceInputThread( void )
        //{};
        //void interfaceOutputThread( void )
        //{};

    private:
        long                iPortNumber;
        RWInetPort      *   iInetPort;
        RWInetAddr      *   iInetAddr;
        RWSocketListener*   iSockListener;
        RWSocketPortal      iSock;
        RWSocketPortal  *   iPortal;
        RWPortalStreambuf * iSInBuf;
        RWPortalStreambuf * iSOutBuf;
        RWpostream        * iOutStream;
        RWpistream        * iInStream;
    
};

#endif // #ifndef __SOCKETINTERFACE_H__
