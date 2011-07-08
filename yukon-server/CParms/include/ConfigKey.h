#pragma once

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

#include <string>

class CtiConfigKey
{
private:
   std::string   Key;

public:

   CtiConfigKey();
   CtiConfigKey(const std::string& key);
   ~CtiConfigKey() {}

   
   CtiConfigKey& operator=(const CtiConfigKey& key);

   std::string   getKey() const { return Key; }
   std::string&   getKey()        { return Key; }
};


