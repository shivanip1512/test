#include "yukon.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#include <rw/collect.h>
#include <rw/cstring.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

#include <rw/tphasht.h>

#include "applist.h"
#include "con_mgr.h"


CtiApplList::CtiApplList() :
   mHash(T::hash)
{
}

CtiApplList::CtiApplList(const CtiApplList &aRef) :
   mHash(T::hash)
{
}

CtiApplList::~CtiApplList()
{
   mHash.clearAndDestroy();
}

int CtiApplList::Add(T* ConMan)
{
   int iRet = NORMAL;
   mHash.insert(ConMan);
   return iRet;
}

int CtiApplList::Remove(T* ConMan)
{
   int iRet = NORMAL;

   if(mHash.remove(ConMan))
   {
      cout << RWTime() << " Removed Connection Manager " << ConMan->getClientName() <<
         " from the List" << endl;
   }
   else
   {
      iRet = !NORMAL;
   }

   return iRet;
}

void
CtiApplList::Puke()
{
   T*   Def;
   RWTPtrHashTableIterator<T>  iter(mHash);

   for(;++iter;)
   {
      Def = iter.key();
      cout << "APPLIST: Application listed " << Def->getClientName() << endl;
   }
}

T*
CtiApplList::getByAppId(RWThreadId &TID)
{
   T*   Def;
   T*   CM = NULL;
   RWTPtrHashTableIterator<T>  iter(mHash);

   for(;++iter;)
   {
      Def = iter.key();
      cout << "APPLIST: Application listed " << Def->getClientName() << endl;
      if(Def->getClientAppId() == TID)
      {
         CM = Def;
         break;
      }
   }

   return CM;
}

