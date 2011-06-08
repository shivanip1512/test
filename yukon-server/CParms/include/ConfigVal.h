#ifndef  __CONFIGVAL_H__
#define  __CONFIGVAL_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>
#include <string>

class CtiConfigValue
{
private:
   std::string Value;
public:

   CtiConfigValue();
   CtiConfigValue(const std::string& val);
   ~CtiConfigValue() {}

   CtiConfigValue& operator=(const CtiConfigValue& key);

   int ReturnStringOpt (char *opt, int len);

   const std::string&  getValue()    { return Value; }

};


#endif // #ifndef  __CONFIGVAL_H__

