
#pragma warning( disable : 4786)
#ifndef __THREAD_REGISTER_DATA_H__
#define __THREAD_REGISTER_DATA_H__

/*---------------------------------------------------------------------------------*
*
* File:   thread_register_data
*
* Class:  
* Date:   9/2/2004
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
 
#include <string>
using namespace std;

#include "boost_time.h"
#include "cticalls.h"

class IM_EX_CTIBASE CtiThreadRegData
{
public:

   enum  //absence detect behaviour type
   {
      Restart,    //call fooptr and remove thread from list
      KillApp,    //call alt fooptr and remove thread from list
      None        //report missing thread and remove from list
   };                

   typedef void (*fooptr)( void * );

   CtiThreadRegData();
   virtual ~CtiThreadRegData();

   bool operator<( const CtiThreadRegData& y ) const;   //just for the queue, me thinks

   string getName( void );
   void setName( const string in );
   int getId( void );
   void setId( const int in );
   int getBehaviour( void );
   void setBehaviour( int in );
   ULONG getTickleFreq( void );
   void setTickleFreq( ULONG seconds );
   ptime getTickledTime( void );
   void setTickledTime( ptime in );
   fooptr getShutdownFunc( void );
   void setShutdownFunc( fooptr in );
   fooptr getAlternate( void );
   void setAlternate( fooptr in );
   bool getReported( void );
   void setReported( const bool in );

protected:

private:

   bool     _reported;
   string   _name;
   int      _id;
   int      _behaviourType;
   fooptr   _shutdown;
   fooptr   _alternate;
   ULONG    _tickleFreq;
   ptime    _tickledTime;
};

#endif // #ifndef __THREAD_REGISTER_DATA_H__
