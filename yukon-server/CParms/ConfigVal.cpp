#include "yukon.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>  
#include "rwutil.h"

#include "configval.h"
#include "hash_functions.h"

RWDEFINE_COLLECTABLE(CtiConfigValue, 0x1235)

CtiConfigValue::CtiConfigValue() : Value()
{;}

CtiConfigValue::CtiConfigValue(const string& key) : Value(key)
{

}


// Assignement operator
CtiConfigValue&
CtiConfigValue::operator=(const CtiConfigValue& val)
{
   Value = val.Value;
   return (*this);
}
/*
RWspace
CtiConfigValue::binaryStoreSize() const
{
   return Value.binaryStoreSize();
}

int
CtiConfigValue::compareTo(const RWCollectable *X ) const
{
   string aStr = ((const CtiConfigValue*)X)->Value;

   if(Value == aStr) return 0;

   if(Value > aStr)
   {
      return 1;
   }
   else
   {
      return -1;
   }
}

unsigned
CtiConfigValue::hash() const
{
    return DJBHash(Value);    
}

RWBoolean
CtiConfigValue::isEqual(const RWCollectable *c) const
{
   string aValue = (((const CtiConfigValue*)c)->Value);
   return (Value == aValue);
}
*/

void
CtiConfigValue::restoreGuts(RWFile& aFile)
{
   RWCollectable::restoreGuts( aFile );
   aFile >> Value;
}
void
CtiConfigValue::restoreGuts(RWvistream& aStream)
{
   RWCollectable::restoreGuts( aStream );
   aStream >> Value;
}

void
CtiConfigValue::saveGuts(RWFile &aFile) const
{
   RWCollectable::saveGuts( aFile );
   aFile << Value;
}

void
CtiConfigValue::saveGuts(RWvostream &aStream) const
{
   RWCollectable::saveGuts( aStream );
   aStream << Value;
}
