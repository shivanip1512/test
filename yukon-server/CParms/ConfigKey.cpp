#include "yukon.h"
#include <string.h>
#include <stdlib.h>
#include <algorithm>
#include "rwutil.h"

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

