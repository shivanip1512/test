#include "yukon.h"

#include <string.h>
#include <stdlib.h>

#include "argkey.h"

RWDEFINE_COLLECTABLE(CtiArgKey, 0x1234)

CtiArgKey::CtiArgKey() : Key()
{;}

CtiArgKey::CtiArgKey(char key) : Key(key)
{
   Key.toLower();
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
   return Key.binaryStoreSize();
}

int
CtiArgKey::compareTo(const RWCollectable *X ) const
{
   RWCString aStr = ((const CtiArgKey*)X)->Key;

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
   return Key.hash();
}

RWBoolean
CtiArgKey::isEqual(const RWCollectable *c) const
{
   RWCString aKey = (((const CtiArgKey*)c)->Key);
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

