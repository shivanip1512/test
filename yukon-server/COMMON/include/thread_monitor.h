
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/09/21 14:34:17 $
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

   void insertThread( CtiThreadRegData *in ); 
   void dump( void );
   void stop( void );
   virtual void run( void );

protected:

private:

   bool noReport( CtiThreadRegData candidate );
   void removeThread( int index );
   void report( int index );
   void setQuit( bool in );
   void checkForExpriration( void );
   void processQueue( void );
   void processExpired( void );

   mutable CtiMutex                                         _collMux;
   bool                                                     _quit;
   CtiQueue < CtiThreadRegData, less< CtiThreadRegData > >  _queue;
   ThreadData                                               _threadData;

};
#endif // #ifndef __THREAD_MONITOR_H__
