
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/09/21 14:34:16 $
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
// NOTE: it must be verified that pids are ints and not dwords or something...
//===========================================================================================================

void CtiThreadMonitor::run( void )
{

   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "Monitor starting up" << endl;
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
      dout << "Monitor dying!" << endl;
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

         ptime check = temp.getTickledTime() + seconds( temp.getTickleFreq() );

         if( check < now  )
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "Setting thread with id " << i->first << " to not reported " << endl;
            }

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

      insertpair = _threadData.insert( ThreadData::value_type( tempId, *temp ) );

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
// run through map yet again, doing whatever appropriate with the remaining expired
//===========================================================================================================

void CtiThreadMonitor::processExpired( void )
{
   for( ThreadData::iterator i = _threadData.begin(); i != _threadData.end(); i++ )
   {
      if( !i->second.getReported() )
      {
         CtiThreadRegData::fooptr hammer = i->second.getShutdownFunc();
         CtiThreadRegData::fooptr alt = i->second.getAlternate();

         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << "Thread w/ID " << i->first << " is AWOL" << endl;
         }

         int reaction_type = i->second.getBehaviour();

         switch( reaction_type )
         {
         case 0:  //FIXME must use defined types
            {
            }
            break;

         case 1:
            {
               if( alt != NULL )
                  alt( 0 );
            }
            break;

         case 2:
            {
               if( alt != NULL )
                  alt( 0 );

               if( hammer != NULL )
                  hammer( 0 );
            }
            break;

         default:
            {
            }
            break;
         }

         i = _threadData.erase( i );

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
      dout << "Thread frequency        : " << temp.getTickleFreq() << endl; 
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
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << "Thread " << in->getId() << " inserted" << endl;
      }
   }
   else
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "Thread id INVALID" << endl;
   }
}

//===========================================================================================================
// we need to check to see if the thread's last reported tickle is recent enough to let it keep going
//===========================================================================================================

bool CtiThreadMonitor::noReport( CtiThreadRegData candidate )
{
   return false;//temp 
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::removeThread( int index )
{
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::report( int index )
{
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::setQuit( bool in )
{
   _quit = in;
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::stop( void )
{
   _quit = true;
}
