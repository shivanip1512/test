
#pragma warning( disable : 4786)

/*--------------------------------------------------------------------------------------------*
*
* File:   thread_listener
*
* Date:   9/8/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/09/08 21:17:38 $
*
* Copyright (c) 1999, 2000, 2001, 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*---------------------------------------------------------------------------------------------*/

#include "thread_listener.h"

//===========================================================================================================
//===========================================================================================================

CtiThreadListener::CtiThreadListener()
{
}

//===========================================================================================================
//===========================================================================================================

CtiThreadListener::~CtiThreadListener()
{
}

//===========================================================================================================
//===========================================================================================================

void CtiThreadListener::run( void )
{

}

//===========================================================================================================
// where we hear about a new thread starting up
//===========================================================================================================

void CtiThreadListener::registerThread( string name, int id, fooptr shutdown, fooptr alt, ULONG freq )
{ /* CtiThreadRegData  temp;

   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << "Thread " << name << " has registered" << endl;
   }

   temp.setName( name );
   temp.setId( id );
   temp.setShutdownFunc( shutdown );
   temp.setAlternate( alt );
   temp.setBehaviour( 1 );             //not sure how we'll decided this one
   temp.setTickleTime( second_clock::local_time() );
   temp.setTickleFreq( freq );

   _threadData.push_back( temp );              */
}


