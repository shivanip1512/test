
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/09/08 21:17:39 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

//#include "message.h"
#include "thread_timer.h"
#include "thread_listener.h"

class CtiThreadMonitor
{

protected:

private:

   bool noReport( CtiThreadRegData candidate );
   void removeThread( int index );
   void report( int index );

   CtiThreadTimer    _timer;
   CtiThreadListener _listener;

public:

	CtiThreadMonitor();
	virtual ~CtiThreadMonitor();

   void dump( void );
};
#endif // #ifndef __THREAD_MONITOR_H__
