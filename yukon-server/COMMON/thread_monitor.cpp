
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/09/08 21:17:39 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/

#include "thread_monitor.h"
#include "logger.h"

//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::CtiThreadMonitor()
{
   _listener.start();
   _timer.start();
}

//===========================================================================================================
//===========================================================================================================

CtiThreadMonitor::~CtiThreadMonitor()
{
}

//===========================================================================================================
// we'll print out the data we have for all the threads we're watching
//===========================================================================================================

void CtiThreadMonitor::dump( void )
{  /*
   CtiLockGuard<CtiLogger> doubt_guard( dout );

   for( int index = 0; index < _threadData.size(); index++ )
   {
      dout << "Thread name             : " << _threadData[index].getName() << endl;
      dout << "Thread id               : " << _threadData[index].getId() << endl;
      dout << "Thread behaviour type   : " << _threadData[index].getBehaviour() << endl;
      dout << "Thread frequency        : " << _threadData[index].getTickleFreq() << endl; 
//      dout << "Thread previous time    : " << _threadData[index].getTickleTime() << endl;
   }  */
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
   /*
   vector <CtiThreadRegData> temp;

   for( int i = 0; i < _threadData.size(); i++ )
   {
      if( i != index )
         temp.push_back( _threadData[i] );
   }

   _threadData = temp;
   */
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadMonitor::report( int index )
{
   /*
   string name = _threadData[index].getName();
   int id = _threadData[index].getId();

   CtiLockGuard<CtiLogger> doubt_guard( dout );
   dout << "Thread " << name << " ID " << id << " failed to report; REMOVING" << endl;
   */
}
