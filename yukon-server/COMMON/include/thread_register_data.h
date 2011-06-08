
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
* REVISION     :  $Revision: 1.18.2.2 $
* DATE         :  $Date: 2008/11/12 17:27:30 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <string>


#include "boost_time.h"
#include "cticalls.h"
#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include <functional>

class IM_EX_CTIBASE CtiThreadRegData
{
public:

   enum Behaviours   //absence detect behaviour type
   {
      None,
      Action,        //Function call
      LogOut
   };

   typedef void (*behaviourFuncPtr)( void *p );

   CtiThreadRegData( int id = 0,
                      std::string name = "default",
                      Behaviours type = None,
                      int tickle_freq_sec = 0,
                      behaviourFuncPtr ptr = 0,
                      void *args = 0 );

   virtual ~CtiThreadRegData();

   bool operator<( const CtiThreadRegData& y ) const;   //just for the queue, me thinks

   std::string getName( void );
   void setName( const std::string in );

   int getId( void );
   void setId( const int &in );

   int getUnreportedCount(void);
   void setUnreportedCount(int count);

   CtiThreadRegData::Behaviours getBehaviour( void );
   void setBehaviour( CtiThreadRegData::Behaviours in );

   ULONG getTickleFreq( void );
   void setTickleFreq( ULONG seconds_between_tickles );

   boost::posix_time::ptime getTickledTime( void );
   void setTickledTime( boost::posix_time::ptime in );

   behaviourFuncPtr getActionFunc( void );
   void setActionFunc( behaviourFuncPtr in );

   void* getActionArgs( void );
   void setActionArgs( void *in );

   bool getReported( void );
   void setReported( const bool in );

   bool getCritical( void );
   void setCritical( const bool in );

   void setActionTaken(const bool in);
   bool getActionTaken(void);

   void resetUnreportedFilter();
   bool testUnreportedFilter();

protected:

private:

//   CtiThreadRegData();

   bool                     _reported;
   bool                     _critical;//is it critical or not (default true)
   bool                     _actionTaken;//clear until action is taken (makes sure we dont take action twice!)
   boost::posix_time::ptime _tickledTime;
   int                      _unreportedCount;
   int                      _unreportedFilter;

   //
   //registeration: must haves
   //
   std::string          _name;
   int                  _id;
   Behaviours           _behaviourType;
   ULONG                _tickleFreq;

   //
   //registeration: optionals
   //
   behaviourFuncPtr     _action;
   void*                _action_args;
};

// This will be used to sort these things in a CtiQueue.
namespace std
{
  template <> struct greater<CtiThreadRegData*>
  {
    bool operator()(CtiThreadRegData const* p1, CtiThreadRegData const* p2)
    {
      //Defined like less for ascending sorting
      if(!p1)
        return true;
      if(!p2)
        return false;
      return *p2 < *p1;
    }
  };
};


typedef boost::shared_ptr< CtiThreadRegData > CtiThreadRegDataSPtr;

#endif // #ifndef __THREAD_REGISTER_DATA_H__
