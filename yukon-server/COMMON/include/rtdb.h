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
#ifndef __RTDB_H__
#define __RTDB_H__
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
struct my_hash
{
   unsigned long operator() (const K x) const { return x.hash(); }
};

template < class T >
class IM_EX_CTIBASE CtiRTDB : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   // This is a keyed Mapping which does not allow duplicates!
   map<long, T* > Map;

   list< T* > _orphans;

   int _dberrorcode;

public:

   typedef std::map< long, T* >::value_type       val_pair;
   typedef std::map< long, T* >::iterator       MapIterator;


   CtiRTDB() {}

   virtual ~CtiRTDB()
   {
      LockGuard guard(monitor());
      delete_list(_orphans);
      _orphans.clear();       // Clean up the leftovers if there are any.
      //deletes memory pointed to in the value pointer
      for (MapIterator itr = Map.begin(); itr != Map.end(); itr++) {
          delete (*itr).second;
      }
      //clear out the remaining
      Map.clear();

   }

   bool orphan( long id )
   {
       bool status = false;

       T* temp = NULL;
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
    template<class _II, class _Fn> inline
    void ts_for_each(_II _F, _II _L, _Fn _Op, void* d)
    {
        for (; _F != _L; ++_F)
            _Op(*_F, d);
    }
    void apply(void (*applyFun)(const long, T*&, void*), void* d)
    {
        LockGuard  gaurd(monitor());
        ts_for_each( Map.begin(), Map.end(), applyFun, d );
    }

   /*
    *  This operation will return a value to us by
    *
    *  Function should have the following definition
    *
    *    RWBoolean yourTester(T*, void* d);
    */

   T* find(bool (*testFun)(T*, void*),void* d)
   {
      LockGuard  gaurd(monitor());

      MapIterator   itr = Map.begin();

      for( ; itr != Map.end() ; ++itr)
      {
         if( testFun((*itr).second, d) )
            return (*itr).second;
      }

      return NULL;
   }

   T* remove(bool (*testFun)(T*, void*), void* d)
   {
      T* temp = NULL;
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

   int removeAndDestroy(bool (*removeFunc)(T*, void*), void* d)
   {
       int count = 0;

       list< long >       deleteKeys;
       list< CtiDeviceBase* >    deleteObjs;

       MapIterator mapitr = Map.begin();
       CtiDeviceBase   *pdev = 0;
       long             pkey = 0;

       for(; mapitr != Map.end() ; ++mapitr )
       {
           pdev = (*mapitr).second;
           pkey = (*mapitr).first;

           if((*removeFunc)(pdev, d))
           {
               count++;
               deleteKeys.push_back(pkey);
               deleteObjs.push_back(pdev);
           }
       }
       std::list<long>::iterator itr = deleteKeys.begin();
       while( itr != deleteKeys.end() )
       //for(int kpos = 0; kpos < deleteKeys.entries(); kpos++)
       {
           itr = Map.erase(*itr);
       }//TSFLAG

       delete_list(deleteObjs);
       deleteKeys.clear();
       deleteObjs.clear();

       return count;
   }

   size_t entries() const
   {
      LockGuard guard(monitor());
      return Map.size();
   }

   map<long , T* > & getMap()      { return Map; }
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

