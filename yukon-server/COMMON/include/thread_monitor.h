
#pragma warning( disable : 4786)
#ifndef __THREAD_MONITOR_H__
#define __THREAD_MONITOR_H__

/*---------------------------------------------------------------------------------*
*
* File:   thread_monitor
*
* Class:  CtiThreadMonitor
* Date:   9/3/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/09/27 17:14:39 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <map>
using namespace std;

#include "queue.h"
#include "thread.h"
#include "thread_register_data.h"

class IM_EX_CTIBASE CtiThreadMonitor : public CtiThread
{

public:                                                             

   typedef map < int, CtiThreadRegData > ThreadData;

   CtiThreadMonitor();
   virtual ~CtiThreadMonitor();

   void tickle( const CtiThreadRegData *in ); 
   void dump( void );
   void removeThread( int id );

protected:

   virtual void run( void );

private:

   void checkForExpriration( void );
   void processQueue( void );
   void processExpired( void );
   string now( void );
   string timeString( ptime in );

   mutable CtiMutex                                         _collMux;
   bool                                                     _quit;
   CtiQueue < CtiThreadRegData, less< CtiThreadRegData > >  _queue;
   ThreadData                                               _threadData;

};
#endif // #ifndef __THREAD_MONITOR_H__
