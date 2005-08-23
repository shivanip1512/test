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
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2005/08/23 19:56:52 $
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
   typedef vector < int > PointIDList;

   CtiThreadMonitor();
   virtual ~CtiThreadMonitor();

//   void tickle( const CtiThreadRegData *in );
   void tickle( CtiThreadRegData *in );
   void dump( void );

   enum State//These are considered internal states, who cares what the point value is, I dont deal with that!!!
   {
       Normal = 0,
       NonCriticalFailure,
       CriticalFailure,
       Dead
   };

   string getString();

   enum PointOffsets
   {
       FirstPoint = 1000,//This must always match the first offset
       Porter = 1000,    //and offsets must be sequential!
       Dispatch = 1001,
       Scanner = 1002,
       Calc = 1003,
       LastPoint
   };

   State getState(void);
   PointIDList getPointIDList();
   void recalculatePointIDList(void);
   int getPointIDFromOffset(int offset);

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

   State _currentState;//status point!

   mutable CtiMutex                                         _monitorMux;
   mutable CtiMutex                                         _vectorMux;//for the pointID list
   CtiQueue < CtiThreadRegData, less< CtiThreadRegData > >  _queue;
   ThreadData                                               _threadData;
   PointIDList                                              _pointIDList;
   string                                                   _output;

};

//  included here instead of dllbase.h because ptimes are fat as of 2004-oct-22;
//    include thread_monitor.h if you want to use the thread monitor in your code
IM_EX_CTIBASE extern CtiThreadMonitor  ThreadMonitor;

#endif // #ifndef __THREAD_MONITOR_H__
