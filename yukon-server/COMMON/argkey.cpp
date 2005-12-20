#include "yukon.h"

#include <string.h>
#include <stdlib.h>
//#include <algorithm>

#include "argkey.h"
#include "rwutil.h"
#include "hash_functions.h"

RWDEFINE_COLLECTABLE(CtiArgKey, 0x1234)

CtiArgKey::CtiArgKey() : Key()
{;}

CtiArgKey::CtiArgKey(char key) : Key(1,key)
{
   std::transform(Key.begin(), Key.end(), Key.begin(), tolower);
}

// Assignement operator
CtiArgKey&
CtiArgKey::operator=(const CtiArgKey& key)
{
   Key = key.Key;
   return (*this);
}

RWspace
CtiArgKey::binaryStoreSize() const
{
   return Key.length();
}

int
CtiArgKey::compareTo(const RWCollectable *X ) const
{
   string aStr = ((const CtiArgKey*)X)->Key;

   if(Key == aStr) return 0;

   if(Key > aStr)
   {
      return 1;
   }
   else
   {
      return -1;
   }
}

unsigned
CtiArgKey::hash() const
{
   return RSHash( Key );
}

RWBoolean
CtiArgKey::isEqual(const RWCollectable *c) const
{
   string aKey = (((const CtiArgKey*)c)->Key);
   return (Key == aKey);
}


void
CtiArgKey::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );
   aStream >> Key;
}

void
CtiArgKey::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );
   aStream << Key;
}

