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
                CTILOG_INFO(dout, "RTDB Archiver Thread Active.");
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


void CtiThreadMonitor::start(PointOffsets offset)
{
    _pointOffset = offset;

    CtiThread::start();
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::run( void )
{
    long snooze = 1000;

    CTILOG_INFO(dout, "Monitor Startup");

    SetThreadName(-1, "ThreadMon");

    while( !isSet(CtiThread::SHUTDOWN) )
    {
        sleep( snooze );

        snooze = checkForExpriration();

        processQueue();

        if( !gConfigParms.isTrue("YUKON_DISABLE_THREADMONITOR") )
        {
            processExtraCommands();

            processExpired();
        }
    }

    CTILOG_INFO(dout, "Monitor Shutdown");
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

namespace {

std::string timeString( ptime in )
{
    return( to_simple_string( in ) );
}

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
                    CTILOG_INFO(dout, "Thread W/ID " << i->getId() << " " << i->getName() << " has reported."
                                      "\nLast heard from: " << timeString(i->getTickledTime()));
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
    std::string nextOutput = "Thread running correctly.";
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

                    CTILOG_WARN(dout, "Thread W/ID " << i->first << " " << regData->getName() << " is UNREPORTED"
                                       "\nLast heard from: " << timeString(regData->getTickledTime()));

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
                                CTILOG_ERROR(dout, "Illegal Behaviour For ID " << i->first);
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
                {
                    CTILOG_DEBUG(dout, "Removing Thread ID " << regData->getId() << " " << regData->getName());
                }
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                {
                    CTILOG_DEBUG(dout, "Thread ID " << i->first << " Logging Out");
                }

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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

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
                {
                    CTILOG_TRACE(dout, "Thread " << in->getId() << " Inserted");
                }

                //our thread may have shut down, so we don't want the queue to keep growing
                //as we won't be processing it anymore
                if( !isRunning() )
                {
                    CTILOG_WARN(dout, "Monitor Is NOT Running, Deleting Monitor Queue");

                    _queue.clearAndDestroy();
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Thread Id INVALID");
            }

            interrupt();   //this should wake us up if we're asleep
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
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
std::string CtiThreadMonitor::getString( void)
{

    CtiLockGuard<CtiMutex> guard(_monitorMux);

    return _output;
}

int CtiThreadMonitor::getProcessPointID()
{
    if( auto id = _processPointId.load() )
    {
        return id;
    }

    return _processPointId = GetPIDFromDeviceAndOffset( 0, _pointOffset );
}

const long threadPoints[] = {
    CtiThreadMonitor::Porter, 
    CtiThreadMonitor::Dispatch,
    CtiThreadMonitor::Scanner,
    CtiThreadMonitor::Calc,
    CtiThreadMonitor::CapControl,
    CtiThreadMonitor::FDR,
    CtiThreadMonitor::Macs,
    CtiThreadMonitor::LoadManager
};

auto CtiThreadMonitor::getAllProcessPointIDs() -> PointIDList 
{
    PointIDList pointIds;

    pointIds.reserve(std::size(threadPoints));

    for( int pointOffset : threadPoints )
    {
        if( const auto pointId = GetPIDFromDeviceAndOffset( 0, pointOffset ) )
        {
            pointIds.push_back(pointId);
        }
    }
    
    return pointIds;
}

