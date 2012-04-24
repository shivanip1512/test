/*--------------------------------------------------------------------------------------------*
*
* File:   thread_montior
*
* Date:   9/3/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.26.4.1 $
* DATE         :  $Date: 2008/11/13 17:23:51 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/
#include "precompiled.h"
#include "dllbase.h"
#include "logger.h"
#include "utility.h"
#include "thread_monitor.h"

#define TEN_MINUTES_IN_SECONDS 10*60
using std::pair;
using std::map;
using std::endl;
using namespace boost::posix_time;

/*********************************************************************************************
        Example usage of thread_monitor.
        To find current implementations search for ThreadMonitor.getState() and ThreadMonitor.tickle( data );

        //Thread Monitor (code for thread) Any thread that is to be monitored needs code similar to this.
        CtiTime lastTickleTime, lastReportTime;     //defined above, not right before as you see here
        if(lastTickleTime.seconds() < (lastTickleTime.now().seconds() - CtiThreadMonitor::StandardTickleTime))
        {
            if(lastReportTime.seconds() < (lastReportTime.now().seconds() - CtiThreadMonitor::StandardMonitorTime))
            {
                lastReportTime = lastReportTime.now();
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " RTDB Archiver Thread Active. TID:  " << rwThreadId() << endl;
            }

            CtiThreadRegData *data;
            if( ShutdownOnThreadTimeout )
            {
                data = CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "RTDB Archiver Thread", CtiThreadRegData::Action,
                                                                      CtiThreadMonitor::StandardMonitorTime, &CtiVanGogh::sendbGCtrlC, CTIDBG_new string("RTDB Archiver Thread") );
            }
            else
            {
                data = CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "RTDB Archiver Thread", CtiThreadRegData::None, CtiThreadMonitor::StandardMonitorTime );
            }
            ThreadMonitor.tickle( data );
            lastTickleTime = lastTickleTime.now();
        }
        //End Thread Monitor Section (thread specific)

        Note that any thread who goes away besides on a shutdown should also include a logout command

        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "RTDB Archiver Thread", CtiThreadRegData::LogOut, CtiThreadMonitor::StandardMonitorTime ));

        Now each application needs a polling and reporting section.
        This example was in the DispatchMsgHandlerThread thread. This is a good choice for several reasons, but
        one good reason is that if the communication to dispatch is down, reporting cannot happen anyway, so this thread really
        cannot be monitored. It also uses the connection to dispatch a lot, and so it was a natural fit for our function

        if((LastThreadMonitorTime.now().minute() - LastThreadMonitorTime.minute()) >= 1) //Wait at least 1 minute before each report
        {
            CtiThreadMonitor::State next;
            LastThreadMonitorTime = LastThreadMonitorTime.now();
            if((next = ThreadMonitor.getState()) != previous || checkCount++ >=3) //This means if the value has changed, report every minute, else report every 3
            {
                previous = next;
                checkCount = 0;

                pointMessage.setType(StatusPointType);
                pointMessage.setValue(next);

                pointMessage.setString((ThreadMonitor.getString().c_str()));

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointMessage));
            }
        }

        ***** When a thread goes unreported, it is not removed from the list, threads are only removed upon logout. Also, their
        unreported status, or the action that is set should be reported every 10 minutes after this until they report.

************************************************************************************************/




//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::CtiThreadMonitor() :
_currentState(Normal),
_output("")
{
}

