
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/09/16 16:17:36 $
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
   while( !_quit )
   {
      sleep( 1000 );

      checkForExpriration();

      processQueue();

      processExpired();

      _queue.clearAndDestroy();
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
            i->second.setReported( false );
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
         (*insertpair.first).second.setReported( true );
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
         fooptr hammer = i->second.getShutdownFunc();

         if( hammer != NULL )   //this will need to be extended to cover different types
            hammer();

         _threadData.erase( i );
      }
   }
}

//===========================================================================================================
// we'll print out the data we have for all the threads we're watching
//===========================================================================================================

void CtiThreadMonitor::dump( void )
{  
   CtiLockGuard<CtiLogger> doubt_guard( dout );

   for( int index = 0; index < _threadData.size(); index++ )
   {
      dout << "Thread name             : " << _threadData[index].getName() << endl;
      dout << "Thread id               : " << _threadData[index].getId() << endl;
      dout << "Thread behaviour type   : " << _threadData[index].getBehaviour() << endl;
      dout << "Thread frequency        : " << _threadData[index].getTickleFreq() << endl; 
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
   }
   else
   {
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
