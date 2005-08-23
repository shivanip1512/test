
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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/08/23 19:56:52 $
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
      None,          
      Action1,       
      Action2,       
      LogOut
   };                

   typedef void (*behaviourFuncPtr)( void *p );

   CtiThreadRegData( int id = 0,
                      string name = "default",
                      Behaviours type = None,
                      int tickle_freq = 0,
                      behaviourFuncPtr ptr1 = 0,
                      void *args1 = 0,
                      behaviourFuncPtr ptr2 = 0,
                      void *args2 = 0 );

   virtual ~CtiThreadRegData();

   bool operator<( const CtiThreadRegData& y ) const;   //just for the queue, me thinks

   string getName( void );
   void setName( const string in );

   int getId( void );
   void setId( const int &in );

   CtiThreadRegData::Behaviours getBehaviour( void );
   void setBehaviour( CtiThreadRegData::Behaviours in );

   ULONG getTickleFreq( void );
   void setTickleFreq( ULONG seconds );

   ptime getTickledTime( void );
   void setTickledTime( ptime in );

   behaviourFuncPtr getShutdownFunc( void );
   void setShutdownFunc( behaviourFuncPtr in );

   void* getShutdownArgs( void );
   void setShutdownArgs( void *in );

   behaviourFuncPtr getAlternateFunc( void );
   void setAlternateFunc( behaviourFuncPtr in );

   void* getAlternateArgs( void );
   void setAlternateArgs( void *in );

   bool getReported( void );
   void setReported( const bool in );

   bool getCritical( void );
   void setCritical( const bool in );

   void setActionTaken(const bool in);
   bool getActionTaken(void);

protected:

private:

//   CtiThreadRegData();

   bool                 _reported;
   bool                 _critical;//is it critical or not (default true)
   bool                 _actionTaken;//clear until action is taken (makes sure we dont take action twice!)
   ptime                _tickledTime;

   //
   //registeration: must haves
   //
   string               _name;
   int                  _id;
   Behaviours           _behaviourType;
   ULONG                _tickleFreq;

   //
   //registeration: optionals
   //
   behaviourFuncPtr     _action_one;
   behaviourFuncPtr     _action_two;
   void*                _action_one_args;
   void*                _action_two_args;
};

#endif // #ifndef __THREAD_REGISTER_DATA_H__
