
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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2004/09/29 20:26:37 $
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
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << " Monitor Startup" << endl;
   }

   while( !_quit )
   {
      sleep( 1000 );

      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << now() << " Monitor Loop" << endl;
      }

      checkForExpriration();

      processQueue();

      processExpired();

      _queue.clearAndDestroy();
   }

   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << " Monitor Shutdown" << endl;
   }
}

//===========================================================================================================
// if map is non-empty(), compare now to each tickledTime, mark any expired
// need to loop to look at each entry
//===========================================================================================================

void CtiThreadMonitor::checkForExpriration( void )
{
   ptime current( second_clock::local_time() );

   if( _threadData.size() != 0 )
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
      {
         CtiThreadRegData temp = i->second;

         ptime check = temp.getTickledTime() + seconds( temp.getTickleFreq() );  //needs validation

         if( check < current )
         {
            i->second.setReported( false );
         }
      } 
   }
}

//===========================================================================================================
// compare queued ids with stored, mark ones that are found to vaild and add any new ones
//===========================================================================================================

void CtiThreadMonitor::processQueue( void )
{
   while( _queue.entries() )
   {
      //this says it removes the first entry, so we won't C&D later
      CtiThreadRegData *temp = _queue.getQueue();

      int tempId = temp->getId();

      pair< ThreadData::iterator, bool > insertpair;

      //we try to put the element from the queue into the map
      insertpair = _threadData.insert( ThreadData::value_type( tempId, *temp ) );

      //note that we heard from a particular thread
      (*insertpair.first).second.setReported( true );

      //update the time so we know when we heard last
      (*insertpair.first).second.setTickledTime( second_clock::local_time() );
   }
}

//===========================================================================================================
// run through map yet again, doing whatever appropriate with the remaining expired threads
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
   try
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); /*i++*/ )
      {
         if( !i->second.getReported() )
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << now() << " Thread w/ID " << i->first << " is UNREPORTED" << endl;
            }

            int reaction_type = i->second.getBehaviour();

            switch( reaction_type )
            {
            case CtiThreadRegData::None:  
               {
               }
               break;

            case CtiThreadRegData::Restart:
               {
                  CtiThreadRegData::behaviourFuncPtr alt = i->second.getAlternateFunc();
                  void* altArgs = i->second.getAlternateArgs();

                  if( alt != NULL )
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

                  if( hammer != NULL )
                  {
                     //it doesn't matter if the args are null
                     hammer( hammerArgs );
                  }
               }
               break;

            default:
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << "Illegal behaviour type for id " << i->first << endl;
               }
               break;
            }

            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "Removing Thread ID " << i->first << " " << i->second.getName() << endl;
            }

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
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << "Monitor: Unknown exception in processExpired()" << endl;
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
         dout << endl;
         dout << "Thread name             : " << temp.getName() << endl;
         dout << "Thread id               : " << temp.getId() << endl;
         dout << "Thread behaviour type   : " << temp.getBehaviour() << endl;
         dout << "Thread tickle frequency : " << temp.getTickleFreq() << endl;
         dout << "Thread tickle time      : " << timeString( temp.getTickledTime() ) << endl;
         dout << endl;
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
      if( in != NULL )
      {
         CtiThreadRegData *data = new CtiThreadRegData( *in );

         if( data->getId() != 0 )
         {
            data->setTickledTime( second_clock::local_time() );

            if( data->getId() != 0 )
            {
               _queue.putQueue( data );

               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << now() << " Thread " << data->getName() << " " << data->getId() << " inserted" << endl;
               }

               //our thread may have shut down, so we don't want the queue to keep growing
               //as we won't be processing it anymore
               if( !isRunning() )
               {
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << now() <<" WARNING: Monitor is NOT running, deleting monitor queue" << endl;
                  }
                  _queue.clearAndDestroy();
               }
            }
            else
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << now() <<" Thread id INVALID" << endl;
            }
         }
      }
   }
   catch( ... )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() <<" Monitor passed BAD data" << endl;
   }
}

//===========================================================================================================
// this guy will allow a thread that has registered and is done working to un-register with us politely
//===========================================================================================================

void CtiThreadMonitor::removeThread( int id )
{
   ThreadData::iterator i = _threadData.find( id );

   i = _threadData.erase( i );

   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << " Thread with id " << id << " has unregistered" << endl;
   }
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
