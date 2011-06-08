#include "yukon.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>  

#include "configval.h"

using std::string;

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
