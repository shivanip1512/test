
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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/10/06 16:32:13 $
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

protected:

   virtual void run( void );

private:

   void checkForExpriration( void );
   void processQueue( void );
   void processExpired( void );
   void processExtraCommands( void );
   string now( void );
   string timeString( ptime in );
   void messageOut( const char *fmt, ... );

   mutable CtiMutex                                         _collMux;
   bool                                                     _quit;
   CtiQueue < CtiThreadRegData, less< CtiThreadRegData > >  _queue;
   ThreadData                                               _threadData;

};
#endif // #ifndef __THREAD_MONITOR_H__
