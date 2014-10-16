#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>

#include "queue.h"
#include "netports.h"
#include "message.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_pcreturn.h"
#include "collectable.h"

#include "connection_client.h"
#include "amq_constants.h"

void main( int argc, char **argv )
{
    if(argc != 3)
    {
        cout << "Arg 1:   this app's registration name" << endl;
        cout << "Arg 2:   # of loops to receive data  " << endl;

        exit(-1);
    }

    try
    {
        CtiClientConnection Connect( Cti::Messaging::ActiveMQ::Queue::dispatch );

        Connect.setName( "SignalSinkTest" );
        Connect.start();

        // Send registration message
        Connect.WriteConnQue( new CtiRegistrationMsg(argv[1], rwThreadId(), FALSE ));

        // Register for a few points which "test.exe" will change.
        Connect.WriteConnQue( new CtiPointRegistrationMsg(REG_EVENTS | REG_ALARMS | REG_NO_UPLOAD ));

        for( int imsg_cnt=0; imsg_cnt < atoi(argv[2]); imsg_cnt++ )
        {
            boost::scoped_ptr<CtiMessage> imsg( Connect.ReadConnQue() );

            if( imsg )
            {
                if(imsg->isA() == MSG_MULTI)
                {
                    cout << CtiTime() << " Received multi message." << endl;
                    CtiMultiMsg *pChg = dynamic_cast<CtiMultiMsg*>(imsg.get());
                    for each( CtiMessage* x in pChg->getData() )
                    {
                        cout << x->toString();
                    }
                }
                else if(imsg->isA() == MSG_SIGNAL)
                {
                    CtiSignalMsg* pSig = dynamic_cast<CtiSignalMsg*>(imsg.get());
                    cout << CtiTime() << " **** SIGNAL RECEIVED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    cout << pSig->toString();
                }
            }
            else
            {
                cout << CtiTime() << " Received unexpected null message." << endl;;
                break;
            }
        }

        cout << CtiTime() << " Ending Test." << endl;

        // Send shutdown request
        Connect.WriteConnQue( new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15 ));
        Connect.close();
    }
    catch(...)
    {
        cout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    exit(0);
}
