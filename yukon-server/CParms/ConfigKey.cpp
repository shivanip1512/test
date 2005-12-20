#include "yukon.h"
#include <string.h>
#include <stdlib.h>
#include <algorithm>
#include "rwutil.h"

using namespace std;

#include "configkey.h"

#define COLLECTABLE_CONFIGKEY 0x1234

unsigned int str_hash(const std::string& str)
{

   unsigned int hash = 5381;

   for(unsigned int i = 0; i < str.length(); i++)
   {
      hash = ((hash << 5) + hash) + str[i];
   }

   return (hash & 0x7FFFFFFF);


}

RWDEFINE_COLLECTABLE(CtiConfigKey, COLLECTABLE_CONFIGKEY)

CtiConfigKey::CtiConfigKey() : Key()
{;}

CtiConfigKey::CtiConfigKey(const string& key)
{
   Key = key;
   std::transform(Key.begin(), Key.end(), Key.begin(), ::toupper);
}

// Assignement operator
CtiConfigKey&
CtiConfigKey::operator=(const CtiConfigKey& key)
{
   Key = key.Key;
   return (*this);
}
/*
RWspace
CtiConfigKey::binaryStoreSize() const
{
   return Key.binaryStoreSize();
}
*/
int
CtiConfigKey::compareTo(const RWCollectable *X ) const
{
   string aStr = ((const CtiConfigKey*)X)->Key;

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
CtiConfigKey::hash() const
{
    return str_hash(Key);
}

RWBoolean
CtiConfigKey::isEqual(const RWCollectable *c) const
{
   RWBoolean aB = FALSE;

   if(c->isA() == COLLECTABLE_CONFIGKEY)
   {
      string aKey = (((const CtiConfigKey*)c)->getKey());

      aB = (Key == aKey);
   }
   return aB;
}


void
CtiConfigKey::restoreGuts(RWFile& aFile)
{
   RWCollectable::restoreGuts( aFile );
   aFile >> Key;
}
void
CtiConfigKey::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );
   aStream >> Key;
}

void
CtiConfigKey::saveGuts(RWFile &aFile) const
{
   RWCollectable::saveGuts( aFile );
   aFile << Key;
}

void
CtiConfigKey::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );
   aStream << Key;
}

