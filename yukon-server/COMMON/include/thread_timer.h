
#pragma warning( disable : 4786)
#ifndef __THREAD_TIMER_H__
#define __THREAD_TIMER_H__

/*---------------------------------------------------------------------------------*
*
* File:   thread_timer
*
* Class:  CtiThreadTimer
* Date:   9/8/2004
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
#include "thread.h"
#include "thread_register_data.h"

class CtiThreadTimer: public CtiThread
{

protected:
   
private:

   void run( void );

public:

	CtiThreadTimer();                                       	
   virtual ~CtiThreadTimer();

};
#endif // #ifndef __THREAD_TIMER_H__
