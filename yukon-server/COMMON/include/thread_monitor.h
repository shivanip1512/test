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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/02/18 14:32:41 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __THREAD_MONITOR_H__
#define __THREAD_MONITOR_H__

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

   long checkForExpriration( void );
   void processQueue( void );
   void processExpired( void );
   void processExtraCommands( void );
   string now( void );
   string timeString( ptime in );
   void messageOut( const char *fmt, ... );

   mutable CtiMutex                                         _collMux;
   CtiQueue < CtiThreadRegData, less< CtiThreadRegData > >  _queue;
   ThreadData                                               _threadData;

};

//  included here instead of dllbase.h because ptimes are fat as of 2004-oct-22
//  include thread_monitor.h if you want to use the thread monitor in your code
IM_EX_CTIBASE extern CtiThreadMonitor  ThreadMonitor;

#endif // #ifndef __THREAD_MONITOR_H__
