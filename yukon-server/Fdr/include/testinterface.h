#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <iostream>

using std::iostream;

#include "ctitime.h"

#include "msg_pdata.h"

#include "fdrinterface.cpp"     //  re:  including .cpp files...  see gripe/info in fdrinterface.h

class CtiTestInterface : public CtiFDRInterface<CtiMessage>
{
public:
    CtiTestInterface( string destination ) : CtiFDRInterface<CtiMessage>( "TESTINTERFACE", destination )
        {   };

    void runInterface( void )
    {
        run( );
    }

private:    

protected:
    void _outgoingThread( void );

public:

};
