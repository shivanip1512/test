
#pragma warning( disable : 4786)
#ifndef __THREAD_LISTENER_H__
#define __THREAD_LISTENER_H__

/*---------------------------------------------------------------------------------*
*
* File:   thread_listener
*
* Class:  CtiThreadListener
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
#include <string>
using namespace std;

//#include "message.h"
#include "thread.h"

typedef int (*fooptr)();//I want this moved down like it is in register_data

class CtiThreadListener: public CtiThread
{

protected:
   
private:

   void run( void );
   void registerThread( string name, int id, fooptr shutdown, fooptr alt, ULONG freq );

public:

	CtiThreadListener();
   virtual ~CtiThreadListener();

};
#endif // #ifndef __THREAD_LISTENER_H__
