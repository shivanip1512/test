
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/09/22 20:34:15 $
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

   enum Behaviours   //absence detect behaviour type
   {
      None,          //report missing thread and remove from list
      Restart,       //call fooptr and remove thread from list
      KillApp        //call all fooptrs and remove thread from list
   };                

   typedef void (*fooptr)( void* );

   CtiThreadRegData();
   virtual ~CtiThreadRegData();

   bool operator<( const CtiThreadRegData& y ) const;   //just for the queue, me thinks

   string getName( void );
   void setName( const string in );

   int getId( void );
   void setId( const int in );

   CtiThreadRegData::Behaviours getBehaviour( void );
   void setBehaviour( CtiThreadRegData::Behaviours in );

   ULONG getTickleFreq( void );
   void setTickleFreq( ULONG seconds );

   ptime getTickledTime( void );
   void setTickledTime( ptime in );

   fooptr getShutdownFunc( void );
   void setShutdownFunc( fooptr in );

   void* getShutdownArgs( void );
   void setShutdownArgs( void* in );

   fooptr getAlternateFunc( void );
   void setAlternateFunc( fooptr in );

   void* getAlternateArgs( void );
   void setAlternateArgs( void* in );

   bool getReported( void );
   void setReported( const bool in );

protected:

private:

   bool           _reported;
   string         _name;
   int            _id;
   Behaviours     _behaviourType;
   fooptr         _shutdown;
   fooptr         _alternate;
   ULONG          _tickleFreq;
   ptime          _tickledTime;
   void*          _shutdown_args;
   void*          _shutdown_arg_count;
   void*          _alt_args;
   void*          _alt_arg_count;
};

#endif // #ifndef __THREAD_REGISTER_DATA_H__
