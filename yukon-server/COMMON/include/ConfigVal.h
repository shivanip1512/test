#pragma once

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