//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::~CtiThreadMonitor()
{
    _queue.clearAndDestroy();
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::run( void )
{
    int cnt = 0;
    long snooze = 1000;

    messageOut( "ts", "Monitor Startup" );

    SetThreadName(-1, "ThreadMon");

    while( !isSet(CtiThread::SHUTDOWN) )
    {
        sleep( snooze );

        if( getDebugLevel() & DEBUGLEVEL_THREAD_MONITOR )
            messageOut( "ts", "Monitor Loop" );  //temp, remove in prod

        snooze = checkForExpriration();

        processQueue();

        if( !gConfigParms.isTrue("YUKON_DISABLE_THREADMONITOR") )
        {

            processExtraCommands();

            processExpired();

            // _queue.clearAndDestroy();
        }
    }

    messageOut( "ts", "Monitor Shutdown" );
}

//===========================================================================================================
// if map is non-empty(), compare now to each tickledTime, mark any expired
// need to loop to look at each entry
//===========================================================================================================

long CtiThreadMonitor::checkForExpriration( void )
{
    ptime current_time( second_clock::local_time() );
    time_duration td( 0, 0, 3, 0 );
    time_duration tempTD( 0, 0, 3, 0 );

    if( _threadData.entries() != 0 )
    {
        for( ThreadData::spiterator i = _threadData.getMap().begin(); i != _threadData.getMap().end(); i++ )
        {
            CtiThreadRegDataSPtr temp = i->second;

            ptime check_time = temp->getTickledTime() + seconds( temp->getTickleFreq() );  //needs validation

            if( current_time > check_time )
            {
                if ( temp->testUnreportedFilter() )
                {
                   temp->setReported( false );
                }
            }
            else
            {
                temp->resetUnreportedFilter();
                //
                //we want to sleep as long as possible, so find the shortest
                //time to the next possible report failure
                //
                tempTD = check_time - current_time;
                if( tempTD < td )
                {
                    td = tempTD;
                }
            }
        }
    }

    if( td.seconds() > 0 )
        return( td.seconds() * 1000 );
    else
        return( 1000 );   //1 second default sleep
}

//===========================================================================================================
// chug thru the queue and add in new entries that we've heard from and mark them as 'updated'
// we're also updating entries that existed previously
//===========================================================================================================

void CtiThreadMonitor::processQueue( void )
{
    while( _queue.entries() )
    {
        //this says it removes the first entry, so we won't C&D later
        CtiThreadRegData *temp = _queue.getQueue();

        if( gConfigParms.isTrue("YUKON_DISABLE_THREADMONITOR") )
        {
            delete temp;
        }
        else
        {
            int tempId = temp->getId();

            ThreadData::ptr_type i = _threadData.find( tempId );

            //Note that currently you are set critical or not at initialization and never again.
            if( i )
            {
                if( !i->getReported() )
                {
                    messageOut( "tsisvs", "Thread W/ID", i->getId(), "", i->getName(), "has reported" );
                    messageOut( "tsisvs", "Thread W/ID", i->getId(), " Last heard from: ", timeString( i->getTickledTime() ),"");
                }
                _threadData.remove(tempId);//smart pointer will delete reg data item!
            }

            ThreadData::insert_pair insertpair;

            //we try to put the element from the queue into the map
            insertpair = _threadData.insert( tempId, temp );

            if( !insertpair.second )
            {   //failed insert, delete the temp. This should be safe.
                delete temp;
            }
        }
    }
}

//===========================================================================================================
// run through map yet again, doing whatever appropriate with the remaining expired threads
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
    State nextState = Normal;
    string nextOutput = "Thread running correctly.";
    try
    {
        for( ThreadData::spiterator i = _threadData.getMap().begin(); i != _threadData.getMap().end(); i++ )
        {
            if( !i->second->getReported())
            {
                CtiThreadRegDataSPtr regData = i->second;

                if( !regData->getActionTaken() ||
                    (regData->getTickledTime() + seconds(TEN_MINUTES_IN_SECONDS*regData->getUnreportedCount())) < ptime(second_clock::local_time()) )
                {
                    regData->setUnreportedCount(regData->getUnreportedCount() + 1);

                    regData->setActionTaken(true);//trying to ensure we dont act twice on an object that does not go away

                    messageOut( "tsisvs", "Thread W/ID", i->first, "", regData->getName(), "Is UNREPORTED" );
                    messageOut( "tsisvs", "Thread W/ID", i->first, " Last heard from: ", timeString( regData->getTickledTime() ),"");

                    int reaction_type = regData->getBehavior();

                    switch( reaction_type )
                    {
                        case CtiThreadRegData::None:
                        case CtiThreadRegData::LogOut:
                            {
                            }
                            break;

                        case CtiThreadRegData::Action:
                            {
                                CtiThreadRegData::BehaviorFunction action1 = regData->getActionFunc();
                                std::string action1_args = regData->getActionArgs();

                                if( action1 )
                                {
                                    //it doesn't matter if the args are null
                                    action1( action1_args );
                                }
                            }
                            break;

                        default:
                            {
                                messageOut( "tsi", "Illegal Behaviour For ID", i->first );
                            }
                            break;
                    }
                }

                if(regData->getCritical())
                {
                    //messageOut( "tsisvs", "Thread W/ID", i->first, " ", i->second.getName(), "Is Critical!" ); //Used for testing
                    nextState = CriticalFailure;

                    nextOutput = "Failure in thread named ";
                    nextOutput.append(regData->getName());
                    nextOutput.append(" (Critical)\n");
                }
                else
                {
                    nextOutput = "Failure in thread named ";
                    nextOutput.append(regData->getName());
                    nextOutput.append(" (Non-Critical)\n");

                    if(nextState != CriticalFailure)
                        nextState = NonCriticalFailure;

                }

                if( getDebugLevel() & DEBUGLEVEL_THREAD_MONITOR )
                    messageOut( "tsisv", "Removing Thread ID", regData->getId(), " ", regData->getName() );
            }
        }
        if(nextOutput.length()>1)
            nextOutput.erase(nextOutput.length()-1,1);//erase last carriage return!

        CtiLockGuard<CtiMutex> guard(_monitorMux);
        _currentState = nextState;
        _output = nextOutput;
    }
    catch( ... )
    {
        messageOut( "ts", "Monitor: Unknown Exception In processExpired()" );
    }
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::processExtraCommands( void )
{
    try
    {
        for( ThreadData::spiterator i = _threadData.getMap().begin(); i != _threadData.getMap().end(); )
        {
            if( i->second->getBehavior() == CtiThreadRegData::LogOut )
            {
                if( getDebugLevel() & DEBUGLEVEL_THREAD_MONITOR )
                    messageOut( "tsis", "Thread ID", i->first, "Logging Out" );

                i = _threadData.getMap().erase( i );
            }
            else
            {
                i++;
            }
        }
    }
    catch( ... )
    {
        messageOut( "ts", "Monitor: Unknown Exception In processExtraCommands()" );
    }
}

//===========================================================================================================
// we'll print out the data we have for all the threads we're watching
//===========================================================================================================
/*
void CtiThreadMonitor::dump( void )
{
    //if we only operate on a copy, we can't explode if someone changes the map
    ThreadData temp_map = _threadData;

    for( ThreadData::spiterator i = temp_map.getMap().begin(); i != temp_map.getMap().end(); i++ )
    {
       CtiThreadRegDataSPtr temp = i->second;

      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         messageOut( "" );
         messageOut( "sv", "Thread name             : ", temp->getName() );
         messageOut( "si", "Thread id               : ", temp->getId() );
         messageOut( "si", "Thread behaviour type   : ", temp->getBehaviour() );
         messageOut( "si", "Thread tickle frequency : ", temp->getTickleFreq() );
         messageOut( "sv", "Thread tickle time      : ", timeString( temp->getTickledTime() ) );
         messageOut( "" );
      }
   }
}
*/
//===========================================================================================================
// each thread that reports to us will give us info (at least initially) that looks like the regdata
// we're trying hard to keep from anyone sending us illegal data, so we check for an id of zero before we
// accept the pointer as that is a CtiThreadRegData default
//
// USERS NOTE: tickle will take care of your pointer, so don't delete it on your end!
//
//===========================================================================================================

void CtiThreadMonitor::tickle( CtiThreadRegData *in )
{
    try
    {
        if( in )
        {
//         CtiThreadRegData *data = new CtiThreadRegData( *in );

            if( in->getId() )
            {
                in->setTickledTime( second_clock::local_time() );

                _queue.putQueue( in );

                if( isDebugLudicrous() )
                    messageOut( "tsis", "Thread", in->getId(), "Inserted" );

                //our thread may have shut down, so we don't want the queue to keep growing
                //as we won't be processing it anymore
                if( !isRunning() )
                {
                    if( getDebugLevel() & DEBUGLEVEL_THREAD_MONITOR )
                        messageOut( "ts", "WARNING: Monitor Is NOT Running, Deleting Monitor Queue" );

                    _queue.clearAndDestroy();
                }
            }
            else
            {
                messageOut( "ts", "Thread Id INVALID" );
            }

            interrupt();   //this should wake us up if we're asleep
        }
    }
    catch( ... )
    {
        messageOut( "ts", "Monitor: Passed BAD Data" );
    }
}

//===========================================================================================================
// http://gethelp.devx.com/techtips/cpp_pro/10min/2001/feb/10min0201-3.asp
//
// fmt == a format string that we look at to decide how to handle the parameters passed in
// 'i' == integer
// 's' == c-style string
// 'v' == a std::string
// 't' == timestamp
//===========================================================================================================

void CtiThreadMonitor::messageOut( const char *fmt, ... )
{
    const char *p = fmt;

    va_list ap;
    va_start( ap, fmt );

    CtiLockGuard<CtiLogger> doubt_guard( dout );

    while( *p )
    {
        if( *p == 'i' )
        {
            int num = va_arg( ap, int );
            dout << " " << num;
        }
        else if( *p == 't' )
        {
            dout << CtiTime();
        }
        else if( *p == 's' )
        {
            string word = va_arg( ap, char * );
            dout << " " << word;
        }
        else if( *p == 'v' )
        {
            string word = va_arg( ap, string );
            dout << " " << word;
        }
        else
        {
            dout << " messageOut format problem" << endl;
        }

        ++p;
    }
    dout << endl;

    va_end( ap );
}

//===========================================================================================================
// Return current state
//===========================================================================================================
CtiThreadMonitor::State CtiThreadMonitor::getState( void)
{
    State returnState;
    {
        CtiLockGuard<CtiMutex> guard(_monitorMux);
        returnState = _currentState;
    }
    return returnState;
}

//===========================================================================================================
// Return current string data
//===========================================================================================================
string CtiThreadMonitor::getString( void)
{

    CtiLockGuard<CtiMutex> guard(_monitorMux);

    return _output;
}

//===========================================================================================================
// Return point ID associated with given offset
//===========================================================================================================
int CtiThreadMonitor::getPointIDFromOffset(int offset)
{
    if(_pointIDList.empty())
    {
        PointIDList tempList;
        for(int i=FirstPoint;i<LastPoint;i++)//note this inserts in the same order as
        {
            //the enumerated list!
            tempList.push_back(GetPIDFromDeviceAndOffset(0,i));
        }
        CtiLockGuard<CtiMutex> guard(_vectorMux);
        _pointIDList = tempList;
    }

    if(_pointIDList.size()>(offset-FirstPoint))
        return _pointIDList[offset-FirstPoint]; //return by value.
    else
        return 0;
}

//===========================================================================================================
// Return all point ID's
// The Point ID will == 0 if the point does not exist. If this is so, do not use this point!
//===========================================================================================================
CtiThreadMonitor::PointIDList CtiThreadMonitor::getPointIDList(void)
{
    if(_pointIDList.empty())
    {
        PointIDList tempList;
        for(int i=FirstPoint;i<LastPoint;i++)//note this inserts in the same order as
        {
            //the enumerated list!
            tempList.push_back(GetPIDFromDeviceAndOffset(0,i));
        }
        CtiLockGuard<CtiMutex> guard(_vectorMux);
        _pointIDList = tempList;
    }
    return _pointIDList; //return by value.
}

//===========================================================================================================
// Re-fills the _pointIDList vector with new information.
//===========================================================================================================
void CtiThreadMonitor::recalculatePointIDList(void)
{
    PointIDList tempList;
    for(int i=FirstPoint;i<LastPoint;i++)//note this inserts in the same order as
    {
        //the enumerated list!
        tempList.push_back(GetPIDFromDeviceAndOffset(0,i));
    }
    CtiLockGuard<CtiMutex> guard(_vectorMux);
    _pointIDList = tempList;
}
//===========================================================================================================
//===========================================================================================================
string CtiThreadMonitor::now( void )
{
    return( to_simple_string( second_clock::local_time() ) );
}

//===========================================================================================================
//===========================================================================================================

string CtiThreadMonitor::timeString( ptime in )
{
    return( to_simple_string( in ) );
}
