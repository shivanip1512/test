#include "yukon.h"

#include <rw/thr/condtion.h>
#include <rw/thr/barrier.h>
#include <rw/thr/thrfunc.h>

#include "dlldefs.h"

#include "fdrinterface.h"
#include "testinterface.h"

vector<CtiTestInterface *> *tivec;

static int globl = 0;

#ifdef __cplusplus
extern "C" {
#endif

    DLLEXPORT int CreateInterface( RWDBConnection *dbConn )
    {

        //  grab the FDR points
        RWCString sql;
        sql =    "SELECT UNIQUE INTERFACETYPE, DESTINATION";
        sql +=   " FROM FDR_TRANSLATION";
        sql +=   " WHERE INTERFACETYPE='TESTINTERFACE'";

        RWDBResult  results     = dbConn->executeSql( sql );
        RWDBTable   resTable    = results.table( );
        RWDBReader  rdr         = resTable.reader( );

        tivec = new vector<CtiTestInterface *>;

        RWCString interfacetype, destination;

        //  iterate through them all
        for( int i = 0; rdr( ); i++ )
        {
            //  grab the point information from the database and stuff it into our class
            rdr["INTERFACETYPE"] >> interfacetype;
            rdr["DESTINATION"]   >> destination;

            sql =    "SELECT *";
            sql +=   " FROM FDR_TRANSLATION";
            sql +=   " WHERE INTERFACETYPE='" + interfacetype + "'";
            sql +=       " AND DESTINATION='" + destination + "'";

            RWDBResult  subresults     = dbConn->executeSql( sql );
            RWDBTable   subresTable    = subresults.table( );
            RWDBReader  subrdr         = subresTable.reader( );

            tivec->push_back( new CtiTestInterface( destination ) );

            //  iterate through them all
            for( int j = 0; subrdr( ); j++ )
            {
                long pointid;
                RWCString destination, directiontype, translation;

                //  grab the point information from the database and stuff it into our class
                subrdr["POINTID"]       >> pointid;
                subrdr["DESTINATION"]   >> destination;
                subrdr["DIRECTIONTYPE"] >> directiontype;
                subrdr["TRANSLATION"]   >> translation;

                cout << pointid << " " << interfacetype << " " << destination << " " << directiontype << " " << translation << endl;
                ((*tivec)[i])->appendPoint( pointid, destination, directiontype, translation );
            }
            if( j == 0 )
            {
                delete tivec->back( );
                tivec->pop_back( );
            }
        }

        if( i == 0 )
        {
            cout << "No entries in database for INTERFACETYPE=\'" << interfacetype << "\', ";
            cout << "quitting..." << endl;
            delete tivec;
            return FALSE;
        }

        return TRUE;
    }

    DLLEXPORT int InitInterface( void )
    {
        int size = tivec->size( );
        for( int i = 0; i < size; i++ )
            ((*tivec)[i])->init( );
        return TRUE;
    }

    DLLEXPORT void GetSuicideTools( RWCondition *suicideVar, RWBarrier *barrier )
    {
        int size = tivec->size( );
        for( int i = 0; i < size; i++ )
            ((*tivec)[i])->submitSuicideTools( suicideVar, barrier );
    }

    DLLEXPORT int RunInterface( void )
    {
        RWThreadFunction interfaceFunc;
        int size = tivec->size( );
        for( int i = 0; i < size; i++ )
        {
            interfaceFunc = rwMakeThreadFunction( *((*tivec)[i]), CtiTestInterface::runInterface );
            interfaceFunc.start( );
        }
        return TRUE;
    }

//  kill this whenever the barrier and suicide signal work.
    DLLEXPORT int hitman( int *sig )
    {
        int size = tivec->size( );
        for( int i = 0; i < size; i++ )
            ((*tivec)[i])->hitman( sig );
        return TRUE;
    }


#ifdef __cplusplus
}
#endif


void CtiTestInterface::_outgoingThread( void )
{
    RWRunnableSelf _pSelf = rwRunnable( );
    CtiMessage *msg;
    int i = globl++;
    cout << RWTime( ) << " - _outgoingThread( )" << endl;
    //  while there's something to receive (otherwise, recv( ) returns NULL)
    try
    {
        //  loop, checking for cancellation, until the I/O class is ready
        while( !_FDRIODispatchPtr->ready( ) )
        {
            _pSelf.serviceCancellation( );
            rwSleep(200);
        }
        for( ; ; )
        {
            //  check for cancellation
            _pSelf.serviceCancellation( );

            //  try to read a message
            if( msg = _FDRIODispatchPtr->recv( 200 ) )
            {
                cout << "    received ";
                switch( msg->isA( ) )
                {
                case MSG_POINTDATA:
                    cout << "point data: [" << ((CtiPointDataMsg *)msg)->getId( )
                    << ", " << ((CtiPointDataMsg *)msg)->getValue( ) << "]" << endl;
                    break;
                default:
                    cout << "message of unknown message type \"" << msg->stringID( ) << "\", discarding..." << endl;
                    break;
                }
                delete msg;
            }
        }
    }
    catch( RWCancellation &cancellationMsg )
    {
        cout << "cancellation..." << endl;
        return;
    }
}

