
/*************************************************************************
 *
 * rtdb.h      7/7/99
 *
 *****
 *
 *  A templatized run/real time data base class.  Initially done with a
 *  RogueWave singly linked list solely for the benefits of the apply and
 *  find functionality without the need for Standard C++ library.
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __RTDBSmartPointer_H__
#define __RTDBSmartPointer_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....


#include <windows.h>
#include <iostream>
#include <functional>
#include <list>
#include <map>

#include <rw/tphdict.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw\thr\mutex.h>

#include "dlldefs.h"
#include "hashkey.h"
#include "utility.h"
using std::list;
using std::map;
/*
 *  These are the Configuration Parameters for the Port Real Time Database
 */

#include "dllbase.h"

using std::equal_to;


template <class K>
struct my_hash_smartPointer
{
   unsigned long operator() (const K x) const { return x.hash(); }
};

template < class T >
class IM_EX_CTIBASE CtiRTDBSmartPointer : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   // This is a keyed Mapping which does not allow duplicates!
   map<long, T > Map;

   list< T > _orphans;

   int _dberrorcode;

public:

   typedef std::map< long, T >::value_type       val_pair;
   typedef std::map< long, T >::iterator       MapIterator;


   CtiRTDBSmartPointer() {}

   virtual ~CtiRTDBSmartPointer()
   {
      LockGuard guard(monitor());
      _orphans.clear();// Clean up the leftovers if there are any.
      Map.clear();// clear out the remaining

   }

   bool orphan( long id )
   {
       bool status = false;

       T temp;
       MapIterator itr;

       LockGuard  gaurd(monitor());
       //CtiHashKey key(id);

       itr = Map.find( id );
       if ( itr != Map.end() ) {
           long foundKey = (*itr).first
           temp = (*itr).second

           Map.erase( id );
       }else
           temp = NULL;

       if(temp)
       {
           status = true;
           _orphans.push_back( temp );     // Save this guy out so we know we found him
       }
       else
       {
           cout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
       }

       return status;
   }

   /*
    *  This operation will return a value to us by
    *
    *  Function should have the following definition
    *
    *    RWBoolean yourTester(T*, void* d);
    */

   T find(bool (*testFun)(T, void*),void* d)
   {
      LockGuard  gaurd(monitor());

      T empty;

      MapIterator   itr = Map.begin();

      for( ; itr != Map.end() ; ++itr)
      {
         if( testFun((*itr).second, d) )
            return (*itr).second;
      }

      return empty;
   }

   T remove(bool (*testFun)(T*, void*), void* d)
   {
      T temp;
      LockGuard  gaurd(monitor());

      MapIterator   itr Map.begin();

      for( ; itr != Map.end() ; ++itr )
      {
         if(testFun( (*itr).second, d))
         {
            temp = (*itr).second;
            long key = (*itr).first;
            Map.erase( (*itr).first );

            break;
         }
      }

      return temp;
   }

   size_t entries() const
   {
      LockGuard guard(monitor());
      return Map.size();
   }

   map<long , T > & getMap()      { return Map; }
   RWRecursiveLock<RWMutexLock> &      getMux()       { return mutex(); }

   int getErrorCode() const { return _dberrorcode; };
   int setErrorCode(int ec)
   {
       if( ec ) _dberrorcode = ec;      // Only set it if there was an error (don't re-set it)
       return ec;
   }

   void resetErrorCode()
   {
       _dberrorcode = 0;      // Only set it if there was an error (don't re-set it)
   }


};

#endif      // #ifndef __RTDB_H__

