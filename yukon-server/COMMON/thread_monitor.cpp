
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/22 16:03:54 $
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
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << " Monitor Startup" << endl;
   }

   while( !_quit )
   {
      sleep( 1000 );

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
   ptime now( second_clock::local_time() );

   if( _threadData.size() != 0 )
   {
      for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
      {
         CtiThreadRegData temp = i->second;

         ptime check = temp.getTickledTime() + seconds( temp.getTickleFreq() );  //needs validation

         if( check < now  )
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
      CtiThreadRegData *temp = _queue.getQueue();

      int tempId = temp->getId();

      pair< ThreadData::iterator, bool > insertpair;

      //we try to put the element from the queue into the map
      insertpair = _threadData.insert( ThreadData::value_type( tempId, *temp ) );

      //if we succeed, that element did not exist in the map before
      //if we fail, we've heard from a thread we already knew about, 
      //so update the time and reported
      if( !insertpair.second )
      {
         //note that we heard from a particular thread
         (*insertpair.first).second.setReported( true );

         //update the time so we know when we heard last
         (*insertpair.first).second.setTickledTime( second_clock::local_time() );
      }
   }
}

//===========================================================================================================
// run through map yet again, doing whatever appropriate with the remaining expired threads
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
   for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
   {
      if( !i->second.getReported() )
      {
         //get any functions we should use
         CtiThreadRegData::fooptr hammer = i->second.getShutdownFunc();
         CtiThreadRegData::fooptr alt = i->second.getAlternateFunc();

         //get any args we should use
         void* hammerArgs = i->second.getShutdownArgs();
         void* altArgs = i->second.getAlternateArgs();

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
               if( alt != NULL )
               {
                  if( altArgs != NULL )
                  {
                     char **args = ( char **)altArgs;
                     alt( args );
                  }
                  else
                  {
                     alt( 0 );
                  }
               }
            }
            break;

         case CtiThreadRegData::KillApp:
            {
               if( alt != NULL )
               {
                  if( altArgs != NULL )
                  {
                     char **args = (char**)altArgs;
                     alt( args );
                  }
                  else
                  {
                     alt( 0 );
                  }
               }

               if( hammer != NULL )
               {
                  if( hammerArgs != NULL )
                  {
                     char **args = (char**)hammerArgs;
                     hammer( args );
                  }
                  else
                  {
                     hammer( 0 );
                  }
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


         i = _threadData.erase( i );
//         i = removeThread( i->first ); //like to use this, but then the return type has to be known elsewhere

         if( i == _threadData.end() )
            break;
      }
   }
}

//===========================================================================================================
// we'll print out the data we have for all the threads we're watching
//===========================================================================================================

void CtiThreadMonitor::dump( void )
{  
   CtiLockGuard<CtiLogger> doubt_guard( dout );

   for( map < int, CtiThreadRegData >::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
   {
      CtiThreadRegData temp = i->second;

      dout << endl;
      dout << "Thread name             : " << temp.getName() << endl;
      dout << "Thread id               : " << temp.getId() << endl;
      dout << "Thread behaviour type   : " << temp.getBehaviour() << endl;
      dout << "Thread tickle frequency : " << temp.getTickleFreq() << endl; 
      dout << endl;
   }  
}

//===========================================================================================================
// each thread that reports to us will give us info (at least initially) that looks like the regdata
//===========================================================================================================

void CtiThreadMonitor::insertThread( CtiThreadRegData *in )
{
   in->setTickledTime( second_clock::local_time() );

   if( in->getId() != 0 )
   {
      _queue.putQueue( in );

      {
         ptime now( second_clock::local_time() );

         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << boost::posix_time::to_simple_string( now ) << " Thread " << in->getId() << " inserted" << endl;
      }
   }
   else
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() <<" Thread id INVALID" << endl;
   }
}

//===========================================================================================================
// this guy will allow a thread that has registered and is done working to un-register with us politely
//===========================================================================================================

//CtiThreadMonitor::ThreadData::iterator CtiThreadMonitor::removeThread( int id )
void CtiThreadMonitor::removeThread( int id )
{
   ThreadData::iterator i = _threadData.find( id );

   i = _threadData.erase( i );

   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << now() << " Thread with id " << id << " has unregistered" << endl;
   }

//   return( i );
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::setQuit( bool in )
{
   _quit = in;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::terminate( void )
{
   _quit = true;
}

//===========================================================================================================
//===========================================================================================================

string CtiThreadMonitor::now( void )
{
   return( boost::posix_time::to_simple_string( second_clock::local_time() ) );
}
