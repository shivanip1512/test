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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2005/08/23 19:56:32 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/
#include "yukon.h"
#include "dllbase.h"
#include "logger.h"
#include "utility.h"
#include "thread_monitor.h"

/*********************************************************************************************
        Example usage of thread_monitor. 
        To find current implementations search for ThreadMonitor.getState() and ThreadMonitor.tickle( data );
        
        //Thread Monitor (code for thread) Any thread that is to be monitored needs code similar to this.
        if(!(++sanity % SANITY_RATE))//SANITY_RATE is used to slow reporting, but must be low enough to allow for the 300 seconds defined below
        {                            //SANITY_RATE currently defined as 300 in most applications
        
            {
                //This is not necessary and can be annoying, but if you want it (which you might) here it is.
                //It should be included or excluded based on thread. Every thread can be implemented differently
                
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DNP Inbound thread active. TID:  " << rwThreadId() << endl;
            }

            //Replace DNP Inbound Thread with whatever thread you want to use
            
            CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "DNP Inbound Thread", CtiThreadRegData::None, 300 );
            ThreadMonitor.tickle( data ); 
        }
        //End Thread Monitor Section (thread specific)

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

                pointMessage.setString(RWCString(ThreadMonitor.getString().c_str()));

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointMessage));
            }
        }

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

   while( !isSet(CtiThread::SHUTDOWN) )
   {
      sleep( snooze );

      if( getDebugLevel() & DEBUGLEVEL_THREAD_SPEW )
         messageOut( "ts", "Monitor Loop" );  //temp, remove in prod

      snooze = checkForExpriration();

      processQueue();

      processExtraCommands();

      processExpired();

     // _queue.clearAndDestroy();
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

   if( _threadData.size() != 0 )
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
      {
         CtiThreadRegData temp = i->second;

         ptime check_time = temp.getTickledTime() + seconds( temp.getTickleFreq() );  //needs validation

         if( current_time > check_time )
         {
            i->second.setReported( false );
         }
         else
         {
            //
            //we want to sleep as long as possible, so find the shortest
            //time to the next possible report failure
            //
            td = check_time - current_time;
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

      int tempId = temp->getId();

      ThreadData::iterator i = _threadData.find( tempId );

      //Note that currently you are set critical or not at initialization and never again.
      if( i != _threadData.end() )
      {
         //update the reg data
         i->second.setBehaviour( temp->getBehaviour() );
         i->second.setTickleFreq( temp->getTickleFreq() );
         i->second.setShutdownFunc( temp->getShutdownFunc() );
         i->second.setShutdownArgs( temp->getShutdownArgs() );
      }

      pair< ThreadData::iterator, bool > insertpair;

      //we try to put the element from the queue into the map
      insertpair = _threadData.insert( ThreadData::value_type( tempId, *temp ) );

      //note that we heard from a particular thread
      (*insertpair.first).second.setReported( true );

      (*insertpair.first).second.setActionTaken( false );

      (*insertpair.first).second.setTickledTime( temp->getTickledTime() );
      //(*insertpair.first).second.setTickledTime( second_clock::local_time() ); //I dont think this is a good idea (JESS)

      delete temp;
   }
}

//===========================================================================================================
// run through map yet again, doing whatever appropriate with the remaining expired threads
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
    State nextState = Normal;
    string nextOutput = "";
   try
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end();i++ )
      {
         if( !i->second.getReported())
         {
             if(!i->second.getActionTaken())
             {

                 i->second.setActionTaken(true);//trying to ensure we dont act twice on an object that does not go away
                 
                 messageOut( "tsisvs", "Thread W/ID", i->first, " ", i->second.getName(), "Is UNREPORTED" );

                 int reaction_type = i->second.getBehaviour();
    
                 switch( reaction_type )
                 {
                 case CtiThreadRegData::None:
                 case CtiThreadRegData::LogOut:
                    {
                    }
                    break;
               
                 case CtiThreadRegData::Action1:
                    {
                       CtiThreadRegData::behaviourFuncPtr action1 = i->second.getAlternateFunc();
                       void* action1_args = i->second.getAlternateArgs();
               
                       if( action1 )
                       {
                          //it doesn't matter if the args are null
                          action1( action1_args );
                       }
                    }
                    break;
               
                 case CtiThreadRegData::Action2:
                    {
                       CtiThreadRegData::behaviourFuncPtr action2 = i->second.getShutdownFunc();
                       void* action2_args = i->second.getShutdownArgs();
               
                       if( action2 )
                       {
                          //it doesn't matter if the args are null
                          action2( action2_args );
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

            if(i->second.getCritical())
            {
                //messageOut( "tsisvs", "Thread W/ID", i->first, " ", i->second.getName(), "Is Critical!" ); //Used for testing
                nextState = CriticalFailure;

                nextOutput.append("Failure in thread named ");
                nextOutput.append(i->second.getName());
                nextOutput.append(" (Critical)\n");
            }
            else
            {
                nextOutput.append("Failure in thread named ");
                nextOutput.append(i->second.getName());
                nextOutput.append(" (Non-Critical)\n");

                if(nextState != CriticalFailure)
                    nextState = NonCriticalFailure;

            }

            if( getDebugLevel() & DEBUGLEVEL_THREAD_SPEW )
               messageOut( "tsisv", "Removing Thread ID", i->first, " ", i->second.getName() );
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
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); )
      {
         if( i->second.getBehaviour() == CtiThreadRegData::LogOut )
         {
            if( getDebugLevel() & DEBUGLEVEL_THREAD_SPEW )
               messageOut( "tsis", "Thread ID", i->first, "Logging Out" );

            i = _threadData.erase( i );
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

void CtiThreadMonitor::dump( void )
{
   //if we only operate on a copy, we can't explode if someone changes the map
   ThreadData temp_map = _threadData;

   for( map < int, CtiThreadRegData >::iterator i = temp_map.begin(); i != temp_map.end(); i++ )
   {
      CtiThreadRegData temp = i->second;

      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         messageOut( "" );
         messageOut( "sv", "Thread name             : ", temp.getName() );
         messageOut( "si", "Thread id               : ", temp.getId() );
         messageOut( "si", "Thread behaviour type   : ", temp.getBehaviour() );
         messageOut( "si", "Thread tickle frequency : ", temp.getTickleFreq() );
         messageOut( "sv", "Thread tickle time      : ", timeString( temp.getTickledTime() ) );
         messageOut( "" );
      }
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

            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
               messageOut( "tsis", "Thread", in->getId(), "Inserted" );

            //our thread may have shut down, so we don't want the queue to keep growing
            //as we won't be processing it anymore
            if( !isRunning() )
            {
               if( getDebugLevel() & DEBUGLEVEL_THREAD_SPEW )
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
         dout << now();
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
        for(int i=PointOffsets::FirstPoint;i<PointOffsets::LastPoint;i++)//note this inserts in the same order as
        {                                                                //the enumerated list!
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
        for(int i=PointOffsets::FirstPoint;i<PointOffsets::LastPoint;i++)//note this inserts in the same order as
        {                                                                //the enumerated list!
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
    for(int i=PointOffsets::FirstPoint;i<PointOffsets::LastPoint;i++)//note this inserts in the same order as
    {                                                                //the enumerated list!
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
