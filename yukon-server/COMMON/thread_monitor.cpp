
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2004/10/07 12:27:52 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/

#include "thread_monitor.h"
#include "logger.h"

//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::CtiThreadMonitor() :
   _quit( false )
{
}

//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::~CtiThreadMonitor()
{
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::run( void )
{
   int cnt = 0;
   long snooze = 1000;

   messageOut( "ts", "Monitor Startup" );

   while( !_quit )
   {
      sleep( snooze );

      messageOut( "ts", "Monitor Loop" );  //temp, remove in prod

      snooze = checkForExpriration();

      messageOut( "ti", snooze );

      processQueue();

      processExtraCommands();

      processExpired();

      _queue.clearAndDestroy();
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

      if( i != _threadData.end() )
      {
         messageOut( "s", "Updating Thread Data" );
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

      //update the time so we know when we heard last
      (*insertpair.first).second.setTickledTime( second_clock::local_time() );

      delete temp;
   }
}

//===========================================================================================================
// run through map yet again, doing whatever appropriate with the remaining expired threads
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
   try
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); )
      {
         if( !i->second.getReported() )
         {
            messageOut( "tsis", "Thread W/ID", i->first, "Is UNREPORTED" );

            int reaction_type = i->second.getBehaviour();

            switch( reaction_type )
            {
            case CtiThreadRegData::None:
            case CtiThreadRegData::LogOut:
               {
               }
               break;

            case CtiThreadRegData::Restart:
               {
                  CtiThreadRegData::behaviourFuncPtr alt = i->second.getAlternateFunc();
                  void* altArgs = i->second.getAlternateArgs();

                  if( alt )
                  {  
                     //it doesn't matter if the args are null
                     alt( altArgs );
                  }
               }
               break;

            case CtiThreadRegData::KillApp:
               {
                  CtiThreadRegData::behaviourFuncPtr hammer = i->second.getShutdownFunc();
                  void* hammerArgs = i->second.getShutdownArgs();

                  if( hammer )
                  {
                     //it doesn't matter if the args are null
                     hammer( hammerArgs );
                  }
               }
               break;

            default:
               {
                  messageOut( "tsi", "Illegal Behaviour For ID", i->first );  
               }
               break;
            }

            messageOut( "tsisv", "Removing Thread ID", i->first, " ", i->second.getName() );

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
//===========================================================================================================

void CtiThreadMonitor::tickle( const CtiThreadRegData *in )
{
   //we need to copy the data locally to put on the queue or we'll destroy
   //data that is not ours when we delete the queue
   try
   {
      if( in )
      {
         CtiThreadRegData *data = new CtiThreadRegData( *in );

         if( data->getId() )
         {
            data->setTickledTime( second_clock::local_time() );

            _queue.putQueue( data );

            messageOut( "tsis", "Thread", data->getId(), "Inserted" );

            //our thread may have shut down, so we don't want the queue to keep growing
            //as we won't be processing it anymore
            if( !isRunning() )
            {
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
