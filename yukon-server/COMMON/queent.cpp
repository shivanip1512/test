#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "queent.h"


RWBoolean TagMatch(const CtiQueueEnt *p, void *pTag)
{
   int Tag = *(int*)pTag;

   if(p->getTag() == Tag)
   {
      return RWBoolean(TRUE);
   }

   return RWBoolean(FALSE);
}

// How about that crap.
