#include "yukon.h"

#include <string.h>
#include <stdlib.h>

#include "argkey.h"

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
