#ifndef __CtiAPPLIST_H__
#define __CtiAPPLIST_H__

#include <rw/collect.h>
#include <rw/collstr.h>
#include <rw/cstring.h>

#include <rw/pstream.h>
#include <rw/rstream.h>
#include <rw/hashdict.h>

#include <rw/tphasht.h>

#include "con_mgr.h"

template <class T, class H, class EQ>
class CtiApplList
{
private:

   // A Hash Dictionary which holds pointers
   RWTPtrHashMultiSet<T, H, EQ>          mHash;

public:
   CtiApplList();
   CtiApplList(const CtiApplList &aRef);
   ~CtiApplList();

   int                     Add(T* ConnMan);
   int                     Remove(T* ConnMan);
   int                     getCount() { return mHash.entries(); }
   void                    Puke();

   T*  getByAppId(RWThreadId &TID);
   RWTPtrHashTable<T, H, EQ>& getTable()  { return mHash; }

   // Error enumerations.
   enum
   {
      NORMAL = 0,
      INSERTFAILED,
      KEYNOTFOUND       // get failed because the key was not found
   };
};

#endif         // #ifndef __CtiAPPLIST__

